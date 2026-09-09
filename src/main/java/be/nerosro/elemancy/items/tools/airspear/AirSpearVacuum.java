package be.nerosro.elemancy.items.tools.airspear;

import java.util.List;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Applies the stationary Air Spear brace's non-damaging pull.
 */
final class AirSpearVacuum {
    static final double RANGE = 7.0D;
    private static final double BURST_RANGE = 4.5D;
    static final double HALF_ANGLE_DEGREES = 25.0D;
    static final double PREFERRED_DISTANCE = 3.5D;
    static final int PULSE_INTERVAL_TICKS = 4;
    static final double PULL_KNOCKBACK = 0.16D;
    private static final float BURST_KNOCKBACK = 1.65F;
    private static final double BURST_LIFT = 0.38D;
    private static final int PARTICLE_COLOR = 0xB8F7FF;
    private static final int CONE_PARTICLE_COUNT = 4;
    private static final int TARGET_PARTICLE_COUNT = 3;

    private AirSpearVacuum() {
    }

    static void pulse(ServerLevel level, Player player) {
        Vec3 forward = player.getLookAngle().multiply(1.0D, 0.0D, 1.0D).normalize();
        Vec3 right = forward.cross(new Vec3(0.0D, 1.0D, 0.0D));
        Vec3 particlePosition = player.position().add(forward.scale(2.0D)).add(0.0D, 0.4D, 0.0D);
        level.sendParticles(
            new DustParticleOptions(PARTICLE_COLOR, 0.75F),
            particlePosition.x,
            particlePosition.y,
            particlePosition.z,
            CONE_PARTICLE_COUNT,
            Math.abs(right.x) * 0.5D,
            0.08D,
            Math.abs(right.z) * 0.5D,
            0.0D
        );

        for (LivingEntity target : findTargets(level, player, RANGE)) {
            Vec3 horizontalOffset = target.position().subtract(player.position()).multiply(1.0D, 0.0D, 1.0D);
            if (horizontalOffset.lengthSqr() <= PREFERRED_DISTANCE * PREFERRED_DISTANCE) {
                continue;
            }

            target.knockback(PULL_KNOCKBACK, horizontalOffset.x, horizontalOffset.z);
            level.sendParticles(
                new DustParticleOptions(PARTICLE_COLOR, 0.5F),
                target.getX(),
                target.getY() + target.getBbHeight() * 0.5D,
                target.getZ(),
                TARGET_PARTICLE_COUNT,
                0.2D,
                0.3D,
                0.2D,
                0.0D
            );
        }
    }

    static void burst(ServerLevel level, Player player) {
        for (LivingEntity target : findTargets(level, player, BURST_RANGE)) {
            Vec3 pushDirection = target.position().subtract(player.position()).multiply(1.0D, 0.0D, 1.0D).normalize();
            target.knockback(BURST_KNOCKBACK, -pushDirection.x, -pushDirection.z);
            target.setDeltaMovement(target.getDeltaMovement().add(0.0D, BURST_LIFT, 0.0D));
            level.sendParticles(
                new DustParticleOptions(PARTICLE_COLOR, 0.9F),
                target.getX(),
                target.getY() + target.getBbHeight() * 0.5D,
                target.getZ(),
                TARGET_PARTICLE_COUNT * 2,
                0.3D,
                0.4D,
                0.3D,
                0.0D
            );
        }
    }

    private static List<LivingEntity> findTargets(ServerLevel level, Player player, double range) {
        Vec3 eyePosition = player.getEyePosition();
        Vec3 lookDirection = player.getLookAngle().normalize();
        double rangeSquared = range * range;
        double minimumDotProduct = Math.cos(Math.toRadians(HALF_ANGLE_DEGREES));
        AABB searchBox = player.getBoundingBox().inflate(range);
        return level.getEntitiesOfClass(LivingEntity.class, searchBox, target -> isValidTarget(
            player, target, eyePosition, lookDirection, rangeSquared, minimumDotProduct));
    }

    private static boolean isValidTarget(
        Player player,
        LivingEntity target,
        Vec3 eyePosition,
        Vec3 lookDirection,
        double rangeSquared,
        double minimumDotProduct
    ) {
        if (target == player || !target.isAlive() || target.isInvulnerable() || !target.canBeHitByProjectile()) {
            return false;
        }
        if (target instanceof Player otherPlayer && !player.canHarmPlayer(otherPlayer)) {
            return false;
        }
        if (!player.hasLineOfSight(target)) {
            return false;
        }

        Vec3 targetCenter = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
        Vec3 toTarget = targetCenter.subtract(eyePosition);
        double distanceSquared = toTarget.lengthSqr();
        return distanceSquared <= rangeSquared && lookDirection.dot(toTarget.normalize()) >= minimumDotProduct;
    }
}