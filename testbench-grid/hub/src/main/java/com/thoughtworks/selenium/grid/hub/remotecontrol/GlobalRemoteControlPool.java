package com.thoughtworks.selenium.grid.hub.remotecontrol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.selenium.grid.hub.Environment;
import com.thoughtworks.selenium.grid.hub.NoSuchEnvironmentException;
import com.thoughtworks.selenium.grid.hub.NoSuchSessionException;

/**
 * Monolithic Remote Control Pool keeping track of all environment and all
 * sessions.
 */
public class GlobalRemoteControlPool implements DynamicRemoteControlPool {

    private static final Log LOGGER = LogFactory
            .getLog(GlobalRemoteControlPool.class);
    private final Map<String, RemoteControlSession> remoteControlsBySessionIds;
    private final Map<String, RemoteControlProvisioner> provisionersByHash;

    public GlobalRemoteControlPool() {
        remoteControlsBySessionIds = new HashMap<String, RemoteControlSession>();
        provisionersByHash = new HashMap<String, RemoteControlProvisioner>();
    }

    public void register(RemoteControlProxy newRemoteControl) {
        final RemoteControlProvisioner provisioner;

        synchronized (provisionersByHash) {
            if (null == getProvisioner(newRemoteControl.hashCode())) {
                createNewProvisionerForHash(newRemoteControl.hashCode());
            }
            provisioner = getProvisioner(newRemoteControl.hashCode());
            provisioner.add(newRemoteControl);
        }
    }

    public boolean unregister(RemoteControlProxy remoteControl) {

        boolean status = false;

        try {
            synchronized (provisionersByHash) {
                synchronized (remoteControlsBySessionIds) {
                    Set<RemoteControlSession> sessionsToRemove = new HashSet<RemoteControlSession>();

                    status = getProvisioner(remoteControl.hashCode()).remove(
                            remoteControl);

                    for (RemoteControlSession session : remoteControlsBySessionIds
                            .values()) {
                        if (session.remoteControl().equals(remoteControl)) {
                            sessionsToRemove.add(session);
                        }
                    }

                    // Remove the session separately from the loop where we
                    // found it to avoid issues with concurrent modification.
                    for (RemoteControlSession session : sessionsToRemove) {
                        removeFromSessionMap(session);
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
        do {
            provisioner = getProvisioner(environment.name());
            if (null == provisioner) {
                throw new NoSuchEnvironmentException(environment.name());
            }

            reserved = provisioner.reserve(environment.name());
        } while (reserved == null);

        return reserved;
    }

    public void associateWithSession(RemoteControlProxy remoteControl,
            String sessionId) {
        LOGGER.info("Associating session id='" + sessionId + "' =>"
                + remoteControl + " for environment "
                + remoteControl.environment());

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
        remoteControl.terminateSession(sessionId);
    }

    public void releaseForSessionWithoutTerminate(String sessionId) {
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

    public List<RemoteControlProxy> allRegisteredRemoteControls() {
        final List<RemoteControlProxy> allRemoteControls;

        allRemoteControls = new LinkedList<RemoteControlProxy>();
        synchronized (provisionersByHash) {
            for (RemoteControlProvisioner provisioner : provisionersByHash
                    .values()) {
                allRemoteControls.addAll(provisioner.allRemoteControls());
            }
        }

        return allRemoteControls;
    }

    // used by heartbeat only
    public boolean isRegistered(RemoteControlProxy remoteControl) {
        for (RemoteControlProvisioner provisioner : provisionersByHash.values()) {
            if (provisioner
                    .contains(remoteControl.host(), remoteControl.port())) {
                return true;
            }
        }
        return false;
    }

    protected RemoteControlProvisioner getProvisioner(String environment) {
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
        if (haveEnvironment.size() > 1) {
            leastWaits = haveEnvironment.get(new Random()
                    .nextInt(haveEnvironment.size()));

            for (RemoteControlProvisioner provisioner : haveEnvironment) {
                if (provisioner.amountWaiting() < leastWaits.amountWaiting()) {
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
        if (null == session) {
            throw new NoSuchSessionException(sessionId);
        }

        return session.remoteControl();
    }

    protected RemoteControlSession getRemoteControlSession(String sessionId) {
        return remoteControlsBySessionIds.get(sessionId);
    }

    protected void removeFromSessionMap(RemoteControlSession session) {
        // Use a real iterator to avoid issues with concurrent modification.
        for (final Iterator<Map.Entry<String, RemoteControlSession>> it = remoteControlsBySessionIds
                .entrySet().iterator(); it.hasNext();) {
            final Map.Entry<String, RemoteControlSession> entry = it.next();

            if (entry.getValue().equals(session)) {
                it.remove();
            }
        }
    }

    protected void logSessionMap() {
        for (Map.Entry<String, RemoteControlSession> entry : remoteControlsBySessionIds
                .entrySet()) {
            LOGGER.debug(entry.getKey() + " => " + entry.getValue());
        }
    }

    protected void createNewProvisionerForHash(int hash) {
        provisionersByHash.put(Integer.toString(hash),
                new RemoteControlProvisioner());
    }

    protected void removeProvisionerForHash(int hash) {
        provisionersByHash.remove(Integer.toString(hash));
    }

    public void unregisterAllUnresponsiveRemoteControls() {
        for (RemoteControlProxy rc : allRegisteredRemoteControls()) {
            unregisterRemoteControlIfUnreliable(rc);
        }
    }

    protected void unregisterRemoteControlIfUnreliable(RemoteControlProxy rc) {
        int response = rc.unreliable();
        if (response != 200) {
            LOGGER.error("RC detected as unreliable: " + rc);
            // LOGGER.warn("Unregistering unreliable RC " + rc);
            // TODO cannot really stop the test in progress, may "get stuck"
            // on the RC
            // TODO this would leave other RCPs on the RC active
            // unregister(rc);
            // not unregistering but releasing; could alternatively
            // unregister all RCPs on the RC
            // release(rc);
            for (RemoteControlSession session : iteratorSafeRemoteControlSessions()) {
                if (session.remoteControl().equals(rc)) {
                    if (response == 503 || response == 408) {
                        // If connection timed out or we don't have route to
                        // host don't try to send RC end session command.
                        releaseForSessionWithoutTerminate(session.sessionId());
                    } else {
                        releaseForSession(session.sessionId());
                    }
                    break;
                }
            }
            // After possible session released unregister
            // unresponsive rc.
            unregister(rc);
        }
    }

    public void updateSessionLastActiveAt(String sessionId) {
        RemoteControlSession session = getRemoteControlSession(sessionId);
        if (session != null) {
            session.updateLastActiveAt();
        }
    }

    public void recycleAllSessionsIdleForTooLong(double maxIdleTimeInSeconds) {
        for (RemoteControlSession session : iteratorSafeRemoteControlSessions()) {
            recycleSessionIfIdleForTooLong(session, maxIdleTimeInSeconds);
        }
    }

    public Set<RemoteControlSession> iteratorSafeRemoteControlSessions() {
        final Set<RemoteControlSession> iteratorSafeCopy;

        iteratorSafeCopy = new HashSet<RemoteControlSession>();
        synchronized (remoteControlsBySessionIds) {
            for (Map.Entry<String, RemoteControlSession> entry : remoteControlsBySessionIds
                    .entrySet()) {
                iteratorSafeCopy.add(entry.getValue());
            }
        }
        return iteratorSafeCopy;
    }

    public void recycleSessionIfIdleForTooLong(RemoteControlSession session,
            double maxIdleTimeInSeconds) {
        final int maxIdleTImeInMilliseconds;

        maxIdleTImeInMilliseconds = (int) (maxIdleTimeInSeconds * 1000);
        if (session.innactiveForMoreThan(maxIdleTImeInMilliseconds)) {
            LOGGER.warn("Releasing session IDLE for more than "
                    + maxIdleTimeInSeconds + " seconds: " + session);
            releaseForSession(session.sessionId());
        }
    }

}
