package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.entities.ally.ToxifinServant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.ElderGuardian;

public class PlaguewhaleSlabServantRenderer extends ToxifinServantRenderer {
   public static final ResourceLocation TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
         "textures/entity/plaguewhale.png");
   public static final ResourceLocation HOSTILE_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
         "textures/entity/plaguewhale_origin.png");

   public PlaguewhaleSlabServantRenderer(EntityRendererProvider.Context renderContext,
         ModelLayerLocation modelLayer) {
      super(renderContext, 1.2F, modelLayer);
   }

   @Override
   public ResourceLocation getTextureLocation(ToxifinServant entity) {
      if (entity.isHostile()) {
         return HOSTILE_TEXTURE;
      } else {
         return TEXTURE;
      }
   }

   protected void scale(ToxifinServant entity, PoseStack poseStack, float partialTick) {
      poseStack.scale(ElderGuardian.ELDER_SIZE_SCALE, ElderGuardian.ELDER_SIZE_SCALE, ElderGuardian.ELDER_SIZE_SCALE);
   }
}
