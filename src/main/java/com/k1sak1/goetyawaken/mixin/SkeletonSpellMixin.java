package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.necromancy.SkeletonSpell;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SoundUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.ally.PoisonousPotatoSkeletonServant;
import com.k1sak1.goetyawaken.common.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkeletonSpell.class)
public class SkeletonSpellMixin {

    @Inject(method = "SpellResult", at = @At("HEAD"), cancellable = true, remap = false)
    private void onSpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff,
            SpellStat spellStat, CallbackInfo ci) {
        if (!staff.is(ModItems.POTATO_STAFF.get())) {
            return;
        }
        ci.cancel();

        SkeletonSpell spell = (SkeletonSpell) (Object) this;
        spell.commonResult(worldIn, caster);

        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }

        int i = 2;
        if (spell.rightStaff(staff)) {
            i = 2 + caster.level().random.nextInt(4);
        }

        if (!spell.isShifting(caster)) {
            for (int i1 = 0; i1 < i; ++i1) {
                PoisonousPotatoSkeletonServant summonedentity = new PoisonousPotatoSkeletonServant(
                        ModEntityType.POISONOUS_POTATO_SKELETON_SERVANT.get(), worldIn);
                BlockPos blockPos = BlockFinder.SummonRadius(caster.blockPosition(), summonedentity, worldIn);
                if (caster.isUnderWater()) {
                    blockPos = BlockFinder.SummonWaterRadius(caster, worldIn);
                }
                summonedentity.setTrueOwner(caster);
                summonedentity.moveTo(blockPos, 0.0F, 0.0F);
                if (summonedentity.getType() != com.Polarice3.Goety.common.entities.ModEntityType.SUNKEN_SKELETON_SERVANT
                        .get()) {
                    MobUtil.moveDownToGround(summonedentity);
                }
                summonedentity.setPersistenceRequired();
                summonedentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                summonedentity.setArrowPower(potency);
                summonedentity.finalizeSpawn(worldIn,
                        worldIn.getCurrentDifficultyAt(caster.blockPosition()),
                        MobSpawnType.MOB_SUMMONED, null, null);
                spell.buffSummon(caster, summonedentity, potency);
                spell.SummonSap(caster, summonedentity);
                spell.setTarget(caster, summonedentity);
                if (worldIn.addFreshEntity(summonedentity)) {
                    spell.uponSummon(worldIn, caster, staff, summonedentity);
                }
                spell.summonAdvancement(caster, summonedentity);
            }
            spell.SummonDown(caster);
            SoundUtil.playNecromancerSummon(caster);
        }
    }
}
