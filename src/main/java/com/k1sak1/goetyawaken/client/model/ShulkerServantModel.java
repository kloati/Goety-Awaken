package com.k1sak1.goetyawaken.client.model;

import com.google.common.collect.ImmutableList;
import com.k1sak1.goetyawaken.common.entities.ally.ShulkerServant;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShulkerServantModel extends ListModel<ShulkerServant> {
    private static final String LID = "lid";
    private static final String BASE = "base";
    private final ModelPart base;
    private final ModelPart lid;
    private final ModelPart head;

    public ShulkerServantModel(ModelPart pRoot) {
        super(RenderType::entityCutoutNoCullZOffset);
        this.lid = pRoot.getChild("lid");
        this.base = pRoot.getChild("base");
        this.head = pRoot.getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("lid",
                CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 12.0F, 16.0F),
                PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("base",
                CubeListBuilder.create().texOffs(0, 28).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 8.0F, 16.0F),
                PartPose.offset(0.0F, 24.0F, 0.0F));
        partdefinition.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 52).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 6.0F, 6.0F),
                PartPose.offset(0.0F, 12.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(ShulkerServant entity, float limbSwing, float limbSwingAmount, float ageInTicks,
            float netHeadYaw, float headPitch) {
        if (entity != null) {
            float f = ageInTicks - (float) entity.tickCount;
            float f1 = (0.5F + entity.getClientPeekAmount(f)) * (float) Math.PI;
            float f2 = -1.0F + Mth.sin(f1);
            float f3 = 0.0F;
            if (f1 > (float) Math.PI) {
                f3 = Mth.sin(ageInTicks * 0.1F) * 0.7F;
            }

            this.lid.setPos(0.0F, 16.0F + Mth.sin(f1) * 8.0F + f3, 0.0F);
            if (entity.getClientPeekAmount(f) > 0.3F) {
                this.lid.yRot = f2 * f2 * f2 * f2 * (float) Math.PI * 0.125F;
            } else {
                this.lid.yRot = 0.0F;
            }

            this.head.xRot = headPitch * ((float) Math.PI / 180F);
            this.head.yRot = (entity.yHeadRot - 180.0F - entity.yBodyRot) * ((float) Math.PI / 180F);
        }
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(this.base, this.lid);
    }

    public ModelPart getLid() {
        return this.lid;
    }

    public ModelPart getHead() {
        return this.head;
    }
}