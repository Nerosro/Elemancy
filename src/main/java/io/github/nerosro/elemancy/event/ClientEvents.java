package io.github.nerosro.elemancy.event;

import io.github.nerosro.elemancy.Elemancy;
import io.github.nerosro.elemancy.client.ManaHudOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Created by Nerosro on 18/07/2023.
 */
public class ClientEvents {
    @Mod.EventBusSubscriber(modid = Elemancy.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvent{
        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiOverlaysEvent event){
            //event.registerAbove(VanillaGuiOverlay.HOTBAR.id(),"mana", ManaHudOverlay.HUD_MANA);
            event.registerAboveAll("mana", ManaHudOverlay.HUD_MANA);
        }
    }

}
