package com.nerosro.elemancy.proxy;

import com.nerosro.elemancy.init.ModArmour;
import com.nerosro.elemancy.init.ModBlocks;
import com.nerosro.elemancy.init.ModItems;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Nerosro on 23/01/2018.
 */
public class ClientProxy extends CommonProxy{

    @Override
    @SideOnly(Side.CLIENT)
    public void preInit(){
        ModItems.registerRenders();
        ModBlocks.registerRenders();
        ModArmour.registerRenders();
    }
}
