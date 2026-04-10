package com.k1sak1.goetyawaken.common.advancements;

import com.k1sak1.goetyawaken.common.advancements.criteria.*;
import net.minecraft.advancements.CriteriaTriggers;

public class ModCriteriaTriggers {
    public static final SummonPaleGolemTrigger SUMMON_PALE_GOLEM = new SummonPaleGolemTrigger();
    public static final SummonWitherServantTrigger SUMMON_WITHER_SERVANT = new SummonWitherServantTrigger();
    public static final GetShadowShriekerTrigger GET_SHADOW_SHRIEKER = new GetShadowShriekerTrigger();
    public static final UpgradeWizardTrigger UPGRADE_WIZARD = new UpgradeWizardTrigger();
    public static final HighAndDryTrigger HIGH_AND_DRY = new HighAndDryTrigger();
    public static final CreeperFocusTrigger CREEPER_FOCUS = new CreeperFocusTrigger();
    public static final ClashRoyaleTrigger CLASH_ROYALE = new ClashRoyaleTrigger();
    public static final TheEndEnvoyTrigger THE_END_ENVOY = new TheEndEnvoyTrigger();
    public static final BedLiceTrigger BED_LICE = new BedLiceTrigger();
    public static final TheSwarmTrigger THE_SWARM = new TheSwarmTrigger();
    public static final RoyalResearchCompletedTrigger ROYAL_RESEARCH_COMPLETED = new RoyalResearchCompletedTrigger();
    public static final MushroomMonstrosityTrigger MUSHROOM_MONSTROSITY = new MushroomMonstrosityTrigger();
    public static final ChaosPrisonTrigger CHAOS_PRISON = new ChaosPrisonTrigger();
    public static final MushroomMonstrosityKillTrigger MUSHROOM_MONSTROSITY_KILL = new MushroomMonstrosityKillTrigger();
    public static final SporearmRaceTrigger SPOREARM_RACE = new SporearmRaceTrigger();

    public static void init() {
        CriteriaTriggers.register(SUMMON_PALE_GOLEM);
        CriteriaTriggers.register(SUMMON_WITHER_SERVANT);
        CriteriaTriggers.register(GET_SHADOW_SHRIEKER);
        CriteriaTriggers.register(UPGRADE_WIZARD);
        CriteriaTriggers.register(HIGH_AND_DRY);
        CriteriaTriggers.register(CREEPER_FOCUS);
        CriteriaTriggers.register(CLASH_ROYALE);
        CriteriaTriggers.register(THE_END_ENVOY);
        CriteriaTriggers.register(BED_LICE);
        CriteriaTriggers.register(THE_SWARM);
        CriteriaTriggers.register(ROYAL_RESEARCH_COMPLETED);
        CriteriaTriggers.register(MUSHROOM_MONSTROSITY);
        CriteriaTriggers.register(CHAOS_PRISON);
        CriteriaTriggers.register(MUSHROOM_MONSTROSITY_KILL);
        CriteriaTriggers.register(SPOREARM_RACE);
    }
}