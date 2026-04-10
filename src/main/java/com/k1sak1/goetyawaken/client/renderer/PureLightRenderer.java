package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.common.entities.projectiles.PureLightEntity;
import com.k1sak1.goetyawaken.utils.RenderHelper;
import com.k1sak1.goetyawaken.utils.Utils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Inspired by irons-spells-n-spellbooks
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/iron431/irons-spells-n-spellbooks">irons-spells-n-spellbooks
 *      Repository</a>
 */

@OnlyIn(Dist.CLIENT)
public class PureLightRenderer extends EntityRenderer<PureLightEntity> {

        public PureLightRenderer(EntityRendererProvider.Context context) {
                super(context);
        }

        @Override
        public boolean shouldRender(PureLightEntity entity, Frustum camera, double camX, double camY, double camZ) {
                return true;
        }

        @Override
        public void render(PureLightEntity entity, float yaw, float partialTicks, PoseStack poseStack,
                        MultiBufferSource bufferSource, int light) {
                poseStack.pushPose();

                float maxRadius = 2.5f;
                float minRadius = 0.005f;
                float deltaTicks = entity.tickCount + partialTicks;
                float deltaUV = -deltaTicks % 10;
                float max = Mth.frac(deltaUV * 0.2F - (float) Mth.floor(deltaUV * 0.1F));
                float min = -1.0F + max;
                float f = deltaTicks / PureLightEntity.WARMUP_TIME;
                f *= f;
                float radius = Mth.clampedLerp(maxRadius, minRadius, f);
                VertexConsumer inner = bufferSource
                                .getBuffer(RenderType.eyes(new ResourceLocation("textures/entity/beacon_beam.png")));

                float halfRadius = radius * .5f;
                float quarterRadius = halfRadius * .5f;
                float yMin = entity.onGround() ? 0
                                : Utils.findRelativeGroundLevel(entity.level(), entity.position(), 8)
                                                - (float) entity.getY();

                for (int i = 0; i < 4; i++) {
                        int baseRed = entity.getRed();
                        int baseGreen = entity.getGreen();
                        int baseBlue = entity.getBlue();
                        int r = (int) (baseRed * Mth.clamp(f, 0, 1));
                        int g = (int) (baseGreen * Mth.clamp(f * f, 0, 1));
                        int b = (int) (baseBlue * Mth.clamp(f * f, 0, 1));
                        int a = 255;

                        var poseMatrix = poseStack.last().pose();
                        var normalMatrix = poseStack.last().normal();

                        inner.vertex(poseMatrix, -halfRadius, yMin, -halfRadius).color(r, g, b, a).uv(0, min)
                                        .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT)
                                        .normal(normalMatrix, 0f, 1f, 0f).endVertex();
                        inner.vertex(poseMatrix, -halfRadius, yMin, halfRadius).color(r, g, b, a).uv(1, min)
                                        .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT)
                                        .normal(normalMatrix, 0f, 1f, 0f).endVertex();
                        inner.vertex(poseMatrix, -halfRadius, 250, halfRadius).color(r, g, b, a).uv(1, max)
                                        .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT)
                                        .normal(normalMatrix, 0f, 1f, 0f).endVertex();
                        inner.vertex(poseMatrix, -halfRadius, 250, -halfRadius).color(r, g, b, a).uv(0, max)
                                        .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT)
                                        .normal(normalMatrix, 0f, 1f, 0f).endVertex();

                        var color = RenderHelper.colorf(
                                        Mth.clamp(baseRed / 255.0f * f, 0, 1),
                                        Mth.clamp(baseGreen / 255.0f * 0.85f * f, 0, 1),
                                        Mth.clamp(baseBlue / 255.0f * 0.7f * f * f, 0, 1));
                        inner.vertex(poseMatrix, -quarterRadius, yMin, -quarterRadius).color(color).uv(0, min)
                                        .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT)
                                        .normal(normalMatrix, 0f, 1f, 0f).endVertex();
                        inner.vertex(poseMatrix, -quarterRadius, yMin, quarterRadius).color(color).uv(1, min)
                                        .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT)
                                        .normal(normalMatrix, 0f, 1f, 0f).endVertex();
                        inner.vertex(poseMatrix, -quarterRadius, 250, quarterRadius).color(color).uv(1, max)
                                        .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT)
                                        .normal(normalMatrix, 0f, 1f, 0f).endVertex();
                        inner.vertex(poseMatrix, -quarterRadius, 250, -quarterRadius).color(color).uv(0, max)
                                        .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT)
                                        .normal(normalMatrix, 0f, 1f, 0f).endVertex();

                        poseStack.mulPose(Axis.YP.rotationDegrees(90));
                }

                poseStack.popPose();

                super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
        }

        @Override
        public ResourceLocation getTextureLocation(PureLightEntity entity) {
                return new ResourceLocation("textures/entity/beacon_beam.png");
        }
}