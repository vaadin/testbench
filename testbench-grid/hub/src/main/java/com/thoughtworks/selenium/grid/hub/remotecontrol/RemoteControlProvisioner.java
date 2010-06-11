package com.thoughtworks.selenium.grid.hub.remotecontrol;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.selenium.grid.hub.HubRegistry;

/**
 * Central authority to track registered remote controls and grant exclusive
 * access to a remote control for a while.
 * <p/>
 * A client will block if it attempts to reserve a remote control and none is
 * available. The call will return as soon as a remote control becomes available
 * again.
 */
public class RemoteControlProvisioner {

    private static final Log LOGGER = LogFactory
            .getLog(RemoteControlProvisioner.class);
    private final List<RemoteControlProxy> remoteControls;
    private final Lock remoteControlListLock;
    private final Condition remoteControlAvailable;
    private static int waiting;

    public RemoteControlProvisioner() {
        remoteControls = new LinkedList<RemoteControlProxy>();
        remoteControlListLock = new ReentrantLock();
        remoteControlAvailable = remoteControlListLock.newCondition();
        waiting = 0;
    }

    public RemoteControlProxy reserve() {
        RemoteControlProxy remoteControl;

        try {
            remoteControlListLock.lock();

            if (remoteControls.isEmpty()) {
                return null;
            }

            remoteControl = blockUntilARemoteControlIsAvailableOrRequestTimesOut();
            if (null == remoteControl) {
                LOGGER
                        .info("Timed out waiting for a remote control for environment.");
                return null;
            }

            while (remoteControl.unreliable() != 200) {
                LOGGER
                        .warn("Reserved RC "
                                + remoteControl
                                + " is detected as unreliable, unregistering it and reserving a new one...");
                tearDownExistingRemoteControl(remoteControl);
                if (remoteControls.isEmpty()) {
                    return null;
                }
                remoteControl = blockUntilARemoteControlIsAvailableOrRequestTimesOut();
                if (null == remoteControl) {
                    LOGGER
                            .info("Timed out waiting for a remote control for environment.");
                    return null;
                }
            }
            remoteControl.registerNewSession();
            LOGGER.info("Reserved remote control" + remoteControl);
            return remoteControl;
        } finally {
            remoteControlListLock.unlock();
        }
    }

    // Reserve Remote Control with environment
    public RemoteControlProxy reserve(String environment) {
        final RemoteControlProxy remoteControl;

        try {
            remoteControlListLock.lock();
            waiting++;
            if (remoteControls.isEmpty()) {
                waiting--;
                return null;
            }

            remoteControl = blockUntilARemoteControlIsAvailable(environment);
            if (remoteControl != null) {
                remoteControl.registerNewSession();
                LOGGER.info("Reserved remote control" + remoteControl);
            }
            waiting--;
            return remoteControl;
        } finally {
            remoteControlListLock.unlock();
        }
    }

    public void release(RemoteControlProxy remoteControl) {
        try {
            remoteControlListLock.lock();
            remoteControl.unregisterSession();
            LOGGER.info("Released remote control" + remoteControl);
            signalThatARemoteControlHasBeenMadeAvailable();
        } finally {
            remoteControlListLock.unlock();
        }
    }

    public void add(RemoteControlProxy newRemoteControl) {
        try {
            remoteControlListLock.lock();
            if (remoteControls.contains(newRemoteControl)) {
                LOGGER.info("RemoteControl exists " + newRemoteControl);
                tearDownExistingRemoteControl(newRemoteControl);
            }
            remoteControls.add(newRemoteControl);
            signalThatARemoteControlHasBeenMadeAvailable();
        } finally {
            remoteControlListLock.unlock();
        }
    }

    /** Not Thread-safe */
    public boolean contains(String host, int port) {
        for (RemoteControlProxy proxy : remoteControls) {
            if (proxy != null && host != null && host.equals(proxy.host())
                    && port == proxy.port()) {
                return true;
            }
        }
        return false;
    }

    public void tearDownExistingRemoteControl(
            RemoteControlProxy newRemoteControl) {
        final RemoteControlProxy oldRemoteControl;

        oldRemoteControl = remoteControls.get(remoteControls
                .indexOf(newRemoteControl));
        if (oldRemoteControl.sessionInProgress()) {
            oldRemoteControl.unregisterSession();
        }
        remoteControls.remove(oldRemoteControl);
    }

    // If remoteControl exists do nothing else add remote control
    public void confirm(RemoteControlProxy checkRemoteControl) {
        try {
            remoteControlListLock.lock();
            if (remoteControls.contains(checkRemoteControl)) {
                return;
            }
            LOGGER.info("Registering RemoteControl "
                    + checkRemoteControl.toString()
                    + " to hub as it seems to have been lost.");
            remoteControls.add(checkRemoteControl);
            signalThatARemoteControlHasBeenMadeAvailable();
        } finally {
            remoteControlListLock.unlock();
        }
    }

    public boolean remove(RemoteControlProxy remoteControl) {
        try {
            remoteControlListLock.lock();
            return remoteControls.remove(remoteControl);
        } finally {
            remoteControlListLock.unlock();
        }
    }

    // Check if remote control has environment
    public boolean hasEnvironment(String environment) {
        for (RemoteControlProxy rc : remoteControls) {
            if (rc.environment().equals(environment)) {
                return true;
            }
        }
        return false;
    }

    public boolean rcFree() {
        return reservedRemoteControls().isEmpty();
    }

    public int amountWaiting() {
        return waiting;
    }

    /**
     * Not thread safe.
     * 
     * @return All available remote controls. Never null.
     */
    public List<RemoteControlProxy> availableRemoteControls() {
        final LinkedList<RemoteControlProxy> availableremoteControls;

        availableremoteControls = new LinkedList<RemoteControlProxy>();
        if (reservedRemoteControls().isEmpty()) {
            for (RemoteControlProxy remoteControl : remoteControls) {
                if (remoteControl.canHandleNewSession()) {
                    availableremoteControls.add(remoteControl);
                }
            }
        }
        return Arrays
                .asList(availableremoteControls
                        .toArray(new RemoteControlProxy[availableremoteControls
                                .size()]));
    }

    /**
     * Not thread safe.
     * 
     * @return All reserved remote controls. Never null.
     */
    public List<RemoteControlProxy> reservedRemoteControls() {
        final LinkedList<RemoteControlProxy> reservedRemoteControls;

        reservedRemoteControls = new LinkedList<RemoteControlProxy>();
        for (RemoteControlProxy remoteControl : remoteControls) {
            if (remoteControl.sessionInProgress()) {
                reservedRemoteControls.add(remoteControl);
            }
        }
        return Arrays
                .asList(reservedRemoteControls
                        .toArray(new RemoteControlProxy[reservedRemoteControls
                                .size()]));
    }

    protected RemoteControlProxy blockUntilARemoteControlIsAvailableOrRequestTimesOut() {
        RemoteControlProxy availableRemoteControl;

        while (true) {
            try {
                availableRemoteControl = findNextAvailableRemoteControl();
                boolean timedOut = false;
                while ((null == availableRemoteControl) && !timedOut
                        && !availableRemoteControls().isEmpty()) {
                    LOGGER.info("Waiting for a remote control...");
                    timedOut = waitForARemoteControlToBeAvailable();
                    availableRemoteControl = findNextAvailableRemoteControl();
                }
                return availableRemoteControl;
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted while reserving remote control", e);
            }
        }
    }

    protected RemoteControlProxy blockUntilARemoteControlIsAvailable(
            String environment) {
        RemoteControlProxy availableRemoteControl;
        int triesToReserve = 0;

        while (true) {
            try {
                availableRemoteControl = findNextAvailableRemoteControl(environment);
                while (null == availableRemoteControl
                        && !availableRemoteControls().isEmpty()) {
                    // if we tried 2 times to get this RC return to global pool
                    // and check provisioners again for possible free RCs
                    if (triesToReserve == 2) {
                        return null;
                    }
                    triesToReserve++;
                    // LOGGER.info("Waiting for an remote control...");
                    waitForARemoteControlToBeAvailable();
                    availableRemoteControl = findNextAvailableRemoteControl(environment);
                }
                return availableRemoteControl;
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted while reserving remote control", e);
            }
        }
    }

    /**
     * Non-blocking, not thread-safe
     * 
     * @return Next Available remote control. Null if none is available.
     */
    protected RemoteControlProxy findNextAvailableRemoteControl() {
        for (RemoteControlProxy remoteControl : remoteControls) {
            if (remoteControl.canHandleNewSession()
                    && reservedRemoteControls().isEmpty()) {
                return remoteControl;
            }
        }
        return null;
    }

    /**
     * Non-blocking, not thread-safe
     * 
     * @return Next Available remote control with environment. Null if none is
     *         available.
     */
    protected RemoteControlProxy findNextAvailableRemoteControl(
            String environment) {
        for (RemoteControlProxy remoteControl : remoteControls) {
            if (remoteControl.environment().equals(environment)
                    && remoteControl.canHandleNewSession()
                    && reservedRemoteControls().isEmpty()) {
                if (remoteControl.unreliable() == 200) {
                    return remoteControl;
                }
            }
        }

        return null;
    }

    /**
     * Wait for a remote control to be available or timeout while waiting.
     * 
     * @return Indicates whether the request timed out.
     * 
     * @throws InterruptedException
     */
    protected boolean waitForARemoteControlToBeAvailable()
            throws InterruptedException {
        final Double maxWaitTime = HubRegistry.registry().gridConfiguration()
                .getHub().getNewSessionMaxWaitTimeInSeconds();

        if (maxWaitTime.isInfinite()) {
            remoteControlAvailable.await();
            return false;
        }
        // TODO return !remoteControlAvailable.await(5, TimeUnit.SECONDS);
        return !remoteControlAvailable.await(maxWaitTime.longValue(),
                TimeUnit.SECONDS);
    }

    protected void signalThatARemoteControlHasBeenMadeAvailable() {
        remoteControlAvailable.signalAll();
    }

    public List<RemoteControlProxy> allRemoteControls() {
        final LinkedList<RemoteControlProxy> allRemoteControls;

        try {
            remoteControlListLock.lock();
            allRemoteControls = new LinkedList<RemoteControlProxy>();
            for (RemoteControlProxy remoteControl : remoteControls) {
                allRemoteControls.add(remoteControl);
            }

        } finally {
            remoteControlListLock.unlock();
        }
        return allRemoteControls;
    }

    /**
     * Return one remote control for this provisioner (as all RC:s are the same
     * with different environment)
     * 
     * @return List with one RemoteControlProvisioner
     */
    public List<RemoteControlProxy> getRemoteControl() {
        final LinkedList<RemoteControlProxy> remoteController = new LinkedList<RemoteControlProxy>();

        try {
            remoteControlListLock.lock();
            if (!remoteControls.isEmpty()) {
                remoteController.add(remoteControls.get(0));
            }
        } finally {
            remoteControlListLock.unlock();
        }
        return remoteController;
    }
}
