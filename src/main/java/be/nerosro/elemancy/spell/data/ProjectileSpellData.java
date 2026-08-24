package be.nerosro.elemancy.spell.data;

import be.nerosro.elemancy.ElemancyColors;
import be.nerosro.elemancy.spell.SpellCategory;
import be.nerosro.elemancy.spell.SpellContext;
import be.nerosro.elemancy.spell.SpellElement;
import be.nerosro.elemancy.spell.SpellShape;
import be.nerosro.soulmark.element.SoulmarkElements;

/**
 * Per-element configuration for projectile-type attack spells.
 * Passed into the projectile handler at registration time to parameterize behavior.
 */
public record ProjectileSpellData(
    SpellElement element,
    float manaCost,
    float baseDamage,
    float speed,
    double gravity,
    float inaccuracy,
    int particleColor,
    OnHitEffect onHitEffect,
    SpellVisual visual
) {
    public SpellContext toContext() {
        return SpellContext.of(element, SpellCategory.ATTACK, SpellShape.PROJECTILE);
    }

    // ── Predefined spell configurations ─────────────────────────────────────

    public static final ProjectileSpellData ELEMENTAL_BLAST = new ProjectileSpellData(
        SpellElement.NONE, 8.0f, 3.0f, 1.5f, 0.01, 1.0f,
        ElemancyColors.BLAST_PARTICLE.rgb(), OnHitEffect.NONE, SpellVisual.PROJECTILE_PARTICLE_TRAIL);

    public static final ProjectileSpellData PEBBLE_SHOT = new ProjectileSpellData(
        SpellElement.EARTH, 8.0f, 4.0f, 1.0f, 0.05, 0.5f,
        SoulmarkElements.EARTH.get().rgb(), OnHitEffect.NONE, SpellVisual.PROJECTILE_ROCK);

    public static final ProjectileSpellData GUST_SLASH = new ProjectileSpellData(
        SpellElement.AIR, 6.0f, 2.0f, 2.0f, 0.0, 0.3f,
        SoulmarkElements.AIR.get().rgb(), OnHitEffect.KNOCKBACK, SpellVisual.PROJECTILE_CRESCENT);
}
