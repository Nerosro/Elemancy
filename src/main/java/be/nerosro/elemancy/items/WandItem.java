package be.nerosro.elemancy.items;

import be.nerosro.elemancy.effects.CastEffects;
import be.nerosro.elemancy.mana.depth.CastResolution;
import be.nerosro.elemancy.mana.depth.ManaDepthSystem;
import be.nerosro.elemancy.skilltree.EquippedSpellUtil;
import be.nerosro.elemancy.spell.SpellContext;
import be.nerosro.elemancy.spell.SpellEntry;
import be.nerosro.elemancy.spell.SpellRegistry;
import be.nerosro.elemancy.spell.SpellShape;
import be.nerosro.elemancy.spell.casting.ContinuousCaster;
import be.nerosro.elemancy.spell.casting.SpellwarpResolver;
import be.nerosro.elemancy.spell.data.ContinuousSpellData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

/**
 * Base class for wand items. Each wand has a WandAspect that determines
 * which spell elements it can channel.
 * <p>
 * Also handles continuous cast spells via the vanilla item-use system:
 * use() starts channeling, onUseTick() drains mana + deals damage,
 * releaseUsing() ends the channel and applies cooldown.
 */
public class WandItem extends Item {

    public static final int COOLDOWN_TICKS = 10;
    private static final int MAX_CHANNEL_TICKS = 72000;

    private final WandAspect aspect;

    public WandItem(Properties properties, WandAspect aspect) {
        super(properties);
        this.aspect = aspect;
    }

    public WandAspect getAspect() {
        return aspect;
    }

    /**
     * Returns the WandAspect for an ItemStack, defaulting to NONE if not a WandItem.
     */
    public static WandAspect getAspect(ItemStack stack) {
        if (stack.getItem() instanceof WandItem wand) {
            return wand.getAspect();
        }
        return WandAspect.NONE;
    }

    /**
     * Checks if the given wand stack can channel the spell's element.
     * Use this before attemptCast to gate elemental access.
     */
    public static boolean canCast(ItemStack wandStack, SpellContext context) {
        return getAspect(wandStack).canChannel(context.element());
    }

    // ── Continuous cast: item-use system ────────────────────────────────────

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (hand != InteractionHand.OFF_HAND) return InteractionResult.PASS;

        Identifier equipped = EquippedSpellUtil.getEquippedSpell(player);
        if (equipped == null) return InteractionResult.PASS;

        // Determine if this cast should channel continuously
        ContinuousSpellData data = resolveContinuousData(player, equipped);
        if (data == null) return InteractionResult.PASS;

        // Validate wand can channel this element
        if (!canCast(player.getOffhandItem(), data.toContext())) return InteractionResult.PASS;

        // Initial cast attempt — subject to fizzle/depth resolution
        if (!level.isClientSide()) {
            CastResolution resolution = ManaDepthSystem.attemptCast(
                player, data.initialManaCost(), data.toContext());

            if (!resolution.castConsumed()) return InteractionResult.PASS;

            if (!resolution.spellResolved()) {
                if (level instanceof ServerLevel serverLevel) {
                    CastEffects.fizzle(serverLevel, player);
                }
                player.getCooldowns().addCooldown(player.getOffhandItem(), COOLDOWN_TICKS);
                return InteractionResult.CONSUME;
            }

            // Channel starts successfully
            ContinuousCaster.applySlowdown(player);
            ContinuousCaster.setActiveChannel(player, data);
        }

        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    /**
     * Resolves ContinuousSpellData for the equipped spell, accounting for Spellwarp.
     * Returns null if this cast should NOT be handled as continuous (let SpellDispatcher handle it).
     */
    private ContinuousSpellData resolveContinuousData(Player player, Identifier equipped) {
        boolean isNaturallyContinuous = SpellRegistry.isContinuous(equipped);

        if (SpellwarpResolver.hasSpellwarp(player)) {
            SpellShape rolledShape = SpellwarpResolver.rollShape(player);

            if (rolledShape == SpellShape.CONTINUOUS) {
                // Spell warped into continuous — use element defaults
                if (isNaturallyContinuous) {
                    return ContinuousSpellData.get(equipped);
                }
                SpellEntry entry = SpellRegistry.get(equipped);
                if (entry == null) return null;
                return SpellwarpResolver.getContinuousData(entry.element());
            }

            // Spellwarp rolled non-continuous — even if spell is naturally continuous,
            // let SpellDispatcher handle it as a one-shot
            return null;
        }

        // No Spellwarp: normal behavior
        if (!isNaturallyContinuous) return null;
        return ContinuousSpellData.get(equipped);
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity user) {
        if (!(user instanceof Player player)) return 0;

        Identifier equipped = EquippedSpellUtil.getEquippedSpell(player);
        if (equipped == null) return 0;

        // If the spell is naturally continuous, always allow channeling
        if (SpellRegistry.isContinuous(equipped)) return MAX_CHANNEL_TICKS;

        // If Spellwarp is active, any attack spell might roll continuous
        if (SpellRegistry.isAttack(equipped) && SpellwarpResolver.hasSpellwarp(player)) {
            return MAX_CHANNEL_TICKS;
        }

        return 0;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack itemStack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int ticksRemaining) {
        if (level.isClientSide()) return;
        if (!(livingEntity instanceof Player player)) return;

        Identifier equipped = EquippedSpellUtil.getEquippedSpell(player);
        if (equipped == null) {
            player.stopUsingItem();
            return;
        }

        ContinuousSpellData data = ContinuousCaster.getActiveChannel(player, equipped);
        if (data == null) {
            player.stopUsingItem();
            return;
        }

        int elapsedTicks = MAX_CHANNEL_TICKS - ticksRemaining;
        ContinuousCaster.tick(player, data, elapsedTicks);
    }

    @Override
    public boolean releaseUsing(ItemStack itemStack, Level level, LivingEntity entity, int remainingTime) {
        if (level.isClientSide()) return false;
        if (!(entity instanceof Player player)) return false;

        ContinuousCaster.removeSlowdown(player);
        ContinuousCaster.clearActiveChannel(player);

        // Apply cooldown and durability loss
        player.getCooldowns().addCooldown(itemStack, COOLDOWN_TICKS);
        itemStack.hurtAndBreak(1, player, EquipmentSlot.OFFHAND);

        return true;
    }
}


