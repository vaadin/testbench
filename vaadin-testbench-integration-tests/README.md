# Infos about Testbench 


## ParallelRunner properties 
* browsers.include
* browsers.exclude


both wil be used to define the final composition of browserconfigs..
have a look at ParallelRunner.getFilteredCapabilities


## elements.ng
in this package you will find the version, that is using the 
context based selenium Grid selection.
Means, that you could switch between Selenium Grids and local installed Browser.

### conf/selenium-grids.properties
Here you should feine per line a target selenium grid or the local browser
see: template_selenium-grids.properties

### conf/browser_combinations.json
Here you should feine the available browserconfigurations that are tested in the 
defined selenium-grids.
see: template_browser_combinations.json

### testbench.properties

Here you could define the properties you need to set.
available properties are f.e.

* firefox.path=/path/firefox
* firefox.profile.path=/path/profile_name
* chrome.driver.path=/path/chromedriver

This properties are used from the class PrivateTB3Configuration
and for the config in the package ng.


