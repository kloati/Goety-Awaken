package com.k1sak1.goetyawaken.client;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.BoulderClusterModel;
import com.k1sak1.goetyawaken.client.model.armor.*;
import com.k1sak1.goetyawaken.client.renderer.*;
import com.k1sak1.goetyawaken.client.renderer.JITBZombieServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.GiantServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.PoisonousPotatoZombieServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.PoisonousPotatoSkeletonServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.ToxifinServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.PlaguewhaleSlabServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.entity.projectile.FlyingAxeRenderer;
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
import com.k1sak1.goetyawaken.client.renderer.GiantHellBlastRenderer;
import com.k1sak1.goetyawaken.client.renderer.util.*;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.MaidFairyServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.MasqueraderServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.SwampjawServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.SwampMineRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.BellringerServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.DameFortunaServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.FortunaDameBombRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.FortunaDameCardRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.RosalyneServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.RoseSpiritServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.ArcherServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.SkirmisherServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.LegionerServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.SculkCentipedeServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.SculkLeechServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.ShatteredservantRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.ShriekWormServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.SludgeServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.ally.Integration.StalkerServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.undead.SkullLordServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.undead.BoneLordServantRenderer;
import com.k1sak1.goetyawaken.client.renderer.undead.ScarletVexRenderer;
import com.k1sak1.goetyawaken.client.renderer.block.NamelessChestRenderer;
import com.k1sak1.goetyawaken.client.renderer.block.DarkMenderRenderer;
import com.k1sak1.goetyawaken.client.renderer.block.ModBlockLayer;
import com.k1sak1.goetyawaken.client.renderer.block.MushroomMonstrosityHeadBlockEntityRenderer;
import com.k1sak1.goetyawaken.client.renderer.block.TowerKeeperStatueRenderer;
import com.k1sak1.goetyawaken.client.renderer.block.CreeperStatueRenderer;
import com.k1sak1.goetyawaken.client.renderer.block.VillagerStatueRenderer;
import com.k1sak1.goetyawaken.client.renderer.block.WildfireStatueRenderer;
import com.k1sak1.goetyawaken.client.renderer.block.GargoyleStatueRenderer;
import com.k1sak1.goetyawaken.client.renderer.block.SpikeTrapBlockRenderer;
import com.k1sak1.goetyawaken.client.renderer.StatueCreeperRenderer;
import com.k1sak1.goetyawaken.client.model.MushroomMonstrosityHeadModel;
import com.k1sak1.goetyawaken.client.model.TowerKeeperStatueModel;
import com.k1sak1.goetyawaken.client.model.WildfireStatueModel;
import com.k1sak1.goetyawaken.client.model.GargoyleStatueModel;
import com.k1sak1.goetyawaken.client.model.StatueCreeperModel;
import com.k1sak1.goetyawaken.client.model.SpikeTrapBlockModel;
import com.k1sak1.goetyawaken.common.blocks.ModBlockEntities;
import com.k1sak1.goetyawaken.common.ModIntegrationRegistry;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.init.ModContainerTypes;
import com.k1sak1.goetyawaken.client.typography.GATextPipeline;
import com.k1sak1.goetyawaken.client.typography.effects.GAErosionHandler;
import net.minecraft.client.gui.screens.MenuScreens;
import com.Polarice3.Goety.client.render.WearRenderer;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.MiscCuriosModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import com.k1sak1.goetyawaken.common.compat.ModLoadedUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

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
                event.registerLayerDefinition(TowerKeeperStatueModel.LAYER_LOCATION,
                                TowerKeeperStatueModel::createBodyLayer);
                event.registerLayerDefinition(SpikeTrapBlockModel.LAYER_LOCATION,
                                SpikeTrapBlockModel::createBodyLayer);
                event.registerLayerDefinition(StatueCreeperModel.LAYER_LOCATION,
                                StatueCreeperModel::createBodyLayer);
                event.registerLayerDefinition(WildfireStatueModel.LAYER_LOCATION,
                                WildfireStatueModel::createBodyLayer);
                event.registerLayerDefinition(GargoyleStatueModel.LAYER_LOCATION,
                                GargoyleStatueModel::createBodyLayer);
                event.registerLayerDefinition(com.k1sak1.goetyawaken.client.model.PotatoStaffModel.LAYER_LOCATION,
                                com.k1sak1.goetyawaken.client.model.PotatoStaffModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void onRegisterRenders(EntityRenderersEvent.RegisterRenderers event) {
                event.registerBlockEntityRenderer(ModBlockEntities.TRIAL_SPAWNER.get(), TrialSpawnerRenderer::new);
                event.registerBlockEntityRenderer(ModBlockEntities.VAULT.get(), VaultRenderer::new);
                event.registerBlockEntityRenderer(ModBlockEntities.NAMELESS_CHEST.get(), NamelessChestRenderer::new);
                event.registerBlockEntityRenderer(ModBlockEntities.DARK_MENDER.get(), DarkMenderRenderer::new);
                event.registerBlockEntityRenderer(ModBlockEntities.MOOSHROOM_MONSTROSITY_HEAD.get(),
                                MushroomMonstrosityHeadBlockEntityRenderer::new);
                event.registerBlockEntityRenderer(ModBlockEntities.TOWER_KEEPER_STATUE.get(),
                                TowerKeeperStatueRenderer::new);
                event.registerBlockEntityRenderer(ModBlockEntities.CREEPER_STATUE.get(),
                                CreeperStatueRenderer::new);
                event.registerBlockEntityRenderer(ModBlockEntities.VILLAGER_STATUE.get(),
                                VillagerStatueRenderer::new);
                event.registerBlockEntityRenderer(ModBlockEntities.WILDFIRE_STATUE.get(),
                                WildfireStatueRenderer::new);
                event.registerBlockEntityRenderer(ModBlockEntities.GARGOYLE_STATUE.get(),
                                GargoyleStatueRenderer::new);
                event.registerBlockEntityRenderer(ModBlockEntities.SPIKE_TRAP_BLOCK.get(),
                                SpikeTrapBlockRenderer::new);
                event.registerEntityRenderer(ModEntityType.MOD_SWORD_PROJECTILE.get(),
                                (rendererManager) -> new ModSwordProjectileRenderer(rendererManager,
                                                net.minecraft.client.Minecraft.getInstance().getItemRenderer(), 1.0F,
                                                true));
        }

        @SubscribeEvent
        public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
                event.registerSpriteSet(com.k1sak1.goetyawaken.init.ModParticleTypes.RING.get(),
                                com.k1sak1.goetyawaken.client.particle.RingParticle.RingFactory::new);
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
                event.enqueueWork(() -> {
                        MenuScreens.register(ModContainerTypes.ENDER_ACCESS_LECTERN.get(),
                                        EnderAccessLecternScreen::new);

                        GATextPipeline.registerHandler(new GAErosionHandler());

                        EntityRenderers.register(ModEntityType.PALE_GOLEM_SERVANT.get(), PaleGolemRenderer::new);
                        EntityRenderers.register(ModEntityType.SILVERFISH_SERVANT.get(),
                                        SilverfishServantRenderer::new);
                        EntityRenderers.register(ModEntityType.CREEPER_SERVANT.get(), CreeperServantRenderer::new);
                        EntityRenderers.register(ModEntityType.JITB_ZOMBIE_SERVANT.get(),
                                        JITBZombieServantRenderer::new);
                        EntityRenderers.register(ModEntityType.GIANT_SERVANT.get(),
                                        GiantServantRenderer::new);
                        EntityRenderers.register(ModEntityType.POISONOUS_POTATO_ZOMBIE_SERVANT.get(),
                                        PoisonousPotatoZombieServantRenderer::new);
                        EntityRenderers.register(ModEntityType.POISONOUS_POTATO_SKELETON_SERVANT.get(),
                                        PoisonousPotatoSkeletonServantRenderer::new);
                        EntityRenderers.register(ModEntityType.STATUE_CREEPER.get(),
                                        StatueCreeperRenderer::new);
                        EntityRenderers.register(ModEntityType.HOSTILE_STATUE_CREEPER.get(),
                                        StatueCreeperRenderer::new);
                        EntityRenderers.register(ModEntityType.POISONOUS_POTATO_CREEPER_SERVANT.get(),
                                        PoisonousPotatoCreeperServantRenderer::new);
                        EntityRenderers.register(ModEntityType.TOXIFIN_SERVANT.get(),
                                        (context) -> new ToxifinServantRenderer(context,
                                                        com.k1sak1.goetyawaken.client.ClientEventHandler.TOXIFIN_LAYER));
                        EntityRenderers.register(ModEntityType.PLAGUEWHALE_SLAB_SERVANT.get(),
                                        (context) -> new PlaguewhaleSlabServantRenderer(context,
                                                        com.k1sak1.goetyawaken.client.ClientEventHandler.TOXIFIN_LAYER));
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
                        EntityRenderers.register(ModEntityType.GIANT_GHAST_FIREBALL.get(),
                                        GiantGhastFireballRenderer::new);
                        EntityRenderers.register(ModEntityType.GIANT_HELL_BLAST.get(),
                                        GiantHellBlastRenderer::new);
                        EntityRenderers.register(ModEntityType.TRACKING_FIREBALL.get(),
                                        TrackingFireballRenderer::new);
                        EntityRenderers.register(ModEntityType.ECHOING_STRIKE.get(), EchoingStrikeRenderer::new);
                        EntityRenderers.register(ModEntityType.VIZIER_SERVANT.get(), VizierServantRenderer::new);
                        EntityRenderers.register(ModEntityType.VIZIER_CLONE_SERVANT.get(),
                                        VizierCloneServantRenderer::new);
                        EntityRenderers.register(ModEntityType.ROYALGUARD_SERVANT.get(),
                                        RoyalguardServantRenderer::new);
                        EntityRenderers.register(ModEntityType.HOSTILE_ROYALGUARD.get(),
                                        com.k1sak1.goetyawaken.client.renderer.illager.HostileRoyalguardRenderer::new);
                        EntityRenderers.register(ModEntityType.TOWER_GUARD_SERVANT.get(),
                                        com.k1sak1.goetyawaken.client.renderer.illager.TowerGuardServantRenderer::new);
                        EntityRenderers.register(ModEntityType.HOSTILE_TOWER_GUARD.get(),
                                        com.k1sak1.goetyawaken.client.renderer.illager.HostileTowerGuardRenderer::new);
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
                        EntityRenderers.register(ModEntityType.GIANT_GHAST.get(),
                                        GiantGhastRenderer::new);
                        EntityRenderers.register(ModEntityType.HOSTILE_GIANT_GHAST.get(),
                                        GiantGhastRenderer::new);
                        EntityRenderers.register(ModEntityType.RAMPART_CAPTAIN.get(),
                                        com.k1sak1.goetyawaken.client.renderer.RampartCaptainRenderer::new);
                        EntityRenderers.register(ModEntityType.HOSTILE_RAMPART_CAPTAIN.get(),
                                        com.k1sak1.goetyawaken.client.renderer.HostileRampartCaptainRenderer::new);
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
                                        ArchIllusionerRenderer::new);
                        EntityRenderers.register(ModEntityType.DEATH_FIRE.get(), DeathFireRenderer::new);
                        EntityRenderers.register(ModEntityType.DEATH_RAY.get(), DeathRayRenderer::new);
                        EntityRenderers.register(ModEntityType.PURE_LIGHT.get(), PureLightRenderer::new);
                        EntityRenderers.register(ModEntityType.MUSHROOM_SCATTER_BOMB.get(),
                                        com.Polarice3.Goety.client.render.ScatterBombRenderer::new);
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
                        EntityRenderers.register(ModEntityType.NAMELESS_BOLT.get(), NamelessBoltRenderer::new);
                        EntityRenderers.register(ModEntityType.FLYING_AXE.get(), FlyingAxeRenderer::new);
                        EntityRenderers.register(ModEntityType.RUBY_SORCERER.get(),
                                        com.k1sak1.goetyawaken.client.renderer.illager.RubySorcererRenderer::new);
                        EntityRenderers.register(ModEntityType.MOUNTAINEER.get(),
                                        com.k1sak1.goetyawaken.client.renderer.MountaineerRenderer::new);
                        EntityRenderers.register(ModEntityType.WIND_CALLER.get(),
                                        com.k1sak1.goetyawaken.client.renderer.WindCallerRenderer::new);
                        EntityRenderers.register(ModEntityType.HERESIARCH_SERVANT.get(),
                                        com.k1sak1.goetyawaken.client.renderer.illager.HeresiarchServantRenderer::new);
                        EntityRenderers.register(ModEntityType.SPRITES.get(),
                                        com.k1sak1.goetyawaken.client.renderer.SpritesRenderer::new);
                        EntityRenderers.register(ModEntityType.BURNING_SHIELD.get(),
                                        BurningShieldRenderer::new);
                        if (ModLoadedUtil.isModLoaded(ModLoadedUtil.TOUHOU_LITTLE_MAID)) {
                                EntityRenderers.register(ModIntegrationRegistry.MAID_FAIRY_SERVANT.get(),
                                                MaidFairyServantRenderer::new);
                        }

                        if (ModLoadedUtil.isModLoaded(ModLoadedUtil.MASQUERADER)) {
                                EntityRenderers.register(ModIntegrationRegistry.MASQUERADER_SERVANT.get(),
                                                MasqueraderServantRenderer::new);
                                EntityRenderers.register(ModIntegrationRegistry.MASQUERADER_SERVANT_CLONE.get(),
                                                MasqueraderServantRenderer::new);
                        }

                        if (ModLoadedUtil.isModLoaded(ModLoadedUtil.MEET_YOUR_FIGHT)) {
                                EntityRenderers.register(ModIntegrationRegistry.SWAMPJAW_SERVANT.get(),
                                                SwampjawServantRenderer::new);
                                EntityRenderers.register(ModIntegrationRegistry.SWAMP_MINE.get(),
                                                SwampMineRenderer::new);
                                EntityRenderers.register(ModIntegrationRegistry.BELLRINGER_SERVANT.get(),
                                                BellringerServantRenderer::new);
                                EntityRenderers.register(ModIntegrationRegistry.ROSALYNE_SERVANT.get(),
                                                RosalyneServantRenderer::new);
                                EntityRenderers.register(ModIntegrationRegistry.ROSE_SPIRIT_SERVANT.get(),
                                                RoseSpiritServantRenderer::new);
                                EntityRenderers.register(ModIntegrationRegistry.DAME_FORTUNA_SERVANT.get(),
                                                DameFortunaServantRenderer::new);
                                EntityRenderers.register(ModIntegrationRegistry.FORTUNA_DAME_BOMB.get(),
                                                FortunaDameBombRenderer::new);
                                EntityRenderers.register(ModIntegrationRegistry.FORTUNA_DAME_CARD.get(),
                                                FortunaDameCardRenderer::new);
                        }

                        if (ModLoadedUtil.isModLoaded(ModLoadedUtil.TAKES_A_PILLAGE)) {
                                EntityRenderers.register(ModIntegrationRegistry.ARCHER_SERVANT.get(),
                                                ArcherServantRenderer::new);
                                EntityRenderers.register(ModIntegrationRegistry.SKIRMISHER_SERVANT.get(),
                                                SkirmisherServantRenderer::new);
                                EntityRenderers.register(ModIntegrationRegistry.LEGIONER_SERVANT.get(),
                                                LegionerServantRenderer::new);
                        }

                        CuriosRendererRegistry.register(
                                        com.k1sak1.goetyawaken.common.items.ModItems.SWELLING_PENDANT.get(),
                                        () -> new WearRenderer(
                                                        new ResourceLocation(GoetyAwaken.MODID,
                                                                        "textures/entity/swelling_pendant.png"),
                                                        new MiscCuriosModel(Minecraft.getInstance().getEntityModels()
                                                                        .bakeLayer(ModModelLayer.AMULET))));

                        if (ModLoadedUtil.isModLoaded(ModLoadedUtil.DEEPER_DARKER)) {
                                EntityRenderers.register(ModIntegrationRegistry.SCULK_CENTIPEDE_SERVANT.get(),
                                                SculkCentipedeServantRenderer::new);
                                EntityRenderers.register(ModIntegrationRegistry.SCULK_LEECH_SERVANT.get(),
                                                SculkLeechServantRenderer::new);
                                EntityRenderers.register(ModIntegrationRegistry.SHATTERED_SERVANT.get(),
                                                ShatteredservantRenderer::new);
                                EntityRenderers.register(ModIntegrationRegistry.SHRIEK_WORM_SERVANT.get(),
                                                ShriekWormServantRenderer::new);
                                EntityRenderers.register(ModIntegrationRegistry.SLUDGE_SERVANT.get(),
                                                SludgeServantRenderer::new);
                                EntityRenderers.register(ModIntegrationRegistry.STALKER_SERVANT.get(),
                                                StalkerServantRenderer::new);
                        }
                });
        }
}