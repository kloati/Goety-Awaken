package com.k1sak1.goetyawaken.client.renderer.illager;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.entities.ally.illager.SorcererServant;
import com.k1sak1.goetyawaken.client.model.illager.SorcererServantModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;

public class SorcererServantRenderer extends
        MobRenderer<SorcererServant, SorcererServantModel<SorcererServant>> {
    private static final ResourceLocation SORCERER_SERVANT = GoetyAwaken
            .location("textures/entity/illager/sorcerer_servant.png");
    private static final ResourceLocation SORCERER_HOSTILE = GoetyAwaken
            .location("textures/entity/illager/sorcerer.png");
    private static final Int2ObjectMap<ResourceLocation> LEVEL_LOCATIONS = Util.make(new Int2ObjectOpenHashMap<>(),
            (map) -> {
                map.put(1, GoetyAwaken.location("textures/entity/illager/sorcerer_level_1.png"));
                map.put(2, GoetyAwaken.location("textures/entity/illager/sorcerer_level_2.png"));
                map.put(3, GoetyAwaken.location("textures/entity/illager/sorcerer_level_3.png"));
                map.put(4, GoetyAwaken.location("textures/entity/illager/sorcerer_level_4.png"));
                map.put(5, GoetyAwaken.location("textures/entity/illager/sorcerer_level_5.png"));
                map.put(6, GoetyAwaken.location("textures/entity/illager/sorcerer_level_6.png"));
            });
    private static final Int2ObjectMap<ResourceLocation> HOSTILE_LEVEL_LOCATIONS = Util.make(
            new Int2ObjectOpenHashMap<>(),
            (map) -> {
                map.put(1, Goety.location("textures/entity/illagers/sorcerer/level_1.png"));
                map.put(2, Goety.location("textures/entity/illagers/sorcerer/level_2.png"));
                map.put(3, Goety.location("textures/entity/illagers/sorcerer/level_3.png"));
                map.put(4, Goety.location("textures/entity/illagers/sorcerer/level_4.png"));
                map.put(5, Goety.location("textures/entity/illagers/sorcerer/level_5.png"));
                map.put(6, GoetyAwaken.location("textures/entity/illager/level_6.png"));
            });

    public SorcererServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn,
                new SorcererServantModel<>(renderManagerIn.bakeLayer(ClientEventHandler.SORCERER_SERVANT_LAYER)),
                0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, renderManagerIn));
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()) {
            public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn,
                    SorcererServant entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks,
                    float ageInTicks, float netHeadYaw, float headPitch) {
                if (entitylivingbaseIn
                        .getArmPose() != AbstractIllagerServant.IllagerServantArmPose.CROSSED) {
                    super.render(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount,
                            partialTicks, ageInTicks, netHeadYaw, headPitch);
                }
            }
        });
        this.addLayer(new CastLevelLayer(this));
    }

    protected void scale(SorcererServant entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(f, f, f);
    }

    protected void setupRotations(SorcererServant pEntityLiving, PoseStack pMatrixStack, float pAgeInTicks,
            float pRotationYaw, float pPartialTicks) {
        super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
        float f = pEntityLiving.getSwimAmount(pPartialTicks);
        if (f > 0.0F) {
            pMatrixStack.mulPose(
                    Axis.XP.rotationDegrees(Mth.lerp(f, pEntityLiving.getXRot(), -10.0F - pEntityLiving.getXRot())));
        }
    }

    @Override
    public ResourceLocation getTextureLocation(SorcererServant entity) {
        if (entity.isHostile()) {
            return SORCERER_HOSTILE;
        }
        return SORCERER_SERVANT;
    }

    public static class CastLevelLayer extends
            RenderLayer<SorcererServant, SorcererServantModel<SorcererServant>> {
        public CastLevelLayer(
                RenderLayerParent<SorcererServant, SorcererServantModel<SorcererServant>> p_i50919_1_) {
            super(p_i50919_1_);
        }

        public void render(PoseStack p_116983_, MultiBufferSource p_116984_, int p_116985_, SorcererServant p_116986_,
                float p_116987_, float p_116988_, float p_116989_, float p_116990_, float p_116991_, float p_116992_) {
            if (!p_116986_.isInvisible()) {
                Int2ObjectMap<ResourceLocation> levelMap = p_116986_.isHostile() ? HOSTILE_LEVEL_LOCATIONS
                        : LEVEL_LOCATIONS;
                ResourceLocation resourcelocation2 = levelMap
                        .get(Mth.clamp(p_116986_.getSorcererLevel(), 1, levelMap.size()));
                renderColoredCutoutModel(this.getParentModel(), resourcelocation2, p_116983_, p_116984_, p_116985_,
                        p_116986_, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}