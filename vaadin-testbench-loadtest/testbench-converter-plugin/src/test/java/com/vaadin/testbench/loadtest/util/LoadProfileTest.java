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

import com.vaadin.testbench.loadtest.util.LoadProfile.LoadPattern;
import com.vaadin.testbench.loadtest.util.LoadProfile.Stage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoadProfileTest {

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
        var profile = new LoadProfile(LoadPattern.RAMP, "10s", "10s",
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
        var profile = new LoadProfile(LoadPattern.RAMP, "20s", "20s",
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
        var profile = new LoadProfile(LoadPattern.STRESS, "0s", "0s",
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
        var profile = new LoadProfile(LoadPattern.SOAK, "0s", "0s", List.of());
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
        var profile = new LoadProfile(LoadPattern.CUSTOM, "0s", "0s", custom);

        List<Stage> stages = profile.toStages(50, "1m30s");
        assertEquals(custom, stages);
    }

    @Test
    void customWithNoStagesThrows() {
        var profile = new LoadProfile(LoadPattern.CUSTOM, "0s", "0s",
                List.of());
        assertThrows(IllegalArgumentException.class,
                () -> profile.toStages(50, "1m"));
    }

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
        var profile = new LoadProfile(LoadPattern.RAMP, "10s", "10s",
                List.of());
        String block = profile.toK6StagesBlock(50, "1m20s", "      ");

        assertTrue(block.contains("stages: ["));
        assertTrue(block.contains("{ duration: '10s', target: 50 }"));
        assertTrue(block.contains("{ duration: '1m', target: 50 }"));
        assertTrue(block.contains("{ duration: '10s', target: 0 }"));
        assertTrue(block.contains("],"));
    }
}
