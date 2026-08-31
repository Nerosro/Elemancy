package be.nerosro.elemancy.items.tools.firesword;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.items.ElemancyItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;

/** Applies Fire Sword's primed fourth-hit combat behavior. */
@EventBusSubscriber(modid = Elemancy.MOD_ID)
public final class FireSwordCombatEvents {
    private FireSwordCombatEvents() {
    }

    @SubscribeEvent
    public static void onCriticalHit(CriticalHitEvent event) {
        if (isPrimedFireSword(event.getEntity())) {
            event.setCriticalHit(false);
            event.setDisableSweep(true);
        }
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player) || !isPrimedFireSword(player)) {
            return;
        }

        ItemStack sword = player.getMainHandItem();
        event.setAmount(event.getAmount() * 2.0F);
        FireSwordHeat.consumeFourthHit(sword, player.level().getGameTime());
    }

    private static boolean isPrimedFireSword(Player player) {
        ItemStack sword = player.getMainHandItem();
        return sword.is(ElemancyItems.FIRE_SWORD.get()) && FireSwordHeat.get(sword) == FireSwordHeat.MAX_HEAT;
    }
}