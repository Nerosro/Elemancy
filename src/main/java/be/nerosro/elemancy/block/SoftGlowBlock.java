package be.nerosro.elemancy.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Invisible, intangible light block placed by the Soft Glow passive.
 * Self-removes via scheduled tick — survives server crashes because the tick is saved with the chunk.
 */
public class SoftGlowBlock extends Block {
    public static final MapCodec<SoftGlowBlock> CODEC = simpleCodec(SoftGlowBlock::new);
    public static final BooleanProperty ENHANCED = BooleanProperty.create("enhanced");
    public static final int BASE_LIGHT_LEVEL = 8;
    public static final int ENHANCED_LIGHT_LEVEL = 10;
    public static final int BASE_DURATION_TICKS = 20 * 20;
    public static final int ENHANCED_DURATION_TICKS = 45 * 20;

    public SoftGlowBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ENHANCED, false));
    }

    @Override
    public MapCodec<SoftGlowBlock> codec() {
        return CODEC;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide()) {
            level.scheduleTick(pos, this, state.getValue(ENHANCED)
                ? ENHANCED_DURATION_TICKS
                : BASE_DURATION_TICKS);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ENHANCED);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.removeBlock(pos, false);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(3) == 0) {
            level.addParticle(ParticleTypes.END_ROD,
                pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5,
                (random.nextDouble() - 0.5) * 0.04, 0.02, (random.nextDouble() - 0.5) * 0.04);
        }
    }
}
