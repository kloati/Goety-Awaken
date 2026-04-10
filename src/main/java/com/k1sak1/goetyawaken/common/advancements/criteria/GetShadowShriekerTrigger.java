package com.k1sak1.goetyawaken.common.advancements.criteria;

import com.google.gson.JsonObject;
import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class GetShadowShriekerTrigger extends SimpleCriterionTrigger<GetShadowShriekerTrigger.Instance> {
    private static final ResourceLocation ID = GoetyAwaken.location("get_shadow_shrieker");

    public ResourceLocation getId() {
        return ID;
    }

    public GetShadowShriekerTrigger.Instance createInstance(JsonObject jsonObject,
            ContextAwarePredicate contextAwarePredicate,
            DeserializationContext deserializationContext) {
        return new GetShadowShriekerTrigger.Instance(contextAwarePredicate);
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, (instance) -> true);
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        public Instance(ContextAwarePredicate contextAwarePredicate) {
            super(GetShadowShriekerTrigger.ID, contextAwarePredicate);
        }
    }
}