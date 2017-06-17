package com.company.util;

/**
 * Created by ekhomiak on 15.06.2017.
 */

public class IntLongHashMap {
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    private static final int INIT_CAPACITY_DEFAULT_VALUE = 16;
    private static final float LOAD_FACTOR_DEFAULT_VALUE = 0.75f;
    private static final long NO_ENTRY_VALUE_DEFAULT_VALUE = Long.MIN_VALUE;

    private static final String ILLEGAL_INITIAL_CAPACITY_PATTERN = "Illegal initial capacity: %d";
    private static final String ILLEGAL_LOAD_FACTOR_PATTERN = "Illegal load factor: : %f";

    private int initialCapacity;
    private float loadFactor;
    private long noEntryValue;

    private int[] keys;
    private long[] values;
    private int size;

    public IntLongHashMap(int initialCapacity, float loadFactor, long noEntryValue) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException(String.format(ILLEGAL_INITIAL_CAPACITY_PATTERN, initialCapacity));
        }
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;

        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException(String.format(ILLEGAL_LOAD_FACTOR_PATTERN, loadFactor));

        this.initialCapacity = initialCapacity;
        this.loadFactor = loadFactor;
        this.noEntryValue = noEntryValue;
    }

    public IntLongHashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, NO_ENTRY_VALUE_DEFAULT_VALUE);
    }

    public IntLongHashMap(int initialCapacity) {
        this(initialCapacity, LOAD_FACTOR_DEFAULT_VALUE);
    }

    public IntLongHashMap() {
        this(INIT_CAPACITY_DEFAULT_VALUE);
    }

    public long getNoEntryValue() {
        return noEntryValue;
    }

    public void setNoEntryValue(long noEntryValue) {
        this.noEntryValue = noEntryValue;
    }

    int hash(int key) {
        return key;
    }

    public int size() {
        return size;
    }

    public long get(int key) {
        return noEntryValue;
    }

    public long put(int key, long value) {
        return noEntryValue;
    }
}
