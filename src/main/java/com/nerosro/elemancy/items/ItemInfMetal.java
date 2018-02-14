package com.nerosro.elemancy.items;

import com.nerosro.elemancy.Elemancy;
import com.nerosro.elemancy.Reference;
import net.minecraft.item.Item;

/**
 * Created by Nerosro on 24/01/2018.
 */
public class ItemInfMetal extends Item {

    public ItemInfMetal(){
        setUnlocalizedName(Reference.ElemItems.INFMETAL.getUnlocName());
        setRegistryName(Reference.ElemItems.INFMETAL.getRegName());
        setCreativeTab(Elemancy.CREATIVE_TAB);
    }
}
