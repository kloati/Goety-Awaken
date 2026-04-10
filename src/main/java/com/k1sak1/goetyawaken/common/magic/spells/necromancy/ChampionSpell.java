package com.k1sak1.goetyawaken.common.magic.spells.necromancy;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.common.magic.spells.necromancy.BlackguardSpell;
import com.Polarice3.Goety.common.magic.spells.necromancy.VanguardSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.LichdomHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.api.IAncientGlint;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton.VanguardChampion;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ChampionSpell extends SummonSpell {

    public int defaultSoulCost() {
        return com.k1sak1.goetyawaken.Config.CHAMPION_FOCUS_SOUL_COST.get();
    }

    public int defaultCastDuration() {
        return com.k1sak1.goetyawaken.Config.CHAMPION_FOCUS_CAST_DURATION.get();
    }

    public int SummonDownDuration() {
        return com.k1sak1.goetyawaken.Config.CHAMPION_FOCUS_COOLDOWN.get();
    }

    @Override
    public SoundEvent loopSound(LivingEntity caster) {
        return ModSounds.VANGUARD_SPELL.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return com.k1sak1.goetyawaken.Config.CHAMPION_FOCUS_COOLDOWN.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NECROMANCY;
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
        return livingEntity -> livingEntity instanceof VanguardChampion;
    }

    @Override
    public int summonLimit() {
        return com.k1sak1.goetyawaken.Config.VANGUARD_CHAMPION_LIMIT.get();
    }

    @Override
    public void commonResultHit(ServerLevel worldIn, LivingEntity caster) {
        for (int i = 0; i < caster.level().random.nextInt(35) + 10; ++i) {
            worldIn.sendParticles(ModParticleTypes.LICH.get(), caster.getX(), caster.getEyeY(), caster.getZ(), 1, 0.0F,
                    0.0F, 0.0F, 0);
        }
        this.playSound(worldIn, caster, ModSounds.VANGUARD_SUMMON.get());
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        if (!(caster instanceof Player player) || !LichdomHelper.isLich(player)) {
            if (worldIn.random.nextFloat() < 0.5F) {
                VanguardSpell vanguardSpell = new VanguardSpell();
                vanguardSpell.SpellResult(worldIn, caster, staff, spellStat);
            } else {
                BlackguardSpell blackguardSpell = new BlackguardSpell();
                blackguardSpell.SpellResult(worldIn, caster, staff, spellStat);
            }
            return;
        }

        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }

        if (!isShifting(caster)) {
            boolean hasNamelessSet = CuriosFinder.hasNamelessSet(caster);
            boolean hasNamelessStaff = staff.is(ModItems.NAMELESS_STAFF.get());
            boolean hasNecroStaff = staff.is(ModItems.NECRO_STAFF.get());

            int summonCount = 1;
            if (hasNamelessSet && hasNamelessStaff) {
                summonCount = 2;
            }

            Vec3 vec3 = caster.position();
            Direction direction = caster.getDirection();
            double stepX = direction.getStepX();
            double stepZ = direction.getStepZ();

            for (int i1 = 0; i1 < summonCount; i1++) {
                VanguardChampion vanguardChampion = new VanguardChampion(ModEntityType.VANGUARD_CHAMPION.get(),
                        worldIn);
                vanguardChampion.setTrueOwner(caster);
                if (summonCount == 2) {
                    double offset = 1.5;
                    double sideOffset = 0.8;
                    double spawnX, spawnZ;
                    if (i1 == 0) {
                        spawnX = vec3.x() + (stepX * offset) - (stepZ * sideOffset);
                        spawnZ = vec3.z() + (stepZ * offset) + (stepX * sideOffset);
                    } else {
                        spawnX = vec3.x() + (stepX * offset) + (stepZ * sideOffset);
                        spawnZ = vec3.z() + (stepZ * offset) - (stepX * sideOffset);
                    }

                    Vec3 spawnPos = new Vec3(spawnX, vec3.y(), spawnZ);
                    vanguardChampion.setPos(spawnPos);
                } else {
                    double spawnX = vec3.x() + (stepX * 2.0);
                    double spawnZ = vec3.z() + (stepZ * 2.0);
                    Vec3 spawnPos = new Vec3(spawnX, vec3.y(), spawnZ);
                    vanguardChampion.setPos(spawnPos);
                }

                MobUtil.moveDownToGround(vanguardChampion);
                vanguardChampion.setPersistenceRequired();
                vanguardChampion.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                this.buffSummon(caster, vanguardChampion, potency);
                vanguardChampion.finalizeSpawn(worldIn, caster.level().getCurrentDifficultyAt(caster.blockPosition()),
                        MobSpawnType.MOB_SUMMONED, null, null);
                if (summonCount == 2) {
                    int currentProtectionPoints = vanguardChampion.getProtectionPoints();
                    vanguardChampion.setProtectionPoints(currentProtectionPoints + 4);
                    if (vanguardChampion instanceof IAncientGlint glint) {
                        glint.setAncientGlint(true);
                        glint.setGlintTextureType("enchant");
                    }
                } else if (hasNecroStaff) {
                    int currentProtectionPoints = vanguardChampion.getProtectionPoints();
                    vanguardChampion.setProtectionPoints(currentProtectionPoints + 2);
                }
                this.SummonSap(caster, vanguardChampion);
                this.setTarget(caster, vanguardChampion);
                if (worldIn.addFreshEntity(vanguardChampion)) {
                    worldIn.sendParticles(ModParticleTypes.LICH.get(),
                            vanguardChampion.getX(), vanguardChampion.getY(), vanguardChampion.getZ(),
                            1, 0, 0, 0, 0.0F);
                    ServerParticleUtil.summonPowerfulUndeadParticles(worldIn, vanguardChampion);
                    this.playSound(worldIn, vanguardChampion, ModSounds.SOUL_EXPLODE.get(),
                            0.25F + (worldIn.random.nextFloat() / 2.0F), 1.0F);
                    this.playSound(worldIn, vanguardChampion, SoundEvents.ENDERMAN_TELEPORT,
                            0.25F + (worldIn.random.nextFloat() / 2.0F), 1.0F);
                }
                this.summonAdvancement(caster, vanguardChampion);
            }
            this.SummonDown(caster);
            this.playSound(worldIn, caster, ModSounds.VANGUARD_SUMMON.get());
        }
    }
}
