package com.k1sak1.goetyawaken.client.model.ally.Integration;

import com.k1sak1.goetyawaken.common.entities.ally.Integration.DameFortunaServant;

import lykrast.meetyourfight.MeetYourFight;
import lykrast.meetyourfight.misc.MYFUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

//Based on https://https://github.com/Lykrast/MeetYourFight, Original by lykrast
public class DameFortunaServantModel extends HumanoidModel<DameFortunaServant> {
	public static final ModelLayerLocation MODEL = new ModelLayerLocation(MeetYourFight.rl("dame_fortuna"), "main");
	public static final ModelLayerLocation MODEL_ARMOR = new ModelLayerLocation(MeetYourFight.rl("dame_fortuna"),
			"armor");
	public static final ModelLayerLocation MODEL_HEAD = new ModelLayerLocation(MeetYourFight.rl("dame_fortuna"),
			"head");

	private static final Pose[] POSE = {
			new Pose(0, 0, 0, 0, 0, 0), // ANIM_IDLE
			new Pose(30, 0, 30, 30, 0, -30), // ANIM_CHIPS_WINDUP
			new Pose(160, 0, 30, 160, 0, -30), // ANIM_CHIPS_LAUNCH
			new Pose(-150, 0, -30, 150, 0, 30), // ANIM_DICE_WINDUP
			new Pose(160, 0, 30, -160, 0, -30), // ANIM_DICE_LAUNCH
			new Pose(-10, 0, -160, 10, 0, 160), // ANIM_SPIN
			new Pose(-10, 0, -95, 10, 0, 95), // ANIM_SPIN_POSE
			new Pose(135, 70, 0, 0, 0, 0), // ANIM_SNAP_PRE
			new Pose(110, 70, 0, 0, 0, 0), // ANIM_SNAP_POST
			new Pose(35, 20, 55, 35, -20, -55), // ANIM_CARD_WAIT
			new Pose(0, 0, -100, 0, 0, -50), // ANIM_FINALE
			new Pose(110, -40, 0, 110, 40, 0) // ANIM_CLAP
	};
	private Pose pose, prevPose;
	private float headProgress, headScale;
	private float animProgress;

	public DameFortunaServantModel(ModelPart modelPart) {
		super(modelPart);
	}

	public static LayerDefinition createBodyLayer(CubeDeformation deform) {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		partdefinition.addOrReplaceChild("head",
				CubeListBuilder.create().texOffs(0, 0).addBox(-4, -4, -4, 8, 8, 8, deform), PartPose.offset(0, -8, 0));
		partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F,
				8.0F, 8.0F, 8.0F, deform.extend(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		partdefinition.addOrReplaceChild("body",
				CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, deform),
				PartPose.offset(0.0F, 0.0F, 0.0F));
		partdefinition.addOrReplaceChild("right_arm",
				CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deform),
				PartPose.offset(-5.0F, 2.0F, 0.0F));
		partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F,
				-2.0F, -2.0F, 4.0F, 12.0F, 4.0F, deform), PartPose.offset(5.0F, 2.0F, 0.0F));
		partdefinition.addOrReplaceChild("right_leg",
				CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, deform),
				PartPose.offset(-1.9F, 12.0F, 0.0F));
		partdefinition.addOrReplaceChild("left_leg",
				CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, deform),
				PartPose.offset(1.9F, 12.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void prepareMobModel(DameFortunaServant entity, float limbSwing, float limbSwingAmount, float partialTick) {
		super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
		headProgress = entity.getHeadRotationProgress(partialTick);
		animProgress = entity.getAnimProgress(partialTick);
		animProgress = MYFUtils.easeOutQuad(animProgress);
		pose = POSE[entity.clientAnim];
		prevPose = POSE[entity.prevAnim];
		if (entity.headRegrowTime > 0)
			headScale = MYFUtils.easeInOut((10 - entity.headRegrowTime + partialTick) / 10f);
		else
			headScale = 1;
	}

	@Override
	public void setupAnim(DameFortunaServant entity, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) {

		float headX = head.xRot;
		float headY = head.yRot;
		float headZ = head.zRot;

		crouching = (entity.clientAnim == DameFortunaServant.ANIM_FINALE);
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		hat.skipDraw = true;

		head.y = -8 + Mth.sin(ageInTicks * (float) Math.PI / 50f);

		head.xRot = rotlerpRad(headProgress, headX, entity.headTargetPitch * Mth.HALF_PI);
		head.yRot = rotlerpRad(headProgress, headY, entity.headTargetYaw * Mth.HALF_PI);
		head.zRot = rotlerpRad(headProgress, headZ, entity.headTargetRoll * Mth.HALF_PI);

		head.xScale = headScale;
		head.yScale = headScale;
		head.zScale = headScale;

		if (animProgress > 0.99) {

			if (pose.usesLeft) {
				leftArm.xRot = pose.leftX;
				leftArm.yRot = pose.leftY;
				leftArm.zRot = pose.leftZ;
			}
			if (pose.usesRight) {
				rightArm.xRot = pose.rightX;
				rightArm.yRot = pose.rightY;
				rightArm.zRot = pose.rightZ;
			}

			if (entity.clientAnim == DameFortunaServant.ANIM_DICE_WINDUP)
				head.y = -8;
		} else {

			if (pose.usesLeft) {
				float oldx, oldy, oldz;
				if (prevPose.usesLeft) {
					oldx = prevPose.leftX;
					oldy = prevPose.leftY;
					oldz = prevPose.leftZ;
				} else {
					oldx = leftArm.xRot;
					oldy = leftArm.yRot;
					oldz = leftArm.zRot;
				}
				leftArm.xRot = rotlerpRad(animProgress, oldx, pose.leftX);
				leftArm.yRot = rotlerpRad(animProgress, oldy, pose.leftY);
				leftArm.zRot = rotlerpRad(animProgress, oldz, pose.leftZ);
			} else if (prevPose.usesLeft) {
				leftArm.xRot = rotlerpRad(animProgress, prevPose.leftX, leftArm.xRot);
				leftArm.yRot = rotlerpRad(animProgress, prevPose.leftY, leftArm.yRot);
				leftArm.zRot = rotlerpRad(animProgress, prevPose.leftZ, leftArm.zRot);
			}

			if (pose.usesRight) {
				float oldx, oldy, oldz;
				if (prevPose.usesRight) {
					oldx = prevPose.rightX;
					oldy = prevPose.rightY;
					oldz = prevPose.rightZ;
				} else {
					oldx = rightArm.xRot;
					oldy = rightArm.yRot;
					oldz = rightArm.zRot;
				}
				rightArm.xRot = rotlerpRad(animProgress, oldx, pose.rightX);
				rightArm.yRot = rotlerpRad(animProgress, oldy, pose.rightY);
				rightArm.zRot = rotlerpRad(animProgress, oldz, pose.rightZ);
			} else if (prevPose.usesLeft) {
				rightArm.xRot = rotlerpRad(animProgress, prevPose.rightX, rightArm.xRot);
				rightArm.yRot = rotlerpRad(animProgress, prevPose.rightY, rightArm.yRot);
				rightArm.zRot = rotlerpRad(animProgress, prevPose.rightZ, rightArm.zRot);
			}

			if (entity.clientAnim == DameFortunaServant.ANIM_DICE_WINDUP)
				head.y = Mth.lerp(animProgress, head.y, -8);
			else if (entity.prevAnim == DameFortunaServant.ANIM_DICE_WINDUP)
				head.y = Mth.lerp(animProgress, -8, head.y);
		}
	}

	private static class Pose {
		public final float leftX, leftY, leftZ, rightX, rightY, rightZ;
		public final boolean usesLeft, usesRight;

		public Pose(float leftX, float leftY, float leftZ, float rightX, float rightY, float rightZ) {

			this.leftX = leftX * -Mth.DEG_TO_RAD;
			this.leftY = leftY * -Mth.DEG_TO_RAD;
			this.leftZ = leftZ * Mth.DEG_TO_RAD;
			this.rightX = rightX * -Mth.DEG_TO_RAD;
			this.rightY = rightY * -Mth.DEG_TO_RAD;
			this.rightZ = rightZ * Mth.DEG_TO_RAD;
			usesLeft = !(leftX == 0 && leftY == 0 && leftZ == 0);
			usesRight = !(rightX == 0 && rightY == 0 && rightZ == 0);
		}
	}

}
