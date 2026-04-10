package com.k1sak1.goetyawaken.common.items;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.blocks.ModBlocks;
import com.k1sak1.goetyawaken.client.renderer.block.NamelessChestISTER;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.items.armor.ChampionArmorItem;
import com.k1sak1.goetyawaken.common.items.armor.MushroomHatItem;
import com.k1sak1.goetyawaken.common.items.magic.GrimoireItem;
import com.k1sak1.goetyawaken.common.items.magic.ShulkerMissileFocus;
import com.k1sak1.goetyawaken.common.items.curios.RippleWalkItem;
import com.k1sak1.goetyawaken.common.items.curios.DauntlessGlovesItem;
import com.k1sak1.goetyawaken.common.items.curios.AssassinGloveItem;
import com.k1sak1.goetyawaken.common.items.curios.GatlingCharmItem;
import com.k1sak1.goetyawaken.common.items.magic.CreeperFocus;
import com.k1sak1.goetyawaken.common.items.magic.StareFocus;
import com.k1sak1.goetyawaken.common.items.magic.TormentFocus;
import com.k1sak1.goetyawaken.common.items.magic.InfestationFocus;
import com.k1sak1.goetyawaken.common.items.magic.KillerFocus;
import com.k1sak1.goetyawaken.common.items.magic.WololoFocus;
import com.k1sak1.goetyawaken.common.items.magic.AgonyFocus;
import com.k1sak1.goetyawaken.common.items.magic.BloodRainFocus;
import com.k1sak1.goetyawaken.common.items.magic.FairyFocus;
import com.k1sak1.goetyawaken.common.items.magic.MushroomMissileFocus;
import com.k1sak1.goetyawaken.common.items.magic.ChampionFocus;
import com.k1sak1.goetyawaken.common.items.magic.DesertPlaguesFocus;
import com.k1sak1.goetyawaken.common.items.magic.DeathRayFocus;
import com.k1sak1.goetyawaken.common.items.magic.HeavenRiftFocus;
import com.k1sak1.goetyawaken.common.items.magic.MarbleFocus;
import com.k1sak1.goetyawaken.common.items.magic.AccessFocus;
import com.k1sak1.goetyawaken.init.ModSounds;
import com.k1sak1.goetyawaken.common.items.block.OminousPaintingItem;
import com.k1sak1.goetyawaken.common.items.food.No1337CandyItem;
import com.k1sak1.goetyawaken.common.items.food.PulsePieItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.StandingAndWallBlockItem;
import com.Polarice3.Goety.common.items.ModSpawnEggItem;
import com.Polarice3.Goety.common.items.ServantSpawnEggItem;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;

public class ModItems {
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
                        GoetyAwaken.MODID);

        public static void init() {
                ModItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<Item> CLAYMORE = ITEMS.register("claymore",
                        () -> new ClaymoreItem());

        public static final RegistryObject<Item> OBSIDIAN_CLAYMORE = ITEMS.register("obsidian_claymore",
                        () -> new ObsidianClaymoreItem());

        public static final RegistryObject<Item> STARLESS_NIGHT = ITEMS.register("starless_night",
                        () -> new StarlessNightItem());

        public static final RegistryObject<Item> TRUTHSEEKER = ITEMS.register("truth_seeker",
                        () -> new TruthseekerItem());

        public static final RegistryObject<Item> DARK_ICE_AXE = ITEMS.register("dark_ice_axe",
                        () -> new DarkIceAxeItem());

        public static final RegistryObject<Item> DARK_NETHERITE_BOW = ITEMS.register("dark_netherite_bow",
                        () -> new DarkNetheriteBowItem());

        public static final RegistryObject<Item> HARP_CROSSBOW = ITEMS.register("harp_crossbow",
                        () -> new HarpCrossbowItem());

        public static final RegistryObject<Item> GRAVE_BANE = ITEMS.register("grave_bane",
                        () -> new GraveBaneSwordItem());

        public static final RegistryObject<Item> GLAIVE = ITEMS.register("glaive",
                        () -> new GlaiveItem());

        public static final RegistryObject<Item> MOONLIGHT_CUT = ITEMS.register("moonlight_cut",
                        () -> new MoonlightCutItem());

        public static final RegistryObject<Item> FROST_SCYTHE = ITEMS.register("frost_scythe",
                        () -> new FrostScytheItem());

        public static final RegistryObject<Item> MACE = ITEMS.register("mace",
                        () -> new MaceItem());

        public static final RegistryObject<Item> SUN_GRACE = ITEMS.register("sun_grace",
                        () -> new SunGraceItem());

        public static final RegistryObject<Item> MOOSHROOM_MACE = ITEMS.register("mooshroom_mace",
                        () -> new MooShroomMaceItem());

        public static final RegistryObject<Item> CHAMPION_HELMET = ITEMS.register("champion_helmet",
                        () -> new ChampionArmorItem(ArmorItem.Type.HELMET));
        public static final RegistryObject<Item> CHAMPION_CHESTPLATE = ITEMS.register("champion_chestplate",
                        () -> new ChampionArmorItem(ArmorItem.Type.CHESTPLATE));
        public static final RegistryObject<Item> CHAMPION_LEGGINGS = ITEMS.register("champion_leggings",
                        () -> new ChampionArmorItem(ArmorItem.Type.LEGGINGS));
        public static final RegistryObject<Item> CHAMPION_BOOTS = ITEMS.register("champion_boots",
                        () -> new ChampionArmorItem(ArmorItem.Type.BOOTS));

        public static final RegistryObject<Item> IRON_GRIMOIRE = ITEMS.register("iron_grimoire",
                        () -> new GrimoireItem(2));

        public static final RegistryObject<Item> GOLD_GRIMOIRE = ITEMS.register("gold_grimoire",
                        () -> new GrimoireItem(3));

        public static final RegistryObject<Item> EMERALD_GRIMOIRE = ITEMS.register("emerald_grimoire",
                        () -> new GrimoireItem(4));

        public static final RegistryObject<Item> DIAMOND_GRIMOIRE = ITEMS.register("diamond_grimoire",
                        () -> new GrimoireItem(5));

        public static final RegistryObject<Item> RUBY_GRIMOIRE = ITEMS.register("ruby_grimoire",
                        () -> new GrimoireItem(6));

        public static final RegistryObject<Item> OBSIDIAN_TEAR = ITEMS.register("obsidian_tear",
                        () -> new ObsidianTear());

        public static final RegistryObject<Item> RIPPLE_WALK = ITEMS.register("ripplewalk",
                        () -> new RippleWalkItem());

        public static final RegistryObject<Item> DAUNTLESS_GLOVES = ITEMS.register("dauntless_gloves",
                        () -> new DauntlessGlovesItem());

        public static final RegistryObject<Item> ASSASSIN_GLOVE = ITEMS.register("assassin_glove",
                        () -> new AssassinGloveItem());

        public static final RegistryObject<Item> GATLING_CHARM = ITEMS.register("gatling_charm",
                        () -> new GatlingCharmItem());

        public static final RegistryObject<Item> OMINOUS_PAINTING = ITEMS.register("ominous_painting",
                        () -> new OminousPaintingItem());

        public static final RegistryObject<Item> SUPERAGGREGATED_MYCELIAL_CIRCUIT = ITEMS.register(
                        "superaggregated_mycelial_circuit",
                        () -> new Item(new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)
                                        .stacksTo(64)));

        public static final RegistryObject<Item> TABOO_FRAGMENT = ITEMS.register("taboo_fragment",
                        () -> new Item(new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)
                                        .stacksTo(64)));

        public static final RegistryObject<Item> DELUSIVE_FRAGMENT = ITEMS.register("delusive_fragment",
                        () -> new Item(new Item.Properties().rarity(net.minecraft.world.item.Rarity.UNCOMMON)
                                        .stacksTo(64)));

        public static final RegistryObject<Item> ENDER_STORAGE_BOOK_IRON = ITEMS.register("ender_storage_book_iron",
                        () -> new EnderStorageBookIron());

        public static final RegistryObject<Item> ENDER_STORAGE_BOOK_GOLD = ITEMS.register("ender_storage_book_gold",
                        () -> new EnderStorageBookGold());

        public static final RegistryObject<Item> ENDER_STORAGE_BOOK_EMERALD = ITEMS.register(
                        "ender_storage_book_emerald",
                        () -> new EnderStorageBookEmerald());

        public static final RegistryObject<Item> ENDER_STORAGE_BOOK_DIAMOND = ITEMS.register(
                        "ender_storage_book_diamond",
                        () -> new EnderStorageBookDiamond());

        public static final RegistryObject<Item> ENDER_STORAGE_BOOK_SAPPHIRE = ITEMS.register(
                        "ender_storage_book_sapphire",
                        () -> new EnderStorageBookSapphire());

        public static final RegistryObject<Item> NAMELESS_PLATINUM = ITEMS.register("nameless_platinum",
                        () -> new Item(new Item.Properties().rarity(net.minecraft.world.item.Rarity.EPIC)
                                        .stacksTo(64)));

        public static final RegistryObject<Item> ROYAL_SCROLL = ITEMS.register("royal_scroll",
                        () -> new RoyalScroll());

        public static final RegistryObject<Item> WRAITH_NECROMANCER_SOUL_JAR = ITEMS.register(
                        "wraith_necromancer_soul_jar",
                        () -> new WraithNecromancerSoulJar());

        public static final RegistryObject<Item> PARCHED_NECROMANCER_SOUL_JAR = ITEMS.register(
                        "parched_necromancer_soul_jar",
                        () -> new ParchedNecromancerSoulJar());

        public static final RegistryObject<Item> CATACOMBS_RELICUARY = ITEMS.register("catacombs_reliquary",
                        () -> new CatacombsReliquaryItem());

        public static final RegistryObject<Item> EYE_OF_OVERWATCH = ITEMS.register("eye_of_overwatch",
                        () -> new EyeOfOverwatchItem());

        public static final RegistryObject<Item> WRAITH_LANTERN = ITEMS.register("wraith_lantern",
                        () -> new WraithLantern());

        public static final RegistryObject<Item> ANCIENT_GONG = ITEMS.register("ancient_gong",
                        () -> new AncientGong());

        public static final RegistryObject<Item> GLOWING_EMBER = ITEMS.register("glowing_ember",
                        () -> new Item(new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)
                                        .stacksTo(64).fireResistant()));

        public static final RegistryObject<Item> GILDED_INGOT = ITEMS.register("gilded_ingot",
                        () -> new GildedIngotItem());

        public static final RegistryObject<Item> SOUL_RUBY_BLOCK = ITEMS.register("soul_ruby_block",
                        () -> new BlockItem(ModBlocks.SOUL_RUBY_BLOCK.get(), new Item.Properties()));

        public static final RegistryObject<Item> SOUL_SAPPHIRE = ITEMS.register("soul_sapphire",
                        () -> new SoulSapphire());

        public static final RegistryObject<Item> SOUL_SAPPHIRE_BLOCK = ITEMS.register("soul_sapphire_block",
                        () -> new BlockItem(ModBlocks.SOUL_SAPPHIRE_BLOCK.get(), new Item.Properties()));

        public static final RegistryObject<Item> TRIAL_SPAWNER_ITEM = ITEMS.register("trial_spawner",
                        () -> new BlockItem(ModBlocks.TRIAL_SPAWNER.get(), new Item.Properties()));

        public static final RegistryObject<Item> VAULT_ITEM = ITEMS.register("vault",
                        () -> new BlockItem(ModBlocks.VAULT.get(), new Item.Properties()));

        public static final RegistryObject<Item> SHADOW_SHRIEKER = ITEMS.register("shadow_shrieker",
                        () -> new BlockItem(ModBlocks.SHADOW_SHRIEKER.get(), new Item.Properties()));

        public static final RegistryObject<Item> DARK_MENDER = ITEMS.register(
                        "dark_mender",
                        () -> new BlockItem(ModBlocks.DARK_MENDER.get(), new Item.Properties()));

        public static final RegistryObject<Item> NETHER_REACTOR_CORE = ITEMS.register("nether_reactor_core",
                        () -> new BlockItem(ModBlocks.NETHER_REACTOR_CORE.get(), new Item.Properties()));

        public static final RegistryObject<Item> ENDER_ACCESS_LECTERN = ITEMS.register("ender_access_lectern",
                        () -> new BlockItem(ModBlocks.ENDER_ACCESS_LECTERN.get(), new Item.Properties()));

        public static final RegistryObject<Item> ECHOING_ENDER_SHELF = ITEMS.register("echoing_ender_shelf",
                        () -> new BlockItem(ModBlocks.ECHOING_ENDER_SHELF.get(), new Item.Properties()));

        public static final RegistryObject<Item> DARK_SOUL_CANDLE = ITEMS.register("dark_soul_candle",
                        () -> new BlockItem(ModBlocks.DARK_SOUL_CANDLE.get(), new Item.Properties()));

        public static final RegistryObject<Item> MUSHROOM_COATED_ALLOY_BLOCK = ITEMS.register(
                        "mushroom_coated_alloy_block",
                        () -> new BlockItem(ModBlocks.MUSHROOM_COATED_ALLOY_BLOCK.get(), new Item.Properties()));

        public static final RegistryObject<Item> ALLY_PITHOS = ITEMS.register(
                        "ally_pithos",
                        () -> new BlockItem(ModBlocks.ALLY_PITHOS.get(), new Item.Properties()));

        public static final RegistryObject<Item> SANDSTONE_COLUMN = ITEMS.register(
                        "sandstone_column",
                        () -> new BlockItem(ModBlocks.SANDSTONE_COLUMN.get(), new Item.Properties()));

        public static final RegistryObject<Item> SLANT_SANDSTONE = ITEMS.register(
                        "slant_sandstone",
                        () -> new BlockItem(ModBlocks.SLANT_SANDSTONE.get(), new Item.Properties()));

        public static final RegistryObject<Item> TOWER_FLOWER_POT = ITEMS.register(
                        "tower_flowerpot",
                        () -> new BlockItem(ModBlocks.TOWER_FLOWER_POT.get(), new Item.Properties()));

        public static final RegistryObject<Item> BAKASMUSIC_DISC = ITEMS.register("bakasmusic_disc",
                        () -> BakasmusicDisc.create(ModSounds.BAKASMUSIC));

        public static final RegistryObject<Item> MUSIC_DISC_WRAITH_NECR = ITEMS.register("music_disc_wraith_necr",
                        () -> MusicDiscWraithNecr.create(ModSounds.WRAITH_NECROMANCER_THEME));

        public static final RegistryObject<Item> MUSIC_DISC_MOOSHROOM = ITEMS.register("music_disc_mooshroom",
                        () -> MusicDiscMooshroom.create(ModSounds.MOOSHROOM_MONSTROSITY_DISC));

        public static final RegistryObject<Item> MUSIC_DISC_NAMELESS = ITEMS.register("music_disc_nameless",
                        () -> MusicDiscNameless.create(ModSounds.NAMELESS_FIGHT_MUSIC));

        public static final RegistryObject<Item> MUSIC_DISC_RUINS_NECR = ITEMS.register("music_disc_ruinsnecr",
                        () -> MusicDiscRuinsNecr.create(ModSounds.RUINS_NECROMANCER_THEME));

        public static final RegistryObject<Item> MUSIC_DISC_ANCIENT = ITEMS.register("music_disc_ancient",
                        () -> MusicDiscAncient.create(ModSounds.MUSIC_DISC_ANCIENT));

        public static final RegistryObject<Item> SHULKER_MISSILE_FOCUS = ITEMS.register("shulker_missile_focus",
                        () -> new ShulkerMissileFocus());

        public static final RegistryObject<Item> CREEPER_FOCUS = ITEMS.register("creeper_focus",
                        () -> new CreeperFocus());

        public static final RegistryObject<Item> STARE_FOCUS = ITEMS.register("stare_focus",
                        () -> new StareFocus());

        public static final RegistryObject<Item> TORMENT_FOCUS = ITEMS.register("torment_focus",
                        () -> new TormentFocus());

        public static final RegistryObject<Item> INFESTATION_FOCUS = ITEMS.register("infestation_focus",
                        () -> new InfestationFocus());

        public static final RegistryObject<Item> KILLER_FOCUS = ITEMS.register("killer_focus",
                        () -> new KillerFocus());

        public static final RegistryObject<Item> WOLOLO_FOCUS = ITEMS.register("wololo_focus",
                        () -> new WololoFocus());

        public static final RegistryObject<Item> BLOOD_RAIN_FOCUS = ITEMS.register("blood_rain_focus",
                        () -> new BloodRainFocus());

        public static final RegistryObject<Item> AGONY_FOCUS = ITEMS.register("agony_focus",
                        () -> new AgonyFocus());

        public static final RegistryObject<Item> FAIRY_FOCUS = ITEMS.register("fairy_focus",
                        () -> new FairyFocus());

        public static final RegistryObject<Item> MUSHROOM_MISSILE_FOCUS = ITEMS.register("mushroom_missile_focus",
                        () -> new MushroomMissileFocus());

        public static final RegistryObject<Item> DESERT_PLAGUES_FOCUS = ITEMS.register("desert_plagues_focus",
                        () -> new DesertPlaguesFocus());

        public static final RegistryObject<Item> DEATH_RAY_FOCUS = ITEMS.register("death_ray_focus",
                        () -> new DeathRayFocus());

        public static final RegistryObject<Item> HEAVEN_RIFT_FOCUS = ITEMS.register("heaven_rift_focus",
                        () -> new HeavenRiftFocus());

        public static final RegistryObject<Item> CHAMPION_FOCUS = ITEMS.register("champion_focus",
                        () -> new ChampionFocus());

        public static final RegistryObject<Item> MARBLE_FOCUS = ITEMS.register("marble_focus",
                        () -> new MarbleFocus());

        public static final RegistryObject<Item> ACCESS_FOCUS = ITEMS.register("access_focus",
                        () -> new AccessFocus());

        public static final RegistryObject<Item> CURSED_VAULT_KEY = ITEMS.register("cursed_trial_key",
                        () -> new Item(new Item.Properties().rarity(net.minecraft.world.item.Rarity.UNCOMMON)
                                        .stacksTo(64)));

        public static final RegistryObject<Item> FAKE_APPOINTMENT = ITEMS.register("fake_appointment",
                        () -> new FakeAppointmentItem());

        public static final RegistryObject<Item> ENDERSENT_ENCHANTED_BOOK = ITEMS.register("endersent_enchanted_book",
                        () -> new EnderDispatcherEnchantedBook());

        public static final RegistryObject<Item> PROFOUND_ECHOING_SHARD = ITEMS.register("profound_echoing_shard",
                        () -> new EchoingShardItem());

        public static final RegistryObject<Item> GLACIAL_WRAITH_ESSENCE = ITEMS.register("glacial_wraith_essence",
                        () -> new Item(new Item.Properties().rarity(net.minecraft.world.item.Rarity.UNCOMMON)
                                        .stacksTo(64)));

        public static final RegistryObject<Item> MUCILAGE = ITEMS.register("mucilage",
                        () -> new MucilageItem());

        public static final RegistryObject<Item> TERROR_SOUL = ITEMS.register("terror_soul",
                        () -> new TerrorSoul());

        public static final RegistryObject<Item> MOOSHROOM_MONSTROSITY_HEAD = ITEMS.register(
                        "mooshroom_monstrosity_head",
                        () -> new MushroomMonstrosityHeadItem(new Item.Properties()));

        public static final RegistryObject<Item> MUSHROOM_HAT = ITEMS.register("mushroom_hat",
                        () -> new MushroomHatItem());

        public static final RegistryObject<Item> DEATH_CAP_MUSHROOM = ITEMS.register("death_cap_mushroom",
                        () -> new DeathCapMushroomItem());

        public static final RegistryObject<Item> NO_1337_CANDY = ITEMS.register(
                        "no_1337_candy",
                        () -> new No1337CandyItem());

        public static final RegistryObject<Item> PULSE_PIE = ITEMS.register(
                        "pulse_pie",
                        () -> new PulsePieItem());

        public static final RegistryObject<Item> OMINOUS_EYE = ITEMS.register("ominous_eye",
                        () -> new OminousEyeItem());

        public static final RegistryObject<Item> PRISON_EYE = ITEMS.register("prison_eye",
                        () -> new PrisonEyeItem());

        public static final RegistryObject<Item> MIRAGE_EYE = ITEMS.register("mirage_eye",
                        () -> new MirageEyeItem());

        public static final RegistryObject<Item> TABOO_BRICKS_CHISELED = ITEMS.register("taboo_bricks_chiseled",
                        () -> new BlockItem(ModBlocks.TABOO_BRICKS_CHISELED.get(), new Item.Properties()));

        public static final RegistryObject<Item> TABOO_TORCH = ITEMS.register("taboo_torch",
                        () -> new StandingAndWallBlockItem(ModBlocks.TABOO_TORCH.get(),
                                        ModBlocks.WALL_TABOO_TORCH.get(), new Item.Properties(), Direction.DOWN));

        public static final RegistryObject<Item> TABOO_GLASS = ITEMS.register("taboo_glass",
                        () -> new BlockItem(ModBlocks.TABOO_GLASS.get(), new Item.Properties()));

        public static final RegistryObject<Item> TABOO_LAMP = ITEMS.register("taboo_lamp",
                        () -> new BlockItem(ModBlocks.TABOO_LAMP.get(), new Item.Properties()));

        public static final RegistryObject<Item> SMOOTH_TABOO_STONE = ITEMS.register("smooth_taboo_stone",
                        () -> new BlockItem(ModBlocks.SMOOTH_TABOO_STONE.get(), new Item.Properties()));

        public static final RegistryObject<Item> TABOO_STONE = ITEMS.register("taboo_stone",
                        () -> new BlockItem(ModBlocks.TABOO_STONE.get(), new Item.Properties()));

        public static final RegistryObject<Item> TABOO_LAMP_CHISELED = ITEMS.register("taboo_lamp_chiseled",
                        () -> new BlockItem(ModBlocks.TABOO_LAMP_CHISELED.get(), new Item.Properties()));

        public static final RegistryObject<Item> TABOO_LAMP_ENGRAVED = ITEMS.register("taboo_lamp_engraved",
                        () -> new BlockItem(ModBlocks.TABOO_LAMP_ENGRAVED.get(), new Item.Properties()));

        public static final RegistryObject<Item> TABOO_BOOKSHELF = ITEMS.register("taboo_bookshelf",
                        () -> new BlockItem(ModBlocks.TABOO_BOOKSHELF.get(), new Item.Properties()));

        public static final RegistryObject<Item> TABOO_TERRACOTTA = ITEMS.register("taboo_terracotta",
                        () -> new BlockItem(ModBlocks.TABOO_TERRACOTTA.get(), new Item.Properties()));

        public static final RegistryObject<Item> TABOO_BRICKS = ITEMS.register("taboo_bricks",
                        () -> new BlockItem(ModBlocks.TABOO_BRICKS.get(), new Item.Properties()));

        public static final RegistryObject<Item> TABOO_BRICKS_ENGRAVED = ITEMS.register("taboo_bricks_engraved",
                        () -> new BlockItem(ModBlocks.TABOO_BRICKS_ENGRAVED.get(), new Item.Properties()));

        public static final RegistryObject<Item> SMOOTH_TABOO_STONE_STAIRS = ITEMS.register("smooth_taboo_stone_stairs",
                        () -> new BlockItem(ModBlocks.SMOOTH_TABOO_STONE_STAIRS.get(), new Item.Properties()));

        public static final RegistryObject<Item> SMOOTH_TABOO_STONE_SLAB = ITEMS.register("smooth_taboo_stone_slab",
                        () -> new BlockItem(ModBlocks.SMOOTH_TABOO_STONE_SLAB.get(), new Item.Properties()));

        public static final RegistryObject<Item> SMOOTH_TABOO_STONE_WALL = ITEMS.register("smooth_taboo_stone_wall",
                        () -> new BlockItem(ModBlocks.SMOOTH_TABOO_STONE_WALL.get(), new Item.Properties()));

        public static final RegistryObject<Item> TABOO_STONE_STAIRS = ITEMS.register("taboo_stone_stairs",
                        () -> new BlockItem(ModBlocks.TABOO_STONE_STAIRS.get(), new Item.Properties()));

        public static final RegistryObject<Item> TABOO_STONE_SLAB = ITEMS.register("taboo_stone_slab",
                        () -> new BlockItem(ModBlocks.TABOO_STONE_SLAB.get(), new Item.Properties()));

        public static final RegistryObject<Item> TABOO_STONE_WALL = ITEMS.register("taboo_stone_wall",
                        () -> new BlockItem(ModBlocks.TABOO_STONE_WALL.get(), new Item.Properties()));

        public static final RegistryObject<Item> TABOO_TERRACOTTA_STAIRS = ITEMS.register("taboo_terracotta_stairs",
                        () -> new BlockItem(ModBlocks.TABOO_TERRACOTTA_STAIRS.get(), new Item.Properties()));

        public static final RegistryObject<Item> TABOO_TERRACOTTA_SLAB = ITEMS.register("taboo_terracotta_slab",
                        () -> new BlockItem(ModBlocks.TABOO_TERRACOTTA_SLAB.get(), new Item.Properties()));

        public static final RegistryObject<Item> TABOO_TERRACOTTA_WALL = ITEMS.register("taboo_terracotta_wall",
                        () -> new BlockItem(ModBlocks.TABOO_TERRACOTTA_WALL.get(), new Item.Properties()));

        public static final RegistryObject<Item> TABOO_BRICKS_STAIRS = ITEMS.register("taboo_bricks_stairs",
                        () -> new BlockItem(ModBlocks.TABOO_BRICKS_STAIRS.get(), new Item.Properties()));

        public static final RegistryObject<Item> TABOO_BRICKS_SLAB = ITEMS.register("taboo_bricks_slab",
                        () -> new BlockItem(ModBlocks.TABOO_BRICKS_SLAB.get(), new Item.Properties()));

        public static final RegistryObject<Item> TABOO_BRICKS_WALL = ITEMS.register("taboo_bricks_wall",
                        () -> new BlockItem(ModBlocks.TABOO_BRICKS_WALL.get(), new Item.Properties()));

        public static final RegistryObject<Item> TABOO_PILLAR = ITEMS.register("taboo_pillar",
                        () -> new BlockItem(ModBlocks.TABOO_PILLAR.get(), new Item.Properties()));

        public static final RegistryObject<Item> TABOO_PILLAR_TOP = ITEMS.register("taboo_pillar_top",
                        () -> new BlockItem(ModBlocks.TABOO_PILLAR_TOP.get(), new Item.Properties()));

        public static final RegistryObject<Item> TABOO_PILLAR_BASE = ITEMS.register("taboo_pillar_base",
                        () -> new BlockItem(ModBlocks.TABOO_PILLAR_BASE.get(), new Item.Properties()));

        public static final RegistryObject<Item> REDSTONE_CLUSTER = ITEMS.register(
                        "redstone_cluster",
                        () -> new BlockItem(ModBlocks.REDSTONE_CLUSTER.get(), new Item.Properties()));

        public static final RegistryObject<Item> REDSTONE_CLUSTER_MIDDLE = ITEMS.register(
                        "redstone_cluster_middle",
                        () -> new BlockItem(ModBlocks.REDSTONE_CLUSTER_MIDDLE.get(), new Item.Properties()));

        public static final RegistryObject<Item> REDSTONE_CLUSTER_SMALL = ITEMS.register(
                        "redstone_cluster_small",
                        () -> new BlockItem(ModBlocks.REDSTONE_CLUSTER_SMALL.get(), new Item.Properties()));

        public static final RegistryObject<Item> NAMELESS_CHEST = ITEMS.register("nameless_chest",
                        () -> new BlockItem(ModBlocks.NAMELESS_CHEST.get(), new Item.Properties()) {
                                @Override
                                public void initializeClient(
                                                java.util.function.Consumer<IClientItemExtensions> consumer) {
                                        consumer.accept(new IClientItemExtensions() {
                                                @Override
                                                public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                                                        return new NamelessChestISTER();
                                                }
                                        });
                                }
                        });

        public static final RegistryObject<Item> SAND_PILE = ITEMS.register("sand_pile",
                        () -> new BlockItem(ModBlocks.SAND_PILE.get(), new Item.Properties()));

        public static final RegistryObject<Item> GORGEOUS_URN_INDIGO = ITEMS.register("gorgeous_urn_indigo",
                        () -> new BlockItem(ModBlocks.GORGEOUS_URN_INDIGO.get(), new Item.Properties()));

        public static final RegistryObject<Item> GORGEOUS_URN_VERDANT = ITEMS.register("gorgeous_urn_verdant",
                        () -> new BlockItem(ModBlocks.GORGEOUS_URN_VERDANT.get(), new Item.Properties()));

        public static final RegistryObject<Item> GORGEOUS_URN_PALE = ITEMS.register("gorgeous_urn_pale",
                        () -> new BlockItem(ModBlocks.GORGEOUS_URN_PALE.get(), new Item.Properties()));

        public static final RegistryObject<Item> GORGEOUS_URN_CRIMSON = ITEMS.register("gorgeous_urn_crimson",
                        () -> new BlockItem(ModBlocks.GORGEOUS_URN_CRIMSON.get(), new Item.Properties()));

        public static final RegistryObject<Item> PALE_GOLEM_SPAWN_EGG = ITEMS.register("pale_golem_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.PALE_GOLEM_SERVANT, 0x888888, 0x444444,
                                        new Item.Properties()));

        public static final RegistryObject<Item> SILVERFISH_SPAWN_EGG = ITEMS.register("silverfish_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.SILVERFISH_SERVANT, 0x6E6E6E, 0x333333,
                                        new Item.Properties()));

        public static final RegistryObject<Item> WIGHT_SPAWN_EGG = ITEMS.register("wight_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.WIGHT_SERVANT, 0x4B0082, 0x8A2BE2,
                                        new Item.Properties()));

        public static final RegistryObject<Item> CREEPER_SPAWN_EGG = ITEMS.register("creeper_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.CREEPER_SERVANT, 0x0DA70B, 0x000000,
                                        new Item.Properties()));

        public static final RegistryObject<Item> ENDERMAN_SPAWN_EGG = ITEMS.register("enderman_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.ENDERMAN_SERVANT, 0x161616, 0x000000,
                                        new Item.Properties()));

        public static final RegistryObject<Item> SHULKER_SPAWN_EGG = ITEMS.register("shulker_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.SHULKER_SERVANT, 0x925fa3, 0x4d2c5d,
                                        new Item.Properties()));

        public static final RegistryObject<Item> ENDERMITE_SPAWN_EGG = ITEMS.register("endermite_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.ENDERMITE_SERVANT, 0x161616, 0x6a2a6a,
                                        new Item.Properties()));

        public static final RegistryObject<Item> WITHER_SPAWN_EGG = ITEMS.register("wither_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.WITHER_SERVANT, 0x000000, 0x333333,
                                        new Item.Properties()));

        public static final RegistryObject<Item> SPIDER_CREEDER_SPAWN_EGG = ITEMS.register(
                        "spider_creeder_servant_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.SPIDER_CREEDER, 0x8B0000, 0x333333,
                                        new Item.Properties()));

        public static final RegistryObject<Item> HOSTILE_SPIDER_CREEDER_SPAWN_EGG = ITEMS.register(
                        "hostile_spider_creeder_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.HOSTILE_SPIDER_CREEDER, 0x8B0000, 0xFF0000,
                                        new Item.Properties()));

        public static final RegistryObject<Item> WARDEN_SPAWN_EGG = ITEMS.register("warden_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.WARDEN_SERVANT, 0x000000, 0x333333,
                                        new Item.Properties()));

        public static final RegistryObject<Item> TORMENTOR_SPAWN_EGG = ITEMS.register("tormentor_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.TORMENTOR_SERVANT, 0x0f1119, 0x959b9b,
                                        new Item.Properties()));

        public static final RegistryObject<Item> ENDERSENT_SPAWN_EGG = ITEMS.register("endersent_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.ENDERSENT_SERVANT, 0x161616, 0x000000,
                                        new Item.Properties()));

        public static final RegistryObject<Item> ENVIOKER_SPAWN_EGG = ITEMS.register("envioker_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.ENVIOKER_SERVANT, 0x959b9b, 0x0f1119,
                                        new Item.Properties()));

        public static final RegistryObject<Item> PREACHER_SPAWN_EGG = ITEMS.register("preacher_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.PREACHER_SERVANT, 0x0f1119, 0x959b9b,
                                        new Item.Properties()));

        public static final RegistryObject<Item> CRONE_SPAWN_EGG = ITEMS.register("crone_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.CRONE_SERVANT, 0x305030, 0x959b9b,
                                        new Item.Properties()));

        public static final RegistryObject<Item> MINISTER_SPAWN_EGG = ITEMS.register("minister_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.MINISTER_SERVANT, 0x0f1119, 0x959b9b,
                                        new Item.Properties()));

        public static final RegistryObject<Item> SORCERER_SPAWN_EGG = ITEMS.register("sorcerer_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.SORCERER_SERVANT, 0x959b9b, 0x0f1119,
                                        new Item.Properties()));

        public static final RegistryObject<Item> ILLUSIONER_SPAWN_EGG = ITEMS.register("illusioner_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.ILLUSIONER_SERVANT, 0x4d2c5d, 0x925fa3,
                                        new Item.Properties()));

        public static final RegistryObject<Item> ARCH_ILLUSIONER_SPAWN_EGG = ITEMS.register("arch_illusioner_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.ARCH_ILLUSIONER, 0x2d1c3d, 0xb27fc3,
                                        new Item.Properties()));

        public static final RegistryObject<Item> ARCH_ILLUSIONER_SPAWN_EGG_HOSTILE = ITEMS.register(
                        "arch_illusioner_servant_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.ARCH_ILLUSIONER_SERVANT, 0x1d0c2d, 0xa26fb3,
                                        new Item.Properties()));

        public static final RegistryObject<Item> VIZIER_CLONE_SPAWN_EGG = ITEMS.register("vizier_clone_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.VIZIER_CLONE_SERVANT, 0x0f1119, 0x959b9b,
                                        new Item.Properties()));

        public static final RegistryObject<Item> VIZIER_SPAWN_EGG = ITEMS.register("vizier_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.VIZIER_SERVANT, 0x959b9b, 0x0f1119,
                                        new Item.Properties()));

        public static final RegistryObject<Item> ROYALGUARD_SPAWN_EGG = ITEMS.register("royalguard_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.ROYALGUARD_SERVANT, 0x4d2c5d, 0x925fa3,
                                        new Item.Properties()));

        public static final RegistryObject<Item> ICE_CREEPER_SPAWN_EGG = ITEMS.register("ice_creeper_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.ICE_CREEPER, 0x0DA70B, 0x00FFFF,
                                        new Item.Properties()));

        public static final RegistryObject<Item> BOULDERING_ZOMBIE_SPAWN_EGG = ITEMS.register(
                        "bouldering_zombie_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.BOULDERING_ZOMBIE, 0x00A800, 0x799C65,
                                        new Item.Properties()));

        public static final RegistryObject<Item> ICE_CREEPER_SERVANT_SPAWN_EGG = ITEMS
                        .register("ice_creeper_servant_spawn_egg",
                                        () -> new ServantSpawnEggItem(ModEntityType.ICE_CREEPER_SERVANT, 0x0DA70B,
                                                        0x00FFFF,
                                                        new Item.Properties()));

        public static final RegistryObject<Item> MUSHROOM_MONSTROSITY_SPAWN_EGG = ITEMS
                        .register("mushroom_monstrosity_spawn_egg",
                                        () -> new ServantSpawnEggItem(ModEntityType.MUSHROOM_MONSTROSITY, 0xFF0000,
                                                        0x00FF00,
                                                        new Item.Properties()));

        public static final RegistryObject<Item> HOSTILE_MUSHROOM_MONSTROSITY_SPAWN_EGG = ITEMS
                        .register("hostile_mushroom_monstrosity_spawn_egg",
                                        () -> new ModSpawnEggItem(ModEntityType.HOSTILE_MUSHROOM_MONSTROSITY, 0xFF0000,
                                                        0x0000FF,
                                                        new Item.Properties()));

        public static final RegistryObject<Item> ANGRY_MOOSHROOM_SPAWN_EGG = ITEMS
                        .register("angry_mooshroom_spawn_egg",
                                        () -> new ServantSpawnEggItem(ModEntityType.ANGRY_MOOSHROOM, 0xA00000,
                                                        0xFF0000,
                                                        new Item.Properties()));

        public static final RegistryObject<Item> HOSTILE_ANGRY_MOOSHROOM_SPAWN_EGG = ITEMS
                        .register("hostile_angry_mooshroom_spawn_egg",
                                        () -> new ModSpawnEggItem(ModEntityType.HOSTILE_ANGRY_MOOSHROOM, 0xA00000,
                                                        0xFF0000,
                                                        new Item.Properties()));

        public static final RegistryObject<Item> HOSTILE_TWILIGHT_GOAT_SPAWN_EGG = ITEMS
                        .register("hostile_twilight_goat_spawn_egg",
                                        () -> new ModSpawnEggItem(ModEntityType.HOSTILE_TWILIGHT_GOAT, 0x8B4513,
                                                        0xD2691E,
                                                        new Item.Properties()));

        public static final RegistryObject<Item> CAERBANNOG_RABBIT_SPAWN_EGG = ITEMS
                        .register("caerbannog_rabbit_spawn_egg",
                                        () -> new ServantSpawnEggItem(ModEntityType.CAERBANNOG_RABBIT_SERVANT, 0xFFFFFF,
                                                        0xFF0000,
                                                        new Item.Properties()));

        public static final RegistryObject<Item> ENDER_KEEPER_SERVANT_SPAWN_EGG = ITEMS
                        .register("ender_keeper_servant_spawn_egg",
                                        () -> new ServantSpawnEggItem(ModEntityType.ENDER_KEEPER_SERVANT, 0x161616,
                                                        0x000000,
                                                        new Item.Properties()));

        public static final RegistryObject<Item> APOSTLE_SERVANT_SPAWN_EGG = ITEMS.register(
                        "apostle_servant_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.APOSTLE_SERVANT, 0x4B0082, 0x8A2BE2,
                                        new Item.Properties()));

        public static final RegistryObject<Item> ZOMBIE_DARKGUARD_SPAWN_EGG = ITEMS.register(
                        "zombie_darkguard_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.ZOMBIE_DARKGUARD, 0x000000,
                                        0x006600,
                                        new Item.Properties()));

        public static final RegistryObject<Item> SKELETON_VANGUARD_SPAWN_EGG = ITEMS.register(
                        "skeleton_vanguard_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.SKELETON_VANGUARD, 0x000000,
                                        0x000066,
                                        new Item.Properties()));

        public static final RegistryObject<Item> VANGUARD_CHAMPION_SPAWN_EGG = ITEMS.register(
                        "vanguard_champion_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.VANGUARD_CHAMPION, 0x000000,
                                        0x660066,
                                        new Item.Properties()));

        public static final RegistryObject<Item> HOSTILE_VANGUARD_CHAMPION_SPAWN_EGG = ITEMS.register(
                        "hostile_vanguard_champion_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.HOSTILE_VANGUARD_CHAMPION, 0x000000,
                                        0x660066,
                                        new Item.Properties()));

        public static final RegistryObject<Item> PARCHED_SPAWN_EGG = ITEMS.register(
                        "parched_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.PARCHED, 0x8B4513,
                                        0xF5DEB3,
                                        new Item.Properties()));

        public static final RegistryObject<Item> SUNKEN_SKELETON_SPAWN_EGG = ITEMS.register(
                        "sunken_skeleton_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.SUNKEN_SKELETON, 0x4d2c5d, 0x925fa3,
                                        new Item.Properties()));

        public static final RegistryObject<Item> PARCHED_SERVANT_SPAWN_EGG = ITEMS.register(
                        "parched_servant_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.PARCHED_SERVANT, 0x8B4513,
                                        0xF5DEB3,
                                        new Item.Properties()));

        public static final RegistryObject<Item> PARCHED_NECROMANCER_SPAWN_EGG = ITEMS.register(
                        "parched_necromancer_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.PARCHED_NECROMANCER, 0x8B4513, 0xF5DEB3,
                                        new Item.Properties()));

        public static final RegistryObject<Item> WRAITH_NECROMANCER_SPAWN_EGG = ITEMS.register(
                        "wraith_necromancer_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.WRAITH_NECROMANCER, 0x4B0082, 0x8A2BE2,
                                        new Item.Properties()));

        public static final RegistryObject<Item> WRAITH_NECROMANCER_SERVANT_SPAWN_EGG = ITEMS.register(
                        "wraith_necromancer_servant_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.WRAITH_NECROMANCER_SERVANT, 0x4B0082, 0x8A2BE2,
                                        new Item.Properties()));

        public static final RegistryObject<Item> SKULL_LORD_SERVANT_SPAWN_EGG = ITEMS.register(
                        "skull_lord_servant_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.SKULL_LORD_SERVANT, 0x404040, 0x1a1a1a,
                                        new Item.Properties()));

        public static final RegistryObject<Item> BONE_LORD_SERVANT_SPAWN_EGG = ITEMS.register(
                        "bone_lord_servant_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.BONE_LORD_SERVANT, 0x8B8000, 0x696969,
                                        new Item.Properties()));

        public static final RegistryObject<Item> BOUND_SORCERER_SPAWN_EGG = ITEMS.register(
                        "bound_sorcerer_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.BOUND_SORCERER, 0x959b9b, 0x0f1119,
                                        new Item.Properties()));

        public static final RegistryObject<Item> HOSTILE_DROWNED_NECROMANCER_SPAWN_EGG = ITEMS.register(
                        "hostile_drowned_necromancer_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.HOSTILE_DROWNED_NECROMANCER, 0x006666, 0x00FFFF,
                                        new Item.Properties()));

        public static final RegistryObject<Item> PARCHED_NECROMANCER_SERVANT_SPAWN_EGG = ITEMS.register(
                        "parched_necromancer_servant_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.PARCHED_NECROMANCER_SERVANT, 0x8d837d, 0x5d5751,
                                        new Item.Properties()));

        public static final RegistryObject<Item> NAMELESS_ONE_SPAWN_EGG = ITEMS.register(
                        "nameless_one_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.NAMELESS_ONE, 0x000000, 0x00FF00,
                                        new Item.Properties()));

        public static final RegistryObject<Item> NAMELESS_ONE_SERVANT_SPAWN_EGG = ITEMS.register(
                        "nameless_one_servant_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.NAMELESS_ONE_SERVANT, 0x00FF00, 0x000000,
                                        new Item.Properties()));

        public static final RegistryObject<Item> HOSTILE_ROYALGUARD_SPAWN_EGG = ITEMS.register(
                        "hostile_royalguard_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.HOSTILE_ROYALGUARD, 0x4d2c5d, 0x925fa3,
                                        new Item.Properties()));

        public static final RegistryObject<Item> MAID_FAIRY_SERVANT_SPAWN_EGG = ITEMS.register(
                        "maid_fairy_servant_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.MAID_FAIRY_SERVANT, 0xFFB6C1, 0x9370DB,
                                        new Item.Properties()));

        public static final RegistryObject<Item> VINDICATOR_CHEF_SPAWN_EGG = ITEMS.register(
                        "vindicator_chef_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.VINDICATOR_CHEF, 0x9D9D9D, 0x4C4C4C,
                                        new Item.Properties()));

        public static final RegistryObject<Item> HOSTILE_GNASHER_SPAWN_EGG = ITEMS.register(
                        "hostile_gnasher_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.HOSTILE_GNASHER, 0x0000FF, 0x00FFFF,
                                        new Item.Properties()));

        public static final RegistryObject<Item> HOSTILE_WILDFIRE_SPAWN_EGG = ITEMS.register(
                        "hostile_wildfire_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.HOSTILE_WILDFIRE, 0xDD6600, 0xFFAA00,
                                        new Item.Properties()));

        public static final RegistryObject<Item> HOSTILE_TROPICAL_SLIME_SPAWN_EGG = ITEMS.register(
                        "hostile_tropical_slime_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.HOSTILE_TROPICAL_SLIME, 0x006600, 0x00FFFF,
                                        new Item.Properties()));

        public static final RegistryObject<Item> CORRUPTED_SLIME_SPAWN_EGG = ITEMS.register(
                        "corrupted_slime_servant_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.CORRUPTED_SLIME, 0x4B0082, 0x8B00FF,
                                        new Item.Properties()));

        public static final RegistryObject<Item> HOSTILE_MINI_GHAST_SPAWN_EGG = ITEMS.register(
                        "hostile_mini_ghast_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.HOSTILE_MINI_GHAST, 0xFFFFFF, 0xFF0000,
                                        new Item.Properties()));

        public static final RegistryObject<Item> JUNGLE_ZOMBIE_SPAWN_EGG = ITEMS.register(
                        "jungle_zombie_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.JUNGLE_ZOMBIE, 0x4C763C, 0x7D8E4D,
                                        new Item.Properties()));

        public static final RegistryObject<Item> HOSTILE_SNAPPER_SPAWN_EGG = ITEMS.register(
                        "hostile_snapper_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.HOSTILE_SNAPPER, 0x8B4513, 0xDAA520,
                                        new Item.Properties()));

        public static final RegistryObject<Item> FROZEN_ZOMBIE_SPAWN_EGG = ITEMS.register(
                        "frozen_zombie_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.FROZEN_ZOMBIE, 0xE0FFFF, 0x4682B4,
                                        new Item.Properties()));

        public static final RegistryObject<Item> SCARLET_VEX_SPAWN_EGG = ITEMS.register(
                        "scarlet_vex_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.SCARLET_VEX, 0xFF3333, 0x660000,
                                        new Item.Properties()));

        public static final RegistryObject<Item> TOWER_WRAITH_SPAWN_EGG = ITEMS.register(
                        "tower_wraith_servant_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.TOWER_WRAITH_SERVANT, 0xE0FFFF, 0x9370DB,
                                        new Item.Properties()));

        public static final RegistryObject<Item> HOSTILE_TOWER_WRAITH_SPAWN_EGG = ITEMS.register(
                        "hostile_tower_wraith_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.HOSTILE_TOWER_WRAITH, 0xFF6B6B, 0x4A0080,
                                        new Item.Properties()));

        public static final RegistryObject<Item> TOWER_WITCH_SPAWN_EGG = ITEMS.register(
                        "tower_witch_spawn_egg",
                        () -> new ModSpawnEggItem(ModEntityType.TOWER_WITCH, 0x5D2E8C, 0x9B7EBD,
                                        new Item.Properties()));

        public static final RegistryObject<Item> TOWER_WITCH_SERVANT_SPAWN_EGG = ITEMS.register(
                        "tower_witch_servant_spawn_egg",
                        () -> new ServantSpawnEggItem(ModEntityType.TOWER_WITCH_SERVANT, 0x5D2E8C, 0x9B7EBD,
                                        new Item.Properties()));

        public static final RegistryObject<Item> WIGHT_BAIT_ITEM = ITEMS.register("wight_bait",
                        () -> new BlockItem(ModBlocks.WIGHT_BAIT.get(), new Item.Properties()));

        public static final RegistryObject<Item> RAVAGER_HUNT_TRIGGER = ITEMS.register(
                        "ravager_hunt_trigger",
                        () -> new BlockItem(ModBlocks.RAVAGER_HUNT_TRIGGER.get(), new Item.Properties()));

        public static final RegistryObject<Item> ENDERSENT_GENERATOR = ITEMS.register(
                        "endersent_generator",
                        () -> new BlockItem(ModBlocks.ENDERSENT_GENERATOR.get(), new Item.Properties()));

        public static final RegistryObject<Item> POISONOUS_MUSHROOM = ITEMS.register(
                        "poisonous_mushroom",
                        () -> new BlockItem(ModBlocks.POISONOUS_MUSHROOM.get(), new Item.Properties()));

        public static final RegistryObject<Item> ANCIENT_RUNE_ALTAR = ITEMS.register(
                        "ancient_rune_altar",
                        () -> new BlockItem(ModBlocks.ANCIENT_RUNE_ALTAR.get(), new Item.Properties()));

        public static final RegistryObject<Item> ANCIENT_SERVANT_ALTAR = ITEMS.register(
                        "ancient_servant_altar",
                        () -> new BlockItem(ModBlocks.ANCIENT_SERVANT_ALTAR.get(), new Item.Properties()));

        public static final RegistryObject<Item> NBT_ENTITY_SPAWN_EGG = ITEMS.register(
                        "nbt_entity_spawn_egg",
                        () -> new NBTEntitySpawnEggItem(new Item.Properties()));

}