package com.k1sak1.goetyawaken.client.events;

import com.k1sak1.goetyawaken.common.network.server.SBossBarPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = com.k1sak1.goetyawaken.GoetyAwaken.MODID, value = Dist.CLIENT)
public class CustomBossBarRenderer {

    protected static final ResourceLocation ANCIENT_BOSS_BAR = com.k1sak1.goetyawaken.GoetyAwaken.location(
            "textures/gui/ancient_hunt.png");

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void renderBossBar(CustomizeGuiOverlayEvent.BossEventProgress event) {
        UUID bossBarId = event.getBossEvent().getId();

        Integer renderType = CustomBossBarHandler.BOSS_BAR_RENDER_TYPES.get(bossBarId);
        if (renderType == null) {
            return;
        }

        switch (renderType) {
            case SBossBarPacket.RENDER_TYPE_ANCIENT:
                renderAncientBossBar(event);
                break;
        }
    }

    private static void renderAncientBossBar(CustomizeGuiOverlayEvent.BossEventProgress event) {
        Minecraft minecraft = Minecraft.getInstance();

        Mob boss = AncientBossBarEvent.ANCIENT_BOSS_BARS.get(event.getBossEvent().getId());
        if (boss == null) {
            return;
        }

        event.setCanceled(true);
        int i = minecraft.getWindow().getGuiScaledWidth();
        int j = i / 2 - 62;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        drawAncientBar(event.getGuiGraphics(), j, event.getY(), event.getPartialTick(), boss);
        Component itextcomponent = boss.getDisplayName();
        int l = minecraft.font.width(itextcomponent);
        int i1 = i / 2 - l / 2;
        event.getGuiGraphics().drawString(minecraft.font, itextcomponent, i1, event.getY() - 9, 16777215);
        if (event.getY() >= minecraft.getWindow().getGuiScaledHeight() / 3) {
            return;
        }
        event.setIncrement(12 + minecraft.font.lineHeight);
    }

    private static void drawAncientBar(GuiGraphics guiGraphics, int pX, int pY, float partialTicks, Mob pEntity) {
        float percent = pEntity.getMaxHealth() > 0 ? pEntity.getHealth() / pEntity.getMaxHealth() : 0;
        int i = (int) (percent * 128.0F);

        if (i > 0) {
            if (pEntity.isInvulnerable() && !pEntity.isInvisible()) {
                guiGraphics.blit(ANCIENT_BOSS_BAR, pX, pY, 0, 24, i, 8, 128, 128);
            } else {
                guiGraphics.blit(ANCIENT_BOSS_BAR, pX, pY, 0, 8, i, 8, 128, 128);
            }
        }
        guiGraphics.blit(ANCIENT_BOSS_BAR, pX - 11, pY, 0, 16, 9, 8, 128, 128);
        guiGraphics.blit(ANCIENT_BOSS_BAR, pX, pY, 0, 0, 128, 8, 128, 128);
    }
}
