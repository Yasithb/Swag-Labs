import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DemoLoginTests {

    WebDriver driver;
    String baseUrl = "https://www.saucedemo.com/";

    @BeforeMethod
    public void launchApp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(baseUrl);
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void validLoginTest() {
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("inventory"), "User failed to login!");
    }

    @Test
    public void invalidPasswordTest() {
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("wrong_password");
        driver.findElement(By.id("login-button")).click();

        WebElement error = driver.findElement(By.cssSelector("h3[data-test='error']"));
        Assert.assertTrue(error.isDisplayed());
        Assert.assertTrue(error.getText().contains("do not match"), "Error message not correct");
    }

    @Test
    public void invalidUsernameTest() {
        driver.findElement(By.id("user-name")).sendKeys("invalid_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        WebElement error = driver.findElement(By.cssSelector("h3[data-test='error']"));
        Assert.assertTrue(error.isDisplayed());
        Assert.assertTrue(error.getText().contains("do not match"), "Error message not correct");
    }

    @Test
    public void lockedOutUserTest() {
        driver.findElement(By.id("user-name")).sendKeys("locked_out_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        WebElement error = driver.findElement(By.cssSelector("h3[data-test='error']"));
        Assert.assertTrue(error.isDisplayed());
        Assert.assertTrue(error.getText().contains("locked out"), "Locked-out message not shown");
    }

    @Test
    public void emptyCredentialsTest() {
        driver.findElement(By.id("login-button")).click();

        WebElement error = driver.findElement(By.cssSelector("h3[data-test='error']"));
        Assert.assertTrue(error.isDisplayed());
        Assert.assertTrue(error.getText().contains("Username is required"), "Error message incorrect");
    }

    @Test
    public void blankPasswordTest() {
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("login-button")).click();

        WebElement error = driver.findElement(By.cssSelector("h3[data-test='error']"));
        Assert.assertTrue(error.isDisplayed());
        Assert.assertTrue(error.getText().contains("Password is required"), "Error message incorrect");
    }
}
