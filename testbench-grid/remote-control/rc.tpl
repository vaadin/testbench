cpvars=lib/selenium-server-1.0.1.jar:lib/selenium-grid-remote-control-standalone-TestBench-@build@.jar

environment=linux-firefox3
userextensions=user-extensions.js

java -cp "$cpvars" com.thoughtworks.selenium.grid.remotecontrol.SelfRegisteringRemoteControlLauncher -env $environment -userExtensions $userextensions
