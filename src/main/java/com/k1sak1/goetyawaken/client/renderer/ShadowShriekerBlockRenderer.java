package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.common.blocks.ShadowShriekerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShadowShriekerBlockRenderer implements BlockEntityRenderer<ShadowShriekerBlockEntity> {

    public ShadowShriekerBlockRenderer(Context context) {
    }

    @Override
    public void render(ShadowShriekerBlockEntity blockEntity, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
    }

    @Override
    public boolean shouldRenderOffScreen(ShadowShriekerBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}