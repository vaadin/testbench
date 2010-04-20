/* Selenium core extensions for Vaadin
 * 
 * Note, thi is used only be RC. IDE/recorder has same additions merged into it.
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
	if(value.length > 1){
		for(i = 0; i < value.length;i++){
			this.doKeyDown(locator, value.charAt(i));
			this.doKeyUp(locator, value.charAt(i));
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
	if(value.toLowerCase() == "left"){
		value="\\37";
	}else if(value.toLowerCase() == "right"){
		value="\\39";
	}else if(value.toLowerCase() == "up"){
		value="\\38";
	}else if(value.toLowerCase() == "down"){
		value="\\40";
	}else if(value.toLowerCase() == "enter"){
		value="\\13";
	}
	this.doKeyDown(locator, value);
	this.doKeyPress(locator, value);
	this.doKeyUp(locator, value);
};

/*Simulates the correct mouse click events.*/
Selenium.prototype.doMouseClick = function(locator, value){
	var element = this.browserbot.findElement(locator);
	var clientXY = getClientXY(element, value);

	this.browserbot.triggerMouseEvent(element, 'mousedown', true, clientXY[0], clientXY[1]);
//	element.focus();
	this.browserbot.triggerMouseEvent(element, 'mouseup', true, clientXY[0], clientXY[1]);
	this.browserbot.clickElement(element);
};

/*Opera requires a special mouseClick as it else clicks twice*/
Selenium.prototype.doMouseClickOpera = function(locator, value){
	var element = this.browserbot.findElement(locator);
	var clientXY = getClientXY(element, value);

	this.browserbot.triggerMouseEvent(element, 'mousedown', true, clientXY[0], clientXY[1]);
//	element.focus();
	this.browserbot.triggerMouseEvent(element, 'mouseup', true, clientXY[0], clientXY[1]);
};

/*Does a mouseClick on the target element. Used descriptive purposes.*/
Selenium.prototype.doCloseNotification = function(locator, value){
	this.doMouseClick(locator, value);
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

