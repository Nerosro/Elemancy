package be.nerosro.elemancy.client.tome.views.index;

import java.util.List;
import java.util.function.Consumer;

import be.nerosro.elemancy.client.tome.TomeConstants;
import be.nerosro.elemancy.client.tome.rendering.TomeLayout;
import be.nerosro.elemancy.client.tome.views.GridIndexView;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.Identifier;

/**
 * View for Crafting tab index - grid of craftable item icons.
 */
public class CraftingView extends GridIndexView<CraftingView.CraftingIndexEntry> {
    private final Consumer<String> onOpenEntry;

    public record CraftingIndexEntry(String id, String title, String itemId) {
    }

    public CraftingView(Font font, TomeLayout layout, List<CraftingIndexEntry> entries,
                        Consumer<String> onOpenEntry, IconRenderer<CraftingIndexEntry> iconRenderer) {
        super(font, layout, entries, iconRenderer);
        this.onOpenEntry = onOpenEntry;
    }

    @Override
    protected String getTitle() {
        return "Crafting";
    }

    @Override
    protected String getSubtitle() {
        return "Select an item to view its recipe.";
    }

    @Override
    protected String getEmptyMessage() {
        return "No recipes available.";
    }

    @Override
    protected String getName(CraftingIndexEntry entry) {
        return entry.title;
    }

    @Override
    protected void onEntryClicked(CraftingIndexEntry entry) {
        onOpenEntry.accept(entry.id);
    }

    @Override
    public boolean isVisible() {
        return !entries.isEmpty(); // Only visible when at least one crafting entry is unlocked
    }

    @Override
    public Identifier getIcon() {
        return TomeConstants.Icons.TAB_CRAFTING;
    }

    @Override
    public String getId() {
        return "crafting";
    }
}
