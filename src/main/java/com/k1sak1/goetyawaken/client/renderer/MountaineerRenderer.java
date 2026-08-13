package com.k1sak1.goetyawaken.client.renderer;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.k1sak1.goetyawaken.client.model.illager.MountaineerModel;
import com.k1sak1.goetyawaken.common.entities.hostile.illager.Mountaineer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class MountaineerRenderer<T extends Mountaineer> extends MobRenderer<T, MountaineerModel<T>> {
    protected static final ResourceLocation ORIGINAL = Goety
            .location("textures/entity/servants/illager/mountaineer_original.png");

    public MountaineerRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new MountaineerModel<>(renderManagerIn.bakeLayer(ModModelLayer.MOUNTAINEER)), 0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, renderManagerIn));
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()));
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
