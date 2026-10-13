package orangehrm.tests;

import orangehrm.base.BaseTest;
import orangehrm.pages.LoginPage;
import orangehrm.pages.DashboardPage;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DashboardTest extends BaseTest {
    @Test
    public void verifyDashboardOpens() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("Admin", "admin123");

        DashboardPage dashboardPage = new DashboardPage(driver);

        Assert.assertTrue(
                dashboardPage.isDashboardDisplayed(),
                "Dashboard page did not open"
        );

        System.out.println("Dashboard Opened Successfully");
    }
    @Test
    public void verifyTimeMenuIsDisplayed() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("Admin", "admin123");

        DashboardPage dashboardPage = new DashboardPage(driver);

        Assert.assertTrue(
                dashboardPage.isTimeMenuDisplayed(),
                "Time menu is not displayed on Dashboard"
        );

        System.out.println("Time Menu Displayed Successfully");
    }

    @Test
    public void verifyProfileDropdownOpens() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("Admin", "admin123");

        DashboardPage dashboardPage = new DashboardPage(driver);

        Assert.assertTrue(
                dashboardPage.isProfileDropdownDisplayed(),
                "Profile dropdown did not open"
        );

        System.out.println("Profile Dropdown Opened Successfully");
    }
    @Test
    public void verifyDashboardAfterNavigatingFromTime() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("Admin", "admin123");

        DashboardPage dashboardPage = new DashboardPage(driver);

        // Open the Time module
        dashboardPage.clickTimeMenu();

        // Return to the previous page
        driver.navigate().back();

        // Verify Dashboard is displayed
        Assert.assertTrue(
                dashboardPage.isDashboardDisplayed(),
                "Dashboard was not displayed after navigating back"
        );

        System.out.println(
                "Dashboard Displayed After Returning From Time"
        );
    }

}