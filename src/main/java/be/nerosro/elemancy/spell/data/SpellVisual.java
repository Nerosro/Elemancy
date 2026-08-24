package be.nerosro.elemancy.spell.data;

/**
 * Visual style for a spell's rendering. Prefixed by shape type for clarity.
 */
public enum SpellVisual {
    // ── Beam styles ─────────────────────────────────────────────────────────

    /**
     * Thin lingering line (Light Dart style).
     */
    BEAM_THIN_LINGER,
    /**
     * Wide beam like water from a hose.
     */
    BEAM_WIDE_BURST,
    /**
     * No beam visual — instant hit with contact particles only (Shadow Flick style).
     */
    BEAM_HITSCAN,

    // ── Projectile styles ───────────────────────────────────────────────────

    /**
     * Invisible entity with dust particle trail (Elemental Blast).
     */
    PROJECTILE_PARTICLE_TRAIL,
    /**
     * Tumbling rock textured quad (Pebble Shot).
     */
    PROJECTILE_ROCK,
    /**
     * Spinning crescent textured quad with random angle per cast (Gust Slash).
     */
    PROJECTILE_CRESCENT
}
