package com.nerosro.elemancy.items;

import com.nerosro.elemancy.Elemancy;
import com.nerosro.elemancy.Reference;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

/**
 * Created by Nerosro on 14/02/2018.
 */
public class ItemRobes extends ItemArmor{
    public ItemRobes(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn, String unlocName) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
        this.setUnlocalizedName(unlocName);
        this.setRegistryName(Reference.MODID, unlocName);
        setCreativeTab(Elemancy.CREATIVE_TAB);
    }
}
