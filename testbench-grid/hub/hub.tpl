#!/bin/sh

# Location of script
path=`dirname $0`

# Directory containing jar files
libdir=$path/lib

cpvars=.:$libdir/selenium-grid-hub-standalone-vaadin-testbench-@build@.jar

java -cp "$cpvars" com.thoughtworks.selenium.grid.hub.HubServer
