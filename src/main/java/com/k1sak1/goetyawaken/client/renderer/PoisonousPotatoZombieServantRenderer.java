package com.k1sak1.goetyawaken.client.renderer;

import com.Polarice3.Goety.client.render.AbstractZombieServantRenderer;
import com.Polarice3.Goety.client.render.model.ZombieServantModel;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.entities.ally.PoisonousPotatoZombieServant;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PoisonousPotatoZombieServantRenderer
        extends
        AbstractZombieServantRenderer<PoisonousPotatoZombieServant, ZombieServantModel<PoisonousPotatoZombieServant>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            GoetyAwaken.MODID, "textures/entity/undead/zombie/poisonous_potato_zombie.png");
    private static final ResourceLocation HOSTILE_TEXTURE = new ResourceLocation(
            GoetyAwaken.MODID, "textures/entity/undead/zombie/poisonous_potato_zombie_origin.png");

    public PoisonousPotatoZombieServantRenderer(EntityRendererProvider.Context context) {
        this(context, ModelLayers.ZOMBIE, ModelLayers.ZOMBIE_INNER_ARMOR, ModelLayers.ZOMBIE_OUTER_ARMOR);
    }

    public PoisonousPotatoZombieServantRenderer(EntityRendererProvider.Context context,
            net.minecraft.client.model.geom.ModelLayerLocation modelLayer,
            net.minecraft.client.model.geom.ModelLayerLocation innerArmorLayer,
            net.minecraft.client.model.geom.ModelLayerLocation outerArmorLayer) {
        super(context,
                new ZombieServantModel<>(context.bakeLayer(modelLayer)),
                new ZombieServantModel<>(context.bakeLayer(innerArmorLayer)),
                new ZombieServantModel<>(context.bakeLayer(outerArmorLayer)));
    }

    @Override
    public ResourceLocation getTextureLocation(PoisonousPotatoZombieServant entity) {
        if (entity.isHostile()) {
            return HOSTILE_TEXTURE;
        } else {
            return TEXTURE;
        }
    }
}
