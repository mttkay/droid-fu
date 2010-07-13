package com.github.droidfu.support;

import java.lang.reflect.Array;

public class ArraySupport {

    @SuppressWarnings("unchecked")
    public static <T> T[] join(T[] head, T[] tail) {
        if (head == null) {
            return tail;
        }
        if (tail == null) {
            return head;
        }
        Class<?> type = head.getClass().getComponentType();
        T[] result = (T[]) Array.newInstance(type, head.length + tail.length);

        System.arraycopy(head, 0, result, 0, head.length);
        System.arraycopy(tail, 0, result, head.length, tail.length);

        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] delete(T[] array, int index) {
        int length = array.length;
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }

        T[] result = (T[]) Array.newInstance(array.getClass().getComponentType(), length - 1);
        System.arraycopy(array, 0, result, 0, index);
        if (index < length - 1) {
            System.arraycopy(array, index + 1, result, index, length - index - 1);
        }

        return result;
    }

}
