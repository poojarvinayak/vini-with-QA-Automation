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
 * DemoBlaze Cart Page Object Model.
 * Handles cart verification and cart operations.
 */
public class CartPage extends BasePage {

    // ==================== Constructors ====================

    /**
     * Constructor for CartPage.
     * Initializes the page with WebDriver instance.
     *
     * @param driver WebDriver instance
     */
    public CartPage(WebDriver driver) {
        super(driver);
    }

    // ==================== Page Elements ====================

    @FindBy(xpath = "//tbody[@id='tbodyid']/tr")
    private List<WebElement> cartItems;

    @FindBy(id = "totalp")
    private WebElement totalPriceElement;

    @FindBy(linkText = "Delete")
    private List<WebElement> deleteButtons;

    @FindBy(xpath = "//button[text()='Place Order']")
    private WebElement placeOrderButton;

    @FindBy(xpath = "//a[text()='Home']")
    private WebElement homeLink;

    @FindBy(id = "cartur")
    private WebElement cartLink;

    // ==================== Page Actions ====================

    /**
     * Verifies if the cart page is loaded.
     */
    @Override
    @Step("Verifying cart page is loaded")
    public boolean isPageLoaded() {
        try {
            // Wait a moment for page to load, then check basic cart indicators
            uiActions.waitForSeconds(3);
            return getCurrentUrl().contains("cart") ||
                   getCurrentUrl().contains("#/cart") ||
                   !cartItems.isEmpty() ||
                   (cartLink != null && cartLink.getAttribute("class") != null &&
                    cartLink.getAttribute("class").contains("active"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the number of items in the cart.
     */
    @Step("Getting cart item count")
    public int getCartItemCount() {
        return cartItems.size();
    }

    /**
     * Gets the total price from cart.
     */
    @Step("Getting cart total price")
    public String getTotalPrice() {
        try {
            return uiActions.getText(totalPriceElement);
        } catch (Exception e) {
            return "0";
        }
    }

    /**
     * Gets the total price as integer.
     */
    @Step("Getting cart total price as number")
    public int getTotalPriceAsInt() {
        String priceText = getTotalPrice();
        try {
            return Integer.parseInt(priceText);
        } catch (NumberFormatException e) {
            logger.warn("Could not parse total price: {}", priceText);
            return 0;
        }
    }

    /**
     * Gets all product names in the cart.
     */
    @Step("Getting cart product names")
    public java.util.List<String> getCartProductNames() {
        java.util.List<String> productNames = new java.util.ArrayList<>();
        for (WebElement item : cartItems) {
            try {
                WebElement nameElement = item.findElement(By.xpath(".//td[2]"));
                productNames.add(nameElement.getText());
            } catch (Exception e) {
                logger.warn("Could not get product name from cart item");
            }
        }
        return productNames;
    }

    /**
     * Gets all product prices in the cart.
     */
    @Step("Getting cart product prices")
    public java.util.List<Integer> getCartProductPrices() {
        java.util.List<Integer> prices = new java.util.ArrayList<>();
        for (WebElement item : cartItems) {
            try {
                WebElement priceElement = item.findElement(By.xpath(".//td[3]"));
                String priceText = priceElement.getText();
                String priceNumber = priceText.replaceAll("[^0-9]", "");
                prices.add(Integer.parseInt(priceNumber));
            } catch (Exception e) {
                logger.warn("Could not get product price from cart item");
            }
        }
        return prices;
    }

    /**
     * Checks if a specific product is in the cart.
     */
    @Step("Checking if product is in cart: {productName}")
    public boolean isProductInCart(String productName) {
        return getCartProductNames().contains(productName);
    }

    /**
     * Deletes a product from cart by index.
     */
    @Step("Deleting product from cart at index: {index}")
    public CartPage deleteProduct(int index) {
        if (index >= 0 && index < deleteButtons.size()) {
            uiActions.click(deleteButtons.get(index));
            uiActions.waitForSeconds(2); // Wait for item to be removed
        } else {
            throw new IndexOutOfBoundsException("Invalid product index: " + index);
        }
        return this;
    }

    /**
     * Deletes a product from cart by name.
     */
    @Step("Deleting product from cart: {productName}")
    public CartPage deleteProduct(String productName) {
        java.util.List<String> productNames = getCartProductNames();
        int index = productNames.indexOf(productName);
        if (index != -1) {
            deleteProduct(index);
        } else {
            throw new RuntimeException("Product '" + productName + "' not found in cart");
        }
        return this;
    }

    /**
     * Clears all items from cart.
     */
    @Step("Clearing all items from cart")
    public CartPage clearCart() {
        int itemCount = getCartItemCount();
        for (int i = 0; i < itemCount; i++) {
            try {
                deleteProduct(0); // Always delete first item
                uiActions.waitForSeconds(1);
            } catch (Exception e) {
                logger.warn("Could not delete item at index {}", i);
            }
        }
        return this;
    }

    /**
     * Clicks the Place Order button.
     */
    @Step("Clicking place order button")
    public void clickPlaceOrder() {
        uiActions.click(placeOrderButton);
    }

    /**
     * Navigates back to home page.
     */
    @Step("Navigating back to home page")
    public HomePage goToHome() {
        // Navigate directly to home page instead of clicking link
        driver.get("https://www.demoblaze.com/");
        return new HomePage(driver);
    }

    /**
     * Validates cart contents.
     */
    @Step("Validating cart contents")
    public boolean validateCart() {
        try {
            int itemCount = getCartItemCount();
            int totalPrice = getTotalPriceAsInt();
            java.util.List<Integer> prices = getCartProductPrices();

            // Calculate expected total
            int expectedTotal = prices.stream().mapToInt(Integer::intValue).sum();

            return totalPrice == expectedTotal;
        } catch (Exception e) {
            logger.warn("Could not validate cart contents", e);
            return false;
        }
    }

    /**
     * Checks if cart is empty.
     */
    @Step("Checking if cart is empty")
    public boolean isCartEmpty() {
        return getCartItemCount() == 0;
    }

    /**
     * Gets cart item details as a formatted string.
     */
    @Step("Getting cart summary")
    public String getCartSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Cart Summary:\n");
        summary.append("Items: ").append(getCartItemCount()).append("\n");

        java.util.List<String> names = getCartProductNames();
        java.util.List<Integer> prices = getCartProductPrices();

        for (int i = 0; i < names.size(); i++) {
            summary.append("- ").append(names.get(i))
                   .append(": $").append(prices.get(i)).append("\n");
        }

        summary.append("Total: $").append(getTotalPrice());

        return summary.toString();
    }
}
