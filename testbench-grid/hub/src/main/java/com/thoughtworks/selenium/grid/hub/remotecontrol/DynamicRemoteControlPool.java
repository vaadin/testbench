package com.thoughtworks.selenium.grid.hub.remotecontrol;

import java.util.List;

/**
 * Remote control pool that grows/shrinks when remote control
 * register/unregister themselves.
 */
public interface DynamicRemoteControlPool extends RemoteControlPool {

    void register(List<RemoteControlProxy> remoteControlList);
    
    void register(RemoteControlProxy newRemoteControl);

    boolean unregister(RemoteControlProxy remoteControl);
    
    boolean unregister(List<RemoteControlProxy> remoteControlList);

    List<RemoteControlProxy> availableRemoteControls();

    List<RemoteControlProxy> reservedRemoteControls();
    
}
