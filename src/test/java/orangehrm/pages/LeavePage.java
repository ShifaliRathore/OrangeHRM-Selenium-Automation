package orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * LeavePage — Navigation Hub
 *
 * Handles sub-menu navigation within the Leave section.
 * Call dashboardPage.clickLeaveMenu() before using any goTo* method.
 */
public class LeavePage {

    WebDriver driver;
    WebDriverWait wait;

    public LeavePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Locators
    By applyLeaveMenu  = By.xpath("//a[normalize-space()='Apply']");
    By myLeaveListMenu = By.xpath("//a[normalize-space()='My Leave List']");
    By leaveListMenu   = By.xpath("//a[normalize-space()='Leave List']");
    By leaveHeading    = By.xpath("//h6[text()='Leave']");

    // Navigation
    public void goToApplyLeave() {
        wait.until(ExpectedConditions.elementToBeClickable(applyLeaveMenu)).click();
    }

    public void goToMyLeaveList() {
        wait.until(ExpectedConditions.elementToBeClickable(myLeaveListMenu)).click();
    }

    public void goToLeaveList() {
        wait.until(ExpectedConditions.elementToBeClickable(leaveListMenu)).click();
    }

    // Verification
    public boolean isLeaveHeadingDisplayed() {
        try {
            return wait.until(ExpectedConditions
                    .visibilityOfElementLocated(leaveHeading))
                    .isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }
}
