package com.k1sak1.goetyawaken.common.magic.spells.wild;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.ally.CaerbannogRabbitServant;
import com.k1sak1.goetyawaken.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class KillerSpell extends SummonSpell {
    @Override
    public int defaultSoulCost() {
        return Config.KILLER_SPELL_SOUL_COST.get();
    }

    @Override
    public int defaultCastDuration() {
        return Config.KILLER_SPELL_CAST_DURATION.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.WILD_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.KILLER_SPELL_COOLDOWN.get();
    }

    @Override
    public int SummonDownDuration() {
        return Config.KILLER_SPELL_SUMMON_DOWN_DURATION.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WILD;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof CaerbannogRabbitServant;
    }

    @Override
    public int summonLimit() {
        return Config.caerbannogRabbitServantLimit;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }

        if (!isShifting(caster)) {
            int i = 1;
            if (rightStaff(staff)) {
                i = 2;
            }

            boolean hasWildRobe = CuriosFinder.hasWildRobe(caster);

            for (int i1 = 0; i1 < i; ++i1) {
                CaerbannogRabbitServant summonedentity = new CaerbannogRabbitServant(
                        ModEntityType.CAERBANNOG_RABBIT_SERVANT.get(), worldIn);
                BlockPos summonPos = BlockFinder.SummonRadius(caster.blockPosition(), summonedentity, worldIn);
                summonedentity.setTrueOwner(caster);
                summonedentity.moveTo(summonPos, 0.0F, 0.0F);
                MobUtil.moveDownToGround(summonedentity);
                summonedentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                summonedentity.setPersistenceRequired();
                summonedentity.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(caster.blockPosition()),
                        MobSpawnType.MOB_SUMMONED, null, null);
                this.buffSummon(caster, summonedentity, potency);
                if (hasWildRobe) {
                    summonedentity.setUpgraded(true);
                }

                this.SummonSap(caster, summonedentity);
                this.setTarget(caster, summonedentity);
                worldIn.addFreshEntity(summonedentity);
                this.summonAdvancement(caster, summonedentity);
                this.summonParticles(worldIn, caster, staff, summonedentity);
            }

            this.SummonDown(caster);
            this.playSound(worldIn, caster, ModSounds.SUMMON_SPELL.get());
        }
    }
}