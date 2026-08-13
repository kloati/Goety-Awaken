package com.k1sak1.goetyawaken.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class TowerKeeperStatueModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation("goetyawaken", "tower_keeper_statue"), "main");

    private final ModelPart body;
    private final ModelPart bone4;
    private final ModelPart bone5;
    private final ModelPart bone6;
    private final ModelPart bone3;
    private final ModelPart bone2;
    private final ModelPart bone;

    public TowerKeeperStatueModel(ModelPart root) {
        this.body = root.getChild("body");
        this.bone4 = this.body.getChild("bone4");
        this.bone5 = this.bone4.getChild("bone5");
        this.bone6 = this.bone4.getChild("bone6");
        this.bone3 = this.body.getChild("bone3");
        this.bone2 = this.body.getChild("bone2");
        this.bone = this.body.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(),
                PartPose.offsetAndRotation(8.0F, 24.0F, 7.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1",
                CubeListBuilder.create().texOffs(0, 177).addBox(-57.0F, -9.0F, -1.0F, 57.0F, 18.0F, 2.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.0F, -58.0F, 8.0F, 1.5708F, 0.0F, -1.6581F));

        PartDefinition bone4 = body.addOrReplaceChild("bone4", CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r2 = bone4.addOrReplaceChild("cube_r2",
                CubeListBuilder.create().texOffs(81, 17).addBox(-9.0F, 0.0F, 0.0F, 18.0F, 10.0F, 5.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.0F, -66.0F, 8.0F, 0.0F, -1.5708F, -1.1345F));

        PartDefinition cube_r3 = bone4.addOrReplaceChild("cube_r3",
                CubeListBuilder.create().texOffs(32, 0).addBox(-3.0F, -7.0F, -1.0F, 4.0F, 7.0F, 2.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-15.0F, -47.0F, 9.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r4 = bone4.addOrReplaceChild("cube_r4",
                CubeListBuilder.create().texOffs(83, 52).addBox(-13.0F, -7.0F, -1.0F, 14.0F, 7.0F, 1.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-15.0F, -49.0F, 14.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r5 = bone4.addOrReplaceChild("cube_r5",
                CubeListBuilder.create().texOffs(0, 25).addBox(-17.0F, -17.0F, -1.0F, 18.0F, 17.0F, 16.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-14.0F, -49.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition bone5 = bone4.addOrReplaceChild("bone5",
                CubeListBuilder.create().texOffs(0, 2)
                        .addBox(-4.0F, -10.0F, -4.0F, 6.0F, 12.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(66, 6).addBox(-1.0F, 2.0F, -4.0F, 3.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-7.0F, -48.0F, -5.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition bone6 = bone4.addOrReplaceChild("bone6",
                CubeListBuilder.create().texOffs(0, 2)
                        .addBox(-4.0F, -10.0F, -4.0F, 6.0F, 12.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(66, 6).addBox(-1.0F, 2.0F, -4.0F, 3.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-7.0F, -48.0F, 21.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition bone3 = body.addOrReplaceChild("bone3",
                CubeListBuilder.create().texOffs(0, 63)
                        .addBox(-15.0F, -49.0F, -1.0F, 16.0F, 16.0F, 18.0F, new CubeDeformation(0.0F))
                        .texOffs(90, 57).addBox(-15.0F, -49.0F, 2.0F, 2.0F, 16.0F, 12.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition bone2 = body.addOrReplaceChild("bone2",
                CubeListBuilder.create().texOffs(0, 102)
                        .addBox(-15.0F, -33.0F, -1.0F, 16.0F, 15.0F, 18.0F, new CubeDeformation(0.0F))
                        .texOffs(91, 90).addBox(-15.0F, -33.0F, 2.0F, 2.0F, 15.0F, 12.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition bone = body.addOrReplaceChild("bone",
                CubeListBuilder.create().texOffs(0, 141)
                        .addBox(-15.0F, -18.0F, -1.0F, 16.0F, 18.0F, 18.0F, new CubeDeformation(0.0F))
                        .texOffs(88, 146).addBox(-15.0F, -18.0F, 2.0F, 2.0F, 18.0F, 12.0F, new CubeDeformation(0.0F))
                        .texOffs(74, 123).addBox(-13.0F, -3.0F, 2.0F, 13.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
                        .texOffs(73, 140).addBox(-11.0F, -18.0F, -4.0F, 8.0F, 15.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r6 = bone.addOrReplaceChild("cube_r6",
                CubeListBuilder.create().texOffs(73, 140).addBox(-7.0F, -15.0F, -1.0F, 8.0F, 15.0F, 3.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-10.0F, -3.0F, 19.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition cube_r7 = bone.addOrReplaceChild("cube_r7",
                CubeListBuilder.create().texOffs(38, 8).addBox(-3.0F, -8.0F, -1.0F, 4.0F, 8.0F, 6.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-4.0F, 0.0F, 6.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r8 = bone.addOrReplaceChild("cube_r8",
                CubeListBuilder.create().texOffs(38, 8).addBox(-3.0F, -8.0F, -1.0F, 4.0F, 8.0F, 6.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-4.0F, 0.0F, 12.0F, 0.0F, -1.5708F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 200);
    }

    @Override
    public void setupAnim(Entity pEntity, float pLimbSwing, float pLimbSwingAmount,
            float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight,
            int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        body.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }
}
