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

public class WildfireStatueModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation("goetyawaken", "wildfire_statue"), "main");
    private final ModelPart bone;
    private final ModelPart bone2;
    private final ModelPart bone3;
    private final ModelPart bone4;
    private final ModelPart bb_main;

    public WildfireStatueModel(ModelPart root) {
        this.bone = root.getChild("bone");
        this.bone2 = this.bone.getChild("bone2");
        this.bone3 = root.getChild("bone3");
        this.bone4 = root.getChild("bone4");
        this.bb_main = root.getChild("bb_main");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone",
                CubeListBuilder.create().texOffs(48, 94).addBox(-4.0F, -37.0F, -4.0F, 8.0F, 37.0F, 8.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.0F, 14.0F, 0.0F, 0.0873F, 0.3491F, 0.0873F));

        PartDefinition bone2 = bone.addOrReplaceChild("bone2",
                CubeListBuilder.create().texOffs(48, 62)
                        .addBox(-9.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 126)
                        .addBox(-9.0F, -20.0F, -8.0F, 16.0F, 20.0F, 16.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(1.0F, -39.0F, 1.0F, -0.2355F, -0.3829F, 0.0459F));

        PartDefinition bone3 = partdefinition.addOrReplaceChild("bone3",
                CubeListBuilder.create().texOffs(80, 125)
                        .addBox(-1.0F, -13.0F, -3.0F, 0.0F, 13.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(80, 131)
                        .addBox(-4.0F, -13.0F, 0.0F, 6.0F, 13.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition bone4 = partdefinition.addOrReplaceChild("bone4",
                CubeListBuilder.create().texOffs(80, 125)
                        .addBox(-1.0F, -13.0F, -3.0F, 0.0F, 13.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(80, 131)
                        .addBox(-4.0F, -13.0F, 0.0F, 6.0F, 13.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(2.0F, -15.0F, -3.0F));

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main",
                CubeListBuilder.create().texOffs(0, 0).addBox(-17.0F, -8.0F, -16.0F, 32.0F, 8.0F, 32.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition cube_r1 = bb_main.addOrReplaceChild("cube_r1",
                CubeListBuilder.create().texOffs(112, 62).addBox(1.0F, -32.0F, -1.0F, 0.0F, 32.0F, 4.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.0F, -38.0F, -16.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition cube_r2 = bb_main.addOrReplaceChild("cube_r2",
                CubeListBuilder.create().texOffs(112, 66).addBox(-3.0F, -32.0F, -1.0F, 4.0F, 32.0F, 0.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-17.0F, -36.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

        PartDefinition cube_r3 = bb_main.addOrReplaceChild("cube_r3",
                CubeListBuilder.create().texOffs(0, 45).addBox(0.0F, 0.0F, -8.0F, 4.0F, 32.0F, 20.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.0F, -44.0F, 10.0F, 1.5708F, -1.0472F, -1.5708F));

        PartDefinition cube_r4 = bb_main.addOrReplaceChild("cube_r4",
                CubeListBuilder.create().texOffs(0, 45).addBox(-4.0F, 0.0F, -10.0F, 4.0F, 32.0F, 20.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.0F, -44.0F, -10.0F, -1.5708F, -1.0908F, 1.5708F));

        PartDefinition cube_r5 = bb_main.addOrReplaceChild("cube_r5",
                CubeListBuilder.create().texOffs(0, 45).addBox(-4.0F, 0.0F, -9.0F, 4.0F, 32.0F, 20.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-11.0F, -44.0F, -1.0F, 0.0F, 0.0F, 0.5236F));

        PartDefinition cube_r6 = bb_main.addOrReplaceChild("cube_r6",
                CubeListBuilder.create().texOffs(0, 45).addBox(0.0F, 0.0F, -10.0F, 4.0F, 32.0F, 20.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(9.0F, -44.0F, 0.0F, 0.0F, 0.0F, -0.5236F));

        return LayerDefinition.create(meshdefinition, 146, 162);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
            float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
            int packedOverlay, float red, float green, float blue, float alpha) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        bone3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        bone4.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
