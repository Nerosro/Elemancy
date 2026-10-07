package be.nerosro.elemancy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public final class EnrichedFarmlandBlock extends FarmlandBlock {
    public EnrichedFarmlandBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        if (level.getBlockState(pos).is(this)) {
            turnToDirt(null, state, level, pos);
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        BlockState current = level.getBlockState(pos);
        if (current.is(this) && current.getValue(MOISTURE) == 0 && !level.isRainingAt(pos.above())) {
            for (BlockPos waterPos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
                if (current.canBeHydrated(level, pos, level.getFluidState(waterPos), waterPos)) {
                    return;
                }
            }
            turnToDirt(null, current, level, pos);
        }
    }
}