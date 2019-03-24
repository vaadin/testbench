# ToDo 

create an aggregating module : name will be testbench-core

meecrowave deployment of this template

inc testcoverage
- tb and plain modules
- Bakery tests migration inside bakery repo

- Documentation for the init config
- Concurrency test with plain junit5
    -   Concurrency is running with container - none
- Disable for dedicated Browser
- PageObject PreLoad
- reference-screenshots are now loaded from Resources (jar)
- UploadElement uses  TB Utilityclass - com/vaadin/testbench/parallel/BrowserUtil

Some low hanging fruit
• Update license header
• Update dev info in poms
• Review package names
• Format with Vaadin Code style
• Remove commented code chunks


TO-DOs
• Ensure functional patterns are not public API (supplier, bifunction)
• Go through shared modules and check what’s required for testbench-core, remove the rest
     For instance logger adapter, when testing an application the application logging shouldn’t come from TB, TB shouldn’t provide logger adapters
• AbstractVaadinPageObject can’t depend on TBElements implementations, can’t use Grid/Label/ButtonElement, etc. WithID doesn’t add enough value

What modules should we have?
• No V## specific modules
• testbench-core
• IT module
• Modules per container implementation


## Phase I 
Extensions only

Docu how to use Vintage
- Spring Skeleton
- Testbench Demo to show how it is running with junit4 and junit5


## Phase II
Testengine with the possibility to run 
old and new test inside the same engine

 
 
 ## Safari
 
 https://developer.apple.com/documentation/webkit/testing_with_webdriver_in_safari
 
 
 ## Docu
 
 description of the different ways to preload a PageObject
 