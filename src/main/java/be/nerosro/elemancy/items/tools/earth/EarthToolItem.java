package be.nerosro.elemancy.items.tools.earth;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Earth tool whose excavation mode is shown by the enchantment glint.
 */
public final class EarthToolItem extends Item {
    public EarthToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return EarthExcavationMode.isEnabled(stack) || super.isFoil(stack);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return false;
    }
}