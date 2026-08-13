package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.RampartCaptainModel;
import com.k1sak1.goetyawaken.common.entities.ally.illager.RampartCaptain;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RampartCaptainRenderer extends MobRenderer<RampartCaptain, RampartCaptainModel<RampartCaptain>> {
    private static final ResourceLocation HOSTILE_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/illager/rampart_captain.png");
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/illager/rampart_captain_servant.png");

    public RampartCaptainRenderer(EntityRendererProvider.Context context) {
        super(context, new RampartCaptainModel<>(context.bakeLayer(RampartCaptainModel.LAYER_LOCATION)), 0.55F);
        this.addLayer(new HierarchicalArmorLayer<>(this, context));
    }

    @Override
    protected void scale(RampartCaptain entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.1F, 1.1F, 1.1F);
        super.scale(entity, poseStack, partialTick);
    }

    @Override
    public ResourceLocation getTextureLocation(RampartCaptain entity) {
        if (entity.isHostile()) {
            return HOSTILE_TEXTURE;
        } else {
            return TEXTURE;
        }
    }
}
