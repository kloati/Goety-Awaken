package com.k1sak1.goetyawaken.common.items;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.PacketDistributor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.Polarice3.Goety.api.items.ISoulRepair;
import com.Polarice3.Goety.api.items.magic.IFocus;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.VoidSlash;
import com.Polarice3.Goety.common.items.ModTiers;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.k1sak1.goetyawaken.common.magic.spells.StarlessNightSpell;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.network.ModNetwork;
import com.k1sak1.goetyawaken.common.network.client.CStarlessNightSlashPacket;
import net.minecraft.world.effect.MobEffectInstance;
import javax.annotation.Nullable;
import java.util.UUID;

public class StarlessNightItem extends SwordItem implements ISoulRepair, IFocus {
    private int attackCount = 0;
    private long lastAttackTime = 0;

    public StarlessNightItem() {
        super(ModTiers.VOID, 16, -3.2F + 1.0F, new Properties().durability(5152).fireResistant());
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.EPIC;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                    (float) Config.starlessNightDamage - 1.0F, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                    (float) Config.starlessNightAttackSpeed - 4.0F, AttributeModifier.Operation.ADDITION));
            builder.put(ForgeMod.ENTITY_REACH.get(),
                    new AttributeModifier(UUID.fromString("CB3F55D3-645C-4F38-A497-E30960792006"),
                            "Starless Night reach modifier", 2.0F, AttributeModifier.Operation.ADDITION));
            builder.put(Attributes.ATTACK_KNOCKBACK,
                    new AttributeModifier(UUID.fromString("CB3F55D3-645C-4F38-A497-E30960792007"),
                            "Starless Night knockback modifier", 1.0F, AttributeModifier.Operation.ADDITION));
            return builder.build();
        }
        return super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 30;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment == ModEnchantments.BURNING.get() ||
                enchantment == ModEnchantments.RANGE.get() ||
                enchantment == ModEnchantments.ABSORB.get() ||
                enchantment == ModEnchantments.MAGNET.get() ||
                enchantment == ModEnchantments.DURATION.get()) {
            return false;
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        target.addEffect(new MobEffectInstance(GoetyEffects.WANE.get(), 100, 0));

        if (attacker instanceof Player player) {
            if (player.getAttackStrengthScale(0) > 0.9F) {
                performFullSweepAttack(stack, player);
            }
        }

        return result;
    }

    private void playSweepEffects(Player player) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.2F, 0.8F);
        player.sweepAttack();
        if (player.level() instanceof ServerLevel serverLevel) {
            double d0 = -Mth.sin(player.getYRot() * ((float) Math.PI / 180F));
            double d1 = Mth.cos(player.getYRot() * ((float) Math.PI / 180F));
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                    player.getX() + d0, player.getY(0.5D), player.getZ() + d1,
                    0, d0, 0.0D, d1, 0.0D);
        }
    }

    @Override
    public AABB getSweepHitBox(ItemStack stack, Player player, Entity target) {
        double range = 5.0D;
        return target.getBoundingBox().inflate(range, 0.25D, range);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return net.minecraftforge.common.ToolActions.DEFAULT_SWORD_ACTIONS.contains(toolAction);
    }

    public void performFullSweepAttack(ItemStack stack, Player player) {
        float baseDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float enchantmentBonus = EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
        float totalBaseDamage = baseDamage + enchantmentBonus;
        int sweepingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, player);
        float sweepRatio = 0.0F;
        if (sweepingLevel <= 3) {
            sweepRatio = 0.25F * sweepingLevel;
        } else {
            sweepRatio = 0.75F + 0.15F * sweepingLevel;
        }
        float damage = totalBaseDamage * (0.25F + sweepRatio);
        double range = 3.0D;
        double entityReach = player.getAttributeValue(ForgeMod.ENTITY_REACH.get());
        AABB sweepBox = player.getBoundingBox().inflate(range, 0.25D, range);
        java.util.List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, sweepBox);
        boolean hitAny = false;

        for (LivingEntity sweepTarget : entities) {
            MobUtil.disableShield(sweepTarget, 30);
            if (sweepTarget != player
                    && !(sweepTarget instanceof net.minecraft.world.entity.decoration.ArmorStand armorStand
                            && armorStand.isMarker())
                    && player.canAttack(sweepTarget) && !MobUtil.areAllies(player, sweepTarget)) {
                double entityReachSq = Mth.square(range + entityReach);
                double distanceSquared = player.distanceToSqr(sweepTarget);
                if (distanceSquared < entityReachSq) {
                    sweepTarget.knockback(0.6F,
                            Mth.sin(player.getYRot() * ((float) Math.PI / 180F)),
                            -Mth.cos(player.getYRot() * ((float) Math.PI / 180F)));
                    sweepTarget.hurt(player.damageSources().playerAttack(player), damage);
                    if (!player.level().isClientSide) {
                        applyOrUpgradeVoidTouchedEffect(sweepTarget, player);
                        float originalHealth = sweepTarget.getHealth();
                        handleChainDamage(player, sweepTarget, damage, originalHealth);
                    }

                    hitAny = true;
                }
            }
        }
        playSweepEffects(player);
    }

    public static void fireVoidSlashOnServer(Level pLevel, Player pPlayer) {
        if (pPlayer.getAttackStrengthScale(0.5F) > 0.9F) {
            pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                    ModSounds.OBSIDIAN_CLAYMORE_SWING.get(), SoundSource.PLAYERS, 2.0F, pPlayer.getVoicePitch());
            ItemStack sword = pPlayer.getMainHandItem();
            float speed = 0.0F;
            float potency = 0.0F;
            float radius = 0.0F;
            if (sword.isEnchanted()) {
                potency = sword.getEnchantmentLevel(ModEnchantments.POTENCY.get()) / 10.0F;
                speed += sword.getEnchantmentLevel(ModEnchantments.VELOCITY.get()) / 5.0F;
                radius += sword.getEnchantmentLevel(ModEnchantments.RADIUS.get()) / 4.0F;
            }
            Vec3 vector3d = pPlayer.getViewVector(1.0F);
            VoidSlash slash = new VoidSlash(pLevel, pPlayer);
            slash.setPos(pPlayer.getX() + vector3d.x / 2,
                    pPlayer.getEyeY() - 0.2,
                    pPlayer.getZ() + vector3d.z / 2);
            slash.setDamage((float) pPlayer.getAttributeValue(Attributes.ATTACK_DAMAGE) * (0.5F + potency));
            slash.setMaxLifeSpan(MathHelper.secondsToTicks(0.75F));
            slash.setRadius(slash.getRadius() + radius);
            slash.setMaxRadius(slash.getMaxRadius() + radius);
            slash.slash(vector3d, 0.6F + speed);
            slash.setVoidLevel(1);
            pLevel.addFreshEntity(slash);
        }
    }

    public static void emptyClick(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof StarlessNightItem) {
            ModNetwork.channel.send(PacketDistributor.SERVER.noArg(), new CStarlessNightSlashPacket());
        }
    }

    public static void entityClick(Player player, Level world) {
        if (player.getMainHandItem().getItem() instanceof StarlessNightItem) {
            if (!player.level().isClientSide && !player.isSpectator()) {
                fireVoidSlashOnServer(world, player);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, java.util.List<Component> tooltip,
            TooltipFlag flag) {
        super.appendHoverText(stack, worldIn, tooltip, flag);
        tooltip.add(Component.translatable("item.goetyawaken.claymore.tooltip")
                .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("item.goetyawaken.starless_night.tooltip1")
                .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("item.goetyawaken.starless_night.tooltip2")
                .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("item.goetyawaken.starless_night.tooltip3")
                .withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE));
    }

    @Override
    public void repairTick(ItemStack stack, Entity entityIn, boolean isSelected) {
        com.Polarice3.Goety.utils.ItemHelper.repairTick(stack, entityIn, isSelected);
    }

    private void applyOrUpgradeVoidTouchedEffect(LivingEntity target, Player player) {
        MobEffectInstance existingEffect = target.getEffect(GoetyEffects.VOID_TOUCHED.get());
        int newAmplifier = 0;
        int duration = 40;
        if (existingEffect != null) {
            newAmplifier = Math.min(existingEffect.getAmplifier() + 1, 4);
        }
        com.k1sak1.goetyawaken.utils.MobEffectUtils.forceAdd(target, new MobEffectInstance(
                com.Polarice3.Goety.common.effects.GoetyEffects.VOID_TOUCHED.get(), duration, newAmplifier), player);
    }

    private void handleChainDamage(Player player, LivingEntity originalTarget, float damage, float originalHealth) {
        float excessDamage = damage - originalHealth;
        if (excessDamage <= 0) {
            return;
        }

        double searchRange = 8.0D;
        int maxChainTargets = 5;
        AABB searchBox = originalTarget.getBoundingBox().inflate(searchRange);
        java.util.List<LivingEntity> nearbyEnemies = player.level()
                .getEntitiesOfClass(LivingEntity.class, searchBox)
                .stream()
                .filter(entity -> entity != originalTarget && entity != player)
                .filter(entity -> entity.isAlive())
                .filter(entity -> player.canAttack(entity))
                .filter(entity -> !MobUtil.areAllies(player, entity))
                .sorted(java.util.Comparator.comparingDouble(entity -> entity.distanceToSqr(originalTarget)))
                .limit(maxChainTargets)
                .toList();

        float remainingDamage = excessDamage;
        for (LivingEntity nearbyEnemy : nearbyEnemies) {
            if (remainingDamage <= 0)
                break;

            float enemyHealth = nearbyEnemy.getHealth();
            if (remainingDamage >= enemyHealth) {
                nearbyEnemy.hurt(player.damageSources().playerAttack(player), remainingDamage);
                remainingDamage -= enemyHealth;
            } else {
                nearbyEnemy.hurt(player.damageSources().playerAttack(player), remainingDamage);
                remainingDamage = 0;
                break;
            }
        }
    }

    @Override
    public ISpell getSpell() {
        return new StarlessNightSpell();
    }
}