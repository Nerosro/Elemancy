package be.nerosro.elemancy.block;

import java.util.List;

import org.jspecify.annotations.Nullable;

import be.nerosro.elemancy.items.tome.TomeItem;
import be.nerosro.elemancy.mana.depth.ManaDepthSystem;
import be.nerosro.elemancy.mana.depth.ScarType;
import be.nerosro.soulmark.network.SoulmarkNetwork;
import be.nerosro.soulmark.traits.Trait;
import be.nerosro.soulmark.traits.TraitData;
import be.nerosro.soulmark.traits.TraitUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A two-block-tall standing mirror that reveals the player's traits and mana scars.
 * Both halves are interactable. Right-click bare-handed shows traits + scars.
 * Right-click with Tome writes traits into it.
 */
public class MirrorBlock extends Block {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 16, 14);

    public MirrorBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }

    // ── Placement ───────────────────────────────────────────────────────────

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos above = context.getClickedPos().above();
        Level level = context.getLevel();
        if (above.getY() >= level.getMaxY() || !level.getBlockState(above).canBeReplaced(context)) {
            return null; // Can't place if no room for top half
        }
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
    }

    // ── Breaking (remove both halves) ───────────────────────────────────────

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
                                     BlockPos pos, Direction direction, BlockPos neighborPos,
                                     BlockState neighborState, RandomSource random) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y) {
            boolean isLowerCheckingAbove = half == DoubleBlockHalf.LOWER && direction == Direction.UP;
            boolean isUpperCheckingBelow = half == DoubleBlockHalf.UPPER && direction == Direction.DOWN;
            if (isLowerCheckingAbove || isUpperCheckingBelow) {
                if (!neighborState.is(this)) {
                    return Blocks.AIR.defaultBlockState();
                }
            }
        }
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            DoubleBlockHalf half = state.getValue(HALF);
            BlockPos otherPos = half == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
            BlockState otherState = level.getBlockState(otherPos);
            if (otherState.is(this) && otherState.getValue(HALF) != half) {
                level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    // ── Shape ───────────────────────────────────────────────────────────────

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    // ── Interaction ─────────────────────────────────────────────────────────

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        if (stack.getItem() instanceof TomeItem) {
            syncTraitsToTome(player, stack);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        displayTraits(player);
        displayScars(player);
        return InteractionResult.SUCCESS;
    }

    // ── Trait Display ───────────────────────────────────────────────────────

    private void displayTraits(Player player) {
        TraitData data = TraitUtil.getTraitData(player);
        if (!data.isInitialized()) {
            player.sendSystemMessage(Component.literal("The mirror shows nothing... your soul is unmarked.")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            return;
        }

        player.sendSystemMessage(Component.literal("─── Your Traits ───")
            .withStyle(ChatFormatting.DARK_PURPLE));

        List<Trait> allTraits = data.getAllTraits();
        for (Trait trait : allTraits) {
            Component name = trait.weight().styledName(trait.name());
            Component type = Component.literal(" [" + trait.type().name().toLowerCase() + "]")
                .withStyle(ChatFormatting.DARK_GRAY);
            Component desc = Component.literal("  " + trait.description())
                .withStyle(ChatFormatting.GRAY);

            player.sendSystemMessage(Component.empty().append(name).append(type));
            player.sendSystemMessage(desc);
        }
    }

    // ── Scar Display ────────────────────────────────────────────────────────

    private void displayScars(Player player) {
        CompoundTag scars = ManaDepthSystem.copyScarData(player);
        boolean hasAnyScar = false;

        for (ScarType scar : ScarType.values()) {
            int ticks = scars.getInt(scar.tickKey()).orElse(0);
            if (ticks <= 0) continue;

            if (!hasAnyScar) {
                player.sendSystemMessage(Component.empty());
                player.sendSystemMessage(Component.literal("─── Active Scars ───")
                    .withStyle(ChatFormatting.DARK_RED));
                hasAnyScar = true;
            }

            int seconds = ticks / 20;

            Component line;
            String stackKey = scar.stackKey();
            if (stackKey != null) {
                int stacks = scars.getInt(stackKey).orElse(0);
                line = Component.literal("  ").append(Component.translatable(scar.translationKey()))
                    .append(Component.literal(" ×" + stacks + " (" + seconds + "s)"))
                    .withStyle(ChatFormatting.RED);
            } else {
                line = Component.literal("  ").append(Component.translatable(scar.translationKey()))
                    .append(Component.literal(" (" + seconds + "s)"))
                    .withStyle(ChatFormatting.RED);
            }
            player.sendSystemMessage(line);
        }

        if (!hasAnyScar) {
            player.sendSystemMessage(Component.empty());
            player.sendSystemMessage(Component.literal("No active scars.")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }

    // ── Tome Sync ───────────────────────────────────────────────────────────

    private void syncTraitsToTome(Player player, ItemStack tome) {
        TraitData data = TraitUtil.getTraitData(player);
        if (!data.isInitialized()) {
            player.sendSystemMessage(Component.literal("The mirror finds nothing to inscribe.")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            return;
        }

        CompoundTag tag = tome.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        CompoundTag traitTag = new CompoundTag();

        List<Trait> allTraits = data.getAllTraits();
        for (int i = 0; i < allTraits.size(); i++) {
            Trait trait = allTraits.get(i);
            CompoundTag entry = new CompoundTag();
            entry.putString("name", trait.name());
            entry.putString("description", trait.description());
            entry.putString("type", trait.type().name());
            entry.putString("weight", trait.weight().name());
            entry.putFloat("value", trait.value());
            traitTag.put("trait_" + i, entry);
        }
        traitTag.putInt("count", allTraits.size());

        tag.put("traits", traitTag);
        tome.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        TraitUtil.revealTraits(player);
        TraitUtil.revealScars(player);
        if (player instanceof ServerPlayer sp) {
            SoulmarkNetwork.syncMana(sp, ManaDepthSystem.hasExperiencedManaCollapse(sp));
        }

        player.sendSystemMessage(Component.literal("Your traits have been inscribed into the Tome.")
            .withStyle(ChatFormatting.DARK_PURPLE));
    }
}
