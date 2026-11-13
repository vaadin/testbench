/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

/**
 * The operating system a browser runs on, as {@link Capabilities} name it.
 * <p>
 * The constants, their names, aliases and families are Selenium's
 * {@code Platform}'s (selenium-api 4.52.0), so a test ports by changing its
 * import, and reference screenshots keep their names: a Windows or macOS
 * version counts as its family, {@link #LINUX} as itself.
 */
public enum Platform {
    WINDOWS("windows", null, "windows"),
    XP("Windows XP", "WINDOWS", "Windows Server 2003", "xp", "windows", "winnt",
            "windows_nt", "windows nt"),
    VISTA("Windows Vista", "WINDOWS", "windows vista", "Windows Server 2008"),
    WIN7("Windows 7", "WINDOWS", "windows 7", "win7"),
    WIN8("Windows 8", "WINDOWS", "Windows Server 2012", "windows 8", "win8"),
    WIN8_1("Windows 8.1", "WINDOWS", "windows 8.1", "win8.1"),
    WIN10("Windows 10", "WINDOWS", "windows 10", "win10"),
    WIN11("Windows 11", "WINDOWS", "windows 11", "win11"),
    MAC("mac", null, "mac", "darwin", "macOS", "mac os x", "os x"),
    SNOW_LEOPARD("OS X 10.6", "MAC", "snow leopard", "os x 10.6", "macos 10.6"),
    MOUNTAIN_LION("OS X 10.8", "MAC", "mountain lion", "os x 10.8",
            "macos 10.8"),
    MAVERICKS("OS X 10.9", "MAC", "mavericks", "os x 10.9", "macos 10.9"),
    YOSEMITE("OS X 10.10", "MAC", "yosemite", "os x 10.10", "macos 10.10"),
    EL_CAPITAN("OS X 10.11", "MAC", "el capitan", "os x 10.11", "macos 10.11"),
    SIERRA("macOS 10.12", "MAC", "sierra", "os x 10.12", "macos 10.12"),
    HIGH_SIERRA("macOS 10.13", "MAC", "high sierra", "os x 10.13",
            "macos 10.13"),
    MOJAVE("macOS 10.14", "MAC", "mojave", "os x 10.14", "macos 10.14"),
    CATALINA("macOS 10.15", "MAC", "catalina", "os x 10.15", "macos 10.15"),
    BIG_SUR("macOS 11.0", "MAC", "big sur", "os x 11.0", "macos 11.0"),
    MONTEREY("macOS 12.0", "MAC", "monterey", "os x 12.0", "macos 12.0"),
    VENTURA("macOS 13.0", "MAC", "ventura", "os x 13.0", "macos 13.0"),
    SONOMA("macOS 14.0", "MAC", "sonoma", "os x 14.0", "macos 14.0"),
    SEQUOIA("macOS 15.0", "MAC", "sequoia", "os x 15.0", "macos 15.0"),
    UNIX("UNIX", null, "solaris", "bsd"),
    LINUX("linux", "UNIX", "linux"),
    ANDROID("ANDROID", null, "android", "dalvik"),
    IOS("IOS", null, "iOS"),
    ANY("any", "ANY", "");

    private final String text;
    private final @Nullable String familyName;
    private final String[] partOfOsName;

    Platform(String text, @Nullable String familyName, String... partOfOsName) {
        this.text = text;
        this.familyName = familyName;
        this.partOfOsName = partOfOsName;
    }

    /**
     * The platform of the given name, its {@link #toString()} or one of its
     * aliases, ignoring case.
     *
     * @throws IllegalArgumentException
     *             if no platform has that name
     */
    public static Platform fromString(String name) {
        for (Platform platform : values()) {
            if (platform.toString().equalsIgnoreCase(name)) {
                return platform;
            }
        }
        for (Platform platform : values()) {
            for (String alias : platform.partOfOsName) {
                if (name.equalsIgnoreCase(alias)) {
                    return platform;
                }
            }
        }
        throw new IllegalArgumentException("Unrecognized platform: " + name);
    }

    /**
     * The platform an {@code os.name} and {@code os.version} describe, as
     * Selenium guesses it: an exact alias, else the longest alias the name
     * contains, else {@link #UNIX}.
     */
    public static Platform extractFromSysProperty(@Nullable String osName,
            @Nullable String osVersion) {
        String name = Objects.requireNonNullElse(osName, "")
                .toLowerCase(Locale.ENGLISH);
        String version = Objects.requireNonNullElse(osVersion, "");
        if ("dalvik".equalsIgnoreCase(System.getProperty("java.vm.name"))) {
            return ANDROID;
        }
        if (version.equals("6.2") && name.startsWith("windows nt")) {
            return WIN8;
        }
        if (version.equals("6.3") && name.startsWith("windows nt")) {
            return WIN8_1;
        }
        Platform mostLikely = UNIX;
        String previousMatch = null;
        for (Platform platform : values()) {
            for (String alias : platform.partOfOsName) {
                if (alias.isEmpty()) {
                    continue;
                }
                String matcher = alias.toLowerCase(Locale.ENGLISH);
                if (matcher.equals(name)) {
                    return platform;
                }
                if (name.contains(matcher) && (previousMatch == null
                        || matcher.length() >= previousMatch.length())) {
                    previousMatch = matcher;
                    mostLikely = platform;
                }
            }
        }
        return mostLikely;
    }

    /** The platform this JVM runs on. */
    public static Platform getCurrent() {
        return extractFromSysProperty(System.getProperty("os.name"),
                System.getProperty("os.version", ""));
    }

    /**
     * The family this platform belongs to: {@link #WINDOWS} for a Windows
     * version, {@link #MAC} for a macOS one, {@link #UNIX} for {@link #LINUX},
     * {@link #ANY} for {@code ANY}, else {@code null}.
     */
    public @Nullable Platform family() {
        return familyName == null ? null : valueOf(familyName);
    }

    /**
     * Whether this platform is the given one, or belongs to it: every platform
     * is {@link #ANY}, but {@code ANY} is only itself.
     */
    public boolean is(Platform compareWith) {
        if (this == ANY) {
            return compareWith == ANY;
        }
        Platform family = family();
        return this == compareWith || compareWith == ANY
                || (family != null && family.is(compareWith));
    }

    /** The names an {@code os.name} or a capability may give it. */
    public String[] getPartOfOsName() {
        return Arrays.copyOf(partOfOsName, partOfOsName.length);
    }

    @Override
    public String toString() {
        return text;
    }
}
