package com.k1sak1.goetyawaken.common.entities.ai;

import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.k1sak1.goetyawaken.common.magic.sorcerer.SorcererSpellCaster;
import com.k1sak1.goetyawaken.common.magic.sorcerer.SorcererSpellData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class SorcererCastingGoal extends Goal {

    private final Mob mob;
    private final SorcererSpellCaster caster;

    public SorcererCastingGoal(Mob mob, SorcererSpellCaster caster) {
        this.mob = mob;
        this.caster = caster;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private SorcererSpellData data() {
        return caster.getSpellData();
    }

    @Override
    public boolean canUse() {
        SorcererSpellData d = data();
        return d != null && d.castingTime > 0;
    }

    @Override
    public void start() {
        super.start();
        mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        super.stop();
        SorcererSpellData d = data();
        if (d != null && d.currentSpell != null) {
            Spell sp = d.currentSpell.getSpell();
            sp.stopSpell((ServerLevel) mob.level(), mob,
                    d.currentSpell.resolveUpgradeStaff(caster.getSorcererLevel()),
                    d.currentSpell.getFocusStack(), d.castTimeCounter,
                    WandUtil.getStats(mob, sp));
        }
        caster.setIsCastingSpell(0);
        caster.setCurrentSpellName("");
        mob.level().broadcastEntityEvent(mob, (byte) 5);
        mob.level().broadcastEntityEvent(mob, (byte) 7);
        if (data() != null) { data().coolDown = 20; data().virtualWand = ItemStack.EMPTY; }
    }

    @Override
    public void tick() {
        if (mob.getTarget() != null) {
            MobUtil.instaLook(mob, mob.getTarget());
        }
        mob.getNavigation().stop();
        mob.getMoveControl().strafe(0.0F, 0.0F);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
