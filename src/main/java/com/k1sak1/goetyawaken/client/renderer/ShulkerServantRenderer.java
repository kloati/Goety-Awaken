package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.client.model.ShulkerServantModel;
import com.k1sak1.goetyawaken.client.renderer.layers.ShulkerServantHeadLayer;
import com.k1sak1.goetyawaken.common.entities.ally.ShulkerServant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

public class ShulkerServantRenderer extends MobRenderer<ShulkerServant, ShulkerServantModel> {
    public static final ResourceLocation SHULKER_SERVANT_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/shulker_servant.png");

    public ShulkerServantRenderer(EntityRendererProvider.Context context) {
        super(context, new ShulkerServantModel(context.bakeLayer(ClientEventHandler.SHULKER_SERVANT_LAYER)), 0.0F);
        this.addLayer(new ShulkerServantHeadLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(ShulkerServant entity) {
        return SHULKER_SERVANT_TEXTURE;
    }

    @Override
    protected void setupRotations(ShulkerServant pEntityLiving, PoseStack pPoseStack, float pAgeInTicks,
            float pRotationYaw, float pPartialTicks) {
        super.setupRotations(pEntityLiving, pPoseStack, pAgeInTicks, pRotationYaw + 180.0F, pPartialTicks);
        pPoseStack.translate(0.0D, 0.5D, 0.0D);
        pPoseStack.mulPose(pEntityLiving.getAttachFace().getOpposite().getRotation());
        pPoseStack.translate(0.0D, -0.5D, 0.0D);
    }

    @Override
    public boolean shouldRender(ShulkerServant pLivingEntity, net.minecraft.client.renderer.culling.Frustum pCamera,
            double pCamX, double pCamY, double pCamZ) {
        return super.shouldRender(pLivingEntity, pCamera, pCamX, pCamY, pCamZ) ? true
                : pLivingEntity.getRenderPosition(0.0F).filter((p_174374_) -> {
                    EntityType<?> entitytype = pLivingEntity.getType();
                    float f = entitytype.getHeight() / 2.0F;
                    float f1 = entitytype.getWidth() / 2.0F;
                    Vec3 vec3 = Vec3.atBottomCenterOf(pLivingEntity.blockPosition());
                    return pCamera.isVisible((new net.minecraft.world.phys.AABB(p_174374_.x, p_174374_.y + (double) f,
                            p_174374_.z, vec3.x, vec3.y + (double) f, vec3.z))
                            .inflate((double) f1, (double) f, (double) f1));
                }).isPresent();
    }

    @Override
    public Vec3 getRenderOffset(ShulkerServant pEntity, float pPartialTicks) {
        return pEntity.getRenderPosition(pPartialTicks).orElse(super.getRenderOffset(pEntity, pPartialTicks));
    }
}