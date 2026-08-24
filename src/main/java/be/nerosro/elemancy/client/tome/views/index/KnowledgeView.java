package be.nerosro.elemancy.client.tome.views.index;

import java.util.List;
import java.util.function.Consumer;

import be.nerosro.elemancy.client.tome.TomeConstants;
import be.nerosro.elemancy.client.tome.rendering.TomeLayout;
import be.nerosro.elemancy.client.tome.views.TomeTabView;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/**
 * View for Knowledge tab index - list of available chapters.
 */
public class KnowledgeView implements TomeTabView {
    private final Font font;
    private final List<KnowledgeIndexEntry> entries;
    private final Consumer<String> onOpenEntry;

    public record KnowledgeIndexEntry(String id, String title) {
    }

    public KnowledgeView(Font font, List<KnowledgeIndexEntry> entries, Consumer<String> onOpenEntry) {
        this.font = font;
        this.entries = entries;
        this.onOpenEntry = onOpenEntry;
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y, int w, int h) {
        int listX = x + TomeConstants.Layout.CONTENT_PADDING_X;
        int listY = y + TomeConstants.Layout.CONTENT_PADDING_Y;
        int listW = w - 28;
        int rowH = 20;

        graphics.text(font, Component.literal("Knowledge"), listX, listY, TomeConstants.Colors.TEXT, false);
        graphics.text(font, Component.literal("Select an entry to open the chapter."), listX, listY + 14, TomeConstants.Colors.TEXT_MUTED, false);

        int rowTop = listY + TomeConstants.Layout.INDEX_CONTENT_START;
        for (KnowledgeIndexEntry entry : entries) {
            graphics.fillGradient(listX, rowTop, listX + listW, rowTop + rowH - 2, TomeConstants.Colors.GRID_CELL, TomeConstants.Colors.GRID_CELL);
            graphics.fillGradient(listX, rowTop, listX + listW, rowTop + 1, TomeConstants.Colors.BORDER, TomeConstants.Colors.BORDER);
            graphics.fillGradient(listX, rowTop + rowH - 2, listX + listW, rowTop + rowH - 1, TomeConstants.Colors.BORDER, TomeConstants.Colors.BORDER);
            graphics.text(font, Component.literal(entry.title), listX + 6, rowTop + 6, TomeConstants.Colors.TEXT, false);
            rowTop += rowH;
        }
    }

    @Override
    public boolean handleClick(double mouseX, double mouseY, int x, int y, int w) {
        int listX = x + TomeConstants.Layout.CONTENT_PADDING_X;
        int listY = y + TomeConstants.Layout.CONTENT_PADDING_Y;
        int listW = w - 28;
        int rowH = 20;

        int rowTop = listY + TomeConstants.Layout.INDEX_CONTENT_START;
        for (KnowledgeIndexEntry entry : entries) {
            if (TomeLayout.isInside((int) mouseX, (int) mouseY, listX, rowTop, listW, rowH - 2)) {
                onOpenEntry.accept(entry.id);
                return true;
            }
            rowTop += rowH;
        }

        return false;
    }

    public boolean isVisible() {
        return true; // Knowledge always visible
    }

    public net.minecraft.resources.Identifier getIcon() {
        return TomeConstants.Icons.TAB_KNOWLEDGE;
    }

    public String getId() {
        return "knowledge";
    }
}
