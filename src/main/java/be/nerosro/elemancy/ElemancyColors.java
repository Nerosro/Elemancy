package be.nerosro.elemancy;

/**
 * Central color palette for the Elemancy mod.
 * All colors used across HUD, particles, radial menus, and items are defined here.
 * This ensures visual consistency and makes it easy to adjust the mod's look.
 * <p>
 * Element colors (Fire, Water, etc.) live in SoulmarkElements — use element.argb()/rgb() directly.
 * Colors are stored as ARGB (0xAARRGGBB) unless noted otherwise.
 */
public enum ElemancyColors {

    // ==================== SPELL COLORS ====================

    /**
     * Raw mana / Infusion — bright cyan
     */
    MANA(0xFF49CEFF),

    /**
     * Transmutation — warm amber/gold
     */
    TRANSMUTE(0xFFE6A800),

    /**
     * Unattuned — neutral grey
     */
    UNATTUNED(0xFFB0B0B0),

    /**
     * Blast projectile trail — neutral light grey
     */
    BLAST_PARTICLE(0xFFCCCCCC),

    // ==================== BLOCK COLORS ====================

    /**
     * Ashen Leaves — bright cyan tint
     */
    ASHEN_LEAVES(0xFF55FFFF),

    // ==================== MANA HUD COLORS ====================

    /**
     * Depth 1 (slight debt) — yellow warning
     */
    HUD_MANA_DEPTH1(0xFFFFFF55),

    /**
     * Depth 2 (moderate debt) — orange
     */
    HUD_MANA_DEPTH2(0xFFFFAA00),

    /**
     * Depth 3 (severe debt) — red
     */
    HUD_MANA_DEPTH3(0xFFFF5555),

    /**
     * Depth 4 (critical / collapse) — dark red
     */
    HUD_MANA_DEPTH4(0xFFAA0000),

    /**
     * Mana collapse — grey (obfuscated)
     */
    HUD_MANA_COLLAPSE(0xFF555555);

    private final int argb;

    ElemancyColors(int argb) {
        this.argb = argb;
    }

    public int argb() {
        return argb;
    }

    public int rgb() {
        return argb & 0x00FFFFFF;
    }

    public float red() {
        return ((argb >> 16) & 0xFF) / 255.0f;
    }

    public float green() {
        return ((argb >> 8) & 0xFF) / 255.0f;
    }

    public float blue() {
        return (argb & 0xFF) / 255.0f;
    }
}
