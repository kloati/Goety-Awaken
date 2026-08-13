package com.k1sak1.goetyawaken.client.renderer;

import net.minecraft.world.phys.AABB;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.ToxifinModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import com.k1sak1.goetyawaken.common.entities.ally.ToxifinServant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import org.joml.Matrix4f;

public class ToxifinServantRenderer extends MobRenderer<ToxifinServant, ToxifinModel> {
      private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
                  "textures/entity/toxifin.png");

      private static final ResourceLocation HOSTILE_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
                  "textures/entity/toxifin_origin.png");

      public static final ResourceLocation TOXIFIN_BEAM_LOCATION = new ResourceLocation(GoetyAwaken.MODID,
                  "textures/entity/toxifin_beam.png");
      private static final RenderType LASER_BEAM_RENDER_TYPE;

      public ToxifinServantRenderer(EntityRendererProvider.Context context, ModelLayerLocation modelLayerLocation) {
            this(context, 0.5F, modelLayerLocation);
      }

      protected ToxifinServantRenderer(EntityRendererProvider.Context context, float f,
                  ModelLayerLocation modelLayerLocation) {
            super(context, new ToxifinModel(context.bakeLayer(modelLayerLocation)), f);
      }

      public boolean shouldRender(ToxifinServant entity, Frustum frustum, double camX, double camY, double camZ) {
            if (super.shouldRender(entity, frustum, camX, camY, camZ)) {
                  return true;
            } else {
                  LivingEntity target;
                  if (entity.hasActiveAttackTarget() && (target = entity.getActiveAttackTarget()) != null) {
                        Vec3 targetPos = this.lerpEntityPos(target, (double) target.getBbHeight() * (double) 0.5F,
                                    1.0F);
                        Vec3 selfPos = this.lerpEntityPos(entity, (double) entity.getEyeHeight(), 1.0F);
                        return frustum.isVisible(
                                    new AABB(selfPos.x, selfPos.y, selfPos.z, targetPos.x, targetPos.y, targetPos.z));
                  } else {
                        return false;
                  }
            }
      }

      private Vec3 lerpEntityPos(LivingEntity entity, double yOffset, float partialTicks) {
            double lerpedX = Mth.lerp((double) partialTicks, entity.xOld, entity.getX());
            double lerpedY = Mth.lerp((double) partialTicks, entity.yOld, entity.getY()) + yOffset;
            double lerpedZ = Mth.lerp((double) partialTicks, entity.zOld, entity.getZ());
            return new Vec3(lerpedX, lerpedY, lerpedZ);
      }

      public void render(ToxifinServant entity, float entityYaw, float partialTicks, PoseStack poseStack,
                  MultiBufferSource bufferSource,
                  int packedLight) {
            super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
            LivingEntity beamTarget = entity.getActiveAttackTarget();
            boolean isBeamStyle = true;
            if (beamTarget != null) {
                  float beamProgress = entity.getAttackAnimationScale(partialTicks);
                  float rawTime = entity.getClientSideAttackTime() + partialTicks;
                  float uvScroll = rawTime * (isBeamStyle ? 0.15F : 0.5F) % 1.0F;
                  float eyeOffset = entity.getEyeHeight();
                  poseStack.pushPose();
                  poseStack.translate(0.0F, eyeOffset, 0.0F);
                  Vec3 targetPos = this.lerpEntityPos(beamTarget, (double) beamTarget.getBbHeight() * (double) 0.5F,
                              partialTicks);
                  Vec3 selfPos = this.lerpEntityPos(entity, (double) eyeOffset, partialTicks);
                  Vec3 toTarget = targetPos.subtract(selfPos);
                  float beamLength = (float) (toTarget.length() + (isBeamStyle ? 0.1 : (double) 1.0F));
                  toTarget = toTarget.normalize();
                  float pitchAngle = (float) Math.acos(toTarget.y);
                  float yawAngle = (float) Math.atan2(toTarget.z, toTarget.x);
                  poseStack.mulPose(
                              Axis.YP.rotationDegrees((((float) Math.PI / 2F) - yawAngle) * (180F / (float) Math.PI)));
                  poseStack.mulPose(Axis.XP.rotationDegrees(pitchAngle * (180F / (float) Math.PI)));
                  boolean unusedFlag = true;
                  float spinOffset = rawTime * 0.05F * -1.5F;
                  float fadeFactor = beamProgress * beamProgress;
                  int colorBlue;
                  int colorGreen;
                  int colorRed;
                  if (isBeamStyle) {
                        colorRed = 255 - (int) (fadeFactor * 127.0F);
                        colorGreen = 255;
                        colorBlue = 255 - (int) (fadeFactor * 127.0F);
                  } else {
                        colorRed = 64 + (int) (fadeFactor * 191.0F);
                        colorGreen = 32 + (int) (fadeFactor * 191.0F);
                        colorBlue = 128 - (int) (fadeFactor * 64.0F);
                  }

                  float innerRadius = 0.2F;
                  float outerRadius = 0.282F;
                  float coreX0 = Mth.cos(spinOffset + 2.3561945F) * 0.282F;
                  float coreZ0 = Mth.sin(spinOffset + 2.3561945F) * 0.282F;
                  float coreX1 = Mth.cos(spinOffset + ((float) Math.PI / 4F)) * 0.282F;
                  float coreZ1 = Mth.sin(spinOffset + ((float) Math.PI / 4F)) * 0.282F;
                  float coreX2 = Mth.cos(spinOffset + 3.926991F) * 0.282F;
                  float coreZ2 = Mth.sin(spinOffset + 3.926991F) * 0.282F;
                  float coreX3 = Mth.cos(spinOffset + 5.4977875F) * 0.282F;
                  float coreZ3 = Mth.sin(spinOffset + 5.4977875F) * 0.282F;
                  float outNegX = Mth.cos(spinOffset + (float) Math.PI) * 0.2F;
                  float outNegZ = Mth.sin(spinOffset + (float) Math.PI) * 0.2F;
                  float outPosX = Mth.cos(spinOffset + 0.0F) * 0.2F;
                  float outPosZ = Mth.sin(spinOffset + 0.0F) * 0.2F;
                  float outLeftX = Mth.cos(spinOffset + ((float) Math.PI / 2F)) * 0.2F;
                  float outLeftZ = Mth.sin(spinOffset + ((float) Math.PI / 2F)) * 0.2F;
                  float outRightX = Mth.cos(spinOffset + ((float) Math.PI * 1.5F)) * 0.2F;
                  float outRightZ = Mth.sin(spinOffset + ((float) Math.PI * 1.5F)) * 0.2F;
                  float beamUBase = 0.0F;
                  float beamUEnd = 0.4999F;
                  float uvStart = -1.0F + uvScroll;
                  float uvEnd = beamLength * 2.5F + uvStart;
                  VertexConsumer builder = bufferSource.getBuffer(LASER_BEAM_RENDER_TYPE);
                  PoseStack.Pose poseEntry = poseStack.last();
                  Matrix4f poseMatrix = poseEntry.pose();
                  Matrix3f normalMatrix = poseEntry.normal();
                  vertex(builder, poseMatrix, normalMatrix, outNegX, beamLength, outNegZ, colorRed, colorGreen,
                              colorBlue,
                              0.4999F, uvEnd);
                  vertex(builder, poseMatrix, normalMatrix, outNegX, 0.0F, outNegZ, colorRed, colorGreen, colorBlue,
                              0.4999F,
                              uvStart);
                  vertex(builder, poseMatrix, normalMatrix, outPosX, 0.0F, outPosZ, colorRed, colorGreen, colorBlue,
                              0.0F,
                              uvStart);
                  vertex(builder, poseMatrix, normalMatrix, outPosX, beamLength, outPosZ, colorRed, colorGreen,
                              colorBlue, 0.0F,
                              uvEnd);
                  vertex(builder, poseMatrix, normalMatrix, outLeftX, beamLength, outLeftZ, colorRed, colorGreen,
                              colorBlue,
                              0.4999F, uvEnd);
                  vertex(builder, poseMatrix, normalMatrix, outLeftX, 0.0F, outLeftZ, colorRed, colorGreen, colorBlue,
                              0.4999F,
                              uvStart);
                  vertex(builder, poseMatrix, normalMatrix, outRightX, 0.0F, outRightZ, colorRed, colorGreen, colorBlue,
                              0.0F,
                              uvStart);
                  vertex(builder, poseMatrix, normalMatrix, outRightX, beamLength, outRightZ, colorRed, colorGreen,
                              colorBlue,
                              0.0F, uvEnd);
                  float innerVOffset = 0.0F;
                  if (!isBeamStyle && entity.tickCount % 2 == 0) {
                        innerVOffset = 0.5F;
                  }

                  vertex(builder, poseMatrix, normalMatrix, coreX0, beamLength, coreZ0, colorRed, colorGreen, colorBlue,
                              0.5F,
                              innerVOffset + 0.5F);
                  vertex(builder, poseMatrix, normalMatrix, coreX1, beamLength, coreZ1, colorRed, colorGreen, colorBlue,
                              1.0F,
                              innerVOffset + 0.5F);
                  vertex(builder, poseMatrix, normalMatrix, coreX3, beamLength, coreZ3, colorRed, colorGreen, colorBlue,
                              1.0F,
                              innerVOffset);
                  vertex(builder, poseMatrix, normalMatrix, coreX2, beamLength, coreZ2, colorRed, colorGreen, colorBlue,
                              0.5F,
                              innerVOffset);
                  poseStack.popPose();
            }

      }

      private static void vertex(VertexConsumer builder, Matrix4f poseMat, Matrix3f normMat, float vx, float vy,
                  float vz,
                  int r, int g, int b, float u, float v) {
            builder.vertex(poseMat, vx, vy, vz).color(r, g, b, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880).normal(normMat, 0.0F, 1.0F, 0.0F).endVertex();
      }

      public ResourceLocation getTextureLocation(ToxifinServant entity) {
            if (entity.isHostile()) {
                  return HOSTILE_TEXTURE;
            } else {
                  return TEXTURE;
            }
      }

      static {
            LASER_BEAM_RENDER_TYPE = RenderType.entityCutoutNoCull(TOXIFIN_BEAM_LOCATION);
      }
}
