package com.k1sak1.goetyawaken.integration.jei;

import com.k1sak1.goetyawaken.common.network.ModNetwork;
import com.k1sak1.goetyawaken.common.storage.container.EnderAccessLecternContainer;
import com.k1sak1.goetyawaken.common.storage.network.message.GridTransferMessage;
import com.k1sak1.goetyawaken.init.ModContainerTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GridRecipeTransferHandler implements IRecipeTransferHandler<EnderAccessLecternContainer, Object> {

    @Override
    public Class<? extends EnderAccessLecternContainer> getContainerClass() {
        return EnderAccessLecternContainer.class;
    }

    @Override
    public Optional<MenuType<EnderAccessLecternContainer>> getMenuType() {
        return Optional.of(ModContainerTypes.ENDER_ACCESS_LECTERN.get());
    }

    @Override
    public RecipeType<Object> getRecipeType() {

        return null;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(
            EnderAccessLecternContainer container, Object recipe,
            IRecipeSlotsView recipeSlots, Player player,
            boolean maxTransfer, boolean doTransfer) {

        if (!doTransfer) {
            return null;
        }

        List<List<ItemStack>> inputs = new ArrayList<>();
        for (IRecipeSlotView slotView : recipeSlots.getSlotViews(RecipeIngredientRole.INPUT)) {
            List<ItemStack> stacks = slotView.getItemStacks()
                    .collect(Collectors.toCollection(ArrayList::new));

            Optional<ItemStack> displayStack = slotView.getDisplayedItemStack();
            displayStack.ifPresent(stack -> {
                int index = stacks.indexOf(stack);
                if (index > 0) {
                    stacks.remove(index);
                    stacks.add(0, stack);
                }
            });

            inputs.add(stacks);
        }

        ItemStack[][] inputsArray = new ItemStack[inputs.size()][];
        for (int i = 0; i < inputs.size(); i++) {
            inputsArray[i] = inputs.get(i).toArray(new ItemStack[0]);
        }

        ModNetwork.channel.sendToServer(new GridTransferMessage(inputsArray));

        return null;
    }
}
