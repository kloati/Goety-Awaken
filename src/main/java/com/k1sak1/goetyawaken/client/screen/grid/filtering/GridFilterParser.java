package com.k1sak1.goetyawaken.client.screen.grid.filtering;

import com.k1sak1.goetyawaken.client.screen.grid.stack.IGridStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class GridFilterParser {
    private GridFilterParser() {
    }

    public static List<Predicate<IGridStack>> getFilters(String query) {
        List<Predicate<IGridStack>> orGroups = new ArrayList<>();
        if (query == null || query.isEmpty()) {
            return orGroups;
        }

        String[] orSplit = query.split("\\|");
        for (String orPart : orSplit) {
            if (orPart == null)
                continue;
            String trimmed = orPart.trim();
            if (trimmed.isEmpty())
                continue;

            String[] andSplit = trimmed.split("\\s+");
            List<Predicate<IGridStack>> andGroup = new ArrayList<>();
            for (String token : andSplit) {
                if (token == null || token.isEmpty())
                    continue;
                andGroup.add(parseToken(token));
            }
            if (andGroup.isEmpty())
                continue;

            Predicate<IGridStack> combined = andGroup.get(0);
            for (int i = 1; i < andGroup.size(); i++) {
                combined = combined.and(andGroup.get(i));
            }
            orGroups.add(combined);
        }
        return orGroups;
    }

    private static Predicate<IGridStack> parseToken(String token) {
        if (token.startsWith("@")) {
            return new ModGridFilter(token.substring(1));
        }
        if (token.startsWith("$")) {
            return new TagGridFilter(token.substring(1));
        }
        if (token.startsWith("#")) {
            return new TooltipGridFilter(token.substring(1));
        }
        return new NameGridFilter(token);
    }
}
