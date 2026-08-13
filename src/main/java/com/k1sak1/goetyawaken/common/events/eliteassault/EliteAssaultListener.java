package com.k1sak1.goetyawaken.common.events.eliteassault;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class EliteAssaultListener extends SimpleJsonResourceReloadListener {
    public static Map<ResourceLocation, EliteSpawnData> ELITE_OTHER_MOBS = new HashMap<>();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).create();
    private final ICondition.IContext context;

    public EliteAssaultListener(ICondition.IContext context) {
        super(GSON, "elite_assault");
        this.context = context;
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new EliteAssaultListener(event.getConditionContext()));
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManagerIn,
            ProfilerFiller profilerIn) {
        ELITE_OTHER_MOBS.clear();
        for (int i = 0; i < objectIn.size(); i++) {
            ResourceLocation location = (ResourceLocation) objectIn.keySet().toArray()[i];
            JsonObject object = objectIn.get(location).getAsJsonObject();
            if (!CraftingHelper.processConditions(object, "conditions", this.context)) {
                LOGGER.debug("Skipping loading elite assault entry {} as it's conditions were not met", location);
            } else {
                String name = object.getAsJsonPrimitive("entity_type").getAsString();
                ResourceLocation entityType = new ResourceLocation(name);
                JsonObject data = object.getAsJsonObject("registry");
                float thresholdTimes = data.getAsJsonPrimitive("threshold_times").getAsFloat();
                int max = data.getAsJsonPrimitive("max").getAsInt();
                int extra = data.getAsJsonPrimitive("extra").getAsInt();
                float chance = data.getAsJsonPrimitive("chance").getAsFloat();
                JsonObject ridingObj = data.getAsJsonObject("riding");
                ResourceLocation riding = null;
                float rideChance = 0.0F;
                if (ridingObj != null) {
                    riding = new ResourceLocation(ridingObj.getAsJsonPrimitive("mount_type").getAsString());
                    if (!ForgeRegistries.ENTITY_TYPES.containsKey(riding)) {
                        riding = null;
                    }
                    rideChance = ridingObj.getAsJsonPrimitive("ride_chance").getAsFloat();
                }
                ELITE_OTHER_MOBS.put(entityType,
                        new EliteSpawnData(thresholdTimes, max, extra, chance, riding, rideChance));
            }
        }
    }

    public static class EliteSpawnData {
        public final float thresholdTimes;
        public final int maxExtraAmount;
        public final int initExtraAmount;
        public final float chance;
        public final ResourceLocation riding;
        public final float rideChance;

        public EliteSpawnData(float thresholdTimes, int maxExtraAmount, int initExtraAmount, float chance,
                ResourceLocation riding, float rideChance) {
            this.thresholdTimes = thresholdTimes;
            this.maxExtraAmount = maxExtraAmount;
            this.initExtraAmount = initExtraAmount;
            this.chance = chance;
            this.riding = riding;
            this.rideChance = rideChance;
        }
    }
}