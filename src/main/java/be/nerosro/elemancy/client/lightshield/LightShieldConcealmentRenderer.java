package be.nerosro.elemancy.client.lightshield;

import be.nerosro.elemancy.effects.ElemancyEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

public final class LightShieldConcealmentRenderer {

    private LightShieldConcealmentRenderer() {
    }

    public static void onRenderPlayer(RenderPlayerEvent.Pre<?> event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null
            && minecraft.level.getEntity(event.getRenderState().id) instanceof Player player
            && player.hasEffect(ElemancyEffects.LIGHT_SHIELD_CONCEALMENT)) {
            event.setCanceled(true);
        }
    }

    public static void onRenderHand(RenderHandEvent event) {
        if (Minecraft.getInstance().player != null
            && Minecraft.getInstance().player.hasEffect(ElemancyEffects.LIGHT_SHIELD_CONCEALMENT)) {
            event.setCanceled(true);
        }
    }
}