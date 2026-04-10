package com.k1sak1.goetyawaken.common.crafting;

import com.k1sak1.goetyawaken.GoetyAwaken;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(
            ForgeRegistries.RECIPE_TYPES, GoetyAwaken.MODID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(
            ForgeRegistries.RECIPE_SERIALIZERS, GoetyAwaken.MODID);

    public static void init() {
        RECIPE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    public static final RegistryObject<RecipeType<ShulkerMissilePotionRecipe>> SHULKER_MISSILE_POTION_RECIPE_TYPE = register(
            "shulker_missile_potion");
    public static final RegistryObject<RecipeSerializer<ShulkerMissilePotionRecipe>> SHULKER_MISSILE_POTION_RECIPE = RECIPE_SERIALIZERS
            .register("shulker_missile_potion",
                    () -> new SimpleCraftingRecipeSerializer<>(ShulkerMissilePotionRecipe::new));

    static <T extends Recipe<?>> RegistryObject<RecipeType<T>> register(final String id) {
        return RECIPE_TYPES.register(id, () -> new RecipeType<T>() {
            public String toString() {
                return id;
            }
        });
    }
}