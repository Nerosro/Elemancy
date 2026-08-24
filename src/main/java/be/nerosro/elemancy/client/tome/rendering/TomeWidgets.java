package be.nerosro.elemancy.client.tome.rendering;

import be.nerosro.elemancy.client.tome.TomeConstants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/**
 * Reusable UI widgets for the Tome screen.
 */
public final class TomeWidgets {
    private TomeWidgets() {
    }

    /**
     * Draws a 4-sided border.
     */
    public static void drawBorder(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int color) {
        graphics.fillGradient(x, y, x + w, y + 1, color, color);
        graphics.fillGradient(x, y + h - 1, x + w, y + h, color, color);
        graphics.fillGradient(x, y, x + 1, y + h, color, color);
        graphics.fillGradient(x + w - 1, y, x + w, y + h, color, color);
    }

    /**
     * Draws a button with border and centered label.
     */
    public static void drawButton(GuiGraphicsExtractor graphics, Font font, int x, int y, int w, int h, String label, boolean enabled) {
        int bg = enabled ? TomeConstants.Colors.BUTTON : TomeConstants.Colors.BUTTON_DISABLED;
        graphics.fillGradient(x, y, x + w, y + h, bg, bg);
        drawBorder(graphics, x, y, w, h, TomeConstants.Colors.BORDER);
        TextRenderer.drawCentered(graphics, font, label, x + (w / 2), y + 6, TomeConstants.Colors.TEXT);
    }

    /**
     * Draws a scrollbar in the given region.
     */
    public static void drawScrollbar(GuiGraphicsExtractor graphics, int barX, int regionTop, int regionBottom, int regionH, int scrollOffset, int contentH) {
        int barH = Math.max(20, regionH * regionH / contentH);
        int maxScroll = Math.max(0, contentH - regionH);
        int barY = regionTop + (maxScroll > 0 ? (int) ((long) (regionH - barH) * scrollOffset / maxScroll) : 0);
        graphics.fillGradient(barX, regionTop, barX + 4, regionBottom, TomeConstants.Colors.SCROLLBAR_TRACK, TomeConstants.Colors.SCROLLBAR_TRACK);
        graphics.fillGradient(barX, barY, barX + 4, barY + barH, TomeConstants.Colors.BORDER, TomeConstants.Colors.BORDER);
    }

    /**
     * Draws a tooltip box with border at mouse position, clamped to screen bounds.
     */
    public static void drawTooltip(GuiGraphicsExtractor graphics, Font font, String text,
                                   int mouseX, int mouseY, int screenWidth, int screenHeight) {
        int pad = 4;
        int textW = font.width(text);
        int boxW = textW + pad * 2;
        int boxH = 10 + pad * 2;

        int tx = mouseX + 10;
        int ty = mouseY - boxH - 2;
        // Clamp so tooltip never leaves the screen
        if (tx + boxW > screenWidth - 2) tx = mouseX - boxW - 4;
        if (ty < 2) ty = mouseY + 12;
        if (ty + boxH > screenHeight - 2) ty = screenHeight - boxH - 2;

        graphics.fillGradient(tx, ty, tx + boxW, ty + boxH, TomeConstants.Colors.TOOLTIP_BG, TomeConstants.Colors.TOOLTIP_BG);
        graphics.fillGradient(tx, ty, tx + boxW, ty + 1, TomeConstants.Colors.BORDER, TomeConstants.Colors.BORDER);
        graphics.fillGradient(tx, ty + boxH - 1, tx + boxW, ty + boxH, TomeConstants.Colors.BORDER, TomeConstants.Colors.BORDER);
        graphics.fillGradient(tx, ty, tx + 1, ty + boxH, TomeConstants.Colors.BORDER, TomeConstants.Colors.BORDER);
        graphics.fillGradient(tx + boxW - 1, ty, tx + boxW, ty + boxH, TomeConstants.Colors.BORDER, TomeConstants.Colors.BORDER);
        graphics.text(font, Component.literal(text), tx + pad, ty + pad, TomeConstants.Colors.TEXT, false);
    }
}
