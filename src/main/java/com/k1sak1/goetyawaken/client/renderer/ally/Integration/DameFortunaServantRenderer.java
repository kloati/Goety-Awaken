package com.k1sak1.goetyawaken.client.renderer.ally.Integration;

import com.k1sak1.goetyawaken.client.model.ally.Integration.DameFortunaServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.DameFortunaServant;
import com.mojang.blaze3d.vertex.PoseStack;
import lykrast.meetyourfight.MeetYourFight;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;

//Based on https://https://github.com/Lykrast/MeetYourFight, Original by lykrast
public class DameFortunaServantRenderer extends HumanoidMobRenderer<DameFortunaServant, DameFortunaServantModel> {
	public static final ResourceLocation TEXTURE = MeetYourFight.rl("textures/entity/dame_fortuna.png"),
			GLOW = MeetYourFight.rl("textures/entity/dame_fortuna_glow.png");

	public DameFortunaServantRenderer(Context context) {
		super(context, new DameFortunaServantModel(context.bakeLayer(DameFortunaServantModel.MODEL)), 0.5F);
		addLayer(new HumanoidArmorLayer<DameFortunaServant, DameFortunaServantModel, HumanoidModel<DameFortunaServant>>(
				this,
				new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
				new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
				context.getModelManager()) {
			@Override
			protected void setPartVisibility(HumanoidModel<DameFortunaServant> model, EquipmentSlot slot) {
				super.setPartVisibility(model, slot);
				if (slot == EquipmentSlot.HEAD) {
					model.head.visible = false;
					model.hat.visible = false;
				}
			}
		});
		addLayer(new GenericGlowLayer<>(this, GLOW));
		addLayer(new FortunaArmorLayer(this, context.getModelSet()));
	}

	@Override
	protected void setupRotations(DameFortunaServant entity, PoseStack stack, float ageInTicks, float rotationYaw,
			float partialTicks) {
		rotationYaw = Mth.wrapDegrees(rotationYaw + entity.getSpinAngle(partialTicks));
		super.setupRotations(entity, stack, ageInTicks, rotationYaw, partialTicks);
	}

	@Override
	public ResourceLocation getTextureLocation(DameFortunaServant entity) {
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
