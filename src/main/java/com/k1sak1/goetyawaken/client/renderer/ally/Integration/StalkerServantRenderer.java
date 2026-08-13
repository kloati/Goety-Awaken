package com.k1sak1.goetyawaken.client.renderer.ally.Integration;

import com.k1sak1.goetyawaken.client.model.ally.Integration.StalkerServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.StalkerServant;
import com.kyanite.deeperdarker.DeeperDarker;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

//Based on https://github.com/KyaniteMods/DeeperAndDarker/tree/forge-1.20, Original by kyanite
@SuppressWarnings("NullableProblems")
public class StalkerServantRenderer extends MobRenderer<StalkerServant, StalkerServantModel> {
    public static final ModelLayerLocation MODEL = new ModelLayerLocation(DeeperDarker.rl("stalker_layer"),
            "main");
    private static final ResourceLocation TEXTURE = DeeperDarker.rl("textures/entity/stalker.png");

    public StalkerServantRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new StalkerServantModel(pContext.bakeLayer(MODEL)), 1);
    }

    @Override
    public ResourceLocation getTextureLocation(StalkerServant pEntity) {
        return TEXTURE;
    }
}
