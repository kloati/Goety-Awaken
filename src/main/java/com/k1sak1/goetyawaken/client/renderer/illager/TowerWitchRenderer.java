package com.k1sak1.goetyawaken.client.renderer.illager;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.illager.TowerWitchModel;
import com.k1sak1.goetyawaken.common.entities.hostile.illager.TowerWitch;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TowerWitchRenderer extends MobRenderer<TowerWitch, TowerWitchModel<TowerWitch>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/illager/tower_witch.png");
    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/illager/tower_witch_glow.png");

    public TowerWitchRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn,
                new TowerWitchModel<>(renderManagerIn.bakeLayer(TowerWitchModel.LAYER_LOCATION)),
                0.5F);
        this.addLayer(new TowerWitchItemLayer(this, renderManagerIn.getItemInHandRenderer()));
        this.addLayer(new HierarchicalArmorLayer(this, renderManagerIn));
    }

    @Override
    public void render(TowerWitch pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack,
            MultiBufferSource pBuffer, int pPackedLight) {
        this.model.setHoldingItem(!pEntity.getMainHandItem().isEmpty());
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(TowerWitch entity) {
        return TEXTURE;
    }

    public ResourceLocation getGlowTextureLocation() {
        return GLOW_TEXTURE;
    }

    static class TowerWitchItemLayer extends CrossedArmsItemLayer<TowerWitch, TowerWitchModel<TowerWitch>> {
        public TowerWitchItemLayer(RenderLayerParent<TowerWitch, TowerWitchModel<TowerWitch>> pRenderer,
                ItemInHandRenderer pItemInHandRenderer) {
            super(pRenderer, pItemInHandRenderer);
        }

        @Override
        public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight,
                TowerWitch pLivingEntity, float pLimbSwing, float pLimbSwingAmount,
                float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            ItemStack itemstack = pLivingEntity.getMainHandItem();
            pPoseStack.pushPose();
            if (itemstack.is(Items.POTION)) {
                this.getParentModel().getHead().translateAndRotate(pPoseStack);
                this.getParentModel().getNose().translateAndRotate(pPoseStack);
                pPoseStack.translate(0.0625F, 0.25F, 0.0F);
                pPoseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
                pPoseStack.mulPose(Axis.XP.rotationDegrees(140.0F));
                pPoseStack.mulPose(Axis.ZP.rotationDegrees(10.0F));
                pPoseStack.translate(0.0F, -0.4F, 0.4F);
            }

            super.render(pPoseStack, pBuffer, pPackedLight, pLivingEntity, pLimbSwing, pLimbSwingAmount,
                    pPartialTicks, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            pPoseStack.popPose();
        }
    }
}
