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
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_ATTACK = create("royal_guard_attack");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_IDLE = create("royal_guard_idle");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_SHIELD_BREAK = create("royal_guard_shield_break");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_HURT_ARMOR = create("royal_guard_hurt_armor");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_HURT = create("royal_guard_hurt");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_DEATH_ARMOR = create("royal_guard_death_armor");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_DEATH = create("royal_guard_death");
        public static final RegistryObject<SoundEvent> ROYAL_GUARD_SMASH = create("royal_guard_smash");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_AMBIENT = create(
                        "wraith_necromancer_ambient");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_HURT = create("wraith_necromancer_hurt");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_DEATH = create("wraith_necromancer_death");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_FLOAT = create("wraith_necromancer_float");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_ATTACK = create("wraith_necromancer_attack");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_SUMMON_START = create(
                        "wraith_necromancer_summon_start");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_SUMMON_FINISH = create(
                        "wraith_necromancer_summon_finish");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_THEME = create("wraith_necromancer_theme");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_THEME2 = create("wraith_necromancer_theme2");
        public static final RegistryObject<SoundEvent> DEATHCAPMUSHROOMEAT = create("deathcapmushroomeat");
        public static final RegistryObject<SoundEvent> HARP_CROSSBOW_SHOOT = create("harp_crossbow_shoot");
        public static final RegistryObject<SoundEvent> MUSHROOM_MONSTROSITY_BATTLE_MUSIC = create(
                        "mushroom_monstrosity_battle_music");
        public static final RegistryObject<SoundEvent> MOOSHROOM_MONSTROSITY_DISC = create(
                        "mooshroom_monstrosity_disc");
        public static final RegistryObject<SoundEvent> WRAITH_NECROMANCER_END = create("wraith_necromancer_end");
        public static final RegistryObject<SoundEvent> PARCHED_IDLE = create("parched_idle");
        public static final RegistryObject<SoundEvent> PARCHED_STEP = create("parched_step");
        public static final RegistryObject<SoundEvent> PARCHED_HURT = create("parched_hurt");
        public static final RegistryObject<SoundEvent> PARCHED_DEATH = create("parched_death");
        public static final RegistryObject<SoundEvent> PARCHED_SHOOT = create("parched_shoot");
        public static final RegistryObject<SoundEvent> PARCHED_LAUGH = create("parched_laugh");
        public static final RegistryObject<SoundEvent> PARCHED_HURT_ARMORED = create("parched_hurt_armored");
        public static final RegistryObject<SoundEvent> PARCHED_SUMMON_PREPARE = create("parched_summon_prepare");
        public static final RegistryObject<SoundEvent> PARCHED_SUMMON_SPAWN = create("parched_summon_spawn");
        public static final RegistryObject<SoundEvent> PARCHED_SPELL = create("parched_spell");
        public static final RegistryObject<SoundEvent> PARCHED_DEATH_NEW = create("parched_death_new");
        public static final RegistryObject<SoundEvent> PARCHED_HURT_NEW = create("parched_hurt_new");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_HURT = create("nameless_one_hurt");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_LAUGH_SHORT = create("nameless_one_laugh_short");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_LAUGH_LONG = create("nameless_one_laugh_long");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_IDLE = create("nameless_one_idle");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_DEATH = create("nameless_one_death");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_SHOOT = create("nameless_one_shoot");
        public static final RegistryObject<SoundEvent> NAMELESS_ONE_FLY = create("nameless_one_fly");
        public static final RegistryObject<SoundEvent> RUINS_NECROMANCER_THEME = create("ruins_necromancer_theme");
        public static final RegistryObject<SoundEvent> NAMELESS_FIGHT_MUSIC = create("namelessfight");
        public static final RegistryObject<SoundEvent> BOULDERING_ZOMBIE_IDLE = create("bouldering_zombie_idle");
        public static final RegistryObject<SoundEvent> BOULDERING_ZOMBIE_CLIMB = create("bouldering_zombie_climb");
        public static final RegistryObject<SoundEvent> BOULDERING_ZOMBIE_HURT = create("bouldering_zombie_hurt");
        public static final RegistryObject<SoundEvent> BOULDERING_ZOMBIE_DEATH = create("bouldering_zombie_death");
        public static final RegistryObject<SoundEvent> ILLUSIONER_ARROW = create("illusioner_arrow");
        public static final RegistryObject<SoundEvent> ARCH_ILLUSIONER_DEATH = create("arch_illusioner_death");
        public static final RegistryObject<SoundEvent> PURE_LIGHT = create("pure_light");
        public static final RegistryObject<SoundEvent> WIND_BURST = create("wind_burst");
        public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_OPEN_SHUTTER = create(
                        "trial_spawner_open_shutter");
        public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_CLOSE_SHUTTER = create(
                        "trial_spawner_close_shutter");
        public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_DETECT_PLAYER = create(
                        "trial_spawner_detect_player");
        public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_EJECT_ITEM_1 = create("trial_spawner_eject_item1");
        public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_SPAWN = create("trial_spawner_spawn");
        public static final RegistryObject<SoundEvent> TRIAL_SPAWNER_AMBIENT = create("trial_spawner_ambient");
        public static final RegistryObject<SoundEvent> VAULT_DEACTIVATE = create("vault_deactivate");
        public static final RegistryObject<SoundEvent> VAULT_ACTIVATE = create("vault_activate");
        public static final RegistryObject<SoundEvent> VAULT_INSERT = create("vault_insert");
        public static final RegistryObject<SoundEvent> VAULT_OPEN_SHUTTER = create("vault_open_shutter");
        public static final RegistryObject<SoundEvent> VAULT_EJECT = create("vault_eject");
        public static final RegistryObject<SoundEvent> VAULT_AMBIENT = create("vault_ambient");
        public static final RegistryObject<SoundEvent> VAULT_INSERT_FAIL = create("vault_insert_fail");
        public static final RegistryObject<SoundEvent> VAULT_REJECT_REWARDED_PLAYER = create(
                        "vault_reject_rewarded_player");
        public static final RegistryObject<SoundEvent> CREEDER_IDLE = create("creeder_idle");
        public static final RegistryObject<SoundEvent> CREEDER_HURT = create("creeder_hurt");
        public static final RegistryObject<SoundEvent> CREEDER_DEATH = create("creeder_death");
        public static final RegistryObject<SoundEvent> CREEDER_EXPLOSION = create("creeder_explosion");
        public static final RegistryObject<SoundEvent> CREEDER_HISS = create("creeder_hiss");
        public static final RegistryObject<SoundEvent> JITBZOMBIE_BOING = create("jitbzombie_boing");
        public static final RegistryObject<SoundEvent> JITBZOMBIE_MUSIC = create("jitbzombie_music");
        public static final RegistryObject<SoundEvent> JITBZOMBIE_EXPLOSION = create("jitbzombie_explosion");
        public static final RegistryObject<SoundEvent> ANCIENT_HUNT_PRE = create("ancient_hunt_pre");
        public static final RegistryObject<SoundEvent> ANCIENT_HUNT_LOOP = create("ancient_hunt_loop");
        public static final RegistryObject<SoundEvent> ANCIENT_HUNT_POST = create("ancient_hunt_post");
        public static final RegistryObject<SoundEvent> MUSIC_DISC_ANCIENT = create("music_disc_ancient");
        public static final RegistryObject<SoundEvent> GIANT_GHAST_IDLE = create("giant_ghast_idle");
        public static final RegistryObject<SoundEvent> GIANT_GHAST_HURT = create("giant_ghast_hurt");
        public static final RegistryObject<SoundEvent> GIANT_GHAST_DEATH = create("giant_ghast_death");
        public static final RegistryObject<SoundEvent> GIANT_GHAST_LAST_WORDS = create("giant_ghast_last_words");
        public static final RegistryObject<SoundEvent> GIANT_GHAST_FIREBALL_SHOOT = create(
                        "giant_ghast_fireball_shoot");
        public static final RegistryObject<SoundEvent> GIANT_GHAST_SIDE_ATTACK_CHARGE = create(
                        "giant_ghast_side_attack_charge");
        public static final RegistryObject<SoundEvent> RAMPART_CAPTAIN_SHOUT = create("rampart_captain_shout");
        public static final RegistryObject<SoundEvent> RAMPART_CAPTAIN_MUMBLE = create("rampart_captain_mumble");
        public static final RegistryObject<SoundEvent> RAMPART_CAPTAIN_HURT = create("rampart_captain_hurt");
        public static final RegistryObject<SoundEvent> RAMPART_CAPTAIN_DEATH = create("rampart_captain_death");
        public static final RegistryObject<SoundEvent> RUBY_SORCERER_FIGHT = create("ruby_sorcerer_fight");
        public static final RegistryObject<SoundEvent> RUBY_SORCERER_DISC = create("ruby_sorcerer_disc");
        public static final RegistryObject<SoundEvent> RAMPART_CAPTAIN_FIGHT = create("rampart_captain_music");
        public static final RegistryObject<SoundEvent> RAMPART_CAPTAIN_POST = create("rampart_captain_post");
        public static final RegistryObject<SoundEvent> DROWNED_NECROMANCER_FIGHT = create("drowned_necromancer_music");
        public static final RegistryObject<SoundEvent> DROWNED_NECROMANCER_POST = create("drowned_necromancer_post");
        public static final RegistryObject<SoundEvent> ARCH_ILLUSIONER_FIGHT = create("arch_illusioner_fight");
        public static final RegistryObject<SoundEvent> ARCH_ILLUSIONER_POST = create("arch_illusioner_post");

        public static final RegistryObject<SoundEvent> POISONOUS_POTATO_ZOMBIE_AMBIENT = create(
                        "poisonous_potato_zombie_ambient");
        public static final RegistryObject<SoundEvent> POISONOUS_POTATO_ZOMBIE_HURT = create(
                        "poisonous_potato_zombie_hurt");
        public static final RegistryObject<SoundEvent> POISONOUS_POTATO_ZOMBIE_DEATH = create(
                        "poisonous_potato_zombie_death");
        public static final RegistryObject<SoundEvent> POISONOUS_POTATO_ZOMBIE_STEP = create(
                        "poisonous_potato_zombie_step");
        public static final RegistryObject<SoundEvent> POISONOUS_POTATO_ZOMBIE_INFECT = create(
                        "poisonous_potato_zombie_infect");

        public static final RegistryObject<SoundEvent> TOXIFIN_AMBIENT = create("toxifin_ambient");
        public static final RegistryObject<SoundEvent> TOXIFIN_AMBIENT_LAND = create("toxifin_ambient_land");
        public static final RegistryObject<SoundEvent> TOXIFIN_HURT = create("toxifin_hurt");
        public static final RegistryObject<SoundEvent> TOXIFIN_HURT_LAND = create("toxifin_hurt_land");
        public static final RegistryObject<SoundEvent> TOXIFIN_DEATH = create("toxifin_death");
        public static final RegistryObject<SoundEvent> TOXIFIN_DEATH_LAND = create("toxifin_death_land");
        public static final RegistryObject<SoundEvent> TOXIFIN_FLOP = create("toxifin_flop");

        public static final RegistryObject<SoundEvent> PLAGUEWHALE_AMBIENT = create("plaguewhale_ambient");
        public static final RegistryObject<SoundEvent> PLAGUEWHALE_AMBIENT_LAND = create("plaguewhale_ambient_land");
        public static final RegistryObject<SoundEvent> PLAGUEWHALE_HURT = create("plaguewhale_hurt");
        public static final RegistryObject<SoundEvent> PLAGUEWHALE_HURT_LAND = create("plaguewhale_hurt_land");
        public static final RegistryObject<SoundEvent> PLAGUEWHALE_DEATH = create("plaguewhale_death");
        public static final RegistryObject<SoundEvent> PLAGUEWHALE_DEATH_LAND = create("plaguewhale_death_land");
        public static final RegistryObject<SoundEvent> PLAGUEWHALE_FLOP = create("plaguewhale_flop");

        public static final RegistryObject<SoundEvent> TOWER_GUARD_ATTACK_PREPARE = create(
                        "tower_guard_attack_prepare");
        public static final RegistryObject<SoundEvent> TOWER_GUARD_ATTACK = create("tower_guard_attack");
        public static final RegistryObject<SoundEvent> TOWER_GUARD_CHARGE_START = create(
                        "tower_guard_charge_start");
        public static final RegistryObject<SoundEvent> TOWER_GUARD_CHARGE_END_COLLIDE = create(
                        "tower_guard_charge_end_collide");
        public static final RegistryObject<SoundEvent> TOWER_GUARD_CHARGE_END_NORMAL = create(
                        "tower_guard_charge_end_normal");
        public static final RegistryObject<SoundEvent> TOWER_GUARD_IDLE = create("tower_guard_idle");
        public static final RegistryObject<SoundEvent> TOWER_GUARD_DEFLECT = create("tower_guard_deflect");
        public static final RegistryObject<SoundEvent> TOWER_GUARD_SHIELD_BREAK = create(
                        "tower_guard_shield_break");
        public static final RegistryObject<SoundEvent> TOWER_GUARD_SHIELD_BREAK_VOICE = create(
                        "tower_guard_shield_break_voice");
        public static final RegistryObject<SoundEvent> TOWER_GUARD_HURT = create("tower_guard_hurt");
        public static final RegistryObject<SoundEvent> TOWER_GUARD_DEATH = create("tower_guard_death");
        public static final RegistryObject<SoundEvent> TOWER_GUARD_STEP = create("tower_guard_step");

        private static RegistryObject<SoundEvent> create(String name) {
                return SOUNDS.register(name,
                                () -> SoundEvent.createVariableRangeEvent(
                                                new ResourceLocation(GoetyAwaken.MODID, name)));
        }
}