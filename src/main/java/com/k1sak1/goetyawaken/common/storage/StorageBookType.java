package com.k1sak1.goetyawaken.common.storage;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public enum StorageBookType {
    IRON("iron", 1024),
    GOLD("gold", 4096),
    EMERALD("emerald", 16384),
    DIAMOND("diamond", 65536),
    SAPPHIRE("sapphire", 262144);

    private final String name;
    private final int capacity;

    StorageBookType(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public static StorageBookType fromCapacity(int capacity) {
        for (StorageBookType type : values()) {
            if (type.capacity == capacity) {
                return type;
            }
        }
        return DIAMOND;
    }
}
