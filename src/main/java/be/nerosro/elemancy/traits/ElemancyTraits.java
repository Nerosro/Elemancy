package be.nerosro.elemancy.traits;

import static be.nerosro.elemancy.traits.TraitNames.AGGRESSIVE;
import static be.nerosro.elemancy.traits.TraitNames.ANCHORED;
import static be.nerosro.elemancy.traits.TraitNames.CLOAKED;
import static be.nerosro.elemancy.traits.TraitNames.COLD_HEARTED;
import static be.nerosro.elemancy.traits.TraitNames.DISSONANT;
import static be.nerosro.elemancy.traits.TraitNames.EFFICIENT;
import static be.nerosro.elemancy.traits.TraitNames.EVASIVE;
import static be.nerosro.elemancy.traits.TraitNames.EXPOSED;
import static be.nerosro.elemancy.traits.TraitNames.FRAGILE;
import static be.nerosro.elemancy.traits.TraitNames.FRUGAL;
import static be.nerosro.elemancy.traits.TraitNames.GUARDIAN;
import static be.nerosro.elemancy.traits.TraitNames.HOT_BLOODED;
import static be.nerosro.elemancy.traits.TraitNames.LIVELY;
import static be.nerosro.elemancy.traits.TraitNames.OPAQUE;
import static be.nerosro.elemancy.traits.TraitNames.PRISMATIC;
import static be.nerosro.elemancy.traits.TraitNames.RESONANT;
import static be.nerosro.elemancy.traits.TraitNames.STAGNANT;
import static be.nerosro.elemancy.traits.TraitNames.STURDY;
import static be.nerosro.elemancy.traits.TraitNames.TIMID;
import static be.nerosro.elemancy.traits.TraitNames.WASTEFUL;

import java.util.function.Supplier;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.soulmark.traits.Trait;
import be.nerosro.soulmark.traits.TraitRegistries;
import be.nerosro.soulmark.traits.TraitType;
import be.nerosro.soulmark.traits.TraitWeight;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * All Elemancy trait registrations (boost, penalty, neutral).
 * Scalable traits are registered once per weight tier via {@link #scalable}.
 */
public class ElemancyTraits {

    public static final DeferredRegister<Trait> TRAITS =
        DeferredRegister.create(TraitRegistries.TRAIT_REGISTRY_KEY, Elemancy.MOD_ID);

    private static final TraitWeight[] SCALE_TIERS = {
        TraitWeight.COMMON, TraitWeight.UNCOMMON, TraitWeight.RARE, TraitWeight.LEGENDARY
    };

    /**
     * Registers a scalable trait across all 4 weight tiers.
     * The description template must contain exactly one {@code %d} placeholder for the percentage.
     */
    private static void scalable(TraitType type, String baseName, String name, String descTemplate, float... values) {
        for (int i = 0; i < SCALE_TIERS.length; i++) {
            TraitWeight weight = SCALE_TIERS[i];
            float val = values[i];
            TRAITS.register(baseName + "_" + weight.name().toLowerCase(),
                () -> new Trait(name, String.format(descTemplate, Math.round(val * 100)), type, weight, val));
        }
    }

    private static void elementalScalable(TraitType type, String baseName, String name, String element,
                                          boolean boost, float... values) {
        String direction = boost ? "more damage and cost" : "less damage and cost";
        String costDirection = boost ? "less" : "more";
        for (int i = 0; i < SCALE_TIERS.length; i++) {
            TraitWeight weight = SCALE_TIERS[i];
            float value = values[i];
            int percentage = Math.round(value * 100);
            TRAITS.register(baseName + "_" + weight.name().toLowerCase(),
                () -> new Trait(name, String.format("%s spells deal %d%% %s and cost %d%% %s mana.",
                    element, percentage, direction, percentage, costDirection), type, weight, value));
        }
    }

    // ── Boost (scalable) ────────────────────────────────────────────────────

    static {
        scalable(TraitType.BOOST, "efficient", EFFICIENT, "Spells cost %d%% less.", 0.05f, 0.08f, 0.11f, 0.15f);
        scalable(TraitType.BOOST, "aggressive", AGGRESSIVE, "Spells deal %d%% more damage.", 0.05f, 0.08f, 0.11f, 0.15f);
        elementalScalable(TraitType.BOOST, "sturdy", STURDY, "Earth", true, 0.05f, 0.08f, 0.11f, 0.15f);
        elementalScalable(TraitType.BOOST, "evasive", EVASIVE, "Air", true, 0.05f, 0.08f, 0.11f, 0.15f);
        elementalScalable(TraitType.BOOST, "hot_blooded", HOT_BLOODED, "Fire", true, 0.05f, 0.08f, 0.11f, 0.15f);
        elementalScalable(TraitType.BOOST, "lively", LIVELY, "Water", true, 0.05f, 0.08f, 0.11f, 0.15f);
        elementalScalable(TraitType.BOOST, "prismatic", PRISMATIC, "Light", true, 0.05f, 0.08f, 0.11f, 0.15f);
        elementalScalable(TraitType.BOOST, "cloaked", CLOAKED, "Dark", true, 0.05f, 0.08f, 0.11f, 0.15f);
        scalable(TraitType.BOOST, "resonant", RESONANT, "Spells of your attuned element are %d%% stronger.", 0.03f, 0.05f, 0.08f, 0.10f);
        scalable(TraitType.BOOST, "guardian", GUARDIAN, "Shields and barriers last %d%% longer.", 0.02f, 0.05f, 0.08f, 0.10f);
    }

    // ── Boost (non-scalable) ────────────────────────────────────────────────

    public static final Supplier<Trait> RAPID = TRAITS.register("rapid",
        () -> new Trait(TraitNames.RAPID, "Mana regeneration resumes faster after casting.", TraitType.BOOST, TraitWeight.UNCOMMON, 0f));

    public static final Supplier<Trait> BLUNT = TRAITS.register("blunt",
        () -> new Trait(TraitNames.BLUNT, "Spells deal more knockback.", TraitType.BOOST, TraitWeight.RARE, 0f));

    public static final Supplier<Trait> HASTY = TRAITS.register("hasty",
        () -> new Trait(TraitNames.HASTY, "The first spell cast after a period of not casting is 10% cheaper.", TraitType.BOOST, TraitWeight.RARE, 0.10f));

    public static final Supplier<Trait> DELAYED = TRAITS.register("delayed",
        () -> new Trait(TraitNames.DELAYED, "The first spell cast after a period of not casting is 10% stronger.", TraitType.BOOST, TraitWeight.RARE, 0.10f));

    public static final Supplier<Trait> SWIFT = TRAITS.register("swift",
        () -> new Trait(TraitNames.SWIFT, "Reduces the mana recovery delay by 33%.", TraitType.BOOST, TraitWeight.RARE, 0.33f));

    public static final Supplier<Trait> WILD = TRAITS.register("wild",
        () -> new Trait(TraitNames.WILD, "Spells occasionally trigger a small bonus effect.", TraitType.BOOST, TraitWeight.LEGENDARY, 0f));

    public static final Supplier<Trait> NOCTURNAL = TRAITS.register("nocturnal",
        () -> new Trait(TraitNames.NOCTURNAL, "Mana regenerates faster at night.", TraitType.BOOST, TraitWeight.LEGENDARY, 0f));

    public static final Supplier<Trait> DIURNAL = TRAITS.register("diurnal",
        () -> new Trait(TraitNames.DIURNAL, "Mana regenerates faster during the day.", TraitType.BOOST, TraitWeight.LEGENDARY, 0f));

    public static final Supplier<Trait> OVERWROUGHT = TRAITS.register("overwrought",
        () -> new Trait(TraitNames.OVERWROUGHT, "Reduces overcast damage, lowers scar chance, and gives a 50% chance to survive Depth 4 casts.", TraitType.BOOST, TraitWeight.EXOTIC, 0f));

    public static final Supplier<Trait> AFTERCAST = TRAITS.register("aftercast",
        () -> new Trait(TraitNames.AFTERCAST, "Spells occasionally repeat.", TraitType.BOOST, TraitWeight.EXOTIC, 0f));

    public static final Supplier<Trait> SURGEBOUND = TRAITS.register("surgebound",
        () -> new Trait(TraitNames.SURGEBOUND, "Holding an attack spell before releasing it causes it to consume more mana and scale in power.", TraitType.BOOST, TraitWeight.EXOTIC, 0f));

    // ── Penalty (scalable) ──────────────────────────────────────────────────

    static {
        scalable(TraitType.PENALTY, "wasteful", WASTEFUL, "Spells cost %d%% more.", 0.03f, 0.06f, 0.09f, 0.12f);
        scalable(TraitType.PENALTY, "timid", TIMID, "Spells deal %d%% less damage.", 0.03f, 0.06f, 0.09f, 0.12f);
        elementalScalable(TraitType.PENALTY, "fragile", FRAGILE, "Earth", false, 0.03f, 0.06f, 0.09f, 0.12f);
        elementalScalable(TraitType.PENALTY, "anchored", ANCHORED, "Air", false, 0.03f, 0.06f, 0.09f, 0.12f);
        elementalScalable(TraitType.PENALTY, "cold_hearted", COLD_HEARTED, "Fire", false, 0.03f, 0.06f, 0.09f, 0.12f);
        elementalScalable(TraitType.PENALTY, "stagnant", STAGNANT, "Water", false, 0.03f, 0.06f, 0.09f, 0.12f);
        elementalScalable(TraitType.PENALTY, "opaque", OPAQUE, "Light", false, 0.03f, 0.06f, 0.09f, 0.12f);
        elementalScalable(TraitType.PENALTY, "exposed", EXPOSED, "Dark", false, 0.03f, 0.06f, 0.09f, 0.12f);
        scalable(TraitType.PENALTY, "dissonant", DISSONANT, "Spells of your opposing affinity element are %d%% weaker.", 0.02f, 0.04f, 0.06f, 0.09f);
    }

    // ── Penalty (non-scalable) ──────────────────────────────────────────────

    public static final Supplier<Trait> ERRATIC = TRAITS.register("erratic",
        () -> new Trait(TraitNames.ERRATIC, "Mana cost varies slightly with each cast.", TraitType.PENALTY, TraitWeight.UNCOMMON, 0f));

    public static final Supplier<Trait> VOLATILE = TRAITS.register("volatile",
        () -> new Trait(TraitNames.VOLATILE, "Spell damage varies wildly.", TraitType.PENALTY, TraitWeight.UNCOMMON, 0f));

    public static final Supplier<Trait> SLUGGISH = TRAITS.register("sluggish",
        () -> new Trait(TraitNames.SLUGGISH, "Mana regeneration resumes slower after casting.", TraitType.PENALTY, TraitWeight.UNCOMMON, 0f));

    public static final Supplier<Trait> LANGUID = TRAITS.register("languid",
        () -> new Trait(TraitNames.LANGUID, "Increases the mana recovery delay by 33%.", TraitType.PENALTY, TraitWeight.RARE, 0.33f));

    public static final Supplier<Trait> DORMANT = TRAITS.register("dormant",
        () -> new Trait(TraitNames.DORMANT, "Spells occasionally trigger a small penalty effect.", TraitType.PENALTY, TraitWeight.LEGENDARY, 0f));

    public static final Supplier<Trait> SLEEPY = TRAITS.register("sleepy",
        () -> new Trait(TraitNames.SLEEPY, "Mana regenerates slower at night.", TraitType.PENALTY, TraitWeight.LEGENDARY, 0f));

    public static final Supplier<Trait> TIRED = TRAITS.register("tired",
        () -> new Trait(TraitNames.TIRED, "Mana regenerates slower during the day.", TraitType.PENALTY, TraitWeight.LEGENDARY, 0f));

    public static final Supplier<Trait> NULLSPARK = TRAITS.register("nullspark",
        () -> new Trait(TraitNames.NULLSPARK, "Spells occasionally fail to cast.", TraitType.PENALTY, TraitWeight.EXOTIC, 0f));

    public static final Supplier<Trait> LIFETITHE = TRAITS.register("lifetithe",
        () -> new Trait(TraitNames.LIFETITHE, "Attack spells occasionally cost health instead of mana.", TraitType.PENALTY, TraitWeight.EXOTIC, 0f));

    public static final Supplier<Trait> RIFTBLIGHT = TRAITS.register("riftblight",
        () -> new Trait(TraitNames.RIFTBLIGHT, "Casting attack spells has a tiny chance to open a temporary rift that spawns hostile creatures.", TraitType.PENALTY, TraitWeight.EXOTIC, 0f));

    // ── Neutral (scalable) ──────────────────────────────────────────────────

    static {
        scalable(TraitType.NEUTRAL, "frugal", FRUGAL, "The first spell cast after reaching full mana is %d%% cheaper.", 0.02f, 0.05f, 0.08f, 0.10f);
    }

    // ── Neutral (non-scalable) ──────────────────────────────────────────────

    public static final Supplier<Trait> RHYTHMIC = TRAITS.register("rhythmic",
        () -> new Trait(TraitNames.RHYTHMIC, "Mana regenerates in waves.", TraitType.NEUTRAL, TraitWeight.UNCOMMON, 0f));

    public static final Supplier<Trait> SPIKY = TRAITS.register("spiky",
        () -> new Trait(TraitNames.SPIKY, "Mana regenerates in bursts.", TraitType.NEUTRAL, TraitWeight.UNCOMMON, 0f));

    public static final Supplier<Trait> CALM = TRAITS.register("calm",
        () -> new Trait(TraitNames.CALM, "Mana regeneration is smooth and predictable.", TraitType.NEUTRAL, TraitWeight.UNCOMMON, 0f));

    public static final Supplier<Trait> FOCUSED = TRAITS.register("focused",
        () -> new Trait(TraitNames.FOCUSED, "Single-target spells are stronger.", TraitType.NEUTRAL, TraitWeight.RARE, 0f));

    public static final Supplier<Trait> DIFFUSE = TRAITS.register("diffuse",
        () -> new Trait(TraitNames.DIFFUSE, "AoE spells are stronger.", TraitType.NEUTRAL, TraitWeight.RARE, 0f));

    public static final Supplier<Trait> PIERCING = TRAITS.register("piercing",
        () -> new Trait(TraitNames.PIERCING, "Spells ignore a small portion of resistances.", TraitType.NEUTRAL, TraitWeight.RARE, 0f));

    public static final Supplier<Trait> IRRADIATED = TRAITS.register("irradiated",
        () -> new Trait(TraitNames.IRRADIATED, "Spells are stronger in sunlight.", TraitType.NEUTRAL, TraitWeight.LEGENDARY, 0f));

    public static final Supplier<Trait> UMBRAL = TRAITS.register("umbral",
        () -> new Trait(TraitNames.UMBRAL, "Spells are stronger in darkness.", TraitType.NEUTRAL, TraitWeight.LEGENDARY, 0f));

    public static final Supplier<Trait> STORMBORN = TRAITS.register("stormborn",
        () -> new Trait(TraitNames.STORMBORN, "Spells cost less mana during rain or thunder.", TraitType.NEUTRAL, TraitWeight.LEGENDARY, 0f));

    public static final Supplier<Trait> HEARTHBOUND = TRAITS.register("hearthbound",
        () -> new Trait(TraitNames.HEARTHBOUND, "Spells deal 5% more damage in the Overworld.", TraitType.NEUTRAL, TraitWeight.LEGENDARY, 0.05f));

    public static final Supplier<Trait> HELL_TOUCHED = TRAITS.register("hell_touched",
        () -> new Trait(TraitNames.HELL_TOUCHED, "Spells deal 7% more damage in the Nether.", TraitType.NEUTRAL, TraitWeight.LEGENDARY, 0.07f));

    public static final Supplier<Trait> STAR_TOUCHED = TRAITS.register("star_touched",
        () -> new Trait(TraitNames.STAR_TOUCHED, "Spells deal 10% more damage in the End.", TraitType.NEUTRAL, TraitWeight.LEGENDARY, 0.10f));

    public static final Supplier<Trait> DRAGON_BLESSED = TRAITS.register("dragon_blessed",
        () -> new Trait(TraitNames.DRAGON_BLESSED, "After defeating the Ender Dragon once, spells cost 10% less mana.", TraitType.NEUTRAL, TraitWeight.LEGENDARY, 0.10f));

    public static final Supplier<Trait> WANDERING = TRAITS.register("wandering",
        () -> new Trait(TraitNames.WANDERING, "Mana regeneration increases while moving.", TraitType.NEUTRAL, TraitWeight.LEGENDARY, 0f));

    public static final Supplier<Trait> ROOTED = TRAITS.register("rooted",
        () -> new Trait(TraitNames.ROOTED, "Mana regeneration increases while standing still.", TraitType.NEUTRAL, TraitWeight.LEGENDARY, 0f));

    public static final Supplier<Trait> COUNTERFLUX = TRAITS.register("counterflux",
        () -> new Trait(TraitNames.COUNTERFLUX, "Chance to reflect a portion of spell damage back at the caster.", TraitType.NEUTRAL, TraitWeight.EXOTIC, 0f));

    public static final Supplier<Trait> MANACOAT = TRAITS.register("manacoat",
        () -> new Trait(TraitNames.MANACOAT, "Spells occasionally refund a small amount of mana.", TraitType.NEUTRAL, TraitWeight.EXOTIC, 0f));

    public static final Supplier<Trait> SPELLWARP = TRAITS.register("spellwarp",
        () -> new Trait(TraitNames.SPELLWARP, "Your attack spells take on a random shape each time you cast them.", TraitType.NEUTRAL, TraitWeight.EXOTIC, 0f));

    public static void register(IEventBus modEventBus) {
        TRAITS.register(modEventBus);
    }
}
