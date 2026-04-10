package com.k1sak1.goetyawaken.init;

import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.item.Item;

public class ModTags {

    public static class EntityTypes {
        private static void init() {
        }

        public static final TagKey<EntityType<?>> ZOMBIE_DARKGUARD_ALLY = tag("zombie_darkguard_ally");
        public static final TagKey<EntityType<?>> SKELETON_VANGUARD_ALLY = tag("skeleton_vanguard_ally");
        public static final TagKey<EntityType<?>> HIGHER_NECROMANCER = tag("higher_necromancer");

        private static TagKey<EntityType<?>> tag(String name) {
            return create(GoetyAwaken.location(name));
        }

        private static TagKey<EntityType<?>> create(ResourceLocation p_215874_) {
            return TagKey.create(Registries.ENTITY_TYPE, p_215874_);
        }
    }

    public static void init() {
        Blocks.init();
        EntityTypes.init();
        Biomes.init();
        Structures.init();
    }

    public static class Items {
        public static final TagKey<Item> DAUNTLESS_GLOVE_BOOST = tag("dauntless_glove_boost");
        public static final TagKey<Item> ASSASSIN_GLOVE_BOOST = tag("assassin_glove_boost");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(GoetyAwaken.location(name));
        }
    }

    public static class Blocks {
        private static void init() {
        }

        private static TagKey<Block> tag(String name) {
            return create(GoetyAwaken.location(name));
        }

        private static TagKey<Block> create(ResourceLocation p_215874_) {
            return TagKey.create(Registries.BLOCK, p_215874_);
        }
    }

    public static class Biomes {
        private static void init() {
        }

        public static final TagKey<Biome> ZOMBIE_DARKGUARD_SPAWN = tag("zombie_darkguard_spawn");
        public static final TagKey<Biome> SKELETON_VANGUARD_SPAWN = tag("skeleton_vanguard_spawn");
        public static final TagKey<Biome> PARCHED_SPAWN = tag("parched_spawn");
        public static final TagKey<Biome> SUNKEN_SKELETON_SPAWN = tag("sunken_skeleton_spawn");
        public static final TagKey<Biome> HOSTILE_GNASHER_SPAWN = tag("hostile_gnasher_spawn");
        public static final TagKey<Biome> HOSTILE_TROPICAL_SLIME_SPAWN = tag("hostile_tropical_slime_spawn");
        public static final TagKey<Biome> HOSTILE_MINI_GHAST_SPAWN = tag("hostile_mini_ghast_spawn");
        public static final TagKey<Biome> HOSTILE_SNAPPER_SPAWN = tag("hostile_snapper_spawn");
        public static final TagKey<Biome> HOSTILE_SPIDER_CREEDER_SPAWN = tag("hostile_spider_creeder_spawn");
        public static final TagKey<Biome> HOSTILE_TWILIGHT_GOAT_SPAWN = tag("hostile_twilight_goat_spawn");

        private static TagKey<Biome> tag(String name) {
            return create(GoetyAwaken.location(name));
        }

        private static TagKey<Biome> create(ResourceLocation p_215874_) {
            return TagKey.create(Registries.BIOME, p_215874_);
        }
    }

    public static class Structures {
        private static void init() {
        }

        public static final TagKey<Structure> WILDFIRE_SPAWN = tag("wildfire_spawn");

        private static TagKey<Structure> tag(String name) {
            return create(GoetyAwaken.location(name));
        }

        private static TagKey<Structure> create(ResourceLocation p_215874_) {
            return TagKey.create(Registries.STRUCTURE, p_215874_);
        }
    }
}