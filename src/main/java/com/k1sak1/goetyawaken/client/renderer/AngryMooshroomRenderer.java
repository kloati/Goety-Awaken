package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.client.model.AngryMooshroomModel;
import com.k1sak1.goetyawaken.client.renderer.layers.AngryMooshroomMushroomLayer;
import com.k1sak1.goetyawaken.common.entities.ally.AngryMooshroom;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AngryMooshroomRenderer extends MobRenderer<AngryMooshroom, AngryMooshroomModel<AngryMooshroom>> {
    private static final ResourceLocation RED_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/angrymooshroom.png");
    private static final ResourceLocation BROWN_TEXTURE = new ResourceLocation(
            "textures/entity/cow/brown_mooshroom.png");

    public AngryMooshroomRenderer(EntityRendererProvider.Context p_174324_) {
        super(p_174324_, new AngryMooshroomModel<>(
                p_174324_.bakeLayer(ModelLayers.MOOSHROOM)), 0.7F);
        this.addLayer(new AngryMooshroomMushroomLayer(this, p_174324_.getBlockRenderDispatcher()));
    }

    @Override
    public ResourceLocation getTextureLocation(AngryMooshroom pEntity) {
        return pEntity.isBrownVariant() ? BROWN_TEXTURE : RED_TEXTURE;
    }

    @Override
    public void render(AngryMooshroom pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack,
            MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }
}