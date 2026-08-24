package be.nerosro.elemancy.particle;

import org.jetbrains.annotations.NotNull;

import be.nerosro.elemancy.ElemancyColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

/**
 * Custom mana spiral particle — elongated, glowing streaks that spiral upward.
 * Used around saplings being mutated by the Paradox Flower.
 * <p>
 * The particle orbits around a center point as it rises, creating a helix effect.
 * Multiple particles spawned in sequence form visible "lines" of energy.
 */
public class ManaSpiralParticle extends SingleQuadParticle {

    private static final int LIGHT_LEVEL_FULL_BRIGHTNESS = 0xF000F0;
    private static final float FADE_START = 0.7f;

    private final double centerX;
    private final double centerZ;
    private final float orbitRadius;
    private final float orbitSpeed;
    private final float initialAngle;

    protected ManaSpiralParticle(ClientLevel level, double x, double y, double z,
                                 double centerX, double centerZ,
                                 float radius, float speed, float angle,
                                 SpriteSet sprites) {
        super(level, x, y, z, sprites.get(level.getRandom()));
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.orbitRadius = radius;
        this.orbitSpeed = speed;
        this.initialAngle = angle;

        // Lifetime: long enough to travel ~1.5 blocks upward
        this.lifetime = 25 + random.nextInt(10);

        // No gravity — rises smoothly
        this.gravity = 0f;

        // Upward drift speed
        this.yd = 0.04 + random.nextDouble() * 0.02;
        this.xd = 0;
        this.zd = 0;

        // Particle size — large enough to be clearly visible
        this.quadSize = 0.15f + random.nextFloat() * 0.08f;

        // Mana cyan color with slight per-particle brightness variation
        float tint = 0.85f + random.nextFloat() * 0.15f;
        this.rCol = ElemancyColors.MANA.red() * tint;
        this.gCol = ElemancyColors.MANA.green() * tint;
        this.bCol = ElemancyColors.MANA.blue() * tint;

        // Start semi-transparent
        this.alpha = 0.8f;

        // No physics — purely visual
        this.hasPhysics = false;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        // Spiral motion — orbit around center as we rise
        float progress = (float) this.age / (float) this.lifetime;
        float angle = initialAngle + age * orbitSpeed;

        // Tighten the radius slightly as it rises (converging spiral)
        float currentRadius = orbitRadius * (1.0f - progress * 0.3f);

        double targetX = centerX + Math.cos(angle) * currentRadius;
        double targetZ = centerZ + Math.sin(angle) * currentRadius;

        // Smooth movement toward orbit position
        this.xd = (targetX - this.x) * 0.3;
        this.zd = (targetZ - this.z) * 0.3;

        // Rise
        this.move(this.xd, this.yd, this.zd);

        // Fade out in the last 30% of life
        if (progress > FADE_START) {
            this.alpha = 0.8f * (1.0f - (progress - FADE_START) / (1.0f - FADE_START));
        }

        // Slight shrink over time
        this.quadSize *= 0.98f;
    }

    @Override
    protected @NotNull SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Override
    public int getLightCoords(float partialTick) {
        // Self-illuminating — always bright
        return LIGHT_LEVEL_FULL_BRIGHTNESS;
    }

    /**
     * Factory for creating mana spiral particles from the particle engine.
     */
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed,
                                       @NotNull RandomSource random) {
            // xSpeed/zSpeed encode the center position offset
            // ySpeed encodes the initial angle
            double centerX = x + xSpeed;
            double centerZ = z + zSpeed;
            float radius = 0.35f + random.nextFloat() * 0.15f;
            float speed = 0.15f + random.nextFloat() * 0.08f;
            float angle = (float) ySpeed;

            return new ManaSpiralParticle(
                level, x, y, z, centerX, centerZ, radius, speed, angle, sprites);
        }
    }
}



