package be.nerosro.elemancy.items.wands;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Applies common offhand wand cast feedback.
 */
public final class WandCastFeedback {
    private WandCastFeedback() {
    }

    public static void castWithWear(Player player, ItemStack wand) {
        cast(player, wand);
        wand.hurtAndBreak(1, player, EquipmentSlot.OFFHAND);
    }

    /**
     * Records a cast after another system has absorbed or redirected the wand durability cost.
     */
    public static void cast(Player player, ItemStack wand) {
        player.swing(InteractionHand.OFF_HAND, true);
        player.getCooldowns().addCooldown(wand, WandItem.COOLDOWN_TICKS);
    }
}