package com.k1sak1.goetyawaken.client.renderer.item;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.PotatoStaffModel;
import com.k1sak1.goetyawaken.common.items.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PotatoStaffItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation TEXTURE_3D = GoetyAwaken
            .location("textures/item/potato_staff_model.png");
    private static final ResourceLocation TEXTURE_INVENTORY = GoetyAwaken
            .location("textures/item/potato_staff.png");

    private final PotatoStaffModel<?> model;

    public PotatoStaffItemRenderer() {
        this(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    public PotatoStaffItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
        this.model = new PotatoStaffModel<>(modelSet.bakeLayer(PotatoStaffModel.LAYER_LOCATION));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType,
            PoseStack poseStack, MultiBufferSource buffer,
            int combinedLight, int combinedOverlay) {
        if (stack.getItem() != ModItems.POTATO_STAFF.get()) {
            return;
        }

        float tick = 0;
        if (Minecraft.getInstance().player != null && !Minecraft.getInstance().isPaused()) {
            tick = Minecraft.getInstance().player.tickCount + Minecraft.getInstance().getPartialTick();
        }

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.scale(1.0F, -1.0F, -1.0F);

        this.model.animate(tick);

        VertexConsumer consumer = ItemRenderer.getArmorFoilBuffer(
                buffer, RenderType.entityCutoutNoCull(TEXTURE_3D), false, stack.hasFoil());
        this.model.renderToBuffer(poseStack, consumer, combinedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }
}
