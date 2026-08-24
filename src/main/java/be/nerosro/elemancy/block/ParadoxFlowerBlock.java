package be.nerosro.elemancy.block;

import java.util.function.BiConsumer;

import be.nerosro.elemancy.effects.ElemancyEffects;
import be.nerosro.elemancy.particle.ElemancyParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The Paradox Flower — a wild magical flower found in Flower Forests.
 * <p>
 * Behaviors:
 * - Within 10 blocks: doubles mana regeneration for nearby players (via mob effect applied in ManaEvents).
 * - Within 5 blocks: slowly mutates nearby Acacia Saplings into Ashen Saplings.
 * - Cannot be harvested intact (always destroyed on break, no drops).
 */
public class ParadoxFlowerBlock extends FlowerBlock {

    /**
     * Range within which the flower affects nearby blocks (sapling mutation, beehive
     * mana-reactivity, and any future effects). Matches BeehiveProximityHelper's range
     * used for discovery and propolis harvesting.
     */
    private static final int EFFECT_RANGE_H = 5;
    private static final int EFFECT_RANGE_V = 3;

    /**
     * Horizontal range for the mana regen aura.
     */
    private static final int AURA_RANGE = 10;
    /**
     * Vertical range for the mana regen aura.
     */
    private static final int AURA_VERTICAL_RANGE = 4;
    /**
     * Duration of the applied mob effect in ticks. Short window — the effect is refreshed every
     * AURA_SCAN_INTERVAL ticks while in range and expires naturally on leave.
     */
    private static final int AURA_EFFECT_DURATION = 40;
    /**
     * Only scan for nearby flowers every N ticks to avoid per-tick block scanning.
     */
    private static final int AURA_SCAN_INTERVAL = 20;

    /**
     * Chance per random tick that a nearby acacia sapling mutates (roughly 1 in 8).
     */
    private static final int MUTATION_CHANCE = 8;

    // ── Particle tuning constants ──
    private static final int PARTICLE_MIN_COUNT = 2;
    private static final int PARTICLE_COUNT_VARIANCE = 2;
    private static final float PARTICLE_RADIUS_MIN = 0.3f;
    private static final float PARTICLE_RADIUS_VARIANCE = 0.2f;
    private static final double PARTICLE_Y_OFFSET_MIN = 0.1;
    private static final double PARTICLE_Y_OFFSET_VARIANCE = 0.3;

    private static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0);

    public ParadoxFlowerBlock(Properties properties) {
        super(ElemancyEffects.MANA_REGEN_BOOST, 5.0F, properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /**
     * Client-side animation tick — runs every frame. Spawns constant mana spiral particles
     * around any Acacia Saplings within mutation range and any beehives within mana-reactive
     * range. This gives a persistent visual cue that the flower is affecting nearby blocks.
     */
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        forEachSaplingInRange(level, pos, (saplingPos, _) ->
            spawnClientSpiralParticles(level, saplingPos, random));
        forEachBeehiveInRange(level, pos, (beehivePos, _) ->
            spawnClientSpiralParticles(level, beehivePos, random));
    }

    /**
     * Spawns mana spiral particles around a sapling on the client side.
     * Runs every tick for consistent, visible particle streams.
     */
    private void spawnClientSpiralParticles(Level level, BlockPos pos, RandomSource random) {
        int count = PARTICLE_MIN_COUNT + random.nextInt(PARTICLE_COUNT_VARIANCE);
        for (int i = 0; i < count; i++) {
            float angle = random.nextFloat() * (float) (Math.PI * 2);
            float radius = PARTICLE_RADIUS_MIN + random.nextFloat() * PARTICLE_RADIUS_VARIANCE;

            double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
            double y = pos.getY() + PARTICLE_Y_OFFSET_MIN + random.nextDouble() * PARTICLE_Y_OFFSET_VARIANCE;
            double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;

            double centerOffsetX = (pos.getX() + 0.5) - x;
            double centerOffsetZ = (pos.getZ() + 0.5) - z;

            level.addParticle(ElemancyParticles.MANA_SPIRAL.get(),
                x, y, z,
                centerOffsetX, angle, centerOffsetZ);
        }
    }

    /**
     * Random tick: attempts sapling mutation only.
     * Mana regen aura is handled by the proximity check in ManaEvents.
     */
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        trySaplingMutation(level, pos, random);
    }

    /**
     * Attempts to mutate a nearby Acacia Sapling into an Ashen Sapling.
     */
    private void trySaplingMutation(ServerLevel level, BlockPos pos, RandomSource random) {
        boolean mutated = false;

        for (BlockPos saplingPos : BlockPos.betweenClosed(
            pos.offset(-EFFECT_RANGE_H, -EFFECT_RANGE_V, -EFFECT_RANGE_H),
            pos.offset(EFFECT_RANGE_H, EFFECT_RANGE_V, EFFECT_RANGE_H))) {

            if (level.getBlockState(saplingPos).is(Blocks.ACACIA_SAPLING)) {
                if (!mutated && random.nextInt(MUTATION_CHANCE) == 0) {
                    level.setBlock(saplingPos.immutable(), ElemancyBlocks.ASHEN_SAPLING.get().defaultBlockState(), Block.UPDATE_ALL);
                    mutated = true;
                }
            }
        }
    }

    /**
     * Iterates all Acacia Saplings within mutation range of this flower.
     */
    private void forEachSaplingInRange(Level level, BlockPos pos, BiConsumer<BlockPos, BlockState> action) {
        for (BlockPos target : BlockPos.betweenClosed(
            pos.offset(-EFFECT_RANGE_H, -EFFECT_RANGE_V, -EFFECT_RANGE_H),
            pos.offset(EFFECT_RANGE_H, EFFECT_RANGE_V, EFFECT_RANGE_H))) {

            BlockState targetState = level.getBlockState(target);
            if (targetState.is(Blocks.ACACIA_SAPLING)) {
                action.accept(target, targetState);
            }
        }
    }

    /**
     * Iterates all beehives within mana-reactive range of this flower.
     */
    private void forEachBeehiveInRange(Level level, BlockPos pos, BiConsumer<BlockPos, BlockState> action) {
        for (BlockPos target : BlockPos.betweenClosed(
            pos.offset(-EFFECT_RANGE_H, -EFFECT_RANGE_V, -EFFECT_RANGE_H),
            pos.offset(EFFECT_RANGE_H, EFFECT_RANGE_V, EFFECT_RANGE_H))) {

            BlockState targetState = level.getBlockState(target);
            if (targetState.is(BlockTags.BEEHIVES)) {
                action.accept(target, targetState);
            }
        }
    }

    /**
     * Checks whether the player is within aura range of any Paradox Flower and applies the
     * mana regen boost mob effect if so. Throttled to scan once per second.
     * Called from the tick orchestrator — the flower owns its own detection logic.
     */
    public static void applyAuraIfNearby(Player player) {
        if (player.tickCount % AURA_SCAN_INTERVAL != 0) return;

        BlockPos playerPos = player.blockPosition();

        for (BlockPos pos : BlockPos.betweenClosed(
            playerPos.offset(-AURA_RANGE, -AURA_VERTICAL_RANGE, -AURA_RANGE),
            playerPos.offset(AURA_RANGE, AURA_VERTICAL_RANGE, AURA_RANGE))) {
            if (player.level().getBlockState(pos).getBlock() instanceof ParadoxFlowerBlock) {
                player.addEffect(new MobEffectInstance(
                    ElemancyEffects.MANA_REGEN_BOOST,
                    AURA_EFFECT_DURATION,
                    0,
                    true,   // ambient — suppresses blinking/countdown icon (beacon-style)
                    false,  // no particles
                    true)); // show icon
                return;
            }
        }
    }
}









