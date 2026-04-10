package com.k1sak1.goetyawaken.client.model;

import com.Polarice3.Goety.client.render.animation.WightAnimations;
import com.k1sak1.goetyawaken.common.entities.ally.WightServant;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.world.entity.Pose;

@OnlyIn(Dist.CLIENT)
public class WightServantModel extends HierarchicalModel<WightServant> {
        private final ModelPart root;
        private final ModelPart wight;
        private final ModelPart body;
        private final ModelPart head;
        private final ModelPart jaw;
        private final ModelPart rightArm;
        private final ModelPart leftArm;
        private final ModelPart rightLeg;
        private final ModelPart leftLeg;
        private final ModelPart rightLowerLeg;
        private final ModelPart leftLowerLeg;

        public WightServantModel(ModelPart root) {
                this.root = root;
                this.wight = root.getChild("wight");
                this.body = this.wight.getChild("body");
                this.head = body.getChild("head");
                this.jaw = head.getChild("jaw");
                this.rightArm = body.getChild("right_arm");
                this.leftArm = body.getChild("left_arm");
                this.rightLeg = this.wight.getChild("right_leg");
                this.leftLeg = this.wight.getChild("left_leg");
                this.rightLowerLeg = rightLeg.getChild("right_lower_leg");
                this.leftLowerLeg = leftLeg.getChild("left_lower_leg");
        }

        public static LayerDefinition createBodyLayer() {
                MeshDefinition meshdefinition = new MeshDefinition();
                PartDefinition partdefinition = meshdefinition.getRoot();

                PartDefinition wight = partdefinition.addOrReplaceChild("wight", CubeListBuilder.create(),
                                PartPose.offset(0.0F, -6.0F, 0.0F));

                PartDefinition body = wight.addOrReplaceChild("body",
                                CubeListBuilder.create().texOffs(0, 61).addBox(
                                                -5.0F, -8.0F, -3.0F, 10.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));
                PartDefinition body_r1 = body
                                .addOrReplaceChild("body_r1",
                                                CubeListBuilder.create().texOffs(0, 45).addBox(-8.0F, -4.0F, -5.0F,
                                                                16.0F, 8.0F, 8.0F,
                                                                new CubeDeformation(0.0F)),
                                                PartPose.offsetAndRotation(0.0F, -9.0F, 0.0F, 0.2182F, 0.0F, 0.0F));
                PartDefinition body_robe = body.addOrReplaceChild("body_robe", CubeListBuilder.create(),
                                PartPose.offset(0.0F, -7.0F, 0.5F));
                PartDefinition body_r2 = body_robe
                                .addOrReplaceChild("body_r2",
                                                CubeListBuilder.create().texOffs(0, 27).addBox(-8.0F, -6.0F, -5.0F,
                                                                16.0F, 10.0F, 8.0F,
                                                                new CubeDeformation(0.25F)),
                                                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2182F, 0.0F, 0.0F));

                PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create(),
                                PartPose.offset(0.0F, -12.0F, -2.0F));
                PartDefinition top = head.addOrReplaceChild("top",
                                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F,
                                                -6.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));
                PartDefinition robe = top.addOrReplaceChild("robe",
                                CubeListBuilder.create().texOffs(32, 0)
                                                .addBox(-4.0F, -10.0F, -6.0F, 8.0F, 10.0F, 8.0F,
                                                                new CubeDeformation(0.5F))
                                                .texOffs(0, 75)
                                                .addBox(-6.0F, -8.0F, -8.0F, 12.0F, 1.0F, 12.0F,
                                                                new CubeDeformation(0.5F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));
                head.addOrReplaceChild("jaw",
                                CubeListBuilder.create().texOffs(0, 18).addBox(-3.5F, 0.0F, -6.5F, 7.0F, 2.0F,
                                                7.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, -2.0F, 1.0F));

                PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(48, 30)
                                .addBox(-3.0F, -2.0F, -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(-4.0F, -10.0F, 0.0F));
                PartDefinition right_lower = right_arm.addOrReplaceChild("right_lower",
                                CubeListBuilder.create().texOffs(48, 47)
                                                .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 15.0F, 2.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(40, 22)
                                                .addBox(-1.5F, 11.5F, -2.0F, 3.0F, 4.0F, 4.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(50, 18)
                                                .addBox(-1.5F, 15.5F, -2.0F, 3.0F, 4.0F, 4.0F,
                                                                new CubeDeformation(0.0F)),
                                PartPose.offset(-2.0F, 13.0F, 0.0F));

                PartDefinition left_arm = body.addOrReplaceChild("left_arm",
                                CubeListBuilder.create().texOffs(48, 30).mirror()
                                                .addBox(-1.0F, -2.0F, -1.0F, 2.0F, 15.0F, 2.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false),
                                PartPose.offset(6.0F, -10.0F, 0.0F));
                PartDefinition left_lower = left_arm.addOrReplaceChild(
                                "left_lower", CubeListBuilder.create().texOffs(48, 47)
                                                .mirror()
                                                .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 15.0F, 2.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false)
                                                .texOffs(40, 22).mirror()
                                                .addBox(-1.5F, 11.5F, -2.0F, 3.0F, 4.0F, 4.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false).texOffs(50, 18).mirror()
                                                .addBox(-1.5F, 15.5F, -2.0F, 3.0F, 4.0F, 4.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false),
                                PartPose.offset(0.0F, 13.0F, 0.0F));

                PartDefinition right_leg = wight.addOrReplaceChild("right_leg", CubeListBuilder.create()
                                .texOffs(56, 30)
                                .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(-2.0F, 0.0F, 0.0F));
                right_leg.addOrReplaceChild("right_lower_leg",
                                CubeListBuilder.create().texOffs(56, 47).addBox(-1.0F, 0.0F,
                                                -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 15.0F, 0.0F));

                PartDefinition left_leg = wight.addOrReplaceChild("left_leg",
                                CubeListBuilder.create().texOffs(56, 30).mirror()
                                                .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 15.0F, 2.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false),
                                PartPose.offset(2.0F, 0.0F, 0.0F));
                left_leg.addOrReplaceChild("left_lower_leg",
                                CubeListBuilder.create().texOffs(56, 47).mirror()
                                                .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 15.0F, 2.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false),
                                PartPose.offset(0.0F, 15.0F, 0.0F));

                return LayerDefinition.create(meshdefinition, 64, 128);
        }

        @Override
        public void setupAnim(WightServant entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                        float netHeadYaw, float headPitch) {
                this.root().getAllParts().forEach(ModelPart::resetPose);

                if (!entity.isDeadOrDying()) {
                        boolean isCrouching = entity.getPose() == Pose.CROUCHING;
                        this.root().getAllParts().forEach(ModelPart::resetPose);
                        if (isCrouching) {
                                if (entity.idleAnimationState != null) {
                                        this.animate((net.minecraft.world.entity.AnimationState) entity.idleAnimationState,
                                                        WightAnimations.CROUCH, ageInTicks);
                                }
                        } else {
                                if (entity.idleAnimationState != null) {
                                        this.animate((net.minecraft.world.entity.AnimationState) entity.idleAnimationState,
                                                        WightAnimations.IDLE, ageInTicks);
                                }
                        }
                        if (!entity.isMeleeAttacking()) {
                                this.animateHeadLookTarget(netHeadYaw, headPitch);
                                if (isCrouching) {
                                        this.head.xRot -= com.Polarice3.Goety.utils.MathHelper.modelDegrees(75);
                                }
                                if (entity.isClimbing()) {
                                        this.animateWalk(WightAnimations.CLIMB, limbSwing, limbSwingAmount, 2.5F, 1.0F);
                                } else {
                                        this.animateWalk(WightAnimations.WALK, limbSwing, limbSwingAmount, 5.0F, 1.0F);
                                }
                        } else if (!isCrouching) {
                                boolean flag = entity.getFallFlyingTicks() > 4;
                                float f = 1.0F;
                                if (flag) {
                                        f = (float) entity.getDeltaMovement().lengthSqr();
                                        f /= 0.2F;
                                        f *= f * f;
                                }

                                if (f < 1.0F) {
                                        f = 1.0F;
                                }
                                this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f;
                                this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F
                                                * limbSwingAmount / f;
                                if (this.rightLeg.xRot > 0.4F) {
                                        this.rightLeg.xRot = 0.4F;
                                }
                                if (this.leftLeg.xRot > 0.4F) {
                                        this.leftLeg.xRot = 0.4F;
                                }
                                if (this.rightLeg.xRot < -0.4F) {
                                        this.rightLeg.xRot = -0.4F;
                                }
                                if (this.leftLeg.xRot < -0.4F) {
                                        this.leftLeg.xRot = -0.4F;
                                }
                        }
                        if (isCrouching) {
                                if (entity.attackAnimationState != null)
                                        this.animate((net.minecraft.world.entity.AnimationState) entity.attackAnimationState,
                                                        WightAnimations.CROUCH_ATTACK, ageInTicks);
                                if (entity.smashAnimationState != null)
                                        this.animate((net.minecraft.world.entity.AnimationState) entity.smashAnimationState,
                                                        WightAnimations.CROUCH_SMASH, ageInTicks);
                        } else {
                                if (entity.attackAnimationState != null)
                                        this.animate((net.minecraft.world.entity.AnimationState) entity.attackAnimationState,
                                                        WightAnimations.ATTACK, ageInTicks);
                                if (entity.smashAnimationState != null)
                                        this.animate((net.minecraft.world.entity.AnimationState) entity.smashAnimationState,
                                                        WightAnimations.SMASH, ageInTicks);
                        }
                        if (entity.unleashAnimationState != null)
                                this.animate((net.minecraft.world.entity.AnimationState) entity.unleashAnimationState,
                                                WightAnimations.UNLEASH, ageInTicks);
                        if (entity.summonAnimationState != null)
                                this.animate((net.minecraft.world.entity.AnimationState) entity.summonAnimationState,
                                                WightAnimations.SUMMON, ageInTicks);
                        if (entity.superSmashAnimationState != null)
                                this.animate((net.minecraft.world.entity.AnimationState) entity.superSmashAnimationState,
                                                WightAnimations.SUPER_SMASH, ageInTicks);
                } else {
                        this.rightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
                        this.leftArm.xRot = -Mth.cos(ageInTicks * 0.6662F) * 0.25F;
                        this.rightArm.zRot = 2.3561945F;
                        this.leftArm.zRot = -2.3561945F;
                        this.rightLeg.xRot = 0.0F;
                        this.rightLeg.yRot = 0.0F;
                        this.rightLeg.zRot = 0.0F;
                        this.leftLeg.xRot = 0.0F;
                        this.leftLeg.yRot = 0.0F;
                        this.leftLeg.zRot = 0.0F;
                        this.rightLowerLeg.xRot = 0.0F;
                        this.rightLowerLeg.yRot = 0.0F;
                        this.rightLowerLeg.zRot = 0.0F;
                        this.leftLowerLeg.xRot = 0.0F;
                        this.leftLowerLeg.yRot = 0.0F;
                        this.leftLowerLeg.zRot = 0.0F;
                        this.head.xRot = -com.Polarice3.Goety.utils.MathHelper.modelDegrees(45.0F);
                        this.head.yRot = this.body.yRot;
                        this.jaw.xRot = com.Polarice3.Goety.utils.MathHelper.modelDegrees(25.0F);
                }
        }

        @Override
        public ModelPart root() {
                return this.root;
        }

        private void animateHeadLookTarget(float netHeadYaw, float headPitch) {
                this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
                this.head.xRot = headPitch * ((float) Math.PI / 180F);
        }
}