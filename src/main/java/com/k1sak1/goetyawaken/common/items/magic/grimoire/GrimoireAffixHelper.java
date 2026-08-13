package com.k1sak1.goetyawaken.common.items.magic.grimoire;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.k1sak1.goetyawaken.Config;
import com.k1sak1.goetyawaken.common.items.magic.GrimoireItem;
import com.k1sak1.goetyawaken.init.ModAttributeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Collection;
import java.util.UUID;

public class GrimoireAffixHelper {

    public static boolean isEnabled() {
        return Config.GRIMOIRE_AFFIX_ENABLED.get();
    }

    public static int maxAffixes(ItemStack stack) {
        if (stack.getItem() instanceof GrimoireItem grimoire) {
            return grimoire.getLevel();
        }
        return 0;
    }

    public static boolean isFull(ItemStack stack) {
        if (!isEnabled()) {
            return true;
        }
        return GrimoireAffix.getAffixes(stack).size() >= maxAffixes(stack);
    }

    public static boolean rollAndAppend(ItemStack grimoire, Collection<ItemStack> materials, long seed) {
        return rollAndAppend(grimoire, materials, seed, 0.0D, false);
    }

    public static boolean rollAndAppend(ItemStack grimoire, Collection<ItemStack> materials, long seed,
            double positionBonus) {
        return rollAndAppend(grimoire, materials, seed, positionBonus, false);
    }

    public static boolean rollAndAppend(ItemStack grimoire, Collection<ItemStack> materials, long seed,
            double positionBonus, boolean forcePositive) {
        if (!isEnabled() || grimoire.isEmpty() || isFull(grimoire)) {
            return false;
        }
        int totalValue = GrimoireValueRegistry.sumValues(materials);
        GrimoireAffix affix = AffixRoller.rollAffix(totalValue, seed, positionBonus, forcePositive);
        if (affix == null) {
            return false;
        }
        return GrimoireAffix.appendAffix(grimoire, affix, maxAffixes(grimoire));
    }

    public static boolean rollAndAppendCrafting(ItemStack grimoire, Collection<ItemStack> materials, long seed) {
        if (!isEnabled() || grimoire.isEmpty() || isFull(grimoire)) {
            return false;
        }
        int totalValue = GrimoireValueRegistry.sumValues(materials);
        GrimoireAffix affix = AffixRoller.rollAffix(totalValue, seed, 0.0D, false, 0.35D, 0.5D);
        if (affix == null) {
            return false;
        }
        return GrimoireAffix.appendAffix(grimoire, affix, maxAffixes(grimoire));
    }

    public static boolean isAffixRitualRecipe(com.Polarice3.Goety.common.crafting.RitualRecipe recipe) {
        return recipe != null && recipe.getId().getPath().startsWith("grimoire_affix_ritual");
    }

    public static long hashInput(ItemStack grimoire, Collection<ItemStack> materials) {
        StringBuilder builder = new StringBuilder();
        builder.append(grimoire.getItem().getDescriptionId());
        builder.append(grimoire.getTag());
        for (ItemStack material : materials) {
            builder.append('|').append(material.getItem().getDescriptionId()).append(material.getTag());
        }
        return UUID.nameUUIDFromBytes(builder.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8))
                .getMostSignificantBits();
    }

    public static Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack, UUID slotUuid) {
        LinkedHashMultimap<Attribute, AttributeModifier> map = LinkedHashMultimap.create();
        if (!isEnabled() || stack.isEmpty()) {
            return map;
        }
        int index = 0;
        for (GrimoireAffix affix : GrimoireAffix.getAffixes(stack)) {
            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(affix.attribute);
            if (attribute == null) {
                for (Attribute candidate : ForgeRegistries.ATTRIBUTES) {
                    ResourceLocation key = ForgeRegistries.ATTRIBUTES.getKey(candidate);
                    if (key != null && key.getPath().equals(affix.attribute.getPath())) {
                        attribute = candidate;
                        break;
                    }
                }
            }
            if (attribute != null) {
                UUID uuid = UUID.nameUUIDFromBytes(("goetyawaken_affix_" + slotUuid + "_" + affix.attribute + "_"
                        + affix.op.ordinal() + "_" + index).getBytes());
                map.put(attribute, new AttributeModifier(uuid, "goetyawaken_affix", affix.value, affix.op));
            }
            index++;
        }
        if (stack.getItem() instanceof GrimoireItem grimoire) {
            double inherent = grimoire.getLevel() / 2.0D;
            Attribute ritualSpeed = ModAttributeRegistry.RITUAL_SPEED.get();
            UUID uuid = UUID.nameUUIDFromBytes(("goetyawaken_inherent_ritual_speed_" + slotUuid).getBytes());
            map.put(ritualSpeed, new AttributeModifier(uuid, "goetyawaken_inherent_ritual_speed", inherent,
                    AttributeModifier.Operation.ADDITION));
            double costReduction = grimoire.getLevel() * 0.04D;
            Attribute ritualCostReduction = ModAttributeRegistry.RITUAL_COST_REDUCTION.get();
            UUID uuid2 = UUID.nameUUIDFromBytes(("goetyawaken_inherent_ritual_cost_reduction_" + slotUuid).getBytes());
            map.put(ritualCostReduction, new AttributeModifier(uuid2, "goetyawaken_inherent_ritual_cost_reduction",
                    costReduction, AttributeModifier.Operation.MULTIPLY_BASE));
        }
        return map;
    }

    public static boolean isFirstOfType(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity == null) {
            return true;
        }
        var inventoryOpt = CuriosApi.getCuriosInventory(entity).resolve();
        if (inventoryOpt.isEmpty()) {
            return true;
        }
        Item targetItem = stack.getItem();
        String currentId = slotContext.identifier();
        int currentIndex = slotContext.index();
        var inventory = inventoryOpt.get();
        for (var entry : inventory.getCurios().entrySet()) {
            var handler = entry.getValue();
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack other = handler.getStacks().getStackInSlot(i);
                if (!other.isEmpty() && other.getItem() == targetItem) {
                    return entry.getKey().equals(currentId) && i == currentIndex;
                }
            }
        }
        return true;
    }
}
