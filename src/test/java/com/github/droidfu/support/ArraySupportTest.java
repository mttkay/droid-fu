package com.github.droidfu.support;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ArraySupportTest {

    @Test
    public void joinArrays() {
        String[] one = { "a", "b" };
        String[] two = { "c" };

        assertArrayEquals(new String[] { "a", "b", "c" }, ArraySupport.join(one, two));
        assertArrayEquals(new String[] { "c", "a", "b" }, ArraySupport.join(two, one));
        assertArrayEquals(one, ArraySupport.join(one, null));
        assertArrayEquals(two, ArraySupport.join(null, two));
    }

    @Test
    public void deleteElement() {
        String[] array = { "a", "b", "c" };

        assertEquals(2, ArraySupport.delete(array, 0).length);

        assertArrayEquals(new String[] { "b", "c" }, ArraySupport.delete(array, 0));
        assertArrayEquals(new String[] { "a", "c" }, ArraySupport.delete(array, 1));
        assertArrayEquals(new String[] { "a", "b" }, ArraySupport.delete(array, 2));

    }

    @Test
    public void findElement() {
        String[] array = { "a", "b", "c" };

        assertEquals(-1, ArraySupport.find(array, "x"));
        assertEquals(0, ArraySupport.find(array, "a"));
        assertEquals(1, ArraySupport.find(array, "b"));
        assertEquals(2, ArraySupport.find(array, "c"));
    }
}
