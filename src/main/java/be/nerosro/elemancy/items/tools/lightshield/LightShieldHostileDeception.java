package be.nerosro.elemancy.items.tools.lightshield;

import be.nerosro.elemancy.entity.LightShieldDecoyEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

/**
 * Applies the Light Shield's one-time hostile target redirection when a decoy is created.
 */
final class LightShieldHostileDeception {

    private static final double GUARANTEED_REDIRECT_RANGE = 7.0D;
    private static final double MAX_REDIRECT_RANGE = GUARANTEED_REDIRECT_RANGE * 2;
    private static final float FAR_REDIRECT_CHANCE = 0.7F;

    private LightShieldHostileDeception() {
    }

    static void apply(ServerLevel level, Player player, LightShieldDecoyEntity decoy) {
        double guaranteedRangeSquared = GUARANTEED_REDIRECT_RANGE * GUARANTEED_REDIRECT_RANGE;
        double maxRangeSquared = MAX_REDIRECT_RANGE * MAX_REDIRECT_RANGE;
        AABB searchBox = decoy.getBoundingBox().inflate(MAX_REDIRECT_RANGE);

        for (Mob mob : level.getEntitiesOfClass(Mob.class, searchBox, mob -> isEligible(mob, player, level))) {
            double distanceSquared = mob.distanceToSqr(decoy);
            if (distanceSquared <= guaranteedRangeSquared) {
                mob.setTarget(decoy);
            } else if (distanceSquared <= maxRangeSquared
                && mob.getTarget() == player
                && mob.hasLineOfSight(decoy)
                && level.getRandom().nextFloat() < FAR_REDIRECT_CHANCE) {
                mob.setTarget(decoy);
            }
        }
    }

    private static boolean isEligible(Mob mob, Player player, ServerLevel level) {
        if (!mob.isAlive()) {
            return false;
        }
        if (mob instanceof NeutralMob neutralMob) {
            return neutralMob.isAngryAt(player, level);
        }
        return mob instanceof Enemy;
    }
}