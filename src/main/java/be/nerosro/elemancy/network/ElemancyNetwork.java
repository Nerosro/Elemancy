package be.nerosro.elemancy.network;

import be.nerosro.elemancy.jobpoint.JobPointUtil;
import be.nerosro.elemancy.mana.depth.ManaDepthSystem;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Handles registration and sending of all Elemancy network payloads.
 */
public final class ElemancyNetwork {

    private ElemancyNetwork() {
    }

    /**
     * Registers all payload types. Called from the mod bus.
     */
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
            ScarSyncPayload.TYPE,
            ScarSyncPayload.STREAM_CODEC,
            (payload, _) -> ClientScarData.update(payload)
        );

        registrar.playToClient(
            JobPointSyncPayload.TYPE,
            JobPointSyncPayload.STREAM_CODEC,
            (payload, _) -> ClientJobPointData.update(payload)
        );

        registrar.playToClient(
            RitualCameraLockPayload.TYPE,
            RitualCameraLockPayload.STREAM_CODEC,
            (payload, _) -> RitualCameraLockState.setLocked(payload.locked())
        );

        registrar.playToServer(
            SpellSelectPayload.TYPE,
            SpellSelectPayload.STREAM_CODEC,
            SpellSelectPayload::handle
        );

        registrar.playToServer(
            DarkBucketScrollPayload.TYPE,
            DarkBucketScrollPayload.STREAM_CODEC,
            DarkBucketScrollPayload::handle
        );

        registrar.playToServer(
            FireSwordMissPayload.TYPE,
            FireSwordMissPayload.STREAM_CODEC,
            FireSwordMissPayload::handle
        );
    }

    /**
     * Builds scar data and sends it to the player's client.
     */
    public static void syncScars(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, ManaDepthSystem.buildScarPayload(player));
    }

    /**
     * Sends the player's Elemancy-owned Job Point balance to their client.
     */
    public static void syncJobPoints(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new JobPointSyncPayload(JobPointUtil.getAvailableBalance(player)));
    }

    /**
     * Locks or restores the initiating player's local camera for an Attunement Ritual.
     */
    public static void setRitualCameraLocked(ServerPlayer player, boolean locked) {
        PacketDistributor.sendToPlayer(player, new RitualCameraLockPayload(locked));
    }
}



