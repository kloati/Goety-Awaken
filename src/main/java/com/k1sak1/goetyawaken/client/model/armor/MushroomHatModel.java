package com.k1sak1.goetyawaken.client.model.armor;

import com.google.common.collect.ImmutableList;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class MushroomHatModel extends HumanoidModel<LivingEntity> {
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "mushroom_hat_layer"), "main");

        public final ModelPart head;

        public MushroomHatModel(ModelPart root) {
                super(root);
                this.head = root.getChild("head");
        }

        public static LayerDefinition createBodyLayer() {
                MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
                PartDefinition partdefinition = meshdefinition.getRoot();

                PartDefinition head = partdefinition.addOrReplaceChild("head",
                                CubeListBuilder.create(),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition bottom_part = head.addOrReplaceChild("bottom_part",
                                CubeListBuilder.create()
                                                .texOffs(0, 13).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 3.0F, 16.0F,
                                                                new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition top_part = head.addOrReplaceChild("top_part",
                                CubeListBuilder.create()
                                                .texOffs(0, 0).addBox(-4.0F, -13.0F, -4.0F, 8.0F, 5.0F, 8.0F,
                                                                new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                return LayerDefinition.create(meshdefinition, 64, 64);
        }

        @Override
        public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                        float netHeadYaw, float headPitch) {
                super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                        int packedOverlay,
                        float red, float green, float blue, float alpha) {
                this.head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }

        @Override
        protected Iterable<ModelPart> bodyParts() {
                return ImmutableList.of(this.head);
        }
}