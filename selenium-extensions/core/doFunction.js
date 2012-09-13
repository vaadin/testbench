/**
 * Overriding to fix IE9 native subwindow opening bug #6896
 */
BrowserBot.prototype.modifyWindowToRecordPopUpDialogs = function(
		windowToModify, browserBot) {
	var self = this;

	windowToModify.seleniumAlert = windowToModify.alert;

	windowToModify.alert = function(alert) {
		browserBot.recordedAlerts.push(alert);
		self.relayBotToRC.call(self, "browserbot.recordedAlerts");
	};

	windowToModify.confirm = function(message) {
		browserBot.recordedConfirmations.push(message);
		var result = browserBot.nextConfirmResult;
		browserBot.nextConfirmResult = true;
		self.relayBotToRC.call(self, "browserbot.recordedConfirmations");
		return result;
	};

	windowToModify.prompt = function(message) {
		browserBot.recordedPrompts.push(message);
		var result = !browserBot.nextConfirmResult ? null
				: browserBot.nextPromptResult;
		browserBot.nextConfirmResult = true;
		browserBot.nextPromptResult = '';
		self.relayBotToRC.call(self, "browserbot.recordedPrompts");
		return result;
	};

	// Keep a reference to all popup windows by name
	// note that in IE the "windowName" argument must be a valid javascript
	// identifier, it seems.
	var originalOpen = windowToModify.open;
	var originalOpenReference;
	if (browserVersion.isHTA) {
		originalOpenReference = 'selenium_originalOpen' + new Date().getTime();
		windowToModify[originalOpenReference] = windowToModify.open;
	}

	var isHTA = browserVersion.isHTA;

	var newOpen = function(url, windowName, windowFeatures, replaceFlag) {
		var myOriginalOpen = originalOpen;

		if (windowName == "" || windowName == "_blank") {
			windowName = "selenium_blank" + Math.round(100000 * Math.random());
			LOG
					.warn("Opening window '_blank', which is not a real window name.  Randomizing target to be: "
							+ windowName);
		}

		/**
		 * This fixes the issues with subwindow opening in IE9. Instead of
		 * directly calling myOriginal open we use the reference to call it.
		 */
		var openedWindow;
		if (isHTA) {
			openedWindow = windowToModify[originalOpenReference](url,
					windowName, windowFeatures, replaceFlag);
		} else {
			openedWindow = myOriginalOpen(url, windowName, windowFeatures,
					replaceFlag);
		}

		LOG.debug("window.open call intercepted; window ID (which you can use with selectWindow()) is \""
						+ windowName + "\"");

		if (windowName != null) {
			openedWindow["seleniumWindowName"] = windowName;
		}
		selenium.browserbot.openedWindows[windowName] = openedWindow;
		return openedWindow;
	};

	if (browserVersion.isHTA) {
		originalOpenReference = 'selenium_originalOpen' + new Date().getTime();
		newOpenReference = 'selenium_newOpen' + new Date().getTime();
		var setOriginalRef = "this['" + originalOpenReference
				+ "'] = this.open;";

		if (windowToModify.eval) {
			windowToModify.eval(setOriginalRef);
			windowToModify.open = newOpen;
		} else {
			// DGF why can't I eval here? Seems like I'm querying the window at
			// a bad time, maybe?
			setOriginalRef += "this.open = this['" + newOpenReference + "'];";
			windowToModify[newOpenReference] = newOpen;
			windowToModify.setTimeout(setOriginalRef, 0);
		}
	} else {
		if (typeof XPCNativeWrapper != "undefined"
				&& "unwrap" in XPCNativeWrapper) {
			// Firefox4 won't call the newOpen method unless we unwrap the
			// window (#6676)
			// Really needed for RC, so fixed also in selenium-server.jar
			windowToModify = XPCNativeWrapper.unwrap(windowToModify);
		}
		windowToModify.open = newOpen;
	}
};


Selenium.prototype.doWaitForVaadin = function(locator, value) {

	// max time to wait for toolkit to settle
	var timeout = 20000;
	var foundClientOnce = false;
	
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
		  		//A Vaadin connector was found so this is most likely a Vaadin application. Keep waiting.
		  		return false;
		  	}
	 }, timeout);
};

Selenium.prototype.doScroll = function(locator, scrollString) {
	var element = this.page().findElement(locator);
	element.scrollTop = scrollString;
};

Selenium.prototype.doScrollLeft = function(locator, scrollString){
	var element = this.page().findElement(locator);
	element.scrollLeft = scrollString;
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

/* Empty screenCapture command for use with export test case Vaadin */
Selenium.prototype.doScreenCapture = function(locator, value){
};

/*Enters a characte so that it gets recognized in comboboxes etc.*/
Selenium.prototype.doEnterCharacter = function(locator, value){
	var start =  new Date().getTime();
	var element = this.browserbot.findElement(locator);
    if (this.browserbot.shiftKeyDown) {
        value = new String(value).toUpperCase();
    }
    
    triggerEvent(element, 'focus', false);
    triggerEvent(element, 'select', true);
    var maxLengthAttr = element.getAttribute("maxLength");
    var actualValue = value;
    if (maxLengthAttr != null) {
        var maxLength = parseInt(maxLengthAttr);
        if (value.length > maxLength) {
            actualValue = value.substr(0, maxLength);
        }
    }

    if (getTagName(element) == "body") {
        if (element.ownerDocument && element.ownerDocument.designMode) {
            var designMode = new String(element.ownerDocument.designMode).toLowerCase();
            if (designMode = "on") {
                // this must be a rich text control!
                element.innerHTML = actualValue;
            }
        }
    } else {
    	if (typeof XPCNativeWrapper != "undefined") {
    		XPCNativeWrapper(element).value = actualValue;	
    	} else {
    		element.value = actualValue;
    	}
    }

    value = value.replace(/\n/g, "");

	if(value.length > 1){
		for(i = 0; i < value.length;i++){
			this.doKeyDown(locator, value.charAt(i));
			this.doKeyUp(locator, value.charAt(i));
			
			var end = new Date().getTime();
			var time = end - start;
			// If typing takes over 24000ms, break and continue test.
			if(time > 24000){
				break;
			}
		}
	}else{
		this.doKeyDown(locator, value);
		this.doKeyUp(locator, value);
	}
	try {
		triggerEvent(element, 'change', true);
	} catch (e) {}
};

/*Sends an arrow press recognized by browsers.*/
Selenium.prototype.doPressSpecialKey = function(locator, value){
	var shift = (new RegExp("shift")).test(value);
	var ctrl = (new RegExp("ctrl")).test(value);
	var alt = (new RegExp("alt")).test(value);
	var meta = (new RegExp("meta")).test(value);
	if((new RegExp("left")).test(value.toLowerCase())){
		value="\\37";
	}else if((new RegExp("right")).test(value.toLowerCase())){
		value="\\39";
	}else if((new RegExp("up")).test(value.toLowerCase())){
		value="\\38";
	}else if((new RegExp("down")).test(value.toLowerCase())){
		value="\\40";
	}else if((new RegExp("enter")).test(value.toLowerCase())){
		value="\\13";
	}else if((new RegExp("space")).test(value.toLowerCase())){
		value="\\32";
	}else if((new RegExp("tab")).test(value.toLowerCase())){
		value="\\9";
	}else{
		value = value.substr(value.lastIndexOf(" ")+1);
	}
	var element = this.browserbot.findElement(locator);
	triggerSpecialKeyEvent(element, 'keydown', value, true, ctrl, alt, shift, meta);
	triggerSpecialKeyEvent(element, 'keypress', value, true,ctrl, alt, shift, meta);
	triggerSpecialKeyEvent(element, 'keyup', value, true, ctrl, alt, shift, meta);
};

/*Simulates the correct mouse click events.*/
Selenium.prototype.doMouseClick = function(locator, value){
	var element = this.browserbot.findElement(locator);
	value = value.split(":");
	var clientXY = getClientXY(element, value[0]);

	if(value.length > 1){
		this.browserbot.shiftKeyDown = (new RegExp("shift")).test(value[1]);
		this.browserbot.controlKeyDown = (new RegExp("ctrl")).test(value[1]);
		this.browserbot.altKeyDown = (new RegExp("alt")).test(value[1]);
		this.browserbot.metaKeyDown = (new RegExp("meta")).test(value[1]);
	}
	
	this.browserbot.triggerMouseEvent(element, 'mousedown', true, clientXY[0], clientXY[1]);
//	element.focus();
	this.browserbot.triggerMouseEvent(element, 'mouseup', true, clientXY[0], clientXY[1]);
	this.browserbot.clickElement(element);

	this.browserbot.shiftKeyDown = this.browserbot.controlKeyDown = this.browserbot.altKeyDown = this.browserbot.metaKeyDown = false;
};

/*Opera requires a special mouseClick as it else clicks twice*/
Selenium.prototype.doMouseClickOpera = function(locator, value){
	var element = this.browserbot.findElement(locator);
	value = value.split(":");
	var clientXY = getClientXY(element, value[0]);

	if(value.length > 1){
		this.browserbot.shiftKeyDown = (new RegExp("shift")).test(value[1]);
		this.browserbot.controlKeyDown = (new RegExp("ctrl")).test(value[1]);
		this.browserbot.altKeyDown = (new RegExp("alt")).test(value[1]);
	}

	this.browserbot.triggerMouseEvent(element, 'mousedown', true, clientXY[0], clientXY[1]);
//	element.focus();
	this.browserbot.triggerMouseEvent(element, 'mouseup', true, clientXY[0], clientXY[1]);

	this.browserbot.shiftKeyDown = this.browserbot.controlKeyDown = this.browserbot.altKeyDown = false;
};

/*Does a mouseClick on the target element. Used descriptive purposes.*/
Selenium.prototype.doCloseNotification = function(locator, value){
	var element = this.browserbot.findElement(locator);
	var doc = element.document;
	this.doMouseClick(locator, value);
	var notificationHidden = function() {
		// IE does not set parentNode to null but attaches the element to a document-fragment
		var hidden = (element.parentNode == null) || element.document != doc;
		return hidden;
	}
	return Selenium.decorateFunctionWithTimeout(notificationHidden, 5000);
};

/*Does a mouse over on target element at point x,y so tooltip shows up over element and not mouse cursor position*/
Selenium.prototype.doShowTooltip = function(locator, value){
	var element = this.browserbot.findElement(locator);
	var clientXY = getClientXY(element, value);

	this.browserbot.triggerMouseEvent(element, 'mouseover', true, clientXY[0], clientXY[1]);
};

/* For adding test to be run before this test */
Selenium.prototype.doIncludeTest = function(locator, path){
};

/**
 * Overridden the default selenium strategy because of IE trim bug
 * 
 *  OptionLocator for options identified by their labels.
 */
OptionLocatorFactory.prototype.OptionLocatorByLabel = function(label) {
    this.label = label;
    this.labelMatcher = new PatternMatcher(this.label);
    this.findOption = function(element) {
        for (var i = 0; i < element.options.length; i++) {
        	// IE does not trim the text property like other browsers
			var text = element.options[i].text.replace(/^\s+|\s+$/g,"");
            if (this.labelMatcher.matches(text)) {
                return element.options[i];
            }
        }
        throw new SeleniumError("Option with label '" + this.label + "' not found");
    };

    this.assertSelected = function(element) {
       	// IE does not trim the text property like other browsers
        var selectedLabel = element.options[element.selectedIndex].text.replace(/^\s+|\s+$/g,"");
        Assert.matches(this.label, selectedLabel)
    };
};

/**
 * Copies triggerKeyEvent from htmlutils.js and removes keycode for charCodeArg on firefox keyEvent
 */
function triggerSpecialKeyEvent(element, eventType, keySequence, canBubble, controlKeyDown, altKeyDown, shiftKeyDown, metaKeyDown) {
    var keycode = getKeyCodeFromKeySequence(keySequence);
    canBubble = (typeof(canBubble) == undefined) ? true : canBubble;
    if (element.fireEvent && element.ownerDocument && element.ownerDocument.createEventObject && element.ownerDocument.createEvent === undefined) {
    	// IE6-8
        var keyEvent = createEventObject(element, controlKeyDown, altKeyDown, shiftKeyDown, metaKeyDown);
        keyEvent.keyCode = keycode;
        try {
			element.fireEvent('on' + eventType, keyEvent);
		} catch (e) {
			if (e.number && e.number == -2147467259) {
				// IE is most likely trying to tell us that the element was
				// removed and the event could thus not be sent. We ignore this.
			} else {
				throw e;
			}
		}
    } else {
        var evt;
        if (window.KeyEvent) {
            evt = document.createEvent('KeyEvents');
            evt.initKeyEvent(eventType, true, true, window, controlKeyDown, altKeyDown, shiftKeyDown, metaKeyDown, keycode, "");
        } else {
        	// WebKit based browsers and IE9
      		evt = element.ownerDocument.createEvent('Events');
            
            evt.shiftKey = shiftKeyDown;
            evt.metaKey = metaKeyDown;
            evt.altKey = altKeyDown;
            evt.ctrlKey = controlKeyDown;

            evt.initEvent(eventType, true, true);
            evt.keyCode = parseInt(keycode);
            evt.which = keycode;
        }

        element.dispatchEvent(evt);
    }
}

Selenium.prototype.getElementPositionTop = function(locator) {
   /**
   * Retrieves the vertical position of an element
   *
   * @param locator an <a href="#locators">element locator</a> pointing to an element OR an element itself
   * @return number of pixels from the edge of the frame.
   */
    var element;
	if ("string"==typeof locator) {
		element = this.browserbot.findElement(locator);
	} else {
		element = locator;
	}

	var y = 0;
	while (element != null) {
        if(document.all) {
            if( (element.tagName != "TABLE") && (element.tagName != "BODY") ) {
				y += element.clientTop;
            }
        } else {
			// Netscape/DOM
            if(element.tagName == "TABLE") {
				var parentBorder = parseInt(element.border);
				if(isNaN(parentBorder)) {
					var parentFrame = element.getAttribute('frame');
					if(parentFrame != null) {
						y += 1;
					}
				} else if(parentBorder > 0) {
					y += parentBorder;
				}
            } else if (!/Opera[\/\s](\d+\.\d+)/.test(navigator.userAgent)) {
				y += element.clientTop;
			}
        }
        y += element.offsetTop;
		element = element.offsetParent;
    }
    return y;
};

//Starts dragging of taget element
Selenium.prototype.doDrag = function(locator, value){
	var element = this.browserbot.findElement(locator);
	var clientXY = getClientXY(element, value);

	this.browserbot.triggerMouseEvent(element, 'mouseover', true, clientXY[0], clientXY[1]);
	this.browserbot.triggerMouseEvent(element, 'mousedown', true, clientXY[0], clientXY[1]);
	this.browserbot.triggerMouseEvent(element, 'mousemove', true, clientXY[0], clientXY[1]);
	this.browserbot.triggerMouseEvent(element, 'mousemove', true, clientXY[0]+1, clientXY[1]+1);
};

// Drops target element from drag on this target element
Selenium.prototype.doDrop = function(locator, value){
	var element = this.browserbot.findElement(locator);
	var clientXY = getClientXY(element, value);

	this.browserbot.triggerMouseEvent(element, 'mouseover', true, clientXY[0]-1, clientXY[1]-1);
	this.browserbot.triggerMouseEvent(element, 'mousemove', true, clientXY[0]-1, clientXY[1]-1);
	this.browserbot.triggerMouseEvent(element, 'mousemove', true, clientXY[0], clientXY[1]);
	this.browserbot.triggerMouseEvent(element, 'mouseup', true, clientXY[0], clientXY[1]);
};

// Assert that an element has the specified css class.
Selenium.prototype.doAssertCSSClass = function(locator, value){
	var element = this.browserbot.findElement(locator);
	var css = element.className;
    
    var splitNames = css.split(' ');
    var matcher = new PatternMatcher(value);
    for (var i = 0; i < splitNames.length; i++) {
        if (matcher.matches(splitNames[i])) {
            return;
        }
    }

    Assert.fail("Element doesn't have the " + value + " class.");
};

Selenium.prototype.doAssertNotCSSClass = function(locator, value){
	var element = this.browserbot.findElement(locator);
	var css = element.className;

    var splitNames = css.split(' ');
    var matcher = new PatternMatcher(value);
    for (var i = 0; i < splitNames.length; i++) {
        if (matcher.matches(splitNames[i])) {
            Assert.fail("Element has the " + value + " class.");
            return;
        }
    }
};

Selenium.prototype.doUploadFile = function(locator, value){
	this.doType(locator, value);
};

/* Override doType to replay as a bunch of enterCharacter's unless value is an empty string */
Selenium.prototype.doType = function(locator, value) {
	if ("" == value) {
	    var element = this.browserbot.findElement(locator);
		core.events.setValue(element, value);
	} else {
		this.doEnterCharacter(locator, value);
	}
}
