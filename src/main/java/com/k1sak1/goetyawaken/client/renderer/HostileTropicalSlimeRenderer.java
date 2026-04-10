package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.client.renderer.layers.TropicalSlimeFishesLayer;
import com.k1sak1.goetyawaken.client.renderer.layers.TropicalSlimeOuterLayer;
import com.k1sak1.goetyawaken.client.renderer.layers.TropicalSlimeSecretLayer;
import com.k1sak1.goetyawaken.common.entities.hostile.HostileTropicalSlime;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class HostileTropicalSlimeRenderer extends
                MobRenderer<HostileTropicalSlime, com.Polarice3.Goety.client.render.model.TropicalSlimeModel<HostileTropicalSlime>> {
        private static final ResourceLocation GOETY_TEXTURE_1 = new ResourceLocation("goety",
                        "textures/entity/servants/slime/tropical_slime.png");
        private static final ResourceLocation GOETY_TEXTURE_2 = new ResourceLocation("goety",
                        "textures/entity/servants/slime/slime_servant_secret.png");

        public HostileTropicalSlimeRenderer(EntityRendererProvider.Context pContext) {
                super(pContext,
                                new com.Polarice3.Goety.client.render.model.TropicalSlimeModel<>(
                                                pContext.bakeLayer(
                                                                com.Polarice3.Goety.client.render.ModModelLayer.TROPICAL_SLIME_INNER)),
                                0.25F);
                this.addLayer(new TropicalSlimeFishesLayer<>(this, pContext.getModelSet()));
                this.addLayer(new TropicalSlimeSecretLayer<>(this, pContext.getModelSet()));
                this.addLayer(new TropicalSlimeOuterLayer<>(this, pContext.getModelSet()));
        }

        @Override
        public ResourceLocation getTextureLocation(HostileTropicalSlime pEntity) {
                return GOETY_TEXTURE_1;
        }

        @Override
        protected void scale(HostileTropicalSlime pLivingEntity, PoseStack pMatrixStack, float pPartialTickTime) {
                float f = 0.999F;
                pMatrixStack.scale(f, f, f);
                pMatrixStack.translate(0.0D, (double) 0.001F, 0.0D);
                float f1 = (float) pLivingEntity.getSize();
                float f2 = Mth.lerp(pPartialTickTime, pLivingEntity.oSquish, pLivingEntity.squish) / (f1 * 0.5F + 1.0F);
                float f3 = 1.0F / (f2 + 1.0F);
                pMatrixStack.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
        }
}