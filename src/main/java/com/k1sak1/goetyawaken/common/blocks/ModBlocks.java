package com.k1sak1.goetyawaken.common.blocks;

import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
                        GoetyAwaken.MODID);

        public static void init() {
                ModBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static boolean never(BlockState state, BlockGetter getter, BlockPos pos) {
                return false;
        }

        public enum LootTableType {
                EMPTY,
                DROP
        }

        public static final RegistryObject<Block> SHADOW_SHRIEKER = BLOCKS.register("shadow_shrieker",
                        () -> new ShadowShriekerBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BLACK)
                                        .instrument(NoteBlockInstrument.BASEDRUM)
                                        .requiresCorrectToolForDrops()
                                        .strength(5.0F, 6.0F)
                                        .sound(SoundType.SCULK_SHRIEKER)
                                        .lightLevel(state -> 7)));

        public static final RegistryObject<Block> SOUL_RUBY_BLOCK = BLOCKS.register("soul_ruby_block",
                        () -> new Block(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BLACK)
                                        .instrument(NoteBlockInstrument.BASEDRUM)
                                        .requiresCorrectToolForDrops()
                                        .strength(5.0F, 6.0F)
                                        .sound(SoundType.METAL)
                                        .lightLevel(state -> 7)));

        public static final RegistryObject<Block> SOUL_SAPPHIRE_BLOCK = BLOCKS.register("soul_sapphire_block",
                        () -> new Block(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BLUE)
                                        .instrument(NoteBlockInstrument.BASEDRUM)
                                        .requiresCorrectToolForDrops()
                                        .strength(5.0F, 6.0F)
                                        .sound(SoundType.METAL)
                                        .lightLevel(state -> 7)));

        public static final RegistryObject<Block> NETHER_REACTOR_CORE = BLOCKS.register("nether_reactor_core",
                        () -> new NetherReactorCoreBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BLACK)
                                        .instrument(NoteBlockInstrument.BASEDRUM)
                                        .requiresCorrectToolForDrops()
                                        .strength(5.0F, 6.0F)
                                        .sound(SoundType.STONE)
                                        .lightLevel(state -> 15)));

        public static final RegistryObject<Block> ENDER_ACCESS_LECTERN = BLOCKS.register("ender_access_lectern",
                        () -> new EnderAccessLecternBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.WOOD)
                                        .instrument(NoteBlockInstrument.BASS)
                                        .strength(2.5F)
                                        .sound(SoundType.WOOD)
                                        .lightLevel(state -> 7)));

        public static final RegistryObject<Block> ECHOING_ENDER_SHELF = BLOCKS.register("echoing_ender_shelf",
                        () -> new EchoingEnderShelfBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.WOOD)
                                        .instrument(NoteBlockInstrument.BASS)
                                        .strength(1.5F)
                                        .sound(SoundType.CHISELED_BOOKSHELF)
                                        .ignitedByLava()));

        public static final RegistryObject<Block> WIGHT_BAIT = BLOCKS.register("wight_bait",
                        () -> new WightBaitBlock());

        public static final RegistryObject<Block> ENDERSENT_GENERATOR = BLOCKS.register("endersent_generator",
                        () -> new EndersentGeneratorBlock());

        public static final RegistryObject<Block> RAVAGER_HUNT_TRIGGER = BLOCKS.register("ravager_hunt_trigger",
                        () -> new RavagerHuntTriggerBlock());

        public static final RegistryObject<Block> MUSHROOM_COATED_ALLOY_BLOCK = BLOCKS.register(
                        "mushroom_coated_alloy_block",
                        () -> new Block(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.METAL)
                                        .requiresCorrectToolForDrops()
                                        .strength(100.0F, 1000.0F)
                                        .sound(SoundType.NETHERITE_BLOCK)));

        public static final RegistryObject<MushroomBlock> POISONOUS_MUSHROOM = BLOCKS.register("poisonous_mushroom",
                        () -> new PoisonousMushroomBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_RED)
                                        .noCollission()
                                        .instabreak()
                                        .sound(SoundType.GRASS)
                                        .offsetType(BlockBehaviour.OffsetType.XZ)
                                        .pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY),
                                        net.minecraft.data.worldgen.features.TreeFeatures.HUGE_RED_MUSHROOM));

        public static final RegistryObject<Block> ANCIENT_RUNE_ALTAR = BLOCKS.register("ancient_rune_altar",
                        () -> new AncientRuneAltarBlock());

        public static final RegistryObject<Block> ANCIENT_SERVANT_ALTAR = BLOCKS.register("ancient_servant_altar",
                        () -> new AncientServantAltarBlock());

        public static final RegistryObject<Block> ALLY_PITHOS = BLOCKS.register("ally_pithos",
                        () -> new AllyPithosBlock(BlockBehaviour.Properties.of()
                                        .pushReaction(PushReaction.BLOCK)
                                        .mapColor(MapColor.STONE)
                                        .requiresCorrectToolForDrops()
                                        .strength(5F, 6.0F)
                                        .sound(SoundType.BONE_BLOCK)));

        public static final RegistryObject<Block> TRIAL_SPAWNER = BLOCKS.register("trial_spawner",
                        () -> new TrialSpawnerBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_PURPLE)
                                        .instrument(NoteBlockInstrument.BASEDRUM)
                                        .requiresCorrectToolForDrops()
                                        .lightLevel(l -> l.getValue(TrialSpawnerBlock.STATE).lightLevel())
                                        .strength(50.0F)
                                        .sound(SoundType.METAL)
                                        .isViewBlocking(ModBlocks::never)
                                        .noOcclusion()));

        public static final RegistryObject<Block> VAULT = BLOCKS.register("vault", VaultBlock::new);

        public static final RegistryObject<Block> TABOO_BRICKS_CHISELED = BLOCKS.register("taboo_bricks_chiseled",
                        () -> new Block(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.STONE)
                                        .requiresCorrectToolForDrops()
                                        .strength(50.0F, 1200.0F)
                                        .sound(SoundType.STONE)));

        public static final RegistryObject<Block> TABOO_TORCH = BLOCKS.register("taboo_torch",
                        () -> new TabooTorchBlock(BlockBehaviour.Properties.of()
                                        .noCollission()
                                        .instabreak()
                                        .lightLevel(state -> 14)));

        public static final RegistryObject<Block> WALL_TABOO_TORCH = BLOCKS.register("wall_taboo_torch",
                        () -> new WallTabooTorchBlock(BlockBehaviour.Properties.of()
                                        .noCollission()
                                        .instabreak()
                                        .lightLevel(state -> 14)));

        public static final RegistryObject<Block> TABOO_GLASS = BLOCKS.register("taboo_glass",
                        () -> new Block(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.NONE)
                                        .strength(5.0F, 600.0F)
                                        .sound(SoundType.GLASS)
                                        .noOcclusion()));

        public static final RegistryObject<Block> TABOO_LAMP = BLOCKS.register("taboo_lamp",
                        () -> new TabooLampBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.METAL)
                                        .requiresCorrectToolForDrops()
                                        .strength(5.0F, 1200.0F)
                                        .sound(SoundType.METAL)
                                        .lightLevel(state -> state.getValue(
                                                        net.minecraft.world.level.block.RedstoneLampBlock.LIT) ? 15
                                                                        : 0)));

        public static final RegistryObject<Block> SMOOTH_TABOO_STONE = BLOCKS.register("smooth_taboo_stone",
                        () -> new Block(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.STONE)
                                        .requiresCorrectToolForDrops()
                                        .strength(50.0F, 1200.0F)
                                        .sound(SoundType.STONE)));

        public static final RegistryObject<Block> TABOO_STONE = BLOCKS.register("taboo_stone",
                        () -> new Block(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.STONE)
                                        .requiresCorrectToolForDrops()
                                        .strength(50.0F, 1200.0F)
                                        .sound(SoundType.STONE)));

        public static final RegistryObject<Block> TABOO_LAMP_CHISELED = BLOCKS.register("taboo_lamp_chiseled",
                        () -> new TabooLampChiseledBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.METAL)
                                        .requiresCorrectToolForDrops()
                                        .strength(5.0F, 1200.0F)
                                        .sound(SoundType.METAL)
                                        .lightLevel(state -> state.getValue(
                                                        net.minecraft.world.level.block.RedstoneLampBlock.LIT) ? 15
                                                                        : 0)));

        public static final RegistryObject<Block> TABOO_LAMP_ENGRAVED = BLOCKS.register("taboo_lamp_engraved",
                        () -> new TabooLampEngravedBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.METAL)
                                        .requiresCorrectToolForDrops()
                                        .strength(5.0F, 1200.0F)
                                        .sound(SoundType.METAL)
                                        .lightLevel(state -> state.getValue(
                                                        net.minecraft.world.level.block.RedstoneLampBlock.LIT) ? 15
                                                                        : 0)));

        public static final RegistryObject<Block> TABOO_BOOKSHELF = BLOCKS.register("taboo_bookshelf",
                        () -> new TabooBookshelfBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.WOOD)
                                        .requiresCorrectToolForDrops()
                                        .strength(50.0F, 1200.0F)
                                        .sound(SoundType.WOOD)));

        public static final RegistryObject<Block> TABOO_TERRACOTTA = BLOCKS.register("taboo_terracotta",
                        () -> new Block(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.STONE)
                                        .requiresCorrectToolForDrops()
                                        .strength(50.0F, 1200.0F)
                                        .sound(SoundType.STONE)));

        public static final RegistryObject<Block> TABOO_BRICKS = BLOCKS.register("taboo_bricks",
                        () -> new Block(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.STONE)
                                        .requiresCorrectToolForDrops()
                                        .strength(50.0F, 1200.0F)
                                        .sound(SoundType.STONE)));

        public static final RegistryObject<Block> TABOO_BRICKS_ENGRAVED = BLOCKS.register("taboo_bricks_engraved",
                        () -> new Block(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.STONE)
                                        .requiresCorrectToolForDrops()
                                        .strength(50.0F, 1200.0F)
                                        .sound(SoundType.STONE)));

        public static final RegistryObject<Block> SMOOTH_TABOO_STONE_STAIRS = BLOCKS.register(
                        "smooth_taboo_stone_stairs",
                        () -> new StairBlock(() -> SMOOTH_TABOO_STONE.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.STONE)
                                                        .requiresCorrectToolForDrops()
                                                        .strength(50.0F, 1200.0F)
                                                        .sound(SoundType.STONE)));

        public static final RegistryObject<Block> SMOOTH_TABOO_STONE_SLAB = BLOCKS.register("smooth_taboo_stone_slab",
                        () -> new SlabBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.STONE)
                                        .requiresCorrectToolForDrops()
                                        .strength(50.0F, 1200.0F)
                                        .sound(SoundType.STONE)));

        public static final RegistryObject<Block> SMOOTH_TABOO_STONE_WALL = BLOCKS.register("smooth_taboo_stone_wall",
                        () -> new WallBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.STONE)
                                        .requiresCorrectToolForDrops()
                                        .strength(50.0F, 1200.0F)
                                        .sound(SoundType.STONE)));

        public static final RegistryObject<Block> TABOO_STONE_STAIRS = BLOCKS.register("taboo_stone_stairs",
                        () -> new StairBlock(() -> TABOO_STONE.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.STONE)
                                                        .requiresCorrectToolForDrops()
                                                        .strength(50.0F, 1200.0F)
                                                        .sound(SoundType.STONE)));

        public static final RegistryObject<Block> TABOO_STONE_SLAB = BLOCKS.register("taboo_stone_slab",
                        () -> new SlabBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.STONE)
                                        .requiresCorrectToolForDrops()
                                        .strength(50.0F, 1200.0F)
                                        .sound(SoundType.STONE)));

        public static final RegistryObject<Block> TABOO_STONE_WALL = BLOCKS.register("taboo_stone_wall",
                        () -> new WallBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.STONE)
                                        .requiresCorrectToolForDrops()
                                        .strength(50.0F, 1200.0F)
                                        .sound(SoundType.STONE)));

        public static final RegistryObject<Block> TABOO_TERRACOTTA_STAIRS = BLOCKS.register("taboo_terracotta_stairs",
                        () -> new StairBlock(() -> TABOO_TERRACOTTA.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.STONE)
                                                        .requiresCorrectToolForDrops()
                                                        .strength(50.0F, 1200.0F)
                                                        .sound(SoundType.STONE)));

        public static final RegistryObject<Block> TABOO_TERRACOTTA_SLAB = BLOCKS.register("taboo_terracotta_slab",
                        () -> new SlabBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.STONE)
                                        .requiresCorrectToolForDrops()
                                        .strength(50.0F, 1200.0F)
                                        .sound(SoundType.STONE)));

        public static final RegistryObject<Block> TABOO_TERRACOTTA_WALL = BLOCKS.register("taboo_terracotta_wall",
                        () -> new WallBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.STONE)
                                        .requiresCorrectToolForDrops()
                                        .strength(50.0F, 1200.0F)
                                        .sound(SoundType.STONE)));

        public static final RegistryObject<Block> TABOO_BRICKS_STAIRS = BLOCKS.register("taboo_bricks_stairs",
                        () -> new StairBlock(() -> TABOO_BRICKS.get().defaultBlockState(),
                                        BlockBehaviour.Properties.of()
                                                        .mapColor(MapColor.STONE)
                                                        .requiresCorrectToolForDrops()
                                                        .strength(50.0F, 1200.0F)
                                                        .sound(SoundType.STONE)));

        public static final RegistryObject<Block> TABOO_BRICKS_SLAB = BLOCKS.register("taboo_bricks_slab",
                        () -> new SlabBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.STONE)
                                        .requiresCorrectToolForDrops()
                                        .strength(50.0F, 1200.0F)
                                        .sound(SoundType.STONE)));

        public static final RegistryObject<Block> TABOO_BRICKS_WALL = BLOCKS.register("taboo_bricks_wall",
                        () -> new WallBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.STONE)
                                        .requiresCorrectToolForDrops()
                                        .strength(50.0F, 1200.0F)
                                        .sound(SoundType.STONE)));

        public static final RegistryObject<Block> TABOO_PILLAR = BLOCKS.register("taboo_pillar",
                        () -> new Block(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.STONE)
                                        .requiresCorrectToolForDrops()
                                        .strength(50.0F, 1200.0F)
                                        .sound(SoundType.STONE)));

        public static final RegistryObject<Block> TABOO_PILLAR_TOP = BLOCKS.register("taboo_pillar_top",
                        () -> new Block(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.STONE)
                                        .requiresCorrectToolForDrops()
                                        .strength(50.0F, 1200.0F)
                                        .sound(SoundType.STONE)));

        public static final RegistryObject<Block> TABOO_PILLAR_BASE = BLOCKS.register("taboo_pillar_base",
                        () -> new Block(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.STONE)
                                        .requiresCorrectToolForDrops()
                                        .strength(50.0F, 1200.0F)
                                        .sound(SoundType.STONE)));

        public static final RegistryObject<Block> SAND_PILE = BLOCKS.register("sand_pile",
                        () -> new SandPileBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.SAND)
                                        .instrument(NoteBlockInstrument.SNARE)
                                        .strength(0.5F)
                                        .sound(SoundType.SAND)));

        public static final RegistryObject<Block> GORGEOUS_URN_INDIGO = BLOCKS.register("gorgeous_urn_indigo",
                        () -> new GorgeousUrnBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_BLUE)
                                        .instrument(NoteBlockInstrument.BASEDRUM)
                                        .requiresCorrectToolForDrops()
                                        .strength(10.0F, 1000.0F)
                                        .sound(SoundType.DECORATED_POT)
                                        .lightLevel(state -> 5)
                                        .emissiveRendering((state, world, pos) -> true)));

        public static final RegistryObject<Block> GORGEOUS_URN_VERDANT = BLOCKS.register("gorgeous_urn_verdant",
                        () -> new GorgeousUrnBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_GREEN)
                                        .instrument(NoteBlockInstrument.BASEDRUM)
                                        .requiresCorrectToolForDrops()
                                        .strength(10.0F, 1000.0F)
                                        .sound(SoundType.DECORATED_POT)
                                        .lightLevel(state -> 5)
                                        .emissiveRendering((state, world, pos) -> true)));

        public static final RegistryObject<Block> GORGEOUS_URN_PALE = BLOCKS.register("gorgeous_urn_pale",
                        () -> new GorgeousUrnBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.TERRACOTTA_WHITE)
                                        .instrument(NoteBlockInstrument.BASEDRUM)
                                        .requiresCorrectToolForDrops()
                                        .strength(10.0F, 1000.0F)
                                        .sound(SoundType.DECORATED_POT)));

        public static final RegistryObject<Block> GORGEOUS_URN_CRIMSON = BLOCKS.register("gorgeous_urn_crimson",
                        () -> new GorgeousUrnBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.COLOR_RED)
                                        .instrument(NoteBlockInstrument.BASEDRUM)
                                        .requiresCorrectToolForDrops()
                                        .strength(10.0F, 1000.0F)
                                        .sound(SoundType.DECORATED_POT)));

        public static final RegistryObject<Block> DARK_SOUL_CANDLE = BLOCKS.register("dark_soul_candle",
                        () -> new DarkSoulCandleBlock());

        public static final RegistryObject<NamelessChestBlock> NAMELESS_CHEST = BLOCKS.register("nameless_chest",
                        () -> new NamelessChestBlock(BlockBehaviour.Properties.of()
                                        .mapColor(MapColor.METAL)
                                        .instrument(NoteBlockInstrument.BASS)
                                        .requiresCorrectToolForDrops()
                                        .strength(50.0F, 1000.0F)
                                        .sound(SoundType.METAL)
                                        .lightLevel(state -> 12)
                                        .emissiveRendering((state, level, pos) -> true)));

        public static final RegistryObject<Block> SLANT_SANDSTONE = BLOCKS.register("slant_sandstone",
                        () -> new SlantSandstoneBlock());

        public static final RegistryObject<Block> SANDSTONE_COLUMN = BLOCKS.register("sandstone_column",
                        () -> new SandstoneColumnBlock());

        public static final RegistryObject<Block> TOWER_FLOWER_POT = BLOCKS.register("tower_flowerpot",
                        () -> new TowerFlowerPotBlock());

        public static final RegistryObject<Block> REDSTONE_CLUSTER = BLOCKS.register("redstone_cluster",
                        () -> new RedstoneClusterBlock(15));

        public static final RegistryObject<Block> REDSTONE_CLUSTER_MIDDLE = BLOCKS.register("redstone_cluster_middle",
                        () -> new RedstoneClusterMiddleBlock(10));

        public static final RegistryObject<Block> REDSTONE_CLUSTER_SMALL = BLOCKS.register("redstone_cluster_small",
                        () -> new RedstoneClusterSmallBlock(5));

        public static final RegistryObject<Block> DARK_MENDER = BLOCKS.register("dark_mender",
                        () -> new DarkMenderBlock());

        public static final RegistryObject<Block> MOOSHROOM_MONSTROSITY_HEAD = BLOCKS.register(
                        "mooshroom_monstrosity_head",
                        () -> new MushroomMonstrosityHeadBlock());

        public static final RegistryObject<Block> WALL_MOOSHROOM_MONSTROSITY_HEAD = BLOCKS.register(
                        "wall_mooshroom_monstrosity_head",
                        () -> new WallMushroomMonstrosityHeadBlock());
}
