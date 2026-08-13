package com.k1sak1.goetyawaken.common.events.eliteassault;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.Polarice3.Goety.common.entities.ai.HuntDownPlayerGoal;
import com.Polarice3.Goety.common.entities.hostile.illagers.HuntingIllagerEntity;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.MobsConfig;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.SEHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EliteAssaultSpawner {

    private static final List<ResourceLocation> BOSSES = Arrays.asList(
            new ResourceLocation("goetyawaken", "ruby_sorcerer"),
            new ResourceLocation("goetyawaken", "arch_illusioner"),
            new ResourceLocation("goetyawaken", "hostile_rampart_captain"),
            new ResourceLocation("goety", "minister"),
            new ResourceLocation("goety", "hostile_redstone_golem"));

    private static final Map<ResourceLocation, List<WeightedElite>> ELITE_BY_BOSS = new HashMap<>();

    private static final List<WeightedElite> BASE_ELITES = Arrays.asList(
            new WeightedElite(new ResourceLocation("goety", "storm_caster"), 10),
            new WeightedElite(new ResourceLocation("minecraft", "illusioner"), 10),
            new WeightedElite(new ResourceLocation("goety", "cryologer"), 10),
            new WeightedElite(new ResourceLocation("goety", "crusher"), 10),
            new WeightedElite(new ResourceLocation("goety", "inquillager"), 10),
            new WeightedElite(new ResourceLocation("goety", "envioker"), 10),
            new WeightedElite(new ResourceLocation("goetyawaken", "wind_caller"), 10),
            new WeightedElite(new ResourceLocation("goetyawaken", "mountaineer"), 10));

    static {
        List<WeightedElite> rampartElites = new ArrayList<>();
        for (WeightedElite e : BASE_ELITES) {
            if (e.type.toString().equals("goety:storm_caster")) {
                rampartElites.add(new WeightedElite(e.type, 20));
            } else if (e.type.toString().equals("goetyawaken:wind_caller")) {
                rampartElites.add(new WeightedElite(e.type, 20));
            } else if (e.type.toString().equals("goetyawaken:mountaineer")) {
                rampartElites.add(new WeightedElite(e.type, 20));
            } else {
                rampartElites.add(e);
            }
        }
        ELITE_BY_BOSS.put(new ResourceLocation("goetyawaken", "hostile_rampart_captain"), rampartElites);

        List<WeightedElite> sorcererElites = new ArrayList<>();
        for (WeightedElite e : BASE_ELITES) {
            if (e.type.toString().equals("goety:storm_caster")) {
                sorcererElites.add(new WeightedElite(e.type, 20));
            } else if (e.type.toString().equals("goety:inquillager")) {
                sorcererElites.add(new WeightedElite(e.type, 20));
            } else {
                sorcererElites.add(e);
            }
        }
        ELITE_BY_BOSS.put(new ResourceLocation("goetyawaken", "ruby_sorcerer"), sorcererElites);

        List<WeightedElite> illusionerElites = new ArrayList<>();
        for (WeightedElite e : BASE_ELITES) {
            if (e.type.toString().equals("minecraft:illusioner")) {
                illusionerElites.add(new WeightedElite(e.type, 20));
            } else {
                illusionerElites.add(e);
            }
        }
        ELITE_BY_BOSS.put(new ResourceLocation("goetyawaken", "arch_illusioner"), illusionerElites);
        ELITE_BY_BOSS.put(new ResourceLocation("goety", "minister"), new ArrayList<>(BASE_ELITES));

        List<WeightedElite> redstoneGolemElites = new ArrayList<>();
        for (WeightedElite e : BASE_ELITES) {
            if (e.type.toString().equals("goety:crusher")) {
                redstoneGolemElites.add(new WeightedElite(e.type, 20));
            } else {
                redstoneGolemElites.add(e);
            }
        }
        ELITE_BY_BOSS.put(new ResourceLocation("goety", "hostile_redstone_golem"), redstoneGolemElites);
    }

    public static class WeightedElite {
        public final ResourceLocation type;
        public final int weight;

        public WeightedElite(ResourceLocation type, int weight) {
            this.type = type;
            this.weight = weight;
        }
    }

    public static List<ResourceLocation> getAvailableBosses() {
        return BOSSES;
    }

    public static void triggerAssault(ServerPlayer player) {
        triggerAssault(player, null);
    }

    public static void triggerAssault(ServerPlayer player, ResourceLocation specifiedBossType) {
        ServerLevel level = player.serverLevel();
        int soulEnergy = SEHelper.getSoulAmountInt(player);
        int threshold = MobsConfig.IllagerAssaultSEThreshold.get();
        RandomSource random = level.random;
        int offsetX = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
        int offsetZ = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
        BlockPos.MutableBlockPos spawnPos = player.blockPosition().mutable().move(offsetX, 0, offsetZ);
        spawnPos.setY(level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnPos).getY());
        ResourceLocation bossType = specifiedBossType != null ? specifiedBossType
                : BOSSES.get(random.nextInt(BOSSES.size()));
        EntityType<?> bossEntityType = ForgeRegistries.ENTITY_TYPES.getValue(bossType);
        if (bossEntityType != null) {
            spawnEntity(level, spawnPos, bossEntityType, player, soulEnergy, threshold, false, null, 0.0F);
        }

        List<WeightedElite> elitePool = ELITE_BY_BOSS.getOrDefault(bossType, new ArrayList<>(BASE_ELITES));
        for (int i = 0; i < 4; i++) {
            ResourceLocation eliteType = selectWeightedRandom(elitePool, random);
            EntityType<?> eliteEntityType = ForgeRegistries.ENTITY_TYPES.getValue(eliteType);
            if (eliteEntityType != null) {
                spawnEntity(level, spawnPos, eliteEntityType, player, soulEnergy, threshold, true, null, 0.0F);
            }
            spawnPos.setX(spawnPos.getX() + random.nextInt(5) - random.nextInt(5));
            spawnPos.setZ(spawnPos.getZ() + random.nextInt(5) - random.nextInt(5));
            spawnPos.setY(level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnPos).getY());
        }

        Map<ResourceLocation, EliteAssaultListener.EliteSpawnData> otherMobs = EliteAssaultListener.ELITE_OTHER_MOBS;
        for (Map.Entry<ResourceLocation, EliteAssaultListener.EliteSpawnData> entry : otherMobs.entrySet()) {
            EliteAssaultListener.EliteSpawnData data = entry.getValue();
            if (data == null) {
                continue;
            }
            EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(entry.getKey());
            if (entityType == null || entityType == EntityType.PIG) {
                continue;
            }
            if (random.nextFloat() <= data.chance) {
                int effectiveSoulEnergy = Math.max(soulEnergy, 1);
                int cost = (int) (effectiveSoulEnergy / data.thresholdTimes);
                int total = Mth.clamp(cost / threshold, 1, data.maxExtraAmount) + 1;
                int randomTotal = random.nextInt(total) + data.initExtraAmount;
                for (int k1 = 0; k1 < randomTotal; k1++) {
                    spawnPos.setY(level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnPos).getY());
                    if (k1 == 0) {
                        if (!spawnEntity(level, spawnPos, entityType, player, soulEnergy, threshold, false, data.riding,
                                data.rideChance)) {
                            break;
                        }
                    } else {
                        spawnEntity(level, spawnPos, entityType, player, soulEnergy, threshold, false, data.riding,
                                data.rideChance);
                    }
                    spawnPos.setX(spawnPos.getX() + random.nextInt(5) - random.nextInt(5));
                    spawnPos.setZ(spawnPos.getZ() + random.nextInt(5) - random.nextInt(5));
                }
            }
        }
    }

    private static ResourceLocation selectWeightedRandom(List<WeightedElite> pool, RandomSource random) {
        int totalWeight = pool.stream().mapToInt(e -> e.weight).sum();
        int roll = random.nextInt(totalWeight);
        int currentWeight = 0;
        for (WeightedElite elite : pool) {
            currentWeight += elite.weight;
            if (roll < currentWeight) {
                return elite.type;
            }
        }
        return pool.get(0).type;
    }

    private static boolean spawnEntity(ServerLevel level, BlockPos pos, EntityType<?> entityType,
            ServerPlayer target, int soulEnergy, int threshold, boolean isBoss,
            ResourceLocation ridingType, float rideChance) {
        Entity entity = entityType.create(level);
        if (!(entity instanceof PathfinderMob mob)) {
            return false;
        }

        BlockState blockState = level.getBlockState(pos);
        if (!NaturalSpawner.isValidEmptySpawnBlock(level, pos, blockState, blockState.getFluidState(), entityType)) {
            return false;
        }

        mob.setPos(pos.getX(), pos.getY(), pos.getZ());
        ForgeEventFactory.onFinalizeSpawn(mob, level, level.getCurrentDifficultyAt(pos), MobSpawnType.PATROL, null,
                null);

        mob.goalSelector.addGoal(0, new HuntDownPlayerGoal<>(mob));

        if (mob instanceof HuntingIllagerEntity huntingIllager) {
            float rawPercent = (float) soulEnergy / MainConfig.MaxArcaSouls.get();
            int sePercent = (int) (rawPercent * 100);
            huntingIllager.upgradeAssault(sePercent);
            if (level.random.nextInt(4) == 0) {
                huntingIllager.setRider(true);
            }
        }

        if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
            mob.setTarget(target);
        }

        if (isBoss) {
            upgradeEquipment(mob, soulEnergy, threshold, level.getDifficulty());
        }

        if (ridingType != null && level.random.nextFloat() <= rideChance) {
            EntityType<?> mountType = ForgeRegistries.ENTITY_TYPES.getValue(ridingType);
            if (mountType != null) {
                Entity mountEntity = mountType.create(level);
                if (mountEntity instanceof PathfinderMob mount) {
                    mount.setPos(pos.getX(), pos.getY(), pos.getZ());
                    ForgeEventFactory.onFinalizeSpawn(mount, level, level.getCurrentDifficultyAt(pos),
                            MobSpawnType.PATROL, null, null);
                    mob.startRiding(mount);
                    if (CuriosFinder.hasCurio(target, ModItems.ALARMING_CHARM.get())) {
                        mount.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60));
                    }
                    level.addFreshEntityWithPassengers(mount);
                    return true;
                }
            }
        }

        if (CuriosFinder.hasCurio(target, ModItems.ALARMING_CHARM.get())) {
            mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60));
        }

        level.addFreshEntityWithPassengers(mob);
        return true;
    }

    private static void upgradeEquipment(LivingEntity entity, int soulEnergy, int threshold, Difficulty difficulty) {
        Level world = entity.level();

        if (soulEnergy >= threshold * 5) {
            boolean highEnchant = soulEnergy >= threshold * 15;
            int enchantLevel = Math.min(soulEnergy / threshold + 1, 20);
            Pair<Item, Item>[] armorPieces = getArmorSet(soulEnergy, threshold);
            if (armorPieces != null) {
                for (int i = 0; i < 4; i++) {
                    Pair<Item, Item> armor = armorPieces[i];
                    if (armor != null) {
                        EquipmentSlot slot = switch (i) {
                            case 0 -> EquipmentSlot.HEAD;
                            case 1 -> EquipmentSlot.CHEST;
                            case 2 -> EquipmentSlot.LEGS;
                            case 3 -> EquipmentSlot.FEET;
                            default -> EquipmentSlot.HEAD;
                        };
                        ItemStack armorStack = new ItemStack(armor.getFirst());
                        if (highEnchant) {
                            EnchantmentHelper.enchantItem(world.random, armorStack, enchantLevel,
                                    false);
                        }
                        entity.setItemSlot(slot, armorStack);
                    }
                }
            }
        }

        upgradeWeapon(entity, soulEnergy, threshold, difficulty);
    }

    private static Pair<Item, Item>[] getArmorSet(int soulEnergy, int threshold) {
        Item[] helmets = { Items.CHAINMAIL_HELMET, Items.IRON_HELMET, Items.DIAMOND_HELMET };
        Item[] chestplates = { Items.CHAINMAIL_CHESTPLATE, Items.IRON_CHESTPLATE, Items.DIAMOND_CHESTPLATE };
        Item[] leggings = { Items.CHAINMAIL_LEGGINGS, Items.IRON_LEGGINGS, Items.DIAMOND_LEGGINGS };
        Item[] boots = { Items.CHAINMAIL_BOOTS, Items.IRON_BOOTS, Items.DIAMOND_BOOTS };

        int tier;
        if (soulEnergy >= threshold * 10) {
            tier = 2;
        } else if (soulEnergy >= threshold * 7.5) {
            tier = 1;
        } else {
            tier = 0;
        }

        Pair<Item, Item>[] set = new Pair[4];
        set[0] = Pair.of(helmets[tier], boots[tier]);
        set[1] = Pair.of(chestplates[tier], boots[tier]);
        set[2] = Pair.of(leggings[tier], boots[tier]);
        set[3] = Pair.of(boots[tier], boots[tier]);
        return set;
    }

    private static void upgradeWeapon(LivingEntity raider, int soulAmount, int threshold, Difficulty difficulty) {
        if (soulAmount < threshold * 5) {
            return;
        }
        Level world = raider.level();
        ItemStack itemstack = raider.getMainHandItem().copy();
        if (!itemstack.isEmpty()) {
            int enchantLevel = Math.min(soulAmount / threshold + 1, 20);
            EnchantmentHelper.enchantItem(world.random, itemstack, enchantLevel, false);
            raider.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
        }
    }

}