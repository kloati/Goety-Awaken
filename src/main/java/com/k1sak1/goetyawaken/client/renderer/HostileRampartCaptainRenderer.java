package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.RampartCaptainModel;
import com.k1sak1.goetyawaken.common.entities.hostile.illager.HostileRampartCaptain.HostileRampartCaptain;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class HostileRampartCaptainRenderer
        extends MobRenderer<HostileRampartCaptain, RampartCaptainModel<HostileRampartCaptain>> {
    private static final ResourceLocation HOSTILE_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/illager/rampart_captain.png");

    public HostileRampartCaptainRenderer(EntityRendererProvider.Context context) {
        super(context, new RampartCaptainModel<>(context.bakeLayer(RampartCaptainModel.LAYER_LOCATION)),
                0.55F);
        this.addLayer(new HierarchicalArmorLayer<>(this, context));
    }

    @Override
    protected void scale(HostileRampartCaptain entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.1F, 1.1F, 1.1F);
        super.scale(entity, poseStack, partialTick);
    }

    @Override
    public ResourceLocation getTextureLocation(HostileRampartCaptain entity) {
        return HOSTILE_TEXTURE;
    }
}
