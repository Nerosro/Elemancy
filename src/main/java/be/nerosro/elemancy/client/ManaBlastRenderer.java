package be.nerosro.elemancy.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import be.nerosro.elemancy.Elemancy;
import be.nerosro.elemancy.entity.ManaBlastProjectile;
import be.nerosro.elemancy.spell.data.SpellVisual;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

/**
 * Renders mana blast projectiles with different visuals per spell:
 * - PARTICLE_TRAIL: invisible (particles handled server-side)
 * - ROCK: tumbling textured quad (Pebble Shot)
 * - CRESCENT: spinning crescent quad at random angle (Gust Slash)
 */
public class ManaBlastRenderer extends EntityRenderer<ManaBlastProjectile, ManaBlastRenderState> {

    private static final Identifier PEBBLE_TEXTURE =
        Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "textures/entity/pebble_shot.png");
    private static final Identifier CRESCENT_TEXTURE =
        Identifier.fromNamespaceAndPath(Elemancy.MOD_ID, "textures/entity/gust_slash.png");

    private static final float PEBBLE_SIZE = 0.25f;
    private static final float CRESCENT_SIZE = 0.5f;

    public ManaBlastRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ManaBlastRenderState createRenderState() {
        return new ManaBlastRenderState();
    }

    @Override
    public void extractRenderState(ManaBlastProjectile entity, ManaBlastRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.visualType = entity.getVisualType();
        state.crescentAngle = entity.getCrescentAngle();
        state.yRot = entity.getYRot(partialTicks);
        state.xRot = entity.getXRot(partialTicks);
    }

    @Override
    public void submit(ManaBlastRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.visualType == SpellVisual.PROJECTILE_PARTICLE_TRAIL) {
            // Invisible — particles do the visual work
            return;
        }

        poseStack.pushPose();

        if (state.visualType == SpellVisual.PROJECTILE_ROCK) {
            // Billboard to face camera, tumble based on age
            poseStack.mulPose(camera.orientation);
            float tumble = state.ageInTicks * 15f;
            poseStack.mulPose(Axis.ZP.rotationDegrees(tumble));
            renderQuad(state, poseStack, collector, PEBBLE_TEXTURE, PEBBLE_SIZE);
        } else if (state.visualType == SpellVisual.PROJECTILE_CRESCENT) {
            // Align with travel direction (vanilla arrow convention)
            poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0f));
            poseStack.mulPose(Axis.ZP.rotationDegrees(state.xRot));
            // Spin around travel axis (local X). Random crescentAngle gives each
            // projectile a unique starting tilt — some vertical, some diagonal.
            float spin = state.crescentAngle + state.ageInTicks * 20f;
            poseStack.mulPose(Axis.XP.rotationDegrees(spin));
            renderQuad(state, poseStack, collector, CRESCENT_TEXTURE, CRESCENT_SIZE);
        }

        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }

    private void renderQuad(ManaBlastRenderState state, PoseStack poseStack, SubmitNodeCollector collector,
                            Identifier texture, float halfSize) {
        RenderType renderType = RenderTypes.entityCutout(texture);
        int light = state.lightCoords;
        collector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
            buffer.addVertex(pose, -halfSize, -halfSize, 0)
                .setColor(255, 255, 255, 255)
                .setUv(0f, 1f)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0f, 0f, 1f);
            buffer.addVertex(pose, halfSize, -halfSize, 0)
                .setColor(255, 255, 255, 255)
                .setUv(1f, 1f)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0f, 0f, 1f);
            buffer.addVertex(pose, halfSize, halfSize, 0)
                .setColor(255, 255, 255, 255)
                .setUv(1f, 0f)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0f, 0f, 1f);
            buffer.addVertex(pose, -halfSize, halfSize, 0)
                .setColor(255, 255, 255, 255)
                .setUv(0f, 0f)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0f, 0f, 1f);
        });
    }
}
