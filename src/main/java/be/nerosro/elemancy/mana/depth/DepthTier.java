package be.nerosro.elemancy.mana.depth;

/**
 * Classification of overcasting depth tiers based on negative mana.
 */
public enum DepthTier {
    NONE,
    DEPTH_1,
    DEPTH_2,
    DEPTH_3,
    DEPTH_4;

    public boolean isOvercast() {
        return this != NONE;
    }

    public static DepthTier fromMana(float currentMana) {
        return ManaDepthRules.fromMana(currentMana);
    }
}
