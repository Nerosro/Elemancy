package be.nerosro.elemancy.items.tools.lightshield;

import be.nerosro.elemancy.effects.ElemancyEffects;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;

public final class LightShieldConcealment {

    private LightShieldConcealment() {

    }

    public static void start(ServerPlayer player) {
        MobEffectInstance effect = new MobEffectInstance(ElemancyEffects.LIGHT_SHIELD_CONCEALMENT, 60, 0, true, false, false);
        player.addEffect(effect);
        player.level().getChunkSource().chunkMap.sendToTrackingPlayers(player,
            new ClientboundUpdateMobEffectPacket(player.getId(), effect, false));
    }

    public static void endOnAction(ServerPlayer player) {
        if (player.removeEffect(ElemancyEffects.LIGHT_SHIELD_CONCEALMENT)) {
            syncRemoval(player);
        }
    }

    static void syncRemoval(ServerPlayer player) {
        player.level().getChunkSource().chunkMap.sendToTrackingPlayers(player,
            new ClientboundRemoveMobEffectPacket(player.getId(), ElemancyEffects.LIGHT_SHIELD_CONCEALMENT));
    }
}