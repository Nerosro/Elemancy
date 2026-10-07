package be.nerosro.elemancy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import be.nerosro.elemancy.block.EnrichedFarmlandEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.PitcherCropBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(PitcherCropBlock.class)
public class PitcherGrowthMixin {
    @WrapOperation(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
    private int elemancy$boostPitcher(RandomSource extraRandom, int bound, Operation<Integer> original,
                                      BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int roll = original.call(extraRandom, bound);
        return roll != 0 && bound > 1 && EnrichedFarmlandEvents.isHydrated(level, pos)
            && extraRandom.nextDouble() < EnrichedFarmlandEvents.GROWTH_BONUS / (bound - 1) ? 0 : roll;
    }
}