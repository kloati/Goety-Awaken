package com.k1sak1.goetyawaken.client.model;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class MushroomMissileModel<T extends Entity> extends EntityModel<T> {
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "mushroom_missile"), "main");
        private final ModelPart bb_main;

        public MushroomMissileModel(ModelPart root) {
                this.bb_main = root.getChild("bb_main");
        }

        public static LayerDefinition createBodyLayer() {
                MeshDefinition meshdefinition = new MeshDefinition();
                PartDefinition partdefinition = meshdefinition.getRoot();

                PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main",
                                CubeListBuilder.create().texOffs(0, 0)
                                                .addBox(-7.0F, -20.0F, -9.0F, 13.0F, 13.0F, 6.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(31, 16).addBox(-4.0F, -17.0F, -3.0F, 7.0F, 7.0F, 8.0F,
                                                                new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                return LayerDefinition.create(meshdefinition, 64, 32);
        }

        @Override
        public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                        float headPitch) {
                this.bb_main.yRot = netHeadYaw * ((float) Math.PI / 180F);
                this.bb_main.xRot = headPitch * ((float) Math.PI / 180F);
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                        int packedOverlay,
                        float red, float green, float blue, float alpha) {
                bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }
}