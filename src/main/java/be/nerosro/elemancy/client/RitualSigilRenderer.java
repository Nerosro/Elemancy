package be.nerosro.elemancy.client;

import org.joml.Matrix4fc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import be.nerosro.elemancy.entity.RitualSigilEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;

/**
 * Renders a clean outer pentagon and inner pentagram between ritual capstones.
 */
public final class RitualSigilRenderer extends EntityRenderer<RitualSigilEntity, RitualSigilRenderState> {

    private static final float LINE_HALF_WIDTH = 0.035f;
    private static final float OPACITY = 0.8f;

    public RitualSigilRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public RitualSigilRenderState createRenderState() {
        return new RitualSigilRenderState();
    }

    @Override
    public void extractRenderState(RitualSigilEntity entity, RitualSigilRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        Vec3 origin = entity.position();
        for (int index = 0; index < state.vertices.length; index++) {
            state.vertices[index] = Vec3.atCenterOf(entity.getVertex(index)).add(0.0, 0.5, 0.0).subtract(origin);
        }
        state.color = entity.getColor(partialTicks);
        state.drawProgress = entity.getDrawProgress(partialTicks);
        state.opacity = entity.getOpacity(partialTicks);
    }

    @Override
    public void submit(RitualSigilRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.opacity <= 0.0f || state.drawProgress <= 0.0f) return;

        float red = (state.color >> 16 & 0xFF) / 255.0f;
        float green = (state.color >> 8 & 0xFF) / 255.0f;
        float blue = (state.color & 0xFF) / 255.0f;
        collector.submitCustomGeometry(poseStack, RenderTypes.lightning(), (pose, buffer) -> {
            Matrix4fc matrix = pose.pose();
            for (int segment = 0; segment < 10; segment++) {
                float segmentProgress = Math.clamp(state.drawProgress * 10.0f - segment, 0.0f, 1.0f);
                if (segmentProgress <= 0.0f) continue;

                int startIndex = segment < 5 ? segment : segment - 5;
                int endIndex = segment < 5 ? (segment + 1) % 5 : (startIndex + 2) % 5;
                Vec3 start = state.vertices[startIndex];
                Vec3 end = start.lerp(state.vertices[endIndex], segmentProgress);
                renderRibbon(matrix, buffer, start, end, red, green, blue, OPACITY * state.opacity);
            }
        });
        super.submit(state, poseStack, collector, camera);
    }

    @Override
    protected boolean affectedByCulling(RitualSigilEntity entity) {
        return false;
    }

    private static void renderRibbon(Matrix4fc matrix, VertexConsumer buffer, Vec3 start, Vec3 end, float red,
                                     float green, float blue, float opacity) {
        Vec3 direction = end.subtract(start).normalize();
        Vec3 horizontalWidth = new Vec3(direction.z, 0.0, -direction.x);
        if (horizontalWidth.lengthSqr() < 0.0001) {
            horizontalWidth = new Vec3(1.0, 0.0, 0.0);
        } else {
            horizontalWidth = horizontalWidth.normalize();
        }
        Vec3 verticalWidth = direction.cross(horizontalWidth).normalize();
        renderQuad(matrix, buffer, start, end, horizontalWidth.scale(LINE_HALF_WIDTH), red, green, blue, opacity);
        renderQuad(matrix, buffer, start, end, verticalWidth.scale(LINE_HALF_WIDTH), red, green, blue, opacity);
    }

    private static void renderQuad(Matrix4fc matrix, VertexConsumer buffer, Vec3 start, Vec3 end, Vec3 width,
                                   float red, float green, float blue, float opacity) {
        addVertex(matrix, buffer, start.subtract(width), red, green, blue, opacity);
        addVertex(matrix, buffer, start.add(width), red, green, blue, opacity);
        addVertex(matrix, buffer, end.add(width), red, green, blue, opacity);
        addVertex(matrix, buffer, end.subtract(width), red, green, blue, opacity);
        addVertex(matrix, buffer, end.subtract(width), red, green, blue, opacity);
        addVertex(matrix, buffer, end.add(width), red, green, blue, opacity);
        addVertex(matrix, buffer, start.add(width), red, green, blue, opacity);
        addVertex(matrix, buffer, start.subtract(width), red, green, blue, opacity);
    }

    private static void addVertex(Matrix4fc matrix, VertexConsumer buffer, Vec3 position, float red, float green,
                                  float blue, float opacity) {
        buffer.addVertex(matrix, (float) position.x, (float) position.y, (float) position.z)
            .setColor(red, green, blue, opacity);
    }
}