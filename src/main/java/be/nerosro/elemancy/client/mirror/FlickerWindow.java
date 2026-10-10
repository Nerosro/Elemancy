package be.nerosro.elemancy.client.mirror;

import net.minecraft.util.RandomSource;

/**
 * The two short visible windows before a flickering reflection settles.
 */
public enum FlickerWindow {
    FIRST(0.15f, 0.28f),
    SECOND(0.42f, 0.58f);

    private static final FlickerWindow[] VALUES = values();

    private final float start;
    private final float end;

    FlickerWindow(float start, float end) {
        this.start = start;
        this.end = end;
    }

    public boolean contains(float progress) {
        return progress >= start && progress < end;
    }

    public static FlickerWindow random(RandomSource random) {
        return VALUES[random.nextInt(VALUES.length)];
    }

    public static boolean isVisible(float progress) {
        for (FlickerWindow window : VALUES) {
            if (window.contains(progress)) return true;
        }
        return false;
    }
}