#!/bin/bash

cd `dirname $0`/..
PROJECTROOT=`pwd`
NEWVERSION=$1
if [ -z "$NEWVERSION" ]
then
    echo "Usage: $0 <new selenium version>"
    exit 1
fi

NEWMINOR=`echo $NEWVERSION|cut -d. -f 1-2`
OLDVERSION=`grep '<selenium.version>' vaadin-testbench-core/pom.xml|cut -d">" -f 2|cut -d"<" -f 1`

# Remove old
git rm -r -f vaadin-testbench-standalone/lib/
rm -f selenium-server-standalone-$NEWVERSION.jar

# Update files with version number
gsed -i "s/$OLDVERSION/$NEWVERSION/g" vaadin-testbench-core/pom.xml vaadin-testbench-standalone/pom.xml doc/license.html

# Download standalone jar
wget https://selenium-release.storage.googleapis.com/$NEWMINOR/selenium-server-standalone-$NEWVERSION.jar
mv selenium-server-standalone-$NEWVERSION.jar selenium-standalone-$NEWVERSION.jar

# Build standalone sources jar
if [ -e selenium ]
then
    pushd selenium
    git reset --hard
    git pull --rebase
else
    git clone https://github.com/SeleniumHQ/selenium.git
    pushd selenium
fi

git checkout selenium-$NEWVERSION
git pull --rebase
pushd java/server/src
jar cvf $PROJECTROOT/selenium-standalone-$NEWVERSION-sources.jar `find org/openqa/selenium -name *.java` `find org/openqa/grid -name *.java`
popd
pushd java/client/src
jar uvf $PROJECTROOT/selenium-standalone-$NEWVERSION-sources.jar `find org/openqa/selenium -name *.java` `find com/thoughtworks -name *.java`
popd
popd

mvn install:install-file -Dfile=selenium-standalone-$NEWVERSION.jar \
       -DgroupId=org.seleniumhq.selenium -DartifactId=selenium-standalone \
       -Dversion=$NEWVERSION -Dpackaging=jar \
       -Dsources=selenium-standalone-$NEWVERSION-sources.jar \
       -DlocalRepositoryPath=$PROJECTROOT/vaadin-testbench-standalone/lib/

git add vaadin-testbench-standalone/lib
git add -u

rm -f selenium-standalone-$NEWVERSION.jar
rm -f selenium-standalone-$NEWVERSION-sources.jar

git commit -m "Upgrade Selenium to $NEWVERSION"
