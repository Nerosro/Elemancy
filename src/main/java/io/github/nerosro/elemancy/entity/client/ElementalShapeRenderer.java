package io.github.nerosro.elemancy.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.nerosro.elemancy.Elemancy;
import io.github.nerosro.elemancy.entity.custom.ElementalShapeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * Created by Nerosro on 13/11/2023.
 */
public class ElementalShapeRenderer extends MobRenderer<ElementalShapeEntity, ElementalShapeModel<ElementalShapeEntity>> {
    public ElementalShapeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new ElementalShapeModel<>(pContext.bakeLayer(ModModelLayers.ELEMENTAL_SHAPE_LAYER)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(ElementalShapeEntity pEntity) {
        return new ResourceLocation(Elemancy.MOD_ID, "textures/entity/creatures/elemental_shape.png");
    }

    @Override
    public void render(ElementalShapeEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }
}
