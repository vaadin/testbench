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
        element.value = actualValue;
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
	triggerSpecialKeyEvent(element, 'keydown', value, true, ctrl, alt, shift, this.browserbot.metaKeyDown);
	triggerSpecialKeyEvent(element, 'keypress', value, true,ctrl, alt, shift, this.browserbot.metaKeyDown);
	triggerSpecialKeyEvent(element, 'keyup', value, true, ctrl, alt, shift, this.browserbot.metaKeyDown);
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
	}
	
	this.browserbot.triggerMouseEvent(element, 'mousedown', true, clientXY[0], clientXY[1]);
//	element.focus();
	this.browserbot.triggerMouseEvent(element, 'mouseup', true, clientXY[0], clientXY[1]);
	this.browserbot.clickElement(element);

	this.browserbot.shiftKeyDown = this.browserbot.controlKeyDown = this.browserbot.altKeyDown = false;
};

//Expect dialog will do a normal mouse click, but the following waitForVaadin will be skipped
//as dialog is expected and the next command needs to be 'assertConfirmation'
Selenium.prototype.doExpectDialog = function(locator, value){
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
	this.browserbot.clickElement(element);

	this.browserbot.shiftKeyDown = this.browserbot.controlKeyDown = this.browserbot.altKeyDown = false;
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
    if (element.fireEvent && element.ownerDocument && element.ownerDocument.createEventObject) { // IE
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
    }
    else {
        var evt;
        if (window.KeyEvent) {
            evt = document.createEvent('KeyEvents');
            evt.initKeyEvent(eventType, true, true, window, controlKeyDown, altKeyDown, shiftKeyDown, metaKeyDown, keycode, "");
        } else {
        	// WebKit based browsers
      		evt = document.createEvent('Events');
            
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

    if (!(new RegExp(value)).test(css)) {
        Assert.fail("Element doesn't have the " + value + " class.");
    }
};

Selenium.prototype.doAssertNotCSSClass = function(locator, value){
	var element = this.browserbot.findElement(locator);
	var css = element.className;

    if ((new RegExp(value)).test(css)) {
        Assert.fail("Element has the " + value + " class.");
    }
};

Selenium.prototype.doUploadFile = function(locator, value){
	this.doType(locator, value);
};
