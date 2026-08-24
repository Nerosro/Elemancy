package be.nerosro.elemancy.spell;

import be.nerosro.elemancy.skilltree.SkillTreeEntries;
import be.nerosro.elemancy.spell.casting.BeamCaster;
import be.nerosro.elemancy.spell.casting.ProjectileCaster;
import be.nerosro.elemancy.spell.casting.SpellDispatcher;
import be.nerosro.elemancy.spell.data.BeamSpellData;
import be.nerosro.elemancy.spell.data.ContinuousSpellData;
import be.nerosro.elemancy.spell.data.ProjectileSpellData;

/**
 * Registers all Elemancy spells with the SpellRegistry (metadata)
 * and SpellDispatcher (one-shot handlers).
 * Called during commonSetup. Adding a new spell only requires a single line here.
 */
public final class ElemancySpells {
    private ElemancySpells() {
    }

    public static void register() {
        // ── Metadata registration (all spells) ──────────────────────────────────

        SpellRegistry.register(SkillTreeEntries.ELEMENTIZE_ID,
            SpellElement.NONE, SpellCategory.INFUSION, SpellShape.PROJECTILE);

        SpellRegistry.register(SkillTreeEntries.ELEMENTAL_BLAST_ID,
            SpellElement.NONE, SpellCategory.ATTACK, SpellShape.PROJECTILE);

        SpellRegistry.register(SkillTreeEntries.PEBBLE_SHOT_ID,
            SpellElement.EARTH, SpellCategory.ATTACK, SpellShape.PROJECTILE);

        SpellRegistry.register(SkillTreeEntries.GUST_SLASH_ID,
            SpellElement.AIR, SpellCategory.ATTACK, SpellShape.PROJECTILE);

        SpellRegistry.register(SkillTreeEntries.FIRE_BLAST_ID,
            SpellElement.FIRE, SpellCategory.ATTACK, SpellShape.CONTINUOUS);

        SpellRegistry.register(SkillTreeEntries.WATER_JET_ID,
            SpellElement.WATER, SpellCategory.ATTACK, SpellShape.CONTINUOUS);

        SpellRegistry.register(SkillTreeEntries.LIGHT_DART_ID,
            SpellElement.LIGHT, SpellCategory.ATTACK, SpellShape.BEAM);

        SpellRegistry.register(SkillTreeEntries.SHADOW_FLICK_ID,
            SpellElement.DARK, SpellCategory.ATTACK, SpellShape.BEAM);

        // ── One-shot handlers (projectile + beam spells only) ───────────────────

        SpellDispatcher.registerHandler(SkillTreeEntries.ELEMENTAL_BLAST_ID,
            ProjectileCaster.projectileHandler(ProjectileSpellData.ELEMENTAL_BLAST));

        SpellDispatcher.registerHandler(SkillTreeEntries.PEBBLE_SHOT_ID,
            ProjectileCaster.projectileHandler(ProjectileSpellData.PEBBLE_SHOT));

        SpellDispatcher.registerHandler(SkillTreeEntries.GUST_SLASH_ID,
            ProjectileCaster.projectileHandler(ProjectileSpellData.GUST_SLASH));

        SpellDispatcher.registerHandler(SkillTreeEntries.LIGHT_DART_ID,
            BeamCaster.beamHandler(BeamSpellData.LIGHT_DART));

        SpellDispatcher.registerHandler(SkillTreeEntries.SHADOW_FLICK_ID,
            BeamCaster.beamHandler(BeamSpellData.SHADOW_FLICK));

        // ── Mana cost labels (used in Tome spell entries) ───────────────────────

        SpellRegistry.registerManaCost(SkillTreeEntries.ELEMENTIZE_ID, "0 mana");
        SpellRegistry.registerManaCost(SkillTreeEntries.ELEMENTAL_BLAST_ID, formatFlat(ProjectileSpellData.ELEMENTAL_BLAST.manaCost()));
        SpellRegistry.registerManaCost(SkillTreeEntries.PEBBLE_SHOT_ID, formatFlat(ProjectileSpellData.PEBBLE_SHOT.manaCost()));
        SpellRegistry.registerManaCost(SkillTreeEntries.GUST_SLASH_ID, formatFlat(ProjectileSpellData.GUST_SLASH.manaCost()));
        SpellRegistry.registerManaCost(SkillTreeEntries.FIRE_BLAST_ID, formatContinuous(ContinuousSpellData.FIRE_BLAST.initialManaCost(), ContinuousSpellData.FIRE_BLAST.manaPerTick()));
        SpellRegistry.registerManaCost(SkillTreeEntries.WATER_JET_ID, formatContinuous(ContinuousSpellData.WATER_JET.initialManaCost(), ContinuousSpellData.WATER_JET.manaPerTick()));
        SpellRegistry.registerManaCost(SkillTreeEntries.LIGHT_DART_ID, formatFlat(BeamSpellData.LIGHT_DART.manaCost()));
        SpellRegistry.registerManaCost(SkillTreeEntries.SHADOW_FLICK_ID, formatFlat(BeamSpellData.SHADOW_FLICK.manaCost()));
    }

    private static String formatFlat(float cost) {
        return (cost == (int) cost ? String.valueOf((int) cost) : String.valueOf(cost)) + " mana per cast";
    }

    private static String formatContinuous(float initial, float perTick) {
        float perSec = perTick * 20f;
        String init = initial == (int) initial ? String.valueOf((int) initial) : String.valueOf(initial);
        String sec = perSec == (int) perSec ? String.valueOf((int) perSec) : String.valueOf(perSec);
        return init + " initial + " + sec + " mana/sec";
    }
}


