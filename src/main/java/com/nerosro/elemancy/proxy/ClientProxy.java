package com.nerosro.elemancy.proxy;

import com.nerosro.elemancy.handlers.EnergizeHandler;
import com.nerosro.elemancy.init.ModItems;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by Nerosro on 23/01/2018.
 */
public class ClientProxy implements CommonProxy{

    @Override
    public void init() {
        ModItems.registerRenders();

        EnergizeHandler handler=new EnergizeHandler();
        MinecraftForge.EVENT_BUS.register(handler);
    }
}
