import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class AdvancedTests {
    WebDriver driver;
    String baseUrl = "https://www.saucedemo.com/";

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get(baseUrl);
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void invalidLoginTest() {
        driver.findElement(By.id("user-name")).sendKeys("invalid_user");
        driver.findElement(By.id("password")).sendKeys("wrong_password");
        driver.findElement(By.id("login-button")).click();

        WebElement error = driver.findElement(By.cssSelector("h3[data-test='error']"));
        Assert.assertTrue(error.isDisplayed(), "Error message not displayed for invalid login!");
        Assert.assertTrue(error.getText().contains("Username and password do not match"),
                "Unexpected error message shown!");
    }

    @Test
    public void emptyCredentialsTest() {
        driver.findElement(By.id("login-button")).click();
        WebElement error = driver.findElement(By.cssSelector("h3[data-test='error']"));
        Assert.assertTrue(error.isDisplayed(), "Error not displayed when credentials are empty!");
    }

    @Test
    public void longInputValuesTest() {
        String longString = "a".repeat(500);
        driver.findElement(By.id("user-name")).sendKeys(longString);
        driver.findElement(By.id("password")).sendKeys(longString);
        driver.findElement(By.id("login-button")).click();

        WebElement error = driver.findElement(By.cssSelector("h3[data-test='error']"));
        Assert.assertTrue(error.isDisplayed(), "Error not displayed for long input values!");
    }

    @Test
    public void specialCharacterInputTest() {
        driver.findElement(By.id("user-name")).sendKeys("!@#$%^&*()");
        driver.findElement(By.id("password")).sendKeys("<script>alert('test')</script>");
        driver.findElement(By.id("login-button")).click();

        WebElement error = driver.findElement(By.cssSelector("h3[data-test='error']"));
        Assert.assertTrue(error.isDisplayed(), "Error not displayed for special characters!");
    }

    @Test
    public void verifyLoginPerformance() {
        long startTime = System.currentTimeMillis();

        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("inventory"));

        long endTime = System.currentTimeMillis();
        long loadTime = endTime - startTime;

        System.out.println("Login load time: " + loadTime + " ms");
        Assert.assertTrue(loadTime < 5000, "Login took too long (over 5 seconds)!");
    }

    @Test
    public void verifyAddToCartPerformance() {
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        long start = System.currentTimeMillis();
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        long end = System.currentTimeMillis();

        System.out.println("Add to cart response time: " + (end - start) + " ms");
        Assert.assertTrue((end - start) < 2000, "Add to cart action is too slow!");
    }

    @Test
    public void verifyNoJavaScriptInjection() {
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("<script>alert('Injected!')</script>");
        driver.findElement(By.id("login-button")).click();

        boolean alertPresent;
        try {
            driver.switchTo().alert();
            alertPresent = true;
        } catch (NoAlertPresentException e) {
            alertPresent = false;
        }

        Assert.assertFalse(alertPresent, "Possible XSS vulnerability detected!");
    }

    @Test
    public void verifyDirectAccessBlocked() {
        driver.get("https://www.saucedemo.com/inventory.html");
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("saucedemo.com"), "Redirection failed!");
        Assert.assertFalse(currentUrl.contains("inventory.html"),
                "User accessed inventory page without login!");
    }

    @Test
    public void verifySessionInvalidAfterLogout() {
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // Logout
        driver.findElement(By.id("react-burger-menu-btn")).click();
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(By.id("logout_sidebar_link"))).click();

        // Try accessing inventory again
        driver.get("https://www.saucedemo.com/inventory.html");
        Assert.assertFalse(driver.getCurrentUrl().contains("inventory"),
                "User still logged in — session not invalidated properly!");
    }
}
