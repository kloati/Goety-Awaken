package com.k1sak1.goetyawaken.client.model.illager;

import com.Polarice3.Goety.client.render.layer.HierarchicalArmor;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class TowerWitchModel<T extends LivingEntity> extends HierarchicalModel<T>
                implements ArmedModel, HierarchicalArmor {
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
                        new ResourceLocation("goetyawaken", "towerwitchmodel"), "main");
        private final ModelPart root;
        private final ModelPart body;
        private final ModelPart cape;
        private final ModelPart Head;
        private final ModelPart headcape;
        private final ModelPart hat;
        private final ModelPart hat2;
        private final ModelPart hat3;
        private final ModelPart hat4;
        private final ModelPart nose;
        private final ModelPart arms;
        private final ModelPart leg0;
        private final ModelPart leg1;
        protected boolean holdingItem;

        public TowerWitchModel(ModelPart root) {
                super();
                this.root = root;
                this.body = root.getChild("body");
                this.cape = this.body.getChild("cape");
                this.Head = this.body.getChild("Head");
                this.headcape = this.Head.getChild("headcape");
                this.hat = this.Head.getChild("hat");
                this.hat2 = this.hat.getChild("hat2");
                this.hat3 = this.hat2.getChild("hat3");
                this.hat4 = this.hat3.getChild("hat4");
                this.nose = this.Head.getChild("nose");
                this.arms = this.body.getChild("arms");
                this.leg0 = this.body.getChild("leg0");
                this.leg1 = this.body.getChild("leg1");
        }

        public static LayerDefinition createBodyLayer() {
                MeshDefinition meshdefinition = new MeshDefinition();
                PartDefinition partdefinition = meshdefinition.getRoot();

                PartDefinition body = partdefinition.addOrReplaceChild("body",
                                CubeListBuilder.create().texOffs(16, 20)
                                                .addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F,
                                                                new CubeDeformation(0.3F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition cape = body.addOrReplaceChild("cape", CubeListBuilder.create(),
                                PartPose.offset(0.0F, 0.0F, 4.0F));

                PartDefinition cape_r1 = cape.addOrReplaceChild("cape_r1",
                                CubeListBuilder.create().texOffs(36, 107).addBox(-6.0F, 0.0F, -0.1F, 12.0F, 20.0F, 1.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 2.9671F, 0.0F, 3.1416F));

                PartDefinition Head = body.addOrReplaceChild("Head",
                                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F,
                                                -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition headcape = Head.addOrReplaceChild("headcape", CubeListBuilder.create().texOffs(0, 98)
                                .addBox(-4.5F, -0.5F, -9.5F, 9.0F, 11.0F, 9.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, -10.0F, 5.0F));

                PartDefinition headcape_r1 = headcape.addOrReplaceChild("headcape_r1",
                                CubeListBuilder.create().texOffs(27, 99).addBox(-4.5F, 0.0F, 0.0F, 9.0F, 2.0F, 6.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offsetAndRotation(0.0F, -0.5F, -0.5F, -0.829F, 0.0F, 0.0F));

                PartDefinition hat = Head.addOrReplaceChild("hat",
                                CubeListBuilder.create().texOffs(16, 76)
                                                .addBox(-6.0F, -3.0187F, -6.0F, 12.0F, 2.0F, 12.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(16, 63)
                                                .addBox(-6.0F, -1.0187F, -6.0F, 12.0F, 1.0F, 12.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(16, 63).addBox(-6.0F, -0.0187F, -6.0F, 12.0F, 1.0F, 12.0F,
                                                                new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, -8.0313F, 0.0F));

                PartDefinition hat2 = hat.addOrReplaceChild("hat2",
                                CubeListBuilder.create().texOffs(0, 76).addBox(-5.0F, -7.0F,
                                                -5.0F, 7.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(1.75F, 0.0313F, 2.0F));

                PartDefinition hat3 = hat2.addOrReplaceChild("hat3",
                                CubeListBuilder.create().texOffs(0, 87).addBox(-3.25F,
                                                -8.0F, -3.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, -3.0F, 0.0F));

                PartDefinition hat4 = hat3.addOrReplaceChild("hat4",
                                CubeListBuilder.create().texOffs(0, 95).addBox(-1.5F,
                                                -7.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, -3.0F, 0.0F));

                PartDefinition nose = Head.addOrReplaceChild("nose",
                                CubeListBuilder.create().texOffs(24, 0)
                                                .addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 0).addBox(0.0F, 1.0F, -6.9F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(-0.1F)),
                                PartPose.offset(0.0F, -2.0F, 0.0F));

                PartDefinition arms = body.addOrReplaceChild("arms",
                                CubeListBuilder.create().texOffs(44, 22)
                                                .addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(43, 46)
                                                .addBox(-8.4F, -2.5F, -2.0F, 4.0F, 8.0F, 4.0F,
                                                                new CubeDeformation(0.5F))
                                                .texOffs(44, 22).mirror()
                                                .addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                                                .mirror(false)
                                                .texOffs(43, 46).mirror()
                                                .addBox(4.4F, -2.5F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.5F))
                                                .mirror(false)
                                                .texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F,
                                                                new CubeDeformation(0.0F)),
                                PartPose.offsetAndRotation(0.0F, 3.0F, -1.0F, -0.7854F, 0.0F, 0.0F));

                PartDefinition leg0 = body.addOrReplaceChild("leg0",
                                CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F,
                                                -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(-2.0F, 12.0F, 0.0F));

                PartDefinition leg1 = body.addOrReplaceChild("leg1",
                                CubeListBuilder.create().texOffs(0, 22).mirror()
                                                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false),
                                PartPose.offset(2.0F, 12.0F, 0.0F));

                return LayerDefinition.create(meshdefinition, 64, 128);
        }

        @Override
        public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                        float headPitch) {
                float f = 1.0F;
                if (entity.getFallFlyingTicks() > 4) {
                        f = (float) entity.getDeltaMovement().lengthSqr();
                        f = f / 0.2F;
                        f = f * f * f;
                }
                if (f < 1.0F) {
                        f = 1.0F;
                }
                this.cape.xRot = com.Polarice3.Goety.utils.MathHelper.modelDegrees(10.0F)
                                + Mth.abs(Mth.cos(limbSwing * 0.6662F) * 0.7F * limbSwingAmount / f);
                this.leg0.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
                this.leg1.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount * 0.5F;
                this.leg0.yRot = 0.0F;
                this.leg1.yRot = 0.0F;
                this.Head.yRot = netHeadYaw * ((float) Math.PI / 180F);
                this.Head.xRot = headPitch * ((float) Math.PI / 180F);
                this.nose.setPos(0.0F, -2.0F, 0.0F);
                float f1 = 0.01F * (float) (entity.getId() % 10);
                this.nose.xRot = Mth.sin((float) entity.tickCount * f1) * 4.5F * ((float) Math.PI / 180F);
                this.nose.yRot = 0.0F;
                this.nose.zRot = Mth.cos((float) entity.tickCount * f1) * 2.5F * ((float) Math.PI / 180F);
                if (this.holdingItem) {
                        this.nose.setPos(0.0F, 1.0F, -1.5F);
                        this.nose.xRot = -0.9F;
                }
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                        int packedOverlay,
                        float red, float green, float blue, float alpha) {
                body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }

        public ModelPart getNose() {
                return this.nose;
        }

        public ModelPart getHead() {
                return this.Head;
        }

        public void setHoldingItem(boolean p_104075_) {
                this.holdingItem = p_104075_;
        }

        @Override
        public ModelPart root() {
                return this.root;
        }

        @Override
        public void translateToHead(ModelPart part, PoseStack poseStack) {
                this.root.translateAndRotate(poseStack);
                this.Head.translateAndRotate(poseStack);
                part.translateAndRotate(poseStack);
        }

        @Override
        public void translateToChest(ModelPart part, PoseStack poseStack) {
                this.root.translateAndRotate(poseStack);
                this.body.translateAndRotate(poseStack);
                part.translateAndRotate(poseStack);
        }

        @Override
        public void translateToLeg(ModelPart part, PoseStack poseStack) {
                this.root.translateAndRotate(poseStack);
                this.body.translateAndRotate(poseStack);
                part.translateAndRotate(poseStack);
        }

        @Override
        public void translateToArms(ModelPart part, PoseStack poseStack) {
                this.root.translateAndRotate(poseStack);
                this.body.translateAndRotate(poseStack);
                this.arms.translateAndRotate(poseStack);
                part.translateAndRotate(poseStack);
        }

        @Override
        public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
                this.root.translateAndRotate(poseStack);
                this.body.translateAndRotate(poseStack);
                this.arms.translateAndRotate(poseStack);
        }

        @Override
        public Iterable<ModelPart> rightHandArmors() {
                return ImmutableList.of();
        }

        @Override
        public Iterable<ModelPart> leftHandArmors() {
                return ImmutableList.of();
        }

        @Override
        public Iterable<ModelPart> rightLegPartArmors() {
                return ImmutableList.of(this.leg0);
        }

        @Override
        public Iterable<ModelPart> leftLegPartArmors() {
                return ImmutableList.of(this.leg1);
        }

        @Override
        public Iterable<ModelPart> bodyPartArmors() {
                return ImmutableList.of(this.body);
        }

        @Override
        public Iterable<ModelPart> headPartArmors() {
                return ImmutableList.of(this.Head);
        }

}
