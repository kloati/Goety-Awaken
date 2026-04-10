package com.k1sak1.goetyawaken.client.model.undead.necromancer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.AbstractNamelessOne;
import com.k1sak1.goetyawaken.client.animation.undead.necromancer.NamelessOneAnimation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class NamelessOneModel<T extends AbstractNamelessOne> extends HierarchicalModel<T> {
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "nameless_one"), "main");
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
        private final ModelPart night1;
        private final ModelPart night2;
        private final ModelPart night3;
        private final ModelPart left_arm;
        private final ModelPart leftItem;
        private final ModelPart left_pauldron;
        private final ModelPart pants;
        private final ModelPart middle;
        private final ModelPart cape;

        public NamelessOneModel(ModelPart root) {
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
                this.night1 = this.staffhead.getChild("night1");
                this.night2 = this.staffhead.getChild("night2");
                this.night3 = this.staffhead.getChild("night3");
                this.left_arm = this.body.getChild("left_arm");
                this.leftItem = this.left_arm.getChild("leftItem");
                this.left_pauldron = this.left_arm.getChild("left_pauldron");
                this.pants = this.body.getChild("pants");
                this.middle = this.pants.getChild("middle");
                this.cape = this.body.getChild("cape");
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

                PartDefinition hat = head
                                .addOrReplaceChild("hat",
                                                CubeListBuilder.create().texOffs(0, 96).addBox(-4.0F, -10.5F, -4.0F,
                                                                8.0F, 6.0F, 8.0F, new CubeDeformation(0.5F)),
                                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition right_arm = body.addOrReplaceChild("right_arm",
                                CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offsetAndRotation(-5.0F, -10.0F, 0.0F, -1.3963F, 0.2618F, 0.0F));

                PartDefinition right_pauldron = right_arm.addOrReplaceChild("right_pauldron",
                                CubeListBuilder.create().texOffs(0, 49).addBox(-5.0F, -4.0F, -3.0F, 6.0F, 5.0F, 6.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 1.0472F, -0.0873F, -0.2618F));

                PartDefinition staff = right_arm.addOrReplaceChild("staff", CubeListBuilder.create(),
                                PartPose.offsetAndRotation(0.0F, 10.0F, 5.5F, 1.4399F, 0.0F, 0.0F));

                PartDefinition handle = staff.addOrReplaceChild(
                                "handle", CubeListBuilder.create().texOffs(60, 39).addBox(0.5F, -16.0F, -19.0F, 1.0F,
                                                24.0F, 1.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(-1.0F, 7.0F, 19.0F));

                PartDefinition group = staff.addOrReplaceChild(
                                "group", CubeListBuilder.create().texOffs(48, 43).addBox(-0.5F, -17.0F, -20.0F, 3.0F,
                                                1.0F, 3.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(-1.0F, 7.0F, 19.0F));

                PartDefinition staffhead = staff.addOrReplaceChild("staffhead", CubeListBuilder.create(),
                                PartPose.offset(1.6667F, -8.0F, -1.8333F));

                PartDefinition night1 = staffhead.addOrReplaceChild("night1",
                                CubeListBuilder.create().texOffs(44, 47).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F,
                                                new CubeDeformation(-3.75F)),
                                PartPose.offset(-1.6667F, -5.0F, 2.3333F));

                PartDefinition night2 = staffhead.addOrReplaceChild(
                                "night2", CubeListBuilder.create().texOffs(44, 55).addBox(-2.0F, -2.0F, -2.0F, 4.0F,
                                                4.0F, 4.0F, new CubeDeformation(-5.0F)),
                                PartPose.offset(-1.6667F, -5.0F, 2.3333F));

                PartDefinition night3 = staffhead.addOrReplaceChild(
                                "night3", CubeListBuilder.create().texOffs(44, 35).addBox(-2.0F, -2.0F, -2.0F, 4.0F,
                                                4.0F, 4.0F, new CubeDeformation(-3.0F)),
                                PartPose.offset(-1.6667F, -5.0F, 2.3333F));

                PartDefinition left_arm = body.addOrReplaceChild("left_arm",
                                CubeListBuilder.create().texOffs(40, 16).mirror()
                                                .addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false),
                                PartPose.offsetAndRotation(5.0F, -10.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

                PartDefinition leftItem = left_arm.addOrReplaceChild("leftItem", CubeListBuilder.create(),
                                PartPose.offset(1.0F, 7.0F, 1.0F));

                PartDefinition left_pauldron = left_arm.addOrReplaceChild("left_pauldron",
                                CubeListBuilder.create().texOffs(0, 49).mirror()
                                                .addBox(-1.0F, -4.0F, -3.0F, 6.0F, 6.0F, 6.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false),
                                PartPose.offset(0.0F, 0.0F, 1.0F));

                PartDefinition pants = body.addOrReplaceChild(
                                "pants", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F,
                                                12.0F, 4.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition middle = pants.addOrReplaceChild(
                                "middle", CubeListBuilder.create().texOffs(40, 36).addBox(-1.0F, 0.0F, 0.0F, 2.0F,
                                                10.0F, 0.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, -2.0F));

                PartDefinition cape = body.addOrReplaceChild(
                                "cape", CubeListBuilder.create().texOffs(24, 64).addBox(-8.0F, 0.0F, -2.0F, 16.0F,
                                                24.0F, 4.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, -12.0F, 1.0F));

                return LayerDefinition.create(meshdefinition, 64, 128);
        }

        @Override
        public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                        float netHeadYaw,
                        float headPitch) {
                this.root().getAllParts().forEach(ModelPart::resetPose);
                this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
                this.head.xRot = headPitch * ((float) Math.PI / 180F);
                this.animate(entity.heartofthenightAnimationState, NamelessOneAnimation.HEART_OF_THE_NIGHT,
                                ageInTicks);
                this.animate(entity.deathAnimationState, NamelessOneAnimation.DEAD, ageInTicks);
                if (!entity.isDeadOrDying()) {
                        this.animate(entity.idleAnimationState, NamelessOneAnimation.IDLE, ageInTicks);
                        this.animate(entity.walkAnimationState, NamelessOneAnimation.WALK, ageInTicks);
                        this.animate(entity.attackAnimationState, NamelessOneAnimation.ATTACK, ageInTicks);
                        this.animate(entity.summonAnimationState, NamelessOneAnimation.SUMMON, ageInTicks);
                        this.animate(entity.spellAnimationState, NamelessOneAnimation.SPELL, ageInTicks);
                        this.animate(entity.alertAnimationState, NamelessOneAnimation.ALERT, ageInTicks);
                        this.animate(entity.flyAnimationState, NamelessOneAnimation.FLY, ageInTicks);
                        this.animate(entity.walk2AnimationState, NamelessOneAnimation.WALK2, ageInTicks);
                        this.animate(entity.updrafAnimationState, NamelessOneAnimation.UPDRAFT, ageInTicks);
                        this.animate(entity.stormAnimationState, NamelessOneAnimation.STORM, ageInTicks);
                        this.animate(entity.storm2AnimationState, NamelessOneAnimation.STORM2, ageInTicks);
                        this.animate(entity.rapidAnimationState, NamelessOneAnimation.RAPID, ageInTicks);
                        this.animate(entity.rangeSpellAttackAnimationState, NamelessOneAnimation.RANGE_SPELL_ATTACK,
                                        ageInTicks);

                        this.animate(entity.heartofthenightAnimationState, NamelessOneAnimation.HEART_OF_THE_NIGHT,
                                        ageInTicks);
                        this.animate(entity.teleportoutAnimationState, NamelessOneAnimation.TELEPORTOUT, ageInTicks);
                        this.animate(entity.teleportinAnimationState, NamelessOneAnimation.TELEPORTIN, ageInTicks);
                        this.animate(entity.wakeAnimationState, NamelessOneAnimation.WAKE, ageInTicks);
                        this.animate(entity.avadaAnimationState, NamelessOneAnimation.AVADA, ageInTicks);
                        this.animate(entity.quake1AnimationState, NamelessOneAnimation.QUAKE1, ageInTicks);
                        this.animate(entity.quake2AnimationState, NamelessOneAnimation.QUAKE2, ageInTicks);
                        this.animate(entity.slowSpellAnimationState, NamelessOneAnimation.SLOW_SPELL, ageInTicks);
                        this.animate(entity.leechingSpellAnimationState, NamelessOneAnimation.LEECHING_SPELL,
                                        ageInTicks);
                        this.animate(entity.stabAnimationState, NamelessOneAnimation.STAB, ageInTicks);
                        this.animate(entity.breatheAnimationState, NamelessOneAnimation.BREATHE, ageInTicks);
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