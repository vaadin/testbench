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
 * patterns (constant, ramp, stress, soak), explicit k6 executor selection with
 * full parameter control, and fully custom scenario definitions.
 *
 * <p>
 * Three modes of configuration:
 * <ol>
 * <li><strong>Predefined patterns</strong> — convenient presets that map to
 * {@code constant-vus} or {@code ramping-vus} executors</li>
 * <li><strong>Explicit executor</strong> — direct selection of any k6 executor
 * (e.g., {@code constant-arrival-rate}, {@code shared-iterations}) with full
 * parameter control</li>
 * <li><strong>Custom scenario</strong> — raw k6 scenario definition for maximum
 * flexibility</li>
 * </ol>
 *
 * <p>
 * Example Maven usage:
 *
 * <pre>
 * // Predefined: ramp pattern with 10s ramp-up and 10s ramp-down (default)
 * mvn k6:run -Dk6.vus=50 -Dk6.duration=2m
 *
 * // Predefined: constant load (no ramping)
 * mvn k6:run -Dk6.vus=50 -Dk6.duration=2m -Dk6.loadPattern=constant
 *
 * // Predefined: custom ramp durations
 * mvn k6:run -Dk6.vus=50 -Dk6.duration=5m -Dk6.rampUp=30s -Dk6.rampDown=15s
 *
 * // Predefined: stress test pattern
 * mvn k6:run -Dk6.vus=50 -Dk6.duration=5m -Dk6.loadPattern=stress
 *
 * // Predefined: fully custom stages
 * mvn k6:run -Dk6.stages="30s:20,1m:50,30s:50,15s:80,1m:80,30s:0"
 *
 * // Explicit executor: constant arrival rate
 * mvn k6:run -Dk6.executor=constant-arrival-rate -Dk6.rate=100 \
 *     -Dk6.duration=2m -Dk6.preAllocatedVUs=50 -Dk6.maxVUs=100
 *
 * // Explicit executor: shared iterations
 * mvn k6:run -Dk6.executor=shared-iterations -Dk6.vus=50 -Dk6.iterations=1000
 *
 * // Fully custom scenario (raw k6 JavaScript)
 * mvn k6:run -Dk6.customScenario="executor: 'ramping-arrival-rate', ..."
 * </pre>
 */
public class LoadProfile {

    /**
     * Predefined load patterns that map to standard k6 executor configurations.
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
     * All k6 executor types. Each executor implements a different workload
     * model for generating load.
     *
     * @see <a href=
     *      "https://grafana.com/docs/k6/latest/using-k6/scenarios/executors/">k6
     *      Executors</a>
     */
    public enum K6Executor {
        /**
         * A fixed number of VUs execute iterations for a specified duration.
         */
        CONSTANT_VUS("constant-vus"),

        /**
         * A variable number of VUs execute iterations, ramping up and down
         * according to configured stages.
         */
        RAMPING_VUS("ramping-vus"),

        /**
         * Each VU executes a fixed number of iterations. Test ends when all VUs
         * complete their iterations or {@code maxDuration} is reached.
         */
        PER_VU_ITERATIONS("per-vu-iterations"),

        /**
         * A fixed total number of iterations is shared among all VUs. Test ends
         * when all iterations complete or {@code maxDuration} is reached.
         */
        SHARED_ITERATIONS("shared-iterations"),

        /**
         * A fixed number of iterations are started per time unit. VUs are
         * pre-allocated and recycled as needed.
         */
        CONSTANT_ARRIVAL_RATE("constant-arrival-rate"),

        /**
         * A variable number of iterations per time unit, following configured
         * stages. Useful for ramping request rates independently of VU count.
         */
        RAMPING_ARRIVAL_RATE("ramping-arrival-rate"),

        /**
         * VU count is controlled externally via k6's REST API. Useful for
         * manual or programmatic load control.
         */
        EXTERNALLY_CONTROLLED("externally-controlled");

        private final String k6Name;

        K6Executor(String k6Name) {
            this.k6Name = k6Name;
        }

        /**
         * Returns the k6 executor name as used in k6 scenario definitions
         * (e.g., {@code "constant-arrival-rate"}).
         *
         * @return the k6 executor name
         */
        public String getK6Name() {
            return k6Name;
        }

        /**
         * Parses an executor name from its k6 name (e.g.,
         * {@code "constant-arrival-rate"}), Java enum name (e.g.,
         * {@code "CONSTANT_ARRIVAL_RATE"}), or hyphenated form.
         *
         * @param name
         *            the executor name to parse
         * @return the matching K6Executor
         * @throws IllegalArgumentException
         *             if no match is found
         */
        public static K6Executor fromString(String name) {
            for (K6Executor e : values()) {
                if (e.k6Name.equals(name) || e.name().equalsIgnoreCase(name)
                        || e.name().replace("_", "-").equalsIgnoreCase(name)) {
                    return e;
                }
            }
            throw new IllegalArgumentException("Unknown k6 executor: '" + name
                    + "'. Valid values: constant-vus, ramping-vus, "
                    + "per-vu-iterations, shared-iterations, "
                    + "constant-arrival-rate, ramping-arrival-rate, "
                    + "externally-controlled");
        }
    }

    /**
     * A single stage in a k6 ramping configuration. For {@code ramping-vus},
     * the target represents VU count. For {@code ramping-arrival-rate}, the
     * target represents iteration rate per time unit.
     *
     * @param duration
     *            stage duration (e.g., "30s", "1m", "2m30s")
     * @param target
     *            target number of VUs or iteration rate at the end of this
     *            stage
     */
    public record Stage(String duration, int target) {
    }

    /**
     * Returns the load stages as a compact string for passing as a k6
     * environment variable. Format: {@code duration:target,duration:target,...}
     * (e.g., {@code 10s:50,30s:50,10s:0}). Avoids JSON quotes/brackets that can
     * cause issues with process argument passing.
     *
     * @param vus
     *            number of virtual users
     * @param duration
     *            test duration
     * @return compact stages string
     */
    public String toStagesEnvVar(int vus, String duration) {
        List<Stage> stages = toStages(vus, duration);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stages.size(); i++) {
            if (i > 0)
                sb.append(",");
            Stage s = stages.get(i);
            sb.append(s.duration()).append(":").append(s.target());
        }
        return sb.toString();
    }

    private static final Pattern DURATION_PATTERN = Pattern
            .compile("(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?");

    /**
     * Constant load profile with no ramping.
     */
    public static final LoadProfile CONSTANT = LoadProfile.constant();

    private LoadPattern pattern;
    private K6Executor executor;
    private String rampUp = "0s";
    private String rampDown = "0s";
    private List<Stage> customStages = List.of();
    private Integer rate;
    private String timeUnit;
    private Integer preAllocatedVUs;
    private Integer maxVUs;
    private Integer iterations;
    private Integer startRate;
    private String customScenarioContent;

    private LoadProfile() {
    }

    // === Pattern factory methods ===

    /** Constant load: all VUs start immediately, no ramping. */
    public static LoadProfile constant() {
        LoadProfile lp = new LoadProfile();
        lp.pattern = LoadPattern.CONSTANT;
        return lp;
    }

    /** Ramping load: ramp up to target VUs, sustain, then ramp down. */
    public static LoadProfile ramp(String rampUp, String rampDown) {
        LoadProfile lp = new LoadProfile();
        lp.pattern = LoadPattern.RAMP;
        lp.rampUp = rampUp;
        lp.rampDown = rampDown;
        return lp;
    }

    /** Stress test: gradual increase with a spike phase. */
    public static LoadProfile stress() {
        LoadProfile lp = new LoadProfile();
        lp.pattern = LoadPattern.STRESS;
        return lp;
    }

    /** Soak test: quick ramp-up, extended sustain, quick ramp-down. */
    public static LoadProfile soak() {
        LoadProfile lp = new LoadProfile();
        lp.pattern = LoadPattern.SOAK;
        return lp;
    }

    /** Custom stages: user provides explicit stage definitions. */
    public static LoadProfile customStages(List<Stage> stages) {
        LoadProfile lp = new LoadProfile();
        lp.pattern = LoadPattern.CUSTOM;
        lp.customStages = copyStages(stages);
        return lp;
    }

    // === Executor factory methods ===

    /** Fixed number of VUs for a specified duration. */
    public static LoadProfile constantVus() {
        LoadProfile lp = new LoadProfile();
        lp.executor = K6Executor.CONSTANT_VUS;
        return lp;
    }

    /** Variable VU count ramping according to configured stages. */
    public static LoadProfile rampingVus(List<Stage> stages) {
        LoadProfile lp = new LoadProfile();
        lp.executor = K6Executor.RAMPING_VUS;
        lp.customStages = copyStages(stages);
        return lp;
    }

    /** Each VU executes a fixed number of iterations. */
    public static LoadProfile perVuIterations(int iterations) {
        LoadProfile lp = new LoadProfile();
        lp.executor = K6Executor.PER_VU_ITERATIONS;
        lp.iterations = iterations;
        return lp;
    }

    /** Fixed total iterations shared among all VUs. */
    public static LoadProfile sharedIterations(int iterations) {
        LoadProfile lp = new LoadProfile();
        lp.executor = K6Executor.SHARED_ITERATIONS;
        lp.iterations = iterations;
        return lp;
    }

    /** Fixed iteration rate per time unit with pre-allocated VUs. */
    public static LoadProfile constantArrivalRate(int rate, String timeUnit) {
        LoadProfile lp = new LoadProfile();
        lp.executor = K6Executor.CONSTANT_ARRIVAL_RATE;
        lp.rate = rate;
        lp.timeUnit = timeUnit;
        return lp;
    }

    /** Variable iteration rate per time unit, following configured stages. */
    public static LoadProfile rampingArrivalRate(String timeUnit,
            List<Stage> stages) {
        LoadProfile lp = new LoadProfile();
        lp.executor = K6Executor.RAMPING_ARRIVAL_RATE;
        lp.timeUnit = timeUnit;
        lp.customStages = copyStages(stages);
        return lp;
    }

    /** VU count controlled externally via k6's REST API. */
    public static LoadProfile externallyControlled() {
        LoadProfile lp = new LoadProfile();
        lp.executor = K6Executor.EXTERNALLY_CONTROLLED;
        return lp;
    }

    /** Generic executor factory for dynamic executor selection. */
    public static LoadProfile executor(K6Executor executor) {
        LoadProfile lp = new LoadProfile();
        lp.executor = executor;
        return lp;
    }

    /**
     * Creates a load profile from raw k6 scenario content. The content is
     * inserted directly into the k6 scenario definition block, giving full
     * control over all scenario properties.
     *
     * @param scenarioContent
     *            raw k6 JavaScript scenario properties
     * @return a LoadProfile that inserts the content directly
     */
    public static LoadProfile customScenario(String scenarioContent) {
        LoadProfile lp = new LoadProfile();
        lp.customScenarioContent = scenarioContent;
        return lp;
    }

    // === Builder-style setters ===

    public LoadProfile stages(List<Stage> stages) {
        this.customStages = copyStages(stages);
        return this;
    }

    public LoadProfile rate(Integer rate) {
        this.rate = rate;
        return this;
    }

    public LoadProfile timeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    public LoadProfile preAllocatedVUs(Integer preAllocatedVUs) {
        this.preAllocatedVUs = preAllocatedVUs;
        return this;
    }

    public LoadProfile maxVUs(Integer maxVUs) {
        this.maxVUs = maxVUs;
        return this;
    }

    public LoadProfile iterations(Integer iterations) {
        this.iterations = iterations;
        return this;
    }

    public LoadProfile startRate(Integer startRate) {
        this.startRate = startRate;
        return this;
    }

    public LoadProfile rampUp(String rampUp) {
        this.rampUp = rampUp;
        return this;
    }

    public LoadProfile rampDown(String rampDown) {
        this.rampDown = rampDown;
        return this;
    }

    private static List<Stage> copyStages(List<Stage> stages) {
        return stages != null
                ? Collections.unmodifiableList(new ArrayList<>(stages))
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

    public K6Executor getExecutor() {
        return executor;
    }

    public Integer getRate() {
        return rate;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public Integer getPreAllocatedVUs() {
        return preAllocatedVUs;
    }

    public Integer getMaxVUs() {
        return maxVUs;
    }

    public Integer getIterations() {
        return iterations;
    }

    public Integer getStartRate() {
        return startRate;
    }

    public String getCustomScenarioContent() {
        return customScenarioContent;
    }

    /**
     * Returns the k6 executor type this profile resolves to. For predefined
     * patterns, this maps {@code CONSTANT} to {@code constant-vus} and all
     * others to {@code ramping-vus}. For explicit executors, returns the
     * configured executor. Returns {@code null} for fully custom scenarios.
     *
     * @return the resolved k6 executor, or {@code null} for custom scenario
     *         content
     */
    public K6Executor resolveExecutor() {
        if (customScenarioContent != null) {
            return null;
        }
        if (executor != null) {
            return executor;
        }
        if (pattern == LoadPattern.CONSTANT) {
            return K6Executor.CONSTANT_VUS;
        }
        return K6Executor.RAMPING_VUS;
    }

    /**
     * Returns whether this profile uses k6's {@code ramping-vus} executor
     * (i.e., has stages rather than constant VUs).
     *
     * @return {@code true} if ramping, {@code false} for constant load
     */
    public boolean isRamping() {
        if (executor != null) {
            return executor == K6Executor.RAMPING_VUS
                    || executor == K6Executor.RAMPING_ARRIVAL_RATE;
        }
        return pattern != null && pattern != LoadPattern.CONSTANT;
    }

    /**
     * Returns whether this profile requires configuration embedded in the k6
     * script (cannot be fully configured via k6 CLI flags alone). Only
     * {@code constant-vus} and {@code ramping-vus} support full CLI
     * configuration; all other executors and custom scenarios require embedded
     * config.
     *
     * @return {@code true} if embedded script configuration is required
     */
    public boolean requiresEmbeddedConfig() {
        if (customScenarioContent != null) {
            return true;
        }
        if (executor != null) {
            return executor != K6Executor.CONSTANT_VUS
                    && executor != K6Executor.RAMPING_VUS;
        }
        return false;
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
     * Generates k6 scenario properties for use inside a scenario definition
     * block. Includes the executor type and all executor-specific properties
     * but does <em>not</em> include the scenario name wrapper or the
     * {@code exec} property (those are added by the caller).
     *
     * <p>
     * Example output for {@code constant-arrival-rate}:
     *
     * <pre>
     *       executor: 'constant-arrival-rate',
     *       rate: 100,
     *       timeUnit: '1s',
     *       duration: '2m',
     *       preAllocatedVUs: 50,
     *       maxVUs: 100,
     * </pre>
     *
     * @param vus
     *            virtual user count (used for VU-based executors; used as
     *            {@code preAllocatedVUs} default for arrival-rate executors)
     * @param duration
     *            test duration (e.g., "30s", "2m", "5m")
     * @param indent
     *            indentation prefix for each line
     * @return the k6 scenario properties as a string
     */
    public String toK6ScenarioProperties(int vus, String duration,
            String indent) {
        if (customScenarioContent != null) {
            return formatCustomContent(indent);
        }

        K6Executor resolved = resolveExecutor();
        StringBuilder sb = new StringBuilder();

        switch (resolved) {
        case CONSTANT_VUS:
            sb.append(indent).append("executor: 'constant-vus',\n");
            sb.append(indent).append("vus: ").append(vus).append(",\n");
            sb.append(indent).append("duration: '").append(duration)
                    .append("',\n");
            break;

        case RAMPING_VUS:
            sb.append(indent).append("executor: 'ramping-vus',\n");
            sb.append(toK6StagesBlock(vus, duration, indent));
            break;

        case PER_VU_ITERATIONS:
            sb.append(indent).append("executor: 'per-vu-iterations',\n");
            sb.append(indent).append("vus: ").append(vus).append(",\n");
            if (iterations != null) {
                sb.append(indent).append("iterations: ").append(iterations)
                        .append(",\n");
            }
            sb.append(indent).append("maxDuration: '").append(duration)
                    .append("',\n");
            break;

        case SHARED_ITERATIONS:
            sb.append(indent).append("executor: 'shared-iterations',\n");
            sb.append(indent).append("vus: ").append(vus).append(",\n");
            if (iterations != null) {
                sb.append(indent).append("iterations: ").append(iterations)
                        .append(",\n");
            }
            sb.append(indent).append("maxDuration: '").append(duration)
                    .append("',\n");
            break;

        case CONSTANT_ARRIVAL_RATE:
            sb.append(indent).append("executor: 'constant-arrival-rate',\n");
            if (rate != null) {
                sb.append(indent).append("rate: ").append(rate).append(",\n");
            }
            sb.append(indent).append("timeUnit: '")
                    .append(timeUnit != null ? timeUnit : "1s").append("',\n");
            sb.append(indent).append("duration: '").append(duration)
                    .append("',\n");
            sb.append(indent).append("preAllocatedVUs: ")
                    .append(preAllocatedVUs != null ? preAllocatedVUs : vus)
                    .append(",\n");
            if (maxVUs != null) {
                sb.append(indent).append("maxVUs: ").append(maxVUs)
                        .append(",\n");
            }
            break;

        case RAMPING_ARRIVAL_RATE:
            sb.append(indent).append("executor: 'ramping-arrival-rate',\n");
            if (startRate != null) {
                sb.append(indent).append("startRate: ").append(startRate)
                        .append(",\n");
            }
            sb.append(indent).append("timeUnit: '")
                    .append(timeUnit != null ? timeUnit : "1s").append("',\n");
            sb.append(indent).append("preAllocatedVUs: ")
                    .append(preAllocatedVUs != null ? preAllocatedVUs : vus)
                    .append(",\n");
            if (maxVUs != null) {
                sb.append(indent).append("maxVUs: ").append(maxVUs)
                        .append(",\n");
            }
            if (!customStages.isEmpty()) {
                sb.append(indent).append("stages: [\n");
                for (Stage stage : customStages) {
                    sb.append(indent).append("  { duration: '")
                            .append(stage.duration()).append("', target: ")
                            .append(stage.target()).append(" },\n");
                }
                sb.append(indent).append("],\n");
            }
            break;

        case EXTERNALLY_CONTROLLED:
            sb.append(indent).append("executor: 'externally-controlled',\n");
            sb.append(indent).append("vus: ").append(vus).append(",\n");
            if (maxVUs != null) {
                sb.append(indent).append("maxVUs: ").append(maxVUs)
                        .append(",\n");
            }
            sb.append(indent).append("duration: '").append(duration)
                    .append("',\n");
            break;
        }

        return sb.toString();
    }

    /**
     * Formats raw custom scenario content with proper indentation.
     */
    private String formatCustomContent(String indent) {
        StringBuilder sb = new StringBuilder();
        for (String line : customScenarioContent.strip().split("\n")) {
            String trimmed = line.strip();
            if (!trimmed.isEmpty()) {
                sb.append(indent).append(trimmed).append("\n");
            }
        }
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
        if (customScenarioContent != null) {
            return "custom scenario (raw k6 definition)";
        }
        if (executor != null) {
            return executor.getK6Name() + " executor";
        }
        return switch (pattern) {
        case CONSTANT -> "constant (no ramping)";
        case RAMP -> "ramp (up: " + rampUp + ", down: " + rampDown + ")";
        case STRESS -> "stress (gradual increase with spike)";
        case SOAK -> "soak (quick ramp, long sustain)";
        case CUSTOM -> "custom (" + customStages.size() + " stages)";
        };
    }
}
