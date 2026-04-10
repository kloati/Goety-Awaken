package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.common.entities.hostile.HostileWildfire;
import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.WildfireModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HostileWildfireRenderer
        extends MobRenderer<HostileWildfire, WildfireModel<HostileWildfire>> {

    private static final ResourceLocation TEXTURE = Goety.location("textures/entity/servants/blaze/wildfire.png");

    public HostileWildfireRenderer(EntityRendererProvider.Context p_173933_) {
        super(p_173933_, new WildfireModel<>(p_173933_.bakeLayer(ModModelLayer.WILDFIRE)), 0.5F);
    }

    @Override
    protected void scale(HostileWildfire pEntity, com.mojang.blaze3d.vertex.PoseStack pMatrixStack,
            float pParticleTicks) {
        pMatrixStack.scale(1.4F, 1.4F, 1.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(HostileWildfire p_113908_) {
        return TEXTURE;
    }
}