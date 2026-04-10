package com.k1sak1.goetyawaken.client.renderer.undead.zombie;

import com.k1sak1.goetyawaken.client.model.BoulderingZombieModel;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.zombie.BoulderingZombie;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BoulderingZombieRenderer
        extends HumanoidMobRenderer<BoulderingZombie, BoulderingZombieModel<BoulderingZombie>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/bouldering_zombie.png");

    public BoulderingZombieRenderer(EntityRendererProvider.Context context) {
        super(context, new BoulderingZombieModel<>(context.bakeLayer(BoulderingZombieModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
    }

    @Override
    public ResourceLocation getTextureLocation(BoulderingZombie entity) {
        return TEXTURE;
    }

    @Override
    protected boolean isShaking(BoulderingZombie entity) {
        return super.isShaking(entity) || entity.isUnderWaterConverting();
    }
}