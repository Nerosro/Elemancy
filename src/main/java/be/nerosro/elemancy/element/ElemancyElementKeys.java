package be.nerosro.elemancy.element;

import java.util.List;

/**
 * The 6 base element registry keys (matching Soulmark's {@code SoulmarkElements} identifiers),
 * as plain strings. Used to register per-element content (Elemetal Blocks/Ingots) at class-load
 * time WITHOUT resolving Soulmark's {@code Element} registry objects - resolving those requires
 * the cross-mod registry to be frozen, which is not yet true at Elemancy's own class-load time.
 * Resolve the actual {@code Element} objects lazily instead, at lookup time (see
 * {@code ElemancyBlocks.getElemetalBlock(Element)} / {@code ElemancyItems.getElemetalIngot(Element)}).
 */
public final class ElemancyElementKeys {
    private ElemancyElementKeys() {
    }

    public static final List<String> BASE_ELEMENT_KEYS = List.of("fire", "water", "earth", "air", "light", "dark");
}
