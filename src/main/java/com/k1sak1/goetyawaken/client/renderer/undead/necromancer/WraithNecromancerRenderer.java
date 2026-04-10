package com.k1sak1.goetyawaken.client.renderer.undead.necromancer;

import com.k1sak1.goetyawaken.client.model.undead.necromancer.WraithNecromancerModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.client.renderer.layers.WraithNecromancerEmissiveLayer;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.WraithNecromancer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WraithNecromancerRenderer
        extends MobRenderer<WraithNecromancer, WraithNecromancerModel<WraithNecromancer>> {
    private static final ResourceLocation WRAITH_NECROMANCER_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/necromancer/wraith_necromancer.png");
    private static final ResourceLocation WRAITH_NECROMANCER_GLOW_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/necromancer/wraith_necromancer_glow.png");

    public WraithNecromancerRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn,
                new WraithNecromancerModel<WraithNecromancer>(
                        renderManagerIn
                                .bakeLayer(ClientEventHandler.WRAITH_NECROMANCER_LAYER)),
                0.5F);
        this.addLayer(new WraithNecromancerEmissiveLayer(this, WRAITH_NECROMANCER_GLOW_TEXTURE));
    }

    @Override
    protected void scale(WraithNecromancer necromancer, PoseStack matrixStackIn, float partialTickTime) {
        float original = 1.45F;
        float f1 = (float) necromancer.getNecroLevel();
        float size = original + Math.max(f1 * 0.15F, 0);
        matrixStackIn.scale(size, size, size);
    }

    @Override
    public void render(WraithNecromancer entity, float entityYaw, float partialTicks, PoseStack matrixStack,
            MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(WraithNecromancer entity) {
        return WRAITH_NECROMANCER_TEXTURE;
    }
}