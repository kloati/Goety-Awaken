package com.k1sak1.goetyawaken.common.crafting;

import com.k1sak1.goetyawaken.common.items.ModItems;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class ShulkerMissilePotionRecipe extends CustomRecipe {
    public ShulkerMissilePotionRecipe(ResourceLocation pId, CraftingBookCategory pCategory) {
        super(pId, pCategory);
    }

    @Override
    public boolean matches(CraftingContainer pContainer, Level pLevel) {
        ItemStack shulkerMissileFocus = ItemStack.EMPTY;
        ItemStack potion = ItemStack.EMPTY;

        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack itemstack = pContainer.getItem(i);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == ModItems.SHULKER_MISSILE_FOCUS.get()) {
                    if (!shulkerMissileFocus.isEmpty()) {
                        return false;
                    }
                    shulkerMissileFocus = itemstack;
                } else if (itemstack.getItem() == Items.POTION ||
                        itemstack.getItem() == Items.SPLASH_POTION ||
                        itemstack.getItem() == Items.LINGERING_POTION ||
                        (itemstack.getItem() instanceof com.Polarice3.Goety.common.items.brew.BrewItem)) {
                    if (!potion.isEmpty()) {
                        return false;
                    }
                    potion = itemstack;
                } else {
                    return false;
                }
            }
        }

        return !shulkerMissileFocus.isEmpty() && !potion.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
        ItemStack shulkerMissileFocus = ItemStack.EMPTY;
        ItemStack potion = ItemStack.EMPTY;

        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack itemstack = pContainer.getItem(i);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == ModItems.SHULKER_MISSILE_FOCUS.get()) {
                    shulkerMissileFocus = itemstack;
                } else if (itemstack.getItem() == Items.POTION ||
                        itemstack.getItem() == Items.SPLASH_POTION ||
                        itemstack.getItem() == Items.LINGERING_POTION ||
                        (itemstack.getItem() instanceof com.Polarice3.Goety.common.items.brew.BrewItem)) {
                    potion = itemstack;
                }
            }
        }

        if (shulkerMissileFocus.isEmpty() || potion.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = shulkerMissileFocus.copy();
        if (!result.hasTag()) {
            result.setTag(new net.minecraft.nbt.CompoundTag());
        }
        net.minecraft.nbt.CompoundTag potionTag = new net.minecraft.nbt.CompoundTag();
        if (potion.getItem() == Items.POTION ||
                potion.getItem() == Items.SPLASH_POTION ||
                potion.getItem() == Items.LINGERING_POTION) {
            net.minecraft.world.effect.MobEffectInstance effectInstance = null;
            java.util.List<net.minecraft.world.effect.MobEffectInstance> effects = PotionUtils.getMobEffects(potion);
            if (!effects.isEmpty()) {
                int randomIndex = effects.size() == 1 ? 0
                        : net.minecraft.util.RandomSource.create().nextInt(effects.size());
                effectInstance = effects.get(randomIndex);
            }

            if (effectInstance != null) {
                ResourceLocation effectKey = ForgeRegistries.MOB_EFFECTS.getKey(effectInstance.getEffect());
                if (effectKey != null) {
                    potionTag.putString("EffectName", effectKey.toString());
                    potionTag.putInt("Duration", effectInstance.getDuration());
                    potionTag.putInt("Amplifier", effectInstance.getAmplifier());
                }
            }
        } else if (potion.getItem() instanceof com.Polarice3.Goety.common.items.brew.BrewItem) {
            if (potion.hasTag()) {
                if (potion.getTag().contains("CustomPotionEffects")) {
                    net.minecraft.nbt.ListTag effectsList = potion.getTag().getList("CustomPotionEffects",
                            net.minecraft.nbt.Tag.TAG_COMPOUND);
                    if (!effectsList.isEmpty()) {
                        int randomIndex = effectsList.size() == 1 ? 0
                                : net.minecraft.util.RandomSource.create().nextInt(effectsList.size());
                        net.minecraft.nbt.CompoundTag effectTag = effectsList.getCompound(randomIndex);
                        String brewId = effectTag.contains("forge:id") ? effectTag.getString("forge:id") : "";
                        if (brewId.isEmpty() && potion.getTag().contains("CustomBrewEffects")) {
                            net.minecraft.nbt.ListTag brewEffectsList = potion.getTag().getList("CustomBrewEffects",
                                    net.minecraft.nbt.Tag.TAG_COMPOUND);
                            if (!brewEffectsList.isEmpty()) {
                                int brewRandomIndex = brewEffectsList.size() == 1 ? 0
                                        : net.minecraft.util.RandomSource.create().nextInt(brewEffectsList.size());
                                net.minecraft.nbt.CompoundTag brewEffectTag = brewEffectsList
                                        .getCompound(brewRandomIndex);
                                brewId = brewEffectTag.getString("BrewId");
                            }
                        }

                        if (brewId != null && !brewId.isEmpty() && !brewId.equals("minecraft:")) {
                            potionTag.putString("EffectName", brewId);
                            potionTag.putInt("Duration", effectTag.getInt("Duration"));
                            potionTag.putInt("Amplifier", effectTag.getInt("Amplifier"));
                        }
                    }
                }
                else if (potion.getTag().contains("Potion")) {
                    java.util.List<net.minecraft.world.effect.MobEffectInstance> effects = PotionUtils
                            .getMobEffects(potion);
                    if (!effects.isEmpty()) {
                        int randomIndex = effects.size() == 1 ? 0
                                : net.minecraft.util.RandomSource.create().nextInt(effects.size());
                        net.minecraft.world.effect.MobEffectInstance effectInstance = effects.get(randomIndex);

                        if (effectInstance != null) {
                            ResourceLocation effectKey = ForgeRegistries.MOB_EFFECTS.getKey(effectInstance.getEffect());
                            if (effectKey != null) {
                                potionTag.putString("EffectName", effectKey.toString());
                                potionTag.putInt("Duration", effectInstance.getDuration());
                                potionTag.putInt("Amplifier", effectInstance.getAmplifier());
                            }
                        }
                    }
                }
            }
        }
        result.getTag().put("PotionEffect", potionTag);

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SHULKER_MISSILE_POTION_RECIPE.get();
    }
}