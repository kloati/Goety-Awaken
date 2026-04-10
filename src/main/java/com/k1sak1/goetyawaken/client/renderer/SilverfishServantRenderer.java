package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.SilverfishServantModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.common.entities.ally.SilverfishServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SilverfishServantRenderer extends MobRenderer<SilverfishServant, SilverfishServantModel> {
    private static final ResourceLocation SILVERFISH_SERVANT_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/silverfish_servant.png");

    public SilverfishServantRenderer(EntityRendererProvider.Context context) {
        super(context, new SilverfishServantModel(context.bakeLayer(ClientEventHandler.SILVERFISH_SERVANT_LAYER)),
                0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(SilverfishServant entity) {
        return SILVERFISH_SERVANT_TEXTURE;
    }
}