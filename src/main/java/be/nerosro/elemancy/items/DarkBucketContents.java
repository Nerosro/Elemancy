package be.nerosro.elemancy.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;

/**
 * Fixed ordered contents and selection state for a Dark Bucket stack.
 */
public final class DarkBucketContents {
    public static final int COMPARTMENT_COUNT = 4;

    public static final String MODEL_EMPTY = "empty";
    public static final String MODEL_WATER = "water";
    public static final String MODEL_LAVA = "lava";
    public static final String MODEL_MILK = "milk";
    public static final String MODEL_POWDER_SNOW = "powder_snow";
    public static final String MODEL_DYNAMIC = "dynamic";

    public static final CustomModelData DEFAULT_MODEL_DATA = modelData(MODEL_EMPTY);

    private static final String TAG_SELECTED_COMPARTMENT = "dark_bucket_selected";

    private DarkBucketContents() {
    }

    public static int getSelectedCompartment(ItemStack stack) {
        return getSelectedCompartment((DataComponentGetter) stack);
    }

    public static int getSelectedCompartment(DataComponentGetter components) {
        CustomData customData = components.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = customData == null ? null : customData.copyTag();
        if (tag == null) {
            return 0;
        }

        return Math.floorMod(tag.getInt(TAG_SELECTED_COMPARTMENT).orElse(0), COMPARTMENT_COUNT);
    }

    public static void cycleSelectedCompartment(ItemStack stack, int direction) {
        if (direction == 0) {
            return;
        }

        CompoundTag tag = ItemDataUtil.getOrCreateCustomTag(stack);
        int selected = getSelectedCompartment(stack);
        tag.putInt(TAG_SELECTED_COMPARTMENT, Math.floorMod(selected + Integer.signum(direction), COMPARTMENT_COUNT));
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        updateVisualVariant(stack);
    }

    public static ItemStack getCompartment(ItemStack stack, int compartment) {
        return getCompartment((DataComponentGetter) stack, compartment);
    }

    public static ItemStack getCompartment(DataComponentGetter components, int compartment) {
        validateCompartment(compartment);
        ItemContainerContents contents = components.get(DataComponents.CONTAINER);
        return contents != null && compartment < contents.getSlots()
            ? contents.getStackInSlot(compartment)
            : ItemStack.EMPTY;
    }

    public static void setCompartment(ItemStack stack, int compartment, ItemStack content) {
        validateCompartment(compartment);

        List<ItemStack> contents = new ArrayList<>(COMPARTMENT_COUNT);
        for (int index = 0; index < COMPARTMENT_COUNT; index++) {
            contents.add(getCompartment(stack, index));
        }
        contents.set(compartment, content.copyWithCount(content.isEmpty() ? 0 : 1));
        stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(contents));
        updateVisualVariant(stack);
    }

    public static FluidStack getSelectedFluid(ItemStack stack) {
        return FluidUtil.getFirstStackContained(getCompartment(stack, getSelectedCompartment(stack)));
    }

    private static void updateVisualVariant(ItemStack stack) {
        ItemStack selectedContent = getCompartment(stack, getSelectedCompartment(stack));
        String model = selectedContent.isEmpty() ? MODEL_EMPTY
            : selectedContent.is(Items.WATER_BUCKET) ? MODEL_WATER
              : selectedContent.is(Items.LAVA_BUCKET) ? MODEL_LAVA
                : selectedContent.is(Items.MILK_BUCKET) ? MODEL_MILK
                  : selectedContent.is(Items.POWDER_SNOW_BUCKET) ? MODEL_POWDER_SNOW
                    : MODEL_DYNAMIC;
        stack.set(DataComponents.CUSTOM_MODEL_DATA, modelData(model));
    }

    private static CustomModelData modelData(String model) {
        return new CustomModelData(List.of(), List.of(), List.of(model), List.of());
    }

    private static void validateCompartment(int compartment) {
        if (compartment < 0 || compartment >= COMPARTMENT_COUNT) {
            throw new IllegalArgumentException("Invalid Dark Bucket compartment: " + compartment);
        }
    }
}