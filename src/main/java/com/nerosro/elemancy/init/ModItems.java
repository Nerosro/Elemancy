package com.nerosro.elemancy.init;

import com.nerosro.elemancy.items.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * Created by Nerosro on 23/01/2018.
 */
public class ModItems {
    //Initialiting items, registering them with Forge, registering the render for inventory

    public static Item wand;
    public static Item tome;
    public static Item infIngot;
    public static Item infMetal;
    public static Item icecream;

    public static void init(){
        wand=new ItemWand();
        tome=new ItemTome();
        infIngot =new ItemInfIngot();
        infMetal=new ItemInfMetal();
        icecream = new ItemIceCream(3, false).setAlwaysEdible();
    }
    public static void register(){
        ForgeRegistries.ITEMS.register(wand);
        ForgeRegistries.ITEMS.register(tome);
        ForgeRegistries.ITEMS.register(infIngot);
        ForgeRegistries.ITEMS.register(infMetal);
        ForgeRegistries.ITEMS.register(icecream);
    }
    public static void registerRenders(){
        registerRender(wand);
        registerRender(tome);
        registerRender(infIngot);
        registerRender(infMetal);
        registerRender(icecream);
    }
    public static void registerRender(Item item){
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
