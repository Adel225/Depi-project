package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;

public class Tasneem extends BaseTest {

    public void login() {
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));
        usernameField.sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();
        wait.until(ExpectedConditions.urlContains("inventory.html"));
    }

    public void openMenu() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement menuBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("react-burger-menu-btn")));
        js.executeScript("arguments[0].click();", menuBtn);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("logout_sidebar_link")));
    }

    // ==========================================
    // الجزء الأول:  الـ Login
    // ==========================================

    @Test
    public void testValidUserLogin() {
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));
        usernameField.sendKeys("standard_user");

        driver.findElement(By.id("password")).sendKeys("secret_sauce");

        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("login-button")));
        loginButton.click();

        wait.until(ExpectedConditions.urlToBe("https://www.saucedemo.com/inventory.html"));

        String expectedUrl = "https://www.saucedemo.com/inventory.html";
        Assert.assertEquals(driver.getCurrentUrl(), expectedUrl, "لم يتم الانتقال لصفحة المنتجات!");
    }

    @Test
    public void testLockedOutUserLogin() {
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));
        usernameField.sendKeys("locked_out_user");

        driver.findElement(By.id("password")).sendKeys("secret_sauce");

        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("login-button")));
        loginButton.click();

        WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='error']")));
        String errorText = errorElement.getText();

        Assert.assertTrue(errorText.contains("Epic sadface: Sorry, this user has been locked out."));
    }

    @Test
    public void testEmptyUsername() {
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));
        usernameField.sendKeys("");

        driver.findElement(By.id("password")).sendKeys("secret_sauce");

        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("login-button")));
        loginButton.click();

        WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='error']")));
        String errorText = errorElement.getText();

        Assert.assertEquals(errorText, "Epic sadface: Username is required");
    }

    @Test
    public void testInvalidUsername() {
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));
        usernameField.sendKeys("000000000");

        driver.findElement(By.id("password")).sendKeys("secret_sauce");

        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("login-button")));
        loginButton.click();

        WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='error']")));
        String errorText = errorElement.getText();

        Assert.assertEquals(errorText, "Epic sadface: Username and password do not match any user in this service");
    }

    // ==========================================
    // الجزء الثاني:  الـ Logout
    // ==========================================

    @Test
    public void testVerifyMenuButtonOpensNavigationMenu() {
        login();
        openMenu();

        WebElement allItemsOpt = driver.findElement(By.id("inventory_sidebar_link"));
        WebElement aboutOpt = driver.findElement(By.id("about_sidebar_link"));
        WebElement logoutOpt = driver.findElement(By.id("logout_sidebar_link"));
        WebElement resetOpt = driver.findElement(By.id("reset_sidebar_link"));

        Assert.assertTrue(allItemsOpt.isDisplayed(), "All Items option should be displayed");
        Assert.assertTrue(aboutOpt.isDisplayed(), "About option should be displayed");
        Assert.assertTrue(logoutOpt.isDisplayed(), "Logout option should be displayed");
        Assert.assertTrue(resetOpt.isDisplayed(), "Reset App State option should be displayed");
    }

    @Test
    public void testVerifyClickingLogoutRedirectsToLoginPage() {
        login();
        openMenu();

        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement logoutLink = driver.findElement(By.id("logout_sidebar_link"));
        js.executeScript("arguments[0].click();", logoutLink);

        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("inventory.html")));

        String baseUrl = "https://www.saucedemo.com/";
        Assert.assertTrue(driver.getCurrentUrl().equals(baseUrl) || driver.getCurrentUrl().equals("https://www.saucedemo.com"));
        Assert.assertTrue(driver.findElement(By.id("user-name")).isDisplayed());
    }

    @Test
    public void testVerifyDirectUrlAccessPostLogoutBlocked() {
        login();
        openMenu();

        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement logoutLink = driver.findElement(By.id("logout_sidebar_link"));
        js.executeScript("arguments[0].click();", logoutLink);
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("inventory.html")));

        driver.get("https://www.saucedemo.com/inventory.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='error']")));
        WebElement errorContainer = driver.findElement(By.cssSelector("[data-test='error']"));

        Assert.assertTrue(errorContainer.getText().contains("You can only access '/inventory.html' when you are logged in"));
    }

    @Test
    public void testVerifyLogoutWorksThroughMultiplePages() {
        login();

        // التنقل المستقر بين الصفحات عبر الـ Navigation دون الحاجة لفتح نافذة جديدة تتعارض مع الـ Guest Mode
        driver.get("https://www.saucedemo.com/cart.html");
        wait.until(ExpectedConditions.urlContains("cart.html"));

        driver.get("https://www.saucedemo.com/checkout-step-one.html");
        wait.until(ExpectedConditions.urlContains("checkout-step-one.html"));

        openMenu();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement logoutLink = driver.findElement(By.id("logout_sidebar_link"));
        js.executeScript("arguments[0].click();", logoutLink);

        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("checkout")));

        String expectedUrl = "https://www.saucedemo.com/";
        Assert.assertTrue(driver.getCurrentUrl().equals(expectedUrl) || driver.getCurrentUrl().equals("https://www.saucedemo.com"));
        Assert.assertTrue(driver.findElement(By.id("user-name")).isDisplayed());
    }
}