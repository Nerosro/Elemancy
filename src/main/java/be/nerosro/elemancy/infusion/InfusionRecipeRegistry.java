package be.nerosro.elemancy.infusion;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Registry for infusion recipes used by Elementize, Transmute, and future infusion spells.
 * Each recipe defines: input item → output item, mana cost per item, and which spell it belongs to.
 * All recipes are 1:1 (one input item produces one output item).
 */
public class InfusionRecipeRegistry {

    private static final Map<Item, InfusionRecipe> RECIPES = new HashMap<>();

    private static final int DEFAULT_MAX_PER_CAST = 16;

    public static void register(Item input, Item output, float manaPerItem, InfusionSpell spell) {
        RECIPES.put(input, new InfusionRecipe(input, output, manaPerItem, spell, DEFAULT_MAX_PER_CAST));
    }

    public static void register(Item input, Item output, float manaPerItem, InfusionSpell spell, int maxPerCast) {
        RECIPES.put(input, new InfusionRecipe(input, output, manaPerItem, spell, maxPerCast));
    }

    public static Optional<InfusionRecipe> getRecipe(ItemStack stack) {
        return Optional.ofNullable(RECIPES.get(stack.getItem()));
    }
}

