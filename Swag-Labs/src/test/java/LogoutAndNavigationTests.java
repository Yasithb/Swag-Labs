import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class LogoutAndNavigationTests {
    WebDriver driver;
    String baseUrl = "https://www.saucedemo.com/";

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
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
    public void verifyMenuCanBeOpened() {
        WebElement menuButton = driver.findElement(By.id("react-burger-menu-btn"));
        menuButton.click();

        WebElement logoutLink = driver.findElement(By.id("logout_sidebar_link"));
        logoutLink.click();
    }

    @Test
    public void verifyLogoutFunctionality() {
        driver.findElement(By.id("react-burger-menu-btn")).click();
        driver.findElement(By.id("logout_sidebar_link")).click();

        // Verify redirection to login page
        Assert.assertTrue(driver.getCurrentUrl().contains("saucedemo.com"),
                "User not redirected to login page after logout!");

        // Verify login button visible again
        WebElement loginButton = driver.findElement(By.id("login-button"));
        Assert.assertTrue(loginButton.isDisplayed(), "Login button not visible after logout!");
    }

    @Test
    public void verifySessionExpiresAfterLogout() {
        driver.findElement(By.id("react-burger-menu-btn")).click();
        driver.findElement(By.id("logout_sidebar_link")).click();

        driver.navigate().back();

        // Try accessing inventory page again
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("saucedemo.com"),
                "User can still access inventory page after logout!");
    }

    @Test
    public void verifyAboutPageNavigation() {
        driver.findElement(By.id("react-burger-menu-btn")).click();
        driver.findElement(By.id("about_sidebar_link")).click();

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("saucelabs.com"), "About page did not open!");
    }

    @Test
    public void verifyCartNavigationFromInventory() {
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        driver.findElement(By.className("shopping_cart_link")).click();

        Assert.assertTrue(driver.getCurrentUrl().contains("cart"), "Cart page not opened!");
    }

    @Test
    public void verifyAppLogoNavigation() {
        driver.findElement(By.id("add-to-cart-sauce-labs-bike-light")).click();
        driver.findElement(By.className("shopping_cart_link")).click();

        // Click the app logo
        driver.findElement(By.className("app_logo")).click();


    }

    @Test
    public void verifyMenuCloseButton() {
        driver.findElement(By.id("react-burger-menu-btn")).click();

        driver.findElement(By.id("react-burger-cross-btn")).click();

    }
}
