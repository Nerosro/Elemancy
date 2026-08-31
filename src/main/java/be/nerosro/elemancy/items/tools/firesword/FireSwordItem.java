package be.nerosro.elemancy.items.tools.firesword;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Fire Elemetal sword that builds Heat through successful direct melee hits. */
public class FireSwordItem extends Item {
    public FireSwordItem(Properties properties) {
        super(properties);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.hurtEnemy(stack, target, attacker);
        if (attacker.level() instanceof ServerLevel level && FireSwordHeat.get(stack) < FireSwordHeat.MAX_HEAT) {
            FireSwordHeat.addSuccessfulHit(stack, level.getGameTime());
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (!(entity instanceof Player) || slot != EquipmentSlot.MAINHAND) {
            FireSwordHeat.reset(stack);
            return;
        }

        FireSwordHeat.tickDecay(stack, level.getGameTime());
    }
}