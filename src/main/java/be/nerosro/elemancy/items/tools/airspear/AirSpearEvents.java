package be.nerosro.elemancy.items.tools.airspear;

import be.nerosro.elemancy.Elemancy;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Drives Air Spear stance effects that vanilla kinetic weapons bypass at item tick time.
 */
@EventBusSubscriber(modid = Elemancy.MOD_ID)
final class AirSpearEvents {
    private AirSpearEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }
        if (player.isUsingItem() && player.getUseItem().getItem() instanceof AirSpearItem) {
            AirSpearItem.tickUse(player);
        } else if (AirSpearStance.is(player, AirSpearStance.Type.BRACE_RELEASED)) {
            AirSpearVacuum.burst((net.minecraft.server.level.ServerLevel) player.level(), player);
            AirSpearItem.clearState(player);
        } else {
            AirSpearItem.clearState(player);
        }
    }
}