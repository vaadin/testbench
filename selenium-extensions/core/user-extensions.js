PageBot.prototype.locateElementByVaadin = function(tkString, inDocument) {

	var wnd = this.currentWindow;
	if (wnd.wrappedJSObject) {
		wnd = wnd.wrappedJSObject;
	}

	if (!wnd.itmill || !wnd.itmill.clients) {
		return null;
	}

	var parts = tkString.split("::");
	var appId = parts[0];

	try {
		var element = wnd.itmill.clients[appId].getElementByPath(parts[1]);
		return element;
	} catch (exception) {
		LOG.error('an error occured when locating element for '+tkString+': ' + exception);
	}
	return null;
}

/*
 PageBot.prototype.locateElementByVaadin.is_fuzzy_match = function(
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
Selenium.prototype.doWaitForVaadin = function(locator, value) {

	// max time to wait for toolkit to settle
	var timeout = 20000;

	return Selenium.decorateFunctionWithTimeout( function() {
		var wnd = selenium.browserbot.getCurrentWindow();
		if (wnd.wrappedJSObject) {
			wnd = wnd.wrappedJSObject;
		}
		var clients = wnd.itmill.clients;
		if (clients) {
			for ( var client in clients) {
				if (clients[client].isActive()) {
					return false;
				}
			}
			return true;
		} else {
			if (!this.VaadinWarnedNoAppFound) {
				// TODO explain what this means & what to do
			LOG.warn("No testable toolkit applications found!");
			this.VaadinWarnedNoAppFound = true;
		}
		return true;
	}
}, timeout);

}
