package com.k1sak1.goetyawaken.client.model.ally.Integration;

import com.k1sak1.goetyawaken.common.entities.ally.Integration.FortunaDameCardEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lykrast.meetyourfight.MeetYourFight;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

//Based on https://https://github.com/Lykrast/MeetYourFight, Original by lykrast
public class FortunaDameCardModel extends EntityModel<FortunaDameCardEntity> {
	// Made with Blockbench 4.1.5 but like I manually imported the stuff
	public static final ModelLayerLocation MODEL = new ModelLayerLocation(MeetYourFight.rl("fortuna_card"), "main");
	private final ModelPart renderer;

	public FortunaDameCardModel(ModelPart modelPart) {
		super(RenderType::entityCutoutNoCull);
		renderer = modelPart.getChild("main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		partdefinition.addOrReplaceChild("main",
				CubeListBuilder.create().texOffs(0, 0).addBox(-14, -40, -0.5f, 28, 40, 1), PartPose.offset(0, 24, 0));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha) {
		renderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	@Override
	public void setupAnim(FortunaDameCardEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) {

		renderer.yRot = Mth.wrapDegrees(180 + netHeadYaw) * Mth.DEG_TO_RAD;
		renderer.xRot = headPitch * Mth.DEG_TO_RAD;
	}

}
