package com.k1sak1.goetyawaken.client.renderer.undead.necromancer;

import com.Polarice3.Goety.utils.ModelSnapshot;
import com.Polarice3.Goety.utils.ModelUtil;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.client.model.undead.necromancer.NamelessOneModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.client.renderer.ModRenderTypes;
import com.k1sak1.goetyawaken.client.renderer.layers.NamelessOneEmissiveLayer;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.NamelessOne;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NamelessOneRenderer
        extends MobRenderer<NamelessOne, NamelessOneModel<NamelessOne>> {
    private static final ResourceLocation NAMELESS_ONE_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/necromancer/nameless_one.png");
    private static final ResourceLocation NAMELESS_ONE_NEW_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/necromancer/false_king.png");
    private static final ResourceLocation NAMELESS_ONE_NEW_GLOW_TEXTURE = new ResourceLocation("goetyawaken",
            "textures/entity/undead/necromancer/nameless_king_glow.png");

    private static final ResourceLocation AFTERIMAGE_TEXTURE = NAMELESS_ONE_NEW_TEXTURE;
    private static final RenderType AFTERIMAGE_RENDER_TYPE = ModRenderTypes
            .entityTranslucentNoDepth(AFTERIMAGE_TEXTURE);

    private final NamelessOneModel<NamelessOne> shadowModel;

    public NamelessOneRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn,
                new NamelessOneModel<NamelessOne>(
                        renderManagerIn
                                .bakeLayer(ClientEventHandler.NAMELESS_ONE_LAYER)),
                0.5F);
        this.addLayer(new NamelessOneEmissiveLayer(this, getGlowTexture()));
        this.shadowModel = new NamelessOneModel<>(
                renderManagerIn.bakeLayer(ClientEventHandler.NAMELESS_ONE_SHADOW_LAYER));
    }

    private static ResourceLocation getTexture() {
        if (Config.ENABLE_HOSTILE_NAMELESS_ONE_NEW_TEXTURE.get()) {
            return NAMELESS_ONE_NEW_TEXTURE;
        }
        return NAMELESS_ONE_TEXTURE;
    }

    private static ResourceLocation getGlowTexture() {
        return NAMELESS_ONE_NEW_GLOW_TEXTURE;
    }

    @Override
    protected void scale(NamelessOne necromancer, PoseStack matrixStackIn, float partialTickTime) {
        float original = 1.45F;
        float f1 = (float) necromancer.getNecroLevel();
        float size = original + Math.max(f1 * 0.15F, 0);
        matrixStackIn.scale(size, size, size);
    }

    @Override
    public void render(NamelessOne entity, float entityYaw, float partialTicks, PoseStack matrixStack,
            MultiBufferSource buffer, int packedLight) {
        if (entity.isDeadOrDying()) {
            entityYaw = entity.deathRotation;
        }
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);

        if (entity.isAlive() && !entity.trailSnapshots.isEmpty()) {
            double currentX = Mth.lerp(partialTicks, entity.xo, entity.getX());
            double currentY = Mth.lerp(partialTicks, entity.yo, entity.getY());
            double currentZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());
            float currentTick = entity.tickCount + partialTicks;

            for (int i = 0; i < entity.trailSnapshots.size(); i++) {
                matrixStack.pushPose();

                Vec3 trailPos = entity.trailSnapshots.get(i).getFirst();
                ModelSnapshot snapshot = entity.trailSnapshots.get(i).getSecond();
                float lifespan = snapshot.xRot();

                if (!snapshot.poses().isEmpty()) {
                    ModelUtil.loadPoseFromSnapshot(snapshot.poses(), this.shadowModel::getAnyDescendantWithName);
                } else {
                    this.shadowModel.prepareMobModel(entity, 0.0F, 0.0F, partialTicks);
                    this.shadowModel.setupAnim(entity, 0.0F, 0.0F, currentTick, 0.0F, 0.0F);
                }

                matrixStack.translate(trailPos.x - currentX, trailPos.y - currentY, trailPos.z - currentZ);
                matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F - snapshot.yRot()));
                matrixStack.scale(-1.0F, -1.0F, 1.0F);
                this.scale(entity, matrixStack, partialTicks);
                matrixStack.translate(0.0F, -1.5F, 0.0F);

                float elapsed = currentTick - snapshot.timestamp();
                float modelAlpha = (1.0F - Mth.clamp(elapsed, 0, lifespan) / lifespan) * 0.35F;

                if (modelAlpha > 0) {
                    VertexConsumer vertexConsumer = buffer.getBuffer(AFTERIMAGE_RENDER_TYPE);
                    this.shadowModel.renderToBuffer(matrixStack, vertexConsumer, packedLight,
                            OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, modelAlpha);
                }

                matrixStack.popPose();
            }
        }
    }

    @Override
    public ResourceLocation getTextureLocation(NamelessOne entity) {
        return getTexture();
    }
}
