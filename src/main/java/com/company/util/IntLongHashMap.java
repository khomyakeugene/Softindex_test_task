package com.company.util;

/**
 * Created by ekhomiak on 21.06.2017.
 */
public interface IntLongHashMap {
    long getNoEntryValue();

    long put(int key, long value);

    long get(int key);

    int size();
}
