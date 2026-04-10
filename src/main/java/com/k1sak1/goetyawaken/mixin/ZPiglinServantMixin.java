package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.Polarice3.Goety.common.entities.neutral.ZPiglinServant")
public class ZPiglinServantMixin {

    private boolean hasFullNetheriteArmor(net.minecraft.world.entity.LivingEntity entity) {
        if (!entity.getItemBySlot(EquipmentSlot.HEAD).is(Items.NETHERITE_HELMET)) {
            return false;
        }
        if (!entity.getItemBySlot(EquipmentSlot.CHEST).is(Items.NETHERITE_CHESTPLATE)) {
            return false;
        }
        if (!entity.getItemBySlot(EquipmentSlot.LEGS).is(Items.NETHERITE_LEGGINGS)) {
            return false;
        }
        if (!entity.getItemBySlot(EquipmentSlot.FEET).is(Items.NETHERITE_BOOTS)) {
            return false;
        }
        return true;
    }

    @Inject(method = "populateDefaultWeapons", at = @At("HEAD"), cancellable = true, remap = false)

    private void modifyWeaponsForSummonedZPiglin(RandomSource randomSource, DifficultyInstance difficulty,
            CallbackInfo ci) {
        if (this instanceof com.Polarice3.Goety.api.entities.IOwned owned) {
            net.minecraft.world.entity.LivingEntity owner = owned.getTrueOwner();
            boolean isSecondPhaseApostle = false;
            if (owner instanceof com.Polarice3.Goety.common.entities.boss.Apostle apostle) {
                isSecondPhaseApostle = apostle.isSecondPhase();
            } else if (owner instanceof com.k1sak1.goetyawaken.common.entities.ally.illager.ApostleServant apostleServant) {
                isSecondPhaseApostle = apostleServant.isSecondPhase();
            }
            boolean hasFullNetheriteArmor = hasFullNetheriteArmor((ZPiglinServant) (Object) this);
            if (isSecondPhaseApostle &&
                    ((net.minecraft.world.level.Level) ((ZPiglinServant) (Object) this).level())
                            .getDifficulty() == net.minecraft.world.Difficulty.HARD
                    &&
                    hasFullNetheriteArmor) {
                ServerLevel serverLevel = (ServerLevel) ((ZPiglinServant) (Object) this).level();
                ItemStack weapon = new ItemStack(Items.NETHERITE_AXE);
                EnchantmentHelper.enchantItem(serverLevel.random, weapon, 15 + serverLevel.random.nextInt(10), false);
                weapon.enchant(Enchantments.BINDING_CURSE, 1);
                weapon.enchant(Enchantments.VANISHING_CURSE, 1);
                ((ZPiglinServant) (Object) this).setItemSlot(EquipmentSlot.MAINHAND, weapon);
                ((ZPiglinServant) (Object) this).setDropChance(EquipmentSlot.MAINHAND, 0.0F);
                ci.cancel();
            }
        }
    }
}