/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest.util;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.vaadin.testbench.loadtest.util.LoadProfile.K6Executor;
import com.vaadin.testbench.loadtest.util.LoadProfile.LoadPattern;
import com.vaadin.testbench.loadtest.util.LoadProfile.Stage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoadProfileTest {

    // === Predefined pattern tests (existing) ===

    @Test
    void constantPatternIsNotRamping() {
        assertFalse(LoadProfile.CONSTANT.isRamping());
    }

    @Test
    void defaultPatternIsRamping() {
        assertTrue(LoadProfile.DEFAULT.isRamping());
    }

    @Test
    void constantProducesSingleStage() {
        List<Stage> stages = LoadProfile.CONSTANT.toStages(50, "2m");
        assertEquals(1, stages.size());
        assertEquals("2m", stages.get(0).duration());
        assertEquals(50, stages.get(0).target());
    }

    @Test
    void rampProducesThreeStages() {
        LoadProfile profile = new LoadProfile(LoadPattern.RAMP, "10s", "10s",
                List.of());
        List<Stage> stages = profile.toStages(50, "2m");

        assertEquals(3, stages.size());
        // Ramp up: 10s → 50 VUs
        assertEquals("10s", stages.get(0).duration());
        assertEquals(50, stages.get(0).target());
        // Sustain: 100s → 50 VUs
        assertEquals("1m40s", stages.get(1).duration());
        assertEquals(50, stages.get(1).target());
        // Ramp down: 10s → 0 VUs
        assertEquals("10s", stages.get(2).duration());
        assertEquals(0, stages.get(2).target());
    }

    @Test
    void rampScalesDownWhenRampExceedsDuration() {
        // rampUp(20s) + rampDown(20s) = 40s > 30s duration
        LoadProfile profile = new LoadProfile(LoadPattern.RAMP, "20s", "20s",
                List.of());
        List<Stage> stages = profile.toStages(50, "30s");

        // Should scale proportionally: 15s up + 15s down
        assertEquals(2, stages.size()); // no sustain stage
        long totalMs = 0;
        for (Stage s : stages) {
            totalMs += LoadProfile.parseDurationMs(s.duration());
        }
        assertEquals(30_000, totalMs);
    }

    @Test
    void stressProducesFiveStages() {
        LoadProfile profile = new LoadProfile(LoadPattern.STRESS, "0s", "0s",
                List.of());
        List<Stage> stages = profile.toStages(100, "5m");

        assertEquals(5, stages.size());
        // Ramp to 50% VUs
        assertEquals(50, stages.get(0).target());
        // Ramp to 100% VUs
        assertEquals(100, stages.get(1).target());
        // Sustain at 100%
        assertEquals(100, stages.get(2).target());
        // Spike to 150%
        assertEquals(150, stages.get(3).target());
        // Ramp down to 0
        assertEquals(0, stages.get(4).target());
    }

    @Test
    void soakProducesThreeStages() {
        LoadProfile profile = new LoadProfile(LoadPattern.SOAK, "0s", "0s",
                List.of());
        List<Stage> stages = profile.toStages(50, "10m");

        assertEquals(3, stages.size());
        // Quick ramp up (5%)
        assertEquals(50, stages.get(0).target());
        // Long sustain (90%)
        assertEquals(50, stages.get(1).target());
        // Quick ramp down (5%)
        assertEquals(0, stages.get(2).target());

        // Verify total duration sums to 10m
        long totalMs = 0;
        for (Stage s : stages) {
            totalMs += LoadProfile.parseDurationMs(s.duration());
        }
        assertEquals(600_000, totalMs);
    }

    @Test
    void customStagesAreUsedDirectly() {
        List<Stage> custom = List.of(new Stage("15s", 20), new Stage("1m", 50),
                new Stage("15s", 0));
        LoadProfile profile = new LoadProfile(LoadPattern.CUSTOM, "0s", "0s",
                custom);

        List<Stage> stages = profile.toStages(50, "1m30s");
        assertEquals(custom, stages);
    }

    @Test
    void customWithNoStagesThrows() {
        LoadProfile profile = new LoadProfile(LoadPattern.CUSTOM, "0s", "0s",
                List.of());
        assertThrows(IllegalArgumentException.class,
                () -> profile.toStages(50, "1m"));
    }

    // === Duration parsing/formatting tests (existing) ===

    @Test
    void parseDurationHandlesSeconds() {
        assertEquals(30_000, LoadProfile.parseDurationMs("30s"));
    }

    @Test
    void parseDurationHandlesMinutes() {
        assertEquals(120_000, LoadProfile.parseDurationMs("2m"));
    }

    @Test
    void parseDurationHandlesMinutesAndSeconds() {
        assertEquals(150_000, LoadProfile.parseDurationMs("2m30s"));
    }

    @Test
    void parseDurationHandlesHours() {
        assertEquals(3_600_000, LoadProfile.parseDurationMs("1h"));
    }

    @Test
    void parseDurationHandlesComplex() {
        assertEquals(5_430_000, LoadProfile.parseDurationMs("1h30m30s"));
    }

    @Test
    void formatDurationSeconds() {
        assertEquals("30s", LoadProfile.formatDuration(30_000));
    }

    @Test
    void formatDurationMinutes() {
        assertEquals("2m", LoadProfile.formatDuration(120_000));
    }

    @Test
    void formatDurationMinutesAndSeconds() {
        assertEquals("2m30s", LoadProfile.formatDuration(150_000));
    }

    @Test
    void formatDurationZero() {
        assertEquals("0s", LoadProfile.formatDuration(0));
    }

    // === Stage parsing tests (existing) ===

    @Test
    void parseStagesSimple() {
        List<Stage> stages = LoadProfile.parseStages("10s:50,1m:50,10s:0");
        assertEquals(3, stages.size());
        assertEquals("10s", stages.get(0).duration());
        assertEquals(50, stages.get(0).target());
        assertEquals("1m", stages.get(1).duration());
        assertEquals(50, stages.get(1).target());
        assertEquals("10s", stages.get(2).duration());
        assertEquals(0, stages.get(2).target());
    }

    @Test
    void parseStagesWithSpaces() {
        List<Stage> stages = LoadProfile.parseStages(" 10s:50 , 1m:50 ");
        assertEquals(2, stages.size());
    }

    @Test
    void parseStagesEmptyReturnsEmptyList() {
        assertEquals(List.of(), LoadProfile.parseStages(""));
        assertEquals(List.of(), LoadProfile.parseStages(null));
    }

    @Test
    void parseStagesInvalidFormatThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> LoadProfile.parseStages("invalid"));
    }

    @Test
    void toK6StagesBlockFormat() {
        LoadProfile profile = new LoadProfile(LoadPattern.RAMP, "10s", "10s",
                List.of());
        String block = profile.toK6StagesBlock(50, "1m20s", "      ");

        assertTrue(block.contains("stages: ["));
        assertTrue(block.contains("{ duration: '10s', target: 50 }"));
        assertTrue(block.contains("{ duration: '1m', target: 50 }"));
        assertTrue(block.contains("{ duration: '10s', target: 0 }"));
        assertTrue(block.contains("],"));
    }

    // === K6Executor enum tests ===

    @Test
    void k6ExecutorFromStringWithK6Name() {
        assertEquals(K6Executor.CONSTANT_VUS,
                K6Executor.fromString("constant-vus"));
        assertEquals(K6Executor.RAMPING_VUS,
                K6Executor.fromString("ramping-vus"));
        assertEquals(K6Executor.PER_VU_ITERATIONS,
                K6Executor.fromString("per-vu-iterations"));
        assertEquals(K6Executor.SHARED_ITERATIONS,
                K6Executor.fromString("shared-iterations"));
        assertEquals(K6Executor.CONSTANT_ARRIVAL_RATE,
                K6Executor.fromString("constant-arrival-rate"));
        assertEquals(K6Executor.RAMPING_ARRIVAL_RATE,
                K6Executor.fromString("ramping-arrival-rate"));
        assertEquals(K6Executor.EXTERNALLY_CONTROLLED,
                K6Executor.fromString("externally-controlled"));
    }

    @Test
    void k6ExecutorFromStringWithEnumName() {
        assertEquals(K6Executor.CONSTANT_ARRIVAL_RATE,
                K6Executor.fromString("CONSTANT_ARRIVAL_RATE"));
    }

    @Test
    void k6ExecutorFromStringCaseInsensitive() {
        assertEquals(K6Executor.RAMPING_VUS,
                K6Executor.fromString("Ramping-Vus"));
    }

    @Test
    void k6ExecutorFromStringInvalidThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> K6Executor.fromString("nonexistent"));
    }

    // === resolveExecutor tests ===

    @Test
    void resolveExecutorForPredefinedConstant() {
        assertEquals(K6Executor.CONSTANT_VUS,
                LoadProfile.CONSTANT.resolveExecutor());
    }

    @Test
    void resolveExecutorForPredefinedRamp() {
        assertEquals(K6Executor.RAMPING_VUS,
                LoadProfile.DEFAULT.resolveExecutor());
    }

    @Test
    void resolveExecutorForExplicitExecutor() {
        LoadProfile profile = new LoadProfile(K6Executor.CONSTANT_ARRIVAL_RATE,
                List.of(), 100, "1s", 50, 200, null, null);
        assertEquals(K6Executor.CONSTANT_ARRIVAL_RATE,
                profile.resolveExecutor());
    }

    @Test
    void resolveExecutorForCustomScenarioIsNull() {
        LoadProfile profile = LoadProfile.customScenario("executor: 'test',");
        assertNull(profile.resolveExecutor());
    }

    // === requiresEmbeddedConfig tests ===

    @Test
    void constantOrRampingVusDoNotRequireEmbeddedConfig() {
        assertFalse(LoadProfile.CONSTANT.requiresEmbeddedConfig());
        assertFalse(LoadProfile.DEFAULT.requiresEmbeddedConfig());

        // explicit ramping vus
        LoadProfile profile = new LoadProfile(K6Executor.RAMPING_VUS,
                List.of(new Stage("10s", 50)), null, null, null, null, null,
                null);
        assertFalse(profile.requiresEmbeddedConfig());
    }

    @Test
    void executorsRequiringEmbeddedConfig() {
        // arrivalRate
        LoadProfile profile = new LoadProfile(K6Executor.CONSTANT_ARRIVAL_RATE,
                List.of(), 100, "1s", 50, 200, null, null);
        assertTrue(profile.requiresEmbeddedConfig());

        // shared iterations
        profile = new LoadProfile(K6Executor.SHARED_ITERATIONS, List.of(), null,
                null, null, null, 1000, null);
        assertTrue(profile.requiresEmbeddedConfig());

        // custom scenario
        profile = LoadProfile.customScenario("executor: 'test',");
        assertTrue(profile.requiresEmbeddedConfig());
    }

    // === isRamping with explicit executors ===

    @Test
    void explicitRampingIsRamping() {
        LoadProfile profile = new LoadProfile(K6Executor.RAMPING_VUS,
                List.of(new Stage("10s", 50)), null, null, null, null, null,
                null);
        assertTrue(profile.isRamping());

        // explicit ramping arrival rate
        profile = new LoadProfile(K6Executor.RAMPING_ARRIVAL_RATE,
                List.of(new Stage("1m", 100)), null, "1s", 50, 200, null, 0);
        assertTrue(profile.isRamping());
    }

    @Test
    void explicitVusIsNotRamping() {
        // Constant Vus
        LoadProfile profile = new LoadProfile(K6Executor.CONSTANT_VUS,
                List.of(), null, null, null, null, null, null);
        assertFalse(profile.isRamping());
        // Shared Iterations
        profile = new LoadProfile(K6Executor.SHARED_ITERATIONS, List.of(), null,
                null, null, null, 500, null);
        assertFalse(profile.isRamping());
    }

    // === toK6ScenarioProperties tests ===

    @Test
    void scenarioPropertiesConstantVus() {
        LoadProfile profile = new LoadProfile(LoadPattern.CONSTANT, "0s", "0s",
                List.of());
        String props = profile.toK6ScenarioProperties(50, "2m", "  ");

        assertTrue(props.contains("executor: 'constant-vus',"));
        assertTrue(props.contains("vus: 50,"));
        assertTrue(props.contains("duration: '2m',"));
    }

    @Test
    void scenarioPropertiesRampingVus() {
        LoadProfile profile = new LoadProfile(LoadPattern.RAMP, "10s", "10s",
                List.of());
        String props = profile.toK6ScenarioProperties(50, "2m", "  ");

        assertTrue(props.contains("executor: 'ramping-vus',"));
        assertTrue(props.contains("stages: ["));
        assertTrue(props.contains("{ duration: '10s', target: 50 }"));
    }

    @Test
    void scenarioPropertiesPerVuIterations() {
        LoadProfile profile = new LoadProfile(K6Executor.PER_VU_ITERATIONS,
                List.of(), null, null, null, null, 20, null);
        String props = profile.toK6ScenarioProperties(10, "5m", "  ");

        assertTrue(props.contains("executor: 'per-vu-iterations',"));
        assertTrue(props.contains("vus: 10,"));
        assertTrue(props.contains("iterations: 20,"));
        assertTrue(props.contains("maxDuration: '5m',"));
    }

    @Test
    void scenarioPropertiesSharedIterations() {
        LoadProfile profile = new LoadProfile(K6Executor.SHARED_ITERATIONS,
                List.of(), null, null, null, null, 1000, null);
        String props = profile.toK6ScenarioProperties(50, "5m", "  ");

        assertTrue(props.contains("executor: 'shared-iterations',"));
        assertTrue(props.contains("vus: 50,"));
        assertTrue(props.contains("iterations: 1000,"));
        assertTrue(props.contains("maxDuration: '5m',"));
    }

    @Test
    void scenarioPropertiesConstantArrivalRate() {
        LoadProfile profile = new LoadProfile(K6Executor.CONSTANT_ARRIVAL_RATE,
                List.of(), 100, "1s", 50, 200, null, null);
        String props = profile.toK6ScenarioProperties(50, "2m", "  ");

        assertTrue(props.contains("executor: 'constant-arrival-rate',"));
        assertTrue(props.contains("rate: 100,"));
        assertTrue(props.contains("timeUnit: '1s',"));
        assertTrue(props.contains("duration: '2m',"));
        assertTrue(props.contains("preAllocatedVUs: 50,"));
        assertTrue(props.contains("maxVUs: 200,"));
    }

    @Test
    void scenarioPropertiesConstantArrivalRateDefaultsPreAllocatedToVus() {
        LoadProfile profile = new LoadProfile(K6Executor.CONSTANT_ARRIVAL_RATE,
                List.of(), 100, "1s", null, null, null, null);
        String props = profile.toK6ScenarioProperties(30, "2m", "  ");

        assertTrue(props.contains("preAllocatedVUs: 30,"));
        assertFalse(props.contains("maxVUs:"));
    }

    @Test
    void scenarioPropertiesRampingArrivalRate() {
        List<Stage> stages = List.of(new Stage("1m", 100), new Stage("2m", 100),
                new Stage("1m", 0));
        LoadProfile profile = new LoadProfile(K6Executor.RAMPING_ARRIVAL_RATE,
                stages, null, "1s", 50, 200, null, 0);
        String props = profile.toK6ScenarioProperties(50, "4m", "  ");

        assertTrue(props.contains("executor: 'ramping-arrival-rate',"));
        assertTrue(props.contains("startRate: 0,"));
        assertTrue(props.contains("timeUnit: '1s',"));
        assertTrue(props.contains("preAllocatedVUs: 50,"));
        assertTrue(props.contains("maxVUs: 200,"));
        assertTrue(props.contains("stages: ["));
        assertTrue(props.contains("{ duration: '1m', target: 100 }"));
        assertTrue(props.contains("{ duration: '2m', target: 100 }"));
        assertTrue(props.contains("{ duration: '1m', target: 0 }"));
    }

    @Test
    void scenarioPropertiesExternallyControlled() {
        LoadProfile profile = new LoadProfile(K6Executor.EXTERNALLY_CONTROLLED,
                List.of(), null, null, null, 200, null, null);
        String props = profile.toK6ScenarioProperties(50, "10m", "  ");

        assertTrue(props.contains("executor: 'externally-controlled',"));
        assertTrue(props.contains("vus: 50,"));
        assertTrue(props.contains("maxVUs: 200,"));
        assertTrue(props.contains("duration: '10m',"));
    }

    @Test
    void scenarioPropertiesCustomScenario() {
        String custom = "executor: 'ramping-arrival-rate',\n"
                + "startRate: 0,\n" + "timeUnit: '1s',\n"
                + "preAllocatedVUs: 50,\n";
        LoadProfile profile = LoadProfile.customScenario(custom);
        String props = profile.toK6ScenarioProperties(0, "0s", "  ");

        assertTrue(props.contains("executor: 'ramping-arrival-rate',"));
        assertTrue(props.contains("startRate: 0,"));
        assertTrue(props.contains("timeUnit: '1s',"));
        assertTrue(props.contains("preAllocatedVUs: 50,"));
    }

}
