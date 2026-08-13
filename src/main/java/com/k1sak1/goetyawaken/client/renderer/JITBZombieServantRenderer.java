package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.client.model.JITBZombieServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.JITBZombieServant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JITBZombieServantRenderer extends MobRenderer<JITBZombieServant, JITBZombieServantModel> {
    private static final ResourceLocation JITB_ZOMBIE_SERVANT_TEXTURE = new ResourceLocation(
            GoetyAwaken.MODID, "textures/entity/undead/zombie/jack_in_the_box_zombie.png");

    public JITBZombieServantRenderer(EntityRendererProvider.Context context) {
        super(context, new JITBZombieServantModel(context.bakeLayer(ClientEventHandler.JITB_ZOMBIE_SERVANT_LAYER)),
                0.5F);
        this.addLayer(new PoweredLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(JITBZombieServant entity) {
        return JITB_ZOMBIE_SERVANT_TEXTURE;
    }

    private class PoweredLayer extends RenderLayer<JITBZombieServant, JITBZombieServantModel> {
        private static final ResourceLocation POWERED_TEXTURE = new ResourceLocation(
                "textures/entity/creeper/creeper_armor.png");

        public PoweredLayer(JITBZombieServantRenderer renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                JITBZombieServant entity, float limbSwing, float limbSwingAmount,
                float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entity.isPowered()) {
                float f = (float) entity.tickCount + partialTick;
                VertexConsumer vertexconsumer = bufferSource
                        .getBuffer(RenderType.energySwirl(POWERED_TEXTURE, f * 0.01F, f * 0.01F));
                this.getParentModel().prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
                this.getParentModel().setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
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
