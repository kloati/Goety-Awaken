package com.k1sak1.goetyawaken.client.renderer;

import com.Polarice3.Goety.client.render.model.TwilightGoatModel;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.entities.hostile.HostileTwilightGoat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HostileTwilightGoatRenderer
        extends MobRenderer<HostileTwilightGoat, TwilightGoatModel<HostileTwilightGoat>> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/twilight_goat.png");

    public HostileTwilightGoatRenderer(EntityRendererProvider.Context p_174153_) {
        super(p_174153_, new TwilightGoatModel<>(p_174153_.bakeLayer(ModelLayers.GOAT)), 0.7F);
    }

    @Override
    protected void scale(HostileTwilightGoat p_115314_, PoseStack p_115315_, float p_115316_) {
        if (p_115314_.isUpgraded()) {
            p_115315_.scale(1.25F, 1.25F, 1.25F);
        }
        super.scale(p_115314_, p_115315_, p_115316_);
    }

    @Override
    public ResourceLocation getTextureLocation(HostileTwilightGoat p_174157_) {
        return TEXTURE_LOCATION;
    }
}
