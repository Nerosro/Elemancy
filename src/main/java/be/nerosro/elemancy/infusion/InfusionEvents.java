package be.nerosro.elemancy.infusion;

import java.util.Comparator;
import java.util.List;

import org.jspecify.annotations.Nullable;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.ElemancyTags;
import be.nerosro.elemancy.effects.CastEffects;
import be.nerosro.elemancy.items.ElemancyItems;
import be.nerosro.elemancy.items.robes.RobeSetBonus;
import be.nerosro.elemancy.items.tome.TomeItem;
import be.nerosro.elemancy.items.wands.WandCastFeedback;
import be.nerosro.elemancy.items.wands.WandItem;
import be.nerosro.elemancy.mana.depth.CastResolution;
import be.nerosro.elemancy.mana.depth.ManaDepthSystem;
import be.nerosro.elemancy.skilltree.EquippedSpellUtil;
import be.nerosro.elemancy.skilltree.SkillTreeEntries;
import be.nerosro.elemancy.spell.SpellContext;
import be.nerosro.elemancy.spell.SpellRegistry;
import be.nerosro.soulmark.attunement.AttunementUtil;
import be.nerosro.soulmark.network.SoulmarkNetwork;
import be.nerosro.soulmark.skilltree.SkillTreeUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Handles infusion spells (Elementize, Transmute, etc.).
 * Right-clicking a dropped ItemEntity while holding a wand in the offhand
 * infuses it using mana, converting the stack to its output.
 */
@EventBusSubscriber(modid = Elemancy.MOD_ID)
public class InfusionEvents {

    /**
     * Maximum distance (in blocks) between the player and the item entity.
     */
    private static final double MAX_RANGE = 6.0;

    /**
     * Cooldown in ticks applied to the stick after a cast attempt.
     */

    // EntityInteract fires when the crosshair targets the item entity directly.
    // The fallback handlers below cover cases where the crosshair targets air/block near the item.
    // Both paths may fire for the same tick — this is safe because tryInfuse consumes/shrinks the
    // target item on success, so any subsequent call for the same entity no-ops.
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (tryInfuse(event.getEntity(), event.getTarget())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (tryFallbackInfuse(event)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (tryFallbackInfuse(event)) event.setCanceled(true);
    }

    private static boolean tryFallbackInfuse(PlayerInteractEvent event) {
        if (event.getLevel().isClientSide()) return false;
        if (event.getHand() != InteractionHand.MAIN_HAND) return false;
        ItemEntity target = findLookTargetItemEntity(event.getEntity());
        return tryInfuse(event.getEntity(), target);
    }

    /**
     * Maximum angle (in degrees) between look direction and item for fallback targeting.
     * MIN_LOOK_DOT is the cosine threshold — dot product must be at least this value.
     */
    private static final double MAX_LOOK_ANGLE_DEG = 15.0;
    private static final double MIN_LOOK_DOT = Math.cos(Math.toRadians(MAX_LOOK_ANGLE_DEG));

    private static ItemEntity findLookTargetItemEntity(Player player) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();

        AABB searchBox = player.getBoundingBox().expandTowards(look.scale(MAX_RANGE)).inflate(1.0);
        List<ItemEntity> candidates = player.level().getEntitiesOfClass(
            ItemEntity.class,
            searchBox,
            e -> {
                if (!e.isAlive() || e.getItem().isEmpty()) return false;
                double distSq = eye.distanceToSqr(e.position());
                if (distSq > MAX_RANGE * MAX_RANGE) return false;
                // Angle check: item must be within the look cone
                Vec3 toEntity = e.position().subtract(eye).normalize();
                return look.dot(toEntity) >= MIN_LOOK_DOT;
            }
        );

        if (candidates.isEmpty()) return null;

        return candidates.stream()
            .min(Comparator.comparingDouble(e -> eye.distanceToSqr(e.position())))
            .orElse(null);
    }

    private static boolean tryInfuse(Player player, @Nullable Entity target) {
        if (!(target instanceof ItemEntity itemEntity)) return false;
        if (player.level().isClientSide()) return false;

        // Only allow infusion when the player has an infusion spell equipped
        Identifier equipped = EquippedSpellUtil.getEquippedSpell(player);
        if (equipped == null) return false;
        if (!SpellRegistry.isInfusion(equipped)) return false;

        // Any item tagged as a wand can cast spells from the offhand
        ItemStack offhand = player.getOffhandItem();
        if (!offhand.is(ElemancyTags.WANDS)) return false;
        if (!WandItem.canCast(offhand, SpellContext.INFUSION)) return false;
        if (player.distanceTo(itemEntity) > MAX_RANGE) return false;

        ItemStack targetStack = itemEntity.getItem();
        if (targetStack.isEmpty()) return false;

        var recipeOpt = InfusionRecipeRegistry.getRecipe(targetStack);
        if (recipeOpt.isEmpty()) return false;

        var recipe = recipeOpt.get();
        // Process up to the per-cast cap so large stacks require multiple casts.
        int toConvert = computeConvertCount(recipe, targetStack);
        float totalCost = toConvert * recipe.manaPerItem();

        CastResolution resolution = ManaDepthSystem.attemptCast(player, totalCost, SpellContext.INFUSION);
        if (!resolution.castConsumed()) return false;

        // Visible cast animation and cooldown are applied to all successful cast attempts, including failures.
        // Infused Robes only protect the early Elementize loop with the Energized Stick.        
        applyCastFeedback(player, offhand);

        if (!resolution.spellResolved()) {
            if (player.level() instanceof ServerLevel serverLevel) {
                CastEffects.fizzle(serverLevel, player);
            }
            return true;
        }

        performConversion(player, itemEntity, targetStack, recipe, toConvert);
        playCastEffects(player, itemEntity, recipe);

        return true;
    }

    private static int computeConvertCount(InfusionRecipe recipe, ItemStack targetStack) {
        return Math.min(targetStack.getCount(), recipe.maxPerCast());
    }

    private static void applyCastFeedback(Player player, ItemStack offhand) {
        if (RobeSetBonus.tryAbsorbElementizeWandDamage(player)) {
            WandCastFeedback.cast(player, offhand);
        } else {
            WandCastFeedback.castWithWear(player, offhand);
        }
    }

    private static void performConversion(Player player, ItemEntity itemEntity, ItemStack targetStack, InfusionRecipe recipe, int toConvert) {
        targetStack.shrink(toConvert);
        if (targetStack.isEmpty()) {
            itemEntity.discard();
        } else {
            itemEntity.setItem(targetStack); // Mark entity dirty for client sync
        }

        ItemStack output;
        Item outputItem = recipe.output();
        if (outputItem == ElemancyItems.INFUSED_INGOT.get() && AttunementUtil.isAttuned(player)) {
            outputItem = ElemancyItems.getElemetalIngot(AttunementUtil.getAttunement(player)).get();
        } else if (outputItem == ElemancyItems.SOULVIAL.get() && AttunementUtil.isAttuned(player)) {
            outputItem = ElemancyItems.getAttunedSoulvial(AttunementUtil.getAttunement(player)).get();
        }
        output = new ItemStack(outputItem, toConvert);
        if (output.getItem() instanceof TomeItem) {
            TomeItem.bindToPlayer(output, player);
            // Discovering the Elemancy skill tree on first tome creation
            SkillTreeUtil.discoverTree(player, SkillTreeEntries.TREE_ID);
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                SoulmarkNetwork.syncSkillTree(serverPlayer);
            }
        }

        ItemEntity outputEntity = new ItemEntity(
            itemEntity.level(),
            itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(),
            output
        );
        outputEntity.setNoPickUpDelay();
        itemEntity.level().addFreshEntity(outputEntity);
    }

    private static void playCastEffects(Player player, ItemEntity itemEntity, InfusionRecipe recipe) {
        if (!(itemEntity.level() instanceof ServerLevel serverLevel)) return;

        int color = recipe.spell().particleColor();
        CastEffects.targetBurst(serverLevel, itemEntity.position(), color, 12);
        CastEffects.casterBurst(serverLevel, player, color);
        CastEffects.infuseSound(serverLevel, itemEntity.blockPosition());
    }
}