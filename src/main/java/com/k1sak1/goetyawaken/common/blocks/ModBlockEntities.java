package com.k1sak1.goetyawaken.common.blocks;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.blocks.ModBlocks;
import com.k1sak1.goetyawaken.common.blocks.entity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
                        .create(ForgeRegistries.BLOCK_ENTITY_TYPES, GoetyAwaken.MODID);

        public static final RegistryObject<BlockEntityType<ShadowShriekerBlockEntity>> SHADOW_SHRIEKER = BLOCK_ENTITIES
                        .register("shadow_shrieker",
                                        () -> BlockEntityType.Builder
                                                        .of(ShadowShriekerBlockEntity::new,
                                                                        ModBlocks.SHADOW_SHRIEKER.get())
                                                        .build(null));

        public static final RegistryObject<BlockEntityType<EnderAccessLecternBlockEntity>> ENDER_ACCESS_LECTERN = BLOCK_ENTITIES
                        .register("ender_access_lectern",
                                        () -> BlockEntityType.Builder
                                                        .of(EnderAccessLecternBlockEntity::new,
                                                                        ModBlocks.ENDER_ACCESS_LECTERN.get())
                                                        .build(null));

        public static final RegistryObject<BlockEntityType<EchoingEnderShelfBlockEntity>> ECHOING_ENDER_SHELF = BLOCK_ENTITIES
                        .register("echoing_ender_shelf",
                                        () -> BlockEntityType.Builder
                                                        .of(EchoingEnderShelfBlockEntity::new,
                                                                        ModBlocks.ECHOING_ENDER_SHELF.get())
                                                        .build(null));

        public static final RegistryObject<BlockEntityType<WightBaitBlockEntity>> WIGHT_BAIT_BLOCK_ENTITY = BLOCK_ENTITIES
                        .register("wight_bait_block_entity",
                                        () -> BlockEntityType.Builder
                                                        .of(WightBaitBlockEntity::new, ModBlocks.WIGHT_BAIT.get())
                                                        .build(null));

        public static final RegistryObject<BlockEntityType<EndersentGeneratorBlockEntity>> ENDERSENT_GENERATOR_BLOCK_ENTITY = BLOCK_ENTITIES
                        .register("endersent_generator_block_entity",
                                        () -> BlockEntityType.Builder
                                                        .of(EndersentGeneratorBlockEntity::new,
                                                                        ModBlocks.ENDERSENT_GENERATOR.get())
                                                        .build(null));

        public static final RegistryObject<BlockEntityType<RavagerHuntTriggerBlockEntity>> RAVAGER_HUNT_TRIGGER_BLOCK_ENTITY = BLOCK_ENTITIES
                        .register("ravager_hunt_trigger_block_entity",
                                        () -> BlockEntityType.Builder
                                                        .of(RavagerHuntTriggerBlockEntity::new,
                                                                        ModBlocks.RAVAGER_HUNT_TRIGGER.get())
                                                        .build(null));

        public static final RegistryObject<BlockEntityType<PoisonousMushroomBlockEntity>> POISONOUS_MUSHROOM = BLOCK_ENTITIES
                        .register("poisonous_mushroom",
                                        () -> BlockEntityType.Builder
                                                        .of(PoisonousMushroomBlockEntity::new,
                                                                        ModBlocks.POISONOUS_MUSHROOM.get())
                                                        .build(null));

        public static final RegistryObject<BlockEntityType<AncientRuneAltarBlockEntity>> ANCIENT_RUNE_ALTAR = BLOCK_ENTITIES
                        .register("ancient_rune_altar",
                                        () -> BlockEntityType.Builder
                                                        .of(AncientRuneAltarBlockEntity::new,
                                                                        ModBlocks.ANCIENT_RUNE_ALTAR.get())
                                                        .build(null));

        public static final RegistryObject<BlockEntityType<AncientServantAltarBlockEntity>> ANCIENT_SERVANT_ALTAR = BLOCK_ENTITIES
                        .register("ancient_servant_altar",
                                        () -> BlockEntityType.Builder
                                                        .of(AncientServantAltarBlockEntity::new,
                                                                        ModBlocks.ANCIENT_SERVANT_ALTAR.get())
                                                        .build(null));
        public static final RegistryObject<BlockEntityType<TrialSpawnerBlockEntity>> TRIAL_SPAWNER = BLOCK_ENTITIES
                        .register("trial_spawner",
                                        () -> BlockEntityType.Builder
                                                        .of(TrialSpawnerBlockEntity::new, ModBlocks.TRIAL_SPAWNER.get())
                                                        .build(null));

        public static final RegistryObject<BlockEntityType<VaultBlockEntity>> VAULT = BLOCK_ENTITIES
                        .register("vault",
                                        () -> BlockEntityType.Builder
                                                        .of(VaultBlockEntity::new, ModBlocks.VAULT.get())
                                                        .build(null));

        public static final RegistryObject<BlockEntityType<AllyPithosBlockEntity>> ALLY_PITHOS = BLOCK_ENTITIES
                        .register("ally_pithos",
                                        () -> BlockEntityType.Builder
                                                        .of(AllyPithosBlockEntity::new, ModBlocks.ALLY_PITHOS.get())
                                                        .build(null));

        public static final RegistryObject<BlockEntityType<GorgeousUrnBlockEntity>> GORGEOUS_URN = BLOCK_ENTITIES
                        .register("gorgeous_urn",
                                        () -> BlockEntityType.Builder
                                                        .of(GorgeousUrnBlockEntity::new,
                                                                        ModBlocks.GORGEOUS_URN_INDIGO.get(),
                                                                        ModBlocks.GORGEOUS_URN_VERDANT.get(),
                                                                        ModBlocks.GORGEOUS_URN_PALE.get(),
                                                                        ModBlocks.GORGEOUS_URN_CRIMSON.get())
                                                        .build(null));

        public static final RegistryObject<BlockEntityType<DarkSoulCandleBlockEntity>> DARK_SOUL_CANDLE = BLOCK_ENTITIES
                        .register("dark_soul_candle",
                                        () -> BlockEntityType.Builder
                                                        .of(DarkSoulCandleBlockEntity::new,
                                                                        ModBlocks.DARK_SOUL_CANDLE.get())
                                                        .build(null));

        public static final RegistryObject<BlockEntityType<NamelessChestBlockEntity>> NAMELESS_CHEST = BLOCK_ENTITIES
                        .register("nameless_chest",
                                        () -> BlockEntityType.Builder
                                                        .of(NamelessChestBlockEntity::new,
                                                                        ModBlocks.NAMELESS_CHEST.get())
                                                        .build(null));

        public static final RegistryObject<BlockEntityType<DarkMenderBlockEntity>> DARK_MENDER = BLOCK_ENTITIES
                        .register("dark_mender",
                                        () -> BlockEntityType.Builder
                                                        .of(DarkMenderBlockEntity::new, ModBlocks.DARK_MENDER.get())
                                                        .build(null));

        public static final RegistryObject<BlockEntityType<MushroomMonstrosityHeadBlockEntity>> MOOSHROOM_MONSTROSITY_HEAD = BLOCK_ENTITIES
                        .register("mooshroom_monstrosity_head",
                                        () -> BlockEntityType.Builder
                                                        .of(MushroomMonstrosityHeadBlockEntity::new,
                                                                        ModBlocks.MOOSHROOM_MONSTROSITY_HEAD.get(),
                                                                        ModBlocks.WALL_MOOSHROOM_MONSTROSITY_HEAD.get())
                                                        .build(null));
}
