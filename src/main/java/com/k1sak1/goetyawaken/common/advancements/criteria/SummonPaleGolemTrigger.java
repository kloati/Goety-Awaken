package com.k1sak1.goetyawaken.common.advancements.criteria;

import com.google.gson.JsonObject;
import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class SummonPaleGolemTrigger extends SimpleCriterionTrigger<SummonPaleGolemTrigger.Instance> {
    private static final ResourceLocation ID = GoetyAwaken.location("summon_pale_golem");

    public ResourceLocation getId() {
        return ID;
    }

    public SummonPaleGolemTrigger.Instance createInstance(JsonObject p_230241_1_, ContextAwarePredicate p_230241_2_,
            DeserializationContext p_230241_3_) {
        return new SummonPaleGolemTrigger.Instance(p_230241_2_);
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, (instance) -> true);
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        public Instance(ContextAwarePredicate p_i231464_1_) {
            super(SummonPaleGolemTrigger.ID, p_i231464_1_);
        }
    }
}