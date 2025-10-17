import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CheckoutTests {
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

        // Add an item to the cart
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        driver.findElement(By.className("shopping_cart_link")).click();
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    // Test 1: Verify checkout page navigation
    @Test
    public void verifyCheckoutPageNavigation() {
        driver.findElement(By.id("checkout")).click();
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-one"),
                "User not redirected to checkout info page!");
    }

    @Test
    public void verifyErrorMessagesForMissingFields() {
        driver.findElement(By.id("checkout")).click();

        // Leave all fields blank
        driver.findElement(By.id("continue")).click();
        String errorMsg = driver.findElement(By.cssSelector("h3[data-test='error']")).getText();
        Assert.assertEquals(errorMsg, "Error: First Name is required");

        // Fill first name only
        driver.findElement(By.id("first-name")).sendKeys("John");
        driver.findElement(By.id("continue")).click();
        errorMsg = driver.findElement(By.cssSelector("h3[data-test='error']")).getText();
        Assert.assertEquals(errorMsg, "Error: Last Name is required");

        // Fill last name but no postal code
        driver.findElement(By.id("last-name")).sendKeys("Doe");
        driver.findElement(By.id("continue")).click();
        errorMsg = driver.findElement(By.cssSelector("h3[data-test='error']")).getText();
        Assert.assertEquals(errorMsg, "Error: Postal Code is required");
    }

    @Test
    public void verifyCheckoutOverviewPage() {
        driver.findElement(By.id("checkout")).click();

        driver.findElement(By.id("first-name")).sendKeys("John");
        driver.findElement(By.id("last-name")).sendKeys("Doe");
        driver.findElement(By.id("postal-code")).sendKeys("10001");
        driver.findElement(By.id("continue")).click();

        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-two"),
                "User not redirected to overview page!");
    }

    @Test
    public void verifyItemDetailsAndTotal() {
        driver.findElement(By.id("checkout")).click();

        driver.findElement(By.id("first-name")).sendKeys("John");
        driver.findElement(By.id("last-name")).sendKeys("Doe");
        driver.findElement(By.id("postal-code")).sendKeys("10001");
        driver.findElement(By.id("continue")).click();

        // Verify product name
        String productName = driver.findElement(By.className("inventory_item_name")).getText();
        Assert.assertEquals(productName, "Sauce Labs Backpack", "Product name mismatch!");

        // Verify price label presence
        WebElement itemPrice = driver.findElement(By.className("inventory_item_price"));
        Assert.assertTrue(itemPrice.isDisplayed(), "Item price not visible!");

        // Verify total summary section
        WebElement summaryTotal = driver.findElement(By.className("summary_total_label"));
        Assert.assertTrue(summaryTotal.getText().contains("Total"), "Total price section missing!");
    }

    @Test
    public void verifyOrderCompletion() {
        driver.findElement(By.id("checkout")).click();

        driver.findElement(By.id("first-name")).sendKeys("John");
        driver.findElement(By.id("last-name")).sendKeys("Doe");
        driver.findElement(By.id("postal-code")).sendKeys("10001");
        driver.findElement(By.id("continue")).click();

        driver.findElement(By.id("finish")).click();

        // Verify success message
        String successHeader = driver.findElement(By.className("complete-header")).getText();
        Assert.assertEquals(successHeader, "Thank you for your order!", "Order success message not displayed!");

        // Verify redirection to confirmation page
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-complete"),
                "Did not navigate to order confirmation page!");
    }

    @Test
    public void verifyBackHomeButton() {
        driver.findElement(By.id("checkout")).click();

        driver.findElement(By.id("first-name")).sendKeys("John");
        driver.findElement(By.id("last-name")).sendKeys("Doe");
        driver.findElement(By.id("postal-code")).sendKeys("10001");
        driver.findElement(By.id("continue")).click();

        driver.findElement(By.id("finish")).click();
        driver.findElement(By.id("back-to-products")).click();

        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "Back Home button not working!");
    }
}
