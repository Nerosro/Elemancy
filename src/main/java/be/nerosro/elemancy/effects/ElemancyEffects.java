package be.nerosro.elemancy.effects;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.ElemancyColors;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers all custom mob effects for Elemancy.
 */
public class ElemancyEffects {

    private static final DeferredRegister<MobEffect> MOB_EFFECTS =
        DeferredRegister.create(Registries.MOB_EFFECT, Elemancy.MOD_ID);

    /**
     * Mana Regen Boost — applied while near a Paradox Flower.
     * Doubles effective mana regeneration rate.
     */
    public static final Holder<MobEffect> MANA_REGEN_BOOST = MOB_EFFECTS.register("mana_regen_boost",
        () -> new MobEffect(MobEffectCategory.BENEFICIAL, ElemancyColors.MANA.argb()) {
        });

    public static void register(IEventBus modEventBus) {
        MOB_EFFECTS.register(modEventBus);
    }
}
