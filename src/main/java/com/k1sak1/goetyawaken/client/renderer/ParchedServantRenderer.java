package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.client.model.ParchedModel;
import com.k1sak1.goetyawaken.client.renderer.layers.ParchedServantOverlayLayer;
import com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton.ParchedServant;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParchedServantRenderer extends HumanoidMobRenderer<ParchedServant, ParchedModel<ParchedServant>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/parched_servant.png");

    public ParchedServantRenderer(EntityRendererProvider.Context context) {
        super(context, new ParchedModel<>(context.bakeLayer(ParchedModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new ParchedModel<>(context.bakeLayer(ModelLayers.SKELETON_INNER_ARMOR)),
                new ParchedModel<>(context.bakeLayer(ModelLayers.SKELETON_OUTER_ARMOR)),
                context.getModelManager()));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new ParchedServantOverlayLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(ParchedServant entity) {
        return TEXTURE;
    }
}