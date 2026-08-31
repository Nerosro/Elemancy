package be.nerosro.elemancy.traits;

import java.util.List;

import be.nerosro.elemancy.spell.SpellContext;
import be.nerosro.elemancy.spell.SpellElement;
import be.nerosro.soulmark.affinity.AffinityUtil;
import be.nerosro.soulmark.traits.Trait;
import be.nerosro.soulmark.traits.TraitUtil;
import net.minecraft.world.entity.player.Player;

/**
 * Resolves trait-based multipliers for the cast pipeline.
 * Cost modifiers affect how much mana a spell costs.
 * Output modifiers affect how strong a spell's effect is (damage, defense, duration).
 */
public final class TraitModifiers {
    private record ElementalTraitNames(String boost, String penalty) {
    }

    private TraitModifiers() {
    }

    // ============================================================
    // COST MODIFIERS — applied in applyCastCostModifiers pipeline
    // ============================================================

    /**
     * Returns the combined trait-based cost multiplier for the given spell context.
     * Includes global and spell-element-specific cost traits.
     */
    public static float costMultiplier(Player player, SpellContext context) {
        float multiplier = 1f;

        List<Trait> boosts = TraitUtil.getBoostTraits(player);
        List<Trait> penalties = TraitUtil.getPenaltyTraits(player);

        // Efficient: global cost reduction
        multiplier *= (1f - getAmountByName(boosts, TraitNames.EFFICIENT));

        // Wasteful: global cost increase
        multiplier *= (1f + getAmountByName(penalties, TraitNames.WASTEFUL));

        ElementalTraitNames elementalTraits = elementalTraits(context.element());
        multiplier *= 1f - getAmountByName(boosts, elementalTraits.boost());
        multiplier *= 1f + getAmountByName(penalties, elementalTraits.penalty());

        return multiplier;
    }

    // ============================================================
    // OUTPUT MODIFIERS — called by spells when computing effect
    // ============================================================

    /**
     * Returns the combined trait-based damage multiplier for an attack spell.
     * Includes global, affinity, and spell-element-specific damage traits.
     */
    public static float damageMultiplier(Player player, SpellContext context) {
        float multiplier = 1f;

        List<Trait> boosts = TraitUtil.getBoostTraits(player);
        List<Trait> penalties = TraitUtil.getPenaltyTraits(player);

        // Aggressive: global damage boost
        multiplier *= (1f + getAmountByName(boosts, TraitNames.AGGRESSIVE));

        // Timid: global damage reduction
        multiplier *= (1f - getAmountByName(penalties, TraitNames.TIMID));

        // Resonant: attuned element spells are stronger
        if (context.element() != SpellElement.NONE
            && AffinityUtil.hasAffinity(player, context.element().toElement())) {
            multiplier *= (1f + getAmountByName(boosts, TraitNames.RESONANT));
        }

        // Dissonant: opposing element spells are weaker
        if (context.element() != SpellElement.NONE
            && AffinityUtil.hasOppositeAffinity(player, context.element().toElement())) {
            multiplier *= (1f - getAmountByName(penalties, TraitNames.DISSONANT));
        }

        ElementalTraitNames elementalTraits = elementalTraits(context.element());
        multiplier *= 1f + getAmountByName(boosts, elementalTraits.boost());
        multiplier *= 1f - getAmountByName(penalties, elementalTraits.penalty());
        return multiplier;
    }

    /**
     * Returns the trait-based duration multiplier for shields and barriers.
     * Includes: Guardian.
     */
    public static float shieldDurationMultiplier(Player player) {
        List<Trait> boosts = TraitUtil.getBoostTraits(player);
        return 1f + getAmountByName(boosts, TraitNames.GUARDIAN);
    }

    // ============================================================
    // INTERNAL HELPERS
    // ============================================================

    /**
     * Finds the first trait matching the given name and returns its amount value.
     * Returns 0 if not found.
     */
    private static float getAmountByName(List<Trait> traits, String traitName) {
        for (Trait trait : traits) {
            if (trait.name().equalsIgnoreCase(traitName)) {
                return trait.value();
            }
        }
        return 0f;
    }

    private static ElementalTraitNames elementalTraits(SpellElement element) {
        return switch (element) {
            case EARTH -> new ElementalTraitNames(TraitNames.STURDY, TraitNames.FRAGILE);
            case AIR -> new ElementalTraitNames(TraitNames.EVASIVE, TraitNames.ANCHORED);
            case FIRE -> new ElementalTraitNames(TraitNames.HOT_BLOODED, TraitNames.COLD_HEARTED);
            case WATER -> new ElementalTraitNames(TraitNames.LIVELY, TraitNames.STAGNANT);
            case LIGHT -> new ElementalTraitNames(TraitNames.PRISMATIC, TraitNames.OPAQUE);
            case DARK -> new ElementalTraitNames(TraitNames.CLOAKED, TraitNames.EXPOSED);
            case NONE -> new ElementalTraitNames("", "");
        };
    }

}


