package be.nerosro.elemancy.spell.data;

import be.nerosro.elemancy.spell.SpellCategory;
import be.nerosro.elemancy.spell.SpellContext;
import be.nerosro.elemancy.spell.SpellElement;
import be.nerosro.elemancy.spell.SpellShape;
import be.nerosro.soulmark.element.SoulmarkElements;

/**
 * Per-element configuration for beam and hitscan attack spells.
 * Beams show a visible line of particles; hitscan is instant with contact particles only.
 */
public record BeamSpellData(
    SpellElement element,
    float manaCost,
    float baseDamage,
    double maxRange,
    int particleColor,
    SpellVisual visual
) {
    public SpellContext toContext() {
        return SpellContext.of(element, SpellCategory.ATTACK, SpellShape.BEAM);
    }

    // ── Predefined spell configurations ─────────────────────────────────────

    public static final BeamSpellData LIGHT_DART = new BeamSpellData(
        SpellElement.LIGHT, 12.0f, 5.0f, 32.0, SoulmarkElements.LIGHT.get().rgb(),
        SpellVisual.BEAM_THIN_LINGER);

    public static final BeamSpellData SHADOW_FLICK = new BeamSpellData(
        SpellElement.DARK, 8.0f, 3.5f, 16.0, SoulmarkElements.DARK.get().rgb(),
        SpellVisual.BEAM_HITSCAN);
}
