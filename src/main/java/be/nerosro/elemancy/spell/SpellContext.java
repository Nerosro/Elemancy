package be.nerosro.elemancy.spell;

import org.jetbrains.annotations.Nullable;

/**
 * Describes what a spell is — its element, category, and shape.
 * Passed into the cast pipeline so modifiers can react to spell metadata.
 * Player-side data (affinity, traits, scars) is read internally by the pipeline.
 */
public record SpellContext(
    SpellElement element,
    SpellCategory category,
    @Nullable SpellShape shape
) {
    /**
     * Untyped utility spell with no element or shape (Elementize, Energize).
     */
    public static final SpellContext INFUSION = new SpellContext(SpellElement.NONE, SpellCategory.INFUSION, null);

    /**
     * Convenience: element + category, no shape.
     */
    public static SpellContext of(SpellElement element, SpellCategory category) {
        return new SpellContext(element, category, null);
    }

    /**
     * Full context with shape.
     */
    public static SpellContext of(SpellElement element, SpellCategory category, SpellShape shape) {
        return new SpellContext(element, category, shape);
    }
}

