package com.k1sak1.goetyawaken.common.events;

import java.util.UUID;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.utils.ItemHelper;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.k1sak1.goetyawaken.common.items.FrostScytheItem;
import com.k1sak1.goetyawaken.common.items.TruthseekerItem;
import com.k1sak1.goetyawaken.common.items.curios.AssassinGloveItem;
import com.k1sak1.goetyawaken.common.items.StarlessNightItem;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.TieredItem;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import com.k1sak1.goetyawaken.common.items.ModItems;
import com.k1sak1.goetyawaken.init.ModTags;
import com.Polarice3.Goety.utils.CuriosFinder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "goetyawaken", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemEvents {

    @SubscribeEvent
    public static void PlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase == TickEvent.Phase.END) {
            AttributeInstance attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
            boolean hasDauntlessGloves = CuriosFinder.hasCurio(player, ModItems.DAUNTLESS_GLOVES.get());
            boolean mainHandValid = player.getMainHandItem().is(ModItems.OBSIDIAN_CLAYMORE.get())
                    || player.getMainHandItem().is(ModItems.CLAYMORE.get())
                    || player.getMainHandItem().is(ModItems.STARLESS_NIGHT.get())
                    || player.getMainHandItem().is(ModTags.Items.DAUNTLESS_GLOVE_BOOST)
                    || com.k1sak1.goetyawaken.utils.AlternativeTagChecker
                            .isItemInDauntlessBoostTag(player.getMainHandItem())
                    || player.getMainHandItem().is(com.Polarice3.Goety.common.items.ModItems.BLADE_OF_ENDER.get());

            float increaseAttackSpeed = 0.25F;
            AttributeModifier attributemodifier = new AttributeModifier(
                    UUID.fromString("4f480e4c-3ef9-4ad6-b6d0-73681939724e"),
                    "Dauntless Proficiency",
                    increaseAttackSpeed,
                    AttributeModifier.Operation.MULTIPLY_TOTAL);
            boolean flag = hasDauntlessGloves && mainHandValid;

            if (attackSpeed != null) {
                if (flag) {
                    if (!attackSpeed.hasModifier(attributemodifier)) {
                        attackSpeed.addPermanentModifier(attributemodifier);
                    }
                } else {
                    if (attackSpeed.hasModifier(attributemodifier)) {
                        attackSpeed.removeModifier(attributemodifier);
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
                                float bonusDamageMultiplier = lostHealthPercent * 3.0F;
                                float additionalDamage = event.getAmount() * bonusDamageMultiplier;
                                event.setAmount(event.getAmount() + additionalDamage);
                            }
                        }
                        if (AssassinGloveItem.hasAssassinGloveItem(livingAttacker)) {
                            boolean isTagMatch = livingAttacker.getMainHandItem()
                                    .is(ModTags.Items.ASSASSIN_GLOVE_BOOST);
                            boolean isAlternativeTagMatch = com.k1sak1.goetyawaken.utils.AlternativeTagChecker
                                    .isItemInAssassinBoostTag(livingAttacker.getMainHandItem());
                            boolean hasDagger = isTagMatch || isAlternativeTagMatch
                                    || livingAttacker.getMainHandItem().is(ModItems.TRUTHSEEKER.get())
                                    || livingAttacker.getMainHandItem()
                                            .is(com.Polarice3.Goety.common.items.ModItems.HUNGRY_DAGGER.get())
                                    || livingAttacker.getMainHandItem()
                                            .is(com.Polarice3.Goety.common.items.ModItems.FANGED_DAGGER.get());

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

    public static boolean isLookingBehindTarget(LivingEntity target, net.minecraft.world.phys.Vec3 attackerLocation) {
        if (attackerLocation != null) {
            net.minecraft.world.phys.Vec3 lookingVector = target.getViewVector(1.0F);
            net.minecraft.world.phys.Vec3 attackAngleVector = attackerLocation.subtract(target.position()).normalize();
            attackAngleVector = new net.minecraft.world.phys.Vec3(attackAngleVector.x, 0.0D, attackAngleVector.z);
            return attackAngleVector.dot(lookingVector) < -0.5D;
        }
        return false;
    }

    private static boolean isBackAttack(LivingEntity attacker, LivingEntity victim,
            net.minecraft.world.phys.Vec3 sourcePosition) {
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
}