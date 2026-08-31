package com.nerosro.elemancy.mixin;

import be.nerosro.elemancy.items.tools.firestriker.FireStrikerState;
import be.nerosro.elemancy.items.tools.firestriker.FireStrikerEffects;
import be.nerosro.elemancy.items.tools.firestriker.StokableHeatSource;
import be.nerosro.elemancy.skilltree.Attachments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlockEntity.class)
public abstract class CampfireBlockEntityMixin implements StokableHeatSource {
    @Unique
    private static final int[] ELEMANCY$EXTINGUISH_CHANCES = {0, 10, 13, 17, 20, 23, 27, 30, 33};

    @Shadow
    @Final
    private NonNullList<ItemStack> items;

    @Shadow
    @Final
    private int[] cookingProgress;

    @Shadow
    @Final
    private int[] cookingTime;

    @Unique
    private int elemancy$occupiedBeforeTick;

    @Override
    public boolean elemancy$tryStoke(ServerLevel level, Player player) {
        BlockEntity blockEntity = (BlockEntity) (Object) this;
        FireStrikerState strikerState = blockEntity.getData(Attachments.FIRE_STRIKER_STATE);
        if (strikerState.isStoked()
            || !blockEntity.getBlockState().getValue(CampfireBlock.LIT)
            || elemancy$countCookingItems() == 0) {
            return false;
        }

        strikerState.stoke();
        blockEntity.setChanged();
        return true;
    }

    @Inject(method = "cookTick", at = @At("HEAD"))
    private static void elemancy$prepareStokedCooking(
        ServerLevel level,
        BlockPos pos,
        BlockState state,
        CampfireBlockEntity entity,
        RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> recipeCache,
        CallbackInfo callback
    ) {
        CampfireBlockEntityMixin mixin = (CampfireBlockEntityMixin) (Object) entity;
        FireStrikerState strikerState = entity.getExistingDataOrNull(Attachments.FIRE_STRIKER_STATE);
        if (strikerState == null || !strikerState.isStoked()) {
            return;
        }

        FireStrikerEffects.spawnStokedParticles(level, pos);
        mixin.elemancy$occupiedBeforeTick = mixin.elemancy$countCookingItems();
        for (int slot = 0; slot < mixin.items.size(); slot++) {
            if (!mixin.items.get(slot).isEmpty()
                && mixin.cookingProgress[slot] < mixin.cookingTime[slot] - 1) {
                mixin.cookingProgress[slot]++;
            }
        }
    }

    @Inject(method = "cookTick", at = @At("TAIL"))
    private static void elemancy$finishStokedCooking(
        ServerLevel level,
        BlockPos pos,
        BlockState state,
        CampfireBlockEntity entity,
        RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> recipeCache,
        CallbackInfo callback
    ) {
        CampfireBlockEntityMixin mixin = (CampfireBlockEntityMixin) (Object) entity;
        FireStrikerState strikerState = entity.getExistingDataOrNull(Attachments.FIRE_STRIKER_STATE);
        if (strikerState == null || !strikerState.isStoked()) {
            return;
        }

        int remainingItems = mixin.elemancy$countCookingItems();
        int completedItems = Math.max(0, mixin.elemancy$occupiedBeforeTick - remainingItems);
        for (int completed = 0; completed < completedItems; completed++) {
            strikerState.recordCompletedItem();
        }

        if (remainingItems > 0) {
            if (completedItems > 0) {
                entity.setChanged();
            }
            return;
        }

        int chance = ELEMANCY$EXTINGUISH_CHANCES[Math.min(
            strikerState.getCompletedItems(), ELEMANCY$EXTINGUISH_CHANCES.length - 1)];
        strikerState.reset();
        entity.setChanged();
        if (chance > 0 && level.getRandom().nextInt(100) < chance) {
            level.setBlock(pos, state.setValue(CampfireBlock.LIT, false), 3);
        }
    }

    @Inject(method = "cooldownTick", at = @At("HEAD"))
    private static void elemancy$clearUnlitStoking(
        Level level,
        BlockPos pos,
        BlockState state,
        CampfireBlockEntity entity,
        CallbackInfo callback
    ) {
        FireStrikerState strikerState = entity.getExistingDataOrNull(Attachments.FIRE_STRIKER_STATE);
        if (strikerState != null && strikerState.isStoked()) {
            strikerState.reset();
            entity.setChanged();
        }
    }

    @Unique
    private int elemancy$countCookingItems() {
        int count = 0;
        for (ItemStack item : items) {
            if (!item.isEmpty()) {
                count++;
            }
        }
        return count;
    }
}