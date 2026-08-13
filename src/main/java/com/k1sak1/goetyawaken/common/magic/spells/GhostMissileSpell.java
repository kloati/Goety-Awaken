package com.k1sak1.goetyawaken.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.ModProjectileLineEntity;
import lykrast.meetyourfight.entity.ProjectileLineEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GhostMissileSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return Config.GHOST_MISSILE_FOCUS_SOUL_COST.get();
    }

    @Override
    public int defaultCastDuration() {
        return Config.GHOST_MISSILE_FOCUS_CAST_DURATION.get();
    }

    @Nullable
    @Override
    public SoundEvent CastingSound() {
        return SoundEvents.BELL_RESONATE;
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.GHOST_MISSILE_FOCUS_COOLDOWN.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NONE;
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

        double extraDamage = potency * Config.GHOST_MISSILE_POTENCY_DAMAGE.get();

        double tx, ty, tz;

        HitResult hitResult = this.rayTrace(worldIn, caster, range, radius);
        if (hitResult instanceof EntityHitResult entityHitResult) {
            Entity targetEntity = entityHitResult.getEntity();
            tx = targetEntity.getX();
            tz = targetEntity.getZ();
            ty = targetEntity.getY() + 0.1;
            if (targetEntity instanceof LivingEntity livingTarget) {
                if (!livingTarget.onGround() && !livingTarget.isInWater()) {
                    Vec3 from = new Vec3(tx, ty, tz);
                    BlockHitResult res = worldIn.clip(new ClipContext(from, from.add(0, -1, 0),
                            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, livingTarget));
                    if (res.getType() != HitResult.Type.MISS)
                        ty = res.getLocation().y;
                    else
                        ty -= 1;
                }
            }
        } else if (hitResult instanceof BlockHitResult blockHitResult) {
            BlockPos blockPos = blockHitResult.getBlockPos().above();
            tx = blockPos.getX() + 0.5;
            tz = blockPos.getZ() + 0.5;
            ty = blockPos.getY();
        } else {
            return;
        }

        int chosenAttack;
        if (caster instanceof Player player) {
            chosenAttack = player.isShiftKeyDown() ? 1 : 0;
        } else {
            chosenAttack = worldIn.random.nextInt(2);
        }

        switch (chosenAttack) {
            default:
            case 0:
                BlockPos self = caster.blockPosition();
                double sx = self.getX();
                double sz = self.getZ();
                Direction dir = Direction.getNearest(tx - sx, 0, tz - sz);
                double cx = dir.getStepX();
                double cz = dir.getStepZ();

                for (int i = -4; i <= 4; i++) {
                    ModProjectileLineEntity ghost = this.createProjectile(worldIn, caster, extraDamage);
                    ghost.setUp(20, cx, 0, cz, tx - 7 * cx + i * cz, ty, tz - 7 * cz + i * cx);
                    worldIn.addFreshEntity(ghost);
                }
                break;
            case 1:
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        ModProjectileLineEntity ghost = this.createProjectile(worldIn, caster, extraDamage);
                        ghost.setUp(20, 0, -1, 0, tx + x, ty + 7, tz + z);
                        worldIn.addFreshEntity(ghost);
                    }
                }
                break;
        }

        worldIn.playSound(null, tx, ty, tz,
                SoundEvents.BELL_BLOCK, caster.getSoundSource(), 2.0F, 1.0F);
    }

    private ModProjectileLineEntity createProjectile(ServerLevel worldIn, LivingEntity caster, double extraDamage) {
        ModProjectileLineEntity ghost = new ModProjectileLineEntity(worldIn, caster);
        ghost.setOwner(caster);
        ghost.setPos(caster.getX() - 2 + worldIn.random.nextDouble() * 4,
                caster.getY() - 2 + worldIn.random.nextDouble() * 4,
                caster.getZ() - 2 + worldIn.random.nextDouble() * 4);
        ghost.setVariant(ProjectileLineEntity.VAR_BELLRINGER);
        ghost.setExtraDamage((float) extraDamage);
        return ghost;
    }
}
