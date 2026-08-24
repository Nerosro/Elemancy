package be.nerosro.elemancy.spell;

import org.jetbrains.annotations.Nullable;

import be.nerosro.soulmark.element.Element;
import be.nerosro.soulmark.element.SoulmarkElements;

/**
 * The elemental type of a spell.
 * Maps to Soulmark's Element registry for affinity comparisons.
 * NONE covers untyped/utility spells.
 */
public enum SpellElement {
    FIRE,
    WATER,
    EARTH,
    AIR,
    LIGHT,
    DARK,
    NONE;

    /**
     * Maps this spell element to the corresponding Soulmark Element instance.
     * Returns null for NONE (untyped spells have no element).
     * Only call after registries have frozen.
     */
    @Nullable
    public Element toElement() {
        return switch (this) {
            case FIRE -> SoulmarkElements.FIRE.get();
            case WATER -> SoulmarkElements.WATER.get();
            case EARTH -> SoulmarkElements.EARTH.get();
            case AIR -> SoulmarkElements.AIR.get();
            case LIGHT -> SoulmarkElements.LIGHT.get();
            case DARK -> SoulmarkElements.DARK.get();
            case NONE -> null;
        };
    }
}

