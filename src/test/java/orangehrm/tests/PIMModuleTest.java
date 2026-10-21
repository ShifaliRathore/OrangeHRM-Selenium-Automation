
package orangehrm.tests;

import orangehrm.base.BaseTest;
import orangehrm.pages.LoginPage;
import orangehrm.pages.PIMPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PIMModuleTest extends BaseTest {

    private PIMPage loginAndOpenPIM() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("Admin", "admin123");

        PIMPage pimPage = new PIMPage(driver);
        pimPage.openPIM();

        return pimPage;
    }

    // Test 1: Verify PIM page opens
    @Test
    public void verifyPIMPageOpens() {
        PIMPage pimPage = loginAndOpenPIM();

        Assert.assertTrue(
                pimPage.isPIMHeadingDisplayed(),
                "PIM heading should be displayed"
        );
    }

    // Test 2: Verify Employee Information is displayed
    @Test
    public void verifyEmployeeListDisplayed() {
        PIMPage pimPage = loginAndOpenPIM();

        Assert.assertTrue(
                pimPage.isEmployeeListDisplayed(),
                "Employee Information heading should be displayed"
        );
    }

    // Test 3: Verify Add Employee page opens
    @Test
    public void verifyAddEmployeePageOpens() {
        PIMPage pimPage = loginAndOpenPIM();
        pimPage.clickAddEmployee();

        Assert.assertTrue(
                pimPage.isAddEmployeeHeadingDisplayed(),
                "Add Employee heading should be displayed"
        );
    }

    // Test 4: Verify Employee Name search field is displayed
    @Test
    public void verifyEmployeeNameSearchFieldDisplayed() {
        PIMPage pimPage = loginAndOpenPIM();

        Assert.assertTrue(
                pimPage.isEmployeeNameSearchDisplayed(),
                "Employee Name search field should be displayed"
        );
    }

    // Test 5: Verify Employee ID search field is displayed
    @Test
    public void verifyEmployeeIdSearchFieldDisplayed() {
        PIMPage pimPage = loginAndOpenPIM();

        Assert.assertTrue(
                pimPage.isEmployeeIdSearchDisplayed(),
                "Employee ID search field should be displayed"
        );
    }

    // Test 6: Verify nonexistent employee search returns no records
    @Test
    public void verifySearchForNonexistentEmployee() {
        PIMPage pimPage = loginAndOpenPIM();

        pimPage.searchEmployee("ZZZ_NoSuchEmployee_999");

        Assert.assertTrue(
                pimPage.isNoRecordsFoundDisplayed(),
                "No Records Found should be displayed for a nonexistent employee"
        );
    }
}
