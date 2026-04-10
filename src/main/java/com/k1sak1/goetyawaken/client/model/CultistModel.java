package com.k1sak1.goetyawaken.client.model;

import com.k1sak1.goetyawaken.common.entities.ally.illager.CroneServant;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CultistModel<T extends CroneServant> extends HumanoidModel<T> {
        public ModelPart clothes;
        public ModelPart arms;
        public ModelPart all;
        protected final ModelPart nose;

        public CultistModel(ModelPart p_170677_) {
                super(p_170677_);
                this.all = p_170677_;
                this.clothes = p_170677_.getChild("clothes");
                this.arms = p_170677_.getChild("arms");
                this.nose = this.head.getChild("nose");
        }

        public static MeshDefinition createMesh() {
                MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
                PartDefinition partdefinition = meshdefinition.getRoot();
                PartDefinition head = partdefinition.addOrReplaceChild("head",
                                (new CubeListBuilder()).texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F),
                                PartPose.ZERO);
                head.addOrReplaceChild("nose",
                                CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F),
                                PartPose.offset(0.0F, -2.0F, 0.0F));
                partdefinition.addOrReplaceChild("hat",
                                CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F,
                                                8.0F, 10.0F, 8.0F, new CubeDeformation(0.5F)),
                                PartPose.ZERO);
                partdefinition.addOrReplaceChild(
                                "body",
                                CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F)
                                                .texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F,
                                                                new CubeDeformation(0.05F)),
                                PartPose.ZERO);
                partdefinition.addOrReplaceChild(
                                "clothes",
                                CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F)
                                                .texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 20.0F, 6.0F,
                                                                new CubeDeformation(0.05F)),
                                PartPose.ZERO);
                partdefinition.addOrReplaceChild("arms",
                                CubeListBuilder.create().texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F)
                                                .texOffs(44, 22)
                                                .mirror().addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, true)
                                                .texOffs(40, 38)
                                                .addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F),
                                PartPose.offsetAndRotation(0.0F, 3.0F, -1.0F, -0.75F, 0.0F, 0.0F));
                partdefinition.addOrReplaceChild("right_leg",
                                CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                                PartPose.offset(-2.0F, 12.0F, 0.0F));
                partdefinition.addOrReplaceChild("left_leg",
                                CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                                PartPose.offset(2.0F, 12.0F, 0.0F));
                return meshdefinition;
        }

        public static LayerDefinition createBodyLayer() {
                return LayerDefinition.create(createMesh(), 64, 64);
        }

        @Override
        protected Iterable<ModelPart> bodyParts() {
                return Iterables.concat(super.bodyParts(), ImmutableList.of(this.arms, this.clothes, this.all));
        }

        public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                        float headPitch) {
                this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
                this.head.xRot = headPitch * ((float) Math.PI / 180F);
                this.hat.yRot = netHeadYaw * ((float) Math.PI / 180F);
                this.hat.xRot = headPitch * ((float) Math.PI / 180F);
                this.arms.z = -1.0F;
                this.arms.xRot = -0.75F;
                if (this.riding) {
                        this.rightLeg.xRot = -1.4137167F;
                        this.rightLeg.yRot = ((float) Math.PI / 10F);
                        this.rightLeg.zRot = 0.07853982F;
                        this.leftLeg.xRot = -1.4137167F;
                        this.leftLeg.yRot = (-(float) Math.PI / 10F);
                        this.leftLeg.zRot = -0.07853982F;
                } else {
                        this.arms.y = 3.0F;
                        this.rightLeg.y = 12.0F;
                        this.leftLeg.y = 12.0F;
                        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
                        this.rightLeg.yRot = 0.0F;
                        this.rightLeg.zRot = 0.0F;
                        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount
                                        * 0.5F;
                        this.leftLeg.yRot = 0.0F;
                        this.leftLeg.zRot = 0.0F;
                }
        }

        public ModelPart func_205062_a() {
                return this.hat;
        }

        public ModelPart getHead() {
                return this.head;
        }

        public void translateToHand(HumanoidArm sideIn, PoseStack matrixStackIn) {
                this.getArm(sideIn).translateAndRotate(matrixStackIn);
        }
}