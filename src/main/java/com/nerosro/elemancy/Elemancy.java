package com.nerosro.elemancy;

import com.nerosro.elemancy.init.ModArmour;
import com.nerosro.elemancy.init.ModBlocks;
import com.nerosro.elemancy.init.ModItems;
import com.nerosro.elemancy.proxy.ClientProxy;
import com.nerosro.elemancy.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by Nerosro on 23/01/2018.
 */
@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, acceptedMinecraftVersions = Reference.ACCEPTED_VERSIONS)

public class Elemancy {
    public static Elemancy instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.SERVER_PROXY)
    public static CommonProxy proxy;
    public static final CreativeTabs CREATIVE_TAB = new ElemancyTab();


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e){
        ModItems.init();
        ModItems.register();
        ModBlocks.init();
        ModBlocks.register();
        ModArmour.init();
        ModArmour.register();

        proxy.preInit();
    }

    @Mod.EventHandler
    public void init (FMLInitializationEvent e){
        System.out.println("I still blame Mow for this!");
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e){
    }
}