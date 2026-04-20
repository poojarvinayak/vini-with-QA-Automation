package com.demoblaze.pages;

import io.qameta.allure.Step;
import org.automationframework.pages.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * DemoBlaze Login Page Object Model.
 * Handles login functionality for https://www.demoblaze.com/
 */
public class LoginPage extends BasePage {

    // ==================== Constructors ====================

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

    @FindBy(id = "login2")
    private WebElement loginButton;

    @FindBy(id = "loginusername")
    private WebElement usernameField;

    @FindBy(id = "loginpassword")
    private WebElement passwordField;

    @FindBy(xpath = "//button[text()='Log in']")
    private WebElement submitLoginButton;

    @FindBy(id = "logout2")
    private WebElement logoutButton;

    @FindBy(id = "nameofuser")
    private WebElement welcomeUserElement;

    @FindBy(xpath = "//div[@id='logInModal']//button[@class='btn btn-secondary']")
    private WebElement closeLoginModalButton;

    // ==================== Page Actions ====================

    /**
     * Verifies if the login page is loaded.
     */
    @Override
    @Step("Verifying login page is loaded")
    public boolean isPageLoaded() {
        try {
            // Check if we're on the DemoBlaze home page
            return getPageTitle().contains("STORE") && loginButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Clicks the login button to open login modal.
     */
    @Step("Clicking login button to open modal")
    public LoginPage clickLoginButton() {
        uiActions.click(loginButton);
        uiActions.waitForElementToBeVisible(usernameField);
        return this;
    }

    /**
     * Enters username in the login modal.
     */
    @Step("Entering username: {username}")
    public LoginPage enterUsername(String username) {
        uiActions.sendKeys(usernameField, username);
        return this;
    }

    /**
     * Enters password in the login modal.
     */
    @Step("Entering password")
    public LoginPage enterPassword(String password) {
        uiActions.sendKeys(passwordField, password);
        return this;
    }

    /**
     * Clicks the submit login button.
     */
    @Step("Clicking submit login button")
    public HomePage clickSubmitLogin() {
        uiActions.click(submitLoginButton);

        // Handle any alert that might appear (success or failure)
        uiActions.acceptAlertIfPresent();

        // Wait for login to complete and home page to load
        uiActions.waitForElementToBeVisible(welcomeUserElement);
        return new HomePage(driver);
    }

    /**
     * Performs complete login flow and returns result.
     */
    @Step("Performing login with username: {username}")
    public LoginResult login(String username, String password) {
        clickLoginButton()
            .enterUsername(username)
            .enterPassword(password);

        uiActions.click(submitLoginButton);

        // Check for alert (indicates login failure)
        String alertText = uiActions.getAlertTextIfPresent();
        if (alertText != null) {
            uiActions.acceptAlertIfPresent();
            return new LoginResult(false, alertText);
        }

        // If no alert, check if login was successful
        try {
            uiActions.waitForElementToBeVisible(welcomeUserElement);
            return new LoginResult(true, null);
        } catch (Exception e) {
            return new LoginResult(false, "Login failed - welcome message not found");
        }
    }

    /**
     * Performs complete login flow (legacy method for backward compatibility).
     */
    @Step("Performing login with username: {username}")
    public HomePage loginAndGetHomePage(String username, String password) {
        LoginResult result = login(username, password);
        if (result.isSuccess()) {
            return new HomePage(driver);
        } else {
            throw new RuntimeException("Login failed: " + result.getErrorMessage());
        }
    }

    /**
     * Checks if user is logged in by verifying welcome message.
     */
    @Step("Checking if user is logged in")
    public boolean isUserLoggedIn() {
        try {
            return welcomeUserElement.isDisplayed() &&
                   welcomeUserElement.getText().contains("Welcome");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the welcome message text.
     */
    @Step("Getting welcome message")
    public String getWelcomeMessage() {
        return uiActions.getText(welcomeUserElement);
    }

    /**
     * Clicks logout button.
     */
    @Step("Clicking logout button")
    public LoginPage clickLogout() {
        uiActions.click(logoutButton);
        return this;
    }

    /**
     * Closes the login modal.
     */
    @Step("Closing login modal")
    public HomePage closeLoginModal() {
        uiActions.click(closeLoginModalButton);
        return new HomePage(driver);
    }

    /**
     * Validates login form elements.
     */
    @Step("Validating login form elements")
    public boolean validateLoginForm() {
        return uiActions.isElementDisplayed(usernameField) &&
               uiActions.isElementDisplayed(passwordField) &&
               uiActions.isElementDisplayed(submitLoginButton);
    }

    /**
     * Inner class to hold login result.
     */
    public static class LoginResult {
        private final boolean success;
        private final String errorMessage;

        public LoginResult(boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
