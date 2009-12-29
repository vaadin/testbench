cpvars=lib/selenium-server-1.0.1.jar:lib/selenium-grid-remote-control-standalone-TestBench-@build@.jar

huburl=http://127.0.0.1:4444
environment=linux-firefox3,linux-opera10
port=5555
userextensions=user-extensions.js

java -cp "$cpvars" com.thoughtworks.selenium.grid.remotecontrol.SelfRegisteringRemoteControlLauncher -port $port -hubUrl $huburl -env $environment -userExtensions $userextensions
