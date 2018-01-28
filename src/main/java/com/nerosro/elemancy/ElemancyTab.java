package com.nerosro.elemancy;

import com.nerosro.elemancy.init.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

/**
 * Created by Nerosro on 24/01/2018.
 */
public class ElemancyTab extends CreativeTabs {
     public ElemancyTab() {
        super("tabElemancy");
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(ModItems.infIngot);
    }
}
