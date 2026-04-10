package com.k1sak1.goetyawaken.client.model;

import com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton.VanguardChampion;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class VanguardChampionModel<T extends Entity> extends HierarchicalModel<VanguardChampion>
                implements HeadedModel {
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
                        new ResourceLocation("goetyawaken", "vanguard_champion"), "main");
        private final ModelPart skeleton;
        private final ModelPart body;
        private final ModelPart head;
        private final ModelPart helmet;
        private final ModelPart feather;
        private final ModelPart feather1;
        private final ModelPart feather2;
        private final ModelPart feather3;
        private final ModelPart feather4;
        private final ModelPart cape;
        private final ModelPart right_arm;
        private final ModelPart right_hand;
        private final ModelPart glaive;
        private final ModelPart l_item;
        private final ModelPart right_pauldron;
        private final ModelPart left_arm;
        private final ModelPart left_hand;
        private final ModelPart shield;
        private final ModelPart left_pauldron;
        private final ModelPart right_leg;
        private final ModelPart right_bone;
        private final ModelPart right_plate;
        private final ModelPart left_leg;
        private final ModelPart left_bone;
        private final ModelPart left_plate;

        public VanguardChampionModel(ModelPart root) {
                this.skeleton = root.getChild("skeleton");
                this.body = this.skeleton.getChild("body");
                this.head = this.body.getChild("head");
                this.helmet = this.head.getChild("helmet");
                this.feather = this.helmet.getChild("feather");
                this.feather1 = this.feather.getChild("feather1");
                this.feather2 = this.feather.getChild("feather2");
                this.feather3 = this.feather.getChild("feather3");
                this.feather4 = this.helmet.getChild("feather4");
                this.cape = this.body.getChild("cape");
                this.right_arm = this.body.getChild("right_arm");
                this.right_hand = this.right_arm.getChild("right_hand");
                this.glaive = this.right_hand.getChild("glaive");
                this.l_item = this.glaive.getChild("l_item");
                this.right_pauldron = this.right_arm.getChild("right_pauldron");
                this.left_arm = this.body.getChild("left_arm");
                this.left_hand = this.left_arm.getChild("left_hand");
                this.shield = this.left_hand.getChild("shield");
                this.left_pauldron = this.left_arm.getChild("left_pauldron");
                this.right_leg = this.skeleton.getChild("right_leg");
                this.right_bone = this.right_leg.getChild("right_bone");
                this.right_plate = this.right_leg.getChild("right_plate");
                this.left_leg = this.skeleton.getChild("left_leg");
                this.left_bone = this.left_leg.getChild("left_bone");
                this.left_plate = this.left_leg.getChild("left_plate");
        }

        public ModelPart root() {
                return this.skeleton;
        }

        @Override
        public ModelPart getHead() {
                return this.head;
        }

        public static LayerDefinition createBodyLayer() {
                MeshDefinition meshdefinition = new MeshDefinition();
                PartDefinition partdefinition = meshdefinition.getRoot();

                PartDefinition skeleton = partdefinition.addOrReplaceChild("skeleton", CubeListBuilder.create(),
                                PartPose.offset(0.0F, 24.0F, 0.0F));

                PartDefinition body = skeleton.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 45)
                                .addBox(-4.0F, -10.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                                .texOffs(66, 19)
                                .addBox(-4.0F, -10.0F, -2.5F, 8.0F, 8.0F, 5.0F, new CubeDeformation(0.5F))
                                .texOffs(64, 83)
                                .addBox(-5.0F, -9.75F, -4.0F, 10.0F, 4.0F, 1.0F, new CubeDeformation(0.1F))
                                .texOffs(50, 67).addBox(-4.0F, -4.35F, -2.5F, 8.0F, 8.0F, 5.0F,
                                                new CubeDeformation(0.5F)),
                                PartPose.offset(0.0F, -14.0F, 0.0F));

                PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(36, 39)
                                .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                                .texOffs(0, 26)
                                .addBox(-4.5F, -9.0F, -4.5F, 9.0F, 10.0F, 9.0F, new CubeDeformation(0.0F))
                                .texOffs(32, 0)
                                .addBox(-4.5F, -9.0F, -4.5F, 9.0F, 10.0F, 9.0F, new CubeDeformation(0.25F))
                                .texOffs(78, 54)
                                .addBox(-4.75F, -5.0F, -4.75F, 4.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
                                .texOffs(78, 54).mirror()
                                .addBox(0.75F, -5.0F, -4.75F, 4.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
                                .texOffs(66, 32)
                                .addBox(-5.5F, -5.0F, -5.5F, 11.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                                .texOffs(32, 19)
                                .addBox(-2.0F, -5.0F, -5.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(32, 19).addBox(1.0F, -5.0F, -5.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(40, 80)
                                .addBox(-5.5F, -5.0F, -0.5F, 11.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(68, 0).addBox(-5.5F, -3.0F, 0.5F, 11.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                                .texOffs(40, 69)
                                .addBox(-1.0F, -11.0F, -5.5F, 2.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(24, 50)
                                .addBox(-1.0F, -11.0F, -6.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(84, 46)
                                .addBox(-6.25F, -11.5F, -4.45F, 6.0F, 6.0F, 1.0F, new CubeDeformation(0.5F))
                                .texOffs(84, 46).mirror()
                                .addBox(0.25F, -11.5F, -4.45F, 6.0F, 6.0F, 1.0F, new CubeDeformation(0.5F))
                                .mirror(false), PartPose.offset(0.0F, -10.0F, 0.0F));

                PartDefinition helmet = head.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(68, 6)
                                .addBox(-2.0F, -4.0F, -7.0F, 4.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
                                .texOffs(84, 38)
                                .addBox(-1.5F, -2.0F, -6.0F, 3.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
                                PartPose.offsetAndRotation(0.0F, -7.0F, 4.5F, -0.2618F, 0.0F, 0.0F));

                PartDefinition feather = helmet.addOrReplaceChild(
                                "feather", CubeListBuilder.create().texOffs(24, 45).addBox(-1.5F, -4.0F, -3.5F, 3.0F,
                                                2.0F, 3.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, -1.0F, 0.0F));

                PartDefinition feather1 = feather.addOrReplaceChild("feather1",
                                CubeListBuilder.create().texOffs(24, 55).addBox(-2.5F, -5.0F, 0.0F, 5.0F, 6.0F, 8.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offsetAndRotation(0.0F, -4.0F, -2.0F, -0.7854F, 0.0F, 0.0F));

                PartDefinition feather2 = feather.addOrReplaceChild("feather2",
                                CubeListBuilder.create().texOffs(24, 55).addBox(-2.5F, -5.0F, 0.0F, 5.0F, 6.0F, 8.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offsetAndRotation(0.0F, -4.0F, -2.0F, 0.7854F, 0.0F, 0.0F));

                PartDefinition feather3 = feather.addOrReplaceChild(
                                "feather3", CubeListBuilder.create().texOffs(24, 55).addBox(-2.5F, -5.0F, 0.0F, 5.0F,
                                                6.0F, 8.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, -4.0F, -2.0F));

                PartDefinition feather4 = helmet.addOrReplaceChild("feather4", CubeListBuilder.create(),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition cube_r1 = feather4.addOrReplaceChild("cube_r1",
                                CubeListBuilder.create().texOffs(84, 83).addBox(-0.5F, -10.0F, -7.5F, 1.0F, 20.0F,
                                                21.0F, new CubeDeformation(0.5F)),
                                PartPose.offsetAndRotation(-1.0F, -3.0F, 1.0F, 0.2182F, 0.0F, 0.0F));

                PartDefinition cape = body.addOrReplaceChild("cape",
                                CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, 0.0F, -4.0F, 12.0F, 22.0F, 4.0F,
                                                new CubeDeformation(0.25F)),
                                PartPose.offsetAndRotation(0.0F, -10.0F, 4.0F, 0.1745F, 0.0F, 0.0F));

                PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create(),
                                PartPose.offset(-5.0F, -8.0F, 0.0F));

                PartDefinition right_hand = right_arm.addOrReplaceChild("right_hand", CubeListBuilder.create()
                                .texOffs(76, 67).mirror()
                                .addBox(-2.0F, -2.25F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.1F))
                                .mirror(false)
                                .texOffs(0, 79).mirror()
                                .addBox(-1.5F, -1.9F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                                .texOffs(100, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition glaive = right_hand.addOrReplaceChild("glaive", CubeListBuilder.create(),
                                PartPose.offset(1.0F, 8.0F, -2.0F));

                PartDefinition l_item = glaive.addOrReplaceChild(
                                "l_item", CubeListBuilder.create().texOffs(14, 90)
                                                .addBox(-14.2929F, -21.7071F, -1.0F, 34.0F, 36.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 126)
                                                .addBox(-14.2929F, 11.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 126)
                                                .addBox(-13.2929F, 12.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 126)
                                                .addBox(-12.2929F, 13.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 124)
                                                .addBox(-11.2929F, 12.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 124)
                                                .addBox(-13.2929F, 10.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 122)
                                                .addBox(-6.2929F, 9.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 124)
                                                .addBox(-6.2929F, 8.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 126)
                                                .addBox(-7.2929F, 8.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 126)
                                                .addBox(-8.2929F, 8.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 126)
                                                .addBox(-9.2929F, 6.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 124)
                                                .addBox(-10.2929F, 8.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 124)
                                                .addBox(-9.2929F, 7.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 124)
                                                .addBox(-9.2929F, 9.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 124)
                                                .addBox(0.7071F, -4.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 124)
                                                .addBox(1.7071F, -4.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 126)
                                                .addBox(3.7071F, -3.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 126)
                                                .addBox(2.7071F, -4.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 124)
                                                .addBox(3.7071F, -2.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 124)
                                                .addBox(3.7071F, -8.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 124)
                                                .addBox(4.7071F, -7.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 124)
                                                .addBox(6.7071F, -3.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 124)
                                                .addBox(6.7071F, -5.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 122)
                                                .addBox(7.7071F, -5.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 122)
                                                .addBox(7.7071F, -4.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 122)
                                                .addBox(3.7071F, -7.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 120)
                                                .addBox(-11.2929F, 11.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 120)
                                                .addBox(-10.2929F, 10.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 120)
                                                .addBox(-7.2929F, 7.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 120)
                                                .addBox(-2.2929F, 2.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 120)
                                                .addBox(-1.2929F, 1.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 120)
                                                .addBox(0.7071F, -0.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 120)
                                                .addBox(-0.2929F, 0.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 120)
                                                .addBox(1.7071F, -1.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 120)
                                                .addBox(2.7071F, -2.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 120)
                                                .addBox(4.7071F, -4.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 120)
                                                .addBox(5.7071F, -5.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 120)
                                                .addBox(5.7071F, -8.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 120)
                                                .addBox(6.7071F, -9.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 118)
                                                .addBox(4.7071F, -6.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 118)
                                                .addBox(3.7071F, -5.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 118)
                                                .addBox(1.7071F, -3.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 118)
                                                .addBox(0.7071F, -2.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 118)
                                                .addBox(-0.2929F, -1.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 118)
                                                .addBox(-1.2929F, -0.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 118)
                                                .addBox(-2.2929F, 0.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 118)
                                                .addBox(-3.2929F, 1.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 118)
                                                .addBox(-8.2929F, 6.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 118)
                                                .addBox(-11.2929F, 9.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 118)
                                                .addBox(-12.2929F, 10.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 116)
                                                .addBox(-6.2929F, 6.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 116)
                                                .addBox(-5.2929F, 5.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 116)
                                                .addBox(-4.2929F, 4.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 116)
                                                .addBox(-3.2929F, 3.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 116)
                                                .addBox(-7.2929F, 5.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 114)
                                                .addBox(-6.2929F, 4.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 114)
                                                .addBox(-5.2929F, 3.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 114)
                                                .addBox(-4.2929F, 2.2929F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 112)
                                                .addBox(7.7071F, -10.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 112)
                                                .addBox(8.7071F, -11.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 112)
                                                .addBox(8.7071F, -7.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 112)
                                                .addBox(8.7071F, -6.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 112)
                                                .addBox(16.7071F, -19.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 110)
                                                .addBox(9.7071F, -12.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 110)
                                                .addBox(10.7071F, -13.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 110)
                                                .addBox(11.7071F, -14.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 110)
                                                .addBox(12.7071F, -15.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 110)
                                                .addBox(13.7071F, -16.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 110)
                                                .addBox(15.7071F, -18.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(0, 110)
                                                .addBox(17.7071F, -20.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(4, 112)
                                                .addBox(14.7071F, -17.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(4, 112)
                                                .addBox(17.7071F, -21.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(4, 112)
                                                .addBox(18.7071F, -20.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(4, 112)
                                                .addBox(18.7071F, -19.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(4, 112)
                                                .addBox(18.7071F, -18.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(4, 112)
                                                .addBox(18.7071F, -17.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(4, 112)
                                                .addBox(18.7071F, -16.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(4, 112)
                                                .addBox(17.7071F, -15.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(4, 112)
                                                .addBox(16.7071F, -14.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(4, 112)
                                                .addBox(15.7071F, -13.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(4, 112)
                                                .addBox(14.7071F, -12.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(4, 112)
                                                .addBox(13.7071F, -11.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(4, 112)
                                                .addBox(12.7071F, -10.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(4, 112)
                                                .addBox(10.7071F, -9.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(4, 110)
                                                .addBox(9.7071F, -8.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(4, 110)
                                                .addBox(9.7071F, -6.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(4, 110).addBox(11.7071F, -10.7071F, -1.0F, 1.0F, 1.0F, 1.0F,
                                                                new CubeDeformation(0.0F)),
                                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.7854F, 1.5708F));

                PartDefinition right_pauldron = right_arm.addOrReplaceChild("right_pauldron", CubeListBuilder.create()
                                .texOffs(50, 55).mirror()
                                .addBox(-6.0F, -4.25F, -3.0F, 8.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
                                .texOffs(40, 84).mirror()
                                .addBox(-3.0F, -5.25F, -2.0F, 5.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                                .mirror(false), PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.2654F));

                PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create(),
                                PartPose.offset(5.0F, -8.0F, 0.0F));

                PartDefinition left_hand = left_arm.addOrReplaceChild("left_hand", CubeListBuilder.create()
                                .texOffs(76, 67)
                                .addBox(-2.0F, -2.25F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.1F))
                                .texOffs(0, 79)
                                .addBox(-1.5F, -1.9F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                                .texOffs(100, 0).mirror()
                                .addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F))
                                .mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition shield = left_hand.addOrReplaceChild("shield", CubeListBuilder.create().texOffs(36, 19)
                                .addBox(-7.0F, -6.5F, -0.5F, 14.0F, 19.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(68, 16)
                                .addBox(-4.0F, -7.5F, -0.5F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                                .texOffs(78, 65).addBox(-4.0F, 12.5F, -0.5F, 8.0F, 1.0F, 1.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 6.5F, 0.5F));

                PartDefinition left_pauldron = left_arm.addOrReplaceChild("left_pauldron",
                                CubeListBuilder.create().texOffs(50, 55)
                                                .addBox(-2.0F, -4.25F, -3.0F, 8.0F, 6.0F, 6.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(40, 84).addBox(-2.0F, -5.25F, -2.0F, 5.0F, 1.0F, 4.0F,
                                                                new CubeDeformation(0.0F)),
                                PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.2654F));

                PartDefinition right_leg = skeleton.addOrReplaceChild("right_leg", CubeListBuilder.create(),
                                PartPose.offset(-2.0F, -12.0F, 0.0F));

                PartDefinition right_bone = right_leg.addOrReplaceChild("right_bone", CubeListBuilder.create()
                                .texOffs(0, 95)
                                .addBox(-1.0F, -0.05F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F))
                                .texOffs(68, 38).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F,
                                                new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition right_plate = right_leg.addOrReplaceChild("right_plate", CubeListBuilder.create()
                                .texOffs(0, 61).mirror()
                                .addBox(-2.5F, -1.0F, -2.5F, 5.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
                                .texOffs(20, 69).mirror()
                                .addBox(-2.5F, -1.0F, -2.5F, 5.0F, 13.0F, 5.0F, new CubeDeformation(0.25F))
                                .mirror(false), PartPose.offsetAndRotation(0.0F, -1.5F, 0.0F, 0.0F, 0.0F, 0.0349F));

                PartDefinition left_leg = skeleton.addOrReplaceChild("left_leg", CubeListBuilder.create(),
                                PartPose.offset(2.0F, -12.0F, 0.0F));

                PartDefinition left_bone = left_leg.addOrReplaceChild("left_bone", CubeListBuilder.create()
                                .texOffs(0, 95).mirror()
                                .addBox(-1.0F, -0.05F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F))
                                .mirror(false)
                                .texOffs(68, 38).mirror()
                                .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition left_plate = left_leg.addOrReplaceChild("left_plate",
                                CubeListBuilder.create().texOffs(0, 61)
                                                .addBox(-2.5F, -1.0F, -2.5F, 5.0F, 13.0F, 5.0F,
                                                                new CubeDeformation(0.0F))
                                                .texOffs(20, 69).addBox(-2.5F, -1.0F, -2.5F, 5.0F, 13.0F, 5.0F,
                                                                new CubeDeformation(0.25F)),
                                PartPose.offsetAndRotation(0.0F, -1.5F, 0.0F, 0.0F, 0.0F, -0.0349F));

                return LayerDefinition.create(meshdefinition, 128, 128);
        }

        @Override
        public void setupAnim(VanguardChampion vanguard, float limbSwing, float limbSwingAmount, float ageInTicks,
                        float netHeadYaw,
                        float headPitch) {
                this.skeleton.getAllParts().forEach(ModelPart::resetPose);
                this.shield.visible = vanguard.hasShield();
                if (!vanguard.isDeadOrDying()) {
                        this.animateHeadLookTarget(netHeadYaw, headPitch);
                }
                this.animate(vanguard.bobAnimationState,
                                com.k1sak1.goetyawaken.client.animation.VanguardChampionAnimation.BOB, ageInTicks);
                this.animate(vanguard.idleAnimationState,
                                com.k1sak1.goetyawaken.client.animation.VanguardChampionAnimation.IDLE, ageInTicks);
                this.animate(vanguard.walkAnimationState,
                                com.k1sak1.goetyawaken.client.animation.VanguardChampionAnimation.WALK, ageInTicks);
                this.animate(vanguard.attackAnimationState,
                                com.k1sak1.goetyawaken.client.animation.VanguardChampionAnimation.ATTACK, ageInTicks);
                this.animate(vanguard.attack2AnimationState,
                                com.k1sak1.goetyawaken.client.animation.VanguardChampionAnimation.ATTACK2, ageInTicks);
                this.animate(vanguard.preAnimationState,
                                com.k1sak1.goetyawaken.client.animation.VanguardChampionAnimation.PRE, ageInTicks);
                this.animate(vanguard.shootAnimationState,
                                com.k1sak1.goetyawaken.client.animation.VanguardChampionAnimation.SHOOT, ageInTicks);
        }

        private void animateHeadLookTarget(float netHeadYaw, float headPitch) {
                this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
                this.head.xRot = headPitch * ((float) Math.PI / 180F);
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                        int packedOverlay,
                        float red, float green, float blue, float alpha) {
                skeleton.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }
}