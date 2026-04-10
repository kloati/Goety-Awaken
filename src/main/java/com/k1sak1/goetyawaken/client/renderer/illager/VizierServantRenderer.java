package com.k1sak1.goetyawaken.client.renderer.illager;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.renderer.layers.VizierServantAuraLayer;
import com.k1sak1.goetyawaken.client.renderer.layers.VizierServantCapeLayer;
import com.k1sak1.goetyawaken.client.model.VizierServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.illager.VizierServant;
import com.k1sak1.goetyawaken.utils.HolidayUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.Polarice3.Goety.config.MobsConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class VizierServantRenderer extends MobRenderer<VizierServant, VizierServantModel> {
    protected static final ResourceLocation DEFAULT_TEXTURE = GoetyAwaken
            .location("textures/entity/illagers/vizier.png");

    public VizierServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new VizierServantModel(
                renderManagerIn.bakeLayer(com.k1sak1.goetyawaken.client.ClientEventHandler.VIZIER_SERVANT_LAYER)),
                0.5F);
        this.addLayer(new VizierServantAuraLayer(this, renderManagerIn.getModelSet()));
        this.addLayer(new VizierServantCapeLayer(this));
        this.addLayer(new CustomHeadLayer<VizierServant, VizierServantModel>(this, renderManagerIn.getModelSet(),
                renderManagerIn.getItemInHandRenderer()));
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()) {
            public void render(PoseStack p_116352_, MultiBufferSource p_116353_, int p_116354_, VizierServant p_116355_,
                    float p_116356_, float p_116357_, float p_116358_, float p_116359_, float p_116360_,
                    float p_116361_) {
                if (p_116355_.isCharging()) {
                    super.render(p_116352_, p_116353_, p_116354_, p_116355_, p_116356_, p_116357_, p_116358_, p_116359_,
                            p_116360_, p_116361_);
                }
            }
        });
    }

    @Override
    protected int getBlockLightLevel(VizierServant p_114496_, BlockPos p_114497_) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(VizierServant entity) {
        String name = entity.getVizierName();
        if (name.isEmpty()) {
            return DEFAULT_TEXTURE;
        }

        if (HolidayUtil.isChristmasMonth() && MobsConfig.HolidaySkins.get()) {
            return GoetyAwaken.location("textures/entity/illager/vizier/" + name + "_christmas.png");
        }

        return GoetyAwaken.location("textures/entity/illager/vizier/" + name + ".png");
    }

    @Override
    public void render(VizierServant p_114366_, float p_114367_, float p_114368_, PoseStack p_114369_,
            MultiBufferSource p_114370_, int p_114371_) {
        if (p_114366_.isCharging()) {
            p_114366_.setYRot(Mth.lerp(p_114368_, p_114366_.yBodyRotO, p_114366_.yBodyRot));
        }
        super.render(p_114366_, p_114367_, p_114368_, p_114369_, p_114370_, p_114371_);
    }
}