package com.nerosro.elemancy.init;

import com.nerosro.elemancy.Reference;
import com.nerosro.elemancy.items.ItemRobes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * Created by Nerosro on 13/02/2018.
 */
public class ModArmour {

    public static ItemArmor.ArmorMaterial RobeCloth = EnumHelper.addArmorMaterial("robecloth", Reference.MODID + ":robecloth", 4, new int[]{1,1,1,1},0,new SoundEvent(new ResourceLocation("item.armor.equip_leather")), 0f);

    public static ItemArmor robeHood;
    public static ItemArmor robeChest;
    public static ItemArmor robeLegs;
    public static ItemArmor robeShoes;

    public static void init(){
        robeHood = new ItemRobes(RobeCloth, 1 , EntityEquipmentSlot.HEAD, "robe_hood");
        robeChest = new ItemRobes(RobeCloth, 1 , EntityEquipmentSlot.CHEST, "robe_chest");
        robeLegs = new ItemRobes(RobeCloth, 2 , EntityEquipmentSlot.LEGS, "robe_legs");
        robeShoes = new ItemRobes(RobeCloth, 1 , EntityEquipmentSlot.FEET, "robe_shoes");
    }
    public static void register(){
        ForgeRegistries.ITEMS.register(robeHood);
        ForgeRegistries.ITEMS.register(robeChest);
        ForgeRegistries.ITEMS.register(robeLegs);
        ForgeRegistries.ITEMS.register(robeShoes);
    }
    public static void registerRenders(){
        registerRender(robeHood);
        registerRender(robeChest);
        registerRender(robeLegs);
        registerRender(robeShoes);
    }

    public static void registerRender(Item item) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item,0,new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

}
