package com.k1sak1.goetyawaken.client.model;

import com.k1sak1.goetyawaken.common.entities.ally.CreeperServant;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreeperServantModel extends CreeperModel<CreeperServant> {
    public CreeperServantModel(ModelPart pRoot) {
        super(pRoot);
    }

    public static LayerDefinition createBodyLayer() {
        return CreeperModel.createBodyLayer(CubeDeformation.NONE);
    }
}