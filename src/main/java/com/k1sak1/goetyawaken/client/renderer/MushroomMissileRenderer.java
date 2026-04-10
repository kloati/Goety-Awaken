package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.MushroomMissileModel;
import com.k1sak1.goetyawaken.common.entities.projectiles.MushroomMissile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class MushroomMissileRenderer extends EntityRenderer<MushroomMissile> {
        private static final ResourceLocation TRAIL = GoetyAwaken.location("textures/entity/projectiles/trail.png");
        private final MushroomMissileModel<MushroomMissile> model;

        public MushroomMissileRenderer(EntityRendererProvider.Context context) {
                super(context);
                this.model = new MushroomMissileModel<>(context.bakeLayer(MushroomMissileModel.LAYER_LOCATION));
        }

        @Override
        public void render(MushroomMissile entity, float entityYaw, float partialTicks, PoseStack poseStack,
                        MultiBufferSource buffer, int packedLight) {
                poseStack.pushPose();
                poseStack.scale(-1.0F, -1.0F, 1.0F);
                poseStack.translate(0.0D, 0.25D, 0.0D);
                float f = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());
                float f1 = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
                VertexConsumer vertexconsumer = buffer
                                .getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
                this.model.setupAnim(entity, 0.0F, 0.0F, 0.0F, f, f1);
                this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F,
                                1.0F,
                                1.0F);
                poseStack.popPose();
                if (entity.hasTrail()) {
                        double x = Mth.lerp(partialTicks, entity.xOld, entity.getX());
                        double y = Mth.lerp(partialTicks, entity.yOld, entity.getY());
                        double z = Mth.lerp(partialTicks, entity.zOld, entity.getZ());
                        poseStack.pushPose();
                        poseStack.translate(-x, -y, -z);
                        renderTrail(entity, partialTicks, poseStack, buffer, 1.0F, 0.0F, 0.0F, 0.6F, packedLight);
                        poseStack.popPose();
                }
                super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }

        @Override
        public ResourceLocation getTextureLocation(MushroomMissile entity) {
                return new ResourceLocation("goetyawaken:textures/entity/projectiles/mushroom_missile.png");
        }

        private void renderTrail(MushroomMissile entityIn, float partialTicks, PoseStack matrixStackIn,
                        MultiBufferSource bufferIn, float red, float green, float blue, float alpha,
                        int packedLightIn) {
                int samples = 0;
                int sampleSize = 1;
                double trailHeight = 0.25D;
                float trailZRot = 0;
                Vec3 topAngleVec = new Vec3(0.0D, trailHeight, 0.0D).zRot(trailZRot);
                Vec3 bottomAngleVec = new Vec3(0.0D, -trailHeight, 0.0D).zRot(trailZRot);
                Vec3 drawFrom = entityIn.getTrailPosition(0, partialTicks);
                VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.entityTranslucent(TRAIL));
                while (samples < sampleSize) {
                        Vec3 sample = entityIn.getTrailPosition(samples + 8, partialTicks);
                        Vec3 draw1 = drawFrom;

                        PoseStack.Pose posestack$pose = matrixStackIn.last();
                        Matrix4f matrix4f = posestack$pose.pose();
                        Matrix3f matrix3f = posestack$pose.normal();
                        float f2 = entityIn.tickCount % 8 / 8.0F;
                        float f3 = f2 + 0.5F;
                        vertexconsumer
                                        .vertex(matrix4f, (float) draw1.x + (float) bottomAngleVec.x,
                                                        (float) draw1.y + (float) bottomAngleVec.y,
                                                        (float) draw1.z + (float) bottomAngleVec.z)
                                        .color(red, green, blue, alpha).uv(f2, 1.0F)
                                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                                        .uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
                        vertexconsumer
                                        .vertex(matrix4f, (float) sample.x + (float) bottomAngleVec.x,
                                                        (float) sample.y + (float) bottomAngleVec.y,
                                                        (float) sample.z + (float) bottomAngleVec.z)
                                        .color(red, green, blue, alpha).uv(f3, 1.0F)
                                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                                        .uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
                        vertexconsumer
                                        .vertex(matrix4f, (float) sample.x + (float) topAngleVec.x,
                                                        (float) sample.y + (float) topAngleVec.y,
                                                        (float) sample.z + (float) topAngleVec.z)
                                        .color(red, green, blue, alpha).uv(f3, 0.0F)
                                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                                        .uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
                        vertexconsumer
                                        .vertex(matrix4f, (float) draw1.x + (float) topAngleVec.x,
                                                        (float) draw1.y + (float) topAngleVec.y,
                                                        (float) draw1.z + (float) topAngleVec.z)
                                        .color(red, green, blue, alpha).uv(f2, 0.0F)
                                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                                        .uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
                        samples++;
                        drawFrom = sample;
                }
        }
}