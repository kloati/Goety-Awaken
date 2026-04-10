package com.k1sak1.goetyawaken.client;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.api.IAncientGlint;
import com.k1sak1.goetyawaken.common.network.client.CClaymoreSweepPacket;
import com.k1sak1.goetyawaken.common.network.client.CWardenRoarPacket;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.WraithNecromancer;
import com.k1sak1.goetyawaken.common.entities.hostile.MushroomMonstrosityHostile;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.NamelessOne;
import com.k1sak1.goetyawaken.common.network.client.CAutoRideablePacket;
import com.k1sak1.goetyawaken.common.network.client.CWitherFlightPacket;
import com.k1sak1.goetyawaken.common.network.client.CWitherRoarPacket;
import com.k1sak1.goetyawaken.init.ModKeybindings;
import com.k1sak1.goetyawaken.init.ModSounds;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.MiscCapHelper;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID, value = Dist.CLIENT)
public class ClientEvents {
    private static int wardenRoarCooldown = 0;
    private static int autoRideableCooldown = 0;
    private static int witherRoarCooldown = 0;
    private static final int COOLDOWN_TIME = 5;
    private static net.minecraft.client.resources.sounds.SoundInstance ancientMusicInstance = null;
    private static boolean hasPlayedPostMusic = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;

            if (player != null && mc.isWindowActive()) {
                if (player.getVehicle() != null
                        && player.getVehicle().getClass().getSimpleName().equals("WitherServant")) {
                    boolean spacePressed = false;
                    if (ModKeybindings.keyBindings.length > 15 && ModKeybindings.keyBindings[15] != null) {
                        spacePressed = ModKeybindings.keyBindings[15].isDown();
                    }
                    boolean ctrlPressed = false;
                    if (ModKeybindings.keyBindings.length > 16 && ModKeybindings.keyBindings[16] != null) {
                        ctrlPressed = ModKeybindings.keyBindings[16].isDown();
                    }
                    if (spacePressed) {
                        GoetyAwaken.network.channel.send(net.minecraftforge.network.PacketDistributor.SERVER.noArg(),
                                new CWitherFlightPacket(true, false));
                    } else if (ctrlPressed) {
                        GoetyAwaken.network.channel.send(net.minecraftforge.network.PacketDistributor.SERVER.noArg(),
                                new CWitherFlightPacket(false, true));
                    } else {
                        GoetyAwaken.network.channel.send(net.minecraftforge.network.PacketDistributor.SERVER.noArg(),
                                new CWitherFlightPacket(false, false));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (wardenRoarCooldown > 0) {
            wardenRoarCooldown--;
        }
        if (autoRideableCooldown > 0) {
            autoRideableCooldown--;
        }
        if (witherRoarCooldown > 0) {
            witherRoarCooldown--;
        }

        if (player != null && mc.isWindowActive()) {
            if (player.getVehicle() != null && player.getVehicle().getClass().getSimpleName().equals("WitherServant")) {
                if (com.Polarice3.Goety.init.ModKeybindings.keyBindings.length > 10
                        && com.Polarice3.Goety.init.ModKeybindings.keyBindings[10] != null
                        && com.Polarice3.Goety.init.ModKeybindings.keyBindings[10].isDown()) {
                    if (witherRoarCooldown <= 0) {
                        GoetyAwaken.network.channel.send(net.minecraftforge.network.PacketDistributor.SERVER.noArg(),
                                new CWitherRoarPacket());
                        witherRoarCooldown = 20;
                    }
                }
            } else {
                if (com.Polarice3.Goety.init.ModKeybindings.keyBindings.length > 11
                        && com.Polarice3.Goety.init.ModKeybindings.keyBindings[11] != null
                        && com.Polarice3.Goety.init.ModKeybindings.keyBindings[11].isDown()) {
                    if (autoRideableCooldown <= 0) {
                        GoetyAwaken.network.channel.send(net.minecraftforge.network.PacketDistributor.SERVER.noArg(),
                                new CAutoRideablePacket());
                        autoRideableCooldown = COOLDOWN_TIME;
                    }
                }
                if (com.Polarice3.Goety.init.ModKeybindings.keyBindings.length > 10
                        && com.Polarice3.Goety.init.ModKeybindings.keyBindings[10] != null
                        && com.Polarice3.Goety.init.ModKeybindings.keyBindings[10].isDown()) {
                    if (wardenRoarCooldown <= 0) {
                        GoetyAwaken.network.channel.send(net.minecraftforge.network.PacketDistributor.SERVER.noArg(),
                                new CWardenRoarPacket());
                        wardenRoarCooldown = COOLDOWN_TIME;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onClientInput(InputEvent.InteractionKeyMappingTriggered event) {
        if (event.isAttack() && !event.isCanceled()) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;

            if (player != null) {
                var stack = player.getMainHandItem();
                var item = stack.getItem();

                boolean isClaymore = item instanceof com.k1sak1.goetyawaken.common.items.ClaymoreItem
                        || item instanceof com.k1sak1.goetyawaken.common.items.ObsidianClaymoreItem
                        || item instanceof com.k1sak1.goetyawaken.common.items.StarlessNightItem;

                if (isClaymore) {
                    if (player.getAttackStrengthScale(0.5F) > 0.9F) {
                        GoetyAwaken.network.channel.send(
                                net.minecraftforge.network.PacketDistributor.SERVER.noArg(),
                                new CClaymoreSweepPacket());
                    }

                }
            }

        }
    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        Entity entity = event.getEntity();
        if (entity.level().isClientSide()) {
            if (MainConfig.BossMusic.get()) {
                if (entity instanceof LivingEntity livingEntity) {
                    boolean isTargetingPlayer = (MiscCapHelper.getMobTarget(livingEntity) instanceof Player)
                            || (MiscCapHelper.getMobTarget(livingEntity) instanceof OwnableEntity ownable
                                    && ownable.getOwner() instanceof Player)
                            || entity.getType().is(ModTags.EntityTypes.GLOBAL_MUSIC_BOSS);

                    if (entity instanceof WraithNecromancer wraithNecromancer && !wraithNecromancer.isNoAi()
                            && com.k1sak1.goetyawaken.Config.WRAITH_NECROMANCER_BOSS_MUSIC.get()) {
                        if (isTargetingPlayer) {
                            if (com.k1sak1.goetyawaken.Config.WRAITH_NECROMANCER_LEGACY_MUSIC.get() == true) {
                                com.Polarice3.Goety.client.events.ClientEvents.playBossMusic(
                                        ModSounds.WRAITH_NECROMANCER_THEME2.get(),
                                        ModSounds.WRAITH_NECROMANCER_END.get(),
                                        wraithNecromancer, 0.75F, 1.0F);
                            } else {
                                com.Polarice3.Goety.client.events.ClientEvents.playBossMusic(
                                        ModSounds.WRAITH_NECROMANCER_THEME.get(),
                                        ModSounds.WRAITH_NECROMANCER_END.get(),
                                        wraithNecromancer, 0.75F, 1.0F);
                            }

                        }
                    }
                    if (entity instanceof MushroomMonstrosityHostile mushroomMonstrosity
                            && !mushroomMonstrosity.isNoAi()
                            && com.k1sak1.goetyawaken.Config.MUSHROOM_MONSTROSITY_BOSS_MUSIC.get()) {
                        if (isTargetingPlayer) {
                            com.Polarice3.Goety.client.events.ClientEvents.playBossMusic(
                                    ModSounds.MUSHROOM_MONSTROSITY_BATTLE_MUSIC.get(), mushroomMonstrosity, 0.75F,
                                    1.0F);
                        }
                    }
                    if (entity instanceof com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.ParchedNecromancer parchedNecromancer
                            && !parchedNecromancer.isNoAi()
                            && com.k1sak1.goetyawaken.Config.PARCHED_NECROMANCER_BOSS_MUSIC.get()) {
                        if (isTargetingPlayer) {
                            com.Polarice3.Goety.client.events.ClientEvents.playBossMusic(
                                    ModSounds.RUINS_NECROMANCER_THEME.get(), parchedNecromancer, 0.75F, 1.0F);
                        }
                    }
                    if (entity instanceof NamelessOne namelessOne
                            && !namelessOne.isNoAi()
                            && com.k1sak1.goetyawaken.Config.NAMELESS_ONE_BOSS_MUSIC.get()) {
                        if (isTargetingPlayer) {
                            com.Polarice3.Goety.client.events.ClientEvents.playBossMusic(
                                    ModSounds.NAMELESS_FIGHT_MUSIC.get(), namelessOne, 0.75F, 1.0F);
                        }
                    }
                    if (entity instanceof Mob mob
                            && mob instanceof IAncientGlint ancientGlint
                            && ancientGlint.hasAncientGlint()
                            && "ancient".equals(ancientGlint.getGlintTextureType())
                            && !mob.isNoAi()) {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.getSoundManager() != null) {
                            boolean isMobAlive = mob.isAlive() && !mob.isDeadOrDying();
                            if (isMobAlive) {
                                hasPlayedPostMusic = false;
                                if (ancientMusicInstance == null
                                        || !mc.getSoundManager().isActive(ancientMusicInstance)) {
                                    ancientMusicInstance = new net.minecraft.client.resources.sounds.SimpleSoundInstance(
                                            ModSounds.MUSIC_DISC_ANCIENT.get(),
                                            net.minecraft.sounds.SoundSource.RECORDS,
                                            0.75F, 1.0F,
                                            net.minecraft.client.resources.sounds.SoundInstance.createUnseededRandom(),
                                            mob.getX(), mob.getY(), mob.getZ());
                                    mc.getSoundManager().play(ancientMusicInstance);
                                }
                            } else if (!hasPlayedPostMusic) {
                                if (ancientMusicInstance != null
                                        && mc.getSoundManager().isActive(ancientMusicInstance)) {
                                    mc.getSoundManager().stop(ancientMusicInstance);
                                }
                                ancientMusicInstance = null;

                                net.minecraft.client.resources.sounds.SoundInstance postMusic = new net.minecraft.client.resources.sounds.SimpleSoundInstance(
                                        ModSounds.ANCIENT_HUNT_POST.get(),
                                        net.minecraft.sounds.SoundSource.RECORDS,
                                        0.75F, 1.0F,
                                        net.minecraft.client.resources.sounds.SoundInstance.createUnseededRandom(),
                                        mob.getX(), mob.getY(), mob.getZ());
                                mc.getSoundManager().play(postMusic);
                                hasPlayedPostMusic = true;
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.level != null) {
            AABB searchArea = minecraft.player.getBoundingBox().inflate(64.0);
            for (NamelessOne namelessOne : minecraft.level.getEntitiesOfClass(NamelessOne.class, searchArea)) {
                if (namelessOne.isAlive() && !namelessOne.isNoAi()) {
                    final float renderDistance = minecraft.gameRenderer.getRenderDistance();
                    event.setNearPlaneDistance(renderDistance * 0.1F);
                    event.setFarPlaneDistance(Math.min(renderDistance, 96.0F) * 0.6F);
                    event.setCanceled(true);
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onGetFogColor(ViewportEvent.ComputeFogColor event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.level != null) {
            AABB searchArea = minecraft.player.getBoundingBox().inflate(64.0);
            for (NamelessOne namelessOne : minecraft.level.getEntitiesOfClass(NamelessOne.class, searchArea)) {
                if (namelessOne.isAlive() && !namelessOne.isNoAi()) {
                    float[] sandstormColor = getSandstormFogColor(event.getPartialTick());
                    event.setRed(sandstormColor[0]);
                    event.setGreen(sandstormColor[1]);
                    event.setBlue(sandstormColor[2]);
                    break;
                }
            }
        }
    }

    private static float[] getSandstormFogColor(double partialTick) {
        float timeVariation = (Mth.sin((float) (partialTick * 0.01F)) + 1.0F) / 2.0F;
        float baseRed = 0.65F;
        float baseGreen = 0.85F;
        float baseBlue = 0.55F;
        float variationAmplitude = 0.12F;
        float red = baseRed + (timeVariation - 0.5F) * variationAmplitude * 2.0F;
        float green = baseGreen + (timeVariation - 0.5F) * variationAmplitude * 2.0F;
        float blue = baseBlue + (timeVariation - 0.5F) * variationAmplitude;
        red = Mth.clamp(red, 0.53F, 0.77F);
        green = Mth.clamp(green, 0.73F, 0.97F);
        blue = Mth.clamp(blue, 0.43F, 0.67F);

        return new float[] { red, green, blue };
    }
}