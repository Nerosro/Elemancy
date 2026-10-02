package be.nerosro.elemancy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import be.nerosro.elemancy.effects.ElemancyEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

@Mixin(LivingEntity.class)
public abstract class LightShieldConcealmentMixin {

    @Inject(method = "updateInvisibilityStatus", at = @At("TAIL"))
    private void elemancy$applyShieldConcealment(CallbackInfo callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.hasEffect(ElemancyEffects.LIGHT_SHIELD_CONCEALMENT)) {
            entity.setInvisible(true);
        }
    }

    @Inject(method = "canBeSeenAsEnemy", at = @At("HEAD"), cancellable = true)
    private void elemancy$hideConcealedPlayerFromMobs(CallbackInfoReturnable<Boolean> callback) {
        if ((LivingEntity) (Object) this instanceof Player player && player.hasEffect(ElemancyEffects.LIGHT_SHIELD_CONCEALMENT)) {
            callback.setReturnValue(false);
        }
    }
}