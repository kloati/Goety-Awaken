package com.k1sak1.goetyawaken.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import net.minecraft.world.entity.Entity;

public class GargoyleStatueModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation("goetyawaken", "gargoyle_statue"), "main");
    private final ModelPart gargoyle;

    public GargoyleStatueModel(ModelPart root) {
        this.gargoyle = root.getChild("gargoyle");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition gargoyle = partdefinition.addOrReplaceChild("gargoyle",
                CubeListBuilder.create().texOffs(400, 267)
                        .addBox(-16.0F, -56.0F, 83.0F, 22.0F, 66.0F, 22.0F, new CubeDeformation(0.0F))
                        .texOffs(400, 400)
                        .addBox(-28.0F, 10.0F, 69.0F, 45.0F, 23.0F, 44.0F, new CubeDeformation(0.0F)),
                PartPose.offset(5.0F, -9.0F, -92.0F));

        PartDefinition head = gargoyle.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(3, 3)
                        .addBox(-33.0F, -125.0F, -1.0F, 56.0F, 56.0F, 55.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        head.addOrReplaceChild("cube_r1",
                CubeListBuilder.create().texOffs(44, 125)
                        .addBox(-27.0F, -55.0F, -13.0F, 56.0F, 55.0F, 14.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-6.0F, -68.0F, 54.0F, 1.7453F, 0.0F, 0.0F));

        PartDefinition bone = head.addOrReplaceChild("bone", CubeListBuilder.create(),
                PartPose.offset(43.5F, -115.0F, 26.0F));

        bone.addOrReplaceChild("cube_r2",
                CubeListBuilder.create().texOffs(331, 33)
                        .addBox(-33.0F, -30.0F, -1.0F, 34.0F, 30.0F, 17.0F, new CubeDeformation(0.0F))
                        .texOffs(541, 172)
                        .addBox(-33.0F, 0.0F, -1.0F, 34.0F, 34.0F, 33.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(11.5F, -1.0F, 16.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition bone2 = head.addOrReplaceChild("bone2", CubeListBuilder.create(),
                PartPose.offsetAndRotation(-53.5F, -115.0F, 26.0F, 0.0F, 3.1416F, 0.0F));

        bone2.addOrReplaceChild("cube_r3",
                CubeListBuilder.create().texOffs(331, 87)
                        .addBox(-33.0F, -30.0F, -1.0F, 34.0F, 30.0F, 17.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-3.5F, -1.0F, -16.0F, 0.0F, 1.5708F, 0.0F));

        bone2.addOrReplaceChild("cube_r4",
                CubeListBuilder.create().texOffs(371, 171)
                        .addBox(-33.0F, -34.0F, -1.0F, 34.0F, 34.0F, 33.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(11.5F, 33.0F, 16.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition body = gargoyle.addOrReplaceChild("body", CubeListBuilder.create(),
                PartPose.offsetAndRotation(-5.0F, -77.0F, 61.0F, 0.3054F, 0.0F, 0.0F));

        body.addOrReplaceChild("cube_r5",
                CubeListBuilder.create().texOffs(170, 403)
                        .addBox(-55.0F, -56.0F, -1.0F, 56.0F, 56.0F, 55.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-27.0F, 28.0F, 52.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition bone5 = body.addOrReplaceChild("bone5", CubeListBuilder.create(),
                PartPose.offsetAndRotation(22.0F, -28.0F, 47.0F, -0.7854F, 0.7854F, 0.3491F));

        PartDefinition bone3 = bone5.addOrReplaceChild("bone3", CubeListBuilder.create(),
                PartPose.offset(0.0F, -27.0F, 19.0F));

        bone3.addOrReplaceChild("cube_r6",
                CubeListBuilder.create().texOffs(47, 317)
                        .addBox(-62.0F, -56.0F, -1.0F, 63.0F, 56.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 25.0F, -12.0F, 0.0F, 1.5708F, 0.0F));

        bone3.addOrReplaceChild("cube_r7",
                CubeListBuilder.create().texOffs(3, 306)
                        .addBox(-5.5F, -29.0F, -5.5F, 11.0F, 58.0F, 11.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.5F, -2.0F, -18.5F, 0.0F, 1.5708F, 0.0F));

        PartDefinition bone4 = bone5.addOrReplaceChild("bone4", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, -57.0F, -4.0F, 0.3927F, 0.0F, 0.0F));

        bone4.addOrReplaceChild("cube_r8",
                CubeListBuilder.create().texOffs(50, 537)
                        .addBox(-79.0F, -96.0F, -1.0F, 80.0F, 96.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -2.0F, 11.0F, 0.0F, 1.5708F, 0.0F));

        bone4.addOrReplaceChild("cube_r9",
                CubeListBuilder.create().texOffs(6, 526)
                        .addBox(-5.5F, -49.0F, -5.5F, 11.0F, 98.0F, 11.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.5F, -50.0F, 4.5F, 0.0F, 1.5708F, 0.0F));

        PartDefinition bone6 = body.addOrReplaceChild("bone6", CubeListBuilder.create(),
                PartPose.offsetAndRotation(-23.0F, -28.0F, 47.0F, -0.7854F, -0.7854F, -0.3491F));

        PartDefinition bone7 = bone6.addOrReplaceChild("bone7", CubeListBuilder.create(),
                PartPose.offset(0.0F, -27.0F, 19.0F));

        bone7.addOrReplaceChild("cube_r10",
                CubeListBuilder.create().texOffs(47, 317)
                        .addBox(-62.0F, -56.0F, -1.0F, 63.0F, 56.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 25.0F, -12.0F, 0.0F, 1.5708F, 0.0F));

        bone7.addOrReplaceChild("cube_r11",
                CubeListBuilder.create().texOffs(3, 306)
                        .addBox(-5.5F, -29.0F, -5.5F, 11.0F, 58.0F, 11.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.5F, -2.0F, -18.5F, 0.0F, 1.5708F, 0.0F));

        PartDefinition bone8 = bone6.addOrReplaceChild("bone8", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, -57.0F, -4.0F, 0.3927F, 0.0F, 0.0F));

        bone8.addOrReplaceChild("cube_r12",
                CubeListBuilder.create().texOffs(50, 537)
                        .addBox(-79.0F, -96.0F, -1.0F, 80.0F, 96.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -2.0F, 11.0F, 0.0F, 1.5708F, 0.0F));

        bone8.addOrReplaceChild("cube_r13",
                CubeListBuilder.create().texOffs(6, 526)
                        .addBox(-5.5F, -49.0F, -5.5F, 11.0F, 98.0F, 11.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.5F, -50.0F, 4.5F, 0.0F, 1.5708F, 0.0F));

        PartDefinition bone9 = gargoyle.addOrReplaceChild("bone9",
                CubeListBuilder.create().texOffs(200, 144)
                        .addBox(32.0F, -111.0F, 63.0F, 30.0F, 97.0F, 31.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 47.0F, 0.0F));

        bone9.addOrReplaceChild("cube_r14",
                CubeListBuilder.create().texOffs(449, 33)
                        .addBox(-46.0F, -47.0F, -4.0F, 47.0F, 47.0F, 48.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(69.0F, -155.0F, 54.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition bone10 = gargoyle.addOrReplaceChild("bone10", CubeListBuilder.create(),
                PartPose.offset(0.0F, 47.0F, 0.0F));

        bone10.addOrReplaceChild("cube_r15",
                CubeListBuilder.create().texOffs(449, 33)
                        .addBox(-46.0F, -47.0F, -6.0F, 47.0F, 47.0F, 48.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-79.0F, -153.0F, 101.0F, 1.5708F, 0.0F, 3.1416F));

        bone10.addOrReplaceChild("cube_r16",
                CubeListBuilder.create().texOffs(200, 144)
                        .addBox(-29.0F, -102.0F, -1.0F, 30.0F, 97.0F, 31.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-42.0F, -9.0F, 92.0F, 0.0F, -1.5708F, 0.0F));

        return LayerDefinition.create(meshdefinition, 711, 711);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount,
            float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer,
            int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        gargoyle.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
