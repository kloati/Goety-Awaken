package com.k1sak1.goetyawaken.common.entities.ally.illager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.utils.BrewUtils;
import com.k1sak1.goetyawaken.Config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber
public class CroneTradeManager {
    private static List<CroneTrade> tradeList = new ArrayList<>();
    private static List<MobEffect> VANILLA_EFFECTS = new ArrayList<>();

    private static final String[] GOETY_BREW_EFFECT_SHORT_IDS = new String[] {
            "transpose", "launch", "blind_jump",
            "thorn_trap", "webbed", "purify_debuff",
            "bats", "bees", "shear",
            "strip", "fertility", "flaying",
            "raise_dead", "love",
            "saturation",
    };

    static {
        initializeTrades();
    }

    private static final Set<String> BLACKLISTED_EFFECT_IDS = Set.of(
            "minecraft:hero_of_the_village",
            "minecraft:bad_omen",
            "goety:buff",
            "goety:soul_armor",
            "goety:tremor_sense",
            "goety:doom",
            "goety:tangled",
            "goety:stunned",
            "goety:soul_hunger",
            "goety:busted",
            "goety:summon_down",
            "goety:illague",
            "goety:iron_hide",
            "goety:chill_hide",
            "goety:shadow_walk");

    private static void ensureMobEffectsInitialized() {
        if (VANILLA_EFFECTS.isEmpty()) {
            for (MobEffect effect : ForgeRegistries.MOB_EFFECTS) {
                ResourceLocation key = ForgeRegistries.MOB_EFFECTS.getKey(effect);
                if (key != null) {
                    String namespace = key.getNamespace();
                    String fullId = key.toString();
                    if (("minecraft".equals(namespace) || "goety".equals(namespace))
                            && !BLACKLISTED_EFFECT_IDS.contains(fullId)) {
                        VANILLA_EFFECTS.add(effect);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new TradeReloadListener());
    }

    public static class TradeReloadListener extends SimplePreparableReloadListener<List<CroneTrade>> {
        @Override
        protected List<CroneTrade> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
            List<CroneTrade> newTradeList = new ArrayList<>();
            ResourceLocation resourceId = new ResourceLocation("goetyawaken", "trades/crone_trades.json");
            if (resourceManager.getResource(resourceId).isPresent()) {
                try {
                    var resource = resourceManager.getResource(resourceId).get();
                    try (java.io.Reader reader = resource.openAsReader()) {
                        loadTradesFromReader(reader, newTradeList);
                    }
                } catch (Exception e) {
                }
            }
            return newTradeList;
        }

        @Override
        protected void apply(List<CroneTrade> newTradeList, ResourceManager resourceManager,
                ProfilerFiller profiler) {
            tradeList.clear();
            tradeList.addAll(newTradeList);
        }
    }

    private static void initializeTrades() {
        loadTradesFromConfig();
    }

    private static void loadTradesFromConfig() {
        try {
            java.nio.file.Path configPath = net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get()
                    .resolve("goetyawaken_crone_trades.json");
            if (java.nio.file.Files.exists(configPath)) {
                loadTradesFromFile(configPath.toFile());
            } else {
                java.io.InputStream inputStream = CroneTradeManager.class.getClassLoader()
                        .getResourceAsStream("data/goetyawaken/trades/crone_trades.json");
                if (inputStream != null) {
                    loadTradesFromInputStream(inputStream);
                }
            }
        } catch (Exception e) {
        }
    }

    private static void loadTradesFromFile(java.io.File configFile) {
        try (java.io.FileReader reader = new java.io.FileReader(configFile)) {
            loadTradesFromReader(reader);
        } catch (java.io.IOException e) {
        }
    }

    private static void loadTradesFromInputStream(java.io.InputStream inputStream) {
        try (java.io.InputStreamReader reader = new java.io.InputStreamReader(inputStream)) {
            loadTradesFromReader(reader);
        } catch (java.io.IOException e) {
        }
    }

    private static void loadTradesFromReader(java.io.Reader reader, List<CroneTrade> targetList) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
        JsonArray tradesArray = jsonObject.getAsJsonArray("trades");

        for (JsonElement element : tradesArray) {
            JsonObject tradeObject = element.getAsJsonObject();
            String itemId = tradeObject.get("item_id").getAsString();
            int minCount = tradeObject.get("min_count").getAsInt();
            int maxCount = tradeObject.get("max_count").getAsInt();
            int weight = tradeObject.get("weight").getAsInt();
            int price = tradeObject.get("price").getAsInt();

            String nbtString = null;
            if (tradeObject.has("nbt")) {
                nbtString = tradeObject.get("nbt").getAsString();
            }

            String potionId = null;
            if (tradeObject.has("potion")) {
                potionId = tradeObject.get("potion").getAsString();
            }

            List<CroneTrade.CustomEffect> customEffects = new ArrayList<>();
            if (tradeObject.has("custom_effects") && tradeObject.get("custom_effects").isJsonArray()) {
                JsonArray effectsArray = tradeObject.getAsJsonArray("custom_effects");
                for (JsonElement effElement : effectsArray) {
                    JsonObject eff = effElement.getAsJsonObject();
                    String effectId = eff.get("id").getAsString();
                    int amplifier = eff.has("amplifier") ? eff.get("amplifier").getAsInt() : 0;
                    int duration = eff.has("duration") ? eff.get("duration").getAsInt() : 3600;
                    boolean ambient = !eff.has("ambient") || eff.get("ambient").getAsBoolean();
                    boolean showParticles = !eff.has("show_particles") || eff.get("show_particles").getAsBoolean();
                    customEffects.add(new CroneTrade.CustomEffect(effectId, amplifier, duration, ambient,
                            showParticles));
                }
            }

            Integer customColor = null;
            if (tradeObject.has("color")) {
                customColor = tradeObject.get("color").getAsInt();
            }
            String secondItemId = null;
            int secondPrice = 0;
            if (tradeObject.has("second_item_id") && tradeObject.has("second_price")) {
                secondItemId = tradeObject.get("second_item_id").getAsString();
                secondPrice = tradeObject.get("second_price").getAsInt();
            }
            boolean isBrewTrade = tradeObject.has("is_brew") && tradeObject.get("is_brew").getAsBoolean();
            String brewTypeId = null;
            if (tradeObject.has("brew_type")) {
                brewTypeId = tradeObject.get("brew_type").getAsString();
            }

            List<CroneTrade.BrewCustomEffect> brewEffects = new ArrayList<>();
            if (tradeObject.has("brew_effects") && tradeObject.get("brew_effects").isJsonArray()) {
                JsonArray brewEffectsArray = tradeObject.getAsJsonArray("brew_effects");
                for (JsonElement effElement : brewEffectsArray) {
                    JsonObject eff = effElement.getAsJsonObject();
                    String effectId = eff.get("id").getAsString();
                    int amplifier = eff.has("amplifier") ? eff.get("amplifier").getAsInt() : 0;
                    int duration = eff.has("duration") ? eff.get("duration").getAsInt() : 3600;
                    boolean isBrewEffect = eff.has("is_brew_effect") && eff.get("is_brew_effect").getAsBoolean();
                    brewEffects.add(new CroneTrade.BrewCustomEffect(effectId, amplifier, duration, isBrewEffect));
                }
            }

            targetList.add(new CroneTrade(itemId, minCount, maxCount, weight, price, nbtString,
                    potionId, customEffects, customColor, secondItemId, secondPrice,
                    isBrewTrade, brewTypeId, brewEffects));
        }
    }

    private static void loadTradesFromReader(java.io.Reader reader) {
        loadTradesFromReader(reader, tradeList);
    }

    public static List<CroneTrade> getAllTrades() {
        return new ArrayList<>(tradeList);
    }

    public static MerchantOffers generateOffers(RandomSource random, int tradeCount) {
        MerchantOffers offers = new MerchantOffers();
        offers.add(createRandomBrewOffer(random));
        List<CroneTrade> availableTrades = getAllTrades();
        if (availableTrades.isEmpty()) {
            return offers;
        }
        Set<Integer> usedIndices = new HashSet<>();
        int remainingCount = tradeCount - 1;
        for (int i = 0; i < remainingCount; i++) {
            if (usedIndices.size() >= availableTrades.size()) {
                break;
            }
            int totalWeight = 0;
            for (int j = 0; j < availableTrades.size(); j++) {
                if (!usedIndices.contains(j)) {
                    totalWeight += availableTrades.get(j).getWeight();
                }
            }
            if (totalWeight <= 0) {
                break;
            }
            int randomValue = random.nextInt(totalWeight);
            int currentWeight = 0;
            int selectedIndex = -1;
            for (int j = 0; j < availableTrades.size(); j++) {
                if (!usedIndices.contains(j)) {
                    currentWeight += availableTrades.get(j).getWeight();
                    if (randomValue < currentWeight) {
                        selectedIndex = j;
                        break;
                    }
                }
            }
            if (selectedIndex >= 0 && !usedIndices.contains(selectedIndex)) {
                usedIndices.add(selectedIndex);
                CroneTrade selectedTrade = availableTrades.get(selectedIndex);
                offers.add(selectedTrade.createMerchantOffer(random));
            }
        }
        return offers;
    }

    private static MerchantOffer createRandomBrewOffer(RandomSource random) {
        ItemStack brewStack = generateRandomBrew(random);
        int basePrice = calculateBrewPrice(brewStack);
        ItemStack costStack = new ItemStack(Items.EMERALD, basePrice);
        return new MerchantOffer(costStack, brewStack, 1,
                Config.croneServantTradeMaxUses, 0.0F);
    }

    private static int calculateBrewPrice(ItemStack brewStack) {
        List<MobEffectInstance> effects = new ArrayList<>();
        List<BrewEffectInstance> brewEffects = new ArrayList<>();
        if (brewStack.hasTag()) {
            CompoundTag tag = brewStack.getTag();
            if (tag.contains("CustomPotionEffects", 9)) {
                ListTag effectList = tag.getList("CustomPotionEffects", 10);
                for (int i = 0; i < effectList.size(); i++) {
                    CompoundTag effectTag = effectList.getCompound(i);
                    try {
                        MobEffectInstance effect = MobEffectInstance.load(effectTag);
                        effects.add(effect);
                    } catch (Exception e) {
                        try {
                            BrewEffectInstance brewEffect = BrewEffectInstance.load(effectTag);
                            brewEffects.add(brewEffect);
                        } catch (Exception e2) {
                        }
                    }
                }
            }
        }

        int totalEffectCount = effects.size() + brewEffects.size();
        if (totalEffectCount == 0)
            return 8;

        int totalDurationSeconds = 0;
        int maxAmplifier = 0;

        for (MobEffectInstance eff : effects) {
            totalDurationSeconds += eff.getDuration() / 20;
            maxAmplifier = Math.max(maxAmplifier, eff.getAmplifier());
        }
        for (BrewEffectInstance eff : brewEffects) {
            totalDurationSeconds += eff.getDuration() / 20;
            maxAmplifier = Math.max(maxAmplifier, eff.getAmplifier());
        }

        int price = 8;
        price += totalEffectCount * 4;
        price += (totalDurationSeconds / 15) * 2;
        price += maxAmplifier * 6;

        return Math.min(64, Math.max(8, price));
    }

    private static ItemStack generateRandomBrew(RandomSource random) {
        Item brewItem = switch (random.nextInt(4)) {
            case 0 -> com.Polarice3.Goety.common.items.ModItems.BREW.get();
            case 1 -> com.Polarice3.Goety.common.items.ModItems.SPLASH_BREW.get();
            case 2 -> com.Polarice3.Goety.common.items.ModItems.LINGERING_BREW.get();
            case 3 -> com.Polarice3.Goety.common.items.ModItems.GAS_BREW.get();
            default -> com.Polarice3.Goety.common.items.ModItems.BREW.get();
        };

        ItemStack brewStack = new ItemStack(brewItem);

        int effectCount = 1 + random.nextInt(2);
        List<MobEffectInstance> mobEffects = new ArrayList<>();
        List<BrewEffectInstance> brewEffects = new ArrayList<>();

        boolean useBrewEffect = random.nextFloat() < 0.3F;

        for (int i = 0; i < effectCount; i++) {
            if (useBrewEffect && i == effectCount - 1 && GOETY_BREW_EFFECT_SHORT_IDS.length > 0) {
                String brewEffectId = GOETY_BREW_EFFECT_SHORT_IDS[random.nextInt(GOETY_BREW_EFFECT_SHORT_IDS.length)];
                try {
                    String fullBrewEffectId = "effect.goety." + brewEffectId;
                    BrewEffect brewEffect = com.Polarice3.Goety.common.effects.brew.BrewEffects.INSTANCE
                            .getBrewEffect(fullBrewEffectId);
                    if (brewEffect != null) {
                        brewEffects.add(new BrewEffectInstance(brewEffect));
                    }
                } catch (Exception e) {
                    addRandomMobEffect(random, mobEffects);
                }
            } else {
                addRandomMobEffect(random, mobEffects);
            }
        }

        if (!mobEffects.isEmpty() || !brewEffects.isEmpty()) {
            brewStack = BrewUtils.setCustomEffects(brewStack, mobEffects, brewEffects);
        }

        brewStack.getOrCreateTag().putInt("CustomPotionColor",
                BrewUtils.getColor(mobEffects, brewEffects));

        return brewStack;
    }

    private static void addRandomMobEffect(RandomSource random, List<MobEffectInstance> effects) {
        ensureMobEffectsInitialized();
        if (VANILLA_EFFECTS.isEmpty())
            return;
        MobEffect effect = VANILLA_EFFECTS.get(random.nextInt(VANILLA_EFFECTS.size()));
        int amplifier = random.nextInt(3);
        int maxUnits = 7 - amplifier * 2;
        int units = 1 + random.nextInt(maxUnits);
        int durationTicks = 1;
        if (effect.isInstantenous()) {
            durationTicks = 1;
        } else {
            durationTicks = units * 300;
        }
        effects.add(new MobEffectInstance(effect, durationTicks, amplifier));
    }
}
