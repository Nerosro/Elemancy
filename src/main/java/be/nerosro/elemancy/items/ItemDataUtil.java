package be.nerosro.elemancy.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * Shared helpers for reading/writing CustomData on item stacks.
 */
public final class ItemDataUtil {
    private ItemDataUtil() {
    }

    public static CompoundTag getCustomTag(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null ? data.copyTag() : null;
    }

    public static CompoundTag getOrCreateCustomTag(ItemStack stack) {
        CompoundTag tag = getCustomTag(stack);
        return tag != null ? tag : new CompoundTag();
    }
}
