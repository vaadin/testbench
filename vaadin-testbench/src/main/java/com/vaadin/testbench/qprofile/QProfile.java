/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3.0>.
 */
package com.vaadin.testbench.qprofile;

import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * The Quick and Dirty Profiler
 * <p>
 * NOTE: This utility is currently for INTERNAL USE ONLY. Do NOT depend on it
 * for functionality. This utility might be removed in the future.
 */
public final class QProfile {

    //
    // Note to anyone reading this:
    // this utility is the result of roughly 8 hours of poking by one man, for
    // the purpose of optimizing server-side graphics analysis routines. The
    // code is crude, but has served its purpose. This profiler and references
    // to it have been left intact for possible future use.
    //

    private static class Result {
        String functionName;
        int callCount;
        double cumulativeTime;
        double averageTime;
    }

    private static class Frame {
        String name = "";
        long tm_entry = 0l;
        long tm_total = 0l;
    }

    private static class MethodData {
        long call_count = 0l;
        long total_time = 0l;
    }

    private static Stack<Frame> callStack = new Stack<Frame>();
    private static Map<String, MethodData> methods = new LinkedHashMap<String, MethodData>();

    private static long beginCount = 0l;
    private static long endCount = 0l;
    private static long profTime = 0l;

    private static boolean enabled = false;

    /**
     * Enable or disable all profiler functionality. The profiler is disabled by
     * default, and needs to be enabled manually to do anything.
     * 
     * @param b
     *            a boolean value
     */
    public static final void setEnabled(boolean b) {
        enabled = b;
    }

    /**
     * Check if profiler is enabled
     * 
     * @return a boolean value
     */
    public static final boolean isEnabled() {
        return enabled;
    }

    /**
     * Clear all profiling data and state
     */
    public static final void clear() {
        methods.clear();
        beginCount = 0l;
        endCount = 0l;
        profTime = 0l;
        callStack.clear();
    }

    /**
     * Extract canonical name of function from which the profiler was called
     */
    private static final String getCurrentMethodName() {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[3];
        return ste.getClassName() + "." + ste.getMethodName();
    }

    /**
     * Indicate that profiling the current function should begin here. Call
     * {@code QProfile.begin()} to start measuring time for a single run of a
     * function. Remember to call {@code QProfile.end()} before returning from
     * that same function; otherwise, you'll have rather interesting timing
     * results (or none at all).
     * <p>
     * The function and class name are gleaned automagically from the call
     * stack.
     */
    public static final void begin() {
        if (!enabled) {
            return;
        }

        long timestamp = System.nanoTime();
        beginCount++;

        // If we have frame lower down in the stack, mark it as 'exited' - i.e.,
        // since we're profiling a function inside of the function we're already
        // profiling, we don't want the inner function's time to contribute to
        // the total time of the outer's.
        if (!callStack.isEmpty()) {
            // Add already elapsed time to the earlier function's total time
            Frame prev_fn = callStack.peek();
            prev_fn.tm_total += timestamp - prev_fn.tm_entry;
        }

        Frame fn = new Frame();
        fn.name = getCurrentMethodName();
        fn.tm_entry = timestamp;
        callStack.push(fn);

        MethodData data = methods.get(fn.name);
        if (data == null) {
            data = new MethodData();
            methods.put(fn.name, data);
        }
        data.call_count++;

        // Time spent in profiler
        long ptime = System.nanoTime() - timestamp;
        profTime += ptime;
    }

    /**
     * Indicate that the function has performed its duties and is ready to
     * return and, therefore, should no longer be tracked. This function should
     * ALWAYS be called before the function returns if {@code QProfile.begin()}
     * has been called; otherwise, you'll get strange timing results, or none at
     * all.
     * <p>
     * The function and class name are gleaned automagically from the call
     * stack.
     */
    public static final void end() {
        if (!enabled) {
            return;
        }

        long timestamp = System.nanoTime();
        endCount++;

        Frame fn = callStack.pop();
        {
            String fname_current = getCurrentMethodName();
            if (!(fn.name.equals(fname_current))) {
                String msg = "!!! Error: exited from function " + fname_current
                        + "; expected " + fn.name;
                System.err.println(msg);
                throw new RuntimeException(msg);
            }
        }

        // Calculate elapsed time for this function, store it in method data
        fn.tm_total += timestamp - fn.tm_entry;
        MethodData data = methods.get(fn.name);
        assert (data != null);
        data.total_time += fn.tm_total;

        if (!callStack.isEmpty()) {
            // If we have a function lower down in the stack, mark it as
            // starting now. We've already added time to the tm_total variable
            // in an earlier begin() call
            Frame next = callStack.peek();
            next.tm_entry = System.nanoTime();
        }

        // Time spent in profiler
        long ptime = System.nanoTime() - timestamp;
        profTime += ptime;
    }

    /**
     * Generate and print a default report to stderr, with results ordered by
     * first call order. Any kind of visualization of nested calls is beyond the
     * scope of this project.
     */
    public static final void report() {
        if (!enabled) {
            return;
        }

        List<Result> results = prepareReport();
        writeReport(results);
    }

    /**
     * Generate and print a report to stderr, with results ordered by call count
     * (largest first)
     */
    public static final void reportByCallCount() {
        if (!enabled) {
            return;
        }

        List<Result> results = prepareReport();
        Collections.sort(results, new Comparator<Result>() {
            @Override
            public int compare(Result o1, Result o2) {
                return o2.callCount - o1.callCount;
            }
        });
        writeReport(results);
    }

    /**
     * Generate and print a report to stderr, with results ordered by cumulative
     * run time (largest first)
     */
    public static final void reportByCumulativeTime() {
        if (!enabled) {
            return;
        }

        List<Result> results = prepareReport();
        Collections.sort(results, new Comparator<Result>() {
            @Override
            public int compare(Result o1, Result o2) {
                return o2.cumulativeTime > o1.cumulativeTime ? 1
                        : o2.cumulativeTime == o1.cumulativeTime ? 0 : -1;
            }
        });
        writeReport(results);
    }

    /**
     * Generate and print a report to stderr, with results ordered by average
     * run time (largest first)
     */
    public static final void reportByAverageTime() {
        if (!enabled) {
            return;
        }

        List<Result> results = prepareReport();
        Collections.sort(results, new Comparator<Result>() {
            @Override
            public int compare(Result o1, Result o2) {
                return o2.averageTime - o1.averageTime > 0 ? 1
                        : (o2.averageTime == o1.averageTime) ? 0 : -1;
            }
        });
        writeReport(results);
    }

    /**
     * Prepare results for a report
     * 
     * @return an ArrayList containing Results in no particular order
     */
    private static final List<Result> prepareReport() {
        List<Result> results = new ArrayList<Result>();

        for (String key : methods.keySet()) {
            MethodData data = methods.get(key);
            Result r = new Result();
            r.functionName = key;
            r.callCount = (int) data.call_count;
            r.cumulativeTime = data.total_time / 1000000.0;
            if (r.cumulativeTime > 0) {
                r.averageTime = (r.cumulativeTime / r.callCount);
            } else {
                r.averageTime = 0;
            }
            results.add(r);
        }

        return results;
    }

    /**
     * Write a pretty report to stderr with an ordered list of Results.
     * 
     * @param list
     *            a List of Result objects
     */
    private static final void writeReport(List<Result> list) {

        // NOTE: While the output may be pretty, the code most certainly is not.
        // Feel free to refactor, optimize and prettify.

        if (endCount != beginCount) {
            System.err
                    .println("!!! WARNING: QProfile.begin()/end() call count mismatch !!!");
            System.err.println("Begin count: " + beginCount + ", end count: "
                    + endCount);
        }

        if (callStack.size() > 0) {
            System.err
                    .println("!!! WARNING: QProfile call stack is NOT empty!");
            System.err.println("Stack still contains " + callStack.size()
                    + " frames");
        }

        if (list.size() == 0) {
            System.err.println("\n----- QProfile report -----");
            System.err.println("No profiling data recorded.");
            System.err.println("===========================\n");
            return;
        }

        final String fname_prefix = "-> ";
        final String fname_suffix = "  ";

        final String ccount_prefix = "call count: ";
        final String ccount_suffix = "  ";

        final String ttime_prefix = "total time: ";
        final String ttime_suffix = " msec  ";

        final String atime_prefix = "average time: ";
        final String atime_suffix = " msec";

        int fname_length = 0;
        int ccount_length = 0;
        int ctime_length = 0;
        int atime_length = 0;
        int total_length = 0;

        int ccount_num_length = 0;
        int ctime_num_length = 0;
        int atime_num_length = 0;

        for (Result r : list) {
            String fname_str = fname_prefix + r.functionName + fname_suffix;
            String ccount_str = ccount_prefix + r.callCount + ccount_suffix;
            String ctime_str = ttime_prefix + num_trunc(r.cumulativeTime)
                    + ttime_suffix;
            String atime_str = atime_prefix + num_trunc(r.averageTime)
                    + atime_suffix;

            fname_length = max(fname_length, fname_str.length());
            ccount_length = max(ccount_length, ccount_str.length());
            ctime_length = max(ctime_length, ctime_str.length());
            atime_length = max(atime_length, atime_str.length());

            ccount_num_length = max(ccount_num_length,
                    ("" + r.callCount).length());
            ctime_num_length = max(ctime_num_length,
                    num_trunc(r.cumulativeTime).length());
            atime_num_length = max(atime_num_length, num_trunc(r.averageTime)
                    .length());
        }

        total_length = fname_length + ccount_length + ctime_length
                + atime_length;

        System.err.println("\n");
        String header = " QProfile report ";

        int header_pad_length = (total_length - header.length()) / 2;
        header = str_repeat("-", header_pad_length) + header
                + str_repeat("-", header_pad_length);
        System.err
                .println(header + (header.length() < total_length ? "-" : ""));

        double tm_total = 0;
        double tm_average = 0;
        double tm_prof = (profTime / 1000000.0);
        int fn_count = 0;
        for (Result r : list) {
            String fname_str = fname_prefix + r.functionName + fname_suffix;
            String ccount_str = ccount_prefix
                    + num_pad(r.callCount, ccount_num_length) + ccount_suffix;
            String ctime_str = ttime_prefix
                    + num_pad(r.cumulativeTime, ctime_num_length)
                    + ttime_suffix;
            String atime_str = atime_prefix
                    + num_pad(r.averageTime, atime_num_length) + atime_suffix;
            tm_total += r.cumulativeTime;
            tm_average += r.averageTime;
            fn_count++;

            StringBuilder b = new StringBuilder();
            b.append(str_pad(fname_str, fname_length));
            b.append(str_pad(ccount_str, ccount_length));
            b.append(str_pad(ctime_str, ctime_length));
            b.append(str_pad(atime_str, atime_length));
            System.err.println(b.toString());
        }
        tm_average /= fn_count;

        String averageTimeMsg = "Average tracked function time: ";
        String totalTimeMsg = "Total cumulative time: ";
        String profilerTimeMsg = "Time spent in profiler: ";
        int maxEndMsgLength = max(totalTimeMsg.length(),
                max(averageTimeMsg.length(), profilerTimeMsg.length()));

        int maxEndValueLength = max(
                num_trunc(tm_total).length(),
                max(num_trunc(tm_average).length(), num_trunc(tm_prof).length()));

        System.err.println();
        System.err.println(str_pad(averageTimeMsg, maxEndMsgLength)
                + num_pad(tm_average, maxEndValueLength) + " msec");
        System.err.println(str_pad(totalTimeMsg, maxEndMsgLength)
                + num_pad(tm_total, maxEndValueLength) + " msec");
        System.err.println(str_pad(profilerTimeMsg, maxEndMsgLength)
                + num_pad(tm_prof, maxEndValueLength) + " msec");

        System.err.println(str_repeat("=", total_length));
        System.err.println("\n");
    }

    /**
     * String handling function - repeat a string a certain number of times.
     * Used for centering the QProfile results banner. :)
     * 
     * @param s
     *            string to repeat
     * @param length
     *            number of times to repeat that string
     * @return a String consisting of {@link s} repeated a number of times
     */
    private static final String str_repeat(String s, int length) {
        StringBuilder b = new StringBuilder();
        while (length-- > 0) {
            b.append(s);
        }
        return b.toString();
    }

    /**
     * String handling function - pad a string with spaces until it is of a
     * certain length. Used for aligning results into nice rows in a table.
     * 
     * @param s
     *            string to pad
     * @param length
     *            length string should be
     * @return a new String consisting of {@link s} followed by a number of
     *         space characters, such that the resulting string is equal to
     *         {@link length} in length.
     */
    private static final String str_pad(String s, int length) {
        StringBuilder b = new StringBuilder();
        b.append(s);
        length -= s.length();
        while (length-- > 0) {
            b.append(" ");
        }
        return b.toString();
    }

    /**
     * String handling function - prepend a string with spaces until it is of a
     * certain length. Used for aligning results into nice rows in a table.
     * 
     * @param s
     *            string to pad
     * @param length
     *            length string should be
     * @return a new String consisting of {@link s} followed by a number of
     *         space characters, such that the resulting string is equal to
     *         {@link length} in length.
     */
    private static final String str_prepad(String s, int length) {
        StringBuilder b = new StringBuilder();
        length -= s.length();
        while (length-- > 0) {
            b.append(" ");
        }
        b.append(s);
        return b.toString();
    }

    /**
     * Pad an integer value with spaces until its string representation is of a
     * certain length
     * 
     * @param value
     *            any integer value
     * @param length
     *            desired length of string
     * @return space-padded string representation of value
     */
    private static final String num_pad(int value, int length) {
        return str_prepad("" + value, length);
    }

    /**
     * Pad an double value with spaces until its string representation is of a
     * certain length. The value is formatted with six values after the decimal
     * point.
     * 
     * @param value
     *            any double value
     * @param length
     *            desired length of string
     * @return space-padded string representation of value
     */
    private static final String num_pad(double value, int length) {
        return str_prepad(num_trunc(value), length);
    }

    /**
     * Truncate a double value so that it has six values after the decimal point
     * 
     * @param value
     *            any double value
     * @return Truncated string representation of value
     */
    private static final String num_trunc(double value) {
        return String.format("%.6f", value);
    }

}
