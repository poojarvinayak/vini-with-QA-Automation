package com.vinayakqa.pages;

import com.automation.framework.base.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Example Dashboard Page implementation.
 * Demonstrates page object pattern for post-login pages.
 */
public class DashboardPage extends BasePage {

    // ==================== Page Elements ====================

    @FindBy(className = "welcome-message")
    private WebElement welcomeMessage;

    @FindBy(id = "user-profile")
    private WebElement userProfileLink;

    @FindBy(id = "logout-button")
    private WebElement logoutButton;

    @FindBy(className = "dashboard-cards")
    private WebElement dashboardCards;

    // ==================== Page Actions ====================

    /**
     * Verifies if the dashboard page is loaded.
     */
    @Override
    @Step("Verifying dashboard page is loaded")
    public boolean isPageLoaded() {
        try {
            return welcomeMessage.isDisplayed() &&
                   userProfileLink.isDisplayed() &&
                   dashboardCards.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the welcome message text.
     */
    @Step("Getting welcome message")
    public String getWelcomeMessage() {
        return uiActions.getText(welcomeMessage);
    }

    /**
     * Clicks on user profile link.
     */
    @Step("Clicking user profile link")
    public UserProfilePage clickUserProfile() {
        uiActions.click(userProfileLink);
        return new UserProfilePage(driver);
    }

    /**
     * Clicks logout button.
     */
    @Step("Clicking logout button")
    public LoginPage clickLogout() {
        uiActions.click(logoutButton);
        return new LoginPage(driver);
    }

    /**
     * Checks if dashboard is properly loaded.
     */
    @Step("Validating dashboard content")
    public boolean validateDashboard() {
        return isPageLoaded() &&
               !getWelcomeMessage().isEmpty() &&
               uiActions.isElementDisplayed(dashboardCards);
    }
}
