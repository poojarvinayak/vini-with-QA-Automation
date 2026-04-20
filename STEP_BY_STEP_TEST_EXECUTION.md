# Step-by-Step Guide: Running Tests in Headed Mode

## Prerequisites Verification

Before running tests, ensure you have:

```bash
# Check Java Installation
java -version
# Expected: Java 17 or higher

# Check Maven Installation  
mvn -version
# Expected: Maven 3.6 or higher

# Check Chrome Browser
# Chrome should be installed on your system
```

---

## Method 1: Running Tests Using Windows Batch Script (EASIEST)

### Step 1: Navigate to Project Directory
```batch
cd C:\Users\User\IdeaProjects\QA_Automation
```

### Step 2: Run the Batch Script
```batch
run-tests.bat
```

### What the Script Does:
1. ✅ Checks for Maven installation
2. ✅ Cleans previous test builds
3. ✅ Compiles framework and test code
4. ✅ Runs SMOKE tests in headed mode
5. ✅ Runs REGRESSION tests in headed mode
6. ✅ Generates Allure report
7. ✅ Saves screenshots and logs

### Expected Output:
```
==========================================
Running DemoBlaze Automation Tests
==========================================

Execution Mode: HEADED (Visual)
Browser: Chrome
Environment: QA

Running SMOKE tests...
[INFO] Scanning for projects...
[INFO] --------< com.automation.framework:QA_Automation >--------
[INFO]
[INFO] --- maven-clean-plugin:3.1.0:clean (default-clean) @ QA_Automation ---
[INFO] --- maven-compiler-plugin:3.11.0:compile (default-compile) @ QA_Automation ---
[INFO] --- maven-surefire-plugin:3.2.5:test (default-test) @ QA_Automation ---
[INFO]
[INFO] T E S T S
[INFO]
[INFO] Running com.demoblaze.tests.LoginTest
[INFO] Starting valid login test
...
```

---

## Method 2: Running Tests Using Maven Directly

### For Smoke Tests Only:
```bash
mvn clean test -Dgroups=smoke
```

### For Regression Tests:
```bash
mvn clean test -Dgroups=regression
```

### For All Tests:
```bash
mvn clean test
```

### For Specific Test Class:
```bash
# Run only LoginTest class
mvn clean test -Dtest=LoginTest

# Run only AddToCartTest class
mvn clean test -Dtest=AddToCartTest
```

### For Specific Test Method:
```bash
# Run only the testValidLogin method
mvn clean test -Dtest=LoginTest#testValidLogin

# Run only the testAddProductToCart method
mvn clean test -Dtest=AddToCartTest#testAddProductToCart
```

---

## Method 3: Running Tests Using Linux/Mac Shell Script

### Step 1: Make Script Executable
```bash
chmod +x run-tests.sh
```

### Step 2: Run the Script
```bash
./run-tests.sh
```

### Step 3: View Results
```bash
# Check test output
tail -f logs/automation.log

# View screenshots
open screenshots/
```

---

## During Test Execution

### What You Should See:

**Chrome Browser Will Open and Display:**

1. ✅ **Login Test Sequence:**
   - DemoBlaze homepage loads
   - Login modal opens
   - Username/password entered automatically
   - Login button clicked
   - User welcome message displays

2. ✅ **Add to Cart Test Sequence:**
   - Product page displays
   - Product details visible
   - Add to cart button clicked
   - Confirmation alert appears
   - Cart page opens
   - Product listed in cart

### Console Output While Running:
```
[INFO] Running com.demoblaze.tests.LoginTest
[INFO] Starting valid login test
[INFO] Navigated to: https://www.demoblaze.com/
[INFO] DemoBlaze home page should be loaded
[INFO] Valid login test completed successfully

[INFO] Running com.demoblaze.tests.AddToCartTest
[INFO] Starting add product to cart test
[INFO] Selected product: Samsung galaxy s6 - $250
[INFO] Add product to cart test completed successfully
```

---

## After Test Execution

### Test Results Location:

| Item | Location |
|------|----------|
| Test Logs | `./logs/automation.log` |
| Screenshots | `./screenshots/` |
| Test Output | `./test-output/` |
| Allure Results | `./allure-results/` |
| Allure Report | `./allure-report/` |

### View Test Reports:

**Option 1: Generate and View Allure Report**
```bash
mvn allure:serve
```
This opens an interactive Allure report in your browser showing:
- ✅ Test execution timeline
- ✅ Test results (passed/failed/skipped)
- ✅ Screenshots for each step
- ✅ Test duration
- ✅ Environment information

**Option 2: Check Test Output Directory**
```bash
# Windows
start test-output/

# Linux/Mac
open test-output/
```

**Option 3: View Screenshots**
```bash
# Windows
start screenshots/

# Linux/Mac
open screenshots/

# All screenshots named with test name and timestamp
# Example: LoginTest_20260420_221500_FAILURE_testValidLogin.png
```

---

## Handling Test Results

### All Tests Passed ✅
```
[INFO] BUILD SUCCESS
[INFO] Total time: 2 minutes 45 seconds
```

**Next Steps:**
- ✅ Review Allure report: `mvn allure:serve`
- ✅ Archive screenshots for records
- ✅ Mark tests as complete

### Some Tests Failed ❌

**Analyze Failure:**

1. Check console output for error messages:
```bash
# Search for FAILURE in logs
grep -i "FAILURE" test-output/
```

2. Review screenshot of failure:
```bash
# Look for screenshots with FAILURE in name
ls screenshots/*FAILURE*
```

3. Check detailed test logs:
```bash
# Last 100 lines of log
tail -100 logs/automation.log
```

4. Review test output:
```bash
# TestNG results XML
cat test-output/testng-results.xml
```

---

## Common Issues & Solutions

### Issue 1: Tests Run Too Fast (Headless-like behavior)
**Solution:** 
- Verify `headless: false` in `application.yaml`
- Check browser window isn't minimized
- Run with explicit window positioning

### Issue 2: Chrome Driver Not Found
**Solution:**
```bash
# WebDriverManager downloads automatically
# If issue persists:
mvn clean install  # Reinstall dependencies
mvn clean test     # Try running again
```

### Issue 3: Element Not Found During Test
**Solution:**
- ✅ Check screenshot in `./screenshots/`
- ✅ Verify DemoBlaze website is accessible
- ✅ Add explicit wait: `uiActions.waitForSeconds(2)`
- ✅ Check if website HTML structure changed

### Issue 4: Tests Hanging
**Solution:**
- ✅ Increase wait times in `application.yaml`
- ✅ Check internet connection
- ✅ Restart Chrome and try again
- ✅ Kill any existing Chrome processes

### Issue 5: Maven Command Not Found
**Solution:**
```bash
# Install Maven
# Windows: https://maven.apache.org/download.cgi
# Linux: sudo apt-get install maven
# Mac: brew install maven

# Verify installation
mvn -version
```

---

## Understanding Test Results

### Test Status Indicators

| Status | Meaning | Action |
|--------|---------|--------|
| ✅ PASSED | Test executed successfully | No action needed |
| ❌ FAILED | Test assertion failed | Review screenshots and logs |
| ⏭️ SKIPPED | Test was skipped | Check skip conditions |
| ⚠️ RETRIED | Test failed then passed | Review failure reason |

### Allure Report Sections

**When you run `mvn allure:serve`:**

1. **Overview Tab**
   - Total tests executed
   - Pass/fail statistics
   - Execution timeline
   - Average duration

2. **Suites Tab**
   - Test groups (Smoke, Regression)
   - Individual test cases
   - Execution order
   - Results for each

3. **Behaviors Tab**
   - Organized by Epic and Feature
   - Test stories
   - Related test cases
   - Feature coverage

4. **Packages Tab**
   - Test packages structure
   - Test organization
   - Results by package

---

## Performance Monitoring

### Test Execution Time Expected:

| Test | Duration (Headed) |
|------|------------------|
| Login Tests (all 5) | ~3-5 minutes |
| Add to Cart Tests (all 6) | ~5-8 minutes |
| Total Suite | ~10-15 minutes |

**Headed mode is slower due to visual rendering.**

### Check Execution Performance:

```bash
# View test duration in logs
grep "Test duration" logs/automation.log

# Or check Allure report in the Duration section
```

---

## Troubleshooting Tips

### Tip 1: Enable Debug Logging
Update `application.yaml`:
```yaml
logging:
  level: DEBUG  # Changed from INFO
```

Then run:
```bash
mvn clean test -Dgroups=smoke 2>&1 | tee test_debug.log
```

### Tip 2: Run Single Test for Quick Verification
```bash
# Test single login test (quick ~30 seconds)
mvn clean test -Dtest=LoginTest#testValidLogin
```

### Tip 3: Keep Chrome Window Visible
- Don't minimize Chrome during test execution
- Don't switch to other applications
- Browser must remain focused for proper interaction

### Tip 4: Check Framework Compilation
```bash
# Only compile without running tests
mvn clean compile
```

### Tip 5: View Real-time Test Execution
```bash
# Run with verbose output
mvn -X clean test -Dgroups=smoke
```

---

## Success Validation Checklist

After running tests, verify:

- ✅ Chrome browser opened during execution
- ✅ Tests logged in successfully
- ✅ Products added to cart successfully
- ✅ Screenshots saved in `./screenshots/` folder
- ✅ Logs saved in `./logs/` folder
- ✅ Test output in `./test-output/` folder
- ✅ Allure results in `./allure-results/` folder
- ✅ Build SUCCESS message displayed
- ✅ All assertions passed

---

## Quick Command Reference

```bash
# Clean and compile
mvn clean compile

# Run smoke tests
mvn clean test -Dgroups=smoke

# Run regression tests  
mvn clean test -Dgroups=regression

# Run all tests
mvn clean test

# Run specific test class
mvn clean test -Dtest=LoginTest

# Run specific test method
mvn clean test -Dtest=LoginTest#testValidLogin

# Generate Allure report
mvn allure:serve

# View logs
tail -f logs/automation.log

# Clean all artifacts
mvn clean
```

---

## Next Steps

1. **First Run:**
   - Use `run-tests.bat` (easiest)
   - Observe Chrome browser
   - Check screenshots

2. **Review Results:**
   - `mvn allure:serve` to view report
   - Check logs for any warnings
   - Review screenshots

3. **Iterate:**
   - Fix any issues found
   - Re-run specific failing tests
   - Continue until all tests pass

4. **Share Results:**
   - Export Allure report
   - Archive screenshots
   - Share with team

---

## Support

If issues persist:

1. Check `FIXES_AND_VALIDATION_SUMMARY.md` for known fixes
2. Review `TEST_EXECUTION_REPORT.md` for expected behavior
3. Check framework documentation: `FRAMEWORK_USAGE_GUIDE.md`
4. Review framework README: `README.md`

---

**Happy Testing! 🚀**

*Framework Status: ✅ Ready for Headed Mode Execution*
*Last Updated: April 20, 2026*

