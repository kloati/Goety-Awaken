package com.k1sak1.goetyawaken.client.model.ally.Integration;

import com.k1sak1.goetyawaken.common.entities.ally.Integration.MasqueraderServant;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//Based on https://github.com/TheDarkPeasant/The-Masquerade, Original by TheDarkPeasant
@OnlyIn(Dist.CLIENT)
public class MasqueraderServantModel<T extends MasqueraderServant> extends HierarchicalModel<T>
        implements ArmedModel, HeadedModel {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation("goetyawaken", "masquerader_servant"), "main");

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart bone;
    private final ModelPart head;
    private final ModelPart arms;
    private final ModelPart cape;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart rightArm;
    private final ModelPart leftArm;

    public MasqueraderServantModel(ModelPart root) {
        this.root = root;
        this.body = this.root.getChild("body");
        this.bone = this.body.getChild("bone");
        this.head = this.root.getChild("head");

        this.cape = this.bone.getChild("cape");
        this.arms = this.body.getChild("arms");
        this.leftLeg = this.body.getChild("leg1");
        this.rightLeg = this.body.getChild("leg0");
        this.leftArm = this.body.getChild("LeftArm");
        this.rightArm = this.body.getChild("RightArm");
    }

    @SuppressWarnings("unchecked")
    public static LayerDefinition createBodyLayer() {
        try {
            Class<?> masqueraderModelClass = Class.forName(
                    "net.random_something.masquerader_mod.client.model.AlternateMasqueraderModel");
            java.lang.reflect.Method method = masqueraderModelClass.getMethod("createBodyLayer");
            return (LayerDefinition) method.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Masquerader model body layer via reflection", e);
        }
    }

    private static AnimationDefinition getMasqueraderAnimation(String name) {
        try {
            Class<?> animClass = Class.forName(
                    "net.random_something.masquerader_mod.client.animation.MasqueraderAnimation");
            java.lang.reflect.Field field = animClass.getField(name);
            return (AnimationDefinition) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get Masquerader animation: " + name, e);
        }
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
            float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        ModelPart var10000;
        this.animate(entity.getAnimationState("roar"), getMasqueraderAnimation("ROAR"), ageInTicks,
                entity.getAnimationSpeed());
        this.animate(entity.getAnimationState("potion"), getMasqueraderAnimation("POTION"), ageInTicks,
                entity.getAnimationSpeed());
        this.animate(entity.getAnimationState("change"), getMasqueraderAnimation("CHANGE"), ageInTicks,
                entity.getAnimationSpeed());
        this.animate(entity.getAnimationState("crossbow"), getMasqueraderAnimation("CROSSBOW"), ageInTicks,
                entity.getAnimationSpeed());
        this.animate(entity.getAnimationState("fangs"), getMasqueraderAnimation("FANGS"), ageInTicks,
                entity.getAnimationSpeed());
        this.animate(entity.getAnimationState("death"), getMasqueraderAnimation("DEATH"), ageInTicks,
                entity.getAnimationSpeed());
        this.animate(entity.getAnimationState("vex"), getMasqueraderAnimation("VEX"), ageInTicks,
                entity.getAnimationSpeed());

        this.cape.xRot = 0.1F + limbSwingAmount * 0.6F;

        AbstractIllagerServant.IllagerServantArmPose armPose = entity.getArmPose();
        if (armPose == AbstractIllagerServant.IllagerServantArmPose.ATTACKING) {
            if (entity.getMainHandItem().isEmpty()) {
                AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, true, this.attackTime, ageInTicks);
            } else {
                AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, entity, this.attackTime,
                        ageInTicks);
            }
        } else if (armPose == AbstractIllagerServant.IllagerServantArmPose.SPELLCASTING) {
            this.rightArm.z = 0.0F;
            this.rightArm.x = -5.0F;
            this.leftArm.z = 0.0F;
            this.leftArm.x = 5.0F;
            this.rightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
            this.leftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
            this.rightArm.zRot = 2.3561945F;
            this.leftArm.zRot = -2.3561945F;
            this.rightArm.yRot = 0.0F;
            this.leftArm.yRot = 0.0F;
        } else if (armPose == AbstractIllagerServant.IllagerServantArmPose.BOW_AND_ARROW) {
            this.rightArm.yRot = -0.1F + this.head.yRot;
            this.rightArm.xRot = (-(float) Math.PI / 2F) + this.head.xRot;
            this.leftArm.xRot = -0.9424779F + this.head.xRot;
            this.leftArm.yRot = this.head.yRot - 0.4F;
            this.leftArm.zRot = ((float) Math.PI / 2F);
        } else if (armPose == AbstractIllagerServant.IllagerServantArmPose.CROSSBOW_HOLD) {
            AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
        } else if (armPose == AbstractIllagerServant.IllagerServantArmPose.CROSSBOW_CHARGE) {
            AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, ((AbstractIllagerServant) entity),
                    true);
        } else if (armPose == AbstractIllagerServant.IllagerServantArmPose.CELEBRATING) {
            this.rightArm.z = 0.0F;
            this.rightArm.x = -5.0F;
            this.rightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.05F;
            this.rightArm.zRot = 2.670354F;
            this.rightArm.yRot = 0.0F;
            this.leftArm.z = 0.0F;
            this.leftArm.x = 5.0F;
            this.leftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.05F;
            this.leftArm.zRot = -2.3561945F;
            this.leftArm.yRot = 0.0F;
        }

        boolean flag = entity.shouldShowArms();
        this.arms.visible = !flag;
        this.leftArm.visible = flag;
        this.rightArm.visible = flag;

        var10000 = this.head;
        var10000.yRot += netHeadYaw * 0.017453292F;
        var10000.xRot += headPitch * 0.017453292F;

        if (this.riding) {
            this.leftLeg.xRot = -1.4137167F;
            this.leftLeg.yRot = -0.31415927F;
            this.leftLeg.zRot = -0.07853982F;
            this.rightLeg.xRot = -1.4137167F;
            this.rightLeg.yRot = 0.31415927F;
            this.rightLeg.zRot = 0.07853982F;
        } else if (!entity.isCharging()) {
            var10000 = this.rightLeg;
            var10000.xRot += Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
            var10000 = this.leftLeg;
            var10000.xRot += Mth.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount * 0.5F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    private ModelPart getArm(HumanoidArm p_191216_1_) {
        return p_191216_1_ == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
    }

    @Override
    public void translateToHand(HumanoidArm p_102108_, PoseStack p_102109_) {
        this.root().translateAndRotate(p_102109_);
        this.body.translateAndRotate(p_102109_);
        this.getArm(p_102108_).translateAndRotate(p_102109_);
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }
}
