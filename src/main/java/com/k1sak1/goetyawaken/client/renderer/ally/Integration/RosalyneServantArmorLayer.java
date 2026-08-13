package com.k1sak1.goetyawaken.client.renderer.ally.Integration;

import com.k1sak1.goetyawaken.client.model.ally.Integration.RosalyneServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.RosalyneServant;
import lykrast.meetyourfight.MeetYourFight;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RosalyneServantArmorLayer extends EnergySwirlLayer<RosalyneServant, RosalyneServantModel> {
	private static final ResourceLocation TEXTURE = MeetYourFight.rl("textures/entity/rosalyne_armor.png");
	private final RosalyneServantModel model;

	public RosalyneServantArmorLayer(RenderLayerParent<RosalyneServant, RosalyneServantModel> parent,
			EntityModelSet modelSet) {
		super(parent);
		model = new RosalyneServantModel(modelSet.bakeLayer(RosalyneServantModel.MODEL_ARMOR));
	}

	@Override
	protected float xOffset(float ticks) {
		return Mth.cos(ticks * 0.02F) * 2;
	}

	@Override
	protected ResourceLocation getTextureLocation() {
		return TEXTURE;
	}

	@Override
	protected EntityModel<RosalyneServant> model() {
		return model;
	}

}
