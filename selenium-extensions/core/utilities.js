
/**
 * Resizes the browser window size so the canvas has the given width and height.
 * <p>
 * Note: Does not work in Opera as Opera does not allow window.resize(w,h).
 * Opera is resized during startup using custom profiles.
 * </p>
 */
function vaadin_testbench_calculateAndSetCanvasSize(width, height) {
	var win = selenium.browserbot.getUserWindow();
	var body = win.document.body;
	
	var innerWidth = win.innerWidth;
    var innerHeight = win.innerHeight;
	if (typeof innerWidth == 'undefined') {
		vaadin_testbench_hideIEScrollBar();
		innerWidth = body.clientWidth;
		innerHeight = body.clientHeight;
	}

    // Need to move browser to top left before resize to avoid the
    // possibility that it goes below or to the right of the screen.
    
    win.moveTo(1,1);
    if (navigator.userAgent.indexOf("Chrome") != -1) {
        // Window resize functions are pretty broken in Chrome 6..
        do {
        	sleep(500);
        	innerWidth = win.innerWidth;
        	innerHeight = win.innerHeight;
        	win.resizeBy(width-innerWidth, height-innerHeight);
        } while (win.innerWidth != width || win.innerHeight != height);
    } else {
    	innerWidth = win.innerWidth;
        innerHeight = win.innerHeight;
    	if (typeof innerWidth == 'undefined') {
    		vaadin_testbench_hideIEScrollBar();
    		innerWidth = body.clientWidth;
    		innerHeight = body.clientHeight;
    	}

    	win.resizeBy(width-innerWidth, height-innerHeight);
    }

    if (navigator.userAgent.indexOf("Linux") != -1 && navigator.userAgent.indexOf("Chrome") != -1) {
        // window.resizeTo() is pretty badly broken in Linux Chrome...

        // Need to wait for innerWidth to stabilize (Chrome issue #55409)
        sleep(500);

    	innerWidth = win.innerWidth;
        innerHeight = win.innerHeight;

        // Hide main view scrollbar to get correct measurements in IE
        // (overflow=hidden)
        if (typeof innerWidth == 'undefined') {
        	body.style.overflow='hidden';
        	innerWidth = body.clientWidth;
        	innerHeight = body.clientHeight;
        }
        var getSize = innerWidth+','+innerHeight;
        var newSizes = getSize().split(",");
        var newWidth = parseInt(newSizes[0]);
        var newHeight = parseInt(newSizes[1]);

        var widthError = width - newWidth;
        var heightError = height - newHeight;

        // Correct the window size
        win.resizeTo(win.outerWidth - win.innerWidth + width + widthError,
        		win.outerHeight - win.innerHeight + height + heightError);
    }

    return win.outerWidth + "," + win.outerHeight;
}

function vaadin_testbench_hideIEScrollBar() {
    // Hide main view scrollbar to get correct measurements in IE
    // (overflow=hidden)
    if (navigator.userAgent.indexOf("MSIE") != -1) {
    	selenium.browserbot.getUserWindow().body.style.overflow='hidden';
    }
}

function vaadin_testbench_setWindowSize(width, height) {
	selenium.browserbot.getUserWindow().resizeTo(width, height);
}

function vaadin_testbench_getCanvasWidth() {
	var win = selenium.browserbot.getUserWindow();
    if (win.innerWidth) {
    	return win.innerWidth;
    }
    if (win.document.body.clientWidth) {
    	return win.document.body.clientWidth;
    }
    if (win.document.documentElement.clientWidth) {
    	return win.document.documentElement.clientWidth;
    }
    return 0;
}

function vaadin_testbench_getCanvasHeight() {
	var win = selenium.browserbot.getUserWindow();
    if (win.innerHeight) {
    	return win.innerHeight;
    }
	if (win.document.body.clientHeight) {
		return win.document.body.clientHeight;
    }
	if (win.document.documentElement.clientHeight) {
		return win.document.documentElement.clientHeight;
	}
	return 0;
}

/**
 * Gets or calculates the x position of the canvas in the upper left corner on
 * screen
 * 
 * @return the x coordinate of the canvas
 */
function vaadin_testbench_getCanvasX() {
	var win = selenium.browserbot.getUserWindow();

    // IE
    if (navigator.userAgent.indexOf("MSIE") != -1) {
	    // FIXME: Canvas position given by IE is 2px off
	    return win.screenLeft + 2;
    }
    var horizontalDecorations = win.outerWidth - win.innerWidth;
    return horizontalDecorations / 2 + win.screenX;
}

/**
 * Gets or calculates the y position of the canvas in the upper left corner on
 * screen
 * 
 * @param canvasHeight
 * @return
 */
function vaadin_testbench_getCanvasY(canvasHeight) {
    if (navigator.userAgent.indexOf("MSIE") != -1) {
	    // FIXME: Canvas position given by IE is 2px off
    	return selenium.browserbot.getUserWindow().screenTop + 2;
    }

    // We need to guess a location that is within the canvas. The window
    // is positioned at (0,0) or (1,1) at this point.

    // Using 0.95*canvasHeight we should always be inside the canvas.
    // 0.95 is used because the detection routine used later on also
    // checks some pixels below this position (for some weird reason).
    return (canvasHeight * 0.95) | 0;
}

function vaadin_testbench_getDimensions() {
    var screenWidth = screen.availWidth;
	var screenHeight = screen.availHeight;
	var canvasWidth = vaadin_testbench_getCanvasWidth();
	var canvasHeight = vaadin_testbench_getCanvasHeight();
	var canvasX = vaadin_testbench_getCanvasX();
	var canvasY = vaadin_testbench_getCanvasY(canvasHeight);
	return "" + screenWidth + "," + screenHeight + "," + canvasWidth
				+ "," + canvasHeight + "," + canvasX + "," + canvasY;
}
