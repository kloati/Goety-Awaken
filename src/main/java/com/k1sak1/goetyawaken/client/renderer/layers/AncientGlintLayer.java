package com.k1sak1.goetyawaken.client.renderer.layers;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.api.IAncientGlint;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class AncientGlintLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
        protected static final RenderStateShard.LightmapStateShard LIGHTMAP = new RenderStateShard.LightmapStateShard(
                        true);
        protected static final RenderStateShard.TransparencyStateShard ADDITIVE_TRANSPARENCY = new RenderStateShard.TransparencyStateShard(
                        "additive_transparency", () -> {
                                RenderSystem.enableBlend();
                                RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
                        }, () -> {
                                RenderSystem.disableBlend();
                                RenderSystem.defaultBlendFunc();
                        });
        protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENERGY_SWIRL_SHADER = new RenderStateShard.ShaderStateShard(
                        GameRenderer::getRendertypeEnergySwirlShader);
        protected static final RenderStateShard.CullStateShard NO_CULL = new RenderStateShard.CullStateShard(false);
        protected static final RenderStateShard.TexturingStateShard ENTITY_GLINT_TEXTURING = new RenderStateShard.TexturingStateShard(
                        "entity_glint_texturing", () -> {
                                setupGlintTexturing(0.16F);
                        }, () -> {
                                RenderSystem.resetTextureMatrix();
                        });
        protected static final RenderStateShard.DepthTestStateShard EQUAL_DEPTH_TEST = new RenderStateShard.DepthTestStateShard(
                        "==", 514);

        protected static final RenderStateShard.WriteMaskStateShard COLOR_WRITE = new RenderStateShard.WriteMaskStateShard(
                        true, false);

        public static final ResourceLocation ANCIENT_GLINT_TEXTURE = GoetyAwaken
                        .location("textures/entity/ancient_glint.png");
        public static final ResourceLocation ENCHANT_GLINT_TEXTURE = GoetyAwaken
                        .location("textures/entity/enchant_glint.png");

        public AncientGlintLayer(RenderLayerParent<T, M> parent) {
                super(parent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity,
                        float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
                        float headPitch) {
                if (!livingEntity.isInvisible() && livingEntity instanceof IAncientGlint glint) {
                        if (glint.hasAncientGlint()) {
                                float intensity = 0.5F;

                                EntityModel<T> entityModel = this.getParentModel();
                                entityModel.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTicks);
                                this.getParentModel().copyPropertiesTo(entityModel);
                                ResourceLocation texture = "enchant".equals(glint.getGlintTextureType())
                                                ? ENCHANT_GLINT_TEXTURE
                                                : ANCIENT_GLINT_TEXTURE;

                                VertexConsumer vertexConsumer = buffer
                                                .getBuffer(ancientGlintSwirl(texture));
                                entityModel.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw,
                                                headPitch);
                                entityModel.renderToBuffer(poseStack, vertexConsumer, packedLight,
                                                OverlayTexture.NO_OVERLAY,
                                                intensity, intensity, intensity, 1.0F);
                        }
                }
        }

        public static RenderType ancientGlintSwirl(ResourceLocation resourceLocation) {
                return RenderType.create("ancient_glint_effect", DefaultVertexFormat.NEW_ENTITY,
                                VertexFormat.Mode.QUADS, 256,
                                false, true,
                                RenderType.CompositeState.builder()
                                                .setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
                                                .setWriteMaskState(COLOR_WRITE)
                                                .setTextureState(new RenderStateShard.TextureStateShard(
                                                                resourceLocation, false, false))
                                                .setTransparencyState(ADDITIVE_TRANSPARENCY)
                                                .setCullState(NO_CULL)
                                                .setDepthTestState(EQUAL_DEPTH_TEST)
                                                .setTexturingState(ENTITY_GLINT_TEXTURING)
                                                .createCompositeState(false));
        }

        private static void setupGlintTexturing(float scale) {
                long i = Util.getMillis() * 8L;
                float f = (float) (i % 110000L) / 110000.0F;
                float f1 = (float) (i % 30000L) / 30000.0F;
                Matrix4f matrix4f = (new Matrix4f()).translation(-f, f1, 0.0F);
                matrix4f.rotateZ(0.17453292F).scale(scale);
                RenderSystem.setTextureMatrix(matrix4f);
        }
}
