package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.common.entities.boss.Apostle;
import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.Polarice3.Goety.common.entities.util.SummonCircle;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.utils.BlockFinder;
import com.k1sak1.goetyawaken.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.Polarice3.Goety.common.entities.boss.Apostle$RangedSummonSpellGoal")
public class ApostleRangedSummonMixin {

    @Shadow
    @Final
    private Apostle this$0;

    @Inject(method = "castSpell", at = @At(value = "TAIL"), remap = false)
    private void injectArmoredZombiePiglinSummon(CallbackInfo ci) {
        if (Config.apostleSummonArmoredZombiePiglin) {
            ServerLevel serverLevel = (ServerLevel) this$0.level();
            LivingEntity target = this$0.getTarget();
            RandomSource random = this$0.getRandom();
            summonArmoredZombiePiglin(this$0, serverLevel, target, random);
            if (this$0.isInNether()) {
                int extraSummons = 1 + random.nextInt(3);
                for (int i = 0; i < extraSummons; i++) {
                    summonArmoredZombiePiglin(this$0, serverLevel, target, random);
                }
            }
        }
    }

    private void summonArmoredZombiePiglin(Apostle apostleInstance, ServerLevel serverLevel, LivingEntity target,
            RandomSource random) {
        if (!Config.apostleSummonArmoredZombiePiglin) {
            return;
        }

        boolean isPhase2 = apostleInstance.isSecondPhase();
        boolean isHardDifficulty = serverLevel.getDifficulty() == Difficulty.HARD;

        ZPiglinServant zombiePiglin;
        if (isPhase2) {
            zombiePiglin = ModEntityType.ZPIGLIN_BRUTE_SERVANT.get().create(serverLevel);
        } else {
            zombiePiglin = ModEntityType.ZPIGLIN_SERVANT.get().create(serverLevel);
        }

        if (zombiePiglin != null) {
            BlockPos.MutableBlockPos blockPos = apostleInstance.blockPosition().mutable();
            blockPos.move(
                    random.nextInt(6) - random.nextInt(6),
                    0,
                    random.nextInt(6) - random.nextInt(6));
            BlockPos finalPos = BlockFinder.SummonRadius(blockPos, zombiePiglin, serverLevel, 5);
            zombiePiglin.moveTo(finalPos, 0.0F, 0.0F);
            if (zombiePiglin instanceof com.Polarice3.Goety.api.entities.IOwned) {
                ((com.Polarice3.Goety.api.entities.IOwned) zombiePiglin).setTrueOwner(apostleInstance);
            }
            EquipmentSlot[] armorSlots = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS,
                    EquipmentSlot.FEET };

            for (EquipmentSlot slot : armorSlots) {
                ItemStack armor;
                if (isHardDifficulty) {
                    switch (slot) {
                        case HEAD -> armor = new ItemStack(net.minecraft.world.item.Items.NETHERITE_HELMET);
                        case CHEST -> armor = new ItemStack(net.minecraft.world.item.Items.NETHERITE_CHESTPLATE);
                        case LEGS -> armor = new ItemStack(net.minecraft.world.item.Items.NETHERITE_LEGGINGS);
                        case FEET -> armor = new ItemStack(net.minecraft.world.item.Items.NETHERITE_BOOTS);
                        default -> armor = ItemStack.EMPTY;
                    }
                } else {
                    switch (slot) {
                        case HEAD -> armor = new ItemStack(net.minecraft.world.item.Items.GOLDEN_HELMET);
                        case CHEST -> armor = new ItemStack(net.minecraft.world.item.Items.GOLDEN_CHESTPLATE);
                        case LEGS -> armor = new ItemStack(net.minecraft.world.item.Items.GOLDEN_LEGGINGS);
                        case FEET -> armor = new ItemStack(net.minecraft.world.item.Items.GOLDEN_BOOTS);
                        default -> armor = ItemStack.EMPTY;
                    }
                }

                if (!armor.isEmpty()) {
                    EnchantmentHelper.enchantItem(serverLevel.random, armor, 10 + serverLevel.random.nextInt(10),
                            false);
                    armor.enchant(Enchantments.BINDING_CURSE, 1);
                    armor.enchant(Enchantments.VANISHING_CURSE, 1);
                }
                zombiePiglin.setItemSlot(slot, armor);
            }
            zombiePiglin.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, false, true));
            zombiePiglin.finalizeSpawn(serverLevel,
                    serverLevel.getCurrentDifficultyAt(finalPos),
                    MobSpawnType.MOB_SUMMONED, null, null);
            if (isPhase2 && isHardDifficulty) {
                ItemStack weapon = new ItemStack(Items.NETHERITE_AXE);
                EnchantmentHelper.enchantItem(serverLevel.random, weapon, 15 + serverLevel.random.nextInt(10), false);
                weapon.enchant(Enchantments.BINDING_CURSE, 1);
                weapon.enchant(Enchantments.VANISHING_CURSE, 1);
                zombiePiglin.setItemSlot(EquipmentSlot.MAINHAND, weapon);
                zombiePiglin.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
            }
            if (target != null) {
                zombiePiglin.setTarget(target);
            }
            SummonCircle summonCircle = new SummonCircle(apostleInstance.level(), finalPos, zombiePiglin, true, true,
                    apostleInstance);
            apostleInstance.level().addFreshEntity(summonCircle);
        }
    }
}
