package tests;

import Utilities.Utility;
import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Yousef extends BaseTest {

    WebDriver driver;

    By usernameField    = By.id("user-name");
    By passwordField    = By.id("password");
    By loginButton      = By.id("login-button");
    By addToCartButton  = By.className("btn_inventory");
    By cartBadge        = By.className("shopping_cart_badge");
    By inventoryItems   = By.className("inventory_item");
    By detailImage      = By.className("inventory_details_img");


    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) driver.quit();
    }

    private void login(String username, String password) {
        driver.get("https://www.saucedemo.com/");
        Utility.sendKey(usernameField, username, driver);
        Utility.sendKey(passwordField, password, driver);
        Utility.click(loginButton, driver);
    }

    private void openProductDetailByName(String productName) {
        List<WebElement> items = driver.findElements(inventoryItems);
        for (WebElement item : items) {
            String name = item.findElement(By.className("inventory_item_name")).getText();
            if (name.equalsIgnoreCase(productName)) {
                item.findElement(By.className("inventory_item_name")).click();
                return;
            }
        }
        throw new RuntimeException("Product not found: " + productName);
    }

    private void openProductByName(String partialName) {
        List<WebElement> items = driver.findElements(inventoryItems);
        for (WebElement item : items) {
            String name = item.findElement(By.className("inventory_item_name")).getText();
            if (name.toLowerCase().contains(partialName.toLowerCase())) {
                item.findElement(By.className("inventory_item_img")).click();
                return;
            }
        }
        throw new RuntimeException("Product not found: " + partialName);
    }

    private boolean isImageRendered(WebElement imgElement) {
        Object width = ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].naturalWidth;", imgElement);
        return width != null && ((Long) width) > 0;
    }

    private boolean isImageUrlReachable(String imgSrc) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(imgSrc).openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    public void AddToCartButtonVisibleOnDetailPage() {
        login("standard_user", "secret_sauce");
        openProductDetailByName("Sauce Labs Onesie");

        WebElement button = driver.findElement(addToCartButton);

        Assert.assertTrue(button.isDisplayed(),
                "Add to Cart button is not visible on the detail page");
        Assert.assertTrue(button.isEnabled(),
                "Add to Cart button is not enabled");
        Assert.assertEquals(button.getText(), "Add to cart",
                "Button label does not read 'Add to cart'");
    }

    @Test
    public void AddToCartButtonEnabledAndClickable() {
        login("standard_user", "secret_sauce");
        openProductDetailByName("Sauce Labs Backpack");

        WebElement button = driver.findElement(addToCartButton);

        Assert.assertNull(button.getAttribute("disabled"),
                "Button has a 'disabled' attribute — it is not clickable");

        button.click();

        WebElement updatedButton = driver.findElement(addToCartButton);
        Assert.assertEquals(updatedButton.getText(), "Remove",
                "Button label did not change after clicking Add to Cart");
        Assert.assertEquals(driver.findElement(cartBadge).getText(), "1",
                "Cart badge did not update after clicking Add to Cart");
    }

    @Test
    public void ButtonChangesToRemoveAfterAddToCart() {
        login("standard_user", "secret_sauce");
        openProductDetailByName("Sauce Labs Bolt T-Shirt");

        Assert.assertEquals(driver.findElement(addToCartButton).getText(), "Add to cart",
                "Button did not start with 'Add to cart' label");

        driver.findElement(addToCartButton).click();

        Assert.assertEquals(driver.findElement(addToCartButton).getText(), "Remove",
                "Button label did not change to 'Remove' after clicking Add to Cart");
    }

    @Test
    public void CartBadgeIncrementsToOneAfterAddToCart() {
        login("standard_user", "secret_sauce");
        openProductDetailByName("Sauce Labs Backpack");

        Assert.assertTrue(driver.findElements(cartBadge).isEmpty(),
                "Cart badge is showing before any item was added");

        driver.findElement(addToCartButton).click();

        Assert.assertEquals(driver.findElement(cartBadge).getText(), "1",
                "Cart badge did not appear or does not show '1' after adding one item");
    }

    @Test
    public void ProductImageHasAltAttribute() {
        login("standard_user", "secret_sauce");
        openProductByName("Sauce Labs Onesie");

        WebElement img = driver.findElement(detailImage);
        String alt = img.getAttribute("alt");

        Assert.assertNotNull(alt,
                "Image alt attribute is null — accessibility requirement not met");
        Assert.assertFalse(alt.trim().isEmpty(),
                "Image alt attribute is empty — should be descriptive");
    }

    @Test
    public void ProductImageLoadsWithoutBrokenIcon() {
        login("standard_user", "secret_sauce");
        openProductByName("Sauce Labs Bike Light");

        WebElement img = driver.findElement(detailImage);
        String src = img.getAttribute("src");

        Assert.assertNotNull(src, "Image src attribute is null");
        Assert.assertFalse(src.isEmpty(), "Image src attribute is empty");
        Assert.assertTrue(isImageRendered(img),
                "Image has naturalWidth = 0, it may be broken");
        Assert.assertTrue(isImageUrlReachable(src),
                "Image URL returned non-200: " + src);
    }

    @Test
    public void LowestPricedProductImageDisplaysCorrectly() {
        login("standard_user", "secret_sauce");
        openProductByName("Sauce Labs Bike Light");

        WebElement img = driver.findElement(detailImage);
        String src = img.getAttribute("src");

        Assert.assertTrue(Utility.elementIsDisplayed(detailImage, driver),
                "Bike Light detail image is not displayed");
        Assert.assertTrue(isImageRendered(img),
                "Bike Light image has naturalWidth = 0 — may be broken or distorted");
        Assert.assertNotNull(src, "Image src is null");
        Assert.assertFalse(src.isEmpty(), "Image src is empty");
    }

    @Test
    public void HighestPricedProductImageDisplaysCorrectly() {
        login("standard_user", "secret_sauce");
        openProductByName("Sauce Labs Fleece Jacket");

        WebElement img = driver.findElement(detailImage);
        String src = img.getAttribute("src");

        Assert.assertTrue(Utility.elementIsDisplayed(detailImage, driver),
                "Fleece Jacket detail image is not displayed");
        Assert.assertTrue(isImageRendered(img),
                "Fleece Jacket image has naturalWidth = 0 — may be broken or distorted");
        Assert.assertNotNull(src, "Image src is null");
        Assert.assertFalse(src.isEmpty(), "Image src is empty");
    }
}