package orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {

    WebDriver driver;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    By username = By.name("username");
    By password = By.name("password");
    By loginButton = By.xpath("//button[@type='submit']");
    By errorMessage = By.xpath("//p[contains(@class,'oxd-alert-content-text')]");

    // Add here
    By forgotPassword =
            By.className("orangehrm-login-forgot-header");

    public void login(String uname, String pwd) {

        driver.findElement(username).sendKeys(uname);
        driver.findElement(password).sendKeys(pwd);
        driver.findElement(loginButton).click();
    }

    public String getErrorMessage() {

        return driver.findElement(errorMessage).getText();
    }

    // Add this method
    public void clickForgotPassword() {

        driver.findElement(forgotPassword).click();
    }
}