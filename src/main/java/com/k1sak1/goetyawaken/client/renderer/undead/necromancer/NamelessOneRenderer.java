package com.k1sak1.goetyawaken.client.renderer.undead.necromancer;

import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.client.model.undead.necromancer.NamelessOneModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.client.renderer.layers.NamelessOneEmissiveLayer;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.NamelessOne;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NamelessOneRenderer
        extends MobRenderer<NamelessOne, NamelessOneModel<NamelessOne>> {
    private static final ResourceLocation NAMELESS_ONE_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/necromancer/nameless_one.png");
    private static final ResourceLocation NAMELESS_ONE_NEW_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/necromancer/false_king.png");
    private static final ResourceLocation NAMELESS_ONE_NEW_GLOW_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/necromancer/nameless_king_glow.png");

    public NamelessOneRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn,
                new NamelessOneModel<NamelessOne>(
                        renderManagerIn
                                .bakeLayer(ClientEventHandler.NAMELESS_ONE_LAYER)),
                0.5F);
        this.addLayer(new NamelessOneEmissiveLayer(this, getGlowTexture()));
    }

    private static ResourceLocation getTexture() {
        if (Config.ENABLE_HOSTILE_NAMELESS_ONE_NEW_TEXTURE.get()) {
            return NAMELESS_ONE_NEW_TEXTURE;
        }
        return NAMELESS_ONE_TEXTURE;
    }

    private static ResourceLocation getGlowTexture() {
        return NAMELESS_ONE_NEW_GLOW_TEXTURE;
    }

    @Override
    protected void scale(NamelessOne necromancer, PoseStack matrixStackIn, float partialTickTime) {
        float original = 1.45F;
        float f1 = (float) necromancer.getNecroLevel();
        float size = original + Math.max(f1 * 0.15F, 0);
        matrixStackIn.scale(size, size, size);
    }

    @Override
    public void render(NamelessOne entity, float entityYaw, float partialTicks, PoseStack matrixStack,
            MultiBufferSource buffer, int packedLight) {
        if (entity.isDeadOrDying()) {
            entityYaw = entity.deathRotation;
        }
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(NamelessOne entity) {
        return getTexture();
    }
}