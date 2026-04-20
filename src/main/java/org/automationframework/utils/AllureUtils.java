package org.automationframework.utils;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;

/**
 * Utility class for Allure reporting enhancements.
 * Provides methods to attach screenshots, logs, and other artifacts to Allure reports.
 */
public class AllureUtils {

    private static final Logger logger = LoggerFactory.getLogger(AllureUtils.class);

    /**
     * Attaches a screenshot to the Allure report.
     *
     * @param driver WebDriver instance
     * @param name Name for the attachment
     */
    @Attachment(value = "{name}", type = "image/png")
    public static byte[] attachScreenshot(WebDriver driver, String name) {
        try {
            byte[] screenshot = ScreenshotUtils.captureScreenshotAsBytes(driver);
            logger.debug("Attached screenshot to Allure report: {}", name);
            return screenshot;
        } catch (Exception e) {
            logger.error("Failed to attach screenshot to Allure report", e);
            return new byte[0];
        }
    }

    /**
     * Attaches a screenshot with custom description.
     *
     * @param driver WebDriver instance
     * @param name Name for the attachment
     * @param description Description for the attachment
     */
    public static void attachScreenshotWithDescription(WebDriver driver, String name, String description) {
        try {
            byte[] screenshot = ScreenshotUtils.captureScreenshotAsBytes(driver);
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), ".png");
            Allure.description(description);
            logger.debug("Attached screenshot with description to Allure report: {}", name);
        } catch (Exception e) {
            logger.error("Failed to attach screenshot with description to Allure report", e);
        }
    }

    /**
     * Attaches text content to the Allure report.
     *
     * @param name Name for the attachment
     * @param content Text content to attach
     */
    @Attachment(value = "{name}", type = "text/plain")
    public static String attachText(String name, String content) {
        logger.debug("Attached text to Allure report: {}", name);
        return content;
    }

    /**
     * Attaches JSON content to the Allure report.
     *
     * @param name Name for the attachment
     * @param jsonContent JSON content to attach
     */
    @Attachment(value = "{name}", type = "application/json")
    public static String attachJson(String name, String jsonContent) {
        logger.debug("Attached JSON to Allure report: {}", name);
        return jsonContent;
    }

    /**
     * Attaches HTML content to the Allure report.
     *
     * @param name Name for the attachment
     * @param htmlContent HTML content to attach
     */
    @Attachment(value = "{name}", type = "text/html")
    public static String attachHtml(String name, String htmlContent) {
        logger.debug("Attached HTML to Allure report: {}", name);
        return htmlContent;
    }

    /**
     * Attaches a file to the Allure report.
     *
     * @param filePath Path to the file to attach
     * @param name Name for the attachment
     */
    public static void attachFile(String filePath, String name) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Allure.addAttachment(name, Files.newInputStream(file.toPath()));
                logger.debug("Attached file to Allure report: {}", name);
            } else {
                logger.warn("File not found for attachment: {}", filePath);
            }
        } catch (Exception e) {
            logger.error("Failed to attach file to Allure report: {}", filePath, e);
        }
    }

    /**
     * Adds a step to the Allure report.
     *
     * @param stepName Name of the step
     * @param description Description of the step
     */
    public static void addStep(String stepName, String description) {
        Allure.step(stepName, () -> {
            Allure.description(description);
            logger.debug("Added step to Allure report: {}", stepName);
        });
    }

    /**
     * Adds test parameters to the Allure report.
     *
     * @param parameters Map of parameter names and values
     */
    public static void addParameters(java.util.Map<String, String> parameters) {
        parameters.forEach((key, value) -> {
            Allure.parameter(key, value);
            logger.debug("Added parameter to Allure report: {} = {}", key, value);
        });
    }

    /**
     * Adds environment information to the Allure report.
     *
     * @param key Environment key
     * @param value Environment value
     */
    public static void addEnvironmentInfo(String key, String value) {
        Allure.addAttachment(key, value);
        logger.debug("Added environment info to Allure report: {} = {}", key, value);
    }

    /**
     * Adds a link to the Allure report.
     *
     * @param name Name of the link
     * @param url URL to link to
     */
    public static void addLink(String name, String url) {
        Allure.link(name, url);
        logger.debug("Added link to Allure report: {} -> {}", name, url);
    }

    /**
     * Adds an issue link to the Allure report.
     *
     * @param issueId Issue ID
     * @param issueUrl URL to the issue
     */
    public static void addIssue(String issueId, String issueUrl) {
        Allure.issue(issueId, issueUrl);
        logger.debug("Added issue to Allure report: {}", issueId);
    }

    /**
     * Adds a test management system link to the Allure report.
     *
     * @param tmsId Test management system ID
     * @param tmsUrl URL to the test case
     */
    public static void addTmsLink(String tmsId, String tmsUrl) {
        Allure.tms(tmsId, tmsUrl);
        logger.debug("Added TMS link to Allure report: {}", tmsId);
    }

    /**
     * Attaches browser console logs to the Allure report.
     *
     * @param driver WebDriver instance
     */
    public static void attachBrowserLogs(WebDriver driver) {
        try {
            // Note: This is a simplified version. In real implementation,
            // you might need to use browser-specific logging capabilities
            String logs = "Browser logs would be captured here";
            attachText("Browser Console Logs", logs);
            logger.debug("Attached browser logs to Allure report");
        } catch (Exception e) {
            logger.error("Failed to attach browser logs to Allure report", e);
        }
    }

    /**
     * Attaches page source to the Allure report.
     *
     * @param driver WebDriver instance
     */
    public static void attachPageSource(WebDriver driver) {
        try {
            String pageSource = driver.getPageSource();
            attachHtml("Page Source", pageSource);
            logger.debug("Attached page source to Allure report");
        } catch (Exception e) {
            logger.error("Failed to attach page source to Allure report", e);
        }
    }

    /**
     * Creates a comprehensive failure attachment including screenshot, page source, and logs.
     *
     * @param driver WebDriver instance
     * @param testName Name of the test
     */
    public static void attachFailureArtifacts(WebDriver driver, String testName) {
        try {
            attachScreenshot(driver, "Failure Screenshot - " + testName);
            attachPageSource(driver);
            attachBrowserLogs(driver);
            logger.info("Attached comprehensive failure artifacts for test: {}", testName);
        } catch (Exception e) {
            logger.error("Failed to attach failure artifacts for test: {}", testName, e);
        }
    }
}

