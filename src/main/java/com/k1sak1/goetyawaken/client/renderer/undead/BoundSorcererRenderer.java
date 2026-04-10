package com.k1sak1.goetyawaken.client.renderer.undead;

import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.VillagerArmorModel;
import com.Polarice3.Goety.common.entities.ally.undead.bound.AbstractBoundIllager;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.entities.ally.undead.BoundSorcerer;
import com.k1sak1.goetyawaken.client.model.undead.BoundSorcererModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BoundSorcererRenderer extends MobRenderer<BoundSorcerer, BoundSorcererModel<BoundSorcerer>> {
    private static final ResourceLocation BOUND_SORCERER = GoetyAwaken
            .location("textures/entity/undead/bound_sorcerer.png");
    private static final ResourceLocation BOUND_SORCERER_CASTING = GoetyAwaken
            .location("textures/entity/undead/bound_sorcerer_casting.png");

    public BoundSorcererRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn,
                new BoundSorcererModel<>(renderManagerIn.bakeLayer(ClientEventHandler.BOUND_SORCERER_LAYER)),
                0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this,
                new VillagerArmorModel<>(renderManagerIn.bakeLayer(ModModelLayer.VILLAGER_ARMOR_INNER)),
                new VillagerArmorModel<>(renderManagerIn.bakeLayer(ModModelLayer.VILLAGER_ARMOR_OUTER)),
                renderManagerIn.getModelManager()));
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()) {
            public void render(PoseStack p_116352_, MultiBufferSource p_116353_, int p_116354_, BoundSorcerer p_116355_,
                    float p_116356_, float p_116357_, float p_116358_, float p_116359_, float p_116360_,
                    float p_116361_) {
                if (p_116355_.getArmPose() != AbstractBoundIllager.BoundArmPose.CROSSED) {
                    super.render(p_116352_, p_116353_, p_116354_, p_116355_, p_116356_, p_116357_, p_116358_, p_116359_,
                            p_116360_, p_116361_);
                }
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(BoundSorcerer entity) {
        if (entity.isCastingSpell() || entity.isCharging() || entity.isShoot()) {
            return BOUND_SORCERER_CASTING;
        }
        return BOUND_SORCERER;
    }

    @Override
    protected void scale(BoundSorcerer entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(f, f, f);
    }

    @Override
    protected void setupRotations(BoundSorcerer pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks,
            float pRotationYaw, float pPartialTicks) {
        super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw,
                pPartialTicks);
        float f = pEntityLiving.getSwimAmount(pPartialTicks);
        if (f > 0.0F) {
            pMatrixStack.mulPose(
                    Axis.XP.rotationDegrees(Mth.lerp(f, pEntityLiving.getXRot(), -10.0F -
                            pEntityLiving.getXRot())));
        }
    }
}