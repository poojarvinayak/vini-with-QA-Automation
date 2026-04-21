package com.vinayakqa.automationframework.pages;

import com.vinayakqa.automationframework.utils.UIActions;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all Page Object Model classes.
 * Provides common functionality and utilities for page interactions.
 */
public abstract class BasePage {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected UIActions uiActions;

    /**
     * Constructor for BasePage.
     * Initializes WebDriver, WebDriverWait, and UIActions.
     *
     * @param driver WebDriver instance
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.uiActions = new UIActions(driver);
        PageFactory.initElements(driver, this);
        logger.debug("Initialized page object: {}", this.getClass().getSimpleName());
    }

    /**
     * Abstract method to verify if the page is loaded correctly.
     * Each page class should implement this method with page-specific verification logic.
     *
     * @return true if page is loaded, false otherwise
     */
    @Step("Verifying page is loaded")
    public abstract boolean isPageLoaded();

    /**
     * Gets the page title.
     *
     * @return Page title
     */
    @Step("Getting page title")
    public String getPageTitle() {
        String title = driver.getTitle();
        logger.debug("Page title: {}", title);
        return title;
    }

    /**
     * Gets the current URL.
     *
     * @return Current URL
     */
    @Step("Getting current URL")
    public String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        logger.debug("Current URL: {}", url);
        return url;
    }

    /**
     * Refreshes the current page.
     */
    @Step("Refreshing page")
    public void refreshPage() {
        driver.navigate().refresh();
        logger.debug("Page refreshed");
    }

    /**
     * Waits for the page to load completely.
     * Override this method in subclasses for page-specific wait conditions.
     */
    @Step("Waiting for page to load")
    public void waitForPageToLoad() {
        // Default implementation - subclasses can override
        try {
            Thread.sleep(1000); // Basic wait - replace with proper wait conditions
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        logger.debug("Page load wait completed");
    }

    /**
     * Clicks on an element and waits for page to load.
     *
     * @param element Element to click
     */
    @Step("Clicking element and waiting for page load")
    protected void clickAndWaitForPageLoad(WebElement element) {
        uiActions.click(element);
        waitForPageToLoad();
    }

    /**
     * Types text into an element and waits for page to load.
     *
     * @param element Element to type into
     * @param text Text to type
     */
    @Step("Typing text and waiting for page load")
    protected void typeAndWaitForPageLoad(WebElement element, String text) {
        uiActions.sendKeys(element, text);
        waitForPageToLoad();
    }

    /**
     * Verifies if an element is displayed.
     *
     * @param element Element to check
     * @return true if displayed, false otherwise
     */
    @Step("Verifying element is displayed")
    public boolean isElementDisplayed(WebElement element) {
        boolean displayed = uiActions.isElementDisplayed(element);
        logger.debug("Element displayed: {}", displayed);
        return displayed;
    }

    /**
     * Verifies if an element is enabled.
     *
     * @param element Element to check
     * @return true if enabled, false otherwise
     */
    @Step("Verifying element is enabled")
    public boolean isElementEnabled(WebElement element) {
        boolean enabled = uiActions.isElementEnabled(element);
        logger.debug("Element enabled: {}", enabled);
        return enabled;
    }

    /**
     * Gets text from an element.
     *
     * @param element Element to get text from
     * @return Element text
     */
    @Step("Getting element text")
    public String getElementText(WebElement element) {
        String text = uiActions.getText(element);
        logger.debug("Element text: {}", text);
        return text;
    }

    /**
     * Gets attribute value from an element.
     *
     * @param element Element to get attribute from
     * @param attributeName Attribute name
     * @return Attribute value
     */
    @Step("Getting element attribute: {attributeName}")
    public String getElementAttribute(WebElement element, String attributeName) {
        String value = uiActions.getAttribute(element, attributeName);
        logger.debug("Element attribute {}: {}", attributeName, value);
        return value;
    }

    /**
     * Scrolls to an element.
     *
     * @param element Element to scroll to
     */
    @Step("Scrolling to element")
    public void scrollToElement(WebElement element) {
        uiActions.scrollToElement(element);
    }

    /**
     * Takes a screenshot of the current page.
     *
     * @param screenshotName Name for the screenshot
     */
    @Step("Taking screenshot: {screenshotName}")
    public void takeScreenshot(String screenshotName) {
        // This would typically use ScreenshotUtils
        logger.debug("Screenshot taken: {}", screenshotName);
    }

    /**
     * Logs page information for debugging.
     */
    @Step("Logging page information")
    public void logPageInfo() {
        logger.info("Page Title: {}", getPageTitle());
        logger.info("Current URL: {}", getCurrentUrl());
        logger.info("Page Loaded: {}", isPageLoaded());
    }

    /**
     * Performs common page validation.
     * Override in subclasses to add page-specific validations.
     */
    @Step("Performing page validation")
    public void validatePage() {
        if (!isPageLoaded()) {
            throw new RuntimeException("Page failed to load properly: " + this.getClass().getSimpleName());
        }
        logger.debug("Page validation passed");
    }
}
