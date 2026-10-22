package orangehrm.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;

    private static final String LOGIN_URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

    private static final By DASHBOARD_HEADING =
            By.xpath("//h6[text()='Dashboard']");

    // Top-right profile name (the linked employee's name, not the login username)
    private static final By TOP_RIGHT_USERNAME =
            By.xpath("//p[@class='oxd-userdropdown-name']");

    @BeforeMethod
    public void setup() {

        WebDriverManager.chromedriver().setup();

        driver = new ChromeDriver();
        driver.manage().window().maximize();

        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(10));

        driver.get(LOGIN_URL);
    }

    /**
     * Logs in as Admin and waits for the Dashboard heading to confirm
     * the session is fully established before any test step runs.
     *
     * WHY THIS EXISTS:
     * On the shared OrangeHRM public demo site, the Admin account's displayed
     * name, sidebar menus and Leave data have been seen to change (see
     * DEF-LM-001). Waiting for the Dashboard heading confirms login finished
     * before the first test action runs.
     */
    protected void loginAsAdmin() {

        driver.get(LOGIN_URL);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")))
                .sendKeys("Admin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")))
                .sendKeys("admin123");
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit']"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(DASHBOARD_HEADING));
    }

    /**
     * Returns true if the top-right profile name equals "Admin".
     *
     * KNOWN LIMITATION: the top-right name is the linked EMPLOYEE's name, not
     * the login username, and it never reads "Admin" on the demo site. This
     * method therefore always returns false, so ensureAdminSession() always
     * logs in again. It is left unchanged because other test classes call
     * ensureAdminSession() (e.g. RecruitmentValidationTest cleanup).
     * LeaveModuleTest no longer uses it.
     */
    protected boolean isSessionStillAdmin() {
        try {
            String name = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(
                            TOP_RIGHT_USERNAME))
                    .getText().trim();
            return name.equalsIgnoreCase("Admin");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Logs in again unless isSessionStillAdmin() returns true. Because of the
     * limitation described there, this currently always performs a fresh login.
     *
     * NOTE: Do not call this mid-form. It navigates away from the current
     * page which will lose any unsaved form state. Only call between steps.
     */
    protected void ensureAdminSession() {
        if (!isSessionStillAdmin()) {
            System.out.println(
                    "WARNING: Could not confirm Admin session — logging in again");
            loginAsAdmin();
        }
    }

    @AfterMethod
    public void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }
}
