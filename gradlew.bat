@echo off
setlocal

set "ROOT_DIR=%~dp0"
set "JAVA_HOME=C:\Program Files\Microsoft\jdk-21.0.12.101-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

pushd "%ROOT_DIR%backend"
call "%ROOT_DIR%backend\gradlew.bat" %*
popd
