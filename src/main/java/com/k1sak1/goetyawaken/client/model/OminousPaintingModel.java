package com.k1sak1.goetyawaken.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class OminousPaintingModel extends Model {
        private final ModelPart root;

        public OminousPaintingModel(ModelPart root) {
                super(RenderType::entityCutout);
                this.root = root;
        }

        public static LayerDefinition createSmallFrameLayer() {
                MeshDefinition meshdefinition = new MeshDefinition();
                PartDefinition partdefinition = meshdefinition.getRoot();

                PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(8, 4)
                                .addBox(-8.0F, -8.0F, -1.0F, 16.0F, 16.0F, 1.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 14.0F, 0.0F));

                PartDefinition frame = bone.addOrReplaceChild("frame",
                                CubeListBuilder.create().texOffs(0, 0)
                                                .addBox(-8.0F, -10.0F, -1.0F, 16.0F, 2.0F, 2.0F,
                                                                new CubeDeformation(-0.01F))
                                                .texOffs(0, 0)
                                                .addBox(-8.0F, 8.0F, -1.0F, 16.0F, 2.0F, 2.0F,
                                                                new CubeDeformation(-0.01F))
                                                .texOffs(0, 4).mirror()
                                                .addBox(8.0F, -10.0F, -1.0F, 2.0F, 20.0F, 2.0F,
                                                                new CubeDeformation(-0.01F))
                                                .mirror(false)
                                                .texOffs(0, 4).addBox(-10.0F, -10.0F, -1.0F, 2.0F, 20.0F, 2.0F,
                                                                new CubeDeformation(-0.01F)),
                                PartPose.offset(0.0F, 0.0F, -0.5F));

                return LayerDefinition.create(meshdefinition, 128, 64);
        }

        public static LayerDefinition createMediumFrameLayer() {
                MeshDefinition meshdefinition = new MeshDefinition();
                PartDefinition partdefinition = meshdefinition.getRoot();

                PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(8, 4)
                                .addBox(-16.0F, -16.0F, -1.0F, 32.0F, 32.0F, 1.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(8.0F, 6.0F, 0.0F));

                PartDefinition frame = bone.addOrReplaceChild("frame",
                                CubeListBuilder.create().texOffs(0, 0)
                                                .addBox(-16.0F, -18.0F, -1.0F, 32.0F, 2.0F, 2.0F,
                                                                new CubeDeformation(-0.01F))
                                                .texOffs(0, 0)
                                                .addBox(-16.0F, 16.0F, -1.0F, 32.0F, 2.0F, 2.0F,
                                                                new CubeDeformation(-0.01F))
                                                .texOffs(0, 4).mirror()
                                                .addBox(16.0F, -18.0F, -1.0F, 2.0F, 36.0F, 2.0F,
                                                                new CubeDeformation(-0.01F))
                                                .mirror(false)
                                                .texOffs(0, 4).addBox(-18.0F, -18.0F, -1.0F, 2.0F, 36.0F, 2.0F,
                                                                new CubeDeformation(-0.01F)),
                                PartPose.offset(0.0F, 0.0F, -0.5F));

                return LayerDefinition.create(meshdefinition, 128, 64);
        }

        public static LayerDefinition createLargeFrameLayer() {
                MeshDefinition meshdefinition = new MeshDefinition();
                PartDefinition partdefinition = meshdefinition.getRoot();

                PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(8, 4)
                                .addBox(-24.0F, -16.0F, -1.0F, 48.0F, 32.0F, 1.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 6.0F, 0.0F));

                PartDefinition frame = bone.addOrReplaceChild("frame",
                                CubeListBuilder.create().texOffs(0, 0)
                                                .addBox(-24.0F, -18.0F, -1.0F, 48.0F, 2.0F, 2.0F,
                                                                new CubeDeformation(-0.01F))
                                                .texOffs(0, 0)
                                                .addBox(-24.0F, 16.0F, -1.0F, 48.0F, 2.0F, 2.0F,
                                                                new CubeDeformation(-0.01F))
                                                .texOffs(0, 4).mirror()
                                                .addBox(24.0F, -18.0F, -1.0F, 2.0F, 36.0F, 2.0F,
                                                                new CubeDeformation(-0.01F))
                                                .mirror(false)
                                                .texOffs(0, 4).addBox(-26.0F, -18.0F, -1.0F, 2.0F, 36.0F, 2.0F,
                                                                new CubeDeformation(-0.01F)),
                                PartPose.offset(0.0F, 0.0F, -0.5F));

                return LayerDefinition.create(meshdefinition, 128, 64);
        }

        public static LayerDefinition createTallFrameLayer() {
                MeshDefinition meshdefinition = new MeshDefinition();
                PartDefinition partdefinition = meshdefinition.getRoot();

                PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(8, 4)
                                .addBox(-8.0F, -16.0F, -1.0F, 16.0F, 32.0F, 1.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 6.0F, 0.0F));

                PartDefinition frame = bone.addOrReplaceChild("frame",
                                CubeListBuilder.create().texOffs(0, 0)
                                                .addBox(-8.0F, -18.0F, -1.0F, 16.0F, 2.0F, 2.0F,
                                                                new CubeDeformation(-0.01F))
                                                .texOffs(0, 0)
                                                .addBox(-8.0F, 16.0F, -1.0F, 16.0F, 2.0F, 2.0F,
                                                                new CubeDeformation(-0.01F))
                                                .texOffs(0, 4).mirror()
                                                .addBox(8.0F, -18.0F, -1.0F, 2.0F, 36.0F, 2.0F,
                                                                new CubeDeformation(-0.01F))
                                                .mirror(false)
                                                .texOffs(0, 4).addBox(-10.0F, -18.0F, -1.0F, 2.0F, 36.0F, 2.0F,
                                                                new CubeDeformation(-0.01F)),
                                PartPose.offset(0.0F, 0.0F, -0.5F));

                return LayerDefinition.create(meshdefinition, 128, 64);
        }

        public static LayerDefinition createWideFrameLayer() {
                MeshDefinition meshdefinition = new MeshDefinition();
                PartDefinition partdefinition = meshdefinition.getRoot();

                PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(8, 4)
                                .addBox(-16.0F, -8.0F, -1.0F, 32.0F, 16.0F, 1.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(8.0F, 14.0F, 0.0F));

                PartDefinition frame = bone.addOrReplaceChild("frame",
                                CubeListBuilder.create().texOffs(0, 0)
                                                .addBox(-16.0F, -10.0F, -1.0F, 32.0F, 2.0F, 2.0F,
                                                                new CubeDeformation(-0.01F))
                                                .texOffs(0, 0)
                                                .addBox(-16.0F, 8.0F, -1.0F, 32.0F, 2.0F, 2.0F,
                                                                new CubeDeformation(-0.01F))
                                                .texOffs(0, 4).mirror()
                                                .addBox(16.0F, -10.0F, -1.0F, 2.0F, 20.0F, 2.0F,
                                                                new CubeDeformation(-0.01F))
                                                .mirror(false)
                                                .texOffs(0, 4).addBox(-18.0F, -10.0F, -1.0F, 2.0F, 20.0F, 2.0F,
                                                                new CubeDeformation(-0.01F)),
                                PartPose.offset(0.0F, 0.0F, -0.5F));

                return LayerDefinition.create(meshdefinition, 128, 64);
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                        int packedOverlay,
                        float red, float green, float blue, float alpha) {
                root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }
}