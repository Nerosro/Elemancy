package be.nerosro.elemancy.client;

import org.joml.Matrix4fc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import be.nerosro.elemancy.entity.RitualLightningEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.RandomSource;

/**
 * Renders vanilla-style forked lightning with an element-specific tint.
 */
public final class RitualLightningRenderer extends EntityRenderer<RitualLightningEntity, RitualLightningRenderState> {

    public RitualLightningRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public RitualLightningRenderState createRenderState() {
        return new RitualLightningRenderState();
    }

    @Override
    public void extractRenderState(RitualLightningEntity entity, RitualLightningRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.color = entity.getColor();
        state.seed = entity.getSeed();
        state.dark = entity.isDark();
    }

    @Override
    public void submit(RitualLightningRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        float red = (state.color >> 16 & 0xFF) / 255.0f;
        float green = (state.color >> 8 & 0xFF) / 255.0f;
        float blue = (state.color & 0xFF) / 255.0f;
        float[] xOffsets = new float[8];
        float[] zOffsets = new float[8];
        float xOffset = 0.0f;
        float zOffset = 0.0f;
        RandomSource random = RandomSource.createThreadLocalInstance(state.seed);

        for (int height = 7; height >= 0; height--) {
            xOffsets[height] = xOffset;
            zOffsets[height] = zOffset;
            xOffset += random.nextInt(11) - 5;
            zOffset += random.nextInt(11) - 5;
        }

        float finalXOffset = xOffset;
        float finalZOffset = zOffset;
        collector.submitCustomGeometry(poseStack, RenderTypes.lightning(), (pose, buffer) -> {
            Matrix4fc matrix = pose.pose();
            if (state.dark) {
                renderBolt(matrix, buffer, state.seed, xOffsets, zOffsets, finalXOffset, finalZOffset,
                    0x26 / 255.0f, 0x0B / 255.0f, 0x35 / 255.0f, 0.18f, 0.80f);
                renderBolt(matrix, buffer, state.seed, xOffsets, zOffsets, finalXOffset, finalZOffset,
                    0x8A / 255.0f, 0x3A / 255.0f, 0xA3 / 255.0f, 0.32f, 0.38f);
            } else {
                renderBolt(matrix, buffer, state.seed, xOffsets, zOffsets, finalXOffset, finalZOffset,
                    red, green, blue, 0.3f, 1.0f);
            }
        });

        super.submit(state, poseStack, collector, camera);
    }

    @Override
    protected boolean affectedByCulling(RitualLightningEntity entity) {
        return false;
    }

    private static void renderBolt(Matrix4fc matrix, VertexConsumer buffer, long seed, float[] xOffsets,
                                   float[] zOffsets, float finalXOffset, float finalZOffset, float red,
                                   float green, float blue, float opacity, float radiusScale) {
        for (int branch = 0; branch < 4; branch++) {
            RandomSource branchRandom = RandomSource.createThreadLocalInstance(seed);
            for (int fork = 0; fork < 3; fork++) {
                int startHeight = fork == 0 ? 7 : 7 - fork;
                int endHeight = fork == 0 ? 0 : startHeight - 2;
                float currentXOffset = xOffsets[startHeight] - finalXOffset;
                float currentZOffset = zOffsets[startHeight] - finalZOffset;

                for (int height = startHeight; height >= endHeight; height--) {
                    float previousXOffset = currentXOffset;
                    float previousZOffset = currentZOffset;
                    int spread = fork == 0 ? 5 : 15;
                    currentXOffset += branchRandom.nextInt(spread * 2 + 1) - spread;
                    currentZOffset += branchRandom.nextInt(spread * 2 + 1) - spread;

                    float lowerRadius = (0.1f + branch * 0.2f) * radiusScale;
                    if (fork == 0) lowerRadius *= height * 0.1f + 1.0f;
                    float upperRadius = (0.1f + branch * 0.2f) * radiusScale;
                    if (fork == 0) upperRadius *= (height - 1.0f) * 0.1f + 1.0f;

                    quad(matrix, buffer, currentXOffset, currentZOffset, height, previousXOffset, previousZOffset,
                        red, green, blue, opacity, lowerRadius, upperRadius, false, false, true, false);
                    quad(matrix, buffer, currentXOffset, currentZOffset, height, previousXOffset, previousZOffset,
                        red, green, blue, opacity, lowerRadius, upperRadius, true, false, true, true);
                    quad(matrix, buffer, currentXOffset, currentZOffset, height, previousXOffset, previousZOffset,
                        red, green, blue, opacity, lowerRadius, upperRadius, true, true, false, true);
                    quad(matrix, buffer, currentXOffset, currentZOffset, height, previousXOffset, previousZOffset,
                        red, green, blue, opacity, lowerRadius, upperRadius, false, true, false, false);
                }
            }
        }
    }

    private static void quad(Matrix4fc pose, VertexConsumer buffer, float currentXOffset, float currentZOffset,
                             int height, float previousXOffset, float previousZOffset, float red, float green,
                             float blue, float opacity, float lowerRadius, float upperRadius, boolean positiveXStart,
                             boolean positiveZStart, boolean positiveXEnd, boolean positiveZEnd) {
        buffer.addVertex(pose, currentXOffset + (positiveXStart ? upperRadius : -upperRadius), height * 16.0f,
                currentZOffset + (positiveZStart ? upperRadius : -upperRadius))
            .setColor(red, green, blue, opacity);
        buffer.addVertex(pose, previousXOffset + (positiveXStart ? lowerRadius : -lowerRadius),
                (height + 1) * 16.0f, previousZOffset + (positiveZStart ? lowerRadius : -lowerRadius))
            .setColor(red, green, blue, opacity);
        buffer.addVertex(pose, previousXOffset + (positiveXEnd ? lowerRadius : -lowerRadius),
                (height + 1) * 16.0f, previousZOffset + (positiveZEnd ? lowerRadius : -lowerRadius))
            .setColor(red, green, blue, opacity);
        buffer.addVertex(pose, currentXOffset + (positiveXEnd ? upperRadius : -upperRadius), height * 16.0f,
                currentZOffset + (positiveZEnd ? upperRadius : -upperRadius))
            .setColor(red, green, blue, opacity);
    }
}