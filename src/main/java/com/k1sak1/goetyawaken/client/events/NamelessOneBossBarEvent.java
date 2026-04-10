package com.k1sak1.goetyawaken.client.events;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.NamelessOne;
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
public class NamelessOneBossBarEvent {

    protected static final ResourceLocation TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/gui/boss_bar.png");
    protected static final ResourceLocation BOSS_HURT = new ResourceLocation(GoetyAwaken.MODID,
            "textures/gui/boss_bar_hurt.png");
    protected static final ResourceLocation BOSS_BAR_1 = new ResourceLocation(GoetyAwaken.MODID,
            "textures/gui/boss_bar_1.png");

    public static Map<UUID, Mob> NAMELESS_ONE_BOSS_BARS = new HashMap<>();

    @SubscribeEvent
    public static void renderBossBar(CustomizeGuiOverlayEvent.BossEventProgress event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (NAMELESS_ONE_BOSS_BARS.containsKey(event.getBossEvent().getId())) {
            Mob boss = NAMELESS_ONE_BOSS_BARS.get(event.getBossEvent().getId());
            if (boss instanceof NamelessOne) {
                event.setCanceled(true);
                int i = minecraft.getWindow().getGuiScaledWidth();
                int k = i / 2 - 100;
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                drawNamelessOneBar(event.getGuiGraphics(), k, event.getY(), event.getPartialTick(), boss);
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
    }

    private static void drawNamelessOneBar(GuiGraphics guiGraphics, int pX, int pY, float partialTicks, Mob pEntity) {
        float percent = pEntity.getMaxHealth() > 0 ? pEntity.getHealth() / pEntity.getMaxHealth() : 0;
        int i = (int) (percent * 182.0F);
        int pX2 = pX + 9;
        int pY2 = pY + 4;

        int offset = (int) ((pEntity.tickCount + partialTicks) % 364);
        if (percent <= 0.25F) {
            offset = (int) (((pEntity.tickCount + partialTicks) * 4) % 364);
        } else if (percent <= 0.5F) {
            offset = (int) (((pEntity.tickCount + partialTicks) * 2) % 364);
        }

        int shake = 0;
        int damage = 0;

        if (i > 0) {
            guiGraphics.blit(BOSS_BAR_1, pX2, pY2, offset, 48, i, 8, 364, 80);
            if (pEntity.hurtTime >= 5) {
                shake = pEntity.getRandom().nextInt(pEntity.hurtTime);
                damage = 96 + pEntity.getRandom().nextInt(pEntity.hurtTime);
                guiGraphics.blit(BOSS_HURT, pX2, pY2, shake, damage, i, 8, 256, 256);
            }
        }
        guiGraphics.blit(TEXTURE, pX, pY, 0, 96, 200, 16, 256, 256);
    }

    public static void addNamelessOneBossBar(UUID id, Mob mob) {
        NAMELESS_ONE_BOSS_BARS.put(id, mob);
    }

    public static void removeNamelessOneBossBar(UUID id, Mob mob) {
        NAMELESS_ONE_BOSS_BARS.remove(id, mob);
    }
}