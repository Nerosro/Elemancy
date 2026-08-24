package be.nerosro.elemancy.jobpoint;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.network.ElemancyNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Synchronizes Elemancy Job Points when player client state is established.
 */
@EventBusSubscriber(modid = Elemancy.MOD_ID)
public final class JobPointEvents {

    private JobPointEvents() {
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ElemancyNetwork.syncJobPoints(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ElemancyNetwork.syncJobPoints(player);
        }
    }
}