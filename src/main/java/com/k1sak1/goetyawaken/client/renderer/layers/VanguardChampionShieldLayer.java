package com.k1sak1.goetyawaken.client.renderer.layers;

import com.k1sak1.goetyawaken.client.model.VanguardChampionModel;
import com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton.VanguardChampion;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VanguardChampionShieldLayer
                extends RenderLayer<VanguardChampion, VanguardChampionModel<VanguardChampion>> {
        private static final ResourceLocation SHIELD_OVERLAY_TEXTURE = new ResourceLocation("goetyawaken",
                        "textures/entity/diagonal_line.png");

        public VanguardChampionShieldLayer(
                        RenderLayerParent<VanguardChampion, VanguardChampionModel<VanguardChampion>> renderer) {
                super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, VanguardChampion entity,
                        float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
                        float headPitch) {

                if (entity.hasShield() && entity.getShieldHealth() == 1 && !entity.isInvisible()) {
                        float textureOffset = (ageInTicks * 0.05F) % 1.0F;
                        VertexConsumer vertexConsumer = buffer
                                        .getBuffer(RenderType.entityTranslucent(SHIELD_OVERLAY_TEXTURE));
                        var root = this.getParentModel().root();
                        if (root.hasChild("body") && root.getChild("body").hasChild("left_arm")
                                        && root.getChild("body").getChild("left_arm").hasChild("left_hand")
                                        && root.getChild("body").getChild("left_arm").getChild("left_hand")
                                                        .hasChild("shield")) {
                                var shieldPart = root.getChild("body").getChild("left_arm").getChild("left_hand")
                                                .getChild("shield");

                                poseStack.pushPose();
                                root.translateAndRotate(poseStack);
                                root.getChild("body").translateAndRotate(poseStack);
                                root.getChild("body").getChild("left_arm").translateAndRotate(poseStack);
                                root.getChild("body").getChild("left_arm").getChild("left_hand")
                                                .translateAndRotate(poseStack);

                                shieldPart.translateAndRotate(poseStack);
                                this.renderShieldMainSurface(poseStack, vertexConsumer, packedLight, textureOffset);
                                poseStack.popPose();
                        }
                }
        }

        private void renderShieldMainSurface(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                        float textureOffset) {
                final float minX = -0.40F;
                final float maxX = 0.40F;
                final float minY = -0.38F;
                final float maxY = 0.73F;
                final float zOffset = -0.1F;

                final float u1 = 0.0F;
                final float u2 = 1.0F;
                final float v1 = textureOffset;
                final float v2 = textureOffset + 1.0F;
                this.renderPreciseQuad(poseStack, vertexConsumer, packedLight,
                                minX, minY, maxX, maxY, zOffset,
                                u1, v1, u2, v2);
        }

        private void renderPreciseQuad(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
                        float x1, float y1, float x2, float y2, float z,
                        float u1, float v1, float u2, float v2) {
                vertexConsumer.vertex(poseStack.last().pose(), x1, y1, z)
                                .color(1.0F, 1.0F, 1.0F, 0.7F)
                                .uv(u1, v1)
                                .overlayCoords(OverlayTexture.NO_OVERLAY)
                                .uv2(packedLight)
                                .normal(poseStack.last().normal(), 0.0F, 0.0F, 1.0F)
                                .endVertex();

                vertexConsumer.vertex(poseStack.last().pose(), x1, y2, z)
                                .color(1.0F, 1.0F, 1.0F, 0.7F)
                                .uv(u1, v2)
                                .overlayCoords(OverlayTexture.NO_OVERLAY)
                                .uv2(packedLight)
                                .normal(poseStack.last().normal(), 0.0F, 0.0F, 1.0F)
                                .endVertex();

                vertexConsumer.vertex(poseStack.last().pose(), x2, y2, z)
                                .color(1.0F, 1.0F, 1.0F, 0.7F)
                                .uv(u2, v2)
                                .overlayCoords(OverlayTexture.NO_OVERLAY)
                                .uv2(packedLight)
                                .normal(poseStack.last().normal(), 0.0F, 0.0F, 1.0F)
                                .endVertex();

                vertexConsumer.vertex(poseStack.last().pose(), x2, y1, z)
                                .color(1.0F, 1.0F, 1.0F, 0.7F)
                                .uv(u2, v1)
                                .overlayCoords(OverlayTexture.NO_OVERLAY)
                                .uv2(packedLight)
                                .normal(poseStack.last().normal(), 0.0F, 0.0F, 1.0F)
                                .endVertex();
        }
}