package com.k1sak1.goetyawaken.client.screen.grid.view;

import com.k1sak1.goetyawaken.client.screen.grid.stack.IGridStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface IGridView {
    List<IGridStack> getStacks();

    void setStacks(List<? extends IGridStack> stacks);

    Collection<IGridStack> getAllStacks();

    @Nullable
    IGridStack get(UUID id);

    void sort();

    void forceSort();

    void postChange(IGridStack stack, int delta);

    void setCanCraft(boolean canCraft);

    boolean canCraft();

    int getRows();
}
