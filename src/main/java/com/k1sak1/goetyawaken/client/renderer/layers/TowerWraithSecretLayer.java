package com.k1sak1.goetyawaken.client.renderer.layers;

import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.client.model.undead.TowerWraithModel;
import com.k1sak1.goetyawaken.common.entities.ally.undead.tower_wraith.AbstractTowerWraith;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class TowerWraithSecretLayer extends RenderLayer<AbstractTowerWraith, TowerWraithModel<AbstractTowerWraith>> {
    private static final ResourceLocation TEXTURES = new ResourceLocation("goety",
            "textures/entity/wraith/wraith_secret.png");
    private final TowerWraithModel<AbstractTowerWraith> layerModel;

    public TowerWraithSecretLayer(
            RenderLayerParent<AbstractTowerWraith, TowerWraithModel<AbstractTowerWraith>> p_i50919_1_,
            EntityModelSet p_174555_) {
        super(p_i50919_1_);
        this.layerModel = new TowerWraithModel<>(p_174555_.bakeLayer(ClientEventHandler.TOWER_WRAITH_LAYER));
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn,
            AbstractTowerWraith entitylivingbaseIn, float limbSwing, float limbSwingAmount,
            float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entitylivingbaseIn.isInterested()) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, TEXTURES,
                    matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn,
                    limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch,
                    partialTicks, 1.0F, 1.0F, 1.0F);
        }
    }
}
