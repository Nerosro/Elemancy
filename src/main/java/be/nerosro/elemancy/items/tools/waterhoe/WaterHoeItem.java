package be.nerosro.elemancy.items.tools.waterhoe;

import be.nerosro.elemancy.block.ElemancyBlocks;
import be.nerosro.elemancy.tome.DiscoveryNodes;
import be.nerosro.elemancy.tome.TomeDiscoveryEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public final class WaterHoeItem extends HoeItem {
    public WaterHoeItem(ToolMaterial material, Properties properties) {
        super(material, 0.0F, -1.0F, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos center = context.getClickedPos();
        if (context.getClickedFace() == Direction.DOWN || !isTillable(level.getBlockState(center))
            || !level.getBlockState(center.above()).isAir()) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        Player player = context.getPlayer();
        ItemStack tool = context.getItemInHand();
        int converted = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos target = center.offset(dx, 0, dz);
                if (!isTillable(level.getBlockState(target)) || !level.getBlockState(target.above()).isAir()
                    || player != null && (!level.mayInteract(player, target)
                    || !player.mayUseItemAt(target, Direction.UP, tool))) {
                    continue;
                }
                BlockState farmland = ElemancyBlocks.ENRICHED_FARMLAND.get().defaultBlockState();
                if (level.setBlock(target, farmland, 11)) {
                    level.gameEvent(GameEvent.BLOCK_CHANGE, target, GameEvent.Context.of(player, farmland));
                    converted++;
                }
            }
        }

        if (converted == 0) return InteractionResult.PASS;
        level.playSound(null, center, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (player != null) {
            if (TomeDiscoveryEvents.unlockDiscovery(player, DiscoveryNodes.ENRICHED_FARMLAND)) {
                player.sendSystemMessage(Component.translatable("message.elemancy.discovery.enriched_farmland"));
            }
            tool.hurtAndBreak(1, player, context.getHand().asEquipmentSlot());
        } else {
            tool.hurtAndBreak(1, (ServerLevel) level, (LivingEntity) null, ignored -> {
            });
        }
        return InteractionResult.SUCCESS;
    }

    private static boolean isTillable(BlockState state) {
        return state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK)
            || state.is(Blocks.DIRT_PATH) || state.is(Blocks.COARSE_DIRT);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return false;
    }
}