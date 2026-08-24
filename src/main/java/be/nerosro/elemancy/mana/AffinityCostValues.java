package be.nerosro.elemancy.mana;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Config entries for affinity-based spell cost modifiers.
 * Controls how the player's birth affinity interacts with spell element costs.
 */
public final class AffinityCostValues {
    private AffinityCostValues() {
    }

    private static ModConfigSpec.BooleanValue enabled;
    private static ModConfigSpec.DoubleValue attunedMultiplier;
    private static ModConfigSpec.DoubleValue adjacentMultiplier;
    private static ModConfigSpec.DoubleValue opposingMultiplier;

    public static void register(ModConfigSpec.Builder builder) {
        builder.push("affinityCost");
        enabled = builder
            .comment("Whether affinity-based cost modifiers are applied.")
            .define("enabled", true);
        attunedMultiplier = builder
            .comment("Cost multiplier when the spell element matches the player's affinity.")
            .defineInRange("attunedMultiplier", 0.90, 0.50, 1.0);
        adjacentMultiplier = builder
            .comment("Cost multiplier when the spell element is adjacent (neither attuned nor opposing).")
            .defineInRange("adjacentMultiplier", 1.0, 0.80, 1.20);
        opposingMultiplier = builder
            .comment("Cost multiplier when the spell element opposes the player's affinity.")
            .defineInRange("opposingMultiplier", 1.15, 1.0, 2.0);
        builder.pop();
    }

    public static boolean enabled() {
        return enabled.get();
    }

    public static float attunedMultiplier() {
        return attunedMultiplier.get().floatValue();
    }

    public static float adjacentMultiplier() {
        return adjacentMultiplier.get().floatValue();
    }

    public static float opposingMultiplier() {
        return opposingMultiplier.get().floatValue();
    }
}

