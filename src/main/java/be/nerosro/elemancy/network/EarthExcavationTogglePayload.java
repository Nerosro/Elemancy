package be.nerosro.elemancy.network;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.items.tools.earth.EarthExcavationMode;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client request to toggle the held Earth tool's session-only excavation mode.
 */
public record EarthExcavationTogglePayload() implements CustomPacketPayload {
    public static final Type<EarthExcavationTogglePayload> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "earth_excavation_toggle"));

    public static final StreamCodec<ByteBuf, EarthExcavationTogglePayload> STREAM_CODEC =
        StreamCodec.unit(new EarthExcavationTogglePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(EarthExcavationTogglePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                EarthExcavationMode.toggle(player.getMainHandItem());
            }
        });
    }
}