package be.nerosro.elemancy.items.tools.lightshield;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.effects.ElemancyEffects;
import be.nerosro.elemancy.entity.LightShieldDecoyEntity;
import be.nerosro.elemancy.items.ElemancyItems;
import be.nerosro.elemancy.skilltree.Attachments;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Resolves Light Shield effects for the first eligible block in each guarding cycle.
 */
@EventBusSubscriber(modid = Elemancy.MOD_ID)
public final class LightShieldBlockEvents {

    private LightShieldBlockEvents() {
    }

    @SubscribeEvent
    public static void onShieldBlock(LivingShieldBlockEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
            || !player.getUseItem().is(ElemancyItems.LIGHT_SHIELD.get())
            || event.getBlockedDamage() <= 0.0F
            || !isEligibleDirectAttack(event.getDamageSource())
            || !player.getData(Attachments.LIGHT_SHIELD_STATE).tryUseRaiseEffects()) {
            return;
        }

        LightShieldDecoyEntity decoy = LightShieldDecoyEntity.spawn(player.level(), player, player.getUsedItemHand());
        LightShieldHostileDeception.apply(player.level(), player, decoy);

        boolean invisibilityEarned = LightShieldPerfectBlock.isPerfectBlock(player)
            && LightShieldPerfectBlock.tryStartInvisibilityCooldown(player);
        if (invisibilityEarned) {
            LightShieldConcealment.start(player);
            player.connection.send(new ClientboundSoundPacket(
                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.AMETHYST_BLOCK_RESONATE),
                SoundSource.PLAYERS, player.getX(), player.getY(), player.getZ(), 1.4F, 1.1F,
                player.getRandom().nextLong()));
        }
    }

    // Stop is skipped when vanilla force-stops use (axe disable, slot swap), so reset on every raise instead.
    @SubscribeEvent
    public static void onUseStart(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof ServerPlayer player && event.getItem().is(ElemancyItems.LIGHT_SHIELD.get())) {
            LightShieldConcealment.endOnAction(player);
            player.getData(Attachments.LIGHT_SHIELD_STATE).resetRaise();
        } else if (event.getEntity() instanceof ServerPlayer player) {
            LightShieldConcealment.endOnAction(player);
        }
    }

    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            LightShieldConcealment.endOnAction(player);
        }
    }

    @SubscribeEvent
    public static void onChangeTarget(LivingChangeTargetEvent event) {
        if (event.getEntity() instanceof Mob
            && event.getNewAboutToBeSetTarget() instanceof ServerPlayer player
            && player.hasEffect(ElemancyEffects.LIGHT_SHIELD_CONCEALMENT)) {
            event.setNewAboutToBeSetTarget(null);
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getEntity() instanceof ServerPlayer observer
            && event.getTarget() instanceof ServerPlayer concealed
            && concealed.getEffect(ElemancyEffects.LIGHT_SHIELD_CONCEALMENT) instanceof MobEffectInstance effect) {
            observer.connection.send(new ClientboundUpdateMobEffectPacket(concealed.getId(), effect, false));
        }
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        if (event.getEntity() instanceof ServerPlayer player
            && event.getEffectInstance() instanceof MobEffectInstance effect
            && effect.getEffect().equals(ElemancyEffects.LIGHT_SHIELD_CONCEALMENT)) {
            LightShieldConcealment.syncRemoval(player);
        }
    }

    private static boolean isEligibleDirectAttack(DamageSource damageSource) {
        Entity directEntity = damageSource.getDirectEntity();
        return directEntity instanceof LivingEntity || directEntity instanceof Projectile;
    }

}