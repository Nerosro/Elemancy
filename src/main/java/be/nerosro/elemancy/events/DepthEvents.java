package be.nerosro.elemancy.events;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.mana.depth.ManaDepthValues;
import be.nerosro.soulmark.mana.ManaOverspendEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * Hooks Elemancy into Soulmark's overspend pipeline.
 * Overspend is enabled globally here; cast handlers apply depth/scar/failure consequences.
 */
@EventBusSubscriber(modid = Elemancy.MOD_ID)
public class DepthEvents {

    @SubscribeEvent
    public static void onManaOverspend(ManaOverspendEvent event) {
        if (event.getPlayer().level().isClientSide()) return;
        if (!ManaDepthValues.depthEnabled()) return;
        event.allowOverspend();
    }
}
