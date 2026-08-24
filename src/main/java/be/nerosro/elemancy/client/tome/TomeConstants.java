package be.nerosro.elemancy.client.tome;

import net.minecraft.resources.Identifier;

/**
 * All constants for the Tome UI system.
 */
public final class TomeConstants {
    private TomeConstants() {
    }

    /**
     * Color values for Tome UI elements.
     */
    public static final class Colors {
        private Colors() {
        }

        public static final int BG = 0x88120E0A;
        public static final int BOOK = 0xFFF2E3C9;
        public static final int BORDER = 0xFF7A5530;
        public static final int TEXT = 0xFF3A2615;
        public static final int TEXT_MUTED = 0xFF6B5238;
        public static final int TAB = 0xFF6B5031;
        public static final int TAB_ACTIVE = 0xFF9A7350;
        public static final int TAB_HOVER = 0xFF8E6D4A;
        public static final int BUTTON = 0xFF7A5530;
        public static final int BUTTON_DISABLED = 0xFF5A4634;
        public static final int GRID_CELL = 0xFFD9C7A7;
        public static final int GRID_CELL_HOVER = 0xFFEAD9BB;
        public static final int SCROLLBAR_TRACK = 0x33000000;
        public static final int TOOLTIP_BG = 0xF0EAD9BB;
        public static final int TRAIT_OUTLINE = 0xFF2A1A0A;

        // Trait rarity colors
        public static final int TRAIT_COMMON = 0xFFFFFFFF;
        public static final int TRAIT_UNCOMMON = 0xFF55FF55;
        public static final int TRAIT_RARE = 0xFF5555FF;
        public static final int TRAIT_LEGENDARY = 0xFFAA00AA;
        public static final int TRAIT_EXOTIC = 0xFFFFAA00;
    }

    /**
     * Layout dimensions and spacing.
     */
    public static final class Layout {
        private Layout() {
        }

        // Book dimensions
        public static final int MARGIN = 14;
        public static final int BOOK_WIDTH = 320;
        public static final int TAB_RAIL_WIDTH = 72;
        public static final int FOOTER_HEIGHT = 32;

        // Content padding and spacing
        public static final int CONTENT_PADDING_X = 14;
        public static final int CONTENT_PADDING_Y = 16;
        public static final int INDEX_CONTENT_START = 34;

        // Grid layout
        public static final int GRID_CELL_SIZE = 38;
    }

    /**
     * Icon identifiers.
     */
    public static final class Icons {
        private Icons() {
        }

        // Tab icons
        public static final Identifier TAB_IDENTITY = id("minecraft:textures/mob_effect/invisibility.png");
        public static final Identifier TAB_SCARS = id("minecraft:textures/mob_effect/glowing.png");
        public static final Identifier TAB_SPELLS = id("minecraft:textures/mob_effect/wind_charged.png");
        public static final Identifier TAB_PASSIVES = id("minecraft:textures/mob_effect/luck.png");
        public static final Identifier TAB_CRAFTING = id("minecraft:textures/mob_effect/haste.png");
        public static final Identifier TAB_RITUALS = id("minecraft:textures/mob_effect/trial_omen.png");
        public static final Identifier TAB_KNOWLEDGE = id("minecraft:textures/mob_effect/conduit_power.png");

        // Scar icons
        public static final Identifier SCAR_PHYSICAL = id("minecraft:textures/mob_effect/slowness.png");
        public static final Identifier SCAR_ARCANE_TREMOR = id("minecraft:textures/mob_effect/unluck.png");
        public static final Identifier SCAR_SPELL_DRIFT = id("minecraft:textures/mob_effect/weakness.png");
        public static final Identifier SCAR_CHANNEL_DISRUPTION = id("minecraft:textures/mob_effect/hunger.png");
        public static final Identifier SCAR_MANA_BURN = id("minecraft:textures/mob_effect/wither.png");
        public static final Identifier SCAR_ARCANE_FATIGUE = id("minecraft:textures/mob_effect/mining_fatigue.png");
        public static final Identifier SCAR_SPELL_WEAKNESS = id("minecraft:textures/mob_effect/instant_damage.png");
        public static final Identifier SCAR_MANA_COLLAPSE = id("minecraft:textures/mob_effect/bad_omen.png");

        private static Identifier id(String path) {
            return Identifier.parse(path);
        }
    }
}
