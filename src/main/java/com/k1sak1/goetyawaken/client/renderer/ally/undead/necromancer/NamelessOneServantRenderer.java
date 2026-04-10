package com.k1sak1.goetyawaken.client.renderer.ally.undead.necromancer;

import com.k1sak1.goetyawaken.client.model.undead.necromancer.NamelessOneModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.client.renderer.layers.NamelessOneServantEmissiveLayer;
import com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer.NamelessOneServant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NamelessOneServantRenderer
        extends MobRenderer<NamelessOneServant, NamelessOneModel<NamelessOneServant>> {
    private static final ResourceLocation NAMELESS_ONE_SERVANT_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/necromancer/nameless_one_servant.png");
    private static final ResourceLocation NAMELESS_ONE_GLOW_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/necromancer/nameless_one_glow.png");

    public NamelessOneServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn,
                new NamelessOneModel<NamelessOneServant>(
                        renderManagerIn
                                .bakeLayer(ClientEventHandler.NAMELESS_ONE_LAYER)),
                0.5F);
        this.addLayer(new NamelessOneServantEmissiveLayer(this, NAMELESS_ONE_GLOW_TEXTURE));
    }

    @Override
    protected void scale(NamelessOneServant necromancer, PoseStack matrixStackIn, float partialTickTime) {
        float original = 1.45F;
        float f1 = (float) necromancer.getNecroLevel();
        float size = original + Math.max(f1 * 0.15F, 0);
        matrixStackIn.scale(size, size, size);
    }

    @Override
    public void render(NamelessOneServant entity, float entityYaw, float partialTicks, PoseStack matrixStack,
            MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(NamelessOneServant entity) {
        return NAMELESS_ONE_SERVANT_TEXTURE;
    }
}