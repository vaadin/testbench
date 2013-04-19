this.seleniumAPI = {};
const subScriptLoader = Components.classes["@mozilla.org/moz/jssubscript-loader;1"].getService(Components.interfaces.mozIJSSubScriptLoader);
subScriptLoader.loadSubScript('chrome://testbench-recorder/content/selenium-core/scripts/selenium-api.js', this.seleniumAPI);
var parser = new DOMParser();
Command.apiDocument = parser.parseFromString(FileUtils.readURL("chrome://testbench-recorder/content/selenium-core/iedoc-core.xml"), "text/xml");

Command.prototype.getAPI = function() {
	return seleniumAPI;
}
