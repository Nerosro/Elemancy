package be.nerosro.elemancy.client.mirror;

import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import be.nerosro.elemancy.block.MirrorBlock;
import be.nerosro.elemancy.block.MirrorBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.phys.Vec3;

/**
 * Renders the active Mirror's portal field and stylized inner-self silhouette.
 */
public final class MirrorReflectionRenderer implements BlockEntityRenderer<MirrorBlockEntity, MirrorReflectionState> {

    private static final float PORTAL_OFFSET = 0.002f;
    private static final float FIGURE_OFFSET = 0.006f;
    private static final float FIGURE_OVERLAY_OFFSET = 0.008f;
    private static final Identifier END_PORTAL_TEXTURE =
        Identifier.withDefaultNamespace("textures/entity/end_portal/end_portal.png");
    private static final int FULL_BRIGHT = 0xF000F0;

    public MirrorReflectionRenderer(BlockEntityRendererProvider.Context context) {
    }

    /**
     * Allocates the reusable render-state object populated before each frame.
     */
    @Override
    public MirrorReflectionState createRenderState() {
        return new MirrorReflectionState();
    }

    /**
     * Captures the local player's activation, skin, and client-local transition state for safe render submission.
     */
    @Override
    public void extractRenderState(MirrorBlockEntity mirror, MirrorReflectionState state, float partialTick,
                                   Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(mirror, state, partialTick, cameraPosition, crumblingOverlay);

        state.facing = mirror.getBlockState().getValue(MirrorBlock.FACING);
        AbstractClientPlayer player = Minecraft.getInstance().player;
        BlockPos firstTile = mirror.getBlockPos().relative(state.facing);
        BlockPos secondTile = firstTile.relative(state.facing);
        state.active = player != null && (player.blockPosition().equals(firstTile) || player.blockPosition().equals(secondTile));
        state.animationTime = mirror.getLevel() == null ? 0.0f : mirror.getLevel().getGameTime() + partialTick;
        var transition = mirror.updateTransition(state.active);
        state.transitionProgress = transition.progress();
        state.transitionStyle = transition.style();
        state.transitionEntering = transition.entering();
        state.flickerAppearance = transition.flickerAppearance();
        state.flickerWindow = transition.flickerWindow();
        if (state.transitionProgress > 0.0f && player != null) {
            PlayerSkin playerSkin = player.getSkin();
            state.skinTexture = playerSkin.body().texturePath();

            // Slim skins use three-pixel arms; default skins use four-pixel arms.
            PlayerModelType playerModelType = playerSkin.model();
            state.slimModel = playerModelType == PlayerModelType.SLIM; //Simplified if to check which player model type is being used
        } else {
            state.skinTexture = null;
            state.slimModel = false;
        }
    }

    /**
     * Submits the currently visible portal and flat skin layers to Minecraft's render graph.
     */
    @Override
    public void submit(MirrorReflectionState state, PoseStack poseStack, SubmitNodeCollector collector,
                       CameraRenderState camera) {
        MirrorSurfaceGeometry.Surface surface = MirrorSurfaceGeometry.front(state.facing);
        if (state.transitionProgress <= 0.0f) {
            return;
        }

        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(END_PORTAL_TEXTURE), (pose, buffer) ->
            emitPortal(buffer, surface, pose, state.transitionStyle, state.transitionProgress)
        );
        if (state.skinTexture != null) {
            FlickerAppearance appearance = rareAppearance(state);
            Identifier texture = appearance == null ? state.skinTexture : appearance.texture();
            collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(texture), (pose, buffer) -> {
                if (appearance == null) {
                    emitSkinFigure(buffer, surface, pose, state.animationTime, state.slimModel,
                        state.transitionStyle, state.transitionProgress);
                } else {
                    MirrorFigureRenderer.emitFlickerAppearance(buffer, surface, pose, appearance, state.animationTime);
                }
            });
            if (appearance == FlickerAppearance.ALLAY) {
                collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(texture), (pose, buffer) ->
                    MirrorFigureRenderer.emitAllayTranslucent(buffer, surface, pose, appearance, state.animationTime)
                );
            }
        }
    }

    /**
     * Returns the rare profile only for its single selected entry flicker window.
     */
    private static FlickerAppearance rareAppearance(MirrorReflectionState state) {
        if (state.transitionStyle != MirrorTransitionStyle.FLICKER || !state.transitionEntering
            || state.flickerAppearance == null || state.flickerWindow == null
            || !state.flickerWindow.contains(state.transitionProgress)) {
            return null;
        }
        return state.flickerAppearance;
    }

    /**
     * Selects the portal reveal geometry for the stored entry/exit transition style.
     */
    private static void emitPortal(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface, PoseStack.Pose pose,
                                   MirrorTransitionStyle style, float progress) {
        // PIXEL_DISSOLVE cannot use one TransitionBounds: it renders up to 48 small rectangles.
        // Dissolve emits many small cells, rather than one rectangular portal region.
        if (style == MirrorTransitionStyle.PIXEL_DISSOLVE) {
            emitDissolvePortal(buffer, surface, pose, progress);
            return;
        }

        // Flicker has intentional invisible windows before becoming stable. handled first to return during this window
        if (style == MirrorTransitionStyle.FLICKER && !isFlickerVisible(progress)) {
            return;
        }

        // Every remaining style renders one rectangular portal region. PIXEL_DISSOLVE is included
        TransitionBounds bounds = switch (style) {
            case CENTER_EXPANSION -> TransitionBounds.centered(progress);
            case TV_SCANLINE -> TransitionBounds.horizontalLine(progress);
            case RISE -> new TransitionBounds(0.0f, 0.0f, 1.0f, progress);
            case SCAN -> new TransitionBounds(0.0f, 1.0f - progress, 1.0f, 1.0f);
            case FLICKER, MATERIALIZE -> TransitionBounds.full();
            case PIXEL_DISSOLVE -> throw new IllegalStateException("Handled above");
        };
        emitPortalRect(buffer, surface, pose, bounds);
    }

    /**
     * Reveals the portal as a fixed 6-by-8 deterministic grid to cap transition geometry cost.
     */
    private static void emitDissolvePortal(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface, PoseStack.Pose pose,
                                           float progress) {
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 6; column++) {
                if (pixelThreshold(column, row) > progress) continue;
                float left = column / 6.0f;
                float bottom = row / 8.0f;
                emitPortalRect(buffer, surface, pose, new TransitionBounds(left, bottom, left + 1.0f / 6.0f,
                    bottom + 1.0f / 8.0f));
            }
        }
    }

    /**
     * Emits one portal-textured rectangular region in normalized panel coordinates.
     */
    private static void emitPortalRect(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface, PoseStack.Pose pose,
                                       TransitionBounds bounds) {
        Vector3f normal = surface.outwardNormal();
        addTexturedVertex(buffer, pose, surface.panelPoint(bounds.left, bounds.bottom, PORTAL_OFFSET), bounds.left,
            1.0f - bounds.bottom, normal);
        addTexturedVertex(buffer, pose, surface.panelPoint(bounds.right, bounds.bottom, PORTAL_OFFSET), bounds.right,
            1.0f - bounds.bottom, normal);
        addTexturedVertex(buffer, pose, surface.panelPoint(bounds.right, bounds.top, PORTAL_OFFSET), bounds.right,
            1.0f - bounds.top, normal);
        addTexturedVertex(buffer, pose, surface.panelPoint(bounds.left, bounds.top, PORTAL_OFFSET), bounds.left,
            1.0f - bounds.top, normal);
    }

    /**
     * Defines the two brief pre-stable visibility windows for the flicker transition.
     */
    private static boolean isFlickerVisible(float progress) {
        return progress >= 0.75f || FlickerWindow.isVisible(progress);
    }

    /**
     * Produces a repeatable reveal threshold for one pixel-dissolve cell.
     */
    private static float pixelThreshold(int column, int row) {
        int value = (column * 23 + row * 37 + column * row * 11) & 63;
        return (value + 1.0f) / 64.0f;
    }

    private record TransitionBounds(float left, float bottom, float right, float top) {
        /**
         * Covers the full reflective panel.
         */
        private static TransitionBounds full() {
            return new TransitionBounds(0.0f, 0.0f, 1.0f, 1.0f);
        }

        /**
         * Expands symmetrically from the panel centre as progress approaches one.
         */
        private static TransitionBounds centered(float progress) {
            float half = progress * 0.5f;
            return new TransitionBounds(0.5f - half, 0.5f - half, 0.5f + half, 0.5f + half);
        }

        /**
         * Keeps full panel width while expanding vertically from a centre scanline.
         */
        private static TransitionBounds horizontalLine(float progress) {
            float halfHeight = progress * 0.5f;
            return new TransitionBounds(0.0f, 0.5f - halfHeight, 1.0f, 0.5f + halfHeight);
        }
    }

    /**
     * Draws base and overlay skin regions with subtle idle motion, gated by the current transition style.
     */
    private static void emitSkinFigure(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface, PoseStack.Pose pose,
                                       float animationTime, boolean slimModel, MirrorTransitionStyle style,
                                       float transitionProgress) {
        float breath = (float) Math.sin(animationTime * 0.12f) * 0.008f;
        float headBreath = breath * 1.25f;
        float leftArmDrift = (float) Math.sin(animationTime * 0.12f + 0.8f) * 0.004f;
        float rightArmDrift = (float) Math.sin(animationTime * 0.12f + 2.0f) * 0.004f;

        boolean legsVisible = isFigurePartVisible(style, transitionProgress, FigurePart.LEGS);
        boolean torsoVisible = isFigurePartVisible(style, transitionProgress, FigurePart.TORSO);
        boolean armsVisible = isFigurePartVisible(style, transitionProgress, FigurePart.ARMS);
        boolean headVisible = isFigurePartVisible(style, transitionProgress, FigurePart.HEAD);

        if (headVisible) {
            skinPart(buffer, surface, pose, 0.275f, 0.68f + headBreath, 0.725f, 0.86f + headBreath,
                FIGURE_OFFSET, 8, 8, 16, 16);
            skinPart(buffer, surface, pose, 0.275f, 0.68f + headBreath, 0.725f, 0.86f + headBreath,
                FIGURE_OVERLAY_OFFSET, 40, 8, 48, 16);
        }
        if (torsoVisible) {
            skinPart(buffer, surface, pose, 0.275f, 0.41f + breath, 0.725f, 0.68f + breath,
                FIGURE_OFFSET, 20, 20, 28, 32);
            skinPart(buffer, surface, pose, 0.275f, 0.41f + breath, 0.725f, 0.68f + breath,
                FIGURE_OVERLAY_OFFSET, 20, 36, 28, 48);
        }
        if (armsVisible) {
            emitArms(buffer, surface, pose, slimModel, breath, leftArmDrift, rightArmDrift, FIGURE_OFFSET, false);
            emitArms(buffer, surface, pose, slimModel, breath, leftArmDrift, rightArmDrift, FIGURE_OVERLAY_OFFSET, true);
        }
        if (legsVisible) {
            skinPart(buffer, surface, pose, 0.275f, 0.14f + breath, 0.50f, 0.41f + breath,
                FIGURE_OFFSET, 4, 20, 8, 32);
            skinPart(buffer, surface, pose, 0.50f, 0.14f + breath, 0.725f, 0.41f + breath,
                FIGURE_OFFSET, 20, 52, 24, 64);
            skinPart(buffer, surface, pose, 0.275f, 0.14f + breath, 0.50f, 0.41f + breath,
                FIGURE_OVERLAY_OFFSET, 4, 36, 8, 48);
            skinPart(buffer, surface, pose, 0.50f, 0.14f + breath, 0.725f, 0.41f + breath,
                FIGURE_OVERLAY_OFFSET, 4, 52, 8, 64);
        }
    }

    /**
     * Draws both arm regions with default/slim widths and their matching skin UV layouts.
     */
    private static void emitArms(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface, PoseStack.Pose pose,
                                 boolean slimModel, float breath, float leftArmDrift, float rightArmDrift,
                                 float offset, boolean overlay) {
        int rightArmU1 = slimModel ? 47 : 48;
        float leftArmLeft = slimModel ? 0.10625f : 0.05f;
        float leftArmRight = 0.275f;
        float rightArmLeft = 0.725f;
        float rightArmRight = slimModel ? 0.89375f : 0.95f;
        int rightArmV = overlay ? 36 : 20;
        int leftArmU = overlay ? 52 : 36;
        int leftArmU1 = overlay ? (slimModel ? 55 : 56) : (slimModel ? 39 : 40);

        skinPart(buffer, surface, pose, leftArmLeft, 0.41f + breath + leftArmDrift,
            leftArmRight, 0.68f + breath + leftArmDrift, offset, 44, rightArmV, rightArmU1, rightArmV + 12);
        skinPart(buffer, surface, pose, rightArmLeft, 0.41f + breath + rightArmDrift,
            rightArmRight, 0.68f + breath + rightArmDrift, offset, leftArmU, 52, leftArmU1, 64);
    }

    /**
     * Determines whether a body region has crossed its reveal threshold for the selected style.
     */
    private static boolean isFigurePartVisible(MirrorTransitionStyle style, float progress, FigurePart part) {
        if (style == MirrorTransitionStyle.FLICKER) return isFlickerVisible(progress);
        float threshold = switch (style) {
            case MATERIALIZE, RISE -> part.threshold;
            case SCAN -> 1.0f - part.threshold;
            case CENTER_EXPANSION -> 0.55f;
            case TV_SCANLINE -> part.scanlineThreshold;
            case PIXEL_DISSOLVE -> 0.72f;
            case FLICKER -> throw new IllegalStateException("Handled above");
        };
        return progress >= threshold;
    }

    private enum FigurePart {
        LEGS(0.18f, 0.72f),
        TORSO(0.40f, 0.36f),
        ARMS(0.58f, 0.36f),
        HEAD(0.78f, 0.72f);

        private final float threshold;
        private final float scanlineThreshold;

        FigurePart(float threshold, float scanlineThreshold) {
            this.threshold = threshold;
            this.scanlineThreshold = scanlineThreshold;
        }
    }

    /**
     * Maps a rectangular skin UV region onto a flat rectangle inside the Mirror panel.
     */
    private static void skinPart(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface, PoseStack.Pose pose,
                                 float left, float bottom, float right, float top, float offset,
                                 int u0, int v0, int u1, int v1) {
        MirrorFigureRenderer.skinPart(buffer, surface, pose, left, bottom, right, top, offset, u0, v0, u1, v1);
    }

    /**
     * Emits a portal-textured vertex with full-bright lighting and the panel-facing normal.
     */
    private static void addTexturedVertex(VertexConsumer buffer, PoseStack.Pose pose, Vector3f position, float u,
                                          float v, Vector3f normal) {
        buffer.addVertex(pose, position)
            .setColor(1.0f, 1.0f, 1.0f, 1.0f)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(FULL_BRIGHT)
            .setNormal(pose, normal.x, normal.y, normal.z);
    }

}