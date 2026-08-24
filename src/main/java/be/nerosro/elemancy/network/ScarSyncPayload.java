package be.nerosro.elemancy.network;

import be.nerosro.elemancy.Elemancy;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Server-to-client payload that syncs active scar presence, stacks, and timers.
 * Used for HUD icons, Mana Collapse special coloring, and future Mirror UI diagnostics.
 * <p>
 * Bit layout for activeScars:
 * 0 = Arcane Tremor
 * 1 = Spell Drift
 * 2 = Channel Disruption
 * 3 = Mana Burn
 * 4 = Arcane Fatigue
 * 5 = Spell Weakness
 * 6 = Mana Collapse
 */
public record ScarSyncPayload(
    byte activeScars,
    byte manaBurnStacks,
    int manaBurnTicks,
    byte arcaneFatigueStacks,
    int arcaneFatigueTicks,
    byte spellWeaknessStacks,
    int spellWeaknessTicks,
    int arcaneTremorTicks,
    int spellDriftTicks,
    int channelDisruptionTicks,
    int manaCollapseTicks
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ScarSyncPayload> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "scar_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ScarSyncPayload> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.BYTE, ScarSyncPayload::activeScars,
            ByteBufCodecs.BYTE, ScarSyncPayload::manaBurnStacks,
            ByteBufCodecs.VAR_INT, ScarSyncPayload::manaBurnTicks,
            ByteBufCodecs.BYTE, ScarSyncPayload::arcaneFatigueStacks,
            ByteBufCodecs.VAR_INT, ScarSyncPayload::arcaneFatigueTicks,
            ByteBufCodecs.BYTE, ScarSyncPayload::spellWeaknessStacks,
            ByteBufCodecs.VAR_INT, ScarSyncPayload::spellWeaknessTicks,
            ByteBufCodecs.VAR_INT, ScarSyncPayload::arcaneTremorTicks,
            ByteBufCodecs.VAR_INT, ScarSyncPayload::spellDriftTicks,
            ByteBufCodecs.VAR_INT, ScarSyncPayload::channelDisruptionTicks,
            ByteBufCodecs.VAR_INT, ScarSyncPayload::manaCollapseTicks,
            ScarSyncPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

