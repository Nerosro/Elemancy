package io.github.nerosro.elemancy.event;

import io.github.nerosro.elemancy.Elemancy;
import io.github.nerosro.elemancy.client.ClientManaData;
import io.github.nerosro.elemancy.mana.PlayerMana;
import io.github.nerosro.elemancy.mana.PlayerManaProvider;
import io.github.nerosro.elemancy.networking.ModMessages;
import io.github.nerosro.elemancy.networking.packet.ManaDataSyncS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;


/**
 * Created by Nerosro on 18/07/2023.
 */
@Mod.EventBusSubscriber(modid = Elemancy.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PlayerManaProvider.PLAYER_MANA).isPresent()) {
                event.addCapability(new ResourceLocation(Elemancy.MOD_ID, "properties"), new PlayerManaProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        //if(event.isWasDeath()) { //Only does on death, not on leaving End
        //TODO remove debug comments here
        //System.out.println("Death happened, trying to revive capabilities");
        event.getOriginal().reviveCaps();

        //System.out.println("Original Max_mana: " +event.getOriginal().getCapability(PlayerManaProvider.PLAYER_MANA).map(playerMana -> playerMana.getMaxMana()).get());
        //System.out.println("New Entity Max_mana: " +event.getEntity().getCapability(PlayerManaProvider.PLAYER_MANA).map(playerMana -> playerMana.getMaxMana()).get());

        LazyOptional<PlayerMana> newPlayerCap = event.getEntity().getCapability(PlayerManaProvider.PLAYER_MANA);
        LazyOptional<PlayerMana> oldPlayerCap = event.getOriginal().getCapability(PlayerManaProvider.PLAYER_MANA);

        newPlayerCap.ifPresent(newCap -> {
            //System.out.println(newCap);
            oldPlayerCap.ifPresent(oldCap -> {
                //System.out.println("oldCap before copy: " + oldCap);
                newCap.copyFrom(oldCap);

                if (event.isWasDeath()) {
                    newCap.lowerManaOnDeath();
                }
                //System.out.println("newCap  after copy: " + newCap);
            });
        });
        event.getOriginal().invalidateCaps();
       /*}
        else {f
            System.out.println("Other event:" + event);
        }*/
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide()) {
            if (event.getEntity() instanceof ServerPlayer player) {
                player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {

                    ModMessages.sendToPlayer(
                            new ManaDataSyncS2CPacket(
                                    mana.getMaxMana(),
                                    mana.getRegenAmount(),
                                    mana.getCurrentMana()
                            ), player);
                });
            }
        }
    }


    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side == LogicalSide.SERVER) {
            if (event.player instanceof ServerPlayer player) {
                event.player.getCapability(PlayerManaProvider.PLAYER_MANA).ifPresent(mana -> {
                    mana.regenMana(1);

                    ModMessages.sendToPlayer(
                            new ManaDataSyncS2CPacket(
                                    mana.getMaxMana(),
                                    mana.getRegenAmount(),
                                    mana.getCurrentMana()
                            ), player);
                });
            }
        }
    }
}
