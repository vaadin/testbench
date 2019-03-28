@sven, I’ve been taking a look at the current code base, 
and have some comments that would like to see what we can do about

Some low hanging fruit
• Update license header
• Update dev info in poms
• Review package names
• Format with Vaadin Code style
• Remove commented code chunks


TO-DOs
• Ensure functional patterns are not public API (supplier, bifunction)
• Go through shared modules and check what’s required for testbench-core, 
     remove the rest
     For instance logger adapter, when testing an application 
     the application logging shouldn’t come from TB, 
     TB shouldn’t provide logger adapters
     
• AbstractVaadinPageObject can’t depend on TBElements implementations, 
   can’t use Grid/Label/ButtonElement, etc. WithID doesn’t add enough value

What modules should we have?
• No V## specific modules -> Done
• testbench-core -> Done
• IT module -> Done but different name until now
• Modules per container implementation -> Done