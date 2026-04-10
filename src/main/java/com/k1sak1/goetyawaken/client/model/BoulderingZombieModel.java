package com.k1sak1.goetyawaken.client.model;

import com.k1sak1.goetyawaken.common.entities.hostile.undead.zombie.BoulderingZombie;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

public class BoulderingZombieModel<T extends BoulderingZombie> extends HumanoidModel<T> {
    public static final net.minecraft.client.model.geom.ModelLayerLocation LAYER_LOCATION = new net.minecraft.client.model.geom.ModelLayerLocation(
            new ResourceLocation("goetyawaken", "bouldering_zombie"), "main");

    public BoulderingZombieModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        return LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F), 64, 64);
    }

    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw,
            float pHeadPitch) {
        super.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, this.isAggressive(pEntity), this.attackTime,
                pAgeInTicks);
    }

    public boolean isAggressive(T entityIn) {
        return entityIn.isAggressive();
    }

}