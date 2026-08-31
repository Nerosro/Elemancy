package be.nerosro.elemancy.items.tools.firestriker;

import be.nerosro.elemancy.particle.ElemancyParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

public final class FireStrikerEffects {
    private static final int PARTICLE_INTERVAL_TICKS = 10;
    private static final int PARTICLE_COUNT = 2;

    private FireStrikerEffects() {
    }

    public static void spawnStokedParticles(ServerLevel level, BlockPos pos) {
        if (Math.floorMod(level.getGameTime() + pos.asLong(), PARTICLE_INTERVAL_TICKS) != 0) {
            return;
        }

        RandomSource random = level.getRandom();
        for (int particle = 0; particle < PARTICLE_COUNT; particle++) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            double y = pos.getY() + 0.65 + random.nextDouble() * 0.2;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
            double xSpeed = (random.nextDouble() - 0.5) * 0.008;
            double ySpeed = 0.015 + random.nextDouble() * 0.01;
            double zSpeed = (random.nextDouble() - 0.5) * 0.008;

            level.sendParticles(ElemancyParticles.FIRE_SPIRAL.get(),
                x, y, z, 0, xSpeed, ySpeed, zSpeed, 1.0);
        }
    }
}