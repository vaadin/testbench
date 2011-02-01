#!/bin/sh

# Location of script
path=`dirname $0`

# Directory containing jar files
libdir=$path/lib

cpvars=$libdir/selenium-grid-remote-control-standalone-vaadin-testbench-@build@.jar:$libdir/selenium-server-1.0.1.jar
userextensions=$path/user-extensions.js

java -cp "$cpvars" com.thoughtworks.selenium.grid.remotecontrol.SelfRegisteringRemoteControlLauncher -userExtensions $userextensions -ensureCleanSession
