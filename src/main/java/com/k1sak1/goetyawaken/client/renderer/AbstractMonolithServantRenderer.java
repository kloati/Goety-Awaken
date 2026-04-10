package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.client.model.ObsidianMonolithServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.ObsidianMonolithServant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.Map;

public abstract class AbstractMonolithServantRenderer<T extends ObsidianMonolithServant> extends EntityRenderer<T> {
    public final ObsidianMonolithServantModel<T> model;

    public AbstractMonolithServantRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ObsidianMonolithServantModel<>(context.bakeLayer(ClientEventHandler.MONOLITH));
    }

    public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack,
            MultiBufferSource pBuffer, int pPackedLight) {
        float f = Math.min(ObsidianMonolithServant.getEmergingTime(), pEntity.getAge());
        pMatrixStack.pushPose();
        pMatrixStack.mulPose(Axis.YP.rotationDegrees(pEntity.getYRot()));
        pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
        pMatrixStack.translate(0.0D, 0.0D, 0.0D);
        pMatrixStack.scale(1.0F, 1.0F, 1.0F);
        this.model.setupAnim(pEntity, f, 0.0F, pPartialTicks, pEntity.getYRot(), pEntity.getXRot());
        VertexConsumer ivertexbuilder = pBuffer.getBuffer(this.model.renderType(getTextureLocation(pEntity)));
        this.model.renderToBuffer(pMatrixStack, ivertexbuilder, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F,
                1.0F, 1.0F);
        if (!pEntity.isEmerging() && !pEntity.isInvisible()) {
            RenderType renderType = getActivatedTextureLocation(pEntity);
            if (renderType != null) {
                VertexConsumer vertexconsumer = pBuffer.getBuffer(renderType);
                this.model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F,
                        1.0F, 1.0F, 1.0F);
            }
            Map<ObsidianMonolithServant.Crackiness, ResourceLocation> locationMap = cracknessLocation();
            if (locationMap != null) {
                ObsidianMonolithServant.Crackiness irongolem$crackiness = pEntity.getCrackiness();
                if (irongolem$crackiness != ObsidianMonolithServant.Crackiness.NONE) {
                    ResourceLocation resourcelocation = locationMap.get(irongolem$crackiness);
                    renderColoredCutoutModel(this.model, resourcelocation, pMatrixStack, pBuffer, pPackedLight, 1.0F,
                            1.0F, 1.0F);
                }
            }
            this.extraLayer(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
        }
        pMatrixStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    protected static <T extends LivingEntity> void renderColoredCutoutModel(EntityModel<T> p_117377_,
            ResourceLocation p_117378_, PoseStack p_117379_, MultiBufferSource p_117380_, int p_117381_,
            float p_117383_, float p_117384_, float p_117385_) {
        VertexConsumer vertexconsumer = p_117380_.getBuffer(RenderType.entityCutoutNoCull(p_117378_));
        p_117377_.renderToBuffer(p_117379_, vertexconsumer, p_117381_, OverlayTexture.NO_OVERLAY, p_117383_, p_117384_,
                p_117385_, 1.0F);
    }

    public void extraLayer(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack,
            MultiBufferSource pBuffer, int pPackedLight) {

    }

    @Nullable
    public abstract RenderType getActivatedTextureLocation(T monolith);

    @Nullable
    public abstract Map<ObsidianMonolithServant.Crackiness, ResourceLocation> cracknessLocation();
}