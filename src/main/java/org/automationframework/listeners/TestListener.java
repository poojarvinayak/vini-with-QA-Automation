package org.automationframework.listeners;

import io.qameta.allure.Allure;
import org.automationframework.utils.AllureUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * TestNG listener for comprehensive test reporting and management.
 * Provides detailed logging, retry logic, and Allure report enhancements.
 */
public class TestListener implements ITestListener, ISuiteListener, IInvokedMethodListener {

    private static final Logger logger = LoggerFactory.getLogger(TestListener.class);
    private static final AtomicInteger testCount = new AtomicInteger(0);
    private static final AtomicInteger passedCount = new AtomicInteger(0);
    private static final AtomicInteger failedCount = new AtomicInteger(0);
    private static final AtomicInteger skippedCount = new AtomicInteger(0);

    private long suiteStartTime;
    private long testStartTime;

    // ==================== Suite Level Listeners ====================

    @Override
    public void onStart(ISuite suite) {
        suiteStartTime = System.currentTimeMillis();
        logger.info("=========================================");
        logger.info("Starting Test Suite: {}", suite.getName());
        logger.info("=========================================");

        AllureUtils.addEnvironmentInfo("Suite Name", suite.getName());
        AllureUtils.addEnvironmentInfo("Suite Start Time", String.valueOf(suiteStartTime));
    }

    @Override
    public void onFinish(ISuite suite) {
        long suiteEndTime = System.currentTimeMillis();
        long suiteDuration = suiteEndTime - suiteStartTime;

        logger.info("=========================================");
        logger.info("Test Suite Completed: {}", suite.getName());
        logger.info("Total Tests: {}", testCount.get());
        logger.info("Passed: {}", passedCount.get());
        logger.info("Failed: {}", failedCount.get());
        logger.info("Skipped: {}", skippedCount.get());
        logger.info("Suite Duration: {} ms", suiteDuration);
        logger.info("=========================================");

        AllureUtils.addEnvironmentInfo("Suite End Time", String.valueOf(suiteEndTime));
        AllureUtils.addEnvironmentInfo("Suite Duration (ms)", String.valueOf(suiteDuration));
        AllureUtils.addEnvironmentInfo("Total Tests", String.valueOf(testCount.get()));
        AllureUtils.addEnvironmentInfo("Passed Tests", String.valueOf(passedCount.get()));
        AllureUtils.addEnvironmentInfo("Failed Tests", String.valueOf(failedCount.get()));
        AllureUtils.addEnvironmentInfo("Skipped Tests", String.valueOf(skippedCount.get()));
    }

    // ==================== Test Level Listeners ====================

    @Override
    public void onTestStart(ITestResult result) {
        testStartTime = System.currentTimeMillis();
        testCount.incrementAndGet();

        String testName = getTestName(result);
        logger.info("Starting Test: {}", testName);

        // Add test information to Allure
        AllureUtils.addParameters(getTestParameters(result));
        Allure.description("Test started at: " + testStartTime);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        passedCount.incrementAndGet();
        long testDuration = System.currentTimeMillis() - testStartTime;

        String testName = getTestName(result);
        logger.info("✓ Test PASSED: {} ({} ms)", testName, testDuration);

        AllureUtils.addStep("Test Execution", "Test completed successfully in " + testDuration + " ms");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        failedCount.incrementAndGet();
        long testDuration = System.currentTimeMillis() - testStartTime;

        String testName = getTestName(result);
        Throwable throwable = result.getThrowable();

        logger.error("✗ Test FAILED: {} ({} ms)", testName, testDuration);
        logger.error("Failure Reason: {}", throwable.getMessage());

        // Add failure details to Allure
        AllureUtils.attachText("Failure Details",
            "Test: " + testName + "\n" +
            "Duration: " + testDuration + " ms\n" +
            "Exception: " + throwable.getClass().getSimpleName() + "\n" +
            "Message: " + throwable.getMessage()
        );

        // Add stack trace
        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            stackTrace.append(element.toString()).append("\n");
        }
        AllureUtils.attachText("Stack Trace", stackTrace.toString());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        skippedCount.incrementAndGet();
        long testDuration = System.currentTimeMillis() - testStartTime;

        String testName = getTestName(result);
        logger.warn("⚠ Test SKIPPED: {} ({} ms)", testName, testDuration);

        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            AllureUtils.attachText("Skip Reason", throwable.getMessage());
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logger.warn("Test failed but within success percentage: {}", getTestName(result));
    }

    // ==================== Method Level Listeners ====================

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            logger.debug("Before method invocation: {}", method.getTestMethod().getMethodName());
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            logger.debug("After method invocation: {}", method.getTestMethod().getMethodName());
        }
    }

    // ==================== Helper Methods ====================

    /**
     * Gets a formatted test name including class and method.
     */
    private String getTestName(ITestResult result) {
        return result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    }

    /**
     * Gets test parameters as a map.
     */
    private java.util.Map<String, String> getTestParameters(ITestResult result) {
        java.util.Map<String, String> parameters = new java.util.HashMap<>();
        Object[] params = result.getParameters();

        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                parameters.put("Parameter " + (i + 1), String.valueOf(params[i]));
            }
        }

        parameters.put("Test Class", result.getTestClass().getName());
        parameters.put("Test Method", result.getMethod().getMethodName());
        parameters.put("Test Start Time", String.valueOf(testStartTime));

        return parameters;
    }
}
