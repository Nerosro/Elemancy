package com.nerosro.elemancy.mixin;

import be.nerosro.elemancy.items.tools.firestriker.FireStrikerState;
import be.nerosro.elemancy.items.tools.firestriker.FireStrikerEffects;
import be.nerosro.elemancy.items.tools.firestriker.StokableHeatSource;
import be.nerosro.elemancy.skilltree.Attachments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin implements StokableHeatSource {
    @Shadow
    protected NonNullList<ItemStack> items;

    @Shadow
    @Final
    private RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickCheck;

    @Shadow
    private int litTimeRemaining;

    @Shadow
    private int cookingTimer;

    @Shadow
    private int cookingTotalTime;

    @Override
    public boolean elemancy$tryStoke(ServerLevel level, Player player) {
        BlockEntity blockEntity = (BlockEntity) (Object) this;
        FireStrikerState strikerState = blockEntity.getData(Attachments.FIRE_STRIKER_STATE);
        if (strikerState.isStoked()
            || !blockEntity.getBlockState().getValue(AbstractFurnaceBlock.LIT)
            || litTimeRemaining <= 1
            || !elemancy$hasProcessableRecipe(level, (AbstractFurnaceBlockEntity) blockEntity)) {
            return false;
        }

        strikerState.stoke();
        blockEntity.setChanged();
        return true;
    }

    @Unique
    private boolean elemancy$hasProcessableRecipe(ServerLevel level, AbstractFurnaceBlockEntity entity) {
        ItemStack ingredient = items.getFirst();
        if (ingredient.isEmpty()) {
            return false;
        }

        SingleRecipeInput input = new SingleRecipeInput(ingredient);
        RecipeHolder<? extends AbstractCookingRecipe> recipe = quickCheck.getRecipeFor(input, level).orElse(null);
        if (recipe == null) {
            return false;
        }

        ItemStack result = recipe.value().assemble(input);
        if (result.isEmpty() || !result.isItemEnabled(level.enabledFeatures())) {
            return false;
        }

        ItemStack output = items.get(2);
        if (output.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(output, result)) {
            return false;
        }

        int combinedCount = output.getCount() + result.getCount();
        return combinedCount <= Math.min(entity.getMaxStackSize(), result.getMaxStackSize());
    }

    @Inject(method = "serverTick", at = @At("HEAD"))
    private static void elemancy$applyStokedProgress(
        ServerLevel level,
        BlockPos pos,
        BlockState state,
        AbstractFurnaceBlockEntity entity,
        CallbackInfo callback
    ) {
        AbstractFurnaceBlockEntityMixin mixin = (AbstractFurnaceBlockEntityMixin) (Object) entity;
        FireStrikerState strikerState = entity.getExistingDataOrNull(Attachments.FIRE_STRIKER_STATE);
        if (strikerState == null || !strikerState.isStoked()) {
            return;
        }

        if (mixin.litTimeRemaining <= 1) {
            strikerState.reset();
            entity.setChanged();
            return;
        }

        FireStrikerEffects.spawnStokedParticles(level, pos);

        if (mixin.cookingTimer > 0 && mixin.cookingTimer < mixin.cookingTotalTime - 1) {
            mixin.cookingTimer++;
        }
    }
}