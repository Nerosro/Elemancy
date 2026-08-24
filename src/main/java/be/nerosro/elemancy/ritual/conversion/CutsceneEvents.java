package be.nerosro.elemancy.ritual.conversion;

import be.nerosro.elemancy.Elemancy;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Wires anchor-scoped Conversion presentations into the server tick.
 */
@EventBusSubscriber(modid = Elemancy.MOD_ID)
public final class CutsceneEvents {
    private CutsceneEvents() {
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        CutsceneEngine.tick();
    }
}