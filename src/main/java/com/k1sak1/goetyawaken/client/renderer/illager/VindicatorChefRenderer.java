package com.k1sak1.goetyawaken.client.renderer.illager;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.illager.VindicatorChefModel;
import com.k1sak1.goetyawaken.common.entities.hostile.illager.VindicatorChef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class VindicatorChefRenderer extends MobRenderer<VindicatorChef, VindicatorChefModel<VindicatorChef>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("goety",
            "textures/entity/servants/illager/vindicator_chef_original.png");

    public VindicatorChefRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn,
                new VindicatorChefModel<>(renderManagerIn.bakeLayer(VindicatorChefModel.LAYER_LOCATION)),
                0.5F);
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()) {
            public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn,
                    VindicatorChef illager, float limbSwing, float limbSwingAmount, float partialTicks,
                    float ageInTicks, float netHeadYaw, float headPitch) {
                if (illager.isAggressive()) {
                    super.render(matrixStackIn, bufferIn, packedLightIn, illager, limbSwing, limbSwingAmount,
                            partialTicks, ageInTicks, netHeadYaw, headPitch);
                }
            }

            @Override
            protected void renderArmWithItem(LivingEntity p_117185_, ItemStack p_117186_, ItemDisplayContext p_270970_,
                    HumanoidArm p_117188_, PoseStack p_117189_,
                    net.minecraft.client.renderer.MultiBufferSource p_117190_, int p_117191_) {
                if (p_117186_.getItem() instanceof AxeItem) {
                    p_117186_ = new ItemStack(net.minecraftforge.registries.ForgeRegistries.ITEMS
                            .getValue(new ResourceLocation("goety", "cooking_ladle")));
                }
                super.renderArmWithItem(p_117185_, p_117186_, p_270970_, p_117188_, p_117189_, p_117190_, p_117191_);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(VindicatorChef entity) {
        return TEXTURE;
    }
}