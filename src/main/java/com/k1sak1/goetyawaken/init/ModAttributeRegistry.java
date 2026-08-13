package com.k1sak1.goetyawaken.init;

import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = GoetyAwaken.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModAttributeRegistry {
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES,
            GoetyAwaken.MODID);

    public static void init() {
        ATTRIBUTES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<Attribute> RITUAL_SPEED = ATTRIBUTES.register("ritual_speed",
            () -> (new RangedAttribute("attribute.name.goetyawaken.ritual_speed", 0.0D, 0.0D, 1024.0D)
                    .setSyncable(true)));

    public static final RegistryObject<Attribute> SUMMON_POTENCY = ATTRIBUTES.register("summon_potency",
            () -> (new RangedAttribute("attribute.name.goetyawaken.summon_potency", 0.0D, -255.0D, 255.0D)
                    .setSyncable(true)));

    public static final RegistryObject<Attribute> SUMMON_COOLDOWN_REDUCTION = ATTRIBUTES.register(
            "summon_cooldown_reduction",
            () -> (new RangedAttribute("attribute.name.goetyawaken.summon_cooldown_reduction", 1.0D, -100.0D, 100.0D)
                    .setSyncable(true)));

    public static final RegistryObject<Attribute> RITUAL_COST_REDUCTION = ATTRIBUTES.register("ritual_cost_reduction",
            () -> (new RangedAttribute("attribute.name.goetyawaken.ritual_cost_reduction", 1.0D, -100.0D, 100.0D)
                    .setSyncable(true)));

    public static final RegistryObject<Attribute> RITUAL_MATERIAL_RETURN = ATTRIBUTES.register("ritual_material_return",
            () -> (new RangedAttribute("attribute.name.goetyawaken.ritual_material_return", 1.0D, 0.0D, 100.0D)
                    .setSyncable(true)));

    public static final RegistryObject<Attribute> SERVANT_DAMAGE_REDUCTION = ATTRIBUTES.register(
            "servant_damage_reduction",
            () -> (new RangedAttribute("attribute.name.goetyawaken.servant_damage_reduction", 1.0D, -1024.0D,
                    1024.0D).setSyncable(true)));

    public static final RegistryObject<Attribute> SERVANT_HEALING = ATTRIBUTES.register("servant_healing",
            () -> (new RangedAttribute("attribute.name.goetyawaken.servant_healing", 0.0D, -1024.0D, 1024.0D)
                    .setSyncable(true)));

    public static final RegistryObject<Attribute> SERVANT_CAPACITY = ATTRIBUTES.register("servant_capacity",
            () -> (new RangedAttribute("attribute.name.goetyawaken.servant_capacity", 0.0D, -256.0D, 256.0D)
                    .setSyncable(true)));

    public static final RegistryObject<Attribute> SERVANT_LOOTING = ATTRIBUTES.register("servant_looting",
            () -> (new RangedAttribute("attribute.name.goetyawaken.servant_looting", 0.0D, -255.0D, 255.0D)
                    .setSyncable(true)));

    public static final RegistryObject<Attribute> SPELL_BOUNCE = ATTRIBUTES.register("spell_bounce",
            () -> (new RangedAttribute("attribute.name.goetyawaken.spell_bounce", 0.0D, -255.0D, 255.0D)
                    .setSyncable(true)));

    public static double getRitualSpeed(LivingEntity livingEntity) {
        return livingEntity.getAttributeValue(RITUAL_SPEED.get());
    }

    public static double getSummonCooldownReduction(LivingEntity livingEntity) {
        return livingEntity.getAttributeValue(SUMMON_COOLDOWN_REDUCTION.get());
    }

    public static double getSummonPotency(LivingEntity livingEntity) {
        return livingEntity.getAttributeValue(SUMMON_POTENCY.get());
    }

    public static double getRitualCostReduction(LivingEntity livingEntity) {
        return livingEntity.getAttributeValue(RITUAL_COST_REDUCTION.get());
    }

    public static double getRitualMaterialReturn(LivingEntity livingEntity) {
        return livingEntity.getAttributeValue(RITUAL_MATERIAL_RETURN.get());
    }

    public static double getServantDamageReduction(LivingEntity livingEntity) {
        return livingEntity.getAttributeValue(SERVANT_DAMAGE_REDUCTION.get());
    }

    public static double getServantHealing(LivingEntity livingEntity) {
        return livingEntity.getAttributeValue(SERVANT_HEALING.get());
    }

    public static double getServantCapacity(LivingEntity livingEntity) {
        return livingEntity.getAttributeValue(SERVANT_CAPACITY.get());
    }

    public static double getServantLooting(LivingEntity livingEntity) {
        return livingEntity.getAttributeValue(SERVANT_LOOTING.get());
    }

    public static int getSpellBounceLevel(LivingEntity livingEntity) {
        return (int) Math.round(livingEntity.getAttributeValue(SPELL_BOUNCE.get()));
    }

    public static int getServantCapacityLevel(LivingEntity livingEntity) {
        return (int) Math.round(getServantCapacity(livingEntity));
    }

    public static int getServantLootingLevel(LivingEntity livingEntity) {
        return (int) Math.round(getServantLooting(livingEntity));
    }

    public static double getServantDamageReductionMultiplier(LivingEntity livingEntity) {
        double value = getServantDamageReduction(livingEntity);
        double softCap;
        if (value <= 1.5D) {
            softCap = value;
        } else {
            softCap = 1.8D - 0.15D / (value - 1.0D);
        }
        return 2.0D - softCap;
    }

    public static double getRitualMaterialReturnChance(LivingEntity livingEntity) {
        return Math.max(0.0D, getRitualMaterialReturn(livingEntity) - 1.0D);
    }

    public static double getSummonCooldownMultiplier(LivingEntity livingEntity) {
        return 2.0D - getSummonCooldownReduction(livingEntity);
    }

    public static double getRitualCostMultiplier(LivingEntity livingEntity) {
        return 2.0D - getRitualCostReduction(livingEntity);
    }

    @SubscribeEvent
    public static void modifyEntityAttributes(EntityAttributeModificationEvent event) {
        event.getTypes()
                .forEach(entity -> ATTRIBUTES.getEntries().forEach(attribute -> event.add(entity, attribute.get())));
    }
}
