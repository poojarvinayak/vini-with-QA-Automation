package com.yourcompany.tests;

import com.automation.framework.base.BaseTest;
import com.automation.framework.listeners.RetryAnalyzer;
import com.automation.framework.utils.YamlReader;
import com.yourcompany.pages.DashboardPage;
import com.yourcompany.pages.LoginPage;
import io.qameta.allure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * Example test class demonstrating how to use the Selenium Automation Framework.
 * This class shows various testing patterns and framework features.
 */
@Epic("Authentication")
@Feature("Login Functionality")
public class LoginTests extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(LoginTests.class);

    /**
     * Simple login test using hardcoded data.
     */
    @Test(description = "Valid login test with hardcoded credentials",
          groups = {"smoke", "regression"},
          retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test login functionality with valid credentials")
    public void testValidLogin() {
        logger.info("Starting valid login test");

        // Navigate to login page
        navigateTo(getConfig().getString("app.baseUrl") + "/login");

        // Create page object and perform login
        LoginPage loginPage = new LoginPage(getDriver());
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        // Perform login
        DashboardPage dashboardPage = loginPage.login("testuser@example.com", "TestPassword123");

        // Verify successful login
        Assert.assertTrue(dashboardPage.isPageLoaded(), "Dashboard should be loaded after login");
        Assert.assertTrue(dashboardPage.validateDashboard(), "Dashboard should be properly displayed");

        logger.info("Valid login test completed successfully");
    }

    /**
     * Data-driven login test using YAML data provider.
     */
    @Test(dataProvider = "loginDataProvider",
          description = "Data-driven login test using YAML",
          groups = {"regression"},
          retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.NORMAL)
    @Description("Test login functionality with multiple data sets from YAML file")
    public void testLoginWithYamlData(String testCase, String username, String password,
                                    String expectedResult, String description) {
        logger.info("Executing test case: {} - {}", testCase, description);

        // Navigate to login page
        navigateTo(getConfig().getString("app.baseUrl") + "/login");

        LoginPage loginPage = new LoginPage(getDriver());
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded");

        if ("success".equals(expectedResult)) {
            // Test successful login
            DashboardPage dashboardPage = loginPage.login(username, password);
            Assert.assertTrue(dashboardPage.isPageLoaded(),
                "Dashboard should be loaded for successful login");

        } else {
            // Test failed login
            loginPage.login(username, password);
            Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Error message should be displayed for failed login");
        }

        logger.info("Completed test case: {}", testCase);
    }

    /**
     * Test login form validation.
     */
    @Test(description = "Login form validation test",
          groups = {"smoke"},
          retryAnalyzer = RetryAnalyzer.class)
    @Severity(SeverityLevel.MINOR)
    @Description("Test login form elements and validation")
    public void testLoginFormValidation() {
        logger.info("Starting login form validation test");

        navigateTo(getConfig().getString("app.baseUrl") + "/login");

        LoginPage loginPage = new LoginPage(getDriver());

        // Validate form elements are present
        Assert.assertTrue(loginPage.validateLoginForm(),
            "All login form elements should be present and enabled");

        // Test empty form submission
        loginPage.clickLoginButton();
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
            "Error message should appear for empty form submission");

        // Test field placeholders
        Assert.assertNotNull(loginPage.getUsernamePlaceholder(),
            "Username field should have placeholder text");
        Assert.assertNotNull(loginPage.getPasswordPlaceholder(),
            "Password field should have placeholder text");

        logger.info("Login form validation test completed");
    }

    /**
     * Test remember me functionality.
     */
    @Test(description = "Remember me checkbox test",
          groups = {"regression"})
    @Severity(SeverityLevel.MINOR)
    @Description("Test remember me checkbox functionality")
    public void testRememberMeFunctionality() {
        logger.info("Starting remember me functionality test");

        navigateTo(getConfig().getString("app.baseUrl") + "/login");

        LoginPage loginPage = new LoginPage(getDriver());

        // Initially unchecked
        Assert.assertFalse(loginPage.isRememberMeChecked(),
            "Remember me should be unchecked by default");

        // Check the checkbox
        loginPage.checkRememberMe();
        Assert.assertTrue(loginPage.isRememberMeChecked(),
            "Remember me should be checked after clicking");

        // Uncheck the checkbox
        loginPage.uncheckRememberMe();
        Assert.assertFalse(loginPage.isRememberMeChecked(),
            "Remember me should be unchecked after clicking again");

        logger.info("Remember me functionality test completed");
    }

    /**
     * Test forgot password link.
     */
    @Test(description = "Forgot password link test",
          groups = {"regression"})
    @Severity(SeverityLevel.MINOR)
    @Description("Test forgot password link navigation")
    public void testForgotPasswordLink() {
        logger.info("Starting forgot password link test");

        navigateTo(getConfig().getString("app.baseUrl") + "/login");

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.clickForgotPasswordLink();

        // In real implementation, verify navigation to forgot password page
        // Assert.assertTrue(forgotPasswordPage.isPageLoaded(), "Should navigate to forgot password page");

        logger.info("Forgot password link test completed");
    }

    /**
     * Test logout functionality.
     */
    @Test(description = "Logout functionality test",
          groups = {"regression"},
          dependsOnMethods = {"testValidLogin"})
    @Severity(SeverityLevel.NORMAL)
    @Description("Test logout functionality after successful login")
    public void testLogoutFunctionality() {
        logger.info("Starting logout functionality test");

        // Assuming user is logged in from previous test
        // In real implementation, you might want to login first
        navigateTo(getConfig().getString("app.baseUrl") + "/dashboard");

        DashboardPage dashboardPage = new DashboardPage(getDriver());
        Assert.assertTrue(dashboardPage.isPageLoaded(), "Should be on dashboard");

        // Perform logout
        LoginPage loginPage = dashboardPage.clickLogout();
        Assert.assertTrue(loginPage.isPageLoaded(), "Should return to login page after logout");

        logger.info("Logout functionality test completed");
    }

    // ==================== Data Providers ====================

    /**
     * Data provider for login test data from YAML file.
     */
    @DataProvider(name = "loginDataProvider")
    public Object[][] getLoginData() {
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
}
