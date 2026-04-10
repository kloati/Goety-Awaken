package com.k1sak1.goetyawaken.client.renderer.layers;

import com.k1sak1.goetyawaken.client.model.WitherServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.WitherServant;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WitherServantArmorLayer extends EnergySwirlLayer<WitherServant, WitherServantModel> {
    private static final ResourceLocation WITHER_ARMOR_LOCATION = new ResourceLocation(
            "textures/entity/wither/wither_armor.png");
    private final WitherServantModel model;

    public WitherServantArmorLayer(RenderLayerParent<WitherServant, WitherServantModel> pRenderer,
            EntityModelSet pModelSet) {
        super(pRenderer);
        this.model = new WitherServantModel(pModelSet.bakeLayer(ModelLayers.WITHER_ARMOR));
    }

    @Override
    protected float xOffset(float pTickCount) {
        return Mth.cos(pTickCount * 0.02F) * 3.0F;
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return WITHER_ARMOR_LOCATION;
    }

    @Override
    protected EntityModel<WitherServant> model() {
        return this.model;
    }
}