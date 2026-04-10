package com.k1sak1.goetyawaken.client.renderer.illager;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.client.model.illager.ApostleServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.illager.ApostleServant;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.VillagerArmorModel;
import com.Polarice3.Goety.config.MobsConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.Random;

public class ApostleServantRenderer extends MobRenderer<ApostleServant, ApostleServantModel<ApostleServant>> {
    protected static final ResourceLocation TEXTURE = GoetyAwaken.location("textures/entity/apostle.png");
    protected static final ResourceLocation TEXTURE_2 = GoetyAwaken.location("textures/entity/apostle_second.png");
    protected static final ResourceLocation TEXTURE_E = GoetyAwaken.location("textures/entity/apostle_e.png");
    protected static final ResourceLocation TEXTURE_SECOND_E = GoetyAwaken
            .location("textures/entity/apostle_second_e.png");
    protected static final ResourceLocation EXPLODE = GoetyAwaken
            .location("textures/entity/apostle_explode.png");
    private static final float HALF_SQRT_3 = (float) (Math.sqrt(3.0D) / 2.0D);

    public ApostleServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn,
                new ApostleServantModel<>(renderManagerIn.bakeLayer(ClientEventHandler.APOSTLE_SERVANT_LAYER)), 0.5F);
        this.addLayer(new MonolithLayer<>(this, renderManagerIn.getModelSet()));
        if (com.k1sak1.goetyawaken.Config.ENABLE_APOSTLE_SERVANT_ARMOR_RENDERER.get()) {
            this.addLayer(new HumanoidArmorLayer<>(this,
                    new VillagerArmorModel<>(renderManagerIn.bakeLayer(ModModelLayer.VILLAGER_ARMOR_INNER)),
                    new VillagerArmorModel<>(renderManagerIn.bakeLayer(ModModelLayer.VILLAGER_ARMOR_OUTER)),
                    renderManagerIn.getModelManager()));
        }
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()) {
            public void render(PoseStack p_116352_, MultiBufferSource p_116353_, int p_116354_,
                    ApostleServant p_116355_,
                    float p_116356_, float p_116357_, float p_116358_, float p_116359_, float p_116360_,
                    float p_116361_) {
                if (p_116355_
                        .getArmPose() != com.Polarice3.Goety.common.entities.ally.illager.cultist.CultistServant.CultistServantArmPose.CROSSED) {
                    super.render(p_116352_, p_116353_, p_116354_, p_116355_, p_116356_, p_116357_, p_116358_, p_116359_,
                            p_116360_, p_116361_);
                }
            }
        });
        this.addLayer(new net.minecraft.client.renderer.entity.layers.CustomHeadLayer<>(this,
                renderManagerIn.getModelSet(), renderManagerIn.getItemInHandRenderer()));
        this.addLayer(new EmissiveLayer(this));
    }

    protected void scale(ApostleServant entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(0.9375F, 0.9375F, 0.9375F);
    }

    protected void setupRotations(ApostleServant pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks,
            float pRotationYaw, float pPartialTicks) {
        super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
        float f = pEntityLiving.getSwimAmount(pPartialTicks);
        if (f > 0.0F) {
            pMatrixStack.mulPose(
                    Axis.XP.rotationDegrees(Mth.lerp(f, pEntityLiving.getXRot(), -10.0F - pEntityLiving.getXRot())));
        }
    }

    @Override
    public void render(ApostleServant pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack,
            MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);

        if (pEntity.deathTime > 0) {
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
            float f8 = MobsConfig.FancierApostleDeath.get() || pEntity.level().dimension() == Level.NETHER ? 300.0F
                    : 30.0F;
            float f9 = (float) pEntity.deathTime / f8;
            VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.dragonExplosionAlpha(EXPLODE));
            this.model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F,
                    1.0F, f9);
            VertexConsumer ivertexbuilder1 = pBuffer
                    .getBuffer(RenderType.entityDecal(this.getTextureLocation(pEntity)));
            this.model.renderToBuffer(pMatrixStack, ivertexbuilder1, pPackedLight, OverlayTexture.pack(0.0F, flag),
                    1.0F, 1.0F, 1.0F, 1.0F);
            pMatrixStack.popPose();

            if (MobsConfig.FancierApostleDeath.get() || pEntity.level().dimension() == Level.NETHER) {
                if (pEntity.deathTime > 20) {
                    float f5 = ((float) pEntity.deathTime + pPartialTicks) / 200.0F;
                    float f7 = Math.min(f5 > 0.8F ? (f5 - 0.8F) / 0.2F : 0.0F, 1.0F);
                    Random random = new Random(432L);
                    VertexConsumer ivertexbuilder2 = pBuffer.getBuffer(RenderType.lightning());
                    pMatrixStack.pushPose();
                    pMatrixStack.translate(0.0D, 1.0D, 0.0D);

                    for (int i = 0; (float) i < (f5 + f5 * f5) / 2.0F * 10.0F; ++i) {
                        pMatrixStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
                        pMatrixStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
                        pMatrixStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F));
                        pMatrixStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
                        pMatrixStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
                        pMatrixStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F + f5 * 90.0F));
                        float f3 = random.nextFloat() * 2.0F + 5.0F + f7 * 10.0F;
                        float f4 = random.nextFloat() + 1.0F + f7;
                        Matrix4f matrix4f = pMatrixStack.last().pose();
                        int j = (int) (255.0F * (1.0F - f7));
                        vertex01(ivertexbuilder2, matrix4f, j);
                        vertex2(ivertexbuilder2, matrix4f, f3, f4);
                        vertex3(ivertexbuilder2, matrix4f, f3, f4);
                        vertex01(ivertexbuilder2, matrix4f, j);
                        vertex3(ivertexbuilder2, matrix4f, f3, f4);
                        vertex4(ivertexbuilder2, matrix4f, f3, f4);
                        vertex01(ivertexbuilder2, matrix4f, j);
                        vertex4(ivertexbuilder2, matrix4f, f3, f4);
                        vertex2(ivertexbuilder2, matrix4f, f3, f4);
                    }

                    pMatrixStack.popPose();
                }
            }
        }
    }

    private static void vertex01(VertexConsumer p_229061_0_, Matrix4f p_229061_1_, int p_229061_2_) {
        p_229061_0_.vertex(p_229061_1_, 0.0F, 0.0F, 0.0F).color(255, 255, 255, p_229061_2_).endVertex();
        p_229061_0_.vertex(p_229061_1_, 0.0F, 0.0F, 0.0F).color(255, 255, 255, p_229061_2_).endVertex();
    }

    private static void vertex2(VertexConsumer p_229060_0_, Matrix4f p_229060_1_, float pY, float p_229060_3_) {
        p_229060_0_.vertex(p_229060_1_, -HALF_SQRT_3 * p_229060_3_, pY, -0.5F * p_229060_3_).color(255, 0, 0, 0)
                .endVertex();
    }

    private static void vertex3(VertexConsumer p_229062_0_, Matrix4f p_229062_1_, float pY, float p_229062_3_) {
        p_229062_0_.vertex(p_229062_1_, HALF_SQRT_3 * p_229062_3_, pY, -0.5F * p_229062_3_).color(255, 0, 0, 0)
                .endVertex();
    }

    private static void vertex4(VertexConsumer p_229063_0_, Matrix4f p_229063_1_, float pY, float p_229063_3_) {
        p_229063_0_.vertex(p_229063_1_, 0.0F, pY, 1.0F * p_229063_3_).color(255, 0, 0, 0).endVertex();
    }

    @Nullable
    protected RenderType getRenderType(ApostleServant p_230496_1_, boolean p_230496_2_, boolean p_230496_3_,
            boolean p_230496_4_) {
        if (p_230496_1_.deathTime > 0) {
            return RenderType.dragonExplosionAlpha(EXPLODE);
        } else {
            return super.getRenderType(p_230496_1_, p_230496_2_, p_230496_3_, p_230496_4_);
        }
    }

    protected boolean isShaking(ApostleServant p_230495_1_) {
        return p_230495_1_.isDeadOrDying() && p_230495_1_.deathTime < 180;
    }

    @Override
    public ResourceLocation getTextureLocation(ApostleServant entity) {
        if (entity.isSecondPhase()) {
            return TEXTURE_2;
        } else {
            return TEXTURE;
        }
    }

    public ResourceLocation getEmissiveTexture(ApostleServant entity) {
        if (entity.isSecondPhase()) {
            return TEXTURE_SECOND_E;
        } else {
            return TEXTURE_E;
        }
    }

    public static class MonolithLayer<T extends ApostleServant>
            extends RenderLayer<T, ApostleServantModel<T>> {
        private static final ResourceLocation ARMOR = new ResourceLocation("goety",
                "textures/entity/cultist/apostle_aura.png");
        private final ApostleServantModel<T> model;

        public MonolithLayer(RenderLayerParent<T, ApostleServantModel<T>> p_116967_, EntityModelSet p_174555_) {
            super(p_116967_);
            this.model = new ApostleServantModel<>(
                    p_174555_.bakeLayer(com.k1sak1.goetyawaken.client.ClientEventHandler.APOSTLE_SERVANT_LAYER));
        }

        public void render(PoseStack p_116970_, MultiBufferSource p_116971_, int p_116972_, T p_116973_,
                float p_116974_, float p_116975_, float p_116976_, float p_116977_, float p_116978_, float p_116979_) {
            if ((p_116973_.isMonolithPower() || p_116973_.obsidianInvul > 0) && !p_116973_.isDeadOrDying()) {
                float f = (float) p_116973_.tickCount + p_116976_;
                ApostleServantModel<T> entitymodel = this.model;
                entitymodel.prepareMobModel(p_116973_, p_116974_, p_116975_, p_116976_);
                this.getParentModel().copyPropertiesTo(entitymodel);
                VertexConsumer vertexconsumer = p_116971_
                        .getBuffer(RenderType.energySwirl(ARMOR, this.xOffset(f) % 1.0F, f * 0.01F % 1.0F));
                entitymodel.setupAnim(p_116973_, p_116974_, p_116975_, p_116977_, p_116978_, p_116979_);
                entitymodel.renderToBuffer(p_116970_, vertexconsumer, p_116972_, OverlayTexture.NO_OVERLAY, 0.5F, 0.5F,
                        0.5F, 1.0F);
                entitymodel.halo.visible = false;
            }
        }

        protected float xOffset(float p_225634_1_) {
            return Mth.cos(p_225634_1_ * 0.02F) * 3.0F;
        }
    }

    public static class EmissiveLayer extends RenderLayer<ApostleServant, ApostleServantModel<ApostleServant>> {
        private final ApostleServantRenderer parentRenderer;

        public EmissiveLayer(ApostleServantRenderer parentRenderer) {
            super(parentRenderer);
            this.parentRenderer = parentRenderer;
        }

        @Override
        public void render(PoseStack p_116970_, MultiBufferSource p_116971_, int p_116972_,
                ApostleServant p_116973_, float p_116974_, float p_116975_, float p_116976_, float p_116977_,
                float p_116978_, float p_116979_) {

            ResourceLocation emissiveTexture = this.parentRenderer.getEmissiveTexture(p_116973_);
            VertexConsumer vertexconsumer = p_116971_.getBuffer(RenderType.eyes(emissiveTexture));
            this.getParentModel().renderToBuffer(p_116970_, vertexconsumer, p_116972_, OverlayTexture.NO_OVERLAY, 1.0F,
                    1.0F, 1.0F, 1.0F);
        }
    }
}