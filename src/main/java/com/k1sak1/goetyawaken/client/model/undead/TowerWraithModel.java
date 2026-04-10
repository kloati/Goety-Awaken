package com.k1sak1.goetyawaken.client.model.undead;

import com.Polarice3.Goety.utils.MathHelper;
import com.k1sak1.goetyawaken.common.entities.ally.undead.tower_wraith.AbstractTowerWraith;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class TowerWraithModel<T extends LivingEntity> extends HierarchicalModel<T> {
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
                        new ResourceLocation("goetyawaken", "tower_wraith"), "main");
        private final ModelPart root;
        private final ModelPart Ghost;
        private final ModelPart head;
        private final ModelPart hat;
        private final ModelPart right_arm;
        private final ModelPart right_robe;
        private final ModelPart right_bone;
        private final ModelPart left_arm;
        private final ModelPart left_robe;
        private final ModelPart left_bone;
        private final ModelPart body;
        private final ModelPart ribs;
        private final ModelPart spine;
        private final ModelPart robe;
        private final ModelPart cape;

        public TowerWraithModel(ModelPart root) {
                this.root = root;
                this.Ghost = root.getChild("Ghost");
                this.head = this.Ghost.getChild("head");
                this.hat = this.head.getChild("hat");
                this.right_arm = this.Ghost.getChild("right_arm");
                this.right_robe = this.right_arm.getChild("right_robe");
                this.right_bone = this.right_arm.getChild("right_bone");
                this.left_arm = this.Ghost.getChild("left_arm");
                this.left_robe = this.left_arm.getChild("left_robe");
                this.left_bone = this.left_arm.getChild("left_bone");
                this.body = this.Ghost.getChild("body");
                this.ribs = this.body.getChild("ribs");
                this.spine = this.body.getChild("spine");
                this.robe = this.body.getChild("robe");
                this.cape = this.robe.getChild("cape");
        }

        public static LayerDefinition createBodyLayer() {
                MeshDefinition meshdefinition = new MeshDefinition();
                PartDefinition partdefinition = meshdefinition.getRoot();

                PartDefinition Ghost = partdefinition.addOrReplaceChild("Ghost", CubeListBuilder.create(),
                                PartPose.offset(0.0F, 24.0F, 0.0F));

                PartDefinition head = Ghost.addOrReplaceChild("head",
                                CubeListBuilder.create().texOffs(2, 3).addBox(-3.0F,
                                                -7.0F, -2.0F, 6.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, -24.0F, -1.0F));

                PartDefinition hat = head.addOrReplaceChild("hat",
                                CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.75F,
                                                -3.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 1.0F, 0.0F));

                PartDefinition hat_r1 = hat.addOrReplaceChild("hat_r1",
                                CubeListBuilder.create().texOffs(2, 58).addBox(-4.0F, 0.0F, -7.0F, 8.0F, 2.0F, 7.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offsetAndRotation(0.0F, -8.75F, 5.0F, -2.5307F, 0.0F, 3.1416F));

                PartDefinition right_arm = Ghost.addOrReplaceChild("right_arm", CubeListBuilder.create(),
                                PartPose.offset(-6.0F, -22.0F, -1.0F));

                PartDefinition right_robe = right_arm.addOrReplaceChild("right_robe",
                                CubeListBuilder.create().texOffs(0, 33)
                                                .addBox(-2.0F, -3.0F, -2.0F, 4.0F, 14.0F, 4.0F,
                                                                new CubeDeformation(-0.01F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition right_bone = right_arm.addOrReplaceChild("right_bone",
                                CubeListBuilder.create().texOffs(0, 16).mirror()
                                                .addBox(-0.5F, -2.0F, -0.5F, 1.0F, 12.0F, 1.0F,
                                                                new CubeDeformation(-0.01F))
                                                .mirror(false),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition left_arm = Ghost.addOrReplaceChild("left_arm", CubeListBuilder.create(),
                                PartPose.offset(6.0F, -22.0F, -1.0F));

                PartDefinition left_robe = left_arm.addOrReplaceChild("left_robe",
                                CubeListBuilder.create().texOffs(16, 33)
                                                .addBox(-2.0F, -3.0F, -2.0F, 4.0F, 14.0F, 4.0F,
                                                                new CubeDeformation(-0.01F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition left_bone = left_arm.addOrReplaceChild("left_bone",
                                CubeListBuilder.create().texOffs(0, 16)
                                                .addBox(-0.5F, -2.0F, -0.5F, 1.0F, 12.0F, 1.0F,
                                                                new CubeDeformation(-0.01F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition body = Ghost.addOrReplaceChild("body", CubeListBuilder.create(),
                                PartPose.offset(0.0F, -24.0F, 0.0F));

                PartDefinition ribs = body.addOrReplaceChild("ribs",
                                CubeListBuilder.create().texOffs(8, 17).addBox(-3.0F, 0.0F,
                                                -4.0F, 6.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition spine = body.addOrReplaceChild("spine",
                                CubeListBuilder.create().texOffs(0, 16).addBox(-0.5F,
                                                -1.0F, -1.5F, 1.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition robe = body.addOrReplaceChild("robe",
                                CubeListBuilder.create().texOffs(35, 34).addBox(-4.0F,
                                                -1.0F, -2.0F, 8.0F, 20.0F, 4.0F, new CubeDeformation(-0.01F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition cape = robe
                                .addOrReplaceChild("cape",
                                                CubeListBuilder.create().texOffs(37, 66).addBox(-5.0F, 0.0F, 0.0F,
                                                                10.0F, 20.0F, 1.0F,
                                                                new CubeDeformation(0.0F)),
                                                PartPose.offsetAndRotation(0.0F, 4.0F, 2.0F, 0.1309F, 0.0F, 0.0F));

                return LayerDefinition.create(meshdefinition, 64, 128);
        }

        @Override
        public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw,
                        float pHeadPitch) {
                this.root().getAllParts().forEach(ModelPart::resetPose);
                if (pEntity instanceof AbstractTowerWraith wraith) {
                        this.animate(wraith.attackAnimationState,
                                        com.k1sak1.goetyawaken.client.animation.undead.TowerWraithAnimations.ATTACK,
                                        pAgeInTicks);
                        this.animate(wraith.breathingAnimationState,
                                        com.k1sak1.goetyawaken.client.animation.undead.TowerWraithAnimations.PUKE,
                                        pAgeInTicks);
                        this.animate(wraith.postTeleportAnimationState,
                                        com.k1sak1.goetyawaken.client.animation.undead.TowerWraithAnimations.TELEPORT_OUT,
                                        pAgeInTicks);
                        this.animate(wraith.acidAnimationState,
                                        com.k1sak1.goetyawaken.client.animation.undead.TowerWraithAnimations.ACID,
                                        pAgeInTicks);
                        this.animate(wraith.modechangeAnimationState,
                                        com.k1sak1.goetyawaken.client.animation.undead.TowerWraithAnimations.MODECHANGE,
                                        pAgeInTicks);
                        if (!wraith.isFiring() && !wraith.isBreathing() && !wraith.isPostTeleporting()) {
                                if (wraith.isTeleporting()) {
                                        float f7 = Mth.sin(((float) (wraith.teleportTime - 20) - wraith.teleportTime2)
                                                        / 20.0F
                                                        * (float) Math.PI * 0.25F);
                                        this.head.xRot = (((float) Math.PI) * f7) + 2.0F;
                                        this.right_arm.xRot = ((float) Math.PI) * f7;
                                        this.left_arm.xRot = ((float) Math.PI) * f7;
                                        this.Ghost.y += (((float) Math.PI) * f7) * 5.0F;
                                } else {
                                        float f = pAgeInTicks * 0.0025F;
                                        if (pEntity.walkAnimation.isMoving()) {
                                                f *= 2.0F;
                                        }
                                        this.Ghost.y = Mth.sin(f * 40.0F) + 24.0F;
                                        float f4 = Math.min(pLimbSwingAmount / 0.3F, 1.0F);
                                        this.robe.xRot = f4 * MathHelper.modelDegrees(40.0F);
                                        this.robe.xRot += Mth.cos(pAgeInTicks * 0.09F) * 0.1F + 0.1F;
                                        this.head.yRot = pNetHeadYaw * ((float) Math.PI / 180F);
                                        float f5 = Math.min(pLimbSwingAmount / 2.0F, 1.0F);
                                        float degrees;
                                        if (wraith.getLookControl().isLookingAtTarget()) {
                                                degrees = 0.0F;
                                        } else {
                                                degrees = MathHelper.modelDegrees(10.0F) - f5;
                                        }
                                        this.head.xRot = pHeadPitch * ((float) Math.PI / 180F) + degrees;
                                        animateArms(this.left_arm, this.right_arm, pLimbSwingAmount, pAgeInTicks);
                                        this.right_bone.xRot = -MathHelper.modelDegrees(12.5F);
                                        this.left_bone.xRot = -MathHelper.modelDegrees(12.5F);
                                }
                        } else if (wraith.isBreathing()) {
                                this.Ghost.yRot = pNetHeadYaw * ((float) Math.PI / 180F);
                                this.Ghost.xRot = pHeadPitch * ((float) Math.PI / 180F);
                        }

                        this.setupCape(wraith, pAgeInTicks);
                } else {
                        this.head.xRot = pHeadPitch * ((float) Math.PI / 180F);
                }
        }

        public static void animateArms(ModelPart leftArm, ModelPart rightArm, float attackTime, float ageInTicks) {
                rightArm.zRot = Math.min(attackTime / 0.9F, 1.0F);
                leftArm.zRot = -Math.min(attackTime / 0.9F, 1.0F);
                rightArm.yRot = -(0.1F - 0 * 0.6F);
                leftArm.yRot = 0.1F - 0 * 0.6F;
                float f2 = -MathHelper.modelDegrees(40.0F);
                rightArm.xRot = f2;
                leftArm.xRot = f2;
                AnimationUtils.bobArms(rightArm, leftArm, ageInTicks);
        }

        private void setupCape(AbstractTowerWraith wraith, float pAgeInTicks) {
                this.cape.xRot = 0.1309F;
                this.cape.yRot = 0.0F;
                this.cape.zRot = 0.0F;
        }

        @Override
        public ModelPart root() {
                return this.root;
        }

}
