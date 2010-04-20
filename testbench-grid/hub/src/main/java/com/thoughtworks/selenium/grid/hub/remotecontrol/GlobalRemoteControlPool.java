package com.thoughtworks.selenium.grid.hub.remotecontrol;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.selenium.grid.hub.Environment;
import com.thoughtworks.selenium.grid.hub.NoSuchEnvironmentException;

/**
 * Monolithic Remote Control Pool keeping track of all environment and all
 * sessions.
 */
public class GlobalRemoteControlPool implements DynamicRemoteControlPool {

    private static final Log LOGGER = LogFactory
            .getLog(GlobalRemoteControlPool.class);
    private final Map<String, RemoteControlSession> remoteControlsBySessionIds;
    private final Map<String, RemoteControlProvisioner> provisionersByEnvironment;
    private final Map<String, RemoteControlProvisioner> provisionersByHash;
    
    public GlobalRemoteControlPool() {
        remoteControlsBySessionIds = new HashMap<String, RemoteControlSession>();
        provisionersByEnvironment = new HashMap<String, RemoteControlProvisioner>();
        provisionersByHash = new HashMap<String, RemoteControlProvisioner>();
    }

    public void register(RemoteControlProxy newRemoteControl) {
        final RemoteControlProvisioner provisioner;

        // synchronized(provisionersByEnvironment) {
        // if (null == getProvisioner(newRemoteControl.environment())) {
        // createNewProvisionerForEnvironment(newRemoteControl.environment());
        // }
        // provisioner = getProvisioner(newRemoteControl.environment());
        // provisioner.add(newRemoteControl);
        // }
        synchronized (provisionersByHash) {
            if (null == getProvisioner(newRemoteControl.hashCode())) {
                createNewProvisionerForHash(newRemoteControl.hashCode());
            }
            provisioner = getProvisioner(newRemoteControl.hashCode());
            provisioner.add(newRemoteControl);
        }
    }

    // Check that remote control is registered on hub and register if not.
    public void status(List<RemoteControlProxy> checkRemoteControls){
        final RemoteControlProvisioner provisioner;
        synchronized (provisionersByHash){
            if(null == getProvisioner(checkRemoteControls.get(0).hashCode())){
                createNewProvisionerForHash(checkRemoteControls.get(0).hashCode());
                provisioner = getProvisioner(checkRemoteControls.get(0).hashCode());
                for(RemoteControlProxy RCProxy: checkRemoteControls){
                    provisioner.confirm(RCProxy);
                }
            }
        }
    }
    
    public boolean unregister(RemoteControlProxy remoteControl) {

        boolean status = false;
        
        try {
            synchronized (provisionersByHash) {
                synchronized (remoteControlsBySessionIds) {
                    status = getProvisioner(remoteControl.hashCode()).remove(
                            remoteControl);
                    for (RemoteControlSession session : remoteControlsBySessionIds
                            .values()) {
                        if (session.remoteControl().equals(remoteControl)) {
                            removeFromSessionMap(session);
                        }
                    }
                }
            }
        } catch (NullPointerException npe) {
            LOGGER.debug("RemoteController not registered for hub");
        }

        return status;
    }

    public RemoteControlProxy reserve(Environment environment) {
        RemoteControlProvisioner provisioner;
        RemoteControlProxy reserved = null;
        // check provisioners if reservation returns as null.
        do{
            provisioner = getProvisioner(environment.name());
            if (null == provisioner) {
                throw new NoSuchEnvironmentException(environment.name());
            }
    
            reserved = provisioner.reserve(environment.name());
        }while(reserved == null);
        
        return reserved;
    }

    public void associateWithSession(RemoteControlProxy remoteControl,
            String sessionId) {
        LOGGER.info("Associating session id='" + sessionId + "' =>"
                + remoteControl + " for environment "
                + remoteControl.environment());
        
        remoteControl.setSession(sessionId);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Asssociating " + sessionId + " => " + remoteControl);
        }
        synchronized (remoteControlsBySessionIds) {
            if (remoteControlsBySessionIds.containsKey(sessionId)) {
                throw new IllegalStateException("Session '" + sessionId
                        + "' is already asssociated with "
                        + remoteControlsBySessionIds.get(sessionId));
            }
            synchronized (remoteControlsBySessionIds) {
                final RemoteControlSession newSession;
  
                newSession = new RemoteControlSession(sessionId, remoteControl);
                remoteControlsBySessionIds.put(sessionId, newSession);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            logSessionMap();
        }
    }

    public RemoteControlProxy retrieve(String sessionId) {
        return getRemoteControlForSession(sessionId);
    }

    // Get remote control provisioner by RC hash and release RC.
    public void release(RemoteControlProxy remoteControl) {
        getProvisioner(remoteControl.hashCode()).release(remoteControl);
    }

    public void releaseForSession(String sessionId) {
        LOGGER.info("Releasing pool for session id='" + sessionId + "'");

        final RemoteControlProxy remoteControl;
        remoteControl = getRemoteControlForSession(sessionId);

        synchronized (remoteControlsBySessionIds) {
            remoteControlsBySessionIds.remove(sessionId);
        }
        getProvisioner(remoteControl.environment()).release(remoteControl);
    }

    public List<RemoteControlProxy> availableRemoteControls() {
        final List<RemoteControlProxy> availableRemoteControls;

        availableRemoteControls = new LinkedList<RemoteControlProxy>();
        synchronized (provisionersByHash) {
            for (RemoteControlProvisioner provisioner : provisionersByHash
                    .values()) {
                availableRemoteControls.addAll(provisioner
                        .availableRemoteControls());
            }
        }
        return availableRemoteControls;
    }

    public List<RemoteControlProxy> reservedRemoteControls() {
        final List<RemoteControlProxy> reservedRemoteControls;

        reservedRemoteControls = new LinkedList<RemoteControlProxy>();
        synchronized (provisionersByHash) {
            for (RemoteControlProvisioner provisioner : provisionersByHash
                    .values()) {
                reservedRemoteControls.addAll(provisioner
                        .reservedRemoteControls());
            }
        }
        return reservedRemoteControls;
    }

    protected RemoteControlProvisioner getProvisioner(String environment) {
        // return provisionersByEnvironment.get(environment);
        
        List<RemoteControlProvisioner> haveEnvironment = new LinkedList<RemoteControlProvisioner>();
        
        // get provisioners with wanted environment
        for (RemoteControlProvisioner provisioner : provisionersByHash.values()) {
            if (provisioner.hasEnvironment(environment)) {
                haveEnvironment.add(provisioner);
            }
        }

        // Return if no matching environment was found
        if (haveEnvironment.isEmpty()) {
            return null;
        }
        
        // check for provisioner with possible free environment
        for (RemoteControlProvisioner provisioner : haveEnvironment) {
            if (provisioner.rcFree()) {
                return provisioner;
            }
        }
        
        // check for provisioner with the least waiting reservations.
        RemoteControlProvisioner leastWaits = haveEnvironment.get(0);
        if(haveEnvironment.size() > 1){
            leastWaits = haveEnvironment.get(new Random().nextInt(haveEnvironment.size()));
        
            for(RemoteControlProvisioner provisioner : haveEnvironment) {
                if(provisioner.amountWaiting() < leastWaits.amountWaiting()){
                    leastWaits = provisioner;
                }
            }
        }
        
        return leastWaits;
    }

    protected RemoteControlProvisioner getProvisioner(int hash) {
        synchronized (provisionersByHash) {
            return provisionersByHash.get(Integer.toString(hash));
        }
    }

    protected RemoteControlProxy getRemoteControlForSession(String sessionId) {
        final RemoteControlSession session;

        session = getRemoteControlSession(sessionId);
        return (null == session) ? null : session.remoteControl();
    }

    protected RemoteControlSession getRemoteControlSession(String sessionId) {
        return remoteControlsBySessionIds.get(sessionId);
    }

    protected void removeFromSessionMap(RemoteControlSession session) {
        for (Map.Entry<String, RemoteControlSession> entry : remoteControlsBySessionIds
                .entrySet()) {
            if (entry.getValue().equals(session)) {
                remoteControlsBySessionIds.remove(entry.getKey());
            }
        }
    }

    protected void logSessionMap() {
        for (Map.Entry<String, RemoteControlSession> entry : remoteControlsBySessionIds
                .entrySet()) {
            LOGGER.debug(entry.getKey() + " => " + entry.getValue());
        }
    }

    protected void createNewProvisionerForEnvironment(String environemntName) {
        provisionersByEnvironment.put(environemntName,
                new RemoteControlProvisioner());
    }

    protected void createNewProvisionerForHash(int hash) {
        provisionersByHash.put(Integer.toString(hash),
                new RemoteControlProvisioner());
    }

    public void updateSessionLastActiveAt(String sessionId) {
        getRemoteControlSession(sessionId).updateLastActiveAt();
    }

}
