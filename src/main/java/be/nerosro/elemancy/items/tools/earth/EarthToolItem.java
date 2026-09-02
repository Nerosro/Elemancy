package be.nerosro.elemancy.items.tools.earth;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
}