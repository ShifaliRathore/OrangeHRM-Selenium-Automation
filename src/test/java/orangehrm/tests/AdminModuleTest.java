

package orangehrm.tests;

import orangehrm.pages.AdminPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

/**
 * Admin module - 10 test cases (OrangeHRM demo site).
 */
public class AdminModuleTest {

    private WebDriver driver;
    private AdminPage admin;

    // Username created in TC08 and removed in TC10
    private String newUsername;

    @BeforeClass
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        // options.addArguments("--headless=new"); // uncomment to run without opening a browser window

        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

        admin = new AdminPage(driver);
        admin.login("Admin", "admin123");
    }

    /** Every test starts from a fresh System Users page. */
    @BeforeMethod
    public void openUsersPage() {
        admin.goToUsersPage();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // TC01
    @Test(priority = 1, description = "TC01 - Navigate to Admin module from the side menu")
    public void tc01_navigateToAdminModule() {
        admin.openDashboard();
        admin.openAdminModule();

        Assert.assertTrue(admin.getCurrentUrl().contains("viewSystemUsers"),
                "URL should contain viewSystemUsers");
        Assert.assertTrue(admin.getModuleHeading().contains("Admin"),
                "Page heading should contain 'Admin'");
    }

    // TC02
    @Test(priority = 2, description = "TC02 - System Users list is displayed with records")
    public void tc02_usersListIsDisplayed() {
        Assert.assertTrue(admin.getRowCount() > 0, "At least one user should be listed");
        Assert.assertTrue(admin.getRecordsFoundText().contains("Found"),
                "Records count text should be displayed");
    }

    // TC03
    @Test(priority = 3, description = "TC03 - Search user by username")
    public void tc03_searchByUsername() {
        admin.searchByUsername("Admin");

        List<String> usernames = admin.getColumnValues(AdminPage.COL_USERNAME);
        Assert.assertFalse(usernames.isEmpty(), "Search should return at least one record");
        for (String name : usernames) {
            Assert.assertTrue(name.toLowerCase().contains("admin"),
                    "Unexpected username in results: " + name);
        }
    }

    // TC04
    @Test(priority = 4, description = "TC04 - Search users by User Role = Admin")
    public void tc04_searchByUserRole() {
        admin.selectUserRole("Admin");
        admin.clickSearch();

        List<String> roles = admin.getColumnValues(AdminPage.COL_ROLE);
        Assert.assertFalse(roles.isEmpty(), "Search should return at least one record");
        for (String role : roles) {
            Assert.assertEquals(role, "Admin", "Every result should have role Admin");
        }
    }

    // TC05
    @Test(priority = 5, description = "TC05 - Search users by Status = Enabled")
    public void tc05_searchByStatus() {
        admin.selectStatus("Enabled");
        admin.clickSearch();

        List<String> statuses = admin.getColumnValues(AdminPage.COL_STATUS);
        Assert.assertFalse(statuses.isEmpty(), "Search should return at least one record");
        for (String status : statuses) {
            Assert.assertEquals(status, "Enabled", "Every result should be Enabled");
        }
    }

    // TC06
    @Test(priority = 6, description = "TC06 - Search with an invalid username shows No Records Found")
    public void tc06_searchInvalidUsername() {
        admin.searchByUsername("no_such_user_xyz_999");

        Assert.assertTrue(admin.getRecordsFoundText().contains("No Records Found"),
                "'No Records Found' message should be displayed");
        Assert.assertEquals(admin.getRowCount(), 0, "No rows should be displayed");
    }

    // TC07
    @Test(priority = 7, description = "TC07 - Reset button clears the search fields")
    public void tc07_resetClearsSearchFields() {
        admin.enterUsername("Admin");
        Assert.assertEquals(admin.getUsernameFieldValue(), "Admin", "Username should be typed in");

        admin.clickReset();
        Assert.assertEquals(admin.getUsernameFieldValue(), "", "Username field should be empty after Reset");
    }

    // TC08
    @Test(priority = 8, description = "TC08 - Add a new user with valid data")
    public void tc08_addNewUser() {
        newUsername = "tanu" + (System.currentTimeMillis() % 100000);

        admin.addUser("ESS", "a", "Enabled", newUsername, "Test@1234");

        Assert.assertTrue(admin.getToastMessage().contains("Success"),
                "Success toast should be displayed after saving the user");

        // verify the new user shows up in the list
        admin.goToUsersPage();
        admin.searchByUsername(newUsername);
        Assert.assertEquals(admin.getRowCount(), 1, "Newly created user should be found");
        Assert.assertEquals(admin.getColumnValues(AdminPage.COL_USERNAME).get(0), newUsername);
    }

    // TC09
    @Test(priority = 9, description = "TC09 - Saving the Add User form empty shows validation errors")
    public void tc09_addUserMandatoryFieldValidation() {
        admin.openAddUserForm();
        admin.clickSave();

        List<String> errors = admin.getValidationErrors();
        Assert.assertTrue(errors.size() >= 4,
                "Mandatory field errors expected, but found: " + errors);
        Assert.assertTrue(errors.contains("Required"), "'Required' message should be displayed");
        Assert.assertTrue(admin.getCurrentUrl().contains("saveSystemUser"),
                "User should stay on the Add User page");
    }

    // TC10
    @Test(priority = 10, dependsOnMethods = "tc08_addNewUser",
            description = "TC10 - Delete the user created in TC08")
    public void tc10_deleteUser() {
        String toast = admin.deleteUser(newUsername);
        Assert.assertTrue(toast.contains("Success"), "Success toast should be displayed after delete");

        admin.searchByUsername(newUsername);
        Assert.assertTrue(admin.getRecordsFoundText().contains("No Records Found"),
                "Deleted user should no longer be found");
    }
}

