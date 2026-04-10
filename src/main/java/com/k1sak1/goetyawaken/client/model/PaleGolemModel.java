package com.k1sak1.goetyawaken.client.model;

import com.k1sak1.goetyawaken.common.entities.ally.PaleGolemServant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PaleGolemModel extends EntityModel<PaleGolemServant> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public PaleGolemModel(ModelPart pRoot) {
        this.root = pRoot;
        this.head = pRoot.getChild("head");
        this.rightArm = pRoot.getChild("right_arm");
        this.leftArm = pRoot.getChild("left_arm");
        this.rightLeg = pRoot.getChild("right_leg");
        this.leftLeg = pRoot.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        return IronGolemModel.createBodyLayer();
    }

    @Override
    public void setupAnim(PaleGolemServant entity, float limbSwing, float limbSwingAmount, float ageInTicks,
            float netHeadYaw,
            float headPitch) {
        root.resetPose();

        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);

        this.rightLeg.xRot = -1.5F * Mth.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
        this.leftLeg.xRot = 1.5F * Mth.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
        this.rightLeg.yRot = 0.0F;
        this.leftLeg.yRot = 0.0F;

        if (this.riding) {
            this.rightLeg.xRot = -1.4137167F;
            this.rightLeg.yRot = ((float) Math.PI / 10F);
            this.rightLeg.zRot = 0.07853982F;
            this.leftLeg.xRot = -1.4137167F;
            this.leftLeg.yRot = (-(float) Math.PI / 10F);
            this.leftLeg.zRot = -0.07853982F;
        }
    }

    @Override
    public void prepareMobModel(PaleGolemServant entity, float limbSwing, float limbSwingAmount, float partialTick) {
        boolean hasWeapon = !entity.getMainHandItem().isEmpty();

        int atkTick = entity.getAttackAnimationTick();
        if (atkTick > 0) {
            float attackProgress = atkTick - partialTick;
            this.rightArm.xRot = -2.0F + 1.5F * Mth.triangleWave(attackProgress, 10.0F);
            this.leftArm.xRot = -2.0F + 1.5F * Mth.triangleWave(attackProgress, 10.0F);
        } else if (entity.isAggressive()) {
            if (hasWeapon) {
                this.rightArm.xRot = -1.8f;
                this.leftArm.xRot = 0;
            } else {
                this.rightArm.xRot = (-0.2F + 1.5F * Mth.triangleWave(limbSwing, 13.0F)) * limbSwingAmount;
                this.leftArm.xRot = (-0.2F - 1.5F * Mth.triangleWave(limbSwing, 13.0F)) * limbSwingAmount;
            }
        } else {
            this.rightArm.xRot = (-0.2F + 1.5F * Mth.triangleWave(limbSwing, 13.0F)) * limbSwingAmount;
            this.leftArm.xRot = (-0.2F - 1.5F * Mth.triangleWave(limbSwing, 13.0F)) * limbSwingAmount;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public ModelPart root() {
        return this.root;
    }
}