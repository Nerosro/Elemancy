package be.nerosro.elemancy;

import java.util.List;

import be.nerosro.elemancy.block.ElemancyBlocks;
import be.nerosro.elemancy.client.AffinityPaperTintSource;
import be.nerosro.elemancy.client.ManaBlastRenderer;
import be.nerosro.elemancy.client.ManaHudOverlay;
import be.nerosro.elemancy.client.RitualLightningRenderer;
import be.nerosro.elemancy.client.RitualSigilRenderer;
import be.nerosro.elemancy.client.tome.TomeScreen;
import be.nerosro.elemancy.entity.EntityTypes;
import be.nerosro.elemancy.items.ElemancyItems;
import be.nerosro.elemancy.network.ClientRitualCameraState;
import be.nerosro.elemancy.network.DarkBucketScrollPayload;
import be.nerosro.elemancy.network.RitualCameraLockState;
import be.nerosro.elemancy.particle.ElemancyParticles;
import be.nerosro.elemancy.particle.ManaSpiralParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@Mod(value = Elemancy.MOD_ID, dist = Dist.CLIENT)
public class ElemancyClient {
    public ElemancyClient(IEventBus modEventBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modEventBus.addListener(ElemancyClient::onRegisterItemTintSources);
        modEventBus.addListener(ElemancyClient::onRegisterBlockColors);
        modEventBus.addListener(ElemancyClient::onRegisterGuiLayers);
        modEventBus.addListener(ElemancyClient::onRegisterEntityRenderers);
        modEventBus.addListener(ElemancyClient::onRegisterParticleProviders);

        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(ElemancyClient::onPlayerLogout);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(ClientRitualCameraState::onClientTick);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(ElemancyClient::onMouseScroll);
    }

    private static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        TomeScreen.clearSessionState();
        RitualCameraLockState.setLocked(false);
        ClientRitualCameraState.restore();
    }

    private static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen != null || minecraft.player == null || !minecraft.player.isShiftKeyDown()
            || !minecraft.player.getMainHandItem().is(ElemancyItems.DARK_BUCKET.get())
            || event.getScrollDeltaY() == 0.0D) {
            return;
        }

        event.setCanceled(true);
        ClientPacketDistributor.sendToServer(new DarkBucketScrollPayload(event.getScrollDeltaY() < 0.0D));
    }

    private static void onRegisterItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(
            Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "affinity_paper"),
            AffinityPaperTintSource.MAP_CODEC
        );
    }

    /**
     * Ashen Leaves use a constant cyan tint.
     */
    private static void onRegisterBlockColors(RegisterColorHandlersEvent.BlockTintSources event) {
        event.register(
            List.of(BlockTintSources.constant(ElemancyColors.ASHEN_LEAVES.argb())),
            ElemancyBlocks.ASHEN_LEAVES.get()
        );
    }

    private static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(
            Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "mana_hud"),
            new ManaHudOverlay()
        );
    }

    private static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityTypes.MANA_BLAST.get(), ManaBlastRenderer::new);
        event.registerEntityRenderer(EntityTypes.RITUAL_LIGHTNING.get(), RitualLightningRenderer::new);
        event.registerEntityRenderer(EntityTypes.RITUAL_SIGIL.get(), RitualSigilRenderer::new);
    }

    private static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ElemancyParticles.MANA_SPIRAL.get(), ManaSpiralParticle.Provider::new);
    }
}
