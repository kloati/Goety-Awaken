package com.k1sak1.goetyawaken.client.renderer.undead.skeleton;

import com.k1sak1.goetyawaken.client.model.ParchedModel;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.skeleton.Parched;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class ParchedRenderer extends HumanoidMobRenderer<Parched, ParchedModel<Parched>> {

    private static final ResourceLocation PARCHED_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/parched.png");

    public ParchedRenderer(EntityRendererProvider.Context renderManager) {
        this(renderManager, ParchedModel.LAYER_LOCATION, ModelLayers.SKELETON_INNER_ARMOR,
                ModelLayers.SKELETON_OUTER_ARMOR);
    }

    public ParchedRenderer(EntityRendererProvider.Context renderManager, ModelLayerLocation skeletonLayer,
            ModelLayerLocation innerArmorLayer, ModelLayerLocation outerArmorLayer) {
        super(renderManager, new ParchedModel<>(renderManager.bakeLayer(skeletonLayer)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new ParchedModel<>(renderManager.bakeLayer(innerArmorLayer)),
                new ParchedModel<>(renderManager.bakeLayer(outerArmorLayer)),
                renderManager.getModelManager()));
    }

    @Override
    public ResourceLocation getTextureLocation(Parched entity) {
        return PARCHED_TEXTURE;
    }
}