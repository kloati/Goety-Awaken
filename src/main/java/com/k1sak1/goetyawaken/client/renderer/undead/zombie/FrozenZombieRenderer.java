package com.k1sak1.goetyawaken.client.renderer.undead.zombie;

import com.Polarice3.Goety.client.render.model.PlayerZombieModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.zombie.FrozenZombie;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FrozenZombieRenderer extends HumanoidMobRenderer<FrozenZombie, PlayerZombieModel<FrozenZombie>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("goety",
            "textures/entity/servants/zombie/frozen_zombie_original.png");

    public FrozenZombieRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerZombieModel<>(context.bakeLayer(ClientEventHandler.FROZEN_ZOMBIE_LAYER)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
    }

    @Override
    public ResourceLocation getTextureLocation(FrozenZombie entity) {
        return TEXTURE;
    }
}
