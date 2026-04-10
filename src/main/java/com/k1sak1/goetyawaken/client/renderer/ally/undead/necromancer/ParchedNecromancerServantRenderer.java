package com.k1sak1.goetyawaken.client.renderer.ally.undead.necromancer;

import com.k1sak1.goetyawaken.client.model.undead.necromancer.ParchedNecromancerModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.client.renderer.layers.ParchedNecromancerServantEmissiveLayer;
import com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer.ParchedNecromancerServant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParchedNecromancerServantRenderer
        extends MobRenderer<ParchedNecromancerServant, ParchedNecromancerModel<ParchedNecromancerServant>> {
    private static final ResourceLocation PARCHED_NECROMANCER_SERVANT_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/necromancer/parched_necromancer_servant.png");
    private static final ResourceLocation PARCHED_NECROMANCER_GLOW_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/necromancer/parched_necromancer_glow.png");

    public ParchedNecromancerServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn,
                new ParchedNecromancerModel<ParchedNecromancerServant>(
                        renderManagerIn
                                .bakeLayer(ClientEventHandler.PARCHED_NECROMANCER_LAYER)),
                0.5F);
        this.addLayer(new ParchedNecromancerServantEmissiveLayer(this, PARCHED_NECROMANCER_GLOW_TEXTURE));
    }

    @Override
    protected void scale(ParchedNecromancerServant necromancer, PoseStack matrixStackIn, float partialTickTime) {
        float original = 1.45F;
        float f1 = (float) necromancer.getNecroLevel();
        float size = original + Math.max(f1 * 0.15F, 0);
        matrixStackIn.scale(size, size, size);
    }

    @Override
    public void render(ParchedNecromancerServant entity, float entityYaw, float partialTicks, PoseStack matrixStack,
            MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ParchedNecromancerServant entity) {
        return PARCHED_NECROMANCER_SERVANT_TEXTURE;
    }
}