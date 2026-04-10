package com.k1sak1.goetyawaken.client.renderer.undead.skeleton;

import com.k1sak1.goetyawaken.client.model.SkeletonVanguardModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.skeleton.SkeletonVanguard;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkeletonVanguardRenderer extends MobRenderer<SkeletonVanguard, SkeletonVanguardModel> {
    private static final ResourceLocation SKELETON_VANGUARD_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/skeleton/skeleton_vanguard.png");

    public SkeletonVanguardRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn,
                new SkeletonVanguardModel(
                        renderManagerIn
                                .bakeLayer(ClientEventHandler.VANGUARD_LAYER)),
                0.5F);
    }

    @Override
    public void render(SkeletonVanguard entity, float entityYaw, float partialTicks, PoseStack matrixStack,
            MultiBufferSource buffer, int packedLight) {
        this.model.attackTime = this.getAttackAnim(entity, partialTicks);
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    protected float getAttackAnim(SkeletonVanguard entity, float partialTicks) {
        return entity.isMeleeAttacking() ? Mth.clamp(((float) entity.attackTick - partialTicks) / 10.0F, 0.0F, 1.0F)
                : 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(SkeletonVanguard entity) {
        return SKELETON_VANGUARD_TEXTURE;
    }
}