
LocatorBuilders.add('itmilltoolkit', function(e) {
	LOG.warn("itmilltoolkit locator: "+e);
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
	
	LOG.warn("searching for window");
	
	for(var windowname in wnd.itmill.clients) {
		var path = wnd.itmill.clients[windowname].getPathForElement(e);
		if (path != null) {
			return "itmilltoolkit=" + windowname + "::" + path;
		}
	}
	
	return null;
});


LocatorBuilders.order.splice(LocatorBuilders.order.indexOf("itmilltoolkit"), 1);
LocatorBuilders.order.unshift("itmilltoolkit");


/* Add waits for each IT Mill Toolkit command */
Recorder.prototype.record_orig = Recorder.prototype.record;

Recorder.prototype.record = function(command, target, value, insertBeforeLastCommand) {
	if(!this.ITMILLFirstWaitAdded) {
		// wait for the initial uidl request to be handled
		this.record_orig("waitForITMillToolkit");
		this.ITMILLFirstWaitAdded = true;
	}
	this.record_orig(command, target, value, insertBeforeLastCommand);
	// TODO This must be conditional, only if the path starts with itmilltoolkit=
	//LOG.warn("command: "+command+"\ntarget: "+target+"\nvalue: "+value+"\ninsertBeforeLastCommand: "+insertBeforeLastCommand+")");
	// add wait after each recorded UI event automatically
	this.record_orig("waitForITMillToolkit");
}

