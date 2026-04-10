package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.common.blocks.entity.TrialSpawnerBlockEntity;
import com.k1sak1.goetyawaken.common.blocks.entity.trial_spawner.TrialSpawner;
import com.k1sak1.goetyawaken.common.blocks.entity.trial_spawner.TrialSpawnerData;
import com.k1sak1.goetyawaken.common.blocks.entity.trial_spawner.TrialSpawnerState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class TrialSpawnerRenderer implements BlockEntityRenderer<TrialSpawnerBlockEntity> {
    private final EntityRenderDispatcher entityRenderer;

    public TrialSpawnerRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderer = context.getEntityRenderer();
    }

    @Override
    public void render(TrialSpawnerBlockEntity pBlockEntity, float pPartialTick, PoseStack pMatrixStack,
            MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay) {
        pMatrixStack.pushPose();
        pMatrixStack.translate(0.5F, 0.0F, 0.5F);
        Level level = pBlockEntity.getLevel();
        if (level != null) {
            TrialSpawner spawner = pBlockEntity.getTrialSpawner();
            TrialSpawnerData data = spawner.getData();
            TrialSpawnerState state = spawner.getState();
            Entity entity = data.getOrCreateDisplayEntity(spawner, level, state);
            if (entity != null) {
                float f = 0.53125F;
                float f1 = Math.max(entity.getBbWidth(), entity.getBbHeight());
                if ((double) f1 > 1.0D) {
                    f /= f1;
                }

                pMatrixStack.translate(0.0F, 0.4F, 0.0F);
                pMatrixStack.mulPose(Axis.YP
                        .rotationDegrees((float) Mth.lerp(pPartialTick, data.getOSpin(), data.getSpin()) * 10.0F));
                pMatrixStack.translate(0.0F, -0.2F, 0.0F);
                pMatrixStack.mulPose(Axis.XP.rotationDegrees(-30.0F));
                pMatrixStack.scale(f, f, f);
                this.entityRenderer.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, pPartialTick, pMatrixStack, pBuffer,
                        pCombinedLight);
            }

        }
        pMatrixStack.popPose();
    }
}
