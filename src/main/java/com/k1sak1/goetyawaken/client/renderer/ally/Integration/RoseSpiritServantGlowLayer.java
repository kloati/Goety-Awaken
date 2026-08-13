package com.k1sak1.goetyawaken.client.renderer.ally.Integration;

import com.k1sak1.goetyawaken.client.model.ally.Integration.RoseSpiritServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.RoseSpiritServant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import lykrast.meetyourfight.MeetYourFight;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class RoseSpiritServantGlowLayer extends RenderLayer<RoseSpiritServant, RoseSpiritServantModel> {
	private static final RenderType NEUTRAL = RenderType
			.entityTranslucentEmissive(MeetYourFight.rl("textures/entity/rose_spirit_neutral.png"), false),
			SHOOTING = RenderType
					.entityTranslucentEmissive(MeetYourFight.rl("textures/entity/rose_spirit_shooting.png"), false),
			HURT = RenderType.entityTranslucentEmissive(MeetYourFight.rl("textures/entity/rose_spirit_hurt.png"),
					false);

	public RoseSpiritServantGlowLayer(RenderLayerParent<RoseSpiritServant, RoseSpiritServantModel> parent) {
		super(parent);
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int p_117351_, RoseSpiritServant entity,
			float p_117353_, float p_117354_, float p_117355_, float p_117356_, float p_117357_, float p_117358_) {
		int status = entity.getStatus();
		RenderType texture = NEUTRAL;
		if (status == RoseSpiritServant.ATTACKING)
			texture = SHOOTING;
		else if (status == RoseSpiritServant.HURT || status == RoseSpiritServant.RETRACTING_HURT)
			texture = HURT;
		VertexConsumer vertexconsumer = buffer.getBuffer(texture);
		getParentModel().renderToBuffer(poseStack, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
	}

}
