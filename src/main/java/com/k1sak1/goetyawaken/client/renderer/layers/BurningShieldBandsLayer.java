package com.k1sak1.goetyawaken.client.renderer.layers;

import com.Polarice3.Goety.Goety;
import com.k1sak1.goetyawaken.client.model.BurningShieldModel;
import com.k1sak1.goetyawaken.common.entities.ally.BurningShield;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class BurningShieldBandsLayer extends RenderLayer<BurningShield, BurningShieldModel<BurningShield>> {

    private static final ResourceLocation TEXTURE = Goety
            .location("textures/entity/servants/blaze/wildfire_servant_bands.png");
    private final BurningShieldModel<BurningShield> layerModel;

    public BurningShieldBandsLayer(RenderLayerParent<BurningShield, BurningShieldModel<BurningShield>> parent,
            EntityModelSet entityModelSet) {
        super(parent);
        this.layerModel = new BurningShieldModel<>(entityModelSet.bakeLayer(BurningShieldModel.LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, BurningShield entity,
            float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
            float headPitch) {
        if (!entity.isInvisible()) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, TEXTURE, poseStack, buffer,
                    packedLight, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch,
                    partialTicks, 1.0F, 1.0F, 1.0F);
        }
    }
}
