package com.nerosro.elemancy.handlers;

import com.nerosro.elemancy.init.ModBlocks;
import com.nerosro.elemancy.init.ModItems;
import com.nerosro.elemancy.items.ItemWand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static net.minecraft.util.EnumHand.MAIN_HAND;
import static net.minecraft.util.EnumHand.OFF_HAND;

/**
 * Created by Nerosro on 25/01/2018.
 */
public class EnergizeHandler {


    /**
     * Handler to change Items.STICK into ModItems.wand on an offhand click
     *
     */

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void EnergizeWand(PlayerInteractEvent e)
    {
        if(e.getHand()==OFF_HAND) {

            ItemStack heldItem=e.getEntityPlayer().getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
            Integer size = heldItem.getCount();
            //System.out.println(heldItem);

            if(heldItem.isItemEqual(new ItemStack(Items.STICK))){
                if(size==1){
                    e.getEntityLiving().replaceItemInInventory(99,new ItemStack(ModItems.wand));
                }
            }

            //TODO remove lower recipes, these are just for testing and debug code to rapidly convert back
            if(heldItem.isItemEqual(new ItemStack(ModItems.infMetal))){
                e.getEntityLiving().replaceItemInInventory(99,new ItemStack(Items.IRON_INGOT,size));
            }
            if(heldItem.isItemEqual(new ItemStack(ModItems.infIngot))){
                e.getEntityLiving().replaceItemInInventory(99,new ItemStack(Items.GOLD_INGOT,size));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public  void Infuse(PlayerInteractEvent e){

        EntityPlayer player= e.getEntityPlayer();

        EnumHand hand = null;
        if(player.getHeldItemOffhand().getItem().equals(ModItems.wand)){hand=OFF_HAND;}
        if(player.getHeldItemMainhand().getItem().equals(ModItems.wand)){hand=MAIN_HAND;}

        //Basically, somewhat less dirty way to check if you have ModItems.wand in either your offhand or main hand
        if(hand != null){

            ItemWand.craft(player, hand, new ItemStack(Items.IRON_INGOT), new ItemStack(ModItems.infMetal));
            ItemWand.craft(player, hand, new ItemStack(Items.GOLD_INGOT), new ItemStack(ModItems.infIngot));
            ItemWand.craft(player, hand, new ItemStack(Blocks.WOOL), new ItemStack(ModBlocks.infWool));

        }
    }
}
