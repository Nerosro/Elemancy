package be.nerosro.elemancy.client.tome.views.detail;

import java.util.List;
import java.util.Locale;

import org.jspecify.annotations.Nullable;

import be.nerosro.elemancy.client.tome.TomeConstants;
import be.nerosro.elemancy.client.tome.data.TomeEntryLoader;
import be.nerosro.elemancy.client.tome.rendering.TextRenderer;
import be.nerosro.elemancy.client.tome.rendering.TomeLayout;
import be.nerosro.elemancy.client.tome.rendering.TomeWidgets;
import be.nerosro.elemancy.spell.SpellCategory;
import be.nerosro.elemancy.spell.SpellEntry;
import be.nerosro.elemancy.spell.SpellRegistry;
import be.nerosro.soulmark.element.SoulmarkElements;
import be.nerosro.soulmark.skilltree.SkillNode;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Dedicated view for rendering Tome entries in ENTRY mode.
 * Handles spell, passive, knowledge, scar, and crafting entry display.
 */
public class EntryReaderView {
    private final Font font;
    private final TomeLayout layout;

    private EntryType type = EntryType.KNOWLEDGE;
    private String title = "";
    private String entryId = "";
    private List<TomeEntryLoader.Page> pages = List.of();
    private int activePage = 0;
    private int scrollOffset = 0;

    @Nullable
    private SkillNode spellNode;
    @Nullable
    private SpellEntry spellEntry;
    @Nullable
    private String resultItemId;
    private final java.util.ArrayList<ItemSlot> recipeSlots = new java.util.ArrayList<>();

    public enum EntryType {
        SPELL, PASSIVE, RITUAL, KNOWLEDGE, SCAR, CRAFTING
    }

    private record ItemSlot(String itemId, int x, int y, int size) {
    }

    public EntryReaderView(Font font, TomeLayout layout) {
        this.font = font;
        this.layout = layout;
    }

    public void setEntry(String id, EntryType type, String title, List<TomeEntryLoader.Page> pages, @Nullable SkillNode node, @Nullable SpellEntry spell, @Nullable String resultItemId) {
        this.entryId = id;
        this.type = type;
        this.title = title;
        this.pages = pages;
        this.activePage = 0;
        this.scrollOffset = 0;
        this.spellNode = node;
        this.spellEntry = spell;
        this.resultItemId = resultItemId;
    }

    public void draw(GuiGraphicsExtractor graphics, int x, int y, int w, IconRenderer iconRenderer) {
        recipeSlots.clear();
        int textX = x + 16;
        int textY = y + 16;
        int bodyW = w - 32;

        int contentStartY = switch (type) {
            case SPELL -> {
                drawHeader(graphics, textX, textY, iconRenderer);
                int separatorY = drawSpellStats(graphics, textX, textY + 40, x + w);
                yield separatorY + 7;
            }
            case PASSIVE -> {
                drawHeader(graphics, textX, textY, iconRenderer);
                int separatorY = drawPassiveStats(graphics, textX, textY + 40, x + w);
                yield separatorY + 7;
            }
            case RITUAL -> {
                drawHeader(graphics, textX, textY, iconRenderer);
                int separatorY = drawRitualStats(graphics, textX, textY + 40, x + w);
                yield separatorY + 7;
            }
            case KNOWLEDGE -> {
                graphics.text(font, Component.literal(title), textX, textY, TomeConstants.Colors.TEXT, false);
                yield textY + 18;
            }
            case SCAR -> {
                graphics.text(font, Component.literal(title.toUpperCase(Locale.ROOT)), textX, textY, TomeConstants.Colors.TEXT, false);
                yield textY + 18;
            }
            case CRAFTING -> {
                if (resultItemId != null) {
                    drawItemHeader(graphics, textX, textY);
                    yield textY + 40;
                } else {
                    graphics.text(font, Component.literal(title), textX, textY, TomeConstants.Colors.TEXT, false);
                    yield textY + 18;
                }
            }
        };

        drawBody(graphics, contentStartY, x, w, bodyW);
    }

    public interface IconRenderer {
        void render(GuiGraphicsExtractor graphics, SkillNode node, int cx, int cy);
    }

    private void drawHeader(GuiGraphicsExtractor graphics, int textX, int textY, IconRenderer iconRenderer) {
        graphics.fillGradient(textX, textY, textX + 32, textY + 32, TomeConstants.Colors.GRID_CELL, TomeConstants.Colors.GRID_CELL);
        TomeWidgets.drawBorder(graphics, textX, textY, 32, 32, TomeConstants.Colors.BORDER);
        if (spellNode != null) {
            iconRenderer.render(graphics, spellNode, textX + 16, textY + 16);
        }
        graphics.text(font, Component.literal(title), textX + 40, textY + 12, TomeConstants.Colors.TEXT, false);
    }

    private void drawItemHeader(GuiGraphicsExtractor graphics, int textX, int textY) {
        graphics.fillGradient(textX, textY, textX + 32, textY + 32, TomeConstants.Colors.GRID_CELL, TomeConstants.Colors.GRID_CELL);
        TomeWidgets.drawBorder(graphics, textX, textY, 32, 32, TomeConstants.Colors.BORDER);
        renderItemIcon(graphics, resultItemId, textX + 8, textY + 8);
        graphics.text(font, Component.literal(title), textX + 40, textY + 12, TomeConstants.Colors.TEXT, false);
    }

    private int drawSpellStats(GuiGraphicsExtractor graphics, int textX, int statsY, int rightEdge) {
        int lineH = 11;
        int line = 0;

        if (spellEntry != null && spellEntry.category() != SpellCategory.INFUSION) {
            drawStatLine(graphics, textX, statsY, "Mana", SpellRegistry.getManaCostLabel(Identifier.parse(entryId)));
            line++;
        }
        if (spellEntry != null) {
            drawStatLine(graphics, textX, statsY + line * lineH, "Shape", spellEntry.shape().displayName);
            line++;
            drawStatLine(graphics, textX, statsY + line * lineH, "Type", spellEntry.category().displayName);
            line++;
        }
        drawStatLine(graphics, textX, statsY + line * lineH, "Element", elementLabel(spellNode));
        line++;

        int separatorY = statsY + line * lineH + 3;
        graphics.fillGradient(textX, separatorY, rightEdge - 16, separatorY + 1, TomeConstants.Colors.BORDER, TomeConstants.Colors.BORDER);
        return separatorY;
    }

    private int drawPassiveStats(GuiGraphicsExtractor graphics, int textX, int statsY, int rightEdge) {
        drawStatLine(graphics, textX, statsY, "Element", elementLabel(spellNode));
        int separatorY = statsY + 14;
        graphics.fillGradient(textX, separatorY, rightEdge - 16, separatorY + 1, TomeConstants.Colors.BORDER, TomeConstants.Colors.BORDER);
        return separatorY;
    }

    private int drawRitualStats(GuiGraphicsExtractor graphics, int textX, int statsY, int rightEdge) {
        drawStatLine(graphics, textX, statsY, "Type", "Ritual");
        int separatorY = statsY + 14;
        graphics.fillGradient(textX, separatorY, rightEdge - 16, separatorY + 1, TomeConstants.Colors.BORDER, TomeConstants.Colors.BORDER);
        return separatorY;
    }

    private void drawBody(GuiGraphicsExtractor graphics, int contentStartY, int x, int w, int bodyW) {
        int textX = x + 16;
        int footerY = layout.footerY;
        int backButtonY = layout.backButtonY();
        int regionBottom = backButtonY - 4;
        int regionH = regionBottom - contentStartY;

        if (pages.isEmpty()) return;

        TomeEntryLoader.Page currentPage = pages.get(activePage);
        int contentH = measurePageHeight(currentPage, bodyW);
        int maxScroll = Math.max(0, contentH - regionH);
        scrollOffset = Math.clamp(scrollOffset, 0, maxScroll);

        if (contentH > regionH) {
            TomeWidgets.drawScrollbar(graphics, x + w - 8, contentStartY, regionBottom, regionH, scrollOffset, contentH);
        }

        graphics.enableScissor(x, contentStartY - 2, x + w - 10, regionBottom);
        renderPage(graphics, currentPage, textX, contentStartY - scrollOffset, bodyW);
        graphics.disableScissor();

        if (pages.size() > 1) {
            String counter = "Page " + (activePage + 1) + " / " + pages.size();
            graphics.text(font, Component.literal(counter), x + w - font.width(counter) - 14, footerY - 14, TomeConstants.Colors.TEXT_MUTED, false);
        }
    }

    private int measurePageHeight(TomeEntryLoader.Page page, int bodyW) {
        int height = 0;
        for (TomeEntryLoader.Section section : page.getVisibleSections()) {
            if (!section.content().isEmpty()) {
                height += TextRenderer.measureHeight(section.content(), bodyW, font) + 6;
            }
            if (section.recipe() != null) {
                height += 110; // 3x3 grid @ 16px per slot + padding
            }
        }
        return height;
    }

    private void renderPage(GuiGraphicsExtractor graphics, TomeEntryLoader.Page page, int x, int y, int w) {
        int currentY = y;

        for (TomeEntryLoader.Section section : page.getVisibleSections()) {
            if (!section.content().isEmpty()) {
                TextRenderer.draw(graphics, font, section.content(), x, currentY, w, TomeConstants.Colors.TEXT_MUTED);
                int textHeight = TextRenderer.measureHeight(section.content(), w, font);
                currentY += textHeight + 6;
            }

            if (section.recipe() != null) {
                currentY = renderRecipe(graphics, section.recipe(), x, currentY);
                currentY += 6;
            }
        }
    }

    private int renderRecipe(GuiGraphicsExtractor graphics, TomeEntryLoader.RecipeDisplay recipe, int x, int y) {
        if (recipe.type().equals("shaped_3x3")) {
            return renderShaped3x3(graphics, recipe, x, y);
        }
        return y;
    }

    private int renderShaped3x3(GuiGraphicsExtractor graphics, TomeEntryLoader.RecipeDisplay recipe, int x, int y) {
        int slotSize = 18;
        int gridSize = slotSize * 3;
        int startX = x + 20;

        // Draw grid background cells
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int slotX = startX + col * slotSize;
                int slotY = y + row * slotSize;
                graphics.fillGradient(slotX, slotY, slotX + slotSize, slotY + slotSize,
                    TomeConstants.Colors.GRID_CELL, TomeConstants.Colors.GRID_CELL);
            }
        }

        // Draw internal grid lines (# shape, no outer border)
        int borderColor = TomeConstants.Colors.BORDER;
        // Vertical lines (between columns)
        for (int col = 1; col < 3; col++) {
            int lineX = startX + col * slotSize;
            graphics.fillGradient(lineX, y, lineX + 1, y + gridSize, borderColor, borderColor);
        }
        // Horizontal lines (between rows)
        for (int row = 1; row < 3; row++) {
            int lineY = y + row * slotSize;
            graphics.fillGradient(startX, lineY, startX + gridSize, lineY + 1, borderColor, borderColor);
        }

        // Render items in grid and store positions for hover detection
        List<String> grid = recipe.grid();
        for (int i = 0; i < Math.min(9, grid.size()); i++) {
            String itemId = grid.get(i);
            if (itemId.isEmpty()) continue;

            int row = i / 3;
            int col = i % 3;
            int slotX = startX + col * slotSize + 1;
            int slotY = y + row * slotSize + 1;

            renderItemIcon(graphics, itemId, slotX, slotY);
            recipeSlots.add(new ItemSlot(itemId, slotX, slotY, 16));
        }

        return y + gridSize + 10;
    }

    private void renderItemIcon(GuiGraphicsExtractor graphics, String itemId, int x, int y) {
        try {
            Identifier id = Identifier.parse(itemId);
            Item item = BuiltInRegistries.ITEM.getValue(id);
            if (item != Items.AIR) {
                ItemStack stack = new ItemStack(item);
                graphics.item(stack, x, y);
            }
        } catch (Exception e) {
            // Invalid item ID, skip rendering
        }
    }

    private void drawStatLine(GuiGraphicsExtractor graphics, int x, int y, String label, String value) {
        String labelPart = label + ": ";
        graphics.text(font, Component.literal(labelPart), x, y, TomeConstants.Colors.TEXT_MUTED, false);
        graphics.text(font, Component.literal(value), x + font.width(labelPart), y, TomeConstants.Colors.TEXT, false);
    }

    private static String elementLabel(@Nullable SkillNode node) {
        if (node == null || node.element() == null) return "Unknown";
        var element = node.element();
        if (element == SoulmarkElements.NONE.get()) return "None";
        if (element == SoulmarkElements.FIRE.get()) return "Fire";
        if (element == SoulmarkElements.WATER.get()) return "Water";
        if (element == SoulmarkElements.EARTH.get()) return "Earth";
        if (element == SoulmarkElements.AIR.get()) return "Air";
        if (element == SoulmarkElements.LIGHT.get()) return "Light";
        if (element == SoulmarkElements.DARK.get()) return "Dark";
        return "None";
    }

    // Navigation methods
    public void nextPage() {
        if (canGoNext()) {
            activePage++;
            scrollOffset = 0;
        }
    }

    public void previousPage() {
        if (canGoPrevious()) {
            activePage--;
            scrollOffset = 0;
        }
    }

    public boolean canGoNext() {
        return activePage < pages.size() - 1;
    }

    public boolean canGoPrevious() {
        return activePage > 0;
    }

    public void scroll(int delta) {
        if (pages.isEmpty()) return;

        int bodyW = layout.bookW - 32;
        TomeEntryLoader.Page currentPage = pages.get(activePage);
        int contentH = measurePageHeight(currentPage, bodyW);
        int contentStartY = getContentStartY();
        int backButtonY = layout.backButtonY();
        int regionBottom = backButtonY - 4;
        int regionH = regionBottom - contentStartY;
        int maxScroll = Math.max(0, contentH - regionH);
        scrollOffset = Math.clamp(scrollOffset + delta, 0, maxScroll);
    }

    private int getContentStartY() {
        int textY = layout.bookY + 16;
        return switch (type) {
            case SPELL -> textY + 40 + getSpellStatsHeight() + 7;
            case PASSIVE -> textY + 40 + 14 + 7;
            case RITUAL -> textY + 40 + 14 + 7;
            case CRAFTING -> resultItemId != null ? textY + 40 : textY + 18;
            case KNOWLEDGE, SCAR -> textY + 18;
        };
    }

    private int getSpellStatsHeight() {
        int lines = 1; // Element always shown
        if (spellEntry != null) {
            if (spellEntry.category() != SpellCategory.INFUSION) lines++;
            lines += 2; // Shape + Type
        }
        return lines * 11 + 3;
    }

    public int getActivePage() {
        return activePage;
    }

    public void setActivePage(int page) {
        if (pages.isEmpty()) {
            activePage = 0;
            return;
        }

        activePage = Math.clamp(page, 0, pages.size() - 1);
    }

    public int getPageCount() {
        return pages.size();
    }

    public @Nullable String getActiveStructure() {
        return pages.isEmpty() ? null : pages.get(activePage).structure();
    }

    public @Nullable String getHoveredRecipeItem(int mouseX, int mouseY) {
        for (ItemSlot slot : recipeSlots) {
            if (TomeLayout.isInside(mouseX, mouseY, slot.x, slot.y, slot.size, slot.size)) {
                return getItemDisplayName(slot.itemId);
            }
        }
        return null;
    }

    private @Nullable String getItemDisplayName(String itemId) {
        try {
            Identifier id = Identifier.parse(itemId);
            Item item = BuiltInRegistries.ITEM.getValue(id);
            if (item != Items.AIR) {
                ItemStack stack = new ItemStack(item);
                return stack.getHoverName().getString();
            }
        } catch (Exception e) {
            // Invalid item ID
        }
        return null;
    }

    public EntryType getType() {
        return type;
    }
}
