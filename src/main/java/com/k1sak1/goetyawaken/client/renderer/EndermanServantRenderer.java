package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.EndermanServantModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.common.entities.ally.EndermanServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.k1sak1.goetyawaken.client.renderer.layers.EndermanServantCarriedBlockLayer;

public class EndermanServantRenderer extends MobRenderer<EndermanServant, EndermanServantModel> {
    private static final ResourceLocation ENDERMAN_SERVANT_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/enderman_servant.png");

    private static final ResourceLocation ENDERMAN_SERVANT_GLOW_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/enderman_servant_glow.png");

    private final RandomSource random = RandomSource.create();

    public EndermanServantRenderer(EntityRendererProvider.Context context) {
        super(context, new EndermanServantModel(context.bakeLayer(ClientEventHandler.ENDERMAN_SERVANT_LAYER)), 0.5F);
        this.addLayer(new EndermanEyesLayer(this));
        this.addLayer(new EndermanServantCarriedBlockLayer(this, context.getBlockRenderDispatcher()));
    }

    @Override
    public ResourceLocation getTextureLocation(EndermanServant entity) {
        return ENDERMAN_SERVANT_TEXTURE;
    }

    @Override
    public void render(EndermanServant entity, float entityYaw, float partialTicks, PoseStack matrixStack,
            MultiBufferSource buffer, int packedLight) {
        this.model.carrying = !entity.getCarriedItem().isEmpty();
        this.model.creepy = entity.isCreepy();
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    protected void setupRotations(EndermanServant entity, PoseStack poseStack, float ageInTicks, float rotationYaw,
            float partialTicks) {
        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTicks);
    }

    @Override
    public Vec3 getRenderOffset(EndermanServant entity, float partialTicks) {
        if (entity.isCreepy()) {
            return new Vec3(this.random.nextGaussian() * 0.02D, 0.0D, this.random.nextGaussian() * 0.02D);
        } else {
            return super.getRenderOffset(entity, partialTicks);
        }
    }

    public static class EndermanEyesLayer extends EyesLayer<EndermanServant, EndermanServantModel> {
        private static final RenderType ENDERMAN_EYES = RenderType
                .eyes(new ResourceLocation(GoetyAwaken.MODID, "textures/entity/enderman_servant_glow.png"));

        public EndermanEyesLayer(RenderLayerParent<EndermanServant, EndermanServantModel> renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, EndermanServant entity,
                float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
                float headPitch) {
            VertexConsumer vertexconsumer = buffer.getBuffer(this.renderType());
            this.getParentModel().renderToBuffer(poseStack, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F,
                    1.0F, 1.0F, 1.0F);
        }

        @Override
        public RenderType renderType() {
            return ENDERMAN_EYES;
        }
    }
}