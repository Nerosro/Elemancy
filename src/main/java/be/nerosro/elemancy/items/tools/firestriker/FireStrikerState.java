package be.nerosro.elemancy.items.tools.firestriker;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

/**
 * Persisted on each stoked furnace or campfire block entity.
 * Tracks whether the heat source is stoked and how many items a campfire has cooked while stoked.
 * Serialization keeps an active stoke session intact across chunk unloads and world reloads.
 */
public final class FireStrikerState implements ValueIOSerializable {
    private static final String TAG_STOKED = "stoked";
    private static final String TAG_COMPLETED_ITEMS = "completed_items";

    private boolean stoked;
    private int completedItems;

    public boolean isStoked() {
        return stoked;
    }

    public void stoke() {
        stoked = true;
    }

    public int getCompletedItems() {
        return completedItems;
    }

    public void recordCompletedItem() {
        completedItems++;
    }

    public void reset() {
        stoked = false;
        completedItems = 0;
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putBoolean(TAG_STOKED, stoked);
        output.putInt(TAG_COMPLETED_ITEMS, completedItems);
    }

    @Override
    public void deserialize(ValueInput input) {
        stoked = input.getBooleanOr(TAG_STOKED, false);
        completedItems = Math.max(0, input.getIntOr(TAG_COMPLETED_ITEMS, 0));
    }
}