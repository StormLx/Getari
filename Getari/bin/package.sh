#!/bin/bash
# $Id$
POM=$(dirname $0)/../pom.xml
VERSION=$(xml_grep --nb_results 1 --text_only version $POM)
INDICATORS_VERSION=$(xml_grep --nb_results 1 --text_only indicators.version $POM)
INDICATORS_SRC=/tmp/Indicators-${INDICATORS_VERSION}-src.tbz2
PDF_EN=/tmp/Getari-${VERSION}-en.pdf
PDF_FR=/tmp/Getari-${VERSION}-fr.pdf
SAMPLES=/tmp/Getari-${VERSION}-samples.zip
SRC=/tmp/Getari-${VERSION}-src.tbz2
JAR=/tmp/Getari-${VERSION}.jar
DEB=/tmp/getari_${VERSION}-1_amd64.deb
RPM=/tmp/getari-${VERSION}-1.x86_64.rpm

if [ "$VERSION" == "" ]; then
	echo "Strange, no GETARI_VERSION found"
	exit 1;
fi
bin/web2site.sh
if [ ! -d /tmp/getari-${VERSION} ]; then
	svn co https://w3.avignon.inrae.fr/svn/Getari/trunk /tmp/getari-${VERSION}
else
	svn update /tmp/getari-${VERSION}
fi
cd /tmp/getari-${VERSION} &&
mvn pre-integration-test &&
bin/web2site.sh &&
cp target/Getari-${VERSION}.jar $JAR &&
cp target/package/getari_${VERSION}-1_amd64.deb $DEB &&
cp target/package/getari-${VERSION}-1.x86_64.rpm $RPM &&
mvn pdf:pdf -DincludeReports=false &&
cp target/pdf/maven-pdf-plugin.pdf $PDF_EN &&
cp target/pdf/fr/maven-pdf-plugin.pdf $PDF_FR &&
zip -r $SAMPLES samples/ &&
rm -fr /tmp/getari-${VERSION}/{bin/tokei,doc,logs,web} &&
mvn clean &&
cd /tmp &&
tar jcvf $SRC --exclude=.svn getari-${VERSION} &&

if [ ! -d indicators-$INDICATORS_VERSION ]; then
    svn co https://w3.avignon.inrae.fr/svn/indicators/tags/$INDICATORS_VERSION indicators-$INDICATORS_VERSION
else
    svn update indicators-$INDICATORS_VERSION
fi
tar jcf ${INDICATORS_SRC} --exclude=.svn indicators-$INDICATORS_VERSION

echo "Fichiers créés :"
ls -lh $PDF_EN $PDF_FR $SAMPLES $SRC $DEB $JAR $RPM $INDICATORS_SRC target/package/*
echo "Lancer bin\package.bat sur un poste Windows"
