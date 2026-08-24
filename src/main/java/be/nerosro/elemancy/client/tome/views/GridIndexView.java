package be.nerosro.elemancy.client.tome.views;

import java.util.List;

import be.nerosro.elemancy.client.tome.TomeConstants;
import be.nerosro.elemancy.client.tome.rendering.TomeLayout;
import be.nerosro.elemancy.client.tome.rendering.TomeWidgets;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * Base class for grid-based index views (Spells, Passives, Scars).
 * Provides shared grid rendering, click handling, and hover detection.
 * Subclasses override text and entry extraction logic.
 */
public abstract class GridIndexView<T> implements TomeTabView {
    protected final Font font;
    protected final TomeLayout layout;
    protected final List<T> entries;
    protected final IconRenderer<T> iconRenderer;

    /**
     * Functional interface for rendering entry icons in grid cells.
     * Generic to support both SkillNode icons and texture-based icons.
     */
    @FunctionalInterface
    public interface IconRenderer<T> {
        void draw(GuiGraphicsExtractor graphics, T entry, int cx, int cy);
    }

    protected GridIndexView(Font font, TomeLayout layout, List<T> entries, IconRenderer<T> iconRenderer) {
        this.font = font;
        this.layout = layout;
        this.entries = entries;
        this.iconRenderer = iconRenderer;
    }

    /**
     * Title text for this index (e.g., "Spells", "Passives", "Scars").
     */
    protected abstract String getTitle();

    /**
     * Subtitle instruction text (e.g., "Select an icon to read about this spell.").
     */
    protected abstract String getSubtitle();

    /**
     * Empty state message when no entries are available.
     */
    protected abstract String getEmptyMessage();

    /**
     * Extract display name from entry for hover tooltip.
     */
    protected abstract String getName(T entry);

    /**
     * Handle entry selection - typically opens entry reader.
     */
    protected abstract void onEntryClicked(T entry);

    // TomeTabView interface methods to be implemented by subclasses

    /**
     * @return Tab visibility state
     */
    public abstract boolean isVisible();

    /**
     * @return Tab icon for tab rail
     */
    public abstract Identifier getIcon();

    /**
     * @return Unique tab identifier
     */
    public abstract String getId();

    /**
     * Render grid of entry icons with title and subtitle.
     */
    @Override
    public void draw(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y, int w, int h) {
        int innerX = x + TomeConstants.Layout.CONTENT_PADDING_X;
        int innerY = y + TomeConstants.Layout.CONTENT_PADDING_Y;
        int innerW = w - 28;

        graphics.text(font, Component.literal(getTitle()), innerX, innerY, TomeConstants.Colors.TEXT, false);
        graphics.text(font, Component.literal(getSubtitle()), innerX, innerY + 14, TomeConstants.Colors.TEXT_MUTED, false);

        if (entries.isEmpty()) {
            graphics.text(font, Component.literal(getEmptyMessage()), innerX, innerY + TomeConstants.Layout.INDEX_CONTENT_START, TomeConstants.Colors.TEXT_MUTED, false);
            return;
        }

        int cellSize = TomeConstants.Layout.GRID_CELL_SIZE;
        int cols = Math.max(1, innerW / cellSize);
        int startY = innerY + TomeConstants.Layout.INDEX_CONTENT_START;

        for (int i = 0; i < entries.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            int cellX = innerX + (col * cellSize);
            int cellY = startY + (row * cellSize);

            boolean hovered = TomeLayout.isInside(mouseX, mouseY, cellX, cellY, 32, 32);
            int cellBg = hovered ? TomeConstants.Colors.GRID_CELL_HOVER : TomeConstants.Colors.GRID_CELL;
            graphics.fillGradient(cellX, cellY, cellX + 32, cellY + 32, cellBg, cellBg);
            TomeWidgets.drawBorder(graphics, cellX, cellY, 32, 32, TomeConstants.Colors.BORDER);
            iconRenderer.draw(graphics, entries.get(i), cellX + 16, cellY + 16);
        }
    }

    /**
     * Handle mouse click - detect which grid cell was clicked and open entry.
     */
    @Override
    public boolean handleClick(double mouseX, double mouseY, int x, int y, int w) {
        if (entries.isEmpty()) return false;

        int innerX = x + TomeConstants.Layout.CONTENT_PADDING_X;
        int innerY = y + TomeConstants.Layout.CONTENT_PADDING_Y;
        int innerW = w - 28;
        int cellSize = TomeConstants.Layout.GRID_CELL_SIZE;
        int cols = Math.max(1, innerW / cellSize);
        int startY = innerY + TomeConstants.Layout.INDEX_CONTENT_START;

        for (int i = 0; i < entries.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            int cellX = innerX + (col * cellSize);
            int cellY = startY + (row * cellSize);

            if (TomeLayout.isInside((int) mouseX, (int) mouseY, cellX, cellY, 32, 32)) {
                onEntryClicked(entries.get(i));
                return true;
            }
        }

        return false;
    }

    /**
     * Get name of entry under mouse cursor for hover tooltip.
     */
    public String getHoveredName(int mouseX, int mouseY, int x, int y, int w) {
        if (entries.isEmpty()) return null;

        int innerX = x + TomeConstants.Layout.CONTENT_PADDING_X;
        int innerY = y + TomeConstants.Layout.CONTENT_PADDING_Y;
        int innerW = w - 28;
        int cellSize = TomeConstants.Layout.GRID_CELL_SIZE;
        int cols = Math.max(1, innerW / cellSize);
        int startY = innerY + TomeConstants.Layout.INDEX_CONTENT_START;

        for (int i = 0; i < entries.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            int cellX = innerX + (col * cellSize);
            int cellY = startY + (row * cellSize);

            if (TomeLayout.isInside(mouseX, mouseY, cellX, cellY, 32, 32)) {
                return getName(entries.get(i));
            }
        }

        return null;
    }
}
