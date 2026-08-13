package com.k1sak1.goetyawaken.client.model.ally.Integration;

import com.k1sak1.goetyawaken.common.entities.ally.Integration.SludgeServant;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

//Based on https://github.com/KyaniteMods/DeeperAndDarker/tree/forge-1.20, Original by kyanite
public class SludgeServantModel extends HierarchicalModel<SludgeServant> {
    private final ModelPart root;

    public SludgeServantModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createInnerBodyModel() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition parts = mesh.getRoot();

        parts.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(0, 0)
                .texOffs(0, 16).addBox(-3.0F, -7.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(32, 0).addBox(-3.25F, -6.0F, -3.25F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(32, 4).addBox(1.25F, -6.0F, -3.25F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    public static LayerDefinition createOuterBodyModel() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition parts = mesh.getRoot();
        parts.addOrReplaceChild("cube",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.offset(0.0F, 24.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public @NotNull ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(@NotNull SludgeServant sludgeservant, float v, float v1, float v2, float v3, float v4) {

    }
}
