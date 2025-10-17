import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class CartTests {
    WebDriver driver;
    String baseUrl = "https://www.saucedemo.com/";

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(baseUrl);

        // Login with valid credentials
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "Login failed!");
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void addSingleItemToCart() {
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();

        // Verify cart badge count
        WebElement cartBadge = driver.findElement(By.className("shopping_cart_badge"));
        Assert.assertEquals(cartBadge.getText(), "1", "Cart count is incorrect!");

        // Verify product appears in cart page
        driver.findElement(By.className("shopping_cart_link")).click();
        String itemName = driver.findElement(By.className("inventory_item_name")).getText();
        Assert.assertEquals(itemName, "Sauce Labs Backpack", "Wrong item in cart!");
    }

    @Test
    public void addMultipleItemsToCart() {
        driver.findElement(By.id("add-to-cart-sauce-labs-bike-light")).click();
        driver.findElement(By.id("add-to-cart-sauce-labs-bolt-t-shirt")).click();
        driver.findElement(By.id("add-to-cart-sauce-labs-fleece-jacket")).click();

        WebElement cartBadge = driver.findElement(By.className("shopping_cart_badge"));
        Assert.assertEquals(cartBadge.getText(), "3", "Cart count is incorrect!");

        driver.findElement(By.className("shopping_cart_link")).click();
        List<WebElement> items = driver.findElements(By.className("cart_item"));
        Assert.assertEquals(items.size(), 3, "Not all items were added to the cart!");
    }

    @Test
    public void removeItemFromCart() {
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        driver.findElement(By.className("shopping_cart_link")).click();

        driver.findElement(By.id("remove-sauce-labs-backpack")).click();
        List<WebElement> items = driver.findElements(By.className("cart_item"));
        Assert.assertEquals(items.size(), 0, "Item was not removed from the cart!");
    }

    @Test
    public void verifyCartBadgeDisappears() {
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        driver.findElement(By.id("add-to-cart-sauce-labs-bike-light")).click();

        WebElement cartBadge = driver.findElement(By.className("shopping_cart_badge"));
        Assert.assertEquals(cartBadge.getText(), "2");

        driver.findElement(By.className("shopping_cart_link")).click();
        driver.findElement(By.id("remove-sauce-labs-backpack")).click();
        driver.findElement(By.id("remove-sauce-labs-bike-light")).click();

        List<WebElement> badges = driver.findElements(By.className("shopping_cart_badge"));
        Assert.assertTrue(badges.isEmpty(), "Cart badge should disappear when empty!");
    }

    @Test
    public void continueShoppingFromCart() {
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        driver.findElement(By.className("shopping_cart_link")).click();

        driver.findElement(By.id("continue-shopping")).click();
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "Did not return to inventory page!");
    }

    @Test
    public void proceedToCheckout() {
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        driver.findElement(By.className("shopping_cart_link")).click();

        driver.findElement(By.id("checkout")).click();
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-one"), "Checkout page not opened!");
    }
}
