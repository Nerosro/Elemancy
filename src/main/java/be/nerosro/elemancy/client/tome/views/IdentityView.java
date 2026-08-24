package be.nerosro.elemancy.client.tome.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import be.nerosro.elemancy.client.tome.TomeConstants;
import be.nerosro.elemancy.client.tome.rendering.TextRenderer;
import be.nerosro.elemancy.client.tome.rendering.TomeLayout;
import be.nerosro.elemancy.client.tome.rendering.TomeWidgets;
import be.nerosro.elemancy.network.ClientScarData;
import be.nerosro.soulmark.element.Element;
import be.nerosro.soulmark.element.ElementRegistry;
import be.nerosro.soulmark.element.ElementUtil;
import be.nerosro.soulmark.element.SoulmarkElements;
import be.nerosro.soulmark.network.ClientAttunementData;
import be.nerosro.soulmark.network.ClientManaData;
import be.nerosro.soulmark.traits.TraitType;
import be.nerosro.soulmark.traits.TraitWeight;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * View for Identity tab - player stats, affinity, traits, and traits detail.
 */
public class IdentityView implements TomeTabView {
    private final Font font;
    private final TomeLayout layout;
    private final Player player;
    private final ItemStack tomeStack;
    private final Runnable onFlipAnimation;

    private boolean showingTraitsDetail = false;
    private int traitsScrollOffset = 0;

    public record TomeTraitEntry(String name, String description, TraitType type, TraitWeight weight) {
    }

    public IdentityView(Font font, TomeLayout layout, Player player, ItemStack tomeStack, Runnable onFlipAnimation) {
        this.font = font;
        this.layout = layout;
        this.player = player;
        this.tomeStack = tomeStack;
        this.onFlipAnimation = onFlipAnimation;
    }

    @Override
    public void draw(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y, int w, int h) {
        if (showingTraitsDetail) {
            drawTraitsDetail(graphics, x, y, w, traitsScrollOffset);
        } else {
            drawMain(graphics, x, y);
        }
    }

    @Override
    public boolean handleClick(double mouseX, double mouseY, int x, int y, int w) {
        if (showingTraitsDetail) {
            // Back button
            if (TomeLayout.isInside((int) mouseX, (int) mouseY, layout.backButtonX(), layout.backButtonY(), 124, 20)) {
                showingTraitsDetail = false;
                traitsScrollOffset = 0;
                onFlipAnimation.run();
                return true;
            }
        } else {
            // Traits detail button
            if (ClientManaData.isTraitsRevealed() && TomeLayout.isInside((int) mouseX, (int) mouseY, layout.nextButtonX(64), layout.footerY, 64, 20)) {
                showingTraitsDetail = true;
                traitsScrollOffset = 0;
                onFlipAnimation.run();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isVisible() {
        return true; // Identity tab always visible
    }

    @Override
    public Identifier getIcon() {
        return TomeConstants.Icons.TAB_IDENTITY;
    }

    @Override
    public String getId() {
        return "identity";
    }

    public void scroll(int delta) {
        if (showingTraitsDetail) {
            int contentH = measureTraitsContentHeight();
            int regionH = layout.traitsRegionH();
            int maxScroll = Math.max(0, contentH - regionH);
            traitsScrollOffset = Math.clamp(traitsScrollOffset + delta, 0, maxScroll);
        }
    }

    public boolean isShowingTraitsDetail() {
        return showingTraitsDetail;
    }

    private void drawMain(GuiGraphicsExtractor graphics, int x, int y) {
        int lineH = 11;
        int sectionGap = 6;

        // ── Player portrait (left column) ──────────────────────────────────
        int portraitX1 = x + TomeConstants.Layout.CONTENT_PADDING_X;
        int portraitY1 = y + 18;
        int portraitX2 = x + 100;
        int portraitY2 = y + 150;
        TomeWidgets.drawBorder(graphics, portraitX1 - 1, portraitY1 - 1, (portraitX2 - portraitX1) + 2, (portraitY2 - portraitY1) + 2, TomeConstants.Colors.BORDER);
        InventoryScreen.renderEntityInInventoryFollowsAngle(graphics,
            portraitX1, portraitY1, portraitX2, portraitY2,
            45, 0f, 0f, 0f, player);

        // ── Stats (right column) ────────────────────────────────────────────
        int textX = x + 120;
        int textY = y + TomeConstants.Layout.CONTENT_PADDING_Y;
        int dy = 0;

        // ── Title ──────────────────────────────────────────────────────────
        graphics.text(font, Component.literal("Identity"), textX, textY, TomeConstants.Colors.TEXT, false);
        dy += 18;

        // ── Mana ───────────────────────────────────────────────────────────
        graphics.text(font, Component.literal("Mana"), textX, textY + dy, TomeConstants.Colors.TEXT, false);
        dy += lineH;
        float manaPool = ClientManaData.getMaxPool();
        if (manaPool <= 0) {
            graphics.text(font, Component.literal("  Syncing..."), textX, textY + dy, TomeConstants.Colors.TEXT_MUTED, false);
            dy += lineH;
        } else {
            float manaBase = ClientManaData.getManaBase();
            float trinketBonus = ClientManaData.getPoolTrinketBonus();
            float displayTotal = Math.round(manaPool * 10) / 10f;
            float displayBase = Math.round(manaBase * 10) / 10f;
            float displayTrinket = Math.round(manaBase * trinketBonus * 10) / 10f;

            String manaLine;
            if (Math.abs(displayTrinket) >= 0.05f) {
                char sign = displayTrinket >= 0 ? '+' : '-';
                manaLine = String.format(Locale.ROOT, "  %.1f (%.1f base %c %.1f trinket)",
                    displayTotal, displayBase, sign, Math.abs(displayTrinket));
            } else {
                manaLine = String.format(Locale.ROOT, "  %.1f (%.1f base)", displayTotal, displayBase);
            }
            graphics.text(font, Component.literal(manaLine), textX, textY + dy, TomeConstants.Colors.TEXT_MUTED, false);
            dy += lineH;

            // Show active scars if any
            if (ClientScarData.hasAnyScars()) {
                StringBuilder scarsLine = new StringBuilder("  Active: ");
                boolean first = true;
                if (ClientScarData.hasArcaneTremor()) {
                    scarsLine.append("Arcane Tremor");
                    first = false;
                }
                if (ClientScarData.hasSpellDrift()) {
                    if (!first) scarsLine.append(", ");
                    scarsLine.append("Spell Drift");
                    first = false;
                }
                if (ClientScarData.hasChannelDisruption()) {
                    if (!first) scarsLine.append(", ");
                    scarsLine.append("Channel Disruption");
                    first = false;
                }
                if (ClientScarData.hasManaBurn()) {
                    if (!first) scarsLine.append(", ");
                    scarsLine.append("Mana Burn");
                    first = false;
                }
                if (ClientScarData.hasArcaneFatigue()) {
                    if (!first) scarsLine.append(", ");
                    scarsLine.append("Arcane Fatigue");
                    first = false;
                }
                if (ClientScarData.hasSpellWeakness()) {
                    if (!first) scarsLine.append(", ");
                    scarsLine.append("Spell Weakness");
                    first = false;
                }
                if (ClientScarData.hasManaCollapse()) {
                    if (!first) scarsLine.append(", ");
                    scarsLine.append("Mana Collapse");
                }
                graphics.text(font, Component.literal(scarsLine.toString()), textX, textY + dy, TomeConstants.Colors.TEXT_MUTED, false);
                dy += lineH;
            }
        }
        dy += sectionGap;

        // ── Affinity (always shown; "Not yet revealed" until paper used) ───
        graphics.text(font, Component.literal("Affinity"), textX, textY + dy, TomeConstants.Colors.TEXT, false);
        dy += lineH;
        if (ClientManaData.isAffinityRevealed()) {
            Element affinity = ClientManaData.getAffinity();
            if (affinity != null) {
                Identifier affinityId = ElementRegistry.ELEMENT_REGISTRY.getKey(affinity);
                String displayName = ElementUtil.getDisplayName(affinityId);
                graphics.text(font, Component.literal("  " + displayName), textX, textY + dy, TomeConstants.Colors.TEXT_MUTED, false);
            } else {
                graphics.text(font, Component.literal("  Unknown"), textX, textY + dy, TomeConstants.Colors.TEXT_MUTED, false);
            }
        } else {
            graphics.text(font, Component.literal("  Not yet revealed"), textX, textY + dy, TomeConstants.Colors.TEXT_MUTED, false);
        }
        dy += lineH + sectionGap;

        // ── Traits (always shown; "Not yet revealed" until mirror used) ───────
        graphics.text(font, Component.literal("Traits"), textX, textY + dy, TomeConstants.Colors.TEXT, false);
        dy += lineH;
        if (ClientManaData.isTraitsRevealed()) {
            for (var entry : readTraitsFromTome()) {
                drawTraitName(graphics, entry, textX + 8, textY + dy);
                dy += lineH;
            }
        } else {
            graphics.text(font, Component.literal("  Not yet revealed"), textX, textY + dy, TomeConstants.Colors.TEXT_MUTED, false);
            dy += lineH;
        }
        dy += sectionGap;

        // ── Attunement (invisible until the ritual has been performed) ─────
        if (ClientAttunementData.isAttuned()) {
            graphics.text(font, Component.literal("Attunement"), textX, textY + dy, TomeConstants.Colors.TEXT, false);
            dy += lineH;
            String attunedName = elementLabel(ClientAttunementData.getAttunement());
            graphics.text(font, Component.literal("  " + attunedName), textX, textY + dy, TomeConstants.Colors.TEXT_MUTED, false);
        }

        // ── Traits detail button (only when revealed) ──────────────────────
        if (ClientManaData.isTraitsRevealed()) {
            TomeWidgets.drawButton(graphics, font, layout.nextButtonX(80), layout.footerY, 80, 20, "Traits →", true);
        }
    }

    private void drawTraitsDetail(GuiGraphicsExtractor graphics, int x, int y, int w, int scrollOffset) {
        int textX = x + 16;
        int lineH = 11;
        int innerW = w - 38; // leave room for scrollbar
        int footerY = layout.footerY;

        // ── Title (fixed, not scrolled) ─────────────────────────────────────
        graphics.text(font, Component.literal("Traits"), textX, y + TomeConstants.Layout.CONTENT_PADDING_Y, TomeConstants.Colors.TEXT, false);

        // ── Scroll region bounds ────────────────────────────────────────────
        int regionTop = y + 38;
        int regionBottom = footerY - 4;
        int regionH = regionBottom - regionTop;

        // ── Measure total content height (for max scroll + scrollbar) ───────
        int totalH = measureTraitsContentHeight();
        int maxScroll = Math.max(0, totalH - regionH);
        scrollOffset = Math.clamp(scrollOffset, 0, maxScroll);

        // ── Draw scrollbar ──────────────────────────────────────────────────
        if (totalH > regionH) {
            TomeWidgets.drawScrollbar(graphics, x + w - 8, regionTop, regionBottom, regionH, scrollOffset, totalH);
        }

        // ── Scissor and draw content ────────────────────────────────────────
        graphics.enableScissor(x, regionTop - 2, x + w - 10, regionBottom);

        var traits = readTraitsFromTome();
        int drawY = regionTop - scrollOffset;

        for (int i = 0; i < traits.size(); i++) {
            var trait = traits.get(i);

            // Name in rarity colour
            drawTraitName(graphics, trait, textX, drawY);
            drawY += lineH;

            // Pool
            String poolLabel = switch (trait.type()) {
                case BOOST -> "Positive";
                case PENALTY -> "Negative";
                case NEUTRAL -> "Neutral";
            };
            graphics.text(font, Component.literal("Pool: " + poolLabel), textX, drawY, TomeConstants.Colors.TEXT_MUTED, false);
            drawY += lineH;

            // Rarity
            String rarityLabel = trait.weight().name().charAt(0) + trait.weight().name().substring(1).toLowerCase();
            graphics.text(font, Component.literal("Rarity: " + rarityLabel), textX, drawY, TomeConstants.Colors.TEXT_MUTED, false);
            drawY += lineH;

            // Description (word-wrapped)
            drawY += TextRenderer.draw(graphics, font, trait.description(), textX, drawY, innerW, TomeConstants.Colors.TEXT_MUTED);

            // Separator between traits
            if (i < traits.size() - 1) {
                drawY += 3;
                graphics.fillGradient(textX, drawY, x + w - 16, drawY + 1, TomeConstants.Colors.BORDER, TomeConstants.Colors.BORDER);
                drawY += 6;
            }
        }

        graphics.disableScissor();

        // ── "← Identity" button ─────────────────────────────────────────────
        TomeWidgets.drawButton(graphics, font, layout.prevButtonX(), footerY, 90, 20, "← Identity", true);
    }

    public int measureTraitsContentHeight() {
        var traits = readTraitsFromTome();
        int innerW = layout.bookW - 38;
        int lineH = 11;
        int total = 0;
        for (int i = 0; i < traits.size(); i++) {
            total += lineH * 3; // name + pool + rarity
            total += TextRenderer.measureHeight(traits.get(i).description(), innerW, font);
            if (i < traits.size() - 1) total += 9; // separator gap
        }
        return total;
    }

    private List<TomeTraitEntry> readTraitsFromTome() {
        CompoundTag root = tomeStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        var traitTagOpt = root.getCompound("traits");
        if (traitTagOpt.isEmpty()) return List.of();
        CompoundTag traitTag = traitTagOpt.get();
        int count = traitTag.getInt("count").orElse(0);
        List<TomeTraitEntry> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            var entryOpt = traitTag.getCompound("trait_" + i);
            if (entryOpt.isEmpty()) continue;
            CompoundTag e = entryOpt.get();
            String name = e.getString("name").orElse("?");
            String desc = e.getString("description").orElse("");
            TraitType type = TraitType.valueOf(e.getString("type").orElse("NEUTRAL"));
            TraitWeight weight = TraitWeight.valueOf(e.getString("weight").orElse("COMMON"));
            result.add(new TomeTraitEntry(name, desc, type, weight));
        }
        return result;
    }

    private void drawTraitName(GuiGraphicsExtractor graphics, TomeTraitEntry trait, int x, int y) {
        String displayName = switch (trait.weight()) {
            case LEGENDARY -> "★ " + trait.name() + " ★";
            case EXOTIC -> "✦ " + trait.name() + " ✦";
            default -> trait.name();
        };
        int color = switch (trait.weight()) {
            case COMMON -> TomeConstants.Colors.TRAIT_COMMON;
            case UNCOMMON -> TomeConstants.Colors.TRAIT_UNCOMMON;
            case RARE -> TomeConstants.Colors.TRAIT_RARE;
            case LEGENDARY -> TomeConstants.Colors.TRAIT_LEGENDARY;
            case EXOTIC -> TomeConstants.Colors.TRAIT_EXOTIC;
        };
        var text = Component.literal(displayName);
        graphics.text(font, text, x - 1, y, TomeConstants.Colors.TRAIT_OUTLINE, false);
        graphics.text(font, text, x + 1, y, TomeConstants.Colors.TRAIT_OUTLINE, false);
        graphics.text(font, text, x, y - 1, TomeConstants.Colors.TRAIT_OUTLINE, false);
        graphics.text(font, text, x, y + 1, TomeConstants.Colors.TRAIT_OUTLINE, false);
        graphics.text(font, text, x, y, color, true);
    }

    private static String elementLabel(Element element) {
        if (element == null || element == SoulmarkElements.NONE.get()) return "None";
        if (element == SoulmarkElements.FIRE.get()) return "Fire";
        if (element == SoulmarkElements.WATER.get()) return "Water";
        if (element == SoulmarkElements.EARTH.get()) return "Earth";
        if (element == SoulmarkElements.AIR.get()) return "Air";
        if (element == SoulmarkElements.LIGHT.get()) return "Light";
        if (element == SoulmarkElements.DARK.get()) return "Dark";
        return "None";
    }
}
