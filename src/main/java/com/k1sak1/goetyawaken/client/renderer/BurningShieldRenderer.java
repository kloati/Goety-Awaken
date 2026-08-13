package com.k1sak1.goetyawaken.client.renderer;

import com.Polarice3.Goety.Goety;
import com.k1sak1.goetyawaken.client.model.BurningShieldModel;
import com.k1sak1.goetyawaken.client.renderer.layers.BurningShieldBandsLayer;
import com.k1sak1.goetyawaken.common.entities.ally.BurningShield;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class BurningShieldRenderer extends MobRenderer<BurningShield, BurningShieldModel<BurningShield>> {

    private static final float SCALE = 1.4F;
    private static final float INWARD_TILT_DEGREES = 15.0F;
    private static final ResourceLocation TEXTURE = Goety
            .location("textures/entity/servants/blaze/wildfire.png");

    public BurningShieldRenderer(EntityRendererProvider.Context context) {
        super(context, new BurningShieldModel<>(context.bakeLayer(BurningShieldModel.LAYER_LOCATION)), 0.0F);
        this.addLayer(new BurningShieldBandsLayer(this, context.getModelSet()));
    }

    @Override
    protected void scale(BurningShield entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(SCALE, SCALE, SCALE);
    }

    @Override
    protected void setupRotations(BurningShield entity, PoseStack poseStack, float ageInTicks,
            float rotationYaw, float partialTicks) {
        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTicks);
        poseStack.mulPose(Axis.XP.rotationDegrees(INWARD_TILT_DEGREES));
    }

    @Override
    protected int getBlockLightLevel(BurningShield entity, BlockPos pos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(BurningShield entity) {
        return TEXTURE;
    }
}