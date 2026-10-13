package orangehrm.tests;

import orangehrm.base.BaseTest;
import orangehrm.pages.DashboardPage;
import orangehrm.pages.LoginPage;
import orangehrm.pages.TimePage;
import orangehrm.pages.TimesheetPage;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TimeModuleTest extends BaseTest {

    @Test
    public void verifyTimePageOpens() throws InterruptedException {

        // Login to OrangeHRM
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("Admin", "admin123");

        Thread.sleep(3000);

        // Navigate to the Time page
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.clickTimeMenu();

        Thread.sleep(2000);

        // Verify the Time page
        TimePage timePage = new TimePage(driver);

        Assert.assertTrue(
                timePage.isTimePageDisplayed(),
                "Time page did not open"
        );

        System.out.println("Time Module Opened Successfully");
    }

    @Test
    public void verifyTimesheetsPageOpens()
            throws InterruptedException {

        // Login to OrangeHRM
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("Admin", "admin123");

        Thread.sleep(3000);

        // Navigate to the Timesheets page
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.clickTimesheets();

        Thread.sleep(2000);

        // Verify the Timesheets page
        TimesheetPage timesheetPage = new TimesheetPage(driver);

        Assert.assertTrue(
                timesheetPage.isTimesheetPageDisplayed(),
                "Timesheets page did not open"
        );

        System.out.println("Timesheets Page Opened Successfully");
    }

    @Test
    public void verifyEmployeeNameFieldIsDisplayed() throws InterruptedException {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("Admin", "admin123");

        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.clickTimesheets();

        TimesheetPage timesheetPage = new TimesheetPage(driver);

        Assert.assertTrue(
                timesheetPage.isEmployeeNameFieldDisplayed(),
                "Employee Name field is not displayed"
        );

        System.out.println("Employee Name Field Displayed Successfully");
    }

    @Test
    public void verifyPendingActionSectionIsDisplayed()
            throws InterruptedException {

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("Admin", "admin123");

        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.clickTimesheets();

        TimesheetPage timesheetPage = new TimesheetPage(driver);

        Assert.assertTrue(
                timesheetPage.isPendingActionSectionDisplayed(),
                "Timesheets Pending Action section is not displayed"
        );

        System.out.println(
                "Timesheets Pending Action Section Displayed Successfully"
        );
    }



}
