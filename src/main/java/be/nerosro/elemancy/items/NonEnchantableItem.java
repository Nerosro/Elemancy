package be.nerosro.elemancy.items;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * An Item that cannot be enchanted or repaired.
 * Used for temporary Stage 0 gear that players shouldn't invest resources into.
 */
public class NonEnchantableItem extends Item {

    public NonEnchantableItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return false;
    }

    @Override
    public boolean isCombineRepairable(ItemStack stack) {
        return false;
    }
}
