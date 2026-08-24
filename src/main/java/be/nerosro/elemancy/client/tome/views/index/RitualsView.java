package be.nerosro.elemancy.client.tome.views.index;

import java.util.List;
import java.util.function.Consumer;

import be.nerosro.elemancy.client.tome.TomeConstants;
import be.nerosro.elemancy.client.tome.rendering.TomeLayout;
import be.nerosro.elemancy.client.tome.views.GridIndexView;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.Identifier;

/**
 * View for the Rituals tab index.
 */
public class RitualsView extends GridIndexView<SkillNodeIndexEntry> {
    private final Consumer<Identifier> onOpenEntry;

    public RitualsView(Font font, TomeLayout layout, List<SkillNodeIndexEntry> entries,
                       Consumer<Identifier> onOpenEntry, IconRenderer<SkillNodeIndexEntry> iconRenderer) {
        super(font, layout, entries, iconRenderer);
        this.onOpenEntry = onOpenEntry;
    }

    @Override
    protected String getTitle() {
        return "Rituals";
    }

    @Override
    protected String getSubtitle() {
        return "Select a ritual to read its details.";
    }

    @Override
    protected String getEmptyMessage() {
        return "No rituals unlocked yet.";
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
        return !entries.isEmpty();
    }

    @Override
    public Identifier getIcon() {
        return TomeConstants.Icons.TAB_RITUALS;
    }

    @Override
    public String getId() {
        return "rituals";
    }
}