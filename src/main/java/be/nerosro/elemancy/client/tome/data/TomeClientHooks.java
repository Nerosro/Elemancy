package be.nerosro.elemancy.client.tome.data;

import be.nerosro.elemancy.client.tome.TomeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

/**
 * Client-only helpers for opening Tome UI screens.
 */
public final class TomeClientHooks {
    private TomeClientHooks() {
    }

    public static void openTomeScreen(Player player, InteractionHand hand) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new TomeScreen(player, hand));
    }
}
