package be.nerosro.elemancy.mana;

import be.nerosro.elemancy.effects.ElemancyEffects;
import be.nerosro.elemancy.items.trinket.TrinketBonuses;
import be.nerosro.elemancy.mana.depth.ScarFacade;
import be.nerosro.soulmark.mana.ManaData;
import be.nerosro.soulmark.mana.ManaUtil;
import net.minecraft.world.entity.player.Player;

/**
 * Resolves the player's effective mana stats each tick by composing all modifier sources.
 * Each source is a separate method returning a multiplier (1.0 = no effect).
 * Pool and regen are independent axes — a source can affect either or both.
 */
public final class ManaStatResolver {
    private ManaStatResolver() {
    }

    private static final float PARADOX_FLOWER_REGEN = 2.0f;

    /**
     * Computes and writes the final effective maxPool and regenRate from all active sources.
     * Called once per tick, after scar timers have been decremented.
     */
    public static void resolve(Player player) {
        ManaData mana = ManaUtil.getMana(player);
        if (!mana.isInitialized()) return;

        mana.setMaxPool(mana.getOriginMaxPool() * effectivePoolFactor(player));
        mana.setRegenRate(mana.getOriginRegenRate() * effectiveRegenFactor(player));

        if (mana.getCurrentMana() > mana.getMaxPool()) {
            mana.setCurrentMana(mana.getMaxPool());
        }
    }

    // ── Composite multipliers ───────────────────────────────────────────────────

    private static float effectivePoolFactor(Player player) {
        float m = 1.0f;
        m *= scarPoolPenalty(player);
        m += TrinketBonuses.poolBoost(player);
        return m;
    }

    private static float effectiveRegenFactor(Player player) {
        float m = 1.0f;
        m *= scarRegenPenalty(player);
        m *= flowerRegenBoost(player);
        m += TrinketBonuses.regenBoost(player);
        return m;
    }

    // ── Individual sources ──────────────────────────────────────────────────────

    private static float scarPoolPenalty(Player player) {
        return ScarFacade.getPoolMultiplier(player);
    }

    private static float scarRegenPenalty(Player player) {
        return ScarFacade.getRegenMultiplier(player);
    }

    private static float flowerRegenBoost(Player player) {
        return player.hasEffect(ElemancyEffects.MANA_REGEN_BOOST)
            ? PARADOX_FLOWER_REGEN : 1.0f;
    }
}
