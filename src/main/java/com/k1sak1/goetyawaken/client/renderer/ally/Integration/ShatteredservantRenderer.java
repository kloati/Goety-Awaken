package com.k1sak1.goetyawaken.client.renderer.ally.Integration;

import com.k1sak1.goetyawaken.client.model.ally.Integration.ShatteredServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.ShatteredServant;
import com.kyanite.deeperdarker.DeeperDarker;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

//Based on https://github.com/KyaniteMods/DeeperAndDarker/tree/forge-1.20, Original by kyanite
@SuppressWarnings("NullableProblems")
public class ShatteredservantRenderer extends MobRenderer<ShatteredServant, ShatteredServantModel> {
    public static final ModelLayerLocation MODEL = new ModelLayerLocation(DeeperDarker.rl("shattered_layer"),
            "main");
    private static final ResourceLocation TEXTURE = DeeperDarker.rl("textures/entity/shattered.png");

    public ShatteredservantRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new ShatteredServantModel(pContext.bakeLayer(MODEL)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(ShatteredServant pEntity) {
        return TEXTURE;
    }
}
