package com.k1sak1.goetyawaken.client.renderer.ally.Integration;

import com.k1sak1.goetyawaken.client.model.ally.Integration.SludgeServantModel;
import com.kyanite.deeperdarker.DeeperDarker;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.SludgeServant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

//Based on https://github.com/KyaniteMods/DeeperAndDarker/tree/forge-1.20, Original by kyanite
@SuppressWarnings("NullableProblems")
public class SludgeServantOuterLayer extends RenderLayer<SludgeServant, SludgeServantModel> {
    public static final ModelLayerLocation OUTER_MODEL = new ModelLayerLocation(DeeperDarker.rl("sludge_layer"),
            "outer");
    private final EntityModel<SludgeServant> model;

    public SludgeServantOuterLayer(RenderLayerParent<SludgeServant, SludgeServantModel> renderer,
            EntityModelSet modelSet) {
        super(renderer);
        this.model = new SludgeServantModel(modelSet.bakeLayer(OUTER_MODEL));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, SludgeServant livingEntity,
            float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw,
            float headPitch) {
        Minecraft minecraft = Minecraft.getInstance();
        boolean flag = minecraft.shouldEntityAppearGlowing(livingEntity) && livingEntity.isInvisible();
        if (!livingEntity.isInvisible() || flag) {
            VertexConsumer vertexconsumer;
            if (flag) {
                vertexconsumer = bufferSource.getBuffer(RenderType.outline(this.getTextureLocation(livingEntity)));
            } else {
                vertexconsumer = bufferSource
                        .getBuffer(RenderType.entityTranslucent(this.getTextureLocation(livingEntity)));
            }

            this.getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTick);
            this.model.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            this.model.renderToBuffer(poseStack, vertexconsumer, packedLight,
                    LivingEntityRenderer.getOverlayCoords(livingEntity, 0.0F), 1, 1, 1, 1);
        }
    }
}
