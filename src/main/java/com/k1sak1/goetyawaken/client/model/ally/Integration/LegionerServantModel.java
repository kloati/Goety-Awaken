package com.k1sak1.goetyawaken.client.model.ally.Integration;

import com.Polarice3.Goety.client.render.model.IllagerServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.LegionerServant;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//Based on https://github.com/izofar/takes-a-pillage, Original by izofar
@OnlyIn(Dist.CLIENT)
public class LegionerServantModel extends IllagerServantModel<LegionerServant> {

    private final ModelPart rightArm;
    private final ModelPart leftArm;

    public LegionerServantModel(ModelPart root) {
        super(root);
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
    }

    @Override
    public void setupAnim(LegionerServant legioner, float limbSwing, float limbSwingAmount, float ageInTicks,
            float netHeadYaw,
            float headPitch) {
        super.setupAnim(legioner, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (legioner.isAlive() && legioner.isUsingShield()) {
            boolean flag = legioner.getMainArm() == HumanoidArm.RIGHT;
            if ((legioner.getShieldHand() == InteractionHand.MAIN_HAND) == flag) {
                this.poseRightArmShield();
            } else if ((legioner.getShieldHand() == InteractionHand.OFF_HAND) == flag) {
                this.poseLeftArmShield();
            }
        }
    }

    private void poseRightArmShield() {
        this.rightArm.xRot = this.rightArm.xRot * 0.5F - 0.9424779F;
        this.rightArm.yRot = (-(float) Math.PI / 6F);
    }

    private void poseLeftArmShield() {
        this.leftArm.xRot = this.leftArm.xRot * 0.5F - 0.9424779F;
        this.leftArm.yRot = ((float) Math.PI / 6F);
    }

}
