package com.company.util;

/**
 * Implemented by Yevhen Khomiak on 21.06.2017.
 */

public interface IntLongHashMap {
    /**
     * Returns the value that will be returned from {@link #get(int)} or {@link #put(int, long)} if no entry exists for a given key.
     *
     * @return the value that represents a null value in this map
     */
    long getNoEntryValue();

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
    long get(int key);

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
    long put(int key, long value);

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    int size();

}
