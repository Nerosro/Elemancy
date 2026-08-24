package be.nerosro.elemancy.infusion;

import java.util.List;

import be.nerosro.elemancy.block.ElemancyBlocks;
import be.nerosro.elemancy.items.ElemancyItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/**
 * Registers all Elementize infusion recipes for Elemancy.
 * Add new recipes here as new infusable materials are introduced.
 */
public class InfusionRecipes {

    /**
     * All vanilla wool items. Cannot use ItemTags.WOOL here — tags aren't bound during FMLCommonSetupEvent.
     * If modded-wool compatibility is ever needed: move registration to ServerAboutToStartEvent
     * (where tags are available), or use a tag-based Predicate at recipe-match time instead of
     * explicit items at registration time.
     */
    private static final List<Item> WOOL_ITEMS = List.of(
        Items.WHITE_WOOL, Items.ORANGE_WOOL, Items.MAGENTA_WOOL, Items.LIGHT_BLUE_WOOL,
        Items.YELLOW_WOOL, Items.LIME_WOOL, Items.PINK_WOOL, Items.GRAY_WOOL,
        Items.LIGHT_GRAY_WOOL, Items.CYAN_WOOL, Items.PURPLE_WOOL, Items.BLUE_WOOL,
        Items.BROWN_WOOL, Items.GREEN_WOOL, Items.RED_WOOL, Items.BLACK_WOOL
    );

    public static void register() {
        InfusionRecipeRegistry.register(Items.COPPER_INGOT, ElemancyItems.INFUSED_INGOT.get(), 2f, InfusionSpell.ELEMENTIZE);
        InfusionRecipeRegistry.register(Items.BOOK, ElemancyItems.TOME.get(), 5f, InfusionSpell.ELEMENTIZE, 1);
        InfusionRecipeRegistry.register(ElemancyItems.ARCANE_VESSEL.get(), ElemancyItems.SOULVIAL.get(), 50f, InfusionSpell.ELEMENTIZE, 1);

        // Any color wool → Infused Wool (the infusion drains the colour)
        WOOL_ITEMS.forEach(wool ->
            InfusionRecipeRegistry.register(wool, ElemancyBlocks.INFUSED_WOOL.get().asItem(), 2f, InfusionSpell.ELEMENTIZE)
        );
    }
}
