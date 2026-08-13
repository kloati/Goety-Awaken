package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.entities.ally.PoisonousPotatoSkeletonServant;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PoisonousPotatoSkeletonServantRenderer
        extends HumanoidMobRenderer<PoisonousPotatoSkeletonServant, SkeletonModel<PoisonousPotatoSkeletonServant>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            GoetyAwaken.MODID, "textures/entity/undead/skeleton/poisonous_skeleton_potato.png");

    public PoisonousPotatoSkeletonServantRenderer(EntityRendererProvider.Context context) {
        this(context, ModelLayers.SKELETON, ModelLayers.SKELETON_INNER_ARMOR, ModelLayers.SKELETON_OUTER_ARMOR);
    }

    public PoisonousPotatoSkeletonServantRenderer(EntityRendererProvider.Context context,
            ModelLayerLocation modelLayer,
            ModelLayerLocation innerArmorLayer,
            ModelLayerLocation outerArmorLayer) {
        super(context, new SkeletonModel<>(context.bakeLayer(modelLayer)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new SkeletonModel<>(context.bakeLayer(innerArmorLayer)),
                new SkeletonModel<>(context.bakeLayer(outerArmorLayer)),
                context.getModelManager()));
    }

    @Override
    public ResourceLocation getTextureLocation(PoisonousPotatoSkeletonServant entity) {
        return TEXTURE;
    }

    @Override
    protected boolean isShaking(PoisonousPotatoSkeletonServant entity) {
        return entity.isShaking();
    }
}
