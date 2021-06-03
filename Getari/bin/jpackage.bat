:: $Id: $
SETLOCAL EnableExtensions EnableDelayedExpansion
SET JAVA_HOME=%1
SET VERSION=%2
SET BUILD_DIR=.\target\build
SET PACKAGE_DIR=.\target\package
SET APP_VERSION=!%VERSION:-SNAPSHOT=!

:: cleanup
RMDIR /S/Q %BUILD_DIR%
RMDIR /S/Q %PACKAGE_DIR%

:: copy Getari files (we could also copy documentation and samples)
MKDIR %BUILD_DIR%
COPY .\target\Getari-%VERSION%.jar %BUILD_DIR%

:: for jpackages resources, see https://docs.oracle.com/en/java/javase/14/jpackage/override-jpackage-resources.html
:: missing: help link, support link, update link

%JAVA_HOME%\bin\jpackage ^
--verbose ^
--type msi ^
--input %BUILD_DIR% ^
--main-jar Getari-%VERSION%.jar ^
--dest %PACKAGE_DIR% ^
--name Getari ^
--app-version %APP_VERSION% ^
--vendor INRAE ^
--copyright "Copyright (C) 2020 INRAE" ^
--description "Generic Evaluation Tool for AgRoclimatic Indicators" ^
--resource-dir ^
./src/main/deploy/package/windows ^
--icon ^
./src/main/deploy/package/windows/Getari.ico ^
--file-associations ^
./src/main/deploy/package/windows/file-associations.properties ^
--license-file ^
./src/main/deploy/package/windows/gpl-3.0.txt ^
--win-per-user-install ^
--win-console ^
--win-dir-chooser ^
--win-menu ^
--win-shortcut

:: delete temporary build directory
RMDIR /S/Q %BUILD_DIR%