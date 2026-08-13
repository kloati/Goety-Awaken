package com.k1sak1.goetyawaken.client.renderer;

import com.Polarice3.Goety.client.render.ModRenderType;
import com.Polarice3.Goety.utils.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Quaternionf;

public class ServantBeamRenderer {
    private static final ResourceLocation JET_INNER = new ResourceLocation("goety", "textures/entity/water_jet/jet_inner.png");
    private static final ResourceLocation JET_OUTER = new ResourceLocation("goety", "textures/entity/water_jet/jet_outer.png");
    private static final float INNER_RADIUS = 0.04F;
    private static final float OUTER_RADIUS = 0.05F;

    public static void renderBeam(RenderLevelStageEvent event, LivingEntity caster, LivingEntity target, String spellName, float r, float g, float b, float a) {
        Vec3 casterPos = caster.getEyePosition(event.getPartialTick());
        Vec3 targetPos = new Vec3(
                Mth.lerp(event.getPartialTick(), target.xo, target.getX()),
                Mth.lerp(event.getPartialTick(), target.yo, target.getY()) + target.getBbHeight() / 2,
                Mth.lerp(event.getPartialTick(), target.zo, target.getZ())
        );

        float yaw = MathHelper.positionToYaw(casterPos, targetPos);
        float pitch = MathHelper.positionToPitch(casterPos, targetPos);
        float length = (float) casterPos.distanceTo(targetPos);

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        poseStack.translate(casterPos.x() - view.x(), casterPos.y() - view.y(), casterPos.z() - view.z());
        poseStack.mulPose(new Quaternionf().rotationX(90 * Mth.DEG_TO_RAD));
        poseStack.mulPose(new Quaternionf().rotationZ((yaw - 90) * Mth.DEG_TO_RAD));
        poseStack.mulPose(new Quaternionf().rotationX(-pitch * Mth.DEG_TO_RAD));
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        float tick = caster.tickCount + event.getPartialTick();
        float innerSpeed = 0.4F;
        float outerSpeed = 0.08F;

        if (spellName != null && spellName.contains("water_jet")) {
            renderLayer(buffer.getBuffer(ModRenderType.magicBeam(JET_INNER)), poseStack, INNER_RADIUS, length, 16.0F, -tick * innerSpeed, 0xFFFFFFFF);
            renderLayer(buffer.getBuffer(ModRenderType.magicBeam(JET_OUTER)), poseStack, OUTER_RADIUS, length, 128.0F, -tick * outerSpeed, 0xFFFFFFFF);
        } else {
            int colorARGB = ((int)(a * 255) << 24) | ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);
            renderLayer(buffer.getBuffer(ModRenderType.magicBeam(JET_INNER)), poseStack, INNER_RADIUS, length, 16.0F, -tick * innerSpeed, colorARGB);
        }

        poseStack.popPose();
        buffer.endBatch();
    }

    private static void renderLayer(VertexConsumer consumer, PoseStack poseStack, float radius, float length, float textureRatio, float textureOffset, int color) {
        PoseStack.Pose pose = poseStack.last();
        float uEnd = length / (radius * textureRatio * 2.0F);

        consumer.vertex(pose.pose(), -radius, 0, -radius).color(color).uv(textureOffset, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), -radius, 0, radius).color(color).uv(textureOffset, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), -radius, length, radius).color(color).uv(uEnd + textureOffset, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), -radius, length, -radius).color(color).uv(uEnd + textureOffset, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();

        consumer.vertex(pose.pose(), -radius, 0, -radius).color(color).uv(textureOffset, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), radius, 0, -radius).color(color).uv(textureOffset, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), radius, length, -radius).color(color).uv(uEnd + textureOffset, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), -radius, length, -radius).color(color).uv(uEnd + textureOffset, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();

        consumer.vertex(pose.pose(), radius, 0, -radius).color(color).uv(textureOffset, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), radius, 0, radius).color(color).uv(textureOffset, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), radius, length, radius).color(color).uv(uEnd + textureOffset, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), radius, length, -radius).color(color).uv(uEnd + textureOffset, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();

        consumer.vertex(pose.pose(), -radius, 0, radius).color(color).uv(textureOffset, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), radius, 0, radius).color(color).uv(textureOffset, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), radius, length, radius).color(color).uv(uEnd + textureOffset, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
        consumer.vertex(pose.pose(), -radius, length, radius).color(color).uv(uEnd + textureOffset, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0, 1, 0).endVertex();
    }
}
