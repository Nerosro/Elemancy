package io.github.nerosro.elemancy.effects;

import io.github.nerosro.elemancy.Elemancy;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Elemancy.MOD_ID);

    public static final RegistryObject<MobEffect> MANA_REGEN = MOB_EFFECTS.register("mana_regen",
            () -> new ManaRegenMobEffect(MobEffectCategory.BENEFICIAL, 3402751));
    public static void register(IEventBus eventBus){
        MOB_EFFECTS.register(eventBus);
    }
}
