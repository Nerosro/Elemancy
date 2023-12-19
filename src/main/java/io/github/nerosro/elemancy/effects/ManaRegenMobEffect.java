package io.github.nerosro.elemancy.effects;

import io.github.nerosro.elemancy.mana.PlayerManaProvider;
import io.github.nerosro.elemancy.networking.ModMessages;
import io.github.nerosro.elemancy.networking.packet.ManaDataSyncS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ManaRegenMobEffect extends MobEffect {

    public ManaRegenMobEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier){
        if(!livingEntity.level().isClientSide()){
            if(livingEntity instanceof Player){

                livingEntity.getCapability(PlayerManaProvider.PLAYER_MANA)
                        .map(playerMana -> {
                            playerMana.regenMana(2);
                            ModMessages.sendToPlayer(
                                    new ManaDataSyncS2CPacket(
                                            playerMana.getMaxMana(),
                                            playerMana.getRegenAmount(),
                                            playerMana.getElement(),
                                            playerMana.getCurrentMana()
                                    ), (ServerPlayer) livingEntity);
                            //return playerMana.regenMana(2);
                            return 0;
                        });
            }
        }
        super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }
}
