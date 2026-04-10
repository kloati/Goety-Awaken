package com.k1sak1.goetyawaken.client.model.undead.necromancer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.AbstractParchedNecromancer;
import com.k1sak1.goetyawaken.client.animation.undead.necromancer.ParchedNecromancerAnimation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class ParchedNecromancerModel<T extends AbstractParchedNecromancer> extends HierarchicalModel<T> {
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "parched_necromancer"), "main");
        private final ModelPart skeleton;
        private final ModelPart body;
        private final ModelPart head;
        private final ModelPart hat;
        private final ModelPart right_arm;
        private final ModelPart right_pauldron;
        private final ModelPart staff;
        private final ModelPart handle;
        private final ModelPart group;
        private final ModelPart staffhead;
        private final ModelPart left_arm;
        private final ModelPart leftItem;
        private final ModelPart left_pauldron;
        private final ModelPart pants;
        private final ModelPart middle;
        private final ModelPart cape;
        private final ModelPart right_leg;
        private final ModelPart left_leg;

        public ParchedNecromancerModel(ModelPart root) {
                this.skeleton = root.getChild("skeleton");
                this.body = this.skeleton.getChild("body");
                this.head = this.body.getChild("head");
                this.hat = this.head.getChild("hat");
                this.right_arm = this.body.getChild("right_arm");
                this.right_pauldron = this.right_arm.getChild("right_pauldron");
                this.staff = this.right_arm.getChild("staff");
                this.handle = this.staff.getChild("handle");
                this.group = this.staff.getChild("group");
                this.staffhead = this.staff.getChild("staffhead");
                this.left_arm = this.body.getChild("left_arm");
                this.leftItem = this.left_arm.getChild("leftItem");
                this.left_pauldron = this.left_arm.getChild("left_pauldron");
                this.pants = this.body.getChild("pants");
                this.middle = this.pants.getChild("middle");
                this.cape = this.body.getChild("cape");
                this.right_leg = this.skeleton.getChild("right_leg");
                this.left_leg = this.skeleton.getChild("left_leg");
        }

        public static LayerDefinition createBodyLayer() {
                MeshDefinition meshdefinition = new MeshDefinition();
                PartDefinition partdefinition = meshdefinition.getRoot();

                PartDefinition skeleton = partdefinition.addOrReplaceChild("skeleton", CubeListBuilder.create(),
                                PartPose.offset(0.0F, 24.0F, 0.0F));

                PartDefinition body = skeleton.addOrReplaceChild("body",
                                CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0F, -0.2618F, 0.0F));

                PartDefinition head = body.addOrReplaceChild("head",
                                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0F, 0.2618F, 0.0F));

                PartDefinition hat = head.addOrReplaceChild("hat",
                                CubeListBuilder.create().texOffs(0, 92).addBox(-5.0F, -12.5F,
                                                -5.0F, 10.0F, 8.0F, 10.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition right_arm = body.addOrReplaceChild("right_arm",
                                CubeListBuilder.create().texOffs(40, 16)
                                                .addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(48, 14).mirror()
                                                .addBox(-1.5F, -1.9F, -2.0F, 3.0F, 12.0F, 4.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false),
                                PartPose.offsetAndRotation(-5.0F, -10.0F, 0.0F, -1.3963F, 0.2618F, 0.0F));

                PartDefinition right_pauldron = right_arm.addOrReplaceChild("right_pauldron",
                                CubeListBuilder.create().texOffs(0, 49)
                                                .addBox(-5.0F, -4.0F, -3.0F, 6.0F, 5.0F, 6.0F,
                                                                new CubeDeformation(0.25F))
                                                .texOffs(0, 63).addBox(-7.0F, -7.0F, 0.25F, 8.0F, 9.0F, 1.0F,
                                                                new CubeDeformation(0.25F)),
                                PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 1.0472F, -0.0873F, -0.2618F));

                PartDefinition cube_r1 = right_pauldron.addOrReplaceChild("cube_r1",
                                CubeListBuilder.create().texOffs(0, 74).mirror()
                                                .addBox(9.1F, -8.5F, 0.4F, 8.0F, 11.0F, 1.0F,
                                                                new CubeDeformation(0.25F))
                                                .mirror(false),
                                PartPose.offsetAndRotation(-16.0F, -3.0F, 3.0F, 1.5708F, 0.0F, 0.0F));

                PartDefinition staff = right_arm.addOrReplaceChild("staff", CubeListBuilder.create(),
                                PartPose.offsetAndRotation(0.0F, 10.0F, 5.5F, 1.4399F, 0.0F, 0.0F));

                PartDefinition handle = staff.addOrReplaceChild("handle",
                                CubeListBuilder.create().texOffs(60, 39).addBox(0.5F,
                                                -20.0F, -19.0F, 1.0F, 28.0F, 1.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(-1.0F, 7.0F, 19.0F));

                PartDefinition group = staff.addOrReplaceChild("group",
                                CubeListBuilder.create().texOffs(48, 46)
                                                .addBox(-0.5F, -17.0F, -20.0F, 3.0F, 1.0F, 3.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(50, 56)
                                                .addBox(-4.5F, -21.0F, -19.0F, 4.0F, 7.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(50, 56)
                                                .addBox(-4.5F, -21.0F, -18.0F, 4.0F, 7.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(56, 40)
                                                .addBox(-3.5F, -20.0F, -19.0F, 1.0F, 5.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(56, 40).mirror()
                                                .addBox(4.5F, -20.0F, -19.0F, 1.0F, 5.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false)
                                                .texOffs(52, 39)
                                                .addBox(-2.5F, -20.0F, -19.0F, 1.0F, 5.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(52, 39).mirror()
                                                .addBox(3.5F, -20.0F, -19.0F, 1.0F, 5.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false)
                                                .texOffs(60, 33)
                                                .addBox(-1.5F, -19.0F, -19.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(60, 33)
                                                .addBox(2.5F, -19.0F, -19.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(60, 31)
                                                .addBox(-1.5F, -17.0F, -19.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(60, 31)
                                                .addBox(2.5F, -17.0F, -19.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(59, 36)
                                                .addBox(-5.5F, -16.0F, -18.5F, 2.0F, 1.0F, 0.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(57, 33)
                                                .addBox(-2.5F, -15.0F, -18.5F, 1.0F, 2.0F, 0.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(59, 36).mirror()
                                                .addBox(5.5F, -16.0F, -18.5F, 2.0F, 1.0F, 0.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false)
                                                .texOffs(57, 33).mirror()
                                                .addBox(4.5F, -15.0F, -18.5F, 1.0F, 2.0F, 0.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false)
                                                .texOffs(59, 38)
                                                .addBox(-5.5F, -19.0F, -18.5F, 2.0F, 1.0F, 0.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(57, 31)
                                                .addBox(-2.5F, -22.0F, -18.5F, 1.0F, 2.0F, 0.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(59, 38).mirror()
                                                .addBox(5.5F, -19.0F, -18.5F, 2.0F, 1.0F, 0.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false)
                                                .texOffs(57, 31).mirror()
                                                .addBox(3.5F, -22.0F, -18.5F, 1.0F, 2.0F, 0.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false)
                                                .texOffs(50, 56).mirror()
                                                .addBox(2.5F, -21.0F, -18.0F, 4.0F, 7.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false)
                                                .texOffs(50, 56).mirror()
                                                .addBox(2.5F, -21.0F, -19.0F, 4.0F, 7.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false),
                                PartPose.offset(-1.0F, 3.0F, 19.0F));

                PartDefinition staffhead = staff.addOrReplaceChild("staffhead", CubeListBuilder.create().texOffs(48, 50)
                                .addBox(-0.5F, -20.0F, -20.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(-1.0F, 3.0F, 19.0F));

                PartDefinition left_arm = body.addOrReplaceChild("left_arm",
                                CubeListBuilder.create().texOffs(40, 16).mirror()
                                                .addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false)
                                                .texOffs(48, 14).addBox(-1.5F, -1.9F, -2.0F, 3.0F, 12.0F, 4.0F,
                                                                new CubeDeformation(0.0F)),
                                PartPose.offsetAndRotation(5.0F, -10.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

                PartDefinition leftItem = left_arm.addOrReplaceChild("leftItem", CubeListBuilder.create(),
                                PartPose.offset(1.0F, 7.0F, 1.0F));

                PartDefinition left_pauldron = left_arm.addOrReplaceChild("left_pauldron",
                                CubeListBuilder.create().texOffs(0, 49).mirror()
                                                .addBox(-1.0F, -4.0F, -3.0F, 6.0F, 6.0F, 6.0F,
                                                                new CubeDeformation(0.25F))
                                                .mirror(false)
                                                .texOffs(0, 63).mirror()
                                                .addBox(-1.0F, -7.0F, 0.25F, 8.0F, 9.0F, 1.0F,
                                                                new CubeDeformation(0.25F))
                                                .mirror(false),
                                PartPose.offset(0.0F, 0.0F, 1.0F));

                PartDefinition cube_r2 = left_pauldron.addOrReplaceChild("cube_r2",
                                CubeListBuilder.create().texOffs(0, 74).addBox(7.1F, -8.5F, 0.4F, 8.0F, 11.0F, 1.0F,
                                                new CubeDeformation(0.25F)),
                                PartPose.offsetAndRotation(-8.0F, -3.0F, 3.0F, 1.5708F, 0.0F, 0.0F));

                PartDefinition pants = body.addOrReplaceChild("pants",
                                CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F,
                                                0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition middle = pants.addOrReplaceChild("middle",
                                CubeListBuilder.create().texOffs(40, 36).addBox(-1.0F,
                                                0.0F, 0.0F, 2.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, -2.0F));

                PartDefinition cape = body.addOrReplaceChild("cape",
                                CubeListBuilder.create().texOffs(24, 64).addBox(-8.0F,
                                                0.0F, -2.0F, 16.0F, 24.0F, 4.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, -12.0F, 1.0F));

                PartDefinition right_leg = skeleton.addOrReplaceChild("right_leg",
                                CubeListBuilder.create().texOffs(0, 16)
                                                .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 30).addBox(-2.0F, 0.15F, -2.0F, 4.0F, 12.0F, 4.0F,
                                                                new CubeDeformation(-0.1F)),
                                PartPose.offsetAndRotation(-2.0F, -12.0F, 0.0F, 0.0F, 0.0F, 0.0436F));

                PartDefinition left_leg = skeleton.addOrReplaceChild("left_leg",
                                CubeListBuilder.create().texOffs(0, 16).mirror()
                                                .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false)
                                                .texOffs(0, 30).mirror()
                                                .addBox(-2.0F, 0.15F, -2.0F, 4.0F, 12.0F, 4.0F,
                                                                new CubeDeformation(-0.1F))
                                                .mirror(false),
                                PartPose.offsetAndRotation(2.0F, -12.0F, 0.0F, 0.0F, 0.0F, -0.1309F));

                return LayerDefinition.create(meshdefinition, 64, 128);
        }

        @Override
        public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                        float netHeadYaw,
                        float headPitch) {
                this.root().getAllParts().forEach(ModelPart::resetPose);
                this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
                this.head.xRot = headPitch * ((float) Math.PI / 180F);

                if (!entity.isDeadOrDying()) {
                        this.animate(entity.idleAnimationState, ParchedNecromancerAnimation.IDLE, ageInTicks);
                        this.animate(entity.walkAnimationState, ParchedNecromancerAnimation.WALK, ageInTicks);
                        this.animate(entity.attackAnimationState, ParchedNecromancerAnimation.ATTACK, ageInTicks);
                        this.animate(entity.summonAnimationState, ParchedNecromancerAnimation.SUMMON, ageInTicks);
                        this.animate(entity.spellAnimationState, ParchedNecromancerAnimation.SPELL, ageInTicks);
                        this.animate(entity.alertAnimationState, ParchedNecromancerAnimation.ALERT, ageInTicks);
                        this.animate(entity.flyAnimationState, ParchedNecromancerAnimation.FLY, ageInTicks);
                        this.animate(entity.walk2AnimationState, ParchedNecromancerAnimation.WALK2, ageInTicks);
                        this.animate(entity.updrafAnimationState, ParchedNecromancerAnimation.UPDRAFT, ageInTicks);
                        this.animate(entity.stormAnimationState, ParchedNecromancerAnimation.STORM, ageInTicks);
                        this.animate(entity.rapidAnimationState, ParchedNecromancerAnimation.RAPID, ageInTicks);
                }
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                        int packedOverlay,
                        float red, float green, float blue, float alpha) {
                skeleton.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }

        @Override
        public ModelPart root() {
                return skeleton;
        }
}