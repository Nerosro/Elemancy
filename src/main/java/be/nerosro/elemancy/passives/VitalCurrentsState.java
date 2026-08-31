package be.nerosro.elemancy.passives;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

/** Stores the cooldown for Water-attuned Vital Currents regeneration pulses. */
public final class VitalCurrentsState implements ValueIOSerializable {
    private static final String TAG_NEXT_TRIGGER_TIME = "next_trigger_time";

    private long nextTriggerTime;

    public boolean isReady(long gameTime) {
        return gameTime >= nextTriggerTime;
    }

    public void trigger(long gameTime, long cooldownTicks) {
        nextTriggerTime = gameTime + cooldownTicks;
    }

    @Override
    public void serialize(ValueOutput output) {
        output.putLong(TAG_NEXT_TRIGGER_TIME, nextTriggerTime);
    }

    @Override
    public void deserialize(ValueInput input) {
        nextTriggerTime = Math.max(0L, input.getLongOr(TAG_NEXT_TRIGGER_TIME, 0L));
    }
}