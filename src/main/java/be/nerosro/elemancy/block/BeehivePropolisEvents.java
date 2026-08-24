package be.nerosro.elemancy.block;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.items.ElemancyItems;
import be.nerosro.elemancy.util.BeehiveProximityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

/**
 * Handles collecting honey with a glass bottle near Paradox Flowers - yields propolis instead
 * of a honey bottle. Shearing beehives is untouched and always drops vanilla honeycomb.
 * Uses proximity detection from BeehiveProximityHelper (5 horizontal, 3 vertical range).
 */
@EventBusSubscriber(modid = Elemancy.MOD_ID)
public class BeehivePropolisEvents {

    @SubscribeEvent
    public static void onUseItemOnBeehive(UseItemOnBlockEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemStack();
        BlockPos pos = event.getPos();
        Level level = event.getLevel();
        BlockState state = level.getBlockState(pos);

        // Only handle beehives
        if (!state.is(BlockTags.BEEHIVES)) return;

        // Only handle glass bottles
        if (!item.is(Items.GLASS_BOTTLE)) return;

        // Check proximity to Paradox Flower
        if (!BeehiveProximityHelper.isNearParadoxFlower(level, pos, 5, 3)) {
            return; // Not in range → let vanilla honey bottle drop
        }

        // Cancel vanilla event to prevent honey bottle
        event.setCanceled(true);

        // Manually handle the harvest (server-side only)
        if (!level.isClientSide()) {
            handleBottleHarvest(level, pos, state, player, event.getHand(), item);
        }
    }

    /**
     * Manually handles bottle harvesting to yield propolis instead of a honey bottle.
     * Mimics vanilla behavior: reduces honey level, consumes the bottle, does not anger bees.
     */
    private static void handleBottleHarvest(Level level, BlockPos pos, BlockState state,
                                            Player player, InteractionHand hand, ItemStack bottleStack) {
        // Get current honey level (0-5 for beehives)
        int honeyLevel = state.getValue(BeehiveBlock.HONEY_LEVEL);

        // Only harvest if honey is full (same as vanilla)
        if (honeyLevel < 5) return;

        // Reduce honey level (same as vanilla)
        level.setBlock(pos, state.setValue(BeehiveBlock.HONEY_LEVEL, 0), 3);

        ItemStack propolis = new ItemStack(ElemancyItems.PROPOLIS.get());

        // Swap the bottle for propolis (same pattern as vanilla honey bottle filling)
        if (bottleStack.getCount() == 1) {
            player.setItemInHand(hand, propolis);
        } else {
            bottleStack.shrink(1);
            if (!player.getInventory().add(propolis)) {
                player.drop(propolis, false);
            }
        }
    }
}