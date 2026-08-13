package com.k1sak1.goetyawaken.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.entities.ally.Integration.FortunaDameBomb;
import lykrast.meetyourfight.registry.MYFItems;
import lykrast.meetyourfight.registry.MYFSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FateDiceFocusSpell extends Spell {

    @Override
    public int defaultSoulCost() {
        return Config.FATE_DICE_FOCUS_SOUL_COST.get();
    }

    @Override
    public int defaultCastDuration() {
        return Config.FATE_DICE_FOCUS_CAST_DURATION.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return Config.FATE_DICE_FOCUS_COOLDOWN.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NONE;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster);
        }

        double extraDamage = potency * Config.FATE_DICE_FOCUS_POTENCY_DAMAGE.get();
        int lineCount = 8 + (potency / 2);
        Vec3 lookAngle = caster.getLookAngle();

        boolean throwMultiple = false;
        if (caster instanceof net.minecraft.world.entity.player.Player
                && CuriosFinder.hasCurio((LivingEntity) caster, MYFItems.slicersDice.get())) {
            throwMultiple = shouldThrowMultiple(caster);
        }

        if (throwMultiple) {
            throwBomb(worldIn, caster, lookAngle, extraDamage, lineCount);
            Vec3 leftAngle = lookAngle.yRot((float) Math.toRadians(-30));
            throwBomb(worldIn, caster, leftAngle, extraDamage, lineCount);
            Vec3 rightAngle = lookAngle.yRot((float) Math.toRadians(30));
            throwBomb(worldIn, caster, rightAngle, extraDamage, lineCount);
        } else {
            throwBomb(worldIn, caster, lookAngle, extraDamage, lineCount);
        }
    }

    private boolean shouldThrowMultiple(LivingEntity caster) {
        int luck = (int) caster.getAttributeValue(Attributes.LUCK);
        double probability;
        if (luck >= 0) {
            probability = (1.0 + luck) / (5.0 + luck);
        } else {
            probability = 1.0 / (5.0 - 3.0 * luck);
        }
        return caster.getRandom().nextDouble() < probability;
    }

    private void throwBomb(ServerLevel worldIn, LivingEntity caster, Vec3 direction,
            double extraDamage, int lineCount) {

        double cx = caster.getX();
        double cy = caster.getY() + 2.0;
        double cz = caster.getZ();

        double tx = cx + direction.x * 10;
        double ty = cy + direction.y * 10;
        double tz = cz + direction.z * 10;

        FortunaDameBomb bomb = new FortunaDameBomb(worldIn, cx, cy, cz, caster);
        bomb.setOwner(caster);
        bomb.setExtraDamage((float) extraDamage);
        bomb.setLineCount(lineCount);

        int fuse = 25;
        bomb.setup(fuse, fuse - 10, tx, ty, tz);

        worldIn.addFreshEntity(bomb);
        worldIn.playSound(null, cx, cy, cz,
                MYFSounds.dameFortunaShoot.get(), caster.getSoundSource(), 2.0F,
                (worldIn.random.nextFloat() - worldIn.random.nextFloat()) * 0.2F + 1.0F);

    }
}
