
LocatorBuilders.add('vaadin', function(e) {
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
			return "vaadin=" + windowname + "::" + path;
		}
	}
	
	return null;
});


LocatorBuilders.order.splice(LocatorBuilders.order.indexOf("vaadin"), 1);
LocatorBuilders.order.unshift("vaadin");


/* Add waits for each IT Mill Toolkit command */
Recorder.prototype.record_orig = Recorder.prototype.record;

Recorder.prototype.record = function(command, target, value, insertBeforeLastCommand) {
	if(!this.VaadinFirstWaitAdded) {
		// wait for the initial uidl request to be handled
		this.record_orig("waitForVaadin");
		this.VaadinFirstWaitAdded = true;
	}
	this.record_orig(command, target, value, insertBeforeLastCommand);
	// TODO This should be conditional, only if the path starts with vaadin=
	//this.log.warn("command: "+command+"\ntarget: "+target+"\nvalue: "+value+"\ninsertBeforeLastCommand: "+insertBeforeLastCommand+")");
	// add wait after each recorded UI event automatically
	this.record_orig("waitForVaadin");
}

