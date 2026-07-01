package tests;

import Utilities.Utility;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class Mohamed {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        // FIX: ماتخلطش implicit مع explicit wait - بيسبب سلوك غير متوقع وفشل عشوائي
        // شيلنا implicitlyWait خالص واعتمدنا بالكامل على WebDriverWait (explicit)
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("https://www.saucedemo.com/");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Locators
    By usernameField = By.id("user-name");
    By passwordField = By.id("password");
    By loginButton = By.id("login-button");
    By errorMessage = By.cssSelector("h3[data-test='error']");

    By addBackpackButton = By.id("add-to-cart-sauce-labs-backpack");
    By removeBackpackButton = By.id("remove-sauce-labs-backpack");

    By addBikeLightButton = By.id("add-to-cart-sauce-labs-bike-light");
    By removeBikeLightButton = By.id("remove-sauce-labs-bike-light");
    By bikeLightName = By.xpath("//div[@class='inventory_item'][.//*[@id='add-to-cart-sauce-labs-bike-light' or @id='remove-sauce-labs-bike-light']]//div[@class='inventory_item_name']");
    By bikeLightPrice = By.xpath("//div[@class='inventory_item'][.//*[@id='add-to-cart-sauce-labs-bike-light' or @id='remove-sauce-labs-bike-light']]//div[@class='inventory_item_price']");

    By cartBadge = By.className("shopping_cart_badge");
    By cartIcon = By.className("shopping_cart_link");

    By backpackTitleLink = By.id("item_4_title_link");
    By detailsAddToCartButton = By.id("add-to-cart-sauce-labs-backpack");
    By backToProductsButton = By.id("back-to-products");

    By cartItemName = By.className("inventory_item_name");
    By cartItemPrice = By.className("inventory_item_price");

    private void login(String username, String password) {
        Utility.sendKey(usernameField, username, driver);
        Utility.sendKey(passwordField, password, driver);
        Utility.click(loginButton, driver);
    }

    // FIX: helper جديد بيستنى الـ badge يظهر فعلياً بالقيمة المطلوبة قبل ما يعمل assert
    // ده بيحل مشكلة الـ race condition في أكتر من تيست
    private void waitForCartBadgeText(String expectedText, int timeoutSeconds) {
        WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        localWait.until(ExpectedConditions.textToBe(cartBadge, expectedText));
    }

    // FIX: helper بيستنى الـ badge يختفي (يعتمد على عدم وجود العنصر) بدل findElements المباشر
    private void waitForCartBadgeToDisappear(int timeoutSeconds) {
        WebDriverWait localWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        localWait.until(ExpectedConditions.numberOfElementsToBe(cartBadge, 0));
    }

    @Test
    public void verifyAddSingleItemToCart() {
        login("standard_user", "secret_sauce");
        Utility.click(addBackpackButton, driver);

        waitForCartBadgeText("1", 10); // FIX: استنى قبل ما تتأكد
        Assert.assertEquals(Utility.getText(cartBadge, driver), "1");
        Assert.assertTrue(Utility.elementIsDisplayed(removeBackpackButton, driver));
    }

    @Test
    public void TC_ADD_05() {
        login("standard_user", "secret_sauce");
        Assert.assertFalse(driver.findElements(cartBadge).size() > 0);
        Utility.click(cartIcon, driver);
        Assert.assertFalse(driver.findElements(cartBadge).size() > 0);
    }

    @Test
    public void TC_ADD_16() {
        login("locked_out_user", "secret_sauce");
        wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)); // FIX
        Assert.assertEquals(
                Utility.getText(errorMessage, driver),
                "Epic sadface: Sorry, this user has been locked out."
        );
    }

    @Test
    public void TC_ADD_18() {
        login("performance_glitch_user", "secret_sauce");
        Utility.click(addBackpackButton, driver);

        // FIX: performance_glitch_user بطيء جداً - بناخد وقت أطول (15-20 ثانية)
        // بدل ما نعتمد على الـ 5 ثواني الافتراضية اللي بتفشل دايماً مع اليوزر ده
        waitForCartBadgeText("1", 20);

        Assert.assertEquals(Utility.getText(cartBadge, driver), "1");
        Assert.assertTrue(Utility.elementIsDisplayed(removeBackpackButton, driver));
    }

    @Test
    public void TC_ADD_INT_01() {
        login("standard_user", "secret_sauce");

        // FIX: استنى ظهور عناصر المنتج الأول قبل قراءتها (الصفحة لسه بتحمل)
        wait.until(ExpectedConditions.visibilityOfElementLocated(bikeLightName));

        String expectedName = Utility.getText(bikeLightName, driver);
        String expectedPrice = Utility.getText(bikeLightPrice, driver);

        Utility.click(addBikeLightButton, driver);
        Utility.click(cartIcon, driver);

        wait.until(ExpectedConditions.visibilityOfElementLocated(cartItemName)); // FIX

        Assert.assertEquals(Utility.getText(cartItemName, driver), expectedName);
        Assert.assertEquals(Utility.getText(cartItemPrice, driver), expectedPrice);
    }

    @Test
    public void TC_ADD_INT_03() {
        login("standard_user", "secret_sauce");
        Utility.click(backpackTitleLink, driver);

        // FIX: استنى صفحة التفاصيل تحمل كويس قبل الدوس على الزرار
        wait.until(ExpectedConditions.elementToBeClickable(detailsAddToCartButton));
        Utility.click(detailsAddToCartButton, driver);

        waitForCartBadgeText("1", 10); // FIX
        Assert.assertEquals(Utility.getText(cartBadge, driver), "1");

        Utility.click(backToProductsButton, driver);

        waitForCartBadgeText("1", 10); // FIX: استنى تاني بعد التنقل
        Assert.assertEquals(Utility.getText(cartBadge, driver), "1");
    }

    @Test
    public void TC_ADD_INT_08() {
        login("standard_user", "secret_sauce");
        Utility.click(addBackpackButton, driver);
        Utility.click(addBikeLightButton, driver);

        // FIX حقيقي: اتنين عنصر = "2" مش "10"!! ده كان باج منطقي في التيست نفسه
        waitForCartBadgeText("2", 10);
        Assert.assertEquals(Utility.getText(cartBadge, driver), "2");

        driver.navigate().refresh();

        waitForCartBadgeText("2", 10); // FIX: استنى بعد الـ refresh
        Assert.assertEquals(Utility.getText(cartBadge, driver), "2");
        Assert.assertTrue(Utility.elementIsDisplayed(removeBackpackButton, driver));
        Assert.assertTrue(Utility.elementIsDisplayed(removeBikeLightButton, driver));
    }

    @Test
    public void verifyCartBadgeTransitionFromZeroToOne() {
        login("standard_user", "secret_sauce");

        Assert.assertFalse(driver.findElements(cartBadge).size() > 0);

        Utility.click(addBackpackButton, driver);

        // FIX: ده كان السبب الأساسي للفشل - findElements بترجع فوراً من غير انتظار
        // استخدمنا WebDriverWait صريح بدل ما نعتمد على findElements المباشر
        wait.until(ExpectedConditions.visibilityOfElementLocated(cartBadge));

        Assert.assertTrue(driver.findElements(cartBadge).size() > 0);
        Assert.assertEquals(Utility.getText(cartBadge, driver), "1");
    }
}