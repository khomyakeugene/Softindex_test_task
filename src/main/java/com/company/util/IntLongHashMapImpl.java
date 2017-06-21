package com.company.util;

/**
 * Created by Yevhen Khomiak on 17.06.2017.
 */

public class IntLongHashMapImpl implements IntLongHashMap {
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    private static final int INIT_CAPACITY_DEFAULT_VALUE = 16;
    private static final float LOAD_FACTOR_DEFAULT_VALUE = 0.75f;
    private static final float MULTIPLIER_DEFAULT_VALUE = 2.0f;
    private static final long NO_ENTRY_VALUE_DEFAULT_VALUE = Long.MIN_VALUE;

    private static final String ILLEGAL_INITIAL_CAPACITY_PATTERN = "Illegal initial capacity: %d";
    private static final String ILLEGAL_LOAD_FACTOR_PATTERN = "Illegal load factor: : %f";
    private static final String ILLEGAL_MULTIPLIER_PATTERN = "Illegal multiplier: : %f";

    private int capacity;
    private final float loadFactor;
    private final float multiplier;
    private final long noEntryValue;

    private int[] keys;
    private long[] values;
    private int size;
    private int limit;
    private boolean entryWithNullKeyIsAssociated;
    private long nullValue;

    public IntLongHashMapImpl(int initialCapacity, float loadFactor, float multiplier, long noEntryValue) {
        if (initialCapacity < 0 || initialCapacity > MAXIMUM_CAPACITY) {
            throw new IllegalArgumentException(String.format(ILLEGAL_INITIAL_CAPACITY_PATTERN, initialCapacity));
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
        nullValue = noEntryValue;

        keys = new int[initialCapacity];
        values = new long[initialCapacity];

        setCapacity(initialCapacity);
    }

    public IntLongHashMapImpl(int initialCapacity, float loadFactor, float multiplier) {
        this(initialCapacity, loadFactor, multiplier, NO_ENTRY_VALUE_DEFAULT_VALUE);
    }

    public IntLongHashMapImpl(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, MULTIPLIER_DEFAULT_VALUE);
    }

    public IntLongHashMapImpl(int initialCapacity) {
        this(initialCapacity, LOAD_FACTOR_DEFAULT_VALUE);
    }

    public IntLongHashMapImpl() {
        this(INIT_CAPACITY_DEFAULT_VALUE);
    }

    public long getNoEntryValue() {
        return noEntryValue;
    }

    private void setCapacity(int capacity) {
        this.capacity = capacity;
        this.limit = (int) (capacity * loadFactor);

        // <limit> should be LESS than "card-size", because method <getIndex>, on purpose not to use
        // "full keys circle", expects that there always should be at least one "empty"
        // (with 0-value) entry in keys array
        if (limit >= capacity) {              // ">=" is actually excess right here, but let's keep it
            limit = capacity - 1;
        }
    }

    // Hash code is using to determine index in hash table, so hash code should be positive
    private static int hashCode(int value) {
        return Integer.hashCode(value) & Integer.MAX_VALUE;
    }

    private int indexFor(int key) {
        return hashCode(key) % capacity;
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
        // With strong expectation that there always should be at least one "empty" (with 0-value) entry in keys array
        while (keys[index] != 0 && keys[index] != key) {
            index++;
            if (index >= capacity) {
                index = 0;
            }
        }
        if (keys[index] == key) {
            result = index;
        }

        return result;
    }

    /**
     * Returns the number of key-value mappings in map.
     *
     * @return the number of key-value mappings in map
     */
    @Override
    public int size() {
        return entryWithNullKeyIsAssociated ? size + 1 : size;
    }

    @Override
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
            // It is obligatory to do <reAllocTables> if the "card" is full BEFORE call of <getIndex> because
            // <getIndex>, on purpose not to use "full keys circle", expects that there should be at least one "empty"
            // (with 0-value) entry in keys array
            if (size >= limit) {
                reAllocTables();
            }

            int index = getIndex(key);
            if (index >= 0) {
                result = values[index];
                values[index] = value;
            } else {
                rawPut(key, value);
            }
        }

        return result;
    }

    @Override
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
}
