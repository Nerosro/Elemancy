package com.nerosro.elemancy.items;

import com.nerosro.elemancy.Elemancy;
import com.nerosro.elemancy.Reference;
import net.minecraft.item.Item;

/**
 * Created by Nerosro on 24/01/2018.
 */
public class ItemInfIngot extends Item{

    public ItemInfIngot(){
        setUnlocalizedName(Reference.ElemItems.INFINGOT.getUnlocName());
        setRegistryName(Reference.ElemItems.INFINGOT.getRegName());
        setCreativeTab(Elemancy.CREATIVE_TAB);
    }
}
