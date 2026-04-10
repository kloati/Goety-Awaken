package com.k1sak1.goetyawaken.client.model.ally.Integration;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.MaidFairyServant;

public class AdaptedMaidFairyModel<T extends MaidFairyServant> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation("goetyawaken", "maid_fairy_servant"), "main");

    private Object adaptedModel;
    private final MaidFairyServantModel<T> fallbackModel;

    public AdaptedMaidFairyModel(ModelPart root) {
        this.adaptedModel = ModelAdapterFactory.createMaidFairyModel(root);
        this.fallbackModel = new MaidFairyServantModel<>(root);
        if (adaptedModel instanceof MaidFairyServantModel) {
            this.adaptedModel = null;
        }
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch) {
        if (adaptedModel != null) {
            ModelAdapterFactory.setupAnim(adaptedModel, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw,
                    headPitch);
        } else {
            fallbackModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        if (adaptedModel != null) {
            ModelAdapterFactory.renderToBuffer(adaptedModel, poseStack, vertexConsumer, packedLight, packedOverlay, red,
                    green, blue, alpha);
        } else {
            fallbackModel.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue,
                    alpha);
        }
    }
}