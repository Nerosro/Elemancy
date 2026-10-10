package be.nerosro.elemancy.client.mirror;

import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.texture.OverlayTexture;

/**
 * Renders flat textured body regions inside the Mirror's reflective panel.
 */
public final class MirrorFigureRenderer {

    private static final int FULL_BRIGHT = 0xF000F0;
    private static final float SKIN_SIZE = 64.0f;
    private static final float BASE_OFFSET = 0.006f;
    private static final float DETAIL_OFFSET = 0.008f;
    private static final float DETAIL_LAYER_OFFSET = 0.001f;

    private static final float MIRROR_PANEL_WIDTH = 12.0f;
    private static final float MIRROR_PANEL_HEIGHT = 30.0f;
    private static final float MIRROR_FLOOR = 1.0f / MIRROR_PANEL_HEIGHT;

    private static final float ALLAY_MODEL_PIXEL_WIDTH = 0.45f / 8.0f;
    private static final float ALLAY_MODEL_PIXEL_HEIGHT = 0.72f / 32.0f;
    private static final float ALLAY_BOTTOM = 0.40f;
    private static final float ALLAY_HEIGHT = 11.0f;
    private static final float ALLAY_ARM_OFFSET = DETAIL_OFFSET + DETAIL_LAYER_OFFSET * 6.0f;

    private MirrorFigureRenderer() {
    }

    /**
     * Maps a rectangular skin UV region onto a flat rectangle inside the Mirror panel.
     */
    public static void skinPart(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface, PoseStack.Pose pose,
                                float left, float bottom, float right, float top, float offset,
                                int u0, int v0, int u1, int v1) {
        texturePart(buffer, surface, pose, left, bottom, right, top, offset, u0, v0, u1, v1,
            SKIN_SIZE, SKIN_SIZE, false);
    }

    /**
     * Draws the selected rare flicker profile as flat body regions inside the Mirror panel.
     */
    public static void emitFlickerAppearance(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface,
                                             PoseStack.Pose pose, FlickerAppearance appearance, float animationTime) {
        float bob = appearance == FlickerAppearance.ALLAY ? (float) Math.sin(animationTime * 0.18f) * 0.018f : 0.0f;
        switch (appearance.profile()) {
            case ALLAY -> emitAllay(buffer, surface, pose, appearance, bob);
            case CHICKEN -> emitChicken(buffer, surface, pose, appearance, bob);
            case CREEPER -> emitCreeper(buffer, surface, pose, appearance, bob);
            case HUMANOID -> emitHumanoid(buffer, surface, pose, appearance, bob);
        }
    }

    private static void drawPart(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface, PoseStack.Pose pose,
                                 FlickerAppearance appearance, float left, float bottom, float right, float top,
                                 int u0, int v0, int u1, int v1) {
        texturePart(buffer, surface, pose, left, bottom, right, top, BASE_OFFSET, u0, v0, u1, v1,
            appearance.textureWidth(), appearance.textureHeight(), true);
    }

    private static void texturePart(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface, PoseStack.Pose pose,
                                    float left, float bottom, float right, float top, float offset,
                                    int u0, int v0, int u1, int v1, float textureWidth, float textureHeight,
                                    boolean flipHorizontally) {
        Vector3f normal = surface.outwardNormal();
        float leftU = (flipHorizontally ? u1 : u0) / textureWidth;
        float rightU = (flipHorizontally ? u0 : u1) / textureWidth;
        addSkinVertex(buffer, pose, surface.panelPoint(left, bottom, offset), leftU, v1 / textureHeight, normal);
        addSkinVertex(buffer, pose, surface.panelPoint(right, bottom, offset), rightU, v1 / textureHeight, normal);
        addSkinVertex(buffer, pose, surface.panelPoint(right, top, offset), rightU, v0 / textureHeight, normal);
        addSkinVertex(buffer, pose, surface.panelPoint(left, top, offset), leftU, v0 / textureHeight, normal);
    }

    private static void addSkinVertex(VertexConsumer buffer, PoseStack.Pose pose, Vector3f position, float u,
                                      float v, Vector3f normal) {
        buffer.addVertex(pose, position)
            .setColor(1.0f, 1.0f, 1.0f, 1.0f)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(FULL_BRIGHT)
            .setNormal(pose, normal.x, normal.y, normal.z);
    }

    private static void emitHumanoid(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface, PoseStack.Pose pose,
                                     FlickerAppearance appearance, float bob) {
        drawPart(buffer, surface, pose, appearance, 0.275f, 0.68f + bob, 0.725f, 0.86f + bob, 8, 8, 16, 16);
        drawPart(buffer, surface, pose, appearance, 0.275f, 0.41f + bob, 0.725f, 0.68f + bob, 20, 20, 28, 32);
        drawPart(buffer, surface, pose, appearance, 0.05f, 0.41f + bob, 0.275f, 0.68f + bob, 48, 20, 52, 32);
        drawPart(buffer, surface, pose, appearance, 0.725f, 0.41f + bob, 0.95f, 0.68f + bob, 48, 20, 52, 32);
        drawPart(buffer, surface, pose, appearance, 0.275f, 0.14f + bob, 0.50f, 0.41f + bob, 4, 20, 8, 32);
        drawPart(buffer, surface, pose, appearance, 0.50f, 0.14f + bob, 0.725f, 0.41f + bob, 4, 20, 8, 32);
    }

    private static void emitCreeper(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface, PoseStack.Pose pose,
                                    FlickerAppearance appearance, float bob) {
        drawPart(buffer, surface, pose, appearance, 0.225f, 0.64f + bob, 0.775f, 0.85f + bob, 8, 8, 16, 16);
        drawPart(buffer, surface, pose, appearance, 0.225f, 0.32f + bob, 0.775f, 0.64f + bob, 20, 20, 28, 32);
        drawPart(buffer, surface, pose, appearance, 0.225f, 0.16f + bob, 0.3625f, 0.32f + bob, 4, 20, 8, 26);
        drawPart(buffer, surface, pose, appearance, 0.3625f, 0.16f + bob, 0.50f, 0.32f + bob, 4, 20, 8, 26);
        drawPart(buffer, surface, pose, appearance, 0.50f, 0.16f + bob, 0.6375f, 0.32f + bob, 4, 20, 8, 26);
        drawPart(buffer, surface, pose, appearance, 0.6375f, 0.16f + bob, 0.775f, 0.32f + bob, 4, 20, 8, 26);
    }

    private static void emitChicken(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface, PoseStack.Pose pose,
                                    FlickerAppearance appearance, float bob) {
        chickenPart(buffer, surface, pose, appearance, -2.0f, 9.0f + bob, 2.0f, 15.0f + bob, 3, 3, 7, 9);
        chickenDetailPart(buffer, surface, pose, appearance, -2.0f, 11.0f + bob, 2.0f, 13.0f + bob,
            DETAIL_OFFSET, 16, 2, 20, 4);
        chickenDetailPart(buffer, surface, pose, appearance, -1.0f, 9.0f + bob, 1.0f, 11.0f + bob,
            DETAIL_OFFSET + DETAIL_LAYER_OFFSET, 16, 6, 18, 8);
        chickenPart(buffer, surface, pose, appearance, -3.0f, 5.0f + bob, 3.0f, 11.0f + bob, 6, 15, 12, 23);
        chickenPart(buffer, surface, pose, appearance, -4.0f, 7.0f + bob, -3.0f, 11.0f + bob, 30, 19, 31, 23);
        chickenPart(buffer, surface, pose, appearance, 3.0f, 7.0f + bob, 4.0f, 11.0f + bob, 30, 19, 31, 23);
        chickenPart(buffer, surface, pose, appearance, -2.0f, 1.0f + bob, -1.0f, 5.0f + bob, 36, 3, 37, 8);
        chickenPart(buffer, surface, pose, appearance, 1.0f, 1.0f + bob, 2.0f, 5.0f + bob, 36, 3, 37, 8);
        chickenPart(buffer, surface, pose, appearance, -3.0f, bob, 0.0f, 1.0f + bob, 32, 0, 35, 3);
        chickenPart(buffer, surface, pose, appearance, 0.0f, bob, 3.0f, 1.0f + bob, 32, 0, 35, 3);
    }

    private static void chickenPart(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface, PoseStack.Pose pose,
                                    FlickerAppearance appearance, float left, float bottom, float right, float top,
                                    int u0, int v0, int u1, int v1) {
        drawPart(buffer, surface, pose, appearance, chickenHorizontal(left), chickenVertical(bottom),
            chickenHorizontal(right), chickenVertical(top), u0, v0, u1, v1);
    }

    private static void chickenDetailPart(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface, PoseStack.Pose pose,
                                          FlickerAppearance appearance, float left, float bottom, float right, float top,
                                          float offset, int u0, int v0, int u1, int v1) {
        chickenFaceDetailPart(buffer, surface, pose, appearance, chickenHorizontal(left), chickenVertical(bottom),
            chickenHorizontal(right), chickenVertical(top), offset, u0, v0, u1, v1);
    }

    private static void chickenFaceDetailPart(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface, PoseStack.Pose pose,
                                              FlickerAppearance appearance, float left, float bottom, float right, float top,
                                              float offset, int u0, int v0, int u1, int v1) {
        texturePart(buffer, surface, pose, left, bottom, right, top, offset, u0, v0, u1, v1,
            appearance.textureWidth(), appearance.textureHeight(), true);
    }

    private static float chickenHorizontal(float position) {
        return 0.5f + position / MIRROR_PANEL_WIDTH;
    }

    private static float chickenVertical(float position) {
        return MIRROR_FLOOR + position / MIRROR_PANEL_HEIGHT;
    }

    private static void emitAllay(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface, PoseStack.Pose pose,
                                  FlickerAppearance appearance, float bob) {
        allayPart(buffer, surface, pose, appearance, -2.5f, 5.0f, -1.5f, 9.0f, bob,
            DETAIL_OFFSET, 0, 12, 2, 16);
        allayPart(buffer, surface, pose, appearance, 1.5f, 5.0f, 2.5f, 9.0f, bob,
            DETAIL_OFFSET, 5, 12, 7, 16);
        allayPart(buffer, surface, pose, appearance, -1.5f, 5.0f, 1.5f, 9.0f, bob,
            DETAIL_OFFSET + DETAIL_LAYER_OFFSET, 2, 12, 5, 16);
        emitAllayArm(buffer, surface, pose, appearance, -2.5f, -0.25f, 2, bob);
        emitAllayArm(buffer, surface, pose, appearance, 1.5f, 0.25f, 8, bob);
        allayPart(buffer, surface, pose, appearance, -2.5f, 0.0f, 2.5f, 5.0f, bob,
            DETAIL_OFFSET + DETAIL_LAYER_OFFSET * 4.0f, 5, 5, 10, 10);
    }

    /**
     * Draws Allay regions that require alpha blending over its opaque base model.
     */
    public static void emitAllayTranslucent(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface,
                                            PoseStack.Pose pose, FlickerAppearance appearance, float animationTime) {
        float bob = (float) Math.sin(animationTime * 0.18f) * 0.018f;
        emitAllayWing(buffer, surface, pose, appearance, -1.5f, -6.5f, bob, false);
        emitAllayWing(buffer, surface, pose, appearance, 1.5f, 6.5f, bob, true);
        emitAllayVeil(buffer, surface, pose, appearance, bob);
    }

    private static void emitAllayVeil(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface, PoseStack.Pose pose,
                                      FlickerAppearance appearance, float bob) {
        allayPart(buffer, surface, pose, appearance, -2.3f, 5.2f, -1.3f, 9.8f, bob,
            DETAIL_OFFSET + DETAIL_LAYER_OFFSET * 2.0f, 0, 18, 2, 23);
        allayPart(buffer, surface, pose, appearance, 1.3f, 5.2f, 2.3f, 9.8f, bob,
            DETAIL_OFFSET + DETAIL_LAYER_OFFSET * 2.0f, 5, 18, 7, 23);
        allayPart(buffer, surface, pose, appearance, -1.3f, 5.2f, 1.3f, 9.8f, bob,
            DETAIL_OFFSET + DETAIL_LAYER_OFFSET * 3.0f, 2, 18, 5, 23);
    }

    private static void allayPart(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface, PoseStack.Pose pose,
                                  FlickerAppearance appearance, float left, float top, float right, float bottom,
                                  float bob, float offset, int u0, int v0, int u1, int v1) {
        texturePart(buffer, surface, pose, allayHorizontal(left), allayVertical(bottom, bob), allayHorizontal(right),
            allayVertical(top, bob), offset, u0, v0, u1, v1, appearance.textureWidth(), appearance.textureHeight(),
            true);
    }

    private static void emitAllayArm(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface, PoseStack.Pose pose,
                                     FlickerAppearance appearance, float left, float horizontalStep, int textureTop,
                                     float bob) {
        for (int segment = 0; segment < 4; segment++) {
            float segmentLeft = left + horizontalStep * segment;
            allayPart(buffer, surface, pose, appearance, segmentLeft, 5.0f + segment, segmentLeft + 1.0f,
                6.0f + segment, bob, ALLAY_ARM_OFFSET, 25, textureTop + segment,
                26, textureTop + segment + 1);
        }
    }

    private static void emitAllayWing(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface, PoseStack.Pose pose,
                                      FlickerAppearance appearance, float innerX, float outerX, float bob,
                                      boolean flipHorizontally) {
        boolean outerOnRight = outerX > innerX;
        AllayPoint bottomLeft = outerOnRight ? new AllayPoint(innerX, 10.0f) : new AllayPoint(outerX, 8.0f);
        AllayPoint bottomRight = outerOnRight ? new AllayPoint(outerX, 8.0f) : new AllayPoint(innerX, 10.0f);
        AllayPoint topRight = outerOnRight ? new AllayPoint(outerX, 3.0f) : new AllayPoint(innerX, 5.0f);
        AllayPoint topLeft = outerOnRight ? new AllayPoint(innerX, 5.0f) : new AllayPoint(outerX, 3.0f);
        allayQuad(buffer, surface, pose, appearance, bottomLeft, bottomRight, topRight, topLeft, bob, BASE_OFFSET,
            16, 22, 24, 27, flipHorizontally);
    }

    private static void allayQuad(VertexConsumer buffer, MirrorSurfaceGeometry.Surface surface, PoseStack.Pose pose,
                                  FlickerAppearance appearance, AllayPoint bottomLeft, AllayPoint bottomRight,
                                  AllayPoint topRight, AllayPoint topLeft, float bob, float offset,
                                  int u0, int v0, int u1, int v1, boolean flipHorizontally) {
        Vector3f normal = surface.outwardNormal();
        float leftU = (flipHorizontally ? u1 : u0) / (float) appearance.textureWidth();
        float rightU = (flipHorizontally ? u0 : u1) / (float) appearance.textureWidth();
        addSkinVertex(buffer, pose, allayPanelPoint(surface, bottomLeft, bob, offset), leftU,
            v1 / (float) appearance.textureHeight(), normal);
        addSkinVertex(buffer, pose, allayPanelPoint(surface, bottomRight, bob, offset), rightU,
            v1 / (float) appearance.textureHeight(), normal);
        addSkinVertex(buffer, pose, allayPanelPoint(surface, topRight, bob, offset), rightU,
            v0 / (float) appearance.textureHeight(), normal);
        addSkinVertex(buffer, pose, allayPanelPoint(surface, topLeft, bob, offset), leftU,
            v0 / (float) appearance.textureHeight(), normal);
    }

    private static Vector3f allayPanelPoint(MirrorSurfaceGeometry.Surface surface, AllayPoint point, float bob,
                                            float offset) {
        return surface.panelPoint(allayHorizontal(point.x()), allayVertical(point.y(), bob), offset);
    }

    private static float allayHorizontal(float position) {
        return 0.5f + position * ALLAY_MODEL_PIXEL_WIDTH;
    }

    private static float allayVertical(float position, float bob) {
        return ALLAY_BOTTOM + (ALLAY_HEIGHT - position) * ALLAY_MODEL_PIXEL_HEIGHT + bob;
    }

    private record AllayPoint(float x, float y) {
    }
}