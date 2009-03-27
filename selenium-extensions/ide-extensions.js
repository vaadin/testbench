
LocatorBuilders.add('itmilltoolkit', function(e) {
	var wnd = this.window;
	if (wnd.wrappedJSObject) {
		wnd = wnd.wrappedJSObject;
	}
	
	if (!wnd.itmill) {
		// Not a toolkit application
		return null;
	}
	
	// Unwrap the element if wrapped so we can access tkPid
	if (e.wrappedJSObject) {
		e = e.wrappedJSObject;
	}
	
	
	for(var windowname in wnd.itmill.clients) {
		var path = wnd.itmill.clients[windowname].getPathForElement(e);
		if (path != null) {
			return "itmilltoolkit=" + windowname + "::" + path;
		}
	}
	
	return null;
});

// Add itmilltoolkit as the first locator
{
	var order = ["itmilltoolkit"];
	for (var i=0; i < LocatorBuilders.order.length; i++) {
		if (LocatorBuilders.order[i] != "itmilltoolkit") {
			order.push(LocatorBuilders.order[i]);
		}
	}
	
	LocatorBuilders.order = order;
}

