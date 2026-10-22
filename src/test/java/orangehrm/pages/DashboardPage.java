package orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class DashboardPage {

    WebDriver driver;

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
    }

    // Dashboard heading locator
    By dashboardHeading =
            By.xpath("//h6[text()='Dashboard']");

    // Profile menu locator
    By profileMenu =
            By.className("oxd-userdropdown-tab");

    // Logout locator
    By logout =
            By.linkText("Logout");

    // Time menu locator
    By timeMenu =
            By.xpath("//span[text()='Time']");

    // Leave menu locator
    By leaveMenu =
            By.xpath("//span[text()='Leave']");

    // Verify Dashboard is displayed
    public boolean isDashboardDisplayed() {
        WebDriverWait wait =
                new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            dashboardHeading
                    )
            ).isDisplayed();

        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    // Verify Time menu is displayed
    public boolean isTimeMenuDisplayed() {
        return driver.findElement(timeMenu).isDisplayed();
    }

    // Verify profile dropdown is displayed
    public boolean isProfileDropdownDisplayed() {
        driver.findElement(profileMenu).click();

        WebDriverWait wait =
                new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(logout)
            ).isDisplayed();

        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    // Click Time menu
    public void clickTimeMenu() {
        driver.findElement(timeMenu).click();
    }

    // Navigate to Timesheets page
    public void clickTimesheets() throws InterruptedException {
        driver.findElement(timeMenu).click();

        Thread.sleep(2000);

        driver.get(
                "https://opensource-demo.orangehrmlive.com/web/index.php/time/viewEmployeeTimesheet"
        );
    }

    // Verify Leave menu is available in the sidebar (it can disappear on the shared demo)
    public boolean isLeaveMenuDisplayed() {
        return !driver.findElements(leaveMenu).isEmpty();
    }

    // Click Leave menu
    public void clickLeaveMenu() {
        driver.findElement(leaveMenu).click();
    }

    // Logout from OrangeHRM
    public void logout() throws InterruptedException {
        driver.findElement(profileMenu).click();

        Thread.sleep(2000);

        driver.findElement(logout).click();
    }
}