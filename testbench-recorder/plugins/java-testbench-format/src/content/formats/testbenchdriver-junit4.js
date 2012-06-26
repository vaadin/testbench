/*
 * Formatter for Vaadin TestBench (Java JUnit).
 */

var subScriptLoader = Components.classes["@mozilla.org/moz/jssubscript-loader;1"].getService(Components.interfaces.mozIJSSubScriptLoader);
subScriptLoader.loadSubScript('chrome://testbench-recorder/content/formats/webdriver.js', this);

function useSeparateEqualsForArray() {
  return true;
}

function testClassName(testName) {
  return testName.split(/[^0-9A-Za-z]+/).map(
      function(x) {
        return capitalize(x);
      }).join('');
}

function testMethodName(testName) {
  return "test" + testClassName(testName);
}

function nonBreakingSpace() {
  return "\"\\u00a0\"";
}

function array(value) {
  var str = 'new String[] {';
  for (var i = 0; i < value.length; i++) {
    str += string(value[i]);
    if (i < value.length - 1) str += ", ";
  }
  str += '}';
  return str;
}

Equals.prototype.toString = function() {
  if (this.e1.toString().match(/^\d+$/)) {
    // int
    return this.e1.toString() + " == " + this.e2.toString();
  } else {
    // string
    return this.e1.toString() + ".equals(" + this.e2.toString() + ")";
  }
};

Equals.prototype.assert = function() {
  return "assertEquals(" + this.e1.toString() + ", " + this.e2.toString() + ");";
};

Equals.prototype.verify = function() {
  return verify(this.assert());
};

NotEquals.prototype.toString = function() {
  return "!" + this.e1.toString() + ".equals(" + this.e2.toString() + ")";
};

NotEquals.prototype.assert = function() {
  return "assertThat(" + this.e1.toString() + ", is(not(" + this.e2.toString() + ")));";
};

NotEquals.prototype.verify = function() {
  return verify(this.assert());
};

function joinExpression(expression) {
  return "join(" + expression.toString() + ", ',')";
}

function statement(expression) {
  var s = expression.toString();
  if (s.length == 0) {
    return null;
  }
  return s + ';';
}

function assignToVariable(type, variable, expression) {
  return type + " " + variable + " = " + expression.toString();
}

function ifCondition(expression, callback) {
  return "if (" + expression.toString() + ") {\n" + callback() + "}";
}

function assertTrue(expression) {
  return "assertTrue(" + expression.toString() + ");";
}

function assertFalse(expression) {
  return "assertFalse(" + expression.toString() + ");";
}

function verify(statement) {
  return "try {\n" +
      indents(1) + statement + "\n" +
      "} catch (Error e) {\n" +
      indents(1) + "verificationErrors.append(e.toString());\n" +
      "}";
}

function verifyTrue(expression) {
  return verify(assertTrue(expression));
}

function verifyFalse(expression) {
  return verify(assertFalse(expression));
}

RegexpMatch.prototype.toString = function() {
  if (this.pattern.match(/^\^/) && this.pattern.match(/\$$/)) {
    return this.expression + ".matches(" + string(this.pattern) + ")";
  } else {
    return "Pattern.compile(" + string(this.pattern) + ").matcher(" + this.expression + ").find()";
  }
};

function waitFor(expression) {
  return "for (int second = 0;; second++) {\n" +
      "\tif (second >= 60) fail(\"timeout\");\n" +
      "\ttry { " + (expression.setup ? expression.setup() + " " : "") +
      "if (" + expression.toString() + ") break; } catch (Exception e) {}\n" +
      "\tThread.sleep(1000);\n" +
      "}\n";
}

function assertOrVerifyFailure(line, isAssert) {
  var message = '"expected failure"';
  var failStatement = "fail(" + message + ");";
  return "try { " + line + " " + failStatement + " } catch (Throwable e) {}";
}

function pause(milliseconds) {
  return "Thread.sleep(" + parseInt(milliseconds, 10) + ");";
}

function echo(message) {
  return "System.out.println(" + xlateArgument(message) + ");";
}

function formatComment(comment) {
  return comment.comment.replace(/.+/mg, function(str) {
    return "// " + str;
  });
}

/**
 * Returns a string representing the suite for this formatter language.
 *
 * @param testSuite  the suite to format
 * @param filename   the file the formatted suite will be saved as
 */
function formatSuite(testSuite, filename) {
  var suiteClass = /^(\w+)/.exec(filename)[1];
  suiteClass = suiteClass[0].toUpperCase() + suiteClass.substring(1);

  var formattedSuite = "import junit.framework.Test;\n"
      + "import junit.framework.TestSuite;\n"
      + "\n"
      + "public class " + suiteClass + " {\n"
      + "\n"
      + indents(1) + "public static Test suite() {\n"
      + indents(2) + "TestSuite suite = new TestSuite();\n";

  for (var i = 0; i < testSuite.tests.length; ++i) {
    var testClass = testSuite.tests[i].getTitle();
    formattedSuite += indents(2)
        + "suite.addTestSuite(" + testClass + ".class);\n";
  }

  formattedSuite += indents(2) + "return suite;\n"
      + indents(1) + "}\n"
      + "\n"
      + indents(1) + "public static void main(String[] args) {\n"
      + indents(2) + "junit.textui.TestRunner.run(suite());\n"
      + indents(1) + "}\n"
      + "}\n";

  return formattedSuite;
}

function defaultExtension() {
  return this.options.defaultExtension;
}

this.options = {
  receiver: "driver",
  environment: "*chrome",
  packageName: "com.example.tests",
  indent:    'tab',
  initialIndents:    '2',
  showSelenese: 'false',
  defaultExtension: "java"
};

options.header =
    "package ${packageName};\n" +
        "\n" +
        "import java.util.regex.Pattern;\n" +
        "import java.util.concurrent.TimeUnit;\n" +
        "import org.junit.*;\n" +
        "import static org.junit.Assert.*;\n" +
        "import static org.hamcrest.CoreMatchers.*;\n" +
        "import org.openqa.selenium.Alert;\n" +
        "import org.openqa.selenium.Capabilities;\n" +
        "import org.openqa.selenium.Cookie;\n" +
        "import org.openqa.selenium.HasCapabilities;\n" +
        "import org.openqa.selenium.HasInputDevices;\n" +
        "import org.openqa.selenium.HasTouchScreen;\n" +
        "import org.openqa.selenium.JavascriptExecutor;\n" +
        "import org.openqa.selenium.Keyboard;\n" +
        "import org.openqa.selenium.Keys;\n" +
        "import org.openqa.selenium.Mouse;\n" +
        "import org.openqa.selenium.NoSuchElementException;\n" +
        "import org.openqa.selenium.OutputType;\n" +
        "import org.openqa.selenium.Rotatable;\n" +
        "import org.openqa.selenium.SearchContext;\n" +
        "import org.openqa.selenium.TakesScreenshot;\n" +
        "import org.openqa.selenium.TouchScreen;\n" +
        "import org.openqa.selenium.WebDriver;\n" +
        "import org.openqa.selenium.WebDriverCommandProcessor;\n" +
        "import org.openqa.selenium.WebElement;\n" +
        "import org.openqa.selenium.firefox.FirefoxDriver;\n" +
		"import org.openqa.selenium.interactions.Actions;\n" +
        "import org.openqa.selenium.support.ui.Select;\n" +
		"import com.vaadin.testbench.By;\n" +
		"import com.vaadin.testbench.TestBench;\n" +
		"import com.vaadin.testbench.TestBenchTestCase;\n" +
        "\n" +
        "public class ${className} extends TestBenchTestCase {\n" +
        "\tprivate WebDriver driver;\n" +
        "\tprivate String baseUrl;\n" +
        "\tprivate StringBuffer verificationErrors = new StringBuffer();\n" +
        "\t@Before\n" +
        "\tpublic void setUp() throws Exception {\n" +
        "\t\tdriver = TestBench.createDriver(new FirefoxDriver());\n" +
        "\t\tbaseUrl = \"${baseURL}\";\n" +
        "\t}\n" +
        "\n" +
        "\t@Test\n" +
        "\tpublic void ${methodName}() throws Exception {\n";

options.footer =
    "\t}\n" +
        "\n" +
        "\t@After\n" +
        "\tpublic void tearDown() throws Exception {\n" +
        "\t\tdriver.quit();\n" +
        "\t\tString verificationErrorString = verificationErrors.toString();\n" +
        "\t\tif (!\"\".equals(verificationErrorString)) {\n" +
        "\t\t\tfail(verificationErrorString);\n" +
        "\t\t}\n" +
        "\t}\n" +
        "}\n";

this.configForm =
    '<description>Variable for Selenium instance</description>' +
        '<textbox id="options_receiver" />' +
        '<description>Environment</description>' +
        '<textbox id="options_environment" />' +
        '<description>Package</description>' +
        '<textbox id="options_packageName" />' +
        '<checkbox id="options_showSelenese" label="Show Selenese"/>';

this.name = "JUnit 4 (Vaadin TestBench)";
this.testcaseExtension = ".java";
this.suiteExtension = ".java";
this.webdriver = true;

WDAPI.Driver = function() {
  this.ref = options.receiver;
};

WDAPI.Driver.searchContext = function(locatorType, locator) {
  var locatorString = xlateArgument(locator);
  switch (locatorType) {
    case 'xpath':
      return 'By.xpath(' + locatorString + ')';
    case 'css':
      return 'By.cssSelector(' + locatorString + ')';
    case 'id':
      return 'By.id(' + locatorString + ')';
    case 'link':
      return 'By.linkText(' + locatorString + ')';
    case 'name':
      return 'By.name(' + locatorString + ')';
    case 'tag_name':
      return 'By.tagName(' + locatorString + ')';
	case 'vaadin':
	  return 'By.vaadin(' + locatorString + ')';
  }
  throw 'Error: unknown strategy [' + locatorType + '] for locator [' + locator + ']';
};

WDAPI.Driver.prototype.back = function() {
  return this.ref + ".navigate().back()";
};

WDAPI.Driver.prototype.close = function() {
  return this.ref + ".close()";
};

WDAPI.Driver.prototype.findElement = function(locatorType, locator) {
  return new WDAPI.Element(this.ref + ".findElement(" + WDAPI.Driver.searchContext(locatorType, locator) + ")");
};

WDAPI.Driver.prototype.findElements = function(locatorType, locator) {
  return new WDAPI.ElementList(this.ref + ".findElements(" + WDAPI.Driver.searchContext(locatorType, locator) + ")");
};

WDAPI.Driver.prototype.getCurrentUrl = function() {
  return this.ref + ".getCurrentUrl()";
};

WDAPI.Driver.prototype.get = function(url) {
  if (url.length > 1 && (url.substring(1,8) == "http://" || url.substring(1,9) == "https://")) { // url is quoted
    return this.ref + ".get(" + url + ")";
  } else {
    return this.ref + ".get(concatUrl(baseUrl, " + url + "))";
  }
};

WDAPI.Driver.prototype.getTitle = function() {
  return this.ref + ".getTitle()";
};

WDAPI.Driver.prototype.refresh = function() {
  return this.ref + ".navigate().refresh()";
};

WDAPI.Element = function(ref) {
  this.ref = ref;
};

WDAPI.Element.prototype.clear = function() {
  return this.ref + ".clear()";
};

WDAPI.Element.prototype.click = function() {
  return this.ref + ".click()";
};

WDAPI.Element.prototype.getAttribute = function(attributeName) {
  return this.ref + ".getAttribute(" + xlateArgument(attributeName) + ")";
};

WDAPI.Element.prototype.getText = function() {
  return this.ref + ".getText()";
};

WDAPI.Element.prototype.isDisplayed = function() {
  return this.ref + ".isDisplayed()";
};

WDAPI.Element.prototype.isSelected = function() {
  return this.ref + ".isSelected()";
};

WDAPI.Element.prototype.sendKeys = function(text) {
  return this.ref + ".sendKeys(" + xlateArgument(text) + ")";
};

WDAPI.Element.prototype.submit = function() {
  return this.ref + ".submit()";
};

WDAPI.Element.prototype.select = function(label) {
  return "new Select(" + this.ref + ").selectByVisibleText(" + xlateArgument(label) + ")";
};

WDAPI.ElementList = function(ref) {
  this.ref = ref;
};

WDAPI.ElementList.prototype.getItem = function(index) {
  return this.ref + "[" + index + "]";
};

WDAPI.ElementList.prototype.getSize = function() {
  return this.ref + ".size()";
};

WDAPI.ElementList.prototype.isEmpty = function() {
  return this.ref + ".isEmpty()";
};

WDAPI.Utils = function() {
};

WDAPI.Utils.isElementPresent = function(how, what) {
  return "isElementPresent(" + WDAPI.Driver.searchContext(how, what) + ")";
};


/****** TestBench commands ******/

WDAPI.Element.prototype.closeNotification = function(element) {
	return "testBenchElement(" + this.ref + ").closeNotification()";
}

WDAPI.Element.prototype.showTooltip = function(element) {
	return "testBenchElement(" + this.ref + ").showTooltip()";
}

WDAPI.Element.prototype.scroll = function(scrollTop) {
	return "testBenchElement(" + this.ref + ").scroll(" + xlateArgument(scrollTop, "number") + ")";
}

WDAPI.Element.prototype.scrollLeft = function(scrollLeft) {
	return "testBenchElement(" + this.ref + ").scrollLeft(" + xlateArgument(scrollLeft, "number") + ")";
}

WDAPI.Element.prototype.pressSpecialKey = function(value) {
	var key = "\"" + value.substr(value.lastIndexOf(" ")+1) + "\"";
	if ((new RegExp("left")).test(value.toLowerCase())){
		key = "Keys.ARROW_LEFT";
	} else if ((new RegExp("right")).test(value.toLowerCase())){
		key = "Keys.ARROW_RIGHT";
	} else if ((new RegExp("up")).test(value.toLowerCase())){
		key = "Keys.ARROW_UP";
	} else if ((new RegExp("down")).test(value.toLowerCase())){
		key = "Keys.ARROW_DOWN";
	} else if ((new RegExp("enter")).test(value.toLowerCase())){
		key = "Keys.RETURN";
	} else if ((new RegExp("space")).test(value.toLowerCase())){
		key = "Keys.SPACE";
	} else if ((new RegExp("tab")).test(value.toLowerCase())){
		key = "Keys.TAB";
	}
	return this.ref + ".sendKeys(" + key + ")";
}

WDAPI.Element.prototype.mouseClick = function(driver, value) {
	value = value.split(":");
	var shift = false;
	var ctrl = false;
	var alt = false;
	var meta = false;
	if(value.length > 1){
		shift = (new RegExp("shift")).test(value[1]);
		ctrl = (new RegExp("ctrl")).test(value[1]);
		alt = (new RegExp("alt")).test(value[1]);
		meta = (new RegExp("meta")).test(value[1]);
	}
	var modifiersDown = "";
	var modifiersUp = "";
	if (shift) {
		modifiersDown += ".keyDown(Keys.SHIFT)";
		modifiersUp += ".keyUp(Keys.SHIFT)";
	}
	if (ctrl) {
		modifiersDown += ".keyDown(Keys.CTRL)"
		modifiersUp += ".keyUp(Keys.CTRL)"
	}
	if (alt) {
		modifiersDown += ".keyDown(Keys.ALT)"
		modifiersUp += ".keyUp(Keys.ALT)"
	}
	if (meta) {
		modifiersDown += ".keyDown(Keys.META)"
		modifiersUp += ".keyUp(Keys.META)"
	}
	return "new Actions(" + driver.ref + ").moveToElement(" + this.ref + ")" + modifiersDown + ".click()" + modifiersUp + ".build().perform()";
}

WDAPI.Element.prototype.contextmenu = function(driver) {
	return "new Actions(" + driver.ref + ").contextClick(" + this.ref + ").perform()";
}

WDAPI.Element.prototype.addSelection = function(label) {
  return "new Select(" + this.ref + ").selectByVisibleText(" + xlateArgument(label) + ")";
};

WDAPI.Driver.prototype.pressModifierKeys = function(value) {
	var modifiers = "";
	if ((new RegExp("shift")).test(value)) {
		modifiers += ".keyDown(Keys.SHIFT)";
	}
	if ((new RegExp("ctrl")).test(value)) {
		modifiers += ".keyDown(Keys.CTRL)";
	}
	if ((new RegExp("alt")).test(value)) {
		modifiers += ".keyDown(Keys.ALT)";
	}
	if ((new RegExp("meta")).test(value)) {
		modifiers += ".keyDown(Keys.META)";
	}
	
	if (modifiers !== "") {
		return "new Actions(" + this.ref + ")" + modifiers + ".build().perform()";
	}
	return "";
}

WDAPI.Driver.prototype.releaseModifierKeys = function(value) {
	var modifiers = "";
	if ((new RegExp("shift")).test(value)) {
		modifiers += ".keyUp(Keys.SHIFT)";
	}
	if ((new RegExp("ctrl")).test(value)) {
		modifiers += ".keyUp(Keys.CTRL)";
	}
	if ((new RegExp("alt")).test(value)) {
		modifiers += ".keyUp(Keys.ALT)";
	}
	if ((new RegExp("meta")).test(value)) {
		modifiers += ".keyUp(Keys.META)";
	}
	if (modifiers !== "") {
		return "new Actions(" + this.ref + ")" + modifiers + ".build().perform()";
	}
	return "";
}

WDAPI.Driver.prototype.waitForVaadin = function() {
	return "testBench(" + this.ref + ").waitForVaadin()";
}

WDAPI.Driver.prototype.isTextPresent = function(value) {
	return this.ref + ".getPageSource().contains(\"" + value + "\")";
}

WDAPI.Driver.prototype.screenCapture = function(value) {
	return "testBench(" + this.ref + ").compareScreen(\"" + value + "\")";
}