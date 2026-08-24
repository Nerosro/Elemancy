package be.nerosro.elemancy.mana.depth;

import org.jspecify.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;

/**
 * Defines all scar types with their NBT keys, stack limits, and penalty parameters.
 * Ordinal determines the bit index for network sync — add new scars at the end to preserve wire compatibility.
 */
public enum ScarType {
    ARCANE_TREMOR("arcane_tremor"),
    SPELL_DRIFT("spell_drift"),
    CHANNEL_DISRUPTION("channel_disruption"),
    MANA_BURN("mana_burn", 5, 0.10f, 0.50f),
    ARCANE_FATIGUE("arcane_fatigue", 5, 0.15f, 0.40f),
    SPELL_WEAKNESS("spell_weakness", 5, 0.05f, 0.75f),
    MANA_COLLAPSE("mana_collapse");

    private final String key;
    private final int maxStacks;
    private final float penaltyPerStack;
    private final float floor;

    ScarType(String key) {
        this(key, 0, 0f, 0f);
    }

    ScarType(String key, int maxStacks, float penaltyPerStack, float floor) {
        this.key = key;
        this.maxStacks = maxStacks;
        this.penaltyPerStack = penaltyPerStack;
        this.floor = floor;
    }

    public String tickKey() {
        return key + "_ticks";
    }

    public @Nullable String stackKey() {
        return maxStacks > 0 ? requiredStackKey() : null;
    }

    public String translationKey() {
        return "scar.elemancy." + key;
    }

    public int maxStacks() {
        return maxStacks;
    }

    /**
     * Computes the stat multiplier for this scar based on current stacks.
     * Returns 1.0 for non-stackable scars.
     */
    public float computeMultiplier(CompoundTag scars) {
        if (maxStacks == 0) return 1f;
        int stacks = scars.getInt(requiredStackKey()).orElse(0);
        return Math.max(floor, 1f - (penaltyPerStack * stacks));
    }

    public String requiredStackKey() {
        if (maxStacks == 0) {
            throw new IllegalStateException(name() + " does not stack");
        }
        return key + "_stacks";
    }

    public int bit() {
        return 1 << ordinal();
    }

    public boolean isActive(byte bitfield) {
        return (bitfield & bit()) != 0;
    }
}
