package be.nerosro.elemancy.items.tools.earth;

import be.nerosro.elemancy.items.ElemancyDataComponents;
import net.minecraft.world.item.ItemStack;

/**
 * Session-only excavation state, stored independently on each Earth tool stack.
 */
public final class EarthExcavationMode {
    private EarthExcavationMode() {
    }

    public static void toggle(ItemStack stack) {
        if (EarthTools.isEarthTool(stack)) {
            stack.set(ElemancyDataComponents.EARTH_EXCAVATION_ENABLED.get(), !isEnabled(stack));
        }
    }

    public static boolean isEnabled(ItemStack stack) {
        return stack.getOrDefault(ElemancyDataComponents.EARTH_EXCAVATION_ENABLED.get(), false);
    }
}