package com.k1sak1.goetyawaken.init;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.effects.*;
import com.k1sak1.goetyawaken.common.effects.MobResurrectionAuraEffect;
import com.k1sak1.goetyawaken.common.effects.RecoverEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
        public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS,
                        GoetyAwaken.MODID);

        public static void init() {
                EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<MobEffect> ENCHANTMENT_SHARPNESS = EFFECTS.register("enchantment_sharpness",
                        () -> new EnchantmentSharpnessEffect(MobEffectCategory.BENEFICIAL, 0xFF6347));

        public static final RegistryObject<MobEffect> ENCHANTMENT_THORNS = EFFECTS.register("enchantment_thorns",
                        () -> new EnchantmentThornsEffect(MobEffectCategory.BENEFICIAL, 0x8FBC8F));

        public static final RegistryObject<MobEffect> CRITICAL_HIT = EFFECTS.register("critical_hit",
                        () -> new CriticalHitEffect(MobEffectCategory.BENEFICIAL, 0xFFD700));

        public static final RegistryObject<MobEffect> FRENZIED = EFFECTS.register("frenzied",
                        () -> new FrenziedEffect(MobEffectCategory.BENEFICIAL, 0xFF4500));

        public static final RegistryObject<MobEffect> WEAKENING_HANDS = EFFECTS.register("weakening_hands",
                        () -> new WeakeningHandsEffect(MobEffectCategory.NEUTRAL, 0x808080));

        public static final RegistryObject<MobEffect> CHAINS = EFFECTS.register("chains",
                        () -> new ChainsEffect(MobEffectCategory.NEUTRAL, 0xC0C0C0));

        public static final RegistryObject<MobEffect> SHOCKWAVE = EFFECTS.register("shockwave",
                        () -> new ShockwaveEffect(MobEffectCategory.BENEFICIAL, 0x1E90FF));

        public static final RegistryObject<MobEffect> ENCHANTMENT_THUNDERING = EFFECTS.register(
                        "enchantment_thundering",
                        () -> new EnchantmentThunderingEffect(MobEffectCategory.BENEFICIAL, 0xFFD700));

        public static final RegistryObject<MobEffect> COMMITTED = EFFECTS.register("committed",
                        () -> new CommittedEffect(MobEffectCategory.BENEFICIAL, 0xDC143C));

        public static final RegistryObject<MobEffect> RAMPAGING = EFFECTS.register("rampaging",
                        () -> new RampagingEffect(MobEffectCategory.BENEFICIAL, 0xFF0000));

        public static final RegistryObject<MobEffect> ECHO = EFFECTS.register("echo",
                        () -> new EchoEffect(MobEffectCategory.BENEFICIAL, 0x9f0be3));

        public static final RegistryObject<MobEffect> BERSERK = EFFECTS.register("berserk",
                        () -> new BerserkEffect(MobEffectCategory.BENEFICIAL, 0xFF0000));

        public static final RegistryObject<MobEffect> VISUAL_DISTURBANCE = EFFECTS.register("visual_disturbance",
                        () -> new VisualDisturbanceEffect(MobEffectCategory.HARMFUL, 0x8A2BE2));

        public static final RegistryObject<MobEffect> MUCILAGE_POSSESSION = EFFECTS.register("mucilage_possession",
                        () -> new MucilagePossessionEffect(MobEffectCategory.HARMFUL, 0x4B0082));

        public static final RegistryObject<MobEffect> MOB_RESURRECTION_AURA = EFFECTS.register("mob_resurrection_aura",
                        () -> new MobResurrectionAuraEffect(MobEffectCategory.BENEFICIAL, 0xFFFFFF));

        public static final RegistryObject<MobEffect> MULTI_SHOT = EFFECTS.register("multi_shot",
                        () -> new MultiShotEffect(MobEffectCategory.BENEFICIAL, 0x4169E1));

        public static final RegistryObject<MobEffect> RECOVER = EFFECTS.register("recover",
                        () -> new RecoverEffect(MobEffectCategory.BENEFICIAL, 0x00FF00));
}