package com.k1sak1.goetyawaken.client.model;

import com.k1sak1.goetyawaken.client.animation.RoyalguardServantAnimations;
import com.k1sak1.goetyawaken.common.entities.ally.illager.RoyalguardServant;
import com.k1sak1.goetyawaken.common.entities.hostile.illager.HostileRoyalguard;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmor;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

@OnlyIn(Dist.CLIENT)
public class RoyalguardModel<T extends LivingEntity> extends HierarchicalModel<T>
		implements HierarchicalArmor, ArmedModel, HeadedModel {
	private final ModelPart root;
	private final ModelPart royalguard;
	private final ModelPart right_leg;
	private final ModelPart left_leg;
	private final ModelPart body;
	private final ModelPart right_arm;
	private final ModelPart rightItem;
	private final ModelPart mace;
	private final ModelPart left_arm;
	private final ModelPart shield;
	private final ModelPart head;
	private final ModelPart nose;

	public RoyalguardModel(ModelPart root) {
		this.root = root;
		this.royalguard = root.getChild("royalguard");
		this.right_leg = this.royalguard.getChild("right_leg");
		this.left_leg = this.royalguard.getChild("left_leg");
		this.body = this.royalguard.getChild("body");
		this.right_arm = this.body.getChild("right_arm");
		this.rightItem = this.right_arm.getChild("rightItem");
		this.mace = this.rightItem.getChild("mace");
		this.left_arm = this.body.getChild("left_arm");
		this.shield = this.left_arm.getChild("shield");
		this.head = this.body.getChild("head");
		this.nose = this.head.getChild("nose");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition royalguard = partdefinition.addOrReplaceChild("royalguard", CubeListBuilder.create(),
				PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition right_leg = royalguard.addOrReplaceChild("right_leg",
				CubeListBuilder.create().texOffs(0, 22)
						.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
						.texOffs(17, 48).addBox(-2.25F, 3.5F, -3.25F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(0, 38).addBox(-2.0F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.5F)),
				PartPose.offsetAndRotation(-3.5F, 0.0F, 1.5F, 0.0873F, 0.6109F, 0.0436F));

		PartDefinition left_leg = royalguard.addOrReplaceChild("left_leg",
				CubeListBuilder.create().texOffs(0, 22).mirror()
						.addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
						.texOffs(17, 48).mirror()
						.addBox(-2.4F, 3.5F, -3.25F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
						.texOffs(0, 38).mirror().addBox(-1.9F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.5F))
						.mirror(false),
				PartPose.offsetAndRotation(2.9F, 0.0F, -3.0F, -0.0873F, -0.3491F, 0.0F));

		PartDefinition body = royalguard.addOrReplaceChild("body",
				CubeListBuilder.create().texOffs(16, 20)
						.addBox(-4.0F, -12.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
						.texOffs(92, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
						.texOffs(60, 20).addBox(-5.0F, -12.0F, -3.0F, 10.0F, 12.0F, 6.0F, new CubeDeformation(0.1F)),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition body_r1 = body.addOrReplaceChild("body_r1",
				CubeListBuilder.create().texOffs(16, 40).mirror()
						.addBox(-3.05F, 1.0F, -0.5F, 5.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
						.texOffs(16, 40).addBox(2.55F, 1.0F, -0.5F, 5.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-2.25F, -12.0F, -3.0F, -0.5236F, 0.0F, 0.0F));

		PartDefinition right_arm = body.addOrReplaceChild("right_arm",
				CubeListBuilder.create().texOffs(44, 38).mirror()
						.addBox(-4.0F, -3.5F, -3.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
						.texOffs(44, 22).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-6.25F, -10.0F, 1.5F, 0.48F, 0.6981F, 0.6981F));

		PartDefinition rightArm_r1 = right_arm.addOrReplaceChild("rightArm_r1",
				CubeListBuilder.create().texOffs(44, 48).addBox(1.5F, 1.0F, -2.5F, 3.0F, 4.0F, 5.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(1.0F, -1.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

		PartDefinition rightArm_r2 = right_arm.addOrReplaceChild("rightArm_r2",
				CubeListBuilder.create().texOffs(0, 47)
						.addBox(-3.0F, 6.5F, -5.0F, 6.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
						.texOffs(0, 52).addBox(-3.0F, 9.5F, -5.0F, 6.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition rightArm_r3 = right_arm.addOrReplaceChild("rightArm_r3",
				CubeListBuilder.create().texOffs(116, 16).addBox(5.0F, -2.5F, -1.0F, 2.0F, 1.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

		PartDefinition rightItem = right_arm.addOrReplaceChild("rightItem", CubeListBuilder.create(),
				PartPose.offset(-1.0F, 7.0F, 1.0F));

		PartDefinition mace = rightItem.addOrReplaceChild("mace",
				CubeListBuilder.create().texOffs(0, 61)
						.addBox(-9.0F, 7.0F, 3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(0, 64).addBox(-11.0F, -4.0F, -7.0F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
						.texOffs(0, 66).addBox(-9.0F, -6.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(8.0F, 0.0F, -7.0F, 0.8802F, 0.1119F, 0.1343F));

		PartDefinition mace_r1 = mace.addOrReplaceChild("mace_r1",
				CubeListBuilder.create().texOffs(0, 66).addBox(5.0F, -5.0F, 12.0F, 1.0F, 2.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-8.0F, 11.0F, 1.0F, 0.0F, 1.5708F, -1.5708F));

		PartDefinition mace_r2 = mace.addOrReplaceChild("mace_r2",
				CubeListBuilder.create().texOffs(0, 66).addBox(-1.0F, 8.0F, 5.0F, 1.0F, 2.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-8.0F, 11.0F, 1.0F, 3.1416F, 0.0F, 0.0F));

		PartDefinition mace_r3 = mace.addOrReplaceChild("mace_r3",
				CubeListBuilder.create().texOffs(0, 66)
						.addBox(-1.0F, -10.0F, 12.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(0, 59).addBox(-1.0F, 0.0F, 5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(0, 57).addBox(-1.0F, -4.0F, 9.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(0, 57).addBox(-1.0F, -3.0F, 8.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(0, 57).addBox(-1.0F, -2.0F, 7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(0, 61).addBox(-1.0F, -1.0F, 6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(0, 59).addBox(-1.0F, 1.0F, 4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-8.0F, 11.0F, 1.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition mace_r4 = mace.addOrReplaceChild("mace_r4",
				CubeListBuilder.create().texOffs(0, 66).addBox(-1.0F, 1.0F, -13.0F, 1.0F, 2.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-8.0F, 11.0F, 1.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition mace_r5 = mace.addOrReplaceChild("mace_r5",
				CubeListBuilder.create().texOffs(0, 66).addBox(-6.0F, -4.0F, 12.0F, 1.0F, 2.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-8.0F, 11.0F, 1.0F, 0.0F, -1.5708F, 1.5708F));

		PartDefinition mace_r6 = mace.addOrReplaceChild("mace_r6",
				CubeListBuilder.create().texOffs(0, 74)
						.addBox(-4.0F, -11.0F, 0.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.0F))
						.texOffs(0, 74).addBox(-4.0F, -11.0F, -1.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-8.0F, 11.0F, 1.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition mace_r7 = mace.addOrReplaceChild("mace_r7",
				CubeListBuilder.create().texOffs(0, 57)
						.addBox(0.0F, -10.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(0, 57).addBox(0.0F, -9.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(0, 57).addBox(0.0F, -8.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-8.0F, 10.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

		PartDefinition mace_r8 = mace.addOrReplaceChild("mace_r8",
				CubeListBuilder.create().texOffs(0, 59)
						.addBox(0.0F, -8.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(0, 59).addBox(0.0F, -6.0F, -3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(0, 59).addBox(0.0F, -7.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(0, 61).addBox(0.0F, -4.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(0, 61).addBox(0.0F, -5.0F, -4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-8.0F, 11.0F, 1.0F, -3.1416F, 0.0F, 3.1416F));

		PartDefinition left_arm = body.addOrReplaceChild("left_arm",
				CubeListBuilder.create().texOffs(44, 22).mirror()
						.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
						.texOffs(116, 16).addBox(4.0F, -2.5F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
						.texOffs(44, 48).addBox(0.5F, 0.0F, -2.5F, 3.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
						.texOffs(44, 38).addBox(0.0F, -3.5F, -3.0F, 4.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(6.0F, -11.0F, 1.5F, -1.0472F, 0.0F, 0.0F));

		PartDefinition leftArm_r1 = left_arm.addOrReplaceChild("leftArm_r1",
				CubeListBuilder.create().texOffs(0, 52)
						.addBox(-3.0F, 9.5F, -5.0F, 6.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
						.texOffs(0, 47).addBox(-3.0F, 6.5F, -5.0F, 6.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition shield = left_arm.addOrReplaceChild("shield",
				CubeListBuilder.create().texOffs(32, 57).addBox(-7.0F, -8.5F, -1.0F, 14.0F, 22.0F, 1.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(1.0F, 11.5F, -2.0F, 1.0472F, 0.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head",
				CubeListBuilder.create().texOffs(0, 0)
						.addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
						.texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.25F))
						.texOffs(64, 0).addBox(-5.0F, -10.5F, -5.0F, 10.0F, 9.0F, 10.0F, new CubeDeformation(0.0F))
						.texOffs(116, 14).mirror()
						.addBox(5.0F, -9.5F, -2.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
						.texOffs(116, 13).addBox(6.0F, -12.5F, -2.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
						.texOffs(116, 14).addBox(-8.0F, -9.5F, -2.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
						.texOffs(116, 13).addBox(-8.0F, -12.5F, -2.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
						.texOffs(104, 13).addBox(-2.0F, -13.5F, -5.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, 0.0F,
				-2.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, -4.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
		this.head.xRot = headPitch * ((float) Math.PI / 180F);
		if (entity instanceof RoyalguardServant) {
			RoyalguardServant servant = (RoyalguardServant) entity;
			this.shield.visible = !servant.isShieldHidden();
			this.animate(servant.idleAnimationState, RoyalguardServantAnimations.IDLE, ageInTicks);
			this.animate(servant.walkAnimationState, RoyalguardServantAnimations.WALK, ageInTicks);
			this.animate(servant.attackAnimationState, RoyalguardServantAnimations.ATTACK, ageInTicks);
			this.animate(servant.standAnimationState, RoyalguardServantAnimations.STAND, ageInTicks);
			this.animate(servant.patrolWalkAnimationState, RoyalguardServantAnimations.PATROL_WALK, ageInTicks);
		} else if (entity instanceof HostileRoyalguard) {
			HostileRoyalguard hostile = (HostileRoyalguard) entity;
			this.shield.visible = !hostile.isShieldHidden();
			this.animate(hostile.idleAnimationState, RoyalguardServantAnimations.IDLE, ageInTicks);
			this.animate(hostile.walkAnimationState, RoyalguardServantAnimations.WALK, ageInTicks);
			this.animate(hostile.attackAnimationState, RoyalguardServantAnimations.ATTACK, ageInTicks);
			this.animate(hostile.standAnimationState, RoyalguardServantAnimations.STAND, ageInTicks);
			this.animate(hostile.patrolWalkAnimationState, RoyalguardServantAnimations.PATROL_WALK, ageInTicks);
		}
	}

	@Override
	public ModelPart root() {
		return this.root;
	}

	@Override
	public void translateToHead(ModelPart modelPart, PoseStack poseStack) {
		this.root.translateAndRotate(poseStack);
		this.royalguard.translateAndRotate(poseStack);
		this.body.translateAndRotate(poseStack);
		modelPart.translateAndRotate(poseStack);
		float f = 0.625F;
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
		return arm == HumanoidArm.LEFT ? this.left_arm : this.right_arm;
	}

	public Iterable<ModelPart> rightHandArmors() {
		return ImmutableList.of();
	}

	public Iterable<ModelPart> leftHandArmors() {
		return ImmutableList.of();
	}

	public Iterable<ModelPart> rightLegPartArmors() {
		return ImmutableList.of(this.right_leg);
	}

	public Iterable<ModelPart> leftLegPartArmors() {
		return ImmutableList.of(this.left_leg);
	}

	public Iterable<ModelPart> bodyPartArmors() {
		return ImmutableList.of(this.body);
	}

	public Iterable<ModelPart> headPartArmors() {
		return ImmutableList.of(this.head);
	}

	@Override
	public ModelPart getHead() {
		return this.head;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		royalguard.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}