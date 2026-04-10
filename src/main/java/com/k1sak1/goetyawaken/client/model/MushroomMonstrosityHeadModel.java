package com.k1sak1.goetyawaken.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class MushroomMonstrosityHeadModel extends SkullModelBase {
        private final ModelPart head;

        public MushroomMonstrosityHeadModel(ModelPart root) {
                this.head = root.getChild("head");
        }

        public static LayerDefinition createBodyLayer() {
                MeshDefinition meshdefinition = new MeshDefinition();
                PartDefinition partdefinition = meshdefinition.getRoot();

                // 简化的单层结构，与 Goety 红石巨兽头颅一致
                PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                // top 组 - 主体部分
                PartDefinition top = head.addOrReplaceChild("top", CubeListBuilder.create()
                                .texOffs(0, 186)
                                .addBox(-14.0F, -25.0F, -20.0F, 28.0F, 31.0F, 21.0F, new CubeDeformation(0.0F))
                                .texOffs(256, 132)
                                .addBox(10.0F, -28.0F, -13.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.7F))
                                .texOffs(224, 80)
                                .addBox(9.0F, -32.5F, -14.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(1.2F))
                                .texOffs(319, 68)
                                .addBox(7.0F, -15.0F, -23.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.01F))
                                .texOffs(240, 80)
                                .addBox(6.0F, -16.0F, -26.0F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.01F)),
                                PartPose.offset(0.0F, -10.0F, 9.0F));

                // right_horn - 右角
                PartDefinition right_horn = top.addOrReplaceChild("right_horn", CubeListBuilder.create()
                                .texOffs(256, 80)
                                .addBox(-7.25F, 0.5F, -6.5F, 20.0F, 13.0F, 13.0F, new CubeDeformation(0.0F))
                                .texOffs(294, 0)
                                .addBox(-7.25F, -14.5F, -6.5F, 9.0F, 15.0F, 13.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(-26.75F, -23.5F, -8.5F));

                // left_horn - 左角
                PartDefinition left_horn = top.addOrReplaceChild("left_horn", CubeListBuilder.create()
                                .texOffs(256, 106)
                                .addBox(-12.75F, 0.5F, -6.5F, 20.0F, 13.0F, 13.0F, new CubeDeformation(0.0F))
                                .texOffs(340, 0)
                                .addBox(-1.75F, -14.5F, -6.5F, 9.0F, 15.0F, 13.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(26.75F, -23.5F, -8.5F));

                // eyes - 眼睛
                PartDefinition eyes = top.addOrReplaceChild("eyes", CubeListBuilder.create()
                                .texOffs(12, 16)
                                .addBox(-14.0F, -4.0F, 0.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                                .texOffs(12, 16)
                                .addBox(10.25F, -4.0F, 0.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                                .texOffs(12, 12)
                                .addBox(-2.0F, -7.0F, 0.0F, 6.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, -2.0F, -20.5F));

                // bottom - 基座
                PartDefinition bottom = head.addOrReplaceChild("bottom", CubeListBuilder.create()
                                .texOffs(232, 137)
                                .addBox(-13.5F, 0.0F, -20.0F, 27.0F, 10.0F, 21.0F, new CubeDeformation(0.0F))
                                .texOffs(369, 44)
                                .addBox(-13.5F, 7.0F, -20.0F, 27.0F, 0.0F, 21.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, -10.0F, 9.0F));

                return LayerDefinition.create(meshdefinition, 512, 256);
        }

        @Override
        public void setupAnim(float pMouthAnimation, float pYRot, float pXRot) {
                this.head.yRot = pYRot * ((float) Math.PI / 180F);
                this.head.xRot = pXRot * ((float) Math.PI / 180F);
        }

        @Override
        public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay,
                        float pRed, float pGreen, float pBlue, float pAlpha) {
                this.head.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }
}
