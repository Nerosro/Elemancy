package com.nerosro.elemancy.items;

import com.nerosro.elemancy.Elemancy;
import com.nerosro.elemancy.Reference;
import net.minecraft.item.Item;

/**
 * Created by Nerosro on 17/02/2018.
 */
public class ItemTome extends Item {

    public ItemTome(){
        setUnlocalizedName(Reference.ElemItems.TOME.getUnlocName());
        setRegistryName(Reference.ElemItems.TOME.getRegName());
        setCreativeTab(Elemancy.CREATIVE_TAB);
        setMaxStackSize(1);
    }
}
