package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.common.entities.ally.CorruptedSlime;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class CorruptedSlimeRenderer extends
        MobRenderer<CorruptedSlime, SlimeModel<CorruptedSlime>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/slimepurple.png");

    public CorruptedSlimeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext,
                new SlimeModel<>(pContext.bakeLayer(net.minecraft.client.model.geom.ModelLayers.SLIME)),
                0.25F);
        this.addLayer(new SlimeOuterLayer<>(this, pContext.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(CorruptedSlime pEntity) {
        return TEXTURE;
    }

    @Override
    protected void scale(CorruptedSlime pLivingEntity, PoseStack pMatrixStack, float pPartialTickTime) {
        float f = 0.999F;
        pMatrixStack.scale(f, f, f);
        pMatrixStack.translate(0.0D, (double) 0.001F, 0.0D);
        float f1 = (float) pLivingEntity.getSize();
        float f2 = Mth.lerp(pPartialTickTime, pLivingEntity.oSquish, pLivingEntity.squish) / (f1 * 0.5F + 1.0F);
        float f3 = 1.0F / (f2 + 1.0F);
        pMatrixStack.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
    }
}
