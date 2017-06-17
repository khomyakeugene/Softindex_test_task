package com.company.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by ekhomiak on 15.06.2017.
 */
public class IntegerLongHashMap extends IntLongHashMap implements Map<Integer, Long> {
    private static final Long NO_ENTRY_VALUE = null;

    //------------- Temporary inner class (deliberately not outer) and "infrastructure" just to support informing that
    // there are some not implemented functionality
    private class ReflectionService {
        private final static int NUMBER_OF_NECESSARY_CALLING_ELEMENT_OF_STACK_AS_METHOD_NUMBER = 3;
        private static final String METHOD_IS_NOT_IMPLEMENTED_YET_PATTERN = "The method <%s.%s> is not implemented yet!";

        String getMethodName() {
            return Thread.currentThread().getStackTrace()[
                    NUMBER_OF_NECESSARY_CALLING_ELEMENT_OF_STACK_AS_METHOD_NUMBER].getMethodName();
        }


        private void methodIsNotImplementedException(String methodName) {
            throw new RuntimeException(String.format(METHOD_IS_NOT_IMPLEMENTED_YET_PATTERN, IntegerLongHashMap.this.getClass().getName(), methodName));
        }

        void methodIsNotImplementedException() {
            methodIsNotImplementedException(getMethodName());
        }
    }
    private ReflectionService reflectionService = new ReflectionService();
    //-------------


    @Override
    public int size() {
        return super.size();
    }

    @Override
    public boolean isEmpty() {
        reflectionService.methodIsNotImplementedException();

        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        reflectionService.methodIsNotImplementedException();

        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        reflectionService.methodIsNotImplementedException();

        return false;
    }

    @Override
    public Long get(Object key) {
        Long value = super.get((Integer)key);
        if (value == getNoEntryValue()) {
            value = NO_ENTRY_VALUE;
        }

        return value;
    }

    @Override
    public Long put(Integer key, Long value) {
        Long previousValue = super.put(key, value);
        if (previousValue == getNoEntryValue()) {
            previousValue = NO_ENTRY_VALUE;
        }

        return previousValue;
    }

    @Override
    public Long remove(Object key) {
        reflectionService.methodIsNotImplementedException();

        return null;
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends Long> m) {
        reflectionService.methodIsNotImplementedException();

    }

    @Override
    public void clear() {
        reflectionService.methodIsNotImplementedException();

    }

    @Override
    public Set<Integer> keySet() {
        reflectionService.methodIsNotImplementedException();

        return null;
    }

    @Override
    public Collection<Long> values() {
        reflectionService.methodIsNotImplementedException();

        return null;
    }

    @Override
    public Set<Entry<Integer, Long>> entrySet() {
        reflectionService.methodIsNotImplementedException();

        return null;
    }
}
