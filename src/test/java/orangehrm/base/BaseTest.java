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

    // Top-right profile name — used to detect mid-test session rotation
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
     * The OrangeHRM public demo site rotates the active session automatically
     * to simulate different demo users (e.g. "Bilol Abdurasul", "Rahul Doe").
     * If a test proceeds immediately after login without this confirmation,
     * the rotated user may not have Admin permissions — causing pages like
     * Apply Leave to render blank or redirect away.
     *
     * NOTE: Every test in LeaveModuleTest calls this instead of using
     * LoginPage directly, so the Dashboard heading is always confirmed
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
     * Returns true if the top-right profile name still shows "Admin".
     *
     * The demo site can rotate the session mid-test between navigations.
     * This method reads the username element in the top-right corner to
     * detect whether rotation has occurred since login.
     *
     * NOTE: Uses a short 5-second wait so it does not slow tests down
     * significantly when called between steps of a multi-step test.
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
     * Re-establishes the Admin session if the demo site has rotated it.
     * Safe to call between steps in a multi-step test — if the session
     * is already Admin it does nothing and returns immediately.
     *
     * NOTE: Do not call this mid-form. It navigates away from the current
     * page which will lose any unsaved form state. Only call between steps.
     */
    protected void ensureAdminSession() {
        if (!isSessionStillAdmin()) {
            System.out.println(
                    "WARNING: Session rotated away from Admin — re-establishing login");
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
