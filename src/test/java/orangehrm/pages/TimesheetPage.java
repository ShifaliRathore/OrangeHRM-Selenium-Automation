package orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class TimesheetPage {

    WebDriver driver;

    public TimesheetPage(WebDriver driver) {
        this.driver = driver;
    }

    // Timesheets page heading locator
    By timesheetHeading = By.xpath(
            "//*[self::h1 or self::h2 or self::h3 or self::h5 or self::h6]" +
                    "[contains(normalize-space(), 'Timesheet')]"
    );

    // Verify that the Timesheets heading is visible
    public boolean isTimesheetPageDisplayed() {
        try {
            WebDriverWait wait = new WebDriverWait(
                    driver, Duration.ofSeconds(10)
            );

            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            timesheetHeading
                    )
            ).isDisplayed();

        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public boolean isSearchButtonDisplayed() {
        By searchButton = By.xpath(
                "//button[.//text()[normalize-space()='Search'] or normalize-space()='Search']"
        );

        WebDriverWait wait =
                new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(searchButton)
            ).isDisplayed();

        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }
    public boolean isResetButtonDisplayed() {
        By resetButton = By.xpath(
                "//button[contains(., 'Reset')]"
        );

        WebDriverWait wait =
                new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(resetButton)
            ).isDisplayed();

        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }
    public boolean isEmployeeNameFieldDisplayed() {
        By employeeNameField = By.xpath(
                "//input[@placeholder='Type for hints...']"
        );

        WebDriverWait wait =
                new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            employeeNameField
                    )
            ).isDisplayed();

        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }
    public boolean isViewButtonDisplayed() {
        By viewButtons = By.xpath(
                "//button[normalize-space()='View']"
        );

        WebDriverWait wait =
                new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            return wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(
                            viewButtons
                    )
            ).size() > 0;

        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }
    public boolean isPendingActionSectionDisplayed() {
        By pendingActionHeading = By.xpath(
                "//*[self::h1 or self::h2 or self::h3 or self::h4 or self::h5 or self::h6]" +
                        "[contains(normalize-space(), 'Timesheets Pending Action')]"
        );

        WebDriverWait wait =
                new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            pendingActionHeading
                    )
            ).isDisplayed();

        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }


}