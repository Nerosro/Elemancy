package be.nerosro.elemancy.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.cow.AbstractCow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;

public class DarkBucketItem extends Item {
    public DarkBucketItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack darkBucket = player.getItemInHand(hand);
        int compartment = DarkBucketContents.getSelectedCompartment(darkBucket);
        ItemStack content = DarkBucketContents.getCompartment(darkBucket, compartment);
        if (content.is(Items.MILK_BUCKET)) {
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack delegate = content.isEmpty() ? new ItemStack(Items.BUCKET) : content.copy();
        player.setItemInHand(hand, delegate);
        boolean restoreInstabuild = content.isEmpty() && player.getAbilities().instabuild;
        if (restoreInstabuild) {
            player.getAbilities().instabuild = false;
        }

        InteractionResult result;
        try {
            result = delegate.getItem().use(level, player, hand);
        } finally {
            if (restoreInstabuild) {
                player.getAbilities().instabuild = true;
            }
        }

        ItemStack resultStack = result instanceof InteractionResult.Success success
            && success.heldItemTransformedTo() != null
            ? success.heldItemTransformedTo()
            : player.getItemInHand(hand);
        player.setItemInHand(hand, darkBucket);

        if (!result.consumesAction()) {
            return result;
        }

        if (content.isEmpty()) {
            if (isEligibleContent(resultStack)) {
                DarkBucketContents.setCompartment(darkBucket, compartment, resultStack);
            }
        } else if (resultStack.is(Items.BUCKET)) {
            DarkBucketContents.setCompartment(darkBucket, compartment, ItemStack.EMPTY);
        }

        return result instanceof InteractionResult.Success success ? success.withoutItem() : result;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack darkBucket = context.getItemInHand();
        int compartment = DarkBucketContents.getSelectedCompartment(darkBucket);
        ItemStack content = DarkBucketContents.getCompartment(darkBucket, compartment);
        if (!(content.getItem() instanceof SolidBucketItem)) {
            return InteractionResult.PASS;
        }

        if (context.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        InteractionHand hand = context.getHand();
        BlockHitResult hitResult = new BlockHitResult(
            context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), context.isInside());
        UseOnContext delegateContext = new UseOnContext(context.getLevel(), player, hand, content, hitResult);
        player.setItemInHand(hand, content);
        InteractionResult result = content.getItem().useOn(delegateContext);
        ItemStack resultStack = player.getItemInHand(hand);
        player.setItemInHand(hand, darkBucket);

        if (result.consumesAction() && resultStack.is(Items.BUCKET)) {
            DarkBucketContents.setCompartment(darkBucket, compartment, ItemStack.EMPTY);
        }

        return result instanceof InteractionResult.Success success ? success.withoutItem() : result;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity,
                                                  InteractionHand hand) {
        if (!(entity instanceof AbstractCow cow) || !DarkBucketContents.getCompartment(stack,
            DarkBucketContents.getSelectedCompartment(stack)).isEmpty()) {
            return InteractionResult.PASS;
        }

        if (player.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        int compartment = DarkBucketContents.getSelectedCompartment(stack);
        player.setItemInHand(hand, new ItemStack(Items.BUCKET));
        boolean restoreInstabuild = player.getAbilities().instabuild;
        if (restoreInstabuild) {
            player.getAbilities().instabuild = false;
        }

        InteractionResult result;
        try {
            result = cow.mobInteract(player, hand);
        } finally {
            if (restoreInstabuild) {
                player.getAbilities().instabuild = true;
            }
        }

        ItemStack resultStack = player.getItemInHand(hand);
        player.setItemInHand(hand, stack);

        if (result.consumesAction() && resultStack.is(Items.MILK_BUCKET)) {
            DarkBucketContents.setCompartment(stack, compartment, resultStack);
        }

        return result;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        ItemStack content = selectedContent(stack);
        return content.is(Items.MILK_BUCKET)
            ? content.getUseDuration(user)
            : super.getUseDuration(stack, user);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        ItemStack content = selectedContent(stack);
        return content.is(Items.MILK_BUCKET)
            ? content.getUseAnimation()
            : super.getUseAnimation(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int ticksRemaining) {
        ItemStack content = selectedContent(stack);
        if (content.is(Items.MILK_BUCKET)) {
            content.onUseTick(level, user, ticksRemaining);
        } else {
            super.onUseTick(level, user, stack, ticksRemaining);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        if (!(user instanceof Player player) || !selectedContent(stack).is(Items.MILK_BUCKET)) {
            return super.finishUsingItem(stack, level, user);
        }

        InteractionHand hand = player.getUsedItemHand();
        int compartment = DarkBucketContents.getSelectedCompartment(stack);
        ItemStack milkBucket = DarkBucketContents.getCompartment(stack, compartment);
        player.setItemInHand(hand, milkBucket);
        milkBucket.getItem().finishUsingItem(milkBucket, level, user);
        player.setItemInHand(hand, stack);
        if (!player.hasInfiniteMaterials()) {
            DarkBucketContents.setCompartment(stack, compartment, ItemStack.EMPTY);
        }
        return stack;
    }

    private static boolean isEligibleContent(ItemStack stack) {
        return stack.is(Items.MILK_BUCKET)
            || stack.getItem() instanceof SolidBucketItem
            || stack.getItem() instanceof BucketItem && !(stack.getItem() instanceof MobBucketItem);
    }

    private static ItemStack selectedContent(ItemStack stack) {
        return DarkBucketContents.getCompartment(stack, DarkBucketContents.getSelectedCompartment(stack));
    }

    static net.minecraft.network.chat.Component contentName(ItemStack content) {
        if (content.isEmpty()) {
            return Component.translatable("tooltip.elemancy.dark_bucket.empty");
        }
        if (content.is(Items.MILK_BUCKET)) {
            return Component.translatable("tooltip.elemancy.dark_bucket.milk");
        }
        if (content.getItem() instanceof BlockItem blockItem) {
            return blockItem.getBlock().getName();
        }

        var fluid = FluidUtil.getFirstStackContained(content);
        return fluid.isEmpty() ? content.getHoverName() : fluid.getHoverName();
    }
}