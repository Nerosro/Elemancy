package com.nerosro.elemancy.mixin;

import be.nerosro.elemancy.effects.ElemancyEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LightShieldConcealmentMixin {

    @Inject(method = "updateInvisibilityStatus", at = @At("TAIL"))
    private void elemancy$applyShieldConcealment(CallbackInfo callback) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.hasEffect(ElemancyEffects.LIGHT_SHIELD_CONCEALMENT)) {
            entity.setInvisible(true);
        }
    }
}