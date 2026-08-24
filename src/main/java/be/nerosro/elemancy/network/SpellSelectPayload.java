package be.nerosro.elemancy.network;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.skilltree.EquippedSpellUtil;
import be.nerosro.soulmark.skilltree.SkillTreeUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client → Server: player selected a spell from the radial menu.
 * Server validates the node is unlocked before equipping.
 */
public record SpellSelectPayload(String spellId) implements CustomPacketPayload {

    public static final Type<SpellSelectPayload> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "spell_select"));

    public static final StreamCodec<ByteBuf, SpellSelectPayload> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SpellSelectPayload::spellId,
            SpellSelectPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SpellSelectPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            Identifier nodeId;
            try {
                nodeId = Identifier.parse(payload.spellId());
            } catch (Exception e) {
                Elemancy.LOGGER.warn("Player {} sent invalid spell ID: '{}'",
                    player.getGameProfile().name(), payload.spellId());
                return;
            }

            // Validate: the node must exist and be unlocked
            if (!SkillTreeUtil.hasNode(player, nodeId)) {
                Elemancy.LOGGER.warn("Player {} tried to equip spell {} which is not unlocked",
                    player.getGameProfile().name(), nodeId);
                return;
            }

            EquippedSpellUtil.setEquippedSpell(player, nodeId);
        });
    }
}

