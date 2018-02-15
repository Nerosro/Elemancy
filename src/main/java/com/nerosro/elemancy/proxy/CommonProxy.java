package com.nerosro.elemancy.proxy;

import com.nerosro.elemancy.handlers.EnergizeHandler;
import net.minecraftforge.common.MinecraftForge;


/**
 * Created by Nerosro on 23/01/2018.
 */
public class CommonProxy {

    public void preInit(){

    }

    public void init(){
        EnergizeHandler handler=new EnergizeHandler();
        MinecraftForge.EVENT_BUS.register(handler);
    }


}
