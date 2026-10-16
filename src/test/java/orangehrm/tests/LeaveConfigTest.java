package orangehrm.tests;

import orangehrm.base.BaseTest;
import orangehrm.pages.LeaveConfigPage;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LeaveConfigTest
 *
 * Tests the four Leave Configuration pages of OrangeHRM:
 *   Leave Types    — /leave/leaveTypeList
 *   Work Week      — /leave/defineWorkWeek
 *   Holiday List   — /leave/viewHolidayList
 *   Leave Period   — /leave/defineLeavePeriod
 *
 * These are system-wide configuration pages. They display identical data
 * for any Admin session and are unaffected by session rotation (DEF-LM-001).
 * They serve as a stable, always-passing baseline for the Leave module suite.
 *
 * Execution order:
 *   priority 1–3   → TC-LT-*  Leave Types
 *   priority 4–6   → TC-WW-*  Work Week
 *   priority 7–10  → TC-HO-*  Holiday List
 *   priority 11–12 → TC-LP-*  Leave Period
 */
public class LeaveConfigTest extends BaseTest {

    // ============================================================
    // LEAVE TYPES (TC-LT-*)
    // ============================================================

    @Test(priority = 1)
    public void verifyLeaveTypesPageLoads() {
        loginAsAdmin();
        LeaveConfigPage configPage = new LeaveConfigPage(driver);
        configPage.goToLeaveTypes();
        Assert.assertTrue(
                configPage.isLeaveTypesPageDisplayed(),
                "TC-LT-01 FAILED: Leave Types page heading not visible");
        System.out.println("TC-LT-01 PASSED: Leave Types page loaded successfully");
    }

    @Test(priority = 2)
    public void verifyLeaveTypesTableHasRecords() {
        loginAsAdmin();
        LeaveConfigPage configPage = new LeaveConfigPage(driver);
        configPage.goToLeaveTypes();
        Assert.assertTrue(
                configPage.hasAtLeastOneLeaveType(),
                "TC-LT-02 FAILED: Leave Types table has no records");
        System.out.println("TC-LT-02 PASSED: Leave Types table has "
                + configPage.getLeaveTypeCount() + " record(s)");
    }

    @Test(priority = 3)
    public void verifyLeaveTypesAddButtonIsDisplayed() {
        loginAsAdmin();
        LeaveConfigPage configPage = new LeaveConfigPage(driver);
        configPage.goToLeaveTypes();
        Assert.assertTrue(
                configPage.isLeaveTypesAddButtonDisplayed(),
                "TC-LT-03 FAILED: 'Add' button not visible on Leave Types page");
        System.out.println("TC-LT-03 PASSED: 'Add' button is visible on Leave Types page");
    }

    // ============================================================
    // WORK WEEK (TC-WW-*)
    // ============================================================

    @Test(priority = 4)
    public void verifyWorkWeekPageLoads() {
        loginAsAdmin();
        LeaveConfigPage configPage = new LeaveConfigPage(driver);
        configPage.goToWorkWeek();
        Assert.assertTrue(
                configPage.isWorkWeekPageDisplayed(),
                "TC-WW-01 FAILED: Work Week page heading not visible");
        System.out.println("TC-WW-01 PASSED: Work Week page loaded successfully");
    }

    /**
     * Verifies the Work Week form renders exactly 7 rows — one per day.
     * This is a structural check; the row count is fixed regardless of
     * which days are configured as working days.
     */
    @Test(priority = 5)
    public void verifyWorkWeekTableHasSevenDayRows() {
        loginAsAdmin();
        LeaveConfigPage configPage = new LeaveConfigPage(driver);
        configPage.goToWorkWeek();
        Assert.assertTrue(
                configPage.hasAllSevenDayRows(),
                "TC-WW-02 FAILED: Work Week table does not have exactly 7 day rows");
        System.out.println("TC-WW-02 PASSED: Work Week table has all 7 day rows");
    }

    @Test(priority = 6)
    public void verifyWorkWeekSaveButtonIsDisplayed() {
        loginAsAdmin();
        LeaveConfigPage configPage = new LeaveConfigPage(driver);
        configPage.goToWorkWeek();
        Assert.assertTrue(
                configPage.isWorkWeekSaveButtonDisplayed(),
                "TC-WW-03 FAILED: 'Save' button not visible on Work Week page");
        System.out.println("TC-WW-03 PASSED: 'Save' button is visible on Work Week page");
    }

    // ============================================================
    // HOLIDAY LIST (TC-HO-*)
    // ============================================================

    /**
     * NOTE: The rendered heading text is "Holidays" — not "Holiday List".
     * The URL path is /viewHolidayList but the page h5 reads "Holidays".
     */
    @Test(priority = 7)
    public void verifyHolidayListPageLoads() {
        loginAsAdmin();
        LeaveConfigPage configPage = new LeaveConfigPage(driver);
        configPage.goToHolidayList();
        Assert.assertTrue(
                configPage.isHolidayListPageDisplayed(),
                "TC-HO-01 FAILED: Holidays page heading not visible");
        System.out.println("TC-HO-01 PASSED: Holiday List page loaded successfully");
    }

    @Test(priority = 8)
    public void verifyHolidayListFilterButtonsAreDisplayed() {
        loginAsAdmin();
        LeaveConfigPage configPage = new LeaveConfigPage(driver);
        configPage.goToHolidayList();
        Assert.assertTrue(
                configPage.areHolidayFilterButtonsDisplayed(),
                "TC-HO-02 FAILED: Search or Reset button not visible on Holiday List");
        System.out.println("TC-HO-02 PASSED: Search and Reset buttons visible on Holiday List");
    }

    @Test(priority = 9)
    public void verifyHolidayListAddButtonIsDisplayed() {
        loginAsAdmin();
        LeaveConfigPage configPage = new LeaveConfigPage(driver);
        configPage.goToHolidayList();
        Assert.assertTrue(
                configPage.isHolidayAddButtonDisplayed(),
                "TC-HO-03 FAILED: 'Add' button not visible on Holiday List page");
        System.out.println("TC-HO-03 PASSED: 'Add' button is visible on Holiday List page");
    }

    /**
     * Clicks Search with no filters and accepts either rows or "No Records Found"
     * as valid — both outcomes are correct depending on demo site data state.
     */
    @Test(priority = 10)
    public void verifyHolidayListSearchRendersValidState() {
        loginAsAdmin();
        LeaveConfigPage configPage = new LeaveConfigPage(driver);
        configPage.goToHolidayList();
        Assert.assertTrue(
                configPage.isHolidaySearchResultValid(),
                "TC-HO-04 FAILED: Holiday List page in unexpected state after search");
        System.out.println("TC-HO-04 PASSED: Holiday List search rendered a valid state");
    }

    // ============================================================
    // LEAVE PERIOD (TC-LP-*)
    // ============================================================

    @Test(priority = 11)
    public void verifyLeavePeriodPageLoads() {
        loginAsAdmin();
        LeaveConfigPage configPage = new LeaveConfigPage(driver);
        configPage.goToLeavePeriod();
        Assert.assertTrue(
                configPage.isLeavePeriodPageDisplayed(),
                "TC-LP-01 FAILED: Leave Period page heading not visible");
        System.out.println("TC-LP-01 PASSED: Leave Period page loaded successfully");
    }

    @Test(priority = 12)
    public void verifyLeavePeriodSaveButtonIsDisplayed() {
        loginAsAdmin();
        LeaveConfigPage configPage = new LeaveConfigPage(driver);
        configPage.goToLeavePeriod();
        Assert.assertTrue(
                configPage.isLeavePeriodSaveButtonDisplayed(),
                "TC-LP-02 FAILED: 'Save' button not visible on Leave Period page");
        System.out.println("TC-LP-02 PASSED: 'Save' button is visible on Leave Period page");
    }
}
