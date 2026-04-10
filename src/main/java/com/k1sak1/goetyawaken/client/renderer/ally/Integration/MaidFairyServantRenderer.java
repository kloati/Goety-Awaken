package com.k1sak1.goetyawaken.client.renderer.ally.Integration;

import com.k1sak1.goetyawaken.common.entities.ally.Integration.MaidFairyServant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MaidFairyServantRenderer extends
        MobRenderer<MaidFairyServant, com.k1sak1.goetyawaken.client.model.ally.Integration.AdaptedMaidFairyModel<MaidFairyServant>> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[18];

    static {
        for (int i = 0; i < 18; i++) {
            TEXTURES[i] = new ResourceLocation("touhou_little_maid",
                    "textures/bedrock/entity/new_maid_fairy/maid_fairy_" + i + ".png");
        }
    }

    public MaidFairyServantRenderer(EntityRendererProvider.Context context) {
        super(context,
                new com.k1sak1.goetyawaken.client.model.ally.Integration.AdaptedMaidFairyModel<>(
                        context.bakeLayer(
                                com.k1sak1.goetyawaken.client.model.ally.Integration.AdaptedMaidFairyModel.LAYER_LOCATION)),
                0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(MaidFairyServant entity) {
        String entityName = entity.getName().getString();
        if ("wu1wu2".equals(entityName) || "marble".equals(entityName)) {
            return new ResourceLocation("goetyawaken", "textures/entity/maid_fairy_wu.png");
        } else if ("rick".equals(entityName)) {
            return new ResourceLocation("touhou_little_maid",
                    "textures/bedrock/entity/new_maid_fairy/maid_fairy_rick.png");
        } else if ("k1sak1".equals(entityName)) {
            return new ResourceLocation("goetyawaken", "textures/entity/maid_fairy_k1.png");
        } else if ("crystalskeleton9".equals(entityName)) {
            return new ResourceLocation("goetyawaken", "textures/entity/maid_fairy_crystalskeleton9.png");
        } else {
            int fairyTypeOrdinal = entity.getFairyTypeOrdinal();
            if (fairyTypeOrdinal >= 0 && fairyTypeOrdinal < TEXTURES.length) {
                return TEXTURES[fairyTypeOrdinal];
            }
            return TEXTURES[0];
        }
    }

    @Override
    protected void setupRotations(MaidFairyServant entity, PoseStack poseStack, float ageInTicks, float rotationYaw,
            float partialTicks) {
        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTicks);
        if (!entity.onGround()) {
            poseStack.mulPose(com.mojang.math.Axis.XN.rotation(8 * (float) Math.PI / 180.0f));
        }
    }
}