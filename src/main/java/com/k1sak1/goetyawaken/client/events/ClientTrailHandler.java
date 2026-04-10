package com.k1sak1.goetyawaken.client.events;

import com.k1sak1.goetyawaken.client.renderer.TrailRenderPipeline;
import com.k1sak1.goetyawaken.client.renderer.trail.TrailPosition;
import com.k1sak1.goetyawaken.common.entities.projectiles.ExplosiveArrow;
import com.k1sak1.goetyawaken.common.entities.projectiles.ModSwordProjectile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.WeakHashMap;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientTrailHandler {
    public static TrailRenderPipeline.TrailTaskManager explosiveArrowTrailManager;
    public static TrailRenderPipeline.TrailTaskManager swordProjectileTrailManager;
    private static final WeakHashMap<ExplosiveArrow, Boolean> registeredArrows = new WeakHashMap<>();

    @SubscribeEvent
    public static void drawTrails(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            Minecraft mc = Minecraft.getInstance();
            ProfilerFiller profilerFiller = mc.getProfiler();
            LightTexture lightTexture = mc.gameRenderer.lightTexture();
            double camX = event.getCamera().getPosition().x;
            double camY = event.getCamera().getPosition().y;
            double camZ = event.getCamera().getPosition().z;

            if (explosiveArrowTrailManager == null) {
                explosiveArrowTrailManager = TrailRenderPipeline.TrailTaskManager.create();
            }
            if (swordProjectileTrailManager == null) {
                swordProjectileTrailManager = TrailRenderPipeline.TrailTaskManager.create();
            }

            lightTexture.turnOnLightLayer();
            lightTexture.updateLightTexture(mc.getPartialTick());
            profilerFiller.push("explosive_arrow_trail");
            event.getPoseStack().pushPose();
            event.getPoseStack().translate(-camX, -camY, -camZ);
            if (mc.level != null) {
                for (Entity entity : mc.level.entitiesForRendering()) {
                    if (entity instanceof ExplosiveArrow arrow && !arrow.isRemoved()) {
                        updateArrowTrailPoints(arrow, event.getPartialTick());

                        if (!arrow.getPublicTrailPoints().isEmpty()) {
                            explosiveArrowTrailManager.queueTrailTask(new ExplosiveArrowTrailTask(arrow));
                        }
                    }
                    if (entity instanceof ModSwordProjectile sword && !sword.isRemoved()) {
                        updateSwordTrailPoints(sword, event.getPartialTick());

                        if (!sword.getPublicTrailPoints().isEmpty()) {
                            swordProjectileTrailManager.queueTrailTask(new ModSwordTrailTask(sword));
                        }
                    }
                }
                explosiveArrowTrailManager.executeTrailRendering(event.getPoseStack());
                swordProjectileTrailManager.executeTrailRendering(event.getPoseStack());
            }

            event.getPoseStack().popPose();
            profilerFiller.pop();
            lightTexture.turnOffLightLayer();
        }
    }

    private static void updateArrowTrailPoints(ExplosiveArrow arrow, float partialTicks) {
        if (arrow.getPublicTrailPoints().isEmpty()) {
            return;
        }

        if (!arrow.hasTrail()) {
            return;
        }

        synchronized (arrow.getPublicTrailPoints()) {
            double x = net.minecraft.util.Mth.lerp(partialTicks, arrow.xOld, arrow.getX());
            double y = net.minecraft.util.Mth.lerp(partialTicks, arrow.yOld, arrow.getY());
            double z = net.minecraft.util.Mth.lerp(partialTicks, arrow.zOld, arrow.getZ());
            arrow.getPublicTrailPoints().set(0, new TrailPosition(
                    new net.minecraft.world.phys.Vec3(x, y, z), 0));
            for (int i = arrow.getPublicTrailPoints().size() - 1; i >= 1; i--) {
                TrailPosition point = arrow.getPublicTrailPoints().get(i);
                if (point.getPosition().distanceToSqr(arrow.getPublicTrailPoints().get(i - 1).getPosition()) < 4) {
                    arrow.getPublicTrailPoints().set(i,
                            point.interpolate(arrow.getPublicTrailPoints().get(i - 1), partialTicks));
                } else {
                    arrow.getPublicTrailPoints().set(i, new TrailPosition(
                            arrow.getPublicTrailPoints().get(i - 1).getPosition()));
                }
            }
        }
    }

    private static void updateSwordTrailPoints(ModSwordProjectile sword, float partialTicks) {
        if (sword.getPublicTrailPoints().isEmpty()) {
            return;
        }

        if (!sword.hasTrail()) {
            return;
        }

        synchronized (sword.getPublicTrailPoints()) {
            double x = net.minecraft.util.Mth.lerp(partialTicks, sword.xOld, sword.getX());
            double y = net.minecraft.util.Mth.lerp(partialTicks, sword.yOld, sword.getY());
            double z = net.minecraft.util.Mth.lerp(partialTicks, sword.zOld, sword.getZ());
            sword.getPublicTrailPoints().set(0, new TrailPosition(
                    new net.minecraft.world.phys.Vec3(x, y, z), 0));
            for (int i = sword.getPublicTrailPoints().size() - 1; i >= 1; i--) {
                TrailPosition point = sword.getPublicTrailPoints().get(i);
                if (point.getPosition().distanceToSqr(sword.getPublicTrailPoints().get(i - 1).getPosition()) < 4) {
                    sword.getPublicTrailPoints().set(i,
                            point.interpolate(sword.getPublicTrailPoints().get(i - 1), partialTicks));
                } else {
                    sword.getPublicTrailPoints().set(i, new TrailPosition(
                            sword.getPublicTrailPoints().get(i - 1).getPosition()));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onTailTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                for (Entity entity : mc.level.entitiesForRendering()) {
                    if (entity instanceof ExplosiveArrow arrow && arrow.isRemoved()) {
                        synchronized (arrow.getPublicTrailPoints()) {
                            arrow.getPublicTrailPoints().clear();
                        }
                    }
                    if (entity instanceof ModSwordProjectile sword && sword.isRemoved()) {
                        synchronized (sword.getPublicTrailPoints()) {
                            sword.getPublicTrailPoints().clear();
                        }
                    }
                }
                if (explosiveArrowTrailManager != null && !explosiveArrowTrailManager.pendingTasks.isEmpty()) {
                    explosiveArrowTrailManager.pendingTasks
                            .forEach(TrailRenderPipeline.TrailTaskManager.TrailRenderTask::onTick);
                }
                if (swordProjectileTrailManager != null && !swordProjectileTrailManager.pendingTasks.isEmpty()) {
                    swordProjectileTrailManager.pendingTasks
                            .forEach(TrailRenderPipeline.TrailTaskManager.TrailRenderTask::onTick);
                }
            }
        }
    }

    private record ExplosiveArrowTrailTask(ExplosiveArrow arrow)
            implements TrailRenderPipeline.TrailTaskManager.TrailRenderTask {
        @Override
        public void executeTask(PoseStack matrix, TrailRenderPipeline.TrailBufferBuilder builder) {
            if (!arrow.getPublicTrailPoints().isEmpty()) {
                builder.withColor(0.5F, 0.8F, 1.0F, 0.6F);
                builder.withRenderType(TrailRenderPipeline.TRAIL_RENDER_TYPE);
                builder.renderTrailPath(matrix, arrow.getPublicTrailPoints(), f -> (1.0F - f) * 0.2F, f -> {
                });
            }
        }

        @Override
        public void onTick() {
            if (Minecraft.getInstance().isPaused())
                return;
            if (arrow.isRemoved()) {
                synchronized (arrow.getPublicTrailPoints()) {
                    arrow.getPublicTrailPoints().clear();
                }
            }
        }
    }

    private record ModSwordTrailTask(ModSwordProjectile sword)
            implements TrailRenderPipeline.TrailTaskManager.TrailRenderTask {
        @Override
        public void executeTask(PoseStack matrix, TrailRenderPipeline.TrailBufferBuilder builder) {
            if (!sword.getPublicTrailPoints().isEmpty()) {
                builder.withColor(1.0F, 1.0F, 1.0F, 0.6F);
                builder.withRenderType(TrailRenderPipeline.TRAIL_RENDER_TYPE);
                builder.renderTrailPath(matrix, sword.getPublicTrailPoints(), f -> (1.0F - f) * 0.2F, f -> {
                });
            }
        }

        @Override
        public void onTick() {
            if (Minecraft.getInstance().isPaused())
                return;
            if (sword.isRemoved()) {
                synchronized (sword.getPublicTrailPoints()) {
                    sword.getPublicTrailPoints().clear();
                }
            }
        }
    }
}