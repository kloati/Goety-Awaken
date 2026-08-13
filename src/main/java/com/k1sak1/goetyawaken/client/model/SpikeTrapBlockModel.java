package com.k1sak1.goetyawaken.client.model;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.animation.SpikeTrapBlockAnimation;
import com.k1sak1.goetyawaken.common.blocks.entity.SpikeTrapBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpikeTrapBlockModel extends HierarchicalModel<Entity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(GoetyAwaken.MODID, "spike_trap_block"), "main");

    private final ModelPart root;
    private final ModelPart bone;
    private final ModelPart spike;
    private final ModelPart bone2;
    private final ModelPart bone3;

    public SpikeTrapBlockModel(ModelPart root) {
        this.root = root;
        this.bone = root.getChild("bone");
        this.spike = root.getChild("spike");
        this.bone2 = this.spike.getChild("bone2");
        this.bone3 = root.getChild("bone3");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 32)
                .addBox(-16.0F, -16.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)),
                PartPose.offset(8.0F, 24.0F, -8.0F));

        PartDefinition spike = partdefinition.addOrReplaceChild("spike",
                CubeListBuilder.create().texOffs(8, 17)
                        .addBox(-1.98F, -6.0F, 0.98F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.01F))
                        .texOffs(8, 0).addBox(-1.0F, -7.98F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.01F))
                        .texOffs(14, 9).addBox(-1.0F, -7.0F, 0.98F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.01F))
                        .texOffs(8, 13).addBox(0.98F, -6.0F, 0.98F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.01F))
                        .texOffs(12, 13).addBox(0.98F, -6.0F, -1.98F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.01F))
                        .texOffs(14, 4).addBox(-1.98F, -7.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.01F))
                        .texOffs(8, 13).addBox(-1.98F, -6.0F, -1.98F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.01F))
                        .texOffs(8, 9).addBox(-1.0F, -7.0F, -1.98F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.01F))
                        .texOffs(8, 4).addBox(0.98F, -7.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(-0.01F)),
                PartPose.offset(0.0F, 16.0F, -2.0F));

        PartDefinition bone2 = spike.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F,
                -14.0F, 7.0F, 2.0F, 14.0F, 2.0F, new CubeDeformation(-0.01F)), PartPose.offset(8.0F, 8.0F, -8.0F));

        PartDefinition bone3 = partdefinition.addOrReplaceChild("bone3",
                CubeListBuilder.create().texOffs(20, 0)
                        .addBox(-1.0F, -5.1667F, -0.5F, 2.0F, 15.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(26, 4).addBox(1.0F, -2.1667F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(27, 8).addBox(-2.0F, -2.1667F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 13.4167F, -1.5F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(Entity pEntity, float pLimbSwing, float pLimbSwingAmount,
            float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
    }

    public void setupAnim(SpikeTrapBlockEntity pEntity, float pAgeInTicks) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animate(pEntity.spikeAnimationState, SpikeTrapBlockAnimation.SPIKE, pAgeInTicks);
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight,
            int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        this.bone.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.spike.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        this.bone3.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }
}
