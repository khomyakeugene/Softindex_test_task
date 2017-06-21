package com.company.util;

/**
 * Implemented by Yevhen Khomiak on 21.06.2017.
 */

public class IntLongHashMapOpenAddr implements IntLongHashMap {
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

    /**
     * Constructs an empty <b>IntLongHashMapOpenAddr</b> with the specified initial
     * capacity, load factor, multiplier and long value that represents a null value in this map.
     *
     * @param  initialCapacity the initial capacity
     * @param  loadFactor      the load factor
     * @param  multiplier      how many times will increase the card capacity when rebuilding
     * @param  noEntryValue    a long value that represents a null value in this map
     * @throws IllegalArgumentException if the initial capacity is less than <b>1</b> or more than <b>1073741824</b>
     *         or the load factor is non-positive or the load factor is more than <b>1.0</b> or multiplier is non-positive
     */
    public IntLongHashMapOpenAddr(int initialCapacity, float loadFactor, float multiplier, long noEntryValue) {
        if (initialCapacity < 1 || initialCapacity > MAXIMUM_CAPACITY) {
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

    /**
     * Constructs an empty <b>IntLongHashMapOpenAddr</b> with the specified initial
     * capacity, load factor and multiplier and the default long value that represents a null value in
     * this map (<b>-9223372036854775808</b>).
     *
     * @param  initialCapacity the initial capacity
     * @param  loadFactor      the load factor
     * @param  multiplier       how many times will increase the card capacity when rebuilding
     * @throws IllegalArgumentException if the initial capacity is less than <b>1</b> or more than <b>1073741824</b>
     *         or the load factor is non-positive or the load factor is more than <b>1.0</b> or multiplier is non-positive
     */
    public IntLongHashMapOpenAddr(int initialCapacity, float loadFactor, float multiplier) {
        this(initialCapacity, loadFactor, multiplier, NO_ENTRY_VALUE_DEFAULT_VALUE);
    }

    /**
     * Constructs an empty <b>IntLongHashMapOpenAddr</b> with the specified initial capacity and load factor and
     * the default multiplier (<b>2.0</b>) and default long value that represents a null value in
     * this map (<b>-9223372036854775808</b>).
     *
     * @param  initialCapacity the initial capacity
     * @param  loadFactor      the load factor
     * @throws IllegalArgumentException if the initial capacity is less than <b>1</b> or more than <b>1073741824</b>
     *         or the load factor is non-positive or the load factor is more than <b>1.0</b>
     */
    public IntLongHashMapOpenAddr(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, MULTIPLIER_DEFAULT_VALUE);
    }

    /**
     * Constructs an empty <b>IntLongHashMapOpenAddr</b> with the specified initial capacity,
     * the default load factor (<b>0.75</b>), default multiplier (<b>2.0</b>) and default
     * long value that represents a null value in this map (<b>-9223372036854775808</b>).
     *
     * @param  initialCapacity the initial capacity
     * @throws IllegalArgumentException if the initial capacity is less than <b>1</b> or more than <b>1073741824</b>
     */
    public IntLongHashMapOpenAddr(int initialCapacity) {
        this(initialCapacity, LOAD_FACTOR_DEFAULT_VALUE);
    }

    /**
     * Constructs an empty <b>IntLongHashMapOpenAddr</b> with the default initial capacity (<b>16</b>),
     * default load factor (<b>0.75</b>), default multiplier (<b>2.0</b>) and default
     * long value that represents a null value in this map (<b>-9223372036854775808</b>).
     *
     */
    public IntLongHashMapOpenAddr() {
        this(INIT_CAPACITY_DEFAULT_VALUE);
    }

    /**
     * Returns the value that will be returned from {@link #get(int)} or {@link #put(int, long)} if no entry exists for a given key.
     * The default value is <b>-9223372036854775808</b>, but can be changed during construction of the map.
     *
     * @return the value that represents a null value in this map
     */
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
     * Returns the value to which the specified key is mapped,
     * or the value which is returned by {@link #getNoEntryValue()} if this map contains no mapping for the key.
     *
     * <p>A return {@link #getNoEntryValue()} value does not <i>necessarily</i>
     * indicate that the map contains no mapping for the key; it's also
     * possible that the map explicitly maps the key to {@link #getNoEntryValue()}.
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or
     *         {@link #getNoEntryValue()} if this map contains no mapping for the key
     * @see #getNoEntryValue
     * @see #put(int, long)
     */
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

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old value is replaced by the specified value.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with key, or
     *         {@link #getNoEntryValue()} if there was no mapping for key.
     *         (A {@link #getNoEntryValue()} return can also indicate that the map
     *         previously associated {@link #getNoEntryValue()} with key)
     * @see #getNoEntryValue
     * @see #get(int)
     */
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

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    @Override
    public int size() {
        return entryWithNullKeyIsAssociated ? size + 1 : size;
    }
}
