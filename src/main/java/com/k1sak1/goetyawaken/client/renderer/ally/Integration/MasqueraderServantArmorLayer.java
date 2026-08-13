package com.k1sak1.goetyawaken.client.renderer.ally.Integration;

import com.k1sak1.goetyawaken.client.model.ally.Integration.MasqueraderServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.MasqueraderServant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//Based on https://github.com/TheDarkPeasant/The-Masquerade, Original by TheDarkPeasant
@OnlyIn(Dist.CLIENT)
public class MasqueraderServantArmorLayer<T extends MasqueraderServant>
        extends EyesLayer<T, MasqueraderServantModel<T>> {
    private static final RenderType LAYER = RenderType.eyes(new ResourceLocation("masquerader_mod",
            "textures/entity/masqueraderservant/masqueraderservant_armor.png"));

    public MasqueraderServantArmorLayer(RenderLayerParent<T, MasqueraderServantModel<T>> p_i226039_1_) {
        super(p_i226039_1_);
    }

    @Override
    public RenderType renderType() {
        return LAYER;
    }

    @Override
    public void render(PoseStack p_225628_1_, MultiBufferSource p_225628_2_, int p_225628_3_, T p_225628_4_,
            float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_,
            float p_225628_10_) {
        if (p_225628_4_ instanceof MasqueraderServant) {

            super.render(p_225628_1_, p_225628_2_, p_225628_3_, p_225628_4_, p_225628_5_, p_225628_6_, p_225628_7_,
                    p_225628_8_, p_225628_9_, p_225628_10_);

        }
    }
}