package be.nerosro.elemancy.client;

import com.mojang.blaze3d.vertex.PoseStack;

import be.nerosro.elemancy.entity.LightShieldDecoyEntity;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.player.PlayerModelType;

/**
 * Renders a Light Shield decoy as a frozen snapshot of its owner at creation time.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class LightShieldDecoyRenderer extends EntityRenderer<LightShieldDecoyEntity, LightShieldDecoyRenderState> {

    private final AvatarRenderer wideRenderer;
    private final AvatarRenderer slimRenderer;

    public LightShieldDecoyRenderer(EntityRendererProvider.Context context) {
        super(context);
        wideRenderer = new AvatarRenderer<>(context, false);
        slimRenderer = new AvatarRenderer<>(context, true);
    }

    @Override
    public LightShieldDecoyRenderState createRenderState() {
        return new LightShieldDecoyRenderState();
    }

    @Override
    public void extractRenderState(LightShieldDecoyEntity entity, LightShieldDecoyRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        ClientAvatarEntity avatar = (ClientAvatarEntity) (Object) entity;
        AvatarRenderer renderer = avatar.getSkin().model() == PlayerModelType.SLIM ? slimRenderer : wideRenderer;
        state.renderer = renderer;
        renderer.extractRenderState(entity, state, partialTicks);
    }

    @Override
    public void submit(LightShieldDecoyRenderState state, PoseStack poseStack, SubmitNodeCollector collector,
                       CameraRenderState camera) {
        if (state.renderer != null) {
            state.renderer.submit(state, poseStack, collector, camera);
        }
    }
}
