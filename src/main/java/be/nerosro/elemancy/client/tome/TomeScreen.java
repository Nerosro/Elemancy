package be.nerosro.elemancy.client.tome;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.jspecify.annotations.NonNull;

import be.nerosro.elemancy.client.tome.data.TomeEntryLoader;
import be.nerosro.elemancy.client.tome.rendering.TextRenderer;
import be.nerosro.elemancy.client.tome.rendering.TomeLayout;
import be.nerosro.elemancy.client.tome.rendering.TomeWidgets;
import be.nerosro.elemancy.client.tome.views.GridIndexView;
import be.nerosro.elemancy.client.tome.views.IdentityView;
import be.nerosro.elemancy.client.tome.views.TomeTabView;
import be.nerosro.elemancy.client.tome.views.detail.EntryReaderView;
import be.nerosro.elemancy.client.tome.views.index.CraftingView;
import be.nerosro.elemancy.client.tome.views.index.KnowledgeView;
import be.nerosro.elemancy.client.tome.views.index.PassivesView;
import be.nerosro.elemancy.client.tome.views.index.RitualsView;
import be.nerosro.elemancy.client.tome.views.index.ScarsView;
import be.nerosro.elemancy.client.tome.views.index.SkillNodeIndexEntry;
import be.nerosro.elemancy.client.tome.views.index.SpellsView;
import be.nerosro.elemancy.skilltree.SkillTreeEntries;
import be.nerosro.elemancy.spell.SpellEntry;
import be.nerosro.elemancy.spell.SpellRegistry;
import be.nerosro.soulmark.network.ClientSkillTreeData;
import be.nerosro.soulmark.skilltree.NodeType;
import be.nerosro.soulmark.skilltree.SkillNode;
import be.nerosro.soulmark.skilltree.SkillTreeRegistries;
import be.nerosro.soulmark.skilltree.SkillTreeUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Main Tome screen displaying Identity, Spells, Passives, and Knowledge tabs.
 */
public class TomeScreen extends Screen {
    private static final int TAB_GAP = 0;

    private TomeLayout layout;

    // ── Session state (in-memory, survives screen close/reopen, cleared on logout) ──
    private record SessionState(String tabId, ReaderMode mode, String entryId, int page,
                                EntryReaderView.EntryType entryType) {
    }

    private static SessionState savedSession = null;

    /**
     * Called on client logout to ensure the next login always opens on Identity.
     */
    public static void clearSessionState() {
        savedSession = null;
    }

    private final Player player;
    private final InteractionHand hand;

    private ReaderMode readerMode = ReaderMode.INDEX;
    private boolean initialized = false;

    private final List<SkillNodeIndexEntry> spellEntries = new ArrayList<>();
    private final List<SkillNodeIndexEntry> passiveEntries = new ArrayList<>();
    private final List<SkillNodeIndexEntry> ritualEntries = new ArrayList<>();
    private final List<KnowledgeView.KnowledgeIndexEntry> knowledgeEntries = new ArrayList<>();
    private final List<CraftingView.CraftingIndexEntry> craftingEntries = new ArrayList<>();

    private TabRegistry tabRegistry;
    private TomeTabView activeTab;
    private EntryReaderView entryReaderView;

    private String activeEntryId;

    private int flipTicks = 0;

    private enum ReaderMode {
        INDEX,
        ENTRY
    }


    public TomeScreen(Player player, InteractionHand hand) {
        super(Component.literal("Tome"));
        this.player = player;
        this.hand = hand;
    }

    @Override
    protected void init() {
        super.init();

        // Save current state before recreating views (init called on resize)
        if (initialized) {
            saveViewState();
        }

        layout = new TomeLayout(this.width, this.height);
        rebuildSpellEntries();
        rebuildPassiveEntries();
        rebuildRitualEntries();
        rebuildKnowledgeEntries();
        rebuildCraftingEntries();

        // Create tab registry and register tabs
        tabRegistry = new TabRegistry();

        IdentityView identityView = new IdentityView(this.font, layout, player, player.getItemInHand(hand), this::startFlip);
        SpellsView spellsView = new SpellsView(this.font, layout, spellEntries,
            id -> openSpellEntry(id, true), (g, entry, x, y) -> drawSpellIcon(g, entry.node(), x, y));
        PassivesView passivesView = new PassivesView(this.font, layout, passiveEntries,
            id -> openPassiveEntry(id, true), (g, entry, x, y) -> drawSpellIcon(g, entry.node(), x, y));
        RitualsView ritualsView = new RitualsView(this.font, layout, ritualEntries,
            id -> openRitualEntry(id, true), (g, entry, x, y) -> drawSpellIcon(g, entry.node(), x, y));
        KnowledgeView knowledgeView = new KnowledgeView(this.font, knowledgeEntries,
            id -> openKnowledgeEntry(id, true));
        CraftingView craftingView = new CraftingView(this.font, layout, craftingEntries,
            id -> openCraftingEntry(id, true), (g, entry, x, y) -> drawItemIcon(g, entry.itemId(), x, y));
        ScarsView scarsView = new ScarsView(this.font, layout, id -> openScarEntry(id, true));

        tabRegistry.register(identityView);
        tabRegistry.register(scarsView);
        tabRegistry.register(spellsView);
        tabRegistry.register(passivesView);
        tabRegistry.register(ritualsView);
        tabRegistry.register(craftingView);
        tabRegistry.register(knowledgeView);

        entryReaderView = new EntryReaderView(this.font, layout);

        // Always restore view state after creating fresh EntryReaderView
        // (init() is called on window resize, not just first open)
        loadViewState();

        if (!initialized) {
            initialized = true;
        }
    }

    @Override
    public void removed() {
        saveViewState();
        super.removed();
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        graphics.fillGradient(0, 0, this.width, this.height, TomeConstants.Colors.BG, TomeConstants.Colors.BG);

        int bookX = layout.bookX;
        int bookY = layout.bookY;
        int bookW = layout.bookW;
        int bookH = layout.bookH;

        drawBook(graphics, bookX, bookY, bookW, bookH);
        drawTabRail(graphics, mouseX, mouseY);

        if (readerMode == ReaderMode.INDEX && activeTab != null) {
            activeTab.draw(graphics, mouseX, mouseY, bookX, bookY, bookW, bookH);
        } else if (readerMode == ReaderMode.ENTRY) {
            entryReaderView.draw(graphics, bookX, bookY, bookW, this::drawSpellIcon);
            drawReaderFooter(graphics);
        }

        drawTitle(graphics);
        drawFlipOverlay(graphics, bookX, bookY, bookW, bookH);

        if (readerMode == ReaderMode.INDEX && activeTab != null && activeTab instanceof GridIndexView<?> gridView) {
            String hovered = gridView.getHoveredName(mouseX, mouseY, bookX, bookY, bookW);
            if (hovered != null) {
                TomeWidgets.drawTooltip(graphics, this.font, hovered, mouseX, mouseY, this.width, this.height);
            }
        }

        if (readerMode == ReaderMode.ENTRY) {
            String hoveredRecipeItem = entryReaderView.getHoveredRecipeItem(mouseX, mouseY);
            if (hoveredRecipeItem != null) {
                TomeWidgets.drawTooltip(graphics, this.font, hoveredRecipeItem, mouseX, mouseY, this.width, this.height);
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (activeTab instanceof IdentityView identityView && identityView.isShowingTraitsDetail()) {
            identityView.scroll((int) (-scrollY * 10));
            return true;
        }
        if (readerMode == ReaderMode.ENTRY) {
            entryReaderView.scroll((int) (-scrollY * 10));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == 256) {
            this.onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() != 0) return super.mouseClicked(event, doubleClick);

        int mouseX = (int) event.x();
        int mouseY = (int) event.y();

        if (tryClickTab(mouseX, mouseY)) {
            return true;
        }

        if (readerMode == ReaderMode.INDEX && activeTab != null) {
            if (activeTab.handleClick(mouseX, mouseY, layout.bookX, layout.bookY, layout.bookW)) {
                return true;
            }
        }

        if (readerMode == ReaderMode.ENTRY) {
            if (TomeLayout.isInside(mouseX, mouseY, layout.backButtonX(), layout.backButtonY(), 124, 20)) {
                readerMode = ReaderMode.INDEX;
                startFlip();
                return true;
            }

            if (TomeLayout.isInside(mouseX, mouseY, layout.prevButtonX(), layout.footerY, 64, 20) && entryReaderView.canGoPrevious()) {
                entryReaderView.previousPage();
                startFlip();
                return true;
            }

            if (TomeLayout.isInside(mouseX, mouseY, layout.nextButtonX(64), layout.footerY, 64, 20) && entryReaderView.canGoNext()) {
                entryReaderView.nextPage();
                startFlip();
                return true;
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void drawTitle(GuiGraphicsExtractor graphics) {
        TextRenderer.drawCentered(graphics, this.font, "Tome", this.width / 2, 8, TomeConstants.Colors.TEXT);
    }

    private void drawBook(GuiGraphicsExtractor graphics, int x, int y, int w, int h) {
        graphics.fillGradient(x, y, x + w, y + h, TomeConstants.Colors.BOOK, TomeConstants.Colors.BOOK);
        graphics.fillGradient(x, y, x + w, y + 1, TomeConstants.Colors.BORDER, TomeConstants.Colors.BORDER);
        graphics.fillGradient(x, y + h - 1, x + w, y + h, TomeConstants.Colors.BORDER, TomeConstants.Colors.BORDER);
        graphics.fillGradient(x, y, x + 1, y + h, TomeConstants.Colors.BORDER, TomeConstants.Colors.BORDER);
        // No right border — the tab rail sits flush here and provides its own left border
    }

    private void drawTabRail(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int tabWidth = layout.bookW / 7;
        int tabHeight = layout.bookH / 7;
        int x = layout.tabX;
        int y = layout.bookY;

        for (TomeTabView tab : tabRegistry.getVisibleTabs()) {
            boolean selected = tab == activeTab;
            boolean hovered = TomeLayout.isInside(mouseX, mouseY, x, y, tabWidth, tabHeight);

            int bg = selected ? TomeConstants.Colors.TAB_ACTIVE : TomeConstants.Colors.TAB;
            if (hovered && !selected) bg = TomeConstants.Colors.TAB_HOVER;

            // Background
            graphics.fillGradient(x, y, x + tabWidth, y + tabHeight, bg, bg);

            // Top border (shared with tab above, except first tab)
            graphics.fillGradient(x, y, x + tabWidth, y + 1, TomeConstants.Colors.BORDER, TomeConstants.Colors.BORDER);

            // Left border (active tab has no left border — merges with book page)
            if (!selected) {
                graphics.fillGradient(x, y, x + 1, y + tabHeight, TomeConstants.Colors.BORDER, TomeConstants.Colors.BORDER);
            }

            // Right border
            graphics.fillGradient(x + tabWidth - 1, y, x + tabWidth, y + tabHeight, TomeConstants.Colors.BORDER, TomeConstants.Colors.BORDER);

            // Icon (centered in tab)
            int iconSize = 18;
            int iconX = x + (tabWidth - iconSize) / 2;
            int iconY = y + (tabHeight - iconSize) / 2;
            graphics.blit(RenderPipelines.GUI_TEXTURED, tab.getIcon(),
                iconX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);

            y += tabHeight + TAB_GAP;
        }

        // Bottom border for last visible tab
        graphics.fillGradient(x, y - TAB_GAP, x + tabWidth, y - TAB_GAP + 1, TomeConstants.Colors.BORDER, TomeConstants.Colors.BORDER);
    }


    private void drawReaderFooter(GuiGraphicsExtractor graphics) {
        int footerY = layout.footerY;
        boolean multiPage = entryReaderView.getPageCount() > 1;

        if (multiPage) {
            TomeWidgets.drawButton(graphics, this.font, layout.prevButtonX(), footerY, 64, 20, "Prev", entryReaderView.canGoPrevious());
            TomeWidgets.drawButton(graphics, this.font, layout.nextButtonX(64), footerY, 64, 20, "Next", entryReaderView.canGoNext());
        }
        TomeWidgets.drawButton(graphics, this.font, layout.backButtonX(), layout.backButtonY(), 124, 20, "Back to Index", true);
    }

    private void drawSpellIcon(GuiGraphicsExtractor graphics, SkillNode node, int cx, int cy) {
        Identifier icon = node.icon();
        if (icon != null) {
            int iconSize = 24;
            int half = iconSize / 2;
            graphics.blit(RenderPipelines.GUI_TEXTURED, icon,
                cx - half, cy - half, 0, 0, iconSize, iconSize, iconSize, iconSize);
            return;
        }

        String marker = switch (node.nodeType()) {
            case UTILITY -> "✦";
            case ABILITY -> "✴";
            default -> "●";
        };
        TextRenderer.drawCentered(graphics, this.font, marker, cx, cy - 4, TomeConstants.Colors.TEXT);
    }

    private void drawItemIcon(GuiGraphicsExtractor graphics, String itemId, int cx, int cy) {
        try {
            Identifier id = Identifier.parse(itemId);
            Item item = BuiltInRegistries.ITEM.getValue(id);
            if (item != Items.AIR) {
                ItemStack stack = new ItemStack(item);
                // Render at default 16x16 size, centered in the 32x32 cell
                graphics.item(stack, cx - 8, cy - 8);
            }
        } catch (Exception e) {
            // Invalid item ID, skip rendering
        }
    }

    private void drawFlipOverlay(GuiGraphicsExtractor graphics, int x, int y, int w, int h) {
        if (flipTicks <= 0) return;

        int alpha = Math.clamp(flipTicks * 26L, 0, 255);
        int color = (alpha << 24) | 0x00FFFFFF;
        graphics.fillGradient(x, y, x + w, y + h, color, color);
        flipTicks--;
    }

    private boolean tryClickTab(int mouseX, int mouseY) {
        int tabWidth = layout.bookW / 7;
        int tabHeight = layout.bookH / 7;
        int x = layout.tabX;
        int y = layout.bookY;

        for (TomeTabView tab : tabRegistry.getVisibleTabs()) {
            if (TomeLayout.isInside(mouseX, mouseY, x, y, tabWidth, tabHeight)) {
                switchTab(tab);
                return true;
            }
            y += tabHeight + TAB_GAP;
        }

        return false;
    }

    private void switchTab(TomeTabView tab) {
        activeTab = tab;
        setActiveEntry(null, "", List.of(), EntryReaderView.EntryType.KNOWLEDGE, null, null, null, false);
        readerMode = ReaderMode.INDEX;
    }

    private boolean openSpellEntry(Identifier spellNodeId, boolean animate) {
        SkillNode node = SkillTreeRegistries.NODE_REGISTRY.getValue(spellNodeId);
        if (node == null) return false;

        Optional<TomeEntryLoader.TomeEntry> loaded = TomeEntryLoader.loadSpellEntry(spellNodeId);
        List<TomeEntryLoader.Page> pages = loaded.map(tomeEntry -> filterVisiblePages(tomeEntry.pages())).orElseGet(() -> defaultSpellPages(node));
        String title = loaded.map(TomeEntryLoader.TomeEntry::title).orElse(node.name());

        setActiveEntry(spellNodeId.toString(), title, pages, EntryReaderView.EntryType.SPELL, node, SpellRegistry.get(spellNodeId), null, animate);
        return true;
    }

    private boolean openPassiveEntry(Identifier nodeId, boolean animate) {
        SkillNode node = SkillTreeRegistries.NODE_REGISTRY.getValue(nodeId);
        if (node == null) return false;

        Optional<TomeEntryLoader.TomeEntry> loaded = TomeEntryLoader.loadPassiveEntry(nodeId);
        List<TomeEntryLoader.Page> pages = loaded.map(tomeEntry -> filterVisiblePages(tomeEntry.pages())).orElseGet(() -> List.of(new TomeEntryLoader.Page(List.of(new TomeEntryLoader.Section(node.description(), null, null)))));
        String title = loaded.map(TomeEntryLoader.TomeEntry::title).orElse(node.name());

        setActiveEntry(nodeId.toString(), title, pages, EntryReaderView.EntryType.PASSIVE, node, null, null, animate);
        return true;
    }

    private boolean openRitualEntry(Identifier nodeId, boolean animate) {
        SkillNode node = SkillTreeRegistries.NODE_REGISTRY.getValue(nodeId);
        if (node == null) return false;

        List<TomeEntryLoader.Page> pages = List.of(new TomeEntryLoader.Page(
            List.of(new TomeEntryLoader.Section(node.description(), null, null))));
        setActiveEntry(nodeId.toString(), node.name(), pages, EntryReaderView.EntryType.RITUAL, node, null, null, animate);
        return true;
    }

    private boolean openKnowledgeEntry(String entryId, boolean animate) {
        Optional<TomeEntryLoader.TomeEntry> loaded = TomeEntryLoader.loadKnowledgeEntry(entryId);
        if (loaded.isEmpty()) return false;

        TomeEntryLoader.TomeEntry entry = loaded.get();
        List<TomeEntryLoader.Page> pages = filterVisiblePages(entry.pages());
        setActiveEntry(entry.id(), entry.title(), pages, EntryReaderView.EntryType.KNOWLEDGE, null, null, null, animate);
        return true;
    }

    private boolean openCraftingEntry(String entryId, boolean animate) {
        Optional<TomeEntryLoader.TomeEntry> loaded = TomeEntryLoader.loadCraftingEntry(entryId);
        if (loaded.isEmpty()) return false;

        TomeEntryLoader.TomeEntry entry = loaded.get();
        List<TomeEntryLoader.Page> pages = filterVisiblePages(entry.pages());
        String resultItemId = extractResultItemId(entry);
        setActiveEntry(entry.id(), entry.title(), pages, EntryReaderView.EntryType.CRAFTING, null, null, resultItemId, animate);
        return true;
    }

    private boolean openScarEntry(String scarId, boolean animate) {
        Optional<TomeEntryLoader.TomeEntry> loaded = TomeEntryLoader.loadScarEntry(scarId);
        if (loaded.isEmpty()) return false;

        TomeEntryLoader.TomeEntry entry = loaded.get();
        List<TomeEntryLoader.Page> pages = filterVisiblePages(entry.pages());
        setActiveEntry(entry.id(), entry.title(), pages, EntryReaderView.EntryType.SCAR, null, null, null, animate);
        return true;
    }

    private List<TomeEntryLoader.Page> filterVisiblePages(List<TomeEntryLoader.Page> pages) {
        List<TomeEntryLoader.Page> visible = pages.stream()
            .filter(page -> !page.getVisibleSections().isEmpty())
            .toList();

        if (visible.isEmpty()) {
            return List.of(new TomeEntryLoader.Page(
                List.of(new TomeEntryLoader.Section("Additional content locked.\n\nProgress further to unlock.", null, null))
            ));
        }

        return visible;
    }

    private @org.jspecify.annotations.Nullable String extractResultItemId(TomeEntryLoader.TomeEntry entry) {
        for (TomeEntryLoader.Page page : entry.pages()) {
            for (TomeEntryLoader.Section section : page.sections()) {
                if (section.recipe() != null) {
                    return section.recipe().result();
                }
            }
        }
        return null;
    }

    private void setActiveEntry(String id, String title, List<TomeEntryLoader.Page> pages, EntryReaderView.EntryType type, SkillNode node, SpellEntry spell, @org.jspecify.annotations.Nullable String resultItemId, boolean animate) {
        activeEntryId = id;
        entryReaderView.setEntry(id, type, title, pages, node, spell, resultItemId);
        readerMode = ReaderMode.ENTRY;
        if (animate) startFlip();
    }

    private void rebuildSpellEntries() {
        spellEntries.clear();

        for (Identifier nodeId : SkillTreeUtil.getAllNodesInTree(SkillTreeEntries.TREE_ID)) {
            if (!ClientSkillTreeData.isUnlocked(nodeId)) continue;

            SkillNode node = SkillTreeRegistries.NODE_REGISTRY.getValue(nodeId);
            if (node == null) continue;
            if (node.nodeType() != NodeType.ABILITY && node.nodeType() != NodeType.UTILITY) continue;

            spellEntries.add(new SkillNodeIndexEntry(nodeId, node));
        }

        sortEntriesByLayoutPosition(spellEntries, SkillNodeIndexEntry::node);
    }

    private void rebuildKnowledgeEntries() {
        knowledgeEntries.clear();
        for (TomeEntryLoader.TomeEntry entry : TomeEntryLoader.loadKnowledgeEntries()) {
            // Check entry-level requirements first, then visible content
            if (entry.isVisible() && entry.hasVisibleContent()) {
                knowledgeEntries.add(new KnowledgeView.KnowledgeIndexEntry(entry.id(), entry.title()));
            }
        }
    }

    private void rebuildCraftingEntries() {
        craftingEntries.clear();
        for (TomeEntryLoader.TomeEntry entry : TomeEntryLoader.loadCraftingEntries()) {
            if (entry.isVisible() && entry.hasVisibleContent()) {
                craftingEntries.add(new CraftingView.CraftingIndexEntry(entry.id(), entry.title(), entry.getResultItemId()));
            }
        }
    }

    private void rebuildPassiveEntries() {
        passiveEntries.clear();

        for (Identifier nodeId : SkillTreeUtil.getAllNodesInTree(SkillTreeEntries.TREE_ID)) {
            if (!ClientSkillTreeData.isUnlocked(nodeId)) continue;

            SkillNode node = SkillTreeRegistries.NODE_REGISTRY.getValue(nodeId);
            if (node == null) continue;
            if (node.nodeType() != NodeType.PASSIVE) continue;

            passiveEntries.add(new SkillNodeIndexEntry(nodeId, node));
        }

        sortEntriesByLayoutPosition(passiveEntries, SkillNodeIndexEntry::node);
    }

    private void rebuildRitualEntries() {
        ritualEntries.clear();

        for (Identifier nodeId : SkillTreeUtil.getAllNodesInTree(SkillTreeEntries.TREE_ID)) {
            if (!ClientSkillTreeData.isUnlocked(nodeId)) continue;

            SkillNode node = SkillTreeRegistries.NODE_REGISTRY.getValue(nodeId);
            if (node == null || node.nodeType() != NodeType.RITUAL) continue;

            ritualEntries.add(new SkillNodeIndexEntry(nodeId, node));
        }

        sortEntriesByLayoutPosition(ritualEntries, SkillNodeIndexEntry::node);
    }

    private void loadViewState() {
        if (savedSession == null) {
            // No session - default to identity tab
            activeTab = tabRegistry.getById("identity");
            return;
        }

        String tabId = savedSession.tabId();
        TomeTabView tab = tabRegistry.getById(tabId);

        // Guard: if saved tab doesn't exist or isn't visible, fall back to identity
        if (tab == null || !tab.isVisible()) {
            activeTab = tabRegistry.getById("identity");
            return;
        }

        activeTab = tab;

        readerMode = savedSession.mode();
        if (readerMode != ReaderMode.ENTRY) return;

        String entryId = savedSession.entryId();
        if (entryId == null || entryId.isEmpty()) {
            readerMode = ReaderMode.INDEX;
            return;
        }

        boolean opened;
        try {
            if (savedSession.entryType() == EntryReaderView.EntryType.SPELL) {
                opened = openSpellEntry(Identifier.parse(entryId), false);
            } else if (savedSession.entryType() == EntryReaderView.EntryType.PASSIVE) {
                opened = openPassiveEntry(Identifier.parse(entryId), false);
            } else if (savedSession.entryType() == EntryReaderView.EntryType.RITUAL) {
                opened = openRitualEntry(Identifier.parse(entryId), false);
            } else if (savedSession.entryType() == EntryReaderView.EntryType.CRAFTING) {
                opened = openCraftingEntry(entryId, false);
            } else if (savedSession.entryType() == EntryReaderView.EntryType.SCAR) {
                opened = openScarEntry(entryId, false);
            } else {
                opened = openKnowledgeEntry(entryId, false);
            }
        } catch (Exception ex) {
            opened = false;
        }

        if (!opened) {
            readerMode = ReaderMode.INDEX;
            return;
        }

        entryReaderView.setActivePage(savedSession.page());
    }

    private void saveViewState() {
        String tabId = activeTab != null ? activeTab.getId() : "identity";
        String entryId = (readerMode == ReaderMode.ENTRY && activeEntryId != null) ? activeEntryId : "";
        int page = readerMode == ReaderMode.ENTRY ? entryReaderView.getActivePage() : 0;
        EntryReaderView.EntryType type = readerMode == ReaderMode.ENTRY ? entryReaderView.getType() : EntryReaderView.EntryType.KNOWLEDGE;
        savedSession = new SessionState(tabId, readerMode, entryId, page, type);
    }

    private List<TomeEntryLoader.Page> defaultSpellPages(SkillNode node) {
        return List.of(new TomeEntryLoader.Page(
            List.of(new TomeEntryLoader.Section(node.description(), null, null))
        ));
    }

    /**
     * Sorts entries by progression depth, then sibling lane within each tier.
     * Used by spell and passive rebuild methods to maintain consistent ordering.
     *
     * @param entries       List of entries to sort in-place
     * @param nodeExtractor Function to extract SkillNode from entry type
     */
    private static <T> void sortEntriesByLayoutPosition(List<T> entries, Function<T, SkillNode> nodeExtractor) {
        entries.sort((a, b) -> {
            SkillNode nodeA = nodeExtractor.apply(a);
            SkillNode nodeB = nodeExtractor.apply(b);
            int depthA = nodeA.depth();
            int depthB = nodeB.depth();
            if (depthA != depthB) return Integer.compare(depthA, depthB);
            return Integer.compare(nodeA.lane(), nodeB.lane());
        });
    }

    private void startFlip() {
        flipTicks = 6;
    }
}
