package com.thoughtworks.selenium.grid;

import java.io.IOException;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
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
        this(new org.apache.commons.httpclient.HttpClient());
    }

    public Response get(String url) throws IOException {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ie) {
            logger.debug("Thread interrupted. " + ie.getMessage());
        }
        return request(new GetMethod(url));
    }

    public Response post(String url, HttpParameters parameters)
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
        final String body;
        try {
            HttpConnectionParams parameters = client.getHttpConnectionManager()
                    .getParams();
            parameters.setSoTimeout(60000);
            statusCode = client.executeMethod(method);
            // Get response body as a byte[] from InputStream
            byte[] response = IOUtils.toByteArray(method
                    .getResponseBodyAsStream());
            body = new String(response, "utf-8");
            return new Response(statusCode, body);
            // Catch exceptions and end test by creating a Response(String);
        } catch (java.net.SocketTimeoutException e) {
            return new Response("Socket response timedout.");
        } catch (java.net.SocketException e) {
            return new Response("Socket exception. " + e.getMessage());
        } catch (IOException e) {
            return new Response("Problem occured. " + e.getMessage());
        } finally {
            method.releaseConnection();
        }
    }
}
