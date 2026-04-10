package com.k1sak1.goetyawaken.client.model.illager;

import com.k1sak1.goetyawaken.common.entities.ally.illager.SorcererServant;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmor;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

public class SorcererServantModel<T extends SorcererServant> extends HumanoidModel<T> implements HierarchicalArmor {
    public final ModelPart clothes;
    public final ModelPart cape;
    public final ModelPart arms;

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

    public SorcererServantModel(ModelPart root) {
        super(root);
        this.clothes = root.getChild("clothes");
        this.cape = root.getChild("cape");
        this.arms = root.getChild("arms");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(
                -4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F,
                -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0)
                .addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(
                -4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition clothes = partdefinition.addOrReplaceChild("clothes",
                CubeListBuilder.create().texOffs(0, 38)
                        .addBox(-8.0F, -11.5F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F))
                        .texOffs(60, 20).addBox(-12.0F, -12.0F, -3.0F, 16.0F, 5.0F, 6.0F, new CubeDeformation(0.6F)),
                PartPose.offset(4.0F, 11.5F, 0.0F));

        PartDefinition cape = partdefinition.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(64, 43).addBox(
                -4.5F, -1.0F, -0.5F, 9.0F, 20.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 3.5F));

        PartDefinition arms = partdefinition.addOrReplaceChild("arms",
                CubeListBuilder.create().texOffs(44, 22).mirror()
                        .addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                        .texOffs(44, 22).addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 2.0F, 0.0F));

        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22)
                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-2.0F, 12.0F, 0.0F));

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(0, 22).mirror()
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(2.0F, 12.0F, 0.0F));

        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(40, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-5.0F, 2.0F, 0.0F));

        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm",
                CubeListBuilder.create().texOffs(40, 46).mirror()
                        .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return Iterables.concat(super.bodyParts(), ImmutableList.of(this.arms, this.cape, this.clothes));
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch) {
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
        this.hat.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.hat.xRot = headPitch * ((float) Math.PI / 180F);
        this.arms.z = -1.0F;
        this.arms.xRot = -0.75F;
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
            this.arms.y = 3.0F;
            this.rightArm.y = 2.0F;
            this.leftArm.y = 2.0F;
            this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = 0.0F;
            this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
            this.leftArm.yRot = 0.0F;
            this.leftArm.zRot = 0.0F;
            this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
            this.rightLeg.yRot = 0.0F;
            this.rightLeg.zRot = 0.0F;
            this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount * 0.5F;
            this.leftLeg.yRot = 0.0F;
            this.leftLeg.zRot = 0.0F;
        }

        AbstractIllagerServant.IllagerServantArmPose armPose = entity.getArmPose();
        switch (armPose) {
            case CROSSED:
                this.rightArm.xRot = 0;
                this.leftArm.xRot = 0;
                break;
            case ATTACKING:
                AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, false, this.attackTime, ageInTicks);
                break;
            case SPELLCASTING:
                this.rightArm.z = 0.0F;
                this.rightArm.x = -5.0F;
                this.leftArm.z = 0.0F;
                this.leftArm.x = 5.0F;
                this.rightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
                this.leftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
                this.rightArm.zRot = 2.3561945F;
                this.leftArm.zRot = -2.3561945F;
                this.rightArm.yRot = 0.0F;
                this.leftArm.yRot = 0.0F;
                break;
            case BOW_AND_ARROW:
                this.rightArm.yRot = -0.1F + this.head.yRot;
                this.rightArm.xRot = -1.5707964F + this.head.xRot;
                this.leftArm.xRot = -0.9424779F + this.head.xRot;
                this.leftArm.yRot = this.head.yRot - 0.4F;
                this.leftArm.zRot = 1.5707964F;
                break;
            case CROSSBOW_HOLD:
                AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
                break;
            case CROSSBOW_CHARGE:
                AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, entity, true);
                break;
            case CELEBRATING:
                this.rightArm.z = 0.0F;
                this.rightArm.x = -5.0F;
                this.rightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.05F;
                this.rightArm.zRot = 2.3561945F;
                this.rightArm.yRot = 0.0F;
                this.leftArm.z = 0.0F;
                this.leftArm.x = 5.0F;
                this.leftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.05F;
                this.leftArm.zRot = -2.3561945F;
                this.leftArm.yRot = 0.0F;
                break;
        }

        float f = 1.0F;
        if (entity.getFallFlyingTicks() > 4) {
            f = (float) entity.getDeltaMovement().lengthSqr();
            f = f / 0.2F;
            f = f * f * f;
        }
        if (f < 1.0F) {
            f = 1.0F;
        }
        this.cape.xRot = com.Polarice3.Goety.utils.MathHelper.modelDegrees(10.0F)
                + Mth.abs(Mth.cos(limbSwing * 0.6662F) * 0.7F * limbSwingAmount / f);

        boolean flag = armPose == AbstractIllagerServant.IllagerServantArmPose.CROSSED;
        this.arms.visible = flag;
        this.leftArm.visible = !flag;
        this.rightArm.visible = !flag;
        boolean flag2 = entity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorItem
                || entity.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof ArmorItem;
        this.clothes.visible = !flag2;
        boolean flag3 = entity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ArmorItem;
        this.hat.visible = !flag3;
    }

    public void prepareMobModel(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        this.rightArmPose = ArmPose.EMPTY;
        this.leftArmPose = ArmPose.EMPTY;
        if (entityIn.getMainArm() == HumanoidArm.RIGHT) {
            this.RightArmPoses(InteractionHand.MAIN_HAND, entityIn);
            this.LeftArmPoses(InteractionHand.OFF_HAND, entityIn);
        } else {
            this.RightArmPoses(InteractionHand.OFF_HAND, entityIn);
            this.LeftArmPoses(InteractionHand.MAIN_HAND, entityIn);
        }
        super.prepareMobModel(entityIn, limbSwing, limbSwingAmount, partialTick);
    }

    private void RightArmPoses(InteractionHand hand, T entityIn) {
        ItemStack itemstack = entityIn.getItemInHand(hand);
        UseAnim useAction = itemstack.getUseAnimation();
        if (entityIn.getArmPose() != AbstractIllagerServant.IllagerServantArmPose.CROSSED) {
            this.rightArmPose = ArmPose.EMPTY;
            if (!itemstack.isEmpty()) {
                this.rightArmPose = ArmPose.ITEM;
            }
        }
    }

    private void LeftArmPoses(InteractionHand hand, T entityIn) {
        ItemStack itemstack = entityIn.getItemInHand(hand);
        UseAnim useAction = itemstack.getUseAnimation();
        if (entityIn.getArmPose() != AbstractIllagerServant.IllagerServantArmPose.CROSSED) {
            this.leftArmPose = ArmPose.EMPTY;
            if (!itemstack.isEmpty()) {
                this.leftArmPose = ArmPose.ITEM;
            }
        }
    }

    public Iterable<ModelPart> rightHandArmors() {
        return ImmutableList.of(this.rightArm);
    }

    public Iterable<ModelPart> leftHandArmors() {
        return ImmutableList.of(this.leftArm);
    }

    public Iterable<ModelPart> rightLegPartArmors() {
        return ImmutableList.of(this.rightLeg);
    }

    public Iterable<ModelPart> leftLegPartArmors() {
        return ImmutableList.of(this.leftLeg);
    }

    public Iterable<ModelPart> bodyPartArmors() {
        return ImmutableList.of(this.body);
    }

    public Iterable<ModelPart> headPartArmors() {
        return ImmutableList.of(this.head);
    }
}