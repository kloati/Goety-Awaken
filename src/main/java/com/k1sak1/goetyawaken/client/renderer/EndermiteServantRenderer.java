package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.EndermiteServantModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.common.entities.ally.EndermiteServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class EndermiteServantRenderer extends MobRenderer<EndermiteServant, EndermiteServantModel> {
    private static final ResourceLocation ENDERMITE_SERVANT_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/endermite_servant.png");

    public EndermiteServantRenderer(EntityRendererProvider.Context context) {
        super(context, new EndermiteServantModel(context.bakeLayer(ClientEventHandler.ENDERMITE_SERVANT_LAYER)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(EndermiteServant entity) {
        return ENDERMITE_SERVANT_TEXTURE;
    }
}