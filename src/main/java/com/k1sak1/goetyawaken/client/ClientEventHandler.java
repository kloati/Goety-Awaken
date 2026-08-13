package com.k1sak1.goetyawaken.client;

import com.Polarice3.Goety.client.render.model.SorcererModel;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.PaleGolemModel;
import com.k1sak1.goetyawaken.client.renderer.layers.AncientGlintLayer;
import com.k1sak1.goetyawaken.client.model.ParchedModel;
import com.k1sak1.goetyawaken.client.model.SilverfishServantModel;
import com.k1sak1.goetyawaken.client.model.CreeperServantModel;
import com.k1sak1.goetyawaken.client.model.StatueCreeperModel;
import com.k1sak1.goetyawaken.client.model.JITBZombieServantModel;
import com.k1sak1.goetyawaken.client.model.IceCreeperServantModel;
import com.k1sak1.goetyawaken.client.model.PoisonousPotatoCreeperServantModel;
import com.k1sak1.goetyawaken.client.model.MushroomMonstrosityModel;
import com.k1sak1.goetyawaken.client.model.EndermanServantModel;
import com.k1sak1.goetyawaken.client.model.EndermiteServantModel;
import com.k1sak1.goetyawaken.client.model.IceCreeperModel;
import com.k1sak1.goetyawaken.client.model.ShulkerServantModel;
import com.k1sak1.goetyawaken.client.model.WitherServantModel;
import com.k1sak1.goetyawaken.client.model.illager.TormentorServantModel;
import com.k1sak1.goetyawaken.client.model.illager.PreacherServantModel;
import com.k1sak1.goetyawaken.client.model.illager.CroneServantModel;
import com.k1sak1.goetyawaken.client.model.illager.SorcererServantModel;
import com.k1sak1.goetyawaken.client.model.illager.MinisterServantModel;
import com.k1sak1.goetyawaken.client.model.ender.EndersentServantModel;
import com.k1sak1.goetyawaken.client.model.WightServantModel;
import com.k1sak1.goetyawaken.client.model.AngryMooshroomModel;
import com.k1sak1.goetyawaken.client.model.BoulderingZombieModel;
import com.k1sak1.goetyawaken.client.model.CaerbannogRabbitServantModel;
import com.k1sak1.goetyawaken.client.model.MushroomMissileModel;
import com.k1sak1.goetyawaken.client.model.EnderKeeperServantModel;
import com.k1sak1.goetyawaken.client.model.ally.Integration.MaidFairyServantModel;
import com.k1sak1.goetyawaken.client.model.illager.ApostleServantModel;
import com.k1sak1.goetyawaken.client.model.illager.IllusionerServantModel;
import com.k1sak1.goetyawaken.client.model.undead.skeleton.SunkenSkeletonModel;
import com.k1sak1.goetyawaken.client.model.RoyalguardModel;
import com.k1sak1.goetyawaken.client.model.TowerGuardModel;
import com.k1sak1.goetyawaken.client.model.SkeletonVanguardModel;
import com.k1sak1.goetyawaken.client.model.SpiderCreederModel;
import com.k1sak1.goetyawaken.client.model.VizierCloneServantModel;
import com.k1sak1.goetyawaken.client.model.VizierServantModel;
import com.k1sak1.goetyawaken.client.model.ZombieDarkguardModel;
import com.k1sak1.goetyawaken.client.model.OminousPaintingModel;
import com.k1sak1.goetyawaken.client.model.HostileSnapperModel;
import com.k1sak1.goetyawaken.client.model.undead.SkullLordServantModel;
import com.k1sak1.goetyawaken.client.model.VanguardChampionModel;
import com.k1sak1.goetyawaken.client.model.ScarletVexModel;
import com.k1sak1.goetyawaken.client.model.GiantGhastModel;
import com.k1sak1.goetyawaken.client.model.RampartCaptainModel;
import com.k1sak1.goetyawaken.client.model.BurningShieldModel;
import com.k1sak1.goetyawaken.client.model.ToxifinModel;
import com.k1sak1.goetyawaken.client.model.ally.Integration.SwampjawServantModel;
import com.k1sak1.goetyawaken.client.model.ally.Integration.SwampMineModel;
import com.k1sak1.goetyawaken.client.model.ally.Integration.BellringerServantModel;
import com.k1sak1.goetyawaken.client.model.ally.Integration.DameFortunaServantModel;
import com.k1sak1.goetyawaken.client.model.ally.Integration.FortunaDameBombModel;
import com.k1sak1.goetyawaken.client.model.ally.Integration.FortunaDameCardModel;
import com.k1sak1.goetyawaken.client.model.ally.Integration.RosalyneServantModel;
import com.k1sak1.goetyawaken.client.model.ally.Integration.RoseSpiritServantModel;
import com.k1sak1.goetyawaken.client.model.ally.Integration.SculkCentipedeServantModel;
import com.k1sak1.goetyawaken.client.model.ally.Integration.SculkLeechServantModel;
import com.k1sak1.goetyawaken.client.model.ally.Integration.ShatteredServantModel;
import com.k1sak1.goetyawaken.client.model.ally.Integration.ShriekWormServantModel;
import com.k1sak1.goetyawaken.client.model.ally.Integration.SludgeServantModel;
import com.k1sak1.goetyawaken.client.model.ally.Integration.StalkerServantModel;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.SculkCentipedeServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.SculkLeechServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.ShatteredservantRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.ShriekWormServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.SludgeServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.SludgeServantOuterLayer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.StalkerServantRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.k1sak1.goetyawaken.common.compat.ModLoadedUtil;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {
        public static final ModelLayerLocation PALE_GOLEM_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "pale_golem"), "main");

        public static final ModelLayerLocation SILVERFISH_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "silverfish_servant"), "main");

        public static final ModelLayerLocation CREEPER_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "creeper_servant"), "main");

        public static final ModelLayerLocation JITB_ZOMBIE_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "jitb_zombie_servant"), "main");

        public static final ModelLayerLocation ICE_CREEPER_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "ice_creeper_servant"), "main");

        public static final ModelLayerLocation POISONOUS_POTATO_CREEPER_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "poisonous_potato_creeper_servant"), "main");

        public static final ModelLayerLocation STATUE_CREEPER_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "statue_creeper"), "main");

        public static final ModelLayerLocation ICE_CREEPER_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "ice_creeper"), "main");

        public static final ModelLayerLocation ENDERMAN_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "enderman_servant"), "main");

        public static final ModelLayerLocation ENDERMITE_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "endermite_servant"), "main");

        public static final ModelLayerLocation WITHER_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "wither_servant"), "main");

        public static final ModelLayerLocation SHULKER_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "shulker_servant"), "main");

        public static final ModelLayerLocation TORMENTOR_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "tormentor_servant"), "main");

        public static final ModelLayerLocation PREACHER_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "preacher_servant"), "main");

        public static final ModelLayerLocation ENDERSENT_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "endersent_servant"), "main");

        public static final ModelLayerLocation CRONE_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "crone_servant"), "main");

        public static final ModelLayerLocation SORCERER_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "sorcerer_servant"), "main");

        public static final ModelLayerLocation MINISTER_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "minister_servant"), "main");

        public static final ModelLayerLocation VIZIER_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "vizier_servant"), "main");

        public static final ModelLayerLocation VIZIER_CLONE_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "vizier_clone_servant"), "main");

        public static final ModelLayerLocation VIZIER_SERVANT_ARMOR_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "vizier_servant_armor"), "main");

        public static final ModelLayerLocation BLACKGUARD_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "blackguard"), "main");

        public static final ModelLayerLocation VANGUARD_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "vanguard"), "main");

        public static final ModelLayerLocation ROYALGUARD_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "royalguard"), "main");

        public static final ModelLayerLocation TOWER_GUARD_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "tower_guard"), "main");

        public static final ModelLayerLocation WIGHT_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "wight_servant"), "main");

        public static final ModelLayerLocation WRAITH_NECROMANCER_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "wraith_necromancer"), "main");

        public static final ModelLayerLocation MUSHROOM_MONSTROSITY_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "mushroom_monstrosity"), "main");

        public static final ModelLayerLocation ANGRY_MOOSHROOM_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "angry_mooshroom"), "main");

        public static final ModelLayerLocation MUSHROOM_MISSILE_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "mushroom_missile"), "main");

        public static final ModelLayerLocation CAERBANNOG_RABBIT_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "caerbannog_rabbit_servant"), "main");

        public static final ModelLayerLocation ENDER_KEEPER_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "ender_keeper_servant"), "main");

        public static final ModelLayerLocation ENDER_KEEPER_SERVANT_SHADOW_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "ender_keeper_servant_shadow"), "main");

        public static final ModelLayerLocation APOSTLE_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "apostle_servant"), "main");

        public static final ModelLayerLocation MONOLITH = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "monolith"), "main");

        public static final ModelLayerLocation MAID_FAIRY_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "maid_fairy_servant"), "main");

        public static final ModelLayerLocation PARCHED_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "parched"), "main");

        public static final ModelLayerLocation BOULDERING_ZOMBIE_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "bouldering_zombie"), "main");

        public static final ModelLayerLocation SUNKEN_SKELETON_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "sunken_skeleton"), "main");

        public static final ModelLayerLocation PARCHED_NECROMANCER_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "parched_necromancer"), "main");

        public static final ModelLayerLocation PARCHED_NECROMANCER_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "parched_necromancer_servant"), "main");

        public static final ModelLayerLocation NAMELESS_ONE_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "nameless_one"), "main");

        public static final ModelLayerLocation NAMELESS_ONE_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "nameless_one_servant"), "main");

        public static final ModelLayerLocation NAMELESS_ONE_SHADOW_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "nameless_one"), "shadow");

        public static final ModelLayerLocation MEDIUM_PAINTING = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "medium_painting"), "main");

        public static final ModelLayerLocation SMALL_PAINTING = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "small_painting"), "main");

        public static final ModelLayerLocation LARGE_PAINTING = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "large_painting"), "main");

        public static final ModelLayerLocation TALL_PAINTING = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "tall_painting"), "main");

        public static final ModelLayerLocation WIDE_PAINTING = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "wide_painting"), "main");

        public static final ModelLayerLocation MINI_GHAST_HOSTILE = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "mini_ghast_hostile"), "main");

        public static final ModelLayerLocation HOSTILE_SNAPPER_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "hostile_snapper"), "main");

        public static final ModelLayerLocation ILLUSIONER_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "illusioner_servant"), "main");

        public static final ModelLayerLocation ARCH_ILLUSIONER_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "arch_illusioner_servant"), "main");

        public static final ModelLayerLocation ARCH_ILLUSIONER_SERVANT_SHADOW_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "arch_illusioner_servant"), "shadow");

        public static final ModelLayerLocation SKULL_LORD_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "skull_lord_servant"), "main");

        public static final ModelLayerLocation BONE_LORD_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "bone_lord_servant"), "main");

        public static final ModelLayerLocation BOUND_SORCERER_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "bound_sorcerer"), "main");

        public static final ModelLayerLocation VANGUARD_CHAMPION_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "vanguard_champion"), "main");

        public static final ModelLayerLocation DROWNED_NECROMANCER_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "drowned_necromancer"), "main");

        public static final ModelLayerLocation SPIDER_CREEDER_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "spider_creeder_servant"), "main");

        public static final ModelLayerLocation FROZEN_ZOMBIE_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "frozen_zombie"), "main");

        public static final ModelLayerLocation SCARLET_VEX_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "scarlet_vex"), "main");

        public static final ModelLayerLocation TOWER_WRAITH_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "tower_wraith"), "main");

        public static final ModelLayerLocation TOWER_WITCH_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "towerwitchmodel"), "main");

        public static final ModelLayerLocation TOWER_WITCH_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "tower_witch_servant"), "main");

        public static final ModelLayerLocation GIANT_GHAST_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "giant_ghast"), "main");

        public static final ModelLayerLocation RAMPART_CAPTAIN_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "rampart_captain"), "main");

        public static final ModelLayerLocation HOSTILE_RAMPART_CAPTAIN_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "hostile_rampart_captain"), "main");

        public static final ModelLayerLocation RUBY_SORCERER_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "ruby_sorcerer"), "main");

        public static final ModelLayerLocation GIANT_GHAST_FIREBALL_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "giant_ghast_fireball"), "main");

        public static final ModelLayerLocation GIANT_HELL_BLAST_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "giant_hell_blast"), "main");

        public static final ModelLayerLocation HERESIARCH_SERVANT_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "heresiarch_servant"), "main");

        public static final ModelLayerLocation HERESIARCH_SERVANT_SHADOW_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "heresiarch_servant"), "shadow");

        public static final ModelLayerLocation BURNING_SHIELD_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "burning_shield"), "main");

        public static final ModelLayerLocation TOXIFIN_LAYER = new ModelLayerLocation(
                        new ResourceLocation(GoetyAwaken.MODID, "toxifin"), "main");

        @SubscribeEvent
        public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
                event.registerLayerDefinition(SMALL_PAINTING, OminousPaintingModel::createSmallFrameLayer);
                event.registerLayerDefinition(MEDIUM_PAINTING, OminousPaintingModel::createMediumFrameLayer);
                event.registerLayerDefinition(LARGE_PAINTING, OminousPaintingModel::createLargeFrameLayer);
                event.registerLayerDefinition(TALL_PAINTING, OminousPaintingModel::createTallFrameLayer);
                event.registerLayerDefinition(WIDE_PAINTING, OminousPaintingModel::createWideFrameLayer);
                event.registerLayerDefinition(PALE_GOLEM_LAYER, PaleGolemModel::createBodyLayer);
                event.registerLayerDefinition(SILVERFISH_SERVANT_LAYER, SilverfishServantModel::createBodyLayer);
                event.registerLayerDefinition(CREEPER_SERVANT_LAYER, CreeperServantModel::createBodyLayer);
                event.registerLayerDefinition(JITB_ZOMBIE_SERVANT_LAYER, JITBZombieServantModel::createBodyLayer);
                event.registerLayerDefinition(ICE_CREEPER_SERVANT_LAYER, IceCreeperServantModel::createBodyLayer);
                event.registerLayerDefinition(POISONOUS_POTATO_CREEPER_SERVANT_LAYER,
                                PoisonousPotatoCreeperServantModel::createBodyLayer);
                event.registerLayerDefinition(STATUE_CREEPER_LAYER, StatueCreeperModel::createBodyLayer);
                event.registerLayerDefinition(ICE_CREEPER_LAYER, IceCreeperModel::createBodyLayer);
                event.registerLayerDefinition(ENDERMAN_SERVANT_LAYER, EndermanServantModel::createBodyLayer);
                event.registerLayerDefinition(ENDERMITE_SERVANT_LAYER, EndermiteServantModel::createBodyLayer);
                event.registerLayerDefinition(WITHER_SERVANT_LAYER, WitherServantModel::createBodyLayer);
                event.registerLayerDefinition(SHULKER_SERVANT_LAYER, ShulkerServantModel::createBodyLayer);
                event.registerLayerDefinition(TORMENTOR_SERVANT_LAYER, TormentorServantModel::createBodyLayer);
                event.registerLayerDefinition(PREACHER_SERVANT_LAYER, PreacherServantModel::createBodyLayer);
                event.registerLayerDefinition(ENDERSENT_SERVANT_LAYER, EndersentServantModel::createBodyLayer);
                event.registerLayerDefinition(CRONE_SERVANT_LAYER, CroneServantModel::createBodyLayer);
                event.registerLayerDefinition(SORCERER_SERVANT_LAYER, SorcererServantModel::createBodyLayer);
                event.registerLayerDefinition(MINISTER_SERVANT_LAYER, MinisterServantModel::createBodyLayer);
                event.registerLayerDefinition(VIZIER_SERVANT_LAYER, VizierServantModel::createBodyLayer);
                event.registerLayerDefinition(VIZIER_CLONE_SERVANT_LAYER, VizierCloneServantModel::createBodyLayer);
                event.registerLayerDefinition(VIZIER_SERVANT_ARMOR_LAYER, VizierServantModel::createBodyLayer);
                event.registerLayerDefinition(BLACKGUARD_LAYER, ZombieDarkguardModel::createBodyLayer);
                event.registerLayerDefinition(VANGUARD_LAYER, SkeletonVanguardModel::createBodyLayer);
                event.registerLayerDefinition(ROYALGUARD_LAYER, RoyalguardModel::createBodyLayer);
                event.registerLayerDefinition(TOWER_GUARD_LAYER, TowerGuardModel::createBodyLayer);
                event.registerLayerDefinition(WIGHT_SERVANT_LAYER, WightServantModel::createBodyLayer);
                event.registerLayerDefinition(WRAITH_NECROMANCER_LAYER,
                                com.k1sak1.goetyawaken.client.model.undead.necromancer.WraithNecromancerModel::createBodyLayer);
                event.registerLayerDefinition(MUSHROOM_MONSTROSITY_LAYER, MushroomMonstrosityModel::createBodyLayer);
                event.registerLayerDefinition(ANGRY_MOOSHROOM_LAYER, AngryMooshroomModel::createBodyLayer);
                event.registerLayerDefinition(MUSHROOM_MISSILE_LAYER, MushroomMissileModel::createBodyLayer);
                event.registerLayerDefinition(CAERBANNOG_RABBIT_SERVANT_LAYER,
                                CaerbannogRabbitServantModel::createBodyLayer);
                event.registerLayerDefinition(ENDER_KEEPER_SERVANT_LAYER, EnderKeeperServantModel::createBodyLayer);
                event.registerLayerDefinition(ENDER_KEEPER_SERVANT_SHADOW_LAYER,
                                EnderKeeperServantModel::createShadowLayer);
                event.registerLayerDefinition(APOSTLE_SERVANT_LAYER, ApostleServantModel::createBodyLayer);
                event.registerLayerDefinition(MONOLITH,
                                com.k1sak1.goetyawaken.client.model.ObsidianMonolithServantModel::createBodyLayer);
                event.registerLayerDefinition(MAID_FAIRY_SERVANT_LAYER, MaidFairyServantModel::createBodyLayer);
                event.registerLayerDefinition(PARCHED_LAYER, ParchedModel::createBodyLayer);
                event.registerLayerDefinition(SCARLET_VEX_LAYER, ScarletVexModel::createBodyLayer);
                event.registerLayerDefinition(BOULDERING_ZOMBIE_LAYER, BoulderingZombieModel::createBodyLayer);
                event.registerLayerDefinition(SUNKEN_SKELETON_LAYER, SunkenSkeletonModel::createBodyLayer);
                event.registerLayerDefinition(PARCHED_NECROMANCER_LAYER,
                                com.k1sak1.goetyawaken.client.model.undead.necromancer.ParchedNecromancerModel::createBodyLayer);
                event.registerLayerDefinition(PARCHED_NECROMANCER_SERVANT_LAYER,
                                com.k1sak1.goetyawaken.client.model.undead.necromancer.ParchedNecromancerModel::createBodyLayer);
                event.registerLayerDefinition(MINI_GHAST_HOSTILE,
                                com.k1sak1.goetyawaken.client.model.MiniGhastModel::createBodyLayer);
                event.registerLayerDefinition(HOSTILE_SNAPPER_LAYER, HostileSnapperModel::createBodyLayer);
                event.registerLayerDefinition(ILLUSIONER_SERVANT_LAYER, IllusionerServantModel::createBodyLayer);
                event.registerLayerDefinition(ARCH_ILLUSIONER_SERVANT_LAYER, IllusionerServantModel::createBodyLayer);
                event.registerLayerDefinition(ARCH_ILLUSIONER_SERVANT_SHADOW_LAYER,
                                IllusionerServantModel::createShadowLayer);
                event.registerLayerDefinition(NAMELESS_ONE_LAYER,
                                com.k1sak1.goetyawaken.client.model.undead.necromancer.NamelessOneModel::createBodyLayer);
                event.registerLayerDefinition(NAMELESS_ONE_SERVANT_LAYER,
                                com.k1sak1.goetyawaken.client.model.undead.necromancer.NamelessOneModel::createBodyLayer);
                event.registerLayerDefinition(NAMELESS_ONE_SHADOW_LAYER,
                                com.k1sak1.goetyawaken.client.model.undead.necromancer.NamelessOneModel::createShadowLayer);
                event.registerLayerDefinition(SKULL_LORD_SERVANT_LAYER, SkullLordServantModel::createBodyLayer);
                event.registerLayerDefinition(BOUND_SORCERER_LAYER,
                                com.k1sak1.goetyawaken.client.model.undead.BoundSorcererModel::createBodyLayer);
                event.registerLayerDefinition(VANGUARD_CHAMPION_LAYER, VanguardChampionModel::createBodyLayer);
                event.registerLayerDefinition(DROWNED_NECROMANCER_LAYER,
                                com.Polarice3.Goety.client.render.model.DrownedNecromancerModel::createBodyLayer);
                event.registerLayerDefinition(SPIDER_CREEDER_LAYER, SpiderCreederModel::createBodyLayer);
                event.registerLayerDefinition(FROZEN_ZOMBIE_LAYER,
                                com.Polarice3.Goety.client.render.model.PlayerZombieModel::createBodyLayer);
                event.registerLayerDefinition(TOWER_WRAITH_LAYER,
                                com.k1sak1.goetyawaken.client.model.undead.TowerWraithModel::createBodyLayer);
                event.registerLayerDefinition(TOWER_WITCH_LAYER,
                                com.k1sak1.goetyawaken.client.model.illager.TowerWitchModel::createBodyLayer);
                event.registerLayerDefinition(TOWER_WITCH_SERVANT_LAYER,
                                com.k1sak1.goetyawaken.client.model.illager.TowerWitchModel::createBodyLayer);
                event.registerLayerDefinition(GIANT_GHAST_LAYER, GiantGhastModel::createBodyLayer);
                event.registerLayerDefinition(RAMPART_CAPTAIN_LAYER, RampartCaptainModel::createBodyLayer);
                event.registerLayerDefinition(HOSTILE_RAMPART_CAPTAIN_LAYER, RampartCaptainModel::createBodyLayer);
                event.registerLayerDefinition(RUBY_SORCERER_LAYER, SorcererModel::createBodyLayer);
                event.registerLayerDefinition(GIANT_GHAST_FIREBALL_LAYER,
                                com.Polarice3.Goety.client.render.model.HellBlastModel::createBodyLayer);
                event.registerLayerDefinition(GIANT_HELL_BLAST_LAYER,
                                com.Polarice3.Goety.client.render.model.HellBlastModel::createBodyLayer);
                event.registerLayerDefinition(HERESIARCH_SERVANT_LAYER,
                                com.k1sak1.goetyawaken.client.model.HeresiarchServantModel::createBodyLayer);
                event.registerLayerDefinition(HERESIARCH_SERVANT_SHADOW_LAYER,
                                com.k1sak1.goetyawaken.client.model.HeresiarchServantModel::createShadowLayer);
                event.registerLayerDefinition(BURNING_SHIELD_LAYER, BurningShieldModel::createBodyLayer);
                event.registerLayerDefinition(TOXIFIN_LAYER, ToxifinModel::createBodyLayer);

                if (ModLoadedUtil.isModLoaded(ModLoadedUtil.MASQUERADER)) {
                        event.registerLayerDefinition(
                                        com.k1sak1.goetyawaken.client.model.ally.Integration.MasqueraderServantModel.LAYER_LOCATION,
                                        com.k1sak1.goetyawaken.client.model.ally.Integration.MasqueraderServantModel::createBodyLayer);
                }

                if (ModLoadedUtil.isModLoaded(ModLoadedUtil.MEET_YOUR_FIGHT)) {
                        event.registerLayerDefinition(SwampjawServantModel.MODEL,
                                        SwampjawServantModel::createBodyLayer);
                        event.registerLayerDefinition(SwampMineModel.MODEL, SwampMineModel::createBodyLayer);
                        event.registerLayerDefinition(BellringerServantModel.MODEL,
                                        () -> BellringerServantModel.createBodyLayer(
                                                        new net.minecraft.client.model.geom.builders.CubeDeformation(
                                                                        0)));
                        event.registerLayerDefinition(RosalyneServantModel.MODEL,
                                        () -> RosalyneServantModel.createBodyLayer(
                                                        new net.minecraft.client.model.geom.builders.CubeDeformation(0),
                                                        true));
                        event.registerLayerDefinition(RosalyneServantModel.MODEL_ARMOR,
                                        () -> RosalyneServantModel.createBodyLayer(
                                                        new net.minecraft.client.model.geom.builders.CubeDeformation(2),
                                                        false));
                        event.registerLayerDefinition(RosalyneServantModel.MODEL_HEAD,
                                        SkullModel::createHumanoidHeadLayer);
                        event.registerLayerDefinition(RoseSpiritServantModel.MODEL,
                                        RoseSpiritServantModel::createBodyLayer);
                        event.registerLayerDefinition(DameFortunaServantModel.MODEL,
                                        () -> DameFortunaServantModel.createBodyLayer(
                                                        new net.minecraft.client.model.geom.builders.CubeDeformation(
                                                                        0)));
                        event.registerLayerDefinition(DameFortunaServantModel.MODEL_ARMOR,
                                        () -> DameFortunaServantModel.createBodyLayer(
                                                        new net.minecraft.client.model.geom.builders.CubeDeformation(
                                                                        2)));
                        event.registerLayerDefinition(DameFortunaServantModel.MODEL_HEAD,
                                        SkullModel::createHumanoidHeadLayer);
                        event.registerLayerDefinition(FortunaDameBombModel.MODEL,
                                        FortunaDameBombModel::createBodyLayer);
                        event.registerLayerDefinition(FortunaDameCardModel.MODEL,
                                        FortunaDameCardModel::createBodyLayer);
                }

                if (ModLoadedUtil.isModLoaded(ModLoadedUtil.DEEPER_DARKER)) {
                        event.registerLayerDefinition(SculkCentipedeServantRenderer.MODEL,
                                        SculkCentipedeServantModel::createBodyModel);
                        event.registerLayerDefinition(SculkLeechServantRenderer.MODEL,
                                        SculkLeechServantModel::createBodyModel);
                        event.registerLayerDefinition(ShatteredservantRenderer.MODEL,
                                        ShatteredServantModel::createBodyModel);
                        event.registerLayerDefinition(ShriekWormServantRenderer.MODEL,
                                        ShriekWormServantModel::createBodyModel);
                        event.registerLayerDefinition(SludgeServantRenderer.MODEL,
                                        SludgeServantModel::createInnerBodyModel);
                        event.registerLayerDefinition(SludgeServantOuterLayer.OUTER_MODEL,
                                        SludgeServantModel::createOuterBodyModel);
                        event.registerLayerDefinition(StalkerServantRenderer.MODEL,
                                        StalkerServantModel::createBodyModel);
                }
        }

        private static boolean layersAdded = false;

        @SubscribeEvent
        public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
                if (layersAdded) {
                        return;
                }
                layersAdded = true;

                event.getSkins().forEach(skin -> {
                        if (event.getSkin(skin) != null) {
                                event.getSkin(skin).addLayer(new AncientGlintLayer(event.getSkin(skin)));
                        }
                });

                Minecraft.getInstance().getEntityRenderDispatcher().renderers.values().forEach(renderer -> {
                        if (renderer instanceof net.minecraft.client.renderer.entity.LivingEntityRenderer) {
                                ((net.minecraft.client.renderer.entity.LivingEntityRenderer<?, ?>) renderer)
                                                .addLayer(new AncientGlintLayer(
                                                                (net.minecraft.client.renderer.entity.LivingEntityRenderer<?, ?>) renderer));
                        }
                });
        }
}