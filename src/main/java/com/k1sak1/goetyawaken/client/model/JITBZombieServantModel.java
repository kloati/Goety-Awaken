package com.k1sak1.goetyawaken.client.model;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.animation.undead.JITBZombieServantAnimations;
import com.k1sak1.goetyawaken.common.entities.ally.JITBZombieServant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JITBZombieServantModel extends HierarchicalModel<JITBZombieServant> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(GoetyAwaken.MODID, "jitb_zombie_servant"), "main");

    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart box;
    private final ModelPart gaizi;
    private final ModelPart joker;
    private final ModelPart left_arm;
    private final ModelPart right_arm;
    private final ModelPart right_leg;
    private final ModelPart left_leg;
    private final ModelPart root;

    public JITBZombieServantModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.box = this.body.getChild("box");
        this.gaizi = this.box.getChild("gaizi");
        this.joker = this.box.getChild("joker");
        this.left_arm = this.body.getChild("left_arm");
        this.right_arm = this.body.getChild("right_arm");
        this.right_leg = root.getChild("right_leg");
        this.left_leg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(16, 16)
                        .addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -12.0F, 0.0F));

        PartDefinition box = body.addOrReplaceChild("box", CubeListBuilder.create().texOffs(0, 37).addBox(-4.0F,
                -4.6667F, -4.6667F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -4.3333F, -5.3333F));

        PartDefinition gaizi = box.addOrReplaceChild("gaizi", CubeListBuilder.create().texOffs(35, 56).addBox(-4.0F,
                0.0F, 0.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.6667F, -4.6667F));

        PartDefinition joker = box.addOrReplaceChild("joker",
                CubeListBuilder.create().texOffs(4, 55).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -1.6667F, -0.6667F, 0.0F, 3.1416F, 0.0F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 28).addBox(
                4.0F, -18.0F, -10.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition cube_r1 = left_arm.addOrReplaceChild("cube_r1",
                CubeListBuilder.create().texOffs(40, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(6.0F, -24.0F, 0.0F, -0.8727F, 0.0F, 0.0F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm",
                CubeListBuilder.create().texOffs(40, 28)
                        .addBox(-1.0F, 3.0F, -8.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.1F))
                        .texOffs(41, 45).addBox(-4.0F, 3.0F, -5.0F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-7.0F, -9.0F, -2.0F));

        PartDefinition cube_r2 = right_arm.addOrReplaceChild("cube_r2",
                CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.0F, -3.0F, 2.0F, -0.8727F, 0.0F, 0.0F));

        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16)
                .addBox(-4.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16)
                .addBox(0.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(JITBZombieServant entity, float limbSwing, float limbSwingAmount, float ageInTicks,
            float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        if (entity.isAlive()) {
            this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
            this.head.xRot = headPitch * ((float) Math.PI / 180F);

            this.animate(entity.swellAnimationState, JITBZombieServantAnimations.SWELL, ageInTicks);
            this.animate(entity.musicAnimationState, JITBZombieServantAnimations.MUSIC, ageInTicks);

            if (limbSwingAmount > 0.0F) {
                float groundSpeed = (float) Math.sqrt(entity.getDeltaMovement().horizontalDistanceSqr());
                this.animate(entity.walkAnimationState, JITBZombieServantAnimations.WALK, ageInTicks, groundSpeed * 20);
            } else {
                this.right_leg.xRot = 0.0F;
                this.left_leg.xRot = 0.0F;
            }
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
