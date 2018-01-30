package com.nerosro.elemancy.blocks;

import com.nerosro.elemancy.Elemancy;
import com.nerosro.elemancy.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

/**
 * Created by Nerosro on 28/01/2018.
 */
public class BlockInfWool extends Block {

    public BlockInfWool() {
        super(Material.CARPET);
        setUnlocalizedName(Reference.ElemBlocks.INFWOOL.getUnlocName());
        setRegistryName(Reference.ElemBlocks.INFWOOL.getRegName());
        setHardness(0.8F);
        setCreativeTab(Elemancy.CREATIVE_TAB);
    }
}
