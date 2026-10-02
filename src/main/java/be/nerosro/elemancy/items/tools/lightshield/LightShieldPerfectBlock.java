package be.nerosro.elemancy.items.tools.lightshield;

import be.nerosro.elemancy.skilltree.Attachments;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.component.BlocksAttacks;

/**
 * Recognises Light Shield perfect blocks and starts the player's invisibility cooldown.
 */
final class LightShieldPerfectBlock {

    private static final int PERFECT_BLOCK_WINDOW_TICKS = 20;

    private LightShieldPerfectBlock() {
    }

    static boolean isPerfectBlock(ServerPlayer player) {
        BlocksAttacks blocksAttacks = player.getUseItem().get(DataComponents.BLOCKS_ATTACKS);
        return blocksAttacks != null
            && player.getTicksUsingItem() - blocksAttacks.blockDelayTicks() < PERFECT_BLOCK_WINDOW_TICKS;
    }

    /**
     * Starts the cooldown and returns true only when no cooldown is running.
     */
    static boolean tryStartInvisibilityCooldown(ServerPlayer player) {
        return player.getData(Attachments.LIGHT_SHIELD_STATE).tryStartInvisibilityCooldown(currentTick(player));
    }

    private static int currentTick(ServerPlayer player) {
        return player.level().getServer().getTickCount();
    }
}
