package com.k1sak1.goetyawaken.client.screen.grid.filtering;

import com.k1sak1.goetyawaken.client.screen.grid.stack.IGridStack;
import com.k1sak1.goetyawaken.integration.pinyin.PinyinIntegration;

import java.util.function.Predicate;

public class NameGridFilter implements Predicate<IGridStack> {
    private final String name;

    public NameGridFilter(String name) {
        this.name = name == null ? "" : name;
    }

    @Override
    public boolean test(IGridStack stack) {
        if (name.isEmpty()) {
            return true;
        }
        return PinyinIntegration.contains(stack.getName(), name);
    }
}
