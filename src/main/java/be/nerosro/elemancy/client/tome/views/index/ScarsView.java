package be.nerosro.elemancy.client.tome.views.index;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import be.nerosro.elemancy.client.tome.TomeConstants;
import be.nerosro.elemancy.client.tome.rendering.TomeLayout;
import be.nerosro.elemancy.client.tome.views.GridIndexView;
import be.nerosro.elemancy.mana.depth.ScarType;
import be.nerosro.soulmark.network.ClientManaData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * View for Scars tab index - grid of scar icons with entries.
 */
public class ScarsView extends GridIndexView<ScarsView.ScarIndexEntry> {
    private final Consumer<String> onOpenEntry;

    public record ScarIndexEntry(String id, String name, Identifier icon) {
    }

    public ScarsView(Font font, TomeLayout layout, Consumer<String> onOpenEntry) {
        super(font, layout, buildEntries(),
            (g, entry, x, y) -> g.blit(RenderPipelines.GUI_TEXTURED, entry.icon, x - 8, y - 8, 0, 0, 18, 18, 18, 18));
        this.onOpenEntry = onOpenEntry;
    }

    private static List<ScarIndexEntry> buildEntries() {
        List<ScarIndexEntry> list = new ArrayList<>();

        // Physical Scars first (combined entry)
        list.add(new ScarIndexEntry(
            "physical_scars",
            "Physical Scars",
            TomeConstants.Icons.SCAR_PHYSICAL
        ));

        // ScarType entries (exclude MANA_COLLAPSE for now)
        list.add(new ScarIndexEntry(
            ScarType.ARCANE_TREMOR.tickKey().replace("_ticks", ""),
            Component.translatable(ScarType.ARCANE_TREMOR.translationKey()).getString(),
            TomeConstants.Icons.SCAR_ARCANE_TREMOR
        ));
        list.add(new ScarIndexEntry(
            ScarType.SPELL_DRIFT.tickKey().replace("_ticks", ""),
            Component.translatable(ScarType.SPELL_DRIFT.translationKey()).getString(),
            TomeConstants.Icons.SCAR_SPELL_DRIFT
        ));
        list.add(new ScarIndexEntry(
            ScarType.CHANNEL_DISRUPTION.tickKey().replace("_ticks", ""),
            Component.translatable(ScarType.CHANNEL_DISRUPTION.translationKey()).getString(),
            TomeConstants.Icons.SCAR_CHANNEL_DISRUPTION
        ));
        list.add(new ScarIndexEntry(
            ScarType.MANA_BURN.tickKey().replace("_ticks", ""),
            Component.translatable(ScarType.MANA_BURN.translationKey()).getString(),
            TomeConstants.Icons.SCAR_MANA_BURN
        ));
        list.add(new ScarIndexEntry(
            ScarType.ARCANE_FATIGUE.tickKey().replace("_ticks", ""),
            Component.translatable(ScarType.ARCANE_FATIGUE.translationKey()).getString(),
            TomeConstants.Icons.SCAR_ARCANE_FATIGUE
        ));
        list.add(new ScarIndexEntry(
            ScarType.SPELL_WEAKNESS.tickKey().replace("_ticks", ""),
            Component.translatable(ScarType.SPELL_WEAKNESS.translationKey()).getString(),
            TomeConstants.Icons.SCAR_SPELL_WEAKNESS
        ));

        // Mana Collapse only appears after player has experienced it once
        if (ClientManaData.hasExperiencedManaCollapse()) {
            list.add(new ScarIndexEntry(
                ScarType.MANA_COLLAPSE.tickKey().replace("_ticks", ""),
                Component.translatable(ScarType.MANA_COLLAPSE.translationKey()).getString(),
                TomeConstants.Icons.SCAR_MANA_COLLAPSE
            ));
        }

        return list;
    }

    @Override
    protected String getTitle() {
        return "Scars";
    }

    @Override
    protected String getSubtitle() {
        return "Select an icon to learn about this scar.";
    }

    @Override
    protected String getEmptyMessage() {
        return "No scars yet.";
    }

    @Override
    protected String getName(ScarIndexEntry entry) {
        return entry.name;
    }

    @Override
    protected void onEntryClicked(ScarIndexEntry entry) {
        onOpenEntry.accept(entry.id);
    }

    @Override
    public boolean isVisible() {
        return be.nerosro.soulmark.network.ClientManaData.isScarsRevealed();
    }

    @Override
    public net.minecraft.resources.Identifier getIcon() {
        return TomeConstants.Icons.TAB_SCARS;
    }

    @Override
    public String getId() {
        return "scars";
    }
}
