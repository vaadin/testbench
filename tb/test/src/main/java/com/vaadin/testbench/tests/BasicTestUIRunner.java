package com.vaadin.testbench.tests;

import org.apache.meecrowave.Meecrowave;

public class BasicTestUIRunner {

    private BasicTestUIRunner() {
    }

    public static void main(String[] args) {
        final Meecrowave.Builder builder = new Meecrowave.Builder();
        builder.setHttpPort(8080);
        builder.setHttp2(true);

        final Meecrowave meecrowave = new Meecrowave(builder);
        meecrowave.bake();
        meecrowave.await();
    }
}
