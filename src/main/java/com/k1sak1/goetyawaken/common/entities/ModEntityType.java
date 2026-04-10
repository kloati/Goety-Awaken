package com.k1sak1.goetyawaken.common.entities;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.entity.BoulderClusterFactory;
import com.k1sak1.goetyawaken.common.entities.ally.PaleGolemServant;
import com.k1sak1.goetyawaken.common.entities.ally.SilverfishServant;
import com.k1sak1.goetyawaken.common.entities.ally.CreeperServant;
import com.k1sak1.goetyawaken.common.entities.ally.EndermanServant;
import com.k1sak1.goetyawaken.common.entities.ally.ShulkerServant;
import com.k1sak1.goetyawaken.common.entities.ally.EndermiteServant;
import com.k1sak1.goetyawaken.common.entities.ally.WitherServant;
import com.k1sak1.goetyawaken.common.entities.ally.WardenServant;
import com.k1sak1.goetyawaken.common.entities.ally.WightServant;
import com.k1sak1.goetyawaken.common.entities.ally.illager.VizierCloneServant;
import com.k1sak1.goetyawaken.common.entities.ally.illager.VizierServant;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.skeleton.SkeletonVanguard;
import com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton.ParchedServant;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.skeleton.Parched;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.skeleton.SunkenSkeleton;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.zombie.ZombieDarkguard;
import com.k1sak1.goetyawaken.common.entities.ally.illager.RoyalguardServant;
import com.k1sak1.goetyawaken.common.entities.ally.IceCreeperServant;
import com.k1sak1.goetyawaken.common.entities.hostile.IceCreeper;
import com.k1sak1.goetyawaken.common.entities.ally.golem.MushroomMonstrosity;
import com.k1sak1.goetyawaken.common.entities.ally.CaerbannogRabbitServant;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.MaidFairyServant;
import com.k1sak1.goetyawaken.common.entities.projectiles.ModShulkerBullet;
import com.k1sak1.goetyawaken.common.entities.projectiles.ModWitherSkullNoBlockBreak;
import com.k1sak1.goetyawaken.common.entities.projectiles.EchoingStrikeEntity;
import com.k1sak1.goetyawaken.common.entities.projectiles.FrostScytheSlash;
import com.k1sak1.goetyawaken.common.entities.projectiles.SilverfishEggEntity;
import com.k1sak1.goetyawaken.common.entities.projectiles.EndermiteEggEntity;
import com.k1sak1.goetyawaken.common.entities.projectiles.GhostFireBolt;
import com.k1sak1.goetyawaken.common.entities.projectiles.MushroomMissile;
import com.k1sak1.goetyawaken.common.entities.projectiles.MushroomScatterBomb;
import com.k1sak1.goetyawaken.common.entities.projectiles.BlockClusterEntity;
import com.k1sak1.goetyawaken.common.entities.projectiles.ExplosiveArrow;
import com.k1sak1.goetyawaken.common.entities.projectiles.DeathFire;
import com.k1sak1.goetyawaken.common.entities.projectiles.CorruptedSoulBolt;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.WraithNecromancer;
import com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer.WraithNecromancerServant;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.NamelessOne;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.ParchedNecromancer;
import com.k1sak1.goetyawaken.common.entities.hostile.HostileDrownedNecromancer;
import com.k1sak1.goetyawaken.common.entities.ally.undead.SkullLordServant;
import com.k1sak1.goetyawaken.common.entities.ally.undead.BoneLordServant;
import com.k1sak1.goetyawaken.common.entities.ally.undead.BoundSorcerer;
import com.k1sak1.goetyawaken.common.entities.ally.undead.tower_wraith.AbstractTowerWraith;
import com.k1sak1.goetyawaken.common.entities.hostile.HostileTowerWraith;
import com.k1sak1.goetyawaken.common.entities.hostile.illager.TowerWitch;
import com.k1sak1.goetyawaken.common.entities.ally.illager.TowerWitchServant;
import com.k1sak1.goetyawaken.common.entities.hostile.HostileTwilightGoat;
import com.k1sak1.goetyawaken.common.entities.ally.ObsidianMonolithServant;
import com.k1sak1.goetyawaken.common.entities.deco.OminousPainting;
import com.k1sak1.goetyawaken.common.entities.hostile.MushroomMonstrosityHostile;
import com.k1sak1.goetyawaken.common.entities.hostile.HostileWildfire;
import com.k1sak1.goetyawaken.common.entities.hostile.HostileGnasher;
import com.k1sak1.goetyawaken.common.entities.hostile.HostileTropicalSlime;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.zombie.BoulderingZombie;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.zombie.JungleZombie;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.zombie.FrozenZombie;
import com.k1sak1.goetyawaken.common.entities.hostile.MiniGhastHostile;
import com.k1sak1.goetyawaken.common.entities.hostile.HostileSnapper;
import com.k1sak1.goetyawaken.common.entities.ally.SpiderCreeder;
import com.k1sak1.goetyawaken.common.entities.ally.CorruptedSlime;
import com.k1sak1.goetyawaken.common.entities.projectiles.PureLightEntity;
import com.k1sak1.goetyawaken.common.entities.projectiles.ModSwordProjectile;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityType {
        public static final DeferredRegister<EntityType<?>> ENTITY_TYPE = DeferredRegister
                        .create(ForgeRegistries.ENTITY_TYPES, GoetyAwaken.MODID);

        public static final RegistryObject<EntityType<ZombieDarkguard>> ZOMBIE_DARKGUARD = ENTITY_TYPE.register(
                        "zombie_darkguard",
                        () -> EntityType.Builder.of(ZombieDarkguard::new, MobCategory.MONSTER)
                                        .sized(0.6F, 1.95F)
                                        .clientTrackingRange(8)
                                        .build("zombie_darkguard"));

        public static final RegistryObject<EntityType<SkeletonVanguard>> SKELETON_VANGUARD = ENTITY_TYPE.register(
                        "skeleton_vanguard",
                        () -> EntityType.Builder.of(SkeletonVanguard::new, MobCategory.MONSTER)
                                        .sized(0.6F, 1.99F)
                                        .clientTrackingRange(8)
                                        .build("skeleton_vanguard"));

        public static final RegistryObject<EntityType<Parched>> PARCHED = ENTITY_TYPE.register(
                        "parched",
                        () -> EntityType.Builder.of(Parched::new, MobCategory.MONSTER)
                                        .sized(0.6F, 1.99F)
                                        .clientTrackingRange(8)
                                        .build("parched"));

        public static final RegistryObject<EntityType<SunkenSkeleton>> SUNKEN_SKELETON = ENTITY_TYPE.register(
                        "sunken_skeleton",
                        () -> EntityType.Builder.of(SunkenSkeleton::new, MobCategory.MONSTER)
                                        .sized(0.6F, 1.99F)
                                        .clientTrackingRange(8)
                                        .build("sunken_skeleton"));

        public static final RegistryObject<EntityType<ParchedServant>> PARCHED_SERVANT = ENTITY_TYPE.register(
                        "parched_servant",
                        () -> EntityType.Builder.of(ParchedServant::new, MobCategory.MISC)
                                        .sized(0.6F, 1.99F)
                                        .clientTrackingRange(8)
                                        .build("parched_servant"));

        public static final RegistryObject<EntityType<PaleGolemServant>> PALE_GOLEM_SERVANT = ENTITY_TYPE.register(
                        "pale_golem_servant",
                        () -> EntityType.Builder.of(PaleGolemServant::new, MobCategory.MISC)
                                        .sized(1.4F, 2.7F)
                                        .clientTrackingRange(10)
                                        .build("pale_golem_servant"));

        public static final RegistryObject<EntityType<SilverfishServant>> SILVERFISH_SERVANT = ENTITY_TYPE.register(
                        "silverfish_servant",
                        () -> EntityType.Builder.of(SilverfishServant::new, MobCategory.MISC)
                                        .sized(0.4F, 0.3F)
                                        .clientTrackingRange(8)
                                        .build("silverfish_servant"));

        public static final RegistryObject<EntityType<CreeperServant>> CREEPER_SERVANT = ENTITY_TYPE.register(
                        "creeper_servant",
                        () -> EntityType.Builder.of(CreeperServant::new, MobCategory.MISC)
                                        .sized(0.6F, 1.7F)
                                        .clientTrackingRange(8)
                                        .build("creeper_servant"));

        public static final RegistryObject<EntityType<EndermanServant>> ENDERMAN_SERVANT = ENTITY_TYPE.register(
                        "enderman_servant",
                        () -> EntityType.Builder.of(EndermanServant::new, MobCategory.MISC)
                                        .sized(0.6F, 2.9F)
                                        .clientTrackingRange(8)
                                        .build("enderman_servant"));

        public static final RegistryObject<EntityType<ShulkerServant>> SHULKER_SERVANT = ENTITY_TYPE.register(
                        "shulker_servant",
                        () -> EntityType.Builder.of(ShulkerServant::new, MobCategory.MISC)
                                        .sized(1.0F, 1.0F)
                                        .clientTrackingRange(8)
                                        .build("shulker_servant"));

        public static final RegistryObject<EntityType<EndermiteServant>> ENDERMITE_SERVANT = ENTITY_TYPE.register(
                        "endermite_servant",
                        () -> EntityType.Builder.of(EndermiteServant::new, MobCategory.MISC)
                                        .sized(0.4F, 0.3F)
                                        .clientTrackingRange(8)
                                        .build("endermite_servant"));

        public static final RegistryObject<EntityType<WitherServant>> WITHER_SERVANT = ENTITY_TYPE.register(
                        "wither_servant",
                        () -> EntityType.Builder.of(WitherServant::new, MobCategory.MISC)
                                        .sized(1.5F, 3.5F)
                                        .clientTrackingRange(10)
                                        .fireImmune()
                                        .build("wither_servant"));

        public static final RegistryObject<EntityType<WardenServant>> WARDEN_SERVANT = ENTITY_TYPE.register(
                        "warden_servant",
                        () -> EntityType.Builder.of(WardenServant::new, MobCategory.MISC)
                                        .sized(0.9F, 2.9F)
                                        .clientTrackingRange(10)
                                        .fireImmune()
                                        .build("warden_servant"));

        public static final RegistryObject<EntityType<WightServant>> WIGHT_SERVANT = ENTITY_TYPE.register(
                        "wight_servant",
                        () -> EntityType.Builder.of(WightServant::new, MobCategory.MISC)
                                        .sized(0.6F, 2.9F)
                                        .clientTrackingRange(16)
                                        .build("wight_servant"));

        public static final RegistryObject<EntityType<ModShulkerBullet>> MOD_SHULKER_BULLET = ENTITY_TYPE.register(
                        "mod_shulker_bullet",
                        () -> EntityType.Builder.<ModShulkerBullet>of(ModShulkerBullet::new, MobCategory.MISC)
                                        .sized(0.3125F, 0.3125F)
                                        .clientTrackingRange(8)
                                        .updateInterval(2)
                                        .build("mod_shulker_bullet"));

        public static final RegistryObject<EntityType<ModWitherSkullNoBlockBreak>> MOD_WITHER_SKULL_NO_BLOCK_BREAK = ENTITY_TYPE
                        .register(
                                        "mod_wither_skull_no_block_break",
                                        () -> EntityType.Builder
                                                        .<ModWitherSkullNoBlockBreak>of(ModWitherSkullNoBlockBreak::new,
                                                                        MobCategory.MISC)
                                                        .sized(0.3125F, 0.3125F)
                                                        .clientTrackingRange(4)
                                                        .updateInterval(10)
                                                        .build("mod_wither_skull_no_block_break"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.ally.illager.TormentorServant>> TORMENTOR_SERVANT = ENTITY_TYPE
                        .register(
                                        "tormentor_servant",
                                        () -> EntityType.Builder.of(
                                                        com.k1sak1.goetyawaken.common.entities.ally.illager.TormentorServant::new,
                                                        MobCategory.MISC)
                                                        .sized(0.6F, 1.95F)
                                                        .fireImmune()
                                                        .clientTrackingRange(8)
                                                        .build("tormentor_servant"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.ally.ender.EndersentServant>> ENDERSENT_SERVANT = ENTITY_TYPE
                        .register(
                                        "endersent_servant",
                                        () -> EntityType.Builder.of(
                                                        com.k1sak1.goetyawaken.common.entities.ally.ender.EndersentServant::new,
                                                        MobCategory.MISC)
                                                        .sized(0.8F, 5.6F)
                                                        .clientTrackingRange(8)
                                                        .build("endersent_servant"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.ally.illager.EnviokerServant>> ENVIOKER_SERVANT = ENTITY_TYPE
                        .register(
                                        "envioker_servant",
                                        () -> EntityType.Builder.of(
                                                        com.k1sak1.goetyawaken.common.entities.ally.illager.EnviokerServant::new,
                                                        MobCategory.MISC)
                                                        .sized(0.6F, 1.95F)
                                                        .clientTrackingRange(8)
                                                        .build("envioker_servant"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.ally.illager.PreacherServant>> PREACHER_SERVANT = ENTITY_TYPE
                        .register(
                                        "preacher_servant",
                                        () -> EntityType.Builder.of(
                                                        com.k1sak1.goetyawaken.common.entities.ally.illager.PreacherServant::new,
                                                        MobCategory.MISC)
                                                        .sized(0.6F, 1.95F)
                                                        .clientTrackingRange(8)
                                                        .build("preacher_servant"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.ally.illager.CroneServant>> CRONE_SERVANT = ENTITY_TYPE
                        .register(
                                        "crone_servant",
                                        () -> EntityType.Builder.of(
                                                        com.k1sak1.goetyawaken.common.entities.ally.illager.CroneServant::new,
                                                        MobCategory.MISC)
                                                        .sized(0.6F, 1.95F)
                                                        .clientTrackingRange(8)
                                                        .build("crone_servant"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.ally.illager.MinisterServant>> MINISTER_SERVANT = ENTITY_TYPE
                        .register(
                                        "minister_servant",
                                        () -> EntityType.Builder.of(
                                                        com.k1sak1.goetyawaken.common.entities.ally.illager.MinisterServant::new,
                                                        MobCategory.MISC)
                                                        .sized(0.6F, 1.95F)
                                                        .clientTrackingRange(8)
                                                        .build("minister_servant"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.ally.illager.SorcererServant>> SORCERER_SERVANT = ENTITY_TYPE
                        .register(
                                        "sorcerer_servant",
                                        () -> EntityType.Builder.of(
                                                        com.k1sak1.goetyawaken.common.entities.ally.illager.SorcererServant::new,
                                                        MobCategory.MISC)
                                                        .sized(0.6F, 1.95F)
                                                        .clientTrackingRange(8)
                                                        .build("sorcerer_servant"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.ally.illager.IllusionerServant>> ILLUSIONER_SERVANT = ENTITY_TYPE
                        .register(
                                        "illusioner_servant",
                                        () -> EntityType.Builder.of(
                                                        com.k1sak1.goetyawaken.common.entities.ally.illager.IllusionerServant::new,
                                                        MobCategory.MISC)
                                                        .sized(0.6F, 1.95F)
                                                        .clientTrackingRange(8)
                                                        .build("illusioner_servant"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.ally.illager.ArchIllusionerServant>> ARCH_ILLUSIONER_SERVANT = ENTITY_TYPE
                        .register(
                                        "arch_illusioner_servant",
                                        () -> EntityType.Builder.of(
                                                        com.k1sak1.goetyawaken.common.entities.ally.illager.ArchIllusionerServant::new,
                                                        MobCategory.MISC)
                                                        .sized(0.6F, 1.95F)
                                                        .clientTrackingRange(8)
                                                        .build("arch_illusioner_servant"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.hostile.illager.ArchIllusioner>> ARCH_ILLUSIONER = ENTITY_TYPE
                        .register(
                                        "arch_illusioner",
                                        () -> EntityType.Builder.of(
                                                        com.k1sak1.goetyawaken.common.entities.hostile.illager.ArchIllusioner::new,
                                                        MobCategory.MISC)
                                                        .sized(0.6F, 1.95F)
                                                        .clientTrackingRange(8)
                                                        .build("arch_illusioner"));

        public static final RegistryObject<EntityType<VizierCloneServant>> VIZIER_CLONE_SERVANT = ENTITY_TYPE
                        .register(
                                        "vizier_clone_servant",
                                        () -> EntityType.Builder.of(
                                                        VizierCloneServant::new,
                                                        MobCategory.MISC)
                                                        .sized(0.6F, 1.95F)
                                                        .clientTrackingRange(8)
                                                        .build("vizier_clone_servant"));

        public static final RegistryObject<EntityType<VizierServant>> VIZIER_SERVANT = ENTITY_TYPE
                        .register(
                                        "vizier_servant",
                                        () -> EntityType.Builder.of(
                                                        VizierServant::new,
                                                        MobCategory.MISC)
                                                        .sized(0.6F, 1.95F)
                                                        .clientTrackingRange(8)
                                                        .build("vizier_servant"));

        public static final RegistryObject<EntityType<RoyalguardServant>> ROYALGUARD_SERVANT = ENTITY_TYPE
                        .register(
                                        "royalguard_servant",
                                        () -> EntityType.Builder.of(
                                                        RoyalguardServant::new,
                                                        MobCategory.MISC)
                                                        .sized(0.6F, 1.95F)
                                                        .clientTrackingRange(8)
                                                        .build("royalguard_servant"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.hostile.illager.HostileRoyalguard>> HOSTILE_ROYALGUARD = ENTITY_TYPE
                        .register(
                                        "hostile_royalguard",
                                        () -> EntityType.Builder.of(
                                                        com.k1sak1.goetyawaken.common.entities.hostile.illager.HostileRoyalguard::new,
                                                        MobCategory.MONSTER)
                                                        .sized(0.6F, 1.95F)
                                                        .clientTrackingRange(8)
                                                        .build("hostile_royalguard"));

        public static final RegistryObject<EntityType<EchoingStrikeEntity>> ECHOING_STRIKE = ENTITY_TYPE
                        .register(
                                        "echoing_strike",
                                        () -> EntityType.Builder
                                                        .<EchoingStrikeEntity>of(EchoingStrikeEntity::new,
                                                                        MobCategory.MISC)
                                                        .sized(0.5F, 0.5F)
                                                        .clientTrackingRange(10)
                                                        .updateInterval(1)
                                                        .build("echoing_strike"));

        public static final RegistryObject<EntityType<IceCreeperServant>> ICE_CREEPER_SERVANT = ENTITY_TYPE
                        .register(
                                        "ice_creeper_servant",
                                        () -> EntityType.Builder.of(
                                                        IceCreeperServant::new,
                                                        MobCategory.MISC)
                                                        .sized(0.6F, 1.7F)
                                                        .clientTrackingRange(8)
                                                        .build("ice_creeper_servant"));

        public static final RegistryObject<EntityType<IceCreeper>> ICE_CREEPER = ENTITY_TYPE
                        .register(
                                        "ice_creeper",
                                        () -> EntityType.Builder.of(
                                                        IceCreeper::new,
                                                        MobCategory.MONSTER)
                                                        .sized(0.6F, 1.7F)
                                                        .clientTrackingRange(8)
                                                        .build("ice_creeper"));

        public static final RegistryObject<EntityType<SilverfishEggEntity>> SILVERFISH_EGG = ENTITY_TYPE
                        .register(
                                        "silverfish_egg",
                                        () -> EntityType.Builder.<SilverfishEggEntity>of(
                                                        (entityType, level) -> new SilverfishEggEntity(entityType,
                                                                        level),
                                                        MobCategory.MISC)
                                                        .sized(0.25F, 0.25F)
                                                        .clientTrackingRange(4)
                                                        .updateInterval(10)
                                                        .build("silverfish_egg"));

        public static final RegistryObject<EntityType<EndermiteEggEntity>> ENDERMITE_EGG = ENTITY_TYPE
                        .register(
                                        "endermite_egg",
                                        () -> EntityType.Builder.<EndermiteEggEntity>of(
                                                        (entityType, level) -> new EndermiteEggEntity(entityType,
                                                                        level),
                                                        MobCategory.MISC)
                                                        .sized(0.25F, 0.25F)
                                                        .clientTrackingRange(4)
                                                        .updateInterval(10)
                                                        .build("endermite_egg"));

        public static final RegistryObject<EntityType<GhostFireBolt>> GHOST_FIRE_BOLT = ENTITY_TYPE.register(
                        "ghost_fire_bolt",
                        () -> EntityType.Builder.<GhostFireBolt>of(GhostFireBolt::new, MobCategory.MISC)
                                        .sized(0.3125F, 0.3125F)
                                        .clientTrackingRange(4)
                                        .updateInterval(1)
                                        .build("ghost_fire_bolt"));

        public static final RegistryObject<EntityType<WraithNecromancer>> WRAITH_NECROMANCER = ENTITY_TYPE.register(
                        "wraith_necromancer",
                        () -> EntityType.Builder.of(WraithNecromancer::new, MobCategory.MONSTER)
                                        .sized(1.2F, 3.0F)
                                        .clientTrackingRange(8)
                                        .build("wraith_necromancer"));

        public static final RegistryObject<EntityType<ParchedNecromancer>> PARCHED_NECROMANCER = ENTITY_TYPE.register(
                        "parched_necromancer",
                        () -> EntityType.Builder.of(ParchedNecromancer::new, MobCategory.MONSTER)
                                        .sized(0.75F, 2.8875F)
                                        .clientTrackingRange(8)
                                        .build("parched_necromancer"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer.ParchedNecromancerServant>> PARCHED_NECROMANCER_SERVANT = ENTITY_TYPE
                        .register(
                                        "parched_necromancer_servant",
                                        () -> EntityType.Builder.of(
                                                        com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer.ParchedNecromancerServant::new,
                                                        MobCategory.MISC)
                                                        .sized(0.75F, 2.8875F)
                                                        .clientTrackingRange(8)
                                                        .build("parched_necromancer_servant"));

        public static final RegistryObject<EntityType<WraithNecromancerServant>> WRAITH_NECROMANCER_SERVANT = ENTITY_TYPE
                        .register(
                                        "wraith_necromancer_servant",
                                        () -> EntityType.Builder.of(WraithNecromancerServant::new, MobCategory.MISC)
                                                        .sized(1.2F, 3.0F)
                                                        .clientTrackingRange(8)
                                                        .build("wraith_necromancer_servant"));

        public static final RegistryObject<EntityType<MushroomMonstrosity>> MUSHROOM_MONSTROSITY = ENTITY_TYPE.register(
                        "mushroom_monstrosity",
                        () -> EntityType.Builder.of(MushroomMonstrosity::new, MobCategory.MONSTER)
                                        .sized(4.0F, 5.4F)
                                        .fireImmune()
                                        .clientTrackingRange(10)
                                        .build("mushroom_monstrosity"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.ally.AngryMooshroom>> ANGRY_MOOSHROOM = ENTITY_TYPE
                        .register(
                                        "angry_mooshroom",
                                        () -> EntityType.Builder.of(
                                                        com.k1sak1.goetyawaken.common.entities.ally.AngryMooshroom::new,
                                                        MobCategory.MISC)
                                                        .sized(0.9F, 1.4F)
                                                        .clientTrackingRange(10)
                                                        .build("angry_mooshroom"));

        public static final RegistryObject<EntityType<MushroomMissile>> MUSHROOM_MISSILE = ENTITY_TYPE.register(
                        "mushroom_missile",
                        () -> EntityType.Builder.<MushroomMissile>of(MushroomMissile::new, MobCategory.MISC)
                                        .sized(1.0F, 1.0F)
                                        .clientTrackingRange(4)
                                        .updateInterval(1)
                                        .build("mushroom_missile"));

        public static final RegistryObject<EntityType<BlockClusterEntity>> BOULDER_CLUSTER = ENTITY_TYPE.register(
                        "boulder_cluster",
                        () -> EntityType.Builder.<BlockClusterEntity>of(BlockClusterEntity::new, MobCategory.MISC)
                                        .sized(2.0F, 2.0F)
                                        .clientTrackingRange(8)
                                        .updateInterval(1)
                                        .setCustomClientFactory(BoulderClusterFactory::make)
                                        .build("boulder_cluster"));

        public static final RegistryObject<EntityType<MushroomScatterBomb>> MUSHROOM_SCATTER_BOMB = ENTITY_TYPE
                        .register(
                                        "mushroom_scatter_bomb",
                                        () -> EntityType.Builder
                                                        .<MushroomScatterBomb>of(MushroomScatterBomb::new,
                                                                        MobCategory.MISC)
                                                        .sized(0.3125F, 0.3125F)
                                                        .clientTrackingRange(4)
                                                        .updateInterval(10)
                                                        .build("mushroom_scatter_bomb"));

        public static final RegistryObject<EntityType<FrostScytheSlash>> FROST_SCYTHE_SLASH = ENTITY_TYPE
                        .register(
                                        "frost_scythe_slash",
                                        () -> EntityType.Builder
                                                        .<FrostScytheSlash>of(FrostScytheSlash::new,
                                                                        MobCategory.MISC)
                                                        .sized(0.5F, 0.5F)
                                                        .clientTrackingRange(4)
                                                        .updateInterval(10)
                                                        .build("frost_scythe_slash"));

        public static final RegistryObject<EntityType<CaerbannogRabbitServant>> CAERBANNOG_RABBIT_SERVANT = ENTITY_TYPE
                        .register(
                                        "caerbannog_rabbit_servant",
                                        () -> EntityType.Builder.of(CaerbannogRabbitServant::new, MobCategory.MISC)
                                                        .sized(0.4F, 0.5F)
                                                        .clientTrackingRange(8)
                                                        .build("caerbannog_rabbit_servant"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.ally.EnderKeeperServant>> ENDER_KEEPER_SERVANT = ENTITY_TYPE
                        .register(
                                        "ender_keeper_servant",
                                        () -> EntityType.Builder.of(
                                                        com.k1sak1.goetyawaken.common.entities.ally.EnderKeeperServant::new,
                                                        MobCategory.MISC)
                                                        .sized(1.5F, 3.0F)
                                                        .clientTrackingRange(8)
                                                        .fireImmune()
                                                        .build("ender_keeper_servant"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.ally.illager.ApostleServant>> APOSTLE_SERVANT = ENTITY_TYPE
                        .register(
                                        "apostle_servant",
                                        () -> EntityType.Builder.of(
                                                        com.k1sak1.goetyawaken.common.entities.ally.illager.ApostleServant::new,
                                                        MobCategory.MISC)
                                                        .fireImmune()
                                                        .sized(0.6F, 1.95F)
                                                        .clientTrackingRange(8)
                                                        .build("apostle_servant"));

        public static final RegistryObject<EntityType<ObsidianMonolithServant>> OBSIDIAN_MONOLITH_SERVANT = ENTITY_TYPE
                        .register(
                                        "obsidian_monolith_servant",
                                        () -> EntityType.Builder.of(
                                                        ObsidianMonolithServant::new,
                                                        MobCategory.MISC)
                                                        .fireImmune().sized(1.0F, 3.1F)
                                                        .clientTrackingRange(8)
                                                        .updateInterval(1)
                                                        .build("obsidian_monolith_servant"));

        public static final RegistryObject<EntityType<SpiderCreeder>> SPIDER_CREEDER = ENTITY_TYPE.register(
                        "spider_creeder_servant",
                        () -> EntityType.Builder.of(SpiderCreeder::new, MobCategory.MONSTER)
                                        .sized(0.8F, 1.7F)
                                        .clientTrackingRange(8)
                                        .fireImmune()
                                        .build("spider_creeder_servant"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.hostile.HostileSpiderCreeder>> HOSTILE_SPIDER_CREEDER = ENTITY_TYPE
                        .register(
                                        "hostile_spider_creeder",
                                        () -> EntityType.Builder.of(
                                                        com.k1sak1.goetyawaken.common.entities.hostile.HostileSpiderCreeder::new,
                                                        MobCategory.MONSTER)
                                                        .sized(0.8F, 1.7F)
                                                        .clientTrackingRange(8)
                                                        .fireImmune()
                                                        .build("hostile_spider_creeder"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.hostile.illager.VindicatorChef>> VINDICATOR_CHEF = ENTITY_TYPE
                        .register(
                                        "vindicator_chef",
                                        () -> EntityType.Builder.of(
                                                        com.k1sak1.goetyawaken.common.entities.hostile.illager.VindicatorChef::new,
                                                        MobCategory.MONSTER)
                                                        .sized(0.6F, 1.95F)
                                                        .clientTrackingRange(8)
                                                        .build("vindicator_chef"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.item.EyeOfOverwatchItemEntity>> EYE_OF_OVERWATCH_ENTITY = ENTITY_TYPE
                        .register(
                                        "eye_of_overwatch_entity",
                                        () -> EntityType.Builder.of(
                                                        (EntityType.EntityFactory<com.k1sak1.goetyawaken.common.entities.item.EyeOfOverwatchItemEntity>) com.k1sak1.goetyawaken.common.entities.item.EyeOfOverwatchItemEntity::new,
                                                        MobCategory.MISC)
                                                        .sized(0.25F, 0.25F)
                                                        .clientTrackingRange(8)
                                                        .updateInterval(1)
                                                        .build("eye_of_overwatch_entity"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.hostile.HostileAngryMooshroom>> HOSTILE_ANGRY_MOOSHROOM = ENTITY_TYPE
                        .register(
                                        "hostile_angry_mooshroom",
                                        () -> EntityType.Builder.of(
                                                        com.k1sak1.goetyawaken.common.entities.hostile.HostileAngryMooshroom::new,
                                                        MobCategory.MONSTER)
                                                        .sized(0.9F, 1.4F)
                                                        .clientTrackingRange(10)
                                                        .build("hostile_angry_mooshroom"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.util.SummonApostleServant>> SUMMON_APOSTLE_SERVANT = ENTITY_TYPE
                        .register(
                                        "summon_apostle_servant",
                                        () -> EntityType.Builder.of(
                                                        (EntityType.EntityFactory<com.k1sak1.goetyawaken.common.entities.util.SummonApostleServant>) com.k1sak1.goetyawaken.common.entities.util.SummonApostleServant::new,
                                                        MobCategory.MISC)
                                                        .sized(2.0F, 0.5F)
                                                        .fireImmune()
                                                        .clientTrackingRange(10)
                                                        .updateInterval(Integer.MAX_VALUE)
                                                        .build("summon_apostle_servant"));

        public static final RegistryObject<EntityType<OminousPainting>> OMINOUS_PAINTING = ENTITY_TYPE.register(
                        "ominous_painting",
                        () -> EntityType.Builder.<OminousPainting>of(
                                        (entityType, level) -> new OminousPainting(entityType, level),
                                        MobCategory.MISC)
                                        .sized(0.5F, 0.5F)
                                        .fireImmune()
                                        .clientTrackingRange(10)
                                        .updateInterval(Integer.MAX_VALUE)
                                        .build("ominous_painting"));

        public static final RegistryObject<EntityType<MushroomMonstrosityHostile>> HOSTILE_MUSHROOM_MONSTROSITY = ENTITY_TYPE
                        .register(
                                        "hostile_mushroom_monstrosity",
                                        () -> EntityType.Builder.of(
                                                        MushroomMonstrosityHostile::new,
                                                        MobCategory.MONSTER)
                                                        .sized(4.0F, 5.4F)
                                                        .fireImmune()
                                                        .clientTrackingRange(10)
                                                        .build("hostile_mushroom_monstrosity"));

        public static final RegistryObject<EntityType<MaidFairyServant>> MAID_FAIRY_SERVANT = ENTITY_TYPE
                        .register(
                                        "maid_fairy_servant",
                                        () -> EntityType.Builder.of(
                                                        MaidFairyServant::new,
                                                        MobCategory.MISC)
                                                        .sized(0.6F, 1.5F)
                                                        .clientTrackingRange(10)
                                                        .build("maid_fairy_servant"));

        public static final RegistryObject<EntityType<HostileGnasher>> HOSTILE_GNASHER = ENTITY_TYPE
                        .register(
                                        "hostile_gnasher",
                                        () -> EntityType.Builder.of(
                                                        HostileGnasher::new,
                                                        MobCategory.MONSTER)
                                                        .sized(1.4F, 0.9F)
                                                        .clientTrackingRange(10)
                                                        .build("hostile_gnasher"));

        public static final RegistryObject<EntityType<HostileWildfire>> HOSTILE_WILDFIRE = ENTITY_TYPE
                        .register(
                                        "hostile_wildfire",
                                        () -> EntityType.Builder.of(
                                                        HostileWildfire::new,
                                                        MobCategory.MONSTER)
                                                        .sized(0.9F, 2.25F)
                                                        .clientTrackingRange(10)
                                                        .fireImmune()
                                                        .build("hostile_wildfire"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.projectiles.OminousEyeEntity>> OMINOUS_EYE = ENTITY_TYPE
                        .register(
                                        "ominous_eye",
                                        () -> EntityType.Builder.<com.k1sak1.goetyawaken.common.entities.projectiles.OminousEyeEntity>of(
                                                        (entityType, level) -> new com.k1sak1.goetyawaken.common.entities.projectiles.OminousEyeEntity(
                                                                        entityType,
                                                                        level),
                                                        MobCategory.MISC)
                                                        .sized(0.25F, 0.25F)
                                                        .clientTrackingRange(4)
                                                        .updateInterval(10)
                                                        .build("ominous_eye"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.projectiles.PrisonEyeEntity>> PRISON_EYE = ENTITY_TYPE
                        .register(
                                        "prison_eye",
                                        () -> EntityType.Builder.<com.k1sak1.goetyawaken.common.entities.projectiles.PrisonEyeEntity>of(
                                                        (entityType, level) -> new com.k1sak1.goetyawaken.common.entities.projectiles.PrisonEyeEntity(
                                                                        entityType,
                                                                        level),
                                                        MobCategory.MISC)
                                                        .sized(0.25F, 0.25F)
                                                        .clientTrackingRange(4)
                                                        .updateInterval(10)
                                                        .build("prison_eye"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.projectiles.MirageEyeEntity>> MIRAGE_EYE = ENTITY_TYPE
                        .register(
                                        "mirage_eye",
                                        () -> EntityType.Builder.<com.k1sak1.goetyawaken.common.entities.projectiles.MirageEyeEntity>of(
                                                        (entityType, level) -> new com.k1sak1.goetyawaken.common.entities.projectiles.MirageEyeEntity(
                                                                        entityType,
                                                                        level),
                                                        MobCategory.MISC)
                                                        .sized(0.25F, 0.25F)
                                                        .clientTrackingRange(4)
                                                        .updateInterval(10)
                                                        .build("mirage_eye"));

        public static final RegistryObject<EntityType<HostileTropicalSlime>> HOSTILE_TROPICAL_SLIME = ENTITY_TYPE
                        .register(
                                        "hostile_tropical_slime",
                                        () -> EntityType.Builder.of(
                                                        HostileTropicalSlime::new,
                                                        MobCategory.MONSTER)
                                                        .sized(2.04F, 2.04F)
                                                        .clientTrackingRange(10)
                                                        .build("hostile_tropical_slime"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.projectiles.DesertPlaguesCloud>> DESERT_PLAGUES_CLOUD = ENTITY_TYPE
                        .register(
                                        "desert_plagues_cloud",
                                        () -> EntityType.Builder.<com.k1sak1.goetyawaken.common.entities.projectiles.DesertPlaguesCloud>of(
                                                        (entityType, level) -> new com.k1sak1.goetyawaken.common.entities.projectiles.DesertPlaguesCloud(
                                                                        entityType,
                                                                        level),
                                                        MobCategory.MISC)
                                                        .sized(2.0F, 0.5F)
                                                        .fireImmune()
                                                        .clientTrackingRange(10)
                                                        .updateInterval(1)
                                                        .build("desert_plagues_cloud"));

        public static final RegistryObject<EntityType<BoulderingZombie>> BOULDERING_ZOMBIE = ENTITY_TYPE.register(
                        "bouldering_zombie",
                        () -> EntityType.Builder.of(BoulderingZombie::new, MobCategory.MONSTER)
                                        .sized(0.6F, 1.95F)
                                        .clientTrackingRange(8)
                                        .build("bouldering_zombie"));

        public static final RegistryObject<EntityType<MiniGhastHostile>> HOSTILE_MINI_GHAST = ENTITY_TYPE.register(
                        "hostile_mini_ghast",
                        () -> EntityType.Builder.of(MiniGhastHostile::new, MobCategory.MONSTER)
                                        .sized(1.2F, 1.2F)
                                        .fireImmune()
                                        .clientTrackingRange(10)
                                        .build("hostile_mini_ghast"));

        public static final RegistryObject<EntityType<JungleZombie>> JUNGLE_ZOMBIE = ENTITY_TYPE.register(
                        "jungle_zombie",
                        () -> EntityType.Builder.of(JungleZombie::new, MobCategory.MONSTER)
                                        .sized(0.6F, 1.95F)
                                        .clientTrackingRange(8)
                                        .build("jungle_zombie"));

        public static final RegistryObject<EntityType<HostileSnapper>> HOSTILE_SNAPPER = ENTITY_TYPE.register(
                        "hostile_snapper",
                        () -> EntityType.Builder.of(HostileSnapper::new, MobCategory.MONSTER)
                                        .sized(0.85F, 0.6F)
                                        .clientTrackingRange(10)
                                        .build("hostile_snapper"));

        public static final RegistryObject<EntityType<ExplosiveArrow>> EXPLOSIVE_ARROW = ENTITY_TYPE.register(
                        "explosive_arrow",
                        () -> EntityType.Builder.<ExplosiveArrow>of(ExplosiveArrow::new, MobCategory.MISC)
                                        .sized(0.5F, 0.5F)
                                        .clientTrackingRange(4)
                                        .updateInterval(20)
                                        .build("explosive_arrow"));
        public static final RegistryObject<EntityType<NamelessOne>> NAMELESS_ONE = ENTITY_TYPE.register(
                        "nameless_one",
                        () -> EntityType.Builder.of(NamelessOne::new, MobCategory.MONSTER)
                                        .sized(0.75F, 2.8875F)
                                        .clientTrackingRange(8)
                                        .build("nameless_one"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer.NamelessOneServant>> NAMELESS_ONE_SERVANT = ENTITY_TYPE
                        .register(
                                        "nameless_one_servant",
                                        () -> EntityType.Builder.of(
                                                        com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer.NamelessOneServant::new,
                                                        MobCategory.MISC)
                                                        .sized(0.75F, 2.8875F)
                                                        .clientTrackingRange(8)
                                                        .build("nameless_one_servant"));

        public static final RegistryObject<EntityType<DeathFire>> DEATH_FIRE = ENTITY_TYPE.register(
                        "death_fire",
                        () -> EntityType.Builder.<DeathFire>of(DeathFire::new, MobCategory.MISC)
                                        .sized(0.8F, 1.0F)
                                        .fireImmune()
                                        .clientTrackingRange(10)
                                        .build("death_fire"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.projectiles.DeathRay>> DEATH_RAY = ENTITY_TYPE
                        .register(
                                        "death_ray",
                                        () -> EntityType.Builder.<com.k1sak1.goetyawaken.common.entities.projectiles.DeathRay>of(
                                                        com.k1sak1.goetyawaken.common.entities.projectiles.DeathRay::new,
                                                        MobCategory.MISC)
                                                        .sized(0.25F, 0.25F)
                                                        .clientTrackingRange(10)
                                                        .fireImmune()
                                                        .updateInterval(1)
                                                        .build("death_ray"));

        public static final RegistryObject<EntityType<PureLightEntity>> PURE_LIGHT = ENTITY_TYPE
                        .register(
                                        "pure_light",
                                        () -> EntityType.Builder
                                                        .<PureLightEntity>of(PureLightEntity::new, MobCategory.MISC)
                                                        .sized(1.5F, 14F)
                                                        .clientTrackingRange(64)
                                                        .build("pure_light"));

        public static final RegistryObject<EntityType<SkullLordServant>> SKULL_LORD_SERVANT = ENTITY_TYPE
                        .register(
                                        "skull_lord_servant",
                                        () -> EntityType.Builder.of(SkullLordServant::new, MobCategory.MONSTER)
                                                        .sized(0.5F, 0.5F)
                                                        .clientTrackingRange(8)
                                                        .build("skull_lord_servant"));

        public static final RegistryObject<EntityType<BoneLordServant>> BONE_LORD_SERVANT = ENTITY_TYPE
                        .register(
                                        "bone_lord_servant",
                                        () -> EntityType.Builder.of(BoneLordServant::new, MobCategory.MONSTER)
                                                        .sized(0.6F, 1.99F)
                                                        .clientTrackingRange(8)
                                                        .build("bone_lord_servant"));

        public static final RegistryObject<EntityType<BoundSorcerer>> BOUND_SORCERER = ENTITY_TYPE.register(
                        "bound_sorcerer",
                        () -> EntityType.Builder.of(BoundSorcerer::new, MobCategory.MONSTER)
                                        .sized(0.6F, 1.95F)
                                        .clientTrackingRange(8)
                                        .build("bound_sorcerer"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton.VanguardChampion>> VANGUARD_CHAMPION = ENTITY_TYPE
                        .register(
                                        "vanguard_champion",
                                        () -> EntityType.Builder.of(
                                                        com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton.VanguardChampion::new,
                                                        MobCategory.MONSTER)
                                                        .sized(0.6F, 1.99F)
                                                        .clientTrackingRange(12)
                                                        .build("vanguard_champion"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.hostile.undead.skeleton.HostileVanguardChampion>> HOSTILE_VANGUARD_CHAMPION = ENTITY_TYPE
                        .register(
                                        "hostile_vanguard_champion",
                                        () -> EntityType.Builder.of(
                                                        com.k1sak1.goetyawaken.common.entities.hostile.undead.skeleton.HostileVanguardChampion::new,
                                                        MobCategory.MONSTER)
                                                        .sized(0.6F, 1.99F)
                                                        .clientTrackingRange(12)
                                                        .build("hostile_vanguard_champion"));

        public static final RegistryObject<EntityType<HostileDrownedNecromancer>> HOSTILE_DROWNED_NECROMANCER = ENTITY_TYPE
                        .register(
                                        "hostile_drowned_necromancer",
                                        () -> EntityType.Builder.of(HostileDrownedNecromancer::new, MobCategory.MONSTER)
                                                        .sized(0.75F, 2.8875F)
                                                        .clientTrackingRange(8)
                                                        .build("hostile_drowned_necromancer"));

        public static final RegistryObject<EntityType<FrozenZombie>> FROZEN_ZOMBIE = ENTITY_TYPE.register(
                        "frozen_zombie",
                        () -> EntityType.Builder.of(FrozenZombie::new, MobCategory.MONSTER)
                                        .sized(0.6F, 1.95F)
                                        .clientTrackingRange(8)
                                        .build("frozen_zombie"));

        public static final RegistryObject<EntityType<ModSwordProjectile>> MOD_SWORD_PROJECTILE = ENTITY_TYPE
                        .register(
                                        "mod_sword_projectile",
                                        () -> EntityType.Builder
                                                        .<ModSwordProjectile>of(
                                                                        (type, level) -> new ModSwordProjectile(type,
                                                                                        level),
                                                                        MobCategory.MISC)
                                                        .sized(0.5F, 0.5F)
                                                        .clientTrackingRange(4)
                                                        .updateInterval(1)
                                                        .build("mod_sword_projectile"));

        public static final RegistryObject<EntityType<com.k1sak1.goetyawaken.common.entities.ally.undead.ScarletVex>> SCARLET_VEX = ENTITY_TYPE
                        .register(
                                        "scarlet_vex",
                                        () -> EntityType.Builder.of(
                                                        com.k1sak1.goetyawaken.common.entities.ally.undead.ScarletVex::new,
                                                        MobCategory.MISC)
                                                        .sized(0.4F, 0.8F)
                                                        .clientTrackingRange(8)
                                                        .fireImmune()
                                                        .build("scarlet_vex"));

        public static final RegistryObject<EntityType<AbstractTowerWraith>> TOWER_WRAITH_SERVANT = ENTITY_TYPE
                        .register(
                                        "tower_wraith_servant",
                                        () -> EntityType.Builder.of(
                                                        AbstractTowerWraith::new,
                                                        MobCategory.MISC)
                                                        .sized(0.6F, 1.99F)
                                                        .clientTrackingRange(8)
                                                        .fireImmune()
                                                        .build("tower_wraith"));

        public static final RegistryObject<EntityType<HostileTowerWraith>> HOSTILE_TOWER_WRAITH = ENTITY_TYPE
                        .register(
                                        "hostile_tower_wraith",
                                        () -> EntityType.Builder.of(
                                                        HostileTowerWraith::new,
                                                        MobCategory.MONSTER)
                                                        .sized(0.6F, 1.99F)
                                                        .clientTrackingRange(8)
                                                        .fireImmune()
                                                        .build("hostile_tower_wraith"));

        public static final RegistryObject<EntityType<HostileTwilightGoat>> HOSTILE_TWILIGHT_GOAT = ENTITY_TYPE
                        .register(
                                        "hostile_twilight_goat",
                                        () -> EntityType.Builder.of(
                                                        HostileTwilightGoat::new,
                                                        MobCategory.MONSTER)
                                                        .sized(0.9F, 1.3F)
                                                        .clientTrackingRange(10)
                                                        .build("hostile_twilight_goat"));

        public static final RegistryObject<EntityType<TowerWitch>> TOWER_WITCH = ENTITY_TYPE
                        .register(
                                        "tower_witch",
                                        () -> EntityType.Builder.of(
                                                        TowerWitch::new,
                                                        MobCategory.MONSTER)
                                                        .sized(0.6F, 1.95F)
                                                        .clientTrackingRange(8)
                                                        .build("tower_witch"));

        public static final RegistryObject<EntityType<TowerWitchServant>> TOWER_WITCH_SERVANT = ENTITY_TYPE
                        .register(
                                        "tower_witch_servant",
                                        () -> EntityType.Builder.of(
                                                        TowerWitchServant::new,
                                                        MobCategory.MONSTER)
                                                        .sized(0.6F, 1.95F)
                                                        .clientTrackingRange(8)
                                                        .build("tower_witch_servant"));

        public static final RegistryObject<EntityType<CorruptedSlime>> CORRUPTED_SLIME = ENTITY_TYPE
                        .register(
                                        "corrupted_slime_servant",
                                        () -> EntityType.Builder.of(
                                                        CorruptedSlime::new,
                                                        MobCategory.MONSTER)
                                                        .sized(2.04F, 2.04F)
                                                        .clientTrackingRange(10)
                                                        .build("corrupted_slime_servant"));

        public static final RegistryObject<EntityType<CorruptedSoulBolt>> CORRUPTED_SOUL_BOLT = ENTITY_TYPE
                        .register(
                                        "corrupted_soul_bolt",
                                        () -> EntityType.Builder
                                                        .<CorruptedSoulBolt>of(CorruptedSoulBolt::new, MobCategory.MISC)
                                                        .sized(0.3125F, 0.3125F)
                                                        .clientTrackingRange(4)
                                                        .updateInterval(1)
                                                        .build("corrupted_soul_bolt"));

}
