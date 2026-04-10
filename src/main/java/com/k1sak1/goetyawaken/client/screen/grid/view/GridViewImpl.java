package com.k1sak1.goetyawaken.client.screen.grid.view;

import com.k1sak1.goetyawaken.client.screen.grid.stack.IGridStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GridViewImpl implements IGridView {
    public static final int SORTING_DIRECTION_ASCENDING = 0;
    public static final int SORTING_DIRECTION_DESCENDING = 1;

    public static final int SORTING_TYPE_NAME = 0;
    public static final int SORTING_TYPE_QUANTITY = 1;

    public static final int VIEW_TYPE_NORMAL = 0;
    public static final int VIEW_TYPE_CRAFTABLES = 1;

    private final Map<UUID, IGridStack> map = new HashMap<>();
    private List<IGridStack> stacks = new ArrayList<>();
    private boolean canCraft;
    private boolean active = false;
    private String searchQuery = "";
    private Runnable scrollbarUpdater;

    private int sortingDirection = SORTING_DIRECTION_ASCENDING;
    private int sortingType = SORTING_TYPE_NAME;
    private int viewType = VIEW_TYPE_NORMAL;

    public GridViewImpl() {
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query != null ? query.toLowerCase() : "";
    }

    public void setScrollbarUpdater(Runnable updater) {
        this.scrollbarUpdater = updater;
    }

    @Override
    public List<IGridStack> getStacks() {
        return stacks;
    }

    @Override
    public void setStacks(List<? extends IGridStack> stacks) {
        map.clear();
        for (IGridStack stack : stacks) {
            map.put(stack.getId(), stack);
        }
    }

    @Override
    public Collection<IGridStack> getAllStacks() {
        return map.values();
    }

    @Nullable
    @Override
    public IGridStack get(UUID id) {
        return map.get(id);
    }

    @Override
    public void sort() {
        forceSort();
    }

    public int getSortingDirection() {
        return sortingDirection;
    }

    public void setSortingDirection(int direction) {
        this.sortingDirection = direction;
    }

    public int getSortingType() {
        return sortingType;
    }

    public void setSortingType(int type) {
        this.sortingType = type;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int type) {
        this.viewType = type;
    }

    @Override
    public void forceSort() {
        Predicate<IGridStack> filter = getFilter();

        Comparator<IGridStack> comparator;
        if (sortingType == SORTING_TYPE_QUANTITY) {
            comparator = Comparator.comparingInt(IGridStack::getQuantity);
        } else {
            comparator = Comparator.comparing(IGridStack::getName, String.CASE_INSENSITIVE_ORDER);
        }

        if (sortingDirection == SORTING_DIRECTION_DESCENDING) {
            comparator = comparator.reversed();
        }

        this.stacks = map.values().stream()
                .filter(filter)
                .sorted(comparator)
                .collect(Collectors.toCollection(ArrayList::new));
        this.active = true;

        if (scrollbarUpdater != null) {
            scrollbarUpdater.run();
        }
    }

    private Predicate<IGridStack> getFilter() {
        if (searchQuery == null || searchQuery.isEmpty()) {
            if (viewType == VIEW_TYPE_CRAFTABLES) {
                return IGridStack::isCraftable;
            }
            return stack -> stack.getQuantity() > 0 || stack.isCraftable();
        }

        return stack -> {
            if (viewType == VIEW_TYPE_CRAFTABLES && !stack.isCraftable()) {
                return false;
            }
            if (stack.getQuantity() <= 0 && !stack.isCraftable()) {
                return false;
            }

            String query = searchQuery;

            if (query.startsWith("@")) {
                return stack.getModId().contains(query.substring(1));
            }

            if (query.startsWith("$")) {
                String tagQuery = query.substring(1);
                return stack.getTags().stream().anyMatch(tag -> tag.contains(tagQuery));
            }

            return stack.getName().toLowerCase().contains(query);
        };
    }

    @Override
    public void postChange(IGridStack stack, int delta) {
        IGridStack existing = map.get(stack.getId());

        if (existing == null) {
            stack.setQuantity(delta);
            map.put(stack.getId(), stack);
        } else {
            int newQty = existing.getQuantity() + delta;
            if (newQty <= 0) {
                map.remove(existing.getId());
            } else {
                existing.setQuantity(newQty);
            }
        }

        forceSort();
    }

    @Override
    public void setCanCraft(boolean canCraft) {
        this.canCraft = canCraft;
    }

    @Override
    public boolean canCraft() {
        return canCraft;
    }

    @Override
    public int getRows() {
        return Math.max(0, (int) Math.ceil((float) stacks.size() / 9F));
    }
}
