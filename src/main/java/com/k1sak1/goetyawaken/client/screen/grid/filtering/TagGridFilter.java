package com.k1sak1.goetyawaken.client.screen.grid.filtering;

import com.k1sak1.goetyawaken.client.screen.grid.stack.IGridStack;

import java.util.Set;
import java.util.function.Predicate;

public class TagGridFilter implements Predicate<IGridStack> {
    private final String query;

    public TagGridFilter(String query) {
        this.query = query == null ? "" : query.toLowerCase();
    }

    @Override
    public boolean test(IGridStack stack) {
        if (query.isEmpty()) {
            return true;
        }
        Set<String> tags = stack.getTags();
        if (tags == null || tags.isEmpty()) {
            return false;
        }
        for (String tag : tags) {
            if (tag != null && tag.toLowerCase().contains(query)) {
                return true;
            }
        }
        return false;
    }
}
