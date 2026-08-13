package com.k1sak1.goetyawaken.client.renderer;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.k1sak1.goetyawaken.client.model.illager.WindCallerModel;
import com.k1sak1.goetyawaken.common.entities.hostile.illager.WindCaller;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WindCallerRenderer<T extends WindCaller> extends MobRenderer<T, WindCallerModel<T>> {
    protected static final ResourceLocation ORIGINAL = Goety
            .location("textures/entity/servants/illager/wind_caller_original.png");

    public WindCallerRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new WindCallerModel<>(renderManagerIn.bakeLayer(ModModelLayer.WIND_CALLER)), 0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, renderManagerIn));
    }

    protected void scale(T entity, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(f, f, f);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return ORIGINAL;
    }
}
