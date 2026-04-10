package com.k1sak1.goetyawaken.client.renderer.ally.undead.skeleton;

import com.k1sak1.goetyawaken.client.model.undead.necromancer.WraithNecromancerModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer.WraithNecromancerServant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WraithNecromancerServantRenderer
                extends MobRenderer<WraithNecromancerServant, WraithNecromancerModel<WraithNecromancerServant>> {
        private static final ResourceLocation WRAITH_NECROMANCER_SERVANT_TEXTURE = new ResourceLocation("goetyawaken",
                        "textures/entity/undead/necromancer/wraith_necromancer_servant.png");
        private static final ResourceLocation WRAITH_NECROMANCER_SERVANT_GLOW_TEXTURE = new ResourceLocation(
                        "goetyawaken",
                        "textures/entity/undead/necromancer/wraith_necromancer_servant_glow.png");

        public WraithNecromancerServantRenderer(EntityRendererProvider.Context renderManagerIn) {
                super(renderManagerIn,
                                new WraithNecromancerModel<WraithNecromancerServant>(
                                                renderManagerIn
                                                                .bakeLayer(ClientEventHandler.WRAITH_NECROMANCER_LAYER)),
                                0.5F);
                this.addLayer(new com.k1sak1.goetyawaken.client.renderer.layers.WraithNecromancerServantEmissiveLayer(
                                this,
                                WRAITH_NECROMANCER_SERVANT_GLOW_TEXTURE));
        }

        @Override
        protected void scale(WraithNecromancerServant necromancer, PoseStack matrixStackIn, float partialTickTime) {
                float original = 1.45F;
                float f1 = (float) necromancer.getNecroLevel();
                float size = original + Math.max(f1 * 0.15F, 0);
                matrixStackIn.scale(size, size, size);
        }

        @Override
        public void render(WraithNecromancerServant entity, float entityYaw, float partialTicks, PoseStack matrixStack,
                        MultiBufferSource buffer, int packedLight) {
                super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
        }

        @Override
        public ResourceLocation getTextureLocation(WraithNecromancerServant entity) {
                return WRAITH_NECROMANCER_SERVANT_TEXTURE;
        }
}