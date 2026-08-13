package com.k1sak1.goetyawaken.client.renderer.ally.Integration;

import com.k1sak1.goetyawaken.client.model.ally.Integration.SculkCentipedeServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.SculkCentipedeServant;
import com.kyanite.deeperdarker.DeeperDarker;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

//Based on https://github.com/KyaniteMods/DeeperAndDarker/tree/forge-1.20, Original by kyanite
@SuppressWarnings("NullableProblems")
public class SculkCentipedeServantRenderer extends MobRenderer<SculkCentipedeServant, SculkCentipedeServantModel> {
    public static final ModelLayerLocation MODEL = new ModelLayerLocation(DeeperDarker.rl("sculk_centipede_layer"),
            "main");
    private static final ResourceLocation TEXTURE = DeeperDarker.rl("textures/entity/sculk_centipede.png");

    public SculkCentipedeServantRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new SculkCentipedeServantModel(pContext.bakeLayer(MODEL)), 0.6f);
    }

    @Override
    public ResourceLocation getTextureLocation(SculkCentipedeServant pEntity) {
        return TEXTURE;
    }
}
