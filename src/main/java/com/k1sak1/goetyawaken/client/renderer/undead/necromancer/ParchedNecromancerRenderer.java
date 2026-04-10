package com.k1sak1.goetyawaken.client.renderer.undead.necromancer;

import com.k1sak1.goetyawaken.client.model.undead.necromancer.ParchedNecromancerModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.client.renderer.layers.ParchedNecromancerEmissiveLayer;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.ParchedNecromancer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParchedNecromancerRenderer
        extends MobRenderer<ParchedNecromancer, ParchedNecromancerModel<ParchedNecromancer>> {
    private static final ResourceLocation PARCHED_NECROMANCER_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/necromancer/parched_necromancer.png");
    private static final ResourceLocation PARCHED_NECROMANCER_GLOW_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/necromancer/parched_necromancer_glow.png");

    public ParchedNecromancerRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn,
                new ParchedNecromancerModel<ParchedNecromancer>(
                        renderManagerIn
                                .bakeLayer(ClientEventHandler.PARCHED_NECROMANCER_LAYER)),
                0.5F);
        this.addLayer(new ParchedNecromancerEmissiveLayer(this, PARCHED_NECROMANCER_GLOW_TEXTURE));
    }

    @Override
    protected void scale(ParchedNecromancer necromancer, PoseStack matrixStackIn, float partialTickTime) {
        float original = 1.45F;
        float f1 = (float) necromancer.getNecroLevel();
        float size = original + Math.max(f1 * 0.15F, 0);
        matrixStackIn.scale(size, size, size);
    }

    @Override
    public void render(ParchedNecromancer entity, float entityYaw, float partialTicks, PoseStack matrixStack,
            MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ParchedNecromancer entity) {
        return PARCHED_NECROMANCER_TEXTURE;
    }
}