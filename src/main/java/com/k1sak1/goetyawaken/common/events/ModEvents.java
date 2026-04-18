package com.k1sak1.goetyawaken.common.events;

import com.Polarice3.Goety.common.blocks.entities.AnimatorBlockEntity;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant;
import com.Polarice3.Goety.common.entities.hostile.servants.Damned;
import com.Polarice3.Goety.common.items.magic.AnimationCore;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import com.k1sak1.goetyawaken.common.advancements.ModCriteriaTriggers;
import com.k1sak1.goetyawaken.common.blocks.ModBlocks;
import com.k1sak1.goetyawaken.common.entities.ally.ObsidianMonolithServant;
import com.k1sak1.goetyawaken.common.entities.ally.WitherServant;
import com.k1sak1.goetyawaken.common.entities.ally.ender.EndersentServant;
import com.k1sak1.goetyawaken.common.entities.ally.illager.ApostleServant;
import com.k1sak1.goetyawaken.common.entities.ally.illager.RoyalguardServant;
import com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton.VanguardChampion;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.AbstractNamelessOne;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.zombie.FrozenZombie;
import com.k1sak1.goetyawaken.common.items.ModItems;
import com.k1sak1.goetyawaken.common.magic.GolemTypeRegistry;
import com.k1sak1.goetyawaken.init.ModTags;
import com.k1sak1.goetyawaken.api.IAncientGlint;
import com.k1sak1.goetyawaken.utils.AnimatorBlockEntityHelper;
import com.k1sak1.goetyawaken.utils.GolemTypeHelper;
import com.k1sak1.goetyawaken.utils.ModDamageSource;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "goetyawaken", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<UUID, Boolean> playerHighAndDryAwarded = new HashMap<>();
    private static final ResourceKey<Structure> MIRAGE_STRUCTURE = ResourceKey.create(
            Registries.STRUCTURE,
            new ResourceLocation("goetyawaken", "mirage"));
    private static final ResourceKey<Structure> CHAOS_PRISON_STRUCTURE = ResourceKey.create(
            Registries.STRUCTURE,
            new ResourceLocation("goetyawaken", "chaos_prison"));
    private static final Map<UUID, Boolean> playerChaosPrisonAwarded = new HashMap<>();

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity killed = event.getEntity();
        Entity killer = event.getSource().getEntity();
        if (killer instanceof WitherServant witherServant) {
            witherServant.onKillEntity(killed);
        }

        if (!killed.level().isClientSide() && killed instanceof IAncientGlint glint) {
            if (glint.hasAncientGlint() && "ancient".equals(glint.getGlintTextureType())) {
                int dropAmount = killed.level().random.nextInt(2) + 1;
                net.minecraft.world.item.ItemStack emberStack = new net.minecraft.world.item.ItemStack(
                        ModItems.GLOWING_EMBER.get(), dropAmount);
                killed.spawnAtLocation(emberStack);
                net.minecraft.world.item.ItemStack treasureBagStack = new net.minecraft.world.item.ItemStack(
                        com.Polarice3.Goety.common.items.ModItems.TREASURE_POUCH.get(), 1);
                killed.spawnAtLocation(treasureBagStack);
            }
        }

        if (killed.getType() == com.k1sak1.goetyawaken.common.entities.ModEntityType.HOSTILE_MUSHROOM_MONSTROSITY
                .get()) {
            ServerPlayer triggeringPlayer = null;
            if (killer instanceof ServerPlayer serverPlayer) {
                triggeringPlayer = serverPlayer;
            } else if (killer instanceof com.Polarice3.Goety.common.entities.ally.Summoned ownedKiller) {
                if (ownedKiller.getMasterOwner() instanceof ServerPlayer masterPlayer) {
                    triggeringPlayer = masterPlayer;
                }
            } else if (killer instanceof net.minecraft.world.entity.projectile.Projectile projectile) {
                if (projectile.getOwner() instanceof ServerPlayer ownerPlayer) {
                    triggeringPlayer = ownerPlayer;
                }
            }

            if (triggeringPlayer != null) {
                ModCriteriaTriggers.MUSHROOM_MONSTROSITY_KILL.trigger(triggeringPlayer);
            }
        }

        if (killed.getType() == com.k1sak1.goetyawaken.common.entities.ModEntityType.HOSTILE_MUSHROOM_MONSTROSITY
                .get()) {
            if (killer != null) {
                if (killer instanceof com.Polarice3.Goety.common.entities.ally.Summoned ownedKiller) {
                    if (ownedKiller.getMasterOwner() instanceof ServerPlayer masterPlayer) {
                        ModCriteriaTriggers.SPOREARM_RACE.trigger(masterPlayer);
                    }
                } else if (killed
                        .getLastHurtByMob() instanceof com.Polarice3.Goety.common.entities.ally.Summoned ownedHurtBy) {
                    if (ownedHurtBy.getMasterOwner() instanceof ServerPlayer masterPlayer) {
                        ModCriteriaTriggers.SPOREARM_RACE.trigger(masterPlayer);
                    }
                }
            }
        }

        Level world = killed.level();
        if (world instanceof ServerLevel serverLevel) {
            if (killed instanceof AbstractIllager || killed instanceof AbstractVillager
                    || killed instanceof WanderingTrader || killed instanceof AbstractIllagerServant) {
                List<ApostleServant> nearbyApostles = world.getEntitiesOfClass(
                        ApostleServant.class,
                        killed.getBoundingBox().inflate(64.0D));

                for (ApostleServant apostle : nearbyApostles) {
                    if (apostle.hasLineOfSight(killed)) {
                        Damned damned = new Damned(ModEntityType.DAMNED.get(), world);
                        damned.moveTo(killed.blockPosition().below(2), apostle.getYHeadRot(), apostle.getXRot());
                        damned.setTrueOwner(apostle);
                        damned.setHuman(false);
                        damned.finalizeSpawn(serverLevel,
                                serverLevel.getCurrentDifficultyAt(killed.blockPosition().below()),
                                net.minecraft.world.entity.MobSpawnType.MOB_SUMMONED, null, null);
                        if (killed.hasCustomName()) {
                            damned.setCustomName(killed.getCustomName());
                        }
                        if (apostle.getTarget() != null) {
                            damned.setTarget(apostle.getTarget());
                        }
                        damned.setLimitedLife(1000);
                        com.Polarice3.Goety.utils.ServerParticleUtil.addParticlesAroundSelf(serverLevel,
                                com.Polarice3.Goety.client.particles.ModParticleTypes.BIG_FIRE.get(), damned);
                        world.addFreshEntity(damned);
                    }
                }
            }
        }

        if (!killed.level().isClientSide()) {
            boolean isAbstractNecromancer = killed instanceof com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;

            net.minecraft.tags.TagKey<net.minecraft.world.entity.EntityType<?>> goetyNecromancersTag = net.minecraft.tags.TagKey
                    .create(
                            net.minecraft.core.registries.Registries.ENTITY_TYPE,
                            new net.minecraft.resources.ResourceLocation("goety", "necromancers"));
            boolean hasGoetyNecromancerTag = killed.getType().is(goetyNecromancersTag);

            if (isAbstractNecromancer || hasGoetyNecromancerTag) {
                Entity killerEntity = event.getSource().getEntity();
                if (killerEntity instanceof Player player) {
                    if (event.getSource().is(net.minecraft.world.damagesource.DamageTypes.PLAYER_ATTACK)) {
                        ItemStack mainHandItem = player.getMainHandItem();
                        if (mainHandItem.getItem() instanceof com.k1sak1.goetyawaken.common.items.MoonlightCutItem) {
                            if (killed.level().getGameRules()
                                    .getBoolean(net.minecraft.world.level.GameRules.RULE_DOMOBLOOT)) {
                                killed.spawnAtLocation(new ItemStack(ModItems.MUCILAGE.get(), 1));
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        com.k1sak1.goetyawaken.common.items.FrostScytheItem.emptyClick(event.getItemStack());
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        com.k1sak1.goetyawaken.common.items.FrostScytheItem.entityClick(event.getEntity(), event.getEntity().level());
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof AnimationCore) {
            BlockState blockState = event.getLevel().getBlockState(event.getPos());

            if (GolemTypeRegistry.isAdditionalGolemType(blockState)) {
                if (GolemTypeRegistry.getMoldForBlockState(blockState).spawnServant(event.getEntity(), stack,
                        event.getLevel(), event.getPos())) {
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockEvent(net.minecraftforge.event.level.BlockEvent.NeighborNotifyEvent event) {
        BlockEntity blockEntity = event.getLevel().getBlockEntity(event.getPos());
        if (blockEntity instanceof AnimatorBlockEntity) {
            AnimatorBlockEntity animator = (AnimatorBlockEntity) blockEntity;
            AnimatorBlockEntityHelper.enhancedSummonGolem(animator);
        }
    }

    @SubscribeEvent
    public static void onServerStarting(net.minecraftforge.event.server.ServerStartingEvent event) {
        GolemTypeHelper.initialize();
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide()) {
            Entity entity = event.getEntity();
            if (entity instanceof RoyalguardServant royalguardServant) {
                LivingEntity owner = royalguardServant.getTrueOwner();
                if (owner instanceof ServerPlayer serverPlayer) {
                    ModCriteriaTriggers.CLASH_ROYALE.trigger(serverPlayer);
                }
            }

            if (entity instanceof EndersentServant endersentServant) {
                LivingEntity owner = endersentServant.getTrueOwner();
                if (owner instanceof ServerPlayer serverPlayer) {
                    ModCriteriaTriggers.THE_SWARM.trigger(serverPlayer);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onItemPickup(net.minecraftforge.event.entity.player.PlayerEvent.ItemPickupEvent event) {
        if (!event.getEntity().level().isClientSide() && event.getEntity() instanceof ServerPlayer player) {
            ItemStack stack = event.getStack();
            if (stack.is(ModBlocks.SHADOW_SHRIEKER.get().asItem())) {
                ModCriteriaTriggers.GET_SHADOW_SHRIEKER.trigger(player);
            }
            if (stack.is(ModItems.CREEPER_FOCUS.get())) {
                ModCriteriaTriggers.CREEPER_FOCUS.trigger(player);
            }
            if (stack.is(ModItems.STARE_FOCUS.get())) {
                ModCriteriaTriggers.THE_END_ENVOY.trigger(player);
            }
            if (stack.is(ModItems.INFESTATION_FOCUS.get())) {
                ModCriteriaTriggers.BED_LICE.trigger(player);
            }
        }
    }

    @SubscribeEvent
    public static void onCrafting(net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent event) {
        if (!event.getEntity().level().isClientSide() && event.getEntity() instanceof ServerPlayer player) {
            if (event.getCrafting().is(ModBlocks.SHADOW_SHRIEKER.get().asItem())) {
                ModCriteriaTriggers.GET_SHADOW_SHRIEKER.trigger(player);
            }
            if (event.getCrafting().is(ModItems.CREEPER_FOCUS.get().asItem())) {
                ModCriteriaTriggers.CREEPER_FOCUS.trigger(player);
            }
            if (event.getCrafting().is(ModItems.STARE_FOCUS.get().asItem())) {
                ModCriteriaTriggers.THE_END_ENVOY.trigger(player);
            }
            if (event.getCrafting().is(ModItems.INFESTATION_FOCUS.get().asItem())) {
                ModCriteriaTriggers.BED_LICE.trigger(player);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide()) {
            Player player = event.player;
            UUID playerId = player.getUUID();
            if (!playerHighAndDryAwarded.getOrDefault(playerId, false)) {
                if (isPlayerInMirageStructure(player)) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        ModCriteriaTriggers.HIGH_AND_DRY.trigger(serverPlayer);
                        playerHighAndDryAwarded.put(playerId, true);
                    }
                }
            }

            if (!playerChaosPrisonAwarded.getOrDefault(playerId, false)) {
                if (isPlayerInChaosPrisonStructure(player)) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        ModCriteriaTriggers.CHAOS_PRISON.trigger(serverPlayer);
                        playerChaosPrisonAwarded.put(playerId, true);
                    }
                }
            }
        }
    }

    private static boolean isPlayerInMirageStructure(Player player) {
        Level level = player.level();
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.structureManager()
                    .getStructureWithPieceAt(player.blockPosition(), MIRAGE_STRUCTURE)
                    .isValid();
        }
        return false;
    }

    private static boolean isPlayerInChaosPrisonStructure(Player player) {
        Level level = player.level();
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.structureManager()
                    .getStructureWithPieceAt(player.blockPosition(), CHAOS_PRISON_STRUCTURE)
                    .isValid();
        }
        return false;
    }

    @SubscribeEvent
    public static void LivingEffects(net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity != null && livingEntity.isAlive()) {
            if (livingEntity instanceof Mob mob) {
                double followRange = 32.0D;
                if (mob.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE) != null) {
                    followRange = mob
                            .getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE)
                            * 2;
                }
                if (mob.getTarget() instanceof ApostleServant apostleServant) {
                    if (apostleServant.obsidianInvul > 5) {
                        for (ObsidianMonolithServant obsidianMonolithServant : mob.level().getEntitiesOfClass(
                                ObsidianMonolithServant.class,
                                mob.getBoundingBox().inflate(followRange, 8.0D, followRange))) {
                            if (obsidianMonolithServant.getTrueOwner() == apostleServant) {
                                mob.setTarget(obsidianMonolithServant);
                            }
                        }
                    }
                }
                if (mob.getTarget() instanceof AbstractNamelessOne namelessone) {
                    for (VanguardChampion vanguardchampion : mob.level().getEntitiesOfClass(
                            VanguardChampion.class,
                            mob.getBoundingBox().inflate(followRange, 8.0D, followRange))) {
                        if (vanguardchampion.getTrueOwner() == namelessone) {
                            mob.setTarget(vanguardchampion);
                            break;
                        }
                    }
                }
            }
        }

    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity victim = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof FrozenZombie) {
            if (!victim.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0, false, false));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        if ((ModDamageSource.isDeathFire(event.getEntity().getLastDamageSource()))
                && event.getAmount() > 0.0F) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingEntityTickWithSunGrace(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide()) {
            boolean hasSunGrace = entity.getMainHandItem().is(ModItems.SUN_GRACE.get()) ||
                    entity.getOffhandItem().is(ModItems.SUN_GRACE.get());
            if (hasSunGrace && entity.tickCount % 100 == 0) {
                entity.addEffect(new MobEffectInstance(
                        GoetyEffects.RADIANCE.get(),
                        200,
                        0,
                        false,
                        false,
                        true));
            }
        }
    }
}