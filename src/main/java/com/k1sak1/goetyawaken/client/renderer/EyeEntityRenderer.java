package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.entities.projectiles.EyeBaseEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EyeEntityRenderer<T extends EyeBaseEntity> extends EntityRenderer<T> {
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean fullBright;
    private final ResourceLocation textureLocation;

    public EyeEntityRenderer(EntityRendererProvider.Context pContext, String texturePath) {
        super(pContext);
        this.itemRenderer = pContext.getItemRenderer();
        this.scale = 1.0F;
        this.fullBright = false;
        this.textureLocation = new ResourceLocation(GoetyAwaken.MODID, texturePath);
    }

    @Override
    public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack,
            MultiBufferSource pBuffer, int pPackedLight) {
        if (pEntity.tickCount >= 2
                || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(pEntity) < 12.25D)) {
            pPoseStack.pushPose();
            pPoseStack.scale(this.scale, this.scale, this.scale);
            pPoseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            this.itemRenderer.renderStatic(pEntity.getItem(), net.minecraft.world.item.ItemDisplayContext.GROUND,
                    getBlockLightLevel(pEntity, BlockPos.containing(pEntity.position())),
                    net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer,
                    pEntity.level(), pEntity.getId());
            pPoseStack.popPose();
        }
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    protected int getBlockLightLevel(T pEntity, BlockPos pPos) {
        return this.fullBright ? 15 : super.getBlockLightLevel(pEntity, pPos);
    }

    @Override
    public ResourceLocation getTextureLocation(T pEntity) {
        return textureLocation;
    }
}