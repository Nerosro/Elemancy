package be.nerosro.elemancy.mana.depth;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Depth/failure tuning config entries and accessors.
 * Keeps gameplay tuning separate from the root config class.
 */
public final class ManaDepthValues {
    private ManaDepthValues() {
    }

    private static ModConfigSpec.DoubleValue depth1MinMana;
    private static ModConfigSpec.DoubleValue depth2MinMana;
    private static ModConfigSpec.DoubleValue depth3MinMana;

    private static ModConfigSpec.DoubleValue failNullspark;
    private static ModConfigSpec.DoubleValue failDepth1;
    private static ModConfigSpec.DoubleValue failDepth2;
    private static ModConfigSpec.DoubleValue failDepth3;
    private static ModConfigSpec.DoubleValue failDepth4;
    private static ModConfigSpec.DoubleValue failMaxCap;

    private static ModConfigSpec.IntValue scarArcaneTremorDurationTicks;
    private static ModConfigSpec.IntValue scarSpellDriftDurationTicks;
    private static ModConfigSpec.IntValue scarChannelDisruptionDurationTicks;
    private static ModConfigSpec.IntValue scarEnergeticDurationTicks;
    private static ModConfigSpec.IntValue scarManaCollapseDurationTicks;

    private static ModConfigSpec.DoubleValue castCostChannelDisruptionMultiplier;

    private static ModConfigSpec.BooleanValue depthEnabled;

    public static void register(ModConfigSpec.Builder builder) {
        depthEnabled = builder
            .comment("Whether the depth/overcast system is active. When disabled, mana cannot go negative and scars will not apply.")
            .define("depthEnabled", true);

        builder.push("depth");
        depth1MinMana = builder
            .comment("Lower boundary of Depth 1. When mana drops below this, the player enters Depth 2.")
            .defineInRange("depth1MinMana", -20.0, -500.0, 0.0);
        depth2MinMana = builder
            .comment("Lower boundary of Depth 2. When mana drops below this, the player enters Depth 3.")
            .defineInRange("depth2MinMana", -50.0, -500.0, 0.0);
        depth3MinMana = builder
            .comment("Lower boundary of Depth 3. When mana drops below this, the player enters Depth 4 (instant death for non-Overwrought players).")
            .defineInRange("depth3MinMana", -115.0, -500.0, 0.0);
        builder.pop();

        builder.push("spellFailure");
        failNullspark = builder
            .comment("Failure chance at non-overcast mana when the player has Nullspark.")
            .defineInRange("nullspark", 0.10, 0.0, 1.0);
        failDepth1 = builder
            .comment("Base failure chance for Depth 1 casts.")
            .defineInRange("depth1", 0.05, 0.0, 1.0);
        failDepth2 = builder
            .comment("Base failure chance for Depth 2 casts.")
            .defineInRange("depth2", 0.15, 0.0, 1.0);
        failDepth3 = builder
            .comment("Base failure chance for Depth 3 casts.")
            .defineInRange("depth3", 0.65, 0.0, 1.0);
        failDepth4 = builder
            .comment("Base failure chance for Depth 4 casts.")
            .defineInRange("depth4", 0.85, 0.0, 1.0);
        failMaxCap = builder
            .comment("Maximum final failure chance after all modifiers.")
            .defineInRange("maxCap", 0.95, 0.0, 1.0);
        builder.pop();

        builder.push("scarDurations");
        scarArcaneTremorDurationTicks = builder
            .comment("Arcane Tremor duration in ticks.")
            .defineInRange("arcaneTremorTicks", 24_000, 20, 240_000);
        scarSpellDriftDurationTicks = builder
            .comment("Spell Drift duration in ticks.")
            .defineInRange("spellDriftTicks", 24_000, 20, 240_000);
        scarChannelDisruptionDurationTicks = builder
            .comment("Channel Disruption duration in ticks.")
            .defineInRange("channelDisruptionTicks", 12_000, 20, 240_000);
        scarEnergeticDurationTicks = builder
            .comment("Energetic scar duration in ticks.")
            .defineInRange("energeticScarTicks", 72_000, 20, 240_000);
        scarManaCollapseDurationTicks = builder
            .comment("Mana Collapse duration in ticks.")
            .defineInRange("manaCollapseTicks", 120_000, 20, 240_000);
        builder.pop();

        builder.push("castCost");
        castCostChannelDisruptionMultiplier = builder
            .comment("Cast cost multiplier applied while Channel Disruption is active.")
            .defineInRange("channelDisruptionMultiplier", 1.25, 1.0, 5.0);
        builder.pop();
    }

    public static boolean depthEnabled() {
        return depthEnabled.get();
    }

    public static float depth1MinMana() {
        return depth1MinMana.get().floatValue();
    }

    public static float depth2MinMana() {
        return depth2MinMana.get().floatValue();
    }

    public static float depth3MinMana() {
        return depth3MinMana.get().floatValue();
    }

    public static float failNullspark() {
        return failNullspark.get().floatValue();
    }

    public static float failDepth1() {
        return failDepth1.get().floatValue();
    }

    public static float failDepth2() {
        return failDepth2.get().floatValue();
    }

    public static float failDepth3() {
        return failDepth3.get().floatValue();
    }

    public static float failDepth4() {
        return failDepth4.get().floatValue();
    }

    public static float failMaxCap() {
        return failMaxCap.get().floatValue();
    }

    public static int scarArcaneTremorDurationTicks() {
        return scarArcaneTremorDurationTicks.get();
    }

    public static int scarSpellDriftDurationTicks() {
        return scarSpellDriftDurationTicks.get();
    }

    public static int scarChannelDisruptionDurationTicks() {
        return scarChannelDisruptionDurationTicks.get();
    }

    public static int scarEnergeticDurationTicks() {
        return scarEnergeticDurationTicks.get();
    }

    public static int scarManaCollapseDurationTicks() {
        return scarManaCollapseDurationTicks.get();
    }

    public static float castCostChannelDisruptionMultiplier() {
        return castCostChannelDisruptionMultiplier.get().floatValue();
    }
}
