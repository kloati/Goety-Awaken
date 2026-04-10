package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.client.model.CaerbannogRabbitServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.CaerbannogRabbitServant;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CaerbannogRabbitServantRenderer
        extends MobRenderer<CaerbannogRabbitServant, CaerbannogRabbitServantModel> {
    private static final ResourceLocation CAERBANNOG_RABBIT_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/caerbannog_rabbit_servant.png");

    public CaerbannogRabbitServantRenderer(EntityRendererProvider.Context context) {
        super(context,
                new CaerbannogRabbitServantModel(context.bakeLayer(ClientEventHandler.CAERBANNOG_RABBIT_SERVANT_LAYER)),
                0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(CaerbannogRabbitServant entity) {
        return CAERBANNOG_RABBIT_TEXTURE;
    }
}