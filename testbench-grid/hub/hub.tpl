#!/bin/sh

# Location of script
path=`dirname $0`

# Directory containing jar files
libdir=$path/lib

cpvars=.:$libdir/selenium-grid-hub-standalone-vaadin-testbench-@build@.jar com.thoughtworks.selenium.grid.hub.HubServer

java -cp "$cpvars"
