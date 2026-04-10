package com.k1sak1.goetyawaken.client.model.undead.necromancer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.animation.undead.necromancer.WraithNecromancerAnimations;
import com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer.AbstractWraithNecromancer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class WraithNecromancerModel<T extends AbstractWraithNecromancer> extends HierarchicalModel<T> {
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "wraithnecromancermodel"), "main");
        public final ModelPart root;
        public final ModelPart skeleton;
        public final ModelPart body;
        public final ModelPart spine;
        public final ModelPart ribs;
        public final ModelPart body_robe;
        public final ModelPart right_arm;
        public final ModelPart right_robe;
        public final ModelPart right_bone;
        public final ModelPart staff;
        public final ModelPart handle;
        public final ModelPart group;
        public final ModelPart staffhead;
        public final ModelPart right_pauldron;
        public final ModelPart left_arm;
        public final ModelPart left_robe;
        public final ModelPart left_bone;
        public final ModelPart left_pauldron;
        public final ModelPart head;
        public final ModelPart hat;
        public final ModelPart hat1;
        public final ModelPart cape;

        public WraithNecromancerModel(ModelPart root) {
                this.root = root;
                this.skeleton = root.getChild("skeleton");
                this.body = this.skeleton.getChild("body");
                this.spine = this.body.getChild("spine");
                this.ribs = this.spine.getChild("ribs");
                this.body_robe = this.body.getChild("body_robe");
                this.right_arm = this.body.getChild("right_arm");
                this.right_robe = this.right_arm.getChild("right_robe");
                this.right_bone = this.right_arm.getChild("right_bone");
                this.staff = this.right_bone.getChild("staff");
                this.handle = this.staff.getChild("handle");
                this.group = this.staff.getChild("group");
                this.staffhead = this.staff.getChild("staffhead");
                this.right_pauldron = this.right_arm.getChild("right_pauldron");
                this.left_arm = this.body.getChild("left_arm");
                this.left_robe = this.left_arm.getChild("left_robe");
                this.left_bone = this.left_arm.getChild("left_bone");
                this.left_pauldron = this.left_arm.getChild("left_pauldron");
                this.head = this.body.getChild("head");
                this.hat = this.head.getChild("hat");
                this.hat1 = this.head.getChild("hat1");
                this.cape = this.body.getChild("cape");
        }

        public static LayerDefinition createBodyLayer() {
                MeshDefinition meshdefinition = new MeshDefinition();
                PartDefinition partdefinition = meshdefinition.getRoot();

                PartDefinition skeleton = partdefinition.addOrReplaceChild("skeleton", CubeListBuilder.create(),
                                PartPose.offset(0.0F, 24.0F, 0.0F));

                PartDefinition body = skeleton.addOrReplaceChild("body", CubeListBuilder.create(),
                                PartPose.offsetAndRotation(0.0F, -24.0F, 0.0F, 0.0F, -0.2618F, 0.0F));

                PartDefinition spine = body.addOrReplaceChild(
                                "spine", CubeListBuilder.create().texOffs(0, 16).addBox(-0.5F, -1.0F, -1.5F, 1.0F,
                                                12.0F, 1.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition ribs = spine
                                .addOrReplaceChild("ribs",
                                                CubeListBuilder.create().texOffs(8, 17).addBox(-3.0F, 0.0F, -4.0F, 6.0F,
                                                                12.0F, 4.0F, new CubeDeformation(0.0F)),
                                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition body_robe = body.addOrReplaceChild(
                                "body_robe", CubeListBuilder.create().texOffs(35, 34).addBox(-4.0F, -1.0F, -2.0F, 8.0F,
                                                20.0F, 4.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create(),
                                PartPose.offsetAndRotation(-6.0F, 1.0F, -1.0F, -1.3963F, 0.2618F, 0.0F));

                PartDefinition right_robe = right_arm.addOrReplaceChild(
                                "right_robe", CubeListBuilder.create().texOffs(0, 33).addBox(-2.0F, -3.0F, -2.0F, 4.0F,
                                                14.0F, 4.0F, new CubeDeformation(0.1F)),
                                PartPose.offset(0.0F, 0.0F, 1.0F));

                PartDefinition right_bone = right_arm.addOrReplaceChild("right_bone", CubeListBuilder.create()
                                .texOffs(0, 16).mirror()
                                .addBox(-0.5F, -2.0F, -0.5F, 1.0F, 12.0F, 1.0F, new CubeDeformation(-0.01F))
                                .mirror(false)
                                .texOffs(12, 111).mirror()
                                .addBox(-1.5F, 6.1F, -1.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                                .texOffs(12, 119).addBox(-1.5F, 5.1F, -1.0F, 3.0F, 1.0F, 4.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition staff = right_bone.addOrReplaceChild("staff", CubeListBuilder.create(),
                                PartPose.offsetAndRotation(0.0F, 10.0F, 6.5F, 1.4399F, 0.0F, 0.0F));

                PartDefinition handle = staff.addOrReplaceChild("handle", CubeListBuilder.create().texOffs(67, 69)
                                .addBox(-0.5F, -20.0F, -20.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                                .texOffs(69, 102)
                                .addBox(-0.5F, -19.0F, -20.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                                .texOffs(79, 69)
                                .addBox(-0.5F, -21.0F, -20.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                                .texOffs(66, 88)
                                .addBox(-2.5F, -21.0F, -20.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                                .texOffs(68, 97)
                                .addBox(2.5F, -21.0F, -20.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                                .texOffs(66, 92)
                                .addBox(-0.5F, -21.0F, -22.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                                .texOffs(85, 94)
                                .addBox(-0.5F, -20.0F, -22.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                                .texOffs(79, 94)
                                .addBox(-0.5F, -20.0F, -15.0F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                                .texOffs(77, 99).addBox(-0.5F, -21.0F, -17.0F, 3.0F, 1.0F, 2.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offset(-1.0F, 7.0F, 19.0F));

                PartDefinition cube_r1 = handle.addOrReplaceChild("cube_r1",
                                CubeListBuilder.create().texOffs(79, 92).addBox(-1.5F, -17.0F, -0.5F, 3.0F, 2.0F, 0.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offsetAndRotation(-3.0F, -3.0F, -18.5F, 0.0F, -1.5708F, 0.0F));

                PartDefinition cube_r2 = handle.addOrReplaceChild("cube_r2",
                                CubeListBuilder.create().texOffs(79, 96).addBox(-1.5F, -17.0F, -0.5F, 3.0F, 2.0F, 0.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offsetAndRotation(4.0F, -3.0F, -18.5F, 0.0F, -1.5708F, 0.0F));

                PartDefinition group = staff.addOrReplaceChild(
                                "group", CubeListBuilder.create().texOffs(60, 50).addBox(0.5F, -15.0F, -19.0F, 1.0F,
                                                27.0F, 1.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(-1.0F, 3.0F, 19.0F));

                PartDefinition staffhead = staff.addOrReplaceChild(
                                "staffhead", CubeListBuilder.create().texOffs(48, 61).addBox(-0.5F, -20.0F, -20.0F,
                                                3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(-1.0F, 3.0F, 19.0F));

                PartDefinition right_pauldron = right_arm.addOrReplaceChild("right_pauldron", CubeListBuilder.create()
                                .texOffs(0, 80).addBox(-3.0F, -8.0F, 3.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 84).addBox(-4.0F, -6.0F, -3.0F, 5.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 59).addBox(-4.0F, -4.0F, -3.0F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.1F))
                                .texOffs(68, 52).mirror()
                                .addBox(-4.0F, -4.0F, -3.0F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.25F))
                                .mirror(false),
                                PartPose.offsetAndRotation(1.0F, -1.0F, 1.0F, 1.0472F, -0.0873F, -0.2618F));

                PartDefinition r_shouldpad_r1 = right_pauldron.addOrReplaceChild("r_shouldpad_r1",
                                CubeListBuilder.create().texOffs(0, 76).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 0.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offsetAndRotation(-5.0F, -6.0F, -1.0F, 0.0F, -1.5708F, 0.0F));

                PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create(),
                                PartPose.offsetAndRotation(6.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

                PartDefinition left_robe = left_arm.addOrReplaceChild(
                                "left_robe", CubeListBuilder.create().texOffs(16, 33).addBox(-2.0F, -3.0F, -2.0F, 4.0F,
                                                14.0F, 4.0F, new CubeDeformation(0.1F)),
                                PartPose.offset(0.0F, 1.0F, 0.0F));

                PartDefinition left_bone = left_arm.addOrReplaceChild(
                                "left_bone", CubeListBuilder.create().texOffs(0, 16).addBox(-0.5F, -2.0F, -0.5F, 1.0F,
                                                12.0F, 1.0F, new CubeDeformation(-0.01F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition left_pauldron = left_arm.addOrReplaceChild("left_pauldron", CubeListBuilder.create()
                                .texOffs(0, 59).mirror()
                                .addBox(-1.0F, -4.0F, -3.0F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.1F)).mirror(false)
                                .texOffs(0, 96).addBox(-1.0F, -8.0F, -3.0F, 2.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 92).addBox(0.0F, -8.0F, 3.0F, 2.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                                .texOffs(92, 38).addBox(-1.0F, -4.0F, -3.0F, 5.0F, 5.0F, 6.0F,
                                                new CubeDeformation(0.25F)),
                                PartPose.offset(-1.0F, 0.0F, 0.0F));

                PartDefinition l_shouldpad_r1 = left_pauldron.addOrReplaceChild("l_shouldpad_r1",
                                CubeListBuilder.create().texOffs(0, 88).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 0.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offsetAndRotation(3.0F, -6.0F, -2.0F, 0.0F, -1.5708F, 0.0F));

                PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(2, 3)
                                .addBox(-3.0F, -7.0F, -2.0F, 6.0F, 7.0F, 6.0F, new CubeDeformation(0.0F))
                                .texOffs(32, 0)
                                .addBox(-3.975F, -7.8F, -3.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.03F))
                                .texOffs(96, 98)
                                .addBox(-3.975F, -7.8F, -3.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.25F)),
                                PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.2618F, 0.0F));

                PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(64, 0)
                                .addBox(-5.0F, -6.5F, -5.0F, 10.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
                                .texOffs(65, 16).addBox(-5.0F, -5.5F, -5.0F, 10.0F, 5.0F, 10.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, -5.0F, 1.0F));

                PartDefinition hat1 = head.addOrReplaceChild("hat1", CubeListBuilder.create().texOffs(-2, 119)
                                .addBox(-5.0F, -8.0F, -1.0F, 3.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 121)
                                .addBox(-4.0F, -11.0F, -4.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 121)
                                .addBox(-2.0F, -11.0F, 4.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 121)
                                .addBox(-6.0F, -11.0F, 0.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 121).addBox(0.0F, -11.0F, 0.0F, 4.0F, 4.0F, 0.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, 1.0F));

                PartDefinition head_r1 = hat1.addOrReplaceChild("head_r1",
                                CubeListBuilder.create().texOffs(0, 115).addBox(-3.0F, -2.0F, -1.0F, 4.0F, 4.0F, 0.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offsetAndRotation(3.0F, -9.0F, -2.0F, 0.0F, -1.5708F, 0.0F));

                PartDefinition head_r2 = hat1.addOrReplaceChild("head_r2",
                                CubeListBuilder.create().texOffs(0, 115).addBox(-3.0F, -2.0F, -1.0F, 4.0F, 4.0F, 0.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offsetAndRotation(-5.0F, -9.0F, 1.0F, 0.0F, -1.5708F, 0.0F));

                PartDefinition cape = body.addOrReplaceChild(
                                "cape", CubeListBuilder.create().texOffs(24, 75).addBox(-8.0F, 0.0F, -2.0F, 16.0F,
                                                24.0F, 4.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, 1.0F));

                return LayerDefinition.create(meshdefinition, 128, 128);
        }

        @Override
        public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                        float netHeadYaw,
                        float headPitch) {
                this.root().getAllParts().forEach(ModelPart::resetPose);
                if (!entity.isDeadOrDying()) {
                        if (entity.cantDo > 0) {
                                this.head.zRot = 0.3F * Mth.sin(0.45F * ageInTicks);
                                this.head.xRot = 0.4F;
                        } else {
                                this.animateHeadLookTarget(netHeadYaw, headPitch);
                        }
                }
                if (entity instanceof com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.WraithNecromancer) {
                        com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.WraithNecromancer wraith = (com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.WraithNecromancer) entity;
                        this.animate(wraith.idleAnimationState, WraithNecromancerAnimations.IDLE, ageInTicks);
                        this.animate(wraith.flyAnimationState, WraithNecromancerAnimations.FLY, ageInTicks);
                        this.animate(wraith.attackAnimationState, WraithNecromancerAnimations.ATTACK, ageInTicks);
                        this.animate(wraith.summonAnimationState, WraithNecromancerAnimations.SUMMON, ageInTicks);
                        this.animate(wraith.spellAnimationState, WraithNecromancerAnimations.SPELL, ageInTicks);
                        this.animate(wraith.alertAnimationState, WraithNecromancerAnimations.ALERT, ageInTicks);
                } else if (entity instanceof com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer.WraithNecromancerServant) {
                        com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer.WraithNecromancerServant servant = (com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer.WraithNecromancerServant) entity;
                        this.animate(servant.idleAnimationState, WraithNecromancerAnimations.IDLE, ageInTicks);
                        this.animate(servant.flyAnimationState, WraithNecromancerAnimations.FLY, ageInTicks);
                        this.animate(servant.attackAnimationState, WraithNecromancerAnimations.ATTACK, ageInTicks);
                        this.animate(servant.summonAnimationState, WraithNecromancerAnimations.SUMMON, ageInTicks);
                        this.animate(servant.spellAnimationState, WraithNecromancerAnimations.SPELL, ageInTicks);
                        this.animate(servant.alertAnimationState, WraithNecromancerAnimations.ALERT, ageInTicks);
                        this.animate(servant.shockwaveAnimationState, WraithNecromancerAnimations.SHOCKWAVE,
                                        ageInTicks);
                }
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                        int packedOverlay,
                        float red, float green, float blue, float alpha) {
                skeleton.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }

        private void animateHeadLookTarget(float netHeadYaw, float headPitch) {
                this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
                this.head.xRot = headPitch * ((float) Math.PI / 180F);
        }

        @Override
        public ModelPart root() {
                return this.root;
        }
}