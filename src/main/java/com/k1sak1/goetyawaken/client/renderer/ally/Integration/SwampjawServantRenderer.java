package com.k1sak1.goetyawaken.client.renderer.ally.Integration;

import com.k1sak1.goetyawaken.client.model.ally.Integration.SwampjawServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.SwampjawServant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import lykrast.meetyourfight.MeetYourFight;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

//Based on https://https://github.com/Lykrast/MeetYourFight, Original by lykrast
public class SwampjawServantRenderer extends MobRenderer<SwampjawServant, SwampjawServantModel> {
	public static final ResourceLocation TEXTURE = MeetYourFight.rl("textures/entity/swampjaw.png");

	public SwampjawServantRenderer(Context context) {
		super(context, new SwampjawServantModel(context.bakeLayer(SwampjawServantModel.MODEL)), 0.75F);
	}

	@Override
	public ResourceLocation getTextureLocation(SwampjawServant entity) {
		return TEXTURE;
	}

	@Override
	protected void scale(SwampjawServant entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
		matrixStackIn.scale(2, 2, 2);
	}

	@Override
	protected void setupRotations(SwampjawServant entityLiving, PoseStack matrixStackIn, float ageInTicks,
			float rotationYaw, float partialTicks) {
		super.setupRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
		matrixStackIn.mulPose(Axis.XP.rotationDegrees(entityLiving.getXRot()));
	}

}
