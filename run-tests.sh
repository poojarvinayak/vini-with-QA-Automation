#!/bin/bash
# Test Runner Script for DemoBlaze Tests

echo "=========================================="
echo "Running DemoBlaze Automation Tests"
echo "=========================================="
echo ""
echo "Execution Mode: HEADED (Visual)"
echo "Browser: Chrome"
echo "Environment: QA"
echo ""

# Navigate to project directory
cd "$(dirname "$0")"

# Run the smoke tests first
echo "Running SMOKE tests..."
mvn clean test -Dgroups=smoke -Dheadless=false

echo ""
echo "=========================================="
echo "Test Execution Completed"
echo "=========================================="
echo ""
echo "Generating Allure Report..."
mvn allure:report

echo ""
echo "Test results are ready!"
echo "Screenshots saved in: ./screenshots/"
echo "Test output saved in: ./test-output/"
