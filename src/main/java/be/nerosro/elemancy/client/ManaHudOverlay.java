package be.nerosro.elemancy.client;

import be.nerosro.elemancy.ElemancyColors;
import be.nerosro.elemancy.ElemancyTags;
import be.nerosro.elemancy.mana.depth.DepthTier;
import be.nerosro.elemancy.network.ClientScarData;
import be.nerosro.soulmark.network.ClientManaData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.GuiLayer;

/**
 * HUD overlay that displays current/max mana while the player holds a wand in the offhand.
 * Color shifts based on depth tier when mana goes negative.
 */
public class ManaHudOverlay implements GuiLayer {

    @Override
    public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        // Only show when holding a wand in the offhand.
        ItemStack offhand = player.getOffhandItem();
        if (!offhand.is(ElemancyTags.WANDS)) return;

        float current = ClientManaData.getCurrentMana();
        float max = ClientManaData.getMaxPool();
        if (max <= 0f) return; // Not yet synced

        boolean manaCollapse = ClientScarData.hasManaCollapse();
        int manaColor = manaCollapse ? ElemancyColors.HUD_MANA_COLLAPSE.argb() : getManaColor(current);

        String manaText = String.format("Mana: %.1f / %.1f", current, max);
        Component manaComponent = manaCollapse
            ? Component.literal(manaText).withStyle(Style.EMPTY.withObfuscated(true))
            : Component.literal(manaText);

        // Position: top-left area, below the hotbar level
        int x = 4;
        int y = graphics.guiHeight() - 18;

        // Draw mana prefix and max separately so max can be colored differently
        graphics.text(mc.font, manaComponent, x, y, manaColor, true);
    }

    private static int getManaColor(float currentMana) {
        return switch (DepthTier.fromMana(currentMana)) {
            case NONE -> ElemancyColors.MANA.argb();
            case DEPTH_1 -> ElemancyColors.HUD_MANA_DEPTH1.argb();
            case DEPTH_2 -> ElemancyColors.HUD_MANA_DEPTH2.argb();
            case DEPTH_3 -> ElemancyColors.HUD_MANA_DEPTH3.argb();
            case DEPTH_4 -> ElemancyColors.HUD_MANA_DEPTH4.argb();
        };
    }
}


