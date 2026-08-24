package be.nerosro.elemancy.items;

import be.nerosro.elemancy.spell.SpellElement;

/**
 * Defines what elements a wand can channel.
 * Every wand always allows SpellElement.NONE (utility spells).
 * The aspect restricts which elemental spells the wand can cast.
 */
public enum WandAspect {
    NONE(SpellElement.NONE),
    ALL(SpellElement.NONE),
    FIRE(SpellElement.FIRE),
    WATER(SpellElement.WATER),
    EARTH(SpellElement.EARTH),
    AIR(SpellElement.AIR),
    LIGHT(SpellElement.LIGHT),
    DARK(SpellElement.DARK);

    private final SpellElement element;

    WandAspect(SpellElement element) {
        this.element = element;
    }

    /**
     * Returns true if this aspect allows casting a spell with the given element.
     */
    public boolean canChannel(SpellElement element) {
        if (element == SpellElement.NONE) return true;
        if (this == ALL) return true;
        if (this == NONE) return false;
        return this.element == element;
    }
}

