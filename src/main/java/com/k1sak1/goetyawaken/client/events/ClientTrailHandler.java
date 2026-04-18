package com.k1sak1.goetyawaken.client.events;

import com.k1sak1.goetyawaken.client.renderer.TrailRenderPipeline;
import com.k1sak1.goetyawaken.client.renderer.trail.TrailPosition;
import com.k1sak1.goetyawaken.common.entities.projectiles.ExplosiveArrow;
import com.k1sak1.goetyawaken.common.entities.projectiles.ModSwordProjectile;
import com.k1sak1.goetyawaken.common.entities.projectiles.NamelessBolt;
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
    public static TrailRenderPipeline.TrailTaskManager namelessBoltTrailManager;
    public static TrailRenderPipeline.TrailTaskManager namelessBoltDarkCoreManager;
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
            if (namelessBoltTrailManager == null) {
                namelessBoltTrailManager = TrailRenderPipeline.TrailTaskManager.create();
            }
            if (namelessBoltDarkCoreManager == null) {
                namelessBoltDarkCoreManager = TrailRenderPipeline.TrailTaskManager.create();
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
                    if (entity instanceof NamelessBolt bolt && !bolt.isRemoved()) {
                        updateNamelessBoltTrailPoints(bolt, event.getPartialTick());

                        if (!bolt.getPublicTrailPoints().isEmpty()) {
                            namelessBoltTrailManager.queueTrailTask(new NamelessBoltOuterTrailTask(bolt));
                            namelessBoltDarkCoreManager.queueTrailTask(new NamelessBoltDarkCoreTask(bolt));
                        }
                    }
                }
                explosiveArrowTrailManager.executeTrailRendering(event.getPoseStack());
                swordProjectileTrailManager.executeTrailRendering(event.getPoseStack());
                namelessBoltTrailManager.executeTrailRendering(event.getPoseStack());
                namelessBoltDarkCoreManager.executeTrailRendering(event.getPoseStack());
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

    private static void updateNamelessBoltTrailPoints(NamelessBolt bolt, float partialTicks) {
        if (bolt.getPublicTrailPoints().isEmpty()) {
            return;
        }

        if (!bolt.hasTrail()) {
            return;
        }

        synchronized (bolt.getPublicTrailPoints()) {
            net.minecraft.world.phys.Vec3 centerPos = bolt.getBoundingBox().getCenter();
            double x = net.minecraft.util.Mth.lerp(partialTicks, bolt.xOld, bolt.getX()) + (centerPos.x - bolt.getX());
            double y = net.minecraft.util.Mth.lerp(partialTicks, bolt.yOld, bolt.getY()) + (centerPos.y - bolt.getY());
            double z = net.minecraft.util.Mth.lerp(partialTicks, bolt.zOld, bolt.getZ()) + (centerPos.z - bolt.getZ());
            bolt.getPublicTrailPoints().set(0, new TrailPosition(
                    new net.minecraft.world.phys.Vec3(x, y, z), 0));
            for (int i = bolt.getPublicTrailPoints().size() - 1; i >= 1; i--) {
                TrailPosition point = bolt.getPublicTrailPoints().get(i);
                if (point.getPosition().distanceToSqr(bolt.getPublicTrailPoints().get(i - 1).getPosition()) < 4) {
                    bolt.getPublicTrailPoints().set(i,
                            point.interpolate(bolt.getPublicTrailPoints().get(i - 1), partialTicks));
                } else {
                    bolt.getPublicTrailPoints().set(i, new TrailPosition(
                            bolt.getPublicTrailPoints().get(i - 1).getPosition()));
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
                    if (entity instanceof NamelessBolt bolt && bolt.isRemoved()) {
                        synchronized (bolt.getPublicTrailPoints()) {
                            bolt.getPublicTrailPoints().clear();
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
                if (namelessBoltTrailManager != null && !namelessBoltTrailManager.pendingTasks.isEmpty()) {
                    namelessBoltTrailManager.pendingTasks
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
                builder.withLight(15728880);
                builder.withColor(0.5F, 0.8F, 1.0F, 0.6F);
                builder.withRenderType(TrailRenderPipeline.getGlowingTrailRenderType());
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
                builder.withLight(15728880);
                builder.withColor(1.0F, 1.0F, 1.0F, 0.6F);
                builder.withRenderType(TrailRenderPipeline.getGlowingTrailRenderType());
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

    private record NamelessBoltOuterTrailTask(NamelessBolt bolt)
            implements TrailRenderPipeline.TrailTaskManager.TrailRenderTask {
        @Override
        public void executeTask(PoseStack matrix, TrailRenderPipeline.TrailBufferBuilder builder) {
            if (!bolt.getPublicTrailPoints().isEmpty()) {
                long time = bolt.level().getGameTime();
                float hueShift = (float) Math.sin(time * 0.05) * 0.05F;
                float yellowGreen = Math.min(1.0F, 0.8F + hueShift);

                float yellowGreenAlpha = 0.35F;
                float yellowGreenWidth = 0.5F;

                builder.withLight(15728880);
                builder.withColor(yellowGreen, yellowGreen, 0.2F, yellowGreenAlpha);
                builder.withRenderType(TrailRenderPipeline.getGlowingTrailRenderType());
                builder.renderTrailPath(matrix, bolt.getPublicTrailPoints(), f -> (1.0F - f) * yellowGreenWidth, f -> {
                });

                float whiteAlpha = 0.5F;
                float whiteWidth = 0.3F;

                builder.withLight(15728880);
                builder.withColor(1.0F, 1.0F, 1.0F, whiteAlpha);
                builder.withRenderType(TrailRenderPipeline.getGlowingTrailRenderType());
                builder.renderTrailPath(matrix, bolt.getPublicTrailPoints(), f -> (1.0F - f) * whiteWidth, f -> {
                });
            }
        }

        @Override
        public void onTick() {
            if (Minecraft.getInstance().isPaused())
                return;
            if (bolt.isRemoved()) {
                synchronized (bolt.getPublicTrailPoints()) {
                    bolt.getPublicTrailPoints().clear();
                }
            }
        }
    }

    private record NamelessBoltDarkCoreTask(NamelessBolt bolt)
            implements TrailRenderPipeline.TrailTaskManager.TrailRenderTask {
        @Override
        public void executeTask(PoseStack matrix, TrailRenderPipeline.TrailBufferBuilder builder) {
            if (!bolt.getPublicTrailPoints().isEmpty()) {
                float blackWidth = 0.15F;
                float blackAlpha = 0.88F;
                builder.withLight(15728880);
                builder.withColor(0.0F, 0.0F, 0.0F, blackAlpha);
                builder.withRenderType(TrailRenderPipeline.getDarkCoreRenderType());
                builder.renderTrailPath(matrix, bolt.getPublicTrailPoints(), f -> (1.0F - f) * blackWidth, f -> {
                });
            }
        }

        @Override
        public void onTick() {
            if (Minecraft.getInstance().isPaused())
                return;
            if (bolt.isRemoved()) {
                synchronized (bolt.getPublicTrailPoints()) {
                    bolt.getPublicTrailPoints().clear();
                }
            }
        }
    }
}