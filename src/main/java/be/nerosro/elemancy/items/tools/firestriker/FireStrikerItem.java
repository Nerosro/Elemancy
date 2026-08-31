package be.nerosro.elemancy.items.tools.firestriker;

import be.nerosro.elemancy.items.NonEnchantableItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class FireStrikerItem extends NonEnchantableItem {
    public FireStrikerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        if (!(blockEntity instanceof StokableHeatSource heatSource)) {
            return InteractionResult.PASS;
        }

        if (!(context.getLevel() instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        if (player == null || !heatSource.elemancy$tryStoke(serverLevel, player)) {
            return InteractionResult.PASS;
        }

        serverLevel.playSound(
            player,
            context.getClickedPos(),
            SoundEvents.FLINTANDSTEEL_USE,
            SoundSource.BLOCKS,
            1.0f,
            serverLevel.getRandom().nextFloat() * 0.4f + 0.8f
        );
        context.getItemInHand().hurtAndBreak(1, player, context.getHand().asEquipmentSlot());
        return InteractionResult.SUCCESS_SERVER;
    }
}