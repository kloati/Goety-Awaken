package com.k1sak1.goetyawaken.client.renderer.illager;

import com.k1sak1.goetyawaken.client.model.illager.CroneServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.illager.CroneServant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import com.Polarice3.Goety.common.items.brew.BrewItem;

public class CroneServantRenderer extends MobRenderer<CroneServant, CroneServantModel<CroneServant>> {
    private static final ResourceLocation CRONE_SERVANT = new ResourceLocation("goetyawaken",
            "textures/entity/illager/crone_servant.png");

    public CroneServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn,
                new CroneServantModel<>(
                        renderManagerIn
                                .bakeLayer(com.k1sak1.goetyawaken.client.ClientEventHandler.CRONE_SERVANT_LAYER)),
                0.5F);
        this.addLayer(
                new CustomHeadLayer<>(this, renderManagerIn.getModelSet(), renderManagerIn.getItemInHandRenderer()));
        this.addLayer(new ModWitchItemLayer(this, renderManagerIn.getItemInHandRenderer()));
    }

    public static class ModWitchItemLayer extends
            net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer<CroneServant, CroneServantModel<CroneServant>> {
        public ModWitchItemLayer(
                net.minecraft.client.renderer.entity.RenderLayerParent<CroneServant, CroneServantModel<CroneServant>> p_234926_,
                ItemInHandRenderer p_234927_) {
            super(p_234926_, p_234927_);
        }

        public void render(PoseStack p_117685_, MultiBufferSource p_117686_, int p_117687_, CroneServant p_117688_,
                float p_117689_, float p_117690_, float p_117691_, float p_117692_, float p_117693_, float p_117694_) {
            ItemStack itemstack = p_117688_.getMainHandItem();
            p_117685_.pushPose();
            if (itemstack.is(Items.POTION)
                    || itemstack.getItem() instanceof com.Polarice3.Goety.common.items.brew.BrewItem) {
                this.getParentModel().getHead().translateAndRotate(p_117685_);
                this.getParentModel().getNose().translateAndRotate(p_117685_);
                p_117685_.translate(0.0625F, 0.25F, 0.0F);
                p_117685_.mulPose(Axis.ZP.rotationDegrees(180.0F));
                p_117685_.mulPose(Axis.XP.rotationDegrees(140.0F));
                p_117685_.mulPose(Axis.ZP.rotationDegrees(10.0F));
                p_117685_.translate(0.0F, -0.4F, 0.4F);
            }

            super.render(p_117685_, p_117686_, p_117687_, p_117688_, p_117689_, p_117690_, p_117691_, p_117692_,
                    p_117693_, p_117694_);
            p_117685_.popPose();
        }
    }

    @Override
    public void render(CroneServant entity, float entityYaw, float partialTicks, PoseStack matrixStack,
            net.minecraft.client.renderer.MultiBufferSource buffer, int packedLight) {
        this.model.setHoldingItem(!entity.getMainHandItem().isEmpty()
                && (entity.getMainHandItem().getItem() instanceof BrewItem
                        || entity.getMainHandItem().getItem() instanceof PotionItem));
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    protected void scale(CroneServant entity, PoseStack matrixStackIn, float partialTickTime) {
        matrixStackIn.scale(1.0F, 1.0F, 1.0F);
    }

    public ResourceLocation getTextureLocation(CroneServant entity) {
        return CRONE_SERVANT;
    }
}