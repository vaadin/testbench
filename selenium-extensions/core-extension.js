Recorder.prototype.record_orig = Recorder.prototype.record;

Recorder.prototype.record = function(command, target, value, insertBeforeLastCommand) {
	if(!this.ITMILLFirstWaitAdded) {
		// wait for the initial uidl request to be handled
		this.record_orig("waitForITMillToolkit");
		this.ITMILLFirstWaitAdded = true;
	}
	this.record_orig(command, target, value, insertBeforeLastCommand);
	// add wait after each recorded UI event automatically
	this.record_orig("waitForITMillToolkit");
}

PageBot.prototype.locateElementByITMillToolkit = function(tkString, inDocument) {

	var wnd = this.currentWindow;
	if (wnd.wrappedJSObject) {
		wnd = wnd.wrappedJSObject;
	}
	
	if (!wnd.itmill || !wnd.itmill.clients) {
		return null;
	}

	var parts = tkString.split("::");
	var appId = parts[0];

	var element = wnd.itmill.clients[appId].getElementByPath(parts[1]);

	return element;
}

/*
PageBot.prototype.locateElementByITMillToolkit.is_fuzzy_match = function(
		source, target) {

	if (source.wrappedJSObject) {
		source = source.wrappedJSObject;
	}
	if (target.wrappedJSObject) {
		target = target.wrappedJSObject;
	}
	if (target == source) {
		return true;
	}

	return true;

}

*/

Selenium.prototype.doWaitForITMillToolkit = function(locator, value) {
	
	// max time to wait for toolkit to settle
	var timeout = 20000;
	
	return Selenium.decorateFunctionWithTimeout(function() {
		var wnd = selenium.browserbot.getCurrentWindow().wrappedJSObject;
		var clients = wnd.itmill.clients;
		if(clients) {
			for(var client in clients) {
				if(clients[client].isActive()) {
					return false;
				}
			}
			return true;
		} else {
			if (!this.ITMILLWarnedNoAppFound) {
				// TODO explain what this means & what to do
				LOG.warn("No testable toolkit applications found!");
				this.ITMILLWarnedNoAppFound = true;
			}
			return true;
		}
	}, timeout);
    
}

