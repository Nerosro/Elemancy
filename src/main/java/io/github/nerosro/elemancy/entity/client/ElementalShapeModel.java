package io.github.nerosro.elemancy.entity.client;// Made with Blockbench 4.8.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class ElementalShapeModel<T extends Entity> extends HierarchicalModel<T> {
	private final ModelPart elemental_shape;

	public ElementalShapeModel(ModelPart root) {
		this.elemental_shape = root.getChild("elemental_shape");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition elemental_shape = partdefinition.addOrReplaceChild("elemental_shape", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = elemental_shape.addOrReplaceChild("body", CubeListBuilder.create().texOffs(3, 1).addBox(-2.0F, -12.0F, -1.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(1, 3).addBox(-1.6F, -11.0F, -1.0F, 0.2F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(1, 1).addBox(-2.0F, -9.0F, -1.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_bottom_r1 = body.addOrReplaceChild("cube_bottom_r1", CubeListBuilder.create().texOffs(5, 1).addBox(-0.625F, -0.575F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.8742F, -6.8361F, -1.0F, 0.0F, 0.0F, 0.48F));

		PartDefinition line_middle_r1 = body.addOrReplaceChild("line_middle_r1", CubeListBuilder.create().texOffs(1, 2).addBox(-1.4142F, -0.2F, 0.0F, 2.8F, 0.2F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1142F, -7.7582F, -1.0F, 0.0F, 0.0F, 0.48F));

		return LayerDefinition.create(meshdefinition, 16, 16);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		elemental_shape.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return elemental_shape;
	}
}