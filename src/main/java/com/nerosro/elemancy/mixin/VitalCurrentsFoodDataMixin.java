package com.nerosro.elemancy.mixin;

import be.nerosro.elemancy.passives.VitalCurrentsState;
import be.nerosro.elemancy.skilltree.Attachments;
import be.nerosro.elemancy.skilltree.SkillTreeEntries;
import be.nerosro.soulmark.attunement.AttunementUtil;
import be.nerosro.soulmark.element.SoulmarkElements;
import be.nerosro.soulmark.skilltree.SkillTreeUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Suppresses exhaustion for an occasional Water-attuned hunger-regeneration pulse. */
@Mixin(FoodData.class)
public abstract class VitalCurrentsFoodDataMixin {
    @Unique
    private static final float TRIGGER_CHANCE = 0.20F;

    @Unique
    private static final long COOLDOWN_TICKS = 20L * 60L;

    @Unique
    private float elemancy$healthBeforeHungerRegen;

    @Inject(
        method = "tick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;heal(F)V", ordinal = 1)
    )
    private void elemancy$recordHealthBeforeHungerRegen(ServerPlayer player, CallbackInfo callback) {
        elemancy$healthBeforeHungerRegen = player.getHealth();
    }

    @Redirect(
        method = "tick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V", ordinal = 1)
    )
    private void elemancy$maybeSuppressHungerRegenExhaustion(FoodData foodData, float exhaustion, ServerPlayer player) {
        if (!elemancy$shouldSuppressExhaustion(player)) {
            foodData.addExhaustion(exhaustion);
        }
    }

    @Unique
    private boolean elemancy$shouldSuppressExhaustion(ServerPlayer player) {
        if (player.getHealth() <= elemancy$healthBeforeHungerRegen
            || !SkillTreeUtil.hasNode(player, SkillTreeEntries.VITAL_CURRENTS_ID)
            || !AttunementUtil.isAttunedTo(player, SoulmarkElements.WATER.get())) {
            return false;
        }

        VitalCurrentsState state = player.getData(Attachments.VITAL_CURRENTS_STATE);
        long gameTime = player.level().getGameTime();
        if (!state.isReady(gameTime) || player.getRandom().nextFloat() >= TRIGGER_CHANCE) {
            return false;
        }

        state.trigger(gameTime, COOLDOWN_TICKS);
        return true;
    }
}