package com.k1sak1.goetyawaken.client.screen.grid.filtering;

import com.k1sak1.goetyawaken.client.screen.grid.stack.IGridStack;

import java.util.function.Predicate;

public class ModGridFilter implements Predicate<IGridStack> {
    private final String inputModName;

    public ModGridFilter(String inputModName) {
        this.inputModName = standardify(inputModName == null ? "" : inputModName);
    }

    @Override
    public boolean test(IGridStack stack) {
        String modId = stack.getModId();
        return modId != null && modId.contains(inputModName);
    }

    private String standardify(String input) {
        return input.toLowerCase().replace(" ", "");
    }
}
