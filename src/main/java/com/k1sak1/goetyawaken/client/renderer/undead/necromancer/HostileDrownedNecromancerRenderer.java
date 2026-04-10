package com.k1sak1.goetyawaken.client.renderer.undead.necromancer;

import com.Polarice3.Goety.client.render.DrownedNecromancerRenderer;
import com.Polarice3.Goety.common.entities.neutral.DrownedNecromancer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class HostileDrownedNecromancerRenderer extends DrownedNecromancerRenderer {
    private static final ResourceLocation DROWNED_NECROMANCER_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/drowned_necromancer.png");

    public HostileDrownedNecromancerRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(DrownedNecromancer entity) {
        return DROWNED_NECROMANCER_TEXTURE;
    }
}
