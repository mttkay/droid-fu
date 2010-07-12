package com.github.droidfu.support;

import static org.junit.Assert.assertArrayEquals;

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

}
