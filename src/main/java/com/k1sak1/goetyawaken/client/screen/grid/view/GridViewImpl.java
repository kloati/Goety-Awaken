package com.k1sak1.goetyawaken.client.screen.grid.view;

import com.k1sak1.goetyawaken.client.screen.grid.filtering.GridFilterParser;
import com.k1sak1.goetyawaken.client.screen.grid.stack.IGridStack;
import com.k1sak1.goetyawaken.common.storage.api.GridConstants;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GridViewImpl implements IGridView {

    public static final int SORTING_DIRECTION_ASCENDING = GridConstants.SORTING_DIRECTION_ASCENDING;
    public static final int SORTING_DIRECTION_DESCENDING = GridConstants.SORTING_DIRECTION_DESCENDING;

    public static final int SORTING_TYPE_NAME = GridConstants.SORTING_TYPE_NAME;
    public static final int SORTING_TYPE_QUANTITY = GridConstants.SORTING_TYPE_QUANTITY;

    public static final int VIEW_TYPE_NORMAL = GridConstants.VIEW_TYPE_NORMAL;
    public static final int VIEW_TYPE_NON_CRAFTABLES = GridConstants.VIEW_TYPE_NON_CRAFTABLES;
    public static final int VIEW_TYPE_CRAFTABLES = GridConstants.VIEW_TYPE_CRAFTABLES;

    public static boolean isValidViewType(int type) {
        return GridConstants.isValidViewType(type);
    }

    public static final int SIZE_STRETCH = GridConstants.SIZE_STRETCH;
    public static final int SIZE_SMALL = GridConstants.SIZE_SMALL;
    public static final int SIZE_MEDIUM = GridConstants.SIZE_MEDIUM;
    public static final int SIZE_LARGE = GridConstants.SIZE_LARGE;

    public static boolean isValidSize(int size) {
        return GridConstants.isValidSize(size);
    }

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
        Comparator<IGridStack> stableComparator = comparator.thenComparing(IGridStack::getId);

        this.stacks = map.values().stream()
                .filter(filter)
                .sorted(stableComparator)
                .collect(Collectors.toCollection(ArrayList::new));
        this.active = true;

        if (scrollbarUpdater != null) {
            scrollbarUpdater.run();
        }
    }

    private void addStack(IGridStack stack) {
        Predicate<IGridStack> filter = getFilter();
        if (!filter.test(stack)) {
            return;
        }

        Comparator<IGridStack> comparator;
        if (sortingType == SORTING_TYPE_QUANTITY) {
            comparator = Comparator.comparingInt(IGridStack::getQuantity);
        } else {
            comparator = Comparator.comparing(IGridStack::getName, String.CASE_INSENSITIVE_ORDER);
        }

        if (sortingDirection == SORTING_DIRECTION_DESCENDING) {
            comparator = comparator.reversed();
        }
        Comparator<IGridStack> stableComparator = comparator.thenComparing(IGridStack::getId);

        int insertionPos = Collections.binarySearch(stacks, stack, stableComparator);
        if (insertionPos < 0) {
            insertionPos = -insertionPos - 1;
        }
        stacks.add(insertionPos, stack);
    }

    private Predicate<IGridStack> getFilter() {
        Predicate<IGridStack> base = stack -> {
            if (viewType == VIEW_TYPE_NON_CRAFTABLES && stack.isCraftable()) {
                return false;
            }
            if (viewType == VIEW_TYPE_CRAFTABLES) {
                if (stack.isCraftable()) {
                    return true;
                }
                if (!CraftableItemCache.isCraftable(stack.getIngredient().getItem())) {
                    return false;
                }
            }
            return stack.getQuantity() > 0 || stack.isCraftable();
        };

        if (searchQuery == null || searchQuery.isEmpty()) {
            return base;
        }

        List<Predicate<IGridStack>> orGroups = GridFilterParser.getFilters(searchQuery);
        if (orGroups.isEmpty()) {
            return base;
        }

        return base.and(stack -> {
            for (Predicate<IGridStack> group : orGroups) {
                if (group.test(stack)) {
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public void postChange(IGridStack stack, int delta) {
        if (!this.active) {
            return;
        }

        IGridStack existing = map.get(stack.getId());

        if (existing == null) {
            if (delta < 0) {
                return;
            }
            stack.setQuantity(delta);
            map.put(stack.getId(), stack);
            existing = stack;

            addStack(existing);
        } else {
            existing.setQuantity(existing.getQuantity() + delta);
            if (existing.getQuantity() <= 0) {
                map.remove(existing.getId());
                stacks.remove(existing);
            } else if (sortingType == SORTING_TYPE_QUANTITY) {
                stacks.remove(existing);
                addStack(existing);
            }
        }

        if (scrollbarUpdater != null) {
            scrollbarUpdater.run();
        }
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
