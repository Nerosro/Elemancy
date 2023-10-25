package io.github.nerosro.elemancy.util;

import io.github.nerosro.elemancy.Elemancy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Created by Nerosro on 8/10/2023.
 */
public class ModTags {
    public static class Blocks{
        public static final TagKey<Block> EXAMPLE_TAG = tag("example_block_tag");

        private static TagKey<Block> tag(String name){
            return BlockTags.create(new ResourceLocation(Elemancy.MOD_ID, name));
        }
    }
    public static class Items{
        //public static final TagKey<Item> EXAMPLE_TAG = tag("example_item_tag");
        private static TagKey<Item> tag(String name){
            return ItemTags.create(new ResourceLocation(Elemancy.MOD_ID, name));
        }
    }
}
