package be.nerosro.elemancy.ritual.attunement;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.Nullable;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.ElemancyTags;
import be.nerosro.elemancy.effects.CastEffects;
import be.nerosro.elemancy.items.ElemancyItems;
import be.nerosro.elemancy.items.RobeSetBonus;
import be.nerosro.elemancy.items.WandItem;
import be.nerosro.elemancy.mana.depth.CastResolution;
import be.nerosro.elemancy.mana.depth.ManaDepthSystem;
import be.nerosro.elemancy.ritual.shared.RitualStructureDetector;
import be.nerosro.elemancy.ritual.shared.StructureRotationTemplate;
import be.nerosro.elemancy.skilltree.EquippedSpellUtil;
import be.nerosro.elemancy.skilltree.SkillTreeEntries;
import be.nerosro.elemancy.spell.SpellContext;
import be.nerosro.elemancy.spell.SpellRegistry;
import be.nerosro.soulmark.attunement.AttunementUtil;
import be.nerosro.soulmark.mana.ManaUtil;
import be.nerosro.soulmark.skilltree.SkillTreeUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * The real Attunement Ritual trigger: casting Elementize on a resting, unattuned Soulvial that
 * is sitting on a validated ritual structure's center block, while standing on the correct
 * platform tile.
 * <p>
 * Mirrors {@code InfusionEvents}' targeting pattern (direct entity interact + look-cone
 * fallback), but is scoped only to Soulvials - it never touches the generic infusion recipe
 * pipeline, since arming/triggering the ritual is not a simple item-to-item conversion.
 */
@EventBusSubscriber(modid = Elemancy.MOD_ID)
public class TriggerEvents {

    /**
     * Ritual player tile sits four blocks from the Soulvial; use reach that remains reliable with vertical offset.
     */
    private static final double MAX_RANGE = 8.0;
    private static final double MAX_LOOK_ANGLE_DEG = 15.0;
    private static final double MIN_LOOK_DOT = Math.cos(Math.toRadians(MAX_LOOK_ANGLE_DEG));

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (tryTriggerRitual(event.getEntity(), event.getTarget())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (tryFallbackTrigger(event)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (tryFallbackTrigger(event)) event.setCanceled(true);
    }

    private static boolean tryFallbackTrigger(PlayerInteractEvent event) {
        if (event.getLevel().isClientSide()) return false;
        if (event.getHand() != InteractionHand.MAIN_HAND) return false;
        ItemEntity target = findLookTargetSoulvial(event.getEntity());
        return tryTriggerRitual(event.getEntity(), target);
    }

    private static ItemEntity findLookTargetSoulvial(Player player) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();

        AABB searchBox = player.getBoundingBox().expandTowards(look.scale(MAX_RANGE)).inflate(1.0);
        List<ItemEntity> candidates = player.level().getEntitiesOfClass(
            ItemEntity.class,
            searchBox,
            e -> {
                if (!e.isAlive() || !ElemancyItems.isSoulvial(e.getItem().getItem())) return false;
                double distSq = eye.distanceToSqr(e.position());
                if (distSq > MAX_RANGE * MAX_RANGE) return false;
                Vec3 toEntity = e.position().subtract(eye).normalize();
                return look.dot(toEntity) >= MIN_LOOK_DOT;
            }
        );

        if (candidates.isEmpty()) return null;
        return candidates.stream()
            .min(Comparator.comparingDouble(e -> eye.distanceToSqr(e.position())))
            .orElse(null);
    }

    private static boolean tryTriggerRitual(Player player, @Nullable Entity target) {
        if (!(target instanceof ItemEntity itemEntity)) return false;
        if (player.level().isClientSide()) return false;

        ItemStack stack = itemEntity.getItem();
        if (!ElemancyItems.isUnattunedSoulvial(stack.getItem())) return false;

        Identifier equipped = EquippedSpellUtil.getEquippedSpell(player);
        if (equipped == null || !SpellRegistry.isInfusion(equipped)) return false;

        ItemStack offhand = player.getOffhandItem();
        if (!offhand.is(ElemancyTags.WANDS)) return false;
        if (!WandItem.canCast(offhand, SpellContext.INFUSION)) return false;
        if (player.distanceTo(itemEntity) > MAX_RANGE) return false;

        if (!SkillTreeUtil.hasNode(player, SkillTreeEntries.ATTUNEMENT_RITUAL_ID)) {
            player.sendSystemMessage(Component.literal("You are missing the required knowledge to activate this.").withStyle(ChatFormatting.RED));
            return true;
        }

        if (AttunementUtil.isAttuned(player)) {
            player.sendSystemMessage(Component.literal("You are already attuned.").withStyle(ChatFormatting.RED));
            return true;
        }

        // Checks if the ritual structure is correctly built
        BlockPos itemPos = itemEntity.blockPosition();
        BlockPos anchor = player.level().getFluidState(itemPos).isSource() ? itemPos : itemPos.below();
        Optional<Integer> rotationOpt = RitualStructureDetector.detect(player.level(), anchor, StructureTemplate.TEMPLATE);
        if (rotationOpt.isEmpty()) {
            player.sendSystemMessage(Component.literal("The ritual site is incomplete.").withStyle(ChatFormatting.RED));
            return true;
        }
        int rotation = rotationOpt.get();
        var element = StructureTemplate.resolveElement(player.level(), anchor);

        BlockPos standingOffset = StructureRotationTemplate.rotateOffset(
            StructureTemplate.STANDING_TILE_OFFSET.getX(),
            StructureTemplate.STANDING_TILE_OFFSET.getY(),
            StructureTemplate.STANDING_TILE_OFFSET.getZ(),
            rotation);
        // .above(): the standing-tile offset points at the platform block itself: a player
        // standing on top of it occupies the block one Y level above, not the block's own position.
        BlockPos standingPos = anchor.offset(standingOffset).above();
        if (!player.blockPosition().equals(standingPos)) {
            player.sendSystemMessage(Component.literal("You need to be part of the ritual.").withStyle(ChatFormatting.RED));
            return true;
        }

        float cost = ManaUtil.getMana(player).getOriginMaxPool() * 1.15f;
        CastResolution resolution = ManaDepthSystem.attemptCast(player, cost, SpellContext.INFUSION);
        if (!resolution.castConsumed()) return false;

        applyCastFeedback(player, offhand);

        if (!resolution.spellResolved()) {
            if (player.level() instanceof ServerLevel serverLevel) {
                CastEffects.fizzle(serverLevel, player);
            }
            return true;
        }

        // The Soulvial itself only transforms into an attuned Soulvial at the moment lightning
        // strikes the center block, during the cutscene's finale - not immediately here.
        CutsceneEngine.start(player, anchor, rotation, element, itemEntity);

        return true;
    }

    private static void applyCastFeedback(Player player, ItemStack offhand) {
        player.swing(InteractionHand.OFF_HAND, true);
        player.getCooldowns().addCooldown(offhand, WandItem.COOLDOWN_TICKS);
        if (!RobeSetBonus.tryAbsorbWandDamage(player)) {
            offhand.hurtAndBreak(1, player, EquipmentSlot.OFFHAND);
        }
    }
}
