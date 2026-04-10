package com.k1sak1.goetyawaken.common.advancements.criteria;

import com.google.gson.JsonObject;
import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class MushroomMonstrosityTrigger extends SimpleCriterionTrigger<MushroomMonstrosityTrigger.Instance> {
    private static final ResourceLocation ID = GoetyAwaken.location("mushroom_monstrosity");

    public ResourceLocation getId() {
        return ID;
    }

    public MushroomMonstrosityTrigger.Instance createInstance(JsonObject p_230241_1_,
            ContextAwarePredicate p_230241_2_,
            DeserializationContext p_230241_3_) {
        return new MushroomMonstrosityTrigger.Instance(p_230241_2_);
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, (instance) -> true);
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        public Instance(ContextAwarePredicate p_i231464_1_) {
            super(MushroomMonstrosityTrigger.ID, p_i231464_1_);
        }
    }
}