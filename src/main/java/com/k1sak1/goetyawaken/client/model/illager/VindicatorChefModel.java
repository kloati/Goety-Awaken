package com.k1sak1.goetyawaken.client.model.illager;

import com.Polarice3.Goety.client.render.model.IllagerServantModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.model.AnimationUtils;

@OnlyIn(Dist.CLIENT)
public class VindicatorChefModel<T extends LivingEntity> extends IllagerServantModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new net.minecraft.resources.ResourceLocation("goety", "vindicator_chef"), "main");
    private final ModelPart chef_hat;

    public VindicatorChefModel(ModelPart root) {
        super(root);
        this.chef_hat = this.head.getChild("chef_hat");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = IllagerServantModel.createMesh();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.getChild("head");

        PartDefinition chef_hat = head.addOrReplaceChild("chef_hat",
                CubeListBuilder.create().texOffs(80, 0)
                        .addBox(-6.0F, -16.0F, -6.0F, 12.0F, 6.0F, 12.0F, new CubeDeformation(0.0F))
                        .texOffs(80, 18).addBox(-5.0F, -10.0F, -5.0F, 10.0F, 3.0F, 10.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = partdefinition.getChild("body");

        PartDefinition clothes = body.getChild("clothes");

        PartDefinition apron = clothes.addOrReplaceChild("apron", CubeListBuilder.create().texOffs(100, 31)
                .addBox(-4.0F, -24.0F, -3.0F, 8.0F, 18.0F, 6.0F, new CubeDeformation(0.75F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entity instanceof Vindicator vindicator) {
            boolean isAggressive = vindicator.isAggressive();
            boolean showCrossedArms = !isAggressive;
            this.arms.visible = showCrossedArms;
            this.LeftArm.visible = !showCrossedArms;
            this.RightArm.visible = !showCrossedArms;
            if (isAggressive) {
                if (vindicator.getMainHandItem().isEmpty()) {
                    AnimationUtils.animateZombieArms(this.LeftArm, this.RightArm, true, this.attackTime, ageInTicks);
                } else {
                    AnimationUtils.swingWeaponDown(this.RightArm, this.LeftArm, vindicator, this.attackTime,
                            ageInTicks);
                }
            }
        }

        ItemStack headItem = entity.getItemBySlot(EquipmentSlot.HEAD);
        this.chef_hat.visible = headItem.isEmpty() || headItem.is(ItemTags.BANNERS);
    }
}