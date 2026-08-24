package be.nerosro.elemancy.spell.data;

import java.util.HashMap;
import java.util.Map;

import be.nerosro.elemancy.skilltree.SkillTreeEntries;
import be.nerosro.elemancy.spell.SpellCategory;
import be.nerosro.elemancy.spell.SpellContext;
import be.nerosro.elemancy.spell.SpellElement;
import be.nerosro.elemancy.spell.SpellShape;
import be.nerosro.soulmark.element.SoulmarkElements;
import net.minecraft.resources.Identifier;

/**
 * Per-element configuration for continuous cast (hold-to-spray) spells.
 * Fire Blast uses a cone; Water Jet uses a focused line (coneHalfAngle = 0).
 */
public record ContinuousSpellData(
    SpellElement element,
    float initialManaCost,
    float manaPerTick,
    float damagePerHit,
    int damageIntervalTicks,
    double range,
    float coneHalfAngleDeg,
    float knockbackStrength,
    int particleColor
) {
    private static final Map<Identifier, ContinuousSpellData> REGISTRY = new HashMap<>();

    public SpellContext toContext() {
        return SpellContext.of(element, SpellCategory.ATTACK, SpellShape.CONTINUOUS);
    }

    /**
     * True if this spell uses a cone shape (fire). False = focused line (water).
     */
    public boolean isCone() {
        return coneHalfAngleDeg > 0;
    }

    /**
     * Look up the continuous spell data for a given spell ID.
     */
    public static ContinuousSpellData get(Identifier id) {
        return REGISTRY.get(id);
    }

    // ── Predefined spell configurations ─────────────────────────────────────

    public static final ContinuousSpellData FIRE_BLAST = register(SkillTreeEntries.FIRE_BLAST_ID,
        new ContinuousSpellData(
            SpellElement.FIRE,
            5.0f,       // initial mana cost to start channel
            0.5f,       // mana per tick (10/sec)
            2.0f,       // damage per hit
            4,          // damage every 4 ticks (5x/sec)
            6.0,        // 6 block range
            20.0f,      // 20° half-angle (40° total cone)
            0f,         // no knockback
            SoulmarkElements.FIRE.get().rgb()
        ));

    public static final ContinuousSpellData WATER_JET = register(SkillTreeEntries.WATER_JET_ID,
        new ContinuousSpellData(
            SpellElement.WATER,
            5.0f,       // initial mana cost
            0.5f,       // mana per tick (10/sec)
            1.5f,       // damage per hit (lower than fire, compensated by knockback)
            4,          // damage every 4 ticks
            12.0,       // 12 block range
            0f,         // line (raycast)
            0.5f,       // knockback per hit
            SoulmarkElements.WATER.get().rgb()
        ));

    private static ContinuousSpellData register(Identifier id, ContinuousSpellData data) {
        REGISTRY.put(id, data);
        return data;
    }
}
