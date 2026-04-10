package com.k1sak1.goetyawaken.common.magic.spells.wild;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.entities.ally.CreeperServant;
import com.k1sak1.goetyawaken.common.entities.ally.IceCreeperServant;
import com.k1sak1.goetyawaken.common.entities.ally.SpiderCreeder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class CreeperSpell extends SummonSpell {
    @Override
    public int defaultSoulCost() {
        return Config.CREEPER_FOCUS_SOUL_COST.get();
    }

    @Override
    public int defaultCastDuration() {
        return Config.CREEPER_FOCUS_CAST_DURATION.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return ModSounds.WILD_PREPARE_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.CREEPER_FOCUS_COOLDOWN.get();
    }

    @Override
    public int SummonDownDuration() {
        return Config.creeperFocusCooldown;
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
        return livingEntity -> livingEntity instanceof CreeperServant || livingEntity instanceof IceCreeperServant;
    }

    @Override
    public int summonLimit() {
        return Config.creeperServantLimit;
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
                i = worldIn.getRandom().nextInt(3) + 2;
            }
            for (int i1 = 0; i1 < i; ++i1) {
                boolean shouldSummonIceCreeper = false;
                if (typeStaff(staff, SpellType.FROST) ||
                        (staff.getItem() instanceof com.Polarice3.Goety.common.items.magic.DarkWand &&
                                staff.getItem().getClass().getSimpleName().contains("Frost"))) {
                    shouldSummonIceCreeper = true;
                }
                if (worldIn.getBiome(caster.blockPosition()).is(Tags.Biomes.IS_COLD_OVERWORLD)) {
                    shouldSummonIceCreeper = true;
                }

                boolean shouldSummonSpiderCreeder = false;
                if (typeStaff(staff, SpellType.NETHER)) {
                    shouldSummonSpiderCreeder = true;
                }
                if (worldIn.dimension() == net.minecraft.world.level.Level.NETHER) {
                    shouldSummonSpiderCreeder = true;
                }

                com.Polarice3.Goety.common.entities.ally.Summoned summonedentity;
                if (shouldSummonSpiderCreeder) {
                    summonedentity = new SpiderCreeder(ModEntityType.SPIDER_CREEDER.get(), worldIn);
                } else if (shouldSummonIceCreeper) {
                    summonedentity = new IceCreeperServant(ModEntityType.ICE_CREEPER_SERVANT.get(), worldIn);
                } else {
                    summonedentity = new CreeperServant(ModEntityType.CREEPER_SERVANT.get(), worldIn);
                }

                if (this.typeStaff(staff, SpellType.STORM)) {
                    if (summonedentity instanceof CreeperServant creeperServant) {
                        creeperServant.setPowered(true);
                    } else if (summonedentity instanceof IceCreeperServant iceCreeperServant) {
                        iceCreeperServant.setPowered(true);
                    } else if (summonedentity instanceof SpiderCreeder creeder) {
                        creeder.setPowered(true);
                    }
                }

                BlockPos summonPos = BlockFinder.SummonRadius(caster.blockPosition(), summonedentity, worldIn);
                summonedentity.setTrueOwner(caster);
                summonedentity.moveTo(summonPos, 0.0F, 0.0F);
                MobUtil.moveDownToGround(summonedentity);
                summonedentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                summonedentity.setPersistenceRequired();
                summonedentity.finalizeSpawn(worldIn, worldIn.getCurrentDifficultyAt(caster.blockPosition()),
                        MobSpawnType.MOB_SUMMONED, null, null);
                this.buffSummon(caster, summonedentity, potency);
                this.SummonSap(caster, summonedentity);
                this.setTarget(caster, summonedentity);
                worldIn.addFreshEntity(summonedentity);
                this.summonAdvancement(caster, summonedentity);
            }
            this.SummonDown(caster);
            this.playSound(worldIn, caster, ModSounds.SUMMON_SPELL.get());
        }
    }
}