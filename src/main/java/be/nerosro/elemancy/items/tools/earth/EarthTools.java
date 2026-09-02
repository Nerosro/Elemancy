package be.nerosro.elemancy.items.tools.earth;

import be.nerosro.elemancy.items.ElemancyItems;
import net.minecraft.world.item.ItemStack;

/**
 * Earth-tool identification shared by input, networking, and mining behavior.
 */
public final class EarthTools {
    private EarthTools() {
    }

    public static boolean isEarthTool(ItemStack stack) {
        return stack.is(ElemancyItems.EARTH_PICKAXE.get()) || stack.is(ElemancyItems.EARTH_SHOVEL.get());
    }
}