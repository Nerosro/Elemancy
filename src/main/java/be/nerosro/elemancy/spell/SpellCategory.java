package be.nerosro.elemancy.spell;

/**
 * The functional category of a spell.
 * Every spell must belong to exactly one category.
 */
public enum SpellCategory {
    ATTACK("Attack"),
    INFUSION("Infusion"),
    MOBILITY("Mobility"),
    DEFENSE("Defense"),
    SUPPORT("Support");

    public final String displayName;

    SpellCategory(String displayName) {
        this.displayName = displayName;
    }
}

