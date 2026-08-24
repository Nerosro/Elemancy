package be.nerosro.elemancy;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * Elemancy item tag keys.
 */
public class ElemancyTags {

    /**
     * Items tagged as wands are eligible to cast Elemancy spells when held in the offhand.
     * Add any new wand items to the data/elemancy/tags/item/wands.json tag file.
     */
    public static final TagKey<Item> WANDS = ItemTags.create(
        Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "wands")
    );

    /**
     * All 6 element-specific Elemetal Block items.
     */
    public static final TagKey<Item> ELEMETAL_BLOCKS = ItemTags.create(
        Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "elemetal_blocks")
    );

    /**
     * All 6 element-specific Elemetal Ingot items.
     */
    public static final TagKey<Item> ELEMETAL_INGOTS = ItemTags.create(
        Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "elemetal_ingots")
    );

    public static final TagKey<Item> FIRE_ELEMETAL_INGOTS = itemTag("fire_elemetal_ingots");
    public static final TagKey<Item> WATER_ELEMETAL_INGOTS = itemTag("water_elemetal_ingots");
    public static final TagKey<Item> EARTH_ELEMETAL_INGOTS = itemTag("earth_elemetal_ingots");
    public static final TagKey<Item> AIR_ELEMETAL_INGOTS = itemTag("air_elemetal_ingots");
    public static final TagKey<Item> LIGHT_ELEMETAL_INGOTS = itemTag("light_elemetal_ingots");
    public static final TagKey<Item> DARK_ELEMETAL_INGOTS = itemTag("dark_elemetal_ingots");

    private static TagKey<Item> itemTag(String path) {
        return ItemTags.create(Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, path));
    }
}

