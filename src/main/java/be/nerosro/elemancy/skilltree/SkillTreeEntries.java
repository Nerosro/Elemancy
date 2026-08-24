package be.nerosro.elemancy.skilltree;

import java.util.function.Supplier;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.jobpoint.JobPointPayment;
import be.nerosro.soulmark.element.Element;
import be.nerosro.soulmark.element.SoulmarkElements;
import be.nerosro.soulmark.skilltree.HiddenCondition;
import be.nerosro.soulmark.skilltree.LayoutDirection;
import be.nerosro.soulmark.skilltree.NodeType;
import be.nerosro.soulmark.skilltree.ParentMode;
import be.nerosro.soulmark.skilltree.SkillNode;
import be.nerosro.soulmark.skilltree.SkillTree;
import be.nerosro.soulmark.skilltree.SkillTreeRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers the Elemancy skill tree and all its nodes.
 * The tree starts with two unattuned spells: Elementize (utility, root)
 * and Elemental Blast (attack, unlocked from Elementize).
 */
public class SkillTreeEntries {

    // ---- Tree ----
    public static final DeferredRegister<SkillTree> TREES =
        DeferredRegister.create(SkillTreeRegistries.TREE_REGISTRY_KEY, Elemancy.MOD_ID);

    public static final Supplier<SkillTree> ELEMANCY_TREE = TREES.register("elemancy",
        () -> new SkillTree("Elemancy", "The art of shaping raw mana into elemental magic.", Elemancy.MOD_ID,
            LayoutDirection.LEFT_RIGHT, JobPointPayment.INSTANCE));
    // LEFT_RIGHT maps lanes vertically and progression depth horizontally.

    // ---- Tree ID shortcut ----
    public static final Identifier TREE_ID = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "elemancy");

    // ---- Nodes ----
    public static final DeferredRegister<SkillNode> NODES =
        DeferredRegister.create(SkillTreeRegistries.NODE_REGISTRY_KEY, Elemancy.MOD_ID);

    // ---- Node IDs (for cross-referencing) ----
    public static final Identifier ELEMENTIZE_ID = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "elementize");
    public static final Identifier ELEMENTAL_BLAST_ID = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "elemental_blast");
    public static final Identifier FIRE_BLAST_ID = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "fire_blast");
    public static final Identifier WATER_JET_ID = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "water_jet");
    public static final Identifier PEBBLE_SHOT_ID = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "pebble_shot");
    public static final Identifier GUST_SLASH_ID = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "gust_slash");
    public static final Identifier LIGHT_DART_ID = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "light_dart");
    public static final Identifier SHADOW_FLICK_ID = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "shadow_flick");

    // ---- Passive node IDs ----
    public static final Identifier SMOLDERING_POWER_ID = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "smoldering_power");
    public static final Identifier VITAL_CURRENTS_ID = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "vital_currents");
    public static final Identifier EARTHEN_POISE_ID = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "earthen_poise");
    public static final Identifier BREEZE_TREAD_ID = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "breeze_tread");
    public static final Identifier SOFT_GLOW_ID = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "soft_glow");
    public static final Identifier NIGHT_SIGHT_ID = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "night_sight");

    // ---- Ritual node IDs ----
    public static final Identifier ATTUNEMENT_RITUAL_ID = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "attunement_ritual");
    public static final Identifier CONVERSION_RITUAL_ID = Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "conversion_ritual");

    /**
     * Elementize — root node, UTILITY type.
     * Free (cost 0) — unlocked by default for all players on login.
     */
    public static final Supplier<SkillNode> ELEMENTIZE = NODES.register("elementize",
        () -> new SkillNode.Builder("Elementize", "Infuse mundane matter with raw mana.", TREE_ID, NodeType.UTILITY, SoulmarkElements.NONE)
            .cost(0)
            .position(0, 0)
            .build());

    // ── Attack spells ──────────────────────────────────────────────────────

    public static final Supplier<SkillNode> ELEMENTAL_BLAST = spell("elemental_blast",
        "Elemental Blast", "A raw burst of unstable mana, shaped only by instinct.",
        ELEMENTIZE_ID, 0, 1, SoulmarkElements.NONE);

    public static final Supplier<SkillNode> FIRE_BLAST = spell("fire_blast",
        "Fire Blast", "A raw spray of flame that keeps anything from getting close.",
        ELEMENTAL_BLAST_ID, -5, 2, SoulmarkElements.FIRE);

    public static final Supplier<SkillNode> WATER_JET = spell("water_jet",
        "Water Jet", "A high-pressure stream of water.",
        ELEMENTAL_BLAST_ID, -3, 2, SoulmarkElements.WATER);

    public static final Supplier<SkillNode> PEBBLE_SHOT = spell("pebble_shot",
        "Pebble Shot", "A small stone, launched at high speed.",
        ELEMENTAL_BLAST_ID, -1, 2, SoulmarkElements.EARTH);

    public static final Supplier<SkillNode> GUST_SLASH = spell("gust_slash",
        "Gust Slash", "A sharp blade of wind.",
        ELEMENTAL_BLAST_ID, 1, 2, SoulmarkElements.AIR);

    public static final Supplier<SkillNode> LIGHT_DART = spell("light_dart",
        "Light Dart", "A small projectile of pure light, fired at high speed.",
        ELEMENTAL_BLAST_ID, 3, 2, SoulmarkElements.LIGHT);

    public static final Supplier<SkillNode> SHADOW_FLICK = spell("shadow_flick",
        "Shadow Flick", "A quick burst of shadowy energy, flicked forward with a snap of the fingers.",
        ELEMENTAL_BLAST_ID, 5, 2, SoulmarkElements.DARK);

    // ── Passive nodes (row 3, children of elemental spells) ────────────────

    public static final Supplier<SkillNode> SMOLDERING_POWER = passive("smoldering_power",
        "Smoldering Power", "Your inner heat fuels every strike, burning with quiet intensity.",
        FIRE_BLAST_ID, -5, 3, SoulmarkElements.FIRE);

    public static final Supplier<SkillNode> VITAL_CURRENTS = passive("vital_currents",
        "Vital Currents", "Your body flows with quiet resilience, sustained by inner tides.",
        WATER_JET_ID, -3, 3, SoulmarkElements.WATER);

    public static final Supplier<SkillNode> EARTHEN_POISE = passive("earthen_poise",
        "Earthen Poise", "You stand firm, grounded by the quiet strength of stone.",
        PEBBLE_SHOT_ID, -1, 3, SoulmarkElements.EARTH);

    public static final Supplier<SkillNode> BREEZE_TREAD = passive("breeze_tread",
        "Breeze-Tread", "The air cushions your steps, carrying you lightly across the world.",
        GUST_SLASH_ID, 1, 3, SoulmarkElements.AIR);

    public static final Supplier<SkillNode> SOFT_GLOW = passive("soft_glow",
        "Soft Glow", "A gentle radiance follows you, pushing back the darkness.",
        LIGHT_DART_ID, 3, 3, SoulmarkElements.LIGHT);

    public static final Supplier<SkillNode> NIGHT_SIGHT = passive("night_sight",
        "Night Sight", "Your eyes adapt to shadow, seeing clearly where others falter.",
        SHADOW_FLICK_ID, 5, 3, SoulmarkElements.DARK);

    // ── Ritual node (row 4, unlockable when any passive is purchased) ────

    /**
     * Attunement Ritual — unlocks access to the attunement ritual.
     * Becomes unlockable when any elemental passive is purchased.
     */
    public static final Supplier<SkillNode> ATTUNEMENT_RITUAL = NODES.register("attunement_ritual",
        () -> new SkillNode.Builder("Attunement",
            "Your gateway into advanced Elemancy.",
            TREE_ID, NodeType.RITUAL, SoulmarkElements.NONE)
            .parents(SMOLDERING_POWER_ID, VITAL_CURRENTS_ID, EARTHEN_POISE_ID,
                BREEZE_TREAD_ID, SOFT_GLOW_ID, NIGHT_SIGHT_ID)
            .parentMode(ParentMode.ANY)
            .cost(1)
            .position(0, 4)
            // .soulGate() // Re-enable when multiple job mods need shared Soul Point gates.
            .build());

    /**
     * Conversion — granted automatically when the player becomes attuned.
     */
    public static final Supplier<SkillNode> CONVERSION_RITUAL = NODES.register("conversion_ritual",
        () -> new SkillNode.Builder("Conversion",
            "Technically six in one!",
            TREE_ID, NodeType.RITUAL, SoulmarkElements.NONE)
            .parent(ATTUNEMENT_RITUAL_ID)
            .hiddenCondition(HiddenCondition.nodeUnlocked(CONVERSION_RITUAL_ID))
            .cost(0)
            .position(2, 4)
            .build());

    // ── Discovery nodes (hidden via NodeType, never visible in skill tree UI) ───

    /**
     * Hidden node unlocked when player right-clicks Paradox Flower with Tome.
     * Gates access to expanded Paradox Flower entry.
     * NodeType.DISCOVERY ensures it never renders in UI.
     */
    public static final Supplier<SkillNode> DISCOVERY_PARADOX_FLOWER = NODES.register("discovery/paradox_flower",
        () -> new SkillNode.Builder("Paradox Flower Discovery", "Hidden discovery node", TREE_ID, NodeType.DISCOVERY, SoulmarkElements.NONE)
            .cost(0)
            .position(-2, 0)
            .build());

    /**
     * Hidden node unlocked when player right-clicks mana-reactive beehive with Tome.
     * Gates access to Mana-Reactive Beehive entry.
     * NodeType.DISCOVERY ensures it never renders in UI.
     */
    public static final Supplier<SkillNode> DISCOVERY_INFUSED_BEEHIVE = NODES.register("discovery/infused_beehive",
        () -> new SkillNode.Builder("Beehive Discovery", "Hidden discovery node", TREE_ID, NodeType.DISCOVERY, SoulmarkElements.NONE)
            .cost(0)
            .position(-2, 0)
            .build());

    public static void register(IEventBus modEventBus) {
        TREES.register(modEventBus);
        NODES.register(modEventBus);
    }

    // ── Helper methods ─────────────────────────────────────────────────────

    private static Supplier<SkillNode> spell(String id, String name, String desc,
                                             Identifier parent, int lane, int depth, Supplier<Element> element) {
        return NODES.register(id, () -> new SkillNode.Builder(name, desc, TREE_ID, NodeType.ABILITY, element)
            .parent(parent).cost(1).position(lane, depth)
            .icon(Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "textures/gui/skills/" + id + ".png"))
            .build());
    }

    private static Supplier<SkillNode> passive(String id, String name, String desc,
                                               Identifier parent, int lane, int depth, Supplier<Element> element) {
        return NODES.register(id, () -> new SkillNode.Builder(name, desc, TREE_ID, NodeType.PASSIVE, element)
            .parent(parent).cost(1).position(lane, depth)
            .icon(Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "textures/gui/skills/" + id + ".png"))
            .build());
    }

    private static Supplier<SkillNode> recipe(String id, String name, String desc,
                                              Identifier parent, int lane, int depth, Supplier<Element> element) {
        return NODES.register(id, () -> new SkillNode.Builder(name, desc, TREE_ID, NodeType.RECIPE, element)
            .parent(parent).cost(1).position(lane, depth)
            .icon(Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "textures/gui/skills/" + id + ".png"))
            .build());
    }
}
