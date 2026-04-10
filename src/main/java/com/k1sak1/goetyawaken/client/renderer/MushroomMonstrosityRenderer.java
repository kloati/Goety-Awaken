package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.client.model.MushroomMonstrosityModel;
import com.k1sak1.goetyawaken.common.entities.ally.golem.MushroomMonstrosity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import javax.annotation.Nullable;

public class MushroomMonstrosityRenderer
        extends MobRenderer<MushroomMonstrosity, MushroomMonstrosityModel<MushroomMonstrosity>> {

    private static final ResourceLocation FRIENDLY_TEXTURES = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/servants/mushroom_monstrosity/mushroom_monstrosity.png");
    private static final ResourceLocation HOSTILE_TEXTURES = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/servants/mushroom_monstrosity/mooshroom_monstrosity_hostile.png");
    private static final ResourceLocation EYES = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/servants/mushroom_monstrosity/mushroom_monstrosity_eyes.png");
    private static final ResourceLocation GLOW = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/servants/mushroom_monstrosity/mushroom_monstrosity_glow.png");
    private static final ResourceLocation GLOW2 = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/servants/mushroom_monstrosity/mushroom_monstrosity_glow2.png");
    private static final ResourceLocation GLOW3 = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/servants/mushroom_monstrosity/mushroom_monstrosity_glow3.png");
    private static final ResourceLocation DEATH = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/servants/mushroom_monstrosity/mushroom_monstrosity_death.png");

    public MushroomMonstrosityRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn,
                new MushroomMonstrosityModel<>(
                        renderManagerIn.bakeLayer(ClientEventHandler.MUSHROOM_MONSTROSITY_LAYER)),
                2.0F);
        this.addLayer(new GlowEyesLayer(this));
        this.addLayer(new RMEmissiveLayer(this, GLOW, (entity, partialTicks, ageInTicks) -> {
            return !entity.isDeadOrDying()
                    && entity.getBigGlow() > 0 ? entity.getBigGlow() : 0.0F;
        }));
        this.addLayer(new RMEmissiveLayer(this, GLOW2, (entity, partialTicks, ageInTicks) -> {
            return !entity.isDeadOrDying()
                    && entity.getMinorGlow() > 0 ? entity.getMinorGlow() : 0.0F;
        }));
        this.addLayer(new RMEmissiveLayer(this, GLOW3, (entity, partialTicks, ageInTicks) -> {
            return !entity.isDeadOrDying()
                    && entity.getBigGlow() > 0 ? entity.getBigGlow() : 0.0F;
        }));
        this.addLayer(new RMEmissiveLayer(this, DEATH, (entity, partialTicks, ageInTicks) -> {
            return entity.getDeathTime() >= 24 ? 1.0F : 0.0F;
        }));
    }

    @Override
    public void render(MushroomMonstrosity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack,
            MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
        if (pEntity.getDeathTime() >= 24) {
            pMatrixStack.pushPose();
            boolean flag = pEntity.hurtTime > 0;
            float f = Mth.rotLerp(pPartialTicks, pEntity.yBodyRotO, pEntity.yBodyRot);
            float f1 = Mth.rotLerp(pPartialTicks, pEntity.yHeadRotO, pEntity.yHeadRot);
            float f2 = f1 - f;
            float f6 = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());
            float f71 = this.getBob(pEntity, pPartialTicks);
            this.setupRotations(pEntity, pMatrixStack, f71, f, pPartialTicks);
            pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
            this.scale(pEntity, pMatrixStack, pPartialTicks);
            pMatrixStack.translate(0.0D, (double) -1.501F, 0.0D);
            this.model.prepareMobModel(pEntity, 0.0F, 0.0F, pPartialTicks);
            this.model.setupAnim(pEntity, 0.0F, 0.0F, f71, f2, f6);
            float f10 = Mth.clamp(1.0F - (((pEntity.getDeathTime() - 24) * 2) / 100.0F), 0.0F, 1.0F);
            VertexConsumer ivertexbuilder1 = pBuffer
                    .getBuffer(RenderType.entityDecal(this.getTextureLocation(pEntity)));
            this.model.renderToBuffer(pMatrixStack, ivertexbuilder1, pPackedLight, OverlayTexture.pack(0.0F, flag), f10,
                    f10, f10, 1.0F);
            pMatrixStack.popPose();
        }
    }

    @Nullable
    protected RenderType getRenderType(MushroomMonstrosity p_230496_1_, boolean p_230496_2_, boolean p_230496_3_,
            boolean p_230496_4_) {
        ResourceLocation texture = p_230496_1_ instanceof com.k1sak1.goetyawaken.common.entities.hostile.MushroomMonstrosityHostile
                ? HOSTILE_TEXTURES
                : FRIENDLY_TEXTURES;
        if (p_230496_1_.getDeathTime() >= 24) {
            return RenderType.dragonExplosionAlpha(texture);
        } else {
            return super.getRenderType(p_230496_1_, p_230496_2_, p_230496_3_, p_230496_4_);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(MushroomMonstrosity entity) {
        if (entity instanceof com.k1sak1.goetyawaken.common.entities.hostile.MushroomMonstrosityHostile) {
            return HOSTILE_TEXTURES;
        } else {
            return FRIENDLY_TEXTURES;
        }
    }

    public static class GlowEyesLayer
            extends EyesLayer<MushroomMonstrosity, MushroomMonstrosityModel<MushroomMonstrosity>> {
        private static final ResourceLocation EYES = new ResourceLocation(GoetyAwaken.MODID,
                "textures/entity/servants/mushroom_monstrosity/mushroom_monstrosity_eyes.png");

        public GlowEyesLayer(
                RenderLayerParent<MushroomMonstrosity, MushroomMonstrosityModel<MushroomMonstrosity>> p_116981_) {
            super(p_116981_);
        }

        @Override
        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_,
                MushroomMonstrosity p_116986_, float p_116987_, float p_116988_, float p_116989_, float p_116990_,
                float p_116991_, float p_116992_) {
            super.render(p_116983_, p_116984_, p_116985_, p_116986_, p_116987_, p_116988_, p_116989_, p_116990_,
                    p_116991_, p_116992_);
        }

        @Override
        public RenderType renderType() {
            return RenderType.eyes(EYES);
        }
    }

    public static class RMEmissiveLayer
            extends RenderLayer<MushroomMonstrosity, MushroomMonstrosityModel<MushroomMonstrosity>> {
        private final ResourceLocation texture;
        private final AlphaFunction alphaFunction;

        public RMEmissiveLayer(
                RenderLayerParent<MushroomMonstrosity, MushroomMonstrosityModel<MushroomMonstrosity>> p_234885_,
                ResourceLocation p_234886_, AlphaFunction p_234887_) {
            super(p_234885_);
            this.texture = p_234886_;
            this.alphaFunction = p_234887_;
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn,
                MushroomMonstrosity entity, float limbSwing, float limbSwingAmount, float partialTicks,
                float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entity.isInvisible()) {
                VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.entityTranslucentEmissive(this.texture));
                this.getParentModel().renderToBuffer(matrixStackIn, vertexconsumer, packedLightIn,
                        LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F,
                        this.alphaFunction.apply(entity, partialTicks, ageInTicks));
            }
        }

        public interface AlphaFunction {
            float apply(MushroomMonstrosity p_234920_, float p_234921_, float p_234922_);
        }
    }
}