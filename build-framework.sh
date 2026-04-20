#!/bin/bash

# Selenium Automation Framework Build Script
# This script builds the framework JAR and installs it to local Maven repository

echo "========================================"
echo "Selenium Automation Framework Builder"
echo "========================================"

# Set project directory
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

echo "Building framework JAR..."
echo

# Clean and compile
echo "Step 1: Cleaning previous builds..."
mvn clean
if [ $? -ne 0 ]; then
    echo "ERROR: Clean failed!"
    exit 1
fi

echo
echo "Step 2: Compiling sources..."
mvn compile
if [ $? -ne 0 ]; then
    echo "ERROR: Compilation failed!"
    exit 1
fi

echo
echo "Step 3: Running tests..."
mvn test
if [ $? -ne 0 ]; then
    echo "WARNING: Some tests failed. Continuing with build..."
fi

echo
echo "Step 4: Packaging JAR..."
mvn package -DskipTests
if [ $? -ne 0 ]; then
    echo "ERROR: Packaging failed!"
    exit 1
fi

echo
echo "Step 5: Installing to local Maven repository..."
mvn install -DskipTests
if [ $? -ne 0 ]; then
    echo "ERROR: Installation failed!"
    exit 1
fi

echo
echo "========================================"
echo "Framework JAR built and installed successfully!"
echo "========================================"
echo
echo "JAR Location: ~/.m2/repository/com/automation/framework/selenium-automation-framework/1.0.0/"
echo
echo "To use in your project, add this dependency to your pom.xml:"
echo
echo "<dependency>"
echo "    <groupId>com.automation.framework</groupId>"
echo "    <artifactId>selenium-automation-framework</artifactId>"
echo "    <version>1.0.0</version>"
echo "</dependency>"
echo
echo "For detailed usage instructions, see FRAMEWORK_USAGE_GUIDE.md"
echo "========================================"
