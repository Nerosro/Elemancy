package com.nerosro.elemancy.items;

import com.nerosro.elemancy.Elemancy;
import com.nerosro.elemancy.Reference;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nerosro on 23/01/2018.
 */
public class ItemWand extends Item {

    //Setting item as item/handheld makes it look like a tool/stick being held
    public ItemWand() {
        setUnlocalizedName(Reference.ElemItems.WAND.getUnlocName());
        setRegistryName(Reference.ElemItems.WAND.getRegName());
        setCreativeTab(Elemancy.CREATIVE_TAB);
        setMaxStackSize(1);
        setMaxDamage(10);
    }

    public static void craftOld(EntityPlayer player, EnumHand hand, ItemStack in, ItemStack out) {
        List<EntityItem> items = player.getEntityWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(player.posX - 3, player.posY - 3, player.posZ - 3, player.posX + 3, player.posY + 3, player.posZ + 3));
        ItemStack heldItem = player.getHeldItem(hand);

        int did = 0;
        if ((heldItem.getItemDamage()+items.size()) <= heldItem.getMaxDamage()) {
            for (EntityItem item : items) {
                ItemStack stack = item.getItem();

                if (!stack.isEmpty()) {
                    if (ItemStack.areItemsEqual(stack, in)) {
                        ItemStack outCopy = out.copy();
                        outCopy.setCount(stack.getCount());
                        if(outCopy.getDisplayName().equals("Tome")){
                            outCopy.setStackDisplayName(player.getName() + "'s " + outCopy.getDisplayName());
                        }
                        item.setItem(outCopy);

                        did = did + in.getCount();

                        for (int i = 0; i < 5; i++) {
                            double m = 0.01;
                            double d3 = 10.0D;
                            for (int j = 0; j < 3; j++) {
                                double d0 = item.getEntityWorld().rand.nextGaussian() * m;
                                double d1 = item.getEntityWorld().rand.nextGaussian() * m;
                                double d2 = item.getEntityWorld().rand.nextGaussian() * m;

                                item.getEntityWorld().spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, item.posX + item.getEntityWorld().rand.nextFloat() * item.width * 2.0F - item.width - d0 * d3, item.posY + item.getEntityWorld().rand.nextFloat() * item.height - d1 * d3, item.posZ + item.getEntityWorld().rand.nextFloat() * item.width * 2.0F - item.width - d2 * d3, d0, d1, d2);
                            }
                        }
                        player.playSound(new SoundEvent(new ResourceLocation("block.fire.extinguish")), 1, 10);
                    }
                }
            }
        }
        else{
            TextComponentString text = new TextComponentString(TextFormatting.DARK_BLUE + Reference.WAND_EMPTY);
            player.sendStatusMessage(text,true);
        }

        if(did>0)
        {
            heldItem.setItemDamage(heldItem.getItemDamage() + did);
            if (heldItem.getItemDamage() >= heldItem.getMaxDamage()) {
                int inv = 0;
                switch (hand) {
                    case OFF_HAND:
                        inv = 99;
                        break;
                    case MAIN_HAND:
                        inv = 98;
                        break;
                }
                player.playSound(new SoundEvent(new ResourceLocation("entity.item.break")), 1, 10);
                player.replaceItemInInventory(inv, ItemStack.EMPTY);
            }
        }
    }




        /*public static void craft(EntityPlayer player, EnumHand hand, ArrayList<ItemStack> listIn, ItemStack out){
        //TODO: Try this crafting again once I have a bit more experience
        //TODO: Don't display WAND_EMPTY message on multi craft
        //TODO: Reduce stacksize by amount so it doesn't remove the items that are left over
        //TODO: Find issue with different recipes changing to 1 result
        //TODO: Fix all of the bugs related to the multi crafting (disappearing items,

        List<EntityItem> items = player.getEntityWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(player.posX - 3, player.posY - 3, player.posZ - 3, player.posX + 3, player.posY + 3, player.posZ + 3));
        ItemStack heldItem = player.getHeldItem(hand);


        int newDamage = 0;
        int itemsCount=0;
        int amount = 64;

        StringBuilder textList= new StringBuilder();
        for (ItemStack aListIn : listIn) {
            textList.append(aListIn.getDisplayName()).append(";");
        }

        if ((heldItem.getItemDamage()+(items.size()/listIn.size())) <= heldItem.getMaxDamage()) {
            for (EntityItem item : items) { //Foreach new variable item in list items
                ItemStack stack = item.getItem();

                if (textList.toString().contains(stack.getDisplayName())) { //Check if all items found are part of the input
                    itemsCount++;
                    if (amount > stack.getCount() ) {
                        amount = stack.getCount();
                        System.out.println("Setting amount to "+ amount);
                    }
                    else if(amount!=stack.getCount() )
                    {
                        TextComponentString text = new TextComponentString(TextFormatting.DARK_BLUE + Reference.LEFTOVER_STACK);
                        player.sendStatusMessage(text,true);
                    }


                    if (items.size() > 1) { //Check if stack is larger that 1
                        if (itemsCount < listIn.size()) {
                            stack.setCount(0);  //TODO Fix removing leftover items, perhaps deny crafting if both stacks aren't the same?
                        }
                    }
                }

                if ((!stack.isEmpty()) && (itemsCount == listIn.size())) {
                    ItemStack outCopy = out.copy();
                    outCopy.setCount(amount);
                    item.setItem(outCopy);
                    newDamage += 1;

                    for (int i = 0; i < 5; i++) {
                        double m = 0.01;
                        double d3 = 10.0D;
                        for (int j = 0; j < 3; j++) {
                            double d0 = item.getEntityWorld().rand.nextGaussian() * m;
                            double d1 = item.getEntityWorld().rand.nextGaussian() * m;
                            double d2 = item.getEntityWorld().rand.nextGaussian() * m;

                            item.getEntityWorld().spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, item.posX + item.getEntityWorld().rand.nextFloat() * item.width * 2.0F - item.width - d0 * d3, item.posY + item.getEntityWorld().rand.nextFloat() * item.height - d1 * d3, item.posZ + item.getEntityWorld().rand.nextFloat() * item.width * 2.0F - item.width - d2 * d3, d0, d1, d2);
                        }
                    }
                    player.playSound(new SoundEvent(new ResourceLocation("block.fire.extinguish")), 1, 10);
                }
            }
        }
        else{
            TextComponentString text = new TextComponentString(TextFormatting.DARK_BLUE + Reference.WAND_EMPTY);
            player.sendStatusMessage(text,true);
        }

        if(newDamage>0)
        {
            //heldItem.setItemDamage(heldItem.getItemDamage() + newDamage);
            if (heldItem.getItemDamage() >= heldItem.getMaxDamage()) {
                int inv = 0;
                switch (hand) {
                    case OFF_HAND:
                        inv = 99;
                        break;
                    case MAIN_HAND:
                        inv = 98;
                        break;
                }
                player.playSound(new SoundEvent(new ResourceLocation("entity.item.break")), 1, 10);
                player.replaceItemInInventory(inv, ItemStack.EMPTY);
            }
        }
    }*/
}