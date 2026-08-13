package com.k1sak1.goetyawaken.client.renderer.ally.Integration;

import com.k1sak1.goetyawaken.client.model.ally.Integration.SludgeServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.SludgeServant;
import com.kyanite.deeperdarker.DeeperDarker;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

//Based on https://github.com/KyaniteMods/DeeperAndDarker/tree/forge-1.20, Original by kyanite
@SuppressWarnings("NullableProblems")
public class SludgeServantRenderer extends MobRenderer<SludgeServant, SludgeServantModel> {
    public static final ModelLayerLocation MODEL = new ModelLayerLocation(DeeperDarker.rl("sludge_layer"),
            "main");
    private static final ResourceLocation TEXTURE = DeeperDarker.rl("textures/entity/sludge.png");

    public SludgeServantRenderer(EntityRendererProvider.Context context) {
        super(context, new SludgeServantModel(context.bakeLayer(MODEL)), 0.25f);
        this.addLayer(new SludgeServantOuterLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(SludgeServant entity) {
        return TEXTURE;
    }

    @Override
    public void render(SludgeServant entity, float entityYaw, float partialTicks, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight) {
        this.shadowRadius = 0.25f * entity.getSize();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    protected void scale(SludgeServant livingEntity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(0.999f, 0.999f, 0.999f);
        poseStack.translate(0f, 0.001f, 0f);
        int size = livingEntity.getSize();
        float lerpSize = Mth.lerp(partialTickTime, livingEntity.oSquish, livingEntity.squish) / (size / 2f + 1);
        float inv = 1f / (lerpSize + 1);
        poseStack.scale(inv * size, 1f / inv * size, inv * size);
    }
}
