package be.nerosro.elemancy.spell;

import net.minecraft.resources.Identifier;

/**
 * Metadata record for a registered spell.
 * Contains the spell's identity, element, category, and default shape.
 */
public record SpellEntry(Identifier id, SpellElement element, SpellCategory category, SpellShape shape) {
}
