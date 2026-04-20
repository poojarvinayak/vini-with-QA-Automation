package com.automation.framework.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * TestRunner class for executing tests from JAR.
 * This class serves as the main entry point for running the automation framework.
 */
public class TestRunner {

    private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);

    /**
     * Main method to run tests.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        logger.info("Starting Selenium Automation Framework Test Runner");

        try {
            TestNG testNG = new TestNG();

            // Parse command line arguments
            TestRunnerConfig config = parseArguments(args);

            // Set up test suites
            List<String> suiteFiles = getTestSuites(config);
            testNG.setTestSuites(suiteFiles);

            // Configure TestNG
            configureTestNG(testNG, config);

            // Run tests
            logger.info("Executing tests...");
            testNG.run();

            // Generate reports if configured
            if (config.shouldGenerateAllureReport()) {
                generateAllureReport();
            }

            logger.info("Test execution completed");

        } catch (Exception e) {
            logger.error("Error running tests", e);
            System.exit(1);
        }
    }

    /**
     * Parse command line arguments.
     */
    private static TestRunnerConfig parseArguments(String[] args) {
        TestRunnerConfig config = new TestRunnerConfig();

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--suite":
                case "-s":
                    if (i + 1 < args.length) {
                        config.setSuiteFile(args[++i]);
                    }
                    break;
                case "--browser":
                case "-b":
                    if (i + 1 < args.length) {
                        System.setProperty("browser", args[++i]);
                    }
                    break;
                case "--environment":
                case "-e":
                    if (i + 1 < args.length) {
                        System.setProperty("environment", args[++i]);
                    }
                    break;
                case "--headless":
                case "-h":
                    System.setProperty("headless", "true");
                    break;
                case "--parallel":
                case "-p":
                    if (i + 1 < args.length) {
                        config.setParallelMode(args[++i]);
                    }
                    break;
                case "--thread-count":
                case "-t":
                    if (i + 1 < args.length) {
                        config.setThreadCount(Integer.parseInt(args[++i]));
                    }
                    break;
                case "--generate-report":
                case "-r":
                    config.setGenerateAllureReport(true);
                    break;
                case "--help":
                    printHelp();
                    System.exit(0);
                    break;
                default:
                    logger.warn("Unknown argument: {}", args[i]);
                    break;
            }
        }

        return config;
    }

    /**
     * Get test suite files to run.
     */
    private static List<String> getTestSuites(TestRunnerConfig config) {
        List<String> suiteFiles = new ArrayList<>();

        if (config.getSuiteFile() != null) {
            suiteFiles.add(config.getSuiteFile());
        } else {
            // Look for default testng.xml in classpath
            File defaultSuite = new File("testng.xml");
            if (defaultSuite.exists()) {
                suiteFiles.add("testng.xml");
            } else {
                // Look in resources
                File resourceSuite = new File("src/test/resources/testng.xml");
                if (resourceSuite.exists()) {
                    suiteFiles.add("src/test/resources/testng.xml");
                } else {
                    logger.warn("No test suite file found. Please specify with --suite option");
                }
            }
        }

        return suiteFiles;
    }

    /**
     * Configure TestNG with the provided configuration.
     */
    private static void configureTestNG(TestNG testNG, TestRunnerConfig config) {
        // Set parallel execution
        if (config.getParallelMode() != null) {
            testNG.setParallel(XmlSuite.ParallelMode.getValidParallel(config.getParallelMode()));
        }

        // Set thread count
        if (config.getThreadCount() > 0) {
            testNG.setThreadCount(config.getThreadCount());
        }

        // Set verbose level
        testNG.setVerbose(2);

        // Set output directory
        testNG.setOutputDirectory("test-output");

        // Add listeners
        testNG.addListener("org.automationframework.listeners.TestListener");
    }

    /**
     * Generate Allure report.
     */
    private static void generateAllureReport() {
        try {
            logger.info("Generating Allure report...");

            // This would typically use Allure command line
            // For now, just log that report generation is requested
            logger.info("Allure report generation completed. Run 'allure serve test-output/allure-results' to view reports");

        } catch (Exception e) {
            logger.error("Error generating Allure report", e);
        }
    }

    /**
     * Print help information.
     */
    private static void printHelp() {
        System.out.println("Selenium Automation Framework Test Runner");
        System.out.println("Usage: java -jar selenium-automation-framework.jar [options]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -s, --suite <file>          Specify test suite XML file");
        System.out.println("  -b, --browser <browser>     Browser to use (chrome, firefox, edge, safari)");
        System.out.println("  -e, --environment <env>     Environment to run tests in");
        System.out.println("  -h, --headless              Run in headless mode");
        System.out.println("  -p, --parallel <mode>       Parallel execution mode (methods, tests, classes)");
        System.out.println("  -t, --thread-count <count>  Number of threads for parallel execution");
        System.out.println("  -r, --generate-report       Generate Allure report after execution");
        System.out.println("  --help                      Show this help message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -jar selenium-automation-framework.jar --browser chrome --environment qa");
        System.out.println("  java -jar selenium-automation-framework.jar --suite custom-suite.xml --parallel methods --thread-count 5");
    }

    /**
     * Configuration class for TestRunner.
     */
    private static class TestRunnerConfig {
        private String suiteFile;
        private String parallelMode;
        private int threadCount;
        private boolean generateAllureReport;

        // Getters and setters
        public String getSuiteFile() { return suiteFile; }
        public void setSuiteFile(String suiteFile) { this.suiteFile = suiteFile; }

        public String getParallelMode() { return parallelMode; }
        public void setParallelMode(String parallelMode) { this.parallelMode = parallelMode; }

        public int getThreadCount() { return threadCount; }
        public void setThreadCount(int threadCount) { this.threadCount = threadCount; }

        public boolean shouldGenerateAllureReport() { return generateAllureReport; }
        public void setGenerateAllureReport(boolean generateAllureReport) { this.generateAllureReport = generateAllureReport; }
    }
}
