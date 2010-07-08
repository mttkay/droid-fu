package com.github.droidfu.testsupport;

import static org.junit.Assert.assertTrue;

import java.util.Collection;

public class DroidFuAssertions {

    public static <E> void assertEqualElements(Collection<E> expected, Collection<E> actual) {
        expected.removeAll(actual);
        assertTrue(expected.isEmpty());
    }

}
