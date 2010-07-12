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
        Class type = head.getClass().getComponentType();
        T[] array = (T[]) Array.newInstance(type, head.length + tail.length);

        System.arraycopy(head, 0, array, 0, head.length);
        System.arraycopy(tail, 0, array, head.length, tail.length);

        return (T[]) array;
    }

}
