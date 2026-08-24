package be.nerosro.elemancy.spell.data;

import be.nerosro.elemancy.ElemancyColors;
import be.nerosro.elemancy.spell.SpellElement;
import be.nerosro.soulmark.element.SoulmarkElements;

/**
 * Cached spell data for Spellwarp shape-shifted variants.
 * These represent how each element "feels" when cast in a shape it doesn't naturally use.
 * Real spells live in their respective data classes (ProjectileSpellData, BeamSpellData, etc.).
 */
public final class SpellwarpSpellData {
    private SpellwarpSpellData() {
    }

    // ── Projectile variants (for elements whose real spell is beam or continuous) ──

    public static final ProjectileSpellData FIRE_PROJECTILE = new ProjectileSpellData(
        SpellElement.FIRE, 8.0f, 3.5f, 1.2f, 0.02, 1.5f,
        SoulmarkElements.FIRE.get().rgb(),
        OnHitEffect.IGNITE,
        SpellVisual.PROJECTILE_PARTICLE_TRAIL);

    public static final ProjectileSpellData WATER_PROJECTILE = new ProjectileSpellData(
        SpellElement.WATER, 8.0f, 3.0f, 1.5f, 0.01, 0.8f,
        SoulmarkElements.WATER.get().rgb(),
        OnHitEffect.KNOCKBACK,
        SpellVisual.PROJECTILE_PARTICLE_TRAIL);

    public static final ProjectileSpellData LIGHT_PROJECTILE = new ProjectileSpellData(
        SpellElement.LIGHT, 10.0f, 4.0f, 2.5f, 0.0, 0.2f,
        SoulmarkElements.LIGHT.get().rgb(),
        OnHitEffect.NONE,
        SpellVisual.PROJECTILE_PARTICLE_TRAIL);

    public static final ProjectileSpellData DARK_PROJECTILE = new ProjectileSpellData(
        SpellElement.DARK, 7.0f, 3.0f, 1.8f, 0.0, 1.0f,
        SoulmarkElements.DARK.get().rgb(),
        OnHitEffect.NONE,
        SpellVisual.PROJECTILE_PARTICLE_TRAIL);

    // ── Beam variants (for elements whose real spell is projectile or continuous) ──

    public static final BeamSpellData FIRE_BEAM = new BeamSpellData(
        SpellElement.FIRE, 10.0f, 4.0f, 16.0,
        SoulmarkElements.FIRE.get().rgb(),
        SpellVisual.BEAM_THIN_LINGER);

    public static final BeamSpellData WATER_BEAM = new BeamSpellData(
        SpellElement.WATER, 10.0f, 3.5f, 24.0,
        SoulmarkElements.WATER.get().rgb(),
        SpellVisual.BEAM_WIDE_BURST);

    public static final BeamSpellData EARTH_BEAM = new BeamSpellData(
        SpellElement.EARTH, 12.0f, 5.0f, 12.0,
        SoulmarkElements.EARTH.get().rgb(),
        SpellVisual.BEAM_HITSCAN);

    public static final BeamSpellData AIR_BEAM = new BeamSpellData(
        SpellElement.AIR, 8.0f, 2.5f, 20.0,
        SoulmarkElements.AIR.get().rgb(),
        SpellVisual.BEAM_THIN_LINGER);

    public static final BeamSpellData NONE_BEAM = new BeamSpellData(
        SpellElement.NONE, 8.0f, 3.0f, 20.0,
        ElemancyColors.BLAST_PARTICLE.rgb(),
        SpellVisual.BEAM_THIN_LINGER);

    // ── Continuous variants (for elements whose real spell is projectile or beam) ──

    public static final ContinuousSpellData EARTH_CONTINUOUS = new ContinuousSpellData(
        SpellElement.EARTH, 6.0f, 0.6f, 2.5f, 5, 5.0, 15.0f, 0f,
        SoulmarkElements.EARTH.get().rgb());

    public static final ContinuousSpellData AIR_CONTINUOUS = new ContinuousSpellData(
        SpellElement.AIR, 4.0f, 0.4f, 1.0f, 3, 8.0, 25.0f, 0.8f,
        SoulmarkElements.AIR.get().rgb());

    public static final ContinuousSpellData LIGHT_CONTINUOUS = new ContinuousSpellData(
        SpellElement.LIGHT, 6.0f, 0.7f, 2.0f, 4, 10.0, 0f, 0f,
        SoulmarkElements.LIGHT.get().rgb());

    public static final ContinuousSpellData DARK_CONTINUOUS = new ContinuousSpellData(
        SpellElement.DARK, 5.0f, 0.5f, 1.5f, 4, 8.0, 10.0f, 0f,
        SoulmarkElements.DARK.get().rgb());

    public static final ContinuousSpellData NONE_CONTINUOUS = new ContinuousSpellData(
        SpellElement.NONE, 5.0f, 0.5f, 1.5f, 4, 6.0, 15.0f, 0f,
        ElemancyColors.BLAST_PARTICLE.rgb());
}
