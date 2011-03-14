/* Selenium core extensions for Vaadin
 */

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

@bot@.prototype.locateElementByVaadin = function(tkString, inDocument) {

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

@bot@.prototype.locateElementByVaadin.is_fuzzy_match = function(node, target) {
	try {
    	if ("unwrap" in XPCNativeWrapper) {
    		target = XPCNativeWrapper.unwrap(target);
    	}else if (target.wrappedJSObject) {
    		target = target.wrappedJSObject;
        }
    	

        var isMatch = (node == target) || is_ancestor(node, target);
        return isMatch;
    }
    catch (e) {
        return false;
    }
}