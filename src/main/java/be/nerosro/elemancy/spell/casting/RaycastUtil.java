package be.nerosro.elemancy.spell.casting;

import java.util.Optional;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Shared ray-entity intersection logic for spell targeting.
 */
public final class RaycastUtil {
    private RaycastUtil() {
    }

    /**
     * Finds the closest living entity along a ray, excluding the caster.
     *
     * @param caster        the player firing the ray (excluded from results)
     * @param start         ray origin (typically eye position)
     * @param end           ray endpoint (typically after block-clip)
     * @param inflateRadius extra radius to expand each candidate's bounding box
     *                      (0 = vanilla precision, 0.6 = generous spray)
     * @return the closest hit entity and hit location, or empty
     */
    public static Optional<EntityHitResult> findClosestEntity(Player caster, Vec3 start, Vec3 end, float inflateRadius) {
        Vec3 direction = end.subtract(start);
        double range = direction.length();

        AABB searchBox = caster.getBoundingBox().expandTowards(direction).inflate(1.0);

        Entity closest = null;
        double closestDist = range;
        Vec3 closestHit = null;

        for (Entity entity : caster.level().getEntities(caster, searchBox,
            e -> e instanceof LivingEntity && e != caster && e.isAlive() && e.isPickable())) {
            AABB entityBox = entity.getBoundingBox().inflate(inflateRadius);
            Optional<Vec3> intersection = entityBox.clip(start, end);
            if (intersection.isPresent()) {
                double dist = start.distanceTo(intersection.get());
                if (dist < closestDist) {
                    closest = entity;
                    closestDist = dist;
                    closestHit = intersection.get();
                }
            }
        }

        if (closest != null) {
            return Optional.of(new EntityHitResult(closest, closestHit));
        }
        return Optional.empty();
    }
}
