# Example setup for load testing Spring Boot app with Playwright tests


In the actual web app project directory (../demo-web-app-playwright), execute `mvn install`.

Then here the most trivial test is to run the web app locally and execute against it:

    mvn verify

Check pom.xml for example show to configure running against another server (more realistic approach that you really should do to get meaningful results).

