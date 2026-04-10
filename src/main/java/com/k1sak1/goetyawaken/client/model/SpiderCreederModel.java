package com.k1sak1.goetyawaken.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.entities.ally.SpiderCreeder;
import com.k1sak1.goetyawaken.client.animation.SpiderCreederAnimations;

public class SpiderCreederModel<T extends SpiderCreeder> extends HierarchicalModel<T> {
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "spider_creeder_servant"), "main");
        private final ModelPart root;
        private final ModelPart body_orbit;
        private final ModelPart body_main;
        private final ModelPart legs;
        private final ModelPart legs_left;
        private final ModelPart leg_back_left_socket;
        private final ModelPart leg_back_left;
        private final ModelPart leg_front_lower_left_socket;
        private final ModelPart leg_front_lower_left;
        private final ModelPart leg_front_upper_left_socket;
        private final ModelPart leg_front_left_socket;
        private final ModelPart leg_front_left;
        private final ModelPart legs_right;
        private final ModelPart leg_back_right_socket;
        private final ModelPart leg_back_right;
        private final ModelPart leg_front_lower_right_socket;
        private final ModelPart leg_front_lower_right;
        private final ModelPart leg_front_upper_right_socket;
        private final ModelPart leg_front_right_socket;
        private final ModelPart leg_front_right;
        private final ModelPart body;
        private final ModelPart head_ext;

        public SpiderCreederModel(ModelPart root) {
                this.root = root.getChild("root");
                this.body_orbit = this.root.getChild("body_orbit");
                this.body_main = this.body_orbit.getChild("body_main");
                this.legs = this.body_main.getChild("legs");
                this.legs_left = this.legs.getChild("legs_left");
                this.leg_back_left_socket = this.legs_left.getChild("leg_back_left_socket");
                this.leg_back_left = this.leg_back_left_socket.getChild("leg_back_left");
                this.leg_front_lower_left_socket = this.legs_left.getChild("leg_front_lower_left_socket");
                this.leg_front_lower_left = this.leg_front_lower_left_socket.getChild("leg_front_lower_left");
                this.leg_front_upper_left_socket = this.legs_left.getChild("leg_front_upper_left_socket");
                this.leg_front_left_socket = this.legs_left.getChild("leg_front_left_socket");
                this.leg_front_left = this.leg_front_left_socket.getChild("leg_front_left");
                this.legs_right = this.legs.getChild("legs_right");
                this.leg_back_right_socket = this.legs_right.getChild("leg_back_right_socket");
                this.leg_back_right = this.leg_back_right_socket.getChild("leg_back_right");
                this.leg_front_lower_right_socket = this.legs_right.getChild("leg_front_lower_right_socket");
                this.leg_front_lower_right = this.leg_front_lower_right_socket.getChild("leg_front_lower_right");
                this.leg_front_upper_right_socket = this.legs_right.getChild("leg_front_upper_right_socket");
                this.leg_front_right_socket = this.legs_right.getChild("leg_front_right_socket");
                this.leg_front_right = this.leg_front_right_socket.getChild("leg_front_right");
                this.body = this.body_main.getChild("body");
                this.head_ext = this.body.getChild("head_ext");
        }

        public static LayerDefinition createBodyLayer() {
                MeshDefinition meshdefinition = new MeshDefinition();
                PartDefinition partdefinition = meshdefinition.getRoot();

                PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(),
                                PartPose.offset(-1.0F, 24.0F, 0.0F));

                PartDefinition body_orbit = root.addOrReplaceChild("body_orbit", CubeListBuilder.create(),
                                PartPose.offset(1.0F, 0.0F, 0.0F));

                PartDefinition body_main = body_orbit.addOrReplaceChild("body_main", CubeListBuilder.create(),
                                PartPose.offset(0.0F, -5.0F, 0.0F));

                PartDefinition legs = body_main.addOrReplaceChild("legs", CubeListBuilder.create(),
                                PartPose.offset(0.0F, -4.0F, -0.5F));

                PartDefinition legs_left = legs.addOrReplaceChild("legs_left", CubeListBuilder.create(),
                                PartPose.offset(3.5F, 0.0F, 0.0F));

                PartDefinition leg_back_left_socket = legs_left.addOrReplaceChild("leg_back_left_socket",
                                CubeListBuilder.create(), PartPose.offset(-1.0F, 0.0F, 2.5F));

                PartDefinition leg_back_left = leg_back_left_socket.addOrReplaceChild(
                                "leg_back_left", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -1.0F, -1.0F,
                                                16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition leg_front_lower_left_socket = legs_left.addOrReplaceChild("leg_front_lower_left_socket",
                                CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.5F));

                PartDefinition leg_front_lower_left = leg_front_lower_left_socket.addOrReplaceChild(
                                "leg_front_lower_left", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -1.0F,
                                                -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition leg_front_upper_left_socket = legs_left.addOrReplaceChild("leg_front_upper_left_socket",
                                CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -1.5F));

                PartDefinition leg_front_left_socket = legs_left.addOrReplaceChild("leg_front_left_socket",
                                CubeListBuilder.create(), PartPose.offset(-1.0F, 0.0F, -1.5F));

                PartDefinition leg_front_left = leg_front_left_socket.addOrReplaceChild(
                                "leg_front_left", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -1.0F, -1.0F,
                                                16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition legs_right = legs.addOrReplaceChild("legs_right", CubeListBuilder.create(),
                                PartPose.offset(-3.5F, 0.0F, 0.0F));

                PartDefinition leg_back_right_socket = legs_right.addOrReplaceChild("leg_back_right_socket",
                                CubeListBuilder.create(), PartPose.offset(1.0F, 0.0F, 2.5F));

                PartDefinition leg_back_right = leg_back_right_socket.addOrReplaceChild("leg_back_right",
                                CubeListBuilder.create().texOffs(18, 0).mirror()
                                                .addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition leg_front_lower_right_socket = legs_right.addOrReplaceChild(
                                "leg_front_lower_right_socket", CubeListBuilder.create(),
                                PartPose.offset(0.0F, 0.0F, 0.5F));

                PartDefinition leg_front_lower_right = leg_front_lower_right_socket.addOrReplaceChild(
                                "leg_front_lower_right",
                                CubeListBuilder.create().texOffs(18, 0).mirror()
                                                .addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition leg_front_upper_right_socket = legs_right.addOrReplaceChild(
                                "leg_front_upper_right_socket", CubeListBuilder.create(),
                                PartPose.offset(0.0F, 0.0F, -1.5F));

                PartDefinition leg_front_right_socket = legs_right.addOrReplaceChild("leg_front_right_socket",
                                CubeListBuilder.create(), PartPose.offset(1.0F, 0.0F, -1.5F));

                PartDefinition leg_front_right = leg_front_right_socket.addOrReplaceChild("leg_front_right",
                                CubeListBuilder.create().texOffs(18, 0).mirror()
                                                .addBox(-15.0F, -1.0F, -1.0F, 16.0F, 2.0F, 2.0F,
                                                                new CubeDeformation(0.0F))
                                                .mirror(false),
                                PartPose.offset(0.0F, 0.0F, 0.0F));

                PartDefinition body = body_main
                                .addOrReplaceChild("body",
                                                CubeListBuilder.create().texOffs(0, 4).addBox(-4.0F, -12.0F, -2.0F,
                                                                8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                                                PartPose.offset(0.0F, -3.0F, 0.0F));

                PartDefinition head_ext = body.addOrReplaceChild(
                                "head_ext", CubeListBuilder.create().texOffs(32, 4).addBox(-4.0F, -8.0F, -4.0F, 8.0F,
                                                8.0F, 8.0F, new CubeDeformation(0.0F)),
                                PartPose.offset(0.0F, -12.0F, 0.0F));

                return LayerDefinition.create(meshdefinition, 64, 32);
        }

        @Override
        public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw,
                        float pHeadPitch) {
                this.root().getAllParts().forEach(ModelPart::resetPose);
                this.animate(pEntity.walkAnimationState, SpiderCreederAnimations.WALK, pAgeInTicks, 1.0F);
                this.animate(pEntity.idleAnimationState, SpiderCreederAnimations.IDLE, pAgeInTicks, 1.0F);
                this.animate(pEntity.swellAnimationState, SpiderCreederAnimations.SWELL, pAgeInTicks, 1.0F);
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                        int packedOverlay, float red, float green, float blue, float alpha) {
                root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }

        @Override
        public ModelPart root() {
                return this.root;
        }

}