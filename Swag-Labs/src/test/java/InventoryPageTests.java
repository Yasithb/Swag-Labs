import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class InventoryPageTests {

    WebDriver driver;
    String baseUrl = "https://www.saucedemo.com/";

    @BeforeMethod
    public void loginAndOpenInventory() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(baseUrl);

        // Login with valid credentials
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // Verify login successful
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "Login failed!");
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void verifyInventoryPageLoads() {
        // Example test case
        String title = driver.getTitle();
        System.out.println("Page title: " + title);

        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "Inventory page not loaded!");
    }

    @Test
    public void verifyInventoryItemsDisplayed() {
        // Check that inventory items are visible
        List<WebElement> items = driver.findElements(By.className("inventory_item"));
        Assert.assertTrue(items.size() > 0, "No inventory items found!");
    }

    @Test
    public void verifyItemNamesAndPricesVisible() {
        // Verify each item has a name and price
        List<WebElement> names = driver.findElements(By.className("inventory_item_name"));
        List<WebElement> prices = driver.findElements(By.className("inventory_item_price"));

        Assert.assertEquals(names.size(), prices.size(), "Mismatch between names and prices count!");
        for (int i = 0; i < names.size(); i++) {
            Assert.assertFalse(names.get(i).getText().isEmpty(), "Item name missing!");
            Assert.assertTrue(prices.get(i).getText().contains("$"), "Price not visible!");
        }
    }

    @Test
    public void verifyAddToCartButtonsWork() {
        WebElement firstAddToCart = driver.findElement(By.xpath("(//button[contains(@id,'add-to-cart')])[1]"));
        firstAddToCart.click();

        // Verify button text changes to "Remove"
        WebElement removeBtn = driver.findElement(By.xpath("(//button[contains(@id,'remove')])[1]"));
        Assert.assertTrue(removeBtn.isDisplayed(), "Add to Cart button did not change to Remove!");
    }

    @Test
    public void verifyCartBadgeIncreasesOnAddingItems() {
        // Add first two products
        driver.findElements(By.xpath("//button[contains(@id,'add-to-cart')]")).get(0).click();
        driver.findElements(By.xpath("//button[contains(@id,'add-to-cart')]")).get(1).click();

        // Check the cart badge count
        WebElement cartBadge = driver.findElement(By.className("shopping_cart_badge"));
        Assert.assertEquals(cartBadge.getText(), "2", "Cart badge count incorrect!");
    }

    @Test
    public void verifyNavigationToProductDetails() {
        WebElement firstItem = driver.findElement(By.className("inventory_item_name"));
        String expectedName = firstItem.getText();
        firstItem.click();

        // Verify details page shows correct name
        WebElement detailTitle = driver.findElement(By.className("inventory_details_name"));
        Assert.assertEquals(detailTitle.getText(), expectedName, "Product details page name mismatch!");
    }


}
