package com.github.droidfu.support;


public class ArraySupport {

    @SuppressWarnings("unchecked")
    public static <T> T[] join(T[] one, T[] two) {
        if (one == null) {
            return two;
        }
        if (two == null) {
            return one;
        }
        Object[] array = new Object[one.length + two.length];

        System.arraycopy(one, 0, array, 0, one.length);
        System.arraycopy(two, 0, array, one.length, two.length);

        return (T[]) array;
    }

}
