package com.k1sak1.goetyawaken.client.renderer;

import com.Polarice3.Goety.Goety;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.EnderKeeperServantModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.common.entities.ally.EnderKeeperServant;
import com.Polarice3.Goety.utils.MathHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

public class EnderKeeperServantRenderer
        extends MobRenderer<EnderKeeperServant, EnderKeeperServantModel<EnderKeeperServant>> {
    private static final ResourceLocation ENDER_KEEPER_SERVANT_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/ender_keeper_servant.png");
    protected static final ResourceLocation DEATH = Goety.location("textures/entity/enderling/keeper/keeper_death.png");

    public EnderKeeperServantRenderer(EntityRendererProvider.Context context) {
        super(context, new EnderKeeperServantModel<>(context.bakeLayer(ClientEventHandler.ENDER_KEEPER_SERVANT_LAYER)),
                0.5F);
        this.addLayer(new GlowLayer<>(this));
    }

    @Override
    public void render(EnderKeeperServant entity, float entityYaw, float partialTicks, PoseStack matrixStack,
            MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
        if (entity.deathTime > MathHelper.secondsToTicks(2.5F)) {
            matrixStack.pushPose();
            boolean flag = entity.hurtTime > 0;
            float f = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
            float f1 = Mth.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
            float f2 = f1 - f;
            float f6 = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
            float f71 = this.getBob(entity, partialTicks);
            this.setupRotations(entity, matrixStack, f71, f, partialTicks);
            matrixStack.scale(-1.0F, -1.0F, 1.0F);
            this.scale(entity, matrixStack, partialTicks);
            matrixStack.translate(0.0D, -1.501F, 0.0D);
            this.model.prepareMobModel(entity, 0.0F, 0.0F, partialTicks);
            this.model.setupAnim(entity, 0.0F, 0.0F, f71, f2, f6);
            float f8 = MathHelper.secondsToTicks(5);
            float f9 = (entity.deathTime - MathHelper.secondsToTicks(2.5F)) / f8;
            float f10 = 1.0F - ((entity.deathTime - MathHelper.secondsToTicks(2.5F)) / f8);
            VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.dragonExplosionAlpha(DEATH));
            this.model.renderToBuffer(matrixStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F,
                    1.0F, f9);
            VertexConsumer ivertexbuilder1 = buffer.getBuffer(RenderType.entityDecal(this.getTextureLocation(entity)));
            this.model.renderToBuffer(matrixStack, ivertexbuilder1, packedLight, OverlayTexture.pack(0.0F, flag), f10,
                    f10, f10, 1.0F);
            matrixStack.popPose();
        }
    }

    @Nullable
    protected RenderType getRenderType(EnderKeeperServant entity, boolean p_230496_2_, boolean p_230496_3_,
            boolean p_230496_4_) {
        if (entity.deathTime > MathHelper.secondsToTicks(2.5F)) {
            return RenderType.dragonExplosionAlpha(DEATH);
        } else {
            return super.getRenderType(entity, p_230496_2_, p_230496_3_, p_230496_4_);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(EnderKeeperServant entity) {
        return ENDER_KEEPER_SERVANT_TEXTURE;
    }

    public static class GlowLayer<T extends EnderKeeperServant, M extends EnderKeeperServantModel<T>>
            extends EyesLayer<T, M> {
        private static final RenderType RENDER_TYPE = RenderType
                .eyes(new ResourceLocation(GoetyAwaken.MODID, "textures/entity/keeper_glow.png"));

        public GlowLayer(RenderLayerParent<T, M> renderer) {
            super(renderer);
        }

        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
                float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
                float headPitch) {
            if (!entity.isInvisible() && !entity.isDeadOrDying()) {
                VertexConsumer vertexconsumer = buffer.getBuffer(this.renderType());
                this.getParentModel().renderToBuffer(poseStack, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY,
                        1.0F, 1.0F, 1.0F, 1.0F);
            }
        }

        @Override
        public RenderType renderType() {
            return RENDER_TYPE;
        }
    }
}