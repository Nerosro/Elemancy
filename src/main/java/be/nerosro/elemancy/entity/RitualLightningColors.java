package be.nerosro.elemancy.entity;

import be.nerosro.soulmark.element.Element;
import be.nerosro.soulmark.element.SoulmarkElements;

/**
 * Lightning-specific tints preserve element readability against the effect's bright blending.
 */
final class RitualLightningColors {

    private static final int EARTH = 0xFFB67823;
    private static final int AIR = 0xFFB6FF3D;
    private static final int LIGHT = 0xFFFFE12B;
    private static final int DARK = 0xFF210A2E;

    private RitualLightningColors() {
    }

    static int forElement(Element element) {
        if (element == SoulmarkElements.EARTH.get()) return EARTH;
        if (element == SoulmarkElements.AIR.get()) return AIR;
        if (element == SoulmarkElements.LIGHT.get()) return LIGHT;
        if (element == SoulmarkElements.DARK.get()) return DARK;
        return element.rgb();
    }

    static boolean isDark(Element element) {
        return element == SoulmarkElements.DARK.get();
    }
}