package com.k1sak1.goetyawaken.client.renderer.ally.Integration;

import com.k1sak1.goetyawaken.client.model.ally.Integration.RosalyneServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.RosalyneServant;
import com.mojang.blaze3d.vertex.PoseStack;
import lykrast.meetyourfight.MeetYourFight;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RosalyneServantRenderer extends MobRenderer<RosalyneServant, RosalyneServantModel> {
	public static final ResourceLocation BASE = MeetYourFight.rl("textures/entity/rosalyne.png"),
			COFFIN = MeetYourFight.rl("textures/entity/rosalyne_coffin.png"),
			CRACKED = MeetYourFight.rl("textures/entity/rosalyne_cracked.png");

	public RosalyneServantRenderer(Context context) {
		super(context, new RosalyneServantModel(context.bakeLayer(RosalyneServantModel.MODEL)), 0.5F);
		addLayer(new RosalyneServantGlowLayer(this));
		addLayer(new RosalyneServantArmorLayer(this, context.getModelSet()));
	}

	@Override
	protected void setupRotations(RosalyneServant entity, PoseStack stack, float ageInTicks, float rotationYaw,
			float partialTicks) {
		int phase = entity.getPhase();
		if (phase == RosalyneServant.BREAKING_OUT || phase == RosalyneServant.MADDENING) {
			rotationYaw += (float) (Math.cos(entity.tickCount * 3.25) * Math.PI * 0.8);
		}
		super.setupRotations(entity, stack, ageInTicks, rotationYaw, partialTicks);
	}

	@Override
	public ResourceLocation getTextureLocation(RosalyneServant entity) {
		int phase = entity.getPhase();
		if (phase == RosalyneServant.ENCASED || phase == RosalyneServant.BREAKING_OUT)
			return COFFIN;
		else if (phase == RosalyneServant.PHASE_3)
			return CRACKED;
		else
			return BASE;
	}

}
