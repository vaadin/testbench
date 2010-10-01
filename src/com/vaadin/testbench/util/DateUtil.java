package com.vaadin.testbench.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {

    /**
     * Replaces date functions with date values. Supported date functions are:
     * <p>
     * DATE(format, offset)
     * </p>
     * <p>
     * In format "d"/"dd"/"M"/"MM"/"yy"/"yyyy" are replaced with the day of
     * month, month, year respectively. The date used is current date offset
     * according to the offset string.
     * </p>
     * 
     * @param value
     *            The string where date functions should be replaced
     * @param referenceDate
     *            The date to use for reference (offset is added/removed from
     *            this date). Typically current date.
     * @return The value parameter with DATE(format,offset) strings replaced.
     */
    public static String replaceDateFunctions(String value, Date referenceDate)
            throws IllegalArgumentException {
        String pattern = "DATE\\(([^\\)]*)\\)";

        Matcher m = Pattern.compile(pattern).matcher(value);
        while (m.find()) {
            String replacement = createDateReplacementString(m.group(1),
                    referenceDate);
            if (replacement != null) {
                value = m.replaceAll(replacement);
                m = Pattern.compile(pattern).matcher(value);
            }
        }

        return value;
    }

    /**
     * Returns the string the DATE(parameterString) function should be replaced
     * with.
     * 
     * @param parameterString
     * @param referenceDate
     * @return The string to replace with or null if there is an error in the
     *         parameters and nothing should be replaced.
     */
    private static String createDateReplacementString(String parameterString,
            Date referenceDate) throws IllegalArgumentException {
        String[] parameters = parameterString.split(",");

        if (parameters.length == 1) {
            return formatDateReplacement(parameters[0], referenceDate);
        } else {
            return formatDateReplacement(parameters[0],
                    parseOffset(referenceDate, parameters[1]));

        }
    }

    private static String formatDateReplacement(String pattern, Date d) {
        return new SimpleDateFormat(pattern).format(d);
    }

    /**
     * Parses the given offsetString into an offset in ms. The offsetString may
     * contain one or more "(+-)number(D|M|Y)" type String. The plus/minus tells
     * if the offset is after or before the current date, the last char is the
     * unit (Days/Months/Years) and the number tells how many "units" the offset
     * should be. For example: -1D == yesterday -1M == one month back -1D-1M ==
     * yesterday but one month back
     * 
     * If the date refers to an invalid date, e.g. "31.3.2010 -1M" an
     * IllegalArgumentException will be thrown.
     * 
     * @param offsetString
     * @return
     */
    private static final String offsetPattern = "([+-])(\\d+)(D|M|Y)";

    private static Date parseOffset(Date referenceDate, String offsetString)
            throws IllegalArgumentException {
        Calendar c = Calendar.getInstance();
        c.setTime(referenceDate);
        c.setLenient(false);

        Pattern p = Pattern.compile(offsetPattern);
        Matcher m = p.matcher(offsetString);
        while (m.find()) {

            // Factor to multiply by to get correct offset
            int plusminus = m.group(1).equals("-") ? -1 : 1;
            int number = Integer.parseInt(m.group(2));
            String unit = m.group(3);

            if (unit.equalsIgnoreCase("D")) {
                c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)
                        + plusminus * number);
            } else if (unit.equalsIgnoreCase("M")) {
                c.set(Calendar.MONTH, c.get(Calendar.MONTH) + plusminus
                        * number);
            } else if (unit.equalsIgnoreCase("Y")) {
                c.set(Calendar.YEAR, c.get(Calendar.YEAR) + plusminus * number);
            }
        }

        // Only at this point the time is actually converted and the date needs
        // to be valid. It therefore does not matter in which order the various
        // fields are set.
        return c.getTime();
    }

}
