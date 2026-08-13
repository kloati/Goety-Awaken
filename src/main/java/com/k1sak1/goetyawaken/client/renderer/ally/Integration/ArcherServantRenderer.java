package com.k1sak1.goetyawaken.client.renderer.ally.Integration;

import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.Polarice3.Goety.client.render.model.IllagerServantModel;
import com.izofar.takesapillage.TakesAPillageMod;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.ArcherServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//Based on https://github.com/izofar/takes-a-pillage, Original by izofar
@OnlyIn(Dist.CLIENT)
public class ArcherServantRenderer extends MobRenderer<ArcherServant, IllagerServantModel<ArcherServant>> {

    private static final ResourceLocation ARCHER = new ResourceLocation(TakesAPillageMod.MODID,
            "textures/entity/archer.png");
    private static final ResourceLocation ARCHER_AWAKEN = new ResourceLocation("goetyawaken",
            "textures/entity/illager/takesapillage/archer.png");

    public ArcherServantRenderer(EntityRendererProvider.Context context) {
        super(context, new IllagerServantModel<>(context.bakeLayer(ModModelLayer.ILLAGER_SERVANT)), 0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, context));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.model.getHat().visible = true;
    }

    public ResourceLocation getTextureLocation(ArcherServant archer) {
        return archer.isHostile() ? ARCHER : ARCHER_AWAKEN;
    }
}
