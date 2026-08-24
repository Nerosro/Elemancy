package be.nerosro.elemancy.infusion;

import net.minecraft.world.item.Item;

/**
 * Defines a single infusion recipe: one input item → one output item at a given mana cost per item.
 * The spell identifies which infusion spell this recipe belongs to.
 * {@code maxPerCast} limits how many items can be converted in a single cast (e.g. 1 for unique items like Tome).
 */
public record InfusionRecipe(Item input, Item output, float manaPerItem, InfusionSpell spell, int maxPerCast) {
}

