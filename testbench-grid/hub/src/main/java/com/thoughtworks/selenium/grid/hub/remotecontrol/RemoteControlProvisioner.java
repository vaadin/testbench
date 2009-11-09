package com.thoughtworks.selenium.grid.hub.remotecontrol;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Central authority to track registered remote controls and grant exclusive
 * access to a remote control for a while.
 * 
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
        final RemoteControlProxy remoteControl;

        try {
            remoteControlListLock.lock();

            if (remoteControls.isEmpty()) {
                return null;
            }

            remoteControl = blockUntilARemoteControlIsAvailable();
            remoteControl.registerNewSession();
            LOGGER.info("Reserved remote control" + remoteControl);
            return remoteControl;
        } finally {
            remoteControlListLock.unlock();
        }
    }

    public RemoteControlProxy reserve(String environment) {
        final RemoteControlProxy remoteControl;

        try {
            remoteControlListLock.lock();
            waiting++;
            if (remoteControls.isEmpty()) {
                return null;
            }

            remoteControl = blockUntilARemoteControlIsAvailable(environment);
            if(remoteControl != null){
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
                RemoteControlProxy existingRemoteControl = remoteControls
                        .get(remoteControls.indexOf(newRemoteControl));
                int concurrentSesssionCount = existingRemoteControl
                        .concurrentSesssionCount();
                for (int i = 0; i < concurrentSesssionCount; i++) {
                    existingRemoteControl.unregisterSession();
                }
                remoteControls.remove(existingRemoteControl);
            }
            remoteControls.add(newRemoteControl);
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

    public int amountWaiting(){
        return waiting;
    }
    
    /**
     * Not thread safe.
     * 
     * @return All available remote controls. Never null.
     */
    public List<RemoteControlProxy> availableRemoteControls() {
        LinkedList<RemoteControlProxy> availableremoteControls;

        availableremoteControls = new LinkedList<RemoteControlProxy>();
        if (reservedRemoteControls().isEmpty()) {
            for (RemoteControlProxy remoteControl : remoteControls) {
                if (remoteControl.canHandleNewSession()) {
                    availableremoteControls.add(remoteControl);
                }
            }
        }
        return availableremoteControls;
    }

    /**
     * Not thread safe.
     * 
     * @return All reserved remote controls. Never null.
     */
    public List<RemoteControlProxy> reservedRemoteControls() {
        LinkedList<RemoteControlProxy> reservedRemoteControls;

        reservedRemoteControls = new LinkedList<RemoteControlProxy>();
        for (RemoteControlProxy remoteControl : remoteControls) {
            if (remoteControl.concurrentSesssionCount() >= 1) {
                reservedRemoteControls.add(remoteControl);
            }
        }
        return reservedRemoteControls;
    }

    protected RemoteControlProxy blockUntilARemoteControlIsAvailable() {
        RemoteControlProxy availableRemoteControl;

        while (true) {
            try {
                availableRemoteControl = findNextAvailableRemoteControl();
                while (null == availableRemoteControl) {
                    LOGGER.info("Waiting for an remote control...");
                    waitForARemoteControlToBeAvailable();
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
                while (null == availableRemoteControl) {
                    // if we tried 2 times to get this RC return to global pool
                    // and check provisioners again for possible free RCs
                    if (triesToReserve == 2) {
                        return null;
                    }
                    triesToReserve++;
//                    LOGGER.info("Waiting for an remote control...");
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

    protected RemoteControlProxy findNextAvailableRemoteControl(
            String environment) {
        for (RemoteControlProxy remoteControl : remoteControls) {
            if (remoteControl.environment().equals(environment)
                    && remoteControl.canHandleNewSession()
                    && reservedRemoteControls().isEmpty()) {
                return remoteControl;
            }
        }
        return null;
    }

    protected void waitForARemoteControlToBeAvailable()
            throws InterruptedException {
        remoteControlAvailable.await(30, TimeUnit.SECONDS);
    }

    protected void signalThatARemoteControlHasBeenMadeAvailable() {
        remoteControlAvailable.signalAll();
    }

}
