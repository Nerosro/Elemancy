package com.nerosro.elemancy.init;

import com.nerosro.elemancy.blocks.BlockInfWool;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * Created by Nerosro on 29/01/2018.
 */
public class ModBlocks {

    public static Block infWool;

    public static void init(){
        infWool=new BlockInfWool();
    }

    public static void register(){

        registerBlock(infWool);
    }

    private  static void registerBlock(Block block){
        ForgeRegistries.BLOCKS.register(block);
        ItemBlock item = new ItemBlock(block);
        item.setRegistryName(block.getRegistryName());
        ForgeRegistries.ITEMS.register(item);
    }

    public static void registerRenders(){
        registerRender(infWool);
    }

    private static void registerRender(Block block){
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(),"inventory"));
    }
}
