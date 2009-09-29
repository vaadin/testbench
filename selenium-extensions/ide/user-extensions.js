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
	
	/* Catches a java.lang.ClassCastException for popupView
	 * that stops the whole Locator search.
	 */
	for ( var windowname in connector.clients) {
		try{
			var path = connector.clients[windowname].getPathForElement(e);
		}catch(err){
			this.log.debug(err);
		}
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

Recorder.addEventHandler('scroll', 'scroll', function(event) {
	if(event.target && event.target.wrappedJSObject) {
		var elem  = event.target.wrappedJSObject;
		if(elem.onscroll) {
			this.log.warn(this);
			var loc = this.findLocators(event.target);
			var top = "" + elem.scrollTop;
			if(this._scrollTimeout) {
				clearTimeout(this._scrollTimeout);
			}
			var s = this;
			this._scrollTimeout = setTimeout(function(){
				s.record_orig("scroll", loc , top);
				// wait for lazy scroller to start possible server visit
				s.record("pause", "300");
				s._scrollTimeout = null;
			},260);
		}
	}
}, { capture: true });

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

/* Add the screenCapture as an action to SeleniumIDE command overlay */
CommandBuilders.add("action", function(window){

	var result = { action: "ScreenCapture" };
	
	return{
		command: "screenCapture"
	};
});

var charBuffer = "";
var typeString = "true";

/* Checks keyCodes on keyup event and adds a pressArrowKey if confirmed. */
Recorder.addEventHandler('pressSpecialKey', 'keyup', function(event){
		
		if(event.keyCode >= 37 && event.keyCode <= 40){
			if(event.shiftKey){//16
				this.record_orig("shiftKeyDown", this.findLocators(event.target), '');
			}
			if(event.ctrlKey){//17
				this.record_orig("controlKeyDown", this.findLocators(event.target), '');
			}
			if(event.altKey){//18
				this.record_orig("altKeyDown", this.findLocators(event.target), '');
			}
			if(charBuffer.length > 0){
				this.record("enterCharacter", this.findLocators(event.target), charBuffer);
				charBuffer = "";
				typeString = "false";
			}
		}
		
		switch(event.keyCode){
		case 13:
			this.log.debug('pressed ENTER!');
			typeString = "false";
			this.record("pressSpecialKey", this.findLocators(event.target), "enter");
			break;
		case 37: 
			this.log.debug('pressed LEFT!');
			if(event.shiftKey || event.ctrlKey || event.altKey){
				this.record_orig("pressSpecialKey", this.findLocators(event.target), "left");
			}else{
				this.record("pressSpecialKey", this.findLocators(event.target), "left");
			}
			break;
		case 39: 
			this.log.debug('pressed RIGHT!');
			if(event.shiftKey || event.ctrlKey || event.altKey){
				this.record_orig("pressSpecialKey", this.findLocators(event.target), "right");
			}else{
				this.record("pressSpecialKey", this.findLocators(event.target), "right");
			}
			break;
		case 38: 
			this.log.debug('pressed UP!');
			if(event.shiftKey || event.ctrlKey || event.altKey){
				this.record_orig("pressSpecialKey", this.findLocators(event.target), "up");
			}else{
				this.record("pressSpecialKey", this.findLocators(event.target), "up");
			}
			break;
		case 40:
			this.log.debug('pressed DOWN!');
			if(event.shiftKey || event.ctrlKey || event.altKey){
				this.record_orig("pressSpecialKey", this.findLocators(event.target), "down");
			}else{
				this.record("pressSpecialKey", this.findLocators(event.target), "down");
			}
		}
		
		if(event.keyCode >= 37 && event.keyCode <= 40){
			if(event.shiftKey){
				this.record("shiftKeyUp", this.findLocators(event.target), '');
			}
			if(event.ctrlKey){
				this.record("controlKeyUp", this.findLocators(event.target), '');
			}
			if(event.altKey){
				this.record("altKeyUp", this.findLocators(event.target), '');
			}
		}
	});

/* record all keypresses to character buffer */
Recorder.addEventHandler('keyPressedDown', 'keypress', function(event){
		if(event.charCode >= 48){
			this.log.debug('Typed key ' + String.fromCharCode(event.charCode));
			charBuffer = charBuffer + String.fromCharCode(event.charCode);
		}else if(event.keyCode >= 48){
			this.log.debug('Typed key ' + String.fromCharCode(event.keyCode));
			charBuffer = charBuffer + fromKeyCode(event.keyCode);
		}else if(event.which >= 48){
			this.log.debug('Typed key ' + String.fromCharCode(event.which));
			charBuffer = charBuffer + String.fromCharCode(event.which);
		}

		if(event.keyCode == 8 || event.charCode == 8){
			this.log.debug('Recieved BACKSPACE');
			if(charBuffer.length == 1){
				charBuffer = "";
			}else{
				charBuffer = charBuffer.substring(0, charBuffer.length-1); 
			}
		}
	});

/* Override default type */
Recorder.removeEventHandler('type');

Recorder.addEventHandler('type', 'change', function(event) {
	var tagName = event.target.tagName.toLowerCase();
	var type = event.target.type;
	var target = this.findLocators(event.target);
	if (('input' == tagName && ('text' == type || 'password' == type || 'file' == type)) ||
		'textarea' == tagName) {
		if(typeString == "true"){

			if(charBuffer.length > 0){
				charBuffer = "";
				this.record("enterCharacter", target, event.target.value);
			}else{
				this.record("type", target, event.target.value);
			}
		}else{
			typeString = "true";
			if(charBuffer.length > 0){
				charBuffer = "";
				this.record("enterCharacter", target, event.target.value);
			}
		}
	}
});

var noSelection = "true";
var clicked = "false";

/* override default click event recorder */
Recorder.removeEventHandler('clickLocator');

Recorder.addEventHandler('clickLocator', 'click', function(event){
		charBuffer = "";
		
		if (event.button == 0 && noSelection == "true") {
            var x = event.clientX - editor.seleniumAPI.Selenium.prototype.getElementPositionLeft(event.target);
            var y = event.clientY - editor.seleniumAPI.Selenium.prototype.getElementPositionTop(event.target);

			var clickable = this.findClickableElement(event.target);
			if (clickable) {
	            if (this.mouseoverLocator) {
	                this.record('mouseOver', this.mouseoverLocator, '');
	                delete this.mouseoverLocator;
	            }

	            /* A class="v-button" requires a click without mouseDown+mouseUp */
	            var target = this.findLocators(event.target);
	            if((new RegExp("Button")).test(target) || (new RegExp("TwinColSelect")).test(target)){
	            	this.record("click", target, '');
	            }else{
	            	this.record("mouseClick", target, x + ',' + y);
	            }
				clicked = "true";
	        } else {
	            var target = event.target;
//	            this.callIfMeaningfulEvent(function() {
	                    this.record("mouseClick", this.findLocators(target), x + ',' + y);
//	                });
	        }
		}else{
			noSelection = "true";
		}
	}, { capture: true });

/* Expand select/addSelection/removeSelection functionality*/
Recorder.removeEventHandler('select');

Recorder.addEventHandler('select', 'change', function(event) {
	var tagName = event.target.tagName.toLowerCase();
	if ('select' == tagName) {
		if (!event.target.multiple) {
            var option = event.target.options[event.target.selectedIndex];
			this.log.debug('selectedIndex=' + event.target.selectedIndex);
			this.record("select", this.findLocators(event.target), this.getOptionLocator(option));
		} else {
			this.log.debug('change selection on select-multiple');
			var options = event.target.options;
			for (var i = 0; i < options.length; i++) {
				this.log.debug('option=' + i + ', ' + options[i].selected);
				if (options[i]._wasSelected == null) {
					this.log.warn('_wasSelected was not recorded');
				}
				if (options[i]._wasSelected != options[i].selected) {
                    var value = this.getOptionLocator(options[i]);
					if (options[i].selected) {
						this.record("addSelection", this.findLocators(event.target), value);
					} else {
						this.record("removeSelection", this.findLocators(event.target), value);
					}
					options[i]._wasSelected = options[i].selected;
				}
			}
		}
		noSelection = "false";
	}
});

var counter = 0;
var closeNotificationRecorded = "false";

Recorder.addEventHandler('append', 'DOMNodeInserted', function(event){
		if(clicked == "true" && event.target.nodeName.toLowerCase() == "div"){
			var target = this.findLocators(event.target);
			if((new RegExp("body")).test(target) && (new RegExp("Notification")).test(event.target.className)){
				this.record("closeNotification", target, '0,0');
				clicked = "false";
				closeNotificationRecorded = "true";
			}
			/*
			 * Stop checking inserted DOM nodes after 5 inserts 
			 */
			if(++counter > 5){
				clicked = "false";
			}
		}
	});