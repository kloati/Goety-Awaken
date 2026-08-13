package com.k1sak1.goetyawaken.mixin;

import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.abyss.GuardianSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.ally.ToxifinServant;
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

@Mixin(GuardianSpell.class)
public class GuardianSpellMixin {

    @Inject(method = "SpellResult", at = @At("HEAD"), cancellable = true, remap = false)
    private void onSpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff,
            SpellStat spellStat, CallbackInfo ci) {
        if (!staff.is(ModItems.POTATO_STAFF.get())) {
            return;
        }
        ci.cancel();

        GuardianSpell spell = (GuardianSpell) (Object) this;
        spell.commonResult(worldIn, caster);

        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }

        if (!spell.isShifting(caster)) {
            int i = 1;
            if (spell.rightStaff(staff)) {
                i = 2;
            }
            for (int i1 = 0; i1 < i; ++i1) {
                ToxifinServant servant = new ToxifinServant(
                        ModEntityType.TOXIFIN_SERVANT.get(), worldIn);
                BlockPos blockPos = BlockFinder.SummonRadius(caster.blockPosition(), servant, worldIn);
                if (caster.isUnderWater()) {
                    blockPos = BlockFinder.SummonWaterRadius(caster, worldIn);
                }
                servant.setTrueOwner(caster);
                servant.moveTo(blockPos, 0.0F, 0.0F);
                servant.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                servant.setPersistenceRequired();
                servant.finalizeSpawn(worldIn,
                        worldIn.getCurrentDifficultyAt(caster.blockPosition()),
                        MobSpawnType.MOB_SUMMONED, null, null);
                spell.buffSummon(caster, servant, potency);
                spell.SummonSap(caster, servant);
                spell.setTarget(caster, servant);
                if (worldIn.addFreshEntity(servant)) {
                    spell.uponSummon(worldIn, caster, staff, servant);
                }
                spell.summonAdvancement(caster, servant);
            }
            spell.SummonDown(caster);
            spell.playSound(worldIn, caster, ModSounds.DROWNED_NECROMANCER_SUMMON.get());
        }
    }
}
