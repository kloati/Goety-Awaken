package com.k1sak1.goetyawaken;

import com.k1sak1.goetyawaken.common.advancements.ModCriteriaTriggers;
import com.k1sak1.goetyawaken.common.events.EchoEffectHandler;
import com.k1sak1.goetyawaken.common.events.FakeAppointmentEvents;
import com.k1sak1.goetyawaken.common.events.GrimoireRenameHandler;
import com.k1sak1.goetyawaken.common.events.GlowingEmberAnvilHandler;
import com.k1sak1.goetyawaken.common.blocks.ModBlockEntities;
import com.k1sak1.goetyawaken.common.blocks.ModBlocks;
import com.k1sak1.goetyawaken.common.crafting.ModRecipeSerializers;
import com.k1sak1.goetyawaken.common.init.GoetyAwakenDataSerializers;
import com.k1sak1.goetyawaken.common.CommonProxy;
import com.k1sak1.goetyawaken.common.network.ModNetwork;
import com.k1sak1.goetyawaken.init.ModContainerTypes;
import com.k1sak1.goetyawaken.init.ModEntities;
import com.k1sak1.goetyawaken.init.ModEffects;
import com.k1sak1.goetyawaken.init.ModCreativeTab;
import com.k1sak1.goetyawaken.init.ModProxy;
import com.k1sak1.goetyawaken.data.ModItemModelProvider;
import com.k1sak1.goetyawaken.data.ModDamageTypeTagsProvider;
import com.k1sak1.goetyawaken.data.ModItemTagsProvider;
import com.k1sak1.goetyawaken.common.world.ModMobSpawnBiomeModifier;
import com.k1sak1.goetyawaken.common.world.structures.ModStructureTypes;
import com.k1sak1.goetyawaken.common.world.structures.ModStructurePlacementTypes;
import com.k1sak1.goetyawaken.common.entities.ally.illager.train.GoetyAwakenIllagerType;
import com.k1sak1.goetyawaken.common.entities.ally.WardenServant;
import com.k1sak1.goetyawaken.common.entities.ally.CreeperServant;
import com.k1sak1.goetyawaken.common.entities.ally.IceCreeperServant;
import com.k1sak1.goetyawaken.common.entities.ally.EndermanServant;
import com.k1sak1.goetyawaken.common.entities.ally.EndermiteServant;
import com.k1sak1.goetyawaken.common.entities.ally.ShulkerServant;
import com.k1sak1.goetyawaken.common.entities.ally.WitherServant;
import com.k1sak1.goetyawaken.common.entities.ally.PaleGolemServant;
import com.k1sak1.goetyawaken.common.entities.ally.illager.RoyalguardServant;
import com.k1sak1.goetyawaken.common.entities.ally.SilverfishServant;
import com.k1sak1.goetyawaken.common.entities.ally.CaerbannogRabbitServant;
import com.k1sak1.goetyawaken.common.entities.ally.golem.MushroomMonstrosity;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.ally.AngryMooshroom;
import com.k1sak1.goetyawaken.common.entities.hostile.MushroomMonstrosityHostile;
import com.k1sak1.goetyawaken.common.entities.hostile.HostileGnasher;
import com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer.WraithNecromancerServant;
import com.k1sak1.goetyawaken.init.ModArgumentTypes;
import com.k1sak1.goetyawaken.init.ModPaintings;
import com.k1sak1.goetyawaken.init.ModTags;
import com.mojang.serialization.Codec;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.Polarice3.Goety.api.entities.ally.illager.IllagerType;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
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
import com.k1sak1.goetyawaken.common.compat.touhoulittlemaid.TouhouLittleMaidLoaded;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.MaidFairyServant;

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

                ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC,
                                "goetyawaken/goetyawaken-common.toml");
                ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AttributesConfig.SPEC,
                                "goetyawaken/goetyawaken-attributes.toml");
                ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
                ModContainerTypes.CONTAINER_TYPES.register(modEventBus);
                GoetyAwakenDataSerializers.DATA_SERIALIZERS.register(modEventBus);
                ModStructureTypes.STRUCTURE_TYPE.register(modEventBus);
                ModStructurePlacementTypes.STRUCTURE_PLACEMENT_TYPE.register(modEventBus);
                ModBlocks.init();
                ModEntities.init();
                ModEffects.init();
                ModRecipeSerializers.init();
                ModPaintings.init();
                ModTags.init();
                ModArgumentTypes.COMMAND_ARGUMENT_TYPES.register(modEventBus);

                ModCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);
                ModCriteriaTriggers.init();

                MinecraftForge.EVENT_BUS.register(this);
                MinecraftForge.EVENT_BUS.register(EchoEffectHandler.class);
                MinecraftForge.EVENT_BUS.register(FakeAppointmentEvents.class);
                MinecraftForge.EVENT_BUS.register(GrimoireRenameHandler.class);
                MinecraftForge.EVENT_BUS.register(GlowingEmberAnvilHandler.class);
                MinecraftForge.EVENT_BUS.register(com.k1sak1.goetyawaken.common.events.ModEffectsEvents.class);
                MinecraftForge.EVENT_BUS.register(com.k1sak1.goetyawaken.common.events.ApostleUpgradeEvents.class);
                MinecraftForge.EVENT_BUS.register(com.k1sak1.goetyawaken.common.events.NBTEggEventHandler.class);
                MinecraftForge.EVENT_BUS
                                .register(com.k1sak1.goetyawaken.common.events.NamelessOneKillConversionEvent.class);
                MinecraftForge.EVENT_BUS.register(com.k1sak1.goetyawaken.common.events.VanguardChampionKillEvent.class);
                MinecraftForge.EVENT_BUS.register(com.k1sak1.goetyawaken.common.events.CreativeTabEventHandler.class);
                MinecraftForge.EVENT_BUS
                                .register(com.k1sak1.goetyawaken.common.events.WitherNecromancerDeathEvent.class);
                MinecraftForge.EVENT_BUS.register(
                                com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.namelessquotes.KillSpecialEnemyQuoteHandler.class);

                MinecraftForge.EVENT_BUS.register(com.k1sak1.goetyawaken.common.init.MobEnchantInit.class);
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

        private void setupEntityAttributeCreation(final EntityAttributeCreationEvent event) {
                event.put(ModEntityType.WARDEN_SERVANT.get(), WardenServant.setCustomAttributes().build());
                event.put(ModEntityType.CREEPER_SERVANT.get(), CreeperServant.setCustomAttributes().build());
                event.put(ModEntityType.ICE_CREEPER_SERVANT.get(), IceCreeperServant.setCustomAttributes().build());
                event.put(ModEntityType.ENDERMAN_SERVANT.get(), EndermanServant.setCustomAttributes().build());
                event.put(ModEntityType.ENDERMITE_SERVANT.get(), EndermiteServant.setCustomAttributes().build());
                event.put(ModEntityType.SHULKER_SERVANT.get(), ShulkerServant.setCustomAttributes().build());
                event.put(ModEntityType.WITHER_SERVANT.get(), WitherServant.setCustomAttributes().build());
                event.put(ModEntityType.PALE_GOLEM_SERVANT.get(), PaleGolemServant.setCustomAttributes().build());
                event.put(ModEntityType.ROYALGUARD_SERVANT.get(), RoyalguardServant.setCustomAttributes().build());
                event.put(ModEntityType.SILVERFISH_SERVANT.get(), SilverfishServant.setCustomAttributes().build());
                event.put(ModEntityType.CAERBANNOG_RABBIT_SERVANT.get(),
                                CaerbannogRabbitServant.setCustomAttributes().build());
                event.put(ModEntityType.MUSHROOM_MONSTROSITY.get(), MushroomMonstrosity.setCustomAttributes().build());
                event.put(ModEntityType.ANGRY_MOOSHROOM.get(), AngryMooshroom.setCustomAttributes().build());
                event.put(ModEntityType.HOSTILE_MUSHROOM_MONSTROSITY.get(),
                                MushroomMonstrosityHostile.setCustomAttributes().build());
                event.put(ModEntityType.WRAITH_NECROMANCER_SERVANT.get(),
                                WraithNecromancerServant.setCustomAttributes().build());
                event.put(ModEntityType.ILLUSIONER_SERVANT.get(),
                                com.k1sak1.goetyawaken.common.entities.ally.illager.IllusionerServant
                                                .setCustomAttributes().build());
                event.put(ModEntityType.HOSTILE_GNASHER.get(), HostileGnasher.setCustomAttributes().build());
                if (TouhouLittleMaidLoaded.TOUHOULITTLEMAID.isLoaded()) {
                        event.put(ModEntityType.MAID_FAIRY_SERVANT.get(),
                                        MaidFairyServant.createFairyAttributes().build());
                }
        }

        @SubscribeEvent
        public void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
                setupEntityAttributeCreation(event);
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
        public void gatherData(GatherDataEvent event) {
                DataGenerator generator = event.getGenerator();
                PackOutput packOutput = generator.getPackOutput();
                ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
                generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, existingFileHelper));
                generator.addProvider(event.includeServer(),
                                new ModDamageTypeTagsProvider(packOutput, event.getLookupProvider(),
                                                existingFileHelper));
                generator.addProvider(event.includeServer(),
                                new ModItemTagsProvider(packOutput, event.getLookupProvider(), existingFileHelper));
        }
}
