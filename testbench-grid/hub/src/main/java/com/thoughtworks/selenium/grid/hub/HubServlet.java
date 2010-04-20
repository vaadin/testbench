package com.thoughtworks.selenium.grid.hub;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.selenium.grid.HttpParameters;
import com.thoughtworks.selenium.grid.Response;
import com.thoughtworks.selenium.grid.hub.remotecontrol.DynamicRemoteControlPool;
import com.thoughtworks.selenium.grid.hub.remotecontrol.commands.SeleneseCommand;

/**
 * Main entry point for the Hub and the Selenium Farm.
 * Load balance selense requests accross a farm of remote control.
 */
public class HubServlet extends HttpServlet {

    private final static Log LOGGER = LogFactory.getLog(HubServer.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final Response remoteControlResponse;
        final HubRegistry registry;
        final HttpParameters parameters;

        registry = HubRegistry.registry();
        parameters = requestParameters(request);
        remoteControlResponse = forward(parameters, registry.remoteControlPool(), registry.environmentManager());
        reply(response, remoteControlResponse);
    }

    protected Response forward(HttpParameters parameters, DynamicRemoteControlPool pool, EnvironmentManager environmentManager) throws IOException {
        final SeleneseCommand command;
        final Response response;

        LOGGER.info("Processing '" + parameters.toString() + "'");
        try {
            command = new HttpCommandParser(parameters).parse(environmentManager);
            response = command.execute(pool);
        } catch (CommandParsingException e) {
            LOGGER.error("Failed to parse '" + parameters.toString() + "' : " + e.getMessage());
            return new Response(e.getMessage());
        } catch (NoSuchEnvironmentException e) {
            LOGGER.error("Could not find any remote control providing the '" + e.environment() +
                         "' environment. Please make sure you started some remote controls which registered as offering this environment.");
            return new Response(e.getMessage());
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("Responding with " + response.statusCode() + "/ '"
                    + response.body() + "'");
        }

        return response;
    }

    protected void reply(HttpServletResponse response, Response remoteControlResponse) throws IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(remoteControlResponse.statusCode());
        response.getWriter().print(remoteControlResponse.body());
    }

    @SuppressWarnings({"unchecked"})
    protected HttpParameters requestParameters(HttpServletRequest request) {
        final HttpParameters parameters;
        parameters = new HttpParameters(request.getParameterMap());
        return parameters;
    }

}
