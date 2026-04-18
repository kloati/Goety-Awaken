package com.k1sak1.goetyawaken.client.renderer.illager;

import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.entities.ally.illager.ArchIllusionerServant;
import com.k1sak1.goetyawaken.client.model.illager.IllusionerServantModel;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArchIllusionerServantRenderer
        extends MobRenderer<ArchIllusionerServant, IllusionerServantModel<ArchIllusionerServant>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/illager/arch_illusioner_servant.png");
    private static final ResourceLocation HOSTILE_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/illager/arch_illusioner.png");

    public ArchIllusionerServantRenderer(EntityRendererProvider.Context p_174186_) {
        super(p_174186_, new IllusionerServantModel<>(p_174186_.bakeLayer(IllusionerServantModel.LAYER_LOCATION)),
                0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, p_174186_));
        this.addLayer(
                new ItemInHandLayer<ArchIllusionerServant, IllusionerServantModel<ArchIllusionerServant>>(this,
                        p_174186_.getItemInHandRenderer()) {
                    public void render(PoseStack p_114989_, MultiBufferSource p_114990_, int p_114991_,
                            ArchIllusionerServant p_114992_,
                            float p_114993_, float p_114994_, float p_114995_, float p_114996_, float p_114997_,
                            float p_114998_) {
                        if (p_114992_.isCastingSpell() || p_114992_.isAggressive()) {
                            super.render(p_114989_, p_114990_, p_114991_, p_114992_, p_114993_, p_114994_, p_114995_,
                                    p_114996_,
                                    p_114997_, p_114998_);
                        }
                    }
                });
        this.model.getHat().visible = true;
    }

    @Override
    public ResourceLocation getTextureLocation(ArchIllusionerServant pEntity) {
        if (pEntity.isHostile()) {
            return HOSTILE_TEXTURE;
        }
        return TEXTURE;
    }

    public void render(ArchIllusionerServant pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack,
            MultiBufferSource pBuffer, int pPackedLight) {
        if (pEntity.isInvisible()) {
            Vec3[] avec3 = pEntity.getIllusionOffsets(pPartialTicks);
            float f = this.getBob(pEntity, pPartialTicks);

            for (int i = 0; i < avec3.length; ++i) {
                pPoseStack.pushPose();
                pPoseStack.translate(avec3[i].x + (double) Mth.cos((float) i + f * 0.5F) * 0.025D,
                        avec3[i].y + (double) Mth.cos((float) i + f * 0.75F) * 0.0125D,
                        avec3[i].z + (double) Mth.cos((float) i + f * 0.7F) * 0.025D);
                super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
                pPoseStack.popPose();
            }
        } else {
            super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
        }
    }

    protected boolean isBodyVisible(ArchIllusionerServant pLivingEntity) {
        return true;
    }
}