#!/bin/sh

# Location of script
path=`dirname $0`

# Directory containing jar files
libdir=$path/lib

cpvars=$libdir/selenium-grid-remote-control-standalone-vaadin-testbench-@build@.jar:$libdir/selenium-server-1.0.1.jar

# by default, obtained from rc_configuration.xml
#environment=linux-firefox3
userextensions=$path/user-extensions.js

#java -cp "$cpvars" com.thoughtworks.selenium.grid.remotecontrol.SelfRegisteringRemoteControlLauncher -env $environment -userExtensions $userextensions
java -cp "$cpvars" com.thoughtworks.selenium.grid.remotecontrol.SelfRegisteringRemoteControlLauncher -userExtensions $userextensions
