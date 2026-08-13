package com.k1sak1.goetyawaken.client.renderer.ally.Integration;

import com.k1sak1.goetyawaken.client.model.ally.Integration.SculkLeechServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.SculkLeechServant;
import com.kyanite.deeperdarker.DeeperDarker;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

//Based on https://github.com/KyaniteMods/DeeperAndDarker/tree/forge-1.20, Original by kyanite
@SuppressWarnings("NullableProblems")
public class SculkLeechServantRenderer extends MobRenderer<SculkLeechServant, SculkLeechServantModel> {
    public static final ModelLayerLocation MODEL = new ModelLayerLocation(DeeperDarker.rl("sculk_leech_layer"), "main");
    private static final ResourceLocation TEXTURE = DeeperDarker.rl("textures/entity/sculk_leech.png");

    public SculkLeechServantRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new SculkLeechServantModel(pContext.bakeLayer(MODEL)), 0.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(SculkLeechServant pEntity) {
        return TEXTURE;
    }
}
