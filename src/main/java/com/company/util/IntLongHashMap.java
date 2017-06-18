package com.company.util;

/**
 * Created by ekhomiak on 17.06.2017.
 */

public class IntLongHashMap {
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    private static final int INIT_CAPACITY_DEFAULT_VALUE = 16;
    private static final float LOAD_FACTOR_DEFAULT_VALUE = 0.75f;
    private static final float MULTIPLIER_DEFAULT_VALUE = 2.0f;
    private static final long NO_ENTRY_VALUE_DEFAULT_VALUE = Long.MIN_VALUE;

    private static final String ILLEGAL_INITIAL_CAPACITY_PATTERN = "Illegal initial capacity: %d";
    private static final String ILLEGAL_LOAD_FACTOR_PATTERN = "Illegal load factor: : %f";
    private static final String ILLEGAL_MULTIPLIER_PATTERN = "Illegal multiplier: : %f";

    private int capacity;
    private float loadFactor;
    private float multiplier;
    private long noEntryValue = NO_ENTRY_VALUE_DEFAULT_VALUE;

    private int[] keys;
    private long[] values;
    private int size;
    private int limit;
    private boolean entryWithNullKeyIsAssociated;
    private long nullValue = noEntryValue;

    public IntLongHashMap(int initialCapacity, float loadFactor, float multiplier, long noEntryValue) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException(String.format(ILLEGAL_INITIAL_CAPACITY_PATTERN, initialCapacity));
        }
        if (initialCapacity > MAXIMUM_CAPACITY) {
            initialCapacity = MAXIMUM_CAPACITY;
        }

        if (Float.isNaN(loadFactor) || loadFactor <= 0 || loadFactor > 1.0) {
            throw new IllegalArgumentException(String.format(ILLEGAL_LOAD_FACTOR_PATTERN, loadFactor));
        }
        if (multiplier <= 0 || Float.isNaN(multiplier)) {
            throw new IllegalArgumentException(String.format(ILLEGAL_MULTIPLIER_PATTERN, multiplier));
        }

        this.loadFactor = loadFactor;
        this.multiplier = multiplier;
        this.noEntryValue = noEntryValue;

        // In our case, when key is <int>, it is better when the hash function is the same as Integer.hashCode(key) -
        // in other words, hash = key. Because of it there is no need to have separate hash-table and keys-table and
        // separate method <hashCode>; let use <keys> also as "the hash-table"
        keys = new int[initialCapacity];
        values = new long[initialCapacity];

        setCapacity(initialCapacity);
    }

    public IntLongHashMap(int initialCapacity, float loadFactor, float multiplier) {
        this(initialCapacity, loadFactor, multiplier, NO_ENTRY_VALUE_DEFAULT_VALUE);
    }

    public IntLongHashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, MULTIPLIER_DEFAULT_VALUE);
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

    private void setCapacity(int capacity) {
        this.capacity = capacity;
        this.limit = (int) (capacity * loadFactor);
    }

    private int indexFor(int hash) {
        return hash % capacity;
    }

    private void rawPut(int key, long value) {
        int index = indexFor(key);
        while (keys[index] != 0) {
            if ((++index) >= capacity) {
                index = 0;
            }
        }

        keys[index] = key;
        values[index] = value;
        size++;
    }

    private void reAllocTables() {
        setCapacity((int) (capacity * multiplier));

        int[] oldKeys = keys;
        long[] oldValues = values;

        keys = new int[capacity];
        values = new long[capacity];

        size = 0;
        for (int i = 0; i < oldKeys.length; i++) {
            if (oldKeys[i] != 0) {
                rawPut(oldKeys[i], oldValues[i]);
            }
        }
    }

    private int getIndex(int key) {
        int result = -1;

        int index = indexFor(key);
        if (keys[index] == key) {
            result = index;
        } else {
            int startIndex = index;
            while (keys[index] != key) {
                index++;
                if (index >= capacity) {
                    index = 0;
                }
                if (index == startIndex) {
                    // The full loop is a sign that no value is associated with given key
                    index = -1;
                    break;
                }
            }
            if (index >= 0) {
                result = index;
            }
        }

        return result;
    }

    public long put(int key, long value) {
        // There is deliberately no control that <value> can be equal to <noEntryValue>. The "side-effect" of such
        // approach is that <noEntryValue> can be returned in case if some entry(ies) really contain(s) <noEntryValue>
        // and also in case, if there no entry associated with given key
        long result = noEntryValue;

        // Separately check for "null key" (0 is default ("empty") initial value for array data, so "null key" 0
        // is deliberately not "described" as a constant here
        if (key == 0) {
            result = nullValue;
            nullValue = value;
            entryWithNullKeyIsAssociated = true;
        } else {
            int index = getIndex(key);
            if (index >= 0) {
                result = values[index];
                values[index] = value;
            } else {
                if (size >= limit) {
                    reAllocTables();
                }
                rawPut(key, value);
            }
        }

        return result;
    }

    public long get(int key) {
        long result = noEntryValue;

        // Separately check for "null key" (0 is default ("empty") value for array data, so "null key" 0 is deliberately
        // not "described" as a constant here
        if (key == 0) {
            result = nullValue;
        } else {
            int index = getIndex(key);
            if (index >= 0) {
                result = values[index];
            }
        }

        return result;
    }

    public int size() {
        return entryWithNullKeyIsAssociated ? size + 1 : size;
    }
}
