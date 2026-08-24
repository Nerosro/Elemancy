package be.nerosro.elemancy.entity;

import be.nerosro.soulmark.element.Element;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * Visual-only, element-colored lightning with vanilla-style sky-to-ground geometry.
 */
public final class RitualLightningEntity extends Entity {

    private static final int LIFETIME_TICKS = 12;
    private static final EntityDataAccessor<Integer> COLOR =
        SynchedEntityData.defineId(RitualLightningEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Long> SEED =
        SynchedEntityData.defineId(RitualLightningEntity.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Boolean> DARK =
        SynchedEntityData.defineId(RitualLightningEntity.class, EntityDataSerializers.BOOLEAN);

    public RitualLightningEntity(EntityType<? extends RitualLightningEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
    }

    public static void spawn(ServerLevel level, Vec3 strikePosition, Element element) {
        RitualLightningEntity lightning = new RitualLightningEntity(EntityTypes.RITUAL_LIGHTNING.get(), level);
        lightning.setPos(strikePosition.x, strikePosition.y, strikePosition.z);
        lightning.entityData.set(COLOR, RitualLightningColors.forElement(element));
        lightning.entityData.set(SEED, level.getRandom().nextLong());
        lightning.entityData.set(DARK, RitualLightningColors.isDark(element));
        level.addFreshEntity(lightning);
        level.playSound(null, strikePosition.x, strikePosition.y, strikePosition.z,
            SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 10000.0f,
            0.8f + level.getRandom().nextFloat() * 0.2f);
    }

    public int getColor() {
        return entityData.get(COLOR);
    }

    public long getSeed() {
        return entityData.get(SEED);
    }

    public boolean isDark() {
        return entityData.get(DARK);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(COLOR, 0xFFFFFFFF);
        builder.define(SEED, 0L);
        builder.define(DARK, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide() && tickCount >= LIFETIME_TICKS) {
            discard();
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
    }
}