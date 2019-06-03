package com.vaadin.testbench.addons.junit5.extensions.container;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

public class ContainerInfo {

    private String host;
    private Integer port;
    private String webapp;
    private InitializationScope initializationScope;

    public ContainerInfo(String host,
                         Integer port,
                         String webapp,
                         InitializationScope initializationScope) {
        this.host = host;
        this.port = port;
        this.webapp = webapp;
        this.initializationScope = initializationScope;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getWebapp() {
        return  webapp;
    }

    public void setWebapp(String webapp) {
        this.webapp = webapp;
    }

    public InitializationScope getInitializationScope() {
        return initializationScope;
    }

    public void setInitializationScope(InitializationScope initializationScope) {
        this.initializationScope = initializationScope;
    }

    public enum InitializationScope {

        BEFORE_EACH,
        BEFORE_ALL
    }
}
