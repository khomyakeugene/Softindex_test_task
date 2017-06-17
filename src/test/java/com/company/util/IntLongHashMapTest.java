package com.company.util;

import com.company.util.data.RandomDataGenerator;
import org.junit.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by ekhomiak on 16.06.2017.
 */
public class IntLongHashMapTest {
    private static final int RANDOM_INT_UPPER_BOUND = 1000; // To have the possibility to regulate "probability" of repetitive keys
    private static final int TEST_QUANTITY = 1000;

    private static final String SIZE_METHOD_SIGNATURE = "size()";
    private static final String PUT_METHOD_PATTERN = "put(%d, %d)";
    private static final String GET_METHOD_PATTERN = "get(%d)";
    private static final String METHOD_FAILED_PATTERN = "The method %s.%s failed:";

    protected Map<Integer, Long> expectedValueSupplier = new HashMap<>();
    protected IntLongHashMap intLongHashMap = new IntLongHashMap();
    private long noEntryValue = intLongHashMap.getNoEntryValue();

    protected int getRandomInteger() {
        return RandomDataGenerator.getRandomInteger(RANDOM_INT_UPPER_BOUND);
    }

    protected long getRandomLong() {
        return RandomDataGenerator.getRandomLong();
    }

    private String getIncorrectMethodResultMessage(String methodName) {
        return String.format(METHOD_FAILED_PATTERN, intLongHashMap.getClass().getName(), methodName);
    }

    private void sizeTest() {
        assertEquals(getIncorrectMethodResultMessage(SIZE_METHOD_SIGNATURE),
                expectedValueSupplier.size(), intLongHashMap.size());
    }

    private void getTest(int key) {
        Long expectedValue = expectedValueSupplier.get(key);

        assertEquals(getIncorrectMethodResultMessage(String.format(GET_METHOD_PATTERN, key)), (expectedValue == null) ?
                noEntryValue : expectedValue, intLongHashMap.get(key));
    }

    private void putTest(int key, long value) {
        Long expectedPreviousValue = expectedValueSupplier.put(key, value);

        assertEquals(getIncorrectMethodResultMessage(String.format(PUT_METHOD_PATTERN, key, value)),
                (expectedPreviousValue == null) ? noEntryValue : expectedPreviousValue, intLongHashMap.put(key, value));
    }

    @Test(timeout = 1000)
    public void sizePutGetTest() throws Exception {
        for (int i = 0; i < TEST_QUANTITY; i++) {
            int key = getRandomInteger();
            long value = getRandomLong();

            sizeTest();
            getTest(key);
            putTest(key, value);
            sizeTest();
            getTest(key);
        }
    }
}