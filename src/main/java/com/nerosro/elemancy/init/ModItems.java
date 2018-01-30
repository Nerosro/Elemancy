package com.nerosro.elemancy.init;

import com.nerosro.elemancy.blocks.BlockInfWool;
import com.nerosro.elemancy.items.ItemInfIngot;
import com.nerosro.elemancy.items.ItemInfMetal;
import com.nerosro.elemancy.items.ItemWand;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * Created by Nerosro on 23/01/2018.
 */
public class ModItems {
    //Initialiting items, registering them with Forge, registering the render for inventory

    public static Item wand;
    public static Item infIngot;
    public static Item infMetal;

    public static void init(){
        wand=new ItemWand();
        infIngot =new ItemInfIngot();
        infMetal=new ItemInfMetal();
    }
    public static void register(){
        ForgeRegistries.ITEMS.register(wand);
        ForgeRegistries.ITEMS.register(infIngot);
        ForgeRegistries.ITEMS.register(infMetal);
    }
    public static void registerRenders(){
        registerRender(wand);
        registerRender(infIngot);
        registerRender(infMetal);
    }
    public static void registerRender(Item item){
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item,0,new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
