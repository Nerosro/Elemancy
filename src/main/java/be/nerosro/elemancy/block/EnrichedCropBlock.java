package be.nerosro.elemancy.block;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public final class EnrichedCropBlock extends CropBlock {
    private final Supplier<? extends ItemLike> plantingItem;

    public EnrichedCropBlock(BlockBehaviour.Properties properties, Supplier<? extends ItemLike> plantingItem) {
        super(properties);
        this.plantingItem = plantingItem;
    }

    @Override
    protected boolean mayPlaceOn(BlockState soil, BlockGetter level, BlockPos pos) {
        return soil.is(ElemancyBlocks.ENRICHED_FARMLAND.get());
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return mayPlaceOn(level.getBlockState(pos.below()), level, pos.below()) && super.canSurvive(state, level, pos);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return plantingItem.get();
    }
}