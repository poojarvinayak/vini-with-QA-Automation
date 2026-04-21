package com.vinayakqa.automationframework.driver;

import com.vinayakqa.automationframework.config.ConfigurationManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * WebDriver Manager for initializing and managing WebDriver instances.
 * Supports Chrome, Firefox, Edge, and Safari browsers.
 * Supports both headed and headless modes.
 */
public class DriverManager {

    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private final ConfigurationManager config = ConfigurationManager.getInstance();

    /**
     * Initializes WebDriver based on configuration.
     */
    public WebDriver initializeDriver() {
        String browserName = config.getString("browser.name", "chrome").toLowerCase();
        boolean headless = config.getBoolean("browser.headless", false);

        logger.info("Initializing {} browser in {} mode", browserName, headless ? "headless" : "headed");

        WebDriver driver = switch (browserName) {
            case "chrome" -> initializeChrome(headless);
            case "firefox" -> initializeFirefox(headless);
            case "edge" -> initializeEdge(headless);
            case "safari" -> initializeSafari();
            default -> throw new IllegalArgumentException("Unsupported browser: " + browserName);
        };

        configureDriver(driver);
        driverThreadLocal.set(driver);
        logger.info("WebDriver initialized successfully");
        return driver;
    }

    /**
     * Initializes Chrome browser.
     */
    private WebDriver initializeChrome(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        if (headless) {
            options.addArguments("--headless=new");
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--start-maximized");

        if (config.getBoolean("browser.chrome.disableNotifications", true)) {
            options.addArguments("--disable-notifications");
        }

        if (config.getBoolean("browser.chrome.disablePopups", true)) {
            options.addArguments("--disable-popup-blocking");
        }

        if (config.getBoolean("browser.chrome.incognito", false)) {
            options.addArguments("--incognito");
        }

        // Download settings
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", config.getString("browser.downloadPath", "./downloads"));
        prefs.put("profile.default_content_settings.popups", 0);
        options.setExperimentalOption("prefs", prefs);

        return new ChromeDriver(options);
    }

    /**
     * Initializes Firefox browser.
     */
    private WebDriver initializeFirefox(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();

        if (headless) {
            options.addArguments("--headless");
        }

        if (config.getBoolean("browser.firefox.privateMode", false)) {
            options.addArguments("-private");
        }

        options.addArguments("--no-sandbox");
        return new FirefoxDriver(options);
    }

    /**
     * Initializes Edge browser.
     */
    private WebDriver initializeEdge(boolean headless) {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();

        if (headless) {
            options.addArguments("--headless");
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        if (config.getBoolean("browser.edge.startMaximized", true)) {
            options.addArguments("--start-maximized");
        }

        return new EdgeDriver(options);
    }

    /**
     * Initializes Safari browser.
     */
    private WebDriver initializeSafari() {
        logger.warn("Safari browser requires manual driver setup. Ensure safaridriver is enabled.");
        return new SafariDriver();
    }

    /**
     * Configures WebDriver with timeouts and other settings.
     */
    private void configureDriver(WebDriver driver) {
        int implicitWait = config.getInt("app.implicitWaitTime", 10);
        int pageLoadTimeout = config.getInt("app.pageLoadTimeout", 30);

        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(implicitWait))
                .pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));

        logger.debug("WebDriver configured with implicit wait: {}s, page load timeout: {}s",
                    implicitWait, pageLoadTimeout);
    }

    /**
     * Gets the current WebDriver instance for this thread.
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new RuntimeException("WebDriver is not initialized. Call initializeDriver() first.");
        }
        return driver;
    }

    /**
     * Quits the WebDriver and removes it from thread local.
     */
    public void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                logger.info("WebDriver closed successfully");
            } catch (Exception e) {
                logger.error("Error closing WebDriver", e);
            } finally {
                driverThreadLocal.remove();
            }
        }
    }

    /**
     * Resets the driver (removes from thread local without quitting).
     */
    public void resetDriver() {
        driverThreadLocal.remove();
    }
}
