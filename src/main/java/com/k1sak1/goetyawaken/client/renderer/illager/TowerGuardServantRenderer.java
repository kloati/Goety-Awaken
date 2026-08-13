package com.k1sak1.goetyawaken.client.renderer.illager;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.client.model.TowerGuardModel;
import com.k1sak1.goetyawaken.client.renderer.ModRenderTypes;
import com.k1sak1.goetyawaken.common.entities.ally.illager.TowerGuardServant;
import com.Polarice3.Goety.utils.ModelPartPose;
import com.Polarice3.Goety.utils.ModelSnapshot;
import com.Polarice3.Goety.utils.ModelUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

public class TowerGuardServantRenderer extends MobRenderer<TowerGuardServant, TowerGuardModel<TowerGuardServant>> {
    private static final ResourceLocation TEXTURE = GoetyAwaken
            .location("textures/entity/illager/tower_guard_servant.png");
    private static final RenderType TRAIL_RENDER_TYPE = ModRenderTypes.entityTranslucentNoDepth(TEXTURE);
    private static final float SNAPSHOT_INTERVAL = 2.0F;
    private static final float SNAPSHOT_LIFESPAN = 10;

    private final TowerGuardModel<TowerGuardServant> shadowModel;

    public TowerGuardServantRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new TowerGuardModel<>(ctx.bakeLayer(ClientEventHandler.TOWER_GUARD_LAYER)), 0.5F);
        this.addLayer(new CustomHeadLayer<>(this, ctx.getModelSet(), ctx.getItemInHandRenderer()));
        this.addLayer(new TowerGuardGlowLayer<>(this));
        this.shadowModel = new TowerGuardModel<>(ctx.bakeLayer(ClientEventHandler.TOWER_GUARD_LAYER));
    }

    @Override
    public void render(TowerGuardServant entity, float entityYaw, float partialTicks, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        if (entity.isAlive()) {
            double currentX = Mth.lerp(partialTicks, entity.xo, entity.getX());
            double currentY = Mth.lerp(partialTicks, entity.yo, entity.getY());
            double currentZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());
            float currentTick = getBob(entity, partialTicks);

            if (entity.trailSnapshots.isEmpty() || currentTick - entity.lastTrailTick > SNAPSHOT_INTERVAL) {
                if (entity.shouldAddTrailSnapshot()) {
                    Map<String, ModelPartPose> snapshot = ModelUtil.saveModelSnapshot(
                            this.getModel().allPartNames,
                            this.getModel()::getAnyDescendantWithName);
                    entity.trailSnapshots.add(0, Pair.of(
                            new Vec3(currentX, currentY, currentZ),
                            new ModelSnapshot(0,
                                    Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot),
                                    currentTick,
                                    snapshot)));
                    entity.lastTrailTick = currentTick;
                }
                entity.trailSnapshots.removeIf(p -> currentTick - p.getSecond().timestamp() > SNAPSHOT_LIFESPAN);
                while (entity.trailSnapshots.size() > 10) {
                    entity.trailSnapshots.remove(entity.trailSnapshots.size() - 1);
                }
            }

            for (int i = 0; i < entity.trailSnapshots.size(); i++) {
                poseStack.pushPose();
                Vec3 trailPos = entity.trailSnapshots.get(i).getFirst();
                ModelSnapshot snapshot = entity.trailSnapshots.get(i).getSecond();
                ModelUtil.loadPoseFromSnapshot(snapshot.poses(), this.shadowModel::getAnyDescendantWithName);
                poseStack.translate(trailPos.x - currentX, trailPos.y - currentY, trailPos.z - currentZ);
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - snapshot.yRot()));
                poseStack.scale(-1.0F, -1.0F, 1.0F);
                this.scale(entity, poseStack, partialTicks);
                poseStack.translate(0.0F, -1.5F, 0.0F);
                float modelAlpha = (1 - Mth.clamp(currentTick - snapshot.timestamp(), 0, SNAPSHOT_LIFESPAN)
                        / SNAPSHOT_LIFESPAN) * 0.35F;
                if (modelAlpha > 0) {
                    VertexConsumer vertexConsumer = buffer.getBuffer(TRAIL_RENDER_TYPE);
                    this.shadowModel.renderToBuffer(poseStack, vertexConsumer, packedLight,
                            OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, modelAlpha);
                }
                poseStack.popPose();
            }
        }
    }

    @Override
    public ResourceLocation getTextureLocation(TowerGuardServant entity) {
        return TEXTURE;
    }
}
