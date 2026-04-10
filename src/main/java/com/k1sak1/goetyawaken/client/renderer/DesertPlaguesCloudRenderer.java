package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.common.entities.projectiles.DesertPlaguesCloud;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

public class DesertPlaguesCloudRenderer extends EntityRenderer<DesertPlaguesCloud> {

    public DesertPlaguesCloudRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(DesertPlaguesCloud entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}