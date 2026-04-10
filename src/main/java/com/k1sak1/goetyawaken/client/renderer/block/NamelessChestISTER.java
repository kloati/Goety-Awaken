package com.k1sak1.goetyawaken.client.renderer.block;

import com.k1sak1.goetyawaken.common.blocks.ModBlocks;
import com.k1sak1.goetyawaken.common.blocks.entity.NamelessChestBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class NamelessChestISTER extends BlockEntityWithoutLevelRenderer {

    public NamelessChestISTER() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack pStack, ItemDisplayContext pCamera, PoseStack pMatrixStack,
            MultiBufferSource pBuffer, int pLight, int pOverlay) {
        NamelessChestBlockEntity chestEntity = new NamelessChestBlockEntity(
                BlockPos.ZERO,
                ModBlocks.NAMELESS_CHEST.get().defaultBlockState());

        Minecraft.getInstance().getBlockEntityRenderDispatcher()
                .renderItem(chestEntity, pMatrixStack, pBuffer, pLight, pOverlay);
    }
}
