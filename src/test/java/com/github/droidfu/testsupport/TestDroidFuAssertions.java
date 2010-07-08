package com.github.droidfu.testsupport;

import java.util.LinkedList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.github.droidfu.testsupport.DroidFuAssertions;

@RunWith(JUnit4.class)
public class TestDroidFuAssertions {

    @Test(expected = AssertionError.class)
    public void assertEqualElementsShouldFailIfNotEqualElements() {
        LinkedList<Integer> expected = new LinkedList<Integer>();
        expected.add(1);
        expected.add(2);

        LinkedList<Integer> actual = new LinkedList<Integer>();
        actual.add(1);

        DroidFuAssertions.assertEqualElements(expected, actual);
    }

    @Test
    public void assertEqualElementsWithSameInsertionOrder() {
        LinkedList<Integer> expected = new LinkedList<Integer>();
        expected.add(1);
        expected.add(2);

        LinkedList<Integer> actual = new LinkedList<Integer>();
        actual.add(1);
        actual.add(2);

        DroidFuAssertions.assertEqualElements(expected, actual);
    }

    @Test
    public void assertEqualElementsWithInverseInsertionOrder() {
        LinkedList<Integer> expected = new LinkedList<Integer>();
        expected.add(1);
        expected.add(2);

        LinkedList<Integer> actual = new LinkedList<Integer>();
        actual.add(2);
        actual.add(1);

        DroidFuAssertions.assertEqualElements(expected, actual);
    }
}
