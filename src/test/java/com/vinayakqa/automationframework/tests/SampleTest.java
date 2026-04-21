package com.vinayakqa.automationframework.tests;

import io.qameta.allure.*;
import com.vinayakqa.automationframework.base.BaseTest;
import com.vinayakqa.automationframework.listeners.RetryAnalyzer;
import com.vinayakqa.automationframework.utils.ExcelReader;
import com.vinayakqa.automationframework.utils.YamlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * Sample test class demonstrating the framework usage.
 * This class shows how to use data-driven testing with Excel and YAML files.
 */
@Epic("Sample Tests")
@Feature("Framework Demonstration")
public class SampleTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(SampleTest.class);

    /**
     * Sample test using YAML data provider.
     */
    @Test(dataProvider = "yamlLoginData", groups = {"smoke", "regression"},
          retryAnalyzer = RetryAnalyzer.class, description = "Sample login test using YAML data")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test login functionality with different user credentials from YAML file")
    public void testLoginWithYamlData(String testCase, String username, String password,
                                    String expectedResult, String description) {
        logger.info("Executing test case: {} - {}", testCase, description);

        // Navigate to login page
        navigateTo(getConfig().getString("app.baseUrl") + "/login");

        // Verify page title
        Assert.assertTrue(getPageTitle().contains("Login"), "Login page should be displayed");

        // This is a sample test - in real implementation, you would:
        // 1. Locate username and password fields
        // 2. Enter credentials
        // 3. Click login button
        // 4. Verify expected result

        logger.info("Login test completed for user: {}", username);

        // Sample assertion
        if ("success".equals(expectedResult)) {
            Assert.assertTrue(true, "Login should be successful");
        } else {
            Assert.assertTrue(true, "Login should fail as expected");
        }
    }

    /**
     * Sample test using Excel data provider.
     */
    @Test(dataProvider = "excelUserData", groups = {"regression"},
          retryAnalyzer = RetryAnalyzer.class, description = "Sample user registration test using Excel data")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test user registration with data from Excel file")
    public void testUserRegistrationWithExcelData(String firstName, String lastName,
                                                String email, String expectedResult) {
        logger.info("Testing user registration for: {} {}", firstName, lastName);

        // Navigate to registration page
        navigateTo(getConfig().getString("app.baseUrl") + "/register");

        // Sample test logic
        Assert.assertNotNull(firstName, "First name should not be null");
        Assert.assertNotNull(lastName, "Last name should not be null");
        Assert.assertNotNull(email, "Email should not be null");

        logger.info("User registration test completed for: {}", email);
    }

    /**
     * Sample API test (placeholder).
     */
    @Test(groups = {"api", "smoke"}, description = "Sample API test")
    @Severity(SeverityLevel.MINOR)
    @Description("Sample API test to demonstrate framework capabilities")
    public void testApiEndpoint() {
        logger.info("Testing API endpoint");

        // This would typically use REST Assured for API testing
        // For now, it's a placeholder test

        Assert.assertTrue(true, "API test placeholder");
    }

    /**
     * Sample performance test (placeholder).
     */
    @Test(groups = {"performance"}, description = "Sample performance test")
    @Severity(SeverityLevel.MINOR)
    @Description("Sample performance test to check page load times")
    public void testPageLoadPerformance() {
        logger.info("Testing page load performance");

        long startTime = System.currentTimeMillis();
        navigateTo(getConfig().getString("app.baseUrl"));
        long loadTime = System.currentTimeMillis() - startTime;

        logger.info("Page load time: {} ms", loadTime);

        // Assert load time is within acceptable range
        Assert.assertTrue(loadTime < 5000, "Page should load within 5 seconds");
    }

    // ==================== Data Providers ====================

    /**
     * Data provider for YAML login test data.
     */
    @DataProvider(name = "yamlLoginData")
    public Object[][] getYamlLoginData() {
        List<Map<String, Object>> testData = YamlReader.readYaml("src/test/resources/testdata/yaml/sample_test_data.yaml");

        // Extract login_tests from the YAML data
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> loginTests = (List<Map<String, Object>>) testData.get(0).get("login_tests");

        Object[][] data = new Object[loginTests.size()][5];

        for (int i = 0; i < loginTests.size(); i++) {
            Map<String, Object> testCase = loginTests.get(i);
            data[i][0] = testCase.get("test_case");
            data[i][1] = testCase.get("username");
            data[i][2] = testCase.get("password");
            data[i][3] = testCase.get("expected_result");
            data[i][4] = testCase.get("description");
        }

        return data;
    }

    /**
     * Data provider for Excel user registration data.
     * Note: This assumes you have an Excel file with user data.
     * In a real scenario, you would create the Excel file first.
     */
    @DataProvider(name = "excelUserData")
    public Object[][] getExcelUserData() {
        // This is a sample - in real implementation, you would read from an actual Excel file
        // For demonstration, returning sample data
        return new Object[][] {
            {"John", "Doe", "john.doe@example.com", "success"},
            {"Jane", "Smith", "jane.smith@example.com", "success"},
            {"Bob", "Johnson", "bob.johnson@example.com", "failure"}
        };
    }

    /**
     * Sample test to demonstrate Excel file creation.
     */
    @Test(description = "Create sample Excel test data file")
    public void createSampleExcelData() {
        String filePath = "src/test/resources/testdata/excel/sample_user_data.xlsx";
        List<Map<String, String>> sampleData = List.of(
            Map.of("firstName", "John", "lastName", "Doe", "email", "john.doe@example.com", "expectedResult", "success"),
            Map.of("firstName", "Jane", "lastName", "Smith", "email", "jane.smith@example.com", "expectedResult", "success"),
            Map.of("firstName", "Bob", "lastName", "Johnson", "email", "bob.johnson@example.com", "expectedResult", "failure")
        );

        ExcelReader.writeExcel(filePath, "Users", sampleData);
        logger.info("Sample Excel file created: {}", filePath);

        // Verify the file was created and can be read
        List<Map<String, String>> readData = ExcelReader.readExcel(filePath, "Users");
        Assert.assertEquals(readData.size(), 3, "Should have 3 rows of data");
    }
}
