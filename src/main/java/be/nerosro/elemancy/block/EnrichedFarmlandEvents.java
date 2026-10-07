package be.nerosro.elemancy.block;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.items.ElemancyItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.PitcherCropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;

@EventBusSubscriber(modid = Elemancy.MOD_ID)
public final class EnrichedFarmlandEvents {
    public static final double GROWTH_BONUS = 0.45D;
    private static final float EXTRA_HARVEST_CHANCE = 0.55F;
    private static final float MUTATION_CHANCE = 0.05F;

    private EnrichedFarmlandEvents() {
    }

    @SubscribeEvent
    public static void onBonemeal(BonemealEvent event) {
        BlockPos cropPos = event.getPos();
        if (event.getState().getBlock() instanceof PitcherCropBlock
            && event.getState().getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
            cropPos = cropPos.below();
        }
        if ((event.getState().getBlock() instanceof CropBlock || event.getState().getBlock() instanceof StemBlock
            || event.getState().getBlock() instanceof PitcherCropBlock)
            && event.getLevel().getBlockState(cropPos.below()).is(ElemancyBlocks.ENRICHED_FARMLAND.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onCropGrow(CropGrowEvent.Pre event) {
        if (!(event.getLevel() instanceof ServerLevel level)
            || event.getResult() != CropGrowEvent.Pre.Result.DEFAULT
            || !usesStandardGrowthChance(event.getState())) {
            return;
        }

        BlockPos pos = event.getPos();
        if (!isHydrated(level, pos)) {
            return;
        }

        int vanillaBound = (int) (25.0F / VanillaGrowthSpeed.at(event.getState(), level, pos)) + 1;
        if (vanillaBound > 1 && level.getRandom().nextDouble() < GROWTH_BONUS / (vanillaBound - 1)) {
            event.setResult(CropGrowEvent.Pre.Result.GROW);
        }
    }

    private static boolean usesStandardGrowthChance(BlockState state) {
        return state.getBlock() instanceof CropBlock || state.getBlock() instanceof StemBlock;
    }

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        BlockState state = event.getState();
        ServerLevel level = event.getLevel();
        BlockPos pos = event.getPos();
        if (!(state.getBlock() instanceof CropBlock crop) || state.getBlock() instanceof StemBlock
            || !crop.isMaxAge(state) || event.getDrops().isEmpty()
            || !level.getBlockState(pos.below()).is(ElemancyBlocks.ENRICHED_FARMLAND.get())) {
            return;
        }

        if (tryMutate(event)) return;

        ItemStack plantingItem = state.getCloneItemStack(level, pos, false);
        if (plantingItem.isEmpty()) return;

        ItemStack primary = ItemStack.EMPTY;
        boolean hasPlantingItem = false;
        for (ItemEntity drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            if (ItemStack.isSameItemSameComponents(stack, plantingItem)) {
                hasPlantingItem = true;
            } else if (!stack.isEmpty()) {
                if (!primary.isEmpty() && !ItemStack.isSameItemSameComponents(primary, stack)
                    && !state.is(Blocks.CARROTS) && !state.is(Blocks.POTATOES)) return;
                primary = stack;
            }
        }

        if (state.is(Blocks.CARROTS) || state.is(Blocks.POTATOES)) {
            primary = plantingItem;
        }
        if (primary.isEmpty()) {
            if (!hasPlantingItem || state.is(Blocks.WHEAT) || state.is(Blocks.BEETROOTS)) return;
            primary = plantingItem;
        }

        // int bonusCount = level.getRandom().nextFloat() < EXTRA_HARVEST_CHANCE ? 2 : 1; //Old extra guaranteed crop drop
        // addDrop(event, primary.copyWithCount(bonusCount));
        if (level.getRandom().nextFloat() < EXTRA_HARVEST_CHANCE) {
            addDrop(event, primary.copyWithCount(1));
        }
        if (!hasPlantingItem && !ItemStack.isSameItemSameComponents(primary, plantingItem)
            && !state.is(ElemancyBlocks.STRAWBERRY.get())) {
            addDrop(event, plantingItem.copyWithCount(1));
        }
    }

    private static boolean tryMutate(BlockDropsEvent event) {
        BlockState state = event.getState();
        EnrichedCropBlock target;
        ItemStack primary;
        if (state.is(Blocks.BEETROOTS)) {
            target = ElemancyBlocks.STRAWBERRY.get();
            primary = new ItemStack(ElemancyItems.STRAWBERRY.get());
        } else if (state.is(Blocks.POTATOES)) {
            target = ElemancyBlocks.YAM.get();
            primary = new ItemStack(ElemancyItems.YAM.get());
        } else if (state.is(Blocks.CARROTS)) {
            target = ElemancyBlocks.MANA_CARROT.get();
            primary = new ItemStack(ElemancyItems.MANA_CARROT.get());
        } else {
            return false;
        }

        ServerLevel level = event.getLevel();
        BlockPos pos = event.getPos();
        boolean flowerNearby = false;
        for (BlockPos flowerPos : BlockPos.betweenClosed(pos.offset(-3, 0, -3), pos.offset(3, 0, 3))) {
            if (level.getBlockState(flowerPos).is(ElemancyBlocks.PARADOX_FLOWER.get())) {
                flowerNearby = true;
                break;
            }
        }
        //If no Paradox flower nearby or the random chance fails, exit
        if (!flowerNearby || level.getRandom().nextFloat() >= MUTATION_CHANCE) return false;

        var replacementDrops = Block.getDrops(target.getStateForAge(CropBlock.MAX_AGE), level, pos,
            null, event.getBreaker(), event.getTool());
        event.getDrops().clear();
        for (ItemStack drop : replacementDrops) {
            if (!drop.isEmpty()) addDrop(event, drop);
        }
        if (replacementDrops.isEmpty()) addDrop(event, primary.copy());
        // addDrop(event, primary.copyWithCount(level.getRandom().nextFloat() < EXTRA_HARVEST_CHANCE ? 2 : 1)); //Old extra guaranteed crop drop
        if (level.getRandom().nextFloat() < EXTRA_HARVEST_CHANCE) {
            addDrop(event, primary.copyWithCount(1));
        }
        return true;
    }

    private static void addDrop(BlockDropsEvent event, ItemStack stack) {
        BlockPos pos = event.getPos();
        event.getDrops().add(new ItemEntity(event.getLevel(),
            pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack));
    }

    public static boolean isHydrated(Level level, BlockPos cropPos) {
        BlockState soil = level.getBlockState(cropPos.below());
        return soil.is(ElemancyBlocks.ENRICHED_FARMLAND.get()) && soil.getValue(FarmlandBlock.MOISTURE) > 0;
    }

    private static final class VanillaGrowthSpeed extends CropBlock {
        private VanillaGrowthSpeed() {
            super(BlockBehaviour.Properties.of());
        }

        private static float at(BlockState state, BlockGetter level, BlockPos pos) {
            return getGrowthSpeed(state, level, pos);
        }
    }
}