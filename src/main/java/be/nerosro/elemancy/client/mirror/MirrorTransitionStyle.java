package be.nerosro.elemancy.client.mirror;

import net.minecraft.util.RandomSource;

/**
 * Reversible visual styles used when a Mirror reflection enters or exits.
 */
public enum MirrorTransitionStyle {
    CENTER_EXPANSION,
    TV_SCANLINE,
    RISE,
    SCAN,
    FLICKER,
    MATERIALIZE,
    PIXEL_DISSOLVE;

    private static final MirrorTransitionStyle[] VALUES = values();

    public boolean isFlicker() {
        return this == FLICKER;
    }

    public static MirrorTransitionStyle random(RandomSource random) {
        return VALUES[random.nextInt(VALUES.length)];
    }
}