package com.k1sak1.goetyawaken.client.events;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID, value = Dist.CLIENT)
public class MasqueraderBossBarEvent {

    protected static final ResourceLocation MASQUERADER_OUTSIDE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/illager/masquerader/masquerader_boss_bar_outside.png");
    protected static final ResourceLocation MASQUERADER_INSIDE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/illager/masquerader/masquerader_boss_bar_inside.png");
    protected static final ResourceLocation MASQUERADER_HURT = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/illager/masquerader/masquerader_boss_bar_inside_hurt.png");

    public static Map<UUID, Mob> MASQUERADER_BOSS_BARS = new HashMap<>();

    private static void cleanInvalidEntries() {
        MASQUERADER_BOSS_BARS.entrySet().removeIf(entry -> {
            Mob mob = entry.getValue();
            return mob == null || mob.isRemoved();
        });
        TRANSITIONS.keySet().removeIf(entityId -> {
            return MASQUERADER_BOSS_BARS.values().stream().noneMatch(m -> m.getId() == entityId);
        });
    }

    private static final int FRAME_WIDTH = 200;
    private static final int FRAME_HEIGHT = 16;
    private static final int FRAME_TEX_WIDTH = 256;
    private static final int FRAME_TEX_HEIGHT = 80;
    private static final int FILL_WIDTH = 182;
    private static final int FILL_HEIGHT = 8;
    private static final int FILL_TEX_WIDTH = 364;
    private static final int FILL_TEX_HEIGHT = 40;
    private static final int HURT_TEX_WIDTH = 256;
    private static final int HURT_TEX_HEIGHT = 16;

    private static final int TRANSITION_DURATION = 16;
    private static final Map<Integer, MaskTransition> TRANSITIONS = new HashMap<>();

    @SubscribeEvent
    public static void renderBossBar(CustomizeGuiOverlayEvent.BossEventProgress event) {
        cleanInvalidEntries();
        Minecraft minecraft = Minecraft.getInstance();
        if (MASQUERADER_BOSS_BARS.containsKey(event.getBossEvent().getId())) {
            Mob boss = MASQUERADER_BOSS_BARS.get(event.getBossEvent().getId());
            event.setCanceled(true);
            int i = minecraft.getWindow().getGuiScaledWidth();
            int k = i / 2 - 100;
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            drawMasqueraderBar(event.getGuiGraphics(), k, event.getY(), event.getPartialTick(), boss);
            Component itextcomponent = boss.getDisplayName();
            int l = minecraft.font.width(itextcomponent);
            int i1 = i / 2 - l / 2;
            event.getGuiGraphics().drawString(minecraft.font, itextcomponent, i1, event.getY() - 9, 16777215);
            if (event.getY() >= minecraft.getWindow().getGuiScaledHeight() / 3) {
                return;
            }
            event.setIncrement(12 + minecraft.font.lineHeight);
        }
    }

    private static void drawMasqueraderBar(GuiGraphics guiGraphics, int pX, int pY, float partialTicks, Mob pEntity) {
        float percent = pEntity.getMaxHealth() > 0 ? pEntity.getHealth() / pEntity.getMaxHealth() : 0;
        int i = (int) (percent * FILL_WIDTH);
        int pX2 = pX + 9;
        int pY2 = pY + 4;

        int offset = (int) ((pEntity.tickCount + partialTicks) % FILL_TEX_WIDTH);
        if (percent <= 0.25F) {
            offset = (int) (((pEntity.tickCount + partialTicks) * 4) % FILL_TEX_WIDTH);
        } else if (percent <= 0.5F) {
            offset = (int) (((pEntity.tickCount + partialTicks) * 2) % FILL_TEX_WIDTH);
        }

        int currentMask = getMaskIndex(pEntity);
        int entityId = pEntity.getId();
        MaskTransition transition = TRANSITIONS.get(entityId);

        if (transition == null || transition.targetMask != currentMask) {
            int oldMask = (transition != null) ? transition.targetMask : currentMask;
            transition = new MaskTransition(oldMask, currentMask, pEntity.tickCount);
            TRANSITIONS.put(entityId, transition);
        }

        float transProgress = transition.getProgress(pEntity.tickCount);

        if (i > 0) {
            if (transProgress >= 1.0F || transition.oldMask == transition.targetMask) {
                drawFillSegment(guiGraphics, pX2, pY2, offset, i, currentMask);
            } else {
                int newWidth = (int) (i * transProgress);
                int halfSide = (i - newWidth) / 2;

                if (halfSide > 0) {
                    drawFillSegment(guiGraphics, pX2, pY2, offset, halfSide, transition.oldMask);
                }
                if (newWidth > 0) {
                    drawFillSegment(guiGraphics, pX2 + halfSide, pY2,
                            offset + halfSide, newWidth, transition.targetMask);
                }
                int rightStart = halfSide + newWidth;
                if (i - rightStart > 0) {
                    drawFillSegment(guiGraphics, pX2 + rightStart, pY2,
                            offset + rightStart, i - rightStart, transition.oldMask);
                }
            }

            if (pEntity.hurtTime >= 5) {
                int shake = pEntity.getRandom().nextInt(pEntity.hurtTime);
                int damage = pEntity.getRandom().nextInt(pEntity.hurtTime);
                guiGraphics.blit(MASQUERADER_HURT,
                        pX2, pY2,
                        shake, 0,
                        i, FILL_HEIGHT,
                        HURT_TEX_WIDTH, HURT_TEX_HEIGHT);
            }
        }

        if (transProgress >= 1.0F || transition.oldMask == transition.targetMask) {
            drawFrameSegment(guiGraphics, pX, pY, 0, FRAME_WIDTH, currentMask);
        } else {
            int frameNewWidth = (int) (FRAME_WIDTH * transProgress);
            int frameHalfSide = (FRAME_WIDTH - frameNewWidth) / 2;

            if (frameHalfSide > 0) {
                drawFrameSegment(guiGraphics, pX, pY, 0, frameHalfSide, transition.oldMask);
            }
            if (frameNewWidth > 0) {
                drawFrameSegment(guiGraphics, pX + frameHalfSide, pY,
                        frameHalfSide, frameNewWidth, transition.targetMask);
            }
            int frameRightStart = frameHalfSide + frameNewWidth;
            if (FRAME_WIDTH - frameRightStart > 0) {
                drawFrameSegment(guiGraphics, pX + frameRightStart, pY,
                        frameRightStart, FRAME_WIDTH - frameRightStart, transition.oldMask);
            }
        }
    }

    private static void drawFillSegment(GuiGraphics guiGraphics, int x, int y,
            int uOffset, int width, int maskIndex) {
        if (width <= 0)
            return;
        guiGraphics.blit(MASQUERADER_INSIDE,
                x, y,
                uOffset, maskIndex * FILL_HEIGHT,
                width, FILL_HEIGHT,
                FILL_TEX_WIDTH, FILL_TEX_HEIGHT);
    }

    private static void drawFrameSegment(GuiGraphics guiGraphics, int x, int y,
            int uOffset, int width, int maskIndex) {
        if (width <= 0)
            return;
        guiGraphics.blit(MASQUERADER_OUTSIDE,
                x, y,
                uOffset, maskIndex * FRAME_HEIGHT,
                width, FRAME_HEIGHT,
                FRAME_TEX_WIDTH, FRAME_TEX_HEIGHT);
    }

    private static int getMaskIndex(Mob entity) {
        try {
            java.lang.reflect.Method method = entity.getClass().getMethod("getMask");
            int mask = (int) method.invoke(entity);
            return Math.max(0, Math.min(4, mask));
        } catch (Exception e) {
            return 0;
        }
    }

    public static void addMasqueraderBossBar(UUID id, Mob mob) {
        MASQUERADER_BOSS_BARS.put(id, mob);
    }

    public static void removeMasqueraderBossBar(UUID id, Mob mob) {
        MASQUERADER_BOSS_BARS.remove(id, mob);
        TRANSITIONS.remove(mob.getId());
    }

    public static void clearAll() {
        MASQUERADER_BOSS_BARS.clear();
        TRANSITIONS.clear();
    }

    private static class MaskTransition {
        final int oldMask;
        final int targetMask;
        final int startTick;

        MaskTransition(int oldMask, int targetMask, int startTick) {
            this.oldMask = oldMask;
            this.targetMask = targetMask;
            this.startTick = startTick;
        }

        float getProgress(int currentTick) {
            if (oldMask == targetMask)
                return 1.0F;
            int elapsed = currentTick - startTick;
            if (elapsed >= TRANSITION_DURATION)
                return 1.0F;
            if (elapsed <= 0)
                return 0.0F;
            float t = (float) elapsed / TRANSITION_DURATION;
            return t * t * (3.0F - 2.0F * t);
        }
    }
}
