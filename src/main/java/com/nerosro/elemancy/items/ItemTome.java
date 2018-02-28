package com.nerosro.elemancy.items;

import com.nerosro.elemancy.Elemancy;
import com.nerosro.elemancy.Reference;
import com.nerosro.elemancy.client.gui.TomeGui;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

/**
 * Created by Nerosro on 17/02/2018.
 */
public class ItemTome extends Item {

    public ItemTome(){
        setUnlocalizedName(Reference.ElemItems.TOME.getUnlocName());
        setRegistryName(Reference.ElemItems.TOME.getRegName());
        setCreativeTab(Elemancy.CREATIVE_TAB);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
        if(!world.isRemote){

        }else {
            if ((!player.getHeldItem(hand).getDisplayName().contains(player.getName()))&&(!player.isCreative())) {
                TextComponentString text = new TextComponentString(TextFormatting.DARK_BLUE + Reference.WRONG_TOME);
                player.sendStatusMessage(text, true);
            } else {
                Minecraft.getMinecraft().displayGuiScreen(new TomeGui()); //Only do this on client side!!!
            }
        }
        return super.onItemRightClick(world,player,hand);
    }
}
