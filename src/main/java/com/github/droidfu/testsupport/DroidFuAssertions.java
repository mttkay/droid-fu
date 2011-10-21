package com.github.droidfu.testsupport;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

public class DroidFuAssertions {

    private static boolean junit4 = true;

    static {
        try {
            Class.forName("org.junit.Assert");
        } catch (ClassNotFoundException e) {
            System.out.println("JUnit4 not found on class path, will use JUnit3 assertions!");
            junit4 = false;
        }
    }

    public static void useJUnit3() {
        junit4 = false;
    }

    private static void assertTrue(String message, boolean condition) {
        if (junit4) {
            org.junit.Assert.assertTrue(message, condition);
        } else {
            junit.framework.Assert.assertTrue(message, condition);
        }
    }

    private static void assertEquals(String message, Object expected, Object actual) {
        if (junit4) {
            org.junit.Assert.assertEquals(message, expected, actual);
        } else {
            junit.framework.Assert.assertEquals(message, expected, actual);
        }
    }

    public static <E> void assertEqualElements(Collection<E> expected, Collection<E> actual) {
        expected.removeAll(actual);
        assertTrue("collections expected to contain the same elements, but didn't",
            expected.isEmpty());
    }

    /**
     * Asserts that two dates are equal down to the granularity of a day. This
     * assertion ignores the time component entirely, i.e. two dates are
     * considered equal even if the times of day differ.
     *
     * @param expected
     * @param actual
     */
    public static void assertDateEquals(Date expected, Date actual) {
        Calendar expectedCal = Calendar.getInstance();
        expectedCal.setTime(expected);
        Calendar actualCal = Calendar.getInstance();
        actualCal.setTime(actual);

        int expectedYear = expectedCal.get(Calendar.YEAR);
        int actualYear = actualCal.get(Calendar.YEAR);
        assertEquals("expected year to be " + expectedYear + ", but was " + actualYear,
            expectedYear, actualYear);

        int expectedMonth = expectedCal.get(Calendar.MONTH);
        int actualMonth = actualCal.get(Calendar.MONTH);
        assertEquals("expected month to be " + expectedMonth + ", but was " + actualMonth,
            expectedMonth, actualMonth);

        int expectedDay = expectedCal.get(Calendar.DAY_OF_MONTH);
        int actualDay = actualCal.get(Calendar.DAY_OF_MONTH);
        assertEquals("expected day to be " + expectedDay + ", but was " + actualDay, expectedDay,
            actualDay);
    }

    /**
     * Asserts that two dates are equal down to the granularity of a second.
     * This assertion ignores milliseconds, so two dates will be considered
     * equal even if their milliseconds fractions differ.
     *
     * @param expected
     * @param actual
     */
    public static void assertTimeEquals(Date expected, Date actual) {
        assertDateEquals(expected, actual);

        Calendar expectedCal = Calendar.getInstance();
        expectedCal.setTime(expected);
        Calendar actualCal = Calendar.getInstance();
        actualCal.setTime(actual);

        int expectedHour = expectedCal.get(Calendar.HOUR_OF_DAY);
        int actualHour = actualCal.get(Calendar.HOUR_OF_DAY);
        assertEquals("expected hour to be " + expectedHour + ", but was " + actualHour,
            expectedHour, actualHour);

        int expectedMinute = expectedCal.get(Calendar.MINUTE);
        int actualMinute = actualCal.get(Calendar.MINUTE);
        assertEquals("expected minute to be " + expectedMinute + ", but was " + actualMinute,
            expectedMinute, actualMinute);

        int expectedSecond = expectedCal.get(Calendar.SECOND);
        int actualSecond = actualCal.get(Calendar.SECOND);
        assertEquals("expected second to be " + expectedSecond + ", but was " + actualSecond,
            expectedSecond, actualSecond);
    }

}
