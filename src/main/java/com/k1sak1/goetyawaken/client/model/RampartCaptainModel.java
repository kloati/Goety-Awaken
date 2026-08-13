package com.k1sak1.goetyawaken.client.model;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.entities.ally.illager.RampartCaptain;
import com.k1sak1.goetyawaken.common.entities.hostile.illager.HostileRampartCaptain.HostileRampartCaptain;
import com.k1sak1.goetyawaken.client.animation.RampartCaptainAnimation;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class RampartCaptainModel<T extends LivingEntity> extends HierarchicalModel<T>
                implements HierarchicalArmor, ArmedModel {
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "rampart_captain"), "main");
        private final ModelPart full;
        private final ModelPart body;
        private final ModelPart head;
        private final ModelPart r_eye;
        private final ModelPart r_eye_white;
        private final ModelPart r_pupil;
        private final ModelPart l_eye;
        private final ModelPart l_eye_white;
        private final ModelPart l_pupil;
        private final ModelPart right_brow;
        private final ModelPart left_brow;
        private final ModelPart nose;
        private final ModelPart right_arm;
        private final ModelPart diamond_ice_axe;
        private final ModelPart left_arm;
        private final ModelPart horn_of_commanding;
        private final ModelPart banner;
        private final ModelPart right_leg;
        private final ModelPart left_leg;

        public RampartCaptainModel(ModelPart root) {
                this.full = root.getChild("full");
                this.body = this.full.getChild("body");
                this.head = this.body.getChild("head");
                this.r_eye = this.head.getChild("r_eye");
                this.r_eye_white = this.r_eye.getChild("r_eye_white");
                this.r_pupil = this.r_eye.getChild("r_pupil");
                this.l_eye = this.head.getChild("l_eye");
                this.l_eye_white = this.l_eye.getChild("l_eye_white");
                this.l_pupil = this.l_eye.getChild("l_pupil");
                this.right_brow = this.head.getChild("right_brow");
                this.left_brow = this.head.getChild("left_brow");
                this.nose = this.head.getChild("nose");
                this.right_arm = this.body.getChild("right_arm");
                this.diamond_ice_axe = this.right_arm.getChild("diamond_ice_axe");
                this.left_arm = this.body.getChild("left_arm");
                this.horn_of_commanding = this.left_arm.getChild("horn_of_commanding");
                this.banner = this.head.getChild("banner");
                this.right_leg = this.full.getChild("right_leg");
                this.left_leg = this.full.getChild("left_leg");
        }

        public static LayerDefinition createBodyLayer() {
                MeshDefinition meshdefinition = new MeshDefinition();
                PartDefinition partdefinition = meshdefinition.getRoot();

                PartDefinition full = partdefinition.addOrReplaceChild("full", CubeListBuilder.create(),
                                PartPose.offset(0.0F, 24.0F, 0.0F));

                PartDefinition body = full.addOrReplaceChild("body", CubeListBuilder.create().texOffs(28, 18)
                                .addBox(-4.0F, -12.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 18).addBox(-4.0F, -12.0F, -3.0F, 8.0F, 13.0F, 6.0F,
                                                new CubeDeformation(0.5F)),
                                PartPose.offset(0.0F, -12.0F, 0.0F));

                PartDefinition head = body
                                .addOrReplaceChild("head",
                                                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F,
                                                                8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)),
                                                PartPose.offset(0.0F, -12.0F, 0.0F));

                PartDefinition r_eye = head.addOrReplaceChild("r_eye", CubeListBuilder.create(),
                                PartPose.offset(-1.0F, -3.0F, -4.0F));

                PartDefinition r_eye_white = r_eye.addOrReplaceChild(
                                "r_eye_white", CubeListBuilder.create().texOffs(50, 23).addBox(-1.0F, -1.0F, -0.002F,
                                                2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(-1.0F, 0.0F, 0.0F));

                PartDefinition r_pupil = r_eye.addOrReplaceChild(
                                "r_pupil", CubeListBuilder.create().texOffs(0, 52).addBox(-1.0F, -1.0F, -0.004F, 1.0F,
                                                1.0F, 0.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition l_eye = head.addOrReplaceChild("l_eye", CubeListBuilder.create(),
                                PartPose.offset(1.0F, -3.0F, -4.0F));

                PartDefinition l_eye_white = l_eye.addOrReplaceChild("l_eye_white",
                                CubeListBuilder.create().texOffs(50, 23).mirror()
                                                .addBox(-1.0F, -1.0F, -0.002F, 2.0F, 1.0F, 0.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false),
                                PartPose.offset(1.0F, 0.0F, 0.0F));

                PartDefinition l_pupil = l_eye.addOrReplaceChild("l_pupil",
                                CubeListBuilder.create().texOffs(0, 52).mirror()
                                                .addBox(0.0F, -1.0F, -0.004F, 1.0F, 1.0F, 0.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition right_brow = head.addOrReplaceChild("right_brow", CubeListBuilder.create()
                                .texOffs(43, 17)
                                .addBox(-3.0F, -1.0F, -0.006F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 51).addBox(-3.0F, -2.0F, -0.006F, 2.0F, 1.0F, 0.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offset(-1.0F, -4.0F, -4.0F));

                PartDefinition left_brow = head.addOrReplaceChild("left_brow", CubeListBuilder.create().texOffs(43, 17)
                                .mirror().addBox(0.0F, -1.0F, -0.006F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                                .mirror(false)
                                .texOffs(0, 51).mirror()
                                .addBox(1.0F, -2.0F, -0.006F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                                .mirror(false), PartPose.offset(1.0F, -4.0F, -4.0F));

                PartDefinition nose = head
                                .addOrReplaceChild("nose",
                                                CubeListBuilder.create().texOffs(20, 37).addBox(-1.0F, -1.0F, -2.0F,
                                                                2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
                                                PartPose.offset(0.0F, -2.0F, -4.0F));

                PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(32, 0)
                                .mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                                .mirror(false)
                                .texOffs(44, 46).mirror()
                                .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false)
                                .texOffs(0, 37).addBox(-3.0F, -2.0F, -3.0F, 4.0F, 5.0F, 6.0F,
                                                new CubeDeformation(0.3F)),
                                PartPose.offset(-5.0F, -10.0F, 0.0F));

                PartDefinition diamond_ice_axe = right_arm.addOrReplaceChild("diamond_ice_axe", CubeListBuilder.create()
                                .texOffs(0, 60)
                                .addBox(-3.0F, -11.0F, -1.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 62)
                                .addBox(-4.0F, -10.0F, -1.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(10, 57)
                                .addBox(-5.0F, -9.0F, -1.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(10, 63)
                                .addBox(-9.0F, -9.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 53).addBox(-7.0F, -8.0F, -1.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 55).addBox(-8.0F, -7.0F, -1.0F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 57).addBox(-8.0F, -6.0F, -1.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(4, 64).addBox(-4.0F, -5.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(12, 53)
                                .addBox(-9.0F, -5.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(8, 66).addBox(-3.0F, -4.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(16, 53)
                                .addBox(-5.0F, -4.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(16, 55)
                                .addBox(-8.0F, -4.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 69)
                                .addBox(-10.0F, -4.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(12, 66)
                                .addBox(-2.0F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(12, 66)
                                .addBox(-1.0F, -2.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(12, 66).addBox(0.0F, -1.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(4, 68).addBox(1.0F, 0.0F, -1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 64).addBox(2.0F, 1.0F, -1.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(10, 59).addBox(3.0F, 2.0F, -1.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
                                PartPose.offsetAndRotation(0.0F, 8.0F, 0.0F, 1.5708F, -0.7854F, 1.5708F));

                PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 0)
                                .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                                .texOffs(44, 46)
                                .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.25F))
                                .texOffs(0, 37).mirror()
                                .addBox(-1.0F, -2.0F, -3.0F, 4.0F, 5.0F, 6.0F, new CubeDeformation(0.3F)).mirror(false),
                                PartPose.offset(5.0F, -10.0F, 0.0F));

                PartDefinition horn_of_commanding = left_arm.addOrReplaceChild("horn_of_commanding", CubeListBuilder
                                .create().texOffs(20, 53)
                                .addBox(1.0F, -9.0F, 0.0F, 5.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(24, 67).addBox(6.0F, -8.0F, 0.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(20, 62).addBox(0.0F, -8.0F, 0.0F, 1.0F, 11.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(32, 55).addBox(-7.0F, -4.0F, 0.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(32, 60).addBox(-1.0F, -3.0F, 0.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(20, 74).addBox(-5.0F, -3.0F, 0.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(24, 62).addBox(-4.0F, -2.0F, 0.0F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(32, 53).addBox(1.0F, -1.0F, 0.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(28, 67).addBox(1.0F, 0.0F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(28, 69).addBox(-6.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(36, 60).addBox(1.0F, 1.0F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(24, 73).addBox(-3.0F, 2.0F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                                PartPose.offsetAndRotation(0.0F, 9.0F, 0.0F, 0.0F, -1.5708F, -3.1416F));

                PartDefinition banner = head.addOrReplaceChild("banner", CubeListBuilder.create().texOffs(120, 0)
                                .addBox(-1.0F, -20.0F, -1.0F, 2.0F, 22.0F, 2.0F, new CubeDeformation(0.0F))
                                .texOffs(68, 0)
                                .addBox(-10.0F, -60.0F, -3.0F, 20.0F, 40.0F, 2.0F, new CubeDeformation(0.0F))
                                .texOffs(112, 0)
                                .addBox(-1.0F, -58.0F, -1.0F, 2.0F, 36.0F, 2.0F, new CubeDeformation(0.0F))
                                .texOffs(68, 42)
                                .addBox(-10.0F, -22.0F, -1.0F, 20.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                                .texOffs(68, 42).addBox(-10.0F, -60.0F, -1.0F, 20.0F, 2.0F, 2.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, -5.0F, 4.0F));

                PartDefinition right_leg = full.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(28, 36)
                                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 48).addBox(-2.0F, 5.0F, -3.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.3F))
                                .texOffs(44, 36).addBox(-2.0F, 6.0F, -2.0F, 4.0F, 6.0F, 4.0F,
                                                new CubeDeformation(0.3F)),
                                PartPose.offset(-2.0F, -12.0F, 0.0F));

                PartDefinition left_leg = full.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(28, 36)
                                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 48).addBox(-2.0F, 5.0F, -3.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.3F))
                                .texOffs(44, 36).addBox(-2.0F, 6.0F, -2.0F, 4.0F, 6.0F, 4.0F,
                                                new CubeDeformation(0.3F)),
                                PartPose.offset(2.0F, -12.0F, 0.0F));

                return LayerDefinition.create(meshdefinition, 128, 128);
        }

        @Override
        public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                        float headPitch) {
                this.root().getAllParts().forEach(ModelPart::resetPose);
                if (entity instanceof HostileRampartCaptain hostilecaptain) {
                        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
                        this.head.xRot = headPitch * ((float) Math.PI / 180F);
                        this.animate(hostilecaptain.idleAnimationState, RampartCaptainAnimation.IDLE, ageInTicks, 1.0F);
                        this.animate(hostilecaptain.alertAnimationState, RampartCaptainAnimation.ALERT, ageInTicks,
                                        1.0F);
                        this.animate(hostilecaptain.runAnimationState, RampartCaptainAnimation.RUN, ageInTicks, 1.0F);
                        this.animate(hostilecaptain.attack1AnimationState, RampartCaptainAnimation.ATTACK1, ageInTicks,
                                        1.0F);
                        this.animate(hostilecaptain.attack2AnimationState, RampartCaptainAnimation.ATTACK2, ageInTicks,
                                        1.0F);
                        this.animate(hostilecaptain.throwAnimationState, RampartCaptainAnimation.THROW, ageInTicks,
                                        1.0F);
                        this.animate(hostilecaptain.summonAnimationState, RampartCaptainAnimation.SUMMON, ageInTicks,
                                        1.0F);
                        this.animate(hostilecaptain.windhornAnimationState, RampartCaptainAnimation.WINDHORN,
                                        ageInTicks,
                                        1.0F);
                        this.animate(hostilecaptain.runAttackAnimationState, RampartCaptainAnimation.RUNATTACK,
                                        ageInTicks,
                                        1.0F);
                        this.animate(hostilecaptain.bannerAnimationState, RampartCaptainAnimation.BANNERSCALE,
                                        ageInTicks,
                                        1.0F);

                        boolean blockWalk = hostilecaptain.alertAnimationState.isStarted()
                                        || hostilecaptain.runAnimationState.isStarted()
                                        || hostilecaptain.attack1AnimationState.isStarted()
                                        || hostilecaptain.attack2AnimationState.isStarted()
                                        || hostilecaptain.throwAnimationState.isStarted()
                                        || hostilecaptain.summonAnimationState.isStarted()
                                        || hostilecaptain.windhornAnimationState.isStarted()
                                        || hostilecaptain.runAttackAnimationState.isStarted();
                        if (!blockWalk) {
                                this.animateWalk(RampartCaptainAnimation.WALK, limbSwing, limbSwingAmount,
                                                2.0F, 2.0F);
                        }
                }
                if (entity instanceof RampartCaptain captain) {
                        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
                        this.head.xRot = headPitch * ((float) Math.PI / 180F);
                        this.animate(captain.idleAnimationState, RampartCaptainAnimation.IDLE, ageInTicks, 1.0F);
                        this.animate(captain.alertAnimationState, RampartCaptainAnimation.ALERT, ageInTicks, 1.0F);
                        this.animate(captain.runAnimationState, RampartCaptainAnimation.RUN, ageInTicks, 1.0F);
                        this.animate(captain.attack1AnimationState, RampartCaptainAnimation.ATTACK1, ageInTicks, 1.0F);
                        this.animate(captain.attack2AnimationState, RampartCaptainAnimation.ATTACK2, ageInTicks, 1.0F);
                        this.animate(captain.throwAnimationState, RampartCaptainAnimation.THROW, ageInTicks, 1.0F);
                        this.animate(captain.summonAnimationState, RampartCaptainAnimation.SUMMON, ageInTicks, 1.0F);
                        this.animate(captain.windhornAnimationState, RampartCaptainAnimation.WINDHORN, ageInTicks,
                                        1.0F);
                        this.animate(captain.runAttackAnimationState, RampartCaptainAnimation.RUNATTACK, ageInTicks,
                                        1.0F);
                        this.animate(captain.bannerAnimationState, RampartCaptainAnimation.BANNERSCALE, ageInTicks,
                                        1.0F);

                        boolean blockWalk = captain.alertAnimationState.isStarted()
                                        || captain.runAnimationState.isStarted()
                                        || captain.attack1AnimationState.isStarted()
                                        || captain.attack2AnimationState.isStarted()
                                        || captain.throwAnimationState.isStarted()
                                        || captain.summonAnimationState.isStarted()
                                        || captain.windhornAnimationState.isStarted()
                                        || captain.runAttackAnimationState.isStarted();
                        if (!blockWalk) {
                                this.animateWalk(RampartCaptainAnimation.WALK, limbSwing, limbSwingAmount,
                                                2.0F, 2.0F);
                        }
                }
        }

        @Override
        public void translateToHead(ModelPart modelPart, PoseStack poseStack) {
                modelPart.translateAndRotate(poseStack);
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

        private ModelPart getArm(HumanoidArm arm) {
                return arm == HumanoidArm.LEFT ? this.left_arm : this.right_arm;
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                        int packedOverlay,
                        float red, float green, float blue, float alpha) {
                full.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }

        public ModelPart root() {
                return this.full;
        }

        public ModelPart getRightArm() {
                return this.right_arm;
        }
}
