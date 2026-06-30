package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Duha extends BaseTest {

    // Login Method
    public void StandardLogin() {
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();
    }

    // TC_SRT_01
    @Test
    public void TC_SRT_01() {

        StandardLogin();

        Assert.assertTrue(
                driver.findElement(By.className("product_sort_container")).isDisplayed(),
                "Sorting dropdown should be displayed.");
    }

    // TC_SRT_04
    @Test
    public void TC_SRT_04() {

        StandardLogin();

        Select select = new Select(driver.findElement(By.className("product_sort_container")));
        select.selectByVisibleText("Name (Z to A)");

        String selected =
                new Select(driver.findElement(By.className("product_sort_container")))
                        .getFirstSelectedOption()
                        .getText();

        Assert.assertEquals(selected, "Name (Z to A)");
    }

    // TC_SRT_06
    @Test
    public void TC_SRT_06() {

        StandardLogin();

        Select select = new Select(driver.findElement(By.className("product_sort_container")));
        select.selectByVisibleText("Price (high to low)");

        List<WebElement> prices = driver.findElements(By.className("inventory_item_price"));

        List<Double> actual = new ArrayList<>();

        for (WebElement price : prices) {
            actual.add(Double.parseDouble(price.getText().replace("$", "")));
        }

        List<Double> expected = new ArrayList<>(actual);
        expected.sort(Collections.reverseOrder());

        Assert.assertEquals(actual, expected);
    }

    // TC_SRT_08
    @Test
    public void TC_SRT_08() {

        StandardLogin();

        // Verify Name (A to Z)
        Select select = new Select(driver.findElement(By.className("product_sort_container")));
        select.selectByVisibleText("Name (A to Z)");

        List<WebElement> nameElements = driver.findElements(By.className("inventory_item_name"));

        List<String> actualNames = new ArrayList<>();

        for (WebElement name : nameElements) {
            actualNames.add(name.getText());
        }

        List<String> expectedNames = new ArrayList<>(actualNames);
        Collections.sort(expectedNames);

        Assert.assertEquals(actualNames, expectedNames);

        // Verify Price (high to low)
        select = new Select(driver.findElement(By.className("product_sort_container")));
        select.selectByVisibleText("Price (high to low)");

        List<WebElement> priceElements = driver.findElements(By.className("inventory_item_price"));

        List<Double> actualPrices = new ArrayList<>();

        for (WebElement price : priceElements) {
            actualPrices.add(Double.parseDouble(price.getText().replace("$", "")));
        }

        List<Double> expectedPrices = new ArrayList<>(actualPrices);
        expectedPrices.sort(Collections.reverseOrder());

        Assert.assertEquals(actualPrices, expectedPrices);
    }

    // TC_SRT_10
    @Test
    public void TC_SRT_10() {

        StandardLogin();

        Select select = new Select(driver.findElement(By.className("product_sort_container")));
        select.selectByVisibleText("Price (low to high)");

        List<WebElement> prices = driver.findElements(By.className("inventory_item_price"));

        List<Double> actual = new ArrayList<>();

        for (WebElement price : prices) {
            actual.add(Double.parseDouble(price.getText().replace("$", "")));
        }

        List<Double> expected = new ArrayList<>(actual);
        Collections.sort(expected);

        Assert.assertEquals(actual, expected);
    }
}