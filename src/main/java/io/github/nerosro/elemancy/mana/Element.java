package io.github.nerosro.elemancy.mana;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Element {
    FIRE(1), WATER(2), EARTH(3), AIR(4), LIGHT(5), DARK(6);
    private final int elementId;

    Element(int elementId) {
        this.elementId = elementId;
    }

    public int getElementId() {
        return elementId;
    }

    private static final List<Element> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final SecureRandom RANDOM = new SecureRandom();

    public static Element getRandomElement()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}
