package org.automationframework.base;

import io.qameta.allure.Step;
import org.automationframework.config.ConfigurationManager;
import org.automationframework.driver.DriverManager;
import org.automationframework.utils.AllureUtils;
import org.automationframework.utils.ScreenshotUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;

/**
 * Base test class that provides common functionality for all test classes.
 * Handles WebDriver initialization, cleanup, and common test setup/teardown.
 */
public class BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected WebDriver driver;
    protected DriverManager driverManager;
    protected ConfigurationManager config;

    /**
     * Setup method that runs before each test method.
     * Initializes WebDriver and navigates to base URL.
     */
    @BeforeMethod(alwaysRun = true)
    @Step("Setting up test environment")
    public void setUp() {
        try {
            config = ConfigurationManager.getInstance();
            driverManager = new DriverManager();
            driver = driverManager.initializeDriver();

            String baseUrl = config.getString("app.baseUrl", "https://www.example.com");
            driver.get(baseUrl);

            logger.info("Test setup completed. Base URL: {}", baseUrl);
            AllureUtils.addEnvironmentInfo("Browser", config.getString("browser.name", "chrome"));
            AllureUtils.addEnvironmentInfo("Environment", config.getString("app.environment", "qa"));
            AllureUtils.addEnvironmentInfo("Base URL", baseUrl);

        } catch (Exception e) {
            logger.error("Failed to setup test environment", e);
            throw new RuntimeException("Test setup failed", e);
        }
    }

    /**
     * Teardown method that runs after each test method.
     * Captures screenshots on failure and cleans up WebDriver.
     */
    @AfterMethod(alwaysRun = true)
    @Step("Cleaning up test environment")
    public void tearDown(ITestResult result) {
        try {
            String testName = result.getMethod().getMethodName();
            String className = result.getTestClass().getName();

            if (result.getStatus() == ITestResult.FAILURE) {
                logger.error("Test failed: {}.{}", className, testName);

                // Capture failure screenshot
                if (config.getBoolean("screenshots.onFailure", true)) {
                    String screenshotPath = ScreenshotUtils.captureFailureScreenshot(driver, className, testName);
                    if (screenshotPath != null) {
                        AllureUtils.attachScreenshot(driver, "Failure Screenshot");
                        logger.info("Failure screenshot captured: {}", screenshotPath);
                    }
                }

                // Attach failure artifacts to Allure
                AllureUtils.attachFailureArtifacts(driver, testName);

                // Log failure details
                Throwable throwable = result.getThrowable();
                if (throwable != null) {
                    AllureUtils.attachText("Failure Details", throwable.getMessage());
                }

            } else if (result.getStatus() == ITestResult.SUCCESS) {
                logger.info("Test passed: {}.{}", className, testName);

                // Capture success screenshot if enabled
                if (config.getBoolean("screenshots.onSuccess", false)) {
                    String screenshotPath = ScreenshotUtils.captureSuccessScreenshot(driver, className, testName);
                    if (screenshotPath != null) {
                        AllureUtils.attachScreenshot(driver, "Success Screenshot");
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Error during test teardown", e);
        } finally {
            // Always cleanup driver
            if (driverManager != null) {
                driverManager.quitDriver();
                logger.info("WebDriver cleanup completed");
            }
        }
    }

    /**
     * Setup method that runs before the test class.
     * Can be used for class-level setup.
     */
    @BeforeClass(alwaysRun = true)
    @Step("Setting up test class")
    public void setUpClass() {
        logger.info("Setting up test class: {}", this.getClass().getSimpleName());
        // Add any class-level setup here
    }

    /**
     * Teardown method that runs after the test class.
     * Can be used for class-level cleanup.
     */
    @AfterClass(alwaysRun = true)
    @Step("Cleaning up test class")
    public void tearDownClass() {
        logger.info("Cleaning up test class: {}", this.getClass().getSimpleName());
        // Add any class-level cleanup here
    }

    /**
     * Setup method that runs before the test suite.
     * Can be used for suite-level setup.
     */
    @BeforeSuite(alwaysRun = true)
    @Step("Setting up test suite")
    public void setUpSuite() {
        logger.info("Setting up test suite");

        // Clean up old screenshots
        ScreenshotUtils.cleanupOldScreenshots(7); // Keep screenshots for 7 days

        // Add suite-level environment info
        AllureUtils.addEnvironmentInfo("Test Framework", "Selenium + TestNG");
        AllureUtils.addEnvironmentInfo("Java Version", System.getProperty("java.version"));
        AllureUtils.addEnvironmentInfo("OS", System.getProperty("os.name"));
    }

    /**
     * Teardown method that runs after the test suite.
     * Can be used for suite-level cleanup.
     */
    @AfterSuite(alwaysRun = true)
    @Step("Cleaning up test suite")
    public void tearDownSuite() {
        logger.info("Cleaning up test suite");
        // Add any suite-level cleanup here
    }

    /**
     * Gets the WebDriver instance.
     * Use this method in test classes instead of accessing driver directly.
     */
    protected WebDriver getDriver() {
        return driver;
    }

    /**
     * Gets the ConfigurationManager instance.
     */
    protected ConfigurationManager getConfig() {
        return config;
    }

    /**
     * Navigates to a specific URL.
     */
    @Step("Navigating to URL: {url}")
    protected void navigateTo(String url) {
        driver.get(url);
        logger.info("Navigated to: {}", url);
    }

    /**
     * Gets the current page title.
     */
    @Step("Getting page title")
    protected String getPageTitle() {
        String title = driver.getTitle();
        logger.debug("Page title: {}", title);
        return title;
    }

    /**
     * Gets the current page URL.
     */
    @Step("Getting current URL")
    protected String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        logger.debug("Current URL: {}", url);
        return url;
    }

    /**
     * Refreshes the current page.
     */
    @Step("Refreshing page")
    protected void refreshPage() {
        driver.navigate().refresh();
        logger.debug("Page refreshed");
    }

    /**
     * Navigates back in browser history.
     */
    @Step("Navigating back")
    protected void navigateBack() {
        driver.navigate().back();
        logger.debug("Navigated back");
    }

    /**
     * Navigates forward in browser history.
     */
    @Step("Navigating forward")
    protected void navigateForward() {
        driver.navigate().forward();
        logger.debug("Navigated forward");
    }

    /**
     * Waits for a specified number of seconds.
     * Use sparingly - prefer explicit waits.
     */
    protected void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
            logger.debug("Waited for {} seconds", seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Wait interrupted", e);
        }
    }
}

