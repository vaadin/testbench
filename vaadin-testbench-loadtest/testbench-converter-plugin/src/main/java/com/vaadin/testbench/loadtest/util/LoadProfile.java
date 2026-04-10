/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Configures the load pattern for k6 test execution. Supports predefined
 * patterns (constant, ramp, stress, soak) and fully custom stage definitions.
 *
 * <p>
 * When a ramping pattern is used, k6's {@code ramping-vus} executor generates
 * stages that control how virtual users are added and removed over time. The
 * {@code constant} pattern uses k6's {@code constant-vus} executor with no
 * ramping.
 *
 * <p>
 * Example Maven usage:
 *
 * <pre>
 * // Default: ramp pattern with 10s ramp-up and 10s ramp-down
 * mvn k6:run -Dk6.vus=50 -Dk6.duration=2m
 *
 * // Constant load (no ramping)
 * mvn k6:run -Dk6.vus=50 -Dk6.duration=2m -Dk6.loadPattern=constant
 *
 * // Custom ramp durations
 * mvn k6:run -Dk6.vus=50 -Dk6.duration=5m -Dk6.rampUp=30s -Dk6.rampDown=15s
 *
 * // Stress test pattern
 * mvn k6:run -Dk6.vus=50 -Dk6.duration=5m -Dk6.loadPattern=stress
 *
 * // Fully custom stages
 * mvn k6:run -Dk6.stages="30s:20,1m:50,30s:50,15s:80,1m:80,30s:0"
 * </pre>
 */
public class LoadProfile {

    /**
     * Predefined load patterns.
     */
    public enum LoadPattern {
        /**
         * Constant load: all VUs start immediately and run for the full
         * duration. Uses k6's {@code constant-vus} executor.
         */
        CONSTANT,

        /**
         * Ramping load (default): ramp up to target VUs, sustain, then ramp
         * down. Uses k6's {@code ramping-vus} executor. Ramp-up and ramp-down
         * durations are configurable (default 10s each).
         */
        RAMP,

        /**
         * Stress test: gradually increases load beyond normal levels with a
         * spike phase. Stages: ramp to 50% → ramp to 100% → sustain → spike to
         * 150% → ramp down. Uses k6's {@code ramping-vus} executor.
         */
        STRESS,

        /**
         * Soak test: quick ramp-up, extended sustain at target load, quick
         * ramp-down. Designed for long-duration tests to detect memory leaks
         * and degradation. Stages: 5% ramp-up → 90% sustain → 5% ramp-down.
         * Uses k6's {@code ramping-vus} executor.
         */
        SOAK,

        /**
         * Custom stages: user provides explicit stage definitions via the
         * {@code k6.stages} parameter. Uses k6's {@code ramping-vus} executor.
         */
        CUSTOM
    }

    /**
     * A single stage in a k6 ramping-vus configuration.
     *
     * @param duration
     *            stage duration (e.g., "30s", "1m", "2m30s")
     * @param target
     *            target number of VUs at the end of this stage
     */
    public record Stage(String duration, int target) {
    }

    private static final Pattern DURATION_PATTERN = Pattern
            .compile("(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?");

    /**
     * Default load profile: ramp pattern with 10s ramp-up and 10s ramp-down.
     */
    public static final LoadProfile DEFAULT = new LoadProfile(LoadPattern.RAMP,
            "10s", "10s", List.of());

    /**
     * Constant load profile with no ramping.
     */
    public static final LoadProfile CONSTANT = new LoadProfile(
            LoadPattern.CONSTANT, "0s", "0s", List.of());

    private final LoadPattern pattern;
    private final String rampUp;
    private final String rampDown;
    private final List<Stage> customStages;

    /**
     * Creates a new load profile.
     *
     * @param pattern
     *            the load pattern to use
     * @param rampUp
     *            ramp-up duration for RAMP pattern (e.g., "10s", "30s")
     * @param rampDown
     *            ramp-down duration for RAMP pattern (e.g., "10s", "30s")
     * @param customStages
     *            explicit stages for CUSTOM pattern (ignored for other
     *            patterns)
     */
    public LoadProfile(LoadPattern pattern, String rampUp, String rampDown,
            List<Stage> customStages) {
        this.pattern = pattern;
        this.rampUp = rampUp;
        this.rampDown = rampDown;
        this.customStages = customStages != null
                ? Collections.unmodifiableList(new ArrayList<>(customStages))
                : List.of();
    }

    public LoadPattern getPattern() {
        return pattern;
    }

    public String getRampUp() {
        return rampUp;
    }

    public String getRampDown() {
        return rampDown;
    }

    public List<Stage> getCustomStages() {
        return customStages;
    }

    /**
     * Returns whether this profile uses k6's {@code ramping-vus} executor
     * (i.e., has stages rather than constant VUs).
     *
     * @return {@code true} if ramping, {@code false} for constant load
     */
    public boolean isRamping() {
        return pattern != LoadPattern.CONSTANT;
    }

    /**
     * Generates the k6 stages for the given VU count and total duration.
     *
     * @param vus
     *            target number of virtual users
     * @param duration
     *            total test duration (e.g., "30s", "2m", "5m")
     * @return list of stages for k6's ramping-vus executor
     * @throws IllegalArgumentException
     *             if CUSTOM pattern is used but no stages are defined
     */
    public List<Stage> toStages(int vus, String duration) {
        return switch (pattern) {
        case CONSTANT -> List.of(new Stage(duration, vus));
        case RAMP -> buildRampStages(vus, duration);
        case STRESS -> buildStressStages(vus, duration);
        case SOAK -> buildSoakStages(vus, duration);
        case CUSTOM -> {
            if (customStages.isEmpty()) {
                throw new IllegalArgumentException(
                        "Custom load pattern requires stages to be defined via k6.stages parameter");
            }
            yield customStages;
        }
        };
    }

    /**
     * Builds ramp stages: ramp-up → sustain → ramp-down.
     */
    private List<Stage> buildRampStages(int vus, String duration) {
        long totalMs = parseDurationMs(duration);
        long rampUpMs = parseDurationMs(rampUp);
        long rampDownMs = parseDurationMs(rampDown);

        // If ramp-up + ramp-down exceeds total, scale them down proportionally
        long rampTotal = rampUpMs + rampDownMs;
        if (rampTotal > totalMs) {
            double scale = (double) totalMs / rampTotal;
            rampUpMs = (long) (rampUpMs * scale);
            rampDownMs = totalMs - rampUpMs;
        }

        long sustainMs = totalMs - rampUpMs - rampDownMs;

        List<Stage> stages = new ArrayList<>();
        if (rampUpMs > 0) {
            stages.add(new Stage(formatDuration(rampUpMs), vus));
        }
        if (sustainMs > 0) {
            stages.add(new Stage(formatDuration(sustainMs), vus));
        }
        if (rampDownMs > 0) {
            stages.add(new Stage(formatDuration(rampDownMs), 0));
        }
        return stages;
    }

    /**
     * Builds stress test stages: ramp to 50% → ramp to 100% → sustain → spike
     * to 150% → ramp down.
     */
    private List<Stage> buildStressStages(int vus, String duration) {
        long totalMs = parseDurationMs(duration);
        int halfVus = Math.max(1, vus / 2);
        int spikeVus = Math.max(vus + 1, (int) (vus * 1.5));

        return List.of(new Stage(formatDuration(totalMs * 10 / 100), halfVus),
                new Stage(formatDuration(totalMs * 10 / 100), vus),
                new Stage(formatDuration(totalMs * 40 / 100), vus),
                new Stage(formatDuration(totalMs * 20 / 100), spikeVus),
                new Stage(formatDuration(totalMs * 20 / 100), 0));
    }

    /**
     * Builds soak test stages: quick ramp-up → long sustain → quick ramp-down.
     */
    private List<Stage> buildSoakStages(int vus, String duration) {
        long totalMs = parseDurationMs(duration);
        long rampUpMs = Math.max(1000, totalMs * 5 / 100);
        long rampDownMs = Math.max(1000, totalMs * 5 / 100);
        long sustainMs = totalMs - rampUpMs - rampDownMs;

        return List.of(new Stage(formatDuration(rampUpMs), vus),
                new Stage(formatDuration(sustainMs), vus),
                new Stage(formatDuration(rampDownMs), 0));
    }

    /**
     * Generates k6 stages block for use inside a scenario definition in
     * {@code export const options}. Example output:
     *
     * <pre>
     *       stages: [
     *         { duration: '10s', target: 50 },
     *         { duration: '1m', target: 50 },
     *         { duration: '10s', target: 0 },
     *       ],
     * </pre>
     *
     * @param vus
     *            target number of virtual users
     * @param duration
     *            total test duration
     * @param indent
     *            indentation prefix for each line
     * @return the k6 stages block as a string
     */
    public String toK6StagesBlock(int vus, String duration, String indent) {
        List<Stage> stages = toStages(vus, duration);
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("stages: [\n");
        for (Stage stage : stages) {
            sb.append(indent).append("  { duration: '").append(stage.duration())
                    .append("', target: ").append(stage.target())
                    .append(" },\n");
        }
        sb.append(indent).append("],\n");
        return sb.toString();
    }

    /**
     * Parses a k6 duration string (e.g., "30s", "2m", "1h30m", "2m30s") to
     * milliseconds.
     *
     * @param duration
     *            the duration string
     * @return duration in milliseconds
     * @throws IllegalArgumentException
     *             if the format is invalid
     */
    public static long parseDurationMs(String duration) {
        if (duration == null || duration.isBlank()) {
            return 0;
        }
        Matcher matcher = DURATION_PATTERN.matcher(duration.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid duration format: "
                    + duration + ". Expected format: 30s, 2m, 1h30m, 2m30s");
        }
        long ms = 0;
        if (matcher.group(1) != null) {
            ms += Long.parseLong(matcher.group(1)) * 3600_000;
        }
        if (matcher.group(2) != null) {
            ms += Long.parseLong(matcher.group(2)) * 60_000;
        }
        if (matcher.group(3) != null) {
            ms += Long.parseLong(matcher.group(3)) * 1000;
        }
        return ms;
    }

    /**
     * Formats a millisecond duration as a k6 duration string. Uses the most
     * natural unit: seconds for &lt; 60s, minutes + seconds otherwise.
     *
     * @param ms
     *            duration in milliseconds
     * @return k6 duration string (e.g., "30s", "2m", "2m30s")
     */
    public static String formatDuration(long ms) {
        if (ms <= 0) {
            return "0s";
        }
        long totalSeconds = ms / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append("h");
        }
        if (minutes > 0) {
            sb.append(minutes).append("m");
        }
        if (seconds > 0 || sb.isEmpty()) {
            sb.append(seconds).append("s");
        }
        return sb.toString();
    }

    /**
     * Parses a stages string into a list of stages. Format:
     * "duration:target,duration:target,..." Example: "10s:50,1m:50,10s:0"
     *
     * @param stagesStr
     *            the stages string to parse
     * @return list of parsed stages
     * @throws IllegalArgumentException
     *             if the format is invalid
     */
    public static List<Stage> parseStages(String stagesStr) {
        if (stagesStr == null || stagesStr.isBlank()) {
            return List.of();
        }
        List<Stage> stages = new ArrayList<>();
        for (String entry : stagesStr.split(",")) {
            String[] parts = entry.trim().split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid stage format: '"
                        + entry.trim()
                        + "'. Expected 'duration:target' (e.g., '30s:50')");
            }
            String dur = parts[0].trim();
            int target = Integer.parseInt(parts[1].trim());
            stages.add(new Stage(dur, target));
        }
        return stages;
    }

    @Override
    public String toString() {
        return switch (pattern) {
        case CONSTANT -> "constant (no ramping)";
        case RAMP -> "ramp (up: " + rampUp + ", down: " + rampDown + ")";
        case STRESS -> "stress (gradual increase with spike)";
        case SOAK -> "soak (quick ramp, long sustain)";
        case CUSTOM -> "custom (" + customStages.size() + " stages)";
        };
    }
}
