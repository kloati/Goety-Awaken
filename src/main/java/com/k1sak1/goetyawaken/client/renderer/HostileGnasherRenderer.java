package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.GoetyAwaken;

import com.k1sak1.goetyawaken.common.entities.hostile.HostileGnasher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

public class HostileGnasherRenderer
        extends MobRenderer<HostileGnasher, com.Polarice3.Goety.client.render.model.GnasherModel<HostileGnasher>> {
    protected static final ResourceLocation TEXTURE = GoetyAwaken.location("textures/entity/hostile_gnasher.png");

    public HostileGnasherRenderer(EntityRendererProvider.Context p_174364_) {
        super(p_174364_, new com.Polarice3.Goety.client.render.model.GnasherModel<>(
                p_174364_.bakeLayer(com.Polarice3.Goety.client.render.ModModelLayer.GNASHER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(HostileGnasher p_115826_) {
        return TEXTURE;
    }

    @Override
    protected void scale(HostileGnasher p_115314_, PoseStack p_115315_, float p_115316_) {
        int i = p_115314_.isUpgraded() ? 1 : 0;
        float f = 1.2F + 0.15F * (float) i;
        if (p_115314_.isBaby()) {
            f /= 2.0F;
        }
        p_115315_.scale(f, f, f);
    }

    protected void setupRotations(HostileGnasher p_115828_, PoseStack p_115829_, float p_115830_, float p_115831_,
            float p_115832_) {
        super.setupRotations(p_115828_, p_115829_, p_115830_, p_115831_, p_115832_);
        float f = 1.0F;
        float f1 = 1.0F;
        float f2 = f * 4.3F * Mth.sin(f1 * 0.6F * p_115830_);
        p_115829_.mulPose(Axis.YP.rotationDegrees(f2));
        p_115829_.translate(0.0F, 0.0F, 0.0F);
    }
}