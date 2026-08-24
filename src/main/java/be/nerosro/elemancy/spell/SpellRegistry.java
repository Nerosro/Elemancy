package be.nerosro.elemancy.spell;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.resources.Identifier;

/**
 * Central metadata registry for all spells.
 * Stores identity, element, category, shape, and mana cost for each spell.
 * Dispatch logic (handlers) is owned by SpellDispatcher.
 */
public final class SpellRegistry {
    private SpellRegistry() {
    }

    // Populated during mod init (single-threaded); effectively immutable at runtime — only reads after that.
    private static final Map<Identifier, SpellEntry> ENTRIES = new LinkedHashMap<>();
    private static final Map<Identifier, String> MANA_COST_LABELS = new HashMap<>();

    /**
     * Register a spell's metadata.
     *
     * @param id       The skill node identifier for this spell.
     * @param element  The elemental type of the spell.
     * @param category The functional category (ATTACK, INFUSION, etc.).
     * @param shape    The default shape of the spell.
     */
    public static void register(Identifier id, SpellElement element, SpellCategory category, SpellShape shape) {
        ENTRIES.put(id, new SpellEntry(id, element, category, shape));
    }

    /**
     * Get the entry for a specific spell, or null if not registered.
     */
    public static SpellEntry get(Identifier id) {
        return ENTRIES.get(id);
    }

    /**
     * Check if a spell is an attack spell.
     */
    public static boolean isAttack(Identifier id) {
        SpellEntry entry = ENTRIES.get(id);
        return entry != null && entry.category() == SpellCategory.ATTACK;
    }

    /**
     * Check if a spell is an infusion spell.
     */
    public static boolean isInfusion(Identifier id) {
        SpellEntry entry = ENTRIES.get(id);
        return entry != null && entry.category() == SpellCategory.INFUSION;
    }

    /**
     * Check if a spell is a continuous cast (hold-to-spray) spell.
     */
    public static boolean isContinuous(Identifier id) {
        SpellEntry entry = ENTRIES.get(id);
        return entry != null && entry.shape() == SpellShape.CONTINUOUS;
    }

    /**
     * Register the mana cost label for a spell.
     * Use "X mana" for one-shot spells, "X + Y/tick mana" for continuous.
     */
    public static void registerManaCost(Identifier id, String label) {
        MANA_COST_LABELS.put(id, label);
    }

    /**
     * Returns the formatted mana cost label, or "Unknown" if not registered.
     */
    public static String getManaCostLabel(Identifier id) {
        return MANA_COST_LABELS.getOrDefault(id, "Unknown");
    }
}


