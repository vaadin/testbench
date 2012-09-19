/*
 * Copyright 2005 Shinya Kasatani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * type
 */
Recorder.addEventHandler('type', 'change', function(event) {
		var tagName = event.target.tagName.toLowerCase();
		var type = event.target.type;
		if (('input' == tagName && ('text' == type || 'password' == type || 'file' == type)) ||
			'textarea' == tagName) {
			this.record("type", this.findLocators(event.target), event.target.value);
		}
	});

/*
 * select / addSelection / removeSelection
 */
Recorder.addEventHandler('selectFocus', 'focus', function(event) {
		if(event.target.nodeName == undefined){
			return;
		}
		var tagName = event.target.nodeName.toLowerCase();
		if ('select' == tagName && event.target.multiple) {
			this.log.debug('remembering selections');
			var options = event.target.options;
			for (var i = 0; i < options.length; i++) {
				if (options[i]._wasSelected == null) {
					// is the focus was gained by mousedown event, _wasSelected would be already set
					options[i]._wasSelected = options[i].selected;
				}
			}
		}
	}, { capture: true });

Recorder.addEventHandler('selectMousedown', 'mousedown', function(event) {
		var tagName = event.target.nodeName.toLowerCase();
		if ('option' == tagName) {
			var parent = event.target.parentNode;
			if (parent.multiple) {
				this.log.debug('remembering selections');
				var options = parent.options;
				for (var i = 0; i < options.length; i++) {
					options[i]._wasSelected = options[i].selected;
				}
			}
		}
	}, { capture: true });

Recorder.prototype.getOptionLocator = function(option) {
    var label = option.text.replace(/^ *(.*?) *$/, "$1");
    if (label.match(/\xA0/)) { // if the text contains &nbsp;
        return "label=regexp:" + label.replace(/[\(\)\[\]\\\^\$\*\+\?\.\|\{\}]/g, function(str) {return '\\' + str})
                                      .replace(/\s+/g, function(str) {
                if (str.match(/\xA0/)) {
                    if (str.length > 1) {
                        return "\\s+";
                    } else {
                        return "\\s";
                    }
                } else {
                    return str;
                }
            });
    } else {
        return "label=" + label;
    }
}

/*
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
		}
	});

Recorder.addEventHandler('clickLocator', 'click', function(event) {
		if (event.button == 0) {
			var clickable = this.findClickableElement(event.target);
			if (clickable) {
                // prepend any required mouseovers. These are defined as
                // handlers that set the "mouseoverLocator" attribute of the
                // interacted element to the locator that is to be used for the
                // mouseover command. For example:
                //
                // Recorder.addEventHandler('mouseoverLocator', 'mouseover', function(event) {
                //     var target = event.target;
                //     if (target.id == 'mmlink0') {
                //         this.mouseoverLocator = 'img' + target._itemRef;
                //     }
                //     else if (target.id.match(/^mmlink\d+$/)) {
                //         this.mouseoverLocator = 'lnk' + target._itemRef;
                //     }
                // }, { alwaysRecord: true, capture: true });
                //
                if (this.mouseoverLocator) {
                    this.record('mouseOver', this.mouseoverLocator, '');
                    delete this.mouseoverLocator;
                }
                this.record("click", this.findLocators(event.target), '');
            } else {
                var target = event.target;
                this.callIfMeaningfulEvent(function() {
                        this.record("click", this.findLocators(target), '');
                    });
            }
		}
	}, { capture: true });

Recorder.prototype.findClickableElement = function(e) {
	if (!e.tagName) return null;
	var tagName = e.tagName.toLowerCase();
	var type = e.type;
	if (e.hasAttribute("onclick") || e.hasAttribute("href") || tagName == "button" ||
		(tagName == "input" && 
		 (type == "submit" || type == "button" || type == "image" || type == "radio" || type == "checkbox" || type == "reset"))) {
		return e;
	} else {
		if (e.parentNode != null) {
			return this.findClickableElement(e.parentNode);
		} else {
			return null;
		}
	}
}
*/

// remember clicked element to be used in CommandBuilders
Recorder.addEventHandler('rememberClickedElement', 'mousedown', function(event) {
		this.clickedElement = event.target;
		this.clickedElementLocators = this.findLocators(event.target);
	}, { alwaysRecord: true, capture: true });

Recorder.addEventHandler('attrModified', 'DOMAttrModified', function(event) {
        this.log.debug('attrModified');
        this.domModified();
    }, {capture: true});

Recorder.addEventHandler('nodeInserted', 'DOMNodeInserted', function(event) {
        this.log.debug('nodeInserted');
        this.domModified();
    }, {capture: true});

Recorder.addEventHandler('nodeRemoved', 'DOMNodeRemoved', function(event) {
        this.log.debug('nodeRemoved');
        this.domModified();
    }, {capture: true});

Recorder.prototype.domModified = function() {
    if (this.delayedRecorder) {
        this.delayedRecorder.apply(this);
        this.delayedRecorder = null;
        if (this.domModifiedTimeout) {
            clearTimeout(this.domModifiedTimeout);
        }
    }
}

Recorder.prototype.callIfMeaningfulEvent = function(handler) {
    this.log.debug("callIfMeaningfulEvent");
    this.delayedRecorder = handler;
    var self = this;
    this.domModifiedTimeout = setTimeout(function() {
            self.log.debug("clear event");
            self.delayedRecorder = null;
            self.domModifiedTimeout = null;
        }, 50);
}

/************ TestBench specifics ************/

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

var previousTop = 0;
var previousLeft = 0;

Recorder.addEventHandler('scroll', 'scroll', function(event) {
	noDnd = true;
	if(event.target && event.target.wrappedJSObject) {
		var elem  = event.target.wrappedJSObject;
		if(elem.onscroll || elem.scrollIntoView) {
			this.log.warn(this);
			var loc = this.findLocators(event.target);
			var top = "" + elem.scrollTop;
			var left  = "" + elem.scrollLeft;
			if(this._scrollTimeout) {
				clearTimeout(this._scrollTimeout);
			}
			var s = this;
			var tree = window.editor.treeView;
			this._scrollTimeout = setTimeout(function(){
				if(previousLeft != left){
					s.record("scrollLeft", loc, left);
					previousLeft = left;
					s.record_orig("pause", "500");
				}
				if(previousTop != top){
					s.record("scroll", loc , top);
					previousTop = top;
					s.record_orig("pause", "500");
				}
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

var charBuffer = "";
var skipType = false;
var clearCharBuffer = false;

var KEYCODE_ENTER = 13;
var KEYCODE_LEFT = 37;
var KEYCODE_RIGHT = 39;
var KEYCODE_UP = 38;
var KEYCODE_DOWN = 40;
var KEYCODE_SPACE = 32;
var KEYCODE_COMMAND = 224;
var KEYCODE_LEFTSTART = 91;
var KETCODE_RIGHTSTART = 92;

/* Checks keyCodes on keydown event and adds a pressSpecialKey if confirmed. */
Recorder.addEventHandler('pressSpecialKey', 'keydown', function(event){
	var target = "";
	// only record modifiers if arrow key or character key pressed
	if(event.keyCode >= KEYCODE_SPACE){
		if (event.ctrlKey) {
			target = target + "ctrl ";
		}
		if (event.shiftKey) {
			target = target + "shift ";
		}
		if (event.altKey) {
			target = target + "alt ";
		}
		if (event.metaKey) {
			target = target + "meta ";
		}
	}
	
	var value=null;

	switch(event.keyCode){
//	case 9:
//		this.log.debug('pressed TAB');
//		value =  "tab";
//		break;
	case KEYCODE_ENTER:
		this.log.debug('pressed ENTER!');
		skipType = true;
		value = "enter";
		break;
	case KEYCODE_LEFT:
		this.log.debug('pressed LEFT!');
		value = target + "left";
		break;
	case KEYCODE_RIGHT:
		this.log.debug('pressed RIGHT!');
		value = target + "right";
		break;
	case KEYCODE_UP:
		this.log.debug('pressed UP!');
		value = target + "up";
		break;
	case KEYCODE_DOWN:
		this.log.debug('pressed DOWN!');
		value = target + "down";
		break;
	default:
		if(event.keyCode == KEYCODE_COMMAND || event.keyCode == KEYCODE_LEFTSTART || event.keyCode == KETCODE_RIGHTSTART){
			break;
		}
		if((event.ctrlKey || event.shiftKey || event.altKey || event.metaKey) && event.keyCode > KEYCODE_SPACE){
			this.log.debug('Recording key ' + String.fromCharCode(event.keyCode));
			value = target + String.fromCharCode(event.keyCode);
		} else if((event.ctrlKey || event.shiftKey || event.altKey || event.metaKey) && event.keyCode == KEYCODE_SPACE){
			// Parsing requires 'space' instead of ' '
			this.log.debug('Recording space');
			value = target + "space";
		}
	}
	
	if(value != null){
		if(charBuffer.length > 0){
			this.record("type", this.findLocators(event.target), charBuffer);
//			this.record("enterCharacter", this.findLocators(event.target), charBuffer);
			charBuffer = "";
			skipType = true;
		}
		this.record("pressSpecialKey", this.findLocators(event.target), value);
		clearCharBuffer = true;
	}
	
}, { capture: true });

/* record all keypresses to character buffer */
Recorder.addEventHandler('keyPressedDown', 'keypress', function(event){
	/* only record character keys and skip special keys */
	if(event.charCode >= KEYCODE_SPACE){
		this.log.debug('Typed key ' + String.fromCharCode(event.charCode));
		charBuffer = charBuffer + String.fromCharCode(event.charCode);
	}else if(event.keyCode >= KEYCODE_SPACE){
		this.log.debug('Typed key ' + String.fromCharCode(event.keyCode));
		charBuffer = charBuffer + String.fromCharCode(event.keyCode);
	}else if(event.which >= KEYCODE_SPACE){
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
	/* Clear buffer due to modifierKey+character*/
	if(clearCharBuffer){
		charBuffer = "";
		clearCharBuffer = false;
	}
});

/* Override default type */
//Recorder.removeEventHandler('type');


var noSelection = true;
var clicked = false;
var cancelClick = false;

Recorder.addEventHandler('clickLocator', 'click', function(event){
	if(cancelClick){
		cancelClick = false;
		return;
	}

	charBuffer = "";
	
	if(Recorder.changeSelection){
		var target = this.findLocator(event.target);
		// Update target for current command
		window.editor.treeView.updateCurrentCommand('target', target);
		// Update new target value to textfield
		document.getElementById('commandTarget').value=target;
		// Set changeSelection to false
		Recorder.reSelectTarget();
		// Stops bubbling of event to browser (click is not made in browser element)
		event.cancelBubble = true;
		return;
	}
	if(Recorder.recordAssertText){
		var target = this.findLocators(event.target);
		this.record("assertText", target, getText(event.target));
		document.getElementById('assert-button').click();
		// Stops bubbling of event to browser (click is not made in browser element)
		event.cancelBubble = true;
		return;
	}
	
	/*
	 * Do not record clicks on select components. Select event will be enough.
	 */
    if(event.target.tagName.toLowerCase() == "select") {
    	return;
    }
	
	/* record mouse click if left button clicked and so select has been made */
	if (event.button == 0 && noSelection) {
        var x = event.clientX - editor.seleniumAPI.Selenium.prototype.getElementPositionLeft(event.target);
        var y = event.clientY - editor.seleniumAPI.Selenium.prototype.getElementPositionTop(event.target);
        
        /* Stop checking mouseOver events */
        if(checkForMouseOver){
        	checkForMouseOver = false;
            	this.record_orig("mouseClick", this.findLocators(event.target), x + ',' + y);
        	return;
        }
        
        /* Check that a mouse click doesn't add a new close for a notification.
         */
        if(closeNotificationRecorded){
        	if((new RegExp("Notification")).test(event.target.className) || (new RegExp("gwt-HTML")).test(event.target.parentNode.className)){
        		/* clicked on notification or it's inner element mark clicked as false and return without further handling of event */
        		closeNotificationRecorded = false;
        		return;
        	}else{
        		/* clicked on something else than the notification mark clicked false and handle event */
        		closeNotificationRecorded = false;
        	}
        }
        
        /* Check if label has a for attribute and skip click as it will call on another element that will create a click */
        if(event.target.hasAttribute("for")){
        	return;
        }

        var control = event.ctrlKey;
        var shift = event.shiftKey;
        var alt = event.altKey;
		var meta = event.metaKey;
        var specials = "";
        if(control){
        	specials = ":ctrl";
        }
        if(shift){
        	if(specials.length == 0){
            	specials = ":shift";
        	} else {
        		specials = specials + " shift";
        	}
        }
        if(alt){
        	if(specials.length == 0){
            	specials = ":alt";
        	} else {
        		specials = specials + " alt";
        	}
        }
		if(meta){
			if(specials.length == 0){
				specials = ":meta";
			} else {
				specials = specials + " meta";
			}
		}
        
        var clickable = this.findClickableElement(event.target);
		if (clickable) {
            if (this.mouseoverLocator) {
                this.record('mouseOver', this.mouseoverLocator, '');
                delete this.mouseoverLocator;
            }
            
            /* mark that a clickable element has been clicked so that DOMNodeInserted will be evaluated */
            /* for possible Notification and MenuBar events that might result.*/
			clicked = true;
			
            var target = this.findLocators(event.target);
            var parent = this.findLocators(event.target.parentNode);
            
            /* Catch links for separate handling */
            if((new RegExp("link")).test(target) || (new RegExp("@href")).test(target)  
            		|| (new RegExp("onclick=\"window.location")).test(target) || (new RegExp("onclick=\"window.location")).test(parent) 
            		|| (event.target.nodeName.toLowerCase() == "a") || (event.target.parentNode.nodeName.toLowerCase() == "a")){
            	
            	/* if target is clearly a vaadin component handle links */
            	if((new RegExp("vaadin=")).test(target)){
            		/* if link is only a uri fragment record with waitForVaadin, but use xPath for target */
            		/* TODO: chage this when vaadin links function with mouseClick */
            		if ((new RegExp("#")).test(event.target.href) || (new RegExp("#")).test(event.target.parentNode.href)){
            			var vaadin = target[0][0];
            			var vaadin_name = target[0][1];
            			target[0][0] = target[1][0];
            			target[0][1] = target[1][1];
            			target[1][0] = vaadin;
            			target[1][1] = vaadin_name;
	            			this.record("mouseClick", target, x + ',' + y + specials);
					/* if either target or target parent (click recorded for img,span, etc inside <a></a>)
					 * is a link <a/> record open instead of mouseClick as it fails in many cases */
            		}else if (event.target.nodeName.toLowerCase() == "a" && !(new RegExp(document.getElementById("baseURL").value)).test(event.target.href) && event.target.target != "_blank"){
            			this.record_orig("open", event.target.href, '');
            		} else if (event.target.parentNode.nodeName.toLowerCase() == "a" && !(new RegExp(document.getElementById("baseURL").value)).test(event.target.parentNode.href) && event.target.parentNode.target != "_blank"){
            			this.record_orig("open", event.target.parentNode.href, '');
            		/* else record mouseClick with possible AndWait added by seleniums editor */
            		} else {
	            			this.record("mouseClick", target, x + ',' + y + specials);
            		}
            	/* else record mouseClick with record_orig so that AndWait comes to right place */
				} else {
	            		this.record_orig("mouseClick", target, x + ',' + y);
				}
            	clicked = false;
            } else if ((new RegExp("@id=\'loginf\'")).test(target) && !(new RegExp("input")).test(target)) {
            	// if login form we need to add a small pause manually (IE can't handle AndWait).
        		this.record_orig("click", target, '');
            	this.record("pause", "1000", '');
        	} else if (((new RegExp("v-button")).test(event.target.className) || (new RegExp("v-button")).test(event.target.parentNode.className)) && event.target.type != "button") {
            	/* A class="v-button" requires a click without mouseDown+mouseUp */
            	this.record("click", target, '');
            } else {
            	/* record mouseClick with waitForVaadin(s) added */
        			this.record("mouseClick", target, x + ',' + y + specials);
            }
        } else {
            var target = event.target;
            /* Record all clicks inside div elements */
            if(event.target.nodeName.toLowerCase() == "div" || event.target.nodeName.toLowerCase() == "span"){
       				this.record("mouseClick", this.findLocators(target), x + ',' + y + specials);
            }
        }
	} else {
		noSelection = true;
	}
}, { capture: true });

Recorder.addEventHandler('dblClickLocator', 'dblclick', function(event) {
		var x = event.clientX - editor.seleniumAPI.Selenium.prototype.getElementPositionLeft(event.target);
	    var y = event.clientY - editor.seleniumAPI.Selenium.prototype.getElementPositionTop(event.target);
	    
	    this.record("doubleClickAt", this.findLocators(event.target), x + ',' + y);
	}, { capture: true });
	
/* Expand select/addSelection/removeSelection functionality*/
//Recorder.removeEventHandler('select');

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
		noSelection = false;
	}
}, { capture: true });

var counter = 0;
var closeNotificationRecorded = false;
var checkForMouseOver = false;
var getTooltip = false;
var openNotifications = 0;
var recordClose = 0;

Recorder.addEventHandler('append', 'DOMNodeInserted', function(event){
	/* Check inserted node if it's a div */
	if(event.target.nodeName.toLowerCase() == "div"){
		/* if we have clicked on something we expect to get a PopupPanel */
		if(clicked){
			var target = this.findLocators(event.target);
			/* if we found a popupPanel enable checking for mouse overs for
			 * recording MenuBar navigation
			 */
			if((new RegExp("gwt-PopupPanel")).test(event.target.className)){
				checkForMouseOver = true;
				clicked = false;
			}
			/*
			 * Stop checking inserted DOM nodes after 5 inserts 
			 */
			if(++counter > 5){
				clicked = false;
			}
		}else if((new RegExp("v-tooltip")).test(event.target.className)){
			/* If we found a v-tooltip enable checking of next mouse out */
			getTooltip = true;
		}
	}
});

Recorder.addEventHandler('remove', 'DOMNodeRemoved', function(event){
	var target = event.target;
	if (target.wrappedJSObject) {
		target = target.wrappedJSObject;
	}
	if(target.nodeName.toLowerCase() == "div"){
		/* If a Notification was removed. Record closeNotification event for closing notification */
		if((new RegExp("Notification")).test(target.className)){
			this.record("closeNotification", this.findLocators(target), '0,0');
		}
	}
});

/* Use mouse over events to record MenuBar navigation */
Recorder.addEventHandler('mouseOverEvent', 'mouseover', function(event){
	if(checkForMouseOver){
		var target = this.findLocators(event.target);
		if((new RegExp("menuitem menuitem-selected")).test(event.target.className)){
			this.record("mouseOver", target, '0,0');
		}
	}
});

/* use mouse out to record a mouseOver to show tooltip */
Recorder.addEventHandler('mouseOutEvent', 'mouseout', function(event){
	/* If tooltip has been shown record tooltip event */
	if(getTooltip){
		getTooltip = false;
		if(Recorder.recordTooltip){
			var target = this.findLocators(event.target);
			this.record("showTooltip", target, '0,0');
			document.getElementById('tooltip-button').click();
		}
	}
});

var mousedownX = 0;
var mousedownY = 0;
var clX = 0;
var clY = 0;
var mousedown = false;
var slider = false;
var split = false;
var calendar_event = false;
var dragTarget = null;
var dragElement = null;
var noDnd = false;

// save element, it's locator and mouse targets for checking if we have a drag event
Recorder.addEventHandler('mouseDownEvent', 'mousedown', function(event){
	if(!Recorder.changeSelection){
		dragElement = event.target;
	    slider = (new RegExp("v-slider")).test(dragElement.className);
	    split = (new RegExp("splitter")).test(dragElement.parentNode.className) && (new RegExp("v-splitpanel")).test(dragElement.parentNode.className);
	    calendar_event = (new RegExp("v-calendar-event")).test(dragElement.className);
	    
	    mousedown = true;
		dragTarget = this.findLocators(dragElement);
	    mousedownX = editor.seleniumAPI.Selenium.prototype.getElementPositionLeft(dragElement);
	    mousedownY = editor.seleniumAPI.Selenium.prototype.getElementPositionTop(dragElement);
	    clX = event.clientX;
	    clY = event.clientY;
	}
}, { capture: true });

// if we have a mouse down and have moved the mouse more than 10px horizontally or vertically then do a drag and drop event
Recorder.addEventHandler('mouseUpEvent', 'mouseup', function(event){
	if(noDnd){
		noDnd = false;
	} else if (mousedown && (Math.abs(clX-event.clientX) >= 10 || Math.abs(clY-event.clientY) >= 10) && !split && !slider) {
    	var target =  dragElement.ownerDocument.elementFromPoint(event.clientX, event.clientY);
    	if (target != null && target.nodeType == 3) {
    		target = target.parentNode;
    	}
    	if(target != dragElement){
			var x = event.clientX - editor.seleniumAPI.Selenium.prototype.getElementPositionLeft(target);
	        var y = event.clientY - editor.seleniumAPI.Selenium.prototype.getElementPositionTop(target);
	
	        this.record("drag", dragTarget, (clX-mousedownX) + ',' + (clY-mousedownY));
	        this.record("drop", this.findLocators(target), x + ',' + y);
    	}

		// Clear all mouse down targets.
		mousedown = slider = split = calendar_event = false;
		mousedownX = mousedownY = 0;
		dragTarget = dragElement = null;
		clX = clY = 0;
	}

}, { capture: false });

Recorder.addEventHandler('slideSplitEvent', 'mouseup', function(event){
	if (slider || split) {
		// alert(slider + "::" + split);
		var x = editor.seleniumAPI.Selenium.prototype.getElementPositionLeft(dragElement) - mousedownX;
	    var y = editor.seleniumAPI.Selenium.prototype.getElementPositionTop(dragElement) - mousedownY;
	    
		this.record("dragAndDrop", dragTarget, x + ',' + y );

		// Clear all mouse down targets.
		mousedown = slider = split = calendar_event = false;
		mousedownX = mousedownY = 0;
		dragTarget = dragElement = null;
		clX = clY = 0;
		
		cancelClick = true;
	} else if (calendar_event){
		var target =  dragElement.ownerDocument.elementFromPoint(event.clientX, event.clientY);
    	if (target != null && target.nodeType == 3) {
    		target = target.parentNode;
    	}
    	if(target != dragElement){
			var x = event.clientX - editor.seleniumAPI.Selenium.prototype.getElementPositionLeft(target);
	        var y = event.clientY - editor.seleniumAPI.Selenium.prototype.getElementPositionTop(target);
	
	        this.record("drag", dragTarget, (clX-mousedownX) + ',' + (clY-mousedownY));
	        this.record("mouseMoveAt", this.findLocators(target), x + ',' + y);
	        this.record("drop", this.findLocators(target), x + ',' + y);
    	}

		// Clear all mouse down targets.
		mousedown = slider = split = calendar_event = false;
		mousedownX = mousedownY = 0;
		dragTarget = dragElement = null;
		clX = clY = 0;
	}
}, {capture: true});



