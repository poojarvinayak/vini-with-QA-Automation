# Your Company Automation Tests

This is an example project demonstrating how to use the **Selenium Automation Framework** JAR in your automation testing projects.

## Project Structure

```
your-company-automation-tests/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/yourcompany/
│   │   │       └── pages/           # Your page objects
│   │   │           ├── LoginPage.java
│   │   │           ├── DashboardPage.java
│   │   │           └── UserProfilePage.java
│   │   └── resources/
│   │       └── application.yaml     # Your configuration
│   └── test/
│       ├── java/
│       │   └── com/yourcompany/tests/
│       │       └── LoginTests.java  # Your test classes
│       └── resources/
│           ├── testng.xml          # TestNG suite configuration
│           └── testdata/           # Your test data
│               └── yaml/
├── pom.xml                        # Project dependencies
└── README.md
```

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- The Selenium Automation Framework JAR (built and installed)

## Setup Instructions

### 1. Build the Framework JAR

First, build and install the framework JAR to your local Maven repository:

**Windows:**
```bash
# From the framework project directory
build-framework.bat
```

**Linux/Mac:**
```bash
# From the framework project directory
chmod +x build-framework.sh
./build-framework.sh
```

### 2. Configure Your Project

1. **Update `pom.xml`:**
   - The framework dependency is already included
   - Update the framework version if needed

2. **Update `application.yaml`:**
   - Set your application URL
   - Configure browser settings
   - Set environment-specific configurations

3. **Update `testng.xml`:**
   - Add your test classes
   - Configure test groups and parallel execution

### 3. Create Your Page Objects

Extend `BasePage` from the framework to create your page objects:

```java
public class LoginPage extends BasePage {
    // Your page elements and methods
}
```

### 4. Create Your Test Classes

Extend `BaseTest` from the framework to create your test classes:

```java
public class LoginTests extends BaseTest {
    // Your test methods
}
```

## Running Tests

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test Suite
```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng.xml
```

### Run with Different Browser
```bash
mvn clean test -Dbrowser=firefox
```

### Run in Headless Mode
```bash
mvn clean test -Dheadless=true
```

### Generate Allure Report
```bash
mvn allure:serve
```

## Framework Features Used

### Configuration Management
```java
ConfigurationManager config = ConfigurationManager.getInstance();
String baseUrl = config.getString("app.baseUrl");
```

### UI Actions
```java
UIActions uiActions = new UIActions(driver);
uiActions.click(element);
uiActions.sendKeys(element, "text");
```

### Data Reading
```java
// YAML data
List<Map<String, Object>> data = YamlReader.readYaml("path/to/data.yaml");

// Excel data
List<Map<String, String>> data = ExcelReader.readExcel("path/to/data.xlsx", "Sheet1");
```

### Screenshot Capture
```java
// Automatic screenshots on failure (configured in BaseTest)
```

### Allure Reporting
```java
@Step("Step description")
public void myMethod() {
    // Method implementation
}
```

## Best Practices

1. **Page Object Model:** Keep page objects focused on single pages
2. **Test Data:** Use YAML/Excel files for test data management
3. **Configuration:** Use environment-specific configurations
4. **Reporting:** Leverage Allure annotations for better reports
5. **Retry Logic:** Use the built-in retry analyzer for flaky tests

## Customization

### Adding Custom Utilities

Create your own utility classes in the `utils` package:

```java
package com.yourcompany.utils;

public class CustomUtils {
    // Your custom utility methods
}
```

### Custom Test Listeners

Extend the framework's listeners for custom behavior:

```java
public class CustomTestListener extends TestListener {
    // Your custom listener methods
}
```

### Environment-Specific Configurations

Create multiple `application-{env}.yaml` files:
- `application-local.yaml`
- `application-qa.yaml`
- `application-prod.yaml`

## Troubleshooting

### Common Issues

1. **Framework JAR not found:**
   - Ensure the framework is built and installed to local Maven repository
   - Check the framework version in your `pom.xml`

2. **Configuration not loading:**
   - Verify `application.yaml` is in `src/main/resources`
   - Check YAML syntax

3. **WebDriver issues:**
   - Ensure browser drivers are available
   - Check browser configuration in `application.yaml`

4. **Test failures:**
   - Check screenshots in the `screenshots` directory
   - Review test logs

### Getting Help

1. Check the framework documentation
2. Review the example implementations
3. Contact the framework development team

## Contributing

1. Follow the established patterns
2. Add appropriate documentation
3. Include test cases for new features
4. Update this README for significant changes

---

**Happy Testing! 🚀**
