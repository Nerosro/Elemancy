package be.nerosro.elemancy.particle;

import java.util.function.Supplier;

import be.nerosro.elemancy.Elemancy;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for all Elemancy particle types.
 */
public class ElemancyParticles {

    public static final DeferredRegister<net.minecraft.core.particles.ParticleType<?>> PARTICLES =
        DeferredRegister.create(Registries.PARTICLE_TYPE, Elemancy.MOD_ID);

    /**
     * Mana Spiral — a streaky, elongated particle used for mana absorption effects.
     * Spirals upward with a teal/cyan glow.
     */
    public static final Supplier<SimpleParticleType> MANA_SPIRAL =
        PARTICLES.register("mana_spiral", () -> new SimpleParticleType(false));

    public static final Supplier<SimpleParticleType> FIRE_SPIRAL =
        PARTICLES.register("fire_spiral", () -> new SimpleParticleType(false));

    public static void register(IEventBus modEventBus) {
        PARTICLES.register(modEventBus);
    }
}

