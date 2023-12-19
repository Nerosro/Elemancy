package io.github.nerosro.elemancy.client;

import io.github.nerosro.elemancy.item.custom.WandItem;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;


/**
 * Created by Nerosro on 18/07/2023.
 */
public class ManaHudOverlay {
    public static IGuiOverlay HUD_MANA = (gui, guiGraphics, partialTicks, width, height) -> {
        var pMinecraft = Minecraft.getInstance();
        var x = 10;
        var y = height-30;


        if(pMinecraft.player.getOffhandItem().getItem() instanceof WandItem){

            //TODO Make it work server side as well >_>
            var mana = ClientManaData.getCurrentMana() + "/" + ClientManaData.getPlayerMana();
            var regen = "Regen: " + ClientManaData.getPlayerRegen();
            var element = "Element: " + ClientManaData.getPlayerElement();

            if (x >= 0 && y >= 0) {
                guiGraphics.drawString(pMinecraft.font.self(), mana, x,y, 0xffffff);
                guiGraphics.drawString(pMinecraft.font.self(), regen, x,y+10, 0xffffff);
                guiGraphics.drawString(pMinecraft.font.self(), element, x,y+20, 0xffffff);
            }
        }
    };
}
