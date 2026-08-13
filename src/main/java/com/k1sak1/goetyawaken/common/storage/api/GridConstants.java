package com.k1sak1.goetyawaken.common.storage.api;

public final class GridConstants {
    public static final int SORTING_DIRECTION_ASCENDING = 0;
    public static final int SORTING_DIRECTION_DESCENDING = 1;

    public static final int SORTING_TYPE_NAME = 0;
    public static final int SORTING_TYPE_QUANTITY = 1;

    public static final int VIEW_TYPE_NORMAL = 0;
    public static final int VIEW_TYPE_NON_CRAFTABLES = 1;
    public static final int VIEW_TYPE_CRAFTABLES = 2;

    public static final int SIZE_STRETCH = 0;
    public static final int SIZE_SMALL = 1;
    public static final int SIZE_MEDIUM = 2;
    public static final int SIZE_LARGE = 3;

    public static boolean isValidSize(int size) {
        return size >= SIZE_STRETCH && size <= SIZE_LARGE;
    }

    public static boolean isValidViewType(int type) {
        return type == VIEW_TYPE_NORMAL
                || type == VIEW_TYPE_NON_CRAFTABLES
                || type == VIEW_TYPE_CRAFTABLES;
    }

    private GridConstants() {
        throw new UnsupportedOperationException();
    }
}
