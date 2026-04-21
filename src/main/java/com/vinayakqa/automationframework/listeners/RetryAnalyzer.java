package com.vinayakqa.automationframework.listeners;

import com.vinayakqa.automationframework.config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retry analyzer for failed tests.
 * Automatically retries failed tests based on configuration.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger logger = LoggerFactory.getLogger(RetryAnalyzer.class);
    private static final ConfigurationManager config = ConfigurationManager.getInstance();

    private int retryCount = 0;
    private final int maxRetryCount = config.getInt("execution.retryCount", 1);

    @Override
    public boolean retry(ITestResult result) {
        boolean shouldRetry = false;

        if (config.getBoolean("execution.retryFailedTests", true)) {
            if (retryCount < maxRetryCount) {
                retryCount++;
                String testName = result.getTestClass().getName() + "." + result.getMethod().getMethodName();

                logger.warn("Retrying test: {} (Attempt {} of {})",
                    testName, retryCount, maxRetryCount + 1);

                // Log retry reason
                Throwable throwable = result.getThrowable();
                if (throwable != null) {
                    logger.warn("Retry reason: {}", throwable.getMessage());
                }

                shouldRetry = true;
            } else {
                logger.error("Test failed after {} retries: {}.{}",
                    maxRetryCount,
                    result.getTestClass().getName(),
                    result.getMethod().getMethodName());
            }
        }

        return shouldRetry;
    }

    /**
     * Gets the current retry count.
     */
    public int getRetryCount() {
        return retryCount;
    }

    /**
     * Gets the maximum retry count.
     */
    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    /**
     * Resets the retry count (useful for test management).
     */
    public void resetRetryCount() {
        retryCount = 0;
    }
}
