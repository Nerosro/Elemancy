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
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

/**
 * Created by Nerosro on 23/01/2018.
 */
public class ItemWand extends Item {

    //Setting item as item/handheld makes it look like a tool/stick being held
    //TODO make item take damage
    public ItemWand(){
        setUnlocalizedName(Reference.ElemItems.WAND.getUnlocName());
        setRegistryName(Reference.ElemItems.WAND.getRegName());
        setCreativeTab(Elemancy.CREATIVE_TAB);
        setMaxStackSize(1);
        setMaxDamage(10);
    }

    public static boolean craft(EntityPlayer player, EnumHand hand, ItemStack in, ItemStack out) {
        List<EntityItem> items = player.getEntityWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(player.posX - 3, player.posY - 3, player.posZ - 3, player.posX + 3, player.posY + 3, player.posZ + 3));

        boolean did = false;
        for(EntityItem item : items) {
            ItemStack stack = item.getItem();
            System.out.println(stack);
            if (!stack.isEmpty()) {
                if (ItemStack.areItemsEqual(stack, in)) {
                    ItemStack outCopy = out.copy();
                    outCopy.setCount(stack.getCount());
                    item.setItem(outCopy);
                    did = true;

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
                }
            }
        }
        if(did) {
            player.getHeldItem(hand).setItemDamage(player.getHeldItem(hand).getItemDamage()+1);
            if(player.getHeldItem(hand).getItemDamage()==9){
                
            }
        }
        return did;
    }
}
