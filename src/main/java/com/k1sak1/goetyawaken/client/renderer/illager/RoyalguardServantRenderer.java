package com.k1sak1.goetyawaken.client.renderer.illager;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.RoyalguardModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.common.entities.ally.illager.RoyalguardServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;

public class RoyalguardServantRenderer extends MobRenderer<RoyalguardServant, RoyalguardModel<RoyalguardServant>> {
    private static final ResourceLocation TEXTURE = GoetyAwaken.location("textures/entity/illager/royalguard.png");

    public RoyalguardServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new RoyalguardModel<>(renderManagerIn.bakeLayer(ClientEventHandler.ROYALGUARD_LAYER)),
                0.5F);
        this.addLayer(new CustomHeadLayer<RoyalguardServant, RoyalguardModel<RoyalguardServant>>(this,
                renderManagerIn.getModelSet(), renderManagerIn.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(RoyalguardServant entity) {
        return TEXTURE;
    }
}