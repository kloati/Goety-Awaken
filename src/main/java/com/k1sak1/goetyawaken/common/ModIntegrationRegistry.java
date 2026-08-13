package com.k1sak1.goetyawaken.common;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.compat.ModLoadedUtil;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.BellringerServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.DameFortunaServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.FortunaDameBomb;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.FortunaDameCardEntity;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.MaidFairyServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.MasqueraderServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.MasqueraderServantClone;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.ModProjectileLineEntity;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.ModProjectileTargetedEntity;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.RosalyneServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.RoseSpiritServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.SwampMine;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.SwampjawServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.ArcherServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.SkirmisherServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.LegionerServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.SculkCentipedeServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.SculkLeechServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.ShatteredServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.ShriekWormServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.SludgeServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.StalkerServant;
import com.k1sak1.goetyawaken.common.items.integration.SculkSpore;
import com.k1sak1.goetyawaken.common.items.magic.FairyFocus;
import com.k1sak1.goetyawaken.common.items.magic.GhostMissileFocus;
import com.k1sak1.goetyawaken.common.items.magic.SwampMineFocus;
import com.k1sak1.goetyawaken.common.items.magic.ChipFocus;
import com.k1sak1.goetyawaken.common.items.magic.ChipRainFocus;
import com.k1sak1.goetyawaken.common.items.magic.FateDiceFocus;
import com.k1sak1.goetyawaken.common.items.magic.DeepdarkVerminFocus;
import com.k1sak1.goetyawaken.common.items.magic.ShatteredFocus;
import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModIntegrationRegistry {
        public static final DeferredRegister<EntityType<?>> INTEGRATION_ENTITY_TYPES = DeferredRegister
                        .create(ForgeRegistries.ENTITY_TYPES, GoetyAwaken.MODID);

        public static final DeferredRegister<Item> INTEGRATION_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
                        GoetyAwaken.MODID);

        // MeetYourFight 实体
        public static final RegistryObject<EntityType<SwampjawServant>> SWAMPJAW_SERVANT;
        public static final RegistryObject<EntityType<SwampMine>> SWAMP_MINE;
        public static final RegistryObject<EntityType<?>> MOD_PROJECTILE_LINE;
        public static final RegistryObject<EntityType<?>> MOD_PROJECTILE_TARGETED;
        public static final RegistryObject<EntityType<BellringerServant>> BELLRINGER_SERVANT;
        public static final RegistryObject<EntityType<RosalyneServant>> ROSALYNE_SERVANT;
        public static final RegistryObject<EntityType<RoseSpiritServant>> ROSE_SPIRIT_SERVANT;
        public static final RegistryObject<EntityType<DameFortunaServant>> DAME_FORTUNA_SERVANT;
        public static final RegistryObject<EntityType<FortunaDameBomb>> FORTUNA_DAME_BOMB;
        public static final RegistryObject<EntityType<FortunaDameCardEntity>> FORTUNA_DAME_CARD;

        // Masquerader 实体
        public static final RegistryObject<EntityType<MasqueraderServant>> MASQUERADER_SERVANT;
        public static final RegistryObject<EntityType<MasqueraderServantClone>> MASQUERADER_SERVANT_CLONE;

        // TouhouLittleMaid 实体
        public static final RegistryObject<EntityType<MaidFairyServant>> MAID_FAIRY_SERVANT;

        // TakesAPillage 实体
        public static final RegistryObject<EntityType<ArcherServant>> ARCHER_SERVANT;
        public static final RegistryObject<EntityType<SkirmisherServant>> SKIRMISHER_SERVANT;
        public static final RegistryObject<EntityType<LegionerServant>> LEGIONER_SERVANT;

        // MeetYourFight 物品
        public static final RegistryObject<Item> SWAMP_MINE_FOCUS;
        public static final RegistryObject<Item> GHOST_MISSILE_FOCUS;
        public static final RegistryObject<Item> CHIP_FOCUS;
        public static final RegistryObject<Item> CHIP_RAIN_FOCUS;
        public static final RegistryObject<Item> FATE_DICE_FOCUS;
        public static final RegistryObject<Item> SWAMPJAW_SERVANT_SPAWN_EGG;
        public static final RegistryObject<Item> BELLRINGER_SERVANT_SPAWN_EGG;
        public static final RegistryObject<Item> ROSALYNE_SERVANT_SPAWN_EGG;
        public static final RegistryObject<Item> DAME_FORTUNA_SERVANT_SPAWN_EGG;

        // Masquerader 物品
        public static final RegistryObject<Item> MASQUERADER_SERVANT_SPAWN_EGG;

        // TouhouLittleMaid 物品
        public static final RegistryObject<Item> FAIRY_FOCUS;
        public static final RegistryObject<Item> MAID_FAIRY_SERVANT_SPAWN_EGG;

        // DeeperDarker 实体
        public static final RegistryObject<EntityType<SculkCentipedeServant>> SCULK_CENTIPEDE_SERVANT;
        public static final RegistryObject<EntityType<SculkLeechServant>> SCULK_LEECH_SERVANT;
        public static final RegistryObject<EntityType<ShatteredServant>> SHATTERED_SERVANT;
        public static final RegistryObject<EntityType<ShriekWormServant>> SHRIEK_WORM_SERVANT;
        public static final RegistryObject<EntityType<SludgeServant>> SLUDGE_SERVANT;
        public static final RegistryObject<EntityType<StalkerServant>> STALKER_SERVANT;

        // TakesAPillage 物品
        public static final RegistryObject<Item> ARCHER_SERVANT_SPAWN_EGG;
        public static final RegistryObject<Item> SKIRMISHER_SERVANT_SPAWN_EGG;
        public static final RegistryObject<Item> LEGIONER_SERVANT_SPAWN_EGG;

        // DeeperDarker 物品
        public static final RegistryObject<Item> DEEPDARK_VERMIN_FOCUS;
        public static final RegistryObject<Item> SHATTERED_FOCUS;
        public static final RegistryObject<Item> SCULK_SPORE;
        public static final RegistryObject<Item> SCULK_CENTIPEDE_SERVANT_SPAWN_EGG;
        public static final RegistryObject<Item> SCULK_LEECH_SERVANT_SPAWN_EGG;
        public static final RegistryObject<Item> SHATTERED_SERVANT_SPAWN_EGG;
        public static final RegistryObject<Item> SHRIEK_WORM_SERVANT_SPAWN_EGG;
        public static final RegistryObject<Item> SLUDGE_SERVANT_SPAWN_EGG;
        public static final RegistryObject<Item> STALKER_SERVANT_SPAWN_EGG;

        static {
                if (ModLoadedUtil.isModLoaded(ModLoadedUtil.MEET_YOUR_FIGHT)) {
                        // 实体
                        SWAMPJAW_SERVANT = INTEGRATION_ENTITY_TYPES.register("swampjaw_servant",
                                        () -> EntityType.Builder
                                                        .<SwampjawServant>of(SwampjawServant::new, MobCategory.MONSTER)
                                                        .sized(2.6F, 1.6F).clientTrackingRange(8)
                                                        .build("swampjaw_servant"));
                        SWAMP_MINE = INTEGRATION_ENTITY_TYPES.register("swamp_mine",
                                        () -> EntityType.Builder.<SwampMine>of(SwampMine::new, MobCategory.MISC)
                                                        .sized(1.0F, 1.0F).clientTrackingRange(8).build("swamp_mine"));
                        MOD_PROJECTILE_LINE = INTEGRATION_ENTITY_TYPES.register("mod_projectile_line",
                                        () -> EntityType.Builder
                                                        .<ModProjectileLineEntity>of(ModProjectileLineEntity::new,
                                                                        MobCategory.MISC)
                                                        .sized(0.5F, 0.5F).clientTrackingRange(8)
                                                        .build("mod_projectile_line"));
                        MOD_PROJECTILE_TARGETED = INTEGRATION_ENTITY_TYPES.register("mod_projectile_targeted",
                                        () -> EntityType.Builder
                                                        .<ModProjectileTargetedEntity>of(
                                                                        ModProjectileTargetedEntity::new,
                                                                        MobCategory.MISC)
                                                        .sized(0.5F, 0.5F).clientTrackingRange(8)
                                                        .build("mod_projectile_targeted"));
                        BELLRINGER_SERVANT = INTEGRATION_ENTITY_TYPES.register("bellringer_servant",
                                        () -> EntityType.Builder
                                                        .<BellringerServant>of(BellringerServant::new,
                                                                        MobCategory.MONSTER)
                                                        .sized(0.6F, 1.95F).clientTrackingRange(8)
                                                        .build("bellringer_servant"));
                        ROSALYNE_SERVANT = INTEGRATION_ENTITY_TYPES.register("rosalyne_servant",
                                        () -> EntityType.Builder
                                                        .<RosalyneServant>of(RosalyneServant::new, MobCategory.MONSTER)
                                                        .sized(0.6F, 1.95F).clientTrackingRange(8)
                                                        .build("rosalyne_servant"));
                        ROSE_SPIRIT_SERVANT = INTEGRATION_ENTITY_TYPES.register("rose_spirit_servant",
                                        () -> EntityType.Builder
                                                        .<RoseSpiritServant>of(RoseSpiritServant::new,
                                                                        MobCategory.MONSTER)
                                                        .sized(0.75F, 1.3125F).clientTrackingRange(8)
                                                        .build("rose_spirit_servant"));
                        DAME_FORTUNA_SERVANT = INTEGRATION_ENTITY_TYPES.register("dame_fortuna_servant",
                                        () -> EntityType.Builder
                                                        .<DameFortunaServant>of(DameFortunaServant::new,
                                                                        MobCategory.MONSTER)
                                                        .sized(0.6F, 2.325F).clientTrackingRange(8)
                                                        .build("dame_fortuna_servant"));
                        FORTUNA_DAME_BOMB = INTEGRATION_ENTITY_TYPES.register("fortuna_dame_bomb",
                                        () -> EntityType.Builder
                                                        .<FortunaDameBomb>of(FortunaDameBomb::new, MobCategory.MISC)
                                                        .sized(0.3125F, 0.3125F).clientTrackingRange(8)
                                                        .build("fortuna_dame_bomb"));
                        FORTUNA_DAME_CARD = INTEGRATION_ENTITY_TYPES.register("fortuna_dame_card",
                                        () -> EntityType.Builder
                                                        .<FortunaDameCardEntity>of(FortunaDameCardEntity::new,
                                                                        MobCategory.MISC)
                                                        .sized(1.75F, 2.5F).clientTrackingRange(8)
                                                        .build("fortuna_dame_card"));

                        // 物品
                        SWAMP_MINE_FOCUS = INTEGRATION_ITEMS.register("swamp_mine_focus",
                                        () -> new SwampMineFocus());
                        GHOST_MISSILE_FOCUS = INTEGRATION_ITEMS.register("ghost_missile_focus",
                                        () -> new GhostMissileFocus());
                        CHIP_FOCUS = INTEGRATION_ITEMS.register("chip_focus",
                                        () -> new ChipFocus());
                        CHIP_RAIN_FOCUS = INTEGRATION_ITEMS.register("chip_rain_focus",
                                        () -> new ChipRainFocus());
                        FATE_DICE_FOCUS = INTEGRATION_ITEMS.register("fate_dice_focus",
                                        () -> new FateDiceFocus());
                        SWAMPJAW_SERVANT_SPAWN_EGG = INTEGRATION_ITEMS.register("swampjaw_servant_spawn_egg",
                                        () -> new ServantSpawnEggItem(SWAMPJAW_SERVANT, 0x2A4B3C, 0x8B4513,
                                                        new Item.Properties()));
                        BELLRINGER_SERVANT_SPAWN_EGG = INTEGRATION_ITEMS.register("bellringer_servant_spawn_egg",
                                        () -> new ServantSpawnEggItem(BELLRINGER_SERVANT, 0x4A3B6B, 0xC8A2C8,
                                                        new Item.Properties()));
                        ROSALYNE_SERVANT_SPAWN_EGG = INTEGRATION_ITEMS.register("rosalyne_servant_spawn_egg",
                                        () -> new ServantSpawnEggItem(ROSALYNE_SERVANT, 0x8B0000, 0xFFD700,
                                                        new Item.Properties()));
                        DAME_FORTUNA_SERVANT_SPAWN_EGG = INTEGRATION_ITEMS.register("dame_fortuna_servant_spawn_egg",
                                        () -> new ServantSpawnEggItem(DAME_FORTUNA_SERVANT, 0x8B008B, 0xFFD700,
                                                        new Item.Properties()));
                } else {
                        SWAMPJAW_SERVANT = null;
                        SWAMP_MINE = null;
                        MOD_PROJECTILE_LINE = null;
                        MOD_PROJECTILE_TARGETED = null;
                        BELLRINGER_SERVANT = null;
                        ROSALYNE_SERVANT = null;
                        ROSE_SPIRIT_SERVANT = null;
                        DAME_FORTUNA_SERVANT = null;
                        FORTUNA_DAME_BOMB = null;
                        FORTUNA_DAME_CARD = null;
                        SWAMP_MINE_FOCUS = null;
                        GHOST_MISSILE_FOCUS = null;
                        CHIP_FOCUS = null;
                        CHIP_RAIN_FOCUS = null;
                        FATE_DICE_FOCUS = null;
                        SWAMPJAW_SERVANT_SPAWN_EGG = null;
                        BELLRINGER_SERVANT_SPAWN_EGG = null;
                        ROSALYNE_SERVANT_SPAWN_EGG = null;
                        DAME_FORTUNA_SERVANT_SPAWN_EGG = null;
                }

                if (ModLoadedUtil.isModLoaded(ModLoadedUtil.MASQUERADER)) {
                        MASQUERADER_SERVANT = INTEGRATION_ENTITY_TYPES.register("masquerader_servant",
                                        () -> EntityType.Builder.of(MasqueraderServant::new, MobCategory.MONSTER)
                                                        .sized(0.6F, 1.95F).clientTrackingRange(8)
                                                        .build("masquerader_servant"));
                        MASQUERADER_SERVANT_CLONE = INTEGRATION_ENTITY_TYPES.register("masquerader_servant_clone",
                                        () -> EntityType.Builder.of(MasqueraderServantClone::new, MobCategory.MONSTER)
                                                        .sized(0.6F, 1.95F).clientTrackingRange(8)
                                                        .build("masquerader_servant_clone"));

                        MASQUERADER_SERVANT_SPAWN_EGG = INTEGRATION_ITEMS.register("masquerader_servant_spawn_egg",
                                        () -> new ServantSpawnEggItem(MASQUERADER_SERVANT, 0x4B0082, 0xFFD700,
                                                        new Item.Properties()));
                } else {
                        MASQUERADER_SERVANT = null;
                        MASQUERADER_SERVANT_CLONE = null;
                        MASQUERADER_SERVANT_SPAWN_EGG = null;
                }

                if (ModLoadedUtil.isModLoaded(ModLoadedUtil.TOUHOU_LITTLE_MAID)) {
                        MAID_FAIRY_SERVANT = INTEGRATION_ENTITY_TYPES.register("maid_fairy_servant",
                                        () -> EntityType.Builder.of(MaidFairyServant::new, MobCategory.MONSTER)
                                                        .sized(0.6F, 1.5F).clientTrackingRange(10)
                                                        .build("maid_fairy_servant"));

                        FAIRY_FOCUS = INTEGRATION_ITEMS.register("fairy_focus",
                                        () -> new FairyFocus());
                        MAID_FAIRY_SERVANT_SPAWN_EGG = INTEGRATION_ITEMS.register("maid_fairy_servant_spawn_egg",
                                        () -> new ServantSpawnEggItem(MAID_FAIRY_SERVANT, 0xFFB6C1, 0x9370DB,
                                                        new Item.Properties()));
                } else {
                        MAID_FAIRY_SERVANT = null;
                        FAIRY_FOCUS = null;
                        MAID_FAIRY_SERVANT_SPAWN_EGG = null;
                }

                if (ModLoadedUtil.isModLoaded(ModLoadedUtil.TAKES_A_PILLAGE)) {
                        ARCHER_SERVANT = INTEGRATION_ENTITY_TYPES.register("archer_servant",
                                        () -> EntityType.Builder.of(ArcherServant::new, MobCategory.MONSTER)
                                                        .sized(0.6F, 1.95F).clientTrackingRange(8)
                                                        .build("archer_servant"));
                        SKIRMISHER_SERVANT = INTEGRATION_ENTITY_TYPES.register("skirmisher_servant",
                                        () -> EntityType.Builder.of(SkirmisherServant::new, MobCategory.MONSTER)
                                                        .sized(0.6F, 1.95F).clientTrackingRange(8)
                                                        .build("skirmisher_servant"));
                        LEGIONER_SERVANT = INTEGRATION_ENTITY_TYPES.register("legioner_servant",
                                        () -> EntityType.Builder.of(LegionerServant::new, MobCategory.MONSTER)
                                                        .sized(0.6F, 1.95F).clientTrackingRange(8)
                                                        .build("legioner_servant"));

                        ARCHER_SERVANT_SPAWN_EGG = INTEGRATION_ITEMS.register("archer_servant_spawn_egg",
                                        () -> new ServantSpawnEggItem(ARCHER_SERVANT, 0x6B4226, 0xA0522D,
                                                        new Item.Properties()));
                        SKIRMISHER_SERVANT_SPAWN_EGG = INTEGRATION_ITEMS.register("skirmisher_servant_spawn_egg",
                                        () -> new ServantSpawnEggItem(SKIRMISHER_SERVANT, 0x4A4A4A, 0x8B4513,
                                                        new Item.Properties()));
                        LEGIONER_SERVANT_SPAWN_EGG = INTEGRATION_ITEMS.register("legioner_servant_spawn_egg",
                                        () -> new ServantSpawnEggItem(LEGIONER_SERVANT, 0x8B0000, 0xC0C0C0,
                                                        new Item.Properties()));
                } else {
                        ARCHER_SERVANT = null;
                        SKIRMISHER_SERVANT = null;
                        LEGIONER_SERVANT = null;
                        ARCHER_SERVANT_SPAWN_EGG = null;
                        SKIRMISHER_SERVANT_SPAWN_EGG = null;
                        LEGIONER_SERVANT_SPAWN_EGG = null;
                }

                if (ModLoadedUtil.isModLoaded(ModLoadedUtil.DEEPER_DARKER)) {
                        DEEPDARK_VERMIN_FOCUS = INTEGRATION_ITEMS.register("deepdark_vermin_focus",
                                        () -> new DeepdarkVerminFocus());
                        SHATTERED_FOCUS = INTEGRATION_ITEMS.register("shattered_focus",
                                        () -> new ShatteredFocus());
                        SCULK_SPORE = INTEGRATION_ITEMS.register("sculk_spore",
                                        () -> new SculkSpore());

                        SCULK_CENTIPEDE_SERVANT = INTEGRATION_ENTITY_TYPES.register("sculk_centipede_servant",
                                        () -> EntityType.Builder.of(SculkCentipedeServant::new, MobCategory.MONSTER)
                                                        .sized(1f, 0.2f).clientTrackingRange(8)
                                                        .build("sculk_centipede_servant"));
                        SCULK_LEECH_SERVANT = INTEGRATION_ENTITY_TYPES.register("sculk_leech_servant",
                                        () -> EntityType.Builder.of(SculkLeechServant::new, MobCategory.MONSTER)
                                                        .sized(0.42f, 0.2f).clientTrackingRange(8)
                                                        .build("sculk_leech_servant"));
                        SHATTERED_SERVANT = INTEGRATION_ENTITY_TYPES.register("shattered_servant",
                                        () -> EntityType.Builder.of(ShatteredServant::new, MobCategory.MONSTER)
                                                        .sized(0.8f, 2.125f).clientTrackingRange(8)
                                                        .build("shattered_servant"));
                        SHRIEK_WORM_SERVANT = INTEGRATION_ENTITY_TYPES.register("shriek_worm_servant",
                                        () -> EntityType.Builder.of(ShriekWormServant::new, MobCategory.MONSTER)
                                                        .sized(1f, 5.7f).clientTrackingRange(8)
                                                        .build("shriek_worm_servant"));
                        SLUDGE_SERVANT = INTEGRATION_ENTITY_TYPES.register("sludge_servant",
                                        () -> EntityType.Builder.of(SludgeServant::new, MobCategory.MONSTER)
                                                        .sized(2.04f, 2.04f).clientTrackingRange(8)
                                                        .build("sludge_servant"));
                        STALKER_SERVANT = INTEGRATION_ENTITY_TYPES.register("stalker_servant",
                                        () -> EntityType.Builder.of(StalkerServant::new, MobCategory.MONSTER)
                                                        .sized(1f, 4.4f).clientTrackingRange(8)
                                                        .build("stalker_servant"));

                        SCULK_CENTIPEDE_SERVANT_SPAWN_EGG = INTEGRATION_ITEMS
                                        .register("sculk_centipede_servant_spawn_egg",
                                                        () -> new ServantSpawnEggItem(SCULK_CENTIPEDE_SERVANT, 0x0E2B3F,
                                                                        0x1CE0D6,
                                                                        new Item.Properties()));
                        SCULK_LEECH_SERVANT_SPAWN_EGG = INTEGRATION_ITEMS.register("sculk_leech_servant_spawn_egg",
                                        () -> new ServantSpawnEggItem(SCULK_LEECH_SERVANT, 0x0E2B3F, 0x1CE0D6,
                                                        new Item.Properties()));
                        SHATTERED_SERVANT_SPAWN_EGG = INTEGRATION_ITEMS.register("shattered_servant_spawn_egg",
                                        () -> new ServantSpawnEggItem(SHATTERED_SERVANT, 0x0E2B3F, 0x1CE0D6,
                                                        new Item.Properties()));
                        SHRIEK_WORM_SERVANT_SPAWN_EGG = INTEGRATION_ITEMS.register("shriek_worm_servant_spawn_egg",
                                        () -> new ServantSpawnEggItem(SHRIEK_WORM_SERVANT, 0x0E2B3F, 0x1CE0D6,
                                                        new Item.Properties()));
                        SLUDGE_SERVANT_SPAWN_EGG = INTEGRATION_ITEMS.register("sludge_servant_spawn_egg",
                                        () -> new ServantSpawnEggItem(SLUDGE_SERVANT, 0x1C4A2E, 0x0E2B3F,
                                                        new Item.Properties()));
                        STALKER_SERVANT_SPAWN_EGG = INTEGRATION_ITEMS.register("stalker_servant_spawn_egg",
                                        () -> new ServantSpawnEggItem(STALKER_SERVANT, 0x0E2B3F, 0x1CE0D6,
                                                        new Item.Properties()));
                } else {
                        SCULK_CENTIPEDE_SERVANT = null;
                        SCULK_LEECH_SERVANT = null;
                        SHATTERED_SERVANT = null;
                        SHRIEK_WORM_SERVANT = null;
                        SLUDGE_SERVANT = null;
                        STALKER_SERVANT = null;
                        SCULK_CENTIPEDE_SERVANT_SPAWN_EGG = null;
                        SCULK_LEECH_SERVANT_SPAWN_EGG = null;
                        SHATTERED_SERVANT_SPAWN_EGG = null;
                        SHRIEK_WORM_SERVANT_SPAWN_EGG = null;
                        SLUDGE_SERVANT_SPAWN_EGG = null;
                        DEEPDARK_VERMIN_FOCUS = null;
                        SHATTERED_FOCUS = null;
                        SCULK_SPORE = null;
                        STALKER_SERVANT_SPAWN_EGG = null;
                }
        }
}
