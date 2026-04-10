package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.client.model.WardenServantModel;
import com.k1sak1.goetyawaken.client.renderer.layers.WardenServantEmissiveLayer;
import com.k1sak1.goetyawaken.common.entities.ally.WardenServant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WardenServantRenderer extends MobRenderer<WardenServant, WardenServantModel> {
        private static final ResourceLocation TEXTURE = new ResourceLocation("goetyawaken",
                        "textures/entity/warden_servant.png");
        private static final ResourceLocation BIOLUMINESCENT_LAYER_TEXTURE = new ResourceLocation("minecraft",
                        "textures/entity/warden/warden_bioluminescent_layer.png");
        private static final ResourceLocation HEART_TEXTURE = new ResourceLocation("minecraft",
                        "textures/entity/warden/warden_heart.png");
        private static final ResourceLocation PULSATING_SPOTS_TEXTURE_1 = new ResourceLocation("minecraft",
                        "textures/entity/warden/warden_pulsating_spots_1.png");
        private static final ResourceLocation PULSATING_SPOTS_TEXTURE_2 = new ResourceLocation("minecraft",
                        "textures/entity/warden/warden_pulsating_spots_2.png");
        private static final ResourceLocation TENDRILS_TEXTURE = new ResourceLocation("minecraft",
                        "textures/entity/warden/warden_tendrils.png");

        public WardenServantRenderer(EntityRendererProvider.Context p_234787_) {
                super(p_234787_,
                                new WardenServantModel(p_234787_.bakeLayer(ModelLayers.WARDEN)),
                                0.9F);

                this.addLayer(new WardenServantEmissiveLayer(this, BIOLUMINESCENT_LAYER_TEXTURE,
                                (p_234809_, p_234810_, p_234811_) -> {
                                        return 1.0F;
                                }, WardenServantModel::getBioluminescentLayerModelParts));

                this.addLayer(
                                new WardenServantEmissiveLayer(this, PULSATING_SPOTS_TEXTURE_1,
                                                (p_234805_, p_234806_, p_234807_) -> {
                                                        return Math.max(0.0F, Mth.cos(p_234807_ * 0.045F) * 0.25F);
                                                }, WardenServantModel::getPulsatingSpotsLayerModelParts));

                this.addLayer(
                                new WardenServantEmissiveLayer(this, PULSATING_SPOTS_TEXTURE_2,
                                                (p_234801_, p_234802_, p_234803_) -> {
                                                        return Math.max(0.0F,
                                                                        Mth.cos(p_234803_ * 0.045F + (float) Math.PI)
                                                                                        * 0.25F);
                                                }, WardenServantModel::getPulsatingSpotsLayerModelParts));

                this.addLayer(new WardenServantEmissiveLayer(this, TEXTURE, (p_234797_, p_234798_, p_234799_) -> {
                        return p_234797_.getTendrilAnimation(p_234798_);
                }, WardenServantModel::getTendrilsLayerModelParts));

                this.addLayer(new WardenServantEmissiveLayer(this, HEART_TEXTURE, (p_234793_, p_234794_, p_234795_) -> {
                        return p_234793_.getHeartAnimation(p_234794_);
                }, WardenServantModel::getHeartLayerModelParts));
        }

        @Override
        public ResourceLocation getTextureLocation(WardenServant p_234791_) {
                return TEXTURE;
        }

        @Override
        protected void scale(WardenServant pLivingEntity, PoseStack pMatrixStack, float pPartialTickTime) {
        }

        @Override
        public void render(WardenServant pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack,
                        MultiBufferSource pBuffer, int pPackedLight) {
                super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
        }
}