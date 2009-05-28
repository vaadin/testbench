/* Selenium core extensions for Vaadin */

/* Also in IDE extensions */
function getVaadinConnector(wnd) {
	if (wnd.wrappedJSObject) {
		wnd = wnd.wrappedJSObject;
	}

	var connector = null;
	if (wnd.itmill) {
		connector = wnd.itmill;
	} else if (wnd.vaadin) {
		connector = wnd.vaadin;
	}

	return connector;
}

PageBot.prototype.locateElementByVaadin = function(tkString, inDocument) {

	var connector = getVaadinConnector(this.currentWindow);

	if (!connector) {
		// Not a toolkit application
		return null;
	}

	var parts = tkString.split("::");
	var appId = parts[0];

	try {
		var element = connector.clients[appId].getElementByPath(parts[1]);
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
		var connector = getVaadinConnector(wnd);
		if (!connector) {
			// No connector found == Not a Vaadin application so we don't need to wait
			return true;
		}
		
		var clients = connector.clients;
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

Selenium.prototype.doScroll = function(locator, scrollString) {
	var element = this.page().findElement(locator);
	element.scrollTop = scrollString;
};

Selenium.prototype.doContextmenu = function(locator) { 
     var element = this.page().findElement(locator); 
     this.page()._fireEventOnElement("contextmenu", element, 0, 0); 
}; 

Selenium.prototype.doContextmenuAt = function(locator, coordString) { 
      if (!coordString) 
    	  coordString = '2, 2'; 
      
      var element = this.page().findElement(locator); 
      var clientXY = getClientXY(element, coordString);
      this.page()._fireEventOnElement("contextmenu", element, clientXY[0], clientXY[1]); 
};
