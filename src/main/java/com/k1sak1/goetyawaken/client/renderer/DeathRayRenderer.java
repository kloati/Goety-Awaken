package com.k1sak1.goetyawaken.client.renderer;

import org.jetbrains.annotations.NotNull;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.entities.projectiles.DeathRay;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DeathRayRenderer extends EntityRenderer<DeathRay> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(
            GoetyAwaken.location("doom_beam_visual"), "primary");

    private final ModelPart beamCore;

    private static final ResourceLocation CORE_TEXTURE_RESOURCE = GoetyAwaken
            .location("textures/entity/projectiles/core.png");
    private static final ResourceLocation OVERLAY_TEXTURE_RESOURCE = GoetyAwaken
            .location("textures/entity/projectiles/overlay.png");

    private static final float BEAM_SCALE_FACTOR = 0.25F;
    private static final float BASE_SEGMENT_LENGTH = 32.0F;
    private static final float OVERLAY_ROTATION_SPEED = 5.0F;
    private static final float CORE_ROTATION_SPEED = -10.0F;
    private static final float INITIAL_OVERLAY_EXPANSION = 1.2F;
    private static final float INITIAL_CORE_EXPANSION = 1.0F;
    private static final int FADE_OUT_OFFSET_TICKS = 5;

    public DeathRayRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
        ModelPart modelPart = renderManagerIn.bakeLayer(MODEL_LAYER_LOCATION);
        this.beamCore = modelPart.getChild("beam_body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("beam_body",
                CubeListBuilder.create().texOffs(0, 0).addBox(-6, -14, -6, 12, 28, 12),
                PartPose.ZERO);
        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    @Override
    public boolean shouldRender(DeathRay entity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public void render(DeathRay entity, float yaw, float partialTicks, PoseStack poseStack,
            MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();

        float entityLifetime = DeathRay.LIFETIME;
        float segmentLength = BASE_SEGMENT_LENGTH * BEAM_SCALE_FACTOR * BEAM_SCALE_FACTOR;
        float currentTick = (float) entity.tickCount + partialTicks;

        poseStack.translate(0.0, entity.getBoundingBox().getYsize() * 0.5, 0.0);
        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot() - 180.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(-entity.getXRot() - 90.0F));
        poseStack.scale(BEAM_SCALE_FACTOR, BEAM_SCALE_FACTOR, BEAM_SCALE_FACTOR);

        float alphaFade = calculateAlphaFade(currentTick, entityLifetime);
        float totalSegments = entity.distance * 4.0F;

        for (float segmentIndex = 0.0F; segmentIndex < totalSegments; segmentIndex += segmentLength) {
            poseStack.translate(0.0F, segmentLength, 0.0F);

            renderOverlayLayer(poseStack, bufferSource, currentTick, entityLifetime, alphaFade);
            renderCoreLayer(poseStack, bufferSource, currentTick, entityLifetime);
        }

        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    private float calculateAlphaFade(float currentTick, float lifetime) {
        return Mth.clamp(1.0F - currentTick / lifetime, 0.0F, 1.0F);
    }

    private void renderOverlayLayer(PoseStack poseStack, MultiBufferSource bufferSource,
            float currentTick, float lifetime, float alpha) {
        VertexConsumer vertexBuffer = bufferSource
                .getBuffer(MagicRenderType.energySwirlNoCull(OVERLAY_TEXTURE_RESOURCE));

        poseStack.pushPose();
        float expansionFactor = Mth.clampedLerp(INITIAL_OVERLAY_EXPANSION, 0.0F, currentTick / lifetime);

        poseStack.mulPose(Axis.YP.rotationDegrees(currentTick * OVERLAY_ROTATION_SPEED));
        poseStack.scale(expansionFactor, 1.0F, expansionFactor);
        poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));

        this.beamCore.render(poseStack, vertexBuffer, 15728880, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, alpha);
        poseStack.popPose();
    }

    private void renderCoreLayer(PoseStack poseStack, MultiBufferSource bufferSource,
            float currentTick, float lifetime) {
        VertexConsumer vertexBuffer = bufferSource
                .getBuffer(DecayRenderType.deterioratingEffect(CORE_TEXTURE_RESOURCE));

        poseStack.pushPose();
        float alphaFade = calculateAlphaFade(currentTick, lifetime);
        float expansionFactor = Mth.clampedLerp(INITIAL_CORE_EXPANSION, 0.0F, currentTick / lifetime);

        poseStack.scale(expansionFactor, 1.0F, expansionFactor);
        poseStack.mulPose(Axis.YP.rotationDegrees(currentTick * CORE_ROTATION_SPEED));

        this.beamCore.render(poseStack, vertexBuffer, 15728880, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, alphaFade);
        poseStack.popPose();
    }

    public static class DecayRenderType extends RenderType {

        private static final RenderStateShard.TransparencyStateShard INVERSE_ALPHA_BLEND = new RenderStateShard.TransparencyStateShard(
                "inverse_alpha_blend", () -> {
                    RenderSystem.enableBlend();
                    RenderSystem.blendFuncSeparate(
                            GlStateManager.SourceFactor.ONE_MINUS_SRC_ALPHA,
                            GlStateManager.DestFactor.SRC_COLOR,
                            GlStateManager.SourceFactor.ONE,
                            GlStateManager.DestFactor.ZERO);
                }, () -> {
                    RenderSystem.disableBlend();
                    RenderSystem.defaultBlendFunc();
                });

        public DecayRenderType(String name, VertexFormat format, VertexFormat.Mode mode,
                int bufferSize, boolean affectsCrumbling, boolean sortOnUpload,
                Runnable setupState, Runnable clearState) {
            super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
        }

        public static @NotNull RenderType deterioratingEffect(@NotNull ResourceLocation texture) {
            return create("decay_effect", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true,
                    CompositeState.builder()
                            .setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
                            .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                            .setTransparencyState(INVERSE_ALPHA_BLEND)
                            .setCullState(NO_CULL)
                            .setLightmapState(LIGHTMAP)
                            .setOverlayState(OVERLAY)
                            .createCompositeState(false));
        }
    }

    public static class MagicRenderType extends RenderType {

        public MagicRenderType(String name, VertexFormat format, VertexFormat.Mode mode,
                int bufferSize, boolean affectsCrumbling, boolean sortOnUpload,
                Runnable setupState, Runnable clearState) {
            super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
        }

        public static RenderType energySwirl(@NotNull ResourceLocation texture) {
            return create("energy_flow", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true,
                    CompositeState.builder()
                            .setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
                            .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                            .setTransparencyState(ADDITIVE_TRANSPARENCY)
                            .setCullState(CULL)
                            .setLightmapState(LIGHTMAP)
                            .setOverlayState(OVERLAY)
                            .createCompositeState(false));
        }

        public static RenderType energySwirlNoCull(@NotNull ResourceLocation texture) {
            return create("energy_flow_transparent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256,
                    false, true,
                    CompositeState.builder()
                            .setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
                            .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                            .setTransparencyState(ADDITIVE_TRANSPARENCY)
                            .setCullState(NO_CULL)
                            .setLightmapState(LIGHTMAP)
                            .setOverlayState(OVERLAY)
                            .createCompositeState(false));
        }

        public static RenderType energySwirlWithOffset(@NotNull ResourceLocation texture, float uOffset,
                float vOffset) {
            return create("energy_flow_offset", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false,
                    true,
                    CompositeState.builder()
                            .setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
                            .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                            .setTexturingState(new RenderStateShard.OffsetTexturingStateShard(uOffset, vOffset))
                            .setTransparencyState(ADDITIVE_TRANSPARENCY)
                            .setCullState(CULL)
                            .setLightmapState(LIGHTMAP)
                            .setOverlayState(OVERLAY)
                            .createCompositeState(false));
        }
    }

    @Override
    public ResourceLocation getTextureLocation(DeathRay entity) {
        return CORE_TEXTURE_RESOURCE;
    }
}