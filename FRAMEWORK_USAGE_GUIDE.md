# Selenium Automation Framework - JAR Distribution Guide

## Overview

This framework is designed to be distributed as a JAR file that can be used by QA team members in their automation projects. The JAR contains all the core framework components, utilities, and base classes, while allowing users to create their own page objects and test implementations.

## Framework Structure

```
selenium-automation-framework.jar
├── com.automation.framework/
│   ├── base/                    # Base classes to extend
│   │   ├── BaseTest.class       # Extend this for test classes
│   │   └── BasePage.class       # Extend this for page objects
│   ├── config/                  # Configuration management
│   │   └── ConfigurationManager.class
│   ├── driver/                  # WebDriver management
│   │   └── DriverManager.class
│   ├── listeners/               # TestNG listeners
│   │   ├── TestListener.class
│   │   └── RetryAnalyzer.class
│   └── utils/                   # Utility classes
│       ├── UIActions.class      # UI interaction utilities
│       ├── ExcelReader.class    # Excel file handling
│       ├── YamlReader.class     # YAML file handling
│       ├── ScreenshotUtils.class
│       └── AllureUtils.class
├── resources/                   # Default configuration files
│   ├── application.yaml
│   └── log4j2.xml
└── META-INF/MANIFEST.MF         # JAR manifest
```

## How to Use the Framework JAR

### 1. Add Framework Dependency

Add the framework JAR to your project's dependencies:

**Maven:**
```xml
<dependency>
    <groupId>com.automation.framework</groupId>
    <artifactId>selenium-automation-framework</artifactId>
    <version>1.0.0</version>
    <scope>compile</scope>
</dependency>
```

**Gradle:**
```gradle
implementation 'com.automation.framework:selenium-automation-framework:1.0.0'
```

### 2. Project Structure

Create your automation project with this structure:

```
your-automation-project/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/yourcompany/
│   │   │       ├── pages/           # Your page objects
│   │   │       │   ├── LoginPage.java
│   │   │       │   ├── HomePage.java
│   │   │       │   └── ...
│   │   │       └── utils/           # Your custom utilities
│   │   └── resources/
│   │       ├── application.yaml     # Your configuration
│   │       ├── log4j2.xml          # Your logging config
│   │       └── testdata/           # Your test data
│   │           ├── yaml/
│   │           └── excel/
│   └── test/
│       ├── java/
│       │   └── com/yourcompany/tests/
│       │       ├── LoginTests.java
│       │       ├── RegressionTests.java
│       │       └── ...
│       └── resources/
│           ├── testng.xml          # TestNG suite
│           └── testdata/           # Additional test data
├── pom.xml                        # Your project POM
└── README.md
```

### 3. Create Page Objects

Extend `BasePage` to create your page objects:

```java
package com.yourcompany.pages;

import com.automation.framework.base.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

    @FindBy(id = "username")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @Override
    public boolean isPageLoaded() {
        return usernameField.isDisplayed() && passwordField.isDisplayed();
    }

    public void login(String username, String password) {
        uiActions.sendKeys(usernameField, username);
        uiActions.sendKeys(passwordField, password);
        uiActions.click(loginButton);
    }
}
```

### 4. Create Test Classes

Extend `BaseTest` to create your test classes:

```java
package com.yourcompany.tests;

import com.automation.framework.base.BaseTest;
import com.yourcompany.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {

    @Test
    public void testValidLogin() {
        // Navigate to application
        navigateTo("https://your-app.com/login");

        // Use your page object
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login("username", "password");

        // Assertions
        Assert.assertTrue(getPageTitle().contains("Dashboard"));
    }
}
```

### 5. Configuration

Create your `application.yaml` configuration file:

```yaml
app:
  environment: qa
  baseUrl: "https://your-app.com"
  implicitWaitTime: 10
  explicitWaitTime: 15
  pageLoadTimeout: 30

browser:
  name: chrome
  headless: false

# ... other configurations
```

## Framework Features Available

### Configuration Management
```java
ConfigurationManager config = ConfigurationManager.getInstance();
String baseUrl = config.getString("app.baseUrl");
boolean headless = config.getBoolean("browser.headless", false);
```

### UI Actions
```java
UIActions uiActions = new UIActions(driver);
uiActions.click(element);
uiActions.sendKeys(element, "text");
uiActions.selectByVisibleText(dropdown, "option");
uiActions.waitForElementToBeVisible(locator);
```

### Data Reading
```java
// YAML data
List<Map<String, Object>> yamlData = YamlReader.readYaml("testdata/login.yaml");

// Excel data
List<Map<String, String>> excelData = ExcelReader.readExcel("testdata/users.xlsx", "Sheet1");
```

### Screenshot Capture
```java
String screenshotPath = ScreenshotUtils.captureScreenshot(driver, "testName");
```

### Allure Reporting
```java
AllureUtils.attachScreenshot(driver, "Screenshot Name");
AllureUtils.addStep("Step Name", "Step Description");
```

## Running Tests

### Using TestNG
```bash
mvn clean test
```

### Using the Framework JAR Runner
```bash
java -jar selenium-automation-framework.jar --browser chrome --environment qa
```

### Command Line Options
- `--suite, -s`: Specify test suite XML file
- `--browser, -b`: Browser (chrome, firefox, edge, safari)
- `--environment, -e`: Environment
- `--headless, -h`: Run in headless mode
- `--parallel, -p`: Parallel mode (methods, tests, classes)
- `--thread-count, -t`: Number of threads
- `--generate-report, -r`: Generate Allure report

## Best Practices

### 1. Page Object Model
- Keep page objects focused on a single page
- Use meaningful method names
- Return page objects for method chaining
- Implement proper wait conditions

### 2. Test Organization
- Group related tests in classes
- Use descriptive test method names
- Leverage data providers for data-driven tests
- Use appropriate TestNG annotations

### 3. Configuration Management
- Use environment-specific configurations
- Keep sensitive data in secure locations
- Document configuration options

### 4. Reporting
- Use Allure annotations for better reports
- Capture screenshots on failures
- Add meaningful step descriptions

## Example Project

See the `examples/` directory for complete working examples of:
- Page object implementations
- Test class structures
- Configuration files
- Test data formats

## Support

For framework-related issues:
1. Check the framework documentation
2. Review example implementations
3. Contact the framework development team

## Version History

- **v1.0.0**: Initial release with core framework features
  - Page Object Model support
  - Data-driven testing (YAML/Excel)
  - Cross-browser support
  - Allure reporting
  - Screenshot capture
  - Retry mechanism
