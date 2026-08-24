package be.nerosro.elemancy.events;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.block.ParadoxFlowerBlock;
import be.nerosro.elemancy.items.TomeItem;
import be.nerosro.elemancy.tome.DiscoveryNodes;
import be.nerosro.elemancy.util.BeehiveProximityHelper;
import be.nerosro.soulmark.network.ClientSkillTreeData;
import be.nerosro.soulmark.network.SoulmarkNetwork;
import be.nerosro.soulmark.skilltree.SkillTreeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * Handles Tome discovery interactions - right-clicking blocks with Tome to unlock entries.
 * Extensible pattern: add new discoveries to the DISCOVERIES list.
 */
@EventBusSubscriber(modid = Elemancy.MOD_ID)
public class TomeDiscoveryEvents {

    /**
     * Tracks players who just had a discovery this tick.
     * Used to suppress Tome screen opening when discovery message shows.
     */
    private static final Map<UUID, Long> RECENT_DISCOVERIES = new WeakHashMap<>();

    /**
     * Registry of all discoverable blocks.
     * Add new discoveries here - no need to write new methods.
     */
    private static final List<Discovery> DISCOVERIES = List.of(
        new Discovery(
            (_, _, state) -> state.getBlock() instanceof ParadoxFlowerBlock,
            DiscoveryNodes.PARADOX_FLOWER,
            "message.elemancy.discovery.paradox_flower"
        ),
        new Discovery(
            (level, pos, state) -> state.is(BlockTags.BEEHIVES) &&
                BeehiveProximityHelper.isNearParadoxFlower(level, pos, 5, 3),
            DiscoveryNodes.INFUSED_BEEHIVE,
            "message.elemancy.discovery.infused_beehive"
        )
    );

    @SubscribeEvent
    public static void onTomeBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack held = player.getItemInHand(event.getHand());

        // Check if holding Tome
        if (!(held.getItem() instanceof TomeItem)) return;

        // Check if player owns this Tome
        if (!TomeItem.isOwner(held, player)) return;

        BlockPos pos = event.getPos();
        BlockState state = event.getLevel().getBlockState(pos);

        // Check all registered discoveries
        for (Discovery discovery : DISCOVERIES) {
            if (discovery.matches(event.getLevel(), pos, state)) {
                if (event.getLevel().isClientSide()) {
                    // Client side: only suppress screen if node not yet unlocked
                    if (!ClientSkillTreeData.isUnlocked(discovery.nodeId())) {
                        RECENT_DISCOVERIES.put(player.getUUID(), player.level().getGameTime());
                    }
                } else {
                    // Server side: do the actual unlock
                    if (unlockDiscovery(player, discovery.nodeId())) {
                        player.level().playSound(null, player.blockPosition(),
                            SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0f, 1.0f);
                        player.sendSystemMessage(Component.translatable(discovery.messageKey()));
                        RECENT_DISCOVERIES.put(player.getUUID(), player.level().getGameTime());
                    }
                }
                break; // Only trigger one discovery per interaction
            }
        }
    }

    /**
     * Checks if this player had a discovery within the last tick.
     * Used by TomeItem to suppress screen opening after discoveries.
     */
    public static boolean hadRecentDiscovery(Player player) {
        Long discoveryTick = RECENT_DISCOVERIES.get(player.getUUID());
        if (discoveryTick == null) return false;

        long currentTick = player.level().getGameTime();
        boolean isRecent = currentTick - discoveryTick <= 1;

        // Clean up stale entries
        if (!isRecent) {
            RECENT_DISCOVERIES.remove(player.getUUID());
        }

        return isRecent;
    }

    /**
     * Attempts to unlock a discovery node for the player.
     * Returns true only on first unlock (prevents duplicate messages).
     */
    private static boolean unlockDiscovery(Player player, Identifier nodeId) {
        boolean unlocked = SkillTreeUtil.getTreeData(player).unlock(nodeId);
        if (unlocked && player instanceof ServerPlayer serverPlayer) {
            SoulmarkNetwork.syncSkillTree(serverPlayer);
        }
        return unlocked;
    }

    /**
     * Defines a discoverable block with its unlock node and message.
     *
     * @param condition  Predicate to check if this block matches (receives Level, BlockPos, and BlockState)
     * @param nodeId     The discovery node to unlock
     * @param messageKey The translation key for the discovery message
     */
    private record Discovery(
        DiscoveryCondition condition,
        Identifier nodeId,
        String messageKey
    ) {
        boolean matches(Level level, BlockPos pos, BlockState state) {
            return condition.test(level, pos, state);
        }
    }

    /**
     * Functional interface for discovery conditions that need access to Level, BlockPos, and BlockState.
     */
    @FunctionalInterface
    private interface DiscoveryCondition {
        boolean test(Level level, BlockPos pos, BlockState state);
    }
}
