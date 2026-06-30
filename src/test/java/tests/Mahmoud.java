package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

public class Mahmoud extends BaseTest { // Remove from Cart===>

    public void StandardLogin() {
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();
    }

    public void addItemsToCart(int count) {
        String[] items = {
                "add-to-cart-sauce-labs-backpack",
                "add-to-cart-sauce-labs-bike-light",
                "add-to-cart-sauce-labs-bolt-t-shirt",
                "add-to-cart-sauce-labs-fleece-jacket",
                "add-to-cart-sauce-labs-onesie",
                "add-to-cart-test.allthethings()-t-shirt-(red)"
        };

        for (int i = 0; i < count; i++) {
            driver.findElement(By.id(items[i])).click();
        }
    }

    // Test Case 1
    // Verify removing one item from cart containing four items
    @Test
    public void VerifyRemovingSingleItemFromFourItemsCart() {

        StandardLogin();
        addItemsToCart(4);

        driver.findElement(By.id("remove-sauce-labs-backpack")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("shopping_cart_badge")));

        String badge = driver.findElement(By.className("shopping_cart_badge")).getText();
        Assert.assertEquals(badge, "3");

        String buttonText = driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).getText();
        Assert.assertEquals(buttonText, "Add to cart");
    }

    // Test Case 3
    // Verify cart badge changes from 6 to 5 after removing one item
    @Test
    public void VerifyCartIconTransitionFromSixToFive() {

        StandardLogin();
        addItemsToCart(6);

        driver.findElement(By.id("remove-sauce-labs-backpack")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("shopping_cart_badge")));

        String badge = driver.findElement(By.className("shopping_cart_badge")).getText();
        Assert.assertEquals(badge, "5");
    }

    // Test Case 8
    // Verify remove button is unavailable when cart is empty
    @Test
    public void VerifyRemovingUnFunctionalityAtZeroItem() {

        StandardLogin();

        Assert.assertTrue(
                driver.findElements(By.xpath("//button[text()='Remove']")).isEmpty(),
                "Remove button should not exist when cart is empty"
        );
    }

    // Test Case 9
    // Verify removing item directly from cart page
    @Test
    public void VerifyRemovingItemFromCartPageDirectly() {

        StandardLogin();
        addItemsToCart(1);

        driver.findElement(By.className("shopping_cart_link")).click();

        wait.until(ExpectedConditions.elementToBeClickable(By.id("remove-sauce-labs-backpack")));

        driver.findElement(By.id("remove-sauce-labs-backpack")).click();

        Assert.assertTrue(
                driver.findElements(By.className("cart_item")).isEmpty(),
                "Item should disappear from cart"
        );

        Assert.assertTrue(
                driver.findElements(By.className("shopping_cart_badge")).isEmpty(),
                "Cart badge should disappear"
        );
    }

    // Test Case 10
    // Verify removing one item keeps remaining items in cart
    @Test
    public void VerifyRemovingOneItemKeepsOtherIntactInCart() {

        StandardLogin();
        addItemsToCart(2);

        driver.findElement(By.className("shopping_cart_link")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("cart_item")));

        driver.findElement(By.id("remove-sauce-labs-backpack")).click();

        Assert.assertEquals(
                driver.findElements(By.className("cart_item")).size(),
                1
        );

        String itemName = driver.findElement(By.className("inventory_item_name")).getText();
        Assert.assertNotEquals(itemName, "Sauce Labs Backpack");
    }

    // Test Case 11
    // Verify Add to Cart button appears after removing item
    @Test
    public void VerifyButtonUIStateChangeUponRemovingFromInventory() {

        StandardLogin();
        addItemsToCart(1);

        driver.findElement(By.id("remove-sauce-labs-backpack")).click();

        String buttonText = driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).getText();

        Assert.assertEquals(buttonText, "Add to cart");
    }

    // Test Case 13
    // Verify cart becomes empty after removing the only item
    @Test
    public void VerifyEmptyCartStateAfterRemoval() {

        StandardLogin();
        addItemsToCart(1);

        driver.findElement(By.className("shopping_cart_link")).click();

        wait.until(ExpectedConditions.elementToBeClickable(By.id("remove-sauce-labs-backpack")));

        driver.findElement(By.id("remove-sauce-labs-backpack")).click();

        Assert.assertTrue(
                driver.findElements(By.className("cart_item")).isEmpty(),
                "Cart should be empty"
        );
    }

    // Test Case 15
    // Verify removed item does not reappear after navigation
    @Test
    public void VerifyRemovalPersistenceAfterNavigation() {

        StandardLogin();
        addItemsToCart(1);

        driver.findElement(By.id("remove-sauce-labs-backpack")).click();

        driver.findElement(By.className("shopping_cart_link")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("cart_list")));

        Assert.assertTrue(
                driver.findElements(By.className("cart_item")).isEmpty(),
                "Removed item should not appear after navigation"
        );
    }
}