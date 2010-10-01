package com.vaadin.testbench.tests;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import com.vaadin.testbench.util.DateUtil;

public class DateUtilTest extends TestCase {

    private static final int DAY_IN_MS = 24 * 3600 * 1000;

    private static final String[] prefixes = new String[] { "Prefix ", null };
    private static final String[] suffixes = new String[] { "Suffix", null };

    String[] patterns = new String[] { "d.MM.yy", "dd.MM.yyyy", "d", "dd", "M",
            "MM", "yy", "yyyy" };

    @SuppressWarnings("deprecation")
    Date referenceDate = new Date(2010 - 1900, 10 - 1, 13);

    @SuppressWarnings("deprecation")
    Date prevMonth = new Date(referenceDate.getYear(),
            referenceDate.getMonth() - 1, referenceDate.getDate());

    @SuppressWarnings("deprecation")
    Date nextMonth = new Date(referenceDate.getYear(),
            referenceDate.getMonth() + 1, referenceDate.getDate());

    @SuppressWarnings("deprecation")
    Date nextYear = new Date(referenceDate.getYear() + 1,
            referenceDate.getMonth(), referenceDate.getDate());

    @SuppressWarnings("deprecation")
    Date prevYear = new Date(referenceDate.getYear() - 1,
            referenceDate.getMonth(), referenceDate.getDate());

    public void testDateReplacement() {
        test(new Date(referenceDate.getTime()), null);
        test(new Date(referenceDate.getTime()), "-10D+10D");

        test(new Date(referenceDate.getTime() - DAY_IN_MS), "-1D");
        test(new Date(referenceDate.getTime() - 2 * DAY_IN_MS), "-2D");

        test(new Date(referenceDate.getTime() - 7 * DAY_IN_MS), "-7D");

        test(new Date(prevMonth.getTime()), "-1M");
        test(new Date(prevMonth.getTime() + DAY_IN_MS), "-1M+1D");
        test(new Date(nextMonth.getTime()), "+1M");
        test(new Date(nextMonth.getTime() - DAY_IN_MS), "+1M-1D+1Y-2Y+1Y");

        test(new Date(nextYear.getTime()), "+1Y");
    }

    private void test(Date date, String offsetString) {
        for (String pattern : patterns) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            for (String prefix : prefixes) {
                for (String suffix : suffixes) {
                    String expected = prefix + sdf.format(date) + suffix;
                    String returned = DateUtil.replaceDateFunctions(prefix
                            + "DATE(" + pattern + "," + offsetString + ")"
                            + suffix, referenceDate);
                    assertEquals(expected, returned);
                }
            }
        }
    }

}
