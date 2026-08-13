package com.k1sak1.goetyawaken.common.items.magic.grimoire;

import com.k1sak1.goetyawaken.Config;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class AffixRoller {

    private static final int TIER_COUNT = 5;
    private static final int BAND_COUNT = 5;
    private static final int[] BAND_THRESHOLDS = { 24, 48, 72, 96 };

    public static GrimoireAffix rollAffix(int totalValue, long seed, double positionBonus, boolean forcePositive) {
        return rollAffix(totalValue, seed, positionBonus, forcePositive, 0.0D, -1.0D);
    }

    public static GrimoireAffix rollAffix(int totalValue, long seed, double positionBonus, boolean forcePositive,
            double extraNegativeChance, double positiveValueFactor) {
        AffixPool.AffixDefinition definition = AffixPool.getRandomDefinition(RandomSource.create(seed));
        if (definition == null) {
            return null;
        }
        RandomSource random = RandomSource.create(seed);
        int tier = bandFor(totalValue);
        double[] range = definition.tierRanges[tier];
        double min = range[0];
        double max = range[1];
        double position = valuePosition(totalValue, positionBonus);
        double value;
        if (min >= 0) {
            value = min + (max - min) * position;
            if (positiveValueFactor >= 0.0D) {
                value *= positiveValueFactor;
            }
        } else if (max <= 0 && !forcePositive) {
            value = min * (1.0D - position);
        } else {
            boolean allowNegative = Config.GRIMOIRE_AFFIX_ALLOW_NEGATIVE.get() && !forcePositive;
            double negativeChance = allowNegative
                    ? Mth.clamp(negativeChance(totalValue, positionBonus) + extraNegativeChance, 0.0D, 0.8D)
                    : 0.0D;
            if (random.nextDouble() < negativeChance) {
                value = min * (1.0D - position);
            } else {
                value = max * position;
                if (positiveValueFactor >= 0.0D) {
                    value *= positiveValueFactor;
                }
            }
        }
        return new GrimoireAffix(definition.attribute, definition.op, value, tier + 1);
    }

    private static int bandFor(int totalValue) {
        int band = 0;
        for (int i = 0; i < BAND_COUNT - 1; i++) {
            if (totalValue >= BAND_THRESHOLDS[i]) {
                band = i + 1;
            }
        }
        return band;
    }

    private static double negativeChance(int totalValue, double positionBonus) {
        return Mth.clamp((0.5D - totalValue * 0.014D) * (1.0D - positionBonus), 0.0D, 0.5D);
    }

    private static double valuePosition(int totalValue, double positionBonus) {
        return Mth.clamp(0.35D + totalValue * 0.02D + positionBonus, 0.35D, 0.95D);
    }
}
