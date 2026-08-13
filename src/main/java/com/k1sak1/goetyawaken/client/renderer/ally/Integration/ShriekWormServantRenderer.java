package com.k1sak1.goetyawaken.client.renderer.ally.Integration;

import com.k1sak1.goetyawaken.client.model.ally.Integration.ShriekWormServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.ShriekWormServant;
import com.kyanite.deeperdarker.DeeperDarker;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

//Based on https://github.com/KyaniteMods/DeeperAndDarker/tree/forge-1.20, Original by kyanite
@SuppressWarnings("NullableProblems")
public class ShriekWormServantRenderer extends MobRenderer<ShriekWormServant, ShriekWormServantModel> {
    public static final ModelLayerLocation MODEL = new ModelLayerLocation(DeeperDarker.rl("shriek_worm"), "main");
    private static final ResourceLocation TEXTURE = DeeperDarker.rl("textures/entity/shriek_worm.png");

    public ShriekWormServantRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new ShriekWormServantModel(pContext.bakeLayer(MODEL)), 1.2f);
    }

    @Override
    public ResourceLocation getTextureLocation(ShriekWormServant pEntity) {
        return TEXTURE;
    }
}
