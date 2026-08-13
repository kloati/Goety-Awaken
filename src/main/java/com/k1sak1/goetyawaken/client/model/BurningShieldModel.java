package com.k1sak1.goetyawaken.client.model;

import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class BurningShieldModel<T extends LivingEntity> extends EntityModel<T> {
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "burning_shield"), "main");
        private final ModelPart shield_body;

        public BurningShieldModel(ModelPart root) {
                this.shield_body = root.getChild("shield_body");
        }

        public static LayerDefinition createBodyLayer() {
                MeshDefinition meshdefinition = new MeshDefinition();
                PartDefinition partdefinition = meshdefinition.getRoot();
                PartDefinition shield_body = partdefinition.addOrReplaceChild(
                                "shield_body",
                                CubeListBuilder.create().texOffs(16, 18).addBox(-5.0F, -17.0F, -1.0F, 10.0F,
                                                17.0F, 2.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 24.0F, 0.0F));

                return LayerDefinition.create(meshdefinition, 64, 64);
        }

        @Override
        public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                        float headPitch) {
        }

        @Override
        public void renderToBuffer(com.mojang.blaze3d.vertex.PoseStack poseStack,
                        com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
                        float red,
                        float green, float blue, float alpha) {
                shield_body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }
}
