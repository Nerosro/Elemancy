package be.nerosro.elemancy.items;

import be.nerosro.soulmark.mana.ManaData;
import be.nerosro.soulmark.mana.ManaUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * A consumable treat that restores a fixed amount of mana when eaten.
 */
public class IceCreamItem extends Item {

    private static final float MANA_RESTORED = 20f;

    public IceCreamItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide() && entity instanceof Player player) {
            ManaData mana = ManaUtil.getMana(player);
            if (mana.isInitialized()) {
                float newMana = Math.min(mana.getCurrentMana() + MANA_RESTORED, mana.getMaxPool());
                mana.setCurrentMana(newMana);
            }

            ItemStack bowl = new ItemStack(Items.BOWL);
            if (!player.getInventory().add(bowl)) {
                player.drop(bowl, false);
            }
        }
        return super.finishUsingItem(stack, level, entity);
    }
}
