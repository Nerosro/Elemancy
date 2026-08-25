package be.nerosro.elemancy.spell.casting;

import java.util.Optional;

import be.nerosro.elemancy.ElemancyTags;
import be.nerosro.elemancy.effects.CastEffects;
import be.nerosro.elemancy.items.wands.WandCastFeedback;
import be.nerosro.elemancy.items.wands.WandItem;
import be.nerosro.elemancy.mana.depth.CastResolution;
import be.nerosro.elemancy.mana.depth.ManaDepthSystem;
import be.nerosro.elemancy.spell.SpellContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Shared cast logic for all instant (non-continuous) spell handlers.
 * Validates wand, spends mana via the depth system, applies swing/cooldown/durability,
 * and handles fizzle. Callers only need to implement the success branch.
 */
public final class SpellCast {
    private SpellCast() {
    }

    /**
     * Attempts a standard spell cast from the offhand wand.
     * Returns empty if the cast cannot proceed (wrong hand, wrong wand, insufficient mana).
     * Returns a CastResolution if mana was spent — check {@code spellResolved()} for success vs fizzle.
     * Swing, cooldown, durability, and fizzle particles are handled internally.
     */
    public static Optional<CastResolution> cast(Player player, float baseCost, SpellContext context) {
        if (player.level().isClientSide()) return Optional.empty();

        ItemStack offhand = player.getOffhandItem();
        if (!offhand.is(ElemancyTags.WANDS)) return Optional.empty();
        if (player.getCooldowns().isOnCooldown(offhand)) return Optional.empty();
        if (!WandItem.canCast(offhand, context)) return Optional.empty();

        CastResolution resolution = ManaDepthSystem.attemptCast(player, baseCost, context);
        if (!resolution.castConsumed()) return Optional.empty();

        WandCastFeedback.castWithWear(player, offhand);

        if (!resolution.spellResolved()) {
            if (player.level() instanceof ServerLevel serverLevel) {
                CastEffects.fizzle(serverLevel, player);
            }
        }

        return Optional.of(resolution);
    }
}
