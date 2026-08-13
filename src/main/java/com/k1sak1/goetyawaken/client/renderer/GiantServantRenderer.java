package com.k1sak1.goetyawaken.client.renderer;

import com.Polarice3.Goety.client.render.AbstractZombieServantRenderer;
import com.Polarice3.Goety.client.render.model.ZombieServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.GiantServant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GiantServantRenderer
        extends AbstractZombieServantRenderer<GiantServant, ZombieServantModel<GiantServant>> {
    public GiantServantRenderer(EntityRendererProvider.Context context) {
        this(context, ModelLayers.ZOMBIE, ModelLayers.ZOMBIE_INNER_ARMOR, ModelLayers.ZOMBIE_OUTER_ARMOR);
    }

    public GiantServantRenderer(EntityRendererProvider.Context context,
            net.minecraft.client.model.geom.ModelLayerLocation modelLayer,
            net.minecraft.client.model.geom.ModelLayerLocation innerArmorLayer,
            net.minecraft.client.model.geom.ModelLayerLocation outerArmorLayer) {
        super(context,
                new ZombieServantModel<>(context.bakeLayer(modelLayer)),
                new ZombieServantModel<>(context.bakeLayer(innerArmorLayer)),
                new ZombieServantModel<>(context.bakeLayer(outerArmorLayer)));
    }

    @Override
    protected void scale(GiantServant entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(6.0F, 6.0F, 6.0F);
    }
}
