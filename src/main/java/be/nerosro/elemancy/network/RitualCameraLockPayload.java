package be.nerosro.elemancy.network;

import be.nerosro.elemancy.Elemancy;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Server command to enforce or restore the local camera during an Attunement Ritual.
 */
public record RitualCameraLockPayload(boolean locked) implements CustomPacketPayload {

    public static final Type<RitualCameraLockPayload> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "ritual_camera_lock"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RitualCameraLockPayload> STREAM_CODEC =
        StreamCodec.composite(ByteBufCodecs.BOOL, RitualCameraLockPayload::locked, RitualCameraLockPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}