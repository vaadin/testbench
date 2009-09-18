/* java-rc.js start */
/*
 * Format for Selenium Remote Control Java client.
 */

load('remoteControl.js');

this.name = "java-rc";

function useSeparateEqualsForArray() {
	return true;
}

function testMethodName(testName) {
	return "test" + capitalize(testName);
}

function assertTrue(expression) {
	return "assertTrue(" + expression.toString() + ");";
}

function verifyTrue(expression) {
	return "verifyTrue(" + expression.toString() + ");";
}

function assertFalse(expression) {
	return "assertFalse(" + expression.toString() + ");";
}

function verifyFalse(expression) {
	return "verifyFalse(" + expression.toString() + ");";
}

function assignToVariable(type, variable, expression) {
	return type + " " + variable + " = " + expression.toString();
}

function ifCondition(expression, callback) {
    return "if (" + expression.toString() + ") {\n" + callback() + "}";
}

function joinExpression(expression) {
    return "join(" + expression.toString() + ", ',')";
}

function waitFor(expression) {
	return "for (int second = 0;; second++) {\n" +
		"\tif (second >= 60) fail(\"timeout\");\n" +
		"\ttry { " + (expression.setup ? expression.setup() + " " : "") +
		"if (" + expression.toString() + ") break; } catch (Exception e) {}\n" +
		"\tThread.sleep(1000);\n" +
		"}\n";
	//return "while (" + not(expression).toString() + ") { Thread.sleep(1000); }";
}

function assertOrVerifyFailure(line, isAssert) {
	var message = '"expected failure"';
    var failStatement = "fail(" + message + ");";
	return "try { " + line + " " + failStatement + " } catch (Throwable e) {}";
}

Equals.prototype.toString = function() {
    if (this.e1.toString().match(/^\d+$/)) {
        // int
	    return this.e1.toString() + " == " + this.e2.toString();
    } else {
        // string
	    return this.e1.toString() + ".equals(" + this.e2.toString() + ")";
    }
}

Equals.prototype.assert = function() {
	return "assertEquals(" + this.e1.toString() + ", " + this.e2.toString() + ");";
}

Equals.prototype.verify = function() {
	return "verifyEquals(" + this.e1.toString() + ", " + this.e2.toString() + ");";
}

NotEquals.prototype.toString = function() {
	return "!" + this.e1.toString() + ".equals(" + this.e2.toString() + ")";
}

NotEquals.prototype.assert = function() {
	return "assertNotEquals(" + this.e1.toString() + ", " + this.e2.toString() + ");";
}

NotEquals.prototype.verify = function() {
	return "verifyNotEquals(" + this.e1.toString() + ", " + this.e2.toString() + ");";
}

RegexpMatch.prototype.toString = function() {
	if (this.pattern.match(/^\^/) && this.pattern.match(/\$$/)) {
		return this.expression + ".matches(" + string(this.pattern) + ")";
	} else {
		return "Pattern.compile(" + string(this.pattern) + ").matcher(" + this.expression + ").find()";
	}
}

function pause(milliseconds) {
	return "Thread.sleep(" + parseInt(milliseconds) + ");";
}

function echo(message) {
	return "System.out.println(" + xlateArgument(message) + ");";
}

function statement(expression) {
	return expression.toString() + ';';
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

function nonBreakingSpace() {
    return "\"\\u00a0\"";
}

CallSelenium.prototype.toString = function() {
	var result = '';
	if (this.negative) {
		result += '!';
	}
	if (options.receiver) {
		result += options.receiver + '.';
	}
	result += this.message;
	result += '(';
	for (var i = 0; i < this.args.length; i++) {
		result += this.args[i];
		if (i < this.args.length - 1) {
			result += ', ';
		}
	}
	result += ')';
	return result;
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

this.options = {
	receiver: "selenium",
	packageName: "com.example.tests",
	superClass: "SeleneseTestCase",
    indent:	'tab',
    initialIndents:	'2'
};

options.header =
	"package ${packageName};\n" +
	"\n" +
	"import com.thoughtworks.selenium.*;\n" +
	"import java.util.regex.Pattern;\n" +
	"\n" +
    "public class ${className} extends ${superClass} {\n" + 
    "\tpublic void setUp() throws Exception {\n" +
    '\t\tsetUp("${baseURL}", "*chrome");\n' +
    "\t}\n" +
    "\tpublic void ${methodName}() throws Exception {\n";

options.footer =
	"\t}\n" +
	"}\n";

this.configForm = 
	'<description>Variable for Selenium instance</description>' +
	'<textbox id="options_receiver" />';


/**
*
* java-rc.js ends
*  
**/

this.name = "vaadin-java-rc";
this.testName = "${methodName}";

function formatComment(comment) {
	if (comment.comment.match(/^selenium\.waitForVaadin\(\)/) ) {
		return "waitForVaadin();"
	}
	
	// Catch command selenium.screenCapture
	if (comment.comment.match(/^selenium\.screenCapture/) ) {
		// Check if screenCapture has a value but not a target
		if(comment.comment.match(/^selenium\.screenCapture\(\"\"/)){
			// Get value for screenCapture
			var str = comment.comment.substring(27, comment.comment.length-2);
			// Replace all \\ with File.separator
			var fileId = str.replace(/\\\\/gi, "\" + File.separator + \"");
			return "validateScreenshot(\"testFileNameHere\", 0.001, " + fileId + ");";
		}else{
			return "validateScreenshot(\"testFileNameHere\", 0.001, \"" + "\");";
		}
	} else if (comment.comment.match(/^selenium\.enterCharacter/)) {

		var parameters = comment.comment.substring(comment.comment.indexOf("\"")+1);
		var locator = parameters.substring(0, parameters.indexOf("\""));
		parameters = parameters.substring(parameters.indexOf("\"")+1);
		var value = parameters.substring(parameters.indexOf("\"")+1, parameters.lastIndexOf("\""));
		
		var result = "selenium.type(\"" + locator + "\", \"" + value + "\");\n";
		

		if(value.length > 1){
			for(i = 0; i < value.length;i++){
				result = result + "selenium.keyDown(\"" + locator + "\", \"" + value.charAt(i) + "\");\n";
				result = result + "selenium.keyUp(\"" + locator + "\", \"" + value.charAt(i) + "\");\n";
			}
		}else{
			result = result + "selenium.keyDown(\"" + locator + "\", \"" + value + "\");\n";
			result = result + "selenium.keyUp(\"" + locator + "\", \"" + value + "\");\n";
		}
		
        return result;
        
    } else if (comment.comment.match(/^selenium\.pressArrowKey/)) {
    	
		var parameters = comment.comment.substring(comment.comment.indexOf("\"")+1);
		var locator = parameters.substring(0, parameters.indexOf("\""));
		parameters = parameters.substring(parameters.indexOf("\"")+1);
		var value = parameters.substring(parameters.indexOf("\"")+1, parameters.lastIndexOf("\""));
		
    	if(value.toLowerCase() == "left"){
    		value="\\\\37";
    	}else if(value.toLowerCase() == "right"){
    		value="\\\\39";
    	}else if(value.toLowerCase() == "up"){
    		value="\\\\38";
    	}else if(value.toLowerCase() == "down"){
    		value="\\\\40";
    	}
		var result = "selenium.keyDown(\"" + locator + "\", \"" + value + "\");\n";
		result = result + "selenium.keyUp(\"" + locator + "\", \"" + value + "\");\n";
		
		return result;
    }
	
	return comment.comment.replace(/.+/mg, function(str) {
			return "// " + str;
		});
}

this.options = {
	receiver: "selenium",
	packageName: "com.example.tests",
	superClass: "AbstractVaadinTestCase",
    indent:	'tab',
    initialIndents:	'2'
};

options.header =
	"package ${packageName};\n" +
	"\n" +
	"import com.thoughtworks.selenium.*;\n" +
	"import java.util.regex.Pattern;\n" +
	"import com.vaadin.testbench.testcase.*;\n" +
	"import java.io.File;\n" +
	"\n" +
    "public class ${className} extends ${superClass} {\n" + 
    "\tpublic void ${methodName}() throws Exception {\n" +
    "\t\tselenium.windowMaximize();\n" + 
    "\t\tselenium.windowFocus();\n";
// in options.header import ..File is for screenshot (no need for manual import add).
// and .windowMaximize() is so that all screenshots are of the same size.

options.footer =
	"\t}\n" +
	"}\n";

this.configForm = 
	'<description>Variable for Selenium instance</description>' +
	'<textbox id="options_receiver" />'+
	'<description>Test package name </description>' +
	'<textbox id="options_packageName" />';

