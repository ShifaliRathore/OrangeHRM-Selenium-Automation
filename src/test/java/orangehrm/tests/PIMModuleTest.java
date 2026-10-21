
package orangehrm.tests;

import orangehrm.base.BaseTest;
import orangehrm.pages.LoginPage;
import orangehrm.pages.PIMPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PIMModuleTest extends BaseTest {

    @Test
    public void verifyPIMPageOpens() {

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("Admin", "admin123");

        PIMPage pimPage = new PIMPage(driver);
        pimPage.openPIM();

        Assert.assertTrue(
                pimPage.isPIMHeadingDisplayed(),
                "PIM heading should be displayed"
        );
    }

    @Test
    public void verifyEmployeeListDisplayed() {

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("Admin", "admin123");

        PIMPage pimPage = new PIMPage(driver);
        pimPage.openPIM();

        Assert.assertTrue(
                pimPage.isEmployeeListDisplayed(),
                "Employee Information heading should be displayed"
        );
    }
}