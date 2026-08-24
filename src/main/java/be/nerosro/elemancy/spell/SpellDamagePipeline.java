package be.nerosro.elemancy.spell;

import be.nerosro.elemancy.mana.depth.ScarFacade;
import be.nerosro.elemancy.traits.TraitModifiers;
import net.minecraft.world.entity.player.Player;

/**
 * Composes all spell damage modifier sources into a single multiplier.
 * Sources: trait modifiers (Aggressive/Timid/Resonant/Dissonant), scar penalties (Spell Weakness).
 */
public final class SpellDamagePipeline {
    private SpellDamagePipeline() {
    }

    /**
     * Returns the final damage multiplier after all sources.
     * Composition order: trait multiplier → scar multiplier.
     */
    public static float resolve(Player player, SpellContext context) {
        float multiplier = TraitModifiers.damageMultiplier(player, context);
        multiplier *= ScarFacade.getDamageMultiplier(player);
        return multiplier;
    }
}
