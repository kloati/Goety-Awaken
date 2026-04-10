package com.k1sak1.goetyawaken.client.renderer.undead.zombie;

import com.k1sak1.goetyawaken.client.model.ZombieDarkguardModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.zombie.ZombieDarkguard;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZombieDarkguardRenderer extends MobRenderer<ZombieDarkguard, ZombieDarkguardModel> {
    private static final ResourceLocation ZOMBIE_DARKGUARD_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/zombie/zombie_darkguard.png");

    public ZombieDarkguardRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn,
                new ZombieDarkguardModel(
                        renderManagerIn
                                .bakeLayer(ClientEventHandler.BLACKGUARD_LAYER)),
                0.5F);
    }

    @Override
    public void render(ZombieDarkguard entity, float entityYaw, float partialTicks, PoseStack matrixStack,
            MultiBufferSource buffer, int packedLight) {
        this.model.attackTime = this.getAttackAnim(entity, partialTicks);
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    protected float getAttackAnim(ZombieDarkguard entity, float partialTicks) {
        return entity.isMeleeAttacking() ? Mth.clamp(((float) entity.attackTick - partialTicks) / 10.0F, 0.0F, 1.0F)
                : 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(ZombieDarkguard entity) {
        return ZOMBIE_DARKGUARD_TEXTURE;
    }
}