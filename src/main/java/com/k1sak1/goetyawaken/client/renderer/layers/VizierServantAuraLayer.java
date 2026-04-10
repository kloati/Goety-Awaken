package com.k1sak1.goetyawaken.client.renderer.layers;

import com.k1sak1.goetyawaken.client.model.VizierServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.illager.VizierServant;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class VizierServantAuraLayer extends EnergySwirlLayer<VizierServant, VizierServantModel> {
    private static final ResourceLocation VIZIER_ARMOR = new ResourceLocation("goety",
            "textures/entity/illagers/vizierarmor.png");
    private final VizierServantModel model;

    public VizierServantAuraLayer(RenderLayerParent<VizierServant, VizierServantModel> renderer,
            EntityModelSet modelSet) {
        super(renderer);
        this.model = new VizierServantModel(
                modelSet.bakeLayer(com.k1sak1.goetyawaken.client.ClientEventHandler.VIZIER_SERVANT_ARMOR_LAYER));
    }

    @Override
    protected float xOffset(float p_225634_1_) {
        return Mth.cos(p_225634_1_ * 0.02F) * 3.0F;
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return VIZIER_ARMOR;
    }

    @Override
    protected EntityModel<VizierServant> model() {
        return this.model;
    }
}