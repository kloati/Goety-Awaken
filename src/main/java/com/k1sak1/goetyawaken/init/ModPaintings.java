package com.k1sak1.goetyawaken.init;

import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPaintings {
        public static final DeferredRegister<PaintingVariant> OMINOUS_PAINTING_VARIANTS = DeferredRegister
                        .create(ForgeRegistries.PAINTING_VARIANTS, GoetyAwaken.MODID);

        public static final RegistryObject<PaintingVariant> ARCH_1 = OMINOUS_PAINTING_VARIANTS.register("arch1",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> ARCH_2 = OMINOUS_PAINTING_VARIANTS.register("arch2",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> CONJURER = OMINOUS_PAINTING_VARIANTS.register("conjurer",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> EVOKER = OMINOUS_PAINTING_VARIANTS.register("evoker",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> FOLLOWER = OMINOUS_PAINTING_VARIANTS.register("follower",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> FREAKAGER = OMINOUS_PAINTING_VARIANTS.register("freakager",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> LEVEL6 = OMINOUS_PAINTING_VARIANTS.register("level6",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> MAGISPELLER = OMINOUS_PAINTING_VARIANTS.register(
                        "magispeller",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> MASQUERADER = OMINOUS_PAINTING_VARIANTS.register(
                        "masquerader",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> MINISTER1 = OMINOUS_PAINTING_VARIANTS.register("minister1",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> MINISTER2 = OMINOUS_PAINTING_VARIANTS.register("minister2",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> MINISTER3 = OMINOUS_PAINTING_VARIANTS.register("minister3",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> SORCERER = OMINOUS_PAINTING_VARIANTS.register("sorcerer",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> SPIRITCALLER = OMINOUS_PAINTING_VARIANTS
                        .register("spiritcaller", () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> VIZIER = OMINOUS_PAINTING_VARIANTS.register("vizier",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> WU1WU2 = OMINOUS_PAINTING_VARIANTS.register("wu1wu2",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> K1SAK1 = OMINOUS_PAINTING_VARIANTS.register("k1sak1",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> WARLOCK = OMINOUS_PAINTING_VARIANTS.register("warlock",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> HERETIC = OMINOUS_PAINTING_VARIANTS.register("heretic",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> WITCH = OMINOUS_PAINTING_VARIANTS.register("witch",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> APOSTLE1 = OMINOUS_PAINTING_VARIANTS.register("apostle1",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> CRONE = OMINOUS_PAINTING_VARIANTS.register("crone",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> APOSTLE2 = OMINOUS_PAINTING_VARIANTS.register("apostle2",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> CHEF = OMINOUS_PAINTING_VARIANTS.register("chef",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> MAVERICK = OMINOUS_PAINTING_VARIANTS.register("maverick",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> NECR = OMINOUS_PAINTING_VARIANTS.register("necr",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> TEXWITCH = OMINOUS_PAINTING_VARIANTS.register("texwitch",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> TEXILLAGER = OMINOUS_PAINTING_VARIANTS.register(
                        "texillager",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> TEXVILLAGER = OMINOUS_PAINTING_VARIANTS.register(
                        "texvillager",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> TEXAXEILLAGER = OMINOUS_PAINTING_VARIANTS.register(
                        "texaxeillager",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> ANOTHERSORCERER = OMINOUS_PAINTING_VARIANTS.register(
                        "another_sorcerer",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> ARCHIVIST = OMINOUS_PAINTING_VARIANTS.register(
                        "archivist",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> HAPPY_GHAST = OMINOUS_PAINTING_VARIANTS.register(
                        "happy_ghast",
                        () -> new PaintingVariant(32, 32));

        public static final RegistryObject<PaintingVariant> ROYAL_GUARD = OMINOUS_PAINTING_VARIANTS.register(
                        "royal_guard",
                        () -> new PaintingVariant(32, 32));

        public static final RegistryObject<PaintingVariant> ARCH_WITH_A_PEARL_EARRING = OMINOUS_PAINTING_VARIANTS
                        .register(
                                        "arch_with_a_pearl_earring",
                                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> RAINBOW_ARCH_2 = OMINOUS_PAINTING_VARIANTS.register(
                        "rainbow_arch_2",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> RAINBOW_ARCH_1 = OMINOUS_PAINTING_VARIANTS.register(
                        "rainbow_arch_1",
                        () -> new PaintingVariant(32, 32));

        public static final RegistryObject<PaintingVariant> ARCH_ILLUSIONER = OMINOUS_PAINTING_VARIANTS.register(
                        "arch_illusioner",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> ARMORED_MOUNTAINEER = OMINOUS_PAINTING_VARIANTS.register(
                        "armored_mountaineer",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> ARMORED_PILLAGER = OMINOUS_PAINTING_VARIANTS.register(
                        "armored_pillager",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> ARMORED_VINDICATOR = OMINOUS_PAINTING_VARIANTS.register(
                        "armored_vindicator",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> BASHER = OMINOUS_PAINTING_VARIANTS.register(
                        "basher",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> CONQUILLAGER = OMINOUS_PAINTING_VARIANTS.register(
                        "conquillager",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> CRYOLOGER = OMINOUS_PAINTING_VARIANTS.register(
                        "cryologer",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> ENCHANTER = OMINOUS_PAINTING_VARIANTS.register(
                        "enchanter",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> ENVIOKER = OMINOUS_PAINTING_VARIANTS.register(
                        "envioker",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> GEOMANCER = OMINOUS_PAINTING_VARIANTS.register(
                        "geomancer",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> ICEOLOGER = OMINOUS_PAINTING_VARIANTS.register(
                        "iceologer",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> ILLUSIONER = OMINOUS_PAINTING_VARIANTS.register(
                        "illusioner",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> MOUNTAINEER = OMINOUS_PAINTING_VARIANTS.register(
                        "mountaineer",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> PIKER = OMINOUS_PAINTING_VARIANTS.register(
                        "piker",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> PILLAGER = OMINOUS_PAINTING_VARIANTS.register(
                        "pillager",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> PREACHER = OMINOUS_PAINTING_VARIANTS.register(
                        "preacher",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> SIGNALER = OMINOUS_PAINTING_VARIANTS.register(
                        "signaler",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> STORM_CASTER = OMINOUS_PAINTING_VARIANTS.register(
                        "storm_caster",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> THE_NEOILLAGERS = OMINOUS_PAINTING_VARIANTS.register(
                        "the_neoillagers",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> TOWER_GUARD = OMINOUS_PAINTING_VARIANTS.register(
                        "tower_guard",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> VINDICATOR = OMINOUS_PAINTING_VARIANTS.register(
                        "vindicator",
                        () -> new PaintingVariant(32, 32));
        public static final RegistryObject<PaintingVariant> WINDCALLER = OMINOUS_PAINTING_VARIANTS.register(
                        "windcaller",
                        () -> new PaintingVariant(32, 32));

        public static void init() {
                OMINOUS_PAINTING_VARIANTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
}