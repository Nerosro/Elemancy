package be.nerosro.elemancy.client.tome.views.index;

import java.util.List;
import java.util.function.Consumer;

import be.nerosro.elemancy.client.tome.TomeConstants;
import be.nerosro.elemancy.client.tome.rendering.TomeLayout;
import be.nerosro.elemancy.client.tome.views.GridIndexView;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.Identifier;

/**
 * View for Passives tab index - grid of unlocked passive icons.
 */
public class PassivesView extends GridIndexView<SkillNodeIndexEntry> {
    private final Consumer<Identifier> onOpenEntry;

    public PassivesView(Font font, TomeLayout layout, List<SkillNodeIndexEntry> entries,
                        Consumer<Identifier> onOpenEntry, IconRenderer<SkillNodeIndexEntry> iconRenderer) {
        super(font, layout, entries, iconRenderer);
        this.onOpenEntry = onOpenEntry;
    }

    @Override
    protected String getTitle() {
        return "Passives";
    }

    @Override
    protected String getSubtitle() {
        return "Select an icon to read about this ability.";
    }

    @Override
    protected String getEmptyMessage() {
        return "No passives unlocked yet.";
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
        return !entries.isEmpty(); // Only visible when passives unlocked
    }

    @Override
    public net.minecraft.resources.Identifier getIcon() {
        return TomeConstants.Icons.TAB_PASSIVES;
    }

    @Override
    public String getId() {
        return "passives";
    }
}
