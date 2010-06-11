package com.thoughtworks.selenium.grid;

import java.io.IOException;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Invoke HTTP GET requests and gather status code and text body for the
 * response. <br/>
 * Implementation is simplistic but should cover Selenium RC limited vocabulary.
 */
public class HttpClient {

    private static final Log logger = LogFactory.getLog(HttpClient.class);
    private final org.apache.commons.httpclient.HttpClient client;

    public HttpClient(org.apache.commons.httpclient.HttpClient client) {
        this.client = client;
    }

    public HttpClient() {
        this(
                new org.apache.commons.httpclient.HttpClient(
                        new org.apache.commons.httpclient.MultiThreadedHttpConnectionManager()));
    }

    public synchronized Response get(String url) throws IOException {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ie) {
            logger.debug("Thread interrupted. " + ie.getMessage());
        }
        return request(new GetMethod(url));
    }

    public synchronized Response post(String url, HttpParameters parameters)
            throws IOException {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ie) {
            logger.debug("Thread interrupted. " + ie.getMessage());
        }
        return request(buildPostMethod(url, parameters));
    }

    protected PostMethod buildPostMethod(String url, HttpParameters parameters) {
        final PostMethod postMethod;

        postMethod = new PostMethod(url);
        postMethod.setRequestHeader("Content-Type",
                "application/x-www-form-urlencoded; ; charset=UTF-8");
        for (String name : parameters.names()) {
            postMethod.setParameter(name, parameters.get(name));
        }
        return postMethod;
    }

    protected Response request(HttpMethod method) throws IOException {
        final int statusCode;
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                retryhandler);
        final String body;
        try {
            HttpConnectionParams parameters = client.getHttpConnectionManager()
                    .getParams();
            parameters.setSoTimeout(30000);
            statusCode = client.executeMethod(method);
            // Get response body as a byte[] from InputStream
            byte[] response = IOUtils.toByteArray(method
                    .getResponseBodyAsStream());
            body = new String(response, "utf-8");
            return new Response(statusCode, body);
            // Catch exceptions and end test by creating a Response(String);
        } catch (java.net.NoRouteToHostException e) {
            logger.warn("No route to host: " + method.getURI());
            return new Response(503, "No route to host");
        } catch (java.net.ConnectException e) {
            logger.warn("Connection timed out");
            return new Response(408, "Connection timed out");
        } catch (java.net.SocketTimeoutException e) {
            logger.warn("Socket response timed out while sending request");
            return new Response(408, "Socket response timedout."
                    + e.getMessage());
        } catch (java.net.SocketException e) {
            logger.warn("Socket exception while sending request");
            return new Response(400, "Socket exception. " + e.getMessage());
        } catch (IOException e) {
            logger.warn("Problem occurred while sending request");
            return new Response(500, "Problem occured. " + e.getMessage());
        } finally {
            method.releaseConnection();
        }
    }

    HttpMethodRetryHandler retryhandler = new HttpMethodRetryHandler() {
        public boolean retryMethod(final HttpMethod method,
                final IOException exception, int executionCount) {
            return false;
        }
    };
}
