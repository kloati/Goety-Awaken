package com.k1sak1.goetyawaken.common.magic.spells.wind;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.ChargingSpell;
import com.k1sak1.goetyawaken.Config;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.common.entities.projectiles.PureLightEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.AABB;
import java.util.ArrayList;
import java.util.List;

public class HeavenRiftSpell extends ChargingSpell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRadius(32.0D);
    }

    public int defaultSoulCost() {
        return Config.HEAVEN_RIFT_FOCUS_SOUL_COST.get();
    }

    @Override
    public int defaultCastUp() {
        return Config.HEAVEN_RIFT_FOCUS_CAST_DURATION.get();
    }

    @Override
    public int Cooldown() {
        return Config.HEAVEN_RIFT_FOCUS_COOLDOWN.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.HEAVEN_RIFT_FOCUS_COOLDOWN.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WIND;
    }

    @Override
    public int Cooldown(LivingEntity caster, ItemStack staff, int shots) {
        return 2 + caster.getRandom().nextInt(14);
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get());
        list.add(com.Polarice3.Goety.common.enchantments.ModEnchantments.RADIUS.get());
        return list;
    }

    public List<LivingEntity> getLivingEntities(ServerLevel worldIn, LivingEntity caster, SpellStat spellStat) {
        int i = (int) caster.getX();
        int j = (int) caster.getY();
        int k = (int) caster.getZ();
        double radius = spellStat.getRadius();
        if (WandUtil.enchantedFocus(caster)) {
            radius += WandUtil.getLevels(com.Polarice3.Goety.common.enchantments.ModEnchantments.RADIUS.get(), caster)
                    * 2.0D;
        }
        return worldIn.getEntitiesOfClass(LivingEntity.class, (new AABB(i, j, k, i, j - 4, k)).inflate(radius));
    }

    @Override
    public int shotsNumber(LivingEntity caster, ItemStack staff) {
        return 50;
    }

    private void spawnPureLight(ServerLevel world, BlockPos pos, int extraDamage, LivingEntity caster) {
        PureLightEntity pureLight = new PureLightEntity(world);
        pureLight.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        pureLight.setExtraDamage((float) (extraDamage) * WandUtil.damageMultiply());
        if (caster != null) {
            pureLight.setOwner(caster);
        }
        world.addFreshEntity(pureLight);
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        RandomSource random = caster.getRandom();
        int extraDamage = 0;
        double radius = spellStat.getRadius();
        if (WandUtil.enchantedFocus(caster)) {
            extraDamage += (int) (WandUtil.getLevels(
                    com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get(),
                    caster) * Config.heavenRiftPotencyDamage);
            radius += WandUtil.getLevels(com.Polarice3.Goety.common.enchantments.ModEnchantments.RADIUS.get(), caster)
                    * 4.0D;
        }

        if (this.rightStaff(staff)) {
            extraDamage += 4;
            radius += 8;
        }

        for (LivingEntity entity : this.getLivingEntities(worldIn, caster, spellStat)) {
            if (entity != caster && !MobUtil.areAllies(entity, caster)) {
                int chance = 20;
                if (this.rightStaff(staff)) {
                    chance = 10;
                }
                if (random.nextInt(chance) == 0) {
                    BlockPos entityPos = entity.blockPosition();
                    this.spawnPureLight(worldIn, entityPos, extraDamage, caster);
                }
            }
        }

        int randomCount = 1
                + WandUtil.getLevels(com.Polarice3.Goety.common.enchantments.ModEnchantments.POTENCY.get(), caster)
                + random.nextInt(14);
        for (int i = 0; i < randomCount; i++) {
            BlockPos centerPos = caster.blockPosition();
            int offsetX = random.nextInt((int) radius * 2 + 1) - (int) radius;
            int offsetZ = random.nextInt((int) radius * 2 + 1) - (int) radius;
            BlockPos targetPos = centerPos.offset(offsetX, 0, offsetZ);
            PureLightEntity tempLight = new PureLightEntity(worldIn);
            tempLight.setPos(targetPos.getX() + 0.5, targetPos.getY() + 10, targetPos.getZ() + 0.5);
            MobUtil.moveDownToGround(tempLight);
            BlockPos groundPos = tempLight.blockPosition();
            if (worldIn.getBlockState(groundPos).isAir() && worldIn.getBlockState(groundPos.above()).isAir()) {
                this.spawnPureLight(worldIn, groundPos, extraDamage, caster);
            }
        }
    }
}