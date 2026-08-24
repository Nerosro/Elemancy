package be.nerosro.elemancy.entity;

import be.nerosro.elemancy.ElemancyColors;
import be.nerosro.elemancy.spell.data.OnHitEffect;
import be.nerosro.elemancy.spell.data.ProjectileSpellData;
import be.nerosro.elemancy.spell.data.SpellVisual;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * A mana projectile used by all projectile-type attack spells.
 * Behavior (gravity, color, on-hit effect) is configured per-spell via setSpellData().
 * Visual type is synched to the client for rendering (particle trail, rock, or crescent).
 */
public class ManaBlastProjectile extends ThrowableProjectile {

    private static final EntityDataAccessor<Byte> VISUAL_TYPE =
        SynchedEntityData.defineId(ManaBlastProjectile.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Float> CRESCENT_ANGLE =
        SynchedEntityData.defineId(ManaBlastProjectile.class, EntityDataSerializers.FLOAT);

    private static final int MAX_LIFETIME_TICKS = 100;
    private float damage = 3.0f;
    private double gravity = 0.01;
    private int particleColor = ElemancyColors.BLAST_PARTICLE.rgb();
    private OnHitEffect onHitEffect = OnHitEffect.NONE;

    public ManaBlastProjectile(EntityType<? extends ManaBlastProjectile> type, Level level) {
        super(type, level);
    }

    public ManaBlastProjectile(Level level, LivingEntity owner) {
        super(EntityTypes.MANA_BLAST.get(), level);
        this.setOwner(owner);
        this.setPos(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setSpellData(ProjectileSpellData data) {
        this.gravity = data.gravity();
        this.particleColor = data.particleColor();
        this.onHitEffect = data.onHitEffect();
        this.entityData.set(VISUAL_TYPE, (byte) data.visual().ordinal());
        if (data.visual() == SpellVisual.PROJECTILE_CRESCENT) {
            this.entityData.set(CRESCENT_ANGLE, this.random.nextFloat() * 360f);
        }
    }

    public SpellVisual getVisualType() {
        SpellVisual[] values = SpellVisual.values();
        int ordinal = this.entityData.get(VISUAL_TYPE);
        return values[Math.clamp(ordinal, 0, values.length - 1)];
    }

    public float getCrescentAngle() {
        return this.entityData.get(CRESCENT_ANGLE);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(VISUAL_TYPE, (byte) 0);
        builder.define(CRESCENT_ANGLE, 0f);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide()) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level() instanceof ServerLevel serverLevel) {
            if (result.getEntity() instanceof LivingEntity target) {
                var prevMotion = target.getDeltaMovement();
                target.hurtServer(serverLevel, this.damageSources().thrown(this, this.getOwner()), damage);
                target.setDeltaMovement(prevMotion);
                applyOnHitEffect(target);
            }
        }
    }

    private void applyOnHitEffect(LivingEntity target) {
        switch (onHitEffect) {
            case IGNITE -> target.igniteForSeconds(3);
            case KNOCKBACK -> {
                var direction = target.position().subtract(this.position()).normalize();
                target.knockback(0.6, -direction.x, -direction.z);
            }
            case NONE -> {
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel serverLevel
            && getVisualType() == SpellVisual.PROJECTILE_PARTICLE_TRAIL) {
            DustParticleOptions dust = new DustParticleOptions(particleColor, 0.6f);
            serverLevel.sendParticles(dust, this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
        }
        if (this.tickCount > MAX_LIFETIME_TICKS) {
            this.discard();
        }
    }

    @Override
    protected double getDefaultGravity() {
        return gravity;
    }
}
