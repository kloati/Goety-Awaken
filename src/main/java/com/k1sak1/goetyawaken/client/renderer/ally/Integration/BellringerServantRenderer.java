package com.k1sak1.goetyawaken.client.renderer.ally.Integration;

import com.k1sak1.goetyawaken.client.model.ally.Integration.BellringerServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.BellringerServant;
import lykrast.meetyourfight.MeetYourFight;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

//Based on https://https://github.com/Lykrast/MeetYourFight, Original by lykrast
public class BellringerServantRenderer extends HumanoidMobRenderer<BellringerServant, BellringerServantModel> {
	public static final ResourceLocation TEXTURE = MeetYourFight.rl("textures/entity/bellringer.png"),
			GLOW = MeetYourFight.rl("textures/entity/bellringer_glow.png");

	public BellringerServantRenderer(Context context) {
		super(context, new BellringerServantModel(context.bakeLayer(BellringerServantModel.MODEL)), 0.5F);
		addLayer(new GenericGlowLayer<>(this, GLOW));
		this.addLayer(new HumanoidArmorLayer<>(this,
				new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
				new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
				context.getModelManager()));
	}

	@Override
	public ResourceLocation getTextureLocation(BellringerServant entity) {
		return TEXTURE;
	}

	public class GenericGlowLayer<T extends Entity, M extends EntityModel<T>> extends EyesLayer<T, M> {
		private final RenderType TYPE;

		public GenericGlowLayer(RenderLayerParent<T, M> parent, ResourceLocation texture) {
			super(parent);
			TYPE = RenderType.entityTranslucentEmissive(texture, false);
		}

		@Override
		public RenderType renderType() {
			return TYPE;
		}

	}
}
