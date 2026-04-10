package com.k1sak1.goetyawaken.client.model;

import com.k1sak1.goetyawaken.client.animation.WardenAnimation;
import com.k1sak1.goetyawaken.common.entities.ally.WardenServant;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class WardenServantModel extends net.minecraft.client.model.HierarchicalModel<WardenServant> {
        public final ModelPart root;
        public final ModelPart bone;
        public final ModelPart body;
        public final ModelPart head;
        public final ModelPart rightTendril;
        public final ModelPart leftTendril;
        public final ModelPart leftLeg;
        public final ModelPart leftArm;
        public final ModelPart leftRibcage;
        public final ModelPart rightArm;
        public final ModelPart rightLeg;
        public final ModelPart rightRibcage;
        private final List<ModelPart> tendrilsLayerModelParts;
        private final List<ModelPart> heartLayerModelParts;
        private final List<ModelPart> bioluminescentLayerModelParts;
        private final List<ModelPart> pulsatingSpotsLayerModelParts;

        public WardenServantModel(ModelPart pRoot) {
                this.root = pRoot;
                this.bone = pRoot.getChild("bone");
                this.body = this.bone.getChild("body");
                this.head = this.body.getChild("head");
                this.rightLeg = this.bone.getChild("right_leg");
                this.leftLeg = this.bone.getChild("left_leg");
                this.rightArm = this.body.getChild("right_arm");
                this.leftArm = this.body.getChild("left_arm");
                this.rightTendril = this.head.getChild("right_tendril");
                this.leftTendril = this.head.getChild("left_tendril");
                this.rightRibcage = this.body.getChild("right_ribcage");
                this.leftRibcage = this.body.getChild("left_ribcage");
                this.tendrilsLayerModelParts = ImmutableList.of(this.leftTendril, this.rightTendril);
                this.heartLayerModelParts = ImmutableList.of(this.body);
                this.bioluminescentLayerModelParts = ImmutableList.of(this.head, this.leftArm, this.rightArm,
                                this.leftLeg, this.rightLeg);
                this.pulsatingSpotsLayerModelParts = ImmutableList.of(this.body, this.head, this.leftArm, this.rightArm,
                                this.leftLeg, this.rightLeg);
        }

        public static LayerDefinition createBodyLayer() {
                MeshDefinition meshdefinition = new MeshDefinition();
                PartDefinition partdefinition = meshdefinition.getRoot();
                PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(),
                                net.minecraft.client.model.geom.PartPose.offset(0.0F, 24.0F, 0.0F));
                PartDefinition partdefinition2 = partdefinition1.addOrReplaceChild("body",
                                CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -13.0F, -4.0F, 18.0F, 21.0F,
                                                11.0F),
                                net.minecraft.client.model.geom.PartPose.offset(0.0F, -21.0F, 0.0F));
                partdefinition2.addOrReplaceChild("right_ribcage",
                                CubeListBuilder.create().texOffs(90, 11).addBox(-2.0F, -11.0F, -0.1F, 9.0F, 21.0F,
                                                0.0F),
                                net.minecraft.client.model.geom.PartPose.offset(-7.0F, -2.0F, -4.0F));
                partdefinition2.addOrReplaceChild("left_ribcage",
                                CubeListBuilder.create().texOffs(90, 11).mirror()
                                                .addBox(-7.0F, -11.0F, -0.1F, 9.0F, 21.0F, 0.0F).mirror(false),
                                net.minecraft.client.model.geom.PartPose.offset(7.0F, -2.0F, -4.0F));
                PartDefinition partdefinition3 = partdefinition2.addOrReplaceChild("head",
                                CubeListBuilder.create().texOffs(0, 32).addBox(-8.0F, -16.0F, -5.0F, 16.0F, 16.0F,
                                                10.0F),
                                net.minecraft.client.model.geom.PartPose.offset(0.0F, -13.0F, 0.0F));
                partdefinition3.addOrReplaceChild("right_tendril",
                                CubeListBuilder.create().texOffs(52, 32).addBox(-16.0F, -13.0F, 0.0F, 16.0F, 16.0F,
                                                0.0F),
                                net.minecraft.client.model.geom.PartPose.offset(-8.0F, -12.0F, 0.0F));
                partdefinition3.addOrReplaceChild("left_tendril",
                                CubeListBuilder.create().texOffs(58, 0).addBox(0.0F, -13.0F, 0.0F, 16.0F, 16.0F, 0.0F),
                                net.minecraft.client.model.geom.PartPose.offset(8.0F, -12.0F, 0.0F));
                partdefinition2.addOrReplaceChild("right_arm",
                                CubeListBuilder.create().texOffs(44, 50).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 28.0F, 8.0F),
                                net.minecraft.client.model.geom.PartPose.offset(-13.0F, -13.0F, 1.0F));
                partdefinition2.addOrReplaceChild("left_arm",
                                CubeListBuilder.create().texOffs(0, 58).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 28.0F, 8.0F),
                                net.minecraft.client.model.geom.PartPose.offset(13.0F, -13.0F, 1.0F));
                partdefinition1.addOrReplaceChild("right_leg",
                                CubeListBuilder.create().texOffs(76, 48).addBox(-3.1F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F),
                                net.minecraft.client.model.geom.PartPose.offset(-5.9F, -13.0F, 0.0F));
                partdefinition1.addOrReplaceChild("left_leg",
                                CubeListBuilder.create().texOffs(76, 76).addBox(-2.9F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F),
                                net.minecraft.client.model.geom.PartPose.offset(5.9F, -13.0F, 0.0F));
                return LayerDefinition.create(meshdefinition, 128, 128);
        }

        @Override
        public void setupAnim(WardenServant entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                        float netHeadYaw, float headPitch) {
                this.root().getAllParts().forEach(ModelPart::resetPose);
                float f = ageInTicks - (float) entity.tickCount;
                this.animateHeadLookTarget(netHeadYaw, headPitch);
                this.animateWalk(limbSwing, limbSwingAmount);
                this.animateIdlePose(ageInTicks);
                this.animateTendrils(entity, ageInTicks, f);
                this.animateHeart(entity, ageInTicks, f);
                this.animate(entity.attackAnimationState, WardenAnimation.WARDEN_ATTACK, ageInTicks);
                this.animate(entity.sonicBoomAnimationState, WardenAnimation.WARDEN_SONIC_BOOM, ageInTicks);
                this.animate(entity.diggingAnimationState, WardenAnimation.WARDEN_DIG, ageInTicks);
                this.animate(entity.emergeAnimationState, WardenAnimation.WARDEN_EMERGE, ageInTicks);
                this.animate(entity.roarAnimationState, WardenAnimation.WARDEN_ROAR, ageInTicks);
                this.animate(entity.sniffAnimationState, WardenAnimation.WARDEN_SNIFF, ageInTicks);
        }

        private void animateHeadLookTarget(float yaw, float pitch) {
                this.head.xRot = pitch * ((float) Math.PI / 180F);
                this.head.yRot = yaw * ((float) Math.PI / 180F);
        }

        private void animateIdlePose(float ageInTicks) {
                float f = ageInTicks * 0.1F;
                float f1 = Mth.cos(f);
                float f2 = Mth.sin(f);
                this.head.zRot += 0.06F * f1;
                this.head.xRot += 0.06F * f2;
                this.body.zRot += 0.025F * f2;
                this.body.xRot += 0.025F * f1;
        }

        private void animateWalk(float limbSwing, float limbSwingAmount) {
                float f = Math.min(0.5F, 3.0F * limbSwingAmount);
                float f1 = limbSwing * 0.8662F;
                float f2 = Mth.cos(f1);
                float f3 = Mth.sin(f1);
                float f4 = Math.min(0.35F, f);
                this.head.zRot += 0.3F * f3 * f;
                this.head.xRot += 1.2F * Mth.cos(f1 + ((float) Math.PI / 2F)) * f4;
                this.body.zRot = 0.1F * f3 * f;
                this.body.xRot = 1.0F * f2 * f4;
                this.leftLeg.xRot = 1.0F * f2 * f;
                this.rightLeg.xRot = 1.0F * Mth.cos(f1 + (float) Math.PI) * f;
                this.leftArm.xRot = -(0.8F * f2 * f);
                this.leftArm.zRot = 0.0F;
                this.rightArm.xRot = -(0.8F * f3 * f);
                this.rightArm.zRot = 0.0F;
                this.resetArmPoses();
        }

        private void resetArmPoses() {
                this.leftArm.yRot = 0.0F;
                this.leftArm.z = 1.0F;
                this.leftArm.x = 13.0F;
                this.leftArm.y = -13.0F;
                this.rightArm.yRot = 0.0F;
                this.rightArm.z = 1.0F;
                this.rightArm.x = -13.0F;
                this.rightArm.y = -13.0F;
        }

        private void animateTendrils(WardenServant entity, float ageInTicks, float partialTick) {
                float f = entity.getTendrilAnimation(partialTick)
                                * (float) (Math.cos((double) ageInTicks * 2.25D) * Math.PI * (double) 0.1F);
                this.leftTendril.xRot = f;
                this.rightTendril.xRot = -f;
        }

        private void animateHeart(WardenServant entity, float ageInTicks, float partialTick) {
                float f = entity.getHeartAnimation(partialTick);
                this.body.xRot += 0.05F * f;
                this.head.xRot += 0.05F * f;
        }

        @Override
        public ModelPart root() {
                return this.root;
        }

        public List<ModelPart> getTendrilsLayerModelParts() {
                return this.tendrilsLayerModelParts;
        }

        public List<ModelPart> getHeartLayerModelParts() {
                return this.heartLayerModelParts;
        }

        public List<ModelPart> getBioluminescentLayerModelParts() {
                return this.bioluminescentLayerModelParts;
        }

        public List<ModelPart> getPulsatingSpotsLayerModelParts() {
                return this.pulsatingSpotsLayerModelParts;
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                        int packedOverlay, float red, float green, float blue, float alpha) {
                this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }
}