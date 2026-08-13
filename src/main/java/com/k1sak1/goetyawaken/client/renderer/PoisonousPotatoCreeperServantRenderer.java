package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.PoisonousPotatoCreeperServantModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.common.entities.ally.PoisonousPotatoCreeperServant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PoisonousPotatoCreeperServantRenderer
        extends MobRenderer<PoisonousPotatoCreeperServant, PoisonousPotatoCreeperServantModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/poisonous_potato_creeper.png");
    private static final ResourceLocation POWERED_TEXTURE = new ResourceLocation(
            "textures/entity/creeper/creeper_armor.png");

    public PoisonousPotatoCreeperServantRenderer(EntityRendererProvider.Context context) {
        super(context, new PoisonousPotatoCreeperServantModel(
                context.bakeLayer(ClientEventHandler.POISONOUS_POTATO_CREEPER_SERVANT_LAYER)), 0.5F);
        this.addLayer(new PoweredLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(PoisonousPotatoCreeperServant entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(PoisonousPotatoCreeperServant creeper, PoseStack poseStack, float partialTickTime) {
        float f = creeper.getSwelling(partialTickTime);
        float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
        f = Mth.clamp(f, 0.0F, 1.0F);
        f = f * f;
        f = f * f;
        float f2 = (1.0F + f * 0.4F) * f1;
        float f3 = (1.0F + f * 0.1F) / f1;
        poseStack.scale(f2, f3, f2);
    }

    @Override
    protected float getWhiteOverlayProgress(PoisonousPotatoCreeperServant creeper, float partialTickTime) {
        float f = creeper.getSwelling(partialTickTime);
        return (int) (f * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

    private class PoweredLayer extends RenderLayer<PoisonousPotatoCreeperServant, PoisonousPotatoCreeperServantModel> {
        public PoweredLayer(PoisonousPotatoCreeperServantRenderer renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                PoisonousPotatoCreeperServant creeper, float limbSwing, float limbSwingAmount,
                float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            if (creeper.isPowered()) {
                float f = (float) creeper.tickCount + partialTick;
                VertexConsumer vertexconsumer = bufferSource
                        .getBuffer(RenderType.energySwirl(POWERED_TEXTURE, f * 0.01F, f * 0.01F));
                this.getParentModel().prepareMobModel(creeper, limbSwing, limbSwingAmount, partialTick);
                this.getParentModel().setupAnim(creeper, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                poseStack.pushPose();
                float scale = 1.25F;
                poseStack.scale(scale, scale, scale);
                this.getParentModel().renderToBuffer(poseStack, vertexconsumer, packedLight,
                        OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 1.0F);
                poseStack.popPose();
            }
        }
    }
}
