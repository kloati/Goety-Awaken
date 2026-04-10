package com.k1sak1.goetyawaken.client.model;

import com.k1sak1.goetyawaken.common.entities.ally.AngryMooshroom;
import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AngryMooshroomModel<T extends AngryMooshroom> extends CowModel<T> {

        public AngryMooshroomModel(ModelPart p_170578_) {
                super(p_170578_);
        }

        public static LayerDefinition createBodyLayer() {
                return CowModel.createBodyLayer();
        }

        @Override
        public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
                        float headPitch) {
                super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                float f = entity.getRammingXHeadRot();
                if (f != 0.0F) {
                        this.head.xRot = f;
                }
        }
}