package com.k1sak1.goetyawaken.common.crafting;

import com.k1sak1.goetyawaken.common.items.magic.GrimoireItem;
import com.k1sak1.goetyawaken.common.items.magic.grimoire.GrimoireAffixHelper;
import com.k1sak1.goetyawaken.init.ModTags;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class GrimoireAffixRecipe extends CustomRecipe {

    private long cachedWorldSeed;

    public GrimoireAffixRecipe(ResourceLocation pId, CraftingBookCategory pCategory) {
        super(pId, pCategory);
    }

    @Override
    public boolean matches(CraftingContainer pContainer, Level pLevel) {
        ItemStack grimoire = ItemStack.EMPTY;
        int materialCount = 0;
        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack stack = pContainer.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof GrimoireItem) {
                if (!grimoire.isEmpty()) {
                    return false;
                }
                grimoire = stack;
            } else if (stack.is(ModTags.Items.GRIMOIRE_MATERIALS)) {
                materialCount++;
            } else {
                return false;
            }
        }
        if (!pLevel.isClientSide) {
            this.cachedWorldSeed = pLevel instanceof net.minecraft.server.level.ServerLevel serverLevel
                    ? serverLevel.getSeed()
                    : 0L;
        }
        return !grimoire.isEmpty() && materialCount >= 1 && !GrimoireAffixHelper.isFull(grimoire);
    }

    @Override
    public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
        ItemStack grimoire = ItemStack.EMPTY;
        List<ItemStack> materials = new ArrayList<>();
        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack stack = pContainer.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof GrimoireItem) {
                grimoire = stack;
            } else {
                materials.add(stack);
            }
        }
        if (grimoire.isEmpty() || materials.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = grimoire.copy();
        long seed = this.cachedWorldSeed ^ GrimoireAffixHelper.hashInput(result, materials);
        GrimoireAffixHelper.rollAndAppendCrafting(result, materials, seed);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.GRIMOIRE_AFFIX_RECIPE.get();
    }
}
