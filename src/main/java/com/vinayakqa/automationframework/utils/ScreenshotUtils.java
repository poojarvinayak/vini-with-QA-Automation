package com.vinayakqa.automationframework.utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for capturing screenshots during test execution.
 * Supports full page screenshots and element-specific screenshots.
 */
public class ScreenshotUtils {

    private static final Logger logger = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = "./screenshots";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    static {
        // Create screenshots directory if it doesn't exist
        try {
            Path screenshotPath = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(screenshotPath)) {
                Files.createDirectories(screenshotPath);
                logger.info("Created screenshots directory: {}", SCREENSHOT_DIR);
            }
        } catch (IOException e) {
            logger.error("Failed to create screenshots directory", e);
        }
    }

    /**
     * Captures full page screenshot.
     *
     * @param driver WebDriver instance
     * @param testName Name of the test for screenshot naming
     * @return Path to the captured screenshot
     */
    public static String captureScreenshot(WebDriver driver, String testName) {
        return captureScreenshot(driver, testName, "");
    }

    /**
     * Captures full page screenshot with additional description.
     *
     * @param driver WebDriver instance
     * @param testName Name of the test for screenshot naming
     * @param description Additional description for the screenshot
     * @return Path to the captured screenshot
     */
    public static String captureScreenshot(WebDriver driver, String testName, String description) {
        try {
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            File sourceFile = screenshot.getScreenshotAs(OutputType.FILE);

            String timestamp = LocalDateTime.now().format(formatter);
            String fileName = String.format("%s_%s%s.png",
                testName,
                timestamp,
                description.isEmpty() ? "" : "_" + description.replaceAll("[^a-zA-Z0-9]", "_")
            );

            Path screenshotPath = Paths.get(SCREENSHOT_DIR, fileName);
            FileUtils.copyFile(sourceFile, screenshotPath.toFile());

            String relativePath = screenshotPath.toString();
            logger.info("Screenshot captured: {}", relativePath);

            return relativePath;

        } catch (Exception e) {
            logger.error("Failed to capture screenshot for test: {}", testName, e);
            return null;
        }
    }

    /**
     * Captures screenshot and returns as byte array (useful for Allure reports).
     *
     * @param driver WebDriver instance
     * @return Screenshot as byte array
     */
    public static byte[] captureScreenshotAsBytes(WebDriver driver) {
        try {
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            return screenshot.getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            logger.error("Failed to capture screenshot as bytes", e);
            return new byte[0];
        }
    }

    /**
     * Captures screenshot on test failure.
     *
     * @param driver WebDriver instance
     * @param testName Name of the test
     * @param methodName Name of the failed method
     * @return Path to the captured screenshot
     */
    public static String captureFailureScreenshot(WebDriver driver, String testName, String methodName) {
        return captureScreenshot(driver, testName, "FAILURE_" + methodName);
    }

    /**
     * Captures screenshot on test success (if enabled).
     *
     * @param driver WebDriver instance
     * @param testName Name of the test
     * @param methodName Name of the successful method
     * @return Path to the captured screenshot
     */
    public static String captureSuccessScreenshot(WebDriver driver, String testName, String methodName) {
        return captureScreenshot(driver, testName, "SUCCESS_" + methodName);
    }

    /**
     * Cleans up old screenshots based on retention policy.
     *
     * @param daysToKeep Number of days to keep screenshots
     */
    public static void cleanupOldScreenshots(int daysToKeep) {
        try {
            Path screenshotPath = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(screenshotPath)) {
                return;
            }

            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);

            Files.walk(screenshotPath)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".png"))
                .filter(path -> {
                    try {
                        return Files.getLastModifiedTime(path).toInstant()
                            .isBefore(cutoffDate.toLocalDate().atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
                    } catch (IOException e) {
                        return false;
                    }
                })
                .forEach(path -> {
                    try {
                        Files.delete(path);
                        logger.debug("Deleted old screenshot: {}", path);
                    } catch (IOException e) {
                        logger.warn("Failed to delete old screenshot: {}", path, e);
                    }
                });

            logger.info("Cleaned up screenshots older than {} days", daysToKeep);

        } catch (IOException e) {
            logger.error("Error during screenshot cleanup", e);
        }
    }

    /**
     * Gets the screenshot directory path.
     *
     * @return Screenshot directory path
     */
    public static String getScreenshotDirectory() {
        return SCREENSHOT_DIR;
    }

    /**
     * Creates a subdirectory for screenshots.
     *
     * @param subDirName Name of the subdirectory
     * @return Path to the created subdirectory
     */
    public static String createScreenshotSubdirectory(String subDirName) {
        try {
            Path subDirPath = Paths.get(SCREENSHOT_DIR, subDirName);
            if (!Files.exists(subDirPath)) {
                Files.createDirectories(subDirPath);
                logger.info("Created screenshot subdirectory: {}", subDirPath);
            }
            return subDirPath.toString();
        } catch (IOException e) {
            logger.error("Failed to create screenshot subdirectory: {}", subDirName, e);
            return SCREENSHOT_DIR;
        }
    }
}
