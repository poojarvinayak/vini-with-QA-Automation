@echo off
REM Selenium Automation Framework Build Script
REM This script builds the framework JAR and installs it to local Maven repository

echo ========================================
echo Selenium Automation Framework Builder
echo ========================================

REM Set project directory
set PROJECT_DIR=%~dp0
cd /d "%PROJECT_DIR%"

echo Building framework JAR...
echo.

REM Clean and compile
echo Step 1: Cleaning previous builds...
call mvn clean
if %errorlevel% neq 0 (
    echo ERROR: Clean failed!
    pause
    exit /b 1
)

echo.
echo Step 2: Compiling sources...
call mvn compile
if %errorlevel% neq 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo.
echo Step 3: Running tests...
call mvn test
if %errorlevel% neq 0 (
    echo WARNING: Some tests failed. Continuing with build...
)

echo.
echo Step 4: Packaging JAR...
call mvn package -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Packaging failed!
    pause
    exit /b 1
)

echo.
echo Step 5: Installing to local Maven repository...
call mvn install -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Installation failed!
    pause
    exit /b 1
)

echo.
echo ========================================
echo Framework JAR built and installed successfully!
echo ========================================
echo.
echo JAR Location: %USERPROFILE%\.m2\repository\com\automation\framework\selenium-automation-framework\1.0.0\
echo.
echo To use in your project, add this dependency to your pom.xml:
echo.
echo ^<dependency^>
echo     ^<groupId^>com.automation.framework^</groupId^>
echo     ^<artifactId^>selenium-automation-framework^</artifactId^>
echo     ^<version^>1.0.0^</version^>
echo ^</dependency^>
echo.
echo For detailed usage instructions, see FRAMEWORK_USAGE_GUIDE.md
echo ========================================

pause
