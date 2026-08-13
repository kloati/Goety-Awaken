package com.k1sak1.goetyawaken.client.model;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.animation.GiantGhastAnimation;
import com.k1sak1.goetyawaken.client.animation.GiantGhastAnimation2;
import com.k1sak1.goetyawaken.common.entities.hostile.GiantGhast;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class GiantGhastModel<T extends GiantGhast> extends HierarchicalModel<T> {
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "giant_ghast"), "main");
        private final ModelPart root;
        private final ModelPart body_main;
        private final ModelPart ghast;
        private final ModelPart body;
        private final ModelPart tentacles;
        private final ModelPart[] tentacleArray = new ModelPart[9];
        private final ModelPart tentacles_0;
        private final ModelPart tentacles_1;
        private final ModelPart tentacles_2;
        private final ModelPart tentacles_3;
        private final ModelPart tentacles_4;
        private final ModelPart tentacles_5;
        private final ModelPart tentacles_6;
        private final ModelPart tentacles_7;
        private final ModelPart tentacles_8;
        private final ModelPart Crown;
        private final ModelPart eye;
        private final ModelPart eye_main1;
        private final ModelPart eye1;
        private final ModelPart eye2;
        private final ModelPart eye_main2;
        private final ModelPart eye3;
        private final ModelPart eye4;
        private final ModelPart mouth;

        public GiantGhastModel(ModelPart root) {
                this.root = root.getChild("root");
                this.body_main = this.root.getChild("body_main");
                this.ghast = this.body_main.getChild("ghast");
                this.body = this.ghast.getChild("body");
                this.tentacles = this.body.getChild("tentacles");
                this.tentacles_0 = this.tentacles.getChild("tentacles_0");
                this.tentacles_1 = this.tentacles.getChild("tentacles_1");
                this.tentacles_2 = this.tentacles.getChild("tentacles_2");
                this.tentacles_3 = this.tentacles.getChild("tentacles_3");
                this.tentacles_4 = this.tentacles.getChild("tentacles_4");
                this.tentacles_5 = this.tentacles.getChild("tentacles_5");
                this.tentacles_6 = this.tentacles.getChild("tentacles_6");
                this.tentacles_7 = this.tentacles.getChild("tentacles_7");
                this.tentacles_8 = this.tentacles.getChild("tentacles_8");
                this.tentacleArray[0] = this.tentacles_0;
                this.tentacleArray[1] = this.tentacles_1;
                this.tentacleArray[2] = this.tentacles_2;
                this.tentacleArray[3] = this.tentacles_3;
                this.tentacleArray[4] = this.tentacles_4;
                this.tentacleArray[5] = this.tentacles_5;
                this.tentacleArray[6] = this.tentacles_6;
                this.tentacleArray[7] = this.tentacles_7;
                this.tentacleArray[8] = this.tentacles_8;

                this.Crown = this.body.getChild("Crown");
                this.eye = this.body.getChild("eye");
                this.eye_main1 = this.eye.getChild("eye_main1");
                this.eye1 = this.eye_main1.getChild("eye1");
                this.eye2 = this.eye_main1.getChild("eye2");
                this.eye_main2 = this.eye.getChild("eye_main2");
                this.eye3 = this.eye_main2.getChild("eye3");
                this.eye4 = this.eye_main2.getChild("eye4");
                this.mouth = this.body.getChild("mouth");
        }

        public static LayerDefinition createBodyLayer() {
                MeshDefinition meshdefinition = new MeshDefinition();
                PartDefinition partdefinition = meshdefinition.getRoot();

                PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(),
                                PartPose.offset(0.0F, -23.0F, 0.0F));

                PartDefinition body_main = root.addOrReplaceChild("body_main", CubeListBuilder.create(),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition ghast = body_main.addOrReplaceChild("ghast", CubeListBuilder.create(),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition body = ghast.addOrReplaceChild(
                                "body", CubeListBuilder.create().texOffs(0, 0).addBox(-16.0F, -32.0F, -16.0F, 32.0F,
                                                32.0F, 32.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 24.0F, 0.0F));

                PartDefinition body_1_r1 = body.addOrReplaceChild("body_1_r1",
                                CubeListBuilder.create().texOffs(0, 120).addBox(-16.0F, -16.0F, -3.5F, 32.0F, 32.0F,
                                                10.0F, new CubeDeformation(-8.0F)),
                                PartPose.offsetAndRotation(-15.75F, -21.75F, -5.75F, 0.0F, -1.5708F, 0.0F));

                PartDefinition body_2_r1 = body.addOrReplaceChild("body_2_r1",
                                CubeListBuilder.create().texOffs(2, 183).addBox(-16.0F, -16.0F, -3.5F, 32.0F, 32.0F,
                                                17.0F, new CubeDeformation(-4.0F)),
                                PartPose.offsetAndRotation(20.75F, -17.5F, 2.0F, 0.0F, -1.5708F, 0.0F));

                PartDefinition tentacles = body.addOrReplaceChild("tentacles", CubeListBuilder.create(),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition tentacles_0 = tentacles.addOrReplaceChild(
                                "tentacles_0", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F,
                                                18.0F, 4.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(-7.5F, -2.0F, -10.0F));

                PartDefinition tentacles_1 = tentacles.addOrReplaceChild(
                                "tentacles_1", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F,
                                                22.0F, 4.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(3.0F, -2.0F, -10.0F));

                PartDefinition tentacles_2 = tentacles.addOrReplaceChild(
                                "tentacles_2", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F,
                                                16.0F, 4.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(13.0F, -2.0F, -10.0F));

                PartDefinition tentacles_3 = tentacles.addOrReplaceChild(
                                "tentacles_3", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F,
                                                18.0F, 4.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(-12.5F, -2.0F, 2.0F));

                PartDefinition tentacles_4 = tentacles.addOrReplaceChild(
                                "tentacles_4", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F,
                                                26.0F, 4.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(-2.5F, -2.0F, 2.0F));

                PartDefinition tentacles_5 = tentacles.addOrReplaceChild(
                                "tentacles_5", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F,
                                                22.0F, 4.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(7.5F, -2.0F, 2.0F));

                PartDefinition tentacles_6 = tentacles.addOrReplaceChild(
                                "tentacles_6", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F,
                                                24.0F, 4.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(-12.5F, -2.0F, 10.0F));

                PartDefinition tentacles_7 = tentacles.addOrReplaceChild(
                                "tentacles_7", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F,
                                                24.0F, 4.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(2.5F, -2.0F, 10.0F));

                PartDefinition tentacles_8 = tentacles.addOrReplaceChild(
                                "tentacles_8", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F,
                                                26.0F, 4.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(12.5F, -2.0F, 10.0F));

                PartDefinition Crown = body.addOrReplaceChild("Crown", CubeListBuilder.create().texOffs(0, 87)
                                .addBox(-6.0F, -0.5F, -6.0F, 12.0F, 1.0F, 12.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 100).addBox(4.0F, -0.5F, -6.0F, 2.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 106).addBox(1.0F, -0.5F, -6.0F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 109)
                                .addBox(-6.0F, -0.5F, -6.0F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                                .texOffs(14, 100)
                                .addBox(-6.0F, -0.5F, -6.0F, 2.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                                .texOffs(28, 100).addBox(4.0F, -0.5F, 1.0F, 2.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 112).addBox(1.0F, -0.5F, 4.0F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                                .texOffs(42, 100)
                                .addBox(-6.0F, -0.5F, 1.0F, 2.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 115).addBox(-6.0F, -0.5F, 4.0F, 5.0F, 1.0F, 2.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, -32.25F, 2.0F));

                PartDefinition eye = body.addOrReplaceChild("eye", CubeListBuilder.create(),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition eye_main1 = eye.addOrReplaceChild("eye_main1", CubeListBuilder.create(),
                                PartPose.offset(0.0F, -21.0F, 0.0F));

                PartDefinition eye1 = eye_main1
                                .addOrReplaceChild("eye1",
                                                CubeListBuilder.create().texOffs(102, 72).addBox(-4.0F, -3.0F, 0.0F,
                                                                8.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)),
                                                PartPose.offset(-6.0F, 0.0F, -16.1F));

                PartDefinition eye2 = eye_main1.addOrReplaceChild("eye2",
                                CubeListBuilder.create().texOffs(102, 72).mirror()
                                                .addBox(-4.0F, -3.0F, 0.0F, 8.0F, 6.0F, 0.0F, new CubeDeformation(0.0F))
                                                .mirror(false),
                                PartPose.offset(6.0F, 0.0F, -16.1F));

                PartDefinition eye_main2 = eye.addOrReplaceChild("eye_main2", CubeListBuilder.create(),
                                PartPose.offset(0.0F, -21.0F, 0.0F));

                PartDefinition eye3 = eye_main2
                                .addOrReplaceChild("eye3",
                                                CubeListBuilder.create().texOffs(102, 8).addBox(-4.0F, -3.0F, 0.0F,
                                                                8.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)),
                                                PartPose.offset(-6.0F, 0.0F, -16.1F));

                PartDefinition eye4 = eye_main2.addOrReplaceChild("eye4",
                                CubeListBuilder.create().texOffs(102, 8).mirror()
                                                .addBox(-4.0F, -3.0F, 0.0F, 8.0F, 6.0F, 0.0F, new CubeDeformation(0.0F))
                                                .mirror(false),
                                PartPose.offset(6.0F, 0.0F, -16.1F));

                PartDefinition mouth = body.addOrReplaceChild(
                                "mouth", CubeListBuilder.create().texOffs(100, 18).addBox(-10.0F, -24.0F, -16.1F, 12.0F,
                                                14.0F, 0.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(4.0F, 10.0F, 0.0F));

                return LayerDefinition.create(meshdefinition, 128, 256);
        }

        @Override
        public void setupAnim(GiantGhast entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                        float netHeadYaw,
                        float headPitch) {
                this.root().getAllParts().forEach(ModelPart::resetPose);
                this.animate(entity.idleAnimationState, GiantGhastAnimation.IDLE, ageInTicks, 1.0F);
                this.animate(entity.flyAnimationState, GiantGhastAnimation2.FLY, ageInTicks);
                this.animate(entity.shootAnimationState, GiantGhastAnimation2.SHOOT, ageInTicks);
                for (int i = 0; i < this.tentacleArray.length; ++i) {
                        this.tentacleArray[i].xRot = 0.2F * Mth.sin(ageInTicks * 0.3F + (float) i) + 0.4F;
                }

                if (!entity.isVehicle()) {
                        this.body.yRot = netHeadYaw * ((float) Math.PI / 180F);
                        this.body.xRot = headPitch * ((float) Math.PI / 180F);
                }
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                        int packedOverlay,
                        float red, float green, float blue, float alpha) {
                root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }

        @Override
        public ModelPart root() {
                return this.root;
        }
}
