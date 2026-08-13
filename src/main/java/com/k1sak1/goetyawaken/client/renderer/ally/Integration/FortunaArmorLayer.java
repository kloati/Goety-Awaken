package com.k1sak1.goetyawaken.client.renderer.ally.Integration;

import com.k1sak1.goetyawaken.client.model.ally.Integration.DameFortunaServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.DameFortunaServant;

import lykrast.meetyourfight.MeetYourFight;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

//Based on https://https://github.com/Lykrast/MeetYourFight, Original by lykrast
public class FortunaArmorLayer extends EnergySwirlLayer<DameFortunaServant, DameFortunaServantModel> {
	private static final ResourceLocation TEXTURE = MeetYourFight.rl("textures/entity/dame_fortuna_armor.png");
	private final DameFortunaServantModel model;

	public FortunaArmorLayer(RenderLayerParent<DameFortunaServant, DameFortunaServantModel> parent,
			EntityModelSet modelSet) {
		super(parent);
		model = new DameFortunaServantModel(modelSet.bakeLayer(DameFortunaServantModel.MODEL_ARMOR));
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
	protected EntityModel<DameFortunaServant> model() {
		return model;
	}

}
