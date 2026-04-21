package com.vinayakqa.automationframework.pages;

import io.qameta.allure.Step;
import com.vinayakqa.automationframework.pages.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Sample Login Page Object Model class.
 * Demonstrates the Page Object Model pattern implementation.
 */
public class LoginPage extends BasePage {

    // ==================== Constructor ====================

    /**
     * Constructor for LoginPage.
     * Initializes the page with WebDriver instance.
     *
     * @param driver WebDriver instance
     */
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // ==================== Page Elements ====================

    @FindBy(id = "username")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(className = "error-message")
    private WebElement errorMessage;

    @FindBy(linkText = "Forgot Password?")
    private WebElement forgotPasswordLink;

    @FindBy(id = "remember-me")
    private WebElement rememberMeCheckbox;

    // ==================== Page Actions ====================

    /**
     * Verifies if the login page is loaded.
     */
    @Override
    @Step("Verifying login page is loaded")
    public boolean isPageLoaded() {
        try {
            return usernameField.isDisplayed() &&
                   passwordField.isDisplayed() &&
                   loginButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Enters username in the username field.
     */
    @Step("Entering username: {username}")
    public LoginPage enterUsername(String username) {
        uiActions.sendKeys(usernameField, username);
        return this;
    }

    /**
     * Enters password in the password field.
     */
    @Step("Entering password")
    public LoginPage enterPassword(String password) {
        uiActions.sendKeys(passwordField, password);
        return this;
    }

    /**
     * Clicks the login button.
     */
    @Step("Clicking login button")
    public void clickLoginButton() {
        uiActions.click(loginButton);
    }

    /**
     * Performs login with username and password.
     */
    @Step("Logging in with username: {username}")
    public void login(String username, String password) {
        enterUsername(username)
            .enterPassword(password)
            .clickLoginButton();
    }

    /**
     * Gets the error message text.
     */
    @Step("Getting error message")
    public String getErrorMessage() {
        return uiActions.getText(errorMessage);
    }

    /**
     * Checks if error message is displayed.
     */
    @Step("Checking if error message is displayed")
    public boolean isErrorMessageDisplayed() {
        return uiActions.isElementDisplayed(errorMessage);
    }

    /**
     * Clicks the forgot password link.
     */
    @Step("Clicking forgot password link")
    public void clickForgotPasswordLink() {
        uiActions.click(forgotPasswordLink);
    }

    /**
     * Checks the remember me checkbox.
     */
    @Step("Checking remember me checkbox")
    public LoginPage checkRememberMe() {
        uiActions.checkCheckbox(rememberMeCheckbox);
        return this;
    }

    /**
     * Unchecks the remember me checkbox.
     */
    @Step("Unchecking remember me checkbox")
    public LoginPage uncheckRememberMe() {
        uiActions.uncheckCheckbox(rememberMeCheckbox);
        return this;
    }

    /**
     * Checks if remember me checkbox is selected.
     */
    @Step("Checking if remember me is selected")
    public boolean isRememberMeChecked() {
        return uiActions.isCheckboxChecked(rememberMeCheckbox);
    }

    /**
     * Clears the username field.
     */
    @Step("Clearing username field")
    public LoginPage clearUsername() {
        uiActions.clearText(usernameField);
        return this;
    }

    /**
     * Clears the password field.
     */
    @Step("Clearing password field")
    public LoginPage clearPassword() {
        uiActions.clearText(passwordField);
        return this;
    }

    /**
     * Performs login and returns the next page (placeholder).
     * In real implementation, this would return the appropriate page object.
     */
    @Step("Performing login and navigating to dashboard")
    public Object loginAndNavigateToDashboard(String username, String password) {
        login(username, password);
        // In real implementation, you would wait for navigation and return appropriate page object
        // return new DashboardPage(driver);
        return null; // Placeholder
    }

    /**
     * Validates login form elements.
     */
    @Step("Validating login form elements")
    public boolean validateLoginForm() {
        return uiActions.isElementDisplayed(usernameField) &&
               uiActions.isElementDisplayed(passwordField) &&
               uiActions.isElementDisplayed(loginButton) &&
               uiActions.isElementEnabled(loginButton);
    }

    /**
     * Gets the placeholder text of username field.
     */
    @Step("Getting username field placeholder")
    public String getUsernamePlaceholder() {
        return uiActions.getAttribute(usernameField, "placeholder");
    }

    /**
     * Gets the placeholder text of password field.
     */
    @Step("Getting password field placeholder")
    public String getPasswordPlaceholder() {
        return uiActions.getAttribute(passwordField, "placeholder");
    }

    /**
     * Checks if login button is enabled.
     */
    @Step("Checking if login button is enabled")
    public boolean isLoginButtonEnabled() {
        return uiActions.isElementEnabled(loginButton);
    }

    // ==================== Business Logic Methods ====================

    /**
     * Attempts login and verifies success.
     */
    @Step("Attempting login and verifying success")
    public boolean loginAndVerifySuccess(String username, String password) {
        login(username, password);
        uiActions.waitForElementToBeInvisible(errorMessage);
        // In real implementation, check for successful login indicators
        return !isErrorMessageDisplayed();
    }

    /**
     * Attempts login and verifies failure with expected error message.
     */
    @Step("Attempting login and verifying failure")
    public boolean loginAndVerifyFailure(String username, String password, String expectedError) {
        login(username, password);
        uiActions.waitForElementToBeVisible(errorMessage);
        return isErrorMessageDisplayed() && getErrorMessage().contains(expectedError);
    }
}
