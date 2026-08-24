package be.nerosro.elemancy.spell.data;

import be.nerosro.elemancy.spell.SpellElement;

/**
 * Per-element default spell parameters for each shape.
 * Used by Spellwarp to cast any spell in any shape — when the shape
 * is randomized, these defaults provide sensible values for the alternate shape.
 * <p>
 * Real spell data lives in their respective data classes.
 * Spellwarp-only variants live in SpellwarpSpellData.
 */
public final class ElementSpellDefaults {
    private ElementSpellDefaults() {
    }

    // ── Projectile defaults ─────────────────────────────────────────────────

    public static ProjectileSpellData projectile(SpellElement element) {
        return switch (element) {
            case FIRE -> SpellwarpSpellData.FIRE_PROJECTILE;
            case WATER -> SpellwarpSpellData.WATER_PROJECTILE;
            case EARTH -> ProjectileSpellData.PEBBLE_SHOT;
            case AIR -> ProjectileSpellData.GUST_SLASH;
            case LIGHT -> SpellwarpSpellData.LIGHT_PROJECTILE;
            case DARK -> SpellwarpSpellData.DARK_PROJECTILE;
            case NONE -> ProjectileSpellData.ELEMENTAL_BLAST;
        };
    }

    // ── Beam defaults ───────────────────────────────────────────────────────

    public static BeamSpellData beam(SpellElement element) {
        return switch (element) {
            case FIRE -> SpellwarpSpellData.FIRE_BEAM;
            case WATER -> SpellwarpSpellData.WATER_BEAM;
            case EARTH -> SpellwarpSpellData.EARTH_BEAM;
            case AIR -> SpellwarpSpellData.AIR_BEAM;
            case LIGHT -> BeamSpellData.LIGHT_DART;
            case DARK -> BeamSpellData.SHADOW_FLICK;
            case NONE -> SpellwarpSpellData.NONE_BEAM;
        };
    }

    // ── Continuous defaults ─────────────────────────────────────────────────

    public static ContinuousSpellData continuous(SpellElement element) {
        return switch (element) {
            case FIRE -> ContinuousSpellData.FIRE_BLAST;
            case WATER -> ContinuousSpellData.WATER_JET;
            case EARTH -> SpellwarpSpellData.EARTH_CONTINUOUS;
            case AIR -> SpellwarpSpellData.AIR_CONTINUOUS;
            case LIGHT -> SpellwarpSpellData.LIGHT_CONTINUOUS;
            case DARK -> SpellwarpSpellData.DARK_CONTINUOUS;
            case NONE -> SpellwarpSpellData.NONE_CONTINUOUS;
        };
    }
}
