package be.nerosro.elemancy.network;

import be.nerosro.elemancy.Elemancy;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Server-to-client synchronization for Elemancy-owned Job Points.
 */
public record JobPointSyncPayload(int availableBalance) implements CustomPacketPayload {

    public static final Type<JobPointSyncPayload> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "job_point_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, JobPointSyncPayload> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            JobPointSyncPayload::availableBalance,
            JobPointSyncPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}