/* Selenium IDE extensions for Vaadin */

/* Also in core extensions */
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

LocatorBuilders.add('vaadin', function(e) {
	var connector = getVaadinConnector(this.window);

	if (!connector) {
		// Not a Vaadin application
		return null;
	}

	/* Unwrap the element if wrapped so we can access tkPid */
	if (e.wrappedJSObject) {
		e = e.wrappedJSObject;
	}

	for ( var windowname in connector.clients) {
		var path = connector.clients[windowname].getPathForElement(e);
		if (path != null) {
			return "vaadin=" + windowname + "::" + path;
		}
	}

	return null;
});

LocatorBuilders.order.splice(LocatorBuilders.order.indexOf("vaadin"), 1);
LocatorBuilders.order.unshift("vaadin");

/* Add waits for each Vaadin command */
Recorder.prototype.record_orig = Recorder.prototype.record;

Recorder.prototype.record = function(command, target, value,
		insertBeforeLastCommand) {
	if (!this.VaadinFirstWaitAdded) {
		/* wait for the initial uidl request to be handled */
		this.record_orig("waitForVaadin");
		this.VaadinFirstWaitAdded = true;
	}
	this.record_orig(command, target, value, insertBeforeLastCommand);
	// TODO This should be conditional, only if the path starts with vaadin=
	// this.log.warn("command: "+command+"\ntarget: "+target+"\nvalue:
	// "+value+"\ninsertBeforeLastCommand: "+insertBeforeLastCommand+")");
	// add wait after each recorded UI event automatically
	this.record_orig("waitForVaadin");
}

/*
 * Override the default findClickableElement so we can decide what is clickable in a GWT compatible fashion.
 */
var defaultFindClickableElement = Recorder.prototype.findClickableElement;

Recorder.prototype.findClickableElement = function(e) {
	if (!e)
		return null;

	var target = e;
	if (e.wrappedJSObject) {
		target = e.wrappedJSObject;
	}
	if (target.onclick && typeof target.onclick == "function") {
		return e;
	}

	/* Original Recorder.prototype.findClickableElement */
	if (!e.tagName)
		return null;
	var tagName = e.tagName.toLowerCase();
	var type = e.type;
	if (e.hasAttribute("onclick")
			|| e.hasAttribute("href")
			|| tagName == "button"
			|| (tagName == "input" && (type == "submit" || type == "button"
					|| type == "image" || type == "radio" || type == "checkbox" || type == "reset"))) {
		return e;
	} else {
		if (e.parentNode != null) {
			return this.findClickableElement(e.parentNode);
		} else {
			return null;
		}
	}
	/* End of original Recorder.prototype.findClickableElement */

};

Recorder.addEventHandler('contextmenu', 'contextmenu', function(event) {
	var hasContextMenuListener = false;
	var elem = event.target.wrappedJSObject;
	while (!hasContextMenuListener && elem != null) {
		if (elem.oncontextmenu) {
			hasContextMenuListener = true;
			break;
		} else {
			elem = elem.parentNode;
		}
	}
	if (hasContextMenuListener) {
		this.record("contextmenu", this.findLocators(event.target));
	}
}, {
	capture : true
});
