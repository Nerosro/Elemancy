package be.nerosro.elemancy.client.tome.views;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

/**
 * Interface for pluggable Tome tabs.
 * Allows tabs to encapsulate their own rendering, interaction, and visibility logic.
 */
public interface TomeTabView {
    /**
     * Renders this tab's content within the book region.
     *
     * @param graphics Rendering context
     * @param mouseX   Mouse X coordinate for hover detection
     * @param mouseY   Mouse Y coordinate for hover detection
     * @param x        Book content area X
     * @param y        Book content area Y
     * @param w        Book content area width
     * @param h        Book content area height
     */
    void draw(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y, int w, int h);

    /**
     * Handles mouse click within the tab's content area.
     *
     * @param mouseX Mouse X coordinate
     * @param mouseY Mouse Y coordinate
     * @param x      Book content area X
     * @param y      Book content area Y
     * @param w      Book content area width
     * @return true if click was handled, false otherwise
     */
    boolean handleClick(double mouseX, double mouseY, int x, int y, int w);

    /**
     * Returns whether this tab should be visible in the tab rail.
     */
    boolean isVisible();

    /**
     * Returns the icon identifier for this tab.
     */
    Identifier getIcon();

    /**
     * Returns the unique ID for this tab (e.g., "spells", "identity").
     */
    String getId();
}
