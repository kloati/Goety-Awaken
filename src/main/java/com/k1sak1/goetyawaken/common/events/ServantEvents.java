package com.k1sak1.goetyawaken.common.events;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.entities.boss.Vizier;
import com.Polarice3.Goety.init.ModTags;
import com.Polarice3.Goety.utils.MiscCapHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.ally.illager.EnviokerServant;
import com.k1sak1.goetyawaken.common.entities.ally.illager.VizierServant;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.AbstractNamelessOne;
import com.k1sak1.goetyawaken.common.items.ModItems;
import com.k1sak1.goetyawaken.common.network.ModNetwork;
import com.k1sak1.goetyawaken.common.network.server.CApostleProgressRequestPacket;
import com.k1sak1.goetyawaken.common.upgrades.ApostleUpgradeManager;
import com.k1sak1.goetyawaken.common.upgrades.SpecialServantHandlers;
import com.k1sak1.goetyawaken.init.ModAttributeRegistry;
import com.k1sak1.goetyawaken.utils.ConversionUtil;
import com.k1sak1.goetyawaken.utils.EntityMappingUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "goetyawaken", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServantEvents {

    private static final TagKey<EntityType<?>> GOETY_NECROMANCERS = TagKey.create(
            Registries.ENTITY_TYPE,
            new ResourceLocation("goety", "necromancers"));

    @SubscribeEvent
    public static void onFakeAppointmentInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity() == null || event.getTarget() == null) {
            return;
        }

        if (event.getTarget() instanceof EnviokerServant envioker) {
            Player player = event.getEntity();
            InteractionHand hand = event.getHand();
            ItemStack itemstack = player.getItemInHand(hand);
            if (itemstack != null && itemstack.getItem() == ModItems.FAKE_APPOINTMENT.get()
                    && envioker.getTrueOwner() == player
                    && !envioker.hasFakeAppointment()) {
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                envioker.setHasFakeAppointment(true);
                envioker.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0F, 1.0F);
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.sidedSuccess(player.level().isClientSide));
            }
        }
    }

    @SubscribeEvent
    public static void onVizierKillConversion(LivingDeathEvent event) {
        if (event.getEntity() instanceof Vizier killedVizer) {
            Entity killer = event.getSource().getEntity();
            if (killer instanceof EnviokerServant envioker && envioker.hasFakeAppointment()) {
                convertEnviokerToVizerServant(envioker, killedVizer);
            }
        }
    }

    private static void convertEnviokerToVizerServant(EnviokerServant envioker, Vizier killedVizer) {
        LivingEntity owner = envioker.getTrueOwner();
        if (!(owner instanceof ServerPlayer player)) {
            return;
        }
        Entity newEntity = MobUtil.convertTo(envioker, ModEntityType.VIZIER_SERVANT.get(), true, player);

        if (newEntity instanceof VizierServant vizerServant) {
            copyVizerProperties(vizerServant, envioker);
            vizerServant.setTrueOwner(player);
            if (envioker.level() instanceof ServerLevel serverLevel) {
                vizerServant.finalizeSpawn(serverLevel,
                        serverLevel.getCurrentDifficultyAt(vizerServant.blockPosition()),
                        net.minecraft.world.entity.MobSpawnType.CONVERSION,
                        null, null);
                net.minecraftforge.event.ForgeEventFactory.onLivingConvert(envioker, vizerServant);
                grantUsurpationAdvancement(player);
            }
        }
    }

    private static void copyVizerProperties(VizierServant vizerServant, EnviokerServant originalEnvioker) {
        float healthRatio = originalEnvioker.getHealth() / originalEnvioker.getMaxHealth();
        vizerServant.setHealth(vizerServant.getMaxHealth() * healthRatio);
        for (net.minecraft.world.entity.EquipmentSlot slot : net.minecraft.world.entity.EquipmentSlot.values()) {
            net.minecraft.world.item.ItemStack itemStack = originalEnvioker.getItemBySlot(slot);
            if (!itemStack.isEmpty()) {
                vizerServant.setItemSlot(slot, itemStack.copy());
            }
        }
        for (int i = 0; i < originalEnvioker.getInventory().getContainerSize()
                && i < vizerServant.getInventory().getContainerSize(); i++) {
            net.minecraft.world.item.ItemStack itemStack = originalEnvioker.getInventory().getItem(i);
            if (!itemStack.isEmpty()) {
                vizerServant.getInventory().setItem(i, itemStack.copy());
            }
        }
    }

    private static void grantUsurpationAdvancement(ServerPlayer player) {
        net.minecraft.advancements.Advancement advancement = player.getServer().getAdvancements().getAdvancement(
                new net.minecraft.resources.ResourceLocation("goetyawaken", "usurpation"));
        if (advancement != null) {
            player.getAdvancements().award(advancement, "envioker_becomes_vizier");
        }
    }

    @SubscribeEvent
    public static void onServantInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity() == null || event.getTarget() == null) {
            return;
        }
        Player player = event.getEntity();
        if (!player.isShiftKeyDown()) {
            return;
        }
        if (!player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty()) {
            return;
        }
        if (!(event.getTarget() instanceof com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant)) {
            return;
        }
        if (player.level().isClientSide) {
            ModNetwork.sendToServer(new CApostleProgressRequestPacket(event.getTarget().getId()));
        } else if (ApostleUpgradeManager.isMarkedForUpgrade((LivingEntity) event.getTarget())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onServantTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity().level().isClientSide || event.getEntity().tickCount % 20 != 0) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity instanceof com.k1sak1.goetyawaken.common.entities.ally.illager.CroneServant crone) {
            if (ApostleUpgradeManager.isMarkedForUpgrade(crone)) {
                int positiveEffects = 0;
                int negativeEffects = 0;
                for (net.minecraft.world.effect.MobEffectInstance effect : crone.getActiveEffects()) {
                    if (effect.getEffect().getCategory() == net.minecraft.world.effect.MobEffectCategory.BENEFICIAL) {
                        positiveEffects++;
                    } else {
                        negativeEffects++;
                    }
                }
                SpecialServantHandlers.handleCroneEffectUpdate(crone, positiveEffects, negativeEffects);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerInteractWithSorcerer(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity() == null || event.getTarget() == null) {
            return;
        }
        if (event.getTarget() instanceof com.k1sak1.goetyawaken.common.entities.ally.illager.SorcererServant sorcerer) {
            ItemStack itemStack = event.getItemStack();
            if (itemStack != null && itemStack.getItem() == net.minecraft.world.item.Items.EMERALD) {
                ApostleUpgradeEvents.lastMoneyAmounts.put(sorcerer.getUUID(), sorcerer.getMoneyAmount());
            }
        }
    }

    @SubscribeEvent
    public static void onServantHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant raider) {
            if (ApostleUpgradeManager.isMarkedForUpgrade(raider)) {
                SpecialServantHandlers.handleRaiderServantHeal(raider, event.getAmount());
            }
        }
    }

    @SubscribeEvent
    public static void onServantHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        LivingEntity source = event.getSource().getEntity() instanceof LivingEntity living ? living : null;
        if (target instanceof com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant raider) {
            if (ApostleUpgradeManager.isMarkedForUpgrade(raider)) {
                if (event.getSource().getMsgId().contains("freeze")) {
                    SpecialServantHandlers.handleRaiderServantFrozenDamage(raider, event.getAmount());
                }
            }
        }
        if (source instanceof com.Polarice3.Goety.common.entities.ally.illager.raider.RaiderServant raider) {
            if (ApostleUpgradeManager.isMarkedForUpgrade(raider)) {
                SpecialServantHandlers.handleRaiderServantDamageDealt(raider, event.getAmount());
            }
        }
    }

    @SubscribeEvent
    public static void onServantLeaveLevel(EntityLeaveLevelEvent event) {
        ApostleUpgradeEvents.lastMoneyAmounts.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onVanguardChampionKill(LivingDeathEvent event) {
        Entity killer = event.getSource().getEntity();

        if (killer instanceof com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton.VanguardChampion vanguardchampion
                && !vanguardchampion.isHostile()) {
            LivingEntity victim = event.getEntity();
            if (!victim.level().isClientSide) {
                boolean isAbstractNecromancer = victim instanceof com.Polarice3.Goety.common.entities.neutral.AbstractNecromancer;
                boolean hasGoetyNecromancerTag = victim.getType().is(GOETY_NECROMANCERS);
                boolean shouldDropMucilage = isAbstractNecromancer || hasGoetyNecromancerTag;

                if (victim.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOMOBLOOT)) {
                    victim.spawnAtLocation(
                            new ItemStack(com.Polarice3.Goety.common.items.ModItems.GRAVE_DUST.get(), 1));
                    victim.spawnAtLocation(new ItemStack(com.Polarice3.Goety.common.items.ModItems.ECTOPLASM.get(), 1));

                    if (shouldDropMucilage) {
                        victim.spawnAtLocation(new ItemStack(ModItems.MUCILAGE.get(), 1));
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onNamelessOneKillConversion(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();

        Entity killer = getActualKiller(event.getSource());

        if (killer == null) {
            return;
        }

        AbstractNamelessOne namelessOne = getNamelessOneKiller(killer);
        if (namelessOne == null) {
            return;
        }

        if (!EntityMappingUtil.canBeConverted(victim.getType())) {
            return;
        }

        if (!ConversionUtil.canConvert(victim, namelessOne)) {
            return;
        }

        LivingEntity servant = ConversionUtil.convertToServant(victim, namelessOne);

        if (servant != null) {
            event.setCanceled(true);
            victim.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    private static Entity getActualKiller(net.minecraft.world.damagesource.DamageSource damageSource) {
        Entity directEntity = damageSource.getDirectEntity();
        if (directEntity instanceof AbstractNamelessOne) {
            return directEntity;
        }

        Entity causingEntity = damageSource.getEntity();
        if (causingEntity instanceof AbstractNamelessOne) {
            return causingEntity;
        }

        if (directEntity != null && isOwnedByNamelessOne(directEntity)) {
            return directEntity;
        }

        if (causingEntity != null && isOwnedByNamelessOne(causingEntity)) {
            return causingEntity;
        }

        return null;
    }

    private static boolean isOwnedByNamelessOne(Entity entity) {
        if (entity instanceof com.Polarice3.Goety.api.entities.IOwned owned) {
            LivingEntity owner = owned.getTrueOwner();
            return owner instanceof AbstractNamelessOne;
        }
        return false;
    }

    private static AbstractNamelessOne getNamelessOneKiller(Entity killer) {
        if (killer instanceof AbstractNamelessOne namelessOne) {
            return namelessOne;
        }

        if (killer instanceof com.Polarice3.Goety.api.entities.IOwned owned) {
            LivingEntity owner = owned.getTrueOwner();
            if (owner instanceof AbstractNamelessOne namelessOne) {
                return namelessOne;
            }
        }

        return null;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServantAttributeHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide || !(target instanceof IOwned owned)) {
            return;
        }
        LivingEntity owner = owned.getTrueOwner();
        if (owner == null || !owner.isAlive()) {
            return;
        }
        double multiplier = ModAttributeRegistry.getServantDamageReductionMultiplier(owner);
        if (multiplier != 1.0D) {
            event.setAmount((float) (event.getAmount() * multiplier));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServantAttributeTick(LivingEvent.LivingTickEvent event) {
        LivingEntity self = event.getEntity();
        if (self.level().isClientSide || self.tickCount % 20 != 0 || !(self instanceof IOwned owned)) {
            return;
        }
        LivingEntity owner = owned.getTrueOwner();
        if (owner == null || !owner.isAlive()) {
            return;
        }
        double healAmount = ModAttributeRegistry.getServantHealing(owner);
        if (healAmount <= 0.0D) {
            return;
        }
        int noHealTime = self instanceof IServant servant ? servant.getNoHealTime()
                : MiscCapHelper.getNoHealTime(self);
        if (noHealTime > 0 || self.getType().is(ModTags.EntityTypes.NO_HEAL_SERVANTS) || self.isOnFire()
                || self.isDeadOrDying() || self.getHealth() >= self.getMaxHealth()) {
            return;
        }
        boolean canPay = true;
        if (owner instanceof Player player) {
            canPay = SEHelper.getSoulsAmount(player, 1);
        }
        if (!canPay) {
            return;
        }
        self.heal((float) healAmount);
        if (owner instanceof Player player) {
            SEHelper.decreaseSouls(player, 1);
        }
        if (self.level() instanceof ServerLevel serverLevel) {
            Vec3 movement = self.getDeltaMovement();
            serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, self.getRandomX(0.5D), self.getRandomY(),
                    self.getRandomZ(0.5D), 0, movement.x * -0.2D, 0.1D, movement.z * -0.2D, 0.5F);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServantAttributeLoot(LootingLevelEvent event) {
        if (event.getDamageSource() == null || event.getEntity() == null || event.getEntity().level().isClientSide) {
            return;
        }
        Entity killer = event.getDamageSource().getEntity();
        if (!(killer instanceof IOwned owned)) {
            return;
        }
        LivingEntity owner = owned.getTrueOwner();
        if (owner == null || !owner.isAlive()) {
            return;
        }
        int looting = ModAttributeRegistry.getServantLootingLevel(owner);
        if (looting > 0) {
            event.setLootingLevel(event.getLootingLevel() + looting);
        }
    }
}
