package be.nerosro.elemancy.mana;

import be.nerosro.elemancy.items.trinket.TrinketBonuses;
import be.nerosro.elemancy.mana.depth.ScarFacade;
import be.nerosro.elemancy.spell.SpellContext;
import be.nerosro.elemancy.traits.TraitModifiers;
import net.minecraft.world.entity.player.Player;

/**
 * Composes all cast cost modifier sources into a single multiplier.
 * Sources: scar penalties, affinity alignment, trait modifiers.
 * Independent of the depth system — any system can query final cost without coupling to overcasting.
 */
public final class CastCostPipeline {
    private CastCostPipeline() {
    }

    /**
     * Returns the final adjusted cost after all modifiers. Uses neutral context.
     */
    public static float resolve(Player player, float baseCost) {
        return resolve(player, baseCost, SpellContext.INFUSION);
    }

    /**
     * Returns the final adjusted cost after all modifiers with full spell context.
     * Composition order: scar multiplier → affinity multiplier → trait multiplier → trinket reduction.
     */
    public static float resolve(Player player, float baseCost, SpellContext context) {
        float cost = ScarFacade.applyCastCostModifiers(player, baseCost);
        cost *= AffinityCostResolver.resolve(player, context.element());
        cost *= TraitModifiers.costMultiplier(player, context);
        cost *= (1.0f - TrinketBonuses.costReduction(player));
        return cost;
    }
}
