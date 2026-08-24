package be.nerosro.elemancy.mana;

import java.util.WeakHashMap;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.block.ParadoxFlowerBlock;
import be.nerosro.elemancy.mana.depth.DepthTier;
import be.nerosro.elemancy.mana.depth.ManaDepthSystem;
import be.nerosro.elemancy.network.ElemancyNetwork;
import be.nerosro.soulmark.mana.ManaData;
import be.nerosro.soulmark.mana.ManaUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Per-tick mana orchestrator and mana lifecycle events.
 * Wires scar timers, environmental buffs, and stat resolution in the correct order.
 */
@EventBusSubscriber(modid = Elemancy.MOD_ID)
public class ManaEvents {

    // WeakHashMap: entries are auto-removed when players disconnect (GC'd).
    // Identity-based key lookup is fine — ServerPlayer instances are stable per session.
    private static final WeakHashMap<Player, Byte> lastSentBitfield = new WeakHashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) return;
        Player player = event.getEntity();

        // 1. Decrement scar timers
        ManaDepthSystem.tickScars(player);
        // 2. Apply environmental buffs (flower proximity → mob effect)
        ParadoxFlowerBlock.applyAuraIfNearby(player);
        // 3. Compose all modifier sources and write final mana stats
        ManaStatResolver.resolve(player);

        // Sync scar state to client only when it changes.
        if (player instanceof ServerPlayer serverPlayer) {
            byte current = ManaDepthSystem.buildScarBitfield(player);
            Byte last = lastSentBitfield.get(player);
            if (last == null || last != current) {
                lastSentBitfield.put(player, current);
                ElemancyNetwork.syncScars(serverPlayer);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        // Transfer only death-persistent scars (collapse, weakness, channel disruption).
        CompoundTag scars = ManaDepthSystem.copyPersistentScarData(event.getOriginal());
        ManaDepthSystem.loadScarData(event.getEntity(), scars);

        // If the player died at Depth 4 or has Mana Collapse, they respawn with 0 mana.
        ManaData originalMana = ManaUtil.getMana(event.getOriginal());
        if (originalMana.isInitialized()) {
            DepthTier deathDepth = DepthTier.fromMana(originalMana.getCurrentMana());
            boolean hasManaCollapse = ManaDepthSystem.hasManaCollapse(event.getEntity());
            if (deathDepth == DepthTier.DEPTH_4 || hasManaCollapse) {
                ManaData newMana = ManaUtil.getMana(event.getEntity());
                newMana.setCurrentMana(0f);
            }
        }
    }

    /**
     * Enforce 0 mana on respawn for Mana Collapse players.
     * Runs at LOW priority to execute after Soulmark's default respawn logic.
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!ManaDepthSystem.hasManaCollapse(player)) return;

        ManaData mana = ManaUtil.getMana(player);
        if (mana.isInitialized()) {
            mana.setCurrentMana(0f);
        }
    }
}