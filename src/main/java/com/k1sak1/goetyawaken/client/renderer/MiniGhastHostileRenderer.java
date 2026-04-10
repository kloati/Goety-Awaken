package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.client.model.MiniGhastModel;
import com.k1sak1.goetyawaken.common.entities.hostile.MiniGhastHostile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MiniGhastHostileRenderer extends MobRenderer<MiniGhastHostile, MiniGhastModel<MiniGhastHostile>> {
    private static final ResourceLocation GHAST_LOCATION = GoetyAwaken
            .location("textures/entity/mini_ghast.png");
    private static final ResourceLocation GHAST_SHOOTING_LOCATION = GoetyAwaken
            .location("textures/entity/mini_ghast_shooting.png");

    public MiniGhastHostileRenderer(EntityRendererProvider.Context context) {
        super(context, new MiniGhastModel<>(context.bakeLayer(ClientEventHandler.MINI_GHAST_HOSTILE)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(MiniGhastHostile entity) {
        if (entity.isCharging()) {
            return GHAST_SHOOTING_LOCATION;
        }
        return GHAST_LOCATION;
    }

    @Override
    protected void scale(MiniGhastHostile entity, PoseStack matrixStack, float partialTickTime) {
        float f = entity.getSwelling(partialTickTime);
        if (f < 0.0F) {
            f = 0.0F;
        }
        f = 1.0F / (f * f * f * f * f * 2.0F + 1.0F);
        float var5 = (3.0F + f) / 2.0F;
        float var6 = (3.0F + 1.0F / f) / 2.0F;
        matrixStack.scale(var6, var5, var6);
    }
}