package com.k1sak1.goetyawaken.common.advancements.criteria;

import com.google.gson.JsonObject;
import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class HighAndDryTrigger extends SimpleCriterionTrigger<HighAndDryTrigger.Instance> {
    private static final ResourceLocation ID = GoetyAwaken.location("high_and_dry");

    public ResourceLocation getId() {
        return ID;
    }

    public HighAndDryTrigger.Instance createInstance(JsonObject jsonObject,
            ContextAwarePredicate contextAwarePredicate,
            DeserializationContext deserializationContext) {
        return new HighAndDryTrigger.Instance(contextAwarePredicate);
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, (instance) -> true);
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        public Instance(ContextAwarePredicate contextAwarePredicate) {
            super(HighAndDryTrigger.ID, contextAwarePredicate);
        }
    }
}