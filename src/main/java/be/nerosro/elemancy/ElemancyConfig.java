package be.nerosro.elemancy;

import be.nerosro.elemancy.mana.AffinityCostValues;
import be.nerosro.elemancy.mana.depth.ManaDepthValues;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Central config for Elemancy systems.
 * Produces a single elemancy-common.toml file.
 */
public class ElemancyConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {
        ManaDepthValues.register(BUILDER);
        AffinityCostValues.register(BUILDER);
    }

    static final ModConfigSpec SPEC = BUILDER.build();
}
