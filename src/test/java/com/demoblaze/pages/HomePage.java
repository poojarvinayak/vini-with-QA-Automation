package com.demoblaze.pages;

import io.qameta.allure.Step;
import org.automationframework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

import java.util.List;

/**
 * DemoBlaze Home Page Object Model.
 * Handles product selection and navigation.
 */
public class HomePage extends BasePage {

    // ==================== Constructors ====================

    /**
     * Constructor for HomePage.
     * Initializes the page with WebDriver instance.
     *
     * @param driver WebDriver instance
     */
    public HomePage(WebDriver driver) {
        super(driver);
    }

    // ==================== Page Elements ====================

    @FindBy(id = "nameofuser")
    private WebElement welcomeUserElement;

    @FindBy(id = "logout2")
    private WebElement logoutButton;

    @FindBy(id = "cartur")
    private WebElement cartLink;

    @FindBy(xpath = "//a[text()='Home']")
    private WebElement homeLink;

    @FindBy(linkText = "Contact")
    private WebElement contactLink;

    @FindBy(linkText = "About us")
    private WebElement aboutUsLink;

    @FindBy(id = "signin2")
    private WebElement signUpButton;

    @FindBy(id = "login2")
    private WebElement loginButton;

    @FindBy(className = "card")
    private List<WebElement> productCards;

    @FindBy(id = "next2")
    private WebElement nextButton;

    @FindBy(id = "prev2")
    private WebElement previousButton;

    // ==================== Page Actions ====================

    /**
     * Verifies if the home page is loaded.
     */
    @Override
    @Step("Verifying home page is loaded")
    public boolean isPageLoaded() {
        try {
            // Wait for page title to contain STORE and either login button (not logged in) or logout button (logged in)
            uiActions.waitForSeconds(2); // Brief wait for page to stabilize
            boolean hasStoreTitle = getPageTitle().contains("STORE");

            // Check for either login button (not logged in) or logout button (logged in)
            boolean hasNavigation = false;
            try {
                hasNavigation = loginButton.isDisplayed() || logoutButton.isDisplayed();
            } catch (Exception e) {
                // If neither button is found, try checking for welcome message or cart link
                try {
                    hasNavigation = cartLink.isDisplayed();
                } catch (Exception e2) {
                    // Last resort - check if we can find any product cards
                    hasNavigation = !productCards.isEmpty();
                }
            }

            return hasStoreTitle && hasNavigation;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if user is logged in.
     */
    @Step("Checking if user is logged in")
    public boolean isUserLoggedIn() {
        try {
            return welcomeUserElement.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the welcome message.
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
        return new LoginPage(driver);
    }

    /**
     * Clicks cart link.
     */
    @Step("Clicking cart link")
    public CartPage clickCart() {
        uiActions.click(cartLink);
        return new CartPage(driver);
    }

    /**
     * Selects a product by name.
     */
    @Step("Selecting product: {productName}")
    public ProductPage selectProduct(String productName) {
        // Find product card by name
        for (WebElement card : productCards) {
            WebElement productNameElement = card.findElement(By.className("card-title"));
            if (productNameElement.getText().equals(productName)) {
                WebElement productLink = card.findElement(By.tagName("a"));
                uiActions.click(productLink);
                break;
            }
        }
        return new ProductPage(driver);
    }

    /**
     * Selects first available product.
     */
    @Step("Selecting first available product")
    public ProductPage selectFirstProduct() {
        if (!productCards.isEmpty()) {
            WebElement firstProduct = productCards.get(0);
            WebElement productLink = firstProduct.findElement(By.tagName("a"));
            uiActions.click(productLink);
            return new ProductPage(driver);
        }
        throw new RuntimeException("No products available on the page");
    }

    /**
     * Gets list of product names on current page.
     */
    @Step("Getting product names")
    public java.util.List<String> getProductNames() {
        java.util.List<String> productNames = new java.util.ArrayList<>();
        for (WebElement card : productCards) {
            WebElement productNameElement = card.findElement(By.className("card-title"));
            productNames.add(productNameElement.getText());
        }
        return productNames;
    }

    /**
     * Gets the number of products displayed.
     */
    @Step("Getting product count")
    public int getProductCount() {
        return productCards.size();
    }

    /**
     * Clicks next button for pagination.
     */
    @Step("Clicking next page button")
    public HomePage clickNextPage() {
        if (nextButton.isDisplayed() && nextButton.isEnabled()) {
            uiActions.click(nextButton);
            uiActions.waitForSeconds(2); // Wait for page to load
        }
        return this;
    }

    /**
     * Clicks previous button for pagination.
     */
    @Step("Clicking previous page button")
    public HomePage clickPreviousPage() {
        if (previousButton.isDisplayed() && previousButton.isEnabled()) {
            uiActions.click(previousButton);
            uiActions.waitForSeconds(2); // Wait for page to load
        }
        return this;
    }

    /**
     * Selects product by category.
     */
    @Step("Selecting category: {category}")
    public HomePage selectCategory(String category) {
        WebElement categoryLink = driver.findElement(By.linkText(category));
        uiActions.click(categoryLink);
        uiActions.waitForSeconds(2); // Wait for products to load
        return this;
    }

    /**
     * Searches for a product by name and selects it.
     */
    @Step("Searching and selecting product: {productName}")
    public ProductPage searchAndSelectProduct(String productName) {
        java.util.List<String> products = getProductNames();
        if (products.contains(productName)) {
            return selectProduct(productName);
        } else {
            // Try next page if product not found
            clickNextPage();
            uiActions.waitForSeconds(2);
            products = getProductNames();
            if (products.contains(productName)) {
                return selectProduct(productName);
            }
        }
        throw new RuntimeException("Product '" + productName + "' not found");
    }
}
