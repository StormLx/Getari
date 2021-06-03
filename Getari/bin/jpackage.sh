#!/bin/bash
JAVA_HOME=$1
VERSION=$2
BUILD_DIR=./target/build
PACKAGE_DIR=./target/package

if [ "$1" == "" ]; then
	echo "Arg1 must be JAVA_HOME"
	exit 1;
fi
if [ "$2" == "" ]; then
	echo "Arg2 must be VERSION"
	exit 1;
fi

# for jpackages resources, see https://docs.oracle.com/en/java/javase/14/jpackage/override-jpackage-resources.html
# missing: help link, support link, update link
function do_jpackage() {
	if [ "$1" != "deb" ] && [ "$1" != "rpm" ]; then		
		echo "Wrong package type: '$1'";
		exit 1;
	fi
	$JAVA_HOME/bin/jpackage \
	--verbose \
	--type $1 \
	--input $BUILD_DIR \
	--main-jar Getari-$VERSION.jar \
	--dest $PACKAGE_DIR \
	--name Getari \
	--linux-package-name getari \
	--app-version $VERSION \
	--vendor INRAE \
	--copyright "Copyright (C) 2020 INRAE" \
	--description "Generic Evaluation Tool for AgRoclimatic Indicators" \
	--icon ./src/main/resources/fr/inrae/agroclim/getari/images/logo_getari_icon.png \
	--file-associations ./src/main/deploy/package/windows/file-associations.properties \
	--license-file License.txt \
	--linux-menu-group Science \
	--linux-shortcut \
	--linux-deb-maintainer 'getari@inrae.fr' \
	--linux-rpm-license-type GPLv3
	# TODO set license for .deb
	# TODO set icon for .deb
}

# test if fakeroot exists
which fakeroot &> /dev/null
if [ $? == 1 ]; then
	echo "fakeroot is missing!";
	exit 1;
fi

# cleanup
rm -fr $BUILD_DIR
rm -fr PACKAGE_DIR

# copy Getari files (we could also copy documentation and samples)
mkdir -p $BUILD_DIR
cp ./target/Getari-$VERSION.jar $BUILD_DIR

# if dpkg exists
which dpkg &> /dev/null
if [ $? == 0 ]; then
	do_jpackage deb
else
	echo "dpkg is missing, no build for .deb"
fi

# test if rpmbuild exists
which rpmbuild &> /dev/null
if [ $? == 0 ]; then
	do_jpackage rpm
else
	echo "rpmbuild is missing, no build for .rpm"
fi

# delete temporary build directory
rm -fr $BUILD_DIR
