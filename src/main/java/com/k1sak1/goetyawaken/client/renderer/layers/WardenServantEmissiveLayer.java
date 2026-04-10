package com.k1sak1.goetyawaken.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import com.k1sak1.goetyawaken.client.model.WardenServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.WardenServant;

@OnlyIn(Dist.CLIENT)
public class WardenServantEmissiveLayer extends RenderLayer<WardenServant, WardenServantModel> {
    private final ResourceLocation texture;
    private final WardenServantEmissiveLayer.AlphaFunction alphaFunction;
    private final WardenServantEmissiveLayer.DrawSelector drawSelector;

    public WardenServantEmissiveLayer(RenderLayerParent<WardenServant, WardenServantModel> pRenderer,
            ResourceLocation pTexture, WardenServantEmissiveLayer.AlphaFunction pAlphaFunction,
            WardenServantEmissiveLayer.DrawSelector pDrawSelector) {
        super(pRenderer);
        this.texture = pTexture;
        this.alphaFunction = pAlphaFunction;
        this.drawSelector = pDrawSelector;
    }

    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, WardenServant pLivingEntity,
            float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw,
            float pHeadPitch) {
        if (!pLivingEntity.isInvisible()) {
            this.onlyDrawSelectedParts();
            VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucentEmissive(this.texture));
            this.getParentModel().renderToBuffer(pPoseStack, vertexconsumer, pPackedLight,
                    LivingEntityRenderer.getOverlayCoords(pLivingEntity, 0.0F), 1.0F, 1.0F, 1.0F,
                    this.alphaFunction.apply(pLivingEntity, pPartialTick, pAgeInTicks));
            this.resetDrawForAllParts();
        }
    }

    private void onlyDrawSelectedParts() {
        List<ModelPart> list = this.drawSelector.getPartsToDraw(this.getParentModel());
        this.getParentModel().root().getAllParts().forEach((p_234918_) -> {
            p_234918_.skipDraw = true;
        });
        list.forEach((p_234916_) -> {
            p_234916_.skipDraw = false;
        });
    }

    private void resetDrawForAllParts() {
        this.getParentModel().root().getAllParts().forEach((p_234913_) -> {
            p_234913_.skipDraw = false;
        });
    }

    @OnlyIn(Dist.CLIENT)
    public interface AlphaFunction {
        float apply(WardenServant pLivingEntity, float pPartialTick, float pAgeInTicks);
    }

    @OnlyIn(Dist.CLIENT)
    public interface DrawSelector {
        List<ModelPart> getPartsToDraw(WardenServantModel pParentModel);
    }
}