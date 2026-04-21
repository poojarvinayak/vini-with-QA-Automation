# Selenium Automation Framework - JAR Distribution

A comprehensive, industry-standard Selenium Web Automation Framework built with Java, TestNG, and supporting data-driven testing with YAML and Excel files. **Designed for JAR distribution** - build once, share with your team, and use across multiple projects.

## Framework Purpose

This framework is designed to be:
- **Built as a JAR** and shared with QA team members
- **Used as a dependency** in multiple automation projects
- **Extended by users** through inheritance (BaseTest, BasePage)
- **Configurable** for different applications and environments

## Distribution Model

```
Framework Team (You)
    ↓ Build JAR
Selenium-Automation-Framework-1.0.0.jar
    ↓ Share with Team
QA Team Members
    ↓ Add JAR dependency
Their Automation Projects
    ↓ Extend BaseTest/BasePage
Their Test Implementations
```

## Quick Start for Framework Users

### 1. Add Framework Dependency

```xml
<dependency>
    <groupId>com.vinayakqa.automationframework</groupId>
    <artifactId>selenium-automation-framework</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Extend Base Classes

```java
// Create your page objects
public class LoginPage extends BasePage {
    // Your page elements and methods
}

// Create your test classes
public class LoginTests extends BaseTest {
    // Your test methods
}
```

### 3. Configure and Run

```bash
mvn clean test
mvn allure:serve  # View reports
```

## Framework Architecture

### Core Components (Included in JAR)

```
com.vinayakqa.automationframework/
├── base/                    # Base classes to extend
│   ├── BaseTest.class       # Extend for test classes
│   └── BasePage.class       # Extend for page objects
├── config/                  # Configuration management
│   └── ConfigurationManager.class
├── driver/                  # WebDriver management
│   └── DriverManager.class
├── listeners/               # TestNG listeners
│   ├── TestListener.class
│   └── RetryAnalyzer.class
└── utils/                   # Utility classes
    ├── UIActions.class      # UI interaction utilities
    ├── ExcelReader.class    # Excel file handling
    ├── YamlReader.class     # YAML file handling
    ├── ScreenshotUtils.class
    └── AllureUtils.class
```

### Your Project Structure

```
your-automation-project/
├── src/main/java/com/vinayakqa/
│   ├── pages/              # Your page objects (extend BasePage)
│   └── utils/              # Your custom utilities
├── src/test/java/com/vinayakqa/tests/
│   └── *Tests.java         # Your test classes (extend BaseTest)
└── src/main/resources/
    └── application.yaml    # Your configuration
```

## Building the Framework JAR

### Prerequisites
- Java 17+
- Maven 3.6+

### Build Commands

**Windows:**
```bash
build-framework.bat
```

**Linux/Mac:**
```bash
chmod +x build-framework.sh
./build-framework.sh
```

### What the Build Does

1. **Cleans** previous builds
2. **Compiles** source code
3. **Runs tests** to ensure quality
4. **Packages** JAR with dependencies
5. **Installs** to local Maven repository

### Output

After building, you'll get:
- `selenium-automation-framework-1.0.0.jar` (main JAR)
- `selenium-automation-framework-1.0.0-sources.jar` (source JAR)
- `selenium-automation-framework-1.0.0-javadoc.jar` (documentation JAR)

## Framework Features

### Included Features

- **Page Object Model (POM)**: Extend `BasePage` for your pages
- **Data-Driven Testing**: YAML and Excel support
- **Cross-Browser Support**: Chrome, Firefox, Edge, Safari
- **Headless Mode**: For CI/CD pipelines
- **Allure Reporting**: Beautiful test reports
- **Screenshot Capture**: Automatic on failures
- **Retry Mechanism**: Configurable retry for failed tests
- **Configuration Management**: YAML-based config
- **Logging**: SLF4J with Log4j2
- **TestNG Integration**: Full TestNG support

### Configuration

Create `application.yaml` in your project:

```yaml
app:
  environment: qa
  baseUrl: "https://your-app.com"
  implicitWaitTime: 10
  explicitWaitTime: 15

browser:
  name: chrome
  headless: false

# ... more configurations
```

### Data-Driven Testing

**YAML Data:**
```java
@Test(dataProvider = "yamlData")
public void testWithYaml(String username, String password) {
    // Test implementation
}
```

**Excel Data:**
```java
@Test(dataProvider = "excelData")
public void testWithExcel(String username, String password) {
    // Test implementation
}
```

## Usage Examples

### Creating Page Objects

```java
public class LoginPage extends BasePage {

    @FindBy(id = "username")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @Override
    public boolean isPageLoaded() {
        return usernameField.isDisplayed();
    }

    public void login(String username, String password) {
        uiActions.sendKeys(usernameField, username);
        uiActions.sendKeys(passwordField, password);
        uiActions.click(loginButton);
    }
}
```

### Creating Test Classes

```java
public class LoginTests extends BaseTest {

    @Test
    public void testValidLogin() {
        navigateTo("https://your-app.com/login");

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login("user@example.com", "password");

        Assert.assertTrue(getPageTitle().contains("Dashboard"));
    }
}
```

## Advanced Usage

### Custom Utilities

```java
package com.vinayakqa.utils;

import com.vinayakqa.automationframework.utils.UIActions;

public class CustomUtils extends UIActions {

    public CustomUtils(WebDriver driver) {
        super(driver);
    }

    public void customAction() {
        // Your custom implementation
    }
}
```

### Custom Listeners

```java
public class CustomListener extends TestListener {
    // Override methods for custom behavior
}
```

## Project Structure

```
QA_Automation_Framework/
├── src/main/java/com/vinayakqa/automationframework/  # Framework source
├── src/test/java/com/vinayakqa/automationframework/  # Sample implementations
├── examples/                              # Example consumer project
│   └── sample-consumer-project/
├── build-framework.bat                    # Windows build script
├── build-framework.sh                     # Linux/Mac build script
├── pom.xml                               # Framework POM
└── README.md
```

## CI/CD Integration

### Jenkins Example

```groovy
pipeline {
    agent any
    stages {
        stage('Build Framework') {
            steps {
                bat 'build-framework.bat'
            }
        }
        stage('Run Tests') {
            steps {
                bat 'mvn clean test -Dheadless=true'
            }
        }
        stage('Generate Reports') {
            steps {
                bat 'mvn allure:serve'
            }
        }
    }
}
```

### GitHub Actions Example

```yaml
name: Build and Test Framework
on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    - name: Build Framework JAR
      run: ./build-framework.sh
    - name: Run Tests
      run: mvn clean test -Dheadless=true
```

## Troubleshooting

### Common Issues

1. **JAR not found in repository**
   - Run build script to install JAR locally
   - Check version numbers match

2. **Configuration not loading**
   - Ensure `application.yaml` is in `src/main/resources`
   - Check YAML syntax

3. **WebDriver issues**
   - Framework handles WebDriver management automatically
   - Check browser configuration

4. **Test failures**
   - Screenshots saved automatically on failures
   - Check `screenshots/` directory

## Contributing to Framework

1. **Framework Code**: Modify classes in `src/main/java`
2. **Sample Code**: Update examples in `src/test/java`
3. **Documentation**: Update guides and READMEs
4. **Build Scripts**: Test on multiple platforms

## Support

- **Framework Issues**: Check this README and guides
- **Usage Questions**: Refer to examples in the project
- **Examples**: See `examples/sample-consumer-project/`

## Version History

- **v1.0.0**: Initial JAR distribution release
  - Complete framework redesign for JAR distribution
  - BaseTest/BasePage architecture
  - Comprehensive documentation
  - Example consumer project
  - Build scripts for multiple platforms

---

## Ready to Use!

Your framework is now ready for JAR distribution. Build it, share with your team, and watch them create amazing automation projects!

**Quick Commands:**
```bash
# Build framework
./build-framework.sh

# Use in your project
# Add dependency to pom.xml, extend BaseTest/BasePage, run tests!
```
