package be.nerosro.elemancy.items;

import java.util.ArrayList;
import java.util.List;

import be.nerosro.elemancy.ElemancyColors;
import be.nerosro.elemancy.effects.CastEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Full-set bonus logic for Infused Robes.
 * When wearing all 4 pieces and casting Elementize with the Energized Stick:
 * 75% chance to skip wand durability, redirecting 1 durability to 2 random robe pieces instead.
 */
public final class RobeSetBonus {
    private RobeSetBonus() {
    }

    private static final float PROTECTION_CHANCE = 0.75f;

    private static final EquipmentSlot[] ROBE_SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    /**
     * Returns true if the player is wearing a full set of Infused Robes.
     */
    public static boolean isWearingFullSet(Player player) {
        for (EquipmentSlot slot : ROBE_SLOTS) {
            ItemStack piece = player.getItemBySlot(slot);
            if (!isRobePiece(piece)) return false;
        }
        return true;
    }

    /**
     * Attempts to apply the robe set bonus.
     * Returns true if the wand durability was absorbed (skip wand damage).
     * Returns false if the bonus did not trigger (apply wand damage normally).
     */
    public static boolean tryAbsorbWandDamage(Player player) {
        if (!isWearingFullSet(player)) return false;
        if (!isEnergizedStick(player.getOffhandItem())) return false;
        if (player.getRandom().nextFloat() >= PROTECTION_CHANCE) return false;

        // Redirect durability to 2 random robe pieces
        List<EquipmentSlot> slots = new ArrayList<>(List.of(ROBE_SLOTS));
        // Fisher-Yates shuffle using MC's RandomSource (not compatible with Collections.shuffle)
        for (int i = slots.size() - 1; i > 0; i--) {
            int j = player.getRandom().nextInt(i + 1);
            EquipmentSlot tmp = slots.get(i);
            slots.set(i, slots.get(j));
            slots.set(j, tmp);
        }
        for (int i = 0; i < 2; i++) {
            EquipmentSlot slot = slots.get(i);
            player.getItemBySlot(slot).hurtAndBreak(1, player, slot);
        }

        // Subtle cyan particle feedback
        if (player.level() instanceof ServerLevel serverLevel) {
            CastEffects.casterBurst(serverLevel, player, ElemancyColors.MANA.rgb());
        }

        return true;
    }

    private static boolean isRobePiece(ItemStack stack) {
        return stack.is(ElemancyItems.ROBE_HELMET.get())
            || stack.is(ElemancyItems.ROBE_CHESTPLATE.get())
            || stack.is(ElemancyItems.ROBE_LEGGINGS.get())
            || stack.is(ElemancyItems.ROBE_BOOTS.get());
    }

    private static boolean isEnergizedStick(ItemStack stack) {
        return stack.is(ElemancyItems.ENERGIZED_STICK.get());
    }
}
