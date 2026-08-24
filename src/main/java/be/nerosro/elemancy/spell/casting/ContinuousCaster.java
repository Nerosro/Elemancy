package be.nerosro.elemancy.spell.casting;

import java.util.List;
import java.util.Optional;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.mana.CastCostPipeline;
import be.nerosro.elemancy.spell.SpellDamagePipeline;
import be.nerosro.elemancy.spell.data.ContinuousSpellData;
import be.nerosro.soulmark.mana.ManaUtil;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Handles the per-tick logic for continuous cast (hold-to-spray) spells.
 * Called from WandItem.onUseTick() each server tick while the player channels.
 */
public final class ContinuousCaster {
    private ContinuousCaster() {
    }

    private static final Identifier CHANNEL_SLOW_ID =
        Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "channel_slow");

    private static final double SPEED_REDUCTION = -0.30; // 30% slower

    /**
     * Active channel data per player — used when Spellwarp overrides the spell shape.
     */
    private static final java.util.WeakHashMap<Player, ContinuousSpellData> ACTIVE_CHANNELS = new java.util.WeakHashMap<>();

    /**
     * Stores the active ContinuousSpellData for a player starting a channel.
     * Called from WandItem.use() when a Spellwarp-resolved continuous cast begins.
     */
    public static void setActiveChannel(Player player, ContinuousSpellData data) {
        ACTIVE_CHANNELS.put(player, data);
    }

    /**
     * Returns the active channel data for a player, falling back to the registry lookup.
     */
    public static ContinuousSpellData getActiveChannel(Player player, Identifier equipped) {
        ContinuousSpellData cached = ACTIVE_CHANNELS.get(player);
        if (cached != null) return cached;
        return ContinuousSpellData.get(equipped);
    }

    /**
     * Clears the active channel data when channeling ends.
     */
    public static void clearActiveChannel(Player player) {
        ACTIVE_CHANNELS.remove(player);
    }

    /**
     * Called every tick while the player is channeling a continuous spell.
     *
     * @param player       the channeling player
     * @param data         the spell configuration
     * @param elapsedTicks how many ticks have elapsed since channel start
     */
    public static void tick(Player player, ContinuousSpellData data, int elapsedTicks) {
        if (player.level().isClientSide()) return;
        if (!(player.level() instanceof ServerLevel serverLevel)) return;

        // Drain mana (always succeeds — depth system handles overspend)
        float adjustedCost = CastCostPipeline.resolve(player, data.manaPerTick(), data.toContext());
        ManaUtil.trySpend(player, adjustedCost);

        // Prevent sprinting
        if (player.isSprinting()) {
            player.setSprinting(false);
        }

        // Apply damage on interval
        if (elapsedTicks > 0 && elapsedTicks % data.damageIntervalTicks() == 0) {
            float damage = data.damagePerHit() * SpellDamagePipeline.resolve(player, data.toContext());

            if (data.isCone()) {
                applyConeDamage(serverLevel, player, data, damage);
            } else {
                applyLineDamage(serverLevel, player, data, damage);
            }
        }

        // Spawn particles every tick
        spawnChannelParticles(serverLevel, player, data);
    }

    /**
     * Applies the movement speed modifier when channeling starts.
     */
    public static void applySlowdown(Player player) {
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;

        // Remove first to avoid stacking
        speed.removeModifier(CHANNEL_SLOW_ID);
        speed.addTransientModifier(new AttributeModifier(
            CHANNEL_SLOW_ID, SPEED_REDUCTION, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    /**
     * Removes the movement speed modifier when channeling ends.
     */
    public static void removeSlowdown(Player player) {
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;
        speed.removeModifier(CHANNEL_SLOW_ID);
    }

    // ── Cone targeting (Fire Blast) ─────────────────────────────────────────

    private static void applyConeDamage(ServerLevel level, Player player, ContinuousSpellData data, float damage) {
        final double PUSH_Y_LIFT = 0.05;

        Vec3 eyePos = player.getEyePosition();
        Vec3 lookDir = player.getLookAngle().normalize();
        double rangeSq = data.range() * data.range();
        double cosHalfAngle = Math.cos(Math.toRadians(data.coneHalfAngleDeg()));

        AABB searchBox = player.getBoundingBox().inflate(data.range());
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, searchBox, entity -> {
            if (entity == player) return false;
            if (!entity.isAlive()) return false;

            Vec3 toEntity = entity.position().add(0, entity.getBbHeight() * 0.5, 0).subtract(eyePos);
            double distSq = toEntity.lengthSqr();
            if (distSq > rangeSq) return false;

            double dot = lookDir.dot(toEntity.normalize());
            return dot >= cosHalfAngle;
        });

        for (LivingEntity target : targets) {
            Vec3 prevMotion = target.getDeltaMovement();
            target.hurtServer(level, player.damageSources().playerAttack(player), damage);
            target.setDeltaMovement(prevMotion);

            if (data.knockbackStrength() > 0) {
                Vec3 pushDir = target.position().subtract(player.position()).normalize();
                target.push(pushDir.x * data.knockbackStrength(), PUSH_Y_LIFT, pushDir.z * data.knockbackStrength());
            }
        }
    }

    // ── Line targeting (Water Jet) ──────────────────────────────────────────

    private static void applyLineDamage(ServerLevel level, Player player, ContinuousSpellData data, float damage) {
        final float RAYCAST_INFLATE_RADIUS = 0.6f;
        final double PUSH_Y_LIFT = 0.1;

        Vec3 eyePos = player.getEyePosition();
        Vec3 lookDir = player.getLookAngle();
        Vec3 endPos = eyePos.add(lookDir.scale(data.range()));

        // Block collision — stop at first solid block
        BlockHitResult blockHit = level.clip(new ClipContext(
            eyePos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if (blockHit.getType() != HitResult.Type.MISS) {
            endPos = blockHit.getLocation();
        }

        // Find closest entity along the ray
        Optional<EntityHitResult> hit = RaycastUtil.findClosestEntity(player, eyePos, endPos, RAYCAST_INFLATE_RADIUS);

        if (hit.isPresent() && hit.get().getEntity() instanceof LivingEntity closestTarget) {
            Vec3 prevMotion = closestTarget.getDeltaMovement();
            closestTarget.hurtServer(level, player.damageSources().playerAttack(player), damage);
            closestTarget.setDeltaMovement(prevMotion);

            if (data.knockbackStrength() > 0) {
                Vec3 pushDir = lookDir.normalize();
                closestTarget.push(
                    pushDir.x * data.knockbackStrength(),
                    PUSH_Y_LIFT,
                    pushDir.z * data.knockbackStrength());
                closestTarget.hurtMarked = true;
            }
        }
    }

    // ── Particles ───────────────────────────────────────────────────────────

    private static void spawnChannelParticles(ServerLevel level, Player player, ContinuousSpellData data) {
        final float DUST_SCALE = 1.0f;
        final int CONE_PARTICLES_PER_TICK = 3;
        final double CONE_SPREAD_XZ = 0.3;
        final double CONE_SPREAD_Y = 0.2;
        final double CONE_POSITION_SPREAD = 0.1;
        final int LINE_PARTICLES_PER_TICK = 4;
        final double LINE_POSITION_SPREAD = 0.05;

        Vec3 eyePos = player.getEyePosition();
        Vec3 lookDir = player.getLookAngle();

        DustParticleOptions particle = new DustParticleOptions(data.particleColor(), DUST_SCALE);

        if (data.isCone()) {
            // Spray particles in a cone pattern
            for (int i = 0; i < CONE_PARTICLES_PER_TICK; i++) {
                double dist = player.getRandom().nextDouble() * data.range();
                double offsetAngle = (player.getRandom().nextDouble() - 0.5) * 2 * Math.toRadians(data.coneHalfAngleDeg());
                double yawOffset = (player.getRandom().nextDouble() - 0.5) * 2 * Math.toRadians(data.coneHalfAngleDeg());

                Vec3 particlePos = eyePos.add(
                    lookDir.x * dist + Math.sin(offsetAngle) * dist * CONE_SPREAD_XZ,
                    lookDir.y * dist + Math.sin(yawOffset) * dist * CONE_SPREAD_Y,
                    lookDir.z * dist + Math.cos(offsetAngle) * dist * CONE_SPREAD_XZ
                );

                level.sendParticles(particle, particlePos.x, particlePos.y, particlePos.z,
                    1, CONE_POSITION_SPREAD, CONE_POSITION_SPREAD, CONE_POSITION_SPREAD, 0.0);
            }
        } else {
            // Stream particles along a line
            double maxDist = data.range();

            // Block collision for particles
            Vec3 endPos = eyePos.add(lookDir.scale(maxDist));
            BlockHitResult blockHit = level.clip(new ClipContext(
                eyePos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            if (blockHit.getType() != HitResult.Type.MISS) {
                maxDist = Math.sqrt(eyePos.distanceToSqr(blockHit.getLocation()));
            }

            for (int i = 0; i < LINE_PARTICLES_PER_TICK; i++) {
                double dist = player.getRandom().nextDouble() * maxDist;
                Vec3 particlePos = eyePos.add(lookDir.scale(dist));

                level.sendParticles(particle, particlePos.x, particlePos.y, particlePos.z,
                    1, LINE_POSITION_SPREAD, LINE_POSITION_SPREAD, LINE_POSITION_SPREAD, 0.0);
            }
        }
    }
}
