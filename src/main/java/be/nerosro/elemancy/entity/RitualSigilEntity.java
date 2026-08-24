package be.nerosro.elemancy.entity;

import java.util.List;

import be.nerosro.elemancy.ritual.shared.Timings;
import be.nerosro.soulmark.element.Element;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * Visual-only ritual pentagram spanning the four capstones and standing tile.
 */
public final class RitualSigilEntity extends Entity {

    private static final int COLOR_TRANSITION_TICKS = 10;
    private static final int DARK_SIGIL_COLOR = 0xFF8A3AA3;

    private static final EntityDataAccessor<BlockPos> VERTEX_0 =
        SynchedEntityData.defineId(RitualSigilEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<BlockPos> VERTEX_1 =
        SynchedEntityData.defineId(RitualSigilEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<BlockPos> VERTEX_2 =
        SynchedEntityData.defineId(RitualSigilEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<BlockPos> VERTEX_3 =
        SynchedEntityData.defineId(RitualSigilEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<BlockPos> VERTEX_4 =
        SynchedEntityData.defineId(RitualSigilEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Integer> COLOR =
        SynchedEntityData.defineId(RitualSigilEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TARGET_COLOR =
        SynchedEntityData.defineId(RitualSigilEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COLOR_TRANSITION_START_TICK =
        SynchedEntityData.defineId(RitualSigilEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FADE_START_TICK =
        SynchedEntityData.defineId(RitualSigilEntity.class, EntityDataSerializers.INT);

    public RitualSigilEntity(EntityType<? extends RitualSigilEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
    }

    public static RitualSigilEntity spawn(ServerLevel level, List<BlockPos> vertices, Element element) {
        if (vertices.size() != 5) {
            throw new IllegalArgumentException("Ritual sigil requires exactly five vertices.");
        }

        RitualSigilEntity sigil = new RitualSigilEntity(EntityTypes.RITUAL_SIGIL.get(), level);
        Vec3 origin = Vec3.atCenterOf(vertices.getFirst());
        sigil.setPos(origin.x, origin.y, origin.z);
        sigil.setVertices(vertices);
        int color = displayColor(element);
        sigil.entityData.set(COLOR, color);
        sigil.entityData.set(TARGET_COLOR, color);
        level.addFreshEntity(sigil);
        return sigil;
    }

    public void startFadeOut() {
        if (entityData.get(FADE_START_TICK) < 0) {
            entityData.set(FADE_START_TICK, tickCount);
        }
    }

    /**
     * Smoothly changes the sigil's element colour without restarting its draw animation.
     */
    public void changeElement(Element element) {
        entityData.set(COLOR, getColor(0.0f));
        entityData.set(TARGET_COLOR, displayColor(element));
        entityData.set(COLOR_TRANSITION_START_TICK, tickCount);
    }

    public BlockPos getVertex(int index) {
        return switch (index) {
            case 0 -> entityData.get(VERTEX_0);
            case 1 -> entityData.get(VERTEX_1);
            case 2 -> entityData.get(VERTEX_2);
            case 3 -> entityData.get(VERTEX_3);
            case 4 -> entityData.get(VERTEX_4);
            default -> throw new IllegalArgumentException("Invalid ritual sigil vertex index: " + index);
        };
    }

    public int getColor(float partialTicks) {
        int transitionStartTick = entityData.get(COLOR_TRANSITION_START_TICK);
        if (transitionStartTick < 0) return entityData.get(COLOR);

        float progress = Math.min(1.0f, (tickCount - transitionStartTick + partialTicks) / COLOR_TRANSITION_TICKS);
        return lerpColor(entityData.get(COLOR), entityData.get(TARGET_COLOR), progress);
    }

    public float getDrawProgress(float partialTicks) {
        return Math.min(1.0f, (tickCount + partialTicks) / Timings.SIGIL_DRAW_TICKS);
    }

    public float getOpacity(float partialTicks) {
        int fadeStartTick = entityData.get(FADE_START_TICK);
        if (fadeStartTick < 0) return 1.0f;
        return Math.max(0.0f, 1.0f - (tickCount - fadeStartTick + partialTicks) / Timings.SIGIL_FADE_TICKS);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(VERTEX_0, BlockPos.ZERO);
        builder.define(VERTEX_1, BlockPos.ZERO);
        builder.define(VERTEX_2, BlockPos.ZERO);
        builder.define(VERTEX_3, BlockPos.ZERO);
        builder.define(VERTEX_4, BlockPos.ZERO);
        builder.define(COLOR, 0xFFFFFFFF);
        builder.define(TARGET_COLOR, 0xFFFFFFFF);
        builder.define(COLOR_TRANSITION_START_TICK, -1);
        builder.define(FADE_START_TICK, -1);
    }

    @Override
    public void tick() {
        super.tick();
        int fadeStartTick = entityData.get(FADE_START_TICK);
        if (!level().isClientSide() && fadeStartTick >= 0 && tickCount - fadeStartTick >= Timings.SIGIL_FADE_TICKS) {
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

    private void setVertices(List<BlockPos> vertices) {
        entityData.set(VERTEX_0, vertices.get(0));
        entityData.set(VERTEX_1, vertices.get(1));
        entityData.set(VERTEX_2, vertices.get(2));
        entityData.set(VERTEX_3, vertices.get(3));
        entityData.set(VERTEX_4, vertices.get(4));
    }

    private static int displayColor(Element element) {
        return RitualLightningColors.isDark(element) ? DARK_SIGIL_COLOR : RitualLightningColors.forElement(element);
    }

    private static int lerpColor(int from, int to, float progress) {
        int alpha = lerpChannel(from >>> 24, to >>> 24, progress);
        int red = lerpChannel(from >> 16 & 0xFF, to >> 16 & 0xFF, progress);
        int green = lerpChannel(from >> 8 & 0xFF, to >> 8 & 0xFF, progress);
        int blue = lerpChannel(from & 0xFF, to & 0xFF, progress);
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    private static int lerpChannel(int from, int to, float progress) {
        return Math.round(from + (to - from) * progress);
    }
}