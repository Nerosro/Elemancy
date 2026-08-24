package be.nerosro.elemancy.items.trinket;

import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

/**
 * Base class for all trinket items. Provides Curios slot compatibility
 * and shared properties (unstackable). Subclass for specific behavior.
 */
public class TrinketItem extends Item implements ICurioItem {

    public TrinketItem(Properties properties) {
        super(properties.stacksTo(1));
    }
}
