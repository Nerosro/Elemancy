package com.nerosro.elemancy.items;

import com.nerosro.elemancy.Elemancy;
import com.nerosro.elemancy.Reference;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Nerosro on 26/02/2018.
 */
public class ItemIceCream extends ItemFood {
    public ItemIceCream(int amount, boolean isWolfFood) {
        super(amount, isWolfFood);

        setUnlocalizedName(Reference.ElemItems.ICECREAM.getUnlocName());
        setRegistryName(Reference.ElemItems.ICECREAM.getRegName());
        setMaxStackSize(1);
        setCreativeTab(Elemancy.CREATIVE_TAB);
    }

    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        super.onItemUseFinish(stack, worldIn, entityLiving);
        return new ItemStack(Items.BOWL);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

        tooltip.add("The perfect snack for any Neromancer");
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
