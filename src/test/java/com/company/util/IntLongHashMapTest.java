package com.company.util;

import com.company.util.data.RandomDataGenerator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Implemented by Yevhen Khomiak on 21.06.2017.
 */

public class IntLongHashMapTest {
    private static final int RANDOM_INT_UPPER_BOUND = 10000; // To have the ability to regulate "probability" of repetitive keys
    private static final int TEST_QUANTITY = 100000;

    private static final String SIZE_METHOD_SIGNATURE = "size()";
    private static final String PUT_METHOD_PATTERN = "put(%d, %d)";
    private static final String GET_METHOD_PATTERN = "get(%d)";
    private static final String METHOD_FAILED_PATTERN = "The method %s.%s failed:";
    private static final String TESTING_ENTRY_PATTERN = "The testing entry N %d is: key: %d, value: %d";
    private static final String EMPTY_CARD_TEST_IS_OK = "Empty card test is ok!";
    private static final String NULL_KEY_TEST_IS_OK = "Null key test is ok!";
    private static final String NO_ENTRY_VALUE_TEST_IS_OK = "No entry value test is ok!";
    private static final String THRESHOLD_KEYS_TEST_IS_OK = "Threshold keys test is ok!";

    private static Map<Integer, Long> expectedValueSupplier;
    private static IntLongHashMap intLongHashMap;
    private static long noEntryValue;

    // To have the ability to use predetermined "etalon" keys
//    private int[] predeterminedKeys = {350, -746, -147, -346, 92, -902, -274, -530, 127, -20, -122, -543, 85, -981, -409, -374, 32};
    private int[] preDeterminedKeys = {};

//    @Rule
//    public TestRule benchmarkRun = new BenchmarkRule();

    @BeforeClass
    public static void setUpClass() throws Exception {
        expectedValueSupplier = new HashMap<>();
        intLongHashMap = new IntLongHashMapOpenAddr();
        noEntryValue = intLongHashMap.getNoEntryValue();
    }

    private int getRandomInteger() {
        return RandomDataGenerator.getRandomInteger(RANDOM_INT_UPPER_BOUND);
    }

    private long getRandomLong() {
        return RandomDataGenerator.getRandomLong();
    }

    private String getIncorrectMethodResultMessage(String methodName) {
        return String.format(METHOD_FAILED_PATTERN, intLongHashMap.getClass().getName(), methodName);
    }

    private String getIncorrectGetMethodResultMessage(int key) {
        return getIncorrectMethodResultMessage(String.format(GET_METHOD_PATTERN, key));
    }

    private String getIncorrectPutMethodResultMessage(int key, long value) {
        return getIncorrectMethodResultMessage(String.format(PUT_METHOD_PATTERN, key, value));
    }

    private void sizeTest() {
        assertEquals(getIncorrectMethodResultMessage(SIZE_METHOD_SIGNATURE),
                expectedValueSupplier.size(), intLongHashMap.size());
    }

    private void getTest(int key) {
        Long expectedValue = expectedValueSupplier.get(key);

        assertEquals(getIncorrectGetMethodResultMessage(key), (expectedValue == null) ?
                noEntryValue : expectedValue, intLongHashMap.get(key));
    }

    private void putTest(int key, long value) {
        Long expectedPreviousValue = expectedValueSupplier.put(key, value);

        assertEquals(getIncorrectPutMethodResultMessage(key, value),
                (expectedPreviousValue == null) ? noEntryValue : expectedPreviousValue, intLongHashMap.put(key, value));
    }

    private void emptyCardTest() throws Exception {
        assertEquals(getIncorrectMethodResultMessage(SIZE_METHOD_SIGNATURE), 0, intLongHashMap.size());

        int key = getRandomInteger();
        assertEquals(getIncorrectGetMethodResultMessage(key), noEntryValue, intLongHashMap.get(key));

        System.out.println(EMPTY_CARD_TEST_IS_OK);
    }

    private void nullKeyTest() throws Exception {
        int nullKey = 0;
        long value = getRandomLong();

        expectedValueSupplier.put(nullKey, value); // Important to synchronize expectedValueSupplier.size() and intLongHashMap.size()
        intLongHashMap.put(nullKey, value);
        assertEquals(getIncorrectGetMethodResultMessage(nullKey), value, intLongHashMap.get(nullKey));

        System.out.println(NULL_KEY_TEST_IS_OK);
    }

    private void thresholdKeysTest() {
        int thresholdKey = Integer.MIN_VALUE;
        long value = getRandomLong();
        expectedValueSupplier.put(thresholdKey, value); // Important to synchronize expectedValueSupplier.size() and intLongHashMap.size()
        intLongHashMap.put(thresholdKey, value);
        assertEquals(getIncorrectGetMethodResultMessage(thresholdKey), value, intLongHashMap.get(thresholdKey));

        thresholdKey = Integer.MAX_VALUE;
        value = getRandomLong();
        expectedValueSupplier.put(thresholdKey, value); // Important to synchronize expectedValueSupplier.size() and intLongHashMap.size()
        intLongHashMap.put(thresholdKey, value);
        assertEquals(getIncorrectGetMethodResultMessage(thresholdKey), value, intLongHashMap.get(thresholdKey));

        System.out.println(THRESHOLD_KEYS_TEST_IS_OK);
    }

    private void allMethodTest() throws Exception {
        int key;
        long value;

        for (int index = 0; index < TEST_QUANTITY; index++) {
            key = (index < preDeterminedKeys.length) ? preDeterminedKeys[index] : getRandomInteger();
            value = getRandomLong();

            System.out.println(String.format(TESTING_ENTRY_PATTERN, index, key, value));

            sizeTest();
            getTest(key);
            putTest(key, value);
            sizeTest();
            getTest(key);
        }

        // Additional "only-get" test for "filled" HashMap
        for (int i = 0; i < TEST_QUANTITY; i++) {
            getTest(getRandomInteger());
        }
    }

    private void sizePutGetNoEntryValueTest() throws Exception {
        int key = getRandomInteger();
        long value = noEntryValue;

        getTest(key);
        putTest(key, value);
        sizeTest();
        getTest(key);

        System.out.println(NO_ENTRY_VALUE_TEST_IS_OK);
    }

    @Test(timeout = 5000)
    public void sizePutGetTest() throws Exception {
        // All the tests - in one "main" test-method in order to simplify test order achievement

        // Empty card test
        emptyCardTest();

        // No entry value test
        sizePutGetNoEntryValueTest();

        // Null key test
        nullKeyTest();

        // Threshold keys test
        thresholdKeysTest();

        // The whole functionality tests
        allMethodTest();

        // No entry value test again
        sizePutGetNoEntryValueTest();

        // Null key test again
        nullKeyTest();

        // Threshold keys test again
        thresholdKeysTest();
    }
}