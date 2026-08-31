package be.nerosro.elemancy.particle;

import be.nerosro.soulmark.element.SoulmarkElements;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

/**
 * A slow-rising ember emitted by stoked heat sources.
 */
public class FireSpiralParticle extends SingleQuadParticle {
    private static final int LIGHT_LEVEL_FULL_BRIGHTNESS = 0xF000F0;
    private static final float FADE_START = 0.6f;
    private static final float TARGET_RED = 1.0f;
    private static final float TARGET_GREEN = 0.72f;
    private static final float TARGET_BLUE = 0.12f;

    private final float startRed;
    private final float startGreen;
    private final float startBlue;
    private final float initialSize;
    private final float flickerPhase;

    protected FireSpiralParticle(
        ClientLevel level,
        double x,
        double y,
        double z,
        double xSpeed,
        double ySpeed,
        double zSpeed,
        int color,
        SpriteSet sprites
    ) {
        super(level, x, y, z, sprites.get(level.getRandom()));
        this.lifetime = 36 + random.nextInt(20);
        this.gravity = 0.0f;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.quadSize = 0.13f + random.nextFloat() * 0.07f;
        this.initialSize = this.quadSize;
        this.startRed = ((color >> 16) & 0xFF) / 255.0f;
        this.startGreen = ((color >> 8) & 0xFF) / 255.0f;
        this.startBlue = (color & 0xFF) / 255.0f;
        this.rCol = this.startRed;
        this.gCol = this.startGreen;
        this.bCol = this.startBlue;
        this.alpha = 0.85f;
        this.flickerPhase = random.nextFloat() * Mth.TWO_PI;
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

        this.xd += (random.nextDouble() - 0.5) * 0.0015;
        this.zd += (random.nextDouble() - 0.5) * 0.0015;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.96;
        this.yd *= 0.99;
        this.zd *= 0.96;

        float progress = (float) this.age / this.lifetime;
        this.rCol = Mth.lerp(progress, this.startRed, TARGET_RED);
        this.gCol = Mth.lerp(progress, this.startGreen, TARGET_GREEN);
        this.bCol = Mth.lerp(progress, this.startBlue, TARGET_BLUE);

        float fade = progress > FADE_START
            ? 1.0f - (progress - FADE_START) / (1.0f - FADE_START)
            : 1.0f;
        float flicker = 0.82f + 0.18f * Mth.sin(this.age * 1.7f + this.flickerPhase);
        this.alpha = 0.85f * fade * flicker;
        this.quadSize = this.initialSize * (1.0f - progress * 0.45f);
    }

    @Override
    protected @NotNull SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Override
    public int getLightCoords(float partialTick) {
        return LIGHT_LEVEL_FULL_BRIGHTNESS;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private final int color;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
            this.color = SoulmarkElements.FIRE.get().rgb();
        }

        @Override
        public Particle createParticle(
            @NotNull SimpleParticleType type,
            @NotNull ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed,
            @NotNull RandomSource random
        ) {
            return new FireSpiralParticle(
                level, x, y, z, xSpeed, ySpeed, zSpeed, this.color, this.sprites);
        }
    }
}