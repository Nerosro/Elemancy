package be.nerosro.elemancy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import be.nerosro.elemancy.block.ElemancyBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.PitcherCropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Level.class)
public abstract class LevelCropRemovalMixin {
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("HEAD"))
    private void elemancy$captureCropRemoval(BlockPos pos, BlockState replacement, int flags, int limit,
                                             CallbackInfoReturnable<Boolean> result, @Share("removedCrop") LocalRef<BlockState> removedCrop) {
        if (!((Object) this instanceof ServerLevel level)
            || !(replacement.isAir() || !replacement.getFluidState().isEmpty())) {
            return;
        }

        BlockState previous = level.getBlockState(pos);
        if ((previous.getBlock() instanceof CropBlock || previous.getBlock() instanceof StemBlock
            || previous.getBlock() instanceof PitcherCropBlock || previous.is(BlockTags.CROPS)
            || previous.is(Blocks.TORCHFLOWER) || previous.is(Blocks.ATTACHED_MELON_STEM)
            || previous.is(Blocks.ATTACHED_PUMPKIN_STEM))
            && level.getBlockState(pos.below()).is(ElemancyBlocks.ENRICHED_FARMLAND.get())) {
            removedCrop.set(previous);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("RETURN"))
    private void elemancy$scheduleSoilReset(BlockPos pos, BlockState replacement, int flags, int limit,
                                            CallbackInfoReturnable<Boolean> result, @Share("removedCrop") LocalRef<BlockState> removedCrop) {
        if (!((Object) this instanceof ServerLevel level) || !Boolean.TRUE.equals(result.getReturnValue())
            || removedCrop.get() == null) {
            return;
        }

        BlockState current = level.getBlockState(pos);
        BlockPos soilPos = pos.below();
        if ((current.isAir() || !current.getFluidState().isEmpty())
            && level.getBlockState(soilPos).is(ElemancyBlocks.ENRICHED_FARMLAND.get())) {
            level.scheduleTick(soilPos, ElemancyBlocks.ENRICHED_FARMLAND.get(), 1);
        }
    }
}