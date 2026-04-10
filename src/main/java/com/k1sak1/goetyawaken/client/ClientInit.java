package com.k1sak1.goetyawaken.client;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.BoulderClusterModel;
import com.k1sak1.goetyawaken.client.model.armor.*;
import com.k1sak1.goetyawaken.client.renderer.*;
import com.k1sak1.goetyawaken.client.renderer.illager.*;
import com.k1sak1.goetyawaken.client.screen.EnderAccessLecternScreen;
import com.k1sak1.goetyawaken.client.renderer.undead.skeleton.SkeletonVanguardRenderer;
import com.k1sak1.goetyawaken.client.renderer.undead.skeleton.SunkenSkeletonRenderer;
import com.k1sak1.goetyawaken.client.renderer.undead.skeleton.ParchedRenderer;
import com.k1sak1.goetyawaken.client.renderer.undead.WightServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.undead.zombie.ZombieDarkguardRenderer;
import com.k1sak1.goetyawaken.client.renderer.undead.zombie.BoulderingZombieRenderer;
import com.k1sak1.goetyawaken.client.renderer.undead.zombie.JungleZombieRenderer;
import com.k1sak1.goetyawaken.client.renderer.undead.zombie.FrozenZombieRenderer;
import com.k1sak1.goetyawaken.client.renderer.undead.necromancer.*;
import com.k1sak1.goetyawaken.client.renderer.undead.necromancer.HostileDrownedNecromancerRenderer;
import com.k1sak1.goetyawaken.client.renderer.undead.TowerWraithRenderer;
import com.k1sak1.goetyawaken.client.renderer.util.*;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.MaidFairyServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.undead.SkullLordServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.undead.BoneLordServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.undead.ScarletVexRenderer;
import com.k1sak1.goetyawaken.client.renderer.block.NamelessChestRenderer;
import com.k1sak1.goetyawaken.client.renderer.block.DarkMenderRenderer;
import com.k1sak1.goetyawaken.client.renderer.block.ModBlockLayer;
import com.k1sak1.goetyawaken.client.renderer.block.MushroomMonstrosityHeadBlockEntityRenderer;
import com.k1sak1.goetyawaken.client.model.MushroomMonstrosityHeadModel;
import com.k1sak1.goetyawaken.common.blocks.ModBlockEntities;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.init.ModContainerTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInit {

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
                event.registerLayerDefinition(MushroomHatModel.LAYER_LOCATION, MushroomHatModel::createBodyLayer);
                event.registerLayerDefinition(ChampionArmorModel.CHAMPION_ARMOR_OUTER_LAYER,
                                ChampionArmorModel::createOuterArmorLayer);
                event.registerLayerDefinition(DeathRayRenderer.MODEL_LAYER_LOCATION, DeathRayRenderer::createBodyLayer);
                event.registerLayerDefinition(ModBlockLayer.NAMELESS_CHEST, NamelessChestRenderer::createBodyLayer);
                event.registerLayerDefinition(BoulderClusterModel.LAYER_LOCATION, BoulderClusterModel::createBodyLayer);
                event.registerLayerDefinition(ModBlockLayer.MOOSHROOM_MONSTROSITY_HEAD,
                                MushroomMonstrosityHeadModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void onRegisterRenders(EntityRenderersEvent.RegisterRenderers event) {
                event.registerBlockEntityRenderer(ModBlockEntities.TRIAL_SPAWNER.get(), TrialSpawnerRenderer::new);
                event.registerBlockEntityRenderer(ModBlockEntities.VAULT.get(), VaultRenderer::new);
                event.registerBlockEntityRenderer(ModBlockEntities.NAMELESS_CHEST.get(), NamelessChestRenderer::new);
                event.registerBlockEntityRenderer(ModBlockEntities.DARK_MENDER.get(), DarkMenderRenderer::new);
                event.registerBlockEntityRenderer(ModBlockEntities.MOOSHROOM_MONSTROSITY_HEAD.get(),
                                MushroomMonstrosityHeadBlockEntityRenderer::new);
                event.registerEntityRenderer(ModEntityType.MOD_SWORD_PROJECTILE.get(),
                                (rendererManager) -> new ModSwordProjectileRenderer(rendererManager,
                                                net.minecraft.client.Minecraft.getInstance().getItemRenderer(), 1.0F,
                                                true));
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
                event.enqueueWork(() -> {
                        MenuScreens.register(ModContainerTypes.ENDER_ACCESS_LECTERN.get(),
                                        EnderAccessLecternScreen::new);

                        EntityRenderers.register(ModEntityType.PALE_GOLEM_SERVANT.get(), PaleGolemRenderer::new);
                        EntityRenderers.register(ModEntityType.SILVERFISH_SERVANT.get(),
                                        SilverfishServantRenderer::new);
                        EntityRenderers.register(ModEntityType.CREEPER_SERVANT.get(), CreeperServantRenderer::new);
                        EntityRenderers.register(ModEntityType.ICE_CREEPER_SERVANT.get(),
                                        IceCreeperServantRenderer::new);
                        EntityRenderers.register(ModEntityType.ICE_CREEPER.get(),
                                        IceCreeperRenderer::new);
                        EntityRenderers.register(ModEntityType.ENDERMAN_SERVANT.get(), EndermanServantRenderer::new);
                        EntityRenderers.register(ModEntityType.SHULKER_SERVANT.get(), ShulkerServantRenderer::new);
                        EntityRenderers.register(ModEntityType.ENDERMITE_SERVANT.get(), EndermiteServantRenderer::new);
                        EntityRenderers.register(ModEntityType.WARDEN_SERVANT.get(), WardenServantRenderer::new);
                        EntityRenderers.register(ModEntityType.WITHER_SERVANT.get(), WitherServantRenderer::new);
                        EntityRenderers.register(ModEntityType.PREACHER_SERVANT.get(), PreacherServantRenderer::new);
                        EntityRenderers.register(ModEntityType.TORMENTOR_SERVANT.get(), TormentorServantRenderer::new);
                        EntityRenderers.register(ModEntityType.ENVIOKER_SERVANT.get(), EnviokerServantRenderer::new);
                        EntityRenderers.register(ModEntityType.ENDERSENT_SERVANT.get(), EndersentServantRenderer::new);
                        EntityRenderers.register(ModEntityType.CRONE_SERVANT.get(), CroneServantRenderer::new);
                        EntityRenderers.register(ModEntityType.MINISTER_SERVANT.get(), MinisterServantRenderer::new);
                        EntityRenderers.register(ModEntityType.SORCERER_SERVANT.get(), SorcererServantRenderer::new);
                        EntityRenderers.register(ModEntityType.MOD_SHULKER_BULLET.get(), ModShulkerBulletRenderer::new);
                        EntityRenderers.register(ModEntityType.MOD_WITHER_SKULL_NO_BLOCK_BREAK.get(),
                                        ModWitherSkullNoBlockBreakRenderer::new);
                        EntityRenderers.register(ModEntityType.EXPLOSIVE_ARROW.get(), ExplosiveArrowRenderer::new);
                        EntityRenderers.register(ModEntityType.ECHOING_STRIKE.get(), EchoingStrikeRenderer::new);
                        EntityRenderers.register(ModEntityType.VIZIER_SERVANT.get(), VizierServantRenderer::new);
                        EntityRenderers.register(ModEntityType.VIZIER_CLONE_SERVANT.get(),
                                        VizierCloneServantRenderer::new);
                        EntityRenderers.register(ModEntityType.ROYALGUARD_SERVANT.get(),
                                        RoyalguardServantRenderer::new);
                        EntityRenderers.register(ModEntityType.HOSTILE_ROYALGUARD.get(),
                                        com.k1sak1.goetyawaken.client.renderer.illager.HostileRoyalguardRenderer::new);
                        EntityRenderers.register(ModEntityType.ZOMBIE_DARKGUARD.get(), ZombieDarkguardRenderer::new);
                        EntityRenderers.register(ModEntityType.SKELETON_VANGUARD.get(), SkeletonVanguardRenderer::new);
                        EntityRenderers.register(ModEntityType.VANGUARD_CHAMPION.get(), VanguardChampionRenderer::new);
                        EntityRenderers.register(ModEntityType.HOSTILE_VANGUARD_CHAMPION.get(),
                                        VanguardChampionRenderer::new);
                        EntityRenderers.register(ModEntityType.PARCHED.get(), ParchedRenderer::new);
                        EntityRenderers.register(ModEntityType.PARCHED_SERVANT.get(), ParchedServantRenderer::new);
                        EntityRenderers.register(ModEntityType.SUNKEN_SKELETON.get(), SunkenSkeletonRenderer::new);
                        EntityRenderers.register(ModEntityType.WIGHT_SERVANT.get(), WightServantRenderer::new);
                        EntityRenderers.register(ModEntityType.SILVERFISH_EGG.get(), SilverfishEggRenderer::new);
                        EntityRenderers.register(ModEntityType.ENDERMITE_EGG.get(), EndermiteEggRenderer::new);
                        EntityRenderers.register(ModEntityType.GHOST_FIRE_BOLT.get(), GhostFireBoltRenderer::new);
                        EntityRenderers.register(ModEntityType.WRAITH_NECROMANCER.get(),
                                        WraithNecromancerRenderer::new);
                        EntityRenderers.register(ModEntityType.PARCHED_NECROMANCER.get(),
                                        ParchedNecromancerRenderer::new);
                        EntityRenderers.register(ModEntityType.HOSTILE_DROWNED_NECROMANCER.get(),
                                        HostileDrownedNecromancerRenderer::new);
                        EntityRenderers.register(ModEntityType.BOULDERING_ZOMBIE.get(),
                                        BoulderingZombieRenderer::new);
                        EntityRenderers.register(ModEntityType.PARCHED_NECROMANCER_SERVANT.get(),
                                        com.k1sak1.goetyawaken.client.renderer.ally.undead.necromancer.ParchedNecromancerServantRenderer::new);
                        EntityRenderers.register(ModEntityType.NAMELESS_ONE.get(),
                                        NamelessOneRenderer::new);
                        EntityRenderers.register(ModEntityType.NAMELESS_ONE_SERVANT.get(),
                                        com.k1sak1.goetyawaken.client.renderer.ally.undead.necromancer.NamelessOneServantRenderer::new);
                        EntityRenderers.register(ModEntityType.WRAITH_NECROMANCER_SERVANT.get(),
                                        com.k1sak1.goetyawaken.client.renderer.ally.undead.skeleton.WraithNecromancerServantRenderer::new);
                        EntityRenderers.register(ModEntityType.ANGRY_MOOSHROOM.get(), AngryMooshroomRenderer::new);
                        EntityRenderers.register(ModEntityType.HOSTILE_ANGRY_MOOSHROOM.get(),
                                        AngryMooshroomRenderer::new);
                        EntityRenderers.register(ModEntityType.MUSHROOM_MONSTROSITY.get(),
                                        MushroomMonstrosityRenderer::new);
                        EntityRenderers.register(ModEntityType.HOSTILE_MUSHROOM_MONSTROSITY.get(),
                                        MushroomMonstrosityRenderer::new);
                        EntityRenderers.register(ModEntityType.MUSHROOM_MISSILE.get(), MushroomMissileRenderer::new);
                        EntityRenderers.register(ModEntityType.CAERBANNOG_RABBIT_SERVANT.get(),
                                        CaerbannogRabbitServantRenderer::new);
                        EntityRenderers.register(ModEntityType.ENDER_KEEPER_SERVANT.get(),
                                        EnderKeeperServantRenderer::new);
                        EntityRenderers.register(ModEntityType.APOSTLE_SERVANT.get(), ApostleServantRenderer::new);
                        EntityRenderers.register(ModEntityType.VINDICATOR_CHEF.get(), VindicatorChefRenderer::new);
                        EntityRenderers.register(ModEntityType.OBSIDIAN_MONOLITH_SERVANT.get(),
                                        ObsidianMonolithServantRenderer::new);
                        EntityRenderers.register(ModEntityType.SUMMON_APOSTLE_SERVANT.get(),
                                        SummonApostleServantRenderer::new);
                        EntityRenderers.register(ModEntityType.OMINOUS_PAINTING.get(),
                                        com.k1sak1.goetyawaken.client.renderer.OminousPaintingRenderer::new);
                        EntityRenderers.register(ModEntityType.HOSTILE_GNASHER.get(),
                                        HostileGnasherRenderer::new);
                        EntityRenderers.register(ModEntityType.HOSTILE_TROPICAL_SLIME.get(),
                                        HostileTropicalSlimeRenderer::new);
                        EntityRenderers.register(ModEntityType.HOSTILE_MINI_GHAST.get(),
                                        MiniGhastHostileRenderer::new);
                        EntityRenderers.register(ModEntityType.JUNGLE_ZOMBIE.get(),
                                        JungleZombieRenderer::new);
                        EntityRenderers.register(ModEntityType.HOSTILE_WILDFIRE.get(),
                                        HostileWildfireRenderer::new);
                        EntityRenderers.register(ModEntityType.FROST_SCYTHE_SLASH.get(),
                                        FrostScytheSlashRenderer::new);
                        EntityRenderers.register(ModEntityType.OMINOUS_EYE.get(),
                                        OminousEyeEntityRenderer::new);
                        EntityRenderers.register(ModEntityType.PRISON_EYE.get(),
                                        PrisonEyeEntityRenderer::new);
                        EntityRenderers.register(ModEntityType.MIRAGE_EYE.get(),
                                        MirageEyeEntityRenderer::new);
                        EntityRenderers.register(ModEntityType.DESERT_PLAGUES_CLOUD.get(),
                                        DesertPlaguesCloudRenderer::new);
                        EntityRenderers.register(ModEntityType.HOSTILE_SNAPPER.get(),
                                        HostileSnapperRenderer::new);
                        EntityRenderers.register(ModEntityType.FROZEN_ZOMBIE.get(),
                                        FrozenZombieRenderer::new);
                        EntityRenderers.register(ModEntityType.ILLUSIONER_SERVANT.get(),
                                        IllusionerServantRenderer::new);
                        EntityRenderers.register(ModEntityType.ARCH_ILLUSIONER_SERVANT.get(),
                                        ArchIllusionerServantRenderer::new);
                        EntityRenderers.register(ModEntityType.ARCH_ILLUSIONER.get(),
                                        ArchIllusionerServantRenderer::new);
                        EntityRenderers.register(ModEntityType.DEATH_FIRE.get(), DeathFireRenderer::new);
                        EntityRenderers.register(ModEntityType.DEATH_RAY.get(), DeathRayRenderer::new);
                        EntityRenderers.register(ModEntityType.PURE_LIGHT.get(), PureLightRenderer::new);
                        EntityRenderers.register(ModEntityType.SKULL_LORD_SERVANT.get(), SkullLordServantRenderer::new);
                        EntityRenderers.register(ModEntityType.BONE_LORD_SERVANT.get(), BoneLordServantRenderer::new);
                        EntityRenderers.register(ModEntityType.BOUND_SORCERER.get(),
                                        com.k1sak1.goetyawaken.client.renderer.undead.BoundSorcererRenderer::new);
                        EntityRenderers.register(ModEntityType.SPIDER_CREEDER.get(), SpiderCreederRenderer::new);
                        EntityRenderers.register(ModEntityType.HOSTILE_SPIDER_CREEDER.get(),
                                        SpiderCreederRenderer::new);
                        EntityRenderers.register(ModEntityType.SCARLET_VEX.get(), ScarletVexRenderer::new);
                        EntityRenderers.register(ModEntityType.TOWER_WRAITH_SERVANT.get(), TowerWraithRenderer::new);
                        EntityRenderers.register(ModEntityType.HOSTILE_TOWER_WRAITH.get(), TowerWraithRenderer::new);
                        EntityRenderers.register(ModEntityType.HOSTILE_TWILIGHT_GOAT.get(),
                                        HostileTwilightGoatRenderer::new);
                        EntityRenderers.register(ModEntityType.TOWER_WITCH.get(),
                                        TowerWitchRenderer::new);
                        EntityRenderers.register(ModEntityType.TOWER_WITCH_SERVANT.get(),
                                        TowerWitchServantRenderer::new);
                        EntityRenderers.register(ModEntityType.CORRUPTED_SLIME.get(),
                                        CorruptedSlimeRenderer::new);
                        EntityRenderers.register(ModEntityType.CORRUPTED_SOUL_BOLT.get(),
                                        CorruptedSoulBoltRenderer::new);
                        EntityRenderers.register(ModEntityType.BOULDER_CLUSTER.get(), BoulderClusterRenderer::new);
                        if (com.k1sak1.goetyawaken.common.compat.touhoulittlemaid.TouhouLittleMaidLoaded.TOUHOULITTLEMAID
                                        .isLoaded()) {
                                EntityRenderers.register(ModEntityType.MAID_FAIRY_SERVANT.get(),
                                                MaidFairyServantRenderer::new);
                        }
                });
        }
}