package be.nerosro.elemancy.items;

import be.nerosro.elemancy.ElemancyTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ToolMaterial;

/**
 * Custom tool materials for Elemancy tools.
 */
public interface ElemancyToolMaterials {

    /**
     * Infused Metal tool material.
     * Stone-tier mining level and speed, iron-tier durability.
     * Enchantment value is 0 — Infused tools are Stage 0 gear, not worth enchanting.
     */
    ToolMaterial INFUSED_METAL = new ToolMaterial(
        BlockTags.INCORRECT_FOR_STONE_TOOL,  // stone mining level
        250,                                  // iron-tier durability
        4.0F,                                 // stone-tier speed
        1.0F,                                 // stone-tier attack damage bonus
        0,                                    // not enchantable
        ItemTags.STONE_TOOL_MATERIALS          // unused (repairable not applied)
    );

    ToolMaterial FIRE_ELEMETAL = new ToolMaterial(
        BlockTags.INCORRECT_FOR_IRON_TOOL,
        250,
        6.0F,
        2.0F,
        14,
        ElemancyTags.FIRE_ELEMETAL_INGOTS
    );

    ToolMaterial WATER_ELEMETAL = new ToolMaterial(
        BlockTags.INCORRECT_FOR_IRON_TOOL,
        250,
        6.0F,
        2.0F,
        14,
        ElemancyTags.WATER_ELEMETAL_INGOTS
    );

    ToolMaterial EARTH_ELEMETAL = new ToolMaterial(
        BlockTags.INCORRECT_FOR_IRON_TOOL,
        250,
        6.0F,
        2.0F,
        14,
        ElemancyTags.EARTH_ELEMETAL_INGOTS
    );

    ToolMaterial AIR_ELEMETAL = new ToolMaterial(
        BlockTags.INCORRECT_FOR_IRON_TOOL,
        250,
        6.0F,
        2.0F,
        14,
        ElemancyTags.AIR_ELEMETAL_INGOTS
    );

    ToolMaterial LIGHT_ELEMETAL = new ToolMaterial(
        BlockTags.INCORRECT_FOR_IRON_TOOL,
        250,
        6.0F,
        2.0F,
        14,
        ElemancyTags.LIGHT_ELEMETAL_INGOTS
    );

    ToolMaterial DARK_ELEMETAL = new ToolMaterial(
        BlockTags.INCORRECT_FOR_IRON_TOOL,
        250,
        6.0F,
        2.0F,
        14,
        ElemancyTags.DARK_ELEMETAL_INGOTS
    );
}
