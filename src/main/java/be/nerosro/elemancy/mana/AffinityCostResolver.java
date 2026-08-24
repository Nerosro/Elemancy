package be.nerosro.elemancy.mana;

import be.nerosro.elemancy.spell.SpellElement;
import be.nerosro.soulmark.affinity.AffinityUtil;
import be.nerosro.soulmark.element.Element;
import be.nerosro.soulmark.element.SoulmarkElements;
import net.minecraft.world.entity.player.Player;

/**
 * Resolves the affinity-based cost multiplier for a spell cast.
 * Compares the player's birth affinity (from Soulmark) against the spell's element.
 */
final class AffinityCostResolver {
    private AffinityCostResolver() {
    }

    /**
     * Returns the affinity cost multiplier for this player + spell element combination.
     * Returns 1.0f when the system is disabled, spell is untyped, or player has no affinity yet.
     */
    static float resolve(Player player, SpellElement spellElement) {
        if (!AffinityCostValues.enabled()) return 1f;
        if (spellElement == SpellElement.NONE) return 1f;

        Element playerElement = AffinityUtil.getAffinity(player);
        if (playerElement == null) return 1f;

        Element spellEl = spellElement.toElement();
        if (spellEl == null) return 1f;

        if (playerElement == spellEl) {
            return AffinityCostValues.attunedMultiplier();
        }

        Element opposite = SoulmarkElements.getOpposite(playerElement);
        if (opposite == spellEl) {
            return AffinityCostValues.opposingMultiplier();
        }

        return AffinityCostValues.adjacentMultiplier();
    }
}

