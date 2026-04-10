package com.k1sak1.goetyawaken.client.model.ally.Integration;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.MaidFairyServant;

public class MaidFairyServantModel<T extends MaidFairyServant> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation("goetyawaken", "maid_fairy_servant"), "main");

    private Object actualModel;
    private final FallbackMaidFairyModel fallbackModel;

    public MaidFairyServantModel(ModelPart root) {
        this.actualModel = ModelAdapterFactory.createMaidFairyModel(root);
        this.fallbackModel = new FallbackMaidFairyModel(root);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch) {
        ModelAdapterFactory.setupAnim(actualModel, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw,
                headPitch);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        ModelAdapterFactory.renderToBuffer(actualModel, poseStack, vertexConsumer, packedLight, packedOverlay, red,
                green, blue, alpha);
    }

    private static class FallbackMaidFairyModel {
        private final ModelPart head;
        private final ModelPart armRight;
        private final ModelPart armLeft;
        private final ModelPart legLeft;
        private final ModelPart legRight;
        private final ModelPart wingLeft;
        private final ModelPart wingRight;
        private final ModelPart blink;

        public FallbackMaidFairyModel(ModelPart root) {
            this.head = root.getChild("body").getChild("head");
            this.armRight = root.getChild("body").getChild("armRight");
            this.armLeft = root.getChild("body").getChild("armLeft");
            this.legLeft = root.getChild("body").getChild("legLeft");
            this.legRight = root.getChild("body").getChild("legRight");
            this.wingLeft = root.getChild("body").getChild("wingLeft");
            this.wingRight = root.getChild("body").getChild("wingRight");
            this.blink = root.getChild("body").getChild("blink");
        }

        public void setupAnim(MaidFairyServant entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                float netHeadYaw,
                float headPitch) {
            head.xRot = headPitch * 0.017453292F;
            head.yRot = netHeadYaw * 0.017453292F;

            armLeft.zRot = net.minecraft.util.Mth.cos(ageInTicks * 0.05f) * 0.05f - 0.4f;
            armRight.zRot = -net.minecraft.util.Mth.cos(ageInTicks * 0.05f) * 0.05f + 0.4f;

            if (entity.onGround()) {
                legLeft.xRot = net.minecraft.util.Mth.cos(limbSwing * 0.67f) * 0.3f * limbSwingAmount;
                legRight.xRot = -net.minecraft.util.Mth.cos(limbSwing * 0.67f) * 0.3f * limbSwingAmount;
                armLeft.xRot = -net.minecraft.util.Mth.cos(limbSwing * 0.67f) * 0.7F * limbSwingAmount;
                armRight.xRot = net.minecraft.util.Mth.cos(limbSwing * 0.67f) * 0.7F * limbSwingAmount;
                wingLeft.yRot = -net.minecraft.util.Mth.cos(ageInTicks * 0.3f) * 0.2f + 1.0f;
                wingRight.yRot = net.minecraft.util.Mth.cos(ageInTicks * 0.3f) * 0.2f - 1.0f;
            } else {
                legLeft.xRot = 0f;
                legRight.xRot = 0f;
                armLeft.xRot = -0.17453292F;
                armRight.xRot = -0.17453292F;
                head.xRot = head.xRot - 8 * 0.017453292F;
                wingLeft.yRot = -net.minecraft.util.Mth.cos(ageInTicks * 0.5f) * 0.4f + 1.2f;
                wingRight.yRot = net.minecraft.util.Mth.cos(ageInTicks * 0.5f) * 0.4f - 1.2f;
            }

            float remainder = ageInTicks % 60;
            blink.visible = (55 < remainder && remainder < 60);
        }

        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                int packedOverlay,
                float red, float green, float blue, float alpha) {
            head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            armRight.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            armLeft.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            legLeft.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            legRight.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            wingLeft.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            wingRight.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            blink.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F),
                PartPose.offset(0.0F, -6.0F, 0.0F));

        body.addOrReplaceChild("armRight", CubeListBuilder.create()
                .texOffs(0, 10).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 8.0F, 2.0F),
                PartPose.offset(-3.0F, -4.0F, 0.0F));

        body.addOrReplaceChild("armLeft", CubeListBuilder.create()
                .texOffs(0, 10).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 8.0F, 2.0F),
                PartPose.offset(3.0F, -4.0F, 0.0F));

        body.addOrReplaceChild("legLeft", CubeListBuilder.create()
                .texOffs(8, 10).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                PartPose.offset(1.5F, -1.0F, 0.0F));

        body.addOrReplaceChild("legRight", CubeListBuilder.create()
                .texOffs(8, 10).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                PartPose.offset(-1.5F, -1.0F, 0.0F));

        body.addOrReplaceChild("wingLeft", CubeListBuilder.create()
                .texOffs(20, 0).mirror().addBox(0.0F, -3.0F, 0.0F, 10.0F, 6.0F, 1.0F),
                PartPose.offsetAndRotation(2.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.3491F));

        body.addOrReplaceChild("wingRight", CubeListBuilder.create()
                .texOffs(20, 0).addBox(0.0F, -3.0F, 0.0F, 10.0F, 6.0F, 1.0F),
                PartPose.offsetAndRotation(-2.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.3491F));

        body.addOrReplaceChild("blink", CubeListBuilder.create()
                .texOffs(0, 10).addBox(-2.5F, -5.0F, -2.49F, 5.0F, 5.0F, 0.0F),
                PartPose.offset(0.0F, -6.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }
}