package com.k1sak1.goetyawaken.client.renderer.illager;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.entities.ally.illager.MinisterServant;
import com.k1sak1.goetyawaken.client.model.illager.MinisterServantModel;

import com.k1sak1.goetyawaken.utils.HolidayUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Axis;

import javax.annotation.Nullable;

public class MinisterServantRenderer extends MobRenderer<MinisterServant, MinisterServantModel<MinisterServant>> {
    private static final ResourceLocation MINISTER_SERVANT = GoetyAwaken
            .location("textures/entity/illager/minister_servant.png");
    private static final ResourceLocation MINISTER_SERVANT_WU1WU2 = GoetyAwaken
            .location("textures/entity/illager/minister_servant_wu1wu2.png");
    private static final ResourceLocation MINISTER_SERVANT_CHRISTMAS = GoetyAwaken
            .location("textures/entity/illager/minister_servant_christmas.png");
    private static final ResourceLocation MINISTER_SERVANT_WUWU = GoetyAwaken
            .location("textures/entity/illager/minister_servant_wuwu.png");
    private static final ResourceLocation MINISTER_SERVANT_WUWU_CHRISTMAS = GoetyAwaken
            .location("textures/entity/illager/minister_servant_wuwu_christmas.png");

    public MinisterServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn,
                new MinisterServantModel<>(
                        renderManagerIn
                                .bakeLayer(com.k1sak1.goetyawaken.client.ClientEventHandler.MINISTER_SERVANT_LAYER)),
                0.5F);
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()) {
            public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn,
                    MinisterServant entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks,
                    float ageInTicks, float netHeadYaw, float headPitch) {
                if (entitylivingbaseIn
                        .getArmPose() != com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant.IllagerServantArmPose.CROSSED) {
                    super.render(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount,
                            partialTicks, ageInTicks, netHeadYaw, headPitch);
                }
            }
        });
        this.addLayer(new net.minecraft.client.renderer.entity.layers.CustomHeadLayer<>(this,
                renderManagerIn.getModelSet(), renderManagerIn.getItemInHandRenderer()));
    }

    protected void scale(MinisterServant entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.0F, 1.0F, 1.0F);
    }

    protected void setupRotations(MinisterServant pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks,
            float pRotationYaw, float pPartialTicks) {
        super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
        float f = pEntityLiving.getSwimAmount(pPartialTicks);
        if (f > 0.0F) {
            pMatrixStack.mulPose(
                    Axis.XP.rotationDegrees(Mth.lerp(f, pEntityLiving.getXRot(), -10.0F - pEntityLiving.getXRot())));
        }
    }

    @Nullable
    @Override
    protected RenderType getRenderType(MinisterServant ministerServant, boolean b, boolean b1, boolean b2) {
        if (ministerServant.deathTime > 0) {
            return RenderType.entityCutoutNoCull(this.getTextureLocation(ministerServant));
        }
        return super.getRenderType(ministerServant, b, b1, b2);
    }

    @Override
    public ResourceLocation getTextureLocation(MinisterServant entity) {
        if (entity.hasCustomName()) {
            String name = entity.getCustomName().getString().toLowerCase();
            if (name.equals("wu1wu2") || name.equals("marble")) {
                if (HolidayUtil.isChristmasMonth()) {
                    return MINISTER_SERVANT_WUWU_CHRISTMAS;
                } else {
                    return MINISTER_SERVANT_WUWU;
                }
            }
        }

        if (HolidayUtil.isChristmasMonth()) {
            return MINISTER_SERVANT_CHRISTMAS;
        }
        switch (entity.getOutfitType()) {
            case 1:
                return MINISTER_SERVANT_WU1WU2;
            case 2:
                return MINISTER_SERVANT;
            default:
                return MINISTER_SERVANT;
        }
    }
}