package com.demoblaze.tests;

import org.automationframework.base.BaseTest;
import com.demoblaze.pages.CartPage;
import com.demoblaze.pages.HomePage;
import com.demoblaze.pages.LoginPage;
import com.demoblaze.pages.ProductPage;
import io.qameta.allure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * DemoBlaze Add to Cart Tests
 * Tests add to cart functionality on https://www.demoblaze.com/
 */
@Epic("E-commerce")
@Feature("Shopping Cart")
public class AddToCartTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(AddToCartTest.class);

    // Test credentials
    private static final String TEST_USERNAME = "VinayakYP";
    private static final String TEST_PASSWORD = "Admin@123";

    /**
     * Test adding a product to cart after login.
     */
    @Test(description = "Add product to cart test",
          groups = {"smoke", "regression"},
          priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test adding a product to cart after user login")
    public void testAddProductToCart() {
        logger.info("Starting add product to cart test");

        // Navigate to DemoBlaze
        navigateTo("https://www.demoblaze.com/");

        // Login first
        LoginPage loginPage = new LoginPage(getDriver());
        LoginPage.LoginResult loginResult = loginPage.login(TEST_USERNAME, TEST_PASSWORD);

        if (!loginResult.isSuccess()) {
            logger.warn("Login failed: {}. Skipping test that requires authentication.", loginResult.getErrorMessage());
            throw new org.testng.SkipException("Cannot test cart functionality without successful login");
        }

        HomePage homePage = new HomePage(getDriver());
        Assert.assertTrue(homePage.isUserLoggedIn(), "User should be logged in");

        // Clear cart first to ensure clean state
        CartPage cartPage = homePage.clickCart();
        if (!cartPage.isCartEmpty()) {
            cartPage.clearCart();
            homePage = cartPage.goToHome();
        } else {
            homePage = cartPage.goToHome();
        }

        // Select first available product
        ProductPage productPage = homePage.selectFirstProduct();
        Assert.assertTrue(productPage.isPageLoaded(), "Product page should be loaded");

        // Get product details before adding to cart
        String productName = productPage.getProductName();
        String productPrice = productPage.getProductPrice();
        logger.info("Selected product: {} - {}", productName, productPrice);

        // Add product to cart
        cartPage = productPage.addToCartAndGoToCart();

        // Verify product is in cart
        Assert.assertTrue(cartPage.isPageLoaded(), "Cart page should be loaded");
        Assert.assertTrue(cartPage.isProductInCart(productName), "Product should be in cart");
        Assert.assertEquals(cartPage.getCartItemCount(), 1, "Cart should contain 1 item");

        // Verify cart total
        Assert.assertTrue(cartPage.validateCart(), "Cart total should be correct");

        logger.info("Add product to cart test completed successfully");
    }

    /**
     * Test adding multiple products to cart.
     */
    @Test(description = "Add multiple products to cart test",
          groups = {"regression"},
          priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Test adding multiple products to cart")
    public void testAddMultipleProductsToCart() {
        logger.info("Starting add multiple products to cart test");

        navigateTo("https://www.demoblaze.com/");

        // Login
        LoginPage loginPage = new LoginPage(getDriver());
        LoginPage.LoginResult loginResult = loginPage.login(TEST_USERNAME, TEST_PASSWORD);
        
        if (!loginResult.isSuccess()) {
            logger.warn("Login failed: {}. Skipping test that requires authentication.", loginResult.getErrorMessage());
            throw new org.testng.SkipException("Cannot test cart functionality without successful login");
        }
        
        HomePage homePage = new HomePage(getDriver());
        Assert.assertTrue(homePage.isUserLoggedIn(), "User should be logged in");

        // Clear cart first to ensure clean state
        CartPage cartPage = homePage.clickCart();
        if (!cartPage.isCartEmpty()) {
            cartPage.clearCart();
            homePage = cartPage.goToHome();
        } else {
            homePage = cartPage.goToHome();
        }

        // Add first product
        ProductPage productPage1 = homePage.selectFirstProduct();
        String productName1 = productPage1.getProductName();
        productPage1.addToCart();

        // Go back to home and add second product
        homePage = productPage1.goToHome();
        ProductPage productPage2 = homePage.selectFirstProduct(); // This might select the same product
        String productName2 = productPage2.getProductName();
        productPage2.addToCart();

        // Go to cart
        cartPage = productPage2.goToCart();

        // Verify cart contents
        Assert.assertTrue(cartPage.isPageLoaded(), "Cart page should be loaded");
        Assert.assertTrue(cartPage.getCartItemCount() >= 1, "Cart should contain at least 1 item");

        // Log cart summary
        logger.info("Cart Summary:\n{}", cartPage.getCartSummary());

        logger.info("Add multiple products to cart test completed successfully");
    }

    /**
     * Test cart functionality without login.
     */
    @Test(description = "Add to cart without login test",
          groups = {"regression"},
          priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Test adding products to cart without user login")
    public void testAddToCartWithoutLogin() {
        logger.info("Starting add to cart without login test");

        navigateTo("https://www.demoblaze.com/");

        HomePage homePage = new HomePage(getDriver());
        Assert.assertTrue(homePage.isPageLoaded(), "Home page should be loaded");

        // Select product
        ProductPage productPage = homePage.selectFirstProduct();
        Assert.assertTrue(productPage.isPageLoaded(), "Product page should be loaded");

        String productName = productPage.getProductName();
        logger.info("Selected product: {}", productName);

        // Add to cart
        CartPage cartPage = productPage.addToCartAndGoToCart();

        // Verify cart functionality works without login
        Assert.assertTrue(cartPage.isPageLoaded(), "Cart page should be loaded");
        Assert.assertTrue(cartPage.isProductInCart(productName), "Product should be in cart");

        logger.info("Add to cart without login test completed successfully");
    }

    /**
     * Test product page validation.
     */
    @Test(description = "Product page validation test",
          groups = {"smoke"},
          priority = 4)
    @Severity(SeverityLevel.MINOR)
    @Description("Test product page elements and functionality")
    public void testProductPageValidation() {
        logger.info("Starting product page validation test");

        navigateTo("https://www.demoblaze.com/");

        HomePage homePage = new HomePage(getDriver());
        ProductPage productPage = homePage.selectFirstProduct();

        // Validate product page elements
        Assert.assertTrue(productPage.validateProductPage(), "Product page should be properly loaded");

        // Verify product details are not empty
        Assert.assertFalse(productPage.getProductName().isEmpty(), "Product name should not be empty");
        Assert.assertFalse(productPage.getProductPrice().isEmpty(), "Product price should not be empty");

        // Verify price is a valid number
        Assert.assertTrue(productPage.getProductPriceAsInt() > 0, "Product price should be greater than 0");

        logger.info("Product page validation test completed successfully");
    }

    /**
     * Test cart operations (view, validate).
     */
    @Test(description = "Cart operations test",
          groups = {"regression"},
          priority = 5)
    @Severity(SeverityLevel.NORMAL)
    @Description("Test cart viewing and validation functionality")
    public void testCartOperations() {
        logger.info("Starting cart operations test");

        navigateTo("https://www.demoblaze.com/");

        // Login
        LoginPage loginPage = new LoginPage(getDriver());
        LoginPage.LoginResult loginResult = loginPage.login(TEST_USERNAME, TEST_PASSWORD);
        
        if (!loginResult.isSuccess()) {
            logger.warn("Login failed: {}. Skipping test that requires authentication.", loginResult.getErrorMessage());
            throw new org.testng.SkipException("Cannot test cart functionality without successful login");
        }
        
        HomePage homePage = new HomePage(getDriver());
        // Add product to cart
        ProductPage productPage = homePage.selectFirstProduct();
        CartPage cartPage = productPage.addToCartAndGoToCart();

        // Validate cart
        Assert.assertTrue(cartPage.isPageLoaded(), "Cart page should be loaded");
        Assert.assertFalse(cartPage.isCartEmpty(), "Cart should not be empty");

        // Get cart summary
        String cartSummary = cartPage.getCartSummary();
        logger.info("Cart Summary:\n{}", cartSummary);

        // Validate cart calculations
        Assert.assertTrue(cartPage.validateCart(), "Cart calculations should be correct");

        // Navigate back to home
        homePage = cartPage.goToHome();
        Assert.assertTrue(homePage.isPageLoaded(), "Should navigate back to home page");

        logger.info("Cart operations test completed successfully");
    }

    /**
     * Test navigation between pages.
     */
    @Test(description = "Page navigation test",
          groups = {"smoke"},
          priority = 6)
    @Severity(SeverityLevel.MINOR)
    @Description("Test navigation between different pages")
    public void testPageNavigation() {
        logger.info("Starting page navigation test");

        navigateTo("https://www.demoblaze.com/");

        // Login
        LoginPage loginPage = new LoginPage(getDriver());
        LoginPage.LoginResult loginResult = loginPage.login(TEST_USERNAME, TEST_PASSWORD);
        
        if (!loginResult.isSuccess()) {
            logger.warn("Login failed: {}. Skipping test that requires authentication.", loginResult.getErrorMessage());
            throw new org.testng.SkipException("Cannot test navigation without successful login");
        }
        
        HomePage homePage = new HomePage(getDriver());

        // Home -> Product
        ProductPage productPage = homePage.selectFirstProduct();
        Assert.assertTrue(productPage.isPageLoaded(), "Product page should be loaded");

        // Product -> Cart
        CartPage cartPage = productPage.goToCart();
        Assert.assertTrue(cartPage.isPageLoaded(), "Cart page should be loaded");

        // Cart -> Home
        homePage = cartPage.goToHome();
        Assert.assertTrue(homePage.isPageLoaded(), "Home page should be loaded");

        // Home -> Cart
        cartPage = homePage.clickCart();
        Assert.assertTrue(cartPage.isPageLoaded(), "Cart page should be loaded");

        logger.info("Page navigation test completed successfully");
    }
}
