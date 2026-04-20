package org.automationframework.utils;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.automationframework.config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * UIActions utility class containing common UI interaction methods.
 * Provides methods for clicking, typing, scrolling, selecting, etc.
 */
public class UIActions {

    private static final Logger logger = LoggerFactory.getLogger(UIActions.class);
    private final WebDriver driver;
    private final WebDriverWait explicitWait;
    private final Actions actions;
    private final ConfigurationManager config = ConfigurationManager.getInstance();

    public UIActions(WebDriver driver) {
        this.driver = driver;
        int explicitWaitTime = config.getInt("app.explicitWaitTime", 15);
        this.explicitWait = new WebDriverWait(driver, Duration.ofSeconds(explicitWaitTime));
        this.actions = new Actions(driver);
    }

    // ==================== Element Interaction Methods ====================

    /**
     * Clicks on an element.
     */
    public void click(WebElement element) {
        try {
            explicitWait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
            logger.debug("Clicked on element: {}", element);
        } catch (StaleElementReferenceException e) {
            logger.warn("Stale element reference, retrying click");
            click(element);
        } catch (Exception e) {
            logger.error("Error clicking on element", e);
            throw new RuntimeException("Failed to click on element", e);
        }
    }

    /**
     * Clicks on an element by locator.
     */
    public void click(By locator) {
        try {
            WebElement element = explicitWait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
            logger.debug("Clicked on element with locator: {}", locator);
        } catch (Exception e) {
            logger.error("Error clicking on element with locator: {}", locator, e);
            throw new RuntimeException("Failed to click on element", e);
        }
    }

    /**
     * Double clicks on an element.
     */
    public void doubleClick(WebElement element) {
        try {
            explicitWait.until(ExpectedConditions.elementToBeClickable(element));
            actions.doubleClick(element).perform();
            logger.debug("Double clicked on element: {}", element);
        } catch (Exception e) {
            logger.error("Error double clicking on element", e);
            throw new RuntimeException("Failed to double click", e);
        }
    }

    /**
     * Right clicks on an element.
     */
    public void rightClick(WebElement element) {
        try {
            explicitWait.until(ExpectedConditions.elementToBeClickable(element));
            actions.contextClick(element).perform();
            logger.debug("Right clicked on element: {}", element);
        } catch (Exception e) {
            logger.error("Error right clicking on element", e);
            throw new RuntimeException("Failed to right click", e);
        }
    }

    /**
     * Types text into an element.
     */
    public void sendKeys(WebElement element, String text) {
        try {
            explicitWait.until(ExpectedConditions.visibilityOf(element));
            element.clear();
            element.sendKeys(text);
            logger.debug("Sent keys to element: {}", text);
        } catch (Exception e) {
            logger.error("Error sending keys to element", e);
            throw new RuntimeException("Failed to send keys", e);
        }
    }

    /**
     * Clears text from an element.
     */
    public void clearText(WebElement element) {
        try {
            explicitWait.until(ExpectedConditions.visibilityOf(element));
            element.clear();
            logger.debug("Cleared text from element: {}", element);
        } catch (Exception e) {
            logger.error("Error clearing text from element", e);
            throw new RuntimeException("Failed to clear text", e);
        }
    }

    /**
     * Gets text from an element.
     */
    public String getText(WebElement element) {
        try {
            explicitWait.until(ExpectedConditions.visibilityOf(element));
            String text = element.getText();
            logger.debug("Retrieved text from element: {}", text);
            return text;
        } catch (Exception e) {
            logger.error("Error getting text from element", e);
            throw new RuntimeException("Failed to get text", e);
        }
    }

    /**
     * Gets attribute value from an element.
     */
    public String getAttribute(WebElement element, String attributeName) {
        try {
            explicitWait.until(ExpectedConditions.visibilityOf(element));
            String value = element.getAttribute(attributeName);
            logger.debug("Retrieved attribute '{}' from element: {}", attributeName, value);
            return value;
        } catch (Exception e) {
            logger.error("Error getting attribute from element", e);
            throw new RuntimeException("Failed to get attribute", e);
        }
    }

    // ==================== Dropdown Methods ====================

    /**
     * Selects option by visible text in dropdown.
     */
    public void selectByVisibleText(WebElement dropdownElement, String visibleText) {
        try {
            explicitWait.until(ExpectedConditions.elementToBeClickable(dropdownElement));
            Select select = new Select(dropdownElement);
            select.selectByVisibleText(visibleText);
            logger.debug("Selected option by visible text: {}", visibleText);
        } catch (Exception e) {
            logger.error("Error selecting option by visible text: {}", visibleText, e);
            throw new RuntimeException("Failed to select option", e);
        }
    }

    /**
     * Selects option by value in dropdown.
     */
    public void selectByValue(WebElement dropdownElement, String value) {
        try {
            explicitWait.until(ExpectedConditions.elementToBeClickable(dropdownElement));
            Select select = new Select(dropdownElement);
            select.selectByValue(value);
            logger.debug("Selected option by value: {}", value);
        } catch (Exception e) {
            logger.error("Error selecting option by value: {}", value, e);
            throw new RuntimeException("Failed to select option", e);
        }
    }

    /**
     * Selects option by index in dropdown.
     */
    public void selectByIndex(WebElement dropdownElement, int index) {
        try {
            explicitWait.until(ExpectedConditions.elementToBeClickable(dropdownElement));
            Select select = new Select(dropdownElement);
            select.selectByIndex(index);
            logger.debug("Selected option by index: {}", index);
        } catch (Exception e) {
            logger.error("Error selecting option by index: {}", index, e);
            throw new RuntimeException("Failed to select option", e);
        }
    }

    /**
     * Gets all options from dropdown.
     */
    public List<WebElement> getAllDropdownOptions(WebElement dropdownElement) {
        try {
            explicitWait.until(ExpectedConditions.elementToBeClickable(dropdownElement));
            Select select = new Select(dropdownElement);
            List<WebElement> options = select.getOptions();
            logger.debug("Retrieved {} options from dropdown", options.size());
            return options;
        } catch (Exception e) {
            logger.error("Error getting dropdown options", e);
            throw new RuntimeException("Failed to get dropdown options", e);
        }
    }

    /**
     * Gets selected option text from dropdown.
     */
    public String getSelectedOptionText(WebElement dropdownElement) {
        try {
            explicitWait.until(ExpectedConditions.elementToBeClickable(dropdownElement));
            Select select = new Select(dropdownElement);
            String selectedText = select.getFirstSelectedOption().getText();
            logger.debug("Retrieved selected option text: {}", selectedText);
            return selectedText;
        } catch (Exception e) {
            logger.error("Error getting selected option text", e);
            throw new RuntimeException("Failed to get selected option", e);
        }
    }

    // ==================== Checkbox and Radio Button Methods ====================

    /**
     * Checks a checkbox if not already checked.
     */
    public void checkCheckbox(WebElement checkboxElement) {
        try {
            explicitWait.until(ExpectedConditions.elementToBeClickable(checkboxElement));
            if (!checkboxElement.isSelected()) {
                checkboxElement.click();
                logger.debug("Checked checkbox");
            }
        } catch (Exception e) {
            logger.error("Error checking checkbox", e);
            throw new RuntimeException("Failed to check checkbox", e);
        }
    }

    /**
     * Unchecks a checkbox if checked.
     */
    public void uncheckCheckbox(WebElement checkboxElement) {
        try {
            explicitWait.until(ExpectedConditions.elementToBeClickable(checkboxElement));
            if (checkboxElement.isSelected()) {
                checkboxElement.click();
                logger.debug("Unchecked checkbox");
            }
        } catch (Exception e) {
            logger.error("Error unchecking checkbox", e);
            throw new RuntimeException("Failed to uncheck checkbox", e);
        }
    }

    /**
     * Checks if checkbox is selected.
     */
    public boolean isCheckboxChecked(WebElement checkboxElement) {
        try {
            explicitWait.until(ExpectedConditions.visibilityOf(checkboxElement));
            boolean isSelected = checkboxElement.isSelected();
            logger.debug("Checkbox selected status: {}", isSelected);
            return isSelected;
        } catch (Exception e) {
            logger.error("Error checking checkbox status", e);
            throw new RuntimeException("Failed to check checkbox status", e);
        }
    }

    // ==================== Scroll Methods ====================

    /**
     * Scrolls to an element.
     */
    public void scrollToElement(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            Thread.sleep(500); // Small delay for smooth scrolling
            logger.debug("Scrolled to element: {}", element);
        } catch (Exception e) {
            logger.error("Error scrolling to element", e);
            throw new RuntimeException("Failed to scroll to element", e);
        }
    }

    /**
     * Scrolls by pixels.
     */
    public void scrollByPixels(int pixels) {
        try {
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, " + pixels + ");");
            logger.debug("Scrolled by {} pixels", pixels);
        } catch (Exception e) {
            logger.error("Error scrolling by pixels", e);
            throw new RuntimeException("Failed to scroll", e);
        }
    }

    /**
     * Scrolls to top of page.
     */
    public void scrollToTop() {
        try {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
            logger.debug("Scrolled to top of page");
        } catch (Exception e) {
            logger.error("Error scrolling to top", e);
            throw new RuntimeException("Failed to scroll to top", e);
        }
    }

    /**
     * Scrolls to bottom of page.
     */
    public void scrollToBottom() {
        try {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            logger.debug("Scrolled to bottom of page");
        } catch (Exception e) {
            logger.error("Error scrolling to bottom", e);
            throw new RuntimeException("Failed to scroll to bottom", e);
        }
    }

    // ==================== Wait Methods ====================

    /**
     * Waits for element to be visible.
     */
    public WebElement waitForElementToBeVisible(By locator) {
        try {
            WebElement element = explicitWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            logger.debug("Element is visible: {}", locator);
            return element;
        } catch (Exception e) {
            logger.error("Error waiting for element to be visible: {}", locator, e);
            throw new RuntimeException("Element not visible", e);
        }
    }

    /**
     * Waits for WebElement to be visible.
     */
    public void waitForElementToBeVisible(WebElement element) {
        try {
            explicitWait.until(ExpectedConditions.visibilityOf(element));
            logger.debug("Element is visible: {}", element);
        } catch (Exception e) {
            logger.error("Error waiting for element to be visible", e);
            throw new RuntimeException("Element not visible", e);
        }
    }

    /**
     * Waits for element to be clickable.
     */
    public WebElement waitForElementToBeClickable(By locator) {
        try {
            WebElement element = explicitWait.until(ExpectedConditions.elementToBeClickable(locator));
            logger.debug("Element is clickable: {}", locator);
            return element;
        } catch (Exception e) {
            logger.error("Error waiting for element to be clickable: {}", locator, e);
            throw new RuntimeException("Element not clickable", e);
        }
    }

    /**
     * Waits for element to be invisible.
     */
    public boolean waitForElementToBeInvisible(By locator) {
        try {
            boolean isInvisible = explicitWait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            logger.debug("Element is invisible: {}", locator);
            return isInvisible;
        } catch (Exception e) {
            logger.error("Error waiting for element to be invisible: {}", locator, e);
            throw new RuntimeException("Element still visible", e);
        }
    }

    /**
     * Waits for WebElement to be invisible.
     */
    public boolean waitForElementToBeInvisible(WebElement element) {
        try {
            boolean isInvisible = explicitWait.until(ExpectedConditions.invisibilityOf(element));
            logger.debug("Element is invisible: {}", element);
            return isInvisible;
        } catch (Exception e) {
            logger.error("Error waiting for element to be invisible", e);
            throw new RuntimeException("Element still visible", e);
        }
    }

    /**
     * Waits for specified number of seconds.
     */
    public void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
            logger.debug("Waited for {} seconds", seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Wait interrupted", e);
            throw new RuntimeException("Wait was interrupted", e);
        }
    }

    // ==================== Alert Methods ====================

    /**
     * Accepts alert.
     */
    public void acceptAlert() {
        try {
            Alert alert = explicitWait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
            logger.debug("Alert accepted");
        } catch (NoAlertPresentException e) {
            logger.warn("No alert present to accept");
        } catch (Exception e) {
            logger.error("Error accepting alert", e);
            throw new RuntimeException("Failed to accept alert", e);
        }
    }

    /**
     * Dismisses alert.
     */
    public void dismissAlert() {
        try {
            Alert alert = explicitWait.until(ExpectedConditions.alertIsPresent());
            alert.dismiss();
            logger.debug("Alert dismissed");
        } catch (NoAlertPresentException e) {
            logger.warn("No alert present to dismiss");
        } catch (Exception e) {
            logger.error("Error dismissing alert", e);
            throw new RuntimeException("Failed to dismiss alert", e);
        }
    }

    /**
     * Gets alert text.
     */
    public String getAlertText() {
        try {
            Alert alert = explicitWait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();
            logger.debug("Alert text: {}", alertText);
            return alertText;
        } catch (Exception e) {
            logger.error("Error getting alert text", e);
            throw new RuntimeException("Failed to get alert text", e);
        }
    }

    /**
     * Types text in alert.
     */
    public void typeInAlert(String text) {
        try {
            Alert alert = explicitWait.until(ExpectedConditions.alertIsPresent());
            alert.sendKeys(text);
            logger.debug("Typed text in alert: {}", text);
        } catch (Exception e) {
            logger.error("Error typing in alert", e);
            throw new RuntimeException("Failed to type in alert", e);
        }
    }

    /**
     * Checks if alert is present.
     */
    public boolean isAlertPresent() {
        try {
            explicitWait.until(ExpectedConditions.alertIsPresent());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets alert text without accepting/dismissing.
     */
    public String getAlertTextIfPresent() {
        try {
            if (isAlertPresent()) {
                Alert alert = driver.switchTo().alert();
                return alert.getText();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Accepts alert if present.
     */
    public void acceptAlertIfPresent() {
        try {
            if (isAlertPresent()) {
                Alert alert = driver.switchTo().alert();
                alert.accept();
                logger.debug("Accepted alert");
            }
        } catch (Exception e) {
            logger.debug("No alert to accept");
        }
    }

    // ==================== Frame Methods ====================

    /**
     * Switches to frame by index.
     */
    public void switchToFrame(int index) {
        try {
            driver.switchTo().frame(index);
            logger.debug("Switched to frame with index: {}", index);
        } catch (Exception e) {
            logger.error("Error switching to frame", e);
            throw new RuntimeException("Failed to switch to frame", e);
        }
    }

    /**
     * Switches to frame by name.
     */
    public void switchToFrame(String nameOrId) {
        try {
            driver.switchTo().frame(nameOrId);
            logger.debug("Switched to frame with name/id: {}", nameOrId);
        } catch (Exception e) {
            logger.error("Error switching to frame", e);
            throw new RuntimeException("Failed to switch to frame", e);
        }
    }

    /**
     * Switches to frame by WebElement.
     */
    public void switchToFrame(WebElement element) {
        try {
            driver.switchTo().frame(element);
            logger.debug("Switched to frame element");
        } catch (Exception e) {
            logger.error("Error switching to frame", e);
            throw new RuntimeException("Failed to switch to frame", e);
        }
    }

    /**
     * Switches back from frame to main content.
     */
    public void switchToDefaultContent() {
        try {
            driver.switchTo().defaultContent();
            logger.debug("Switched to default content");
        } catch (Exception e) {
            logger.error("Error switching to default content", e);
            throw new RuntimeException("Failed to switch to default content", e);
        }
    }

    // ==================== Element Existence Methods ====================

    /**
     * Checks if element exists.
     */
    public boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            logger.debug("Element not found: {}", locator);
            return false;
        }
    }

    /**
     * Checks if element is displayed.
     */
    public boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            logger.debug("Element not found");
            return false;
        }
    }

    /**
     * Checks if element is enabled.
     */
    public boolean isElementEnabled(WebElement element) {
        try {
            return element.isEnabled();
        } catch (NoSuchElementException e) {
            logger.debug("Element not found");
            return false;
        }
    }

    // ==================== Hover Methods ====================

    /**
     * Hovers over an element.
     */
    public void hoverOverElement(WebElement element) {
        try {
            explicitWait.until(ExpectedConditions.visibilityOf(element));
            actions.moveToElement(element).perform();
            logger.debug("Hovered over element");
        } catch (Exception e) {
            logger.error("Error hovering over element", e);
            throw new RuntimeException("Failed to hover", e);
        }
    }

    /**
     * Hovers and clicks on an element.
     */
    public void hoverAndClick(WebElement element) {
        try {
            explicitWait.until(ExpectedConditions.visibilityOf(element));
            actions.moveToElement(element).click().perform();
            logger.debug("Hovered and clicked on element");
        } catch (Exception e) {
            logger.error("Error hovering and clicking", e);
            throw new RuntimeException("Failed to hover and click", e);
        }
    }

    // ==================== Drag and Drop Methods ====================

    /**
     * Drags and drops element.
     */
    public void dragAndDrop(WebElement sourceElement, WebElement targetElement) {
        try {
            explicitWait.until(ExpectedConditions.visibilityOf(sourceElement));
            explicitWait.until(ExpectedConditions.visibilityOf(targetElement));
            actions.dragAndDrop(sourceElement, targetElement).perform();
            logger.debug("Dragged and dropped element");
        } catch (Exception e) {
            logger.error("Error dragging and dropping element", e);
            throw new RuntimeException("Failed to drag and drop", e);
        }
    }

    // ==================== Keyboard Methods ====================

    /**
     * Presses key on element.
     */
    public void pressKey(WebElement element, Keys key) {
        try {
            explicitWait.until(ExpectedConditions.visibilityOf(element));
            actions.moveToElement(element).sendKeys(key).perform();
            logger.debug("Pressed key: {}", key);
        } catch (Exception e) {
            logger.error("Error pressing key", e);
            throw new RuntimeException("Failed to press key", e);
        }
    }

    /**
     * Presses Tab key.
     */
    public void pressTab(WebElement element) {
        try {
            explicitWait.until(ExpectedConditions.visibilityOf(element));
            element.sendKeys(Keys.TAB);
            logger.debug("Pressed Tab key");
        } catch (Exception e) {
            logger.error("Error pressing Tab key", e);
            throw new RuntimeException("Failed to press Tab key", e);
        }
    }

    /**
     * Presses Enter key.
     */
    public void pressEnter(WebElement element) {
        try {
            explicitWait.until(ExpectedConditions.visibilityOf(element));
            element.sendKeys(Keys.ENTER);
            logger.debug("Pressed Enter key");
        } catch (Exception e) {
            logger.error("Error pressing Enter key", e);
            throw new RuntimeException("Failed to press Enter key", e);
        }
    }
}
