/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.time.temporal.ChronoUnit.SECONDS;

public class WaitForDevBundle {

    public static void main(String... args) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        HttpClient client = HttpClient.newBuilder().executor(executor).build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/PageObjectView"))
                .timeout(Duration.of(3, SECONDS)).GET().build();

        System.out.print("Wait for dev-bundle creation ");
        int maxAttempts = Integer.getInteger("test.waitForDevBundle.attempts",
                60);
        long sleepTime = Long.getLong("test.waitForDevBundle.sleepMillis",
                1000);
        Exception exceptionHolder;
        int remainingAttempts = maxAttempts;
        do {
            HttpResponse<String> response = null;
            System.out.print(".");
            try {
                response = client.send(request,
                        HttpResponse.BodyHandlers.ofString());
                exceptionHolder = null;
            } catch (Exception e) {
                exceptionHolder = e;
            } finally {
                remainingAttempts--;
            }
            if (response != null && response.headers()
                    .firstValue("X-DevModePending").isEmpty()) {
                remainingAttempts = -1;
            } else {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }

        } while (remainingAttempts > 0);

        executor.shutdownNow();

        System.out.println();
        if (exceptionHolder != null) {
            System.out.println("Error while waiting for bundle creation");
            throw exceptionHolder;
        }
        if (remainingAttempts == -1) {
            System.out.println("Dev-bundle created");
        } else {
            System.out.println("Dev-bundle is still under creation after "
                    + Duration.ofMillis(maxAttempts * sleepTime).toSeconds()
                    + " seconds. Proceed anyway.");
        }
    }

}
