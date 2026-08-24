package be.nerosro.elemancy.items.trinket;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * Queries equipped Curios items for mana stat trinket bonuses.
 * Returns additive multiplier contributions (e.g. 0.08 for 8% total boost).
 */
public final class TrinketBonuses {
    private TrinketBonuses() {
    }

    /**
     * Sum of all equipped trinkets' pool boost magnitudes.
     */
    public static float poolBoost(Player player) {
        return sumByType(player, ManaStatTrinketItem.ManaModifierType.POOL_BOOST);
    }

    /**
     * Sum of all equipped trinkets' regen boost magnitudes.
     */
    public static float regenBoost(Player player) {
        return sumByType(player, ManaStatTrinketItem.ManaModifierType.REGEN_BOOST);
    }

    /**
     * Sum of all equipped trinkets' cost reduction magnitudes.
     */
    public static float costReduction(Player player) {
        return sumByType(player, ManaStatTrinketItem.ManaModifierType.COST_REDUCTION);
    }

    private static float sumByType(Player player, ManaStatTrinketItem.ManaModifierType type) {
        return CuriosApi.getCuriosInventory(player)
            .map(inventory -> {
                float sum = 0f;
                var equipped = inventory.findCurios(stack -> isManaStatTrinket(stack, type));
                for (var entry : equipped) {
                    sum += ((ManaStatTrinketItem) entry.stack().getItem()).getMagnitude();
                }
                return sum;
            })
            .orElse(0f);
    }

    private static boolean isManaStatTrinket(ItemStack stack, ManaStatTrinketItem.ManaModifierType type) {
        return stack.getItem() instanceof ManaStatTrinketItem trinket
            && trinket.getModifierType() == type;
    }
}
