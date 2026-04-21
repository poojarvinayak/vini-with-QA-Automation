package com.demoblaze.pages;

import io.qameta.allure.Step;
import com.vinayakqa.automationframework.pages.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * DemoBlaze Product Page Object Model.
 * Handles product details and add to cart functionality.
 */
public class ProductPage extends BasePage {

    // ==================== Constructors ====================

    /**
     * Constructor for ProductPage.
     * Initializes the page with WebDriver instance.
     *
     * @param driver WebDriver instance
     */
    public ProductPage(WebDriver driver) {
        super(driver);
    }

    // ==================== Page Elements ====================

    @FindBy(className = "name")
    private WebElement productNameElement;

    @FindBy(className = "price-container")
    private WebElement productPriceElement;

    @FindBy(id = "more-information")
    private WebElement productDescriptionElement;

    @FindBy(xpath = "//div[@id='imgp']//img")
    private WebElement productImage;

    @FindBy(linkText = "Add to cart")
    private WebElement addToCartButton;

    @FindBy(id = "cartur")
    private WebElement cartLink;

    @FindBy(xpath = "//a[contains(text(),'Home')]")
    private WebElement homeLink;

    // Alert elements (handled via JavaScript alerts)
    // Alert text will be handled in the addToCart method

    // ==================== Page Actions ====================

    /**
     * Verifies if the product page is loaded.
     */
    @Override
    @Step("Verifying product page is loaded")
    public boolean isPageLoaded() {
        try {
            return productNameElement.isDisplayed() &&
                   productPriceElement.isDisplayed() &&
                   addToCartButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the product name.
     */
    @Step("Getting product name")
    public String getProductName() {
        return uiActions.getText(productNameElement);
    }

    /**
     * Gets the product price.
     */
    @Step("Getting product price")
    public String getProductPrice() {
        return uiActions.getText(productPriceElement);
    }

    /**
     * Gets the product description.
     */
    @Step("Getting product description")
    public String getProductDescription() {
        return uiActions.getText(productDescriptionElement);
    }

    /**
     * Adds product to cart and handles the alert.
     */
    @Step("Adding product to cart")
    public ProductPage addToCart() {
        uiActions.click(addToCartButton);

        // Handle the alert that appears after adding to cart
        try {
            uiActions.waitForSeconds(1); // Wait for alert to appear
            String alertText = uiActions.getAlertText();
            logger.info("Alert text: {}", alertText);
            if (alertText != null && alertText.contains("Product added")) {
                uiActions.acceptAlert();
                logger.info("Product added to cart successfully");
            } else if (alertText != null) {
                uiActions.acceptAlert();
                logger.warn("Unexpected alert text: {}", alertText);
            }
        } catch (Exception e) {
            logger.debug("No alert appeared after adding to cart (this might be normal)");
        }

        return this;
    }

    /**
     * Clicks the cart link to navigate to cart page.
     */
    @Step("Navigating to cart page")
    public CartPage goToCart() {
        uiActions.click(cartLink);
        return new CartPage(driver);
    }

    /**
     * Clicks the home link to navigate back to home page.
     */
    @Step("Navigating back to home page")
    public HomePage goToHome() {
        // Navigate directly to home page instead of clicking link
        driver.get("https://www.demoblaze.com/");
        return new HomePage(driver);
    }

    /**
     * Adds product to cart and navigates to cart page.
     */
    @Step("Adding product to cart and going to cart")
    public CartPage addToCartAndGoToCart() {
        addToCart();
        uiActions.waitForSeconds(2); // Wait for alert to be handled
        return goToCart();
    }

    /**
     * Checks if the product image is displayed.
     */
    @Step("Checking if product image is displayed")
    public boolean isProductImageDisplayed() {
        try {
            return productImage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates all product page elements.
     */
    @Step("Validating product page elements")
    public boolean validateProductPage() {
        return isPageLoaded() &&
               !getProductName().isEmpty() &&
               !getProductPrice().isEmpty() &&
               isProductImageDisplayed();
    }

    /**
     * Gets the product price as integer (removes currency symbols).
     */
    @Step("Getting product price as number")
    public int getProductPriceAsInt() {
        String priceText = getProductPrice();
        // Remove currency symbols and extract number
        String priceNumber = priceText.replaceAll("[^0-9]", "");
        try {
            return Integer.parseInt(priceNumber);
        } catch (NumberFormatException e) {
            logger.warn("Could not parse price: {}", priceText);
            return 0;
        }
    }
}
