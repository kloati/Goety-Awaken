package com.k1sak1.goetyawaken.client;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.api.IAncientGlint;
import com.k1sak1.goetyawaken.common.network.client.CClaymoreSweepPacket;
import com.k1sak1.goetyawaken.common.network.client.CWardenRoarPacket;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.WraithNecromancer;
import com.k1sak1.goetyawaken.common.entities.hostile.MushroomMonstrosityHostile;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.NamelessOne;
import com.k1sak1.goetyawaken.common.entities.hostile.illager.RubySorcerer;
import com.k1sak1.goetyawaken.common.entities.hostile.illager.HostileRampartCaptain.HostileRampartCaptain;
import com.k1sak1.goetyawaken.common.entities.hostile.HostileDrownedNecromancer;
import com.k1sak1.goetyawaken.common.entities.hostile.illager.ArchIllusioner;
import com.k1sak1.goetyawaken.common.entities.ally.WitherServant;
import com.k1sak1.goetyawaken.common.entities.ally.ender.EndersentServant;
import com.k1sak1.goetyawaken.common.network.client.CAutoRideablePacket;
import com.k1sak1.goetyawaken.common.network.client.CGiantGhastFlightPacket;
import com.k1sak1.goetyawaken.common.network.client.CWitherFlightPacket;
import com.k1sak1.goetyawaken.common.network.client.CWitherRoarPacket;
import com.k1sak1.goetyawaken.init.ModKeybindings;
import com.k1sak1.goetyawaken.init.ModSounds;

import com.k1sak1.goetyawaken.client.renderer.ServantBeamRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
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

    public static net.minecraft.client.resources.sounds.AbstractTickableSoundInstance BOSS_MUSIC;

    public static void playBossMusic(net.minecraft.sounds.SoundEvent soundEvent,
            net.minecraft.sounds.SoundEvent postBossMusic, net.minecraft.world.entity.Mob mob, float volume,
            float pitch) {
        if (com.Polarice3.Goety.config.MainConfig.BossMusic.get()) {
            Minecraft minecraft = Minecraft.getInstance();
            if (soundEvent != null && mob.isAlive()) {
                if (BOSS_MUSIC == null) {
                    if (postBossMusic == null) {
                        postBossMusic = com.Polarice3.Goety.init.ModSounds.BOSS_POST.get();
                    }
                    BOSS_MUSIC = new com.k1sak1.goetyawaken.client.audio.BossLoopMusic(
                            soundEvent, postBossMusic, mob, volume, pitch);
                }
            } else {
                BOSS_MUSIC = null;
            }
            if (BOSS_MUSIC != null && !minecraft.getSoundManager().isActive(BOSS_MUSIC)) {
                minecraft.getSoundManager().play(BOSS_MUSIC);
            }
        }
    }

    public static void playBossMusic(net.minecraft.sounds.SoundEvent soundEvent, net.minecraft.world.entity.Mob mob,
            float volume, float pitch) {
        playBossMusic(soundEvent, null, mob, volume, pitch);
    }

    public static void clearBossMusic(com.k1sak1.goetyawaken.client.audio.BossLoopMusic music) {
        if (BOSS_MUSIC == music || (music != null && music.isStopped())) {
            BOSS_MUSIC = null;
        }
    }

    public static void clearAllBossMusic() {
        BOSS_MUSIC = null;
        ancientMusicInstance = null;
        hasPlayedPostMusic = false;
    }

    public static com.k1sak1.goetyawaken.client.audio.BossLoopMusic getCurrentBossMusic() {
        if (BOSS_MUSIC instanceof com.k1sak1.goetyawaken.client.audio.BossLoopMusic music && !music.isStopped()) {
            return music;
        }
        return null;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;

            if (player != null && mc.isWindowActive()) {
                if (player.getVehicle() != null
                        && player.getVehicle() instanceof WitherServant) {
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
                if (player.getVehicle() instanceof com.k1sak1.goetyawaken.common.entities.hostile.GiantGhast) {
                    boolean spaceGhastPressed = false;
                    if (ModKeybindings.keyBindings.length > 15 && ModKeybindings.keyBindings[15] != null) {
                        spaceGhastPressed = ModKeybindings.keyBindings[15].isDown();
                    }
                    boolean ctrlGhastPressed = false;
                    if (ModKeybindings.keyBindings.length > 16 && ModKeybindings.keyBindings[16] != null) {
                        ctrlGhastPressed = ModKeybindings.keyBindings[16].isDown();
                    }
                    if (spaceGhastPressed) {
                        GoetyAwaken.network.channel.send(net.minecraftforge.network.PacketDistributor.SERVER.noArg(),
                                new CGiantGhastFlightPacket(true, false));
                    } else if (ctrlGhastPressed) {
                        GoetyAwaken.network.channel.send(net.minecraftforge.network.PacketDistributor.SERVER.noArg(),
                                new CGiantGhastFlightPacket(false, true));
                    } else {
                        GoetyAwaken.network.channel.send(net.minecraftforge.network.PacketDistributor.SERVER.noArg(),
                                new CGiantGhastFlightPacket(false, false));
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
                        int exemptTargetId = -1;
                        if (mc.hitResult instanceof EntityHitResult entityHitResult) {
                            Entity target = entityHitResult.getEntity();
                            if (target instanceof LivingEntity && target.isAlive()) {
                                exemptTargetId = target.getId();
                            }
                        }

                        GoetyAwaken.network.channel.send(
                                net.minecraftforge.network.PacketDistributor.SERVER.noArg(),
                                new CClaymoreSweepPacket(exemptTargetId));
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
                    boolean isTargetingPlayer = ((MiscCapHelper.getMobTarget(livingEntity) instanceof Player)
                            || (MiscCapHelper.getMobTarget(livingEntity) instanceof OwnableEntity ownable
                                    && ownable.getOwner() instanceof Player))
                            || (entity.getType().is(ModTags.EntityTypes.GLOBAL_MUSIC_BOSS)
                                    && MiscCapHelper.getMobTarget(livingEntity) != null);

                    if (entity instanceof WraithNecromancer wraithNecromancer && !wraithNecromancer.isNoAi()
                            && wraithNecromancer.isAggressive()
                            && com.k1sak1.goetyawaken.Config.WRAITH_NECROMANCER_BOSS_MUSIC.get()) {
                        if (isTargetingPlayer) {
                            if (com.k1sak1.goetyawaken.Config.WRAITH_NECROMANCER_LEGACY_MUSIC.get() == true) {
                                playBossMusic(
                                        ModSounds.WRAITH_NECROMANCER_THEME2.get(),
                                        ModSounds.WRAITH_NECROMANCER_END.get(),
                                        wraithNecromancer, 0.75F, 1.0F);
                            } else {
                                playBossMusic(
                                        ModSounds.WRAITH_NECROMANCER_THEME.get(),
                                        ModSounds.WRAITH_NECROMANCER_END.get(),
                                        wraithNecromancer, 0.75F, 1.0F);
                            }

                        }
                    }
                    if (entity instanceof MushroomMonstrosityHostile mushroomMonstrosity
                            && !mushroomMonstrosity.isNoAi()
                            && mushroomMonstrosity.isAggressive()
                            && com.k1sak1.goetyawaken.Config.MUSHROOM_MONSTROSITY_BOSS_MUSIC.get()) {
                        if (isTargetingPlayer) {
                            playBossMusic(
                                    ModSounds.MUSHROOM_MONSTROSITY_BATTLE_MUSIC.get(), mushroomMonstrosity, 0.75F,
                                    1.0F);
                        }
                    }
                    if (entity instanceof com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.ParchedNecromancer parchedNecromancer
                            && !parchedNecromancer.isNoAi()
                            && parchedNecromancer.isAggressive()
                            && com.k1sak1.goetyawaken.Config.PARCHED_NECROMANCER_BOSS_MUSIC.get()) {
                        if (isTargetingPlayer) {
                            playBossMusic(
                                    ModSounds.RUINS_NECROMANCER_THEME.get(), parchedNecromancer, 0.75F, 1.0F);
                        }
                    }
                    if (entity instanceof NamelessOne namelessOne
                            && !namelessOne.isNoAi()
                            && !namelessOne.isMirror()
                            && namelessOne.isAggressive()
                            && com.k1sak1.goetyawaken.Config.NAMELESS_ONE_BOSS_MUSIC.get()) {
                        if (isTargetingPlayer) {
                            playBossMusic(
                                    ModSounds.NAMELESS_FIGHT_MUSIC.get(), namelessOne, 0.75F, 1.0F);
                        }
                    }
                    if (entity instanceof RubySorcerer rubySorcerer
                            && !rubySorcerer.isNoAi()
                            && rubySorcerer.isAggressive()
                            && com.k1sak1.goetyawaken.Config.RUBY_SORCERER_BOSS_MUSIC.get()) {
                        if (isTargetingPlayer) {
                            playBossMusic(
                                    ModSounds.RUBY_SORCERER_FIGHT.get(),
                                    ModSounds.ARCH_ILLUSIONER_POST.get(),
                                    rubySorcerer, 0.75F, 1.0F);
                        }
                    }
                    if (entity instanceof HostileRampartCaptain rampartCaptain
                            && !rampartCaptain.isNoAi()
                            && rampartCaptain.isAggressive()
                            && com.k1sak1.goetyawaken.Config.RAMPART_CAPTAIN_BOSS_MUSIC.get()) {
                        if (isTargetingPlayer) {
                            playBossMusic(
                                    ModSounds.RAMPART_CAPTAIN_FIGHT.get(),
                                    ModSounds.RAMPART_CAPTAIN_POST.get(),
                                    rampartCaptain, 0.75F, 1.0F);
                        }
                    }
                    if (entity instanceof HostileDrownedNecromancer drownedNecromancer
                            && !drownedNecromancer.isNoAi()
                            && drownedNecromancer.isAggressive()
                            && com.k1sak1.goetyawaken.Config.DROWNED_NECROMANCER_BOSS_MUSIC.get()) {
                        if (isTargetingPlayer) {
                            playBossMusic(
                                    ModSounds.DROWNED_NECROMANCER_FIGHT.get(),
                                    ModSounds.DROWNED_NECROMANCER_POST.get(),
                                    drownedNecromancer, 0.75F, 1.0F);
                        }
                    }
                    if (entity instanceof ArchIllusioner archIllusioner
                            && !archIllusioner.isNoAi()
                            && !archIllusioner.isIllusion()
                            && archIllusioner.isAggressive()
                            && com.k1sak1.goetyawaken.Config.ARCH_ILLUSIONER_BOSS_MUSIC.get()) {
                        if (isTargetingPlayer) {
                            playBossMusic(
                                    ModSounds.ARCH_ILLUSIONER_FIGHT.get(),
                                    ModSounds.ARCH_ILLUSIONER_POST.get(),
                                    archIllusioner, 0.75F, 1.0F);
                        }
                    }
                    if (entity instanceof EndersentServant endersentServant
                            && !endersentServant.isNoAi()
                            && endersentServant.isHostile()
                            && endersentServant.isAggressive()) {
                        if (isTargetingPlayer) {
                            playBossMusic(
                                    com.Polarice3.Goety.init.ModSounds.ENDERSENT_THEME.get(),
                                    com.Polarice3.Goety.init.ModSounds.ARENA_END.get(),
                                    endersentServant, 0.75F, 1.0F);
                        }
                    }
                    if (entity instanceof com.k1sak1.goetyawaken.common.entities.hostile.HostileGiantGhast hostileGiantGhast
                            && !hostileGiantGhast.isNoAi()
                            && hostileGiantGhast.isAggressive()) {
                        if (isTargetingPlayer) {
                            playBossMusic(
                                    com.Polarice3.Goety.init.ModSounds.APOSTLE_THEME.get(),
                                    com.Polarice3.Goety.init.ModSounds.APOSTLE_THEME_POST.get(),
                                    hostileGiantGhast, 0.75F, 1.0F);
                        }
                    }
                    if (entity instanceof Mob mob
                            && mob instanceof IAncientGlint ancientGlint
                            && ancientGlint.hasAncientGlint()
                            && "ancient".equals(ancientGlint.getGlintTextureType())
                            && !mob.isNoAi()
                            && !entity.getType().is(ModTags.EntityTypes.GLOBAL_MUSIC_BOSS)) {
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

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        AABB searchArea = mc.player.getBoundingBox().inflate(64.0);
        for (com.k1sak1.goetyawaken.common.entities.ally.illager.SorcererServant servant : mc.level.getEntitiesOfClass(com.k1sak1.goetyawaken.common.entities.ally.illager.SorcererServant.class, searchArea)) {
            if (!servant.isCastingSpell2() || servant.getTarget() == null) continue;
            String spellName = servant.getCurrentSpellName();
            if (spellName == null || spellName.isEmpty()) continue;
            float[] color = getBeamColor(spellName);
            if (color != null) {
                ServantBeamRenderer.renderBeam(event, servant, servant.getTarget(), spellName, color[0], color[1], color[2], color[3]);
            }
        }
        for (com.k1sak1.goetyawaken.common.entities.ally.undead.BoundSorcerer bound : mc.level.getEntitiesOfClass(com.k1sak1.goetyawaken.common.entities.ally.undead.BoundSorcerer.class, searchArea)) {
            if (!bound.isCastingSpell2() || bound.getTarget() == null) continue;
            String spellName = bound.getCurrentSpellName();
            if (spellName == null || spellName.isEmpty()) continue;
            float[] color = getBeamColor(spellName);
            if (color != null) {
                ServantBeamRenderer.renderBeam(event, bound, bound.getTarget(), spellName, color[0], color[1], color[2], color[3]);
            }
        }
    }

    private static float[] getBeamColor(String spellName) {
        if (spellName.contains("water_jet") || spellName.contains("waterjet")) return new float[] { 0.2F, 0.4F, 1.0F, 0.7F };
        if (spellName.contains("burrowing")) return new float[] { 1.0F, 0.5F, 0.2F, 0.7F };
        if (spellName.contains("prisma_beam") || spellName.contains("prismabeam")) return new float[] { 0.8F, 0.3F, 1.0F, 0.7F };
        if (spellName.contains("corrupted_beam")) return new float[] { 0.6F, 0.1F, 0.8F, 0.7F };
        return null;
    }
}