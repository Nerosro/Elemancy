package be.nerosro.elemancy.client.tome.views.index;

import java.util.List;
import java.util.function.Consumer;

import be.nerosro.elemancy.client.tome.TomeConstants;
import be.nerosro.elemancy.client.tome.rendering.TomeLayout;
import be.nerosro.elemancy.client.tome.views.GridIndexView;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.Identifier;

/**
 * View for Spells tab index - grid of unlocked spell icons.
 */
public class SpellsView extends GridIndexView<SkillNodeIndexEntry> {
    private final Consumer<Identifier> onOpenEntry;

    public SpellsView(Font font, TomeLayout layout, List<SkillNodeIndexEntry> entries,
                      Consumer<Identifier> onOpenEntry, IconRenderer<SkillNodeIndexEntry> iconRenderer) {
        super(font, layout, entries, iconRenderer);
        this.onOpenEntry = onOpenEntry;
    }

    @Override
    protected String getTitle() {
        return "Spells";
    }

    @Override
    protected String getSubtitle() {
        return "Select an icon to read about this spell.";
    }

    @Override
    protected String getEmptyMessage() {
        return "No unlocked spells available.";
    }

    @Override
    protected String getName(SkillNodeIndexEntry entry) {
        return entry.node().name();
    }

    @Override
    protected void onEntryClicked(SkillNodeIndexEntry entry) {
        onOpenEntry.accept(entry.id());
    }

    @Override
    public boolean isVisible() {
        return true; // Spells always visible
    }

    @Override
    public net.minecraft.resources.Identifier getIcon() {
        return TomeConstants.Icons.TAB_SPELLS;
    }

    @Override
    public String getId() {
        return "spells";
    }
}
