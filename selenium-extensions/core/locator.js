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

/**
 * Overriding window.open to fix Chrome 12+ bug
 * 
 * TODO Should be removed when Chrome is fixed.
 */
if(navigator.userAgent.toLowerCase().indexOf('chrome/') > -1){
  window.open_ = window.open;  
  window.open = function(url, name, props) {          
    var win = window.open_("",name,props);
    win.opener = null;
    win.document.location = url; 
    return win;          
  };  
}

@bot@.prototype.locateElementByVaadin = function(tkString, inDocument) {

	var connector = getVaadinConnector(this.currentWindow);

	if (!connector) {
		// Not a vaadin application
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
};

@bot@.prototype.locateElementByVaadin.is_fuzzy_match = function(node, target) {
	try {
		var unwrapped = target;
    	if ("unwrap" in XPCNativeWrapper) {
    		unwrapped = XPCNativeWrapper.unwrap(target);
    	}else if (target.wrappedJSObject) {
    		unwrapped = target.wrappedJSObject;
        }
    	
        var isMatch = (node == target || node == unwrapped) || is_ancestor(node, target) || is_ancestor(node, unwrapped);
        return isMatch;
    }
    catch (e) {
        return false;
    }
};