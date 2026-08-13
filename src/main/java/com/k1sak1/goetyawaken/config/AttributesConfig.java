package com.k1sak1.goetyawaken.config;

import net.minecraftforge.common.ForgeConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import java.io.File;

public class AttributesConfig {

        public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        public static final ForgeConfigSpec SPEC;

        // Warden Servant
        public static final ForgeConfigSpec.ConfigValue<Double> WardenServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> WardenServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> WardenServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> WardenServantArmorToughness;

        // Creeper Servant
        public static final ForgeConfigSpec.ConfigValue<Double> CreeperServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> CreeperServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> CreeperServantArmorToughness;

        // JITB Zombie Servant
        public static final ForgeConfigSpec.ConfigValue<Double> JITBZombieServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> JITBZombieServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> JITBZombieServantArmorToughness;

        // Giant Servant
        public static final ForgeConfigSpec.ConfigValue<Double> GiantServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> GiantServantMovementSpeed;
        public static final ForgeConfigSpec.ConfigValue<Double> GiantServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> GiantServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> GiantServantArmorToughness;

        // Poisonous Potato Zombie Servant
        public static final ForgeConfigSpec.ConfigValue<Double> PoisonousPotatoZombieServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> PoisonousPotatoZombieServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> PoisonousPotatoZombieServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> PoisonousPotatoZombieServantArmorToughness;

        // Poisonous Potato Skeleton Servant
        public static final ForgeConfigSpec.ConfigValue<Double> PoisonousPotatoSkeletonServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> PoisonousPotatoSkeletonServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> PoisonousPotatoSkeletonServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> PoisonousPotatoSkeletonServantArmorToughness;

        // Poisonous Potato Creeper Servant
        public static final ForgeConfigSpec.ConfigValue<Double> PoisonousPotatoCreeperServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> PoisonousPotatoCreeperServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> PoisonousPotatoCreeperServantArmorToughness;

        // Toxifin Servant
        public static final ForgeConfigSpec.ConfigValue<Double> ToxifinServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> ToxifinServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> ToxifinServantMovementSpeed;
        public static final ForgeConfigSpec.ConfigValue<Double> ToxifinServantFollowRange;

        // Plaguewhale Slab Servant
        public static final ForgeConfigSpec.ConfigValue<Double> PlaguewhaleSlabServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> PlaguewhaleSlabServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> PlaguewhaleSlabServantMovementSpeed;
        public static final ForgeConfigSpec.ConfigValue<Double> PlaguewhaleSlabServantFollowRange;

        // Ice Creeper Servant
        public static final ForgeConfigSpec.ConfigValue<Double> IceCreeperServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> IceCreeperServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> IceCreeperServantArmorToughness;

        // Spider Creeder
        public static final ForgeConfigSpec.ConfigValue<Double> SpiderCreederHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> SpiderCreederArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> SpiderCreederArmorToughness;

        // Statue Creeper
        public static final ForgeConfigSpec.ConfigValue<Double> StatueCreeperHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> StatueCreeperArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> StatueCreeperArmorToughness;

        // Enderman Servant
        public static final ForgeConfigSpec.ConfigValue<Double> EndermanServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> EndermanServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> EndermanServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> EndermanServantArmorToughness;

        // Endermite Servant
        public static final ForgeConfigSpec.ConfigValue<Double> EndermiteServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> EndermiteServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> EndermiteServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> EndermiteServantArmorToughness;

        // Shulker Servant
        public static final ForgeConfigSpec.ConfigValue<Double> ShulkerServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> ShulkerServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> ShulkerServantArmorToughness;

        // Wither Servant
        public static final ForgeConfigSpec.ConfigValue<Double> WitherServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> WitherServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> WitherServantArmorToughness;

        // Pale Golem Servant
        public static final ForgeConfigSpec.ConfigValue<Double> PaleGolemServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> PaleGolemServantDamage;

        // Royalguard Servant
        public static final ForgeConfigSpec.ConfigValue<Double> RoyalguardServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> RoyalguardServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> RoyalguardServantArmorToughness;
        public static final ForgeConfigSpec.ConfigValue<Double> RoyalguardServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> RoyalguardServantShieldCapacity;

        // Tower Guard
        public static final ForgeConfigSpec.ConfigValue<Double> TowerGuardHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> TowerGuardArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> TowerGuardArmorToughness;
        public static final ForgeConfigSpec.ConfigValue<Double> TowerGuardDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> TowerGuardShieldCapacity;
        public static final ForgeConfigSpec.ConfigValue<Double> TowerGuardMovementSpeed;

        // Silverfish Servant
        public static final ForgeConfigSpec.ConfigValue<Double> SilverfishServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> SilverfishServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> SilverfishServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> SilverfishServantArmorToughness;

        // Caerbannog Rabbit Servant
        public static final ForgeConfigSpec.ConfigValue<Double> CaerbannogRabbitServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> CaerbannogRabbitServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> CaerbannogRabbitServantArmor;

        // Mushroom Monstrosity
        public static final ForgeConfigSpec.ConfigValue<Double> MushroomMonstrosityHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> MushroomMonstrosityArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> MushroomMonstrosityArmorToughness;
        public static final ForgeConfigSpec.ConfigValue<Double> MushroomMonstrosityDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> MushroomMonstrosityFollowRange;
        public static final ForgeConfigSpec.ConfigValue<Double> MushroomMonstrosityDamageCap;
        public static final ForgeConfigSpec.ConfigValue<Double> MushroomMonstrosityPercentageDamage;
        public static final ForgeConfigSpec.ConfigValue<Integer> MushroomMonstrosityDynamicShieldDefaultLimitTime;
        public static final ForgeConfigSpec.ConfigValue<Boolean> AllowPoisonousMushroomHealSpecialOwners;
        public static final ForgeConfigSpec.ConfigValue<Boolean> AllowMushroomMonstrosityPlantPoisonousMushroom;

        // Angry Mooshroom
        public static final ForgeConfigSpec.ConfigValue<Double> AngryMooshroomHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> AngryMooshroomArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> AngryMooshroomDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> AngryMooshroomMovementSpeed;

        // Wraith Necromancer
        public static final ForgeConfigSpec.ConfigValue<Double> WraithNecromancerHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> WraithNecromancerArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> WraithNecromancerDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> WraithNecromancerFollowRange;

        // Parched Necromancer
        public static final ForgeConfigSpec.ConfigValue<Double> ParchedNecromancerHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> ParchedNecromancerArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> ParchedNecromancerArmorToughness;
        public static final ForgeConfigSpec.ConfigValue<Double> ParchedNecromancerDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> ParchedNecromancerFollowRange;

        // Nameless One
        public static final ForgeConfigSpec.ConfigValue<Double> NamelessOneHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> NamelessOneArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> NamelessOneArmorToughness;
        public static final ForgeConfigSpec.ConfigValue<Double> NamelessOneDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> NamelessOneFollowRange;
        // Nameless One Damage Cap Settings
        public static final ForgeConfigSpec.ConfigValue<Double> NamelessOneDamageCapPercent;
        public static final ForgeConfigSpec.ConfigValue<Integer> NamelessOneHitCooldown;
        public static final ForgeConfigSpec.ConfigValue<Integer> NamelessOneDynamicReductionTime;
        // Nameless One Heal Settings
        public static final ForgeConfigSpec.ConfigValue<Integer> NamelessOneHealInterval;
        public static final ForgeConfigSpec.ConfigValue<Double> NamelessOneHealAmount;

        // Illusioner Servant
        public static final ForgeConfigSpec.ConfigValue<Double> IllusionerServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> IllusionerServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> IllusionerServantArmorToughness;
        public static final ForgeConfigSpec.ConfigValue<Double> IllusionerServantMovementSpeed;
        public static final ForgeConfigSpec.ConfigValue<Double> IllusionerServantFollowRange;

        // Arch Illusioner Servant
        public static final ForgeConfigSpec.ConfigValue<Double> ArchIllusionerServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> ArchIllusionerServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> ArchIllusionerServantArmorToughness;
        public static final ForgeConfigSpec.ConfigValue<Double> ArchIllusionerServantMovementSpeed;
        public static final ForgeConfigSpec.ConfigValue<Double> ArchIllusionerServantFollowRange;
        public static final ForgeConfigSpec.ConfigValue<Double> ArchIllusionerDamage;

        // Bound Sorcerer
        public static final ForgeConfigSpec.ConfigValue<Double> BoundSorcererHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> BoundSorcererArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> BoundSorcererFollowRange;

        // Vanguard Champion
        public static final ForgeConfigSpec.ConfigValue<Double> VanguardChampionHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> VanguardChampionMovementSpeed;
        public static final ForgeConfigSpec.ConfigValue<Double> VanguardChampionDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> VanguardChampionAttackKnockback;
        public static final ForgeConfigSpec.ConfigValue<Double> VanguardChampionArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> VanguardChampionArmorToughness;

        // Scarlet Vex
        public static final ForgeConfigSpec.ConfigValue<Double> ScarletVexHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> ScarletVexDamage;

        // Tower Wraith
        public static final ForgeConfigSpec.ConfigValue<Double> TowerWraithHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> TowerWraithArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> TowerWraithDamage;

        // Tower Witch
        public static final ForgeConfigSpec.ConfigValue<Double> TowerWitchHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> TowerWitchArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> TowerWitchMovementSpeed;

        // Giant Ghast
        public static final ForgeConfigSpec.ConfigValue<Double> GiantGhastHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> GiantGhastArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> GiantGhastArmorToughness;
        public static final ForgeConfigSpec.ConfigValue<Double> GiantGhastDamage;
        public static final ForgeConfigSpec.ConfigValue<Boolean> GiantGhastAllowGiantHellBlast;

        // Rampart Captain
        public static final ForgeConfigSpec.ConfigValue<Double> RampartCaptainHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> RampartCaptainDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> RampartCaptainArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> RampartCaptainArmorToughness;
        public static final ForgeConfigSpec.ConfigValue<Double> RampartCaptainRunAttackLoopChance;

        // Burning Shield
        public static final ForgeConfigSpec.ConfigValue<Double> BurningShieldHealth;

        // Masquerader Servant
        public static final ForgeConfigSpec.ConfigValue<Double> MasqueraderServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> MasqueraderServantMovementSpeed;
        public static final ForgeConfigSpec.ConfigValue<Double> MasqueraderServantFollowRange;
        public static final ForgeConfigSpec.ConfigValue<Double> MasqueraderServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> MasqueraderServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> MasqueraderServantArmorToughness;

        // Swampjaw Servant
        public static final ForgeConfigSpec.ConfigValue<Double> SwampjawServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> SwampjawServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> SwampjawServantKnockbackResistance;
        public static final ForgeConfigSpec.ConfigValue<Double> SwampjawServantExplosionPower;
        public static final ForgeConfigSpec.ConfigValue<Double> SwampjawServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> SwampjawServantArmorToughness;

        // Bellringer Servant
        public static final ForgeConfigSpec.ConfigValue<Double> BellringerServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> BellringerServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> BellringerServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> BellringerServantArmorToughness;

        // Rosalyne Servant
        public static final ForgeConfigSpec.ConfigValue<Double> RosalyneServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> RosalyneServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> RosalyneServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> RosalyneServantArmorToughness;

        // Rose Spirit Servant
        public static final ForgeConfigSpec.ConfigValue<Double> RoseSpiritServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> RoseSpiritServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> RoseSpiritServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> RoseSpiritServantArmorToughness;

        // Dame Fortuna Servant
        public static final ForgeConfigSpec.ConfigValue<Double> DameFortunaServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> DameFortunaServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> DameFortunaServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> DameFortunaServantArmorToughness;

        // Archer Servant
        public static final ForgeConfigSpec.ConfigValue<Double> ArcherServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> ArcherServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> ArcherServantMovementSpeed;
        public static final ForgeConfigSpec.ConfigValue<Double> ArcherServantFollowRange;
        public static final ForgeConfigSpec.ConfigValue<Double> ArcherServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> ArcherServantArmorToughness;

        // Skirmisher Servant
        public static final ForgeConfigSpec.ConfigValue<Double> SkirmisherServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> SkirmisherServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> SkirmisherServantMovementSpeed;
        public static final ForgeConfigSpec.ConfigValue<Double> SkirmisherServantFollowRange;
        public static final ForgeConfigSpec.ConfigValue<Double> SkirmisherServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> SkirmisherServantArmorToughness;

        // Legioner Servant
        public static final ForgeConfigSpec.ConfigValue<Double> LegionerServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> LegionerServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> LegionerServantMovementSpeed;
        public static final ForgeConfigSpec.ConfigValue<Double> LegionerServantFollowRange;
        public static final ForgeConfigSpec.ConfigValue<Double> LegionerServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> LegionerServantArmorToughness;

        // Sculk Centipede Servant
        public static final ForgeConfigSpec.ConfigValue<Double> SculkCentipedeServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> SculkCentipedeServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> SculkCentipedeServantMovementSpeed;
        public static final ForgeConfigSpec.ConfigValue<Double> SculkCentipedeServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> SculkCentipedeServantArmorToughness;

        // Sculk Leech Servant
        public static final ForgeConfigSpec.ConfigValue<Double> SculkLeechServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> SculkLeechServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> SculkLeechServantMovementSpeed;
        public static final ForgeConfigSpec.ConfigValue<Double> SculkLeechServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> SculkLeechServantArmorToughness;

        // Shattered Servant
        public static final ForgeConfigSpec.ConfigValue<Double> ShatteredServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> ShatteredServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> ShatteredServantMovementSpeed;
        public static final ForgeConfigSpec.ConfigValue<Double> ShatteredServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> ShatteredServantFollowRange;
        public static final ForgeConfigSpec.ConfigValue<Double> ShatteredServantArmorToughness;

        // Shriek Worm Servant
        public static final ForgeConfigSpec.ConfigValue<Double> ShriekWormServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> ShriekWormServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> ShriekWormServantMovementSpeed;
        public static final ForgeConfigSpec.ConfigValue<Double> ShriekWormServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> ShriekWormServantArmorToughness;

        // Sludge Servant
        public static final ForgeConfigSpec.ConfigValue<Double> SludgeServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> SludgeServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> SludgeServantMovementSpeed;
        public static final ForgeConfigSpec.ConfigValue<Double> SludgeServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> SludgeServantArmorToughness;

        // Stalker Servant
        public static final ForgeConfigSpec.ConfigValue<Double> StalkerServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> StalkerServantDamage;
        public static final ForgeConfigSpec.ConfigValue<Double> StalkerServantMovementSpeed;
        public static final ForgeConfigSpec.ConfigValue<Double> StalkerServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> StalkerServantKnockbackResistance;
        public static final ForgeConfigSpec.ConfigValue<Double> StalkerServantArmorToughness;
        static {
                BUILDER.push("Attributes");

                // Warden Servant
                BUILDER.push("Warden Servant");
                WardenServantHealth = BUILDER.comment("How much Max Health Warden Servants have, Default: 500.0")
                                .defineInRange("wardenServantHealth", 500.0, 1.0, Double.MAX_VALUE);
                WardenServantDamage = BUILDER.comment("How much damage Warden Servants deals, Default: 30.0")
                                .defineInRange("wardenServantDamage", 30.0, 1.0, Double.MAX_VALUE);
                WardenServantArmor = BUILDER.comment("How much natural armor points Warden Servants have, Default: 0.0")
                                .defineInRange("wardenServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                WardenServantArmorToughness = BUILDER
                                .comment("How much armor toughness Warden Servants have, Default: 0.0")
                                .defineInRange("wardenServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Creeper Servant
                BUILDER.push("Creeper Servant");
                CreeperServantHealth = BUILDER.comment("How much Max Health Creeper Servants have, Default: 20.0")
                                .defineInRange("creeperServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                CreeperServantArmor = BUILDER
                                .comment("How much natural armor points Creeper Servants have, Default: 0.0")
                                .defineInRange("creeperServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                CreeperServantArmorToughness = BUILDER
                                .comment("How much armor toughness Creeper Servants have, Default: 0.0")
                                .defineInRange("creeperServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // JITB Zombie Servant
                BUILDER.push("JITB Zombie Servant");
                JITBZombieServantHealth = BUILDER
                                .comment("How much Max Health JITB Zombie Servants have, Default: 20.0")
                                .defineInRange("jitbZombieServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                JITBZombieServantArmor = BUILDER
                                .comment("How much natural armor points JITB Zombie Servants have, Default: 2.0")
                                .defineInRange("jitbZombieServantArmor", 2.0, 0.0, Double.MAX_VALUE);
                JITBZombieServantArmorToughness = BUILDER
                                .comment("How much armor toughness JITB Zombie Servants have, Default: 0.0")
                                .defineInRange("jitbZombieServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Giant Servant
                BUILDER.push("Giant Servant");
                GiantServantHealth = BUILDER
                                .comment("How much Max Health Giant Servants have, Default: 100.0")
                                .defineInRange("giantServantHealth", 100.0, 1.0, Double.MAX_VALUE);
                GiantServantMovementSpeed = BUILDER
                                .comment("How much movement speed Giant Servants have, Default: 0.5")
                                .defineInRange("giantServantMovementSpeed", 0.5, 0.0, Double.MAX_VALUE);
                GiantServantDamage = BUILDER
                                .comment("How much damage Giant Servants deals, Default: 15.0")
                                .defineInRange("giantServantDamage", 15.0, 1.0, Double.MAX_VALUE);
                GiantServantArmor = BUILDER
                                .comment("How much natural armor points Giant Servants have, Default: 0.0")
                                .defineInRange("giantServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                GiantServantArmorToughness = BUILDER
                                .comment("How much armor toughness Giant Servants have, Default: 0.0")
                                .defineInRange("giantServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Poisonous Potato Zombie Servant
                BUILDER.push("Poisonous Potato Zombie Servant");
                PoisonousPotatoZombieServantHealth = BUILDER
                                .comment("How much Max Health Poisonous Potato Zombie Servants have, Default: 20.0")
                                .defineInRange("poisonousPotatoZombieServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                PoisonousPotatoZombieServantDamage = BUILDER
                                .comment("How much damage Poisonous Potato Zombie Servants deals, Default: 3.0")
                                .defineInRange("poisonousPotatoZombieServantDamage", 3.0, 1.0, Double.MAX_VALUE);
                PoisonousPotatoZombieServantArmor = BUILDER
                                .comment("How much natural armor points Poisonous Potato Zombie Servants have, Default: 2.0")
                                .defineInRange("poisonousPotatoZombieServantArmor", 2.0, 0.0, Double.MAX_VALUE);
                PoisonousPotatoZombieServantArmorToughness = BUILDER
                                .comment("How much armor toughness Poisonous Potato Zombie Servants have, Default: 0.0")
                                .defineInRange("poisonousPotatoZombieServantArmorToughness", 0.0, 0.0,
                                                Double.MAX_VALUE);
                BUILDER.pop();

                // Poisonous Potato Skeleton Servant
                BUILDER.push("Poisonous Potato Skeleton Servant");
                PoisonousPotatoSkeletonServantHealth = BUILDER
                                .comment("How much Max Health Poisonous Potato Skeleton Servants have, Default: 20.0")
                                .defineInRange("poisonousPotatoSkeletonServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                PoisonousPotatoSkeletonServantDamage = BUILDER
                                .comment("How much damage Poisonous Potato Skeleton Servants deals, Default: 2.0")
                                .defineInRange("poisonousPotatoSkeletonServantDamage", 2.0, 1.0, Double.MAX_VALUE);
                PoisonousPotatoSkeletonServantArmor = BUILDER
                                .comment("How much natural armor points Poisonous Potato Skeleton Servants have, Default: 0.0")
                                .defineInRange("poisonousPotatoSkeletonServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                PoisonousPotatoSkeletonServantArmorToughness = BUILDER
                                .comment("How much armor toughness Poisonous Potato Skeleton Servants have, Default: 0.0")
                                .defineInRange("poisonousPotatoSkeletonServantArmorToughness", 0.0, 0.0,
                                                Double.MAX_VALUE);
                BUILDER.pop();

                BUILDER.push("Poisonous Potato Creeper Servant");
                PoisonousPotatoCreeperServantHealth = BUILDER
                                .comment("How much Max Health Poisonous Potato Creeper Servants have, Default: 20.0")
                                .defineInRange("poisonousPotatoCreeperServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                PoisonousPotatoCreeperServantArmor = BUILDER
                                .comment("How much natural armor points Poisonous Potato Creeper Servants have, Default: 2.0")
                                .defineInRange("poisonousPotatoCreeperServantArmor", 2.0, 0.0, Double.MAX_VALUE);
                PoisonousPotatoCreeperServantArmorToughness = BUILDER
                                .comment("How much armor toughness Poisonous Potato Creeper Servants have, Default: 0.0")
                                .defineInRange("poisonousPotatoCreeperServantArmorToughness", 0.0, 0.0,
                                                Double.MAX_VALUE);
                BUILDER.pop();

                // Toxifin Slab Servant
                BUILDER.push("Toxifin Slab Servant");
                ToxifinServantHealth = BUILDER
                                .comment("How much Max Health Toxifin Slab Servants have, Default: 30.0")
                                .defineInRange("toxifinServantHealth", 30.0, 1.0, Double.MAX_VALUE);
                ToxifinServantDamage = BUILDER
                                .comment("How much damage Toxifin Slab Servants deals, Default: 6.0")
                                .defineInRange("toxifinServantDamage", 6.0, 1.0, Double.MAX_VALUE);
                ToxifinServantMovementSpeed = BUILDER
                                .comment("How much movement speed Toxifin Slab Servants have, Default: 0.5")
                                .defineInRange("toxifinServantMovementSpeed", 0.5, 0.0, Double.MAX_VALUE);
                ToxifinServantFollowRange = BUILDER
                                .comment("How much follow range Toxifin Slab Servants have, Default: 16.0")
                                .defineInRange("toxifinServantFollowRange", 16.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Plaguewhale Slab Servant
                BUILDER.push("Plaguewhale Slab Servant");
                PlaguewhaleSlabServantHealth = BUILDER
                                .comment("How much Max Health Plaguewhale Slab Servants have, Default: 80.0")
                                .defineInRange("plaguewhaleSlabServantHealth", 80.0, 1.0, Double.MAX_VALUE);
                PlaguewhaleSlabServantDamage = BUILDER
                                .comment("How much damage Plaguewhale Slab Servants deals, Default: 8.0")
                                .defineInRange("plaguewhaleSlabServantDamage", 8.0, 1.0, Double.MAX_VALUE);
                PlaguewhaleSlabServantMovementSpeed = BUILDER
                                .comment("How much movement speed Plaguewhale Slab Servants have, Default: 0.3")
                                .defineInRange("plaguewhaleSlabServantMovementSpeed", 0.3, 0.0, Double.MAX_VALUE);
                PlaguewhaleSlabServantFollowRange = BUILDER
                                .comment("How much follow range Plaguewhale Slab Servants have, Default: 16.0")
                                .defineInRange("plaguewhaleSlabServantFollowRange", 16.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Ice Creeper Servant
                BUILDER.push("Ice Creeper Servant");
                IceCreeperServantHealth = BUILDER
                                .comment("How much Max Health Ice Creeper Servants have, Default: 20.0")
                                .defineInRange("iceCreeperServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                IceCreeperServantArmor = BUILDER
                                .comment("How much natural armor points Ice Creeper Servants have, Default: 0.0")
                                .defineInRange("iceCreeperServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                IceCreeperServantArmorToughness = BUILDER
                                .comment("How much armor toughness Ice Creeper Servants have, Default: 0.0")
                                .defineInRange("iceCreeperServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Spider Creeder
                BUILDER.push("Spider Creeder");
                SpiderCreederHealth = BUILDER
                                .comment("How much Max Health Spider Creeder has, Default: 20.0")
                                .defineInRange("spiderCreederHealth", 20.0, 1.0, Double.MAX_VALUE);
                SpiderCreederArmor = BUILDER
                                .comment("How much natural armor points Spider Creeder have, Default: 0.0")
                                .defineInRange("spiderCreederArmor", 0.0, 0.0, Double.MAX_VALUE);
                SpiderCreederArmorToughness = BUILDER
                                .comment("How much armor toughness Spider Creeder have, Default: 0.0")
                                .defineInRange("spiderCreederArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Statue Creeper
                BUILDER.push("Statue Creeper");
                StatueCreeperHealth = BUILDER
                                .comment("How much Max Health Statue Creeper has, Default: 12.0")
                                .defineInRange("statueCreeperHealth", 12.0, 1.0, Double.MAX_VALUE);
                StatueCreeperArmor = BUILDER
                                .comment("How much natural armor points Statue Creeper has, Default: 2.0")
                                .defineInRange("statueCreeperArmor", 2.0, 0.0, Double.MAX_VALUE);
                StatueCreeperArmorToughness = BUILDER
                                .comment("How much armor toughness Statue Creeper has, Default: 2.0")
                                .defineInRange("statueCreeperArmorToughness", 2.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Enderman Servant
                BUILDER.push("Enderman Servant");
                EndermanServantHealth = BUILDER.comment("How much Max Health Enderman Servants have, Default: 40.0")
                                .defineInRange("endermanServantHealth", 40.0, 1.0, Double.MAX_VALUE);
                EndermanServantDamage = BUILDER.comment("How much damage Enderman Servants deals, Default: 7.0")
                                .defineInRange("endermanServantDamage", 7.0, 1.0, Double.MAX_VALUE);
                EndermanServantArmor = BUILDER
                                .comment("How much natural armor points Enderman Servants have, Default: 0.0")
                                .defineInRange("endermanServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                EndermanServantArmorToughness = BUILDER
                                .comment("How much armor toughness Enderman Servants have, Default: 0.0")
                                .defineInRange("endermanServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Endermite Servant
                BUILDER.push("Endermite Servant");
                EndermiteServantHealth = BUILDER.comment("How much Max Health Endermite Servants have, Default: 8.0")
                                .defineInRange("endermiteServantHealth", 8.0, 1.0, Double.MAX_VALUE);
                EndermiteServantDamage = BUILDER.comment("How much damage Endermite Servants deals, Default: 2.0")
                                .defineInRange("endermiteServantDamage", 2.0, 1.0, Double.MAX_VALUE);
                EndermiteServantArmor = BUILDER
                                .comment("How much natural armor points Endermite Servants have, Default: 0.0")
                                .defineInRange("endermiteServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                EndermiteServantArmorToughness = BUILDER
                                .comment("How much armor toughness Endermite Servants have, Default: 0.0")
                                .defineInRange("endermiteServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Shulker Servant
                BUILDER.push("Shulker Servant");
                ShulkerServantHealth = BUILDER.comment("How much Max Health Shulker Servants have, Default: 30.0")
                                .defineInRange("shulkerServantHealth", 30.0, 1.0, Double.MAX_VALUE);
                ShulkerServantArmor = BUILDER
                                .comment("How much natural armor points Shulker Servants have, Default: 20.0")
                                .defineInRange("shulkerServantArmor", 20.0, 0.0, Double.MAX_VALUE);
                ShulkerServantArmorToughness = BUILDER
                                .comment("How much armor toughness Shulker Servants have, Default: 0.0")
                                .defineInRange("shulkerServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Wither Servant
                BUILDER.push("Wither Servant");
                WitherServantHealth = BUILDER.comment("How much Max Health Wither Servants have, Default: 300.0")
                                .defineInRange("witherServantHealth", 300.0, 1.0, Double.MAX_VALUE);
                WitherServantArmor = BUILDER.comment("How much natural armor points Wither Servants have, Default: 4.0")
                                .defineInRange("witherServantArmor", 4.0, 0.0, Double.MAX_VALUE);
                WitherServantArmorToughness = BUILDER
                                .comment("How much armor toughness Wither Servants have, Default: 0.0")
                                .defineInRange("witherServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Pale Golem Servant
                BUILDER.push("Pale Golem Servant");
                PaleGolemServantHealth = BUILDER.comment("How much Max Health Pale Golem Servants have, Default: 120.0")
                                .defineInRange("paleGolemServantHealth", 120.0, 1.0, Double.MAX_VALUE);
                PaleGolemServantDamage = BUILDER.comment("How much damage Pale Golem Servants deals, Default: 15.0")
                                .defineInRange("paleGolemServantDamage", 15.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Royalguard Servant
                BUILDER.push("Royalguard Servant");
                RoyalguardServantHealth = BUILDER.comment("How much Max Health Royalguard Servants have, Default: 24.0")
                                .defineInRange("royalguardServantHealth", 24.0, 1.0, Double.MAX_VALUE);
                RoyalguardServantArmor = BUILDER
                                .comment("How much natural armor points Royalguard Servants have, Default: 20.0")
                                .defineInRange("royalguardServantArmor", 20.0, 0.0, Double.MAX_VALUE);
                RoyalguardServantArmorToughness = BUILDER
                                .comment("How much armor toughness Royalguard Servants have, Default: 8.0")
                                .defineInRange("royalguardServantArmorToughness", 8.0, 0.0, Double.MAX_VALUE);
                RoyalguardServantDamage = BUILDER.comment("How much damage Royalguard Servants deals, Default: 9.0")
                                .defineInRange("royalguardServantDamage", 9.0, 1.0, Double.MAX_VALUE);
                RoyalguardServantShieldCapacity = BUILDER
                                .comment("How much damage Royalguards' shield can absorb before breaking, Default: 10.0")
                                .defineInRange("royalguardServantShieldCapacity", 10.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Tower Guard Servant
                BUILDER.push("Tower Guard");
                TowerGuardHealth = BUILDER.comment("How much Max Health Tower Guards have, Default: 24.0")
                                .defineInRange("towerGuardHealth", 24.0, 1.0, Double.MAX_VALUE);
                TowerGuardArmor = BUILDER.comment("How much natural armor points Tower Guards have, Default: 20.0")
                                .defineInRange("towerGuardArmor", 20.0, 0.0, Double.MAX_VALUE);
                TowerGuardArmorToughness = BUILDER.comment("How much armor toughness Tower Guards have, Default: 12.0")
                                .defineInRange("towerGuardArmorToughness", 12.0, 0.0, Double.MAX_VALUE);
                TowerGuardDamage = BUILDER.comment("How much damage Tower Guards deals, Default: 11.0")
                                .defineInRange("towerGuardDamage", 11.0, 1.0, Double.MAX_VALUE);
                TowerGuardShieldCapacity = BUILDER.comment(
                                "How much damage Tower Guards' shield can absorb before breaking, Default: 10.0")
                                .defineInRange("towerGuardShieldCapacity", 10.0, 1.0, Double.MAX_VALUE);
                TowerGuardMovementSpeed = BUILDER.comment("How much movement speed Tower Guards have, Default: 0.30")
                                .defineInRange("towerGuardMovementSpeed", 0.30, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Silverfish Servant
                BUILDER.push("Silverfish Servant");
                SilverfishServantHealth = BUILDER.comment("How much Max Health Silverfish Servants have, Default: 8.0")
                                .defineInRange("silverfishServantHealth", 8.0, 1.0, Double.MAX_VALUE);
                SilverfishServantDamage = BUILDER.comment("How much damage Silverfish Servants deals, Default: 1.0")
                                .defineInRange("silverfishServantDamage", 1.0, 1.0, Double.MAX_VALUE);
                SilverfishServantArmor = BUILDER
                                .comment("How much natural armor points Silverfish Servants have, Default: 0.0")
                                .defineInRange("silverfishServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                SilverfishServantArmorToughness = BUILDER
                                .comment("How much armor toughness Silverfish Servants have, Default: 0.0")
                                .defineInRange("silverfishServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Caerbannog Rabbit Servant
                BUILDER.push("Caerbannog Rabbit Servant");
                CaerbannogRabbitServantHealth = BUILDER
                                .comment("How much Max Health Caerbannog Rabbit Servants have, Default: 3.0")
                                .defineInRange("caerbannogRabbitServantHealth", 3.0, 1.0, Double.MAX_VALUE);
                CaerbannogRabbitServantDamage = BUILDER
                                .comment("How much damage Caerbannog Rabbit Servants deals, Default: 8.0")
                                .defineInRange("caerbannogRabbitServantDamage", 8.0, 1.0, Double.MAX_VALUE);
                CaerbannogRabbitServantArmor = BUILDER
                                .comment("How much natural armor points Caerbannog Rabbit Servants have, Default: 8.0")
                                .defineInRange("caerbannogRabbitServantArmor", 8.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Mushroom Monstrosity
                BUILDER.push("Mushroom Monstrosity");
                MushroomMonstrosityHealth = BUILDER
                                .comment("How much Max Health Mooshroom Monstrosities have, Default: 1000.0")
                                .defineInRange("mushroomMonstrosityHealth", 1000.0, 1.0, Double.MAX_VALUE);
                MushroomMonstrosityArmor = BUILDER
                                .comment("How much natural armor points Mooshroom Monstrosities have, Default: 4.0")
                                .defineInRange("mushroomMonstrosityArmor", 4.0, 0.0, Double.MAX_VALUE);
                MushroomMonstrosityArmorToughness = BUILDER
                                .comment("How much armor toughness Mooshroom Monstrosities have, Default: 0.0")
                                .defineInRange("mushroomMonstrosityArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                MushroomMonstrosityDamage = BUILDER
                                .comment("How much damage Mooshroom Monstrosities deals, Default: 16.0")
                                .defineInRange("mushroomMonstrosityDamage", 16.0, 1.0, Double.MAX_VALUE);
                MushroomMonstrosityFollowRange = BUILDER
                                .comment("How much following/detection range Mooshroom Monstrosities have, Default: 48.0")
                                .defineInRange("mushroomMonstrosityFollowRange", 48.0, 1.0, 2048.0);
                MushroomMonstrosityDamageCap = BUILDER
                                .comment("Mooshroom Monstrosity damage cap as a percentage of max health (default: 0.25 = 25%)")
                                .defineInRange("mushroomMonstrosityDamageCap", 0.25, 0.0, 1.0);
                MushroomMonstrosityPercentageDamage = BUILDER
                                .comment("Mooshroom Monstrosity percentage-based damage (default: 0.08 = 8%)")
                                .defineInRange("mushroomMonstrosityPercentageDamage", 0.08, 0.0, 1.0);
                MushroomMonstrosityDynamicShieldDefaultLimitTime = BUILDER
                                .comment("Default time limit for Mooshroom Monstrosity dynamic shield in ticks (default: 5)")
                                .defineInRange("mushroomDynamicShieldDefaultLimitTime", 5, 0, Integer.MAX_VALUE);
                AllowPoisonousMushroomHealSpecialOwners = BUILDER
                                .comment("Should Poisonous Mushroom Block heal owner(Mooshroom Monstrosity)? Default: false")
                                .define("allowPoisonousMushroomHealSpecialOwners", false);
                AllowMushroomMonstrosityPlantPoisonousMushroom = BUILDER
                                .comment("Should Mooshroom Monstrosity be allowed to plant poisonous mushrooms? Default: true")
                                .define("allowMushroomMonstrosityPlantPoisonousMushroom", true);
                BUILDER.pop();

                // Angry Mooshroom
                BUILDER.push("Angry Mooshroom");
                AngryMooshroomHealth = BUILDER.comment("How much Max Health Angry Mooshrooms have, Default: 24.0")
                                .defineInRange("angryMooshroomHealth", 24.0, 1.0, Double.MAX_VALUE);
                AngryMooshroomArmor = BUILDER
                                .comment("How much armor points Angry Mooshrooms have, Default: 0.0")
                                .defineInRange("angryMooshroomArmor", 0.0, 0.0, Double.MAX_VALUE);
                AngryMooshroomDamage = BUILDER.comment("How much damage Angry Mooshrooms deals, Default: 6.0")
                                .defineInRange("angryMooshroomDamage", 6.0, 1.0, Double.MAX_VALUE);
                AngryMooshroomMovementSpeed = BUILDER
                                .comment("How much movement speed Angry Mooshrooms have, Default: 0.25")
                                .defineInRange("angryMooshroomMovementSpeed", 0.25, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Wraith Necromancer
                BUILDER.push("Wraith Necromancer");
                WraithNecromancerHealth = BUILDER
                                .comment("How much Max Health Wraith Necromancers have, Default: 220.0")
                                .defineInRange("wraithNecromancerHealth", 220.0, 1.0, Double.MAX_VALUE);
                WraithNecromancerArmor = BUILDER
                                .comment("How much natural armor points Wraith Necromancers have, Default: 0.0")
                                .defineInRange("wraithNecromancerArmor", 0.0, 0.0, Double.MAX_VALUE);
                WraithNecromancerDamage = BUILDER.comment("How much damage Wraith Necromancers deals, Default: 4.0")
                                .defineInRange("wraithNecromancerDamage", 4.0, 1.0, Double.MAX_VALUE);
                WraithNecromancerFollowRange = BUILDER
                                .comment("How much follow range Wraith Necromancers have, Default: 32.0")
                                .defineInRange("wraithNecromancerFollowRange", 32.0, 1.0, 2048.0);
                BUILDER.pop();

                BUILDER.push("Parched Necromancer");
                ParchedNecromancerHealth = BUILDER
                                .comment("How much Max Health Parched Necromancers have, Default: 220.0")
                                .defineInRange("parchedNecromancerHealth", 220.0, 1.0, Double.MAX_VALUE);
                ParchedNecromancerArmor = BUILDER
                                .comment("How much natural armor points Parched Necromancers have, Default: 4.0")
                                .defineInRange("parchedNecromancerArmor", 4.0, 0.0, Double.MAX_VALUE);
                ParchedNecromancerArmorToughness = BUILDER
                                .comment("How much armor toughness Parched Necromancers have, Default: 0.0")
                                .defineInRange("parchedNecromancerArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                ParchedNecromancerDamage = BUILDER.comment("How much damage Parched Necromancers deals, Default: 1.0")
                                .defineInRange("parchedNecromancerDamage", 1.0, 0.0, Double.MAX_VALUE);
                ParchedNecromancerFollowRange = BUILDER
                                .comment("How much follow range Parched Necromancers have, Default: 48.0")
                                .defineInRange("parchedNecromancerFollowRange", 48.0, 1.0, 2048.0);
                BUILDER.pop();

                BUILDER.push("Nameless One");
                NamelessOneHealth = BUILDER
                                .comment("How much Max Health Nameless One have, Default: 444.0")
                                .defineInRange("namelessOneHealth", 444.0, 1.0, Double.MAX_VALUE);
                NamelessOneArmor = BUILDER
                                .comment("How much natural armor points Nameless One have, Default: 8.0")
                                .defineInRange("namelessOneArmor", 8.0, 0.0, Double.MAX_VALUE);
                NamelessOneArmorToughness = BUILDER
                                .comment("How much armor toughness Nameless One have, Default: 4.0")
                                .defineInRange("namelessOneArmorToughness", 4.0, 0.0, Double.MAX_VALUE);

                NamelessOneDamage = BUILDER.comment("How much damage Nameless One deals, Default: 4.0")
                                .defineInRange("namelessOneDamage", 4.0, 0.0, Double.MAX_VALUE);
                NamelessOneFollowRange = BUILDER
                                .comment("How much follow range Nameless One have, Default: 48.0")
                                .defineInRange("namelessOneFollowRange", 48.0, 1.0, 2048.0);

                NamelessOneDamageCapPercent = BUILDER
                                .comment("Damage cap percentage for Nameless One (fraction of max health), Default: 0.2 (20%)")
                                .defineInRange("namelessOneDamageCapPercent", 0.2, 0.01, 1.0);
                NamelessOneHitCooldown = BUILDER
                                .comment("Hit cooldown in ticks for Nameless One damage cap, Default: 5")
                                .defineInRange("namelessOneHitCooldown", 5, 1, 100);
                NamelessOneDynamicReductionTime = BUILDER
                                .comment("Dynamic damage reduction time in ticks for Nameless One, Default: 10")
                                .defineInRange("namelessOneDynamicReductionTime", 10, 1, 100);

                NamelessOneHealInterval = BUILDER
                                .comment("Nameless One heal interval in ticks (default: 20 - 1 second)")
                                .defineInRange("namelessOneHealInterval", 20, 1, Integer.MAX_VALUE);

                NamelessOneHealAmount = BUILDER
                                .comment("Nameless One heal amount per interval (default: 1.0)")
                                .defineInRange("namelessOneHealAmount", 1.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Illusioner Servant
                BUILDER.push("Illusioner Servant");
                IllusionerServantHealth = BUILDER.comment("How much Max Health Illusioner Servants have, Default: 32.0")
                                .defineInRange("illusionerServantHealth", 32.0, 1.0, Double.MAX_VALUE);
                IllusionerServantArmor = BUILDER
                                .comment("How much natural armor points Illusioner Servants have, Default: 0.0")
                                .defineInRange("illusionerServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                IllusionerServantArmorToughness = BUILDER
                                .comment("How much armor toughness Illusioner Servants have, Default: 0.0")
                                .defineInRange("illusionerServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                IllusionerServantMovementSpeed = BUILDER
                                .comment("How much movement speed Illusioner Servants have, Default: 0.5")
                                .defineInRange("illusionerServantMovementSpeed", 0.5, 0.0, Double.MAX_VALUE);
                IllusionerServantFollowRange = BUILDER
                                .comment("How much following/detection range Illusioner Servants have, Default: 18.0")
                                .defineInRange("illusionerServantFollowRange", 18.0, 1.0, 2048.0);
                BUILDER.pop();

                // Arch Illusioner Servant
                BUILDER.push("Arch Illusioner Servant");
                ArchIllusionerServantHealth = BUILDER
                                .comment("How much Max Health Arch Illusioner Servants have, Default: 100.0")
                                .defineInRange("archIllusionerServantHealth", 100.0, 1.0, Double.MAX_VALUE);
                ArchIllusionerServantArmor = BUILDER
                                .comment("How much natural armor points Arch Illusioner Servants have, Default: 8.0")
                                .defineInRange("archIllusionerServantArmor", 8.0, 0.0, Double.MAX_VALUE);
                ArchIllusionerServantArmorToughness = BUILDER
                                .comment("How much armor toughness Arch Illusioner Servants have, Default: 4.0")
                                .defineInRange("archIllusionerServantArmorToughness", 4.0, 0.0, Double.MAX_VALUE);
                ArchIllusionerServantMovementSpeed = BUILDER
                                .comment("How much movement speed Arch Illusioner Servants have, Default: 0.5")
                                .defineInRange("archIllusionerServantMovementSpeed", 0.5, 0.0, Double.MAX_VALUE);
                ArchIllusionerServantFollowRange = BUILDER
                                .comment("How much following/detection range Arch Illusioner Servants have, Default: 48.0")
                                .defineInRange("archIllusionerServantFollowRange", 48.0, 1.0, 2048.0);
                ArchIllusionerDamage = BUILDER
                                .comment("How much attack damage Arch Illusioners have, affects Explosive Arrow damage, Default: 1.0")
                                .defineInRange("archIllusionerDamage", 1.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Bound Sorcerer
                BUILDER.push("Bound Sorcerer");
                BoundSorcererHealth = BUILDER
                                .comment("How much Max Health Bound Sorcerers have, Default: 24.0")
                                .defineInRange("boundSorcererHealth", 24.0, 1.0, Double.MAX_VALUE);
                BoundSorcererArmor = BUILDER
                                .comment("How much natural armor points Bound Sorcerers have, Default: 2.0")
                                .defineInRange("boundSorcererArmor", 2.0, 0.0, Double.MAX_VALUE);
                BoundSorcererFollowRange = BUILDER
                                .comment("How much following/detection range Bound Sorcerers have, Default: 32.0")
                                .defineInRange("boundSorcererFollowRange", 32.0, 1.0, 2048.0);
                BUILDER.pop();

                // Vanguard Champion
                BUILDER.push("Vanguard Champion");
                VanguardChampionHealth = BUILDER
                                .comment("How much Max Health Vanguard Champions have, Default: 24.0")
                                .defineInRange("vanguardChampionHealth", 24.0, 1.0, Double.MAX_VALUE);
                VanguardChampionMovementSpeed = BUILDER
                                .comment("How much movement speed Vanguard Champions have, Default: 0.3")
                                .defineInRange("vanguardChampionMovementSpeed", 0.3, 0.0, Double.MAX_VALUE);
                VanguardChampionDamage = BUILDER
                                .comment("How much base damage Vanguard Champions deals, Default: 1.0")
                                .defineInRange("vanguardChampionDamage", 1.0, 1.0, Double.MAX_VALUE);
                VanguardChampionAttackKnockback = BUILDER
                                .comment("How much attack knockback Vanguard Champions have, Default: 1.0")
                                .defineInRange("vanguardChampionAttackKnockback", 1.0, 0.0, Double.MAX_VALUE);
                VanguardChampionArmor = BUILDER
                                .comment("How much natural armor points Vanguard Champions have, Default: 20.0")
                                .defineInRange("vanguardChampionArmor", 20.0, 0.0, Double.MAX_VALUE);
                VanguardChampionArmorToughness = BUILDER
                                .comment("How much armor toughness Vanguard Champions have, Default: 8.0")
                                .defineInRange("vanguardChampionArmorToughness", 8.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Scarlet Vex
                BUILDER.push("Scarlet Vex");
                ScarletVexHealth = BUILDER.comment("How much Max Health Scarlet Vexes have, Default: 12.0")
                                .defineInRange("scarletVexHealth", 12.0, 1.0, Double.MAX_VALUE);
                ScarletVexDamage = BUILDER.comment("How much damage Scarlet Vexes deals, Default: 4.0")
                                .defineInRange("scarletVexDamage", 4.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Tower Wraith
                BUILDER.push("Tower Wraith");
                TowerWraithHealth = BUILDER.comment("How much Max Health Tower Wraiths have, Default: 30.0")
                                .defineInRange("towerWraithHealth", 30.0, 1.0, Double.MAX_VALUE);
                TowerWraithArmor = BUILDER.comment("How much natural armor points Tower Wraiths have, Default: 2.0")
                                .defineInRange("towerWraithArmor", 2.0, 0.0, Double.MAX_VALUE);
                TowerWraithDamage = BUILDER.comment("How much damage Tower Wraiths deals, Default: 3.0")
                                .defineInRange("towerWraithDamage", 3.0, 1.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Tower Witch
                BUILDER.push("Tower Witch");
                TowerWitchHealth = BUILDER.comment("How much Max Health Tower Witches have, Default: 30.0")
                                .defineInRange("towerWitchHealth", 30.0, 1.0, Double.MAX_VALUE);
                TowerWitchArmor = BUILDER.comment("How much natural armor points Tower Witches have, Default: 2.0")
                                .defineInRange("towerWitchArmor", 2.0, 0.0, Double.MAX_VALUE);
                TowerWitchMovementSpeed = BUILDER.comment("How much movement speed Tower Witches have, Default: 0.3")
                                .defineInRange("towerWitchMovementSpeed", 0.3, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Giant Ghast
                BUILDER.push("Giant Ghast");
                GiantGhastHealth = BUILDER.comment("How much Max Health Giant Ghast has, Default: 140.0")
                                .defineInRange("giantGhastHealth", 140.0, 1.0, Double.MAX_VALUE);
                GiantGhastArmor = BUILDER.comment("How much natural armor points Giant Ghast has, Default: 4.0")
                                .defineInRange("giantGhastArmor", 4.0, 0.0, Double.MAX_VALUE);
                GiantGhastArmorToughness = BUILDER.comment("How much armor toughness Giant Ghast has, Default: 0.0")
                                .defineInRange("giantGhastArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                GiantGhastDamage = BUILDER.comment("How much damage Giant Ghast deals, Default: 5.0")
                                .defineInRange("giantGhastDamage", 5.0, 0.0, Double.MAX_VALUE);
                GiantGhastAllowGiantHellBlast = BUILDER
                                .comment("Allow Giant Ghast to fire Giant Hell Blast, Default: false")
                                .define("giantGhastAllowGiantHellBlast", false);
                BUILDER.pop();

                // Rampart Captain
                BUILDER.push("Rampart Captain");
                RampartCaptainHealth = BUILDER.comment("How much Max Health Rampart Captains have, Default: 120.0")
                                .defineInRange("rampartCaptainHealth", 120.0, 1.0, Double.MAX_VALUE);
                RampartCaptainDamage = BUILDER.comment("How much damage Rampart Captains deals, Default: 6.5")
                                .defineInRange("rampartCaptainDamage", 6.5, 1.0, Double.MAX_VALUE);
                RampartCaptainArmor = BUILDER.comment("How much armor Rampart Captains have, Default: 20.0")
                                .defineInRange("rampartCaptainArmor", 20.0, 0.0, Double.MAX_VALUE);
                RampartCaptainArmorToughness = BUILDER
                                .comment("How much armor toughness Rampart Captains have, Default: 8.0")
                                .defineInRange("rampartCaptainArmorToughness", 8.0, 0.0, Double.MAX_VALUE);
                RampartCaptainRunAttackLoopChance = BUILDER
                                .comment("Chance for Rampart Captain's Run Attack to loop (0.0-1.0), Default: 0.7 (70%)")
                                .defineInRange("rampartCaptainRunAttackLoopChance", 0.7, 0.0, 1.0);
                BUILDER.pop();

                // Burning Shield
                BUILDER.push("Burning Shield");
                BurningShieldHealth = BUILDER.comment("How much Max Health Burning Shields have, Default: 12.5")
                                .defineInRange("burningShieldHealth", 12.5, 1.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Masquerader Servant
                BUILDER.push("Masquerader Servant");
                MasqueraderServantHealth = BUILDER
                                .comment("How much Max Health Masquerader Servants have, Default: 300.0")
                                .defineInRange("masqueraderServantHealth", 300.0, 1.0, Double.MAX_VALUE);
                MasqueraderServantMovementSpeed = BUILDER
                                .comment("How much movement speed Masquerader Servants have, Default: 0.4")
                                .defineInRange("masqueraderServantMovementSpeed", 0.4, 0.0, Double.MAX_VALUE);
                MasqueraderServantFollowRange = BUILDER
                                .comment("How much following/detection range Masquerader Servants have, Default: 64.0")
                                .defineInRange("masqueraderServantFollowRange", 64.0, 1.0, 2048.0);
                MasqueraderServantDamage = BUILDER.comment("How much damage Masquerader Servants deals, Default: 6.0")
                                .defineInRange("masqueraderServantDamage", 6.0, 1.0, Double.MAX_VALUE);
                MasqueraderServantArmor = BUILDER
                                .comment("How much natural armor Masquerader Servants have, Default: 0.0")
                                .defineInRange("masqueraderServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                MasqueraderServantArmorToughness = BUILDER
                                .comment("How much armor toughness Masquerader Servants have, Default: 0.0")
                                .defineInRange("masqueraderServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Swampjaw Servant
                BUILDER.push("Swampjaw Servant");
                SwampjawServantHealth = BUILDER
                                .comment("How much Max Health Swampjaw Servants have, Default: 100.0")
                                .defineInRange("swampjawServantHealth", 100.0, 1.0, Double.MAX_VALUE);
                SwampjawServantDamage = BUILDER
                                .comment("How much damage Swampjaw Servants deals, Default: 12.0")
                                .defineInRange("swampjawServantDamage", 12.0, 0.0, Double.MAX_VALUE);
                SwampjawServantKnockbackResistance = BUILDER
                                .comment("How much knockback resistance Swampjaw Servants have, Default: 0.8")
                                .defineInRange("swampjawServantKnockbackResistance", 0.8, 0.0, Double.MAX_VALUE);
                SwampjawServantExplosionPower = BUILDER
                                .comment("How much explosion power Swampjaw Servant's mines have, Default: 3.0")
                                .defineInRange("swampjawServantExplosionPower", 3.0, 0.0, Double.MAX_VALUE);
                SwampjawServantArmor = BUILDER
                                .comment("How much natural armor Swampjaw Servants have, Default: 0.0")
                                .defineInRange("swampjawServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                SwampjawServantArmorToughness = BUILDER
                                .comment("How much armor toughness Swampjaw Servants have, Default: 0.0")
                                .defineInRange("swampjawServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Bellringer Servant
                BUILDER.push("Bellringer Servant");
                BellringerServantHealth = BUILDER
                                .comment("How much Max Health Bellringer Servants have, Default: 200.0")
                                .defineInRange("bellringerServantHealth", 200.0, 1.0, Double.MAX_VALUE);
                BellringerServantDamage = BUILDER
                                .comment("How much damage Bellringer Servants deals, Default: 10.0")
                                .defineInRange("bellringerServantDamage", 10.0, 1.0, Double.MAX_VALUE);
                BellringerServantArmor = BUILDER
                                .comment("How much armor Bellringer Servants have, Default: 0.0")
                                .defineInRange("bellringerServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                BellringerServantArmorToughness = BUILDER
                                .comment("How much armor toughness Bellringer Servants have, Default: 0.0")
                                .defineInRange("bellringerServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Rosalyne Servant
                BUILDER.push("Rosalyne Servant");
                RosalyneServantHealth = BUILDER
                                .comment("How much Max Health Rosalyne Servants have, Default: 500.0")
                                .defineInRange("rosalyneServantHealth", 500.0, 1.0, Double.MAX_VALUE);
                RosalyneServantDamage = BUILDER
                                .comment("How much damage Rosalyne Servants deals, Default: 24.0")
                                .defineInRange("rosalyneServantDamage", 24.0, 1.0, Double.MAX_VALUE);
                RosalyneServantArmor = BUILDER
                                .comment("How much armor Rosalyne Servants have, Default: 8.0")
                                .defineInRange("rosalyneServantArmor", 8.0, 0.0, Double.MAX_VALUE);
                RosalyneServantArmorToughness = BUILDER
                                .comment("How much armor toughness Rosalyne Servants have, Default: 4.0")
                                .defineInRange("rosalyneServantArmorToughness", 4.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Rose Spirit Servant
                BUILDER.push("Rose Spirit Servant");
                RoseSpiritServantHealth = BUILDER
                                .comment("How much Max Health Rose Spirit Servants have, Default: 40.0")
                                .defineInRange("roseSpiritServantHealth", 40.0, 1.0, Double.MAX_VALUE);
                RoseSpiritServantDamage = BUILDER
                                .comment("How much damage Rose Spirit Servants deals, Default: 16.0")
                                .defineInRange("roseSpiritServantDamage", 16.0, 1.0, Double.MAX_VALUE);
                RoseSpiritServantArmor = BUILDER
                                .comment("How much armor Rose Spirit Servants have, Default: 5.0")
                                .defineInRange("roseSpiritServantArmor", 5.0, 0.0, Double.MAX_VALUE);
                RoseSpiritServantArmorToughness = BUILDER
                                .comment("How much armor toughness Rose Spirit Servants have, Default: 0.0")
                                .defineInRange("roseSpiritServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Dame Fortuna Servant
                BUILDER.push("Dame Fortuna Servant");
                DameFortunaServantHealth = BUILDER
                                .comment("How much Max Health Dame Fortuna Servants have, Default: 300.0")
                                .defineInRange("dameFortunaServantHealth", 300.0, 1.0, Double.MAX_VALUE);
                DameFortunaServantDamage = BUILDER
                                .comment("How much damage Dame Fortuna Servants deals, Default: 16.0")
                                .defineInRange("dameFortunaServantDamage", 16.0, 1.0, Double.MAX_VALUE);
                DameFortunaServantArmor = BUILDER
                                .comment("How much armor Dame Fortuna Servants have, Default: 5.0")
                                .defineInRange("dameFortunaServantArmor", 5.0, 0.0, Double.MAX_VALUE);
                DameFortunaServantArmorToughness = BUILDER
                                .comment("How much armor toughness Dame Fortuna Servants have, Default: 0.0")
                                .defineInRange("dameFortunaServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Archer Servant
                BUILDER.push("Archer Servant");
                ArcherServantHealth = BUILDER
                                .comment("How much Max Health Archer Servants have, Default: 24.0")
                                .defineInRange("archerServantHealth", 24.0, 1.0, Double.MAX_VALUE);
                ArcherServantDamage = BUILDER
                                .comment("How much damage Archer Servants deals, Default: 5.0")
                                .defineInRange("archerServantDamage", 5.0, 1.0, Double.MAX_VALUE);
                ArcherServantMovementSpeed = BUILDER
                                .comment("How much movement speed Archer Servants have, Default: 0.36")
                                .defineInRange("archerServantMovementSpeed", 0.36, 0.0, Double.MAX_VALUE);
                ArcherServantFollowRange = BUILDER
                                .comment("How much follow range Archer Servants have, Default: 40.0")
                                .defineInRange("archerServantFollowRange", 40.0, 1.0, 2048.0);
                ArcherServantArmor = BUILDER
                                .comment("How much natural armor points Archer Servants have, Default: 0.0")
                                .defineInRange("archerServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                ArcherServantArmorToughness = BUILDER
                                .comment("How much armor toughness Archer Servants have, Default: 0.0")
                                .defineInRange("archerServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Skirmisher Servant
                BUILDER.push("Skirmisher Servant");
                SkirmisherServantHealth = BUILDER
                                .comment("How much Max Health Skirmisher Servants have, Default: 24.0")
                                .defineInRange("skirmisherServantHealth", 24.0, 1.0, Double.MAX_VALUE);
                SkirmisherServantDamage = BUILDER
                                .comment("How much damage Skirmisher Servants deals, Default: 5.0")
                                .defineInRange("skirmisherServantDamage", 5.0, 1.0, Double.MAX_VALUE);
                SkirmisherServantMovementSpeed = BUILDER
                                .comment("How much movement speed Skirmisher Servants have, Default: 0.36")
                                .defineInRange("skirmisherServantMovementSpeed", 0.36, 0.0, Double.MAX_VALUE);
                SkirmisherServantFollowRange = BUILDER
                                .comment("How much follow range Skirmisher Servants have, Default: 16.0")
                                .defineInRange("skirmisherServantFollowRange", 16.0, 1.0, 2048.0);
                SkirmisherServantArmor = BUILDER
                                .comment("How much natural armor points Skirmisher Servants have, Default: 0.0")
                                .defineInRange("skirmisherServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                SkirmisherServantArmorToughness = BUILDER
                                .comment("How much armor toughness Skirmisher Servants have, Default: 0.0")
                                .defineInRange("skirmisherServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Legioner Servant
                BUILDER.push("Legioner Servant");
                LegionerServantHealth = BUILDER
                                .comment("How much Max Health Legioner Servants have, Default: 40.0")
                                .defineInRange("legionerServantHealth", 40.0, 1.0, Double.MAX_VALUE);
                LegionerServantDamage = BUILDER
                                .comment("How much damage Legioner Servants deals, Default: 6.5")
                                .defineInRange("legionerServantDamage", 6.5, 1.0, Double.MAX_VALUE);
                LegionerServantMovementSpeed = BUILDER
                                .comment("How much movement speed Legioner Servants have, Default: 0.275")
                                .defineInRange("legionerServantMovementSpeed", 0.275, 0.0, Double.MAX_VALUE);
                LegionerServantFollowRange = BUILDER
                                .comment("How much follow range Legioner Servants have, Default: 24.0")
                                .defineInRange("legionerServantFollowRange", 24.0, 1.0, 2048.0);
                LegionerServantArmor = BUILDER
                                .comment("How much natural armor Legioner Servants have, Default: 6.0")
                                .defineInRange("legionerServantArmor", 6.0, 0.0, Double.MAX_VALUE);
                LegionerServantArmorToughness = BUILDER
                                .comment("How much armor toughness Legioner Servants have, Default: 0.0")
                                .defineInRange("legionerServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Sculk Centipede Servant
                BUILDER.push("Sculk Centipede Servant");
                SculkCentipedeServantHealth = BUILDER
                                .comment("How much Max Health Sculk Centipede Servants have, Default: 20.0")
                                .defineInRange("sculkCentipedeServantHealth", 20.0, 1.0, Double.MAX_VALUE);
                SculkCentipedeServantDamage = BUILDER
                                .comment("How much damage Sculk Centipede Servants deals, Default: 3.0")
                                .defineInRange("sculkCentipedeServantDamage", 3.0, 1.0, Double.MAX_VALUE);
                SculkCentipedeServantMovementSpeed = BUILDER
                                .comment("How much movement speed Sculk Centipede Servants have, Default: 0.2")
                                .defineInRange("sculkCentipedeServantMovementSpeed", 0.2, 0.0, Double.MAX_VALUE);
                SculkCentipedeServantArmor = BUILDER
                                .comment("How much natural armor Sculk Centipede Servants have, Default: 0.0")
                                .defineInRange("sculkCentipedeServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                SculkCentipedeServantArmorToughness = BUILDER
                                .comment("How much armor toughness Sculk Centipede Servants have, Default: 0.0")
                                .defineInRange("sculkCentipedeServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Sculk Leech Servant
                BUILDER.push("Sculk Leech Servant");
                SculkLeechServantHealth = BUILDER
                                .comment("How much Max Health Sculk Leech Servants have, Default: 4.0")
                                .defineInRange("sculkLeechServantHealth", 4.0, 1.0, Double.MAX_VALUE);
                SculkLeechServantDamage = BUILDER
                                .comment("How much damage Sculk Leech Servants deals, Default: 1.0")
                                .defineInRange("sculkLeechServantDamage", 1.0, 1.0, Double.MAX_VALUE);
                SculkLeechServantMovementSpeed = BUILDER
                                .comment("How much movement speed Sculk Leech Servants have, Default: 0.25")
                                .defineInRange("sculkLeechServantMovementSpeed", 0.25, 0.0, Double.MAX_VALUE);
                SculkLeechServantArmor = BUILDER
                                .comment("How much natural armor Sculk Leech Servants have, Default: 0.0")
                                .defineInRange("sculkLeechServantArmor", 0.0, 0.0, Double.MAX_VALUE);
                SculkLeechServantArmorToughness = BUILDER
                                .comment("How much armor toughness Sculk Leech Servants have, Default: 0.0")
                                .defineInRange("sculkLeechServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Shattered Servant
                BUILDER.push("Shattered Servant");
                ShatteredServantHealth = BUILDER
                                .comment("How much Max Health Shattered Servants have, Default: 50.0")
                                .defineInRange("shatteredServantHealth", 50.0, 1.0, Double.MAX_VALUE);
                ShatteredServantDamage = BUILDER
                                .comment("How much damage Shattered Servants deals, Default: 6.0")
                                .defineInRange("shatteredServantDamage", 6.0, 1.0, Double.MAX_VALUE);
                ShatteredServantMovementSpeed = BUILDER
                                .comment("How much movement speed Shattered Servants have, Default: 0.2")
                                .defineInRange("shatteredServantMovementSpeed", 0.2, 0.0, Double.MAX_VALUE);
                ShatteredServantArmor = BUILDER
                                .comment("How much natural armor Shattered Servants have, Default: 3.5")
                                .defineInRange("shatteredServantArmor", 3.5, 0.0, Double.MAX_VALUE);
                ShatteredServantFollowRange = BUILDER
                                .comment("How much follow range Shattered Servants have, Default: 10.0")
                                .defineInRange("shatteredServantFollowRange", 10.0, 1.0, 2048.0);
                ShatteredServantArmorToughness = BUILDER
                                .comment("How much armor toughness Shattered Servants have, Default: 0.0")
                                .defineInRange("shatteredServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Shriek Worm Servant
                BUILDER.push("Shriek Worm Servant");
                ShriekWormServantHealth = BUILDER
                                .comment("How much Max Health Shriek Worm Servants have, Default: 100.0")
                                .defineInRange("shriekWormServantHealth", 100.0, 1.0, Double.MAX_VALUE);
                ShriekWormServantDamage = BUILDER
                                .comment("How much damage Shriek Worm Servants deals, Default: 7.0")
                                .defineInRange("shriekWormServantDamage", 7.0, 1.0, Double.MAX_VALUE);
                ShriekWormServantMovementSpeed = BUILDER
                                .comment("How much movement speed Shriek Worm Servants have, Default: 0.0")
                                .defineInRange("shriekWormServantMovementSpeed", 0.0, 0.0, Double.MAX_VALUE);
                ShriekWormServantArmor = BUILDER
                                .comment("How much natural armor Shriek Worm Servants have, Default: 3.5")
                                .defineInRange("shriekWormServantArmor", 3.5, 0.0, Double.MAX_VALUE);
                ShriekWormServantArmorToughness = BUILDER
                                .comment("How much armor toughness Shriek Worm Servants have, Default: 0.0")
                                .defineInRange("shriekWormServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Sludge Servant
                BUILDER.push("Sludge Servant");
                SludgeServantHealth = BUILDER
                                .comment("How much Max Health Sludge Servants have (per size), Default: 16.0")
                                .defineInRange("sludgeServantHealth", 16.0, 1.0, Double.MAX_VALUE);
                SludgeServantDamage = BUILDER
                                .comment("How much damage Sludge Servants deals, Default: 4.0")
                                .defineInRange("sludgeServantDamage", 4.0, 1.0, Double.MAX_VALUE);
                SludgeServantMovementSpeed = BUILDER
                                .comment("How much movement speed Sludge Servants have, Default: 0.6")
                                .defineInRange("sludgeServantMovementSpeed", 0.6, 0.0, Double.MAX_VALUE);
                SludgeServantArmor = BUILDER
                                .comment("How much natural armor Sludge Servants have, Default: 2.5")
                                .defineInRange("sludgeServantArmor", 2.5, 0.0, Double.MAX_VALUE);
                SludgeServantArmorToughness = BUILDER
                                .comment("How much armor toughness Sludge Servants have, Default: 0.0")
                                .defineInRange("sludgeServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Stalker Servant
                BUILDER.push("Stalker Servant");
                StalkerServantHealth = BUILDER
                                .comment("How much Max Health Stalker Servants have, Default: 200.0")
                                .defineInRange("stalkerServantHealth", 200.0, 1.0, Double.MAX_VALUE);
                StalkerServantDamage = BUILDER
                                .comment("How much damage Stalker Servants deals, Default: 22.0")
                                .defineInRange("stalkerServantDamage", 22.0, 1.0, Double.MAX_VALUE);
                StalkerServantMovementSpeed = BUILDER
                                .comment("How much movement speed Stalker Servants have, Default: 0.3")
                                .defineInRange("stalkerServantMovementSpeed", 0.3, 0.0, Double.MAX_VALUE);
                StalkerServantArmor = BUILDER
                                .comment("How much natural armor Stalker Servants have, Default: 4.0")
                                .defineInRange("stalkerServantArmor", 4.0, 0.0, Double.MAX_VALUE);
                StalkerServantKnockbackResistance = BUILDER
                                .comment("How much knockback resistance Stalker Servants have, Default: 1.0")
                                .defineInRange("stalkerServantKnockbackResistance", 1.0, 0.0, 1.0);
                StalkerServantArmorToughness = BUILDER
                                .comment("How much armor toughness Stalker Servants have, Default: 0.0")
                                .defineInRange("stalkerServantArmorToughness", 0.0, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                BUILDER.pop();
                SPEC = BUILDER.build();
        }

        public static void loadConfig(ForgeConfigSpec config, String path) {
                File configFile = new File(path);
                File configDir = configFile.getParentFile();
                if (configDir != null && !configDir.exists()) {
                        configDir.mkdirs();
                }
                final CommentedFileConfig file = CommentedFileConfig.builder(configFile)
                                .sync()
                                .autosave()
                                .writingMode(WritingMode.REPLACE)
                                .build();
                file.load();
                config.setConfig(file);
        }
}