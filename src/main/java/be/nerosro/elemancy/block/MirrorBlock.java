package be.nerosro.elemancy.block;

import java.util.List;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import be.nerosro.elemancy.items.tome.TomeItem;
import be.nerosro.elemancy.items.tome.TomeTraitSnapshot;
import be.nerosro.elemancy.mana.depth.ManaDepthSystem;
import be.nerosro.elemancy.mana.depth.ScarType;
import be.nerosro.soulmark.network.SoulmarkNetwork;
import be.nerosro.soulmark.traits.Trait;
import be.nerosro.soulmark.traits.TraitData;
import be.nerosro.soulmark.traits.TraitUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A standing mirror that reveals the player's traits and mana scars.
 * Right-click bare-handed shows traits + scars.
 * Right-click with Tome writes traits into it.
 */
public class MirrorBlock extends BaseEntityBlock {

    private static final MapCodec<MirrorBlock> CODEC = simpleCodec(MirrorBlock::new);

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    private static final VoxelShape LOWER_SHAPE = Block.box(0, 0, 0, 16, 32, 16);
    private static final VoxelShape UPPER_SHAPE = Block.box(0, -16, 0, 16, 16, 16);

    public MirrorBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF);
    }

    @Override
    protected MapCodec<MirrorBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? new MirrorBlockEntity(pos, state) : null;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
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

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks,
                                     BlockPos pos, Direction direction, BlockPos neighborPos,
                                     BlockState neighborState, RandomSource random) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y
            && (half == DoubleBlockHalf.LOWER) == (direction == Direction.UP)
            && (!neighborState.is(this) || neighborState.getValue(HALF) == half)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && state.getValue(HALF) == DoubleBlockHalf.UPPER
            && (player.preventsBlockDrops() || !player.hasCorrectToolForDrops(state, level, pos))) {
            BlockPos below = pos.below();
            BlockState lower = level.getBlockState(below);
            if (lower.is(this) && lower.getValue(HALF) == DoubleBlockHalf.LOWER) {
                level.setBlock(below, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    // ── Shape ───────────────────────────────────────────────────────────────

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? UPPER_SHAPE : LOWER_SHAPE;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    // ── Interaction ─────────────────────────────────────────────────────────

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        if (stack.isEmpty()) {
            displayTraits(player);
            displayScars(player);
            diagnoseManaCollapse(player);
            return InteractionResult.SUCCESS;
        }

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
        diagnoseManaCollapse(player);
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

        boolean traitsWereRevealed = TraitUtil.isTraitsRevealed(player);
        revealMirrorDiscovery(player);
        diagnoseManaCollapse(player);
        if (TomeTraitSnapshot.write(tome, player) || !traitsWereRevealed) {
            player.sendSystemMessage(Component.translatable("message.elemancy.mirror.traits_inscribed")
                .withStyle(ChatFormatting.DARK_PURPLE));
        }
    }

    private void revealMirrorDiscovery(Player player) {
        if (TraitUtil.isTraitsRevealed(player)) return;

        TraitUtil.revealTraits(player);
        TraitUtil.revealScars(player);
        if (player instanceof ServerPlayer sp) {
            SoulmarkNetwork.syncMana(sp);
        }
    }

    private void diagnoseManaCollapse(Player player) {
        if (!TraitUtil.isScarsRevealed(player) || !ManaDepthSystem.diagnoseManaCollapse(player)) return;

        player.sendSystemMessage(Component.translatable("message.elemancy.mirror.mana_collapse_diagnosed")
            .withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC));
        if (player instanceof ServerPlayer sp) {
            SoulmarkNetwork.syncMana(sp);
        }
    }
}
