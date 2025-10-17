import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class UIAccessibilityTests {
    WebDriver driver;
    String baseUrl = "https://www.saucedemo.com/";

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(baseUrl);
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void verifyLoginPageElementsAreVisible() {
        WebElement logo = driver.findElement(By.className("login_logo"));
        WebElement username = driver.findElement(By.id("user-name"));
        WebElement password = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));

        Assert.assertTrue(logo.isDisplayed(), "Logo is not visible!");
        Assert.assertTrue(username.isDisplayed(), "Username field is missing!");
        Assert.assertTrue(password.isDisplayed(), "Password field is missing!");
        Assert.assertTrue(loginButton.isDisplayed(), "Login button not visible!");
    }

    @Test
    public void verifyLabelsHaveForAttributes() {
        List<WebElement> labels = driver.findElements(By.tagName("label"));
        for (WebElement label : labels) {
            String forAttr = label.getAttribute("for");
            Assert.assertNotNull(forAttr, "Label missing 'for' attribute: " + label.getText());
            Assert.assertFalse(forAttr.isEmpty(), "Empty 'for' attribute on label: " + label.getText());
        }
    }

    @Test
    public void verifyButtonsHaveAccessibleNames() {
        List<WebElement> buttons = driver.findElements(By.tagName("button"));
        for (WebElement button : buttons) {
            String text = button.getText().trim();
            String aria = button.getAttribute("aria-label");
            Assert.assertTrue(
                    !text.isEmpty() || (aria != null && !aria.isEmpty()),
                    "Button without accessible name found!"
            );
        }
    }

    @Test
    public void verifyTabNavigationWorks() {
        WebElement username = driver.findElement(By.id("user-name"));
        WebElement password = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));

        username.sendKeys("standard_user");
        username.sendKeys(Keys.TAB);
        WebElement activeElement = driver.switchTo().activeElement();
        Assert.assertEquals(activeElement, password, "Tab key did not move to password field!");

        password.sendKeys(Keys.TAB);
        activeElement = driver.switchTo().activeElement();
        Assert.assertEquals(activeElement, loginButton, "Tab key did not move to login button!");
    }

    @Test
    public void verifyButtonColorContrast() {
        WebElement loginButton = driver.findElement(By.id("login-button"));
        String bgColor = loginButton.getCssValue("background-color");
        String textColor = loginButton.getCssValue("color");

        System.out.println("Button background color: " + bgColor);
        System.out.println("Button text color: " + textColor);

        Assert.assertNotEquals(bgColor, textColor, "Low contrast: Button text color too similar to background!");
    }

    @Test
    public void verifyImagesHaveAltAttributes() {
        List<WebElement> images = driver.findElements(By.tagName("img"));
        for (WebElement img : images) {
            String alt = img.getAttribute("alt");
            Assert.assertNotNull(alt, "Image missing alt attribute!");
            Assert.assertFalse(alt.isEmpty(), "Empty alt attribute found!");
        }
    }

    @Test
    public void verifyErrorMessageIsReadable() {
        driver.findElement(By.id("login-button")).click(); // Try to login without credentials
        WebElement errorMessage = driver.findElement(By.cssSelector("h3[data-test='error']"));

        Assert.assertTrue(errorMessage.isDisplayed(), "Error message not shown!");
        String color = errorMessage.getCssValue("color");
        System.out.println("Error message color: " + color);

        Assert.assertTrue(color.contains("rgb"), "Error message color not readable or missing!");
    }
}
