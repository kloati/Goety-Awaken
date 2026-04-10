package com.k1sak1.goetyawaken.client.renderer.illager;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.illager.PreacherServantModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.common.entities.ally.illager.PreacherServant;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class PreacherServantRenderer extends MobRenderer<PreacherServant, PreacherServantModel<PreacherServant>> {
    protected static final ResourceLocation TEXTURE = GoetyAwaken
            .location("textures/entity/illager/preacher_servant.png");

    public PreacherServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn,
                new PreacherServantModel<PreacherServant>(
                        renderManagerIn.bakeLayer(ClientEventHandler.PREACHER_SERVANT_LAYER)),
                0.5F);
    }

    protected void scale(PreacherServant entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    public ResourceLocation getTextureLocation(PreacherServant entity) {
        return TEXTURE;
    }
}