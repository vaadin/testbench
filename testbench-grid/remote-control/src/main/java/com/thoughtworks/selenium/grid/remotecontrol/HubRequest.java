package com.thoughtworks.selenium.grid.remotecontrol;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Selenium Remote Control Request to Grid Hub.
 */
public class HubRequest {

    private static final Log logger = LogFactory.getLog(HubRequest.class);

    private final String environment;
    private final String targetURL;
    private final String host;
    private final String port;

    public HubRequest(String targetURL, String host, String port,
            String environment) {
        this.targetURL = targetURL;
        this.environment = environment;
        this.host = host;
        this.port = port;
    }

    public int execute() throws IOException {
        return new HttpClient().executeMethod(postMethod());
    }

    public PostMethod postMethod() {
        final PostMethod postMethod = new PostMethod(targetURL);
        postMethod.addParameter("host", host);
        postMethod.addParameter("port", port);
        postMethod.addParameter("environment", environment);
        postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                retryhandler);

        return postMethod;
    }

    public String targetURL() {
        return targetURL;
    }

    public String environment() {
        return environment;
    }

    HttpMethodRetryHandler retryhandler = new HttpMethodRetryHandler() {
        public boolean retryMethod(final HttpMethod method,
                final IOException exception, int executionCount) {
            try {
                logger.info("Could not connect to hub. Retrying after 5s.");
                Thread.sleep(5000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }

            return true;
        }
    };
}
