package com.k1sak1.goetyawaken.client.model;

import com.k1sak1.goetyawaken.common.entities.ally.golem.MushroomMonstrosity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class MushroomMonstrosityModel<T extends Entity> extends HierarchicalModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
			new ResourceLocation("modid", "mushroommonstrositymodel"), "main");
	private final ModelPart root;
	private final ModelPart monstrosity;
	private final ModelPart upper;
	private final ModelPart body;
	private final ModelPart mushroom1;
	private final ModelPart head;
	private final ModelPart top;
	private final ModelPart right_horn;
	private final ModelPart left_horn;
	private final ModelPart eyes;
	private final ModelPart bottom;
	private final ModelPart right_arm;
	private final ModelPart right_shoulder;
	private final ModelPart mushroom2;
	private final ModelPart right_middle;
	private final ModelPart right_hand;
	private final ModelPart r_finger_s;
	private final ModelPart r_finger_n;
	private final ModelPart r_thumb;
	private final ModelPart left_arm;
	private final ModelPart left_shoulder;
	private final ModelPart mushroom3;
	private final ModelPart left_middle;
	private final ModelPart left_hand;
	private final ModelPart l_finger_s;
	private final ModelPart l_finger_n;
	private final ModelPart l_thumb;
	private final ModelPart pelvis;
	private final ModelPart right_leg;
	private final ModelPart left_leg;

	public MushroomMonstrosityModel(ModelPart root) {
		this.root = root;
		this.monstrosity = root.getChild("monstrosity");
		this.upper = this.monstrosity.getChild("upper");
		this.body = this.upper.getChild("body");
		this.mushroom1 = this.body.getChild("mushroom1");
		this.head = this.upper.getChild("head");
		this.top = this.head.getChild("top");
		this.right_horn = this.top.getChild("right_horn");
		this.left_horn = this.top.getChild("left_horn");
		this.eyes = this.top.getChild("eyes");
		this.bottom = this.head.getChild("bottom");
		this.right_arm = this.upper.getChild("right_arm");
		this.right_shoulder = this.right_arm.getChild("right_shoulder");
		this.mushroom2 = this.right_shoulder.getChild("mushroom2");
		this.right_middle = this.right_shoulder.getChild("right_middle");
		this.right_hand = this.right_middle.getChild("right_hand");
		this.r_finger_s = this.right_hand.getChild("r_finger_s");
		this.r_finger_n = this.right_hand.getChild("r_finger_n");
		this.r_thumb = this.right_hand.getChild("r_thumb");
		this.left_arm = this.upper.getChild("left_arm");
		this.left_shoulder = this.left_arm.getChild("left_shoulder");
		this.mushroom3 = this.left_shoulder.getChild("mushroom3");
		this.left_middle = this.left_shoulder.getChild("left_middle");
		this.left_hand = this.left_middle.getChild("left_hand");
		this.l_finger_s = this.left_hand.getChild("l_finger_s");
		this.l_finger_n = this.left_hand.getChild("l_finger_n");
		this.l_thumb = this.left_hand.getChild("l_thumb");
		this.pelvis = this.monstrosity.getChild("pelvis");
		this.right_leg = this.monstrosity.getChild("right_leg");
		this.left_leg = this.monstrosity.getChild("left_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition monstrosity = partdefinition.addOrReplaceChild("monstrosity", CubeListBuilder.create(),
				PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition upper = monstrosity.addOrReplaceChild("upper", CubeListBuilder.create(),
				PartPose.offset(0.0F, -36.0F, 0.0F));

		PartDefinition body = upper.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-37.0F,
				-57.0F, -14.5F, 74.0F, 57.0F, 30.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1",
				CubeListBuilder.create().texOffs(297, 32).addBox(-14.0F, -22.5F, 15.0F, 28.0F, 16.0F, 11.0F,
						new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -28.5F, 41.5F, 0.0F, 3.1416F, 0.0F));

		PartDefinition mushroom1 = body.addOrReplaceChild("mushroom1",
				CubeListBuilder.create().texOffs(319, 68)
						.addBox(-26.0F, -68.0F, 11.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.2F))
						.texOffs(319, 68).addBox(18.0F, -68.0F, -10.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.2F))
						.texOffs(319, 68).addBox(24.0F, -68.0F, 12.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.4F))
						.texOffs(334, 67).addBox(26.0F, -70.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.2F))
						.texOffs(319, 68).addBox(-14.0F, -68.0F, -7.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.7F))
						.texOffs(319, 68).addBox(-2.0F, -68.0F, 2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.7F))
						.texOffs(208, 80).addBox(-27.0F, -71.0F, 10.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.2F))
						.texOffs(208, 80).addBox(17.0F, -71.0F, -11.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.2F))
						.texOffs(208, 80).mirror()
						.addBox(25.0F, -73.0F, -7.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.2F)).mirror(false)
						.texOffs(208, 80).addBox(23.0F, -71.0F, 11.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.5F))
						.texOffs(224, 80).mirror()
						.addBox(-15.0F, -73.0F, -8.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(1.6F)).mirror(false)
						.texOffs(208, 80).addBox(-3.0F, -73.0F, 1.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(1.6F))
						.texOffs(319, 68).addBox(24.0F, -50.0F, 15.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.2F))
						.texOffs(319, 68).addBox(-5.0F, -50.0F, 27.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.2F))
						.texOffs(319, 68).addBox(-26.0F, -62.0F, 15.3F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.4F))
						.texOffs(248, 168).mirror()
						.addBox(-27.0F, -63.0F, 18.5F, 4.0F, 4.0F, 3.0F, new CubeDeformation(1.3F)).mirror(false)
						.texOffs(248, 168).addBox(23.0F, -51.0F, 17.0F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.3F))
						.texOffs(248, 175).addBox(-6.0F, -51.0F, 29.0F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.3F))
						.texOffs(319, 68).addBox(32.0F, -42.0F, -17.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.1F))
						.texOffs(240, 80).addBox(31.0F, -43.0F, -19.5F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 9.0F, 0.0F));

		PartDefinition head = upper.addOrReplaceChild("head", CubeListBuilder.create(),
				PartPose.offset(0.0F, -28.0F, -15.0F));

		PartDefinition top = head.addOrReplaceChild("top",
				CubeListBuilder.create().texOffs(0, 186)
						.addBox(-14.0F, -25.0F, -20.0F, 28.0F, 31.0F, 21.0F, new CubeDeformation(0.0F))
						.texOffs(256, 132).addBox(10.0F, -28.0F, -13.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.7F))
						.texOffs(224, 80).addBox(9.0F, -32.5F, -14.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(1.2F))
						.texOffs(319, 68).addBox(7.0F, -15.0F, -23.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.01F))
						.texOffs(240, 80).addBox(6.0F, -16.0F, -26.0F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.01F)),
				PartPose.offset(0.0F, 0.0F, -1.0F));

		PartDefinition right_horn = top.addOrReplaceChild("right_horn",
				CubeListBuilder.create().texOffs(256, 80)
						.addBox(-7.25F, 0.5F, -6.5F, 20.0F, 13.0F, 13.0F, new CubeDeformation(0.0F))
						.texOffs(294, 0).addBox(-7.25F, -14.5F, -6.5F, 9.0F, 15.0F, 13.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-26.75F, -23.5F, -8.5F));

		PartDefinition left_horn = top.addOrReplaceChild("left_horn",
				CubeListBuilder.create().texOffs(256, 106)
						.addBox(-12.75F, 0.5F, -6.5F, 20.0F, 13.0F, 13.0F, new CubeDeformation(0.0F))
						.texOffs(340, 0).addBox(-1.75F, -14.5F, -6.5F, 9.0F, 15.0F, 13.0F, new CubeDeformation(0.0F)),
				PartPose.offset(26.75F, -23.5F, -8.5F));

		PartDefinition eyes = top.addOrReplaceChild("eyes",
				CubeListBuilder.create().texOffs(12, 16)
						.addBox(-14.0F, -4.0F, 0.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
						.texOffs(12, 16).addBox(10.25F, -4.0F, 0.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
						.texOffs(12, 12).addBox(-2.0F, -7.0F, 0.0F, 6.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, -2.0F, -20.5F));

		PartDefinition bottom = head.addOrReplaceChild("bottom",
				CubeListBuilder.create().texOffs(232, 137)
						.addBox(-13.5F, 0.0F, -20.0F, 27.0F, 10.0F, 21.0F, new CubeDeformation(0.0F))
						.texOffs(369, 44).addBox(-13.5F, 7.0F, -20.0F, 27.0F, 0.0F, 21.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 0.0F, -1.0F));

		PartDefinition right_arm = upper.addOrReplaceChild("right_arm", CubeListBuilder.create(),
				PartPose.offset(-38.0F, -44.0F, -0.5F));

		PartDefinition right_shoulder = right_arm.addOrReplaceChild("right_shoulder", CubeListBuilder.create()
				.texOffs(0, 87).addBox(-36.0F, -12.0F, -13.5F, 37.0F, 23.0F, 27.0F, new CubeDeformation(0.0F))
				.texOffs(98, 186).addBox(-19.0F, -35.0F, -13.5F, 20.0F, 23.0F, 27.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 8.0F, 0.0F));

		PartDefinition mushroom2 = right_shoulder.addOrReplaceChild("mushroom2",
				CubeListBuilder.create().texOffs(319, 68)
						.addBox(-30.5F, -5.0F, -4.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.2F))
						.texOffs(78, 246).mirror()
						.addBox(-33.5F, -6.0F, -5.5F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.4F)).mirror(false)
						.texOffs(319, 68).addBox(-20.5F, -14.0F, 0.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.1F))
						.texOffs(224, 80).addBox(-21.5F, -16.5F, -0.5F, 4.0F, 3.0F, 4.0F, new CubeDeformation(-0.1F))
						.texOffs(319, 68).addBox(-13.5F, -28.0F, -1.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.1F))
						.texOffs(78, 246).addBox(-16.5F, -29.0F, -2.5F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.2F))
						.texOffs(319, 68).addBox(-10.5F, -37.0F, -5.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.5F))
						.texOffs(208, 80).mirror()
						.addBox(-11.5F, -41.0F, -6.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.7F)).mirror(false)
						.texOffs(319, 68).addBox(-1.5F, -24.0F, -15.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
						.texOffs(240, 80).addBox(-2.5F, -25.0F, -18.5F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.1F)),
				PartPose.offset(-7.5F, 0.0F, 0.5F));

		PartDefinition right_middle = right_shoulder.addOrReplaceChild("right_middle", CubeListBuilder.create()
				.texOffs(386, 0).addBox(-11.0F, 3.0F, -13.0F, 22.0F, 22.0F, 16.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-18.0F, 8.0F, 4.5F));

		PartDefinition right_hand = right_middle.addOrReplaceChild("right_hand", CubeListBuilder.create()
				.texOffs(0, 137).addBox(-14.5F, -1.0F, -14.5F, 29.0F, 20.0F, 29.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-0.5F, 26.0F, -4.5F));

		PartDefinition r_finger_s = right_hand.addOrReplaceChild("r_finger_s", CubeListBuilder.create()
				.texOffs(232, 168).addBox(-1.5F, 1.0F, -2.5F, 3.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-8.0F, 18.0F, 4.0F));

		PartDefinition r_finger_n = right_hand.addOrReplaceChild("r_finger_n", CubeListBuilder.create()
				.texOffs(232, 168).addBox(-1.5F, 1.0F, -2.5F, 3.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-8.0F, 18.0F, -5.0F));

		PartDefinition r_thumb = right_hand.addOrReplaceChild("r_thumb",
				CubeListBuilder.create().texOffs(232, 168).mirror()
						.addBox(-1.5F, 1.0F, -2.5F, 3.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offset(7.0F, 18.0F, 1.0F));

		PartDefinition left_arm = upper.addOrReplaceChild("left_arm", CubeListBuilder.create(),
				PartPose.offset(38.0F, -44.0F, 0.0F));

		PartDefinition left_shoulder = left_arm.addOrReplaceChild("left_shoulder", CubeListBuilder.create()
				.texOffs(128, 87).addBox(-1.0F, -12.0F, -13.5F, 37.0F, 23.0F, 27.0F, new CubeDeformation(0.0F))
				.texOffs(192, 186).addBox(-1.0F, -35.0F, -13.5F, 20.0F, 23.0F, 27.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, 8.0F, -0.5F));

		PartDefinition mushroom3 = left_shoulder.addOrReplaceChild("mushroom3",
				CubeListBuilder.create().texOffs(248, 182)
						.addBox(28.5F, 4.0F, 5.5F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.1F))
						.texOffs(78, 238).addBox(30.5F, 3.0F, 4.5F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.2F))
						.texOffs(319, 68).addBox(25.5F, -14.0F, -8.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.3F))
						.texOffs(208, 80).addBox(24.5F, -18.0F, -9.5F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.8F))
						.texOffs(319, 68).addBox(3.5F, -1.0F, -14.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.1F))
						.texOffs(240, 80).addBox(2.5F, -1.0F, -18.0F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.1F))
						.texOffs(319, 68).addBox(6.5F, -37.0F, -8.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.1F))
						.texOffs(319, 68).addBox(-6.5F, -37.0F, 7.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.1F))
						.texOffs(224, 80).addBox(5.5F, -40.0F, -9.5F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.1F))
						.texOffs(208, 80).mirror()
						.addBox(-7.5F, -40.0F, 6.5F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.2F)).mirror(false),
				PartPose.offset(7.5F, 0.0F, 0.5F));

		PartDefinition left_middle = left_shoulder.addOrReplaceChild("left_middle",
				CubeListBuilder.create().texOffs(386, 0).mirror()
						.addBox(-11.0F, 3.0F, -13.0F, 22.0F, 22.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offset(17.0F, 8.0F, 4.5F));

		PartDefinition left_hand = left_middle.addOrReplaceChild("left_hand", CubeListBuilder.create().texOffs(116, 137)
				.addBox(-14.5F, -1.0F, -14.5F, 29.0F, 20.0F, 29.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.5F, 26.0F, -4.5F));

		PartDefinition l_finger_s = left_hand.addOrReplaceChild("l_finger_s",
				CubeListBuilder.create().texOffs(232, 168).mirror()
						.addBox(-1.5F, 1.0F, -2.5F, 3.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offset(8.0F, 18.0F, 4.0F));

		PartDefinition l_finger_n = left_hand.addOrReplaceChild("l_finger_n",
				CubeListBuilder.create().texOffs(232, 168).mirror()
						.addBox(-1.5F, 1.0F, -2.5F, 3.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offset(8.0F, 18.0F, -5.0F));

		PartDefinition l_thumb = left_hand.addOrReplaceChild("l_thumb", CubeListBuilder.create().texOffs(232, 168)
				.addBox(-1.5F, 1.0F, -2.5F, 3.0F, 10.0F, 5.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-7.0F, 18.0F, 1.0F));

		PartDefinition pelvis = monstrosity.addOrReplaceChild("pelvis", CubeListBuilder.create().texOffs(208, 48)
				.addBox(-14.0F, -5.5F, -10.5F, 28.0F, 11.0F, 21.0F, new CubeDeformation(0.0F)),
				PartPose.offset(0.0F, -30.5F, 0.5F));

		PartDefinition right_leg = monstrosity.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(208, 0)
				.addBox(-22.0F, 0.0F, -9.0F, 24.0F, 29.0F, 19.0F, new CubeDeformation(0.0F)),
				PartPose.offset(-9.0F, -29.0F, 0.0F));

		PartDefinition left_leg = monstrosity.addOrReplaceChild("left_leg",
				CubeListBuilder.create().texOffs(208, 0).mirror()
						.addBox(-2.0F, 0.0F, -9.0F, 24.0F, 29.0F, 19.0F, new CubeDeformation(0.0F)).mirror(false),
				PartPose.offset(9.0F, -29.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 512, 256);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (entity instanceof MushroomMonstrosity mushroomMonstrosity) {
			this.animate(mushroomMonstrosity.activateAnimationState,
					com.k1sak1.goetyawaken.client.animation.MushroomMonstrosityAnimations.ACTIVATE,
					ageInTicks);

			this.animate(mushroomMonstrosity.idleAnimationState,
					com.k1sak1.goetyawaken.client.animation.MushroomMonstrosityAnimations.IDLE, ageInTicks);

			this.animateWalk(com.k1sak1.goetyawaken.client.animation.MushroomMonstrosityAnimations.WALK,
					limbSwing, limbSwingAmount, 2.0F, 2.5F);

			this.animate(mushroomMonstrosity.toSitAnimationState,
					com.k1sak1.goetyawaken.client.animation.MushroomMonstrosityAnimations.TO_SIT, ageInTicks);

			this.animate(mushroomMonstrosity.toStandAnimationState,
					com.k1sak1.goetyawaken.client.animation.MushroomMonstrosityAnimations.TO_STAND,
					ageInTicks);

			this.animate(mushroomMonstrosity.sitAnimationState,
					com.k1sak1.goetyawaken.client.animation.MushroomMonstrosityAnimations.SIT, ageInTicks);

			this.animate(mushroomMonstrosity.deathAnimationState,
					com.k1sak1.goetyawaken.client.animation.MushroomMonstrosityAnimations.DEATH, ageInTicks);

			this.animate(mushroomMonstrosity.smashOldAnimationState,
					com.k1sak1.goetyawaken.client.animation.MushroomMonstrosityAnimations.SMASH_OLD,
					ageInTicks);

			this.animate(mushroomMonstrosity.smashAnimationState,
					com.k1sak1.goetyawaken.client.animation.MushroomMonstrosityAnimations.SMASH,
					ageInTicks);

			this.animate(mushroomMonstrosity.summonAnimationState,
					com.k1sak1.goetyawaken.client.animation.MushroomMonstrosityAnimations.SUMMON,
					ageInTicks);

			this.animate(mushroomMonstrosity.summon2AnimationState,
					com.k1sak1.goetyawaken.client.animation.MushroomMonstrosityAnimations.SUMMON2,
					ageInTicks);

			this.animate(mushroomMonstrosity.spitAnimationState,
					com.k1sak1.goetyawaken.client.animation.MushroomMonstrosityAnimations.SPIT,
					ageInTicks);

			this.animate(mushroomMonstrosity.strafeAnimationState,
					com.k1sak1.goetyawaken.client.animation.MushroomMonstrosityAnimations.STRAFE,
					ageInTicks);
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {
		monstrosity.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public ModelPart root() {
		return this.root;
	}
}