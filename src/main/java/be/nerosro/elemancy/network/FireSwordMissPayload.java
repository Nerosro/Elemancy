package be.nerosro.elemancy.network;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.items.ElemancyItems;
import be.nerosro.elemancy.items.tools.firesword.FireSwordHeat;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Client report of a literal main-hand Fire Sword air swing. */
public record FireSwordMissPayload() implements CustomPacketPayload {
    public static final Type<FireSwordMissPayload> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "fire_sword_miss"));

    public static final StreamCodec<ByteBuf, FireSwordMissPayload> STREAM_CODEC = StreamCodec.unit(new FireSwordMissPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(FireSwordMissPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player
                && player.getMainHandItem().is(ElemancyItems.FIRE_SWORD.get())) {
                FireSwordHeat.reset(player.getMainHandItem());
            }
        });
    }
}