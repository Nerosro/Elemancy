package be.nerosro.elemancy.spell.casting;

import java.util.Optional;

import be.nerosro.elemancy.effects.CastEffects;
import be.nerosro.elemancy.mana.depth.CastResolution;
import be.nerosro.elemancy.spell.SpellCastHandler;
import be.nerosro.elemancy.spell.SpellContext;
import be.nerosro.elemancy.spell.SpellDamagePipeline;
import be.nerosro.elemancy.spell.data.BeamSpellData;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Handles casting all beam and hitscan attack spells.
 * Each spell is parameterized via BeamSpellData at registration time.
 * <p>
 * Raycasts from the player's eye position along the look direction,
 * hits the first living entity within range, applies damage + effects,
 * and spawns visual particles based on the beam style.
 */
public final class BeamCaster {
    private BeamCaster() {
    }

    /**
     * Returns a SpellCastHandler configured for the given beam/hitscan spell data.
     */
    public static SpellCastHandler beamHandler(BeamSpellData data) {
        return player -> tryCast(player, data);
    }

    private static boolean tryCast(Player player, BeamSpellData data) {
        SpellContext context = data.toContext();
        Optional<CastResolution> result = SpellCast.cast(player, data.manaCost(), context);
        if (result.isEmpty()) return false;
        if (!result.get().spellResolved()) return true;

        if (player.level() instanceof ServerLevel serverLevel) {
            float damage = data.baseDamage() * SpellDamagePipeline.resolve(player, context);
            Vec3 eyePos = player.getEyePosition();
            Vec3 lookDir = player.getLookAngle();
            Vec3 endPos = eyePos.add(lookDir.scale(data.maxRange()));

            // Block collision — stop ray at first solid block
            BlockHitResult blockHit = serverLevel.clip(new ClipContext(
                eyePos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            if (blockHit.getType() != HitResult.Type.MISS) {
                endPos = blockHit.getLocation();
            }

            // Raycast for entity hit (only up to block wall)
            Optional<EntityHitResult> hit = RaycastUtil.findClosestEntity(player, eyePos, endPos, 0f);
            Vec3 hitPoint = hit.map(EntityHitResult::getLocation).orElse(endPos);

            // Apply damage to hit entity (no vanilla knockback — spells control their own)
            if (hit.isPresent() && hit.get().getEntity() instanceof LivingEntity target) {
                Vec3 prevMotion = target.getDeltaMovement();
                target.hurtServer(serverLevel, player.damageSources().playerAttack(player), damage);
                target.setDeltaMovement(prevMotion);
            }

            // Spawn visual particles
            spawnBeamParticles(serverLevel, player, eyePos, hitPoint, data);
        }

        return true;
    }


    private static void spawnBeamParticles(ServerLevel level, Player player, Vec3 start, Vec3 end, BeamSpellData data) {
        CastEffects.casterBurst(level, player, data.particleColor());

        switch (data.visual()) {
            case BEAM_THIN_LINGER -> spawnThinLingerBeam(level, start, end, data.particleColor());
            case BEAM_WIDE_BURST -> spawnWideBurstBeam(level, start, end, data.particleColor());
            case BEAM_HITSCAN -> spawnHitscanImpact(level, end, data.particleColor());
        }
    }

    /**
     * Light Dart style: thin line of small particles along the ray that linger briefly.
     */
    private static void spawnThinLingerBeam(ServerLevel level, Vec3 start, Vec3 end, int color) {
        final float DUST_SCALE = 0.5f;
        final double STEP_SIZE = 0.5;
        final int PARTICLES_PER_STEP = 1;
        final double SPREAD = 0.0;

        DustParticleOptions dust = new DustParticleOptions(color, DUST_SCALE);
        Vec3 direction = end.subtract(start);
        double length = direction.length();
        Vec3 step = direction.normalize().scale(STEP_SIZE);
        int steps = (int) (length / STEP_SIZE);

        for (int i = 0; i < steps; i++) {
            Vec3 pos = start.add(step.scale(i));
            level.sendParticles(dust, pos.x, pos.y, pos.z, PARTICLES_PER_STEP, SPREAD, SPREAD, SPREAD, 0.0);
        }
    }

    /**
     * Water Jet style: wider beam with more particles, like water from a hose.
     */
    private static void spawnWideBurstBeam(ServerLevel level, Vec3 start, Vec3 end, int color) {
        final float DUST_SCALE = 1.5f;
        final double STEP_SIZE = 0.8;
        final int PARTICLES_PER_STEP = 3;
        final double SPREAD = 0.1;

        DustParticleOptions dust = new DustParticleOptions(color, DUST_SCALE);
        Vec3 direction = end.subtract(start);
        double length = direction.length();
        Vec3 step = direction.normalize().scale(STEP_SIZE);
        int steps = (int) (length / STEP_SIZE);

        for (int i = 0; i < steps; i++) {
            Vec3 pos = start.add(step.scale(i));
            level.sendParticles(dust, pos.x, pos.y, pos.z, PARTICLES_PER_STEP, SPREAD, SPREAD, SPREAD, 0.0);
        }
    }

    /**
     * Shadow Flick style: no beam, just a burst of particles at the impact point.
     */
    private static void spawnHitscanImpact(ServerLevel level, Vec3 hitPoint, int color) {
        final float DUST_SCALE = 1.0f;
        final int PARTICLE_COUNT = 12;
        final double SPREAD = 0.2;
        final double SPEED = 0.02;

        DustParticleOptions dust = new DustParticleOptions(color, DUST_SCALE);
        level.sendParticles(dust, hitPoint.x, hitPoint.y, hitPoint.z, PARTICLE_COUNT, SPREAD, SPREAD, SPREAD, SPEED);
    }
}
