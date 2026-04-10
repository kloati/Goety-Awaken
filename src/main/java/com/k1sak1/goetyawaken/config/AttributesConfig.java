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

        // Ice Creeper Servant
        public static final ForgeConfigSpec.ConfigValue<Double> IceCreeperServantHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> IceCreeperServantArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> IceCreeperServantArmorToughness;

        // Spider Creeder
        public static final ForgeConfigSpec.ConfigValue<Double> SpiderCreederHealth;
        public static final ForgeConfigSpec.ConfigValue<Double> SpiderCreederArmor;
        public static final ForgeConfigSpec.ConfigValue<Double> SpiderCreederArmorToughness;

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
                                .comment("How much Max Health Mushroom Monstrosities have, Default: 1000.0")
                                .defineInRange("mushroomMonstrosityHealth", 1000.0, 1.0, Double.MAX_VALUE);
                MushroomMonstrosityArmor = BUILDER
                                .comment("How much natural armor points Mushroom Monstrosities have, Default: 12.0")
                                .defineInRange("mushroomMonstrosityArmor", 12.0, 0.0, Double.MAX_VALUE);
                MushroomMonstrosityArmorToughness = BUILDER
                                .comment("How much armor toughness Mushroom Monstrosities have, Default: 8.0")
                                .defineInRange("mushroomMonstrosityArmorToughness", 8.0, 0.0, Double.MAX_VALUE);
                MushroomMonstrosityDamage = BUILDER
                                .comment("How much damage Mushroom Monstrosities deals, Default: 24.0")
                                .defineInRange("mushroomMonstrosityDamage", 24.0, 1.0, Double.MAX_VALUE);
                MushroomMonstrosityFollowRange = BUILDER
                                .comment("How much following/detection range Mushroom Monstrosities have, Default: 32.0")
                                .defineInRange("mushroomMonstrosityFollowRange", 32.0, 1.0, 2048.0);
                BUILDER.pop();

                // Angry Mooshroom
                BUILDER.push("Angry Mooshroom");
                AngryMooshroomHealth = BUILDER.comment("How much Max Health Angry Mooshrooms have, Default: 30.0")
                                .defineInRange("angryMooshroomHealth", 30.0, 1.0, Double.MAX_VALUE);
                AngryMooshroomArmor = BUILDER
                                .comment("How much natural armor points Angry Mooshrooms have, Default: 0.0")
                                .defineInRange("angryMooshroomArmor", 0.0, 0.0, Double.MAX_VALUE);
                AngryMooshroomDamage = BUILDER.comment("How much damage Angry Mooshrooms deals, Default: 6.0")
                                .defineInRange("angryMooshroomDamage", 6.0, 1.0, Double.MAX_VALUE);
                AngryMooshroomMovementSpeed = BUILDER
                                .comment("How much movement speed Angry Mooshrooms have, Default: 0.3")
                                .defineInRange("angryMooshroomMovementSpeed", 0.3, 0.0, Double.MAX_VALUE);
                BUILDER.pop();

                // Wraith Necromancer
                BUILDER.push("Wraith Necromancer");
                WraithNecromancerHealth = BUILDER
                                .comment("How much Max Health Wraith Necromancers have, Default: 220.0")
                                .defineInRange("wraithNecromancerHealth", 220.0, 1.0, Double.MAX_VALUE);
                WraithNecromancerArmor = BUILDER
                                .comment("How much natural armor points Wraith Necromancers have, Default: 0.0")
                                .defineInRange("wraithNecromancerArmor", 0.0, 0.0, Double.MAX_VALUE);
                WraithNecromancerDamage = BUILDER.comment("How much damage Wraith Necromancers deals, Default: 8.0")
                                .defineInRange("wraithNecromancerDamage", 8.0, 1.0, Double.MAX_VALUE);
                WraithNecromancerFollowRange = BUILDER
                                .comment("How much following/detection range Wraith Necromancers have, Default: 32.0")
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
                ParchedNecromancerDamage = BUILDER.comment("How much damage Parched Necromancers deals, Default: 6.0")
                                .defineInRange("parchedNecromancerDamage", 6.0, 0.0, Double.MAX_VALUE);
                ParchedNecromancerFollowRange = BUILDER
                                .comment("How much follow range Parched Necromancers have, Default: 48.0")
                                .defineInRange("parchedNecromancerFollowRange", 48.0, 1.0, 2048.0);
                BUILDER.pop();

                BUILDER.push("Nameless One");
                NamelessOneHealth = BUILDER
                                .comment("How much Max Health Nameless One have, Default: 444.0")
                                .defineInRange("namelessOneHealth", 444.0, 1.0, Double.MAX_VALUE);
                NamelessOneArmor = BUILDER
                                .comment("How much natural armor points Nameless One have, Default: 12.0")
                                .defineInRange("namelessOneArmor", 12.0, 0.0, Double.MAX_VALUE);
                NamelessOneArmorToughness = BUILDER
                                .comment("How much armor toughness Nameless One have, Default: 8.0")
                                .defineInRange("namelessOneArmorToughness", 8.0, 0.0, Double.MAX_VALUE);
                NamelessOneDamage = BUILDER.comment("How much damage Parched Necromancers deals, Default: 14.0")
                                .defineInRange("namelessOneDamage", 14.0, 0.0, Double.MAX_VALUE);
                NamelessOneFollowRange = BUILDER
                                .comment("How much follow range Nameless One have, Default: 48.0")
                                .defineInRange("namelessOneFollowRange", 48.0, 1.0, 2048.0);

                NamelessOneDamageCapPercent = BUILDER
                                .comment("Damage cap percentage for Nameless One (fraction of max health), Default: 0.07 (7%)")
                                .defineInRange("namelessOneDamageCapPercent", 0.07, 0.01, 1.0);
                NamelessOneHitCooldown = BUILDER
                                .comment("Hit cooldown in ticks for Nameless One damage cap, Default: 10")
                                .defineInRange("namelessOneHitCooldown", 10, 1, 100);
                NamelessOneDynamicReductionTime = BUILDER
                                .comment("Dynamic damage reduction time in ticks for Nameless One, Default: 20")
                                .defineInRange("namelessOneDynamicReductionTime", 20, 1, 100);

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
                                .comment("How much Max Health Vanguard Champions have, Default: 30.0")
                                .defineInRange("vanguardChampionHealth", 30.0, 1.0, Double.MAX_VALUE);
                VanguardChampionMovementSpeed = BUILDER
                                .comment("How much movement speed Vanguard Champions have, Default: 0.3")
                                .defineInRange("vanguardChampionMovementSpeed", 0.3, 0.0, Double.MAX_VALUE);
                VanguardChampionDamage = BUILDER
                                .comment("How much damage Vanguard Champions deals, Default: 5.0")
                                .defineInRange("vanguardChampionDamage", 5.0, 1.0, Double.MAX_VALUE);
                VanguardChampionAttackKnockback = BUILDER
                                .comment("How much attack knockback Vanguard Champions have, Default: 1.0")
                                .defineInRange("vanguardChampionAttackKnockback", 1.0, 0.0, Double.MAX_VALUE);
                VanguardChampionArmor = BUILDER
                                .comment("How much natural armor points Vanguard Champions have, Default: 24.0")
                                .defineInRange("vanguardChampionArmor", 24.0, 0.0, Double.MAX_VALUE);
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
                TowerWraithDamage = BUILDER.comment("How much damage Tower Wraiths deals, Default: 6.0")
                                .defineInRange("towerWraithDamage", 6.0, 1.0, Double.MAX_VALUE);
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