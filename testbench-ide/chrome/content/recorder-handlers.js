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
//Recorder.addEventHandler('type', 'change', function(event) {
//		var tagName = event.target.tagName.toLowerCase();
//		var type = event.target.type;
//		if (('input' == tagName && ('text' == type || 'password' == type || 'file' == type)) ||
//			'textarea' == tagName) {
//			this.record("type", this.findLocators(event.target), event.target.value);
//		}
//	});

/*
 * select / addSelection / removeSelection
 */
Recorder.addEventHandler('selectFocus', 'focus', function(event) {
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

//Recorder.addEventHandler('select', 'change', function(event) {
//		var tagName = event.target.tagName.toLowerCase();
//		if ('select' == tagName) {
//			if (!event.target.multiple) {
//                var option = event.target.options[event.target.selectedIndex];
//				this.log.debug('selectedIndex=' + event.target.selectedIndex);
//				this.record("select", this.findLocators(event.target), this.getOptionLocator(option));
//			} else {
//				this.log.debug('change selection on select-multiple');
//				var options = event.target.options;
//				for (var i = 0; i < options.length; i++) {
//					this.log.debug('option=' + i + ', ' + options[i].selected);
//					if (options[i]._wasSelected == null) {
//						this.log.warn('_wasSelected was not recorded');
//					}
//					if (options[i]._wasSelected != options[i].selected) {
//                        var value = this.getOptionLocator(options[i]);
//						if (options[i].selected) {
//							this.record("addSelection", this.findLocators(event.target), value);
//						} else {
//							this.record("removeSelection", this.findLocators(event.target), value);
//						}
//						options[i]._wasSelected = options[i].selected;
//					}
//				}
//			}
//		}
//	});
//
//Recorder.addEventHandler('clickLocator', 'click', function(event) {
//		if (event.button == 0) {
//			var clickable = this.findClickableElement(event.target);
//			if (clickable) {
//                // prepend any required mouseovers. These are defined as
//                // handlers that set the "mouseoverLocator" attribute of the
//                // interacted element to the locator that is to be used for the
//                // mouseover command. For example:
//                //
//                // Recorder.addEventHandler('mouseoverLocator', 'mouseover', function(event) {
//                //     var target = event.target;
//                //     if (target.id == 'mmlink0') {
//                //         this.mouseoverLocator = 'img' + target._itemRef;
//                //     }
//                //     else if (target.id.match(/^mmlink\d+$/)) {
//                //         this.mouseoverLocator = 'lnk' + target._itemRef;
//                //     }
//                // }, { alwaysRecord: true, capture: true });
//                //
//                if (this.mouseoverLocator) {
//                    this.record('mouseOver', this.mouseoverLocator, '');
//                    delete this.mouseoverLocator;
//                }
//                this.record("click", this.findLocators(event.target), '');
//            } else {
//                var target = event.target;
//                this.callIfMeaningfulEvent(function() {
//                        this.record("click", this.findLocators(target), '');
//                    });
//            }
//		}
//	}, { capture: true });
//
//Recorder.prototype.findClickableElement = function(e) {
//	if (!e.tagName) return null;
//	var tagName = e.tagName.toLowerCase();
//	var type = e.type;
//	if (e.hasAttribute("onclick") || e.hasAttribute("href") || tagName == "button" ||
//		(tagName == "input" && 
//		 (type == "submit" || type == "button" || type == "image" || type == "radio" || type == "checkbox" || type == "reset"))) {
//		return e;
//	} else {
//		if (e.parentNode != null) {
//			return this.findClickableElement(e.parentNode);
//		} else {
//			return null;
//		}
//	}
//}

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
			this._scrollTimeout = setTimeout(function(){
				if(previousLeft != left){
					s.record_orig("scrollLeft", loc, left);
					previousLeft = left;
				}
				if(previousTop != top){
					s.record_orig("scroll", loc , top);
					previousTop = top;
				}
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

var charBuffer = "";
var typeString = "true";

/* Checks keyCodes on keyup event and adds a pressArrowKey if confirmed. */
Recorder.addEventHandler('pressSpecialKey', 'keyup', function(event){
		/* only record modifiers if arrow keys pressed */
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
			if(event.target.nodeName.toLowerCase() == "input"){
				this.record("pressSpecialKey", this.findLocators(event.target), "enter");
			}
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
		
		/* only record modifiers if arrow keys pressed */
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
		/* only record character keys and skip special keys */
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
//Recorder.removeEventHandler('type');

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
//Recorder.removeEventHandler('clickLocator');

Recorder.addEventHandler('clickLocator', 'click', function(event){
		charBuffer = "";
		
		if(Recorder.changeSelection=="true"){
			var target = this.findLocator(event.target);
			// Update target for current command
			window.editor.treeView.updateCurrentCommand('target', target);
			// Update new target value to textfield
			document.getElementById('commandTarget').value=target;
			// Set changeSelection to false
			Recorder.reSelectTarget();
			// Stops bubbling of event to browser (click is not made in browser element)
			event.cancelBubble="true";
			return;
		}
		
		/* record mouse click if left button clicked and so select has been made */
		if (event.button == 0 && noSelection == "true") {
            var x = event.clientX - editor.seleniumAPI.Selenium.prototype.getElementPositionLeft(event.target);
            var y = event.clientY - editor.seleniumAPI.Selenium.prototype.getElementPositionTop(event.target);

            /* Stop checking mouseOver events */
            if(checkForMouseOver == "true"){
            	checkForMouseOver = "false";
            	this.record_orig("mouseClick", this.findLocators(event.target), x + ',' + y);
            	return;
            }
            
            /* Check that a mouse click doesn't add a new close for a notification.
             */
            if(closeNotificationRecorded == "true"){
            	if((new RegExp("Notification")).test(event.target.className) || (new RegExp("gwt-HTML")).test(event.target.parentNode.className)){
            		/* clicked on notification or it's inner element mark clicked as false and return without further handling of event */
            		closeNotificationRecorded = "false";
            		return;
            	}else{
            		/* clicked on something else than the notification mark clicked false and handle event */
            		closeNotificationRecorded = "false";
            	}
            }
            
             /* Check if label has a for attribute and skip click as it will call on another element that will create a click */
            if(event.target.hasAttribute("for")){
            	return;
            }

            var clickable = this.findClickableElement(event.target);
			if (clickable) {
	            if (this.mouseoverLocator) {
	                this.record('mouseOver', this.mouseoverLocator, '');
	                delete this.mouseoverLocator;
	            }
	            
	            /* mark that a clickable element has been clicked so that DOMNodeInserted will be evaluated */
	            /* for possible Notification and MenuBar events that might result.*/
				clicked = "true";
				
	            var target = this.findLocators(event.target);
	            var parent = this.findLocators(event.target.parentNode);
	            
	            /* Catch links for separate handling */
	            if((new RegExp("link")).test(target) || (new RegExp("@href")).test(target)  
	            		|| (new RegExp("onclick=\"window.location")).test(target) || (new RegExp("onclick=\"window.location")).test(parent) 
	            		|| (event.target.nodeName.toLowerCase() == "a") || (event.target.parentNode.nodeName.toLowerCase() == "a")){
	            	
	            	/* if target is clearly a vaadin component handle links */
	            	if((new RegExp("vaadin=")).test(target)){
	            		/* if link is only a hash record with waitForVaadin */
	            		if ((new RegExp("#")).test(event.target.href) || (new RegExp("#")).test(event.target.parentNode.href)){
							this.record("mouseClick", target, x + ',' + y);
						/* if either target or target parent (click recorded for img,span, etc inside <a></a>)
						 * is a link <a/> record open instead of mouseClick as it fails in many cases */
	            		} else if (event.target.nodeName.toLowerCase() == "a" && event.target.target != "_blank"){
	            			this.record_orig("open", event.target.href, '');
	            		} else if (event.target.parentNode.nodeName.toLowerCase() == "a" && event.target.parentNode.target != "_blank"){
	            			this.record_orig("open", event.target.parentNode.href, '');
	            		/* else record mouseClick with possible AndWait added by seleniums editor */
	            		} else {
	            			this.record_orig("mouseClick", target, x + ',' + y);
	            		}
	            	/* else record mouseClick with record_orig so that AndWait comes to right place */
					} else {
	            		this.record_orig("mouseClick", target, x + ',' + y);
					}
	            	clicked = "false";
	            } else if ((new RegExp("v-button")).test(event.target.className) && event.target.type != "button"){
	            	/* A class="v-button" requires a click without mouseDown+mouseUp */
	            	this.record("click", target, '');
	            } else if ( x < 0 || y < 0){
	            	/* Check that cordinates are inside an actual element. 
	                 * (RichTextField buttons record a negative and positive placement 
	                 * this will drop the negative one)
	                 */
	            } else {
	            	/* record mouseClick with waitForVaadin(s) added */
	            	this.record("mouseClick", target, x + ',' + y);
	            }
	        } else {
	            var target = event.target;
//	            this.callIfMeaningfulEvent(function() {
	            /* Record all clicks inside div elements */
	            if(event.target.nodeName.toLowerCase() == "div"){
	            	this.record("mouseClick", this.findLocators(target), x + ',' + y);
	            }
//	                });
	        }
		} else {
			noSelection = "true";
		}
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
		noSelection = "false";
	}
});

var counter = 0;
var closeNotificationRecorded = "false";
var checkForMouseOver = "false";
var getTooltip = "false";
var openNotifications = 0;
var recordClose = 0;

Recorder.addEventHandler('append', 'DOMNodeInserted', function(event){
		/* Check inserted node if it's a div */
		if(event.target.nodeName.toLowerCase() == "div"){
			/* if we have clicked on something we expect to get a PopupPanel or a notification */
			if(clicked == "true"){
				var target = this.findLocators(event.target);
				/* if we found a notification record a closeNotification event */
				if((new RegExp("Notification")).test(event.target.className)){
					openNotifications++;
					if(openNotifications > 1 || getTooltip == "true"){
						recordClose++;
					}else{
						this.record("closeNotification", target, '0,0');
					}
					clicked = "false";
					closeNotificationRecorded = "true";
					
				/* if we found a popupPanel enable checking for mouse overs for
				 * recording MenuBar navigation
				 */
				}else if((new RegExp("gwt-PopupPanel")).test(event.target.className)){
					checkForMouseOver = "true";
					clicked = "false";
				}
				/*
				 * Stop checking inserted DOM nodes after 5 inserts 
				 */
				if(++counter > 5){
					clicked = "false";
				}
			}else if((new RegExp("v-tooltip")).test(event.target.className)){
				/* If we found a v-tooltip enable checking of next mouse out */
				getTooltip = "true";
			}
		}
	});

Recorder.addEventHandler('remove', 'DOMNodeRemoved', function(event){
		if(event.target.nodeName.toLowerCase() == "div"){
			if((new RegExp("Notification")).test(event.target.className)){
				openNotifications--;
				if(recordClose > 0){
					recordClose--;
					this.record("closeNotification", this.findLocators(event.target), '0,0');
				}
			}
		}
	});

/* Use mouse over events to record MenuBar navigation */
Recorder.addEventHandler('mouseOverEvent', 'mouseover', function(event){
		if(checkForMouseOver == "true"){
			var target = this.findLocators(event.target);
			if((new RegExp("menuitem menuitem-selected")).test(event.target.className)){
				this.record("mouseOver", target, '0,0');
			}
		}
	});

/* use mouse out to record a mouseOver to show tooltip */
Recorder.addEventHandler('mouseOutEvent', 'mouseout', function(event){
		/* If tooltip has been shown record tooltip event */
		if(getTooltip == "true"){
			getTooltip = "false";
			if(Recorder.recordTooltip == "true"){
				var target = this.findLocators(event.target);
				this.record("showTooltip", target, '0,0');
				document.getElementById('tooltip-button').click();
			}
		}
	});
