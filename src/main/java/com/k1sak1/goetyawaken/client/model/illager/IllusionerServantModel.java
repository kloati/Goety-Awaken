package com.k1sak1.goetyawaken.client.model.illager;

import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmor;
import com.Polarice3.Goety.utils.ModelUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class IllusionerServantModel<T extends Mob> extends HierarchicalModel<T>
        implements ArmedModel, HeadedModel, HierarchicalArmor {
    public static final net.minecraft.client.model.geom.ModelLayerLocation LAYER_LOCATION = new net.minecraft.client.model.geom.ModelLayerLocation(
            new net.minecraft.resources.ResourceLocation("goetyawaken",
                    "illusioner_servant"),
            "main");
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart hat;
    private final ModelPart arms;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    public final List<String> allPartNames;

    public IllusionerServantModel(ModelPart pRoot) {
        this.root = pRoot;
        this.head = pRoot.getChild("head");
        this.hat = this.head.getChild("hat");
        this.hat.visible = false;
        this.arms = pRoot.getChild("arms");
        this.leftLeg = pRoot.getChild("left_leg");
        this.rightLeg = pRoot.getChild("right_leg");
        this.leftArm = pRoot.getChild("left_arm");
        this.rightArm = pRoot.getChild("right_arm");
        this.allPartNames = Stream.concat(Stream.of("root"), ModelUtil.getAllPartNames(this.root)).toList();
    }

    public static LayerDefinition createBodyLayer() {
        return createBodyLayer(0);
    }

    public static LayerDefinition createShadowLayer() {
        return createBodyLayer(-0.05F);
    }

    public static LayerDefinition createBodyLayer(float deformation) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        partdefinition1.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F,
                8.0F, 12.0F, 8.0F, new CubeDeformation(0.45F)), PartPose.ZERO);
        partdefinition1.addOrReplaceChild("nose",
                CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offset(0.0F, -2.0F, 0.0F));
        partdefinition.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F).texOffs(0, 38)
                        .addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition partdefinition2 = partdefinition.addOrReplaceChild("arms",
                CubeListBuilder.create().texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F).texOffs(40, 38)
                        .addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 3.0F, -1.0F, -0.75F, 0.0F, 0.0F));
        partdefinition2.addOrReplaceChild("left_shoulder",
                CubeListBuilder.create().texOffs(44, 22).mirror().addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F),
                PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-2.0F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(2.0F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_arm",
                CubeListBuilder.create().texOffs(40, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-5.0F, 2.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_arm",
                CubeListBuilder.create().texOffs(40, 46).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(5.0F, 2.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public ModelPart root() {
        return this.root;
    }

    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw,
            float pHeadPitch) {
        this.head.yRot = pNetHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = pHeadPitch * ((float) Math.PI / 180F);
        if (this.riding) {
            this.rightArm.xRot = (-(float) Math.PI / 5F);
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = 0.0F;
            this.leftArm.xRot = (-(float) Math.PI / 5F);
            this.leftArm.yRot = 0.0F;
            this.leftArm.zRot = 0.0F;
            this.rightLeg.xRot = -1.4137167F;
            this.rightLeg.yRot = ((float) Math.PI / 10F);
            this.rightLeg.zRot = 0.07853982F;
            this.leftLeg.xRot = -1.4137167F;
            this.leftLeg.yRot = (-(float) Math.PI / 10F);
            this.leftLeg.zRot = -0.07853982F;
        } else {
            this.rightArm.xRot = Mth.cos(pLimbSwing * 0.6662F + (float) Math.PI) * 2.0F * pLimbSwingAmount * 0.5F;
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = 0.0F;
            this.leftArm.xRot = Mth.cos(pLimbSwing * 0.6662F) * 2.0F * pLimbSwingAmount * 0.5F;
            this.leftArm.yRot = 0.0F;
            this.leftArm.zRot = 0.0F;
            this.rightLeg.xRot = Mth.cos(pLimbSwing * 0.6662F) * 1.4F * pLimbSwingAmount * 0.5F;
            this.rightLeg.yRot = 0.0F;
            this.rightLeg.zRot = 0.0F;
            this.leftLeg.xRot = Mth.cos(pLimbSwing * 0.6662F + (float) Math.PI) * 1.4F * pLimbSwingAmount * 0.5F;
            this.leftLeg.yRot = 0.0F;
            this.leftLeg.zRot = 0.0F;
        }

        String poseKey;
        if (pEntity instanceof AbstractIllagerServant servant) {
            poseKey = servant.getArmPose().name();
        } else if (pEntity instanceof AbstractIllager illager) {
            poseKey = illager.getArmPose().name();
        } else {
            poseKey = "NEUTRAL";
        }
        if (poseKey.equals("ATTACKING")) {
            if (pEntity.getMainHandItem().isEmpty()) {
                net.minecraft.client.model.AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, true,
                        this.attackTime, pAgeInTicks);
            } else {
                net.minecraft.client.model.AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, pEntity,
                        this.attackTime, pAgeInTicks);
            }
        } else if (poseKey.equals("SPELLCASTING")) {
            this.rightArm.z = 0.0F;
            this.rightArm.x = -5.0F;
            this.leftArm.z = 0.0F;
            this.leftArm.x = 5.0F;
            this.rightArm.xRot = Mth.cos(pAgeInTicks * 0.6662F) * 0.25F;
            this.leftArm.xRot = Mth.cos(pAgeInTicks * 0.6662F) * 0.25F;
            this.rightArm.zRot = 2.3561945F;
            this.leftArm.zRot = -2.3561945F;
            this.rightArm.yRot = 0.0F;
            this.leftArm.yRot = 0.0F;
        } else if (poseKey.equals("BOW_AND_ARROW")) {
            this.rightArm.yRot = -0.1F + this.head.yRot;
            this.rightArm.xRot = (-(float) Math.PI / 2F) + this.head.xRot;
            this.leftArm.xRot = -0.9424779F + this.head.xRot;
            this.leftArm.yRot = this.head.yRot - 0.4F;
            this.leftArm.zRot = ((float) Math.PI / 2F);
        } else if (poseKey.equals("CROSSBOW_HOLD")) {
            net.minecraft.client.model.AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
        } else if (poseKey.equals("CROSSBOW_CHARGE")) {
            net.minecraft.client.model.AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, pEntity, true);
        } else if (poseKey.equals("CELEBRATING")) {
            this.rightArm.z = 0.0F;
            this.rightArm.x = -5.0F;
            this.rightArm.xRot = Mth.cos(pAgeInTicks * 0.6662F) * 0.05F;
            this.rightArm.zRot = 2.670354F;
            this.rightArm.yRot = 0.0F;
            this.leftArm.z = 0.0F;
            this.leftArm.x = 5.0F;
            this.leftArm.xRot = Mth.cos(pAgeInTicks * 0.6662F) * 0.05F;
            this.leftArm.zRot = -2.3561945F;
            this.leftArm.yRot = 0.0F;
        }

        boolean flag = poseKey.equals("CROSSED");
        this.arms.visible = flag;
        this.leftArm.visible = !flag;
        this.rightArm.visible = !flag;

        boolean flag2 = pEntity.getMainArm() == HumanoidArm.RIGHT;
        if (pEntity.isUsingItem()) {
            if (pEntity.getUsedItemHand() == net.minecraft.world.InteractionHand.MAIN_HAND) {
                if (flag2) {
                    this.leftArm.xRot = this.leftArm.xRot * 0.5F - ((float) Math.PI / 10F)
                            * (float) pEntity.getUseItemRemainingTicks() /
                            pEntity.getUseItem().getUseDuration();
                } else {
                    this.rightArm.xRot = this.rightArm.xRot * 0.5F - ((float) Math.PI / 10F)
                            * (float) pEntity.getUseItemRemainingTicks() /
                            pEntity.getUseItem().getUseDuration();
                }
            } else if (flag2) {
                this.rightArm.xRot = this.rightArm.xRot * 0.5F - ((float) Math.PI / 10F)
                        * (float) pEntity.getUseItemRemainingTicks() /
                        pEntity.getUseItem().getUseDuration();
            } else {
                this.leftArm.xRot = this.leftArm.xRot * 0.5F - ((float) Math.PI / 10F)
                        * (float) pEntity.getUseItemRemainingTicks() /
                        pEntity.getUseItem().getUseDuration();
            }
        }
    }

    private ModelPart getArm(HumanoidArm pArm) {
        return pArm == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
    }

    public ModelPart getHat() {
        return this.hat;
    }

    public ModelPart getHead() {
        return this.head;
    }

    public void translateToHand(HumanoidArm pSide, PoseStack pPoseStack) {
        this.getArm(pSide).translateAndRotate(pPoseStack);
    }

    @Override
    public void translateToHead(ModelPart modelPart, PoseStack poseStack) {
        modelPart.translateAndRotate(poseStack);
    }

    @Override
    public void translateToChest(ModelPart modelPart, PoseStack poseStack) {
        modelPart.translateAndRotate(poseStack);
    }

    @Override
    public void translateToLeg(ModelPart modelPart, PoseStack poseStack) {
        modelPart.translateAndRotate(poseStack);
    }

    @Override
    public void translateToArms(ModelPart modelPart, PoseStack poseStack) {
        modelPart.translateAndRotate(poseStack);
    }
}