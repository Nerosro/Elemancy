package be.nerosro.elemancy.items.tools.earth;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.items.ElemancyItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

/**
 * Applies Earth-tool face-aligned excavation after a normal primary block break.
 */
@EventBusSubscriber(modid = Elemancy.MOD_ID)
public final class EarthExcavationEvents {
    private static final Map<UUID, PendingBreak> PENDING_BREAKS = new HashMap<>();
    private static final Set<UUID> SECONDARY_BREAKS = new HashSet<>();

    private EarthExcavationEvents() {
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
            || !EarthExcavationMode.isEnabled(player.getMainHandItem())
            || !EarthTools.isEarthTool(player.getMainHandItem())) {
            return;
        }
        PENDING_BREAKS.put(player.getUUID(), new PendingBreak(event.getPos(), event.getFace()));
    }

    @SubscribeEvent
    public static void onBlockBreak(BreakBlockEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        UUID playerId = player.getUUID();
        if (SECONDARY_BREAKS.contains(playerId)) return;

        PendingBreak pending = PENDING_BREAKS.remove(playerId);
        if (pending == null || !pending.pos().equals(event.getPos()) || !EarthExcavationMode.isEnabled(player.getMainHandItem()))
            return;

        BlockState primaryState = event.getState();
        if (!EarthTools.isEarthTool(player.getMainHandItem()) || !player.getMainHandItem().isCorrectToolForDrops(primaryState))
            return;

        SECONDARY_BREAKS.add(playerId);
        try {
            for (BlockPos target : surroundingPositions(player.getMainHandItem(), event.getPos(), pending.face())) {
                BlockState targetState = player.level().getBlockState(target);
                if (!matchesPrimaryBlock(player.getMainHandItem(), primaryState, targetState)
                    || !player.getMainHandItem().isCorrectToolForDrops(targetState)) {
                    continue;
                }
                player.gameMode.destroyBlock(target);
            }
        } finally {
            SECONDARY_BREAKS.remove(playerId);
        }
    }

    private static boolean matchesPrimaryBlock(ItemStack tool, BlockState primary, BlockState target) {
        return tool.is(ElemancyItems.EARTH_SHOVEL.get())
            && isSurfaceSoil(primary)
            && isSurfaceSoil(target)
            || primary.getBlock() == target.getBlock();
    }

    private static boolean isSurfaceSoil(BlockState state) {
        return state.is(BlockTags.DIRT) || state.is(Blocks.GRASS_BLOCK);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        clear(event.getEntity());
    }

    private static Iterable<BlockPos> surroundingPositions(ItemStack tool, BlockPos origin, Direction face) {
        Set<BlockPos> positions = new HashSet<>();
        for (int first = -1; first <= 1; first++) {
            for (int second = -1; second <= 1; second++) {
                if (first == 0 && second == 0) continue;
                if (tool.is(ElemancyItems.EARTH_SHOVEL.get())) {
                    positions.add(origin.offset(first, 0, second));
                } else {
                    positions.add(switch (face.getAxis()) {
                        case X -> origin.offset(0, first, second);
                        case Y -> origin.offset(first, 0, second);
                        case Z -> origin.offset(first, second, 0);
                    });
                }
            }
        }
        return positions;
    }

    private static void clear(net.minecraft.world.entity.Entity entity) {
        if (entity instanceof ServerPlayer player) {
            PENDING_BREAKS.remove(player.getUUID());
        }
    }

    private record PendingBreak(BlockPos pos, Direction face) {
    }
}