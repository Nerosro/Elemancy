package be.nerosro.elemancy.items.tools.airaxe;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.items.ElemancyItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

/**
 * Fells bounded, leaf-qualified trees broken with the Air Axe.
 */
@EventBusSubscriber(modid = Elemancy.MOD_ID)
public final class AirAxeEvents {
    private static final int MAX_TREE_LOGS = 64;
    private static final int MAX_SCAN_DISTANCE = 64;
    private static final int MAX_SCAN_DISTANCE_SQUARED = MAX_SCAN_DISTANCE * MAX_SCAN_DISTANCE;
    private static final Set<UUID> SECONDARY_BREAKS = new HashSet<>();

    private AirAxeEvents() {
    }

    @SubscribeEvent
    public static void onDestroyBlock(BreakBlockEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)
            || !event.getState().is(BlockTags.LOGS)
            || !player.getMainHandItem().is(ElemancyItems.AIR_AXE.get())
            || !player.getMainHandItem().isCorrectToolForDrops(event.getState())) {
            return;
        }

        UUID playerId = player.getUUID();
        if (!SECONDARY_BREAKS.add(playerId)) return;

        try {
            for (BlockPos log : scanTree(player, event.getPos())) {
                if (!log.equals(event.getPos())) {
                    player.gameMode.destroyBlock(log);
                }
            }
        } finally {
            SECONDARY_BREAKS.remove(playerId);
        }
    }

    private static Set<BlockPos> scanTree(ServerPlayer player, BlockPos origin) {
        Set<BlockPos> visited = new HashSet<>();
        Set<BlockPos> logs = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        boolean foundLeaves = false;

        visited.add(origin);
        logs.add(origin);
        queue.add(origin);

        while (!queue.isEmpty()) {
            BlockPos current = queue.remove();

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;

                        BlockPos neighbor = current.offset(x, y, z);
                        if (!visited.add(neighbor)
                            || origin.distSqr(neighbor) > MAX_SCAN_DISTANCE_SQUARED
                            || !player.level().isLoaded(neighbor)) {
                            continue;
                        }

                        BlockState state = player.level().getBlockState(neighbor);
                        if (isNaturalLeaf(state)) {
                            foundLeaves = true;
                        } else if (state.is(BlockTags.LOGS) && logs.size() < MAX_TREE_LOGS) {
                            logs.add(neighbor);
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }

        return foundLeaves ? logs : Set.of();
    }

    private static boolean isNaturalLeaf(BlockState state) {
        return state.getBlock() instanceof LeavesBlock
            && !state.getValue(LeavesBlock.PERSISTENT);
    }
}