package com.k1sak1.goetyawaken.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.PaleGolemModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.common.entities.ally.PaleGolemServant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;

import java.util.Map;

public class PaleGolemRenderer extends MobRenderer<PaleGolemServant, PaleGolemModel> {
    private static final ResourceLocation PALE_GOLEM_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/pale_golem.png");
    private static final ResourceLocation PALE_GOLEM_EMISSIVE_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/pale_golem_glow.png");
    private static final Map<IronGolem.Crackiness, ResourceLocation> CRACK_TEXTURES = ImmutableMap.of(
            IronGolem.Crackiness.LOW, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_low.png"),
            IronGolem.Crackiness.MEDIUM,
            new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_medium.png"),
            IronGolem.Crackiness.HIGH,
            new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_high.png"));

    public PaleGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new PaleGolemModel(context.bakeLayer(ClientEventHandler.PALE_GOLEM_LAYER)), 0.7F);
        this.addLayer(new PaleGolemEmissiveLayer(this));
        this.addLayer(new PaleGolemCrackinessLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(PaleGolemServant entity) {
        return PALE_GOLEM_TEXTURE;
    }

    class PaleGolemEmissiveLayer extends RenderLayer<PaleGolemServant, PaleGolemModel> {
        private final PaleGolemRenderer renderer;

        public PaleGolemEmissiveLayer(PaleGolemRenderer renderer) {
            super(renderer);
            this.renderer = renderer;
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                PaleGolemServant entity,
                float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
                float headPitch) {
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.eyes(PALE_GOLEM_EMISSIVE_TEXTURE));
            this.getParentModel().renderToBuffer(poseStack, vertexConsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F,
                    1.0F, 1.0F, 0.5F);
        }
    }

    class PaleGolemCrackinessLayer extends RenderLayer<PaleGolemServant, PaleGolemModel> {
        public PaleGolemCrackinessLayer(PaleGolemRenderer renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                PaleGolemServant entity,
                float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
                float headPitch) {
            if (!entity.isInvisible()) {
                IronGolem.Crackiness crack = entity.getCrackiness();
                if (crack != IronGolem.Crackiness.NONE) {
                    ResourceLocation resourcelocation = CRACK_TEXTURES.get(crack);
                    renderColoredCutoutModel(this.getParentModel(), resourcelocation, poseStack, bufferSource,
                            packedLight, entity, 1.0F, 1.0F, 1.0F);
                }
            }
        }
    }
}