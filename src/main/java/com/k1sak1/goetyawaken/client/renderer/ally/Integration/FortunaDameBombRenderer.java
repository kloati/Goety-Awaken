package com.k1sak1.goetyawaken.client.renderer.ally.Integration;

import com.k1sak1.goetyawaken.client.model.ally.Integration.FortunaDameBombModel;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.FortunaDameBomb;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import lykrast.meetyourfight.MeetYourFight;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

//Based on https://https://github.com/Lykrast/MeetYourFight, Original by lykrast
public class FortunaDameBombRenderer extends EntityRenderer<FortunaDameBomb> {
	private static final ResourceLocation TEXTURE = MeetYourFight.rl("textures/entity/fortuna_dice.png");
	private static final ResourceLocation TEXTURE_WARNING = MeetYourFight.rl("textures/entity/fortuna_dice_white.png");
	private final FortunaDameBombModel<FortunaDameBomb> model;

	public FortunaDameBombRenderer(Context context) {
		super(context);
		model = new FortunaDameBombModel<>(context.bakeLayer(FortunaDameBombModel.MODEL));
	}

	@Override
	protected int getBlockLightLevel(FortunaDameBomb entityIn, BlockPos partialTicks) {
		return 15;
	}

	@Override
	public void render(FortunaDameBomb entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn,
			MultiBufferSource bufferIn, int packedLightIn) {
		matrixStackIn.pushPose();
		float f = Mth.rotLerp(partialTicks, entityIn.yRotO, entityIn.getYRot());
		float f1 = Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot());
		model.setupAnim(entityIn, 0, 0, 0, f, f1);
		VertexConsumer ivertexbuilder = bufferIn.getBuffer(model.renderType(TEXTURE));
		matrixStackIn.translate(0, 0.15, 0);
		matrixStackIn.pushPose();

		int fuse = entityIn.getFuse();
		int overlay = OverlayTexture.NO_OVERLAY;
		if (fuse - partialTicks + 1 < 10) {
			float f2 = 1.0F - ((float) fuse - partialTicks + 1.0F) / 10.0F;
			f2 = Mth.clamp(f2, 0, 1);
			f2 *= f2;
			f2 *= f2;
			float scale = 1.0F + f2 * 2;
			matrixStackIn.scale(scale, scale, scale);
			if (fuse - partialTicks + 1 < 5)
				overlay = OverlayTexture.pack(OverlayTexture.u(1), 10);
		}

		float f2 = (float) entityIn.tickCount + partialTicks;
		matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.sin(f2 * 0.1F) * 180));
		matrixStackIn.mulPose(Axis.XP.rotationDegrees(Mth.cos(f2 * 0.1F) * 180));
		matrixStackIn.mulPose(Axis.ZP.rotationDegrees(Mth.sin(f2 * 0.15F) * 360));

		model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, overlay, 1, 1, 1, 1);
		matrixStackIn.popPose();

		if (fuse - partialTicks + 1 < 5) {
			VertexConsumer warningvertex = bufferIn.getBuffer(model.renderType(TEXTURE_WARNING));
			float scale = 1f - (fuse - partialTicks) / 5f;
			scale = Mth.clamp(scale, 0, 1);
			scale *= scale;
			scale *= scale;
			scale = (1 - scale) * 0.125f;
			model.setupAnim(entityIn, 0, 0, 0, 0, 0);

			int lineCount = entityIn.getLineCount();
			double angleStep = 360.0 / lineCount;
			for (int i = 0; i < lineCount; i++) {
				matrixStackIn.pushPose();
				matrixStackIn.mulPose(Axis.YP.rotationDegrees((float) (i * angleStep)));
				matrixStackIn.translate(4, 0, 0);
				matrixStackIn.scale(32, scale, scale);
				model.renderToBuffer(matrixStackIn, warningvertex, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1,
						1);
				matrixStackIn.popPose();
			}
		}
		matrixStackIn.popPose();

		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(FortunaDameBomb entity) {
		return TEXTURE;
	}

	@Override
	public boolean shouldRender(FortunaDameBomb entity, Frustum frustrum, double p_114171_, double p_114172_,
			double p_114173_) {

		return super.shouldRender(entity, frustrum, p_114171_, p_114172_, p_114173_) || entity.getFuse() < 7;
	}

}
