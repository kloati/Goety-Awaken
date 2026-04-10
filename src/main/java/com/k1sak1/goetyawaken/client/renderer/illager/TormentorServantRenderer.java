package com.k1sak1.goetyawaken.client.renderer.illager;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.illager.TormentorServantModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.common.entities.ally.illager.TormentorServant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class TormentorServantRenderer extends MobRenderer<TormentorServant, TormentorServantModel> {
    private static final ResourceLocation TEXTURE = GoetyAwaken.location("textures/entity/illager/ally_tormentor.png");
    private static final ResourceLocation TEXTURE_CHARGE = GoetyAwaken
            .location("textures/entity/illager/ally_tormentor_charge.png");

    public TormentorServantRenderer(EntityRendererProvider.Context p_i47190_1_) {
        super(p_i47190_1_, new TormentorServantModel(p_i47190_1_.bakeLayer(ClientEventHandler.TORMENTOR_SERVANT_LAYER)),
                0.5F);
        this.addLayer(new ItemInHandLayer<>(this, p_i47190_1_.getItemInHandRenderer()));
        this.addLayer(new TormentorServantVisageLayer(this));
    }

    protected int getBlockLightLevel(TormentorServant pEntity, BlockPos pPos) {
        return 15;
    }

    public ResourceLocation getTextureLocation(TormentorServant pEntity) {
        return pEntity.isCharging() ? TEXTURE_CHARGE : TEXTURE;
    }

    protected void scale(TormentorServant entitylivingbaseIn, com.mojang.blaze3d.vertex.PoseStack matrixStackIn,
            float partialTickTime) {
        matrixStackIn.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    public void render(TormentorServant entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn,
            MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    protected RenderType getRenderType(TormentorServant entityIn, boolean p_230495_2_, boolean p_230495_3_,
            boolean p_230495_4_) {
        return super.getRenderType(entityIn, p_230495_2_, p_230495_3_, p_230495_4_);
    }
}