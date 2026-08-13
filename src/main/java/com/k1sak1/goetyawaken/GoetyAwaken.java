package com.k1sak1.goetyawaken;

import com.k1sak1.goetyawaken.common.advancements.ModCriteriaTriggers;
import com.k1sak1.goetyawaken.common.items.magic.grimoire.AffixPool;
import com.k1sak1.goetyawaken.common.items.magic.grimoire.GrimoireValueRegistry;
import com.k1sak1.goetyawaken.common.magic.sorcerer.SorcererSpellConfig;
import com.k1sak1.goetyawaken.common.network.client.CSyncSpellConfigPacket;
import net.minecraftforge.event.entity.player.PlayerEvent;
import com.k1sak1.goetyawaken.common.blocks.ModBlockEntities;
import com.k1sak1.goetyawaken.common.blocks.ModBlocks;
import com.k1sak1.goetyawaken.common.crafting.ModRecipeSerializers;
import com.k1sak1.goetyawaken.init.GoetyAwakenDataSerializers;
import com.k1sak1.goetyawaken.common.CommonProxy;
import com.k1sak1.goetyawaken.common.network.ModNetwork;
import com.k1sak1.goetyawaken.init.ModContainerTypes;
import com.k1sak1.goetyawaken.init.ModEntities;
import com.k1sak1.goetyawaken.init.ModEffects;
import com.k1sak1.goetyawaken.init.ModAttributeRegistry;
import com.k1sak1.goetyawaken.init.ModCreativeTab;
import com.k1sak1.goetyawaken.init.ModProxy;
import com.k1sak1.goetyawaken.data.ModItemModelProvider;
import com.k1sak1.goetyawaken.data.ModDamageTypeTagsProvider;
import com.k1sak1.goetyawaken.common.world.ModMobSpawnBiomeModifier;
import com.k1sak1.goetyawaken.common.world.ModMobSpawnStructureModifier;
import com.k1sak1.goetyawaken.common.world.structures.ModStructureTypes;
import com.k1sak1.goetyawaken.common.world.structures.ModStructurePlacementTypes;
import com.k1sak1.goetyawaken.common.entities.ally.illager.train.GoetyAwakenIllagerType;
import com.k1sak1.goetyawaken.common.ModIntegrationRegistry;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.init.ModArgumentTypes;
import com.k1sak1.goetyawaken.init.ModPaintings;
import com.k1sak1.goetyawaken.init.ModParticleTypes;
import com.k1sak1.goetyawaken.init.ModTags;
import com.mojang.serialization.Codec;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.Polarice3.Goety.api.entities.ally.illager.IllagerType;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.fml.ModList;
import java.io.IOException;
import com.k1sak1.goetyawaken.config.AttributesConfig;
import com.k1sak1.goetyawaken.utils.ConfigUpdater;
import net.minecraftforge.fml.DistExecutor;
import com.k1sak1.goetyawaken.init.SidedInit;
import com.k1sak1.goetyawaken.init.ClientSideInit;

@Mod(GoetyAwaken.MODID)
public class GoetyAwaken {
        public static final String MODID = "goetyawaken";
        public static final Logger LOGGER = LogManager.getLogger();

        public static ModNetwork network;
        public static ModProxy PROXY = DistExecutor.unsafeRunForDist(
                        () -> com.k1sak1.goetyawaken.client.ClientProxy::new,
                        () -> CommonProxy::new);
        public static SidedInit SIDED_INIT = DistExecutor.unsafeRunForDist(() -> ClientSideInit::new,
                        () -> SidedInit::new);

        private static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIERS = DeferredRegister
                        .create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, MODID);

        private static final DeferredRegister<Codec<? extends StructureModifier>> STRUCTURE_MODIFIERS = DeferredRegister
                        .create(ForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, MODID);

        public static ResourceLocation location(String path) {
                return new ResourceLocation(MODID, path);
        }

        public GoetyAwaken() {
                IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

                modEventBus.addListener(this::gatherData);
                modEventBus.addListener(this::setup);
                modEventBus.addListener(this::loadComplete);
                modEventBus.addListener(this::SpawnPlacementEvent);
                modEventBus.addListener(this::addPackFinders);

                BIOME_MODIFIERS.register(modEventBus);
                BIOME_MODIFIERS.register("mob_spawns", ModMobSpawnBiomeModifier::makeCodec);
                STRUCTURE_MODIFIERS.register(modEventBus);
                STRUCTURE_MODIFIERS.register("structure_mob_spawns", ModMobSpawnStructureModifier::makeCodec);

                ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC,
                                "goetyawaken/goetyawaken-common.toml");
                Config.loadConfig(Config.SPEC,
                                FMLPaths.CONFIGDIR.get().resolve("goetyawaken/goetyawaken-common.toml").toString());
                ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AttributesConfig.SPEC,
                                "goetyawaken/goetyawaken-attributes.toml");
                AttributesConfig.loadConfig(AttributesConfig.SPEC,
                                FMLPaths.CONFIGDIR.get().resolve("goetyawaken/goetyawaken-attributes.toml").toString());
                ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
                ModContainerTypes.CONTAINER_TYPES.register(modEventBus);
                GoetyAwakenDataSerializers.DATA_SERIALIZERS.register(modEventBus);
                ModStructureTypes.STRUCTURE_TYPE.register(modEventBus);
                ModStructurePlacementTypes.STRUCTURE_PLACEMENT_TYPE.register(modEventBus);
                ModBlocks.init();
                ModEntities.init();
                ModEffects.init();
                ModAttributeRegistry.init();
                ModRecipeSerializers.init();
                ModPaintings.init();
                ModTags.init();
                ModArgumentTypes.COMMAND_ARGUMENT_TYPES.register(modEventBus);

                ModParticleTypes.PARTICLE_TYPES.register(modEventBus);

                ModIntegrationRegistry.INTEGRATION_ENTITY_TYPES.register(modEventBus);
                ModIntegrationRegistry.INTEGRATION_ITEMS.register(modEventBus);

                ModCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);
                ModCriteriaTriggers.init();

                MinecraftForge.EVENT_BUS.register(this);
                MinecraftForge.EVENT_BUS.register(com.k1sak1.goetyawaken.common.events.CreativeTabEventHandler.class);
                MinecraftForge.EVENT_BUS.register(
                                com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.namelessquotes.KillSpecialEnemyQuoteHandler.class);
                MinecraftForge.EVENT_BUS
                                .register(com.k1sak1.goetyawaken.common.events.eliteassault.EliteAssaultListener.class);

                MinecraftForge.EVENT_BUS.register(com.k1sak1.goetyawaken.init.MobCommandInit.class);
                MinecraftForge.EVENT_BUS
                                .register(com.k1sak1.goetyawaken.common.mobenchant.MobEnchantEventHandler.class);
                MinecraftForge.EVENT_BUS
                                .register(com.k1sak1.goetyawaken.common.mobenchant.MobEnchantResurrectionManager.class);
                MinecraftForge.EVENT_BUS
                                .register(com.k1sak1.goetyawaken.common.mobenchant.MultiShotMobEnchant.class);

                SIDED_INIT.init();
        }

        private void setup(final FMLCommonSetupEvent event) {
                network = new ModNetwork();
                network.init();
                SorcererSpellConfig.init();
                com.k1sak1.goetyawaken.common.world.structures.foundation.FoundationConfigManager.init();

                event.enqueueWork(() -> {
                        com.Polarice3.Goety.common.research.ResearchList.registerResearch("royal",
                                        com.k1sak1.goetyawaken.common.research.ResearchList.ROYAL);

                        // com.k1sak1.goetyawaken.common.compat.tetra.TetraCompat.initIfPresent();
                        // com.k1sak1.goetyawaken.common.compat.tetra.TetraCompat.registerEventHandlers();
                });
        }

        private void loadComplete(final FMLLoadCompleteEvent event) {
                event.enqueueWork(() -> {
                        IllagerType.create("GOETY_AWAKEN", new GoetyAwakenIllagerType());
                        if (Config.callbackApostle) {
                                ConfigUpdater.updateGoetyApostleConfig();
                        }
                });
        }

        private void SpawnPlacementEvent(SpawnPlacementRegisterEvent event) {
                event.register(ModEntityType.ZOMBIE_DARKGUARD.get(),
                                net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND,
                                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                net.minecraft.world.entity.monster.Monster::checkMonsterSpawnRules,
                                SpawnPlacementRegisterEvent.Operation.AND);

                event.register(ModEntityType.SKELETON_VANGUARD.get(),
                                net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND,
                                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                net.minecraft.world.entity.monster.Monster::checkMonsterSpawnRules,
                                SpawnPlacementRegisterEvent.Operation.AND);

                event.register(ModEntityType.PARCHED.get(),
                                net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND,
                                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                com.k1sak1.goetyawaken.common.entities.hostile.undead.skeleton.Parched::checkMonsterSpawnRules,
                                SpawnPlacementRegisterEvent.Operation.AND);

                event.register(ModEntityType.ICE_CREEPER.get(),
                                net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND,
                                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                com.Polarice3.Goety.common.entities.neutral.Owned::checkHostileSpawnRules,
                                SpawnPlacementRegisterEvent.Operation.AND);

                event.register(ModEntityType.BOULDERING_ZOMBIE.get(),
                                net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND,
                                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                net.minecraft.world.entity.monster.Monster::checkMonsterSpawnRules,
                                SpawnPlacementRegisterEvent.Operation.AND);

                event.register(ModEntityType.JUNGLE_ZOMBIE.get(),
                                net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND,
                                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                net.minecraft.world.entity.monster.Monster::checkMonsterSpawnRules,
                                SpawnPlacementRegisterEvent.Operation.AND);

                event.register(ModEntityType.FROZEN_ZOMBIE.get(),
                                net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND,
                                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                net.minecraft.world.entity.monster.Monster::checkMonsterSpawnRules,
                                SpawnPlacementRegisterEvent.Operation.AND);

                event.register(ModEntityType.HOSTILE_SNAPPER.get(),
                                net.minecraft.world.entity.SpawnPlacements.Type.IN_WATER,
                                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                com.Polarice3.Goety.common.entities.neutral.Owned::checkHostileSpawnRules,
                                SpawnPlacementRegisterEvent.Operation.AND);

                event.register(ModEntityType.HOSTILE_TROPICAL_SLIME.get(),
                                net.minecraft.world.entity.SpawnPlacements.Type.IN_WATER,
                                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                com.Polarice3.Goety.common.entities.neutral.Owned::checkHostileSpawnRules,
                                SpawnPlacementRegisterEvent.Operation.AND);

                event.register(ModEntityType.HOSTILE_MINI_GHAST.get(),
                                net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND,
                                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                com.Polarice3.Goety.common.entities.neutral.Owned::checkHostileSpawnRules,
                                SpawnPlacementRegisterEvent.Operation.AND);

                event.register(ModEntityType.HOSTILE_GNASHER.get(),
                                net.minecraft.world.entity.SpawnPlacements.Type.IN_WATER,
                                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                com.Polarice3.Goety.common.entities.neutral.Owned::checkHostileSpawnRules,
                                SpawnPlacementRegisterEvent.Operation.AND);

                event.register(ModEntityType.SUNKEN_SKELETON.get(),
                                net.minecraft.world.entity.SpawnPlacements.Type.IN_WATER,
                                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                com.Polarice3.Goety.common.entities.neutral.Owned::checkHostileSpawnRules,
                                SpawnPlacementRegisterEvent.Operation.AND);

                event.register(ModEntityType.HOSTILE_WILDFIRE.get(),
                                net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND,
                                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                com.k1sak1.goetyawaken.common.entities.hostile.HostileWildfire::checkWildfireSpawnRules,
                                SpawnPlacementRegisterEvent.Operation.AND);

                event.register(ModEntityType.HOSTILE_SPIDER_CREEDER.get(),
                                net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND,
                                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                com.Polarice3.Goety.common.entities.neutral.Owned::checkHostileSpawnRules,
                                SpawnPlacementRegisterEvent.Operation.AND);

                event.register(ModEntityType.HOSTILE_TWILIGHT_GOAT.get(),
                                net.minecraft.world.entity.SpawnPlacements.Type.ON_GROUND,
                                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                com.Polarice3.Goety.common.entities.neutral.Owned::checkHostileSpawnRules,
                                SpawnPlacementRegisterEvent.Operation.AND);
        }

        private void addPackFinders(AddPackFindersEvent event) {
                try {
                        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
                                addBuiltinPack(event, "apostle_servant_pack",
                                                Component.literal("apostle_servant_pack"));
                        }
                } catch (IOException ex) {

                }
        }

        private static void addBuiltinPack(AddPackFindersEvent event, String filename, Component displayName)
                        throws IOException {
                filename = "builtin_resource_packs/" + filename;
                String id = "builtin/" + filename;
                var resourcePath = ModList.get().getModFileById(GoetyAwaken.MODID).getFile().findResource(filename);
                var pack = Pack.readMetaAndCreate(id, displayName, false,
                                (path) -> new net.minecraftforge.resource.PathPackResources(path, true, resourcePath),
                                PackType.CLIENT_RESOURCES, Pack.Position.TOP, PackSource.BUILT_IN);
                event.addRepositorySource((packConsumer) -> packConsumer.accept(pack));
        }

        @SubscribeEvent
        public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
                if (!event.getEntity().level().isClientSide) {
                        ModNetwork.sendTo(event.getEntity(),
                                        new CSyncSpellConfigPacket(SorcererSpellConfig.getSpellEntries()));
                }
        }

        @SubscribeEvent
        public void onAddReloadListeners(net.minecraftforge.event.AddReloadListenerEvent event) {
                event.addListener(new AffixPool.ReloadListener());
                event.addListener(new GrimoireValueRegistry.ReloadListener());
        }

        @SubscribeEvent
        public void gatherData(GatherDataEvent event) {
                DataGenerator generator = event.getGenerator();
                PackOutput packOutput = generator.getPackOutput();
                ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
                generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, existingFileHelper));
                generator.addProvider(event.includeServer(),
                                new ModDamageTypeTagsProvider(packOutput, event.getLookupProvider(),
                                                existingFileHelper));
        }
}
