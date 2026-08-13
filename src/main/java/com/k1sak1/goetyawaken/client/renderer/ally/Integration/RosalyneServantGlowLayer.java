package com.k1sak1.goetyawaken.client.renderer.ally.Integration;

import com.k1sak1.goetyawaken.client.model.ally.Integration.RosalyneServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.RosalyneServant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lykrast.meetyourfight.MeetYourFight;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class RosalyneServantGlowLayer extends RenderLayer<RosalyneServant, RosalyneServantModel> {
	private static final RenderType COFFIN_GLOW = RenderType
			.entityTranslucentEmissive(MeetYourFight.rl("textures/entity/rosalyne_coffin_glow.png"), false),
			BASE_GLOW = RenderType.entityTranslucentEmissive(MeetYourFight.rl("textures/entity/rosalyne_glow.png"),
					false);

	public RosalyneServantGlowLayer(RenderLayerParent<RosalyneServant, RosalyneServantModel> parent) {
		super(parent);
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource buffer, int p_117351_, RosalyneServant entity,
			float p_117353_, float p_117354_, float p_117355_, float p_117356_, float p_117357_, float p_117358_) {
		int phase = entity.getPhase();

		VertexConsumer vertexconsumer = buffer.getBuffer(
				(phase == RosalyneServant.ENCASED || phase == RosalyneServant.BREAKING_OUT) ? COFFIN_GLOW : BASE_GLOW);
		getParentModel().renderToBuffer(poseStack, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
	}

}
