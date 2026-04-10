package com.k1sak1.goetyawaken.client.renderer.block;

import com.k1sak1.goetyawaken.common.blocks.entity.DarkMenderBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class DarkMenderRenderer implements BlockEntityRenderer<DarkMenderBlockEntity> {
    public DarkMenderRenderer(BlockEntityRendererProvider.Context p_i226007_1_) {
    }

    @Override
    public void render(DarkMenderBlockEntity pBlockEntity, float pPartialTicks, PoseStack pMatrixStack,
            MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay) {
        ItemStack itemStack = pBlockEntity.getItem(0);
        Minecraft minecraft = Minecraft.getInstance();
        if (!itemStack.isEmpty()) {
            pMatrixStack.pushPose();
            pMatrixStack.translate(0.5F, 0.5F, 0.5F);
            pMatrixStack.scale(1.0F, 1.0F, 1.0F);
            if (minecraft.level != null) {
                pMatrixStack
                        .mulPose(Axis.YP.rotationDegrees(3 * (minecraft.level.getGameTime() % 360 + pPartialTicks)));
            }
            minecraft.getItemRenderer().renderStatic(itemStack, ItemDisplayContext.GROUND, pCombinedLight,
                    pCombinedOverlay, pMatrixStack, pBuffer, pBlockEntity.getLevel(), 0);
            pMatrixStack.popPose();
        }
    }
}
