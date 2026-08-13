package com.k1sak1.goetyawaken.client.renderer.entity.projectile;

import com.k1sak1.goetyawaken.common.entities.projectiles.FlyingAxeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;

public class FlyingAxeRenderer extends EntityRenderer<FlyingAxeEntity> {
    private final ItemRenderer itemRenderer;

    public FlyingAxeRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(FlyingAxeEntity entity, float entityYaw, float partialTicks, PoseStack stack,
            MultiBufferSource buffer, int packedLight) {
        stack.pushPose();
        ItemStack itemStack = entity.getItem();

        stack.translate(0.0D, 0.15D, 0.0D);

        if (!entity.isInGround()) {
            float yaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
            float spin = (entity.tickCount + partialTicks) * 18F;

            stack.mulPose(Axis.YP.rotationDegrees(yaw + 90.0F));
            stack.mulPose(Axis.ZP.rotationDegrees(spin));
        } else {
            stack.mulPose(Axis.YP.rotationDegrees(
                    Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
            stack.mulPose(Axis.ZP.rotationDegrees(
                    Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) - 45.0F));
        }

        this.itemRenderer.renderStatic(itemStack, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY,
                stack, buffer, entity.level(), 0);

        stack.popPose();
        super.render(entity, entityYaw, partialTicks, stack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(FlyingAxeEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
