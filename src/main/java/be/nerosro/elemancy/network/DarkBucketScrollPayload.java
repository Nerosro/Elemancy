package be.nerosro.elemancy.network;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.items.ElemancyItems;
import be.nerosro.elemancy.items.tools.darkbucket.DarkBucketContents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client request to change the selected compartment of a main-hand Dark Bucket.
 */
public record DarkBucketScrollPayload(boolean forward) implements CustomPacketPayload {
    public static final Type<DarkBucketScrollPayload> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "dark_bucket_scroll"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DarkBucketScrollPayload> STREAM_CODEC =
        StreamCodec.composite(ByteBufCodecs.BOOL, DarkBucketScrollPayload::forward, DarkBucketScrollPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DarkBucketScrollPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)
                || !player.isShiftKeyDown()
                || player.isUsingItem()
                || !player.getMainHandItem().is(ElemancyItems.DARK_BUCKET.get())) {
                return;
            }

            DarkBucketContents.cycleSelectedCompartment(player.getMainHandItem(), payload.forward() ? 1 : -1);
        });
    }
}