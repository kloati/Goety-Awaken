package com.k1sak1.goetyawaken.client.renderer.ally.Integration;

import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.izofar.takesapillage.TakesAPillageMod;
import com.k1sak1.goetyawaken.client.model.ally.Integration.SkirmisherServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.SkirmisherServant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//Based on https://github.com/izofar/takes-a-pillage, Original by izofar
@OnlyIn(Dist.CLIENT)
public class SkirmisherServantRenderer extends MobRenderer<SkirmisherServant, SkirmisherServantModel> {

    private static final ResourceLocation SKIRMISHER = new ResourceLocation(TakesAPillageMod.MODID,
            "textures/entity/skirmisher.png");
    private static final ResourceLocation SKIRMISHER_AWAKEN = new ResourceLocation("goetyawaken",
            "textures/entity/illager/takesapillage/skirmisher.png");

    public SkirmisherServantRenderer(EntityRendererProvider.Context context) {
        super(context, new SkirmisherServantModel(SkirmisherServantModel.createBodyLayer().bakeRoot()), 0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, context));
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    public ResourceLocation getTextureLocation(SkirmisherServant skirmisher) {
        return skirmisher.isHostile() ? SKIRMISHER : SKIRMISHER_AWAKEN;
    }

    protected void scale(SkirmisherServant skirmisher, PoseStack stack, float f) {
        stack.scale(0.9375F, 0.9375F, 0.9375F);
    }
}
