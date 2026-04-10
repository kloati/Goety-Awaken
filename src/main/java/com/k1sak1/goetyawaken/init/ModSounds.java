package com.k1sak1.goetyawaken.init;

import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
        public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,
                        GoetyAwaken.MODID);

        public static final RegistryObject<SoundEvent> BAKASMUSIC = SOUNDS.register("bakasmusic",
                        () -> SoundEvent.createVariableRangeEvent(
                                        new ResourceLocation(GoetyAwaken.MODID, "bakasmusic")));
        public static final RegistryObject<SoundEvent> BAKA = create("baka");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_ATTACK_1 = create("royal_guard_attack_1");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_ATTACK_2 = create("royal_guard_attack_2");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_ATTACK_3 = create("royal_guard_attack_3");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_ATTACK_4 = create("royal_guard_attack_4");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_IDLE_1 = create("royal_guard_idle_1");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_IDLE_2 = create("royal_guard_idle_2");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_IDLE_3 = create("royal_guard_idle_3");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_IDLE_4 = create("royal_guard_idle_4");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_SHIELD_BREAK_1 = create(
                        "royal_guard_shield_break_1");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_SHIELD_BREAK_2 = create(
                        "royal_guard_shield_break_2");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_SHIELD_BREAK_3 = create(
                        "royal_guard_shield_break_3");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_HURT_ARMOR_1 = create("royal_guard_hurt_armor_1");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_HURT_ARMOR_2 = create("royal_guard_hurt_armor_2");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_HURT_ARMOR_3 = create("royal_guard_hurt_armor_3");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_HURT_ARMOR_4 = create("royal_guard_hurt_armor_4");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_HURT_ARMOR_5 = create("royal_guard_hurt_armor_5");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_HURT = create("royal_guard_hurt");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_HURT_3 = create("royal_guard_hurt_3");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_DEATH_ARMOR_1 = create("royal_guard_death_armor_1");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_DEATH_ARMOR_2 = create("royal_guard_death_armor_2");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_DEATH_ARMOR_3 = create("royal_guard_death_armor_3");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_DEATH_ARMOR_4 = create("royal_guard_death_armor_4");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_DEATH_ARMOR_5 = create("royal_guard_death_armor_5");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_DEATH_ARMOR_6 = create("royal_guard_death_armor_6");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_DEATH_ARMOR_7 = create("royal_guard_death_armor_7");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_DEATH_1 = create("royal_guard_death_1");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_DEATH_2 = create("royal_guard_death_2");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_SMASH_1 = create("royal_guard_smash_1");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_SMASH_2 = create("royal_guard_smash_2");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_SMASH_3 = create("royal_guard_smash_3");

        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_AMBIENT_1 = create(
                        "wraith_necromancer_ambient_1");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_AMBIENT_2 = create(
                        "wraith_necromancer_ambient_2");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_AMBIENT_3 = create(
                        "wraith_necromancer_ambient_3");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_AMBIENT_4 = create(
                        "wraith_necromancer_ambient_4");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_AMBIENT_5 = create(
                        "wraith_necromancer_ambient_5");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_HURT_1 = create("wraith_necromancer_hurt_1");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_HURT_2 = create("wraith_necromancer_hurt_2");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_HURT_3 = create("wraith_necromancer_hurt_3");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_DEATH_1 = create(
                        "wraith_necromancer_death_1");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_DEATH_2 = create(
                        "wraith_necromancer_death_2");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_FLOAT_1 = create(
                        "wraith_necromancer_float_1");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_FLOAT_2 = create(
                        "wraith_necromancer_float_2");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_FLOAT_3 = create(
                        "wraith_necromancer_float_3");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_FLOAT_4 = create(
                        "wraith_necromancer_float_4");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_FLOAT_5 = create(
                        "wraith_necromancer_float_5");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_ATTACK_1 = create(
                        "wraith_necromancer_attack_1");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_ATTACK_2 = create(
                        "wraith_necromancer_attack_2");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_ATTACK_3 = create(
                        "wraith_necromancer_attack_3");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_SUMMON_START = create(
                        "wraith_necromancer_summon_start");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_SUMMON_FINISH = create(
                        "wraith_necromancer_summon_finish");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_THEME = create("wraith_necromancer_theme");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_THEME2 = create("wraith_necromancer_theme2");

        public static final RegistryObject<SoundEvent> DEATHCAPMUSHROOMEAT1 = create("deathcapmushroomeat1");
        public static final RegistryObject<SoundEvent> DEATHCAPMUSHROOMEAT2 = create("deathcapmushroomeat2");
        public static final RegistryObject<SoundEvent> DEATHCAPMUSHROOMEAT3 = create("deathcapmushroomeat3");
        public static final RegistryObject<SoundEvent> HARP_CROSSBOW_SHOOT_1 = create("harp_crossbow_shoot_1");
        public static final RegistryObject<SoundEvent> HARP_CROSSBOW_SHOOT_2 = create("harp_crossbow_shoot_2");
        public static final RegistryObject<SoundEvent> HARP_CROSSBOW_SHOOT_3 = create("harp_crossbow_shoot_3");
        public static final RegistryObject<SoundEvent> HARP_CROSSBOW_SHOOT_4 = create("harp_crossbow_shoot_4");
        public static final RegistryObject<SoundEvent> MUSHROOM_MONSTROSITY_BATTLE_MUSIC = create(
                        "mushroom_monstrosity_battle_music");

        public static final RegistryObject<SoundEvent> MOOSHROOM_MONSTROSITY_DISC = create(
                        "mooshroom_monstrosity_disc");

        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_END = create("wraith_necromancer_end");

        public static final RegistryObject<SoundEvent> PARCHED_IDLE_1 = create("parched_idle_1");
        public static final RegistryObject<SoundEvent> PARCHED_IDLE_2 = create("parched_idle_2");
        public static final RegistryObject<SoundEvent> PARCHED_IDLE_3 = create("parched_idle_3");
        public static final RegistryObject<SoundEvent> PARCHED_IDLE_4 = create("parched_idle_4");
        public static final RegistryObject<SoundEvent> PARCHED_STEP_1 = create("parched_step_1");
        public static final RegistryObject<SoundEvent> PARCHED_STEP_2 = create("parched_step_2");
        public static final RegistryObject<SoundEvent> PARCHED_STEP_3 = create("parched_step_3");
        public static final RegistryObject<SoundEvent> PARCHED_STEP_4 = create("parched_step_4");
        public static final RegistryObject<SoundEvent> PARCHED_HURT_1 = create("parched_hurt_1");
        public static final RegistryObject<SoundEvent> PARCHED_HURT_2 = create("parched_hurt_2");
        public static final RegistryObject<SoundEvent> PARCHED_HURT_3 = create("parched_hurt_3");
        public static final RegistryObject<SoundEvent> PARCHED_HURT_4 = create("parched_hurt_4");
        public static final RegistryObject<SoundEvent> PARCHED_DEATH = create("parched_death");

        public static final RegistryObject<SoundEvent> PARCHED_SHOOT_1 = create("parched_shoot_1");
        public static final RegistryObject<SoundEvent> PARCHED_SHOOT_2 = create("parched_shoot_2");
        public static final RegistryObject<SoundEvent> PARCHED_LAUGH_1 = create("parched_laugh_1");
        public static final RegistryObject<SoundEvent> PARCHED_LAUGH_2 = create("parched_laugh_2");
        public static final RegistryObject<SoundEvent> PARCHED_LAUGH_3 = create("parched_laugh_3");
        public static final RegistryObject<SoundEvent> PARCHED_HURT_ARMORED_1 = create("parched_hurt_armored_1");
        public static final RegistryObject<SoundEvent> PARCHED_HURT_ARMORED_2 = create("parched_hurt_armored_2");
        public static final RegistryObject<SoundEvent> PARCHED_SUMMON_PREPARE = create("parched_summon_prepare");
        public static final RegistryObject<SoundEvent> PARCHED_SUMMON_SPAWN = create("parched_summon_spawn");
        public static final RegistryObject<SoundEvent> PARCHED_SPELL = create("parched_spell");
        public static final RegistryObject<SoundEvent> PARCHED_DEATH_NEW = create("parched_death_new");
        public static final RegistryObject<SoundEvent> PARCHED_HURT_1_NEW = create("parched_hurt_1_new");
        public static final RegistryObject<SoundEvent> PARCHED_HURT_2_NEW = create("parched_hurt_2_new");
        public static final RegistryObject<SoundEvent> PARCHED_HURT_3_NEW = create("parched_hurt_3_new");

        public static final RegistryObject<SoundEvent> NAMELESS_ONE_HURT_1 = create("nameless_one_hurt_1");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_HURT_2 = create("nameless_one_hurt_2");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_HURT_3 = create("nameless_one_hurt_3");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_HURT_4 = create("nameless_one_hurt_4");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_LAUGH_SHORT_1 = create(
                        "nameless_one_laugh_short_1");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_LAUGH_SHORT_2 = create(
                        "nameless_one_laugh_short_2");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_LAUGH_SHORT_3 = create(
                        "nameless_one_laugh_short_3");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_LAUGH_LONG_1 = create("nameless_one_laugh_long_1");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_LAUGH_LONG_2 = create("nameless_one_laugh_long_2");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_LAUGH_LONG_3 = create("nameless_one_laugh_long_3");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_IDLE_1 = create("nameless_one_idle_1");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_IDLE_2 = create("nameless_one_idle_2");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_IDLE_3 = create("nameless_one_idle_3");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_DEATH = create("nameless_one_death");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_SHOOT_1 = create("nameless_one_shoot_1");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_SHOOT_2 = create("nameless_one_shoot_2");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_SHOOT_3 = create("nameless_one_shoot_3");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_FLY_1 = create("nameless_one_fly_1");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_FLY_2 = create("nameless_one_fly_2");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_FLY_3 = create("nameless_one_fly_3");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_FLY_4 = create("nameless_one_fly_4");

        public static final RegistryObject<SoundEvent> RUINS_NECROMANCER_THEME = create("ruins_necromancer_theme");

        public static final RegistryObject<SoundEvent> NAMELESS_FIGHT_MUSIC = create("namelessfight");

        public static final RegistryObject<SoundEvent> BOULDERING_ZOMBIE_IDLE_1 = create("bouldering_zombie_idle_1");
        public static final RegistryObject<SoundEvent> BOULDERING_ZOMBIE_IDLE_2 = create("bouldering_zombie_idle_2");
        public static final RegistryObject<SoundEvent> BOULDERING_ZOMBIE_IDLE_3 = create("bouldering_zombie_idle_3");
        public static final RegistryObject<SoundEvent> BOULDERING_ZOMBIE_IDLE_4 = create("bouldering_zombie_idle_4");
        public static final RegistryObject<SoundEvent> BOULDERING_ZOMBIE_IDLE_5 = create("bouldering_zombie_idle_5");
        public static final RegistryObject<SoundEvent> BOULDERING_ZOMBIE_CLIMB_1 = create("bouldering_zombie_climb_1");
        public static final RegistryObject<SoundEvent> BOULDERING_ZOMBIE_CLIMB_2 = create("bouldering_zombie_climb_2");
        public static final RegistryObject<SoundEvent> BOULDERING_ZOMBIE_CLIMB_3 = create("bouldering_zombie_climb_3");
        public static final RegistryObject<SoundEvent> BOULDERING_ZOMBIE_CLIMB_4 = create("bouldering_zombie_climb_4");
        public static final RegistryObject<SoundEvent> BOULDERING_ZOMBIE_HURT_1 = create("bouldering_zombie_hurt_1");
        public static final RegistryObject<SoundEvent> BOULDERING_ZOMBIE_HURT_2 = create("bouldering_zombie_hurt_2");
        public static final RegistryObject<SoundEvent> BOULDERING_ZOMBIE_HURT_3 = create("bouldering_zombie_hurt_3");
        public static final RegistryObject<SoundEvent> BOULDERING_ZOMBIE_HURT_4 = create("bouldering_zombie_hurt_4");
        public static final RegistryObject<SoundEvent> BOULDERING_ZOMBIE_DEATH = create("bouldering_zombie_death");

        public static final RegistryObject<SoundEvent> ILLUSIONER_ARROW1 = create("illusioner_arrow1");
        public static final RegistryObject<SoundEvent> ILLUSIONER_ARROW2 = create("illusioner_arrow2");
        public static final RegistryObject<SoundEvent> ILLUSIONER_ARROW3 = create("illusioner_arrow3");
        public static final RegistryObject<SoundEvent> ILLUSIONER_ARROW4 = create("illusioner_arrow4");

        public static final RegistryObject<SoundEvent> ARCH_ILLUSIONER_DEATH_1 = create("arch_illusioner_death_1");
        public static final RegistryObject<SoundEvent> ARCH_ILLUSIONER_DEATH_2 = create("arch_illusioner_death_2");

        public static final RegistryObject<SoundEvent> PURE_LIGHT_1 = create("pure_light_1");
        public static final RegistryObject<SoundEvent> PURE_LIGHT_2 = create("pure_light_2");

        public static final RegistryObject<SoundEvent> WIND_BURST_1 = create("wind_burst1");
        public static final RegistryObject<SoundEvent> WIND_BURST_2 = create("wind_burst2");
        public static final RegistryObject<SoundEvent> WIND_BURST_3 = create("wind_burst3");

        public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_OPEN_SHUTTER = create(
                        "trial_spawner_open_shutter");
        public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_CLOSE_SHUTTER = create(
                        "trial_spawner_close_shutter");

        public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_DETECT_PLAYER_1 = create(
                        "trial_spawner_detect_player1");
        public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_DETECT_PLAYER_2 = create(
                        "trial_spawner_detect_player2");
        public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_DETECT_PLAYER_3 = create(
                        "trial_spawner_detect_player3");

        public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_EJECT_ITEM_1 = create("trial_spawner_eject_item1");

        public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_SPAWN_1 = create("trial_spawner_spawn1");
        public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_SPAWN_2 = create("trial_spawner_spawn2");
        public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_SPAWN_3 = create("trial_spawner_spawn3");
        public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_SPAWN_4 = create("trial_spawner_spawn4");

        public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_AMBIENT_1 = create("trial_spawner_ambient1");
        public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_AMBIENT_2 = create("trial_spawner_ambient2");
        public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_AMBIENT_3 = create("trial_spawner_ambient3");
        public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_AMBIENT_4 = create("trial_spawner_ambient4");
        public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_AMBIENT_5 = create("trial_spawner_ambient5");

        public static final RegistryObject<SoundEvent> VAULT_DEACTIVATE = create("vault_deactivate");

        public static final RegistryObject<SoundEvent> VAULT_ACTIVATE = create("vault_activate");

        public static final RegistryObject<SoundEvent> VAULT_INSERT = create("vault_insert");

        public static final RegistryObject<SoundEvent> VAULT_OPEN_SHUTTER = create("vault_open_shutter");

        public static final RegistryObject<SoundEvent> VAULT_EJECT_1 = create("vault_eject1");
        public static final RegistryObject<SoundEvent> VAULT_EJECT_2 = create("vault_eject2");
        public static final RegistryObject<SoundEvent> VAULT_EJECT_3 = create("vault_eject3");

        public static final RegistryObject<SoundEvent> VAULT_AMBIENT_1 = create("vault_ambient1");
        public static final RegistryObject<SoundEvent> VAULT_AMBIENT_2 = create("vault_ambient2");
        public static final RegistryObject<SoundEvent> VAULT_AMBIENT_3 = create("vault_ambient3");

        public static final RegistryObject<SoundEvent> VAULT_INSERT_FAIL = create("vault_insert_fail");
        public static final RegistryObject<SoundEvent> VAULT_REJECT_REWARDED_PLAYER = create(
                        "vault_reject_rewarded_player");

        public static final RegistryObject<SoundEvent> CREEDER_IDLE = create("creeder_idle");
        public static final RegistryObject<SoundEvent> CREEDER_HURT = create("creeder_hurt");
        public static final RegistryObject<SoundEvent> CREEDER_DEATH = create("creeder_death");
        public static final RegistryObject<SoundEvent> CREEDER_EXPLOSION = create("creeder_explosion");
        public static final RegistryObject<SoundEvent> CREEDER_HISS = create("creeder_hiss");

        public static final RegistryObject<SoundEvent> ANCIENT_HUNT_PRE = create("ancient_hunt_pre");
        public static final RegistryObject<SoundEvent> ANCIENT_HUNT_LOOP = create("ancient_hunt_loop");
        public static final RegistryObject<SoundEvent> ANCIENT_HUNT_POST = create("ancient_hunt_post");

        public static final RegistryObject<SoundEvent> MUSIC_DISC_ANCIENT = create("music_disc_ancient");

        private static RegistryObject<SoundEvent> create(String name) {
                return SOUNDS.register(name,
                                () -> SoundEvent.createVariableRangeEvent(
                                                new ResourceLocation(GoetyAwaken.MODID, name)));
        }
}