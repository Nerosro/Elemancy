package be.nerosro.elemancy.client.tome.rendering;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/**
 * Unified text wrapping and measurement.
 */
public final class TextRenderer {
    private static final int LINE_HEIGHT = 10;

    private TextRenderer() {
    }

    /**
     * Measures wrapped text height without rendering.
     */
    public static int measureHeight(String text, int width, Font font) {
        int dy = 0;
        for (String paragraph : text.split("\\n", -1)) {
            String[] words = paragraph.split(" ", -1);
            StringBuilder current = new StringBuilder();
            for (String word : words) {
                String candidate = current.isEmpty() ? word : current + " " + word;
                if (font.width(candidate) > width && !current.isEmpty()) {
                    dy += LINE_HEIGHT;
                    current = new StringBuilder(word);
                } else {
                    current = new StringBuilder(candidate);
                }
            }
            dy += LINE_HEIGHT;
        }
        return dy;
    }

    /**
     * Draws wrapped text. Returns total height used.
     */
    public static int draw(GuiGraphicsExtractor graphics, Font font, String text, int x, int y, int width, int color) {
        int dy = 0;
        for (String paragraph : text.split("\\n", -1)) {
            String[] words = paragraph.split(" ", -1);
            StringBuilder current = new StringBuilder();
            for (String word : words) {
                String candidate = current.isEmpty() ? word : current + " " + word;
                if (font.width(candidate) > width && !current.isEmpty()) {
                    graphics.text(font, Component.literal(current.toString()), x, y + dy, color, false);
                    dy += LINE_HEIGHT;
                    current = new StringBuilder(word);
                } else {
                    current = new StringBuilder(candidate);
                }
            }
            if (!current.isEmpty()) {
                graphics.text(font, Component.literal(current.toString()), x, y + dy, color, false);
            }
            dy += LINE_HEIGHT;
        }
        return dy;
    }

    /**
     * Draws centered text.
     */
    public static void drawCentered(GuiGraphicsExtractor graphics, Font font, String text, int cx, int y, int color) {
        graphics.text(font, Component.literal(text), cx - font.width(text) / 2, y, color, false);
    }
}
