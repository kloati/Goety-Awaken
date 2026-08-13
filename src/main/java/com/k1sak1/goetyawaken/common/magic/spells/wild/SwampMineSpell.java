package com.k1sak1.goetyawaken.common.magic.spells.wild;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.entities.ModEntityType;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.SwampMine;
import lykrast.meetyourfight.registry.MYFSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SwampMineSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return Config.SWAMP_MINE_FOCUS_SOUL_COST.get();
    }

    @Override
    public int defaultCastDuration() {
        return Config.SWAMP_MINE_FOCUS_CAST_DURATION.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return MYFSounds.swampjawIdle.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.SWAMP_MINE_FOCUS_COOLDOWN.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WILD;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        int range = spellStat.getRange();
        double radius = spellStat.getRadius();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster);
            range += WandUtil.getRangeLevel(caster);
        }

        float extraExplosionPower = (float) (potency * Config.SWAMP_MINE_POTENCY_EXPLOSION.get());
        int mineCount = (this.rightStaff(staff) || staff.is(ModItems.GEO_STAFF.get())) ? 3 : 1;

        Vec3 targetPos;

        if (this.rightStaff(staff)) {
            LivingEntity target = this.getTarget(caster, range);
            if (target == null) {
                return;
            }
            targetPos = target.position();
        } else {
            HitResult hitResult = this.rayTrace(worldIn, caster, range, radius);
            if (hitResult instanceof EntityHitResult entityHitResult) {
                targetPos = entityHitResult.getEntity().position();
            } else if (hitResult instanceof BlockHitResult blockHitResult) {
                targetPos = Vec3.atCenterOf(blockHitResult.getBlockPos().above());
            } else {
                return;
            }
        }

        BlockPos spawnPos = BlockPos.containing(targetPos.x, targetPos.y + 8.0D, targetPos.z);

        for (int i = 0; i < mineCount; ++i) {
            SwampMine swampMine = new SwampMine(worldIn,
                    spawnPos.getX() + (i == 0 ? 0 : worldIn.random.nextDouble() * 6 - 3),
                    spawnPos.getY(),
                    spawnPos.getZ() + (i == 0 ? 0 : worldIn.random.nextDouble() * 6 - 3),
                    caster);
            swampMine.setOwner(caster);
            swampMine.setExtraExplosionPower(extraExplosionPower);
            worldIn.addFreshEntity(swampMine);
        }

        worldIn.playSound(null, targetPos.x, targetPos.y, targetPos.z,
                MYFSounds.swampjawBomb.get(), caster.getSoundSource(), 10.0F,
                0.95F + worldIn.random.nextFloat() * 0.1F);
    }
}
