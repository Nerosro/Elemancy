package be.nerosro.elemancy.spell;

/**
 * The delivery shape of a spell.
 * Placeholder for future shape-based cost modifiers.
 */
public enum SpellShape {
    PROJECTILE("Projectile"),
    BEAM("Beam"),
    HITSCAN("Hitscan"),
    CONTINUOUS("Continuous"),
    BURST("Burst"),
    WAVE("Wave"),
    SELF("Self"),
    PLACED("Placed");

    public final String displayName;

    SpellShape(String displayName) {
        this.displayName = displayName;
    }
}

