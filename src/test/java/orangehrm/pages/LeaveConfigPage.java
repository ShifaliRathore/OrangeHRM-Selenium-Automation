package orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page object for the Leave configuration pages: Leave Types, Work Week,
 * Holiday List and Leave Period. Pages are opened by direct URL because
 * the Configure sub-menu needs an extra expand step.
 */
public class LeaveConfigPage {

    WebDriver driver;
    WebDriverWait wait;

    // -------------------------------------------------------
    // DIRECT URLS — each config page navigated independently
    // -------------------------------------------------------

    private static final String LEAVE_TYPES_URL =
            "https://opensource-demo.orangehrmlive.com" +
            "/web/index.php/leave/leaveTypeList";

    private static final String WORK_WEEK_URL =
            "https://opensource-demo.orangehrmlive.com" +
            "/web/index.php/leave/defineWorkWeek";

    private static final String HOLIDAY_LIST_URL =
            "https://opensource-demo.orangehrmlive.com" +
            "/web/index.php/leave/viewHolidayList";

    private static final String LEAVE_PERIOD_URL =
            "https://opensource-demo.orangehrmlive.com" +
            "/web/index.php/leave/defineLeavePeriod";

    public LeaveConfigPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ===============================================================
    // NAVIGATION
    // ===============================================================

    /**
     * Navigates directly to the Leave Types page.
     */
    public void goToLeaveTypes() {
        driver.get(LEAVE_TYPES_URL);
    }

    /**
     * Navigates directly to the Work Week configuration page.
     */
    public void goToWorkWeek() {
        driver.get(WORK_WEEK_URL);
    }

    /**
     * Navigates directly to the Holiday List page.
     */
    public void goToHolidayList() {
        driver.get(HOLIDAY_LIST_URL);
    }

    /**
     * Navigates directly to the Define Leave Period page.
     */
    public void goToLeavePeriod() {
        driver.get(LEAVE_PERIOD_URL);
    }

    // ===============================================================
    // LEAVE TYPES — LOCATORS
    // ===============================================================

    // NOTE: OrangeHRM uses <h6> for this page heading.
    By leaveTypesHeading =
            By.xpath("//h6[normalize-space()='Leave Types']");

    // Each row in the Leave Types table
    By leaveTypeRows =
            By.xpath("//div[@class='oxd-table-body']/div");

    // "Add" button — top-right of the Leave Types page
    By leaveTypesAddButton =
            By.xpath("//button[normalize-space()='Add']");

    // ===============================================================
    // LEAVE TYPES — VERIFICATION METHODS
    // ===============================================================

    /**
     * TC-LT-01: Returns true when the Leave Types heading is visible.
     */
    public boolean isLeaveTypesPageDisplayed() {
        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            leaveTypesHeading)
            ).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /** TC-LT-02: Returns true when the table has at least one leave type. */
    public boolean hasAtLeastOneLeaveType() {
        try {
            List<WebElement> rows = wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(
                            leaveTypeRows));
            return rows.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns the count of leave type rows — used for console logging.
     */
    public int getLeaveTypeCount() {
        return driver.findElements(leaveTypeRows).size();
    }

    /**
     * TC-LT-03: Returns true when the "Add" button is visible on the
     * Leave Types page.
     */
    public boolean isLeaveTypesAddButtonDisplayed() {
        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            leaveTypesAddButton)
            ).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    // ===============================================================
    // WORK WEEK — LOCATORS
    // ===============================================================

    By workWeekHeading =
            By.xpath("//p[normalize-space()='Work Week']");

    // One row per day (7 rows)
    By workWeekDayRows =
            By.xpath("//div[@class='oxd-table-body']/div");

    By workWeekSaveButton =
            By.xpath("//button[normalize-space()='Save']");

    // ===============================================================
    // WORK WEEK — VERIFICATION METHODS
    // ===============================================================

    /**
     * TC-WW-01: Returns true when the Work Week heading is visible.
     */
    public boolean isWorkWeekPageDisplayed() {
        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            workWeekHeading)
            ).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /** TC-WW-02: Returns true when exactly 7 day rows are present. */
    public boolean hasAllSevenDayRows() {
        try {
            List<WebElement> rows = wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(
                            By.className("oxd-form-row")
                    )
            );
            return rows.size() == 7;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * TC-WW-03: Returns true when the "Save" button is visible.
     */
    public boolean isWorkWeekSaveButtonDisplayed() {
        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            workWeekSaveButton)
            ).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    // ===============================================================
    // HOLIDAY LIST — LOCATORS
    // ===============================================================

    // The heading reads "Holidays", not "Holiday List"
    By holidayListHeading =
            By.xpath("//h5[normalize-space()='Holidays']");

    By holidaySearchButton =
            By.xpath("//button[normalize-space()='Search']");

    By holidayResetButton =
            By.xpath("//button[normalize-space()='Reset']");

    By holidayAddButton =
            By.xpath("//button[normalize-space()='Add']");

    By holidayRows =
            By.xpath("//div[@class='oxd-table-body']/div");

    By noRecordsMessage =
            By.xpath("//*[contains(text(),'No Records Found')]");

    // ===============================================================
    // HOLIDAY LIST — VERIFICATION METHODS
    // ===============================================================

    /**
     * TC-HO-01: Returns true when the Holidays page heading is visible.
     */
    public boolean isHolidayListPageDisplayed() {
        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            holidayListHeading)
            ).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /**
     * TC-HO-02: Returns true when both Search and Reset buttons are
     * visible on the Holiday List filter panel.
     */
    public boolean areHolidayFilterButtonsDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    holidayListHeading));
            return driver.findElement(holidaySearchButton).isDisplayed()
                    && driver.findElement(holidayResetButton).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * TC-HO-03: Returns true when the "Add" button is visible.
     */
    public boolean isHolidayAddButtonDisplayed() {
        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            holidayAddButton)
            ).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /**
     * TC-HO-04: Clicks Search with no filters; returns true when rows or
     * "No Records Found" is displayed.
     */
    public boolean isHolidaySearchResultValid() {
        try {
            driver.findElement(holidaySearchButton).click();

            // Short wait for either outcome to settle
            try {
                wait.until(ExpectedConditions.or(
                        ExpectedConditions.visibilityOfElementLocated(
                                holidayRows),
                        ExpectedConditions.visibilityOfElementLocated(
                                noRecordsMessage)
                ));
            } catch (Exception ignored) { }

            boolean hasRows    = driver.findElements(holidayRows).size() > 0;
            boolean hasNoMsg   = driver.findElements(noRecordsMessage).size() > 0;

            return hasRows || hasNoMsg;

        } catch (Exception e) {
            return false;
        }
    }

    // ===============================================================
    // LEAVE PERIOD — LOCATORS
    // ===============================================================

    By leavePeriodHeading =
            By.xpath("//p[normalize-space()='Leave Period']");

    By leavePeriodSaveButton =
            By.xpath("//button[normalize-space()='Save']");

    // ===============================================================
    // LEAVE PERIOD — VERIFICATION METHODS
    // ===============================================================

    /**
     * TC-LP-01: Returns true when the Leave Period heading is visible.
     */
    public boolean isLeavePeriodPageDisplayed() {
        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            leavePeriodHeading)
            ).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    /**
     * TC-LP-02: Returns true when the "Save" button is visible on the
     * Leave Period configuration page.
     */
    public boolean isLeavePeriodSaveButtonDisplayed() {
        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            leavePeriodSaveButton)
            ).isDisplayed();
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }
}
