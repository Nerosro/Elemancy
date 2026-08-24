package be.nerosro.elemancy.items.trinket;

/**
 * A trinket that passively modifies mana stats while equipped.
 * Used by Tier 0 trinkets (all pure stat sticks).
 */
public class ManaStatTrinketItem extends TrinketItem {

    private final ManaModifierType modifierType;
    private final float magnitude;

    public ManaStatTrinketItem(Properties properties, ManaModifierType modifierType, float magnitude) {
        super(properties);
        this.modifierType = modifierType;
        this.magnitude = magnitude;
    }

    public ManaModifierType getModifierType() {
        return modifierType;
    }

    /**
     * Returns the modifier as a multiplier fraction (e.g. 0.05 for 5%).
     */
    public float getMagnitude() {
        return magnitude;
    }

    public enum ManaModifierType {
        COST_REDUCTION,
        REGEN_BOOST,
        POOL_BOOST
    }
}
