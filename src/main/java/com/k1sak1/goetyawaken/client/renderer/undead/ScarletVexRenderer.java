package com.k1sak1.goetyawaken.client.renderer.undead;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.ScarletVexModel;
import com.k1sak1.goetyawaken.common.entities.ally.undead.ScarletVex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class ScarletVexRenderer extends MobRenderer<ScarletVex, ScarletVexModel<ScarletVex>> {
    private static final ResourceLocation VEX_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/undead/scarletvex.png");
    private static final ResourceLocation VEX_CHARGING_LOCATION = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/undead/scarletvex.png");
    private static final ResourceLocation CHAIN_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/undead/scarlet_vex_chain.png");
    private static final RenderType CHAIN_RENDER_TYPE = RenderType.entityCutoutNoCull(CHAIN_TEXTURE);

    public ScarletVexRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ScarletVexModel<>(renderManagerIn.bakeLayer(ScarletVexModel.LAYER_LOCATION)), 0.3F);
    }

    public void render(ScarletVex pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack,
            MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
        LivingEntity target = pEntity.getTargetClient();
        if (target == null) {
            return;
        }

        pMatrixStack.pushPose();
        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 start = new Vec3(
                Mth.lerp(pPartialTicks, pEntity.xo, pEntity.getX()),
                Mth.lerp(pPartialTicks, pEntity.yo, pEntity.getY()) + pEntity.getBbHeight() / 2,
                Mth.lerp(pPartialTicks, pEntity.zo, pEntity.getZ()));
        pMatrixStack.translate(-start.x, -(start.y - pEntity.getBbHeight() / 2), -start.z);
        Vec3 end = new Vec3(
                Mth.lerp(pPartialTicks, target.xo, target.getX()),
                Mth.lerp(pPartialTicks, target.yo, target.getY())
                        + target.getBbHeight() / 2,
                Mth.lerp(pPartialTicks, target.zo, target.getZ()));
        VertexConsumer vertexConsumer = pBuffer.getBuffer(CHAIN_RENDER_TYPE);
        Vec3 offset = end.subtract(start);
        Vec3 sight = camPos.subtract(start).scale(-1);
        Vec3 sideOffset = offset.cross(sight).normalize().scale(0.25);
        float age = pEntity.tickCount + pPartialTicks;
        float uOffset = age * 0.4f;
        PoseStack.Pose pose = pMatrixStack.last();
        vertex(vertexConsumer, pose, start.add(sideOffset), uOffset, 0);
        vertex(vertexConsumer, pose, start.add(sideOffset.scale(-1)), uOffset, 1);
        vertex(vertexConsumer, pose, end.add(sideOffset.scale(-1)),
                (float) (offset.length() * 2) + uOffset, 1);
        vertex(vertexConsumer, pose, end.add(sideOffset), (float) (offset.length() * 2) + uOffset, 0);
        pMatrixStack.popPose();
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, Vec3 vec3, float u, float v) {
        consumer.vertex(pose.pose(), (float) vec3.x(), (float) vec3.y(), (float) vec3.z())
                .color(255, 255, 255, 128)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(ScarletVex entity) {
        return entity.isCharging() ? VEX_CHARGING_LOCATION : VEX_TEXTURE;
    }
}
