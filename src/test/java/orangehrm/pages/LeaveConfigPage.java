package orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * LeaveConfigPage
 *
 * Page object covering the four Leave Configuration pages of OrangeHRM.
 * These pages hold system-wide settings that are NOT tied to any specific
 * logged-in user. They render identically regardless of which Admin
 * account is active — making them immune to the session rotation issue
 * documented in defect report DEF-LM-001.
 *
 * Pages covered:
 *   Leave Types    — /leave/leaveTypeList
 *   Work Week      — /leave/defineWorkWeek
 *   Holiday List   — /leave/viewHolidayList
 *   Leave Period   — /leave/defineLeavePeriod
 *
 * NOTE: All four pages are Admin-only. They require an active Admin
 * session but do NOT require a specific Admin account — any Admin will
 * see the same data. loginAsAdmin() in BaseTest guarantees this.
 *
 * NOTE: These pages are navigated to via direct URL rather than
 * sub-menu clicks because the Configure sub-menu requires an extra
 * hover/expand step that varies by OrangeHRM version. Direct URL
 * navigation is more reliable for configuration pages.
 *
 * SEPARATION NOTE: This page object is intentionally separate from
 * LeavePage.java, ApplyLeavePage.java, MyLeaveListPage.java, and
 * LeaveListPage.java. Those four files cover user-dependent Leave
 * workflow pages. This file covers only user-independent system
 * configuration pages tested in LeaveConfigTest.java.
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

    /**
     * TC-LT-02: Returns true when at least one leave type row is present
     * in the table.
     *
     * NOTE: The demo site always has system leave types pre-seeded
     * (e.g. "CAN - Bereavement", "Annual Leave"). This test only fails
     * if the entire leave types table is wiped — which does not happen
     * on a standard demo site reset.
     */
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

    // One row per day of the week (Monday through Sunday) — always 7 rows.
    // NOTE: This locator targets the table body rows on the Work Week page.
    // ⚠ WARNING: If OrangeHRM splits the work week table into two separate
    // tables (e.g. Mon–Fri and Sat–Sun), this count will differ from 7
    // and TC-WW-02 will fail. Verify the DOM structure if this occurs.
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

    /**
     * TC-WW-02: Returns true when exactly 7 day rows are present —
     * one for each day of the week (Monday through Sunday).
     *
     * NOTE: This is a structural test. The Work Week page always renders
     * all 7 days regardless of which are configured as working days.
     * It verifies the form is fully rendered, not what days are selected.
     */
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

    // NOTE: The rendered page heading is "Holidays" — not "Holiday List".
    // The URL path is viewHolidayList but the h6 text reads "Holidays".
    // If TC-HO-01 fails, inspect the exact heading text in the browser DOM.
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
     * TC-HO-04: Clicks Search with no filters applied and returns true
     * when the page renders a valid state — either rows appear in the
     * table OR "No Records Found" is displayed.
     * Returns false only if the page crashes or neither state is reached.
     *
     * NOTE: The demo site may or may not have holidays seeded. Both
     * outcomes (records found / no records found) are valid and the
     * test passes for either.
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
