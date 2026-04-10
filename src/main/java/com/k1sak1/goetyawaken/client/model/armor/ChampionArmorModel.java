package com.k1sak1.goetyawaken.client.model.armor;

import com.google.common.collect.ImmutableList;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import net.minecraft.client.player.AbstractClientPlayer;

public class ChampionArmorModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation CHAMPION_ARMOR_OUTER_LAYER = new ModelLayerLocation(
            new ResourceLocation(GoetyAwaken.MODID, "champion_armor_outer"), "main");

    public final ModelPart helmet;
    public final ModelPart feather;
    public final ModelPart feather1;
    public final ModelPart feather2;
    public final ModelPart feather3;
    public final ModelPart feather4;
    public final ModelPart cape;
    public final ModelPart right_hand;
    public final ModelPart right_pauldron;
    public final ModelPart left_hand;
    public final ModelPart left_pauldron;
    public final ModelPart right_plate;
    public final ModelPart left_plate;

    public final ModelPart right_boot;
    public final ModelPart left_boot;

    private boolean capeVisible = false;
    private float capeF1 = 0.0F;
    private float capeF2 = 0.0F;
    private float capeF3 = 0.0F;
    private boolean capeCrouching = false;

    public ChampionArmorModel(ModelPart root) {
        super(root);
        this.helmet = this.head.getChild("helmet");
        this.feather = this.helmet.getChild("feather");
        this.feather1 = this.feather.getChild("feather1");
        this.feather2 = this.feather.getChild("feather2");
        this.feather3 = this.feather.getChild("feather3");
        this.feather4 = this.helmet.getChild("feather4");
        this.cape = this.body.getChild("cape");
        this.right_hand = this.rightArm.getChild("right_hand");
        this.right_pauldron = this.rightArm.getChild("right_pauldron");
        this.left_hand = this.leftArm.getChild("left_hand");
        this.left_pauldron = this.leftArm.getChild("left_pauldron");
        this.right_plate = this.rightLeg.getChild("right_plate");
        this.left_plate = this.leftLeg.getChild("left_plate");
        this.right_boot = this.rightLeg.getChild("right_boot");
        this.left_boot = this.leftLeg.getChild("left_boot");
    }

    public static LayerDefinition createOuterArmorLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(0.0F), 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 26)
                        .addBox(-4.5F, -9.0F, -4.5F, 9.0F, 10.0F, 9.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 0).addBox(-4.5F, -9.0F, -4.5F, 9.0F, 10.0F, 9.0F, new CubeDeformation(0.25F))
                        .texOffs(78, 54).addBox(-4.75F, -5.0F, -4.75F, 4.0F, 6.0F, 5.0F, new CubeDeformation(0.0F))
                        .texOffs(78, 54).mirror()
                        .addBox(0.75F, -5.0F, -4.75F, 4.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
                        .texOffs(66, 32).addBox(-5.5F, -5.0F, -5.5F, 11.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 19).addBox(-2.0F, -5.0F, -5.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(32, 19).addBox(1.0F, -5.0F, -5.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(40, 80).addBox(-5.5F, -5.0F, -0.5F, 11.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(68, 0).addBox(-5.5F, -3.0F, 0.5F, 11.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                        .texOffs(40, 69).addBox(-1.0F, -11.0F, -5.5F, 2.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(24, 50).addBox(-1.0F, -11.0F, -6.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(84, 46).addBox(-6.25F, -11.5F, -4.45F, 6.0F, 6.0F, 1.0F, new CubeDeformation(0.5F))
                        .texOffs(84, 46).mirror()
                        .addBox(0.25F, -11.5F, -4.45F, 6.0F, 6.0F, 1.0F, new CubeDeformation(0.5F)).mirror(false),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition helmet = head.addOrReplaceChild("helmet",
                CubeListBuilder.create().texOffs(68, 6)
                        .addBox(-2.0F, -4.0F, -7.0F, 4.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(84, 38).addBox(-1.5F, -2.0F, -6.0F, 3.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -7.0F, 4.5F, -0.2618F, 0.0F, 0.0F));

        PartDefinition feather = helmet.addOrReplaceChild("feather", CubeListBuilder.create().texOffs(24, 45).addBox(
                -1.5F, -4.0F, -3.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 0.0F));

        PartDefinition feather1 = feather.addOrReplaceChild("feather1",
                CubeListBuilder.create().texOffs(24, 55).addBox(-2.5F, -5.0F, 0.0F, 5.0F, 6.0F, 8.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -4.0F, -2.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition feather2 = feather.addOrReplaceChild("feather2",
                CubeListBuilder.create().texOffs(24, 55).addBox(-2.5F, -5.0F, 0.0F, 5.0F, 6.0F, 8.0F,
                        new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -4.0F, -2.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition feather3 = feather.addOrReplaceChild("feather3", CubeListBuilder.create().texOffs(24, 55).addBox(
                -2.5F, -5.0F, 0.0F, 5.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, -2.0F));

        PartDefinition feather4 = helmet.addOrReplaceChild("feather4", CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = feather4.addOrReplaceChild("cube_r1",
                CubeListBuilder.create().texOffs(84, 83).addBox(-0.5F, -10.0F, -7.5F, 1.0F, 20.0F, 21.0F,
                        new CubeDeformation(0.5F)),
                PartPose.offsetAndRotation(-1.0F, -3.0F, 1.0F, 0.2182F, 0.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 45)
                        .addBox(-4.0F, -0.2F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.1F))
                        .texOffs(66, 19).addBox(-4.0F, 0.0F, -2.5F, 8.0F, 8.0F, 5.0F, new CubeDeformation(0.49F))
                        .texOffs(64, 83).addBox(-5.0F, 0.25F, -4.0F, 10.0F, 4.0F, 1.0F, new CubeDeformation(0.1F))
                        .texOffs(50, 67).addBox(-4.0F, 5.65F, -2.5F, 8.0F, 8.0F, 5.0F, new CubeDeformation(0.5F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cape = body.addOrReplaceChild("cape",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, 0.0F, -4.0F, 12.0F, 22.0F, 4.0F,
                        new CubeDeformation(0.25F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(),
                PartPose.offset(-5.5F, 2.0F, 0.0F));

        PartDefinition right_hand = right_arm.addOrReplaceChild("right_hand",
                CubeListBuilder.create().texOffs(76, 67).mirror()
                        .addBox(-3.0F, -2.25F, -2.0F, 5.0F, 12.0F, 4.0F, new CubeDeformation(0.1F)).mirror(false)
                        .texOffs(0, 79).mirror()
                        .addBox(-2.5F, -1.9F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.01F)).mirror(false),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_pauldron = right_arm.addOrReplaceChild("right_pauldron",
                CubeListBuilder.create().texOffs(50, 55).mirror()
                        .addBox(-6.0F, -4.25F, -3.0F, 8.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
                        .texOffs(40, 84).mirror()
                        .addBox(-3.0F, -5.25F, -2.0F, 5.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.2654F));

        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(),
                PartPose.offset(5.5F, 2.0F, 0.0F));

        PartDefinition left_hand = left_arm.addOrReplaceChild("left_hand",
                CubeListBuilder.create().texOffs(76, 67)
                        .addBox(-2.0F, -2.25F, -2.0F, 5.0F, 12.0F, 4.0F, new CubeDeformation(0.1F))
                        .texOffs(0, 79).addBox(-1.5F, -1.9F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.01F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_pauldron = left_arm.addOrReplaceChild("left_pauldron",
                CubeListBuilder.create().texOffs(50, 55)
                        .addBox(-2.0F, -4.25F, -3.0F, 8.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(40, 84).addBox(-2.0F, -5.25F, -2.0F, 5.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.2654F));

        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(),
                PartPose.offset(-1.9F, 12.0F, 0.0F));

        PartDefinition right_plate = right_leg.addOrReplaceChild("right_plate",
                CubeListBuilder.create().texOffs(0, 61).mirror()
                        .addBox(-2.5F, -1.0F, -2.5F, 5.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false)
                        .texOffs(20, 69).mirror()
                        .addBox(-2.5F, -1.0F, -2.5F, 5.0F, 13.0F, 5.0F, new CubeDeformation(0.25F)).mirror(false),
                PartPose.offsetAndRotation(-0.1F, -1.5F, 0.0F, 0.0F, 0.0F, 0.0349F));

        PartDefinition right_boot = right_leg.addOrReplaceChild("right_boot",
                CubeListBuilder.create().texOffs(68, 38)
                        .addBox(-2.3F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)),
                PartPose.offset(-0.1F, 0.0F, 0.0F));

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        PartDefinition left_plate = left_leg.addOrReplaceChild("left_plate",
                CubeListBuilder.create().texOffs(0, 61)
                        .addBox(-2.5F, -1.0F, -2.5F, 5.0F, 13.0F, 5.0F, new CubeDeformation(0.0F))
                        .texOffs(20, 69).addBox(-2.5F, -1.0F, -2.5F, 5.0F, 13.0F, 5.0F, new CubeDeformation(0.25F)),
                PartPose.offsetAndRotation(0.1F, -1.5F, 0.0F, 0.0F, 0.0F, -0.0349F));

        PartDefinition left_boot = left_leg.addOrReplaceChild("left_boot",
                CubeListBuilder.create().texOffs(68, 38).mirror()
                        .addBox(-1.7F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)).mirror(false),
                PartPose.offset(0.1F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
            float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (!this.body.visible) {
            this.capeVisible = false;
            return;
        }
        if (entity instanceof AbstractClientPlayer) {
            this.capeVisible = false;
        } else {
            this.capeVisible = true;
            float f1 = Mth.sin(limbSwing * 6.0F) * 32.0F * limbSwingAmount;
            f1 = Mth.clamp(f1, -6.0F, 32.0F);
            float f2 = limbSwingAmount * 100.0F;
            f2 = Mth.clamp(f2, 0.0F, 150.0F);
            if (f2 < 0.0F)
                f2 = 0.0F;
            float f3 = 0.0F;
            f3 = Mth.clamp(f3, -20.0F, 20.0F);
            if (entity.isCrouching())
                f1 += 25.0F;
            this.capeF1 = f1;
            this.capeF2 = f2;
            this.capeF3 = f3;
            this.capeCrouching = entity.isCrouching();
        }
    }

    public ChampionArmorModel animate(LivingEntity entity) {
        return this;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        this.cape.visible = false;
        this.bodyParts().forEach((modelPart -> modelPart.render(poseStack, vertexConsumer, packedLight, packedOverlay,
                red, green, blue, alpha)));

        if (this.capeVisible) {
            poseStack.pushPose();
            this.body.translateAndRotate(poseStack);
            poseStack.translate(0.0F, 0.0F, 4.0F);
            poseStack.mulPose(Axis.XP.rotation(0.1745F));
            poseStack.translate(0.0F, 0.0F, 0.125F);
            if (this.capeCrouching) {
                poseStack.translate(0.0D, 0.1D, 0.0D);
            }
            poseStack.mulPose(Axis.XP.rotationDegrees(6.0F + this.capeF2 / 2.0F + this.capeF1));
            poseStack.mulPose(Axis.ZP.rotationDegrees(this.capeF3 / 2.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - this.capeF3 / 2.0F));
            this.cape.x = 0.0F;
            this.cape.y = 0.0F;
            this.cape.z = 0.0F;
            this.cape.xRot = 0.0F;
            this.cape.yRot = 0.0F;
            this.cape.zRot = 0.0F;
            this.cape.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();
        }
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.head, this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg);
    }
}
