package com.thoughtworks.selenium.grid.remotecontrol;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.server.SeleniumServer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/*
 * Selenium Remote Control that registers/unregisters itself to a central Hub when it starts/stops.
 *
 * @author Philippe Hanrigou
 */
public class SelfRegisteringRemoteControl {


    private static final Log logger = LogFactory.getLog(SelfRegisteringRemoteControlLauncher.class);
    private final String seleniumHubURL;
    private final String environment;
    private final String host;
    private final String port;
    private final Timer timer;
    
    public SelfRegisteringRemoteControl(String seleniumHubURL, String environment, String host, String port) {
        this.seleniumHubURL = seleniumHubURL;
        this.environment = environment;
        this.host = host;
        this.port = port;
        timer = new Timer();
        timer.schedule(new ConnectionTest(), 1000*60, 1000*60);
    }

    public void register() throws IOException {
        int status = 0;
        do{
            try{
                status = new RegistrationRequest(seleniumHubURL, host, port, environment).execute();
            }catch(Exception e){
                status = 0;
                logger.info("Hub seems to be down. Retrying connection.");
            }
        }while(status != 200);
    }

    public void unregister() throws IOException {
        new UnregistrationRequest(seleniumHubURL, host, port, environment).execute();
    }

    public String hubURL() {
        return seleniumHubURL;
    }

    public String environment() {
        return environment;
    }

    public String host() {
        return host;
    }

    public String port() {
        return port;
    }


    public void launch(String[] args) throws Exception {
        logger.info("Starting selenium server with options:");
        for (String arg : args) {
            logger.info(arg);
        }
        logger.info("Running Build @build@");
        SeleniumServer.main(args);
    }

    protected void ensureUnregisterOnExit() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    unregister();
                } catch (IOException e) {
                    logger.error("Could not unregister " + this, e);
                }
            }
        });
    }
    
    private class ConnectionTest extends TimerTask{
        public void run(){
            try{
                int status = this.execute();
            }catch(Exception e){
                logger.info("Hub seems to be down. Retrying connection in a moment.");
            }
        }
        
        public int execute() throws IOException {
            return new HttpClient().executeMethod(postMethod());
        }

        public PostMethod postMethod() {
            final PostMethod postMethod = new PostMethod(seleniumHubURL + "/registration-manager/remotecontrolstatus");

            postMethod.addParameter("host", host);
            postMethod.addParameter("port", port);
            postMethod.addParameter("environment", environment);

            return postMethod;
        }
    }
}
