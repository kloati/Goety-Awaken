package com.k1sak1.goetyawaken.client.model;

import com.k1sak1.goetyawaken.common.entities.ally.EndermanServant;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndermanServantModel extends EndermanModel<EndermanServant> {
    public boolean carrying;
    public boolean creepy;

    public EndermanServantModel(ModelPart pRoot) {
        super(pRoot);
    }

    public static LayerDefinition createBodyLayer() {
        return EndermanModel.createBodyLayer();
    }

    @Override
    public void setupAnim(EndermanServant entity, float limbSwing, float limbSwingAmount, float ageInTicks,
            float netHeadYaw, float headPitch) {
        this.carrying = !entity.getCarriedItem().isEmpty();
        this.creepy = entity.isCreepy();
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (this.carrying) {
            this.rightArm.xRot = -0.5F;
            this.leftArm.xRot = -0.5F;
            this.rightArm.zRot = 0.05F;
            this.leftArm.zRot = -0.05F;
        }
        if (this.creepy) {
            this.head.y -= 5.0F;
        }
    }
}