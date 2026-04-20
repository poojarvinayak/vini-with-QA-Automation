@echo off
REM Test Runner Script for DemoBlaze Tests in Headed Mode

cls
echo.
echo ==========================================
echo Running DemoBlaze Automation Tests
echo ==========================================
echo.
echo Execution Mode: HEADED (Visual)
echo Browser: Chrome
echo Environment: QA
echo.

REM Navigate to project directory
cd /d "%~dp0"

REM Check if Maven is available
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven and add it to your PATH
    pause
    exit /b 1
)

REM Run the smoke tests first
echo Running SMOKE tests...
echo.
mvn clean test -Dgroups=smoke -Dheadless=false

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ==========================================
    echo SMOKE Tests Completed Successfully!
    echo ==========================================
    echo.
    echo Running REGRESSION tests...
    echo.
    mvn test -Dgroups=regression

    if %ERRORLEVEL% EQU 0 (
        echo.
        echo ==========================================
        echo All Tests Completed Successfully!
        echo ==========================================
    ) else (
        echo.
        echo ==========================================
        echo Some REGRESSION tests failed
        echo ==========================================
    )
) else (
    echo.
    echo ==========================================
    echo SMOKE tests failed
    echo ==========================================
)

echo.
echo Generating Allure Report...
mvn allure:report

echo.
echo ==========================================
echo Test Execution Report Summary
echo ==========================================
echo Screenshots saved in: .\screenshots\
echo Test output saved in: .\test-output\
echo Test logs saved in: .\logs\
echo.
pause
