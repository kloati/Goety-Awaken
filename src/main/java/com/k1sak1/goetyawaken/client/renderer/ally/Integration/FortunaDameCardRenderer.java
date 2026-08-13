package com.k1sak1.goetyawaken.client.renderer.ally.Integration;

import com.k1sak1.goetyawaken.client.model.ally.Integration.FortunaDameCardModel;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.FortunaDameCardEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lykrast.meetyourfight.MeetYourFight;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

//Based on https://https://github.com/Lykrast/MeetYourFight, Original by lykrast
public class FortunaDameCardRenderer extends EntityRenderer<FortunaDameCardEntity> {
	private static final ResourceLocation[] TEXTURES = {
			MeetYourFight.rl("textures/entity/fortuna_card_club.png"),
			MeetYourFight.rl("textures/entity/fortuna_card_heart.png"),
			MeetYourFight.rl("textures/entity/fortuna_card_diamond.png"),
			MeetYourFight.rl("textures/entity/fortuna_card_spade.png"),
			MeetYourFight.rl("textures/entity/fortuna_card_amogus.png")
	};
	private static final RenderType[] TEXTURES_OVERLAY = {
			RenderType.entityTranslucentEmissive(MeetYourFight.rl("textures/entity/fortuna_card_ask_club.png"), false),
			RenderType.entityTranslucentEmissive(MeetYourFight.rl("textures/entity/fortuna_card_ask_heart.png"), false),
			RenderType.entityTranslucentEmissive(MeetYourFight.rl("textures/entity/fortuna_card_ask_diamond.png"),
					false),
			RenderType.entityTranslucentEmissive(MeetYourFight.rl("textures/entity/fortuna_card_ask_spade.png"), false),
			RenderType.entityTranslucentEmissive(MeetYourFight.rl("textures/entity/fortuna_card_ask_amogus.png"), false)

	};
	private static final ResourceLocation TEXTURE_HIDDEN = MeetYourFight.rl("textures/entity/fortuna_card_hidden.png");
	private final FortunaDameCardModel model;

	public FortunaDameCardRenderer(Context context) {
		super(context);
		model = new FortunaDameCardModel(context.bakeLayer(FortunaDameCardModel.MODEL));
	}

	@Override
	protected int getBlockLightLevel(FortunaDameCardEntity entityIn, BlockPos partialTicks) {
		return 15;
	}

	@Override
	public void render(FortunaDameCardEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn,
			MultiBufferSource bufferIn, int packedLightIn) {
		int anim = entityIn.clientAnim;
		if (anim == FortunaDameCardEntity.ANIM_NOTHERE)
			return;
		matrixStackIn.pushPose();

		matrixStackIn.scale(-1.0F, -1.0F, 1.0F);
		float yaw = Mth.rotLerp(partialTicks, entityIn.yRotO, entityIn.getYRot());
		if (anim == FortunaDameCardEntity.ANIM_HIDE) {
			float progress = (FortunaDameCardEntity.ANIM_APPEAR_DUR - entityIn.animTimer + partialTicks)
					/ (float) FortunaDameCardEntity.ANIM_APPEAR_DUR;
			yaw = Mth.wrapDegrees(yaw + progress * 360);
		} else if (anim == FortunaDameCardEntity.ANIM_REVEAL) {
			float progress = (FortunaDameCardEntity.ANIM_REVEAL_DUR - entityIn.animTimer + partialTicks)
					/ (float) FortunaDameCardEntity.ANIM_REVEAL_DUR;
			yaw = Mth.wrapDegrees(yaw + progress * 360);
		} else if (anim == FortunaDameCardEntity.ANIM_HINT) {

			float progress = (FortunaDameCardEntity.ANIM_HINT_DUR - entityIn.animTimer + partialTicks)
					/ (float) FortunaDameCardEntity.ANIM_HINT_DUR;
			yaw = Mth.wrapDegrees(yaw + Mth.sin(progress * 3 * Mth.PI) * 30);
		}
		float pitch = Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot());
		matrixStackIn.translate(0, -1.5, 0);
		model.setupAnim(entityIn, 0, 0, 0, yaw, pitch);
		if (anim == FortunaDameCardEntity.ANIM_APPEAR) {
			float scale = (FortunaDameCardEntity.ANIM_APPEAR_DUR - entityIn.animTimer + partialTicks)
					/ (float) FortunaDameCardEntity.ANIM_APPEAR_DUR;
			if (scale > 1)
				scale = 1;
			scale *= scale;
			scale *= scale;
			matrixStackIn.scale(scale, 1, scale);
		}
		VertexConsumer ivertexbuilder = bufferIn.getBuffer(model.renderType(getTextureLocation(entityIn)));
		model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F,
				1.0F);
		if (entityIn.clientAnim == FortunaDameCardEntity.ANIM_IDLE_QUESTION) {
			ivertexbuilder = bufferIn.getBuffer(
					TEXTURES_OVERLAY[Mth.clamp(entityIn.getVariantQuestion(), 0, TEXTURES_OVERLAY.length - 1)]);
			model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F,
					1.0F, 1.0F);
		}
		matrixStackIn.popPose();
		super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
	}

	private static final int HIDE_HALF = FortunaDameCardEntity.ANIM_APPEAR_DUR / 2,
			REVEAL_HALF = FortunaDameCardEntity.ANIM_REVEAL_DUR / 2;

	@Override
	public ResourceLocation getTextureLocation(FortunaDameCardEntity entity) {
		int anim = entity.clientAnim;
		if (anim == FortunaDameCardEntity.ANIM_IDLE_HIDDEN || anim == FortunaDameCardEntity.ANIM_IDLE_QUESTION
				|| (anim == FortunaDameCardEntity.ANIM_HIDE && entity.animTimer <= HIDE_HALF)
				|| (anim == FortunaDameCardEntity.ANIM_REVEAL && entity.animTimer >= REVEAL_HALF))
			return TEXTURE_HIDDEN;
		return TEXTURES[Mth.clamp(entity.getVariant(), 0, TEXTURES.length - 1)];
	}

}
