package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.HostileSnapperModel;
import com.k1sak1.goetyawaken.common.entities.hostile.HostileSnapper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HostileSnapperRenderer extends MobRenderer<HostileSnapper, HostileSnapperModel<HostileSnapper>> {
    protected static final ResourceLocation TEXTURE = GoetyAwaken.location("textures/entity/snapper.png");

    public HostileSnapperRenderer(EntityRendererProvider.Context context) {
        super(context, new HostileSnapperModel<>(context.bakeLayer(HostileSnapperModel.LAYER_LOCATION)), 0.4F);
        this.addLayer(new HostileSnapperEyesLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(HostileSnapper entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(HostileSnapper entity, PoseStack poseStack, float partialTick) {
        int i = entity.isUpgraded() ? 1 : 0;
        float f = 1.0F + 0.15F * (float) i;
        poseStack.scale(f, f, f);
    }

    @Override
    protected void setupRotations(HostileSnapper entity, PoseStack poseStack, float ageInTicks, float rotationYaw,
            float partialTick) {
        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTick);
        float f = 1.0F;
        float f1 = 1.0F;
        if (!entity.isInWater()) {
            f = 1.3F;
            f1 = 1.7F;
        }

        float f2 = f * 4.3F * Mth.sin(f1 * 0.6F * ageInTicks);
        poseStack.mulPose(Axis.YP.rotationDegrees(f2));
        poseStack.translate(0.0F, 0.0F, 0.0F);
        if (!entity.isInWater()) {
            poseStack.translate(0.2F, 0.1F, 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }
    }

    public static class HostileSnapperEyesLayer<T extends Mob, M extends HostileSnapperModel<T>>
            extends EyesLayer<T, M> {
        private static final RenderType EYES = RenderType
                .eyes(new ResourceLocation("goety", "textures/entity/servants/snapper/snapper_eyes.png"));

        public HostileSnapperEyesLayer(RenderLayerParent<T, M> renderer) {
            super(renderer);
        }

        @Override
        public RenderType renderType() {
            return EYES;
        }
    }
}