package com.k1sak1.goetyawaken.client.renderer.ally.Integration;

import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.izofar.takesapillage.TakesAPillageMod;
import com.k1sak1.goetyawaken.client.model.ally.Integration.LegionerServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.LegionerServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//Based on https://github.com/izofar/takes-a-pillage, Original by izofar
@OnlyIn(Dist.CLIENT)
public class LegionerServantRenderer extends MobRenderer<LegionerServant, LegionerServantModel> {

    private static final ResourceLocation LEGIONER = new ResourceLocation(TakesAPillageMod.MODID,
            "textures/entity/legioner.png");
    private static final ResourceLocation LEGIONER_AWAKEN = new ResourceLocation("goetyawaken",
            "textures/entity/illager/takesapillage/legioner.png");

    public LegionerServantRenderer(EntityRendererProvider.Context context) {
        super(context, new LegionerServantModel(context.bakeLayer(ModModelLayer.ILLAGER_SERVANT)), 0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, context));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.model.getHat().visible = true;
    }

    public ResourceLocation getTextureLocation(LegionerServant legioner) {
        return legioner.isHostile() ? LEGIONER : LEGIONER_AWAKEN;
    }
}
