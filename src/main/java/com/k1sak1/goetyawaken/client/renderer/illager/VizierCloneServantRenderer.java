package com.k1sak1.goetyawaken.client.renderer.illager;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.VizierCloneServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.illager.VizierCloneServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class VizierCloneServantRenderer extends MobRenderer<VizierCloneServant, VizierCloneServantModel> {
    protected static final ResourceLocation TEXTURE = GoetyAwaken
            .location("textures/entity/illager/vizier/vizier_clone.png");

    public VizierCloneServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new VizierCloneServantModel(
                renderManagerIn.bakeLayer(com.k1sak1.goetyawaken.client.ClientEventHandler.VIZIER_CLONE_SERVANT_LAYER)),
                0.5F);
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()));
    }

    @Override
    protected int getBlockLightLevel(VizierCloneServant p_114496_, BlockPos p_114497_) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(VizierCloneServant entity) {
        return TEXTURE;
    }
}