package com.k1sak1.goetyawaken.client.model;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class PotatoStaffModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(GoetyAwaken.location("potato_staff"),
            "main");
    private final ModelPart staff;
    private final ModelPart poisonousPotato;
    private final ModelPart leafLeft;
    private final ModelPart leafRight;

    public PotatoStaffModel(ModelPart root) {
        this.staff = root.getChild("staff");
        this.poisonousPotato = this.staff.getChild("poisonous_potato");
        this.leafLeft = root.getChild("leaf_left");
        this.leafRight = root.getChild("leaf_right");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition staff = partdefinition.addOrReplaceChild("staff",
                CubeListBuilder.create().texOffs(0, 16)
                        .addBox(0.0F, -27.0F, -1.0F, 1.0F, 27.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(5, 24)
                        .addBox(-1.0F, -28.0F, -2.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition poisonous_potato = staff.addOrReplaceChild("poisonous_potato",
                CubeListBuilder.create().texOffs(0, 1)
                        .addBox(-6.9103F, -6.1026F, -0.5F, 13.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(7, 17).addBox(-2.9103F, -3.1026F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(7, 17).addBox(-1.9103F, -3.1026F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(7, 17).addBox(-0.9103F, -3.1026F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(7, 17).addBox(0.0897F, -4.1026F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(7, 17).addBox(1.0897F, -4.1026F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(7, 17).addBox(2.0897F, -4.1026F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(7, 17).addBox(-5.9103F, -0.1026F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(7, 17).addBox(-5.9103F, 0.8974F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(7, 17).addBox(5.0897F, -2.1026F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(12, 17).addBox(4.0897F, 2.8974F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(12, 17).addBox(-3.9103F, -3.1026F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(12, 17).addBox(5.0897F, -6.1026F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(7, 20).addBox(5.0897F, -1.1026F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(7, 20).addBox(5.0897F, -0.1026F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(7, 20).addBox(5.0897F, 0.8974F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(7, 20).addBox(1.0897F, 3.8974F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(7, 20).addBox(2.0897F, 2.8974F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(12, 17).addBox(4.0897F, -6.1026F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(12, 17).addBox(3.0897F, -5.1026F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(12, 17).addBox(-4.9103F, 4.8974F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(12, 17).addBox(-4.9103F, -4.1026F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(7, 20).addBox(-0.9103F, 4.8974F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(7, 20).addBox(-1.9103F, 4.8974F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(7, 20).addBox(-2.9103F, 4.8974F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(7, 20).addBox(-5.9103F, 2.8974F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(7, 20).addBox(0.0897F, 3.8974F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(12, 17).addBox(-5.9103F, -4.1026F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(7, 20).addBox(-5.9103F, 1.8974F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(12, 17).addBox(-6.9103F, -2.1026F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(12, 17).addBox(-6.9103F, -3.1026F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(17, 17).addBox(-4.9103F, 3.8974F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(17, 17).addBox(-3.9103F, 4.8974F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(17, 17).addBox(3.0897F, 2.8974F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(17, 17).addBox(5.0897F, -4.1026F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(17, 17).addBox(5.0897F, -5.1026F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(17, 17).addBox(4.0897F, 1.8974F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(17, 17).addBox(4.0897F, -3.1026F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(17, 17).addBox(-5.9103F, -1.1026F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.9103F, -37.8974F, -0.5F));

        PartDefinition leaf_left = partdefinition.addOrReplaceChild("leaf_left",
                CubeListBuilder.create().texOffs(5, 29)
                        .addBox(-3.0F, 0.0F, -2.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.5672F));

        leaf_left.addOrReplaceChild("cube_r1",
                CubeListBuilder.create().texOffs(6, 34)
                        .addBox(-3.0F, -0.5F, -1.5F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-3.0F, 0.5F, 0.0F, 0.0F, 0.0F, -0.8727F));

        PartDefinition leaf_right = partdefinition.addOrReplaceChild("leaf_right", CubeListBuilder.create(),
                PartPose.offsetAndRotation(1.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.5672F));

        leaf_right.addOrReplaceChild("cube_r2",
                CubeListBuilder.create().texOffs(6, 34)
                        .addBox(-3.0F, -0.5F, -0.5F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(4.0F, 0.5F, 0.0F, 3.1416F, 0.0F, -2.0944F));

        leaf_right.addOrReplaceChild("cube_r3",
                CubeListBuilder.create().texOffs(18, 29)
                        .addBox(-4.0F, 0.0F, -1.0F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 3.1416F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public void animate(float ageInTicks) {
        this.staff.getAllParts().forEach(ModelPart::resetPose);
        this.poisonousPotato.yRot = (float) Math.toRadians(-(ageInTicks * 12.0F % 360.0F));
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        this.staff.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leafLeft.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.leafRight.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
