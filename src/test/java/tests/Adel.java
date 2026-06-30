package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

public class Adel extends BaseTest {


    public void StandardLogin() {
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();
    }
    public void goToCheckoutForm() {
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        driver.findElement(By.cssSelector("[data-test='shopping-cart-link']")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-test='checkout']")));
        driver.findElement(By.cssSelector("[data-test='checkout']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("first-name")));
    }
    public void fillForm(String firstName, String lastName, String postalCode) {
        driver.findElement(By.id("first-name")).sendKeys(firstName);
        driver.findElement(By.id("last-name")).sendKeys(lastName);
        driver.findElement(By.id("postal-code")).sendKeys(postalCode);
        driver.findElement(By.cssSelector("[data-test='continue']")).click();
    }


//1. Stable internet connection
//2. Latest browser version
//3. User is logged in as standard_user
//4. Cart has at least 1 item
//5. Checkout: Your Information step completed
@Test
    public void OrderedItemsInOverviewPage() {
        StandardLogin();
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        driver.findElement(By.cssSelector("[data-test='shopping-cart-link']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='shopping-cart-link']")));
        boolean itemVisible = driver.findElement(By.className("inventory_item_name")).isDisplayed();
        Assert.assertTrue(itemVisible, "Cart item should be visible in cart page");
}


//1. Stable internet connection
//2. Latest browser version
//3. User is logged in as standard_user
//4. Cart has at least 1 item
//5. Checkout: Your Information step completed
@Test
    public void PaymentInfoDisplayedInOverviewPage() {
        StandardLogin();
        goToCheckoutForm();
        fillForm("Adel", "Sameh", "11829");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("summary_value_label")));
        boolean paymentVisible = driver.findElement(By.className("summary_value_label")).isDisplayed();
        Assert.assertTrue(paymentVisible, "Payment info should be displayed on overview page");

    // Observe the item list on the Overview page
    }

//1. Stable internet connection
//2. Latest browser version
//3. User is logged in as standard_user
//4. Cart has at least 1 item
//5. Checkout: Your Information step completed
@Test
    public void ItemTotalEqualToSumOfAllPrices() {
        StandardLogin();
        goToCheckoutForm();
        fillForm("Adel", "Sameh", "11829");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("summary_subtotal_label")));
        String itemTotalText = driver.findElement(By.className("summary_subtotal_label")).getText();
        // itemTotalText looks like "Item total: $29.99"
        double itemTotal = Double.parseDouble(itemTotalText.replace("Item total: $", "").trim());

        double sumOfPrices = 0;
        for (WebElement price : driver.findElements(By.className("inventory_item_price"))) {
            sumOfPrices += Double.parseDouble(price.getText().replace("$", "").trim());
        }

        Assert.assertEquals(itemTotal, sumOfPrices, "Item total should equal sum of all item prices");
    }

//1. Stable internet connection
//2. Latest browser version
//3. User is logged in as standard_user
//4. Cart has at least 1 item
//5. Checkout: Your Information step completed
@Test
    public void ChackSuccessMessageAfterClickingFinish() {
        StandardLogin();
        goToCheckoutForm();
        // fill form data
        fillForm("Adel", "Sameh", "11829");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("finish")));
        driver.findElement(By.id("finish")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("checkout_complete_container")));
        String successMsg = driver.findElement(By.className("complete-header")).getText();
        Assert.assertEquals(successMsg, "Thank you for your order!", "Success message should appear after finishing order");
    }

//1-Stabel Internet
//2)Latest Website is opened
//3)User is the loggedin using the Testdata
@Test
    public void VerifyFirstNameAcceptsInputCorrectly() {
        StandardLogin();
        goToCheckoutForm();
        fillForm("Adel", "Sameh", "11829");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("summary_info")));
        boolean overviewLoaded = driver.findElement(By.className("summary_info")).isDisplayed();
        Assert.assertTrue(overviewLoaded, "Valid first name should proceed to overview page");
    }

//1-Stabel Internet
//2)Latest Website is opened
//3)User is the loggedin using the Testdata
@Test
    public void VerifyLeavingFirstNameEmpty() {
        StandardLogin();
        goToCheckoutForm();
        fillForm("", "Sameh", "11829");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='error']")));
        String errorMsg = driver.findElement(By.cssSelector("[data-test='error']")).getText();
        Assert.assertTrue(errorMsg.contains("First Name is required"), "Error should appear when first name is empty");
    }

//1-Stabel Internet
//2)Latest Website is opened
//3)User is the loggedin using the Testdata
@Test
    public void VerifyAddingnOneNumberInFirstName() {
        StandardLogin();
        goToCheckoutForm();
        fillForm("a1aa", "Sameh", "11829");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("summary_info")));
        boolean overviewLoaded = driver.findElement(By.className("summary_info")).isDisplayed();
        Assert.assertTrue(overviewLoaded, "Alphanumeric first name should be accepted");
    }

//1-Stabel Internet
//2)Latest Website is opened
//3)User is the loggedin using the Testdata
@Test
    public void VerifyAlphanumericPostalCode() {
        StandardLogin();
        goToCheckoutForm();
        fillForm("Adel", "Sameh", "K1A 0B1");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("summary_info")));
        boolean overviewLoaded = driver.findElement(By.className("summary_info")).isDisplayed();
        Assert.assertTrue(overviewLoaded, "Alphanumeric postal code should be accepted");
    }
}
