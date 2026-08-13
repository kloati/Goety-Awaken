package com.k1sak1.goetyawaken.client.model.undead.necromancer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.AbstractNamelessOne;
import com.k1sak1.goetyawaken.client.animation.undead.necromancer.NamelessOneAnimation;
import com.Polarice3.Goety.utils.ModelPartPose;
import com.Polarice3.Goety.utils.ModelUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class NamelessOneModel<T extends AbstractNamelessOne> extends HierarchicalModel<T> {
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "nameless_one"), "main");
        private final ModelPart skeleton;
        private final ModelPart body;
        private final ModelPart head;
        private final ModelPart hat;
        private final ModelPart crown;
        private final ModelPart crown1;
        private final ModelPart crown2;
        private final ModelPart right_arm;
        private final ModelPart staff;
        private final ModelPart handle;
        private final ModelPart group;
        private final ModelPart staffhead;
        private final ModelPart night1;
        private final ModelPart night3;
        private final ModelPart night2;
        private final ModelPart right_pauldron;
        private final ModelPart left_arm;
        private final ModelPart leftItem;
        private final ModelPart left_pauldron;
        private final ModelPart pants;
        private final ModelPart middle;
        private final ModelPart cape;
        private final ModelPart collar;
        public final List<String> allPartNames;
        private static final int TRANSITION_DURATION = 5;

        public NamelessOneModel(ModelPart root) {
                this.skeleton = root.getChild("skeleton");
                this.body = this.skeleton.getChild("body");
                this.head = this.body.getChild("head");
                this.hat = this.head.getChild("hat");
                this.crown = this.head.getChild("crown");
                this.crown1 = this.crown.getChild("crown1");
                this.crown2 = this.crown1.getChild("crown2");
                this.right_arm = this.body.getChild("right_arm");
                this.staff = this.right_arm.getChild("staff");
                this.handle = this.staff.getChild("handle");
                this.group = this.staff.getChild("group");
                this.staffhead = this.staff.getChild("staffhead");
                this.night1 = this.staffhead.getChild("night1");
                this.night3 = this.night1.getChild("night3");
                this.night2 = this.staffhead.getChild("night2");
                this.right_pauldron = this.right_arm.getChild("right_pauldron");
                this.left_arm = this.body.getChild("left_arm");
                this.leftItem = this.left_arm.getChild("leftItem");
                this.left_pauldron = this.left_arm.getChild("left_pauldron");
                this.pants = this.body.getChild("pants");
                this.middle = this.pants.getChild("middle");
                this.cape = this.body.getChild("cape");
                this.collar = this.body.getChild("collar");
                this.allPartNames = Stream.concat(Stream.of("root"), ModelUtil.getAllPartNames(this.skeleton)).toList();
        }

        public static LayerDefinition createBodyLayer() {
                return createBodyLayer(0.0F);
        }

        public static LayerDefinition createShadowLayer() {
                return createBodyLayer(-0.05F);
        }

        public static LayerDefinition createBodyLayer(float deformation) {
                MeshDefinition meshdefinition = new MeshDefinition();
                PartDefinition partdefinition = meshdefinition.getRoot();

                PartDefinition skeleton = partdefinition.addOrReplaceChild("skeleton", CubeListBuilder.create(),
                                PartPose.offset(0.0F, 24.0F, 0.0F));

                PartDefinition body = skeleton.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 44)
                                .addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(deformation))
                                .texOffs(0, 112)
                                .addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F,
                                                new CubeDeformation(0.25F + deformation)),
                                PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0F, -0.2618F, 0.0F));

                PartDefinition head = body.addOrReplaceChild("head",
                                CubeListBuilder.create().texOffs(42, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F,
                                                new CubeDeformation(deformation)),
                                PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0F, 0.2618F, 0.0F));

                PartDefinition hat = head
                                .addOrReplaceChild("hat",
                                                CubeListBuilder.create().texOffs(86, 103).addBox(-5.0F, -9.0F, -4.0F,
                                                                8.0F, 8.0F, 8.0F,
                                                                new CubeDeformation(0.5F + deformation)),
                                                PartPose.offset(1.0F, -2.0F, 0.0F));

                PartDefinition crown = head.addOrReplaceChild("crown", CubeListBuilder.create(),
                                PartPose.offset(0.0F, -4.0F, 0.0F));

                PartDefinition crown1 = crown.addOrReplaceChild("crown1", CubeListBuilder.create().texOffs(56, 45)
                                .addBox(-4.0F, -1.0F, -4.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(deformation))
                                .texOffs(42, 16).addBox(-4.0F, -7.0F, -4.0F, 8.0F, 7.0F, 8.0F,
                                                new CubeDeformation(deformation)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition crown2 = crown1.addOrReplaceChild(
                                "crown2", CubeListBuilder.create().texOffs(42, 31).addBox(-4.0F, -6.0F, -4.0F, 8.0F,
                                                6.0F, 8.0F, new CubeDeformation(deformation)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(70, 69)
                                .addBox(-1.05F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(deformation))
                                .texOffs(0, 60)
                                .addBox(-1.75F, -2.05F, -1.25F, 3.0F, 12.0F, 4.0F, new CubeDeformation(deformation))
                                .texOffs(86, 53)
                                .addBox(-4.0F, -3.0F, -2.5F, 5.0F, 8.0F, 6.0F,
                                                new CubeDeformation(0.25F + deformation)),
                                PartPose.offsetAndRotation(-5.0F, -10.0F, 0.0F, -1.3963F, 0.2618F, 0.0F));

                PartDefinition staff = right_arm.addOrReplaceChild("staff", CubeListBuilder.create(),
                                PartPose.offsetAndRotation(0.0F, 10.0F, 5.5F, 1.4399F, 0.0F, 0.0F));

                PartDefinition handle = staff.addOrReplaceChild(
                                "handle", CubeListBuilder.create().texOffs(34, 61).addBox(0.5F, -16.0F, -19.0F, 1.0F,
                                                24.0F, 1.0F, new CubeDeformation(deformation)),
                                PartPose.offset(-1.0F, 7.0F, 19.0F));

                PartDefinition group = staff.addOrReplaceChild("group", CubeListBuilder.create().texOffs(14, 71)
                                .addBox(-0.5F, -18.0F, -20.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(deformation))
                                .texOffs(38, 82)
                                .addBox(-0.5F, -17.0F, -20.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(deformation))
                                .texOffs(14, 61).addBox(-1.5F, -23.0F, -21.0F, 5.0F, 5.0F, 5.0F,
                                                new CubeDeformation(deformation)),
                                PartPose.offset(-1.0F, 7.0F, 19.0F));

                PartDefinition staffhead = staff.addOrReplaceChild("staffhead", CubeListBuilder.create(),
                                PartPose.offsetAndRotation(0.0F, -13.25F, 0.5F, 0.3927F, -0.3927F, -0.3927F));

                PartDefinition night1 = staffhead.addOrReplaceChild("night1",
                                CubeListBuilder.create().texOffs(54, 67).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F,
                                                new CubeDeformation(-3.75F + deformation)),
                                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3927F, 0.3927F, 0.3927F));

                PartDefinition night3 = night1.addOrReplaceChild(
                                "night3", CubeListBuilder.create().texOffs(38, 67).addBox(-2.0F, -2.0F, -2.0F, 4.0F,
                                                4.0F, 4.0F, new CubeDeformation(-1.25F + deformation)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition night2 = staffhead.addOrReplaceChild("night2", CubeListBuilder.create().texOffs(70, 61)
                                .addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-6.0F + deformation))
                                .texOffs(70, 53).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F,
                                                new CubeDeformation(-4.75F + deformation)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition right_pauldron = right_arm.addOrReplaceChild("right_pauldron",
                                CubeListBuilder.create().texOffs(48, 53).addBox(-4.0F, -4.0F, -3.0F, 5.0F, 5.0F, 6.0F,
                                                new CubeDeformation(deformation)),
                                PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 1.0472F, -0.0873F, -0.2618F));

                PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(70, 69)
                                .mirror()
                                .addBox(-0.95F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(deformation))
                                .mirror(false)
                                .texOffs(0, 60).mirror()
                                .addBox(-1.25F, -2.05F, -1.25F, 3.0F, 12.0F, 4.0F, new CubeDeformation(deformation))
                                .mirror(false)
                                .texOffs(86, 53).mirror()
                                .addBox(-1.0F, -3.0F, -2.5F, 5.0F, 8.0F, 6.0F, new CubeDeformation(0.25F + deformation))
                                .mirror(false), PartPose.offsetAndRotation(5.0F, -10.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

                PartDefinition leftItem = left_arm.addOrReplaceChild("leftItem", CubeListBuilder.create(),
                                PartPose.offset(1.0F, 7.0F, 1.0F));

                PartDefinition left_pauldron = left_arm.addOrReplaceChild("left_pauldron",
                                CubeListBuilder.create().texOffs(48, 53).mirror()
                                                .addBox(-1.0F, -4.0F, -3.0F, 5.0F, 5.0F, 6.0F,
                                                                new CubeDeformation(deformation))
                                                .mirror(false),
                                PartPose.offset(0.0F, 0.0F, 1.0F));

                PartDefinition pants = body.addOrReplaceChild(
                                "pants", CubeListBuilder.create().texOffs(24, 45).addBox(-4.0F, 0.0F, -2.0F, 8.0F,
                                                12.0F, 4.0F, new CubeDeformation(deformation)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition middle = pants.addOrReplaceChild(
                                "middle", CubeListBuilder.create().texOffs(26, 71).addBox(-1.0F, 0.0F, 0.0F, 2.0F,
                                                10.0F, 0.0F, new CubeDeformation(deformation)),
                                PartPose.offset(0.0F, 0.0F, -2.0F));

                PartDefinition cape = body.addOrReplaceChild("cape", CubeListBuilder.create().texOffs(88, 0)
                                .addBox(-8.0F, 0.0F, -2.0F, 16.0F, 24.0F, 4.0F, new CubeDeformation(deformation))
                                .texOffs(0, 0).addBox(-8.0F, 0.0F, -3.0F, 16.0F, 24.0F, 5.0F,
                                                new CubeDeformation(0.25F + deformation)),
                                PartPose.offset(0.0F, -12.0F, 1.0F));

                PartDefinition collar = body.addOrReplaceChild("collar",
                                CubeListBuilder.create().texOffs(0, 29).addBox(-5.5F, -4.0F, -1.5F, 11.0F, 5.0F, 10.0F,
                                                new CubeDeformation(deformation)),
                                PartPose.offsetAndRotation(0.0F, -10.5F, 3.0F, 1.0036F, 0.0F, 0.0F));

                return LayerDefinition.create(meshdefinition, 128, 128);
        }

        @Override
        public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                        float netHeadYaw,
                        float headPitch) {
                int transitionTick = entity.baseAnimTransitionTick;
                String fromKey = "";
                String toKey = "";
                if (transitionTick > 0) {
                        fromKey = entity.transitionFromKey;
                        toKey = entity.transitionToKey;
                }
                if (transitionTick > 0 && !fromKey.isEmpty()) {
                        float partialTick = ageInTicks - (float) entity.tickCount;
                        float t = 1.0F - ((float) transitionTick - partialTick) / (float) TRANSITION_DURATION;
                        t = Mth.clamp(t, 0.0F, 1.0F);
                        Map<String, ModelPartPose> fromPose = this.evaluatePass(fromKey, entity, ageInTicks,
                                        netHeadYaw, headPitch);
                        Map<String, ModelPartPose> newPose = this.evaluatePass(toKey, entity, ageInTicks,
                                        netHeadYaw, headPitch);
                        float eased = t < 0.5F ? 4.0F * t * t * t : 1.0F - (float) Math.pow(-2.0F * t + 2.0F, 3) / 2.0F;
                        this.blendPoses(fromPose, newPose, eased);
                } else {
                        String activeKey = entity.getCurrentAnimKey();
                        if (activeKey != null && !activeKey.isEmpty()) {
                                this.evaluatePass(activeKey, entity, ageInTicks, netHeadYaw, headPitch);
                        } else {
                                this.root().getAllParts().forEach(ModelPart::resetPose);
                                this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
                                this.head.xRot = headPitch * ((float) Math.PI / 180F);
                        }
                }
        }

        private Map<String, ModelPartPose> evaluatePass(String key, T entity, float ageInTicks,
                        float netHeadYaw,
                        float headPitch) {
                this.root().getAllParts().forEach(ModelPart::resetPose);
                this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
                this.head.xRot = headPitch * ((float) Math.PI / 180F);
                this.animate(entity.heartofthenightAnimationState, NamelessOneAnimation.HEART_OF_THE_NIGHT,
                                ageInTicks);
                switch (key) {
                        case "idle":
                                this.animate(entity.idleAnimationState, NamelessOneAnimation.IDLE, ageInTicks);
                                break;
                        case "walk":
                                this.animate(entity.walkAnimationState, NamelessOneAnimation.WALK, ageInTicks);
                                break;
                        case "attack":
                                this.animate(entity.attackAnimationState, NamelessOneAnimation.ATTACK, ageInTicks);
                                break;
                        case "summon":
                                this.animate(entity.summonAnimationState, NamelessOneAnimation.SUMMON, ageInTicks);
                                break;
                        case "spell":
                                this.animate(entity.spellAnimationState, NamelessOneAnimation.SPELL, ageInTicks);
                                break;
                        case "alert":
                                this.animate(entity.alertAnimationState, NamelessOneAnimation.ALERT, ageInTicks);
                                break;
                        case "fly":
                                this.animate(entity.flyAnimationState, NamelessOneAnimation.FLY, ageInTicks);
                                break;
                        case "walk2":
                                this.animate(entity.walk2AnimationState, NamelessOneAnimation.WALK2, ageInTicks);
                                break;
                        case "updraft":
                                this.animate(entity.updrafAnimationState, NamelessOneAnimation.UPDRAFT, ageInTicks);
                                break;
                        case "storm":
                                this.animate(entity.stormAnimationState, NamelessOneAnimation.STORM, ageInTicks);
                                break;
                        case "storm2":
                                this.animate(entity.storm2AnimationState, NamelessOneAnimation.STORM2, ageInTicks);
                                break;
                        case "rapid":
                                this.animate(entity.rapidAnimationState, NamelessOneAnimation.RAPID, ageInTicks);
                                break;
                        case "range_spell_attack":
                                this.animate(entity.rangeSpellAttackAnimationState,
                                                NamelessOneAnimation.RANGE_SPELL_ATTACK, ageInTicks);
                                break;
                        case "teleportout":
                                this.animate(entity.teleportoutAnimationState, NamelessOneAnimation.TELEPORTOUT,
                                                ageInTicks);
                                break;
                        case "teleportin":
                                this.animate(entity.teleportinAnimationState, NamelessOneAnimation.TELEPORTIN,
                                                ageInTicks);
                                break;
                        case "wake":
                                this.animate(entity.wakeAnimationState, NamelessOneAnimation.WAKE, ageInTicks);
                                break;
                        case "avada":
                                this.animate(entity.avadaAnimationState, NamelessOneAnimation.AVADA, ageInTicks);
                                break;
                        case "quake1":
                                this.animate(entity.quake1AnimationState, NamelessOneAnimation.QUAKE1, ageInTicks);
                                break;
                        case "quake2":
                                this.animate(entity.quake2AnimationState, NamelessOneAnimation.QUAKE2, ageInTicks);
                                break;
                        case "slow_spell":
                                this.animate(entity.slowSpellAnimationState,
                                                NamelessOneAnimation.SLOW_SPELL, ageInTicks);
                                break;
                        case "leeching_spell":
                                this.animate(entity.leechingSpellAnimationState,
                                                NamelessOneAnimation.LEECHING_SPELL, ageInTicks);
                                break;
                        case "stab":
                                this.animate(entity.stabAnimationState, NamelessOneAnimation.STAB, ageInTicks);
                                break;
                        case "breathe":
                                this.animate(entity.breatheAnimationState, NamelessOneAnimation.BREATHE,
                                                ageInTicks);
                                break;
                        case "death":
                                this.animate(entity.deathAnimationState, NamelessOneAnimation.DEAD, ageInTicks);
                                break;
                }
                return ModelUtil.saveModelSnapshot(this.allPartNames, this::getAnyDescendantWithName);
        }

        private void blendPoses(Map<String, ModelPartPose> from, Map<String, ModelPartPose> to, float progress) {
                for (Map.Entry<String, ModelPartPose> entry : from.entrySet()) {
                        String boneName = entry.getKey();
                        ModelPartPose fromPose = entry.getValue();
                        ModelPartPose toPose = to.get(boneName);
                        if (toPose == null)
                                continue;
                        this.getAnyDescendantWithName(boneName).ifPresent(part -> {
                                part.xRot = fromPose.xRot() + (toPose.xRot() - fromPose.xRot()) * progress;
                                part.yRot = fromPose.yRot() + (toPose.yRot() - fromPose.yRot()) * progress;
                                part.zRot = fromPose.zRot() + (toPose.zRot() - fromPose.zRot()) * progress;
                                part.x = fromPose.x() + (toPose.x() - fromPose.x()) * progress;
                                part.y = fromPose.y() + (toPose.y() - fromPose.y()) * progress;
                                part.z = fromPose.z() + (toPose.z() - fromPose.z()) * progress;
                        });
                }
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                        int packedOverlay, float red, float green, float blue, float alpha) {
                skeleton.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }

        @Override
        public ModelPart root() {
                return skeleton;
        }
}