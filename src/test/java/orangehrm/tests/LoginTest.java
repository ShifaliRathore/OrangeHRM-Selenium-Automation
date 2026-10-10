package orangehrm.tests;

import orangehrm.base.BaseTest;
import orangehrm.pages.DashboardPage;
import orangehrm.pages.LoginPage;

import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test
    public void verifyValidLogin() throws InterruptedException {

        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("Admin", "admin123");

        Thread.sleep(3000);

        DashboardPage dashboardPage =
                new DashboardPage(driver);

        Assert.assertTrue(
                dashboardPage.isDashboardDisplayed());

        System.out.println("Login Successful");
    }

    @Test
    public void verifyInvalidLogin() throws InterruptedException {

        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("Admin123", "wrong123");

        Thread.sleep(2000);

        String actualMessage =
                loginPage.getErrorMessage();

        Assert.assertTrue(
                actualMessage.contains("Invalid"));

        System.out.println("Invalid Login Verified");
    }

    @Test
    public void verifyEmptyUsername() throws InterruptedException {

        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("", "admin123");

        Thread.sleep(2000);

        Assert.assertTrue(driver.getPageSource()
                .contains("Required"));

        System.out.println("Empty Username Validation Successful");
    }
    @Test
    public void verifyEmptyPassword() throws InterruptedException {

        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("Admin", "");

        Thread.sleep(2000);

        Assert.assertTrue(driver.getPageSource().contains("Required"));

        System.out.println("Empty Password Validation Successful");
    }
    @Test
    public void verifyEmptyCredentials() throws InterruptedException {

        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("", "");

        Thread.sleep(2000);

        Assert.assertTrue(driver.getPageSource().contains("Required"));

        System.out.println("Empty Credentials Validation Successful");
    }
    @Test
    public void verifyForgotPasswordLink() throws InterruptedException {

        LoginPage loginPage = new LoginPage(driver);

        loginPage.clickForgotPassword();

        Thread.sleep(2000);

        Assert.assertTrue(
                driver.getCurrentUrl()
                        .contains("requestPasswordResetCode"));

        System.out.println("Forgot Password Page Opened");
    }
    @Test
    public void verifyLogout() throws InterruptedException {

        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("Admin", "admin123");

        Thread.sleep(3000);

        DashboardPage dashboardPage =
                new DashboardPage(driver);

        dashboardPage.logout();

        Thread.sleep(2000);

        Assert.assertTrue(
                driver.getCurrentUrl().contains("login"));

        System.out.println("Logout Successful");
    }
    @Test
    public void verifyUsernameWithSpaces() throws InterruptedException {

        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("   Admin   ", "admin123");

        Thread.sleep(2000);

        String actualMessage =
                loginPage.getErrorMessage();

        Assert.assertTrue(actualMessage.contains("Invalid"));

        System.out.println("Username Space Validation Successful");
    }
    @Test
    public void verifyPasswordWithSpaces() throws InterruptedException {

        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("Admin", "   admin123   ");

        Thread.sleep(2000);

        String actualMessage =
                loginPage.getErrorMessage();

        Assert.assertTrue(actualMessage.contains("Invalid"));

        System.out.println("Password Space Validation Successful");
    }
}