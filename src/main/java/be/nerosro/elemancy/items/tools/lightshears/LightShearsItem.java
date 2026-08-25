package be.nerosro.elemancy.items.tools.lightshears;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.IShearable;

/** Shears that refract each white-sheep wool drop into an independent random color. */
public class LightShearsItem extends ShearsItem {
    private static final Item[] WOOL_BY_COLOR = {
        Items.WHITE_WOOL, Items.ORANGE_WOOL, Items.MAGENTA_WOOL, Items.LIGHT_BLUE_WOOL,
        Items.YELLOW_WOOL, Items.LIME_WOOL, Items.PINK_WOOL, Items.GRAY_WOOL,
        Items.LIGHT_GRAY_WOOL, Items.CYAN_WOOL, Items.PURPLE_WOOL, Items.BLUE_WOOL,
        Items.BROWN_WOOL, Items.GREEN_WOOL, Items.RED_WOOL, Items.BLACK_WOOL
    };

    public LightShearsItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity,
                                                   InteractionHand hand) {
        if (!(entity instanceof Sheep sheep) || sheep.getColor() != DyeColor.WHITE) {
            return super.interactLivingEntity(stack, player, entity, hand);
        }

        if (!(entity instanceof IShearable shearable)) {
            return InteractionResult.PASS;
        }

        BlockPos position = entity.blockPosition();
        if (!shearable.isShearable(player, stack, entity.level(), position)) {
            return InteractionResult.PASS;
        }

        List<ItemStack> drops = recolorWoolDrops(shearable.onSheared(player, stack, entity.level(), position), player);
        if (entity.level() instanceof ServerLevel serverLevel) {
            for (ItemStack drop : drops) {
                shearable.spawnShearedDrop(serverLevel, position, drop);
            }
            stack.hurtAndBreak(1, player, hand.asEquipmentSlot());
        }

        entity.gameEvent(GameEvent.SHEAR, player);
        return InteractionResult.SUCCESS;
    }

    private static List<ItemStack> recolorWoolDrops(List<ItemStack> drops, Player player) {
        List<ItemStack> recoloredDrops = new ArrayList<>();
        for (ItemStack drop : drops) {
            if (!drop.is(ItemTags.WOOL)) {
                recoloredDrops.add(drop);
                continue;
            }

            for (int index = 0; index < drop.getCount(); index++) {
                recoloredDrops.add(new ItemStack(WOOL_BY_COLOR[player.getRandom().nextInt(WOOL_BY_COLOR.length)]));
            }
        }
        return recoloredDrops;
    }
}