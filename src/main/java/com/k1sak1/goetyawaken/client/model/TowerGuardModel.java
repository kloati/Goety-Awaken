package com.k1sak1.goetyawaken.client.model;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.animation.TowerGuardAnimation;
import com.k1sak1.goetyawaken.common.entities.ally.illager.TowerGuardServant;
import com.k1sak1.goetyawaken.common.entities.hostile.illager.HostileTowerGuard;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmor;
import com.Polarice3.Goety.utils.ModelUtil;
import com.Polarice3.Goety.utils.ModelPartPose;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class TowerGuardModel<T extends LivingEntity> extends HierarchicalModel<T>
		implements HierarchicalArmor, ArmedModel, HeadedModel {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation(GoetyAwaken.MODID, "tower_guard"), "main");
	private final ModelPart root;
	private final ModelPart Guard;
	private final ModelPart Body;
	private final ModelPart Head;
	private final ModelPart r_eye;
	private final ModelPart r_eye_white;
	private final ModelPart r_pupil;
	private final ModelPart l_eye;
	private final ModelPart l_eye_white;
	private final ModelPart l_pupil;
	private final ModelPart right_brow;
	private final ModelPart left_brow;
	private final ModelPart nose;
	private final ModelPart Cape;
	private final ModelPart RightArm;
	private final ModelPart Spear;
	private final ModelPart LeftArm;
	private final ModelPart Shield;
	private final ModelPart RightLeg;
	private final ModelPart LeftLeg;

	public final List<String> allPartNames;

	private static final int TRANSITION_DURATION = 5;

	public TowerGuardModel(ModelPart root) {
		this.root = root;
		this.Guard = root.getChild("Guard");
		this.Body = this.Guard.getChild("Body");
		this.Head = this.Body.getChild("Head");
		this.r_eye = this.Head.getChild("r_eye");
		this.r_eye_white = this.r_eye.getChild("r_eye_white");
		this.r_pupil = this.r_eye.getChild("r_pupil");
		this.l_eye = this.Head.getChild("l_eye");
		this.l_eye_white = this.l_eye.getChild("l_eye_white");
		this.l_pupil = this.l_eye.getChild("l_pupil");
		this.right_brow = this.Head.getChild("right_brow");
		this.left_brow = this.Head.getChild("left_brow");
		this.nose = this.Head.getChild("nose");
		this.Cape = this.Body.getChild("Cape");
		this.RightArm = this.Body.getChild("RightArm");
		this.Spear = this.RightArm.getChild("Spear");
		this.LeftArm = this.Body.getChild("LeftArm");
		this.Shield = this.LeftArm.getChild("Shield");
		this.RightLeg = this.Guard.getChild("RightLeg");
		this.LeftLeg = this.Guard.getChild("LeftLeg");
		this.allPartNames = Stream.concat(Stream.of("root"), ModelUtil.getAllPartNames(this.root)).toList();
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Guard = partdefinition.addOrReplaceChild("Guard", CubeListBuilder.create(),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition Body = Guard.addOrReplaceChild("Body",
				CubeListBuilder.create().texOffs(38, 0)
						.addBox(-4.0F, -12.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
						.texOffs(30, 37).addBox(-5.0F, -12.5F, -3.0F, 10.0F, 12.0F, 6.0F, new CubeDeformation(0.125F)),
				PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition Armor_r1 = Body.addOrReplaceChild("Armor_r1",
				CubeListBuilder.create().texOffs(50, 55).addBox(-5.0F, -2.0F, -1.5F, 10.0F, 4.0F, 3.0F,
						new CubeDeformation(-0.125F)),
				PartPose.offsetAndRotation(0.0F, -9.9532F, -3.2887F, -0.4363F, 0.0F, 0.0F));

		PartDefinition Head = Body.addOrReplaceChild("Head",
				CubeListBuilder.create().texOffs(30, 19)
						.addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
						.texOffs(0, 75).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.5F))
						.texOffs(0, 0).addBox(-5.0F, -11.0F, -4.5F, 10.0F, 10.0F, 9.0F, new CubeDeformation(0.04F)),
				PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition Helmet_r1 = Head.addOrReplaceChild("Helmet_r1",
				CubeListBuilder.create().texOffs(26, 55).addBox(-5.0F, 0.05F, -2.0F, 10.0F, 8.0F, 2.0F,
						new CubeDeformation(0.05F)),
				PartPose.offsetAndRotation(0.0F, -11.0F, 4.5F, 0.6545F, 0.0F, 0.0F));

		PartDefinition r_eye = Head.addOrReplaceChild("r_eye", CubeListBuilder.create(),
				PartPose.offset(-1.0F, -3.0F, -4.0F));

		PartDefinition r_eye_white = r_eye.addOrReplaceChild("r_eye_white", CubeListBuilder.create().texOffs(44, 18)
				.addBox(-1.0F, -1.0F, -0.002F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-1.0F, 0.0F, 0.0F));

		PartDefinition r_pupil = r_eye.addOrReplaceChild("r_pupil", CubeListBuilder.create().texOffs(52, 18).addBox(
				-1.0F, -1.0F, -0.004F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition l_eye = Head.addOrReplaceChild("l_eye", CubeListBuilder.create(),
				PartPose.offset(1.0F, -3.0F, -4.0F));

		PartDefinition l_eye_white = l_eye.addOrReplaceChild("l_eye_white",
				CubeListBuilder.create().texOffs(44, 18).mirror()
						.addBox(-1.0F, -1.0F, -0.002F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offset(1.0F, 0.0F, 0.0F));

		PartDefinition l_pupil = l_eye.addOrReplaceChild("l_pupil",
				CubeListBuilder.create().texOffs(52, 18).mirror()
						.addBox(0.0F, -1.0F, -0.004F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition right_brow = Head.addOrReplaceChild("right_brow",
				CubeListBuilder.create().texOffs(38, 18)
						.addBox(-3.0F, -1.0F, -0.006F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
						.texOffs(48, 18).addBox(-3.0F, -2.0F, -0.006F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-1.0F, -4.0F, -4.0F));

		PartDefinition left_brow = Head.addOrReplaceChild("left_brow",
				CubeListBuilder.create().texOffs(38, 18).mirror()
						.addBox(0.0F, -1.0F, -0.006F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
						.texOffs(48, 18).mirror()
						.addBox(1.0F, -2.0F, -0.006F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offset(1.0F, -4.0F, -4.0F));

		PartDefinition nose = Head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(36, 65).addBox(-1.0F,
				-1.0F, -2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -4.0F));

		PartDefinition Cape = Body.addOrReplaceChild("Cape", CubeListBuilder.create().texOffs(0, 42).addBox(-6.0F, 0.0F,
				-0.5F, 12.0F, 20.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 3.5F));

		PartDefinition RightArm = Body.addOrReplaceChild("RightArm",
				CubeListBuilder.create().texOffs(62, 34)
						.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
						.texOffs(50, 62).addBox(-4.0F, -2.5F, -3.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.125F))
						.texOffs(0, 63).addBox(-4.0F, 1.75F, -3.0F, 4.0F, 2.0F, 6.0F, new CubeDeformation(0.125F))
						.texOffs(66, 0).addBox(-3.5F, 1.5F, -2.5F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.25F)),
				PartPose.offset(-5.0F, -10.0F, 0.0F));

		PartDefinition RightFistArmor_r1 = RightArm.addOrReplaceChild("RightFistArmor_r1",
				CubeListBuilder.create().texOffs(62, 50).addBox(-3.0F, -1.0F, -1.5F, 6.0F, 2.0F, 3.0F,
						new CubeDeformation(-0.2F)),
				PartPose.offsetAndRotation(-2.5F, 9.8F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition RightFistArmor_r2 = RightArm.addOrReplaceChild("RightFistArmor_r2",
				CubeListBuilder.create().texOffs(66, 12).addBox(-3.0F, -1.5F, -1.0F, 6.0F, 3.0F, 2.0F,
						new CubeDeformation(-0.2F)),
				PartPose.offsetAndRotation(-3.0F, 7.7F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition Spear = RightArm.addOrReplaceChild("Spear",
				CubeListBuilder.create().texOffs(44, 65)
						.addBox(16.0F, -19.0F, 0.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(26, 49).addBox(15.0F, -18.0F, 0.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(44, 70).addBox(14.0F, -17.0F, 0.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(44, 70).addBox(13.0F, -16.0F, 0.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(52, 72).addBox(16.0F, -15.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(70, 65).addBox(12.0F, -15.0F, 0.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(26, 42).addBox(11.0F, -14.0F, 0.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(0, 71).addBox(10.0F, -13.0F, 0.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(20, 63).addBox(8.0F, -12.0F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(4, 71).addBox(9.0F, -11.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(4, 71).addBox(8.0F, -10.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(8, 71).addBox(7.0F, -9.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(12, 71).addBox(6.0F, -8.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(12, 71).addBox(5.0F, -7.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(12, 71).addBox(4.0F, -6.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(12, 71).addBox(3.0F, -5.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(12, 71).addBox(2.0F, -4.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(16, 71).addBox(1.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(36, 71).addBox(0.0F, -2.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(40, 71).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(70, 71).addBox(-2.0F, 0.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(36, 71).addBox(-3.0F, 1.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(40, 71).addBox(-4.0F, 2.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(48, 72).addBox(-5.0F, 3.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.0F, 8.0F, 0.0F, 1.5708F, 0.7854F, 1.5708F));

		PartDefinition LeftArm = Body.addOrReplaceChild("LeftArm",
				CubeListBuilder.create().texOffs(62, 34).mirror()
						.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
						.texOffs(50, 62).mirror()
						.addBox(0.0F, -2.5F, -3.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.125F)).mirror(false)
						.texOffs(0, 63).mirror()
						.addBox(0.0F, 1.75F, -3.0F, 4.0F, 2.0F, 6.0F, new CubeDeformation(0.125F)).mirror(false)
						.texOffs(66, 0).addBox(0.5F, 1.5F, -2.5F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.25F)),
				PartPose.offset(5.0F, -10.0F, 0.0F));

		PartDefinition LeftFistArmor_r1 = LeftArm.addOrReplaceChild("LeftFistArmor_r1",
				CubeListBuilder.create().texOffs(62, 50).mirror()
						.addBox(-3.0F, -2.2F, -1.5F, 6.0F, 2.0F, 3.0F, new CubeDeformation(-0.2F)).mirror(false),
				PartPose.offsetAndRotation(2.5F, 11.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition LeftFistArmor_r2 = LeftArm.addOrReplaceChild("LeftFistArmor_r2",
				CubeListBuilder.create().texOffs(66, 12).mirror()
						.addBox(-3.0F, -1.3F, -1.0F, 6.0F, 3.0F, 2.0F, new CubeDeformation(-0.2F)).mirror(false),
				PartPose.offsetAndRotation(3.0F, 7.5F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition Shield = LeftArm.addOrReplaceChild("Shield", CubeListBuilder.create(),
				PartPose.offset(1.0F, 12.0F, -6.25F));

		PartDefinition plate_r1 = Shield.addOrReplaceChild("plate_r1",
				CubeListBuilder.create().texOffs(66, 8).addBox(-4.0F, -1.5F, -0.5F, 8.0F, 3.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -1.0F, -6.25F, 1.5708F, 0.0F, 0.0F));

		PartDefinition plate_r2 = Shield.addOrReplaceChild("plate_r2",
				CubeListBuilder.create().texOffs(0, 19).addBox(-7.0F, -11.0F, -0.5F, 14.0F, 22.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -1.0F, 6.25F, 1.5708F, 0.0F, 0.0F));

		PartDefinition RightLeg = Guard.addOrReplaceChild("RightLeg",
				CubeListBuilder.create().texOffs(62, 18)
						.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
						.texOffs(20, 65).addBox(-2.0F, 6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.45F))
						.texOffs(70, 62).addBox(-2.0F, 5.0F, -3.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.45F)),
				PartPose.offset(-2.0F, -12.0F, 0.0F));

		PartDefinition LeftLeg = Guard.addOrReplaceChild("LeftLeg",
				CubeListBuilder.create().texOffs(62, 18).mirror()
						.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
						.texOffs(20, 65).mirror()
						.addBox(-2.0F, 6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.45F)).mirror(false)
						.texOffs(70, 62).mirror()
						.addBox(-2.0F, 5.0F, -3.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.45F)).mirror(false),
				PartPose.offset(2.0F, -12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {
		int transitionTick = 0;
		String fromKey = "";
		String toKey = "";

		if (entity instanceof TowerGuardServant servant) {
			transitionTick = servant.baseAnimTransitionTick;
			if (transitionTick > 0) {
				fromKey = servant.transitionFromKey;
				toKey = servant.transitionToKey;
			}
		} else if (entity instanceof HostileTowerGuard hostile) {
			transitionTick = hostile.baseAnimTransitionTick;
			if (transitionTick > 0) {
				fromKey = hostile.transitionFromKey;
				toKey = hostile.transitionToKey;
			}
		}

		if (transitionTick > 0 && !fromKey.isEmpty()) {
			float partialTick = ageInTicks - (float) entity.tickCount;
			float t = 1.0F - ((float) transitionTick - partialTick) / (float) TRANSITION_DURATION;
			t = Mth.clamp(t, 0.0F, 1.0F);

			Map<String, ModelPartPose> fromPose = this.evaluatePass(fromKey, entity, ageInTicks, netHeadYaw, headPitch);
			Map<String, ModelPartPose> newPose = this.evaluatePass(toKey, entity, ageInTicks, netHeadYaw, headPitch);
			float eased = t < 0.5F ? 4.0F * t * t * t : 1.0F - (float) Math.pow(-2.0F * t + 2.0F, 3) / 2.0F;
			this.blendPoses(fromPose, newPose, eased);
		} else {
			String activeKey = null;
			if (entity instanceof TowerGuardServant s) {
				activeKey = s.getCurrentAnimKey();
			} else if (entity instanceof HostileTowerGuard h) {
				activeKey = h.getCurrentAnimKey();
			}
			if (activeKey != null && !activeKey.isEmpty()) {

				this.evaluatePass(activeKey, entity, ageInTicks, netHeadYaw, headPitch);
			} else {

				this.root().getAllParts().forEach(ModelPart::resetPose);
				this.Head.yRot = netHeadYaw * ((float) Math.PI / 180F);
				this.Head.xRot = headPitch * ((float) Math.PI / 180F);
			}
		}
	}

	private Map<String, ModelPartPose> evaluatePass(String key, T entity, float ageInTicks, float netHeadYaw,
			float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.Head.yRot = netHeadYaw * ((float) Math.PI / 180F);
		this.Head.xRot = headPitch * ((float) Math.PI / 180F);

		if (entity instanceof TowerGuardServant servant) {
			this.Shield.visible = servant.hasShield() && !servant.isShieldHidden();
			this.animate(servant.saceAnimationState, TowerGuardAnimation.SACE, ageInTicks);
			switch (key) {
				case "base_walk":
					this.animate(servant.walkAnimationState, TowerGuardAnimation.WALK, ageInTicks,
							servant.walkAnimSpeed);
					break;
				case "base_idle":
					this.animate(servant.idleAnimationState, TowerGuardAnimation.IDLE, ageInTicks);
					break;
				case "base_standing_by":
					this.animate(servant.standingByAnimationState, TowerGuardAnimation.STANDING_BY, ageInTicks);
					break;
				case "action_attack":
					this.animate(servant.attackAnimationState, TowerGuardAnimation.ATTACK, ageInTicks);
					break;
				case "action_shield_break":
					this.animate(servant.shieldBreakAnimationState, TowerGuardAnimation.SHIELD_BREAK, ageInTicks);
					break;
				case "action_charge":
					this.animate(servant.chargeAnimationState, TowerGuardAnimation.CHARGE, ageInTicks);
					break;
				case "action_charge_collided_stop":
					this.animate(servant.chargeCollidedStopAnimationState, TowerGuardAnimation.CHARGE_COLLIDED_STOP,
							ageInTicks);
					break;
				case "action_charge_normal_stop":
					this.animate(servant.chargeNormalStopAnimationState, TowerGuardAnimation.CHARGE_NORMAL_STOP,
							ageInTicks);
					break;
			}
		} else if (entity instanceof HostileTowerGuard hostile) {
			this.Shield.visible = hostile.hasShield() && !hostile.isShieldHidden();
			this.animate(hostile.saceAnimationState, TowerGuardAnimation.SACE, ageInTicks);
			switch (key) {
				case "base_walk":
					this.animate(hostile.walkAnimationState, TowerGuardAnimation.WALK, ageInTicks,
							hostile.walkAnimSpeed);
					break;
				case "base_idle":
					this.animate(hostile.idleAnimationState, TowerGuardAnimation.IDLE, ageInTicks);
					break;
				case "base_standing_by":
					this.animate(hostile.standingByAnimationState, TowerGuardAnimation.STANDING_BY, ageInTicks);
					break;
				case "action_attack":
					this.animate(hostile.attackAnimationState, TowerGuardAnimation.ATTACK, ageInTicks);
					break;
				case "action_shield_break":
					this.animate(hostile.shieldBreakAnimationState, TowerGuardAnimation.SHIELD_BREAK, ageInTicks);
					break;
				case "action_charge":
					this.animate(hostile.chargeAnimationState, TowerGuardAnimation.CHARGE, ageInTicks);
					break;
				case "action_charge_collided_stop":
					this.animate(hostile.chargeCollidedStopAnimationState, TowerGuardAnimation.CHARGE_COLLIDED_STOP,
							ageInTicks);
					break;
				case "action_charge_normal_stop":
					this.animate(hostile.chargeNormalStopAnimationState, TowerGuardAnimation.CHARGE_NORMAL_STOP,
							ageInTicks);
					break;
			}
		}
		return ModelUtil.saveModelSnapshot(this.allPartNames, this::getAnyDescendantWithName);
	}

	private void blendPoses(Map<String, ModelPartPose> from, Map<String, ModelPartPose> to, float progress) {
		for (Map.Entry<String, ModelPartPose> entry : from.entrySet()) {
			String boneName = entry.getKey();
			ModelPartPose fromPose = entry.getValue();
			ModelPartPose toPose = to.get(boneName);
			if (toPose == null)
				continue;

			this.getAnyDescendantWithName(boneName).ifPresent(part -> {
				part.xRot = fromPose.xRot() + (toPose.xRot() - fromPose.xRot()) * progress;
				part.yRot = fromPose.yRot() + (toPose.yRot() - fromPose.yRot()) * progress;
				part.zRot = fromPose.zRot() + (toPose.zRot() - fromPose.zRot()) * progress;
				part.x = fromPose.x() + (toPose.x() - fromPose.x()) * progress;
				part.y = fromPose.y() + (toPose.y() - fromPose.y()) * progress;
				part.z = fromPose.z() + (toPose.z() - fromPose.z()) * progress;
			});
		}
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void translateToHead(ModelPart modelPart, PoseStack poseStack) {
		this.root.translateAndRotate(poseStack);
		this.Guard.translateAndRotate(poseStack);
		this.Body.translateAndRotate(poseStack);
		modelPart.translateAndRotate(poseStack);
		poseStack.translate(0.0F, -0.25F, 0.0F);
		poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F));
		poseStack.scale(0.625F, -0.625F, -0.625F);
	}

	@Override
	public void translateToChest(ModelPart modelPart, PoseStack poseStack) {
		modelPart.translateAndRotate(poseStack);
	}

	@Override
	public void translateToLeg(ModelPart modelPart, PoseStack poseStack) {
		modelPart.translateAndRotate(poseStack);
	}

	@Override
	public void translateToArms(ModelPart modelPart, PoseStack poseStack) {
		modelPart.translateAndRotate(poseStack);
	}

	@Override
	public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
		this.getArm(arm).translateAndRotate(poseStack);
	}

	public ModelPart getArm(HumanoidArm arm) {
		return arm == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
	}

	public Iterable<ModelPart> rightHandArmors() {
		return ImmutableList.of();
	}

	public Iterable<ModelPart> leftHandArmors() {
		return ImmutableList.of();
	}

	public Iterable<ModelPart> rightLegPartArmors() {
		return ImmutableList.of(this.RightLeg);
	}

	public Iterable<ModelPart> leftLegPartArmors() {
		return ImmutableList.of(this.LeftLeg);
	}

	public Iterable<ModelPart> bodyPartArmors() {
		return ImmutableList.of(this.Body);
	}

	public Iterable<ModelPart> headPartArmors() {
		return ImmutableList.of(this.Head);
	}

	@Override
	public ModelPart getHead() {
		return this.Head;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		Guard.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}