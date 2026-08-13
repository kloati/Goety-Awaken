package com.k1sak1.goetyawaken.client.model;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.animation.StatueCreeperAnimations;
import com.k1sak1.goetyawaken.common.entities.ally.StatueCreeper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class StatueCreeperModel<T extends StatueCreeper> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(GoetyAwaken.MODID, "statue_creeper"), "main");

    private final ModelPart bone;
    private final ModelPart Body;
    private final ModelPart Head;
    private final ModelPart Horns;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;

    public StatueCreeperModel(ModelPart root) {
        this.bone = root.getChild("bone");
        this.Body = this.bone.getChild("Body");
        this.Head = this.Body.getChild("Head");
        this.Horns = this.Head.getChild("Horns");
        this.leg0 = this.bone.getChild("leg0");
        this.leg1 = this.bone.getChild("leg1");
        this.leg2 = this.bone.getChild("leg2");
        this.leg3 = this.bone.getChild("leg3");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition Body = bone.addOrReplaceChild("Body",
                CubeListBuilder.create().texOffs(0, 20).addBox(-4.0F, -12.0F, -3.0F, 8.0F, 12.0F, 6.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -7.0F, 0.0F));

        PartDefinition Head = Body.addOrReplaceChild("Head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -9.0F, -5.0F, 10.0F, 10.0F, 10.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -13.0F, 0.0F));

        PartDefinition Horns = Head.addOrReplaceChild("Horns", CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = Horns.addOrReplaceChild("cube_r1",
                CubeListBuilder.create().texOffs(28, 32).addBox(-1.0F, -4.0F, -5.0F, 2.0F, 3.0F, 6.0F,
                        new CubeDeformation(0.0F))
                        .texOffs(28, 32).mirror().addBox(11.0F, -4.0F, -5.0F, 2.0F, 3.0F, 6.0F,
                                new CubeDeformation(0.0F))
                        .mirror(false),
                PartPose.offsetAndRotation(-6.0F, -5.0F, -2.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition leg0 = bone.addOrReplaceChild("leg0",
                CubeListBuilder.create().texOffs(28, 20).addBox(-3.0F, -1.0F, -3.0F, 5.0F, 7.0F, 5.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offset(-2.0F, -6.0F, 4.0F));

        PartDefinition leg1 = bone.addOrReplaceChild("leg1",
                CubeListBuilder.create().texOffs(28, 20).mirror().addBox(-2.0F, -1.0F, -3.0F, 5.0F, 7.0F, 5.0F,
                        new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(2.0F, -6.0F, 4.0F));

        PartDefinition leg2 = bone.addOrReplaceChild("leg2",
                CubeListBuilder.create().texOffs(28, 20).mirror().addBox(-3.0F, -1.0F, -2.0F, 5.0F, 7.0F, 5.0F,
                        new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(-2.0F, -6.0F, -4.0F));

        PartDefinition leg3 = bone.addOrReplaceChild("leg3",
                CubeListBuilder.create().texOffs(28, 20).addBox(-2.0F, -1.0F, -2.0F, 5.0F, 7.0F, 5.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offset(2.0F, -6.0F, -4.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw,
            float pHeadPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animate(pEntity.statueAnimationState, StatueCreeperAnimations.STATUE, pAgeInTicks, 1.0F);
        this.animate(pEntity.awakenAnimationState, StatueCreeperAnimations.AWAKEN, pAgeInTicks, 1.0F);
        this.animate(pEntity.idleAnimationState, StatueCreeperAnimations.IDLE, pAgeInTicks, 1.0F);
        this.animate(pEntity.walkAnimationState, StatueCreeperAnimations.WALK, pAgeInTicks, 1.0F);
        this.animate(pEntity.explodeAnimationState, StatueCreeperAnimations.EXPLODE_FINAL, pAgeInTicks, 1.0F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
            int packedOverlay, float red, float green, float blue, float alpha) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return this.bone;
    }
}
