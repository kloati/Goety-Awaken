package com.k1sak1.goetyawaken.common.crafting;

import com.k1sak1.goetyawaken.Config;
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
                    int duration = effectInstance.getDuration();
                    int amplifier = effectInstance.getAmplifier();
                    duration = Math.min(duration, Config.SHULKER_MISSILE_MAX_POTION_DURATION.get());
                    amplifier = Math.min(amplifier, Config.SHULKER_MISSILE_MAX_POTION_AMPLIFIER.get());
                    potionTag.putInt("Duration", duration);
                    potionTag.putInt("Amplifier", amplifier);
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
                            // Apply configuration limits for brew effects
                            int duration = effectTag.getInt("Duration");
                            int amplifier = effectTag.getInt("Amplifier");

                            // Try to get the actual effect to check if it's instantaneous
                            ResourceLocation brewEffectKey = new ResourceLocation(brewId);
                            net.minecraft.world.effect.MobEffect effect = ForgeRegistries.MOB_EFFECTS
                                    .getValue(brewEffectKey);

                            if (effect != null && !effect.isInstantenous()) {
                                // For non-instantaneous effects, apply duration and amplifier limits
                                duration = Math.min(duration, Config.shulkerMissileMaxPotionDuration);
                                amplifier = Math.min(amplifier, Config.shulkerMissileMaxPotionAmplifier);
                            } else {
                                // For instantaneous effects or unknown effects, only limit amplifier
                                amplifier = Math.min(amplifier, Config.shulkerMissileMaxPotionAmplifier);
                            }

                            potionTag.putInt("Duration", duration);
                            potionTag.putInt("Amplifier", amplifier);
                        }
                    }
                } else if (potion.getTag().contains("Potion")) {
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
                                // Apply configuration limits
                                int duration = effectInstance.getDuration();
                                int amplifier = effectInstance.getAmplifier();

                                boolean isInstantaneous = effectInstance.getEffect().isInstantenous();

                                if (!isInstantaneous) {
                                    duration = Math.min(duration, Config.shulkerMissileMaxPotionDuration);
                                    amplifier = Math.min(amplifier, Config.shulkerMissileMaxPotionAmplifier);
                                } else {
                                    amplifier = Math.min(amplifier, Config.shulkerMissileMaxPotionAmplifier);
                                }

                                potionTag.putInt("Duration", duration);
                                potionTag.putInt("Amplifier", amplifier);
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