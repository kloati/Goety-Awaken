package com.k1sak1.goetyawaken;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import java.io.File;
import java.util.List;
import java.util.Arrays;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
        private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        static {
                BUILDER.push("Servant Limits");
        }

        private static final ForgeConfigSpec.IntValue PALE_GOLEM_LIMIT = BUILDER
                        .comment("Pale Golem Servant limit per player")
                        .defineInRange("paleGolemLimit", 4, 1, Integer.MAX_VALUE);

        private static final ForgeConfigSpec.IntValue WARDEN_SERVANT_LIMIT = BUILDER
                        .comment("Warden Servant limit per player")
                        .defineInRange("wardenServantLimit", 2, 2, Integer.MAX_VALUE);

        private static final ForgeConfigSpec.IntValue WIGHT_SERVANT_LIMIT = BUILDER
                        .comment("Wight Servant limit per player (default: 4)")
                        .defineInRange("wightServantLimit", 4, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue CREEPER_SERVANT_LIMIT = BUILDER
                        .comment("Creeper Servant limit per player (default: 32)")
                        .defineInRange("creeperServantLimit", 32, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue CAERBANNOG_RABBIT_SERVANT_LIMIT = BUILDER
                        .comment("Caerbannog Rabbit Servant limit per player (default: 16)")
                        .defineInRange("caerbannogRabbitServantLimit", 16, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue WRAITH_NECROMANCER_LIMIT = BUILDER
                        .comment("Wraith Necromancer limit per player (default: 2)")
                        .defineInRange("wraithNecromancerLimit", 2, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue RUINS_NECROMANCER_LIMIT = BUILDER
                        .comment("Ruins Necromancer limit per player (default: 2)")
                        .defineInRange("ruinsNecromancerLimit", 2, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue NAMELESS_ONE_LIMIT = BUILDER
                        .comment("Nameless One limit per player (default: 1)")
                        .defineInRange("namelessOneLimit", 1, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue ENDERMAN_SERVANT_LIMIT = BUILDER
                        .comment("Enderman Servant limit per player (default: 16)")
                        .defineInRange("endermanServantLimit", 16, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue TORMENTOR_SERVANT_LIMIT = BUILDER
                        .comment("Tormentor Servant limit per player (default: 8)")
                        .defineInRange("tormentorServantLimit", 8, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue SHULKER_SERVANT_LIMIT = BUILDER
                        .comment("Shulker Servant limit per player (default: 8)")
                        .defineInRange("shulkerServantLimit", 8, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue WITHER_SERVANT_LIMIT = BUILDER
                        .comment("Wither Servant limit per player (default: 2)")
                        .defineInRange("witherServantLimit", 2, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue ENDER_KEEPER_SERVANT_LIMIT = BUILDER
                        .comment("Ender Keeper Servant limit per player (default: 1)")
                        .defineInRange("enderKeeperServantLimit", 2, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue APOSTLE_SERVANT_LIMIT = BUILDER
                        .comment("Apostle Servant limit per player (default: 2)")
                        .defineInRange("apostleServantLimit", 2, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue MUSHROOM_MONSTROSITY_LIMIT = BUILDER
                        .comment("Mooshroom Monstrosity limit per player (default: 1)")
                        .defineInRange("mushroomMonstrosityLimit", 1, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue VANGUARD_CHAMPION_LIMIT = BUILDER
                        .comment("Vanguard Champion limit per player (default: 7)")
                        .defineInRange("vanguardChampionLimit", 4, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue ANGRY_MOOSHROOM_LIMIT = BUILDER
                        .comment("Angry Mooshroom limit per player (default: 16)")
                        .defineInRange("angryMooshroomLimit", 16, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue BURNING_SHIELD_LIMIT = BUILDER
                        .comment("Burning Shield limit per player (default: 12)")
                        .defineInRange("burningShieldLimit", 12, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue GIANT_SERVANT_LIMIT = BUILDER
                        .comment("Giant Servant limit per player (default: 2)")
                        .defineInRange("giantServantLimit", 2, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue TOXIFIN_SERVANT_LIMIT = BUILDER
                        .comment("Toxifin Servant limit per player (default: 16)")
                        .defineInRange("toxifinServantLimit", 16, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue PLAGUEWHALE_SLAB_SERVANT_LIMIT = BUILDER
                        .comment("Plaguewhale Slab Servant limit per player (default: 4)")
                        .defineInRange("plaguewhaleSlabServantLimit", 4, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue MAID_FAIRY_SERVANT_LIMIT = BUILDER
                        .comment("Maid Fairy Servant limit per player (default: 64)")
                        .defineInRange("maidFairyServantLimit", 64, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue MASQUERADER_SERVANT_LIMIT = BUILDER
                        .comment("Masquerader Servant limit per player (default: 2)")
                        .defineInRange("masqueraderServantLimit", 2, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue SWAMPJAW_SERVANT_LIMIT = BUILDER
                        .comment("Swampjaw Servant limit per player (default: 4)")
                        .defineInRange("swampjawServantLimit", 4, 4, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue BELLRINGER_SERVANT_LIMIT = BUILDER
                        .comment("Bellringer Servant limit per player (default: 4)")
                        .defineInRange("bellringerServantLimit", 4, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue ROSALYNE_SERVANT_LIMIT = BUILDER
                        .comment("Rosalyne Servant limit per player (default: 2)")
                        .defineInRange("rosalyneServantLimit", 2, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue DAME_FORTUNA_SERVANT_LIMIT = BUILDER
                        .comment("Dame Fortuna Servant limit per player (default: 3)")
                        .defineInRange("dameFortunaServantLimit", 3, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue SCULK_CENTIPEDE_SERVANT_LIMIT = BUILDER
                        .comment("Sculk Centipede Servant limit per player (default: 24)")
                        .defineInRange("sculkCentipedeServantLimit", 24, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue SCULK_LEECH_SERVANT_LIMIT = BUILDER
                        .comment("Sculk Leech Servant limit per player (default: 64)")
                        .defineInRange("sculkLeechServantLimit", 64, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue SHATTERED_SERVANT_LIMIT = BUILDER
                        .comment("Shattered Servant limit per player (default: 8)")
                        .defineInRange("shatteredServantLimit", 8, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue SHRIEK_WORM_SERVANT_LIMIT = BUILDER
                        .comment("Shriek Worm Servant limit per player (default: 4)")
                        .defineInRange("shriekWormServantLimit", 4, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue SLUDGE_SERVANT_LIMIT = BUILDER
                        .comment("Sludge Servant limit per player (default: 32)")
                        .defineInRange("sludgeServantLimit", 32, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue STALKER_SERVANT_LIMIT = BUILDER
                        .comment("Stalker Servant limit per player (default: 3)")
                        .defineInRange("stalkerServantLimit", 3, 1, Integer.MAX_VALUE);

        static {
                BUILDER.pop();
                BUILDER.push("Apostle Settings");
        }

        public static final ForgeConfigSpec.BooleanValue CALLBACK_APOSTLE = BUILDER
                        .comment("Callback Apostle feature - enables pre-nerf apostle abilities (default: true)")
                        .define("callbackApostle", true);

        public static final ForgeConfigSpec.BooleanValue APOSTLE_SUMMON_ARMORED_ZOMBIE_PIGLIN = BUILDER
                        .comment("Allow Apostles to summon armored Zombie Piglin Brutes (default: false)")
                        .define("apostleSummonArmoredZombiePiglin", false);

        public static final ForgeConfigSpec.BooleanValue ENABLE_APOSTLE_SERVANT_ARMOR_RENDERER = BUILDER
                        .comment("Enable Apostle Servant Armor Renderer(default: true)")
                        .define("enableApostleServantArmorRenderer", true);

        public static final ForgeConfigSpec.BooleanValue APOSTLE_SECOND_PHASE_THUNDER_STORM = BUILDER
                        .comment("Enable thunder storm weather when Apostle Servant enters second phase (default: true)")
                        .define("apostleSecondPhaseThunderStorm", true);

        static {
                BUILDER.push("Upgrade Conditions");
        }

        public static final ForgeConfigSpec.DoubleValue UPGRADE_HEAL_MULTIPLIER = BUILDER
                        .comment("Apostle title 0 (the Risen): heal amount required, multiplied by servant max health (default: 1000.0)")
                        .defineInRange("apostleUpgradeHealMultiplier", 1000.0, 1.0, 1.0E9);

        public static final ForgeConfigSpec.DoubleValue UPGRADE_DAMAGE_MULTIPLIER = BUILDER
                        .comment("Apostle title 1 (the Abhorrent): damage dealt required, multiplied by servant max health (default: 1000.0)")
                        .defineInRange("apostleUpgradeDamageMultiplier", 1000.0, 1.0, 1.0E9);

        public static final ForgeConfigSpec.IntValue UPGRADE_BLIGHT_KILLS = BUILDER
                        .comment("Apostle title 2 (the Defiler): poisoned entity kills required (default: 100)")
                        .defineInRange("apostleUpgradeBlightKills", 100, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue UPGRADE_WITHER_DAMAGE = BUILDER
                        .comment("Apostle title 3 (the Dark): damage dealt to Wither required (default: 900)")
                        .defineInRange("apostleUpgradeWitherDamage", 900, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue UPGRADE_WITHER_KILLS = BUILDER
                        .comment("Apostle title 3 (the Dark): Wither kills required (default: 3)")
                        .defineInRange("apostleUpgradeWitherKills", 3, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue UPGRADE_WARDEN_DAMAGE = BUILDER
                        .comment("Apostle title 4 (the Great Shadow): damage dealt to Warden required (default: 1000)")
                        .defineInRange("apostleUpgradeWardenDamage", 1000, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue UPGRADE_WARDEN_KILLS = BUILDER
                        .comment("Apostle title 4 (the Great Shadow): Warden kills required (default: 2)")
                        .defineInRange("apostleUpgradeWardenKills", 2, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue UPGRADE_POSITIVE_EFFECTS = BUILDER
                        .comment("Apostle title 5 (the Witch King): beneficial effects count required (default: 30)")
                        .defineInRange("apostleUpgradePositiveEffects", 30, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue UPGRADE_NEGATIVE_EFFECTS = BUILDER
                        .comment("Apostle title 5 (the Witch King): harmful effects count required (default: 20)")
                        .defineInRange("apostleUpgradeNegativeEffects", 20, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue UPGRADE_BLAZE_KILLS = BUILDER
                        .comment("Apostle title 6 (the Pyre Lord): Blaze kills required (default: 100)")
                        .defineInRange("apostleUpgradeBlazeKills", 100, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue UPGRADE_TRADING_EMERALDS = BUILDER
                        .comment("Apostle title 7 (the Profane): emeralds earned through trading required (default: 4096)")
                        .defineInRange("apostleUpgradeTradingEmeralds", 4096, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue UPGRADE_FROZEN_DAMAGE_MULTIPLIER = BUILDER
                        .comment("Apostle title 8 (the Cruel): frozen damage required, multiplied by servant max health (default: 10.0)")
                        .defineInRange("apostleUpgradeFrozenDamageMultiplier", 10.0, 1.0, 1.0E9);

        public static final ForgeConfigSpec.IntValue UPGRADE_SWIFT_TICKS = BUILDER
                        .comment("Apostle title 9 (the Terrible): ticks spent with Swiftness II+ required (default: 120000)")
                        .defineInRange("apostleUpgradeSwiftTicks", 120000, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue UPGRADE_FOLLOWER_COUNT = BUILDER
                        .comment("Apostle title 10 (the Glorious): follower count required (default: 64)")
                        .defineInRange("apostleUpgradeFollowerCount", 64, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue UPGRADE_VILLAGER_KILLS = BUILDER
                        .comment("Apostle title 11 (the Atrocious): villager kills required (default: 666)")
                        .defineInRange("apostleUpgradeVillagerKills", 666, 1, Integer.MAX_VALUE);

        static {
                BUILDER.pop();
                BUILDER.pop();
                BUILDER.push("Boss & Music Settings");
        }

        public static final ForgeConfigSpec.BooleanValue WRAITH_NECROMANCER_BOSS_MUSIC = BUILDER
                        .comment("Enable boss music for Wraith Necromancer. Default: true")
                        .define("wraithNecromancerBossMusic", true);

        public static final ForgeConfigSpec.BooleanValue WRAITH_NECROMANCER_LEGACY_MUSIC = BUILDER
                        .comment("Enable legacy music for Wraith Necromancer. Default: false")
                        .define("wraithNecromancerLegacyMusic", false);

        public static final ForgeConfigSpec.BooleanValue MUSHROOM_MONSTROSITY_BOSS_MUSIC = BUILDER
                        .comment("Enable boss music for Mooshroom Monstrosity. Default: true")
                        .define("mushroomMonstrosityBossMusic", true);

        public static final ForgeConfigSpec.BooleanValue PARCHED_NECROMANCER_BOSS_MUSIC = BUILDER
                        .comment("Enable boss music for Parched Necromancer. Default: true")
                        .define("parchedNecromancerBossMusic", true);

        public static final ForgeConfigSpec.BooleanValue NAMELESS_ONE_BOSS_MUSIC = BUILDER
                        .comment("Enable boss music for Nameless One. Default: true")
                        .define("namelessOneBossMusic", true);

        public static final ForgeConfigSpec.BooleanValue RUBY_SORCERER_BOSS_MUSIC = BUILDER
                        .comment("Enable boss music for Ruby Sorcerer. Default: true")
                        .define("rubySorcererBossMusic", true);

        public static final ForgeConfigSpec.BooleanValue RAMPART_CAPTAIN_BOSS_MUSIC = BUILDER
                        .comment("Enable boss music for Rampart Captain. Default: true")
                        .define("rampartCaptainBossMusic", true);

        public static final ForgeConfigSpec.BooleanValue DROWNED_NECROMANCER_BOSS_MUSIC = BUILDER
                        .comment("Enable boss music for Drowned Necromancer. Default: true")
                        .define("drownedNecromancerBossMusic", true);

        public static final ForgeConfigSpec.BooleanValue ARCH_ILLUSIONER_BOSS_MUSIC = BUILDER
                        .comment("Enable boss music for Arch Illusioner. Default: true")
                        .define("archIllusionerBossMusic", true);

        public static final ForgeConfigSpec.BooleanValue NAMELESS_ONE_EASY_MODE = BUILDER
                        .comment("Enable Easy mode for Nameless One (which is more like Minecraft Dungeons). Default: false")
                        .define("namelessOneEasyMode", false);

        public static final ForgeConfigSpec.BooleanValue NAMELESS_ONE_MULTI_SHOT = BUILDER
                        .comment("Allow Nameless One to fire multiple NamelessBolts at once (spread shot). Default: false")
                        .define("namelessOneMultiShot", false);

        public static final ForgeConfigSpec.BooleanValue ENABLE_NAMELESS_ONE_DESERT_PLAGUES = BUILDER
                        .comment("Enable Desert Plagues Spell for Nameless One. Default: false")
                        .define("enableNamelessOneDesertPlagues", false);

        public static final ForgeConfigSpec.BooleanValue ENABLE_NAMELESS_ONE_AVADA = BUILDER
                        .comment("Enable Avada Spell for Nameless One. Default: false")
                        .define("enableNamelessOneAvada", false);

        public static final ForgeConfigSpec.BooleanValue ENABLE_NAMELESS_ONE_THUNDERSTORM = BUILDER
                        .comment("Enable Thunderstorm Spell for Nameless One. Default: true")
                        .define("enableNamelessOneThunderstorm", true);

        public static final ForgeConfigSpec.BooleanValue ENABLE_NAMELESS_ONE_HEAVEN_RIFT = BUILDER
                        .comment("Enable Heaven Rift Spell for Nameless One. Default: false")
                        .define("enableNamelessOneHeavenRift", false);

        public static final ForgeConfigSpec.BooleanValue ENABLE_NAMELESS_ONE_ENCHANTMENT = BUILDER
                        .comment("Enable Enchantment Spell (variant) for Nameless One. Default: true")
                        .define("enableNamelessOneEnchantment", true);

        public static final ForgeConfigSpec.BooleanValue ENABLE_NAMELESS_ONE_SUPPORT_SPELLS = BUILDER
                        .comment("Enable Support and Buff Spells for Nameless One. Default: true")
                        .define("enableNamelessOneSupportSpells", true);

        public static final ForgeConfigSpec.BooleanValue ENABLE_NAMELESS_ONE_NEW_TEXTURE = BUILDER
                        .comment("Enable new texture for Ally Nameless One. Default: true")
                        .define("enableNamelessOneNewTexture", true);

        public static final ForgeConfigSpec.BooleanValue ENABLE_NAMELESS_ONE_SCARLET_VEX_SUMMON = BUILDER
                        .comment("Enable Scarlet Vex summoning ability for Nameless One. Default: false")
                        .define("enableNamelessOneScarletVexSummon", false);

        public static final ForgeConfigSpec.BooleanValue ENABLE_NAMELESS_BOLT_DEATH_FIRE = BUILDER
                        .comment("Enable Death Fire creation for Nameless Bolt projectile. Default: false")
                        .define("enableNamelessBoltDeathFire", false);

        public static final ForgeConfigSpec.BooleanValue ENABLE_HOSTILE_NAMELESS_ONE_NEW_TEXTURE = BUILDER
                        .comment("Enable new texture for Hostile Nameless One. Default: false")
                        .define("enableHostileNamelessOneNewTexture", false);

        public static final ForgeConfigSpec.BooleanValue MUSHROOM_MONSTROSITY_EASY_MODE = BUILDER
                        .comment("Enable Easy mode for Mooshroom Monstrosity (which is more like Minecraft Dungeons). Default: false")
                        .define("mushroomMonstrosityEasyMode", false);

        static {
                BUILDER.pop();
                BUILDER.push("Trade");
        }

        public static final ForgeConfigSpec.IntValue SORCERER_SERVANT_RESTOCK_LIMIT = BUILDER
                        .comment("Sorcerer Servant maximum restocks per day (default: 2)")
                        .defineInRange("sorcererServantRestockLimit", 2, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue SORCERER_SERVANT_RESTOCK_INTERVAL = BUILDER
                        .comment("Sorcerer Servant minimum interval between restocks in ticks (default: 2400 = 2 minutes)")
                        .defineInRange("sorcererServantRestockInterval", 2400, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue SORCERER_SERVANT_RESTOCK_COOLDOWN = BUILDER
                        .comment("Sorcerer Servant cooldown after restocking in ticks (default: 12000 = 10 minutes)")
                        .defineInRange("sorcererServantRestockCooldown", 12000, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue SORCERER_SERVANT_TRADE_MAX_USES = BUILDER
                        .comment("Sorcerer Servant maximum uses per trade offer (default: 4)")
                        .defineInRange("sorcererServantTradeMaxUses", 4, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue HERESIARCH_SERVANT_TRADE_MAX_USES = BUILDER
                        .comment("Heresiarch Servant maximum uses per trade offer (default: 4)")
                        .defineInRange("heresiarchServantTradeMaxUses", 4, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue HERESIARCH_SERVANT_TRADE_COUNT = BUILDER
                        .comment("Number of trade offers generated for Heresiarch Servant (default: 10)")
                        .defineInRange("heresiarchServantTradeCount", 10, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue HERESIARCH_SERVANT_RESTOCK_LIMIT = BUILDER
                        .comment("Heresiarch Servant maximum restocks per day (default: 2)")
                        .defineInRange("heresiarchServantRestockLimit", 2, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue HERESIARCH_SERVANT_RESTOCK_INTERVAL = BUILDER
                        .comment("Heresiarch Servant minimum interval between restocks in ticks (default: 2400 = 2 minutes)")
                        .defineInRange("heresiarchServantRestockInterval", 2400, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue HERESIARCH_SERVANT_RESTOCK_COOLDOWN = BUILDER
                        .comment("Heresiarch Servant cooldown after restocking in ticks (default: 12000 = 10 minutes)")
                        .defineInRange("heresiarchServantRestockCooldown", 12000, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue CRONE_SERVANT_TRADE_MAX_USES = BUILDER
                        .comment("Crone Servant maximum uses per trade offer (default: 4)")
                        .defineInRange("croneServantTradeMaxUses", 4, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue CRONE_SERVANT_TRADE_COUNT = BUILDER
                        .comment("Number of trade offers generated for Crone Servant (default: 10)")
                        .defineInRange("croneServantTradeCount", 10, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue CRONE_SERVANT_RESTOCK_LIMIT = BUILDER
                        .comment("Crone Servant maximum restocks per day (default: 2)")
                        .defineInRange("croneServantRestockLimit", 2, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue CRONE_SERVANT_RESTOCK_INTERVAL = BUILDER
                        .comment("Crone Servant minimum interval between restocks in ticks (default: 2400 = 2 minutes)")
                        .defineInRange("croneServantRestockInterval", 2400, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue CRONE_SERVANT_RESTOCK_COOLDOWN = BUILDER
                        .comment("Crone Servant cooldown after restocking in ticks (default: 12000 = 10 minutes)")
                        .defineInRange("croneServantRestockCooldown", 12000, 0, Integer.MAX_VALUE);

        static {
                BUILDER.pop();
                BUILDER.push("Focus Spells");
        }

        static {
                BUILDER.push("Fairy Focus");
        }

        public static final ForgeConfigSpec.IntValue FAIRY_FOCUS_SOUL_COST = BUILDER
                        .comment("Fairy Focus soul cost (default: 4)")
                        .defineInRange("fairyFocusSoulCost", 4, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue FAIRY_FOCUS_CAST_DURATION = BUILDER
                        .comment("Fairy Focus cast duration in ticks (default: 40 - 2 seconds)")
                        .defineInRange("fairyFocusCastDuration", 40, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue FAIRY_FOCUS_COOLDOWN = BUILDER
                        .comment("Fairy Focus cooldown in ticks (default: 80 - 4 seconds)")
                        .defineInRange("fairyFocusCooldown", 80, 0, Integer.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Creeper Focus");
        }

        public static final ForgeConfigSpec.IntValue CREEPER_FOCUS_SOUL_COST = BUILDER
                        .comment("Creeper Focus soul cost (default: 32)")
                        .defineInRange("creeperFocusSoulCost", 32, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue CREEPER_FOCUS_CAST_DURATION = BUILDER
                        .comment("Creeper Focus cast duration in ticks (default: 40)")
                        .defineInRange("creeperFocusCastDuration", 40, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue CREEPER_FOCUS_COOLDOWN = BUILDER
                        .comment("Creeper Focus cooldown in ticks (default: 100)")
                        .defineInRange("creeperFocusCooldown", 100, 0, Integer.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Stare Focus");
        }

        public static final ForgeConfigSpec.IntValue STARE_FOCUS_SOUL_COST = BUILDER
                        .comment("Stare Focus soul cost (default: 48)")
                        .defineInRange("stareFocusSoulCost", 48, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue STARE_FOCUS_CAST_DURATION = BUILDER
                        .comment("Stare Focus cast duration in ticks (default: 80)")
                        .defineInRange("stareFocusCastDuration", 80, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue STARE_FOCUS_COOLDOWN = BUILDER
                        .comment("Stare Focus cooldown in ticks (default: 600)")
                        .defineInRange("stareFocusCooldown", 600, 0, Integer.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Torment Focus");
        }

        public static final ForgeConfigSpec.IntValue TORMENT_FOCUS_SOUL_COST = BUILDER
                        .comment("Torment Focus soul cost (default: 48)")
                        .defineInRange("tormentFocusSoulCost", 48, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue TORMENT_FOCUS_CAST_DURATION = BUILDER
                        .comment("Torment Focus cast duration in ticks (default: 80)")
                        .defineInRange("tormentFocusCastDuration", 80, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue TORMENT_FOCUS_COOLDOWN = BUILDER
                        .comment("Torment Focus cooldown in ticks (default: 600)")
                        .defineInRange("tormentFocusCooldown", 600, 0, Integer.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Shulker Missile Focus");
        }

        public static final ForgeConfigSpec.IntValue SHULKER_MISSILE_FOCUS_SOUL_COST = BUILDER
                        .comment("Shulker Missile Focus soul cost (default: 16)")
                        .defineInRange("shulkerMissileFocusSoulCost", 16, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue SHULKER_MISSILE_FOCUS_CAST_DURATION = BUILDER
                        .comment("Shulker Missile Focus cast duration in ticks (default: 0)")
                        .defineInRange("shulkerMissileFocusCastDuration", 0, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue SHULKER_MISSILE_FOCUS_COOLDOWN = BUILDER
                        .comment("Shulker Missile Focus cooldown in ticks (default: 100)")
                        .defineInRange("shulkerMissileFocusCooldown", 100, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue SHULKER_MISSILE_BASE_DAMAGE = BUILDER
                        .comment("Shulker Missile Spell base damage (default: 4.0)")
                        .defineInRange("shulkerMissileBaseDamage", 4.0, 0.0, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue SHULKER_MISSILE_POTENCY_DAMAGE = BUILDER
                        .comment("Shulker Missile Spell damage increase per potency level (default: 1.0)")
                        .defineInRange("shulkerMissilePotencyDamage", 1.0, 0.0, Double.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue SHULKER_MISSILE_MAX_POTION_AMPLIFIER = BUILDER
                        .comment("Shulker Missile Focus maximum potion effect amplifier from crafting (default: 1, which is level II)")
                        .defineInRange("shulkerMissileMaxPotionAmplifier", 1, 0, 255);

        public static final ForgeConfigSpec.IntValue SHULKER_MISSILE_MAX_POTION_DURATION = BUILDER
                        .comment("Shulker Missile Focus maximum potion effect duration from crafting in ticks (default: 200 = 10 seconds)")
                        .defineInRange("shulkerMissileMaxPotionDuration", 200, 20, Integer.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Mushroom Missile Focus");
        }

        public static final ForgeConfigSpec.IntValue MUSHROOM_MISSILE_FOCUS_SOUL_COST = BUILDER
                        .comment("Mushroom Missile Focus soul cost (default: 32)")
                        .defineInRange("mushroomMissileFocusSoulCost", 32, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue MUSHROOM_MISSILE_FOCUS_CAST_DURATION = BUILDER
                        .comment("Mushroom Missile Focus cast duration in ticks (default: 20)")
                        .defineInRange("mushroomMissileFocusCastDuration", 20, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue MUSHROOM_MISSILE_FOCUS_COOLDOWN = BUILDER
                        .comment("Mushroom Missile Focus cooldown in ticks (default: 100)")
                        .defineInRange("mushroomMissileFocusCooldown", 100, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue MUSHROOM_MISSILE_BASE_DAMAGE = BUILDER
                        .comment("Mushroom Missile Spell base explosion damage (default: 12.0)")
                        .defineInRange("mushroomMissileBaseDamage", 12.0, 0.0, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue MUSHROOM_MISSILE_POTENCY_DAMAGE = BUILDER
                        .comment("Mushroom Missile Spell damage increase per potency level (default: 1.0)")
                        .defineInRange("mushroomMissilePotencyDamage", 1.0, 0.0, Double.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue MUSHROOM_MISSILE_MAX_POTION_AMPLIFIER = BUILDER
                        .comment("Mushroom Missile Focus maximum potion effect amplifier from crafting (default: 1, which is level II)")
                        .defineInRange("mushroomMissileMaxPotionAmplifier", 1, 0, 255);

        public static final ForgeConfigSpec.IntValue MUSHROOM_MISSILE_MAX_POTION_DURATION = BUILDER
                        .comment("Mushroom Missile Focus maximum potion effect duration from crafting in ticks (default: 200 = 10 seconds)")
                        .defineInRange("mushroomMissileMaxPotionDuration", 200, 20, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue MUSHROOM_MISSILE_AREA_CLOUD_DURATION = BUILDER
                        .comment("Mushroom Missile area effect cloud duration in ticks, affected by lasting enchantment (default: 100 = 5 seconds)")
                        .defineInRange("mushroomMissileAreaCloudDuration", 100, 20, Integer.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Swamp Mine Focus");
        }

        public static final ForgeConfigSpec.IntValue SWAMP_MINE_FOCUS_SOUL_COST = BUILDER
                        .comment("Swamp Mine Focus soul cost (default: 16)")
                        .defineInRange("swampMineFocusSoulCost", 16, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue SWAMP_MINE_FOCUS_CAST_DURATION = BUILDER
                        .comment("Swamp Mine Focus cast duration in ticks (default: 80 - 4 seconds)")
                        .defineInRange("swampMineFocusCastDuration", 80, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue SWAMP_MINE_FOCUS_COOLDOWN = BUILDER
                        .comment("Swamp Mine Focus cooldown in ticks (default: 160 - 8 seconds)")
                        .defineInRange("swampMineFocusCooldown", 160, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue SWAMP_MINE_POTENCY_EXPLOSION = BUILDER
                        .comment("Swamp Mine Spell extra explosion power per potency level (default: 0.1)")
                        .defineInRange("swampMinePotencyExplosion", 0.1, 0.0, Double.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Ghost Missile Focus");
        }

        public static final ForgeConfigSpec.IntValue GHOST_MISSILE_FOCUS_SOUL_COST = BUILDER
                        .comment("Ghost Missile Focus soul cost (default: 16)")
                        .defineInRange("ghostMissileFocusSoulCost", 16, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue GHOST_MISSILE_FOCUS_CAST_DURATION = BUILDER
                        .comment("Ghost Missile Focus cast duration in ticks (default: 0)")
                        .defineInRange("ghostMissileFocusCastDuration", 0, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue GHOST_MISSILE_FOCUS_COOLDOWN = BUILDER
                        .comment("Ghost Missile Focus cooldown in ticks (default: 40 - 2 seconds)")
                        .defineInRange("ghostMissileFocusCooldown", 40, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue GHOST_MISSILE_BASE_DAMAGE = BUILDER
                        .comment("Ghost Missile Spell base damage (default: 10.0)")
                        .defineInRange("ghostMissileBaseDamage", 10.0, 0.0, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue GHOST_MISSILE_POTENCY_DAMAGE = BUILDER
                        .comment("Ghost Missile Spell damage increase per potency level (default: 1.0)")
                        .defineInRange("ghostMissilePotencyDamage", 1.0, 0.0, Double.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Chip Focus");
        }

        public static final ForgeConfigSpec.IntValue CHIP_FOCUS_SOUL_COST = BUILDER
                        .comment("Chip Focus soul cost (default: 16)")
                        .defineInRange("chipFocusSoulCost", 16, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue CHIP_FOCUS_CAST_DURATION = BUILDER
                        .comment("Chip Focus cast duration in ticks (default: 0)")
                        .defineInRange("chipFocusCastDuration", 0, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue CHIP_FOCUS_COOLDOWN = BUILDER
                        .comment("Chip Focus cooldown in ticks (default: 80 - 4 seconds)")
                        .defineInRange("chipFocusCooldown", 80, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue CHIP_FOCUS_POTENCY_DAMAGE = BUILDER
                        .comment("Chip Focus Spell damage increase per potency level (default: 1.0)")
                        .defineInRange("chipFocusPotencyDamage", 1.0, 0.0, Double.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Chip Rain Focus");
        }

        public static final ForgeConfigSpec.IntValue CHIP_RAIN_FOCUS_SOUL_COST = BUILDER
                        .comment("Chip Rain Focus soul cost (default: 32)")
                        .defineInRange("chipRainFocusSoulCost", 32, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue CHIP_RAIN_FOCUS_CAST_UP = BUILDER
                        .comment("Chip Rain Focus charge time in ticks (default: 40 - 2 seconds)")
                        .defineInRange("chipRainFocusCastUp", 40, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue CHIP_RAIN_FOCUS_COOLDOWN = BUILDER
                        .comment("Chip Rain Focus cooldown in ticks (default: 300 - 15 seconds)")
                        .defineInRange("chipRainFocusCooldown", 300, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue CHIP_RAIN_FOCUS_POTENCY_DAMAGE = BUILDER
                        .comment("Chip Rain Focus Spell damage increase per potency level (default: 1.0)")
                        .defineInRange("chipRainFocusPotencyDamage", 1.0, 0.0, Double.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Fate Dice Focus");
        }

        public static final ForgeConfigSpec.IntValue FATE_DICE_FOCUS_SOUL_COST = BUILDER
                        .comment("Fate Dice Focus soul cost (default: 12)")
                        .defineInRange("fateDiceFocusSoulCost", 12, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue FATE_DICE_FOCUS_CAST_DURATION = BUILDER
                        .comment("Fate Dice Focus cast duration in ticks (default: 30 - 1.5 seconds)")
                        .defineInRange("fateDiceFocusCastDuration", 30, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue FATE_DICE_FOCUS_COOLDOWN = BUILDER
                        .comment("Fate Dice Focus cooldown in ticks (default: 80 - 4 seconds)")
                        .defineInRange("fateDiceFocusCooldown", 80, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue FATE_DICE_FOCUS_POTENCY_DAMAGE = BUILDER
                        .comment("Fate Dice Focus Spell damage increase per potency level (default: 1.0)")
                        .defineInRange("fateDiceFocusPotencyDamage", 1.0, 0.0, Double.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Infestation Focus");
        }

        public static final ForgeConfigSpec.IntValue INFESTATION_FOCUS_SOUL_COST = BUILDER
                        .comment("Infestation Focus soul cost (default: 8)")
                        .defineInRange("infestationFocusSoulCost", 8, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue INFESTATION_FOCUS_CAST_DURATION = BUILDER
                        .comment("Infestation Focus cast duration in ticks (default: 0)")
                        .defineInRange("infestationFocusCastDuration", 0, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue INFESTATION_FOCUS_COOLDOWN = BUILDER
                        .comment("Infestation Focus cooldown in ticks (default: 60)")
                        .defineInRange("infestationFocusCooldown", 60, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue INFESTATION_BASE_DAMAGE = BUILDER
                        .comment("Infestation Spell base damage per egg (default: 6.0)")
                        .defineInRange("infestationBaseDamage", 6.0, 0.0, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue INFESTATION_POTENCY_DAMAGE = BUILDER
                        .comment("Infestation Spell damage increase per potency level (default: 1.0)")
                        .defineInRange("infestationPotencyDamage", 1.0, 0.0, Double.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Killer Spell");
        }

        public static final ForgeConfigSpec.IntValue KILLER_SPELL_SOUL_COST = BUILDER
                        .comment("Killer Spell soul cost (default: 8)")
                        .defineInRange("killerSpellSoulCost", 8, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue KILLER_SPELL_CAST_DURATION = BUILDER
                        .comment("Killer Spell cast duration in ticks (default: 40)")
                        .defineInRange("killerSpellCastDuration", 40, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue KILLER_SPELL_COOLDOWN = BUILDER
                        .comment("Killer Spell cooldown in ticks (default: 160)")
                        .defineInRange("killerSpellCooldown", 160, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue KILLER_SPELL_SUMMON_DOWN_DURATION = BUILDER
                        .comment("Killer Spell summon down duration in ticks (default: 160)")
                        .defineInRange("killerSpellSummonDownDuration", 160, 0, Integer.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Wololo Focus");
        }

        public static final ForgeConfigSpec.IntValue WOLOLO_FOCUS_SOUL_COST = BUILDER
                        .comment("Wololo Focus soul cost (default: 1)")
                        .defineInRange("wololoFocusSoulCost", 1, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue WOLOLO_FOCUS_CAST_DURATION = BUILDER
                        .comment("Wololo Focus cast duration in ticks (default: 60)")
                        .defineInRange("wololoFocusCastDuration", 60, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue WOLOLO_FOCUS_COOLDOWN = BUILDER
                        .comment("Wololo Focus cooldown in ticks (default: 140)")
                        .defineInRange("wololoFocusCooldown", 140, 0, Integer.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Chasing Flame Focus");
        }

        public static final ForgeConfigSpec.IntValue CHASING_FLAME_FOCUS_SOUL_COST = BUILDER
                        .comment("Chasing Flame Focus soul cost (default: 24)")
                        .defineInRange("chasingFlameFocusSoulCost", 24, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue CHASING_FLAME_FOCUS_CAST_DURATION = BUILDER
                        .comment("Chasing Flame Focus cast duration in ticks (default: 20)")
                        .defineInRange("chasingFlameFocusCastDuration", 20, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue CHASING_FLAME_FOCUS_COOLDOWN = BUILDER
                        .comment("Chasing Flame Focus cooldown in ticks (default: 60)")
                        .defineInRange("chasingFlameFocusCooldown", 60, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue CHASING_FLAME_POTENCY_DAMAGE = BUILDER
                        .comment("Chasing Flame Spell damage increase per potency level (default: 2.0)")
                        .defineInRange("chasingFlamePotencyDamage", 2.0, 0.0, Double.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Great Meteor Focus");
        }

        public static final ForgeConfigSpec.IntValue GREAT_METEOR_FOCUS_SOUL_COST = BUILDER
                        .comment("Great Meteor Focus soul cost (default: 48)")
                        .defineInRange("greatMeteorFocusSoulCost", 48, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue GREAT_METEOR_FOCUS_CAST_DURATION = BUILDER
                        .comment("Great Meteor Focus cast duration in ticks (default: 80)")
                        .defineInRange("greatMeteorFocusCastDuration", 80, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue GREAT_METEOR_FOCUS_COOLDOWN = BUILDER
                        .comment("Great Meteor Focus cooldown in ticks (default: 160)")
                        .defineInRange("greatMeteorFocusCooldown", 160, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue GREAT_METEOR_POTENCY_DAMAGE = BUILDER
                        .comment("Great Meteor Spell damage increase per potency level (default: 3.0)")
                        .defineInRange("greatMeteorPotencyDamage", 3.0, 0.0, Double.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Burning Shield Focus");
        }

        public static final ForgeConfigSpec.IntValue BURNING_SHIELD_FOCUS_SOUL_COST = BUILDER
                        .comment("Burning Shield Focus soul cost (default: 64)")
                        .defineInRange("burningShieldFocusSoulCost", 64, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue BURNING_SHIELD_FOCUS_CAST_DURATION = BUILDER
                        .comment("Burning Shield Focus cast duration in ticks (default: 80)")
                        .defineInRange("burningShieldFocusCastDuration", 80, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue BURNING_SHIELD_FOCUS_COOLDOWN = BUILDER
                        .comment("Burning Shield Focus cooldown in ticks (default: 600)")
                        .defineInRange("burningShieldFocusCooldown", 600, 0, Integer.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Blood Rain Focus");
        }

        public static final ForgeConfigSpec.IntValue BLOOD_RAIN_FOCUS_SOUL_COST = BUILDER
                        .comment("Blood Rain Focus soul cost (default: 16)")
                        .defineInRange("bloodRainFocusSoulCost", 16, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue BLOOD_RAIN_FOCUS_CAST_DURATION = BUILDER
                        .comment("Blood Rain Focus cast duration in ticks (default: 80 - 4 seconds)")
                        .defineInRange("bloodRainFocusCastDuration", 80, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue BLOOD_RAIN_FOCUS_COOLDOWN = BUILDER
                        .comment("Blood Rain Focus cooldown in ticks (default: 240 - 12 seconds)")
                        .defineInRange("bloodRainFocusCooldown", 240, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue BLOOD_RAIN_BASE_DAMAGE = BUILDER
                        .comment("Blood Rain Spell base damage (default: 1.0)")
                        .defineInRange("bloodRainBaseDamage", 1.0, 0.0, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue BLOOD_RAIN_POTENCY_DAMAGE = BUILDER
                        .comment("Blood Rain Spell damage increase per potency level (default: 1.0)")
                        .defineInRange("bloodRainPotencyDamage", 1.0, 0.0, Double.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Desert Plagues Focus");
        }

        public static final ForgeConfigSpec.IntValue DESERT_PLAGUES_FOCUS_SOUL_COST = BUILDER
                        .comment("Desert Plagues Focus soul cost (default: 64)")
                        .defineInRange("desertPlaguesFocusSoulCost", 64, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue DESERT_PLAGUES_FOCUS_CAST_DURATION = BUILDER
                        .comment("Desert Plagues Focus cast duration in ticks (default: 200 - 10 seconds)")
                        .defineInRange("desertPlaguesFocusCastDuration", 200, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue DESERT_PLAGUES_FOCUS_COOLDOWN = BUILDER
                        .comment("Desert Plagues Focus cooldown in ticks (default: 1200 - 60 seconds)")
                        .defineInRange("desertPlaguesFocusCooldown", 1200, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue DESERT_PLAGUES_POTENCY_DAMAGE = BUILDER
                        .comment("Desert Plagues Spell damage increase per potency level (default: 1.0)")
                        .defineInRange("desertPlaguesPotencyDamage", 1.0, 0.0, Double.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Deepdark Vermin Focus");
        }

        public static final ForgeConfigSpec.IntValue DEEPDARK_VERMIN_FOCUS_SOUL_COST = BUILDER
                        .comment("Deepdark Vermin Focus soul cost (default: 16)")
                        .defineInRange("deepdarkVerminFocusSoulCost", 16, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue DEEPDARK_VERMIN_FOCUS_CAST_DURATION = BUILDER
                        .comment("Deepdark Vermin Focus cast duration in ticks (default: 40 - 2 seconds)")
                        .defineInRange("deepdarkVerminFocusCastDuration", 40, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue DEEPDARK_VERMIN_FOCUS_COOLDOWN = BUILDER
                        .comment("Deepdark Vermin Focus cooldown in ticks (default: 160 - 8 seconds)")
                        .defineInRange("deepdarkVerminFocusCooldown", 160, 0, Integer.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Shattered Focus");
        }

        public static final ForgeConfigSpec.IntValue SHATTERED_FOCUS_SOUL_COST = BUILDER
                        .comment("Shattered Focus soul cost (default: 16)")
                        .defineInRange("shatteredFocusSoulCost", 16, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue SHATTERED_FOCUS_CAST_DURATION = BUILDER
                        .comment("Shattered Focus cast duration in ticks (default: 60 - 3 seconds)")
                        .defineInRange("shatteredFocusCastDuration", 60, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue SHATTERED_FOCUS_COOLDOWN = BUILDER
                        .comment("Shattered Focus cooldown in ticks (default: 160 - 8 seconds)")
                        .defineInRange("shatteredFocusCooldown", 160, 0, Integer.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Agony Focus");
        }

        public static final ForgeConfigSpec.IntValue AGONY_FOCUS_SOUL_COST = BUILDER
                        .comment("Agony Focus soul cost (default: 16)")
                        .defineInRange("agonyFocusSoulCost", 16, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue AGONY_FOCUS_CAST_UP = BUILDER
                        .comment("Agony Focus cast up duration in ticks (default: 0)")
                        .defineInRange("agonyFocusCastUp", 0, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue AGONY_FOCUS_COOLDOWN = BUILDER
                        .comment("Agony Focus cooldown in ticks (default: 200)")
                        .defineInRange("agonyFocusCooldown", 200, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue AGONY_FOCUS_POTENCY_DAMAGE = BUILDER
                        .comment("Agony Focus Spell damage increase per potency level (default: 2.0)")
                        .defineInRange("agonyFocusPotencyDamage", 2.0, 0.0, Double.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Death Ray Spell");
        }

        public static final ForgeConfigSpec.IntValue DEATH_RAY_SPELL_SOUL_COST = BUILDER
                        .comment("Death Ray Spell soul cost (default: 16)")
                        .defineInRange("deathRaySpellSoulCost", 16, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue DEATH_RAY_SPELL_CAST_DURATION = BUILDER
                        .comment("Death Ray Spell cast duration in ticks (default: 0)")
                        .defineInRange("deathRaySpellCastDuration", 0, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue DEATH_RAY_SPELL_COOLDOWN = BUILDER
                        .comment("Death Ray Spell cooldown in ticks (default: 20)")
                        .defineInRange("deathRaySpellCooldown", 20, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue DEATH_RAY_BASE_DAMAGE = BUILDER
                        .comment("Death Ray Spell base damage (default: 5.0)")
                        .defineInRange("deathRayBaseDamage", 5.0, 0.0, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue DEATH_RAY_POTENCY_DAMAGE = BUILDER
                        .comment("Death Ray Spell damage increase per potency level (default: 3.0)")
                        .defineInRange("deathRayPotencyDamage", 3.0, 0.0, Double.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Fireball Feast Focus");
        }

        public static final ForgeConfigSpec.IntValue FIREBALL_FEAST_FOCUS_SOUL_COST = BUILDER
                        .comment("Fireball Feast Focus soul cost (default: 64)")
                        .defineInRange("fireballFeastFocusSoulCost", 64, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue FIREBALL_FEAST_FOCUS_CAST_DURATION = BUILDER
                        .comment("Fireball Feast Focus cast up duration in ticks (default: 80)")
                        .defineInRange("fireballFeastFocusCastDuration", 80, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue FIREBALL_FEAST_FOCUS_COOLDOWN = BUILDER
                        .comment("Fireball Feast Focus cooldown in ticks (default: 400)")
                        .defineInRange("fireballFeastFocusCooldown", 400, 0, Integer.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Heaven Rift Focus");
        }

        public static final ForgeConfigSpec.IntValue HEAVEN_RIFT_FOCUS_SOUL_COST = BUILDER
                        .comment("Heaven Rift Focus soul cost (default: 256)")
                        .defineInRange("heavenRiftFocusSoulCost", 256, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue HEAVEN_RIFT_FOCUS_CAST_DURATION = BUILDER
                        .comment("Heaven Rift Focus cast duration in ticks (default: 0)")
                        .defineInRange("heavenRiftFocusCastDuration", 0, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue HEAVEN_RIFT_FOCUS_COOLDOWN = BUILDER
                        .comment("Heaven Rift Focus cooldown in ticks (default: 1200)")
                        .defineInRange("heavenRiftFocusCooldown", 1200, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue HEAVEN_RIFT_BASE_DAMAGE = BUILDER
                        .comment("Heaven Rift Spell base damage per Pure Light (default: 9.0)")
                        .defineInRange("heavenRiftBaseDamage", 9.0, 0.0, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue HEAVEN_RIFT_POTENCY_DAMAGE = BUILDER
                        .comment("Heaven Rift Spell damage increase per potency level (default: 2.0)")
                        .defineInRange("heavenRiftPotencyDamage", 2.0, 0.0, Double.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Champion Focus");
        }

        public static final ForgeConfigSpec.IntValue CHAMPION_FOCUS_SOUL_COST = BUILDER
                        .comment("Champion Focus soul cost (default: 1000)")
                        .defineInRange("championFocusSoulCost", 1000, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue CHAMPION_FOCUS_CAST_DURATION = BUILDER
                        .comment("Champion Focus cast duration in ticks (default: 180 - 9 seconds)")
                        .defineInRange("championFocusCastDuration", 180, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue CHAMPION_FOCUS_COOLDOWN = BUILDER
                        .comment("Champion Focus cooldown in ticks (default: 1200 - 60 seconds)")
                        .defineInRange("championFocusCooldown", 1200, 0, Integer.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Access Focus");
        }

        public static final ForgeConfigSpec.IntValue ACCESS_FOCUS_SOUL_COST = BUILDER
                        .comment("Access Focus soul cost (default: 10)")
                        .defineInRange("accessFocusSoulCost", 10, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue ACCESS_FOCUS_CAST_DURATION = BUILDER
                        .comment("Access Focus cast duration in ticks (default: 0)")
                        .defineInRange("accessFocusCastDuration", 0, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue ACCESS_FOCUS_COOLDOWN = BUILDER
                        .comment("Access Focus cooldown in ticks (default: 0)")
                        .defineInRange("accessFocusCooldown", 0, 0, Integer.MAX_VALUE);

        static {
                BUILDER.pop();
        }

        static {
                BUILDER.push("Mooshroom Focus");
        }

        public static final ForgeConfigSpec.IntValue MOOSHROOM_FOCUS_SOUL_COST = BUILDER
                        .comment("Mooshroom Focus soul cost (default: 32)")
                        .defineInRange("mooshroomFocusSoulCost", 32, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue MOOSHROOM_FOCUS_CAST_DURATION = BUILDER
                        .comment("Mooshroom Focus cast duration in ticks (default: 60 - 3 seconds)")
                        .defineInRange("mooshroomFocusCastDuration", 60, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue MOOSHROOM_FOCUS_COOLDOWN = BUILDER
                        .comment("Mooshroom Focus cooldown in ticks (default: 160 - 8 seconds)")
                        .defineInRange("mooshroomFocusCooldown", 160, 0, Integer.MAX_VALUE);

        static {
                BUILDER.pop();
                BUILDER.pop();
                BUILDER.push("Weapon Stats");
        }

        public static final ForgeConfigSpec.DoubleValue TRUTH_SEEKER_DAMAGE = BUILDER
                        .comment("Truth Seeker base damage (default: 12.0)")
                        .defineInRange("truthSeekerDamage", 12.0, 0.0, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue TRUTH_SEEKER_ATTACK_SPEED = BUILDER
                        .comment("Truth Seeker attack speed (default: 0.8)")
                        .defineInRange("truthSeekerAttackSpeed", 0.8, Double.MIN_VALUE, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue FROST_SCYTHE_DAMAGE = BUILDER
                        .comment("Frost Scythe base damage (default: 8.5)")
                        .defineInRange("frostScytheDamage", 8.5, 0.0, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue FROST_SCYTHE_ATTACK_SPEED = BUILDER
                        .comment("Frost Scythe attack speed (default: 0.6)")
                        .defineInRange("frostScytheAttackSpeed", 0.6, 0.0, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue GLAIVE_DAMAGE = BUILDER
                        .comment("Glaive base damage (default: 8.0)")
                        .defineInRange("glaiveDamage", 8.0, 0.0, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue GLAIVE_ATTACK_SPEED = BUILDER
                        .comment("Glaive attack speed (default: 1.2)")
                        .defineInRange("glaiveAttackSpeed", 1.2, Double.MIN_VALUE, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue MOONLIGHT_CUT_DAMAGE = BUILDER
                        .comment("Moonlight Cut base damage (default: 10.0)")
                        .defineInRange("moonlightCutDamage", 10.0, 0.0, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue MOONLIGHT_CUT_ATTACK_SPEED = BUILDER
                        .comment("Moonlight Cut attack speed (default: 1.2)")
                        .defineInRange("moonlightCutAttackSpeed", 1.2, Double.MIN_VALUE, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue GRAVE_BANE_DAMAGE = BUILDER
                        .comment("Grave Bane base damage (default: 9.0)")
                        .defineInRange("graveBaneDamage", 9.0, 0.0, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue GRAVE_BANE_ATTACK_SPEED = BUILDER
                        .comment("Grave Bane attack speed (default: 1.2)")
                        .defineInRange("graveBaneAttackSpeed", 1.2, Double.MIN_VALUE, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue SUN_GRACE_DAMAGE = BUILDER
                        .comment("Sun Grace base damage (default: 11.0)")
                        .defineInRange("sunGraceDamage", 11.0, 0.0, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue SUN_GRACE_ATTACK_SPEED = BUILDER
                        .comment("Sun Grace attack speed (default: 0.5)")
                        .defineInRange("sunGraceAttackSpeed", 0.5, Double.MIN_VALUE, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue MACE_DAMAGE = BUILDER
                        .comment("Mace base damage (default: 9.0)")
                        .defineInRange("maceDamage", 9.0, 0.0, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue MACE_ATTACK_SPEED = BUILDER
                        .comment("Mace attack speed (default: 0.5)")
                        .defineInRange("maceAttackSpeed", 0.5, Double.MIN_VALUE, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue MOO_SHROOM_MACE_DAMAGE = BUILDER
                        .comment("MooShroom Mace base damage (default: 15.0)")
                        .defineInRange("mooShroomMaceDamage", 15.0, 0.0, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue MOO_SHROOM_MACE_ATTACK_SPEED = BUILDER
                        .comment("MooShroom Mace attack speed (default: 0.5)")
                        .defineInRange("mooShroomMaceAttackSpeed", 0.5, Double.MIN_VALUE, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue CLAYMORE_DAMAGE = BUILDER
                        .comment("Claymore base damage (default: 10.0)")
                        .defineInRange("claymoreDamage", 10.0, 0.0, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue CLAYMORE_ATTACK_SPEED = BUILDER
                        .comment("Claymore attack speed (default: 0.6)")
                        .defineInRange("claymoreAttackSpeed", 0.6, Double.MIN_VALUE, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue STARLESS_NIGHT_DAMAGE = BUILDER
                        .comment("Starless Night base damage (default: 16.0)")
                        .defineInRange("starlessNightDamage", 16.0, 0.0, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue STARLESS_NIGHT_ATTACK_SPEED = BUILDER
                        .comment("Starless Night attack speed (default: 0.6)")
                        .defineInRange("starlessNightAttackSpeed", 0.6, Double.MIN_VALUE, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue OBSIDIAN_CLAYMORE_DAMAGE = BUILDER
                        .comment("Obsidian Claymore base damage (default: 13.0)")
                        .defineInRange("obsidianClaymoreDamage", 13.0, 0.0, Double.MAX_VALUE);

        public static final ForgeConfigSpec.DoubleValue OBSIDIAN_CLAYMORE_ATTACK_SPEED = BUILDER
                        .comment("Obsidian Claymore attack speed (default: 0.6)")
                        .defineInRange("obsidianClaymoreAttackSpeed", 0.6, Double.MIN_VALUE, Double.MAX_VALUE);

        static {
                BUILDER.pop();
                BUILDER.push("Misc Settings");
        }

        public static final ForgeConfigSpec.BooleanValue OBSIDIAN_MONOLITH_GLOW = BUILDER
                        .comment("Whether Obsidian Monolith should glow (default: true)")
                        .define("obsidianMonolithGlow", true);

        public static final ForgeConfigSpec.BooleanValue WIGHT_SERVANT_AMBIENT_SOUND = BUILDER
                        .comment("Whether Wight Servant should play ambient sound (default: true)")
                        .define("wightServantAmbientSound", true);

        private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ENDERMAN_SERVANT_BLACKLIST = BUILDER
                        .comment("Enderman Servant blacklisted blocks that cannot be picked up. Default: Minecraft's unbreakable blocks, chests, barrels, and shulker boxes")
                        .defineListAllowEmpty(List.of("endermanServantBlacklist"),
                                        () -> Arrays.asList(
                                                        "minecraft:bedrock",
                                                        "minecraft:command_block",
                                                        "minecraft:chain_command_block",
                                                        "minecraft:repeating_command_block",
                                                        "minecraft:barrier",
                                                        "minecraft:structure_block",
                                                        "minecraft:structure_void",
                                                        "minecraft:crafting_table"),
                                        obj -> obj instanceof String);

        private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ALLOW_MULTISHOT_PROJECTILES = BUILDER
                        .comment("Allow the multi shot for projectile. Use the full name (eg: minecraft:arrow)")
                        .defineListAllowEmpty("allowMultiShotProjectiles",
                                        () -> Arrays.asList(
                                                        "minecraft:arrow", "minecraft:tipped_arrow",
                                                        "minecraft:spectral_arrow",
                                                        "minecraft:snowball", "minecraft:firework_rocket",
                                                        "minecraft:fireball", "minecraft:llama_spit",
                                                        "minecraft:small_fireball", "minecraft:shulker_bullet",
                                                        "minecraft:dragon_fireball",
                                                        "goety:soul_bolt",
                                                        "goety:death_arrow",
                                                        "goety:fireball", "goety:lavaball", "goety:hell_bolt",
                                                        "goety:hell_chant", "goety:sword", "goety:hell_blast",
                                                        "goety:ice_spike", "goety:ice_spear", "goety:ice_storm",
                                                        "goety:ghost_arrow", "goety:death_arrow", "goety:harpoon",
                                                        "goety:poison_quill", "goety:bone_shard", "goety:scythe",
                                                        "goety:dragon_fireball", "goety:haunted_skull_shot",
                                                        "goety:wither_skull", "goety:soul_bullet", "goety:soul_bolt",
                                                        "goety:poison_bolt", "goety:steam_missile", "goety:wither_bolt",
                                                        "goety:necro_bolt", "goety:magic_bolt", "goety:void_shock_bomb",
                                                        "goety:ill_bomb", "goety:electro_orb", "goety:surging_orb",
                                                        "goety:bouncy_bubble", "goety:scatter_bomb",
                                                        "goety:snap_fungus", "goety:blast_fungus",
                                                        "goety:berserk_fungus", "goety:magma_bomb",
                                                        "goety:blossom_ball", "goety:web_shot", "goety:snareling_shot",
                                                        "goety:ender_goo",
                                                        "goety:tidal_surge", "goety:razor_wind",
                                                        "goety:void_slash", "goetyawaken:silverfish_egg",
                                                        "goetyawaken:endermite_egg", "goetyawaken:ghost_fire_bolt",
                                                        "goetyawaken:mushroom_missile",
                                                        "goetyawaken:mushroom_scatter_bomb",
                                                        "goetyawaken:frost_scythe_slash", "goetyawaken:explosive_arrow",
                                                        "goetyawaken:mod_sword_projectile",
                                                        "goetyawaken:giant_ghast_fireball",
                                                        "goetyawaken:tracking_fireball",
                                                        "goetyawaken:giant_hell_blast",
                                                        "goetyawaken:flying_axe",
                                                        "goetyawaken:nameless_bolt",
                                                        "goetyawaken:corrupted_soul_bolt"),
                                        obj -> obj instanceof String);

        static {
                BUILDER.pop();
                BUILDER.push("Enhancements");
        }

        private static final ForgeConfigSpec.BooleanValue ENABLE_BLASTLING_ENHANCEMENT = BUILDER
                        .comment("Enable Blastling Enhancement(default: true)")
                        .define("enableBlastlingEnhancement", true);

        private static final ForgeConfigSpec.BooleanValue ENABLE_SNARELING_ENHANCEMENT = BUILDER
                        .comment("Enable Snareling Enhancement(default: true)")
                        .define("enableSnarelingEnhancement", true);

        private static final ForgeConfigSpec.BooleanValue ENABLE_WATCHLING_ENHANCEMENT = BUILDER
                        .comment("Enable Watchling Enhancement(default: true)")
                        .define("enableWatchlingEnhancement", true);

        private static final ForgeConfigSpec.BooleanValue ENABLE_SQUALLGOLEM_ENHANCEMENT = BUILDER
                        .comment("Enable Squall Golem Enhancement(default: true)")
                        .define("enableSquallGolemEnhancement", true);

        private static final ForgeConfigSpec.BooleanValue ENABLE_LEAPLEAF_ENHANCEMENT = BUILDER
                        .comment("Enable Leapleaf Enhancement(default: true)")
                        .define("enableLeapleafEnhancement", true);

        private static final ForgeConfigSpec.BooleanValue ENABLE_WILDFIRE_ENHANCEMENT = BUILDER
                        .comment("Enable Wildfire Enhancement(default: true)")
                        .define("enableWildfireEnhancement", true);

        private static final ForgeConfigSpec.BooleanValue ENABLE_PIKER_ENHANCEMENT = BUILDER
                        .comment("Enable Piker Enhancement(default: true)")
                        .define("enablePikerEnhancement", true);

        private static final ForgeConfigSpec.BooleanValue ENABLE_BROODMOTHER_ENHANCEMENT = BUILDER
                        .comment("Enable Brood Mother Enhancement(default: true)")
                        .define("enableBroodMotherEnhancement", true);

        public static final ForgeConfigSpec.BooleanValue CRONE_SERVANT_ALLOW_GAS_BREW = BUILDER
                        .comment("Allow Crone Servant to throw Gas Brew (default: false)")
                        .define("croneServantAllowGasBrew", false);

        public static final ForgeConfigSpec.IntValue GLOWING_EMBER_MAX_ENHANCEMENTS = BUILDER
                        .comment("The maximum number of times a single item can be enhanced on an anvil using a Gilded Ingot to improve its enchantments. (default: 1)")
                        .defineInRange("gildedIngotMaxEnhancements", 1, 1, Integer.MAX_VALUE);

        static {
                BUILDER.pop();
                BUILDER.push("Structure Settings");
        }

        private static final ForgeConfigSpec.IntValue FOUNDATION_SCAN_ABOVE = BUILDER
                        .comment("How many blocks above the structure base to scan for cavities")
                        .defineInRange("foundationScanAbove", 7, 1, 50);

        private static final ForgeConfigSpec.IntValue FOUNDATION_SCAN_BELOW = BUILDER
                        .comment("How many blocks below the structure base to scan for cavities. Increase this if you see hollow spaces under structures.")
                        .defineInRange("foundationScanBelow", 50, 10, 200);

        private static final ForgeConfigSpec.ConfigValue<List<? extends String>> FOUNDATION_WHITELIST = BUILDER
                        .comment("List of structures that will receive foundation generation. Format: modid:structure_name")
                        .defineList("foundationWhitelist",
                                        Arrays.asList(
                                                        "goetyawaken:mirage",
                                                        "goetyawaken:ominous_castle",
                                                        "goetyawaken:arch_illusioner_keep"),
                                        obj -> obj instanceof String);

        static {
                BUILDER.pop();
                BUILDER.push("Item Settings");
        }

        public static final ForgeConfigSpec.IntValue DEATH_CAP_MUSHROOM_COOLDOWN = BUILDER
                        .comment("Death Cap Mushroom cooldown in ticks after eating (default: 1200 - 60 seconds)")
                        .defineInRange("deathCapMushroomCooldown", 1200, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue DEATH_CAP_MUSHROOM_MAX_USAGES = BUILDER
                        .comment("Death Cap Mushroom maximum usages before being consumed (default: 10)")
                        .defineInRange("deathCapMushroomMaxUsages", 10, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue DEATH_CAP_MUSHROOM_EFFECT_DURATION = BUILDER
                        .comment("Death Cap Mushroom BERSERK effect duration in ticks (default: 600 - 30 seconds)")
                        .defineInRange("deathCapMushroomEffectDuration", 600, 0, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.IntValue DEATH_CAP_MUSHROOM_EFFECT_LEVEL = BUILDER
                        .comment("Death Cap Mushroom BERSERK effect level/amplifier (default: 4, which is level IV)")
                        .defineInRange("deathCapMushroomEffectLevel", 4, 1, 255);

        public static final ForgeConfigSpec.IntValue SWELLING_PENDANT_MAX_SLIME_VALUE = BUILDER
                        .comment("Swelling Pendant max slime value (default: 10000)")
                        .defineInRange("swellingPendantMaxSlimeValue", 10000, 1, Integer.MAX_VALUE);

        public static final ForgeConfigSpec.BooleanValue GRIMOIRE_AFFIX_ENABLED = BUILDER
                        .comment("Enable Grimoire random affix system (crafting/ritual affix rolls and Curios attribute modifiers). Default: true")
                        .define("grimoireAffixEnabled", true);

        public static final ForgeConfigSpec.BooleanValue GRIMOIRE_AFFIX_ALLOW_NEGATIVE = BUILDER
                        .comment("Allow negative attribute values on Grimoire affixes. Default: true")
                        .define("grimoireAffixAllowNegative", true);

        static {
                BUILDER.pop();
                BUILDER.push("Mob Spawning");
        }

        public static final ForgeConfigSpec.BooleanValue ZOMBIE_DARKGUARD_SPAWN = BUILDER
                        .comment("Allow Zombie Darkguard to naturally spawn. Default: false")
                        .define("zombieDarkguardSpawn", false);

        public static final ForgeConfigSpec.BooleanValue SKELETON_VANGUARD_SPAWN = BUILDER
                        .comment("Allow Skeleton Vanguard to naturally spawn. Default: false")
                        .define("skeletonVanguardSpawn", false);

        public static final ForgeConfigSpec.BooleanValue PARCHED_SPAWN = BUILDER
                        .comment("Allow Parched to naturally spawn. Default: false")
                        .define("parchedSpawn", false);

        public static final ForgeConfigSpec.BooleanValue ICE_CREEPER_SPAWN = BUILDER
                        .comment("Allow Ice Creeper to naturally spawn. Default: false")
                        .define("iceCreeperSpawn", false);

        public static final ForgeConfigSpec.BooleanValue BOULDERING_ZOMBIE_SPAWN = BUILDER
                        .comment("Allow Bouldering Zombie to naturally spawn. Default: false")
                        .define("boulderingZombieSpawn", false);

        public static final ForgeConfigSpec.BooleanValue JUNGLE_ZOMBIE_SPAWN = BUILDER
                        .comment("Allow Jungle Zombie to naturally spawn. Default: false")
                        .define("jungleZombieSpawn", false);

        public static final ForgeConfigSpec.BooleanValue FROZEN_ZOMBIE_SPAWN = BUILDER
                        .comment("Allow Frozen Zombie to naturally spawn. Default: false")
                        .define("frozenZombieSpawn", false);

        public static final ForgeConfigSpec.BooleanValue HOSTILE_SNAPPER_SPAWN = BUILDER
                        .comment("Allow Hostile Snapper to naturally spawn. Default: false")
                        .define("hostileSnapperSpawn", false);

        public static final ForgeConfigSpec.BooleanValue HOSTILE_TROPICAL_SLIME_SPAWN = BUILDER
                        .comment("Allow Hostile Tropical Slime to naturally spawn. Default: false")
                        .define("hostileTropicalSlimeSpawn", false);

        public static final ForgeConfigSpec.BooleanValue HOSTILE_MINI_GHAST_SPAWN = BUILDER
                        .comment("Allow Hostile Mini Ghast to naturally spawn. Default: false")
                        .define("hostileMiniGhastSpawn", false);

        public static final ForgeConfigSpec.BooleanValue HOSTILE_GNASHER_SPAWN = BUILDER
                        .comment("Allow Hostile Gnasher to naturally spawn. Default: false")
                        .define("hostileGnasherSpawn", false);

        public static final ForgeConfigSpec.BooleanValue SUNKEN_SKELETON_SPAWN = BUILDER
                        .comment("Allow Sunken Skeleton to naturally spawn. Default: false")
                        .define("sunkenSkeletonSpawn", false);

        public static final ForgeConfigSpec.BooleanValue HOSTILE_WILDFIRE_SPAWN = BUILDER
                        .comment("Allow Hostile Wildfire to naturally spawn. Default: false")
                        .define("hostileWildfireSpawn", false);

        public static final ForgeConfigSpec.BooleanValue HOSTILE_SPIDER_CREEDER_SPAWN = BUILDER
                        .comment("Allow Hostile Spider Creeder to naturally spawn. Default: false")
                        .define("hostileSpiderCreederSpawn", false);

        public static final ForgeConfigSpec.BooleanValue HOSTILE_TWILIGHT_GOAT_SPAWN = BUILDER
                        .comment("Allow Hostile Twilight Goat to naturally spawn. Default: false")
                        .define("hostileTwilightGoatSpawn", false);

        static {
                BUILDER.pop();
        }

        static final ForgeConfigSpec SPEC = BUILDER.build();

        public static int paleGolemLimit;
        public static int wardenServantLimit;
        public static int wightServantLimit;
        public static int creeperServantLimit;
        public static int caerbannogRabbitServantLimit;
        public static int endermanServantLimit;
        public static int tormentorServantLimit;
        public static int shulkerServantLimit;
        public static int witherServantLimit;
        public static int wraithNecromancerLimit;
        public static int ruinsNecromancerLimit;
        public static int namelessOneLimit;
        public static int enderKeeperServantLimit;
        public static int apostleServantLimit;
        public static int angryMooshroomLimit;
        public static int maidFairyServantLimit;

        public static int fairyFocusSoulCost;
        public static int fairyFocusCastDuration;
        public static int fairyFocusCooldown;

        public static int deepdarkVerminFocusSoulCost;
        public static int deepdarkVerminFocusCastDuration;
        public static int deepdarkVerminFocusCooldown;

        public static int shatteredFocusSoulCost;
        public static int shatteredFocusCastDuration;
        public static int shatteredFocusCooldown;

        public static int fireballFeastFocusSoulCost;
        public static int fireballFeastFocusCastDuration;
        public static int fireballFeastFocusCooldown;

        public static boolean callbackApostle;
        public static boolean apostleSummonArmoredZombiePiglin;
        public static boolean enableApostleServantArmorRenderer;
        public static boolean apostleSecondPhaseThunderStorm;

        public static int creeperFocusSoulCost;
        public static int creeperFocusCastDuration;
        public static int creeperFocusCooldown;

        public static int stareFocusSoulCost;
        public static int stareFocusCastDuration;
        public static int stareFocusCooldown;

        public static int tormentFocusSoulCost;
        public static int tormentFocusCastDuration;
        public static int tormentFocusCooldown;

        public static int shulkerMissileFocusSoulCost;
        public static int shulkerMissileFocusCastDuration;
        public static int shulkerMissileFocusCooldown;

        public static int mushroomMissileFocusSoulCost;
        public static int mushroomMissileFocusCastDuration;
        public static int mushroomMissileFocusCooldown;

        public static int infestationFocusSoulCost;
        public static int infestationFocusCastDuration;
        public static int infestationFocusCooldown;

        public static int killerSpellSoulCost;
        public static int killerSpellCastDuration;
        public static int killerSpellCooldown;
        public static int killerSpellSummonDownDuration;

        public static int wololoFocusSoulCost;
        public static int wololoFocusCastDuration;
        public static int wololoFocusCooldown;

        public static int bloodRainFocusSoulCost;
        public static int bloodRainFocusCastDuration;
        public static int bloodRainFocusCooldown;

        public static int desertPlaguesFocusSoulCost;
        public static int desertPlaguesFocusCastDuration;
        public static int desertPlaguesFocusCooldown;

        public static int agonyFocusSoulCost;
        public static int agonyFocusCastUp;
        public static int agonyFocusCooldown;

        public static int deathRaySpellSoulCost;
        public static int deathRaySpellCastDuration;
        public static int deathRaySpellCooldown;

        public static int heavenRiftFocusSoulCost;
        public static int heavenRiftFocusCastDuration;
        public static int heavenRiftFocusCooldown;

        public static double infestationBaseDamage;
        public static double infestationPotencyDamage;
        public static double deathRayBaseDamage;
        public static double deathRayPotencyDamage;
        public static double bloodRainBaseDamage;
        public static double bloodRainPotencyDamage;
        public static double agonyFocusPotencyDamage;
        public static double desertPlaguesPotencyDamage;
        public static double heavenRiftBaseDamage;
        public static double heavenRiftPotencyDamage;
        public static double mushroomMissileBaseDamage;
        public static double mushroomMissilePotencyDamage;
        public static double shulkerMissileBaseDamage;
        public static double shulkerMissilePotencyDamage;

        public static int swampMineFocusSoulCost;
        public static int swampMineFocusCastDuration;
        public static int swampMineFocusCooldown;
        public static double swampMinePotencyExplosion;

        public static int ghostMissileFocusSoulCost;
        public static int ghostMissileFocusCastDuration;
        public static int ghostMissileFocusCooldown;
        public static double ghostMissileBaseDamage;
        public static double ghostMissilePotencyDamage;

        public static int chipFocusSoulCost;
        public static int chipFocusCastDuration;
        public static int chipFocusCooldown;
        public static double chipFocusPotencyDamage;

        public static int chipRainFocusSoulCost;
        public static int chipRainFocusCastUp;
        public static int chipRainFocusCooldown;
        public static double chipRainFocusPotencyDamage;

        public static int fateDiceFocusSoulCost;
        public static int fateDiceFocusCastDuration;
        public static int fateDiceFocusCooldown;
        public static double fateDiceFocusPotencyDamage;

        public static int championFocusSoulCost;
        public static int championFocusCastDuration;
        public static int championFocusCooldown;

        public static int mooshroomFocusSoulCost;
        public static int mooshroomFocusCastDuration;
        public static int mooshroomFocusCooldown;

        public static boolean obsidianMonolithGlow;

        public static List<? extends String> endermanServantBlacklist;

        public static double mushroomMonstrosityDamageCap;
        public static int mushroomMonstrosityLimit;
        public static int vanguardChampionLimit;

        public static boolean wraithNecromancerBossMusic;
        public static boolean wraithNecromancerLegacyMusic;
        public static boolean mushroomMonstrosityBossMusic;
        public static boolean parchedNecromancerBossMusic;
        public static boolean namelessOneBossMusic;
        public static boolean namelessOneEasyMode;
        public static boolean enableNamelessOneDesertPlagues;
        public static boolean enableNamelessOneAvada;
        public static boolean enableNamelessOneThunderstorm;
        public static boolean enableNamelessOneHeavenRift;
        public static boolean enableNamelessOneEnchantment;
        public static boolean enableNamelessOneSupportSpells;
        public static boolean enableNamelessOneScarletVexSummon;
        public static boolean enableNamelessBoltDeathFire;
        public static boolean mushroomMonstrosityEasyMode;

        public static int deathCapMushroomCooldown;
        public static int deathCapMushroomMaxUsages;
        public static int deathCapMushroomEffectDuration;
        public static int deathCapMushroomEffectLevel;
        public static int swellingPendantMaxSlimeValue;

        public static double truthSeekerDamage;
        public static double truthSeekerAttackSpeed;

        public static double frostScytheDamage;
        public static double frostScytheAttackSpeed;

        public static double glaiveDamage;
        public static double glaiveAttackSpeed;
        public static double moonlightCutDamage;
        public static double moonlightCutAttackSpeed;
        public static double sunGraceDamage;
        public static double sunGraceAttackSpeed;
        public static double maceDamage;
        public static double maceAttackSpeed;
        public static double mooShroomMaceDamage;
        public static double mooShroomMaceAttackSpeed;
        public static double claymoreDamage;
        public static double claymoreAttackSpeed;
        public static double starlessNightDamage;
        public static double starlessNightAttackSpeed;
        public static double obsidianClaymoreDamage;
        public static double obsidianClaymoreAttackSpeed;
        public static double graveBaneDamage;
        public static double graveBaneAttackSpeed;

        public static int shulkerMissileMaxPotionAmplifier;
        public static int shulkerMissileMaxPotionDuration;
        public static int mushroomMissileMaxPotionAmplifier;
        public static int mushroomMissileMaxPotionDuration;
        public static int mushroomMissileAreaCloudDuration;

        public static boolean enableBlastlingEnhancement;
        public static boolean enableSnarelingEnhancement;
        public static boolean enableWatchlingEnhancement;
        public static boolean enableSquallGolemEnhancement;
        public static boolean enableLeapleafEnhancement;
        public static boolean enableWildfireEnhancement;
        public static boolean enablePikerEnhancement;
        public static boolean enableBroodMotherEnhancement;
        public static boolean croneServantAllowGasBrew;
        public static List<? extends String> allowMultiShotProjectiles;

        public static int foundationScanAbove;
        public static int foundationScanBelow;
        public static List<? extends String> foundationWhitelist;

        public static int gildedIngotMaxEnhancements;

        public static int sorcererServantRestockLimit;
        public static int sorcererServantRestockInterval;
        public static int sorcererServantRestockCooldown;
        public static int sorcererServantTradeMaxUses;

        public static int heresiarchServantTradeMaxUses;
        public static int heresiarchServantTradeCount;
        public static int heresiarchServantRestockLimit;
        public static int heresiarchServantRestockInterval;
        public static int heresiarchServantRestockCooldown;

        public static int croneServantTradeMaxUses;
        public static int croneServantTradeCount;
        public static int croneServantRestockLimit;
        public static int croneServantRestockInterval;
        public static int croneServantRestockCooldown;

        public static boolean zombieDarkguardSpawn;
        public static boolean skeletonVanguardSpawn;
        public static boolean parchedSpawn;
        public static boolean iceCreeperSpawn;
        public static boolean boulderingZombieSpawn;
        public static boolean jungleZombieSpawn;
        public static boolean frozenZombieSpawn;
        public static boolean hostileSnapperSpawn;
        public static boolean hostileTropicalSlimeSpawn;
        public static boolean hostileMiniGhastSpawn;
        public static boolean hostileGnasherSpawn;
        public static boolean sunkenSkeletonSpawn;
        public static boolean hostileWildfireSpawn;
        public static boolean hostileSpiderCreederSpawn;
        public static boolean hostileTwilightGoatSpawn;

        @SubscribeEvent
        static void onLoad(final ModConfigEvent event) {
                paleGolemLimit = PALE_GOLEM_LIMIT.get();
                wardenServantLimit = WARDEN_SERVANT_LIMIT.get();
                wightServantLimit = WIGHT_SERVANT_LIMIT.get();
                creeperServantLimit = CREEPER_SERVANT_LIMIT.get();
                caerbannogRabbitServantLimit = CAERBANNOG_RABBIT_SERVANT_LIMIT.get();
                endermanServantLimit = ENDERMAN_SERVANT_LIMIT.get();
                tormentorServantLimit = TORMENTOR_SERVANT_LIMIT.get();
                shulkerServantLimit = SHULKER_SERVANT_LIMIT.get();
                witherServantLimit = WITHER_SERVANT_LIMIT.get();
                wraithNecromancerLimit = WRAITH_NECROMANCER_LIMIT.get();
                ruinsNecromancerLimit = RUINS_NECROMANCER_LIMIT.get();
                namelessOneLimit = NAMELESS_ONE_LIMIT.get();
                enderKeeperServantLimit = ENDER_KEEPER_SERVANT_LIMIT.get();
                apostleServantLimit = APOSTLE_SERVANT_LIMIT.get();
                angryMooshroomLimit = ANGRY_MOOSHROOM_LIMIT.get();
                maidFairyServantLimit = MAID_FAIRY_SERVANT_LIMIT.get();
                mushroomMonstrosityLimit = MUSHROOM_MONSTROSITY_LIMIT.get();
                vanguardChampionLimit = VANGUARD_CHAMPION_LIMIT.get();

                fairyFocusSoulCost = FAIRY_FOCUS_SOUL_COST.get();
                fairyFocusCastDuration = FAIRY_FOCUS_CAST_DURATION.get();
                fairyFocusCooldown = FAIRY_FOCUS_COOLDOWN.get();

                deepdarkVerminFocusSoulCost = DEEPDARK_VERMIN_FOCUS_SOUL_COST.get();
                deepdarkVerminFocusCastDuration = DEEPDARK_VERMIN_FOCUS_CAST_DURATION.get();
                deepdarkVerminFocusCooldown = DEEPDARK_VERMIN_FOCUS_COOLDOWN.get();

                shatteredFocusSoulCost = SHATTERED_FOCUS_SOUL_COST.get();
                shatteredFocusCastDuration = SHATTERED_FOCUS_CAST_DURATION.get();
                shatteredFocusCooldown = SHATTERED_FOCUS_COOLDOWN.get();

                callbackApostle = CALLBACK_APOSTLE.get();
                apostleSummonArmoredZombiePiglin = APOSTLE_SUMMON_ARMORED_ZOMBIE_PIGLIN.get();
                enableApostleServantArmorRenderer = ENABLE_APOSTLE_SERVANT_ARMOR_RENDERER.get();
                apostleSecondPhaseThunderStorm = APOSTLE_SECOND_PHASE_THUNDER_STORM.get();

                creeperFocusSoulCost = CREEPER_FOCUS_SOUL_COST.get();
                creeperFocusCastDuration = CREEPER_FOCUS_CAST_DURATION.get();
                creeperFocusCooldown = CREEPER_FOCUS_COOLDOWN.get();

                stareFocusSoulCost = STARE_FOCUS_SOUL_COST.get();
                stareFocusCastDuration = STARE_FOCUS_CAST_DURATION.get();
                stareFocusCooldown = STARE_FOCUS_COOLDOWN.get();

                tormentFocusSoulCost = TORMENT_FOCUS_SOUL_COST.get();
                tormentFocusCastDuration = TORMENT_FOCUS_CAST_DURATION.get();
                tormentFocusCooldown = TORMENT_FOCUS_COOLDOWN.get();

                shulkerMissileFocusSoulCost = SHULKER_MISSILE_FOCUS_SOUL_COST.get();
                shulkerMissileFocusCastDuration = SHULKER_MISSILE_FOCUS_CAST_DURATION.get();
                shulkerMissileFocusCooldown = SHULKER_MISSILE_FOCUS_COOLDOWN.get();

                mushroomMissileFocusSoulCost = MUSHROOM_MISSILE_FOCUS_SOUL_COST.get();
                mushroomMissileFocusCastDuration = MUSHROOM_MISSILE_FOCUS_CAST_DURATION.get();
                mushroomMissileFocusCooldown = MUSHROOM_MISSILE_FOCUS_COOLDOWN.get();

                infestationFocusSoulCost = INFESTATION_FOCUS_SOUL_COST.get();
                infestationFocusCastDuration = INFESTATION_FOCUS_CAST_DURATION.get();
                infestationFocusCooldown = INFESTATION_FOCUS_COOLDOWN.get();

                killerSpellSoulCost = KILLER_SPELL_SOUL_COST.get();
                killerSpellCastDuration = KILLER_SPELL_CAST_DURATION.get();
                killerSpellCooldown = KILLER_SPELL_COOLDOWN.get();
                killerSpellSummonDownDuration = KILLER_SPELL_SUMMON_DOWN_DURATION.get();

                wololoFocusSoulCost = WOLOLO_FOCUS_SOUL_COST.get();
                wololoFocusCastDuration = WOLOLO_FOCUS_CAST_DURATION.get();
                wololoFocusCooldown = WOLOLO_FOCUS_COOLDOWN.get();

                bloodRainFocusSoulCost = BLOOD_RAIN_FOCUS_SOUL_COST.get();
                bloodRainFocusCastDuration = BLOOD_RAIN_FOCUS_CAST_DURATION.get();
                bloodRainFocusCooldown = BLOOD_RAIN_FOCUS_COOLDOWN.get();

                desertPlaguesFocusSoulCost = DESERT_PLAGUES_FOCUS_SOUL_COST.get();
                desertPlaguesFocusCastDuration = DESERT_PLAGUES_FOCUS_CAST_DURATION.get();
                desertPlaguesFocusCooldown = DESERT_PLAGUES_FOCUS_COOLDOWN.get();

                agonyFocusSoulCost = AGONY_FOCUS_SOUL_COST.get();
                agonyFocusCastUp = AGONY_FOCUS_CAST_UP.get();
                agonyFocusCooldown = AGONY_FOCUS_COOLDOWN.get();

                deathRaySpellSoulCost = DEATH_RAY_SPELL_SOUL_COST.get();
                deathRaySpellCastDuration = DEATH_RAY_SPELL_CAST_DURATION.get();
                deathRaySpellCooldown = DEATH_RAY_SPELL_COOLDOWN.get();

                heavenRiftFocusSoulCost = HEAVEN_RIFT_FOCUS_SOUL_COST.get();
                heavenRiftFocusCastDuration = HEAVEN_RIFT_FOCUS_CAST_DURATION.get();
                heavenRiftFocusCooldown = HEAVEN_RIFT_FOCUS_COOLDOWN.get();

                infestationBaseDamage = INFESTATION_BASE_DAMAGE.get();
                infestationPotencyDamage = INFESTATION_POTENCY_DAMAGE.get();
                deathRayBaseDamage = DEATH_RAY_BASE_DAMAGE.get();
                deathRayPotencyDamage = DEATH_RAY_POTENCY_DAMAGE.get();
                bloodRainBaseDamage = BLOOD_RAIN_BASE_DAMAGE.get();
                bloodRainPotencyDamage = BLOOD_RAIN_POTENCY_DAMAGE.get();
                agonyFocusPotencyDamage = AGONY_FOCUS_POTENCY_DAMAGE.get();
                desertPlaguesPotencyDamage = DESERT_PLAGUES_POTENCY_DAMAGE.get();
                heavenRiftBaseDamage = HEAVEN_RIFT_BASE_DAMAGE.get();
                heavenRiftPotencyDamage = HEAVEN_RIFT_POTENCY_DAMAGE.get();
                mushroomMissileBaseDamage = MUSHROOM_MISSILE_BASE_DAMAGE.get();
                mushroomMissilePotencyDamage = MUSHROOM_MISSILE_POTENCY_DAMAGE.get();
                shulkerMissileBaseDamage = SHULKER_MISSILE_BASE_DAMAGE.get();
                shulkerMissilePotencyDamage = SHULKER_MISSILE_POTENCY_DAMAGE.get();

                championFocusSoulCost = CHAMPION_FOCUS_SOUL_COST.get();
                championFocusCastDuration = CHAMPION_FOCUS_CAST_DURATION.get();
                championFocusCooldown = CHAMPION_FOCUS_COOLDOWN.get();

                obsidianMonolithGlow = OBSIDIAN_MONOLITH_GLOW.get();

                endermanServantBlacklist = ENDERMAN_SERVANT_BLACKLIST.get();

                wraithNecromancerBossMusic = WRAITH_NECROMANCER_BOSS_MUSIC.get();
                wraithNecromancerLegacyMusic = WRAITH_NECROMANCER_LEGACY_MUSIC.get();
                mushroomMonstrosityBossMusic = MUSHROOM_MONSTROSITY_BOSS_MUSIC.get();
                parchedNecromancerBossMusic = PARCHED_NECROMANCER_BOSS_MUSIC.get();
                namelessOneBossMusic = NAMELESS_ONE_BOSS_MUSIC.get();
                namelessOneEasyMode = NAMELESS_ONE_EASY_MODE.get();
                enableNamelessOneDesertPlagues = ENABLE_NAMELESS_ONE_DESERT_PLAGUES.get();
                enableNamelessOneAvada = ENABLE_NAMELESS_ONE_AVADA.get();
                enableNamelessOneThunderstorm = ENABLE_NAMELESS_ONE_THUNDERSTORM.get();
                enableNamelessOneHeavenRift = ENABLE_NAMELESS_ONE_HEAVEN_RIFT.get();
                enableNamelessOneEnchantment = ENABLE_NAMELESS_ONE_ENCHANTMENT.get();
                enableNamelessOneSupportSpells = ENABLE_NAMELESS_ONE_SUPPORT_SPELLS.get();
                enableNamelessOneScarletVexSummon = ENABLE_NAMELESS_ONE_SCARLET_VEX_SUMMON.get();
                enableNamelessBoltDeathFire = ENABLE_NAMELESS_BOLT_DEATH_FIRE.get();
                mushroomMonstrosityEasyMode = MUSHROOM_MONSTROSITY_EASY_MODE.get();

                deathCapMushroomCooldown = DEATH_CAP_MUSHROOM_COOLDOWN.get();
                deathCapMushroomMaxUsages = DEATH_CAP_MUSHROOM_MAX_USAGES.get();
                deathCapMushroomEffectDuration = DEATH_CAP_MUSHROOM_EFFECT_DURATION.get();
                deathCapMushroomEffectLevel = DEATH_CAP_MUSHROOM_EFFECT_LEVEL.get();
                swellingPendantMaxSlimeValue = SWELLING_PENDANT_MAX_SLIME_VALUE.get();

                truthSeekerDamage = TRUTH_SEEKER_DAMAGE.get();
                truthSeekerAttackSpeed = TRUTH_SEEKER_ATTACK_SPEED.get();

                frostScytheDamage = FROST_SCYTHE_DAMAGE.get();
                frostScytheAttackSpeed = FROST_SCYTHE_ATTACK_SPEED.get();

                glaiveDamage = GLAIVE_DAMAGE.get();
                glaiveAttackSpeed = GLAIVE_ATTACK_SPEED.get();
                moonlightCutDamage = MOONLIGHT_CUT_DAMAGE.get();
                moonlightCutAttackSpeed = MOONLIGHT_CUT_ATTACK_SPEED.get();
                sunGraceDamage = SUN_GRACE_DAMAGE.get();
                sunGraceAttackSpeed = SUN_GRACE_ATTACK_SPEED.get();
                maceDamage = MACE_DAMAGE.get();
                maceAttackSpeed = MACE_ATTACK_SPEED.get();
                mooShroomMaceDamage = MOO_SHROOM_MACE_DAMAGE.get();
                mooShroomMaceAttackSpeed = MOO_SHROOM_MACE_ATTACK_SPEED.get();
                claymoreDamage = CLAYMORE_DAMAGE.get();
                claymoreAttackSpeed = CLAYMORE_ATTACK_SPEED.get();
                starlessNightDamage = STARLESS_NIGHT_DAMAGE.get();
                starlessNightAttackSpeed = STARLESS_NIGHT_ATTACK_SPEED.get();
                obsidianClaymoreDamage = OBSIDIAN_CLAYMORE_DAMAGE.get();
                obsidianClaymoreAttackSpeed = OBSIDIAN_CLAYMORE_ATTACK_SPEED.get();
                graveBaneDamage = GRAVE_BANE_DAMAGE.get();
                graveBaneAttackSpeed = GRAVE_BANE_ATTACK_SPEED.get();

                shulkerMissileMaxPotionAmplifier = SHULKER_MISSILE_MAX_POTION_AMPLIFIER.get();
                shulkerMissileMaxPotionDuration = SHULKER_MISSILE_MAX_POTION_DURATION.get();
                mushroomMissileMaxPotionAmplifier = MUSHROOM_MISSILE_MAX_POTION_AMPLIFIER.get();
                mushroomMissileMaxPotionDuration = MUSHROOM_MISSILE_MAX_POTION_DURATION.get();
                mushroomMissileAreaCloudDuration = MUSHROOM_MISSILE_AREA_CLOUD_DURATION.get();

                enableBlastlingEnhancement = ENABLE_BLASTLING_ENHANCEMENT.get();
                enableSnarelingEnhancement = ENABLE_SNARELING_ENHANCEMENT.get();
                enableWatchlingEnhancement = ENABLE_WATCHLING_ENHANCEMENT.get();
                enableSquallGolemEnhancement = ENABLE_SQUALLGOLEM_ENHANCEMENT.get();
                enableLeapleafEnhancement = ENABLE_LEAPLEAF_ENHANCEMENT.get();
                enableWildfireEnhancement = ENABLE_WILDFIRE_ENHANCEMENT.get();
                enablePikerEnhancement = ENABLE_PIKER_ENHANCEMENT.get();
                enableBroodMotherEnhancement = ENABLE_BROODMOTHER_ENHANCEMENT.get();
                croneServantAllowGasBrew = CRONE_SERVANT_ALLOW_GAS_BREW.get();
                allowMultiShotProjectiles = ALLOW_MULTISHOT_PROJECTILES.get();

                foundationScanAbove = FOUNDATION_SCAN_ABOVE.get();
                foundationScanBelow = FOUNDATION_SCAN_BELOW.get();
                foundationWhitelist = FOUNDATION_WHITELIST.get();

                gildedIngotMaxEnhancements = GLOWING_EMBER_MAX_ENHANCEMENTS.get();

                fireballFeastFocusSoulCost = FIREBALL_FEAST_FOCUS_SOUL_COST.get();
                fireballFeastFocusCastDuration = FIREBALL_FEAST_FOCUS_CAST_DURATION.get();
                fireballFeastFocusCooldown = FIREBALL_FEAST_FOCUS_COOLDOWN.get();

                mooshroomFocusSoulCost = MOOSHROOM_FOCUS_SOUL_COST.get();
                mooshroomFocusCastDuration = MOOSHROOM_FOCUS_CAST_DURATION.get();
                mooshroomFocusCooldown = MOOSHROOM_FOCUS_COOLDOWN.get();

                sorcererServantRestockLimit = SORCERER_SERVANT_RESTOCK_LIMIT.get();
                sorcererServantRestockInterval = SORCERER_SERVANT_RESTOCK_INTERVAL.get();
                sorcererServantRestockCooldown = SORCERER_SERVANT_RESTOCK_COOLDOWN.get();
                sorcererServantTradeMaxUses = SORCERER_SERVANT_TRADE_MAX_USES.get();
                heresiarchServantTradeMaxUses = HERESIARCH_SERVANT_TRADE_MAX_USES.get();
                heresiarchServantTradeCount = HERESIARCH_SERVANT_TRADE_COUNT.get();
                heresiarchServantRestockLimit = HERESIARCH_SERVANT_RESTOCK_LIMIT.get();
                heresiarchServantRestockInterval = HERESIARCH_SERVANT_RESTOCK_INTERVAL.get();
                heresiarchServantRestockCooldown = HERESIARCH_SERVANT_RESTOCK_COOLDOWN.get();
                croneServantTradeMaxUses = CRONE_SERVANT_TRADE_MAX_USES.get();
                croneServantTradeCount = CRONE_SERVANT_TRADE_COUNT.get();
                croneServantRestockLimit = CRONE_SERVANT_RESTOCK_LIMIT.get();
                croneServantRestockInterval = CRONE_SERVANT_RESTOCK_INTERVAL.get();
                croneServantRestockCooldown = CRONE_SERVANT_RESTOCK_COOLDOWN.get();

                zombieDarkguardSpawn = ZOMBIE_DARKGUARD_SPAWN.get();
                skeletonVanguardSpawn = SKELETON_VANGUARD_SPAWN.get();
                parchedSpawn = PARCHED_SPAWN.get();
                iceCreeperSpawn = ICE_CREEPER_SPAWN.get();
                boulderingZombieSpawn = BOULDERING_ZOMBIE_SPAWN.get();
                jungleZombieSpawn = JUNGLE_ZOMBIE_SPAWN.get();
                frozenZombieSpawn = FROZEN_ZOMBIE_SPAWN.get();
                hostileSnapperSpawn = HOSTILE_SNAPPER_SPAWN.get();
                hostileTropicalSlimeSpawn = HOSTILE_TROPICAL_SLIME_SPAWN.get();
                hostileMiniGhastSpawn = HOSTILE_MINI_GHAST_SPAWN.get();
                hostileGnasherSpawn = HOSTILE_GNASHER_SPAWN.get();
                sunkenSkeletonSpawn = SUNKEN_SKELETON_SPAWN.get();
                hostileWildfireSpawn = HOSTILE_WILDFIRE_SPAWN.get();
                hostileSpiderCreederSpawn = HOSTILE_SPIDER_CREEDER_SPAWN.get();
                hostileTwilightGoatSpawn = HOSTILE_TWILIGHT_GOAT_SPAWN.get();

                swampMineFocusSoulCost = SWAMP_MINE_FOCUS_SOUL_COST.get();
                swampMineFocusCastDuration = SWAMP_MINE_FOCUS_CAST_DURATION.get();
                swampMineFocusCooldown = SWAMP_MINE_FOCUS_COOLDOWN.get();
                swampMinePotencyExplosion = SWAMP_MINE_POTENCY_EXPLOSION.get();

                ghostMissileFocusSoulCost = GHOST_MISSILE_FOCUS_SOUL_COST.get();
                ghostMissileFocusCastDuration = GHOST_MISSILE_FOCUS_CAST_DURATION.get();
                ghostMissileFocusCooldown = GHOST_MISSILE_FOCUS_COOLDOWN.get();
                ghostMissileBaseDamage = GHOST_MISSILE_BASE_DAMAGE.get();
                ghostMissilePotencyDamage = GHOST_MISSILE_POTENCY_DAMAGE.get();

                chipFocusSoulCost = CHIP_FOCUS_SOUL_COST.get();
                chipFocusCastDuration = CHIP_FOCUS_CAST_DURATION.get();
                chipFocusCooldown = CHIP_FOCUS_COOLDOWN.get();
                chipFocusPotencyDamage = CHIP_FOCUS_POTENCY_DAMAGE.get();

                chipRainFocusSoulCost = CHIP_RAIN_FOCUS_SOUL_COST.get();
                chipRainFocusCastUp = CHIP_RAIN_FOCUS_CAST_UP.get();
                chipRainFocusCooldown = CHIP_RAIN_FOCUS_COOLDOWN.get();
                chipRainFocusPotencyDamage = CHIP_RAIN_FOCUS_POTENCY_DAMAGE.get();

                fateDiceFocusSoulCost = FATE_DICE_FOCUS_SOUL_COST.get();
                fateDiceFocusCastDuration = FATE_DICE_FOCUS_CAST_DURATION.get();
                fateDiceFocusCooldown = FATE_DICE_FOCUS_COOLDOWN.get();
                fateDiceFocusPotencyDamage = FATE_DICE_FOCUS_POTENCY_DAMAGE.get();
        }

        public static void loadConfig(ForgeConfigSpec config, String path) {
                File configFile = new File(path);
                File parentDir = configFile.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                        parentDir.mkdirs();
                }
                CommentedFileConfig file = CommentedFileConfig.builder(configFile).sync()
                                .autosave().writingMode(WritingMode.REPLACE).build();
                file.load();
                config.setConfig(file);
        }
}
