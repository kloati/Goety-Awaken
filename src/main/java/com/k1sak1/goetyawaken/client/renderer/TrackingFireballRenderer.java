package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.common.entities.projectiles.TrackingFireball;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class TrackingFireballRenderer extends ThrownItemRenderer<TrackingFireball> {

    public TrackingFireballRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, 3.0F, true);
    }
}
