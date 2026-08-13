package com.k1sak1.goetyawaken.common.events;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.SEHelper;
import com.k1sak1.goetyawaken.common.items.DarkNetheriteBowItem;
import com.k1sak1.goetyawaken.common.items.FrostScytheItem;
import com.k1sak1.goetyawaken.common.items.NBTEntitySpawnEggItem;
import com.k1sak1.goetyawaken.common.items.TruthseekerItem;
import com.k1sak1.goetyawaken.common.items.curios.AssassinGloveItem;
import com.k1sak1.goetyawaken.common.items.curios.DetonationRingItem;
import com.k1sak1.goetyawaken.common.items.StarlessNightItem;
import com.k1sak1.goetyawaken.common.items.magic.GrimoireItem;
import com.k1sak1.goetyawaken.common.items.ModItems;
import com.k1sak1.goetyawaken.init.ModTags;
import com.k1sak1.goetyawaken.common.advancements.ModCriteriaTriggers;
import com.k1sak1.goetyawaken.common.entities.ally.illager.SorcererServant;
import com.k1sak1.goetyawaken.common.entities.projectiles.ExplosiveArrow;
import com.k1sak1.goetyawaken.Config;
import com.Polarice3.Goety.common.entities.ally.illager.Neollager;
import com.Polarice3.Goety.client.inventory.container.DarkAnvilMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "goetyawaken", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemEvents {

    private static final AttributeModifier DAUNTLESS_ATTACK_SPEED_MODIFIER = new AttributeModifier(
            UUID.fromString("4f480e4c-3ef9-4ad6-b6d0-73681939724e"),
            "Dauntless Proficiency",
            0.25F,
            AttributeModifier.Operation.MULTIPLY_TOTAL);

    private static final String ENHANCEMENT_COUNT_TAG = "GlowingEmberEnhancementCount";
    private static final int MAX_ENHANCEMENTS = Config.GLOWING_EMBER_MAX_ENHANCEMENTS.get();
    private static final int EXPERIENCE_COST = 30;

    private static final Random RANDOM = new Random();

    private static final String LV2NAME1_PREFIX = "grimoire.lv2.name1.";
    private static final int LV2NAME1_COUNT = 23;

    private static final String LV2NAME2_PREFIX = "grimoire.lv2.name2.";
    private static final int LV2NAME2_COUNT = 14;

    private static final String LV3NAME1_PREFIX = "grimoire.lv3.name1.";
    private static final int LV3NAME1_COUNT = 12;

    private static final String LV3NAME2_PREFIX = "grimoire.lv3.name2.";
    private static final int LV3NAME2_COUNT = 17;

    private static final String LV4NAME1_PREFIX = "grimoire.lv4.name1.";
    private static final int LV4NAME1_COUNT = 15;

    private static final String LV4NAME2_PREFIX = "grimoire.lv4.name2.";
    private static final int LV4NAME2_COUNT = 18;

    private static final String LV5NAME1_PREFIX = "grimoire.lv5.name1.";
    private static final int LV5NAME1_COUNT = 30;

    private static final String LV5NAME2_PREFIX = "grimoire.lv5.name2.";
    private static final int LV5NAME2_COUNT = 21;

    private static final String LV6NAME1_PREFIX = "grimoire.lv6.name1.";
    private static final int LV6NAME1_COUNT = 20;

    private static final String LV6NAME2_PREFIX = "grimoire.lv6.name2.";
    private static final int LV6NAME2_COUNT = 21;

    @SubscribeEvent
    public static void PlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase == TickEvent.Phase.END) {
            AttributeInstance attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
            boolean hasDauntlessGloves = CuriosFinder.hasCurio(player, ModItems.DAUNTLESS_GLOVES.get());
            boolean mainHandValid = player.getMainHandItem().is(ModTags.Items.DAUNTLESS_GLOVE_BOOST);

            boolean flag = hasDauntlessGloves && mainHandValid;

            if (attackSpeed != null) {
                if (flag) {
                    if (!attackSpeed.hasModifier(DAUNTLESS_ATTACK_SPEED_MODIFIER)) {
                        attackSpeed.addPermanentModifier(DAUNTLESS_ATTACK_SPEED_MODIFIER);
                    }
                } else {
                    if (attackSpeed.hasModifier(DAUNTLESS_ATTACK_SPEED_MODIFIER)) {
                        attackSpeed.removeModifier(DAUNTLESS_ATTACK_SPEED_MODIFIER);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void HurtEvent(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        Entity directEntity = event.getSource().getDirectEntity();
        if (event.getAmount() > 0.0F) {
            if (directEntity instanceof LivingEntity livingAttacker) {

                if (ModDamageSource.physicalAttacks(event.getSource())) {
                    ItemHelper.setItemEffect(livingAttacker.getMainHandItem(), victim);
                    if (livingAttacker.getMainHandItem().getItem() instanceof TieredItem weapon) {
                        if (weapon instanceof TruthseekerItem) {
                            float maxHealth = victim.getMaxHealth();
                            float currentHealth = victim.getHealth();
                            float lostHealthPercent = (maxHealth - currentHealth) / maxHealth;

                            if (lostHealthPercent > 0) {
                                float bonusDamageMultiplier = lostHealthPercent * 2.0F;
                                float additionalDamage = event.getAmount() * bonusDamageMultiplier;
                                event.setAmount(event.getAmount() + additionalDamage);
                            }
                        }
                        if (AssassinGloveItem.hasAssassinGloveItem(livingAttacker)) {
                            boolean hasDagger = livingAttacker.getMainHandItem()
                                    .is(ModTags.Items.ASSASSIN_GLOVE_BOOST);

                            boolean isBackAttack = isBackAttack(livingAttacker, victim,
                                    event.getSource().getSourcePosition());

                            if (isBackAttack && hasDagger) {
                                event.setAmount(event.getAmount() * 2.0F);
                            }
                        }
                        if (weapon instanceof FrostScytheItem) {
                            victim.playSound(ModSounds.SCYTHE_HIT_MEATY.get());
                        } else if (weapon instanceof StarlessNightItem) {
                            victim.playSound(ModSounds.OBSIDIAN_CLAYMORE_SWING.get());
                        }
                        if (weapon instanceof FrostScytheItem) {
                            if (!victim.hasEffect(GoetyEffects.WANE.get())) {
                                victim.addEffect(new MobEffectInstance(GoetyEffects.WANE.get(), 100));
                            } else {
                                if (victim.level().random.nextFloat() <= 0.2F) {
                                    EffectsUtil.amplifyEffect(victim, GoetyEffects.WANE.get(), 100);
                                } else {
                                    EffectsUtil.resetDuration(victim, GoetyEffects.WANE.get(), 100);
                                }
                            }
                            if (!victim.hasEffect(GoetyEffects.FREEZING.get())) {
                                victim.addEffect(new MobEffectInstance(GoetyEffects.FREEZING.get(), 100));
                                victim.playSound(SoundEvents.SHIELD_BREAK, 2.0F, 1.0F);
                            } else {
                                if (victim.level().random.nextFloat() <= 0.2F) {
                                    EffectsUtil.amplifyEffect(victim, GoetyEffects.FREEZING.get(), 100);
                                    victim.playSound(SoundEvents.SHIELD_BREAK, 2.0F, 1.0F);
                                } else {
                                    EffectsUtil.resetDuration(victim, GoetyEffects.FREEZING.get(), 100);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean isLookingBehindTarget(LivingEntity target, Vec3 attackerLocation) {
        if (attackerLocation != null) {
            Vec3 lookingVector = target.getViewVector(1.0F);
            Vec3 attackAngleVector = attackerLocation.subtract(target.position()).normalize();
            attackAngleVector = new Vec3(attackAngleVector.x, 0.0D, attackAngleVector.z);
            return attackAngleVector.dot(lookingVector) < -0.5D;
        }
        return false;
    }

    private static boolean isBackAttack(LivingEntity attacker, LivingEntity victim,
            Vec3 sourcePosition) {
        return isLookingBehindTarget(victim, sourcePosition);
    }

    @SubscribeEvent
    public static void EmptyClickEvents(PlayerInteractEvent.LeftClickEmpty event) {
        if (event.getItemStack().getItem() instanceof StarlessNightItem) {
            StarlessNightItem.emptyClick(event.getItemStack());
        }
    }

    @SubscribeEvent
    public static void PlayerAttackEvents(AttackEntityEvent event) {
        if (event.getEntity().getMainHandItem().getItem() instanceof StarlessNightItem) {
            StarlessNightItem.entityClick(event.getEntity(), event.getEntity().level());
        }
    }

    @SubscribeEvent
    public static void onDarkNetheriteBowDeath(LivingDeathEvent event) {
        Entity sourceEntity = event.getSource().getEntity();
        if (sourceEntity instanceof Player player) {
            ItemStack weapon = player.getMainHandItem();
            if (weapon.getItem() instanceof DarkNetheriteBowItem) {
                LivingEntity victim = event.getEntity();
                double baseSouls = SEHelper.getSoulGiven(victim);
                int extraSouls = (int) (baseSouls * 0.5);
                SEHelper.increaseSouls(player, extraSouls);
            }
        } else if (event.getSource().getDirectEntity() != null) {
            Entity directEntity = event.getSource().getDirectEntity();
            if (directEntity instanceof net.minecraft.world.entity.projectile.AbstractArrow arrow) {
                Entity shooter = arrow.getOwner();
                if (shooter instanceof Player player) {
                    ItemStack weapon = player.getMainHandItem();
                    if (weapon.getItem() instanceof DarkNetheriteBowItem) {
                        LivingEntity victim = event.getEntity();
                        double baseSouls = SEHelper.getSoulGiven(victim);
                        int extraSouls = (int) (baseSouls * 0.5);

                        SEHelper.increaseSouls(player, extraSouls);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onNbtEggCapture(AttackEntityEvent event) {
        Player player = event.getEntity();
        Entity targetEntity = event.getTarget();
        for (net.minecraft.world.InteractionHand hand : net.minecraft.world.InteractionHand.values()) {
            ItemStack heldItem = player.getItemInHand(hand);
            if (heldItem.getItem() instanceof NBTEntitySpawnEggItem nbtEggItem) {
                if (player.isCreative() && !NBTEntitySpawnEggItem.hasStoredEntityData(heldItem)) {
                    boolean captured = nbtEggItem.tryCaptureEntityFromEntity(targetEntity, player, heldItem);

                    if (captured) {
                        event.setCanceled(true);
                        return;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        Player player = event.getPlayer();

        if (!right.is(ModItems.GILDED_INGOT.get())) {
            return;
        }

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(left);
        boolean isEnchantedBook = left.getItem() instanceof EnchantedBookItem &&
                !EnchantedBookItem.getEnchantments(left).isEmpty();

        if (enchantments.isEmpty() && !isEnchantedBook) {
            return;
        }

        int enhancementCount = getEnhancementCount(left);
        if (enhancementCount >= MAX_ENHANCEMENTS) {
            return;
        }

        ItemStack output = left.copy();
        Map<Enchantment, Integer> newEnchantments = new HashMap<>(enchantments);

        if (isEnchantedBook) {
            CompoundTag tag = left.getTag();
            if (tag != null && tag.contains("StoredEnchantments", 9)) {
                var storedEnchantments = tag.getList("StoredEnchantments", 10);
                for (int i = 0; i < storedEnchantments.size(); i++) {
                    CompoundTag enchantmentTag = storedEnchantments.getCompound(i);
                    ResourceLocation enchantmentId = new ResourceLocation(enchantmentTag.getString("id"));
                    Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.getOptional(enchantmentId).orElse(null);
                    if (enchantment != null) {
                        int level = enchantmentTag.getInt("lvl");
                        newEnchantments.put(enchantment, level + 1);
                    }
                }
            }
        } else {
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                Enchantment enchantment = entry.getKey();
                int level = entry.getValue();
                newEnchantments.put(enchantment, level + 1);
            }
        }
        EnchantmentHelper.setEnchantments(newEnchantments, output);
        CompoundTag outputTag = output.getOrCreateTag();
        outputTag.putInt(ENHANCEMENT_COUNT_TAG, enhancementCount + 1);
        int currentRepairCost = output.getBaseRepairCost();
        output.setRepairCost(currentRepairCost + 1);
        event.setOutput(output);
        event.setCost(EXPERIENCE_COST);
        event.setMaterialCost(1);

        if (player.containerMenu instanceof DarkAnvilMenu darkAnvilMenu) {
            darkAnvilMenu.repairItemCountCost = 1;
        }
    }

    public static int getEnhancementCount(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(ENHANCEMENT_COUNT_TAG)) {
            return 0;
        }
        return tag.getInt(ENHANCEMENT_COUNT_TAG);
    }

    public static boolean isMaxEnhanced(ItemStack stack) {
        return getEnhancementCount(stack) >= MAX_ENHANCEMENTS;
    }

    @SubscribeEvent
    public static void onGrimoireInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity() == null || event.getTarget() == null) {
            return;
        }

        Player player = event.getEntity();
        ItemStack itemStack = event.getItemStack();
        if (itemStack != null && itemStack.getItem() instanceof GrimoireItem grimoire) {
            if (event.getTarget() instanceof SorcererServant sorcererServant) {
                if (sorcererServant.getTrueOwner() == player) {
                    int grimoireLevel = grimoire.getLevel();
                    int currentLevel = sorcererServant.getSorcererLevel();
                    if (currentLevel < grimoireLevel) {
                        if (grimoireLevel == 6) {
                            if (player instanceof ServerPlayer serverPlayer) {
                                ModCriteriaTriggers.UPGRADE_WIZARD.trigger(serverPlayer);
                            }
                        }
                        if (!player.getAbilities().instabuild) {
                            itemStack.shrink(1);
                        }
                        sorcererServant.setSorcererLevel(grimoireLevel, true);
                        event.setCanceled(true);
                        event.setCancellationResult(InteractionResult.SUCCESS);
                    } else {
                        event.setCanceled(true);
                        event.setCancellationResult(InteractionResult.FAIL);
                    }
                } else {
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.FAIL);
                }
            } else if (event.getTarget() instanceof Neollager neollager) {
                if (neollager.getTrueOwner() == player && !neollager.isMagic()) {
                    if (!player.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }
                    neollager.setMagic(true);
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                } else {
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.FAIL);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onGrimoireRename(ItemStackedOnOtherEvent event) {
        ItemStack carriedItem = event.getCarriedItem();
        if (carriedItem.getItem() instanceof GrimoireItem) {
            if (!carriedItem.hasCustomHoverName()) {
                GrimoireItem grimoire = (GrimoireItem) carriedItem.getItem();
                int level = grimoire.getLevel();
                renameGrimoire(carriedItem, level);
            }
        }
    }

    public static void renameGrimoire(ItemStack stack, int level) {
        if (stack.hasCustomHoverName()) {
            return;
        }

        String name1Prefix = "";
        String name2Prefix = "";
        int name1Count = 0;
        int name2Count = 0;

        switch (level) {
            case 2:
                name1Prefix = LV2NAME1_PREFIX;
                name2Prefix = LV2NAME2_PREFIX;
                name1Count = LV2NAME1_COUNT;
                name2Count = LV2NAME2_COUNT;
                break;
            case 3:
                name1Prefix = LV3NAME1_PREFIX;
                name2Prefix = LV3NAME2_PREFIX;
                name1Count = LV3NAME1_COUNT;
                name2Count = LV3NAME2_COUNT;
                break;
            case 4:
                name1Prefix = LV4NAME1_PREFIX;
                name2Prefix = LV4NAME2_PREFIX;
                name1Count = LV4NAME1_COUNT;
                name2Count = LV4NAME2_COUNT;
                break;
            case 5:
                name1Prefix = LV5NAME1_PREFIX;
                name2Prefix = LV5NAME2_PREFIX;
                name1Count = LV5NAME1_COUNT;
                name2Count = LV5NAME2_COUNT;
                break;
            case 6:
                name1Prefix = LV6NAME1_PREFIX;
                name2Prefix = LV6NAME2_PREFIX;
                name1Count = LV6NAME1_COUNT;
                name2Count = LV6NAME2_COUNT;
                break;
        }

        if (name1Count > 0 && name2Count > 0) {
            int name1Index = RANDOM.nextInt(name1Count);
            int name2Index = RANDOM.nextInt(name2Count);

            String name1Key = name1Prefix + name1Index;
            String name2Key = name2Prefix + name2Index;

            MutableComponent name1Component = Component.translatable(name1Key);
            MutableComponent name2Component = Component.translatable(name2Key);

            MutableComponent newName = Component.literal("《")
                    .append(name1Component)
                    .append(name2Component)
                    .append("》");
            ChatFormatting color = ChatFormatting.WHITE;
            switch (level) {
                case 2:
                    color = ChatFormatting.GRAY;
                    break;
                case 3:
                    color = ChatFormatting.YELLOW;
                    break;
                case 4:
                    color = ChatFormatting.GREEN;
                    break;
                case 5:
                    color = ChatFormatting.AQUA;
                    break;
                case 6:
                    color = ChatFormatting.RED;
                    break;
            }

            newName.withStyle(color);
            stack.setHoverName(newName);
        }
    }

    @SubscribeEvent
    public static void onArrowSpawn(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof net.minecraft.world.entity.projectile.Arrow arrow) || entity.level().isClientSide) {
            return;
        }

        net.minecraft.resources.ResourceLocation entityId = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE
                .getKey(entity.getType());
        if (!entityId.toString().equals("minecraft:arrow")) {
            return;
        }

        LivingEntity shooter = arrow.getOwner() instanceof LivingEntity livingEntity ? livingEntity : null;
        if (shooter == null) {
            return;
        }

        if (!DetonationRingItem.hasDetonationRing(shooter)) {
            return;
        }

        Level level = entity.level();
        if (level.random.nextFloat() < 0.25F) {
            ExplosiveArrow explosiveArrow = new ExplosiveArrow(level, shooter);
            explosiveArrow.setPos(arrow.getX(), arrow.getY(), arrow.getZ());
            explosiveArrow.setDeltaMovement(arrow.getDeltaMovement());
            explosiveArrow.setBaseDamage(arrow.getBaseDamage());
            explosiveArrow.setPierceLevel(arrow.getPierceLevel());
            if (arrow.isCritArrow()) {
                explosiveArrow.setCritArrow(true);
            }

            if (arrow.isOnFire()) {
                explosiveArrow.setSecondsOnFire(100);
            }
            explosiveArrow.load(arrow.saveWithoutId(new CompoundTag()));
            event.setCanceled(true);
            level.addFreshEntity(explosiveArrow);
        }
    }
}
