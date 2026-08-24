package be.nerosro.elemancy.tome;

import be.nerosro.elemancy.Elemancy;
import net.minecraft.resources.Identifier;

/**
 * Discovery node ID constants for Tome entry unlocks.
 * <p>
 * Discovery nodes are hidden skill nodes (not visible in skill tree UI).
 * They exist purely to track which Tome entries the player has unlocked
 * through world interactions (right-clicking blocks with Tome).
 */
public final class DiscoveryNodes {
    private DiscoveryNodes() {
    }

    /**
     * Unlocked when player right-clicks a Paradox Flower with their Tome.
     * Gates access to the expanded Paradox Flower entry.
     */
    public static final Identifier PARADOX_FLOWER =
        Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "discovery/paradox_flower");

    /**
     * Unlocked when player right-clicks a beehive near a Paradox Flower with their Tome.
     * Gates access to the Mana-Reactive Beehive entry.
     */
    public static final Identifier INFUSED_BEEHIVE =
        Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "discovery/infused_beehive");
}
