package com.k1sak1.goetyawaken.common.items.magic.grimoire;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GrimoireAffix {
    public static final String TAG_AFFIXES = "goetyawaken:affixes";
    private static final String TAG_ATTR = "attr";
    private static final String TAG_OP = "op";
    private static final String TAG_VALUE = "value";
    private static final String TAG_TIER = "tier";

    public final ResourceLocation attribute;
    public final AttributeModifier.Operation op;
    public final double value;
    public final int tier;

    public GrimoireAffix(ResourceLocation attribute, AttributeModifier.Operation op, double value, int tier) {
        this.attribute = attribute;
        this.op = op;
        this.value = value;
        this.tier = tier;
    }

    public static List<GrimoireAffix> getAffixes(ItemStack stack) {
        List<GrimoireAffix> affixes = new ArrayList<>();
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_AFFIXES, Tag.TAG_LIST)) {
            return affixes;
        }
        ListTag list = tag.getList(TAG_AFFIXES, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            GrimoireAffix affix = fromNbt(list.getCompound(i));
            if (affix != null) {
                affixes.add(affix);
            }
        }
        return affixes;
    }

    public static void setAffixes(ItemStack stack, List<GrimoireAffix> affixes) {
        ListTag list = new ListTag();
        for (GrimoireAffix affix : affixes) {
            list.add(toNbt(affix));
        }
        stack.getOrCreateTag().put(TAG_AFFIXES, list);
    }

    public static boolean appendAffix(ItemStack stack, GrimoireAffix affix, int maxAffixes) {
        List<GrimoireAffix> affixes = getAffixes(stack);
        for (int i = 0; i < affixes.size(); i++) {
            GrimoireAffix existing = affixes.get(i);
            if (existing.attribute.equals(affix.attribute) && existing.op == affix.op) {
                if (Math.abs(affix.value) > Math.abs(existing.value)) {
                    affixes.set(i, affix);
                    setAffixes(stack, affixes);
                }
                return true;
            }
        }
        if (affixes.size() >= maxAffixes) {
            return false;
        }
        affixes.add(affix);
        setAffixes(stack, affixes);
        return true;
    }

    public static CompoundTag toNbt(GrimoireAffix affix) {
        CompoundTag tag = new CompoundTag();
        tag.putString(TAG_ATTR, affix.attribute.toString());
        tag.putByte(TAG_OP, (byte) affix.op.ordinal());
        tag.putDouble(TAG_VALUE, affix.value);
        tag.putInt(TAG_TIER, affix.tier);
        return tag;
    }

    public static GrimoireAffix fromNbt(CompoundTag tag) {
        if (!tag.contains(TAG_ATTR) || !tag.contains(TAG_OP) || !tag.contains(TAG_VALUE)) {
            return null;
        }
        ResourceLocation attr;
        try {
            attr = new ResourceLocation(tag.getString(TAG_ATTR));
        } catch (Exception e) {
            return null;
        }
        int opId = tag.getByte(TAG_OP);
        if (opId < 0 || opId >= AttributeModifier.Operation.values().length) {
            return null;
        }
        return new GrimoireAffix(attr, AttributeModifier.Operation.values()[opId], tag.getDouble(TAG_VALUE),
                tag.getInt(TAG_TIER));
    }
}
