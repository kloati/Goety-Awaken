package com.k1sak1.goetyawaken.client.renderer.illager;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.RoyalguardModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.common.entities.hostile.illager.HostileRoyalguard;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;

public class HostileRoyalguardRenderer extends MobRenderer<HostileRoyalguard, RoyalguardModel<HostileRoyalguard>> {
    private static final ResourceLocation TEXTURE = GoetyAwaken
            .location("textures/entity/illager/hostileroyalguard.png");

    public HostileRoyalguardRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new RoyalguardModel<>(renderManagerIn.bakeLayer(ClientEventHandler.ROYALGUARD_LAYER)),
                0.5F);
        this.addLayer(new CustomHeadLayer<HostileRoyalguard, RoyalguardModel<HostileRoyalguard>>(this,
                renderManagerIn.getModelSet(), renderManagerIn.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(HostileRoyalguard entity) {
        return TEXTURE;
    }
}
