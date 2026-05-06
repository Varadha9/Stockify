@echo off
setlocal

set "GRADLE_USER_HOME=%~dp0.gradle-local"

if not exist "%GRADLE_USER_HOME%" mkdir "%GRADLE_USER_HOME%"

echo Building release APK with isolated Gradle cache...
echo Gradle home: %GRADLE_USER_HOME%
echo.

call gradlew.bat assembleRelease

echo.
if %ERRORLEVEL%==0 (
    echo BUILD SUCCESSFUL
    echo APK: app\build\outputs\apk\release\app-release-unsigned.apk
) else (
    echo BUILD FAILED
)

endlocal
