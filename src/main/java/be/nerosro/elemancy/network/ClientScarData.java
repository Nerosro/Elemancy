package be.nerosro.elemancy.network;

import be.nerosro.elemancy.mana.depth.ScarType;

/**
 * Client-side cache for active scar state received from the server.
 * HUD rendering reads from here for icon display and Mana Collapse coloring.
 * Future Mirror UI will read stack/timer data for detailed diagnostics.
 */
public final class ClientScarData {

    private static byte activeScars;

    // Stackable scar data
    private static byte manaBurnStacks;
    private static int manaBurnTicks;
    private static byte arcaneFatigueStacks;
    private static int arcaneFatigueTicks;
    private static byte spellWeaknessStacks;
    private static int spellWeaknessTicks;

    // Non-stackable scar timers
    private static int arcaneTremorTicks;
    private static int spellDriftTicks;
    private static int channelDisruptionTicks;
    private static int manaCollapseTicks;

    private ClientScarData() {
    }

    public static void update(ScarSyncPayload payload) {
        activeScars = payload.activeScars();
        manaBurnStacks = payload.manaBurnStacks();
        manaBurnTicks = payload.manaBurnTicks();
        arcaneFatigueStacks = payload.arcaneFatigueStacks();
        arcaneFatigueTicks = payload.arcaneFatigueTicks();
        spellWeaknessStacks = payload.spellWeaknessStacks();
        spellWeaknessTicks = payload.spellWeaknessTicks();
        arcaneTremorTicks = payload.arcaneTremorTicks();
        spellDriftTicks = payload.spellDriftTicks();
        channelDisruptionTicks = payload.channelDisruptionTicks();
        manaCollapseTicks = payload.manaCollapseTicks();
    }

    public static void clear() {
        activeScars = 0;
    }

    public static boolean hasArcaneTremor() {
        return ScarType.ARCANE_TREMOR.isActive(activeScars);
    }

    public static boolean hasSpellDrift() {
        return ScarType.SPELL_DRIFT.isActive(activeScars);
    }

    public static boolean hasChannelDisruption() {
        return ScarType.CHANNEL_DISRUPTION.isActive(activeScars);
    }

    public static boolean hasManaBurn() {
        return ScarType.MANA_BURN.isActive(activeScars);
    }

    public static boolean hasArcaneFatigue() {
        return ScarType.ARCANE_FATIGUE.isActive(activeScars);
    }

    public static boolean hasSpellWeakness() {
        return ScarType.SPELL_WEAKNESS.isActive(activeScars);
    }

    public static boolean hasManaCollapse() {
        return ScarType.MANA_COLLAPSE.isActive(activeScars);
    }

    /**
     * Returns true if any scar is currently active.
     */
    public static boolean hasAnyScars() {
        return activeScars != 0;
    }

    // ── Stack and timer accessors for future Mirror UI ──────────────────────────

    public static byte getManaBurnStacks() {
        return manaBurnStacks;
    }

    public static int getManaBurnTicks() {
        return manaBurnTicks;
    }

    public static byte getArcaneFatigueStacks() {
        return arcaneFatigueStacks;
    }

    public static int getArcaneFatigueTicks() {
        return arcaneFatigueTicks;
    }

    public static byte getSpellWeaknessStacks() {
        return spellWeaknessStacks;
    }

    public static int getSpellWeaknessTicks() {
        return spellWeaknessTicks;
    }

    public static int getArcaneTremorTicks() {
        return arcaneTremorTicks;
    }

    public static int getSpellDriftTicks() {
        return spellDriftTicks;
    }

    public static int getChannelDisruptionTicks() {
        return channelDisruptionTicks;
    }

    public static int getManaCollapseTicks() {
        return manaCollapseTicks;
    }
}

