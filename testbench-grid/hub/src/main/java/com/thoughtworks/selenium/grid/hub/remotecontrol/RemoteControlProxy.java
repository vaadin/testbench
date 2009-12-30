package com.thoughtworks.selenium.grid.hub.remotecontrol;

import com.thoughtworks.selenium.grid.HttpParameters;
import com.thoughtworks.selenium.grid.Response;
import com.thoughtworks.selenium.grid.HttpClient;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Local interface to a real remote control running somewhere in the grid.
 */
public class RemoteControlProxy {

    private final int concurrentSessionMax;
    private int concurrentSessionCount;
    private final HttpClient httpClient;
    private final String environment;
    private final String host;
    private final int port;
    
    // Timer for stuck RemoteControls
    private Timer wdt;
    private String session;
    private int waitTime;
    private GlobalRemoteControlPool pool;
    private final RemoteControlProxy RC;


    public RemoteControlProxy(String host, int port, String environment, int concurrentSessionMax, HttpClient httpClient) {
        if (null == host) {
            throw new IllegalArgumentException("host cannot be null");
        }
        if (null == environment) {
            throw new IllegalArgumentException("environment cannot be null");
        }
        this.host = host;
        this.port = port;
        this.environment = environment;
        this.concurrentSessionMax = concurrentSessionMax;
        this.concurrentSessionCount = 0;
        this.httpClient = httpClient;
        // Set timer properties
        RC = this;
        wdt = new Timer();
        waitTime = 1000*60*3;
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public String environment() {
        return environment;
    }

    public String remoteControlURL() {
        return "http://" + host + ":" + port + "/selenium-server/driver/";
    }

    public Response forward(HttpParameters parameters) throws IOException {
        // If no session return failure Response for timeout.
        if(concurrentSessionCount == 0){
            return new Response("Test failed due to timeout.");
        }
        // Reset timer only if we have a session
        if(session != null){
            resetTimer();
        }
        return httpClient.post(remoteControlURL(), parameters);
    }

    public String toString() {
        return "[RemoteControlProxy " + host + ":" + port + " " + environment + " "
                                      + concurrentSessionCount  + "/" + concurrentSessionMax + "]";
    }

    // Added environment to equals so same RC can register multiple environments
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final RemoteControlProxy otherRemoteControl = (RemoteControlProxy) other;
        return host.equals(otherRemoteControl.host)
                && port == otherRemoteControl.port && environment.equals(otherRemoteControl.environment);
    }

    public int hashCode() {
        return (host + port).hashCode();
    }

    public int concurrentSessionsMax() {
        return concurrentSessionMax;
    }

    public int concurrentSesssionCount() {
        return concurrentSessionCount;
    }

    public void registerNewSession() {
        if (concurrentSessionCount == concurrentSessionMax) {
            throw new IllegalStateException("Exceeded concurrent session max for " + toString());
        }
        concurrentSessionCount += 1;
        // Schedule watch dog to 3 minutes
        wdt.schedule(new WatchDog(), waitTime);
    }

    public void unregisterSession() {
        if (0 == concurrentSessionCount) {
            throw new IllegalStateException("Unregistering session on an idle remote control : " + toString());
        }
        concurrentSessionCount -= 1;
        clearTimer();
    }

    public boolean canHandleNewSession() {
        return concurrentSessionCount < concurrentSessionMax;
    }
    
    public void resetTimer(){
        wdt.cancel();
        wdt.purge();
        wdt = new Timer();
        wdt.schedule(new WatchDog(), waitTime);
    }
    
    public void clearTimer(){
        session = null;
        // Terminate timer and create a new one
        wdt.cancel();
        wdt = new Timer();
    }
    
    public void setSession(String sessionId){
        session = sessionId;
    }
    
    public void setPool(GlobalRemoteControlPool pool){
        this.pool = pool;
    }
    
    // If timer is not reset or cleared before time runs out release this session
    private class WatchDog extends TimerTask{
        public void run(){
            try{
                System.out.println("Sending finish to RC and releasing session " + session);
                HttpParameters parameters = new HttpParameters();
                parameters.put("cmd", "testComplete");
                parameters.put("sessionId", session);
                while(httpClient.isInUse()){
                    Thread.sleep(100);
                }
                // Send testComplete to Remote Control
                httpClient.post(remoteControlURL(), parameters);
                // Release session and free Remote Control
                pool.releaseForSession(session);
            }catch(Exception e){
            }
        }
    }
}
