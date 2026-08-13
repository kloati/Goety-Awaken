package com.k1sak1.goetyawaken.client.renderer.illager;

import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.Polarice3.Goety.utils.ModelPartPose;
import com.Polarice3.Goety.utils.ModelSnapshot;
import com.Polarice3.Goety.utils.ModelUtil;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.client.renderer.ModRenderTypes;
import com.k1sak1.goetyawaken.common.entities.hostile.illager.ArchIllusioner;
import com.k1sak1.goetyawaken.client.model.illager.IllusionerServantModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ArchIllusionerRenderer
        extends MobRenderer<ArchIllusioner, IllusionerServantModel<ArchIllusioner>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/illager/arch_illusioner.png");

    private static final RenderType AFTERIMAGE_RENDER_TYPE = ModRenderTypes.entityTranslucentNoDepth(TEXTURE);

    private static final float SNAPSHOT_INTERVAL = 4.0F;
    private static final float SNAPSHOT_LIFESPAN = 6.0F;

    private final IllusionerServantModel<ArchIllusioner> shadowModel;

    public ArchIllusionerRenderer(EntityRendererProvider.Context p_174186_) {
        super(p_174186_, new IllusionerServantModel<>(p_174186_.bakeLayer(IllusionerServantModel.LAYER_LOCATION)),
                0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, p_174186_));
        this.addLayer(
                new ItemInHandLayer<ArchIllusioner, IllusionerServantModel<ArchIllusioner>>(this,
                        p_174186_.getItemInHandRenderer()) {
                    public void render(PoseStack p_114989_, MultiBufferSource p_114990_, int p_114991_,
                            ArchIllusioner p_114992_,
                            float p_114993_, float p_114994_, float p_114995_, float p_114996_, float p_114997_,
                            float p_114998_) {
                        if (p_114992_.isCastingSpell() || p_114992_.isAggressive()) {
                            super.render(p_114989_, p_114990_, p_114991_, p_114992_, p_114993_, p_114994_, p_114995_,
                                    p_114996_,
                                    p_114997_, p_114998_);
                        }
                    }
                });
        this.model.getHat().visible = true;
        this.shadowModel = new IllusionerServantModel<>(
                p_174186_.bakeLayer(ClientEventHandler.ARCH_ILLUSIONER_SERVANT_SHADOW_LAYER));
    }

    @Override
    public ResourceLocation getTextureLocation(ArchIllusioner pEntity) {
        return TEXTURE;
    }

    public void render(ArchIllusioner pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack,
            MultiBufferSource pBuffer, int pPackedLight) {
        if (pEntity.isInvisible()) {
            Vec3[] avec3 = pEntity.getIllusionOffsets(pPartialTicks);
            float f = this.getBob(pEntity, pPartialTicks);

            for (int i = 0; i < avec3.length; ++i) {
                pPoseStack.pushPose();
                pPoseStack.translate(avec3[i].x + (double) Mth.cos((float) i + f * 0.5F) * 0.025D,
                        avec3[i].y + (double) Mth.cos((float) i + f * 0.75F) * 0.0125D,
                        avec3[i].z + (double) Mth.cos((float) i + f * 0.7F) * 0.025D);
                super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
                pPoseStack.popPose();
            }
        } else {
            super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
        }

        if (pEntity.isAlive()) {
            double currentX = Mth.lerp(pPartialTicks, pEntity.xo, pEntity.getX());
            double currentY = Mth.lerp(pPartialTicks, pEntity.yo, pEntity.getY());
            double currentZ = Mth.lerp(pPartialTicks, pEntity.zo, pEntity.getZ());
            float currentTick = getBob(pEntity, pPartialTicks);

            if (pEntity.trailSnapshots.isEmpty() || currentTick - pEntity.lastTrailTick > SNAPSHOT_INTERVAL) {
                if (pEntity.shouldAddTrailSnapshot()) {
                    Map<String, ModelPartPose> snapshot = ModelUtil.saveModelSnapshot(
                            this.getModel().allPartNames,
                            this.getModel()::getAnyDescendantWithName);
                    pEntity.trailSnapshots.add(0, Pair.of(
                            new Vec3(currentX, currentY, currentZ),
                            new ModelSnapshot(
                                    0,
                                    Mth.rotLerp(pPartialTicks, pEntity.yBodyRotO, pEntity.yBodyRot),
                                    currentTick,
                                    snapshot)));
                    pEntity.lastTrailTick = currentTick;
                }

                pEntity.trailSnapshots.removeIf(p -> currentTick - p.getSecond().timestamp() > SNAPSHOT_LIFESPAN);

                while (pEntity.trailSnapshots.size() > 32) {
                    pEntity.trailSnapshots.remove(pEntity.trailSnapshots.size() - 1);
                }
            }

            for (int i = 0; i < pEntity.trailSnapshots.size(); i++) {
                pPoseStack.pushPose();

                Vec3 trailPos = pEntity.trailSnapshots.get(i).getFirst();
                ModelSnapshot snapshot = pEntity.trailSnapshots.get(i).getSecond();

                ModelUtil.loadPoseFromSnapshot(snapshot.poses(), this.shadowModel::getAnyDescendantWithName);

                pPoseStack.translate(trailPos.x - currentX, trailPos.y - currentY, trailPos.z - currentZ);

                pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F - snapshot.yRot()));

                pPoseStack.scale(-1.0F, -1.0F, 1.0F);

                this.scale(pEntity, pPoseStack, pPartialTicks);
                pPoseStack.translate(0.0F, -1.5F, 0.0F);

                float modelAlpha = (1
                        - Mth.clamp(currentTick - snapshot.timestamp(), 0, SNAPSHOT_LIFESPAN) / SNAPSHOT_LIFESPAN)
                        * 0.35F;

                if (modelAlpha > 0) {
                    VertexConsumer vertexConsumer = pBuffer.getBuffer(AFTERIMAGE_RENDER_TYPE);
                    this.shadowModel.renderToBuffer(pPoseStack, vertexConsumer, pPackedLight, OverlayTexture.NO_OVERLAY,
                            1.0F, 1.0F, 1.0F, modelAlpha);
                }

                pPoseStack.popPose();
            }
        }
    }

    protected boolean isBodyVisible(ArchIllusioner pLivingEntity) {
        return true;
    }
}
