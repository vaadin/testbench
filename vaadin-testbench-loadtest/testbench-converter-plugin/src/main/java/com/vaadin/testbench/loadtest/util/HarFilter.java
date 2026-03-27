/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * Filters out requests to external domains from a HAR file. This removes
 * browser background traffic (Google services, telemetry, etc.) that isn't part
 * of the application under test.
 */
public class HarFilter {

    private static final List<String> EXTERNAL_DOMAINS = List.of(
            // Google services (browser background requests)
            "google.com", "googleapis.com", "gstatic.com",
            "googleusercontent.com", "google-analytics.com",
            // Mozilla services
            "mozilla.com", "mozilla.org", "firefox.com",
            // Microsoft services
            "microsoft.com", "msn.com", "live.com",
            // Apple services
            "apple.com", "icloud.com",
            // Common CDNs and analytics (usually not part of app testing)
            "cloudflare.com", "akamai.net", "fastly.net");

    private static final Logger log = Logger
            .getLogger(HarFilter.class.getName());
    private final ObjectMapper objectMapper;

    /**
     * Creates a new HAR filter with a configured Jackson ObjectMapper.
     */
    public HarFilter() {
        JsonFactory jsonFactory = JsonFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder()
                        .maxStringLength(Integer.MAX_VALUE).build())
                .build();
        this.objectMapper = JsonMapper.builder(jsonFactory)
                .enable(SerializationFeature.INDENT_OUTPUT).build();
    }

    /**
     * Filters a HAR file in place, removing requests to external domains.
     *
     * @param harFile
     *            the path to the HAR file
     * @return the filter result with statistics
     * @throws IOException
     *             if reading or writing fails
     */
    public FilterResult filter(Path harFile) throws IOException {
        return filter(harFile, harFile);
    }

    /**
     * Filters a HAR file, removing requests to external domains.
     *
     * @param inputFile
     *            the input HAR file
     * @param outputFile
     *            the output HAR file (can be same as input)
     * @return the filter result with statistics
     * @throws IOException
     *             if reading or writing fails
     */
    public FilterResult filter(Path inputFile, Path outputFile)
            throws IOException {
        log.info("Filtering external domains from HAR file...");

        HarFile har = objectMapper.readValue(inputFile.toFile(), HarFile.class);

        int originalCount = har.log().entries().size();
        List<HarEntry> filteredEntries = new ArrayList<>();

        for (HarEntry entry : har.log().entries()) {
            if (entry.request() != null && entry.request().url() != null) {
                String url = entry.request().url();
                if (isExternalDomain(url)) {
                    String truncatedUrl = url.length() > 80
                            ? url.substring(0, 80) + "..."
                            : url;
                    log.fine("  Filtered: " + truncatedUrl);
                    continue;
                }
                // Filter Vaadin session unload requests (sent when browser tab
                // closes)
                if (isUnloadRequest(entry)) {
                    log.fine("  Filtered UNLOAD request: " + url);
                    continue;
                }
            }
            filteredEntries.add(entry);
        }

        // Create new HAR with filtered entries
        HarFile filteredHar = new HarFile(new HarLog(har.log().version(),
                har.log().creator(), filteredEntries, har.log().pages()));

        objectMapper.writeValue(outputFile.toFile(), filteredHar);

        int filteredCount = originalCount - filteredEntries.size();
        int remainingCount = filteredEntries.size();

        log.info("Done! " + filteredCount + " of " + originalCount
                + " requests filtered.");
        log.info("Remaining: " + remainingCount + " requests");

        return new FilterResult(originalCount, filteredCount, remainingCount);
    }

    /**
     * Check if a request is a Vaadin session unload (sent when the browser tab
     * closes). These contain {@code "UNLOAD":true} in the POST body and should
     * not be replayed.
     */
    private boolean isUnloadRequest(HarEntry entry) {
        if (entry.request().postData() != null
                && entry.request().postData().text() != null) {
            return entry.request().postData().text()
                    .contains("\"UNLOAD\":true");
        }
        return false;
    }

    /**
     * Check if a URL belongs to an external domain that should be filtered.
     */
    private boolean isExternalDomain(String url) {
        try {
            URI uri = new URI(url);
            String hostname = uri.getHost();
            if (hostname == null) {
                return false;
            }
            hostname = hostname.toLowerCase();

            for (String domain : EXTERNAL_DOMAINS) {
                if (hostname.equals(domain)
                        || hostname.endsWith("." + domain)) {
                    return true;
                }
            }
            return false;
        } catch (URISyntaxException e) {
            // If URL parsing fails, keep the entry
            return false;
        }
    }

    /**
     * Result of filtering operation.
     */
    public record FilterResult(int originalCount, int filteredCount,
            int remainingCount) {
    }

    // HAR format DTOs using Java records
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HarFile(HarLog log) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HarLog(String version, HarCreator creator,
            List<HarEntry> entries, List<HarPage> pages) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HarCreator(String name, String version) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HarPage(String startedDateTime, String id, String title,
            HarPageTimings pageTimings) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HarPageTimings(Double onContentLoad, Double onLoad) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HarEntry(String startedDateTime, Double time,
            HarRequest request, HarResponse response, HarCache cache,
            HarTimings timings, String serverIPAddress, String connection,
            String pageref) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HarRequest(String method, String url, String httpVersion,
            List<HarHeader> headers, List<HarQueryString> queryString,
            List<HarCookie> cookies, Integer headersSize, Integer bodySize,
            HarPostData postData) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HarResponse(Integer status, String statusText,
            String httpVersion, List<HarHeader> headers,
            List<HarCookie> cookies, HarContent content, String redirectURL,
            Integer headersSize, Integer bodySize) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HarHeader(String name, String value) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HarQueryString(String name, String value) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HarCookie(String name, String value, String path,
            String domain, String expires, Boolean httpOnly, Boolean secure) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HarPostData(String mimeType, String text,
            List<HarParam> params) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HarParam(String name, String value, String fileName,
            String contentType) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HarContent(Long size, Long compression, String mimeType,
            String text, String encoding) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HarCache(HarCacheEntry beforeRequest,
            HarCacheEntry afterRequest) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HarCacheEntry(String expires, String lastAccess, String eTag,
            Integer hitCount) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HarTimings(Double blocked, Double dns, Double connect,
            Double send, @JsonProperty("wait") Double waitTime, Double receive,
            Double ssl) {
    }
}
