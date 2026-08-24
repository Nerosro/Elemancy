package be.nerosro.elemancy.passives;

import be.nerosro.elemancy.Elemancy;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Orchestrator for all always-on passive skill effects from the Elemancy skill tree.
 * Dispatches to specialized handlers; does not contain passive logic itself.
 */
@EventBusSubscriber(modid = Elemancy.MOD_ID)
public final class PassiveEffects {
    private PassiveEffects() {
    }

    private static final int ATTRIBUTE_SYNC_INTERVAL = 20; // once per second

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (!(player instanceof ServerPlayer)) return;

        if (player.tickCount % ATTRIBUTE_SYNC_INTERVAL == 0) {
            AttributePassives.sync(player);
        }

        TickPassives.tick(player);
        SoftGlowPassive.tick(player);
    }

    /**
     * Removes all passive modifiers and cleans up state. Used for dev reset.
     */
    public static void removeAllPassiveModifiers(Player player) {
        AttributePassives.removeAll(player);
    }
}
