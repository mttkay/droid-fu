package com.github.droidfu.testsupport;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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

    @Test(expected = AssertionError.class)
    public void assertEqualDatesShouldFailIfYearNotEqual() {
        Calendar cal = Calendar.getInstance();
        cal.set(2010, Calendar.JANUARY, 1, 12, 0, 0);
        Date expected = cal.getTime();
        cal.set(2011, Calendar.JANUARY, 1, 12, 0, 0);
        Date actual = cal.getTime();

        DroidFuAssertions.assertDateEquals(expected, actual);
    }

    @Test(expected = AssertionError.class)
    public void assertEqualDatesShouldFailIfMonthNotEqual() {
        Calendar cal = Calendar.getInstance();
        cal.set(2010, Calendar.JANUARY, 1, 12, 0, 0);
        Date expected = cal.getTime();
        cal.set(2010, Calendar.FEBRUARY, 1, 12, 0, 0);
        Date actual = cal.getTime();

        DroidFuAssertions.assertDateEquals(expected, actual);
    }

    @Test(expected = AssertionError.class)
    public void assertEqualDatesShouldFailIfDayNotEqual() {
        Calendar cal = Calendar.getInstance();
        cal.set(2010, Calendar.JANUARY, 1, 12, 0, 0);
        Date expected = cal.getTime();
        cal.set(2010, Calendar.JANUARY, 2, 12, 0, 0);
        Date actual = cal.getTime();

        DroidFuAssertions.assertDateEquals(expected, actual);
    }

    @Test
    public void assertEqualDates() {
        Calendar cal = Calendar.getInstance();
        cal.set(2010, Calendar.JANUARY, 1, 12, 0, 0);
        Date expected = cal.getTime();
        cal.set(2010, Calendar.JANUARY, 1, 13, 1, 1);
        Date actual = cal.getTime();

        DroidFuAssertions.assertDateEquals(expected, actual);
    }

    @Test(expected = AssertionError.class)
    public void assertEqualTimesShouldFailIfYearNotEqual() {
        Calendar cal = Calendar.getInstance();
        cal.set(2010, Calendar.JANUARY, 1, 12, 0, 0);
        Date expected = cal.getTime();
        cal.set(2011, Calendar.JANUARY, 1, 12, 0, 0);
        Date actual = cal.getTime();

        DroidFuAssertions.assertTimeEquals(expected, actual);
    }

    @Test(expected = AssertionError.class)
    public void assertEqualTimesShouldFailIfMonthNotEqual() {
        Calendar cal = Calendar.getInstance();
        cal.set(2010, Calendar.JANUARY, 1, 12, 0, 0);
        Date expected = cal.getTime();
        cal.set(2010, Calendar.FEBRUARY, 1, 12, 0, 0);
        Date actual = cal.getTime();

        DroidFuAssertions.assertTimeEquals(expected, actual);
    }

    @Test(expected = AssertionError.class)
    public void assertEqualTimesShouldFailIfDayNotEqual() {
        Calendar cal = Calendar.getInstance();
        cal.set(2010, Calendar.JANUARY, 1, 12, 0, 0);
        Date expected = cal.getTime();
        cal.set(2010, Calendar.JANUARY, 2, 12, 0, 0);
        Date actual = cal.getTime();

        DroidFuAssertions.assertTimeEquals(expected, actual);
    }

    @Test(expected = AssertionError.class)
    public void assertEqualTimesShouldFailIfHourNotEqual() {
        Calendar cal = Calendar.getInstance();
        cal.set(2010, Calendar.JANUARY, 1, 12, 0, 0);
        Date expected = cal.getTime();
        cal.set(2011, Calendar.JANUARY, 1, 13, 0, 0);
        Date actual = cal.getTime();

        DroidFuAssertions.assertTimeEquals(expected, actual);
    }

    @Test(expected = AssertionError.class)
    public void assertEqualTimesShouldFailIfMinuteNotEqual() {
        Calendar cal = Calendar.getInstance();
        cal.set(2010, Calendar.JANUARY, 1, 12, 0, 0);
        Date expected = cal.getTime();
        cal.set(2011, Calendar.JANUARY, 1, 12, 1, 0);
        Date actual = cal.getTime();

        DroidFuAssertions.assertTimeEquals(expected, actual);
    }

    @Test(expected = AssertionError.class)
    public void assertEqualTimesShouldFailIfSecondsNotEqual() {
        Calendar cal = Calendar.getInstance();
        cal.set(2010, Calendar.JANUARY, 1, 12, 0, 0);
        Date expected = cal.getTime();
        cal.set(2011, Calendar.JANUARY, 1, 12, 0, 1);
        Date actual = cal.getTime();

        DroidFuAssertions.assertTimeEquals(expected, actual);
    }

    @Test
    public void assertEqualTimes() {
        Calendar cal = Calendar.getInstance();
        cal.set(2010, Calendar.JANUARY, 1, 12, 0, 0);

        Date expected = cal.getTime();
        Date actual = new Date(cal.getTime().getTime() + 50); // 50ms deviation

        DroidFuAssertions.assertTimeEquals(expected, actual);
    }
}
