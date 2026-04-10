package com.k1sak1.goetyawaken.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class EntityMappingUtil {

    private static final Map<ResourceLocation, ResourceLocation> ENTITY_MAPPING = new HashMap<>();

    static {
        ENTITY_MAPPING.put(new ResourceLocation("minecraft", "ravager"),
                new ResourceLocation("goety", "zombie_ravager"));
        ENTITY_MAPPING.put(new ResourceLocation("minecraft", "zombified_piglin"),
                new ResourceLocation("goety", "zpiglin_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("minecraft", "piglin_brute"),
                new ResourceLocation("goety", "zpiglin_brute_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("minecraft", "evoker"),
                new ResourceLocation("goety", "bound_evoker"));
        ENTITY_MAPPING.put(new ResourceLocation("minecraft", "zombie"),
                new ResourceLocation("goety", "zombie_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("minecraft", "vindicator"),
                new ResourceLocation("goety", "zombie_vindicator"));
        ENTITY_MAPPING.put(new ResourceLocation("minecraft", "pillager"),
                new ResourceLocation("goety", "skeleton_pillager"));
        ENTITY_MAPPING.put(new ResourceLocation("minecraft", "piglin"),
                new ResourceLocation("goety", "zpiglin_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("minecraft", "phantom"),
                new ResourceLocation("goety", "phantom_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("minecraft", "husk"),
                new ResourceLocation("goety", "husk_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("minecraft", "drowned"),
                new ResourceLocation("goety", "drowned_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("minecraft", "zombie_villager"),
                new ResourceLocation("goety", "zombie_villager_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("minecraft", "wolf"),
                new ResourceLocation("goety", "skeleton_wolf"));
        ENTITY_MAPPING.put(new ResourceLocation("minecraft", "wither_skeleton"),
                new ResourceLocation("goety", "wither_skeleton_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("minecraft", "wandering_trader"),
                new ResourceLocation("goety", "zombie_villager_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("minecraft", "villager"),
                new ResourceLocation("goety", "zombie_villager_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("minecraft", "stray"),
                new ResourceLocation("goety", "stray_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("minecraft", "slime"),
                new ResourceLocation("goety", "crypt_slime_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("minecraft", "skeleton"),
                new ResourceLocation("goety", "skeleton_servant"));

        ENTITY_MAPPING.put(new ResourceLocation("goety", "necromancer"),
                new ResourceLocation("goety", "necromancer_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("goety", "black_wolf"),
                new ResourceLocation("goety", "skeleton_wolf"));
        ENTITY_MAPPING.put(new ResourceLocation("goety", "border_wraith"),
                new ResourceLocation("goety", "border_wraith_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("goety", "cairn_necromancer"),
                new ResourceLocation("goety", "cairn_necromancer_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("goety", "crypt_slime"),
                new ResourceLocation("goety", "crypt_slime_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("goety", "frayed"),
                new ResourceLocation("goety", "frayed_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("goety", "haunted_armor"),
                new ResourceLocation("goety", "haunted_armor_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("goety", "mossy_necromancer"),
                new ResourceLocation("goety", "mossy_necromancer_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("goety", "muck_wraith"),
                new ResourceLocation("goety", "muck_wraith_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("goety", "prisoner"),
                new ResourceLocation("goety", "zombie_villager_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("goety", "rattled"),
                new ResourceLocation("goety", "rattled_servant"));

        ENTITY_MAPPING.put(new ResourceLocation("goetyawaken", "vindicator_chef"),
                new ResourceLocation("goety", "zombie_vindicator"));
        ENTITY_MAPPING.put(new ResourceLocation("goetyawaken", "bouldering_zombie"),
                new ResourceLocation("goety", "zombie_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("goetyawaken", "jungle_zombie"),
                new ResourceLocation("goety", "jungle_zombie_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("goetyawaken", "parched"),
                new ResourceLocation("goetyawaken", "parched_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("goetyawaken", "skeleton_vanguard"),
                new ResourceLocation("goety", "vanguard_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("goetyawaken", "sunken_skeleton"),
                new ResourceLocation("goety", "sunken_skeleton_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("goetyawaken", "zombie_darkguard"),
                new ResourceLocation("goety", "darkguard_servant"));
        ENTITY_MAPPING.put(new ResourceLocation("goetyawaken", "hostile_tropical_slime"),
                new ResourceLocation("goety", "crypt_slime_servant"));

        if (com.k1sak1.goetyawaken.common.compat.GoetyCataclysmLoaded.INSTANCE.isLoaded()) {
            ENTITY_MAPPING.put(new ResourceLocation("cataclysm", "draugr"),
                    new ResourceLocation("goety_cataclysm", "draugr_servant"));
            ENTITY_MAPPING.put(new ResourceLocation("cataclysm", "drowned_host"),
                    new ResourceLocation("goety_cataclysm", "drowned_host_servant"));
            ENTITY_MAPPING.put(new ResourceLocation("cataclysm", "elite_draugr"),
                    new ResourceLocation("goety_cataclysm", "elite_draugr_servant"));
            ENTITY_MAPPING.put(new ResourceLocation("cataclysm", "ignited_berserker"),
                    new ResourceLocation("goety_cataclysm", "ignited_berserker_servant"));
            ENTITY_MAPPING.put(new ResourceLocation("cataclysm", "koboleton"),
                    new ResourceLocation("goety_cataclysm", "koboleton_servant"));
            ENTITY_MAPPING.put(new ResourceLocation("cataclysm", "royal_draugr"),
                    new ResourceLocation("goety_cataclysm", "royal_draugr_servant"));
            ENTITY_MAPPING.put(new ResourceLocation("goety_cataclysm", "draugr_necromancer"),
                    new ResourceLocation("goety_cataclysm", "draugr_necromancer_servant"));
        }
    }

    public static EntityType<?> getServantType(EntityType<?> originalEntity) {
        ResourceLocation originalKey = ForgeRegistries.ENTITY_TYPES.getKey(originalEntity);
        if (originalKey == null) {
            return null;
        }

        ResourceLocation servantKey = ENTITY_MAPPING.get(originalKey);
        if (servantKey == null) {
            return null;
        }

        return ForgeRegistries.ENTITY_TYPES.getValue(servantKey);
    }

    public static boolean hasServantMapping(EntityType<?> entityType) {
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(entityType);
        return key != null && ENTITY_MAPPING.containsKey(key);
    }

    public static int getMappingCount() {
        return ENTITY_MAPPING.size();
    }

    public static boolean canBeConverted(EntityType<?> entityType) {
        return hasServantMapping(entityType);
    }
}