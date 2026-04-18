package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.client.model.VanguardChampionModel;
import com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton.VanguardChampion;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class VanguardChampionRenderer extends MobRenderer<VanguardChampion, VanguardChampionModel<VanguardChampion>> {
        private static final ResourceLocation TEXTURE = new ResourceLocation("goetyawaken",
                        "textures/entity/undead/skeleton/vanguard_champion_servant.png");
        private static final ResourceLocation HOSTILE_TEXTURE = new ResourceLocation("goetyawaken",
                        "textures/entity/undead/skeleton/vanguard_champion.png");
        private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation("goetyawaken",
                        "textures/entity/undead/skeleton/vanguard_champion_glow.png");
        private static final ResourceLocation CHAIN_TEXTURE = new ResourceLocation("goetyawaken",
                        "textures/entity/nameless_chain.png");
        private static final RenderType CHAIN_RENDER_TYPE = RenderType.entityCutoutNoCull(CHAIN_TEXTURE, false);

        public VanguardChampionRenderer(EntityRendererProvider.Context context) {
                super(context, new VanguardChampionModel<>(context.bakeLayer(VanguardChampionModel.LAYER_LOCATION)),
                                0.7F);
                // this.addLayer(new VanguardChampionShieldLayer(this));
                this.addLayer(new CustomHeadLayer<VanguardChampion, VanguardChampionModel<VanguardChampion>>(this,
                                context.getModelSet(), context.getItemInHandRenderer()));
                this.addLayer(new VanguardChampionGlowLayer(this));
        }

        @Override
        public void render(VanguardChampion entity, float entityYaw, float partialTicks, PoseStack matrixStack,
                        MultiBufferSource buffer, int packedLight) {
                this.model.attackTime = this.getAttackAnim(entity, partialTicks);
                super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
                renderChainConnection(entity, partialTicks, matrixStack, buffer, packedLight);
        }

        private void renderChainConnection(VanguardChampion pEntity, float pPartialTicks, PoseStack pMatrixStack,
                        MultiBufferSource pBuffer, int pPackedLight) {
                LivingEntity trueOwner = pEntity.getTrueOwner();
                if (trueOwner != null
                                && trueOwner instanceof com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.AbstractNamelessOne) {
                        pMatrixStack.pushPose();
                        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                        Vec3 start = new Vec3(
                                        Mth.lerp(pPartialTicks, pEntity.xo, pEntity.getX()),
                                        Mth.lerp(pPartialTicks, pEntity.yo, pEntity.getY()) + pEntity.getBbHeight() / 2,
                                        Mth.lerp(pPartialTicks, pEntity.zo, pEntity.getZ()));

                        pMatrixStack.translate(-start.x, -(start.y - pEntity.getBbHeight() / 2), -start.z);
                        Vec3 end = new Vec3(
                                        Mth.lerp(pPartialTicks, trueOwner.xo, trueOwner.getX()),
                                        Mth.lerp(pPartialTicks, trueOwner.yo, trueOwner.getY())
                                                        + trueOwner.getBbHeight() / 2,
                                        Mth.lerp(pPartialTicks, trueOwner.zo, trueOwner.getZ()));
                        VertexConsumer vertexConsumer = pBuffer.getBuffer(CHAIN_RENDER_TYPE);
                        Vec3 offset = end.subtract(start);
                        Vec3 sight = camPos.subtract(start).scale(-1);
                        Vec3 sideOffset = offset.cross(sight).normalize().scale(0.25);
                        float age = pEntity.tickCount + pPartialTicks;
                        float uOffset = -age * 0.4f;
                        PoseStack.Pose pose = pMatrixStack.last();
                        vertex(vertexConsumer, pose, start.add(sideOffset), uOffset, 0);
                        vertex(vertexConsumer, pose, start.add(sideOffset.scale(-1)), uOffset, 1);
                        vertex(vertexConsumer, pose, end.add(sideOffset.scale(-1)),
                                        (float) (offset.length() * 2) + uOffset, 1);
                        vertex(vertexConsumer, pose, end.add(sideOffset), (float) (offset.length() * 2) + uOffset, 0);

                        pMatrixStack.popPose();
                }
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

        protected float getAttackAnim(VanguardChampion entity, float partialTicks) {
                return entity.isMeleeAttacking()
                                ? Mth.clamp(((float) entity.attackTick - partialTicks) / 10.0F, 0.0F, 1.0F)
                                : 0.0F;
        }

        @Override
        public @NotNull ResourceLocation getTextureLocation(@NotNull VanguardChampion entity) {
                if (entity instanceof com.k1sak1.goetyawaken.common.entities.hostile.undead.skeleton.HostileVanguardChampion
                                || entity.isHostile()) {
                        return HOSTILE_TEXTURE;
                }
                return TEXTURE;
        }

        public ResourceLocation getGlowTextureLocation() {
                return GLOW_TEXTURE;
        }

        public static class VanguardChampionGlowLayer
                        extends EyesLayer<VanguardChampion, VanguardChampionModel<VanguardChampion>> {
                private static final RenderType GLOW_RENDER_TYPE = RenderType.eyes(GLOW_TEXTURE);

                public VanguardChampionGlowLayer(
                                RenderLayerParent<VanguardChampion, VanguardChampionModel<VanguardChampion>> renderer) {
                        super(renderer);
                }

                @Override
                public RenderType renderType() {
                        return GLOW_RENDER_TYPE;
                }
        }
}