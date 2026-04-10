package com.k1sak1.goetyawaken.client.renderer.undead.skeleton;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.undead.skeleton.SunkenSkeletonModel;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.skeleton.SunkenSkeleton;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SunkenSkeletonRenderer extends HumanoidMobRenderer<SunkenSkeleton, SunkenSkeletonModel<SunkenSkeleton>> {
    private static final ResourceLocation TEXTURES = new ResourceLocation("goety", "textures/entity/servants/skeleton/sunken_skeleton.png");

    public SunkenSkeletonRenderer(EntityRendererProvider.Context pContext) {
        this(pContext, new ModelLayerLocation(GoetyAwaken.location("sunken_skeleton"), "main"), ModelLayers.SKELETON_INNER_ARMOR, ModelLayers.SKELETON_OUTER_ARMOR);
    }

    public SunkenSkeletonRenderer(EntityRendererProvider.Context pContext, ModelLayerLocation pModelLayerLocation, ModelLayerLocation pInnerArmorLayerLocation, ModelLayerLocation pOuterArmorLayerLocation) {
        super(pContext, new SunkenSkeletonModel<>(pContext.bakeLayer(pModelLayerLocation)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new SunkenSkeletonModel<>(pContext.bakeLayer(pInnerArmorLayerLocation)), new SunkenSkeletonModel<>(pContext.bakeLayer(pOuterArmorLayerLocation)), pContext.getModelManager()));
    }

    @Override
    public ResourceLocation getTextureLocation(SunkenSkeleton pEntity) {
        return TEXTURES;
    }

    @Override
    protected void setupRotations(SunkenSkeleton pEntity, PoseStack pPoseStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks) {
        super.setupRotations(pEntity, pPoseStack, pAgeInTicks, pRotationYaw, pPartialTicks);
        float f = pEntity.getSwimAmount(pPartialTicks);
        if (f > 0.0F) {
            pPoseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(f, pEntity.getXRot(), -10.0F - pEntity.getXRot())));
        }
    }

    @Override
    protected boolean isShaking(SunkenSkeleton pEntity) {
        return pEntity.isShaking();
    }

    public static class SunkenSkeletonLayer<T extends SunkenSkeleton, M extends EntityModel<T>> extends RenderLayer<T, M> {
        private static final ResourceLocation TEXTURES = new ResourceLocation("goety", "textures/entity/servants/skeleton/sunken_skeleton_overlay.png");
        private final SunkenSkeletonModel<T> layerModel;

        public SunkenSkeletonLayer(RenderLayerParent<T, M> pRenderer, EntityModelSet pModelSet) {
            super(pRenderer);
            this.layerModel = new SunkenSkeletonModel<>(pModelSet.bakeLayer(new ModelLayerLocation(GoetyAwaken.location("sunken_skeleton"), "main")));
        }

        @Override
        public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            
        }
    }
}